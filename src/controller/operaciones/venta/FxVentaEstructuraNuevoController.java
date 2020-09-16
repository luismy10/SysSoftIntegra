package controller.operaciones.venta;

import controller.contactos.clientes.FxClienteProcesoController;
import controller.inventario.suministros.FxSuministrosProcesoModalController;
import controller.tools.*;

import java.awt.print.Book;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
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
import model.VentaADO;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

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
    private ComboBox<ClienteTB> cbCliente;
    @FXML
    private ComboBox<TipoDocumentoTB> cbComprobante;
    @FXML
    private Text lblSerie;
    @FXML
    private Text lblNumeracion;

    private AnchorPane vbPrincipal;

    private BillPrintable billPrintable;

    private ConvertMonedaCadena monedaCadena;

    private ObservableList<SuministroTB> listSuministros;

    private AnchorPane hbEncabezado;

    private AnchorPane hbDetalleCabecera;

    private AnchorPane hbPie;

    private String monedaSimbolo;

    private int totalPaginacion;

    private int paginacion;

    private short opcion;

    private boolean state;

    private double subTotal;

    private double descuento;

    private double subTotalImporte;

    private double totalImporte;

    private double totalImpuesto;

    private double total;

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

        SearchComboBox<ClienteTB> searchComboBoxCliente = new SearchComboBox<>(cbCliente, false);
        searchComboBoxCliente.getSearchComboBoxSkin().getSearchBox().setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                if (!searchComboBoxCliente.getSearchComboBoxSkin().getItemView().getItems().isEmpty()) {
                    searchComboBoxCliente.getSearchComboBoxSkin().getItemView().getSelectionModel().select(0);
                    searchComboBoxCliente.getSearchComboBoxSkin().getItemView().requestFocus();
                }
            } else if (t.getCode() == KeyCode.ESCAPE) {
                searchComboBoxCliente.getComboBox().hide();
            }
        });
        searchComboBoxCliente.getSearchComboBoxSkin().getSearchBox().setOnKeyReleased(t -> {
            searchComboBoxCliente.getComboBox().getItems().clear();
            List<ClienteTB> clienteTBs = ClienteADO.GetSearchComboBoxCliente((short) 4, searchComboBoxCliente.getSearchComboBoxSkin().getSearchBox().getText().trim());
            clienteTBs.forEach(e -> cbCliente.getItems().add(e));
        });
        searchComboBoxCliente.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
            if (null == t.getCode()) {
                searchComboBoxCliente.getSearchComboBoxSkin().getSearchBox().requestFocus();
                searchComboBoxCliente.getSearchComboBoxSkin().getSearchBox().selectAll();
            } else {
                switch (t.getCode()) {
                    case ENTER:
                    case SPACE:
                    case ESCAPE:
                        searchComboBoxCliente.getComboBox().hide();
                        break;
                    case UP:
                    case DOWN:
                    case LEFT:
                    case RIGHT:
                        break;
                    default:
                        searchComboBoxCliente.getSearchComboBoxSkin().getSearchBox().requestFocus();
                        searchComboBoxCliente.getSearchComboBoxSkin().getSearchBox().selectAll();
                        break;
                }
            }
        });
        searchComboBoxCliente.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                searchComboBoxCliente.getComboBox().getSelectionModel().select(item);
                if (searchComboBoxCliente.getSearchComboBoxSkin().isClickSelection()) {
                    searchComboBoxCliente.getComboBox().hide();
                }
            }
        });

        loadDataComponent();
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

        cbCliente.getItems().clear();
        if (!Session.CLIENTE_ID.equalsIgnoreCase("")) {
            cbCliente.getItems().add(new ClienteTB(Session.CLIENTE_ID, Session.CLIENTE_NUMERO_DOCUMENTO, Session.CLIENTE_DATOS, "", "", Session.CLIENTE_DIRECCION));
            cbCliente.getSelectionModel().select(0);
        }
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

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                return SuministroADO.ListSuministrosListaView(tipo, value, (paginacion - 1) * 20, 20);
            }
        };

        task.setOnSucceeded(e -> {
            fpProductos.getChildren().clear();
            listSuministros.clear();
            ArrayList<Object> objects = task.getValue();
            if (!objects.isEmpty()) {
                fpProductos.setAlignment(Pos.TOP_CENTER);
                listSuministros.addAll((ObservableList<SuministroTB>) objects.get(0));
                for (SuministroTB tvList1 : listSuministros) {
                    Rectangle2D screenBounds = Screen.getPrimary().getBounds();
                    double dpi = (screenBounds.getWidth() / screenBounds.getHeight()) * 70;
                    VBox vBox = new VBox();
                    File fileImage = new File(tvList1.getImagenTB());
                    ImageView imageView = new ImageView(new Image(fileImage.exists() ? fileImage.toURI().toString() : "/view/image/no-image.png"));
                    imageView.setFitWidth(dpi);
                    imageView.setFitHeight(dpi);
                    vBox.getChildren().add(imageView);

                    Label lblCodigo = new Label(tvList1.getClave());
                    lblCodigo.getStyleClass().add("labelOpenSansRegular13");
                    lblCodigo.setTextFill(Color.web("#020203"));
                    vBox.getChildren().add(lblCodigo);

                    Label lblProducto = new Label(tvList1.getNombreMarca());
                    lblProducto.getStyleClass().add("labelRobotoBold13");
                    lblProducto.setTextFill(Color.web("#020203"));
                    lblProducto.setWrapText(true);
                    lblProducto.setTextAlignment(TextAlignment.CENTER);
                    vBox.getChildren().add(lblProducto);

                    Label lblMarca = new Label(tvList1.getMarcaName());
                    lblMarca.getStyleClass().add("labelOpenSansRegular13");
                    lblMarca.setTextFill(Color.web("#1a2226"));
                    vBox.getChildren().add(lblMarca);

                    double impuestoimpuesto = tvList1.getImpuestoValor();
                    double impuestototal = Tools.calculateTax(impuestoimpuesto, tvList1.getPrecioVentaGeneral());
                    double precioventa = tvList1.getPrecioVentaGeneral() + impuestototal;

                    Label lblTotalProducto = new Label(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(precioventa, 2));
                    lblTotalProducto.getStyleClass().add("labelRobotoBold17");
                    lblTotalProducto.setTextFill(Color.web("#0478b2"));
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
                totalPaginacion = (int) (Math.ceil(((Integer) objects.get(1)) / 20.00));
                lblPaginaActual.setText(paginacion + "");
                lblPaginaSiguiente.setText(totalPaginacion + "");
            }
            state = false;
        });
        task.setOnFailed(e -> state = false);
        task.setOnScheduled(e -> {
            state = true;
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

    private void addElementListView(SuministroTB tvList1) {
        SuministroTB suministroTB = new SuministroTB();
        suministroTB.setIdSuministro(tvList1.getIdSuministro());
        suministroTB.setClave(tvList1.getClave());
        suministroTB.setNombreMarca(tvList1.getNombreMarca());
        suministroTB.setCantidad(1);
        suministroTB.setCostoCompra(tvList1.getCostoCompra());

        suministroTB.setDescuento(0);
        suministroTB.setDescuentoCalculado(0);
        suministroTB.setDescuentoSumado(0);

        suministroTB.setPrecioVentaGeneralUnico(tvList1.getPrecioVentaGeneral());
        suministroTB.setPrecioVentaGeneralReal(tvList1.getPrecioVentaGeneral());
        suministroTB.setPrecioVentaGeneralAuxiliar(suministroTB.getPrecioVentaGeneralReal());

        suministroTB.setImpuestoOperacion(tvList1.getImpuestoOperacion());
        suministroTB.setImpuestoId(tvList1.getImpuestoId());
        suministroTB.setImpuestoNombre(tvList1.getImpuestoNombre());
        suministroTB.setImpuestoValor(tvList1.getImpuestoValor());
        suministroTB.setImpuestoSumado(suministroTB.getCantidad() * Tools.calculateTax(suministroTB.getImpuestoValor(), suministroTB.getPrecioVentaGeneralReal()));

        suministroTB.setPrecioVentaGeneral(suministroTB.getPrecioVentaGeneralReal() + suministroTB.getImpuestoSumado());

        suministroTB.setSubImporte(suministroTB.getPrecioVentaGeneralUnico() * suministroTB.getCantidad());
        suministroTB.setSubImporteDescuento(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
        suministroTB.setTotalImporte(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());

        suministroTB.setInventario(tvList1.isInventario());
        suministroTB.setUnidadVenta(tvList1.getUnidadVenta());
        suministroTB.setValorInventario(tvList1.getValorInventario());

        if (validateDuplicateArticulo(lvProductoAgregados, suministroTB)) {
            for (int i = 0; i < lvProductoAgregados.getItems().size(); i++) {
                if (lvProductoAgregados.getItems().get(i).getSuministroTB().getIdSuministro().equalsIgnoreCase(suministroTB.getIdSuministro())) {
                    BbItemProducto bbItemProducto = lvProductoAgregados.getItems().get(i);
                    bbItemProducto.getSuministroTB().setCantidad(bbItemProducto.getSuministroTB().getCantidad() + 1);
                    double porcentajeRestante = bbItemProducto.getSuministroTB().getPrecioVentaGeneralUnico() * (bbItemProducto.getSuministroTB().getDescuento() / 100.00);

                    bbItemProducto.getSuministroTB().setDescuentoSumado(porcentajeRestante * bbItemProducto.getSuministroTB().getCantidad());
                    bbItemProducto.getSuministroTB().setImpuestoSumado(bbItemProducto.getSuministroTB().getCantidad() * (bbItemProducto.getSuministroTB().getPrecioVentaGeneralReal() * (bbItemProducto.getSuministroTB().getImpuestoValor() / 100.00)));

                    bbItemProducto.getSuministroTB().setSubImporte(bbItemProducto.getSuministroTB().getPrecioVentaGeneralUnico() * bbItemProducto.getSuministroTB().getCantidad());
                    bbItemProducto.getSuministroTB().setSubImporteDescuento(bbItemProducto.getSuministroTB().getCantidad() * bbItemProducto.getSuministroTB().getPrecioVentaGeneralReal());
                    bbItemProducto.getSuministroTB().setTotalImporte(bbItemProducto.getSuministroTB().getCantidad() * bbItemProducto.getSuministroTB().getPrecioVentaGeneralReal());
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

        subTotal = 0;
        lvProductoAgregados.getItems().forEach(e -> subTotal += e.getSuministroTB().getSubImporte());
//        lblValorVenta.setText(monedaSimbolo + " " + Tools.roundingValue(subTotal, 2));

        descuento = 0;
        lvProductoAgregados.getItems().forEach(e -> descuento += e.getSuministroTB().getDescuentoSumado());
//        lblDescuento.setText(monedaSimbolo + " " + (Tools.roundingValue(descuento * (-1), 2)));

        subTotalImporte = 0;
        lvProductoAgregados.getItems().forEach(e -> subTotalImporte += e.getSuministroTB().getSubImporteDescuento());
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

        if (!lvProductoAgregados.getItems().isEmpty()) {
            for (int i = 0; i < lvProductoAgregados.getItems().size(); i++) {
                totalImpuestos += lvProductoAgregados.getItems().get(i).getSuministroTB().getImpuestoSumado();
            }
        }

        totalImporte = 0;
        totalImpuesto = 0;
        total = 0;
        lvProductoAgregados.getItems().forEach(e -> totalImporte += e.getSuministroTB().getTotalImporte());
        totalImpuesto = totalImpuestos;
        total = totalImporte + totalImpuesto;
        //lblImporteTotal.setText(monedaSimbolo + " " + Tools.roundingValue(total, 2));
        lblTotal.setText(monedaSimbolo + " " + Tools.roundingValue(Double.parseDouble(Tools.roundingValue(total, 1)), 2));

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
                ArrayList<Object> objects = VentaADO.ListCompletaVentasDetalle(idVenta);
                try {
                    if (!objects.isEmpty()) {
                        VentaTB ventaTB = (VentaTB) objects.get(0);
//                        EmpleadoTB empleadoTB = (EmpleadoTB) objects.get(1);
                        ObservableList<SuministroTB> suministroTBs = (ObservableList<SuministroTB>) objects.get(2);

                        if (format.equalsIgnoreCase("a4")) {
                            ArrayList<SuministroTB> list = new ArrayList();
                            suministroTBs.stream().map((suministroTB) -> {
                                SuministroTB stb = new SuministroTB();
                                stb.setClave(suministroTB.getClave());
                                stb.setNombreMarca(suministroTB.getNombreMarca());
                                stb.setCantidad(suministroTB.getCantidad());
                                stb.setUnidadCompraName(suministroTB.getUnidadCompraName());
                                stb.setPrecioVentaGeneral(suministroTB.getPrecioVentaGeneral());
                                stb.setDescuento(suministroTB.getDescuento());
                                stb.setTotalImporte(suministroTB.getCantidad() * +suministroTB.getPrecioVentaGeneral());
                                return stb;
                            }).forEachOrdered((stb) -> {
                                list.add(stb);
                            });

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

                            billPrintable.loadEstructuraTicket(Session.TICKET_VENTA_ID, Session.TICKET_VENTA_RUTA, hbEncabezado, hbDetalleCabecera, hbPie);

                            for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
                                HBox box = ((HBox) hbEncabezado.getChildren().get(i));
                                billPrintable.hbEncebezado(box,
                                        ventaTB.getComprobanteName(),
                                        ventaTB.getSerie() + "-" + ventaTB.getNumeracion(),
                                        ventaTB.getClienteTB().getNumeroDocumento(),
                                        ventaTB.getClienteTB().getInformacion(),
                                        ventaTB.getClienteTB().getCelular(),
                                        ventaTB.getClienteTB().getDireccion(),
                                        ventaTB.getCodigo());
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
                                        Tools.roundingValue(ventaTB.getTotal(), 2),
                                        Tools.roundingValue(ventaTB.getEfectivo(), 2),
                                        Tools.roundingValue(ventaTB.getVuelto(), 2),
                                        ventaTB.getClienteTB().getNumeroDocumento(),
                                        ventaTB.getClienteTB().getInformacion(), ventaTB.getCodigo(),
                                        ventaTB.getClienteTB().getCelular());
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
                        }
                    } else {
                        return "empty";
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
                    billPrintable.loadEstructuraTicket(Session.TICKET_PRE_VENTA_ID, Session.TICKET_PRE_VENTA_RUTA, hbEncabezado, hbDetalleCabecera, hbPie);

                    String numeroDocumento = "";
                    String informacion = "";
                    String celular = "";
                    String email = "";
                    String direccion = "";

                    if (cbCliente.getSelectionModel().getSelectedIndex() >= 0) {
                        ClienteTB clienteTB = cbCliente.getSelectionModel().getSelectedItem();
                        numeroDocumento = clienteTB.getNumeroDocumento() == null ? "" : clienteTB.getNumeroDocumento().toUpperCase();
                        informacion = clienteTB.getInformacion() == null ? "" : clienteTB.getInformacion().toUpperCase();
                        celular = clienteTB.getCelular() == null ? "" : clienteTB.getCelular().toUpperCase();
                        email = clienteTB.getEmail() == null ? "" : clienteTB.getEmail();
                        direccion = clienteTB.getDireccion() == null ? "" : clienteTB.getDireccion().toUpperCase();
                    }

                    for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
                        HBox box = ((HBox) hbEncabezado.getChildren().get(i));
                        billPrintable.hbEncebezado(box,
                                "PRE VENTA",
                                "-------",
                                numeroDocumento,
                                informacion,
                                celular,
                                direccion,
                                "00000000");
                    }

                    ObservableList<SuministroTB> observableList = FXCollections.observableArrayList();
                    lvProductoAgregados.getItems().forEach(o -> observableList.add(o.getSuministroTB()));
                    AnchorPane hbDetalle = new AnchorPane();
                    for (int m = 0; m < lvProductoAgregados.getItems().size(); m++) {
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
                                Tools.roundingValue(subTotal, 2),
                                "-" + Tools.roundingValue(descuento, 2),
                                Tools.roundingValue(subTotalImporte, 2),
                                Tools.roundingValue(total, 2),
                                "EFECTIVO",
                                "VUELTO",
                                numeroDocumento,
                                informacion,
                                "CODIGO DE VENTA",
                                celular);
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
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
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
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
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
            } else if (cbCliente.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(vbWindow, "Venta", "Seleccione un cliente.");
                cbCliente.requestFocus();
            } else if (cbComprobante.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(vbWindow, "Venta", "Seleccione el tipo de comprobante.");
                cbComprobante.requestFocus();
            } else if (lvProductoAgregados.getItems().isEmpty()) {
                Tools.AlertMessageWarning(vbWindow, "Venta", "No hay productos en la lista para vender.");
                txtSearch.requestFocus();
            } else if (total <= 0) {
                Tools.AlertMessageWarning(vbWindow, "Venta", "El total de la venta no puede ser menor que 0.");
            } else {

                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
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
                stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
                stage.show();

                ClienteTB clienteTB = new ClienteTB();
                clienteTB.setIdCliente(cbCliente.getSelectionModel().getSelectedItem().getIdCliente());
                clienteTB.setTipoDocumento(cbCliente.getSelectionModel().getSelectedItem().getTipoDocumento());
                clienteTB.setNumeroDocumento(cbCliente.getSelectionModel().getSelectedItem().getNumeroDocumento());
                clienteTB.setInformacion(cbCliente.getSelectionModel().getSelectedItem().getInformacion());
                clienteTB.setCelular(cbCliente.getSelectionModel().getSelectedItem().getCelular());
                clienteTB.setEmail(cbCliente.getSelectionModel().getSelectedItem().getEmail());
                clienteTB.setDireccion(cbCliente.getSelectionModel().getSelectedItem().getDireccion());

                VentaTB ventaTB = new VentaTB();
                ventaTB.setVendedor(Session.USER_ID);
                ventaTB.setComprobante(cbComprobante.getSelectionModel().getSelectedIndex() >= 0
                        ? cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento()
                        : 0);
                ventaTB.setComprobanteName(cbComprobante.getSelectionModel().getSelectedIndex() >= 0
                        ? cbComprobante.getSelectionModel().getSelectedItem().getNombre()
                        : "");
                ventaTB.setMoneda(cbMoneda.getSelectionModel().getSelectedIndex() >= 0 ? cbMoneda.getSelectionModel().getSelectedItem().getIdMoneda() : 0);
                ventaTB.setMonedaName(monedaSimbolo);
                ventaTB.setSerie(lblSerie.getText());
                ventaTB.setNumeracion(lblNumeracion.getText());
                ventaTB.setFechaVenta(Tools.getDate());
                ventaTB.setHoraVenta(Tools.getHour());
                ventaTB.setSubTotal(subTotal);
                ventaTB.setDescuento(descuento);
                ventaTB.setSubImporte(subTotalImporte);
                ventaTB.setImpuesto(totalImpuesto);
                ventaTB.setTotal(total);
                ventaTB.setClienteTB(clienteTB);
                ArrayList<SuministroTB> suministroTBs = new ArrayList<>();
                lvProductoAgregados.getItems().forEach(e -> suministroTBs.add(e.getSuministroTB()));
                controller.setInitComponents(ventaTB, suministroTBs);
            }
        } catch (IOException ex) {
            System.out.println("openWindowVentaProceso():" + ex.getLocalizedMessage());
        }
    }

    private void onEventCliente() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_CLIENTE_PROCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxClienteProcesoController controller = fXMLLoader.getController();
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Cliente", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
            controller.setValueAdd();
        } catch (IOException ex) {
            System.out.println("Cliente controller en openWindowAddCliente()" + ex.getLocalizedMessage());
        }
    }

    private void onEventProducto() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_SUMINISTROS_PROCESO_MODAL);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxSuministrosProcesoModalController controller = fXMLLoader.getController();
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Producto", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
            controller.setInitArticulo();
        } catch (IOException ex) {
            System.out.println("Error en producto lista:" + ex.getLocalizedMessage());
        }
    }

    private void onEventMovimientoCaja() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
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
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
        } catch (IOException ex) {
            Tools.println("Venta estructura nuevo onEventMovimientoCaja:" + ex.getLocalizedMessage());
        }
    }

    public void openWindowMostrarVentas() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_VENTA_MOSTRAR);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            //  FxVentaMostrarController controller = fXMLLoader.getController();
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Mostrar ventas", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
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
                cbCliente.requestFocus();
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
        paginacion = 1;
        searchTable(event, txtSearch.getText().trim());
        opcion = 1;
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
            onEventCliente();
        }
    }

    @FXML
    private void onActionCliente(ActionEvent event) {
        onEventCliente();
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

    public TextField getTxtSearch() {
        return txtSearch;
    }

    public ComboBox<MonedaTB> getCbMoneda() {
        return cbMoneda;
    }

    public ListView<BbItemProducto> getLvProductoAgregados() {
        return lvProductoAgregados;
    }

    public String getMonedaNombre() {
        return cbMoneda.getSelectionModel().getSelectedItem().getNombre();
    }

    public int getIdTipoComprobante() {
        return cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento();
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
