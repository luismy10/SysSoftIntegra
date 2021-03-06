package controller.consultas.compras;

import controller.menus.FxPrincipalController;
import controller.reporte.FxReportViewController;
import controller.tools.ConvertMonedaCadena;
import controller.tools.FilesRouters;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.CompraADO;
import model.CompraCreditoTB;
import model.CompraTB;
import model.DetalleCompraTB;
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
    private Label lblProveedor;
    @FXML
    private Label lblTelefonoCelular;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblDireccion;
    @FXML
    private Label lblTipo;
    @FXML
    private Label lblEstado;
    @FXML
    private Label lblComprobante;
    @FXML
    private GridPane gpList;
    @FXML
    private Label lblTotalNeto;
    @FXML
    private Label lblDescuento;
    @FXML
    private Label lblSubTotal;
    @FXML
    private Label lblTotalBruto;
    @FXML
    private Label lblObservacion;
    @FXML
    private Label lblNotas;
    @FXML
    private Label lblTotalCompra;
    @FXML
    private VBox vbCondicion;
    @FXML
    private Button btnReporte;
    @FXML
    private Button btnAnular;
    @FXML
    private Label lblMetodoPago;
    @FXML
    private Label lblImpuesto;

    private FxComprasRealizadasController comprascontroller;

    private FxPrincipalController fxPrincipalController;

    private String idCompra;

    private ObservableList<DetalleCompraTB> arrList = null;

    private ArrayList<CompraCreditoTB> listComprasCredito;

    private ConvertMonedaCadena monedaCadena;

    private CompraTB compraTB = null;

    private double totalBruto;

    private double descuento;

    private double subTotal;

    private double impuestoTotal;

    private double totalNeto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        monedaCadena = new ConvertMonedaCadena();
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
            protected ArrayList<Object> call() {
                return CompraADO.ListCompletaDetalleCompra(idCompra);
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
                    lblProveedor.setText(compraTB.getProveedorTB().getNumeroDocumento()
                            + " " + compraTB.getProveedorTB().getRazonSocial().toUpperCase());
                    lblTelefonoCelular.setText(compraTB.getProveedorTB().getTelefono() + "-" + compraTB.getProveedorTB().getCelular());
                    lblEmail.setText(compraTB.getProveedorTB().getEmail());
                    lblDireccion.setText(compraTB.getProveedorTB().getDireccion());
                    lblFechaCompra.setText(compraTB.getFechaCompra().toUpperCase() + " " + compraTB.getHoraCompra());
                    lblComprobante.setText(compraTB.getSerie() + " - N ??" + compraTB.getNumeracion());
                    lblObservacion.setText(compraTB.getObservaciones().equalsIgnoreCase("") ? "NO TIENE NINGUNA OBSERVACI??N" : compraTB.getObservaciones());
                    lblNotas.setText(compraTB.getNotas().equalsIgnoreCase("") ? "NO TIENE NINGUNA NOTA" : compraTB.getNotas());
                    lblTipo.setText(compraTB.getTipoName());
                    lblEstado.setText(compraTB.getEstadoName());
                    lblTotalCompra.setText(compraTB.getMonedaNombre() + " " + Tools.roundingValue(compraTB.getTotal(), 2));

                    switch (compraTB.getEstado()) {
                        case 2:
                            btnReporte.setDisable(false);
                            btnAnular.setDisable(false);
                            break;
                        case 3:
                            btnReporte.setDisable(false);
                            btnAnular.setDisable(true);
                            lblEstado.setTextFill(Color.web("#990000"));
                            break;
                        case 4:
                            btnReporte.setDisable(false);
                            btnAnular.setDisable(true);
                            vbCondicion.getChildren().add(adddElementCondicion("La compra se encuentra en un estado de guardado"));
                            lblMetodoPago.setText("");
                            break;
                        default:
                            btnReporte.setDisable(false);
                            btnAnular.setDisable(false);
                            break;
                    }

                    listComprasCredito = (ArrayList<CompraCreditoTB>) objects.get(2);
                    if (!listComprasCredito.isEmpty()) {
                        lblMetodoPago.setText("M??todo de pago al cr??dito");
                        for (int i = 0; i < listComprasCredito.size(); i++) {
                            vbCondicion.getChildren().add(adddElementCondicion("Nro." + ((i + 1) < 10 ? "00" + (i + 1) : ((i + 1) >= 10 && (i + 1) <= 99 ? "0" + (i + 1) : (i + 1))) + " Pago el " + listComprasCredito.get(i).getFechaPago()+ " por " + compraTB.getMonedaNombre() + " " + Tools.roundingValue(listComprasCredito.get(i).getMonto(), 2)));
                        }
                    } else {
                        lblMetodoPago.setText("M??todo de pago al contado");
                        vbCondicion.getChildren().add(adddElementCondicion("Pago al contado por el valor de: " + lblTotalCompra.getText()));
                    }
                }
                fillArticlesTable(empList);
            } else {
                Tools.AlertMessageWarning(cpWindow, "Detalle de Compra", "Error el conectar al servidor, revise su conexi??n e intente nuevamente.");
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
                gpList.add(addElementGridPane("l4" + (i + 1), Tools.roundingValue(arrList.get(i).getPrecioCompra(), 2) + "(" + Tools.roundingValue(arrList.get(i).getDescuento(), 2) + "%)", Pos.CENTER_RIGHT), 3, (i + 1));
                gpList.add(addElementGridPane("l5" + (i + 1), Tools.roundingValue(arrList.get(i).getValorImpuesto(), 2) + "%", Pos.CENTER_RIGHT), 4, (i + 1));
                gpList.add(addElementGridPane("l6" + (i + 1), Tools.roundingValue(arrList.get(i).getCantidad(), 2), Pos.CENTER_RIGHT), 5, (i + 1));
                gpList.add(addElementGridPane("l7" + (i + 1), arrList.get(i).getSuministroTB().getUnidadCompraName(), Pos.CENTER_RIGHT), 6, (i + 1));
                gpList.add(addElementGridPane("l8" + (i + 1), compraTB.getMonedaTB().getSimbolo() + "" + Tools.roundingValue(arrList.get(i).getImporte(), 2), Pos.CENTER_RIGHT), 7, (i + 1));
            }
            calcularTotales();
        }
    }

    private Label addElementGridPane(String id, String nombre, Pos pos) {
        Label label = new Label(nombre);
        label.setId(id);
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

    private void calcularTotales() {

        totalBruto = 0;
        arrList.forEach(e -> totalBruto += (e.getCantidad() * e.getPrecioCompraUnico()));
        lblTotalBruto.setText(compraTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(totalBruto, 2));

        descuento = 0;
        arrList.forEach(e -> descuento += e.getDescuentoSumado());
        lblDescuento.setText(compraTB.getMonedaTB().getSimbolo() + " " + (Tools.roundingValue(descuento * (-1), 2)));

        subTotal = 0;
        arrList.forEach(e -> subTotal += e.getPrecioCompraReal() * e.getCantidad());
        lblSubTotal.setText(compraTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(subTotal, 2));

        impuestoTotal = 0;
        arrList.forEach(e -> impuestoTotal += e.getImpuestoSumado());
        lblImpuesto.setText(compraTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(impuestoTotal, 2));

        totalNeto = 0;
        arrList.forEach(e -> totalNeto += e.getPrecioCompra() * e.getCantidad());
        lblTotalNeto.setText(compraTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue((totalNeto), 2));

    }

    private Text adddElementCondicion(String value) {
        Text txtTitulo = new Text(value);
        txtTitulo.setStyle("-fx-fill:#020203");
        txtTitulo.getStyleClass().add("labelOpenSansRegular13");
        return txtTitulo;
    }

    private Label addLabelTitle(String nombre, Pos pos) {
        Label label = new Label(nombre);
        label.setStyle("-fx-text-fill:#020203;-fx-padding: 0.4166666666666667em 0em  0.4166666666666667em 0em;");
        label.getStyleClass().add("labelRoboto13");
        label.setAlignment(pos);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Control.USE_COMPUTED_SIZE);
        label.setMaxHeight(Control.USE_COMPUTED_SIZE);
        return label;
    }

    private Label addLabelTotal(String nombre, Pos pos) {
        Label label = new Label(nombre);
        label.setStyle("-fx-text-fill:#0771d3;");
        label.getStyleClass().add("labelRobotoMedium15");
        label.setAlignment(pos);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        return label;
    }

    private void onEventReporte() {
        try {
            ArrayList<DetalleCompraTB> list = new ArrayList();
            arrList.forEach(e -> {
                DetalleCompraTB detalleCompraTB = new DetalleCompraTB();
                detalleCompraTB.setId(e.getId());
                detalleCompraTB.setCantidad(e.getCantidad());
                detalleCompraTB.setMedida(e.getSuministroTB().getUnidadCompraName());
                detalleCompraTB.setDescripcion(e.getSuministroTB().getClave() + "\n" + e.getSuministroTB().getNombreMarca());
                detalleCompraTB.setDescuento(e.getDescuento());
                detalleCompraTB.setPrecioCompra(e.getPrecioCompra());                
                list.add(detalleCompraTB);
            });
            if (list.isEmpty()) {
                Tools.AlertMessageWarning(cpWindow, "Compra realizada", "No hay registros para mostrar en el reporte.");
                return;
            }

            InputStream logo
                    = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            InputStream icon
                    = getClass().getResourceAsStream(FilesRouters.IMAGE_ICON);

            Map map = new HashMap();
            map.put("LOGO", logo);
            map.put("ICON", icon);
            map.put("EMPRESA", Session.COMPANY_RAZON_SOCIAL);
            map.put("DIRECCION", Session.COMPANY_DOMICILIO);
            map.put("TELEFONOCELULAR", "TEL.: " + Session.COMPANY_TELEFONO + " CEL.: " + Session.COMPANY_CELULAR);
            map.put("EMAIL", "EMAIL: " + Session.COMPANY_EMAIL.toUpperCase());
            map.put("DOCUMENTOEMPRESA", "R.U.C " + Session.COMPANY_NUMERO_DOCUMENTO);
            map.put("NUMEROCOMPRA", idCompra);

            map.put("FECHAELABORACION", compraTB.getFechaCompra().toUpperCase());
            map.put("MONEDA", compraTB.getMonedaTB().getNombre());
            map.put("DOCUMENTOPROVEEDOR", compraTB.getProveedorTB().getNumeroDocumento());
            map.put("DATOSPROVEEDOR", compraTB.getProveedorTB().getRazonSocial());
            map.put("DIRECCIONPROVEEDOR", compraTB.getProveedorTB().getDireccion().length() == 0 ? "--" : compraTB.getProveedorTB().getDireccion());
            map.put("PROVEEDORTELEFONOS", "TEL.: " + (compraTB.getProveedorTB().getTelefono().length() == 0 ? "--" : compraTB.getProveedorTB().getTelefono().length()) + "  CEL.: " + (compraTB.getProveedorTB().getCelular().length() == 0 ? "" : compraTB.getProveedorTB().getCelular()));
            map.put("PROVEEDOREMAIL", compraTB.getProveedorTB().getEmail().length() == 0 ? "--" : compraTB.getProveedorTB().getEmail());

            map.put("NOTAS", compraTB.getNotas());
            map.put("SIMBOLO", compraTB.getMonedaNombre());
            map.put("VALORSOLES", monedaCadena.Convertir(Tools.roundingValue(compraTB.getTotal(), 2), true, compraTB.getMonedaTB().getNombre()));

            map.put("VALOR_VENTA", Tools.roundingValue(totalBruto, 2));
            map.put("DESCUENTO", Tools.roundingValue(descuento, 2));
            map.put("SUB_IMPORTE", Tools.roundingValue(subTotal, 2));
            map.put("IMPUESTO_TOTAL", Tools.roundingValue(impuestoTotal, 2));
            map.put("IMPORTE_TOTAL", Tools.roundingValue(totalNeto, 2));
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
  
    private void eventCancelarVenta() {
        if (!idCompra.equalsIgnoreCase("") || idCompra != null) {
            try {
                fxPrincipalController.openFondoModal();
                URL url = getClass().getResource(FilesRouters.FX_COMPRAS_CANCELAR);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxComprasCancelarController controller = fXMLLoader.getController();
                controller.setComprasDetalleController(this);
                controller.loadComponents();
                //
                Stage stage = WindowStage.StageLoaderModal(parent, "Anular su compra", cpWindow.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
                stage.show();
            } catch (IOException ex) {
                System.out.println("Controller compras" + ex.getLocalizedMessage());
            }

        }
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
        fxPrincipalController.getVbContent().getChildren().remove(cpWindow);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(comprascontroller.getWindow(), 0d);
        AnchorPane.setTopAnchor(comprascontroller.getWindow(), 0d);
        AnchorPane.setRightAnchor(comprascontroller.getWindow(), 0d);
        AnchorPane.setBottomAnchor(comprascontroller.getWindow(), 0d);
        fxPrincipalController.getVbContent().getChildren().add(comprascontroller.getWindow());
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

    public ScrollPane getCpWindow() {
        return cpWindow;
    }

    public CompraTB getCompraTB() {
        return compraTB;
    }

    public ObservableList<DetalleCompraTB> getArrList() {
        return arrList;
    }

    public ArrayList<CompraCreditoTB> getListComprasCredito() {
        return listComprasCredito;
    }

    public void setInitComptrasController(FxComprasRealizadasController comprascontroller, FxPrincipalController fxPrincipalController) {
        this.comprascontroller = comprascontroller;
        this.fxPrincipalController = fxPrincipalController;
    }

}
