package controller.operaciones.venta;

import controller.inventario.suministros.FxSuministrosProcesoModalController;
import controller.menus.FxPrincipalController;
import controller.tools.*;

import java.awt.print.Book;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.ClienteTB;
import model.ComprobanteADO;
import model.MonedaADO;
import model.MonedaTB;
import model.SuministroADO;
import model.SuministroTB;
import model.TipoDocumentoADO;
import model.TipoDocumentoTB;
import model.VentaTB;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;
import model.ClienteADO;
import model.DetalleADO;
import model.DetalleTB;
import model.PrivilegioTB;
import model.VentaADO;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.json.simple.JSONObject;

public class FxVentaEstructuraNuevoController implements Initializable {

    @FXML
    private VBox vbWindow;
    @FXML
    private FlowPane fpProductos;
    @FXML
    private ListView<BbItemProducto> lvProductoAgregados;
    @FXML
    private Label lblPaginaActual;
    @FXML
    private Label lblPaginaSiguiente;
    @FXML
    private TextField txtSearch;
    @FXML
    private Label lblTotal;
    @FXML
    private ComboBox<MonedaTB> cbMoneda;
    @FXML
    private ComboBox<TipoDocumentoTB> cbComprobante;
    @FXML
    private Text lblSerie;
    @FXML
    private Text lblNumeracion;
    @FXML
    private ComboBox<DetalleTB> cbTipoDocumento;
    @FXML
    private TextField txtNumeroDocumento;
    @FXML
    private TextField txtDatosCliente;
    @FXML
    private TextField txtCelularCliente;
    @FXML
    private TextField txtCorreoElectronico;
    @FXML
    private TextField txtDireccionCliente;
    @FXML
    private VBox vbBodyCliente;
    @FXML
    private HBox hbLoadCliente;
    @FXML
    private Button btnCancelarProceso;

    private FxPrincipalController fxPrincipalController;

    private BillPrintable billPrintable;

    private ConvertMonedaCadena monedaCadena;

    private ObservableList<SuministroTB> listSuministros;

    private AnchorPane hbEncabezado;

    private AnchorPane hbDetalleCabecera;

    private AnchorPane hbPie;

    private String monedaSimbolo;

    private String idCliente;

    private AutoCompletionBinding<String> autoCompletionBinding;

    private final Set<String> posiblesWord = new HashSet<>();

    private boolean vender_con_cantidades_negativas;

    private int totalPaginacion;

    private int paginacion;

    private short opcion;

    private boolean state;

    private double importeBruto;

    private double descuentoBruto;

    private double subImporteNeto;

    private double impuestoNeto;

