package controller.consultas.compras;

import controller.reporte.FxReportViewController;
import controller.tools.ConvertMonedaCadena;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.awt.HeadlessException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.CompraADO;
import model.CompraCreditoTB;
import model.CompraTB;
import model.DetalleCompraTB;
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.SuministroTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FxComprasDetalleController implements Initializable {

    @FXML
    private ScrollPane cpWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private Text lblFechaCompra;
    @FXML
    private Label lblDocumento;
    @FXML
    private Label lblProveedor;
    @FXML
    private Label lblDomicilio;
    @FXML
    private Label lblEstado;
    @FXML
    private Label lblContacto;
    @FXML
    private Label lblComprobante;
    @FXML
    private Label lblNumeracion;
    @FXML
    private GridPane gpList;
    @FXML
    private Text lblTotalNeto;
    @FXML
    private Text lblDescuento;
    @FXML
    private Text lblSubTotal;
    @FXML
    private Text lblTotalBruto;
    @FXML
    private VBox hbAgregarImpuesto;
    @FXML
    private Label lblObservacion;
    @FXML
    private Label lblNotas;
    @FXML
    private Label lblTotalCompra;
    @FXML
    private Text lblValorLetras;
    @FXML
    private VBox vbCondicion;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnAnular;

    private double totalBruto;

    private double descuento;

    private double subTotal;

    private double totalNeto;

    private FxComprasRealizadasController comprascontroller;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    private String idCompra;

    private ObservableList<DetalleCompraTB> arrList = null;

    private ArrayList<ImpuestoTB> arrayArticulos;

    private ConvertMonedaCadena monedaCadena;

    private CompraTB compraTB = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        arrayArticulos = new ArrayList<>();
        monedaCadena = new ConvertMonedaCadena();
    }

    private Label addElementGridPane(String id, String nombre, Pos pos) {
        Label label = new Label(nombre);
        label.setId(id);
        label.setStyle("-fx-text-fill:#020203;-fx-background-color: #dddddd;-fx-padding: 0.4166666666666667em 0.8333333333333334em 0.4166666666666667em 0.8333333333333334em;");
        label.getStyleClass().add("labelRoboto14");
        label.setAlignment(pos);
        label.setWrapText(true);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        return label;
    }

    public void setLoadDetalle(String idCompra) {
        this.idCompra = idCompra;

        ExecutorService executor = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            protected ArrayList<Object> call() throws Exception {
                arrayArticulos.clear();
                ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> {
                    arrayArticulos.add(new ImpuestoTB(e.getIdImpuesto(), e.getNombreOperacion(), e.getNombreImpuesto(), e.getValor(), e.getPredeterminado()));
                });
                ArrayList<Object> objects = CompraADO.ListCompletaDetalleCompra(idCompra);
                return objects;
            }
        };

        task.setOnScheduled(e -> {
            lblLoad.setVisible(true);
        });
        task.setOnRunning(e -> {
            lblLoad.setVisible(true);
        });
        task.setOnFailed(e -> {
            lblLoad.setVisible(false);
        });
        task.setOnSucceeded(e -> {
            ArrayList<Object> objects = task.getValue();
            if (!objects.isEmpty()) {
                compraTB = (CompraTB) objects.get(0);
                ObservableList<DetalleCompraTB> empList = (ObservableList<DetalleCompraTB>) objects.get(1);
                if (compraTB != null) {
                    lblFechaCompra.setText(compraTB.getFechaCompra() + " " + compraTB.getHoraCompra());
                    lblComprobante.setText(compraTB.getComprobanteName());
                    lblNumeracion.setText(compraTB.getNumeracion());
                    lblObservacion.setText(compraTB.getObservaciones().equalsIgnoreCase("") ? "No tiene ninguna observación" : compraTB.getObservaciones());
                    lblNotas.setText(compraTB.getNotas().equalsIgnoreCase("") ? "No tiene ninguna nota" : compraTB.getNotas());
                    lblEstado.setText(compraTB.getTipoName() + " - " + compraTB.getEstadoName());
                    lblTotalCompra.setText(compraTB.getTipoMonedaName() + " " + Tools.roundingValue(compraTB.getTotal(), 2));
                    lblValorLetras.setText(monedaCadena.Convertir(Tools.roundingValue(compraTB.getTotal(), 2), true, compraTB.getMonedaTB().getNombre()));

                    lblDocumento.setText(compraTB.getProveedorTB().getNumeroDocumento());
                    lblProveedor.setText(compraTB.getProveedorTB().getRazonSocial());
                    lblDomicilio.setText(compraTB.getProveedorTB().getDireccion().equalsIgnoreCase("")
                            ? "No tiene un domicilio registrado"
                            : compraTB.getProveedorTB().getDireccion());
                    lblContacto.setText("Tel: " + compraTB.getProveedorTB().getTelefono() + " Cel: " + compraTB.getProveedorTB().getCelular());
                }

                fillArticlesTable(empList);

                if (compraTB != null && compraTB.getEstado() == 4) {
                    btnEditar.setDisable(false);
                    vbCondicion.getChildren().add(adddElementCondicion("La compra se encuentra en un estado de guardado"));
                } else {
                    btnAnular.setDisable(false);
                    ArrayList<CompraCreditoTB> listComprasCredito = (ArrayList<CompraCreditoTB>) objects.get(2);
                    if (!listComprasCredito.isEmpty()) {
                        for (int i = 0; i < listComprasCredito.size(); i++) {
                            vbCondicion.getChildren().add(adddElementCondicion("Compra al crédito-Couto Nro." + ((i + 1) < 10 ? "00" + (i + 1) : ((i + 1) >= 10 && (i + 1) <= 99 ? "0" + (i + 1) : (i + 1))) + " Vence el " + listComprasCredito.get(i).getFechaRegistro() + " por " + compraTB.getTipoMonedaName() + " " + Tools.roundingValue(listComprasCredito.get(i).getMonto(), 2) + " Estado " + (listComprasCredito.get(i).isEstado() ? "Pagado" : "Pendiente") + " " + (listComprasCredito.get(i).isEstado() ? "el " + listComprasCredito.get(i).getFechaPago() : "")));
                        }
                    } else {
                        vbCondicion.getChildren().add(adddElementCondicion("Pago al contado por el valor de: " + lblTotalCompra.getText()));
                    }
                }

            } else {
                Tools.AlertMessageWarning(cpWindow, "Detalle de Compra", "Error el conectar al servidor, revise su conexión e intente nuevamente.");
            }
            lblLoad.setVisible(false);
        });
        executor.execute(task);
        if (!executor.isShutdown()) {
            executor.shutdown();
        }

    }

    private void fillArticlesTable(ObservableList<DetalleCompraTB> arr) {
        arrList = arr;
        if (arrList != null) {
            for (int i = 0; i < arrList.size(); i++) {
                gpList.add(addElementGridPane("l1" + (i + 1), arrList.get(i).getId() + "", Pos.CENTER), 0, (i + 1));
                gpList.add(addElementGridPane("l2" + (i + 1), arrList.get(i).getSuministroTB().getClave() + "\n" + arrList.get(i).getSuministroTB().getNombreMarca(), Pos.CENTER_LEFT), 1, (i + 1));
                gpList.add(addElementGridPane("l3" + (i + 1), compraTB.getMonedaTB().getSimbolo() + "" + Tools.roundingValue(arrList.get(i).getPrecioCompra(), 2), Pos.CENTER_RIGHT), 2, (i + 1));
                gpList.add(addElementGridPane("l4" + (i + 1), Tools.roundingValue(arrList.get(i).getPrecioCompraCalculado(), 2) + "(" + Tools.roundingValue(arrList.get(i).getDescuento(), 2) + "%)", Pos.CENTER_RIGHT), 3, (i + 1));
                gpList.add(addElementGridPane("l5" + (i + 1), Tools.roundingValue(arrList.get(i).getValorImpuesto(), 2) + "%", Pos.CENTER_RIGHT), 4, (i + 1));
                gpList.add(addElementGridPane("l6" + (i + 1), Tools.roundingValue(arrList.get(i).getCantidad(), 2), Pos.CENTER_RIGHT), 5, (i + 1));
                gpList.add(addElementGridPane("l7" + (i + 1), arrList.get(i).getSuministroTB().getUnidadCompraName(), Pos.CENTER_RIGHT), 6, (i + 1));
                gpList.add(addElementGridPane("l8" + (i + 1), compraTB.getMonedaTB().getSimbolo() + "" + Tools.roundingValue(arrList.get(i).getImporte(), 2), Pos.CENTER_RIGHT), 7, (i + 1));
            }
            calcularTotales();
        }

    }

    private void calcularTotales() {

        arrList.forEach(e -> {
            totalBruto += (e.getCantidad() * e.getPrecioCompra());
        });
        lblTotalBruto.setText(compraTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(totalBruto, 2));
        totalBruto = 0;

        arrList.forEach(e -> {
            descuento += e.getCantidad() * (e.getPrecioCompra() * (e.getDescuento() / 100.00));
        });
        lblDescuento.setText(compraTB.getMonedaTB().getSimbolo() + " " + (Tools.roundingValue(descuento * (-1), 2)));
        descuento = 0;

        arrList.forEach(e -> {
            subTotal += (e.getCantidad() * e.getPrecioCompra()) - (e.getCantidad() * (e.getPrecioCompra() * (e.getDescuento() / 100.00)));
        });
        lblSubTotal.setText(compraTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(subTotal, 2));
        subTotal = 0;

        hbAgregarImpuesto.getChildren().clear();
        boolean addElement = false;
        double sumaElement = 0;

        for (int k = 0; k < arrayArticulos.size(); k++) {
            for (int i = 0; i < arrList.size(); i++) {
                if (arrayArticulos.get(k).getIdImpuesto() == arrList.get(i).getIdImpuesto()) {
                    addElement = true;
                    sumaElement += arrList.get(i).getImpuestoSumado();
                }
            }
            if (addElement) {
                addElementImpuesto(arrayArticulos.get(k).getIdImpuesto() + "", arrayArticulos.get(k).getNombreImpuesto(), compraTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(sumaElement, 2));
                addElement = false;
                sumaElement = 0;
            }

        }

        arrList.forEach(e -> {
            totalNeto += e.getImporte();
        });
        lblTotalNeto.setText(compraTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue((totalNeto), 2));
        totalNeto = 0;
    }

    private Text adddElementCondicion(String value) {
        Text txtTitulo = new Text(value);
        txtTitulo.setStyle("-fx-fill:#020203");
        txtTitulo.getStyleClass().add("labelOpenSansRegular14");
        return txtTitulo;
    }

    private void addElementImpuesto(String id, String titulo, String total) {
        Text txtTitulo = new Text(titulo);
        txtTitulo.setStyle("-fx-fill:#020203");
        txtTitulo.getStyleClass().add("labelOpenSansRegular14");

        Text txtTotal = new Text(total);
        txtTotal.setStyle("-fx-fill:#004db9");
        txtTotal.getStyleClass().add("labelOpenSansRegular14");

        HBox hBox = new HBox(txtTitulo, txtTotal);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setStyle("-fx-spacing: 0.8333333333333334em");
        hBox.setId(id);

        hbAgregarImpuesto.getChildren().add(hBox);
    }

    private void onEventReporte() {
        try {
            ArrayList<SuministroTB> list = new ArrayList();
            arrList.stream().map((DetalleCompraTB detalleCompraTB) -> {
                SuministroTB stb = new SuministroTB();
                stb.setCantidad(detalleCompraTB.getCantidad());
                stb.setUnidadCompraName(detalleCompraTB.getSuministroTB().getUnidadCompraName());
                stb.setNombreMarca(detalleCompraTB.getSuministroTB().getNombreMarca());
                stb.setCostoCompra(detalleCompraTB.getPrecioCompra());
                stb.setDescuento(detalleCompraTB.getDescuento());
                stb.setTotalImporte(detalleCompraTB.getImporte());
                return stb;
            }).forEachOrdered((stb) -> {
                list.add(stb);
            });

            boolean addOperacion = false;
            double sumaOperacion = 0;

            boolean addImpuesto = false;
            double sumaImpuesto = 0;

            ArrayList<SuministroTB> list_totales = new ArrayList();

            for (int k = 0; k < arrayArticulos.size(); k++) {
                for (int i = 0; i < arrList.size(); i++) {
                    if (arrayArticulos.get(k).getIdImpuesto() == arrList.get(i).getIdImpuesto()) {
                        addOperacion = true;
                        sumaOperacion += (arrList.get(i).getCantidad() * arrList.get(i).getPrecioCompra()) - (arrList.get(i).getCantidad() * (arrList.get(i).getPrecioCompra() * (arrList.get(i).getDescuento() / 100.00)));
                    }
                }
                if (addOperacion) {
                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setImpuestoArticuloName(arrayArticulos.get(k).getNombreOperacion().toLowerCase().substring(0, 1).toUpperCase() + arrayArticulos.get(k).getNombreOperacion().toLowerCase().substring(1, arrayArticulos.get(k).getNombreOperacion().length()).toLowerCase() + ":");
                    suministroTB.setImpuestoValor(sumaOperacion);
                    list_totales.add(suministroTB);
                    addOperacion = false;
                    sumaOperacion = 0;
                }
            }

            for (int k = 0; k < arrayArticulos.size(); k++) {
                for (int i = 0; i < arrList.size(); i++) {
                    if (arrayArticulos.get(k).getIdImpuesto() == arrList.get(i).getIdImpuesto()) {
                        addImpuesto = true;
                        sumaImpuesto += arrList.get(i).getImpuestoSumado();
                    }
                }
                if (addImpuesto) {
                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setImpuestoArticuloName(arrayArticulos.get(k).getNombreImpuesto() + ":");
                    suministroTB.setImpuestoValor(sumaImpuesto);
                    list_totales.add(suministroTB);
                    addImpuesto = false;
                    sumaImpuesto = 0;
                }
            }

            if (list.isEmpty()) {
                Tools.AlertMessageWarning(cpWindow, "Compra realizada", "No hay registros para mostrar en el reporte.");
                return;
            }

            InputStream imgInputStream
                    = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            Map map = new HashMap();
            map.put("LOGO", imgInputStream);
            map.put("EMPRESA", Session.COMPANY_RAZON_SOCIAL);
            map.put("DIRECCION", Session.COMPANY_DOMICILIO);
            map.put("TELEFONOCELULAR", "Tel.: " + Session.COMPANY_TELEFONO + " Cel.: " + Session.COMPANY_CELULAR);
            map.put("EMAIL", "Email: " + Session.COMPANY_EMAIL);
            map.put("DOCUMENTOEMPRESA", "R.U.C " + Session.COMPANY_NUM_DOCUMENTO);

            map.put("DATOSPROVEEDOR", lblProveedor.getText());
            map.put("NUMERODOCUMENTOPROVEEDOR", lblDocumento.getText());
            map.put("DIRECCIONPROVEEDOR", lblDomicilio.getText());
            map.put("FECHAELABORACION", compraTB.getFechaCompra());
            map.put("MONEDA", compraTB.getTipoMonedaName());
            map.put("DOCUMENTOPROVEEDOR", compraTB.getProveedorTB().getTipoDocumentoName());

            map.put("SIMBOLO", compraTB.getTipoMonedaName());
            map.put("CALCULAR_TOTALES", new JRBeanCollectionDataSource(list_totales));
            map.put("VALORSOLES", monedaCadena.Convertir(Tools.roundingValue(compraTB.getTotal(), 2), true, compraTB.getMonedaTB().getNombre()));
            map.put("VALOR_COMPRA", lblTotalBruto.getText());
            map.put("DESCUENTO", lblDescuento.getText());
            map.put("SUB_TOTAL", lblSubTotal.getText());
            map.put("TOTAL", lblTotalNeto.getText());

            JasperPrint jasperPrint = JasperFillManager.fillReport(FxComprasDetalleController.class.getResourceAsStream("/report/CompraRealizada.jasper"), map, new JRBeanCollectionDataSource(list));

            URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxReportViewController controller = fXMLLoader.getController();
            controller.setJasperPrint(jasperPrint);
            controller.show();
            Stage stage = WindowStage.StageLoader(parent, "Reporte General de Compra");
            stage.setResizable(true);
            stage.show();
            stage.requestFocus();
        } catch (HeadlessException | JRException | IOException ex) {
            Tools.AlertMessageError(cpWindow, "Reporte General de Compras", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    private void eventEditarVenta() {
        
        if(compraTB!=null && compraTB.getEstado() == 4){
            
        }
        
//        if (estadoCompra.equals("anulado".toUpperCase())) {
//            if (!idCompra.equalsIgnoreCase("") || idCompra != null) {
//                try {
//                    ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
//                    URL url = getClass().getResource(FilesRouters.FX_COMPRAS_EDITAR);
//                    FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
//                    Parent parent = fXMLLoader.load(url.openStream());
//                    //Controlller here
//                    FxComprasEditarController controller = fXMLLoader.getController();
//                    controller.setInitComprasValue(idCompra);
//                    //
//                    Stage stage = WindowStage.StageLoaderModal(parent, "Editar su compra", cpWindow.getScene().getWindow());
//                    stage.setResizable(false);
//                    stage.sizeToScene();
//                    stage.setOnHiding((w) -> {
//                        vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
//                    });
//                    stage.show();
//                } catch (IOException ex) {
//                    System.out.println("Controller compras" + ex.getLocalizedMessage());
//                }
//            }
//        } else {
//            Tools.AlertMessageWarning(cpWindow, "Detalle de Compra", "Debe anular la compra para poder editar.");
//        }
    }

    private void eventCancelarVenta() {
//        if (!idCompra.equalsIgnoreCase("") || idCompra != null) {
//
//            try {
//                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
//                URL url = getClass().getResource(FilesRouters.FX_COMPRAS_CANCELAR);
//                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
//                Parent parent = fXMLLoader.load(url.openStream());
//                //Controlller here
//                FxComprasCancelarController controller = fXMLLoader.getController();
//                controller.setTableList(arrList);
//                controller.setIdCompra(idCompra);
//                //
//                Stage stage = WindowStage.StageLoaderModal(parent, "Cancelar su compra", cpWindow.getScene().getWindow());
//                stage.setResizable(false);
//                stage.sizeToScene();
//                stage.setOnHiding((w) -> {
//                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
//                });
//                stage.show();
//            } catch (IOException ex) {
//                System.out.println("Controller compras" + ex.getLocalizedMessage());
//            }
//
//        }
    }

    @FXML
    private void onKeyPressedImprimir(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventReporte();
        }
    }

    @FXML
    private void onActionImprimir(ActionEvent event) {
        onEventReporte();
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) throws IOException {
        vbContent.getChildren().remove(cpWindow);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(comprascontroller.getWindow(), 0d);
        AnchorPane.setTopAnchor(comprascontroller.getWindow(), 0d);
        AnchorPane.setRightAnchor(comprascontroller.getWindow(), 0d);
        AnchorPane.setBottomAnchor(comprascontroller.getWindow(), 0d);
        vbContent.getChildren().add(comprascontroller.getWindow());
    }

    @FXML
    private void onKeyPressedCancelar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            eventCancelarVenta();
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        eventCancelarVenta();
    }

    @FXML
    private void onKeyPressedEditar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            eventEditarVenta();
        }
    }

    @FXML
    private void onActionEditar(ActionEvent event) {
        eventEditarVenta();
    }

    public void setInitComptrasController(FxComprasRealizadasController comprascontroller, AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.comprascontroller = comprascontroller;
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}