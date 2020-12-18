package controller.consultas.pagar;

import controller.configuracion.impresoras.FxOpcionesImprimirController;
import controller.operaciones.compras.FxAmortizarPagosController;
import controller.tools.ConvertMonedaCadena;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.CompraADO;
import model.CompraCreditoTB;
import model.CompraTB;

public class FxCuentasPorPagarVisualizarController implements Initializable {

    @FXML
    private ScrollPane spWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private Label lblProveedor;
    @FXML
    private Label lblComprobante;
    @FXML
    private Label lblEstado;
    @FXML
    private Label lblObservacion;
    @FXML
    private Label lblTelefonoCelular;
    @FXML
    private Label lblDireccion;
    @FXML
    private Label lblEmail;
    @FXML
    private GridPane gpList;
    @FXML
    private Label lblMontoPagado;
    @FXML
    private Label lblDiferencia;
    @FXML
    private Button btnAmortizar;
    @FXML
    private Label lblMontoTotal;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    private FxCuentasPorPagarController cuentasPorPagarController;

    private ConvertMonedaCadena monedaCadena;

    private CompraTB compraTB;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        monedaCadena = new ConvertMonedaCadena();
    }

    public void loadTableCompraCredito(String idCompra) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<CompraTB> task = new Task<CompraTB>() {
            @Override
            public CompraTB call() {
                return CompraADO.Listar_Compra_Credito(idCompra);
            }
        };
        task.setOnScheduled(w -> {
            lblLoad.setVisible(true);
        });
        task.setOnFailed(w -> {
            lblLoad.setVisible(false);
        });
        task.setOnSucceeded(w -> {
            compraTB = task.getValue();
            if(compraTB != null){
                lblProveedor.setText(compraTB.getProveedorTB().getNumeroDocumento()+" - "+compraTB.getProveedorTB().getRazonSocial());
                lblTelefonoCelular.setText(compraTB.getProveedorTB().getCelular());
                lblDireccion.setText(compraTB.getProveedorTB().getDireccion());
                lblEmail.setText(compraTB.getProveedorTB().getEmail());
                lblComprobante.setText(compraTB.getSerie()+" - "+compraTB.getNumeracion());
                lblEstado.setText(compraTB.getEstadoName());
                lblObservacion.setText(compraTB.getObservaciones());
                lblMontoTotal.setText(Tools.roundingValue(compraTB.getTotal(), 2));      
                for (CompraCreditoTB vc : compraTB.getCompraCreditoTBs()) {
                    vc.getBtnImprimir().setOnAction(event-> 
                            openModalImpresion(idCompra, vc.getIdCompraCredito())
                    );
                    vc.getBtnImprimir().setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.ENTER) {
                            openModalImpresion(idCompra, vc.getIdCompraCredito());
                        }
                    });
                }
                fillVentasDetalleTable();
                lblLoad.setVisible(false);
            }else{                
                lblLoad.setVisible(false);
            }
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }
    
     private void fillVentasDetalleTable() {
        double montoPagado = 0;
        for (int i = 0; i < compraTB.getCompraCreditoTBs().size(); i++) {
            gpList.add(addElementGridPane("l1" + (i + 1), compraTB.getCompraCreditoTBs().get(i).getId() + "", Pos.CENTER, null), 0, (i + 1));
            gpList.add(addElementGridPane("l2" + (i + 1), compraTB.getCompraCreditoTBs().get(i).getIdCompraCredito(), Pos.CENTER, null), 1, (i + 1));
            gpList.add(addElementGridPane("l3" + (i + 1), compraTB.getCompraCreditoTBs().get(i).getFechaPago(), Pos.CENTER, null), 2, (i + 1));
            gpList.add(addElementGridPane("l4" + (i + 1), compraTB.getCompraCreditoTBs().get(i).isEstado()+ "", Pos.CENTER, null), 3, (i + 1));
            gpList.add(addElementGridPane("l5" + (i + 1), Tools.roundingValue(compraTB.getCompraCreditoTBs().get(i).getMonto(), 2), Pos.CENTER, null), 4, (i + 1));
            gpList.add(addElementGridPane("l6" + (i + 1), compraTB.getCompraCreditoTBs().get(i).getObservacion(), Pos.CENTER, null), 5, (i + 1));
            gpList.add(addElementGridPane("l7" + (i + 1), "", Pos.CENTER, compraTB.getCompraCreditoTBs().get(i).getBtnImprimir()), 6, (i + 1));
            montoPagado = compraTB.getCompraCreditoTBs().get(i).getMonto();
        }
        lblMontoPagado.setText(Tools.roundingValue(montoPagado, 2));
        lblDiferencia.setText(Tools.roundingValue(compraTB.getTotal() - montoPagado, 2));
    }

    private Label addElementGridPane(String id, String nombre, Pos pos, Node node) {
        Label label = new Label(nombre);
        label.setId(id);
        label.setGraphic(node);
        label.setStyle("-fx-text-fill:#020203;-fx-background-color: #dddddd;-fx-padding: 0.4166666666666667em 0.8333333333333334em 0.4166666666666667em 0.8333333333333334em;");
        label.getStyleClass().add("labelRoboto13");
        label.setAlignment(pos);
        label.setWrapText(true);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        return label;
    }

    private void onEventAmortizar() {
        if (compraTB != null) {
            try {
                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                URL url = getClass().getResource(FilesRouters.FX_AMARTIZAR_PAGOS);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxAmortizarPagosController controller = fXMLLoader.getController();
                controller.setInitValues(compraTB.getIdCompra());
                controller.setInitAmortizarPagosController(this);
                //
                Stage stage = WindowStage.StageLoaderModal(parent, "Generar Pago", spWindow.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
                stage.show();
            } catch (IOException ex) {
                System.out.println("Controller banco" + ex.getLocalizedMessage());
            }
        } else {
            Tools.AlertMessageWarning(spWindow, "Generar Pago", "No se puedo cargar los datos, intente nuevamente.");
        }
    }

    public void openModalImpresion(String idCompra, String idCompraCredito) {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_OPCIONES_IMPRIMIR);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxOpcionesImprimirController controller = fXMLLoader.getController();
             controller.loadDataCuentaPorPagar(idCompra, idCompraCredito);
            controller.setInitOpcionesImprimirCuentasPorPagar(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Imprimir", spWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
        } catch (IOException ex) {
            System.out.println("Controller banco" + ex.getLocalizedMessage());
        }
    }

    public void openWindowReport(String idTransaccion) {
//
//        if (idTransaccion.equals("")) {
//            Tools.AlertMessageWarning(spWindow, "Amortizar pago", "No se pudo generar el reporte por problemas con el número de transacción..");
//            return;
//        }
//
//        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
//            Thread t = new Thread(runnable);
//            t.setDaemon(true);
//            return t;
//        });
//
//        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
//            @Override
//            public ArrayList<Object> call() {
//                return CompraADO.Listar_Compra_Credito_By_IdTransaccion(idTransaccion);
//            }
//        };
//
//        task.setOnScheduled(w -> {
//            Tools.println("inicio");
//        });
//
//        task.setOnFailed(w -> {
//            Tools.println("fallo");
//        });
//
//        task.setOnSucceeded(w -> {
//            try {
//                ArrayList<Object> objects = task.getValue();
//                if (objects == null) {
//                    Tools.AlertMessageWarning(spWindow, "Amortizar pago", "No se pudo crear el reporte por problemas de conexión intente nuevamente.");
//                } else if (objects.get(0) == null && objects.get(1) == null && objects.get(2) == null) {
//                    Tools.AlertMessageWarning(spWindow, "Amortizar pago", "No se pudo crear el reporte por problemas de conexión intente nuevamente.");
//                } else {
//
//                    CompraTB compraTB = (CompraTB) objects.get(0);
//                    ArrayList<CompraCreditoTB> empListThis = (ArrayList<CompraCreditoTB>) objects.get(1);
//                    TransaccionTB transaccionTB = (TransaccionTB) objects.get(2);
//
//                    double montoPagar = 0;
//                    for (CompraCreditoTB cc : empListThis) {
//                        montoPagar += cc.getMonto();
//                    }
//
//                    InputStream dir = getClass().getResourceAsStream("/report/CompraAmortizarPago.jasper");
//
//                    InputStream imgInputStream = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);
//
//                    if (Session.COMPANY_IMAGE != null) {
//                        imgInputStream = new ByteArrayInputStream(Session.COMPANY_IMAGE);
//                    }
//
//                    Map map = new HashMap();
//                    map.put("LOGO", imgInputStream);
//
//                    map.put("NOMBRE_EMPRESA", Session.COMPANY_RAZON_SOCIAL);
//                    map.put("NUMERODOCUMENTO_EMPRESA", Session.COMPANY_NUMERO_DOCUMENTO);
//                    map.put("DIRECCION_EMPRESA", Session.COMPANY_DOMICILIO.equalsIgnoreCase("") ? "- - -" : Session.COMPANY_DOMICILIO);
//                    map.put("TELEFONOS_EMPRESA", (Session.COMPANY_TELEFONO.equalsIgnoreCase("") ? "- - -" : Session.COMPANY_TELEFONO) + " " + (Session.COMPANY_CELULAR.equalsIgnoreCase("") ? "- - -" : Session.COMPANY_CELULAR));
//                    map.put("EMAIL_EMPRESA", Session.COMPANY_EMAIL.equalsIgnoreCase("") ? "- - -" : Session.COMPANY_EMAIL);
//                    map.put("PAGINAWEB_EMPRESA", Session.COMPANY_PAGINAWEB.equalsIgnoreCase("") ? "- - -" : Session.COMPANY_PAGINAWEB);
////
//                    map.put("NUMERODOCUMENTO_PROVEEDOR", compraTB.getProveedorTB().getNumeroDocumento());
//                    map.put("INFORMACION_PROVEEDOR", compraTB.getProveedorTB().getRazonSocial());
//                    map.put("TELEFONO_PROVEEDOR", compraTB.getProveedorTB().getTelefono().equalsIgnoreCase("") ? "- - -" : compraTB.getProveedorTB().getTelefono());
//                    map.put("CELULAR_PROVEEDOR", compraTB.getProveedorTB().getCelular().equalsIgnoreCase("") ? "- - -" : compraTB.getProveedorTB().getCelular());
//                    map.put("EMAIL_PROVEEDOR", compraTB.getProveedorTB().getEmail().equalsIgnoreCase("") ? "- - -" : compraTB.getProveedorTB().getEmail());
//                    map.put("DIRECCION_PROVEEDOR", compraTB.getProveedorTB().getDireccion().equalsIgnoreCase("") ? "- - -" : compraTB.getProveedorTB().getDireccion());
//
//                    map.put("NUM_TRANSACCION", idTransaccion);
//                    map.put("FECHA_PAGO", transaccionTB.getFecha());
//                    map.put("METODO_PAGO", "EFECTIVO");
//
//                    map.put("TOTAL_LETRAS", monedaCadena.Convertir(Tools.roundingValue(montoPagar, 2), true, Session.MONEDA_NOMBRE));
//                    map.put("TOTAL", Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(montoPagar, 2));
//
//                    JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JRBeanCollectionDataSource(empListThis));
//
//                    URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
//                    FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
//                    Parent parent = fXMLLoader.load(url.openStream());
//                    //Controlller here
//                    FxReportViewController controller = fXMLLoader.getController();
//                    controller.setJasperPrint(jasperPrint);
//                    controller.show();
//                    Stage stage = WindowStage.StageLoader(parent, "Reporte de Pago");
//                    stage.setResizable(true);
//                    stage.show();
//                    stage.requestFocus();
//                }
//
//            } catch (HeadlessException | JRException | IOException ex) {
//                Tools.AlertMessageError(spWindow, "Reporte de Pago", "Error al generar el reporte: " + ex.getLocalizedMessage());
//            }
//        });
//
//        exec.execute(task);
//        if (!exec.isShutdown()) {
//            exec.shutdown();
//        }
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        vbContent.getChildren().remove(spWindow);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(cuentasPorPagarController.getVbWindow(), 0d);
        AnchorPane.setTopAnchor(cuentasPorPagarController.getVbWindow(), 0d);
        AnchorPane.setRightAnchor(cuentasPorPagarController.getVbWindow(), 0d);
        AnchorPane.setBottomAnchor(cuentasPorPagarController.getVbWindow(), 0d);
        vbContent.getChildren().add(cuentasPorPagarController.getVbWindow());
    }

    @FXML
    private void onKeyPressedAmortizar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventAmortizar();
        }
    }

    @FXML
    private void onActionAmortizar(ActionEvent event) {
        onEventAmortizar();
    }

    @FXML
    private void onKeyPressedTicket(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionTicket(ActionEvent event) {

    }

    public void setInitCuentasPorPagar(AnchorPane vbPrincipal, AnchorPane vbContent, FxCuentasPorPagarController cuentasPorPagarController) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
        this.cuentasPorPagarController = cuentasPorPagarController;
    }

}