    private double importeNeto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listSuministros = FXCollections.observableArrayList();
        billPrintable = new BillPrintable();
        monedaCadena = new ConvertMonedaCadena();
        hbEncabezado = new AnchorPane();
        hbDetalleCabecera = new AnchorPane();
        hbPie = new AnchorPane();
        paginacion = 0;
        totalPaginacion = 0;
        opcion = 0;
        state = false;
        monedaSimbolo = "M";
        idCliente = "";
        loadDataComponent();
        autoCompletionBinding = TextFields.bindAutoCompletion(txtNumeroDocumento, posiblesWord);
        autoCompletionBinding.setOnAutoCompleted(e -> {
            if (!Tools.isText(txtNumeroDocumento.getText())) {
                onExecuteCliente((short) 2, txtNumeroDocumento.getText().trim());
                txtSearch.requestFocus();
            }
        });
    }

    private void loadDataComponent() {
        cbComprobante.getItems().clear();
        TipoDocumentoADO.GetDocumentoCombBox().forEach(e -> cbComprobante.getItems().add(e));
        if (!cbComprobante.getItems().isEmpty()) {
            for (int i = 0; i < cbComprobante.getItems().size(); i++) {
                if (cbComprobante.getItems().get(i).isPredeterminado()) {
                    cbComprobante.getSelectionModel().select(i);
                    break;
                }
            }

            if (cbComprobante.getSelectionModel().getSelectedIndex() >= 0) {
                String[] array = ComprobanteADO.GetSerieNumeracionEspecifico(cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento()).split("-");
                lblSerie.setText(array[0]);
                lblNumeracion.setText(array[1]);
            }
        }

        cbMoneda.getItems().clear();
        MonedaADO.GetMonedasCombBox().forEach(e -> cbMoneda.getItems().add(new MonedaTB(e.getIdMoneda(), e.getNombre(), e.getSimbolo(), e.getPredeterminado())));

        if (!cbMoneda.getItems().isEmpty()) {
            for (int i = 0; i < cbMoneda.getItems().size(); i++) {
                if (cbMoneda.getItems().get(i).getPredeterminado()) {
                    cbMoneda.getSelectionModel().select(i);
                    monedaSimbolo = cbMoneda.getItems().get(i).getSimbolo();
                    break;
                }
            }
        }

        cbTipoDocumento.getItems().clear();
        DetalleADO.GetDetailId("0003").forEach(e -> cbTipoDocumento.getItems().add(e));

        idCliente = Session.CLIENTE_ID;
        txtNumeroDocumento.setText(Session.CLIENTE_NUMERO_DOCUMENTO);
        txtDatosCliente.setText(Session.CLIENTE_DATOS);
        txtCelularCliente.setText(Session.CLIENTE_CELULAR);
        txtCorreoElectronico.setText(Session.CLIENTE_EMAIL);
        txtDireccionCliente.setText(Session.CLIENTE_DIRECCION);

        ObjectGlobal.DATA_CLIENTS.forEach(c -> posiblesWord.add(c));

        if (!cbTipoDocumento.getItems().isEmpty()) {
            for (DetalleTB detalleTB : cbTipoDocumento.getItems()) {
                if (detalleTB.getIdDetalle() == Session.CLIENTE_TIPO_DOCUMENTO) {
                    cbTipoDocumento.getSelectionModel().select(detalleTB);
                    break;
                }
            }
        }
    }

    public void loadPrivilegios(ObservableList<PrivilegioTB> privilegioTBs) {
        vender_con_cantidades_negativas = privilegioTBs.get(34).getIdPrivilegio() != 0 && !privilegioTBs.get(34).isEstado();
    }

    public void onEventPaginacion() {
        switch (opcion) {
            case 0:
                fillSuministrosTable((short) 0, "");
                break;
            case 1:
                fillSuministrosTable((short) 1, txtSearch.getText().trim());
                break;
        }
    }

    private void fillSuministrosTable(short tipo, String value) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<Object> task = new Task<Object>() {
            @Override
            public Object call() {
                return SuministroADO.ListSuministrosListaView(tipo, value, (paginacion - 1) * 20, 20);
            }
        };

        task.setOnSucceeded(e -> {
            Object object = task.getValue();
            if (object instanceof Object[]) {
                Object[] objects = (Object[]) object;
                fpProductos.getChildren().clear();
                fpProductos.setAlignment(Pos.TOP_CENTER);
                listSuministros.addAll((ObservableList<SuministroTB>) objects[0]);
                if (!listSuministros.isEmpty()) {
                    for (SuministroTB tvList1 : listSuministros) {
                        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
                        PPI display = new PPI("" + screenBounds.getWidth(), "" + screenBounds.getHeight(), "" + 96);
                        double resultNumber = display.calc();
                        double dpi = resultNumber * 8;

                        VBox vBox = new VBox();
                        ImageView imageView = new ImageView(new Image("/view/image/no-image.png"));
                        if (tvList1.getNuevaImagen() != null) {
                            imageView = new ImageView(new Image(new ByteArrayInputStream(tvList1.getNuevaImagen())));
                        }
                        imageView.setFitWidth(dpi * 1.3);
                        imageView.setFitHeight(dpi * 1.2);
                        vBox.getChildren().add(imageView);

//                    Label lblCodigo = new Label(tvList1.getClave());
//                    lblCodigo.getStyleClass().add("labelOpenSansRegular13");
//                    lblCodigo.setTextFill(Color.web("#020203"));
//                    vBox.getChildren().add(lblCodigo);
                        Label lblProducto = new Label(tvList1.getNombreMarca());
                        lblProducto.getStyleClass().add("labelRobotoBold15");
                        lblProducto.setTextFill(Color.web("#020203"));
//                    lblProducto.setWrapText(true);
                        lblProducto.setTextAlignment(TextAlignment.CENTER);
                        lblProducto.setAlignment(Pos.CENTER);
                        lblProducto.setMinWidth(Control.USE_PREF_SIZE);
                        lblProducto.setPrefWidth(dpi * 1.3);
//                    lblProducto.setMaxWidth(Double.MAX_VALUE);
//                    VBox.setVgrow(lblProducto, Priority.ALWAYS);
                        vBox.getChildren().add(lblProducto);

                        Label lblMarca = new Label(Tools.isText(tvList1.getMarcaName()) ? "No Marca" : tvList1.getMarcaName());
                        lblMarca.getStyleClass().add("labelOpenSansRegular13");
                        lblMarca.setTextFill(Color.web("#1a2226"));
                        vBox.getChildren().add(lblMarca);

                        Label lblCantidad = new Label(Tools.roundingValue(tvList1.getCantidad(), 2));
                        lblCantidad.getStyleClass().add("labelRobotoBold17");
                        lblCantidad.setTextFill(tvList1.getCantidad() <= 0 ? Color.web("#e40000") : Color.web("#117cee"));
                        vBox.getChildren().add(lblCantidad);

                        Label lblTotalProducto = new Label(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(tvList1.getPrecioVentaGeneral(), 2));
                        lblTotalProducto.getStyleClass().add("labelRobotoBold19");
                        lblTotalProducto.setTextFill(Color.web("#009a1e"));
                        lblTotalProducto.maxWidth(Double.MAX_VALUE);
                        vBox.getChildren().add(lblTotalProducto);

                        vBox.setStyle("-fx-spacing: 0.4166666666666667em;-fx-padding: 0.4166666666666667em;-fx-border-color: #0478b2;-fx-border-width:2px;-fx-background-color:transparent;");
                        vBox.setAlignment(Pos.TOP_CENTER);
                        vBox.setMinWidth(Control.USE_COMPUTED_SIZE);
                        vBox.setPrefWidth(dpi * 1.5);
                        vBox.maxWidth(Control.USE_COMPUTED_SIZE);

                        Button button = new Button();
                        button.getStyleClass().add("buttonView");
                        button.setGraphic(vBox);
                        button.setOnAction(event -> {
                            addElementListView(tvList1);
                        });
                        button.setOnKeyPressed(event -> {
                            if (event.getCode() == KeyCode.ENTER) {
                                addElementListView(tvList1);
                            }
                        });

                        fpProductos.getChildren().add(button);

                    }
                    totalPaginacion = (int) (Math.ceil(((Integer) objects[1]) / 20.00));
                    lblPaginaActual.setText(paginacion + "");
                    lblPaginaSiguiente.setText(totalPaginacion + "");
                } else {
                    fpProductos.getChildren().clear();
                    fpProductos.setAlignment(Pos.CENTER);
                    Label lblLoad = new Label("No hay datos para mostrar.");
                    lblLoad.getStyleClass().add("labelRoboto15");
                    lblLoad.setTextFill(Color.web("#1a2226"));
                    lblLoad.setContentDisplay(ContentDisplay.TOP);
                    fpProductos.getChildren().add(lblLoad);
                }
            } else if (object instanceof String) {
                fpProductos.getChildren().clear();
                fpProductos.setAlignment(Pos.CENTER);
                Label lblLoad = new Label((String) object);
                lblLoad.getStyleClass().add("labelRoboto15");
                lblLoad.setTextFill(Color.web("#a70820"));
                lblLoad.setContentDisplay(ContentDisplay.TOP);
                fpProductos.getChildren().add(lblLoad);
            } else {
                fpProductos.getChildren().clear();
                fpProductos.setAlignment(Pos.CENTER);
                Label lblLoad = new Label("Error en traer los datos, intente nuevamente.");
                lblLoad.getStyleClass().add("labelRoboto15");
                lblLoad.setTextFill(Color.web("#a70820"));
                lblLoad.setContentDisplay(ContentDisplay.TOP);
                fpProductos.getChildren().add(lblLoad);
            }
            state = false;
        });
        task.setOnFailed(e -> {
            state = false;
            fpProductos.getChildren().clear();
            fpProductos.setAlignment(Pos.CENTER);
            Label lblLoad = new Label(task.getMessage());
            lblLoad.getStyleClass().add("labelRoboto15");
            lblLoad.setTextFill(Color.web("#a70820"));
            lblLoad.setContentDisplay(ContentDisplay.TOP);
            fpProductos.getChildren().add(lblLoad);
        });
        task.setOnScheduled(e -> {
            state = true;
            listSuministros.clear();
            fpProductos.getChildren().clear();
            fpProductos.setAlignment(Pos.CENTER);
            Label lblLoad = new Label("Cargando productos para su venta");
            lblLoad.getStyleClass().add("labelRoboto15");
            lblLoad.setTextFill(Color.web("#1a2226"));
            ProgressIndicator progressIndicator = new ProgressIndicator();
            progressIndicator.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            lblLoad.setContentDisplay(ContentDisplay.TOP);
            lblLoad.setGraphic(progressIndicator);
            fpProductos.getChildren().add(lblLoad);
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void addElementListView(SuministroTB a) {
        if (vender_con_cantidades_negativas && a.getCantidad() <= 0) {
            Tools.AlertMessageWarning(vbWindow, "Producto", "No puede agregar el producto ya que tiene la cantidad menor que 0.");
            return;
        }
        SuministroTB suministroTB = new SuministroTB();
        suministroTB.setIdSuministro(a.getIdSuministro());
        suministroTB.setClave(a.getClave());
        suministroTB.setNombreMarca(a.getNombreMarca());
        suministroTB.setCantidad(1);
        suministroTB.setCostoCompra(a.getCostoCompra());
        suministroTB.setBonificacion(0);

        double valor_sin_impuesto = a.getPrecioVentaGeneral() / ((a.getImpuestoValor() / 100.00) + 1);
        double descuento = suministroTB.getDescuento();
        double porcentajeRestante = valor_sin_impuesto * (descuento / 100.00);
        double preciocalculado = valor_sin_impuesto - porcentajeRestante;

        suministroTB.setDescuento(0);
        suministroTB.setDescuentoCalculado(0);
        suministroTB.setDescuentoSumado(0);

        suministroTB.setPrecioVentaGeneralUnico(valor_sin_impuesto);
        suministroTB.setPrecioVentaGeneralReal(preciocalculado);

        suministroTB.setImpuestoOperacion(a.getImpuestoOperacion());
        suministroTB.setImpuestoId(a.getImpuestoId());
        suministroTB.setImpuestoNombre(a.getImpuestoNombre());
        suministroTB.setImpuestoValor(a.getImpuestoValor());

        double impuesto = Tools.calculateTax(suministroTB.getImpuestoValor(), suministroTB.getPrecioVentaGeneralReal());
        suministroTB.setImpuestoSumado(suministroTB.getCantidad() * impuesto);
        suministroTB.setPrecioVentaGeneral(suministroTB.getPrecioVentaGeneralReal() + impuesto);
        suministroTB.setPrecioVentaGeneralAuxiliar(suministroTB.getPrecioVentaGeneral());

        suministroTB.setImporteBruto(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralUnico());
        suministroTB.setSubImporteNeto(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
        suministroTB.setImporteNeto(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneral());

        suministroTB.setInventario(a.isInventario());
        suministroTB.setUnidadVenta(a.getUnidadVenta());
        suministroTB.setValorInventario(a.getValorInventario());

        addProducto(suministroTB);
    }

    private void addProducto(SuministroTB suministroTB) {
        if (validateDuplicateArticulo(lvProductoAgregados, suministroTB)) {
            for (int i = 0; i < lvProductoAgregados.getItems().size(); i++) {
                if (lvProductoAgregados.getItems().get(i).getSuministroTB().getIdSuministro().equalsIgnoreCase(suministroTB.getIdSuministro())) {
                    BbItemProducto bbItemProducto = lvProductoAgregados.getItems().get(i);
                    bbItemProducto.getSuministroTB().setCantidad(bbItemProducto.getSuministroTB().getCantidad() + 1);
                    double porcentajeRestante = bbItemProducto.getSuministroTB().getPrecioVentaGeneralUnico() * (bbItemProducto.getSuministroTB().getDescuento() / 100.00);

                    bbItemProducto.getSuministroTB().setDescuentoSumado(porcentajeRestante * bbItemProducto.getSuministroTB().getCantidad());
                    bbItemProducto.getSuministroTB().setImpuestoSumado(bbItemProducto.getSuministroTB().getCantidad() * (bbItemProducto.getSuministroTB().getPrecioVentaGeneralReal() * (bbItemProducto.getSuministroTB().getImpuestoValor() / 100.00)));

                    bbItemProducto.getSuministroTB().setImporteBruto(bbItemProducto.getSuministroTB().getCantidad() * bbItemProducto.getSuministroTB().getPrecioVentaGeneralUnico());
                    bbItemProducto.getSuministroTB().setSubImporteNeto(bbItemProducto.getSuministroTB().getCantidad() * bbItemProducto.getSuministroTB().getPrecioVentaGeneralReal());
                    bbItemProducto.getSuministroTB().setImporteNeto(bbItemProducto.getSuministroTB().getCantidad() * bbItemProducto.getSuministroTB().getPrecioVentaGeneral());
                    bbItemProducto.getChildren().clear();
                    bbItemProducto.addElementListView();

                    lvProductoAgregados.getItems().set(i, bbItemProducto);
                    calculateTotales();
                }
            }
        } else {
            BbItemProducto bbItemProducto = new BbItemProducto(suministroTB, lvProductoAgregados, this);
            bbItemProducto.addElementListView();
            lvProductoAgregados.getItems().add(bbItemProducto);
            calculateTotales();
        }
    }

    private boolean validateDuplicateArticulo(ListView<BbItemProducto> view, SuministroTB suministroTB) {
        boolean ret = false;
        for (int i = 0; i < view.getItems().size(); i++) {
            if (view.getItems().get(i).getSuministroTB().getClave().equals(suministroTB.getClave())) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    private void searchTable(KeyEvent event, String value) {
        if (event.getCode() != KeyCode.ESCAPE
                && event.getCode() != KeyCode.F1
                && event.getCode() != KeyCode.F2
                && event.getCode() != KeyCode.F3
                && event.getCode() != KeyCode.F4
                && event.getCode() != KeyCode.F5
                && event.getCode() != KeyCode.F6
                && event.getCode() != KeyCode.F7
                && event.getCode() != KeyCode.F8
                && event.getCode() != KeyCode.F9
                && event.getCode() != KeyCode.F10
                && event.getCode() != KeyCode.F11
                && event.getCode() != KeyCode.F12
                && event.getCode() != KeyCode.ALT
                && event.getCode() != KeyCode.CONTROL
                && event.getCode() != KeyCode.UP
                && event.getCode() != KeyCode.DOWN
                && event.getCode() != KeyCode.RIGHT
                && event.getCode() != KeyCode.LEFT
                && event.getCode() != KeyCode.TAB
                && event.getCode() != KeyCode.CAPS
                && event.getCode() != KeyCode.SHIFT
                && event.getCode() != KeyCode.HOME
                && event.getCode() != KeyCode.WINDOWS
                && event.getCode() != KeyCode.ALT_GRAPH
                && event.getCode() != KeyCode.CONTEXT_MENU
                && event.getCode() != KeyCode.END
                && event.getCode() != KeyCode.INSERT
                && event.getCode() != KeyCode.PAGE_UP
                && event.getCode() != KeyCode.PAGE_DOWN
                && event.getCode() != KeyCode.NUM_LOCK
                && event.getCode() != KeyCode.PRINTSCREEN
                && event.getCode() != KeyCode.SCROLL_LOCK
                && event.getCode() != KeyCode.PAUSE) {
            if (!state) {
                paginacion = 1;
                fillSuministrosTable((short) 1, value);
                opcion = 1;
            }
        }
    }

    public void calculateTotales() {

        importeBruto = 0;
        lvProductoAgregados.getItems().forEach(e -> importeBruto += e.getSuministroTB().getImporteBruto());
//        lblValorVenta.setText(monedaSimbolo + " " + Tools.roundingValue(subTotal, 2));

        descuentoBruto = 0;
        lvProductoAgregados.getItems().forEach(e -> descuentoBruto += e.getSuministroTB().getDescuentoSumado());
//        lblDescuento.setText(monedaSimbolo + " " + (Tools.roundingValue(descuento * (-1), 2)));

        subImporteNeto = 0;
        lvProductoAgregados.getItems().forEach(e -> subImporteNeto += e.getSuministroTB().getSubImporteNeto());
//        lblSubImporte.setText(monedaSimbolo + " " + Tools.roundingValue(subTotalImporte, 2));

//        gpTotales.getChildren().clear();
//
//        boolean addElement = false;
//        double sumaElement = 0;
//        double totalImpuestos = 0;
//        if (!lvProductoAgregados.getItems().isEmpty()) {
//            for (ImpuestoTB impuestoTB : arrayArticulosImpuesto) {
//                for (int i = 0; i < lvProductoAgregados.getItems().size(); i++) {
//                    if (impuestoTB.getIdImpuesto() == lvProductoAgregados.getItems().get(i).getSuministroTB().getImpuestoId()) {
//                        addElement = true;
//                        sumaElement += lvProductoAgregados.getItems().get(i).getSuministroTB().getImpuestoSumado();
//                    }
//                }
//                if (addElement) {
////                    gpTotales.add(addLabelTitle(arrayArticulosImpuesto.get(k).getNombreImpuesto().substring(0, 1).toUpperCase()
////                            + "" + arrayArticulosImpuesto.get(k).getNombreImpuesto().substring(1, arrayArticulosImpuesto.get(k).getNombreImpuesto().length()).toLowerCase(),
////                            Pos.CENTER_LEFT), 0, k + 1);
////                    gpTotales.add(addLabelTotal(monedaSimbolo + " " + Tools.roundingValue(sumaElement, 2), Pos.CENTER_RIGHT), 1, k + 1);
//                    totalImpuestos += sumaElement;
//
//                    addElement = false;
//                    sumaElement = 0;
//                }
//            }
//        }
        double totalImpuestos = 0;
        impuestoNeto = 0;
        if (!lvProductoAgregados.getItems().isEmpty()) {
            for (int i = 0; i < lvProductoAgregados.getItems().size(); i++) {
                totalImpuestos += lvProductoAgregados.getItems().get(i).getSuministroTB().getImpuestoSumado();
            }
        }
        impuestoNeto = totalImpuestos;

        importeNeto = 0;
        importeNeto = subImporteNeto + impuestoNeto;
        lblTotal.setText(monedaSimbolo + " " + Tools.roundingValue(importeNeto, 2));

    }

    public void resetVenta() {
        paginacion = 0;
        totalPaginacion = 0;
        opcion = 0;
        state = false;
        listSuministros.clear();
        fpProductos.getChildren().clear();
        lvProductoAgregados.getItems().clear();

        loadDataComponent();
        lblPaginaActual.setText(paginacion + "");
        lblPaginaSiguiente.setText(totalPaginacion + "");
        lblTotal.setText(monedaSimbolo + " 0.00");
        txtSearch.clear();
        txtSearch.requestFocus();
        calculateTotales();
    }

    private JasperPrint reportA4(VentaTB ventaTB, ArrayList<SuministroTB> list) throws JRException {

        InputStream imgInputStreamIcon = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

        InputStream imgInputStream = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

        if (Session.COMPANY_IMAGE != null) {
            imgInputStream = new ByteArrayInputStream(Session.COMPANY_IMAGE);
        }

        InputStream dir = getClass().getResourceAsStream("/report/VentaRealizada.jasper");

        Map map = new HashMap();
        map.put("LOGO", imgInputStream);
        map.put("ICON", imgInputStreamIcon);
        map.put("EMPRESA", Session.COMPANY_RAZON_SOCIAL);
        map.put("DIRECCION", Session.COMPANY_DOMICILIO);
        map.put("TELEFONOCELULAR", "TELÉFONO: " + Session.COMPANY_TELEFONO + " CELULAR: " + Session.COMPANY_CELULAR);
        map.put("EMAIL", "EMAIL: " + Session.COMPANY_EMAIL);

        map.put("DOCUMENTOEMPRESA", "R.U.C " + Session.COMPANY_NUMERO_DOCUMENTO);
        map.put("NOMBREDOCUMENTO", ventaTB.getComprobanteName());
        map.put("NUMERODOCUMENTO", ventaTB.getSerie() + "-" + ventaTB.getNumeracion());

        map.put("DATOSCLIENTE", ventaTB.getClienteTB().getInformacion());
        map.put("DOCUMENTOCLIENTE", ventaTB.getClienteTB().getTipoDocumentoName() + " N°:");
        map.put("NUMERODOCUMENTOCLIENTE", ventaTB.getClienteTB().getNumeroDocumento());
        map.put("CELULARCLIENTE", ventaTB.getClienteTB().getCelular().equals("") ? "---" : ventaTB.getClienteTB().getCelular());
        map.put("EMAILCLIENTE", ventaTB.getClienteTB().getEmail().equals("") ? "---" : ventaTB.getClienteTB().getEmail());
        map.put("DIRECCIONCLIENTE", ventaTB.getClienteTB().getDireccion().equals("") ? "---" : ventaTB.getClienteTB().getDireccion());

        map.put("FECHAEMISION", ventaTB.getFechaVenta());
        map.put("MONEDA", ventaTB.getMonedaTB().getNombre() + "-" + ventaTB.getMonedaTB().getAbreviado());
        map.put("CONDICIONPAGO", ventaTB.getTipoName() + "-" + ventaTB.getEstadoName());

        map.put("SIMBOLO", ventaTB.getMonedaTB().getSimbolo());
        map.put("VALORSOLES", monedaCadena.Convertir(Tools.roundingValue(ventaTB.getTotal(), 2), true, ventaTB.getMonedaTB().getNombre()));

        map.put("VALOR_VENTA", ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(ventaTB.getSubTotal(), 2));
        map.put("DESCUENTO", ventaTB.getMonedaTB().getSimbolo() + " -" + Tools.roundingValue(ventaTB.getDescuento(), 2));
        map.put("SUB_IMPORTE", ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(ventaTB.getSubImporte(), 2));
        map.put("IMPUESTO_TOTAL", Tools.roundingValue(ventaTB.getImpuesto(), 2));
        map.put("IMPORTE_TOTAL", ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(ventaTB.getTotal(), 2));
        map.put("QRDATA", Session.COMPANY_NUMERO_DOCUMENTO + "|" + ventaTB.getCodigoAlterno() + "|" + ventaTB.getSerie() + "|" + ventaTB.getNumeracion() + "|" + Tools.roundingValue(ventaTB.getImpuesto(), 2) + "|" + Tools.roundingValue(importeNeto, 2) + "|" + ventaTB.getFechaVenta() + "|" + ventaTB.getClienteTB().getIdAuxiliar() + "|" + ventaTB.getClienteTB().getNumeroDocumento() + "|");

        JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JRBeanCollectionDataSource(list));
        return jasperPrint;
    }

    public void imprimirVenta(String idVenta) {
        if (!Session.ESTADO_IMPRESORA_VENTA && Tools.isText(Session.NOMBRE_IMPRESORA_VENTA) && Tools.isText(Session.FORMATO_IMPRESORA_VENTA)) {
            Tools.AlertMessageWarning(vbWindow, "Venta", "No esta configurado la ruta de impresión, ve a la sección configuración/impresora.");
            return;
        }

        if (Session.FORMATO_IMPRESORA_VENTA.equalsIgnoreCase("ticket")) {
            if (Session.TICKET_VENTA_ID == 0 && Tools.isText(Session.TICKET_VENTA_RUTA)) {
                Tools.AlertMessageWarning(vbWindow, "Venta", "No hay un diseño predeterminado para la impresión, configure su ticket en la sección configuración/tickets.");
            } else {
                executeProcessPrinterVenta(idVenta, Session.NOMBRE_IMPRESORA_VENTA, Session.CORTAPAPEL_IMPRESORA_VENTA, Session.FORMATO_IMPRESORA_VENTA);
            }
        } else if (Session.FORMATO_IMPRESORA_VENTA.equalsIgnoreCase("a4")) {
            executeProcessPrinterVenta(idVenta, Session.NOMBRE_IMPRESORA_VENTA, Session.CORTAPAPEL_IMPRESORA_VENTA, Session.FORMATO_IMPRESORA_VENTA);
        } else {
            Tools.AlertMessageWarning(vbWindow, "Venta", "Error al validar el formato de impresión, configure en la sección configuración/impresora.");
        }
    }

    private void executeProcessPrinterVenta(String idVenta, String printerName, boolean printerCut, String format) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<String> task = new Task<String>() {
            @Override
            public String call() {
                Object result = VentaADO.ListCompletaVentasDetalle(idVenta);
                try {
                    if (result instanceof VentaTB) {
                        VentaTB ventaTB = (VentaTB) result;
                        ObservableList<SuministroTB> suministroTBs = FXCollections.observableArrayList(ventaTB.getSuministroTBs());

                        if (format.equalsIgnoreCase("a4")) {
                            ArrayList<SuministroTB> list = new ArrayList();

                            for (int i = 0; i < suministroTBs.size(); i++) {
                                SuministroTB stb = new SuministroTB();
                                stb.setId(i + 1);
                                stb.setClave(suministroTBs.get(i).getClave());
                                stb.setNombreMarca(suministroTBs.get(i).getNombreMarca());
                                stb.setCantidad(suministroTBs.get(i).getCantidad());
                                stb.setUnidadCompraName(suministroTBs.get(i).getUnidadCompraName());
                                stb.setPrecioVentaGeneral(suministroTBs.get(i).getPrecioVentaGeneral());
                                stb.setDescuento(suministroTBs.get(i).getDescuento());
                                stb.setImporteNeto(suministroTBs.get(i).getCantidad() * +suministroTBs.get(i).getPrecioVentaGeneral());
                                list.add(stb);
                            }

                            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
                            printRequestAttributeSet.add(new Copies(1));

                            PrinterName pn = new PrinterName(printerName, null);

                            PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
                            printServiceAttributeSet.add(pn);

                            JRPrintServiceExporter exporter = new JRPrintServiceExporter();

                            exporter.setParameter(JRExporterParameter.JASPER_PRINT, reportA4(ventaTB, list));
                            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
                            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceAttributeSet);
                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
                            exporter.exportReport();
                            return "completed";
                        } else {
                            if (Session.DESING_IMPRESORA_VENTA.equalsIgnoreCase("withdesing")) {
                                billPrintable.loadEstructuraTicket(Session.TICKET_VENTA_ID, Session.TICKET_VENTA_RUTA, hbEncabezado, hbDetalleCabecera, hbPie);
                                ObjectGlobal.QR_PERU_DATA = "|" + Session.COMPANY_NUMERO_DOCUMENTO + "|" + ventaTB.getCodigoAlterno() + "|" + ventaTB.getSerie() + "|" + ventaTB.getNumeracion() + "|" + Tools.roundingValue(ventaTB.getImpuesto(), 2) + "|" + Tools.roundingValue(ventaTB.getTotal(), 2) + "|" + ventaTB.getFechaVenta() + "|" + ventaTB.getClienteTB().getIdAuxiliar() + "|" + ventaTB.getClienteTB().getNumeroDocumento() + "|";

                                for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
                                    HBox box = ((HBox) hbEncabezado.getChildren().get(i));
                                    billPrintable.hbEncebezado(box,
                                            ventaTB.getComprobanteName(),
                                            ventaTB.getSerie() + "-" + ventaTB.getNumeracion(),
                                            ventaTB.getClienteTB().getNumeroDocumento(),
                                            ventaTB.getClienteTB().getInformacion(),
                                            ventaTB.getClienteTB().getCelular(),
                                            ventaTB.getClienteTB().getDireccion(),
                                            ventaTB.getCodigo(),
                                            monedaCadena.Convertir(Tools.roundingValue(ventaTB.getTotal(), 2), true, ventaTB.getMonedaTB().getNombre()),
                                            ventaTB.getFechaVenta(),
                                            ventaTB.getHoraVenta(),
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "0",
                                            "0",
                                            "0",
                                            "0",
                                            "0");
                                }

                                AnchorPane hbDetalle = new AnchorPane();
                                for (int m = 0; m < suministroTBs.size(); m++) {
                                    for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                                        HBox hBox = new HBox();
                                        hBox.setId("dc_" + m + "" + i);
                                        HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                                        billPrintable.hbDetalle(hBox, box, suministroTBs, m);
                                        hbDetalle.getChildren().add(hBox);
                                    }
                                }

                                for (int i = 0; i < hbPie.getChildren().size(); i++) {
                                    HBox box = ((HBox) hbPie.getChildren().get(i));
                                    billPrintable.hbPie(box, ventaTB.getMonedaTB().getSimbolo(),
                                            Tools.roundingValue(ventaTB.getSubImporte(), 2),
                                            "-" + Tools.roundingValue(ventaTB.getDescuento(), 2),
                                            Tools.roundingValue(ventaTB.getSubImporte(), 2),
                                            Tools.roundingValue(ventaTB.getImpuesto(), 2),
                                            Tools.roundingValue(ventaTB.getSubImporte(), 2),
                                            Tools.roundingValue(ventaTB.getTotal(), 2),
                                            Tools.roundingValue(ventaTB.getTarjeta(), 2),
                                            Tools.roundingValue(ventaTB.getEfectivo(), 2),
                                            Tools.roundingValue(ventaTB.getVuelto(), 2),
                                            ventaTB.getClienteTB().getNumeroDocumento(),
                                            ventaTB.getClienteTB().getInformacion(), ventaTB.getCodigo(),
                                            ventaTB.getClienteTB().getCelular(),
                                            monedaCadena.Convertir(Tools.roundingValue(ventaTB.getTotal(), 2), true, ventaTB.getMonedaTB().getNombre()),
                                            "",
                                            "",
                                            "",
                                            "",
                                            "");
                                }

                                billPrintable.generatePDFPrint(hbEncabezado, hbDetalle, hbPie);

                                DocPrintJob job = billPrintable.findPrintService(printerName, PrinterJob.lookupPrintServices()).createPrintJob();

                                if (job != null) {
                                    PrinterJob pj = PrinterJob.getPrinterJob();
                                    pj.setPrintService(job.getPrintService());
                                    pj.setJobName(printerName);
                                    Book book = new Book();
                                    book.append(billPrintable, billPrintable.getPageFormat(pj));
                                    pj.setPageable(book);
                                    pj.print();
                                    if (printerCut) {
                                        billPrintable.printCortarPapel(printerName);
                                    }
                                    return "completed";
                                } else {
                                    return "error_name";
                                }
                            } else {
                                billPrintable.loadEstructuraTicket(Session.TICKET_VENTA_ID, Session.TICKET_VENTA_RUTA, hbEncabezado, hbDetalleCabecera, hbPie);
                                return imprimirSinFormatoVenta(ventaTB, suministroTBs, Session.NOMBRE_IMPRESORA_VENTA, Session.CORTAPAPEL_IMPRESORA_VENTA);
                            }
                        }
                    } else {
                        return (String) result;
                    }
                } catch (PrinterException | IOException | PrintException | JRException ex) {
                    return "Error en imprimir: " + ex.getLocalizedMessage();
                }
            }
        };

        task.setOnSucceeded(w -> {
            String result = task.getValue();
            if (result.equalsIgnoreCase("completed")) {
                Tools.showAlertNotification("/view/image/information_large.png",
                        "Envío de impresión",
                        "Se completo el proceso de impresión correctamente.",
                        Duration.seconds(5),
                        Pos.BOTTOM_RIGHT);
            } else if (result.equalsIgnoreCase("error_name")) {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "Error en encontrar el nombre de la impresión por problemas de puerto o driver.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else if (result.equalsIgnoreCase("no_config")) {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "Error al validar el tipo de impresión, cofigure nuevamente la impresora.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else if (result.equalsIgnoreCase("empty")) {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "No hay registros para mostrar en el reporte.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else {
                Tools.showAlertNotification("/view/image/error_large.png",
                        "Envío de impresión",
                        "Se producto un problema por problemas de la impresora\n" + result,
                        Duration.seconds(10),
                        Pos.CENTER);
            }
        });

        task.setOnFailed(w -> {
            Tools.showAlertNotification("/view/image/warning_large.png",
                    "Envío de impresión",
                    "Se produjo un problema en el proceso de envío, \n intente nuevamente o comuníquese con su proveedor del sistema.",
                    Duration.seconds(10),
                    Pos.BOTTOM_RIGHT);
        });

        task.setOnScheduled(w -> {
            Tools.showAlertNotification("/view/image/print.png",
                    "Envío de impresión",
                    "Se envió la impresión a la cola, este\n proceso puede tomar unos segundos.",
                    Duration.seconds(5),
                    Pos.BOTTOM_RIGHT);
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private String imprimirSinFormatoVenta(VentaTB ventaTB, ObservableList<SuministroTB> suministroTBs, String printerName, boolean printerCut) {
        ArrayList<HBox> object = new ArrayList<>();
        int rows = 0;
        int lines = 0;
        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
            object.add((HBox) hbEncabezado.getChildren().get(i));
            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
            rows++;
            lines += billPrintable.hbEncebezado(box,
                    ventaTB.getComprobanteName(),
                    ventaTB.getSerie() + "-" + ventaTB.getNumeracion(),
                    ventaTB.getClienteTB().getNumeroDocumento(),
                    ventaTB.getClienteTB().getInformacion(),
                    ventaTB.getClienteTB().getCelular(),
                    ventaTB.getClienteTB().getDireccion(),
                    ventaTB.getCodigo(),
                    monedaCadena.Convertir(Tools.roundingValue(ventaTB.getTotal(), 2), true, ventaTB.getMonedaTB().getNombre()),
                    ventaTB.getFechaVenta(),
                    ventaTB.getHoraVenta(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "0",
                    "0",
                    "0",
                    "0",
                    "0");
        }

        for (int m = 0; m < suministroTBs.size(); m++) {
            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                HBox hBox = new HBox();
                hBox.setId("dc_" + m + "" + i);
                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                rows++;
                lines += billPrintable.hbDetalle(hBox, box, suministroTBs, m);
                object.add(hBox);
            }
        }

        for (int i = 0; i < hbPie.getChildren().size(); i++) {
            object.add((HBox) hbPie.getChildren().get(i));
            HBox box = ((HBox) hbPie.getChildren().get(i));
            rows++;
            lines += billPrintable.hbPie(box, ventaTB.getMonedaTB().getSimbolo(),
                    Tools.roundingValue(ventaTB.getSubImporte(), 2),
                    "-" + Tools.roundingValue(ventaTB.getDescuento(), 2),
                    Tools.roundingValue(ventaTB.getSubImporte(), 2),
                    Tools.roundingValue(ventaTB.getImpuesto(), 2),
                    Tools.roundingValue(ventaTB.getSubImporte(), 2),
                    Tools.roundingValue(ventaTB.getTotal(), 2),
                    Tools.roundingValue(ventaTB.getTarjeta(), 2),
                    Tools.roundingValue(ventaTB.getEfectivo(), 2),
                    Tools.roundingValue(ventaTB.getVuelto(), 2),
                    ventaTB.getClienteTB().getNumeroDocumento(),
                    ventaTB.getClienteTB().getInformacion(), ventaTB.getCodigo(),
                    ventaTB.getClienteTB().getCelular(),
                    monedaCadena.Convertir(Tools.roundingValue(ventaTB.getTotal(), 2), true, ventaTB.getMonedaTB().getNombre()),
                    "",
                    "",
                    "",
                    "",
                    "");
        }
        return billPrintable.modelTicket(rows + lines + 1 + 10, lines, object, printerName, printerCut);
    }

    private void imprimirPreVenta() {
        if (!Session.ESTADO_IMPRESORA_PRE_VENTA && Tools.isText(Session.NOMBRE_IMPRESORA_PRE_VENTA) && Tools.isText(Session.FORMATO_IMPRESORA_PRE_VENTA)) {
            Tools.AlertMessageWarning(vbWindow, "Venta", "No esta configurado la ruta de impresión, ve a la sección configuración/impresora.");
            return;
        }

        if (Session.FORMATO_IMPRESORA_PRE_VENTA.equalsIgnoreCase("ticket")) {
            if (Session.TICKET_PRE_VENTA_ID == 0 && Tools.isText(Session.TICKET_PRE_VENTA_RUTA)) {
                Tools.AlertMessageWarning(vbWindow, "Venta", "No hay un diseño predeterminado para la impresión, configure su ticket en la sección configuración/tickets.");
            } else {
                executeProcessPrinterPreVenta(Session.NOMBRE_IMPRESORA_PRE_VENTA, Session.CORTAPAPEL_IMPRESORA_PRE_VENTA);
            }
        } else if (Session.FORMATO_IMPRESORA_PRE_VENTA.equalsIgnoreCase("a4")) {
//            executeProcessPrinterPreVenta(Session.NOMBRE_IMPRESORA_PRE_VENTA);
        } else {
            Tools.AlertMessageWarning(vbWindow, "Venta", "Error al validar el formato de impresión, configure en la sección configuración/impresora.");

        }
    }

    private void executeProcessPrinterPreVenta(String printerName, boolean printerCut) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<String> task = new Task<String>() {
            @Override
            public String call() throws PrintException {
                try {
                    String numeroDocumento = "";
                    String informacion = "";
                    String celular = "";
                    String email = "";
                    String direccion = "";

                    numeroDocumento = txtNumeroDocumento.getText().trim().length() == 0 ? "" : txtNumeroDocumento.getText().trim().toUpperCase();
                    informacion = txtDatosCliente.getText().trim().length() == 0 ? "" : txtDatosCliente.getText().trim().toUpperCase();
                    celular = txtCelularCliente.getText().trim().length() == 0 ? "" : txtCelularCliente.getText().trim().toUpperCase();
                    email = txtCorreoElectronico.getText().trim().length() == 0 ? "" : txtCorreoElectronico.getText().trim().toUpperCase();
                    direccion = txtDireccionCliente.getText().trim().length() == 0 ? "" : txtDireccionCliente.getText().trim().toUpperCase();

                    if (Session.DESING_IMPRESORA_PRE_VENTA.equalsIgnoreCase("withdesing")) {
                        billPrintable.loadEstructuraTicket(Session.TICKET_PRE_VENTA_ID, Session.TICKET_PRE_VENTA_RUTA, hbEncabezado, hbDetalleCabecera, hbPie);

                        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
                            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
                            billPrintable.hbEncebezado(box,
                                    "NOTA DE VENTA",
                                    "---",
                                    numeroDocumento,
                                    informacion,
                                    celular,
                                    direccion,
                                    "00000000",
                                    Tools.getDate(),
                                    Tools.getTime(),
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "0",
                                    "0",
                                    "0",
                                    "0",
                                    "0");
                        }

                        ObservableList<SuministroTB> observableList = FXCollections.observableArrayList();
                        lvProductoAgregados.getItems().forEach(o -> observableList.add(o.getSuministroTB()));
                        AnchorPane hbDetalle = new AnchorPane();
                        for (int m = 0; m < observableList.size(); m++) {
                            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                                HBox hBox = new HBox();
                                hBox.setId("dc_" + m + "" + i);
                                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                                billPrintable.hbDetalle(hBox, box, observableList, m);
                                hbDetalle.getChildren().add(hBox);
                            }
                        }

                        for (int i = 0; i < hbPie.getChildren().size(); i++) {
                            HBox box = ((HBox) hbPie.getChildren().get(i));
                            billPrintable.hbPie(box, monedaSimbolo,
                                    Tools.roundingValue(importeBruto, 2),
                                    "-" + Tools.roundingValue(descuentoBruto, 2),
                                    Tools.roundingValue(subImporteNeto, 2),
                                    Tools.roundingValue(impuestoNeto, 2),
                                    Tools.roundingValue(subImporteNeto, 2),
                                    Tools.roundingValue(importeNeto, 2),
                                    "TARJETA",
                                    "EFECTIVO",
                                    "VUELTO",
                                    numeroDocumento,
                                    informacion,
                                    "CODIGO DE VENTA",
                                    celular,
                                    monedaCadena.Convertir(Tools.roundingValue(importeNeto, 2), true, ""),
                                    "",
                                    "",
                                    "",
                                    "",
                                    "");
                        }

                        billPrintable.generatePDFPrint(hbEncabezado, hbDetalle, hbPie);

                        DocPrintJob job = billPrintable.findPrintService(printerName, PrinterJob.lookupPrintServices()).createPrintJob();

                        if (job != null) {
                            PrinterJob pj = PrinterJob.getPrinterJob();
                            pj.setPrintService(job.getPrintService());
                            pj.setJobName(printerName);
                            Book book = new Book();
                            book.append(billPrintable, billPrintable.getPageFormat(pj));
                            pj.setPageable(book);
                            pj.print();
                            if (printerCut) {
                                billPrintable.printCortarPapel(printerName);
                            }
                            return "completed";
                        } else {
                            return "error_name";
                        }
                    } else {
                        billPrintable.loadEstructuraTicket(Session.TICKET_PRE_VENTA_ID, Session.TICKET_PRE_VENTA_RUTA, hbEncabezado, hbDetalleCabecera, hbPie);
                        ArrayList<HBox> object = new ArrayList<>();
                        int rows = 0;
                        int lines = 0;
                        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
                            object.add((HBox) hbEncabezado.getChildren().get(i));
                            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
                            rows++;
                            lines += billPrintable.hbEncebezado(box,
                                    "NOTA DE VENTA",
                                    "---",
                                    numeroDocumento,
                                    informacion,
                                    celular,
                                    direccion,
                                    "00000000",
                                    Tools.getDate(),
                                    Tools.getTime(),
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "0",
                                    "0",
                                    "0",
                                    "0",
                                    "0");
                        }

                        ObservableList<SuministroTB> observableList = FXCollections.observableArrayList();
                        lvProductoAgregados.getItems().forEach(o -> observableList.add(o.getSuministroTB()));
                        for (int m = 0; m < observableList.size(); m++) {
                            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                                HBox hBox = new HBox();
                                hBox.setId("dc_" + m + "" + i);
                                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                                rows++;
                                lines += billPrintable.hbDetalle(hBox, box, observableList, m);
                                object.add(hBox);
                            }
                        }

                        for (int i = 0; i < hbPie.getChildren().size(); i++) {
                            object.add((HBox) hbPie.getChildren().get(i));
                            HBox box = ((HBox) hbPie.getChildren().get(i));
                            rows++;
                            lines += billPrintable.hbPie(box, monedaSimbolo,
                                    Tools.roundingValue(importeBruto, 2),
                                    "-" + Tools.roundingValue(descuentoBruto, 2),
                                    Tools.roundingValue(subImporteNeto, 2),
                                    Tools.roundingValue(impuestoNeto, 2),
                                    Tools.roundingValue(subImporteNeto, 2),
                                    Tools.roundingValue(importeNeto, 2),
                                    "TARJETA",
                                    "EFECTIVO",
                                    "VUELTO",
                                    numeroDocumento,
                                    informacion,
                                    "CODIGO DE VENTA",
                                    celular,
                                    monedaCadena.Convertir(Tools.roundingValue(importeNeto, 2), true, ""),
                                    "",
                                    "",
                                    "",
                                    "",
                                    "");
                        }
                        return billPrintable.modelTicket(rows + lines + 1 + 5, lines, object, printerName, printerCut);
                    }

                } catch (PrinterException | IOException | PrintException ex) {
                    return "Error en imprimir: " + ex.getLocalizedMessage();
                }
            }
        };

        task.setOnSucceeded(w -> {
            String result = task.getValue();
            if (result.equalsIgnoreCase("completed")) {
                Tools.showAlertNotification("/view/image/information_large.png",
                        "Envío de impresión",
                        "Se completo el proceso de impresión correctamente.",
                        Duration.seconds(5),
                        Pos.BOTTOM_RIGHT);
            } else if (result.equalsIgnoreCase("error_name")) {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "Error en encontrar el nombre de la impresión por problemas de puerto o driver.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else if (result.equalsIgnoreCase("no_config")) {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "Error al validar el tipo de impresión, cofigure nuevamente la impresora.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else if (result.equalsIgnoreCase("empty")) {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "No hay registros para mostrar en el reporte.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else {
                Tools.showAlertNotification("/view/image/error_large.png",
                        "Envío de impresión",
                        "Error en la configuración de su impresora: " + result,
                        Duration.seconds(10),
                        Pos.CENTER);
            }
        });

        task.setOnFailed(w -> {
            Tools.showAlertNotification("/view/image/warning_large.png",
                    "Envío de impresión",
                    "Se produjo un problema en el proceso de envío, \n intente nuevamente o comuníquese con su proveedor del sistema.",
                    Duration.seconds(10),
                    Pos.BOTTOM_RIGHT);
        });

        task.setOnScheduled(w -> {
            Tools.showAlertNotification("/view/image/print.png",
                    "Envío de impresión",
                    "Se envió la impresión a la cola, este\n proceso puede tomar unos segundos.",
                    Duration.seconds(5),
                    Pos.BOTTOM_RIGHT);
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void cancelarVenta() {
        short value = Tools.AlertMessageConfirmation(vbWindow, "Venta", "¿Está seguro de limpiar la venta?");
        if (value == 1) {
            resetVenta();
        }
    }

    private void openWindowDetalleProducto(int index, BbItemProducto bbItemProducto) {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_VENTA_DETALLE_PRODUCTO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxVentaDetalleProductoController controller = fXMLLoader.getController();
            controller.loadData(index, bbItemProducto);
            controller.setInitVentaEstructuraNuevoController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Detalle producto", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
        } catch (IOException ex) {
            System.out.println("openWindowImpresora():" + ex.getLocalizedMessage());
        }
    }

    public void onEventCobrar() {
        try {
            if (cbMoneda.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(vbWindow, "Venta", "Seleccione la moneda ha usar.");
                cbMoneda.requestFocus();
            } else if (cbTipoDocumento.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(vbWindow, "Ventas", "Seleccione el tipo de documento del cliente.");
                cbTipoDocumento.requestFocus();
            } else if (!Tools.isNumeric(txtNumeroDocumento.getText().trim())) {
                Tools.AlertMessageWarning(vbWindow, "Ventas", "Ingrese el número del documento del cliente.");
                txtNumeroDocumento.requestFocus();
            } else if (cbComprobante.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(vbWindow, "Venta", "Seleccione el tipo de comprobante.");
                cbComprobante.requestFocus();
            } else if (lvProductoAgregados.getItems().isEmpty()) {
                Tools.AlertMessageWarning(vbWindow, "Venta", "No hay productos en la lista para vender.");
                txtSearch.requestFocus();
            } else if (importeNeto <= 0) {
                Tools.AlertMessageWarning(vbWindow, "Venta", "El total de la venta no puede ser menor que 0.");
            } else {
                fxPrincipalController.openFondoModal();
                URL url = getClass().getResource(FilesRouters.FX_VENTA_PROCESO_NUEVO);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxVentaProcesoNuevoController controller = fXMLLoader.getController();
                controller.setInitVentaEstructuraNuevoController(this);
                //
                Stage stage = WindowStage.StageLoaderModal(parent, "Completar la venta", vbWindow.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
                stage.show();

                ClienteTB clienteTB = new ClienteTB();
                clienteTB.setIdCliente(idCliente);
                clienteTB.setTipoDocumento(cbTipoDocumento.getSelectionModel().getSelectedItem().getIdDetalle());
                clienteTB.setNumeroDocumento(txtNumeroDocumento.getText().trim().toUpperCase());
                clienteTB.setInformacion(txtDatosCliente.getText().trim().toUpperCase());
                clienteTB.setCelular(txtCelularCliente.getText().trim().toUpperCase());
                clienteTB.setEmail(txtCorreoElectronico.getText().trim().toUpperCase());
                clienteTB.setDireccion(txtDireccionCliente.getText().trim().toUpperCase());

                VentaTB ventaTB = new VentaTB();
                ventaTB.setVendedor(Session.USER_ID);
                ventaTB.setIdComprobante(cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento());
                ventaTB.setComprobanteName(cbComprobante.getSelectionModel().getSelectedItem().getNombre());
                ventaTB.setIdMoneda(cbMoneda.getSelectionModel().getSelectedIndex() >= 0 ? cbMoneda.getSelectionModel().getSelectedItem().getIdMoneda() : 0);
                ventaTB.setMonedaName(monedaSimbolo);
                ventaTB.setSerie(lblSerie.getText());
                ventaTB.setNumeracion(lblNumeracion.getText());
                ventaTB.setFechaVenta(Tools.getDate());
                ventaTB.setHoraVenta(Tools.getTime());
                ventaTB.setSubTotal(importeBruto);
                ventaTB.setDescuento(descuentoBruto);
                ventaTB.setSubImporte(subImporteNeto);
                ventaTB.setImpuesto(impuestoNeto);
                ventaTB.setTotal(importeNeto);
                ventaTB.setClienteTB(clienteTB);
                ArrayList<SuministroTB> suministroTBs = new ArrayList<>();
                lvProductoAgregados.getItems().forEach(e -> suministroTBs.add(e.getSuministroTB()));
                controller.setInitComponents(ventaTB, suministroTBs, vender_con_cantidades_negativas, cbMoneda.getSelectionModel().getSelectedItem().getNombre());
            }
        } catch (IOException ex) {
            System.out.println("openWindowVentaProceso():" + ex.getLocalizedMessage());
        }
    }

    public void onExecuteCliente(short opcion, String search) {

        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ClienteTB> task = new Task<ClienteTB>() {
            @Override
            public ClienteTB call() {
                return ClienteADO.GetSearchClienteNumeroDocumento(opcion, search);
            }
        };

        task.setOnScheduled(e -> {
            txtDatosCliente.setText("");
            txtCelularCliente.setText("");
            txtCorreoElectronico.setText("");
            txtDireccionCliente.setText("");
            vbBodyCliente.setDisable(true);
            hbLoadCliente.setVisible(true);

            if (btnCancelarProceso.getOnAction() != null) {
                btnCancelarProceso.removeEventHandler(ActionEvent.ACTION, btnCancelarProceso.getOnAction());
            }
            btnCancelarProceso.setOnAction(event -> {
                if (task.isRunning()) {
                    task.cancel();
                }
                vbBodyCliente.setDisable(false);
                hbLoadCliente.setVisible(false);
            });
        });

        task.setOnCancelled(e -> {
            Tools.showAlertNotification("/view/image/warning_large.png",
                    "Buscando clíente",
                    "Se canceló la busqueda, \nreintente por favor.",
                    Duration.seconds(10),
                    Pos.TOP_RIGHT);
            clearDataClient();
        });

        task.setOnFailed(e -> {
            Tools.showAlertNotification("/view/image/error_large.png",
                    "Buscando clíente",
                    "Se produjo un error al buscar al cliente intenten\n nuevamente, si persiste el problema comuniquese con su \nproveedor del sistema.",
                    Duration.seconds(10),
                    Pos.TOP_RIGHT);
            clearDataClient();

            vbBodyCliente.setDisable(false);
            hbLoadCliente.setVisible(false);
        });

        task.setOnSucceeded(e -> {
            ClienteTB clienteTB = task.getValue();
            if (clienteTB != null) {
                Tools.showAlertNotification("/view/image/succes_large.png",
                        "Buscando clíente",
                        "Se completo la busqueda con exito.",
                        Duration.seconds(5),
                        Pos.TOP_RIGHT);

                idCliente = clienteTB.getIdCliente();
                txtDatosCliente.setText(clienteTB.getInformacion());
                txtCelularCliente.setText(clienteTB.getCelular());
                txtCorreoElectronico.setText(clienteTB.getEmail());
                txtDireccionCliente.setText(clienteTB.getDireccion());
                for (int i = 0; i < cbTipoDocumento.getItems().size(); i++) {
                    if (cbTipoDocumento.getItems().get(i).getIdDetalle() == clienteTB.getTipoDocumento()) {
                        cbTipoDocumento.getSelectionModel().select(i);
                        break;
                    }
                }
            } else {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Buscando clíente",
                        "Hubo un problema en validar el resultado de\n datos intente nuevamente.",
                        Duration.seconds(5),
                        Pos.TOP_RIGHT);
                clearDataClient();
            }

            vbBodyCliente.setDisable(false);
            hbLoadCliente.setVisible(false);
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void executeApiSunat() {
        ApiPeru apiSunat = new ApiPeru();
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                ArrayList<Object> objects = new ArrayList();
                ClienteTB clienteTB = ClienteADO.GetSearchClienteNumeroDocumento((short) 2, txtNumeroDocumento.getText().trim());
                if (clienteTB == null) {
                    objects.add("client-no-exists");
                    objects.add("");
                    objects.add(apiSunat.getUrlSunatApisPeru(txtNumeroDocumento.getText().trim()));
                } else {
                    objects.add("client-exists");
                    objects.add(clienteTB);
                    objects.add(apiSunat.getUrlSunatApisPeru(txtNumeroDocumento.getText().trim()));
                }
                return objects;
            }
        };

        task.setOnScheduled(e -> {
            txtDatosCliente.setText("");
            txtCelularCliente.setText("");
            txtCorreoElectronico.setText("");
            txtDireccionCliente.setText("");

            vbBodyCliente.setDisable(true);
            hbLoadCliente.setVisible(true);

            if (btnCancelarProceso.getOnAction() != null) {
                btnCancelarProceso.removeEventHandler(ActionEvent.ACTION, btnCancelarProceso.getOnAction());
            }
            btnCancelarProceso.setOnAction(event -> {
                if (task.isRunning()) {
                    task.cancel();
                }
                vbBodyCliente.setDisable(false);
                hbLoadCliente.setVisible(false);
            });
        });

        task.setOnCancelled(e -> {
            Tools.showAlertNotification("/view/image/warning_large.png",
                    "Buscando clíente",
                    "Se canceló la busqueda, \nreintente por favor.",
                    Duration.seconds(10),
                    Pos.TOP_RIGHT);
            clearDataClient();
        });

        task.setOnFailed(e -> {
            Tools.showAlertNotification("/view/image/error_large.png",
                    "Buscando clíente",
                    "Se produjo un error al buscar al cliente intenten\n nuevamente, si persiste el problema comuniquese con su \nproveedor del sistema.",
                    Duration.seconds(10),
                    Pos.TOP_RIGHT);
            clearDataClient();

            vbBodyCliente.setDisable(false);
            hbLoadCliente.setVisible(false);
        });

        task.setOnSucceeded(e -> {

            ArrayList<Object> result = task.getValue();
            if (!result.isEmpty()) {
                String stateClient = (String) result.get(0);
                String api = (String) result.get(2);

                if (api.equalsIgnoreCase("200") && !Tools.isText(apiSunat.getJsonURL())) {

                    JSONObject sONObject = Json.obtenerObjetoJSON(apiSunat.getJsonURL());
                    if (sONObject == null) {
                        Tools.showAlertNotification("/view/image/warning_large.png",
                                "Buscando clíente",
                                "Hubo un problema en validar el resultado de\n datos intente nuevamente.",
                                Duration.seconds(5),
                                Pos.TOP_RIGHT);
                        clearDataClient();
                    } else {
                        Tools.showAlertNotification("/view/image/succes_large.png",
                                "Buscando clíente",
                                "Se completo la busqueda con exito.",
                                Duration.seconds(5),
                                Pos.TOP_RIGHT);

                        if (sONObject.get("ruc") != null) {
                            txtNumeroDocumento.setText(sONObject.get("ruc").toString());
                        }
                        if (sONObject.get("razonSocial") != null) {
                            txtDatosCliente.setText(sONObject.get("razonSocial").toString());
                        }
                        if (sONObject.get("direccion") != null) {
                            txtDireccionCliente.setText(sONObject.get("direccion").toString());
                        }

                        if (stateClient.equals("client-exists")) {
                            ClienteTB clienteTB = (ClienteTB) result.get(1);
                            txtCelularCliente.setText(clienteTB.getCelular());
                            txtCorreoElectronico.setText(clienteTB.getEmail());
                            for (int i = 0; i < cbTipoDocumento.getItems().size(); i++) {
                                if (cbTipoDocumento.getItems().get(i).getIdDetalle() == clienteTB.getTipoDocumento()) {
                                    cbTipoDocumento.getSelectionModel().select(i);
                                    break;
                                }
                            }
                        } else {
                            for (int i = 0; i < cbTipoDocumento.getItems().size(); i++) {
                                if (cbTipoDocumento.getItems().get(i).getIdAuxiliar().equals("6")) {
                                    cbTipoDocumento.getSelectionModel().select(i);
                                    break;
                                }
                            }
                        }
                    }

                } else {
                    Tools.showAlertNotification("/view/image/warning_large.png",
                            "Buscando clíente",
                            "Paso un problema en trear la información por\n problemas de conexión o error el número ruc.",
                            Duration.seconds(5),
                            Pos.TOP_RIGHT);
                    clearDataClient();
                }
            } else {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Buscando clíente",
                        "Hubo un problema interno, intente nuevamente.",
                        Duration.seconds(5),
                        Pos.TOP_RIGHT);
                clearDataClient();
            }
            vbBodyCliente.setDisable(false);
            hbLoadCliente.setVisible(false);
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void executeApiReniec() {
        ApiPeru apiSunat = new ApiPeru();
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                ArrayList<Object> objects = new ArrayList();
                ClienteTB clienteTB = ClienteADO.GetSearchClienteNumeroDocumento((short) 2, txtNumeroDocumento.getText().trim());
                if (clienteTB == null) {
                    objects.add("client-no-exists");
                    objects.add("");
                    objects.add(apiSunat.getUrlReniecApisPeru(txtNumeroDocumento.getText().trim()));
                } else {
                    objects.add("client-exists");
                    objects.add(clienteTB);
                    objects.add(apiSunat.getUrlReniecApisPeru(txtNumeroDocumento.getText().trim()));
                }
                return objects;
            }
        };

        task.setOnScheduled(e -> {
            txtDatosCliente.setText("");
            txtCelularCliente.setText("");
            txtDireccionCliente.setText("");
            txtDireccionCliente.setText("");

            vbBodyCliente.setDisable(true);
            hbLoadCliente.setVisible(true);

            if (btnCancelarProceso.getOnAction() != null) {
                btnCancelarProceso.removeEventHandler(ActionEvent.ACTION, btnCancelarProceso.getOnAction());
            }
            btnCancelarProceso.setOnAction(event -> {
                if (task.isRunning()) {
                    task.cancel();
                }
                vbBodyCliente.setDisable(false);
                hbLoadCliente.setVisible(false);
            });
        });

        task.setOnCancelled(e -> {
            Tools.showAlertNotification("/view/image/warning_large.png",
                    "Buscando clíente",
                    "Se canceló la busqueda, \nreintente por favor.",
                    Duration.seconds(10),
                    Pos.TOP_RIGHT);
            clearDataClient();
        });

        task.setOnFailed(e -> {
            Tools.showAlertNotification("/view/image/error_large.png",
                    "Buscando clíente",
                    "Se produjo un error al buscar al cliente intenten\n nuevamente, si persiste el problema comuniquese con su \nproveedor del sistema.",
                    Duration.seconds(10),
                    Pos.TOP_RIGHT);
            clearDataClient();
            vbBodyCliente.setDisable(false);
            hbLoadCliente.setVisible(false);
        });

        task.setOnSucceeded(e -> {
            ArrayList<Object> result = task.getValue();
            if (!result.isEmpty()) {
                String stateClient = (String) result.get(0);
                String api = (String) result.get(2);

                if (api.equalsIgnoreCase("200") && !Tools.isText(apiSunat.getJsonURL())) {
                    JSONObject sONObject = Json.obtenerObjetoJSON(apiSunat.getJsonURL());
                    if (sONObject == null) {
                        Tools.showAlertNotification("/view/image/warning_large.png",
                                "Buscando clíente",
                                "Hubo un problema en validar el resultado de\n datos intente nuevamente.",
                                Duration.seconds(5),
                                Pos.TOP_RIGHT);
                        clearDataClient();
                    } else {
                        Tools.showAlertNotification("/view/image/succes_large.png",
                                "Buscando clíente",
                                "Se completo la busqueda con exito.",
                                Duration.seconds(5),
                                Pos.TOP_RIGHT);

                        if (sONObject.get("dni") != null) {
                            txtNumeroDocumento.setText(sONObject.get("dni").toString());
                        }
                        if (sONObject.get("apellidoPaterno") != null && sONObject.get("apellidoMaterno") != null && sONObject.get("nombres") != null) {
                            txtDatosCliente.setText(sONObject.get("apellidoPaterno").toString() + " " + sONObject.get("apellidoMaterno").toString() + " " + sONObject.get("nombres").toString());
                        }
                        if (stateClient.equals("client-exists")) {
                            ClienteTB clienteTB = (ClienteTB) result.get(1);

                            txtCelularCliente.setText(clienteTB.getCelular());
                            txtCorreoElectronico.setText(clienteTB.getEmail());
                            txtDireccionCliente.setText(clienteTB.getDireccion());

                            for (int i = 0; i < cbTipoDocumento.getItems().size(); i++) {
                                if (cbTipoDocumento.getItems().get(i).getIdDetalle() == clienteTB.getTipoDocumento()) {
                                    cbTipoDocumento.getSelectionModel().select(i);
                                    break;
                                }
                            }
                        } else {
                            for (int i = 0; i < cbTipoDocumento.getItems().size(); i++) {
                                if (cbTipoDocumento.getItems().get(i).getIdAuxiliar().equals("1")) {
                                    cbTipoDocumento.getSelectionModel().select(i);
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    Tools.showAlertNotification("/view/image/warning_large.png",
                            "Buscando clíente",
                            "Paso un problema en trear la información por\n problemas de conexión o error el número del dni.",
                            Duration.seconds(5),
                            Pos.TOP_RIGHT);
                    clearDataClient();
                }

            } else {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Buscando clíente",
                        "Hubo un problema interno, intente nuevamente.",
                        Duration.seconds(5),
                        Pos.TOP_RIGHT);
                clearDataClient();
            }
            vbBodyCliente.setDisable(false);
            hbLoadCliente.setVisible(false);
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void clearDataClient() {
        txtDatosCliente.setText("");
        txtCelularCliente.setText("");
        txtCorreoElectronico.setText("");
        txtDireccionCliente.setText("");

        for (int i = 0; i < cbTipoDocumento.getItems().size(); i++) {
            if (cbTipoDocumento.getItems().get(i).getIdAuxiliar().equals("0")) {
                cbTipoDocumento.getSelectionModel().select(i);
                break;
            }
        }
    }

    private void onEventProducto() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_SUMINISTROS_PROCESO_MODAL);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxSuministrosProcesoModalController controller = fXMLLoader.getController();
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Producto", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
            controller.setInitArticulo();
        } catch (IOException ex) {
            System.out.println("Error en producto lista:" + ex.getLocalizedMessage());
        }
    }

    private void onEventMovimientoCaja() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_VENTA_MOVIMIENTO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
//            FxVentaMovimientoController controller = fXMLLoader.getController();
//            controller.setInitVentaEstructuraController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Movimiento de caja", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
        } catch (IOException ex) {
            Tools.println("Venta estructura nuevo onEventMovimientoCaja:" + ex.getLocalizedMessage());
        }
    }

    public void openWindowMostrarVentas() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_VENTA_MOSTRAR);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            //  FxVentaMostrarController controller = fXMLLoader.getController();
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Mostrar ventas", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();

        } catch (IOException ex) {
            Tools.println("Venta estructura openWindowMostrarVentas: " + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onKeyReleasedWindow(KeyEvent event) {
        switch (event.getCode()) {
            case F1:
                onEventCobrar();
                break;
            case F2:
                txtSearch.selectAll();
                txtSearch.requestFocus();
                break;
            case F3:
                cbMoneda.requestFocus();
                break;
            case F4:
                txtNumeroDocumento.requestFocus();
                break;
            case F5:

                break;
            case F6:
                cbComprobante.requestFocus();
                break;
            case F7:
                onEventMovimientoCaja();
                break;
            case F8:
                openWindowMostrarVentas();
                break;
            case F9:
                imprimirPreVenta();
                break;
            case F10:
                cancelarVenta();
                break;
            default:
                break;
        }
    }

    private void learnWord(String text) {
        posiblesWord.add(text);
        if (autoCompletionBinding != null) {
            autoCompletionBinding.dispose();
        }
        autoCompletionBinding = TextFields.bindAutoCompletion(txtNumeroDocumento, posiblesWord);
    }

    @FXML
    private void onKeyPressedToSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            fpProductos.requestFocus();
            if (!fpProductos.getChildren().isEmpty()) {
                Button button = (Button) fpProductos.getChildren().get(0);
                button.requestFocus();
            }
        }
    }

    @FXML
    private void onKeyReleasedToSearch(KeyEvent event) {
        if (event.getCode() != KeyCode.ESCAPE
                && event.getCode() != KeyCode.F1
                && event.getCode() != KeyCode.F2
                && event.getCode() != KeyCode.F3
                && event.getCode() != KeyCode.F4
                && event.getCode() != KeyCode.F5
                && event.getCode() != KeyCode.F6
                && event.getCode() != KeyCode.F7
                && event.getCode() != KeyCode.F8
                && event.getCode() != KeyCode.F9
                && event.getCode() != KeyCode.F10
                && event.getCode() != KeyCode.F11
                && event.getCode() != KeyCode.F12
                && event.getCode() != KeyCode.ALT
                && event.getCode() != KeyCode.CONTROL
                && event.getCode() != KeyCode.UP
                && event.getCode() != KeyCode.DOWN
                && event.getCode() != KeyCode.RIGHT
                && event.getCode() != KeyCode.LEFT
                && event.getCode() != KeyCode.TAB
                && event.getCode() != KeyCode.CAPS
                && event.getCode() != KeyCode.SHIFT
                && event.getCode() != KeyCode.HOME
                && event.getCode() != KeyCode.WINDOWS
                && event.getCode() != KeyCode.ALT_GRAPH
                && event.getCode() != KeyCode.CONTEXT_MENU
                && event.getCode() != KeyCode.END
                && event.getCode() != KeyCode.INSERT
                && event.getCode() != KeyCode.PAGE_UP
                && event.getCode() != KeyCode.PAGE_DOWN
                && event.getCode() != KeyCode.NUM_LOCK
                && event.getCode() != KeyCode.PRINTSCREEN
                && event.getCode() != KeyCode.SCROLL_LOCK
                && event.getCode() != KeyCode.PAUSE) {
            if (!Tools.isText(txtSearch.getText())) {
                if (!state) {
                    paginacion = 1;
                    searchTable(event, txtSearch.getText().trim());
                    opcion = 1;
                }
            }
        }
    }

    private void onKeyPressReload(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!state) {
                paginacion = 1;
                fillSuministrosTable((short) 0, "");
                opcion = 0;
            }
        }
    }

    private void onActionReload(ActionEvent event) {
        if (!state) {
            paginacion = 1;
            fillSuministrosTable((short) 0, "");
            opcion = 0;
        }
    }

    @FXML
    private void onKeyPressedAnterior(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!state) {
                if (paginacion > 1) {
                    paginacion--;
                    onEventPaginacion();
                }
            }
        }
    }

    @FXML
    private void onActionAnterior(ActionEvent event) {
        if (!state) {
            if (paginacion > 1) {
                paginacion--;
                onEventPaginacion();
            }
        }
    }

    @FXML
    private void onKeyPressedSiguiente(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!state) {
                if (paginacion < totalPaginacion) {
                    paginacion++;
                    onEventPaginacion();
                }
            }
        }
    }

    @FXML
    private void onActionSiguiente(ActionEvent event) {
        if (!state) {
            if (paginacion < totalPaginacion) {
                paginacion++;
                onEventPaginacion();
            }
        }
    }

    @FXML
    private void onActionMoneda(ActionEvent event) {
        if (cbMoneda.getSelectionModel().getSelectedIndex() >= 0) {
            monedaSimbolo = cbMoneda.getSelectionModel().getSelectedItem().getSimbolo();
            calculateTotales();
        }
    }

    @FXML
    private void onMouseClickedProductosAgregados(MouseEvent event) {
        if (event.getClickCount() == 2) {
            if (lvProductoAgregados.getSelectionModel().getSelectedIndex() >= 0) {
                openWindowDetalleProducto(lvProductoAgregados.getSelectionModel().getSelectedIndex(), lvProductoAgregados.getSelectionModel().getSelectedItem());
                event.consume();
            }
        }
    }

    @FXML
    private void onKeyPressedProductosAgregados(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (lvProductoAgregados.getSelectionModel().getSelectedIndex() >= 0) {
                openWindowDetalleProducto(lvProductoAgregados.getSelectionModel().getSelectedIndex(), lvProductoAgregados.getSelectionModel().getSelectedItem());
            }
        }
    }

    @FXML
    private void onActionComprobante(ActionEvent event) {
        if (cbComprobante.getSelectionModel().getSelectedIndex() >= 0) {
            String[] array = ComprobanteADO.GetSerieNumeracionEspecifico(cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento()).split("-");
            lblSerie.setText(array[0]);
            lblNumeracion.setText(array[1]);
        }
    }

    @FXML
    private void onKeyPressedProducto(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventProducto();
        }
    }

    @FXML
    private void onActionProducto(ActionEvent event) {
        onEventProducto();
    }

    @FXML
    private void onKeyPressedCliente(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onExecuteCliente((short) 2, txtNumeroDocumento.getText().trim());
            learnWord(txtNumeroDocumento.getText().trim());
        }
    }

    @FXML
    private void onActionCliente(ActionEvent event) {
        onExecuteCliente((short) 2, txtNumeroDocumento.getText().trim());
        learnWord(txtNumeroDocumento.getText().trim());
    }

    @FXML
    private void onKeyPressedCobrar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventCobrar();
        }
    }

    @FXML
    private void onActionCobrar(ActionEvent event) {
        onEventCobrar();
    }

    @FXML
    private void onKeyPressedMovimientoCaja(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventMovimientoCaja();
        }
    }

    @FXML
    private void onActionMovimientoCaja(ActionEvent event) {
        onEventMovimientoCaja();
    }

    @FXML
    private void onKeyPressedTicket(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            imprimirPreVenta();
        }
    }

    @FXML
    private void onActionTicket(ActionEvent event) {
        imprimirPreVenta();
    }

    @FXML
    private void onKeyPressedLimpiar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            cancelarVenta();
        }
    }

    @FXML
    private void onActionLimpiar(ActionEvent event) {
        cancelarVenta();
    }

    @FXML
    private void onKeyPressedVentasPorDia(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowMostrarVentas();
        }
    }

    @FXML
    private void onActionVentasPorDia(ActionEvent event) {
        openWindowMostrarVentas();
    }

    @FXML
    private void onKeyTypedNumeroDocumento(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b')) {
            event.consume();
        }
    }

    @FXML
    private void onKeyPressedSunat(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            executeApiSunat();
            learnWord(txtNumeroDocumento.getText().trim());
        }
    }

    @FXML
    private void onActionSunat(ActionEvent event) {
        executeApiSunat();
        learnWord(txtNumeroDocumento.getText().trim());
    }

    @FXML
    private void onKeyPressedReniec(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            executeApiReniec();
            learnWord(txtNumeroDocumento.getText().trim());
        }
    }

    @FXML
    private void onActionReniec(ActionEvent event) {
        executeApiReniec();
        learnWord(txtNumeroDocumento.getText().trim());
    }

    public TextField getTxtSearch() {
        return txtSearch;
    }

    public ComboBox<MonedaTB> getCbMoneda() {
        return cbMoneda;
    }

    public ListView<BbItemProducto> getLvProductoAgregados() {
        return lvProductoAgregados;
    }

    public int getIdTipoComprobante() {
        return cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento();
    }

    public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }

}
