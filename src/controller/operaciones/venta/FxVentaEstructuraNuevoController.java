package controller.operaciones.venta;

import controller.contactos.clientes.FxClienteProcesoController;
import controller.inventario.suministros.FxSuministrosProcesoModalController;
import controller.tools.*;

import java.awt.print.Book;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
import javafx.stage.Stage;
import javafx.util.Duration;
import model.ClienteTB;
import model.ComprobanteADO;
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.MonedaADO;
import model.MonedaTB;
import model.SuministroADO;
import model.SuministroTB;
import model.TipoDocumentoADO;
import model.TipoDocumentoTB;
import model.VentaTB;
import org.controlsfx.control.Notifications;

import javax.print.DocPrintJob;
import javax.print.PrintException;
import model.ClienteADO;
import model.DetalleADO;
import model.DetalleTB;

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
    private TextField txtDireccionCliente;

    private AnchorPane vbPrincipal;

    private BillPrintable billPrintable;

    private ObservableList<SuministroTB> listSuministros;

    private ArrayList<ImpuestoTB> arrayArticulosImpuesto;

    private AnchorPane hbEncabezado;

    private AnchorPane hbDetalleCabecera;

    private AnchorPane hbPie;

    private String monedaSimbolo;

    private String idCliente;

    private int totalPaginacion;

    private int paginacion;

    private short opcion;

    private boolean state;

    private double subTotal;

    private double descuento;

    private double subTotalImporte;

    private double totalImporte;

    private double total;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        arrayArticulosImpuesto = new ArrayList<>();
        listSuministros = FXCollections.observableArrayList();
        billPrintable = new BillPrintable();
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

        arrayArticulosImpuesto.clear();
        ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> arrayArticulosImpuesto.add(new ImpuestoTB(e.getIdImpuesto(), e.getNombreImpuesto(), e.getValor(), e.getPredeterminado())));

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
        DetalleADO.GetDetailId("0003").forEach(e -> cbTipoDocumento.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre())));

        if (!cbTipoDocumento.getItems().isEmpty()) {
            for (DetalleTB detalleTB : cbTipoDocumento.getItems()) {
                if (detalleTB.getIdDetalle().get() == Session.CLIENTE_TIPO_DOCUMENTO) {
                    cbTipoDocumento.getSelectionModel().select(detalleTB);
                    break;
                }
            }
        }

        idCliente = Session.CLIENTE_ID;
        txtNumeroDocumento.setText(Session.CLIENTE_NUMERO_DOCUMENTO);
        txtDatosCliente.setText(Session.CLIENTE_DATOS);
        txtDireccionCliente.setText(Session.CLIENTE_DIRECCION);
        txtCelularCliente.setText("");
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

    public void fillSuministrosTable(short tipo, String value) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                return SuministroADO.ListSuministrosListaView(tipo, value, (paginacion - 1) * 10, 10);
            }
        };

        task.setOnSucceeded(e -> {
            fpProductos.getChildren().clear();
            listSuministros.clear();
            ArrayList<Object> objects = task.getValue();
            if (!objects.isEmpty()) {
                fpProductos.setAlignment(Pos.TOP_CENTER);
                listSuministros.addAll((ObservableList<SuministroTB>) objects.get(0));
                listSuministros.stream().map(tvList1 -> {
                    VBox vBox = new VBox();
                    vBox.setMinWidth(Control.USE_PREF_SIZE);
                    vBox.setPrefWidth(300);
                    vBox.maxWidth(Control.USE_PREF_SIZE);
                    vBox.setStyle("-fx-spacing: 0.4166666666666667em;-fx-padding: 0.4166666666666667em;-fx-border-color:  #0478b2;-fx-border-width:1px;-fx-background-color:transparent;");
                    vBox.setAlignment(Pos.TOP_CENTER);
                    File fileImage = new File(tvList1.getImagenTB());
                    ImageView imageView = new ImageView(new Image(fileImage.exists() ? fileImage.toURI().toString() : "/view/image/no-image.png"));
                    imageView.setFitWidth(160);
                    imageView.setFitHeight(160);
                    vBox.getChildren().add(imageView);

                    Label lblCodigo = new Label(tvList1.getClave());
                    lblCodigo.getStyleClass().add("labelOpenSansRegular14");
                    lblCodigo.setTextFill(Color.web("#020203"));
                    vBox.getChildren().add(lblCodigo);

                    Label lblProducto = new Label(tvList1.getNombreMarca());
                    lblProducto.getStyleClass().add("labelRobotoBold14");
                    lblProducto.setTextFill(Color.web("#020203"));
                    lblProducto.setWrapText(true);
                    lblProducto.setTextAlignment(TextAlignment.CENTER);
                    vBox.getChildren().add(lblProducto);

                    Label lblMarca = new Label(tvList1.getMarcaName());
                    lblMarca.getStyleClass().add("labelOpenSansRegular14");
                    lblMarca.setTextFill(Color.web("#1a2226"));
                    vBox.getChildren().add(lblMarca);

                    double impuestoimpuesto = getTaxValue(tvList1.getImpuestoArticulo());
                    double impuestototal = Tools.calculateTax(impuestoimpuesto, tvList1.getPrecioVentaGeneral());
                    double precioventa = tvList1.getPrecioVentaGeneral() + impuestototal;

                    Label lblTotalProducto = new Label(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(precioventa, 2));
                    lblTotalProducto.getStyleClass().add("labelRobotoBold18");
                    lblTotalProducto.setTextFill(Color.web("#0478b2"));
                    lblTotalProducto.maxWidth(Double.MAX_VALUE);
                    vBox.getChildren().add(lblTotalProducto);

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
                    return button;
                }).forEachOrdered(vBox -> fpProductos.getChildren().add(vBox));
                totalPaginacion = (int) (Math.ceil(((Integer) objects.get(1)) / 10.00));
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
            lblLoad.getStyleClass().add("labelRoboto16");
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

        suministroTB.setImpuestoOperacion(getTaxValueOperacion(tvList1.getImpuestoArticulo()));
        suministroTB.setImpuestoArticulo(tvList1.getImpuestoArticulo());
        suministroTB.setImpuestoArticuloName(getTaxName(tvList1.getImpuestoArticulo()));
        suministroTB.setImpuestoValor(getTaxValue(tvList1.getImpuestoArticulo()));
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

    public int getTaxValueOperacion(int impuesto) {
        int valor = 0;
        for (ImpuestoTB impuestoTB : arrayArticulosImpuesto) {
            if (impuestoTB.getIdImpuesto() == impuesto) {
                valor = impuestoTB.getOperacion();
                break;
            }
        }
        return valor;
    }

    public double getTaxValue(int impuesto) {
        double valor = 0;
        for (ImpuestoTB impuestoTB : arrayArticulosImpuesto) {
            if (impuestoTB.getIdImpuesto() == impuesto) {
                valor = impuestoTB.getValor();
                break;
            }
        }
        return valor;
    }

    public String getTaxName(int impuesto) {
        String valor = "";
        for (ImpuestoTB impuestoTB : arrayArticulosImpuesto) {
            if (impuestoTB.getIdImpuesto() == impuesto) {
                valor = impuestoTB.getNombreImpuesto();
                break;
            }
        }
        return valor;
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
        boolean addElement = false;
        double sumaElement = 0;
        double totalImpuestos = 0;
        if (!lvProductoAgregados.getItems().isEmpty()) {
            for (ImpuestoTB impuestoTB : arrayArticulosImpuesto) {
                for (int i = 0; i < lvProductoAgregados.getItems().size(); i++) {
                    if (impuestoTB.getIdImpuesto() == lvProductoAgregados.getItems().get(i).getSuministroTB().getImpuestoArticulo()) {
                        addElement = true;
                        sumaElement += lvProductoAgregados.getItems().get(i).getSuministroTB().getImpuestoSumado();
                    }
                }
                if (addElement) {
//                    gpTotales.add(addLabelTitle(arrayArticulosImpuesto.get(k).getNombreImpuesto().substring(0, 1).toUpperCase()
//                            + "" + arrayArticulosImpuesto.get(k).getNombreImpuesto().substring(1, arrayArticulosImpuesto.get(k).getNombreImpuesto().length()).toLowerCase(),
//                            Pos.CENTER_LEFT), 0, k + 1);
//                    gpTotales.add(addLabelTotal(monedaSimbolo + " " + Tools.roundingValue(sumaElement, 2), Pos.CENTER_RIGHT), 1, k + 1);
                    totalImpuestos += sumaElement;

                    addElement = false;
                    sumaElement = 0;
                }
            }
        }

        totalImporte = 0;
        total = 0;
        lvProductoAgregados.getItems().forEach(e -> totalImporte += e.getSuministroTB().getTotalImporte());
        total = totalImporte + totalImpuestos;
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

        idCliente = Session.CLIENTE_ID;
        txtNumeroDocumento.setText(Session.CLIENTE_NUMERO_DOCUMENTO);
        txtDatosCliente.setText(Session.CLIENTE_DATOS);
        txtDireccionCliente.setText(Session.CLIENTE_DIRECCION);
        txtCelularCliente.setText("");

        loadDataComponent();
        lblPaginaActual.setText(paginacion + "");
        lblPaginaSiguiente.setText(totalPaginacion + "");
        lblTotal.setText(monedaSimbolo + " 0.00");
        txtSearch.clear();
        txtSearch.requestFocus();
        calculateTotales();
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

        task.setOnSucceeded(e -> {
            ClienteTB clienteTB = task.getValue();
            if (clienteTB != null) {
                idCliente = clienteTB.getIdCliente();
                txtDatosCliente.setText(clienteTB.getInformacion());
                txtDireccionCliente.setText(clienteTB.getDireccion());
                txtCelularCliente.setText(clienteTB.getCelular());
                for (int i = 0; i < cbTipoDocumento.getItems().size(); i++) {
                    if (cbTipoDocumento.getItems().get(i).getIdDetalle().get() == clienteTB.getTipoDocumento()) {
                        cbTipoDocumento.getSelectionModel().select(i);
                        break;
                    }
                }
            } else {
                idCliente = "";
                txtDatosCliente.setText("");
                txtDireccionCliente.setText("");
                txtCelularCliente.setText("");
            }
            txtNumeroDocumento.setDisable(false);
        });

        task.setOnFailed(e -> txtNumeroDocumento.setDisable(false));

        task.setOnScheduled(e -> txtNumeroDocumento.setDisable(true));

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }

    }

    public void imprimirVenta(String serieNumeracion, String codigoVenta, String efectivo, String vuelto, boolean ticket) {
        if (Session.TICKET_VENTA_ID == 0 && Session.TICKET_VENTA_RUTA.equalsIgnoreCase("")) {
            Tools.AlertMessageWarning(vbWindow, "Venta", "No hay un diseño predeterminado para la impresión, configure su ticket en la sección configuración/tickets.");
            if (ticket) {
                resetVenta();
            }
            return;
        }
        if (!Session.ESTADO_IMPRESORA && Session.NOMBRE_IMPRESORA.equalsIgnoreCase("")) {
            Tools.AlertMessageWarning(vbWindow, "Venta", "No hay ruta de impresión, presione F8 o has un click en la opción impresora del mismo formulario actual, para configurar la ruta de impresión..");
            if (ticket) {
                resetVenta();
            }
            return;
        }

        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        try {
            Task<String> task = new Task<String>() {
                @Override
                public String call() {

                    try {
                        billPrintable.loadEstructuraTicket(Session.TICKET_VENTA_ID, Session.TICKET_VENTA_RUTA, hbEncabezado, hbDetalleCabecera, hbPie);

                        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
                            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
                            billPrintable.hbEncebezado(box,
                                    ticket ? cbComprobante.getSelectionModel().getSelectedItem().getNombre() : "PRE VENTA",
                                    serieNumeracion,
                                    txtNumeroDocumento.getText().trim().toUpperCase(),
                                    txtDatosCliente.getText().trim().toUpperCase(),
                                    txtCelularCliente.getText().trim().toUpperCase(),
                                    txtDireccionCliente.getText().trim().toUpperCase(),
                                    codigoVenta);
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
                                    efectivo,
                                    vuelto,
                                    txtNumeroDocumento.getText().trim().toUpperCase(),
                                    txtDatosCliente.getText().trim().toUpperCase(),
                                    codigoVenta,
                                    txtCelularCliente.getText().trim().trim().toUpperCase());
                        }

                        billPrintable.generatePDFPrint(hbEncabezado, hbDetalle, hbPie);

                        DocPrintJob job = billPrintable.findPrintService(Session.NOMBRE_IMPRESORA, PrinterJob.lookupPrintServices()).createPrintJob();

                        if (job != null) {
                            PrinterJob pj = PrinterJob.getPrinterJob();
                            pj.setPrintService(job.getPrintService());
                            pj.setJobName(Session.NOMBRE_IMPRESORA);
                            Book book = new Book();
                            book.append(billPrintable, billPrintable.getPageFormat(pj));
                            pj.setPageable(book);
                            pj.print();
                            if (Session.CORTAPAPEL_IMPRESORA) {
                                billPrintable.printCortarPapel(Session.NOMBRE_IMPRESORA);
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
//                if (!task.isRunning()) {
//                    if (alert != null) {
//                        ((Stage) (alert.getDialogPane().getScene().getWindow())).close();
//                    }
//                }
                String result = task.getValue();
                if (result.equalsIgnoreCase("completed")) {
//                    Tools.AlertMessageInformation(window, "Ventas", "Se completo el proceso de impresión correctamente.");
//                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                    Image image = new Image("/view/image/information_large.png");
                    Notifications notifications = Notifications.create()
                            .title("Envío de impresión")
                            .text("Se completo el proceso de impresión correctamente.")
                            .graphic(new ImageView(image))
                            .hideAfter(Duration.seconds(5))
                            .position(Pos.BOTTOM_RIGHT)
                            .onAction(Tools::println);
                    notifications.darkStyle();
                    notifications.show();
                    if (ticket) {
                        resetVenta();
                    }
                } else if (result.equalsIgnoreCase("error_name")) {
                    Image image = new Image("/view/image/warning_large.png");
                    Notifications notifications = Notifications.create()
                            .title("Envío de impresión")
                            .text("Error en encontrar el nombre de la impresión por problemas de puerto o driver.")
                            .graphic(new ImageView(image))
                            .hideAfter(Duration.seconds(10))
                            .position(Pos.CENTER)
                            .onAction(Tools::println);
                    notifications.darkStyle();
                    notifications.show();
                    if (ticket) {
                        resetVenta();
                    }
                } else {
//                    Tools.AlertMessageError(window, "Ventas", result);
//                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                    Image image = new Image("/view/image/error_large.png");
                    Notifications notifications = Notifications.create()
                            .title("Envío de impresión")
                            .text("Error en la configuración de su impresora: " + result)
                            .graphic(new ImageView(image))
                            .hideAfter(Duration.seconds(10))
                            .position(Pos.CENTER)
                            .onAction(Tools::println);
                    notifications.darkStyle();
                    notifications.show();
                    if (ticket) {
                        resetVenta();
                    }
                }
            });
            task.setOnFailed(w -> {
//                if (alert != null) {
//                    ((Stage) (alert.getDialogPane().getScene().getWindow())).close();
//                }
//                Tools.AlertMessageWarning(window, "Ventas", "Se produjo un problema en el proceso de envío, intente nuevamente.");
//                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                Image image = new Image("/view/image/warning_large.png");
                Notifications notifications = Notifications.create()
                        .title("Envío de impresión")
                        .text("Se produjo un problema en el proceso de envío, \n intente nuevamente o comuníquese con su proveedor del sistema.")
                        .graphic(new ImageView(image))
                        .hideAfter(Duration.seconds(10))
                        .position(Pos.BOTTOM_RIGHT)
                        .onAction(Tools::println);
                notifications.darkStyle();
                notifications.show();
                if (ticket) {
                    resetVenta();
                }
            });

            task.setOnScheduled(w -> {
                Image image = new Image("/view/image/print.png");
                Notifications notifications = Notifications.create()
                        .title("Envío de impresión")
                        .text("Se envió la impresión a la cola, este\n proceso puede tomar unos segundos.")
                        .graphic(new ImageView(image))
                        .hideAfter(Duration.seconds(5))
                        .position(Pos.BOTTOM_RIGHT)
                        .onAction(Tools::println);
                notifications.darkStyle();
                notifications.show();

//                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
//                alert = Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.NONE, "Se envió la impresión a la cola, este proceso puede tomar unos segundos.");
            });
            exec.execute(task);

        } finally {
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

    private void openWindowImpresora() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_IMPRESORA_TICKET);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxImpresoraTicketController controller = fXMLLoader.getController();
//            controller.setInitVentaEstructuraController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Configurar impresora", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w
                    -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE)
            );
            controller.loadConfigurationDefauld();
            stage.show();
        } catch (IOException ex) {
            System.out.println("openWindowImpresora():" + ex.getLocalizedMessage());
        }
    }

    public void onEventCobrar() {
        try {
            if (cbMoneda.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(vbWindow, "Venta", "Seleccione la moneda ha usar.");
            } else if (txtNumeroDocumento.getText().trim().equalsIgnoreCase("")) {
                Tools.AlertMessageWarning(vbWindow, "Venta", "Ingrese el número del documento del cliente.");
            } else if (txtDatosCliente.getText().trim().equalsIgnoreCase("")) {
                Tools.AlertMessageWarning(vbWindow, "Venta", "Ingrese los datos del cliente.");
            } else if (cbComprobante.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(vbWindow, "Venta", "Seleccione el tipo de comprobante.");
            } else if (lvProductoAgregados.getItems().isEmpty()) {
                Tools.AlertMessageWarning(vbWindow, "Venta", "No hay productos en la lista para vender.");
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
                clienteTB.setIdCliente(idCliente);
                clienteTB.setTipoDocumento(cbTipoDocumento.getSelectionModel().getSelectedItem().getIdDetalle().get());
                clienteTB.setNumeroDocumento(txtNumeroDocumento.getText().trim().toUpperCase());
                clienteTB.setInformacion(txtDatosCliente.getText().trim().toUpperCase());
                clienteTB.setDireccion(txtDireccionCliente.getText().trim().toUpperCase());
                clienteTB.setCelular(txtCelularCliente.getText().trim().toUpperCase());

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

    @FXML
    private void onKeyPressedWindow(KeyEvent event) {
        if (null != event.getCode()) {
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
                    onEventCliente();
                    break;
                case F5:
                    if (!state) {
                        paginacion = 1;
                        fillSuministrosTable((short) 0, "");
                        opcion = 0;
                    }
                    break;
                case F6:
                    cbComprobante.requestFocus();
                    break;
                case F7:
                    onEventMovimientoCaja();
                    break;
                default:
                    break;
            }
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

    @FXML
    private void onKeyPressReload(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!state) {
                paginacion = 1;
                fillSuministrosTable((short) 0, "");
                opcion = 0;
            }
        }
    }

    @FXML
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
        if (lvProductoAgregados.getSelectionModel().getSelectedIndex() >= 0) {
            openWindowDetalleProducto(lvProductoAgregados.getSelectionModel().getSelectedIndex(), lvProductoAgregados.getSelectionModel().getSelectedItem());
            event.consume();
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
    private void onKeyPressedCobrar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventCobrar();
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
            imprimirVenta("LISTA DE PEDIDO", "000000000", "00", "00", false);
        }
    }

    @FXML
    private void onActionTicket(ActionEvent event) {
        imprimirVenta("LISTA DE PEDIDO", "000000000", "00", "00", false);
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
    private void onKeyPressedImpresora(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowImpresora();
        }
    }

    @FXML
    private void onActionImpresora(ActionEvent event) {
        openWindowImpresora();
    }

    @FXML
    private void onKeyTypedNumeroDocumento(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
            event.consume();
        }
    }

    @FXML
    private void onActionSearchCliente(ActionEvent event) {
        onExecuteCliente((short) 2, txtNumeroDocumento.getText().trim());
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
