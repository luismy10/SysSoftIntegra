package controller.egresos.compras;

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
import javafx.scene.control.Alert;
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
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import model.CompraADO;
import model.CompraTB;
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.ProveedorTB;
import model.SuministroTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

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
    private Text lblTotal;
    @FXML
    private GridPane gpList;
    @FXML
    private Text lblSubTotal;
    @FXML
    private Text lblDescuento;
    @FXML
    private Text lblSubTotalNuevo;
    @FXML
    private VBox hbAgregarImpuesto;
    @FXML
    private Label lblObservacion;
    @FXML
    private Label lblNotas;
    @FXML
    private Label lblTotalCompra;

    private double subImporte;

    private double descuento;

    private double subTotalImporte;

    private double totalImporte;

    private FxComprasRealizadasController comprascontroller;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    private String idCompra;

    private double total;

    private String tipoCompra;
    
    private String estadoCompra;

    private String simboloMoneda;

    private ObservableList<SuministroTB> arrList = null;

    private ArrayList<ImpuestoTB> arrayArticulos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        arrayArticulos = new ArrayList<>();
        simboloMoneda = "M";

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
                    arrayArticulos.add(new ImpuestoTB(e.getIdImpuesto(), e.getNombreImpuesto(), e.getValor(), e.getPredeterminado()));
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
                CompraTB compraTB = (CompraTB) objects.get(0);
                ProveedorTB proveedorTB = (ProveedorTB) objects.get(1);
                ObservableList<SuministroTB> empList = (ObservableList<SuministroTB>) objects.get(2);
                if (compraTB != null) {
                    lblFechaCompra.setText(compraTB.getFecha() + " " + compraTB.getHora());
                    lblComprobante.setText(compraTB.getComprobanteName());
                    lblNumeracion.setText(compraTB.getNumeracion());
                    lblObservacion.setText(compraTB.getObservaciones().equalsIgnoreCase("") ? "No tiene ninguna observación" : compraTB.getObservaciones());
                    lblNotas.setText(compraTB.getNotas().equalsIgnoreCase("") ? "No tiene ninguna nota" : compraTB.getNotas());
                    lblEstado.setText(compraTB.getTipoName() + " - " + compraTB.getEstadoName());
                    lblTotalCompra.setText(compraTB.getTipoMonedaName() + " " + Tools.roundingValue(compraTB.getTotal().get(), 2));
                    simboloMoneda = compraTB.getTipoMonedaName();
                    tipoCompra = compraTB.getTipoName();
                    estadoCompra = compraTB.getEstadoName();
                    total = compraTB.getTotal().get();
                }

                if (proveedorTB != null) {
                    lblDocumento.setText(proveedorTB.getNumeroDocumento().get());
                    lblProveedor.setText(proveedorTB.getRazonSocial().get());
                    lblDomicilio.setText(proveedorTB.getDireccion().equalsIgnoreCase("")
                            ? "No tiene un domicilio registrado"
                            : proveedorTB.getDireccion());
                    lblContacto.setText("Tel: " + proveedorTB.getTelefono() + " Cel: " + proveedorTB.getCelular());

                }

                fillArticlesTable(empList);

            } else {
                Tools.AlertMessage(cpWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Detalle de Compra", "Error el conectar al servidor, revise su conexión e intente nuevamente.", false);
            }
            lblLoad.setVisible(false);
        });
        executor.execute(task);
        if (!executor.isShutdown()) {
            executor.shutdown();
        }

    }

    private void fillArticlesTable(ObservableList<SuministroTB> arr) {
        arrList = arr;
        if (arrList != null) {
            for (int i = 0; i < arrList.size(); i++) {
                gpList.add(addElementGridPane("l1" + (i + 1), arrList.get(i).getId() + "", Pos.CENTER), 0, (i + 1));
                gpList.add(addElementGridPane("l2" + (i + 1), arrList.get(i).getClave() + "\n" + arrList.get(i).getNombreMarca(), Pos.CENTER_LEFT), 1, (i + 1));
                gpList.add(addElementGridPane("l3" + (i + 1), simboloMoneda + "" + Tools.roundingValue(arrList.get(i).getCostoCompra(), 2), Pos.CENTER_RIGHT), 2, (i + 1));
                gpList.add(addElementGridPane("l4" + (i + 1), Tools.roundingValue(arrList.get(i).getDescuento(), 2) + "%", Pos.CENTER_RIGHT), 3, (i + 1));
                //gpList.add(addElementGridPane("l5" + (i + 1), Tools.roundingValue(arrList.get(i).getImpuestoValor(), 2) + "%", Pos.CENTER_RIGHT), 4, (i + 1));
                gpList.add(addElementGridPane("l5" + (i + 1), Tools.roundingValue(arrList.get(i).getCantidad(), 2), Pos.CENTER_RIGHT), 4, (i + 1));
                gpList.add(addElementGridPane("l6" + (i + 1), arrList.get(i).getUnidadCompraName(), Pos.CENTER_RIGHT), 5, (i + 1));
                gpList.add(addElementGridPane("l7" + (i + 1), simboloMoneda + "" + Tools.roundingValue(arrList.get(i).getTotalImporte(), 2), Pos.CENTER_RIGHT), 6, (i + 1));
            }
            calcularTotales();
        }

    }

    private void calcularTotales() {
        if (arrList != null) {
            hbAgregarImpuesto.getChildren().clear();

            arrList.forEach(e -> subImporte += e.getSubImporte());
            lblSubTotal.setText(simboloMoneda + " " + Tools.roundingValue(subImporte, 2));
            subImporte = 0;

            arrList.forEach(e -> descuento += e.getDescuentoSumado());
            lblDescuento.setText(simboloMoneda + " -" + Tools.roundingValue(descuento, 2));
            descuento = 0;

            arrList.forEach(e -> subTotalImporte += e.getSubImporteDescuento());
            lblSubTotalNuevo.setText(simboloMoneda + " " + Tools.roundingValue(subTotalImporte, 2));
            subTotalImporte = 0;

//            boolean addElement = false;
//            double sumaElement = 0;
//            double totalImpuestos = 0;
//            for (int k = 0; k < arrayArticulos.size(); k++) {
//                for (int i = 0; i < arrList.size(); i++) {
//                    if (arrayArticulos.get(k).getIdImpuesto() == arrList.get(i).getImpuestoArticulo()) {
//                        addElement = true;
//                        sumaElement += arrList.get(i).getImpuestoSumado();
//                    }
//                }
//                if (addElement) {
//                    addElementImpuesto(arrayArticulos.get(k).getIdImpuesto() + "", arrayArticulos.get(k).getNombre(), simboloMoneda + " " + Tools.roundingValue(sumaElement, 2));
//                    totalImpuestos += sumaElement;
//                    addElement = false;
//                    sumaElement = 0;
//                }
//            }
            arrList.forEach(e -> totalImporte += e.getTotalImporte());
//            lblTotal.setText(simboloMoneda + " " + Tools.roundingValue((totalImporte + totalImpuestos), 2));
            lblTotal.setText(simboloMoneda + " " + Tools.roundingValue((totalImporte), 2));
            totalImporte = 0;
        }
    }

    private void addElementImpuesto(String id, String titulo, String total) {
        Text text = new Text(titulo);
        text.setStyle("-fx-fill:#020203;");
        text.getStyleClass().add("labelOpenSansRegular14");

        Text text1 = new Text(total);
        text1.setStyle("-fx-fill:#1976d2;");
        text1.getStyleClass().add("labelOpenSansRegular14");

        HBox hBox = new HBox(text, text1);
        hBox.setStyle("-fx-padding: 0.5em 0  0.5em 0;-fx-spacing:1em");
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setId(id);

        hbAgregarImpuesto.getChildren().add(hBox);
    }

    private void onEventReporte() {
        try {

            InputStream dir = getClass().getResourceAsStream("/report/DetalleCompra.jasper");
            InputStream imgInputStream
                    = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(dir);
            Map map = new HashMap();
            map.put("IDCOMPRA", idCompra);
            map.put("EMPRESA", Session.COMPANY_RAZON_SOCIAL);
            map.put("LOGO", imgInputStream);
            map.put("EMAIL", "EMAIL" + Session.COMPANY_EMAIL);
            map.put("TELEFONOCELULAR", "TEL:" + Session.COMPANY_TELEFONO + " CEL:" + Session.COMPANY_CELULAR);
            map.put("DIRECCION", Session.COMPANY_DOMICILIO);

            map.put("FECHACOMPRA", lblFechaCompra.getText());
            map.put("PROVEEDOR", lblProveedor.getText());
            map.put("PRODIRECCION", lblDomicilio.getText());
            map.put("PROTELEFONOCELULAR", lblContacto.getText());
            map.put("PROEMAIL", Session.COMPANY_EMAIL);
            map.put("TOTAL", lblTotal.getText());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JRBeanCollectionDataSource(arrList));

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setIconImage(new ImageIcon(getClass().getResource(FilesRouters.IMAGE_ICON)).getImage());
            jasperViewer.setTitle("Detalle de compra");
            jasperViewer.setSize(840, 650);
            jasperViewer.setLocationRelativeTo(null);
            jasperViewer.setVisible(true);

        } catch (HeadlessException | JRException | ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            System.out.println("Error al generar el reporte : " + ex);
        }
    }

    private void openWindowHistorialPagos() throws IOException {
        ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
        URL url = getClass().getResource(FilesRouters.FX_HISTORIAL_PAGOS);
        FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
        Parent parent = fXMLLoader.load(url.openStream());

        FxHistorialPagosController controller = fXMLLoader.getController();
        controller.setInitHistorialPagosController(this, idCompra, total, simboloMoneda);

        Stage stage = WindowStage.StageLoaderModal(parent, "Historial de Pagos", cpWindow.getScene().getWindow());
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setOnHiding((w) -> {
            vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            setLoadDetalle(idCompra);
        });
        stage.show();
        controller.initListHistorialPagos();
    }

    private void eventEditarVenta() {
        
        if(estadoCompra.equals("anulado".toUpperCase())){
            if (!idCompra.equalsIgnoreCase("") || idCompra != null) {
                try {
                    ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                    URL url = getClass().getResource(FilesRouters.FX_COMPRAS_EDITAR);
                    FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                    Parent parent = fXMLLoader.load(url.openStream());
                    //Controlller here
                    FxComprasEditarController controller = fXMLLoader.getController();
                    controller.setInitComprasValue(idCompra);
                    //
                    Stage stage = WindowStage.StageLoaderModal(parent, "Editar su compra", cpWindow.getScene().getWindow());
                    stage.setResizable(false);
                    stage.sizeToScene();
                    stage.setOnHiding((w) -> {
                        vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                    });
                    stage.show();
                } catch (IOException ex) {
                    System.out.println("Controller compras" + ex.getLocalizedMessage());
                }
            }
        }else{
            Tools.AlertMessageWarning(cpWindow, "Detalle de Compra", "Debe anular la compra para poder editar.");
        }
    }

    private void eventCancelarVenta() {
        if (!idCompra.equalsIgnoreCase("") || idCompra != null) {

            try {
                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                URL url = getClass().getResource(FilesRouters.FX_COMPRAS_CANCELAR);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxComprasCancelarController controller = fXMLLoader.getController();
                controller.setTableList(arrList);
                controller.setIdCompra(idCompra);
                //
                Stage stage = WindowStage.StageLoaderModal(parent, "Cancelar su compra", cpWindow.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.setOnHiding((w) -> {
                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                });
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
        vbContent.getChildren().remove(cpWindow);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(comprascontroller.getWindow(), 0d);
        AnchorPane.setTopAnchor(comprascontroller.getWindow(), 0d);
        AnchorPane.setRightAnchor(comprascontroller.getWindow(), 0d);
        AnchorPane.setBottomAnchor(comprascontroller.getWindow(), 0d);
        vbContent.getChildren().add(comprascontroller.getWindow());
    }

    @FXML
    private void onActionHistorialPagos(ActionEvent event) throws IOException {
        if (tipoCompra.equals("credito".toUpperCase())) {
            openWindowHistorialPagos();
        } else {
            Tools.AlertMessage(cpWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Detalle de Compra", "La compra se realizó al contado.", false);
        }

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
