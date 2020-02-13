package controller.consultas.compras;

import controller.operaciones.compras.FxComprasProcesoController;
import controller.contactos.proveedores.FxProveedorListaController;
import controller.inventario.suministros.FxSuministrosListaController;
import controller.tools.FilesRouters;
import controller.tools.Session; 
import controller.tools.Tools;
import controller.tools.WindowStage;
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
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.CompraADO;
import model.CompraTB;
import model.DetalleCompraTB;
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.LoteTB;
import model.MonedaADO;
import model.MonedaTB;
import model.ProveedorADO;
import model.SuministroTB;
import model.TipoDocumentoADO;
import model.TipoDocumentoTB;

public class FxComprasEditarController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private VBox vbContenido;
    @FXML
    private TextField txtProveedor;
    @FXML
    private ComboBox<TipoDocumentoTB> cbComprobante;
    @FXML
    private TextField cbNumeracion;
    @FXML
    private DatePicker tpFechaCompra;
    @FXML
    private TableView<DetalleCompraTB> tvList;
    @FXML
    private TableColumn<DetalleCompraTB, String> tcArticulo;
    @FXML
    private TableColumn<DetalleCompraTB, String> tcCantidad;
    @FXML
    private TableColumn<DetalleCompraTB, String> tcCosto;
    @FXML
    private TableColumn<DetalleCompraTB, String> tcDescuento;
    //private TableColumn<SuministroTB, String> tcImpuesto;
    @FXML
    private TableColumn<SuministroTB, String> tcImporte;
    @FXML
    private Text lblSubTotal;
    @FXML
    private Text lblDescuento;
    @FXML
    private Text lblSubTotalNuevo;
    @FXML
    private Text lblTotal;
    @FXML
    private Text lblMonedaSubTotal;
    @FXML
    private Text lblMonedaDescuento;
    @FXML
    private Text lblMonedaSubTotalNuevo;
    @FXML
    private Text lblMonedaTotal;
    @FXML
    private ComboBox<MonedaTB> cbMoneda;
    @FXML
    private HBox hbAgregarImpuesto;
    @FXML
    private TextField txtObservaciones;
    @FXML
    private TextField txtNotas;
    @FXML
    private Button btnArticulo;

    private AnchorPane vbPrincipal;

    private String idProveedor;

    private double subImporte;

    private double descuento;

    private double subTotalImporte;

    private double totalImporte;

    private ArrayList<ImpuestoTB> arrayArticulosImpuesto;

    private ObservableList<LoteTB> loteTBs;

    private String monedaSimbolo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
        apWindow.setOnKeyReleased((KeyEvent event) -> {

            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case F1:
                        openWindowRegister();
                        break;
                    case F2:
                        openWindowSuministrasAdd();
                        break;
                    case F3:
                        openWindowArticulosEdit();
                        break;
                    case F4:
                        onViewRemove();
                        break;
                    case F5:
                        openWindowProvedores();
                        break;
                }
            }
        });
        idProveedor = "";
        monedaSimbolo = "M";
        loteTBs = FXCollections.observableArrayList();
        Tools.actualDate(Tools.getDate(), tpFechaCompra);

        arrayArticulosImpuesto = new ArrayList<>();
        ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> {
            arrayArticulosImpuesto.add(new ImpuestoTB(e.getIdImpuesto(), e.getNombreImpuesto(), e.getValor(), e.getPredeterminado()));
        });

        cbComprobante.getItems().clear();
        TipoDocumentoADO.GetDocumentoCombBox().forEach(e -> {
            cbComprobante.getItems().add(new TipoDocumentoTB(e.getIdTipoDocumento(), e.getNombre(), e.isPredeterminado()));
        });
        if (!cbComprobante.getItems().isEmpty()) {
            for (int i = 0; i < cbComprobante.getItems().size(); i++) {
                if (cbComprobante.getItems().get(i).isPredeterminado() == true) {
                    cbComprobante.getSelectionModel().select(i);
                    Session.DEFAULT_COMPROBANTE = i;
                    break;
                }
            }
        }

        cbMoneda.getItems().clear();
        cbMoneda.getItems().add(new MonedaTB(0, "Seleccione una moneda", "", false));
        MonedaADO.GetMonedasCombBox().forEach(e -> {
            cbMoneda.getItems().add(new MonedaTB(e.getIdMoneda(), e.getNombre(), e.getSimbolo(), e.getPredeterminado()));
        });

        if (!cbMoneda.getItems().isEmpty()) {
            for (int i = 0; i < cbMoneda.getItems().size(); i++) {
                if (cbMoneda.getItems().get(i).getPredeterminado() == true) {
                    cbMoneda.getSelectionModel().select(i);
                    monedaSimbolo = cbMoneda.getItems().get(i).getSimbolo();
                    break;
                }
            }
        }

        lblMonedaSubTotal.setText(monedaSimbolo);
        lblMonedaDescuento.setText(monedaSimbolo);
        lblMonedaSubTotalNuevo.setText(monedaSimbolo);
        lblMonedaTotal.setText(monedaSimbolo);

        initTable();

        tcArticulo.prefWidthProperty().bind(tvList.widthProperty().multiply(0.38));
        tcCantidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcCosto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.16));
        tcDescuento.prefWidthProperty().bind(tvList.widthProperty().multiply(0.16));
        //tcImpuesto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcImporte.prefWidthProperty().bind(tvList.widthProperty().multiply(0.16));

    }

    private void initTable() {
//        tcArticulo.setCellValueFactory(cellData -> Bindings.concat(
//                cellData.getValue().getClave() + "\n" + cellData.getValue().getNombreMarca()
//        ));
//        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(
//                Tools.roundingValue(cellData.getValue().getCantidad(), 2)));
//        tcCosto.setCellValueFactory(cellData -> Bindings.concat(
//                Tools.roundingValue(cellData.getValue().getCostoCompra(), 2)));
//        tcDescuento.setCellValueFactory(cellData -> Bindings.concat(
//                Tools.roundingValue(cellData.getValue().getDescuento(), 0) + "%"
//        ));
//        //tcImpuesto.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getImpuestoArticuloName()));
//        tcImporte.setCellValueFactory(cellData -> Bindings.concat(
//                Tools.roundingValue(cellData.getValue().getTotalImporte(), 2)));

    }

    public void setInitComprasValue(String idCompra) {
        setLoadDetalle(idCompra);
    }

    public void setInitComprasValue(String... value) {
        idProveedor = ProveedorADO.GetProveedorId(value[0]);
        txtProveedor.setText(value[1]);
    }

    private void setLoadDetalle(String idCompra) {

        ExecutorService executor = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            protected ArrayList<Object> call() {
                ArrayList<Object> objects = CompraADO.GetComprasForEditar(idCompra);
                return objects;
            }
        };

        task.setOnScheduled(e -> {

        });
        task.setOnRunning(e -> {

        });
        task.setOnFailed(e -> {

        });
        task.setOnSucceeded(e -> {
            ArrayList<Object> arrayList = task.getValue();
            if (!arrayList.isEmpty()) {
                if (arrayList.get(0) != null) {
                    CompraTB compraTB = (CompraTB) arrayList.get(0);
                    idProveedor = compraTB.getProveedor();
                    txtProveedor.setText(compraTB.getProveedorTB().getRazonSocial());
                    Tools.actualDate(compraTB.getFechaCompra(), tpFechaCompra);
                    cbNumeracion.setText(compraTB.getNumeracion());

                    if (!cbComprobante.getItems().isEmpty()) {
                        for (int i = 0; i < cbComprobante.getItems().size(); i++) {
                            if (cbComprobante.getItems().get(i).getIdTipoDocumento() == compraTB.getComprobante()) {
                                cbComprobante.getSelectionModel().select(i);
                                break;
                            }
                        }
                    }

                    if (!cbMoneda.getItems().isEmpty()) {
                        for (int i = 0; i < cbMoneda.getItems().size(); i++) {
                            if (cbMoneda.getItems().get(i).getIdMoneda() == compraTB.getTipoMoneda()) {
                                cbMoneda.getSelectionModel().select(i);
                                monedaSimbolo = cbMoneda.getItems().get(i).getSimbolo();
                                break;
                            }
                        }
                    }

                    txtObservaciones.setText(compraTB.getObservaciones());
                    txtNotas.setText(compraTB.getNotas());

                }
                if (arrayList.get(1) != null) {
//                    tvList.setItems((ObservableList<SuministroTB>) arrayList.get(1));
                    calculateTotals();
                }
            }
            vbContenido.setDisable(false);
        });
        executor.execute(task);
        if (!executor.isShutdown()) {
            executor.shutdown();
        }

    }

    private void openWindowRegister() {
        try {
            if (txtProveedor.getText().isEmpty() && idProveedor.equalsIgnoreCase("")) {
                Tools.AlertMessageWarning(apWindow, "Editar compra", "Ingrese un proveedor, por favor.");
                txtProveedor.requestFocus();
            } else if (cbComprobante.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(apWindow, "Editar compra", "Seleccione tipo de comprobante, por favor.");
                cbComprobante.requestFocus();
            } else if (cbNumeracion.getText().isEmpty()) {
                Tools.AlertMessageWarning(apWindow, "Editar compra", "Ingrese la numeración del comprobante, por favor.");
                cbNumeracion.requestFocus();
            } else if (tpFechaCompra.getValue() == null) {
                Tools.AlertMessageWarning(apWindow, "Editar compra", "Ingrese la fecha de compra, por favor.");
                tpFechaCompra.requestFocus();
            } else if (tvList.getItems().isEmpty()) {
                Tools.AlertMessageWarning(apWindow, "Editar compra", "Ingrese algún producto para realizar la compra, por favor.");
                btnArticulo.requestFocus();
            } else {
                CompraTB compraTB = new CompraTB();
                compraTB.setProveedor(idProveedor);
                compraTB.setComprobante(cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento());
                compraTB.setNumeracion(cbNumeracion.getText().trim());
                compraTB.setTipoMoneda(cbMoneda.getSelectionModel().getSelectedIndex() >= 1
                        ? cbMoneda.getSelectionModel().getSelectedItem().getIdMoneda() : 0);
                compraTB.setFechaCompra(Tools.getDatePicker(tpFechaCompra));
                compraTB.setHoraCompra(Tools.getHour());
                compraTB.setSubTotal(Double.parseDouble(lblSubTotal.getText()));
                compraTB.setDescuento(Double.parseDouble(lblDescuento.getText()));
                compraTB.setTotal(Double.parseDouble(lblTotal.getText()));
                compraTB.setObservaciones(txtObservaciones.getText().isEmpty() ? "" : txtObservaciones.getText());
                compraTB.setNotas(txtNotas.getText().isEmpty() ? "" : txtNotas.getText());
                compraTB.setUsuario(Session.USER_ID);

                URL url = getClass().getResource(FilesRouters.FX_COMPRAS_PROCESO);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
//            Controlller here
                FxComprasProcesoController controller = fXMLLoader.getController();
                controller.setInitComprasEditarController(this);
//                controller.setLoadProcess(compraTB, tvList, loteTBs, txtProveedor.getText(), monedaSimbolo);

                Stage stage = WindowStage.StageLoaderModal(parent, "Pago de la compra", apWindow.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();

                stage.show();

            }
        } catch (IOException ex) {
            System.out.println("Controller compras" + ex.getLocalizedMessage());
        }
    }

    private void openWindowSuministrasAdd() {
        try {
            URL url = getClass().getResource(FilesRouters.FX_SUMINISTROS_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxSuministrosListaController controller = fXMLLoader.getController();
            controller.setInitComprasEditarController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Suministros", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();

            stage.show();
            controller.fillSuministrosTablePaginacion();
        } catch (IOException ex) {
            System.out.println("Controller compras" + ex.getLocalizedMessage());
        }

    }

    private void openWindowArticulosEdit() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
//            ObservableList<SuministroTB> suministroTBs;
//            suministroTBs = tvList.getSelectionModel().getSelectedItems();
//            suministroTBs.forEach(e -> {
//                try {
//                    URL url = getClass().getResource(FilesRouters.FX_SUMINISTROS_COMPRA);
//                    FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
//                    Parent parent = fXMLLoader.load(url.openStream());
//                    //Controlller here
//                    FxSuministrosCompraController controller = fXMLLoader.getController();
//                    controller.setInitComprasEditarController(this);
//                    //
//                    Stage stage = WindowStage.StageLoaderModal(parent, "Editar suministro", apWindow.getScene().getWindow());
//                    stage.setResizable(false);
//                    stage.show();
//
//                    SuministroTB suministroTB = new SuministroTB();
//                    suministroTB.setIdSuministro(e.getIdSuministro());
//                    suministroTB.setClave(e.getClave());
//                    suministroTB.setNombreMarca(e.getNombreMarca());
//                    suministroTB.setCantidad(e.getCantidad());
//                    suministroTB.setCostoCompra(e.getCostoCompra());
//                    suministroTB.setCostoCompraReal(e.getCostoCompraReal());
//
//                    suministroTB.setPrecioVentaGeneral(e.getPrecioVentaGeneral());
//                    suministroTB.setPrecioMargenGeneral(e.getPrecioMargenGeneral());
//                    suministroTB.setPrecioUtilidadGeneral(e.getPrecioUtilidadGeneral());
//
//                    suministroTB.setDescuento(e.getDescuento());
//                    suministroTB.setTotalImporte(e.getTotalImporte());
//                    suministroTB.setImpuestoArticulo(e.getImpuestoArticulo());
//                    suministroTB.setLote(e.isLote());
//                    suministroTB.setUnidadVenta(e.getUnidadVenta());
//                    suministroTB.setDescripcion(e.getDescripcion());
////                    controller.setLoadEdit(suministroTB, tvList.getSelectionModel().getSelectedIndex(), loteTBs);
//                } catch (IOException ex) {
//                    System.out.println(ex.getLocalizedMessage());
//                }
//            });
        } else {
            Tools.AlertMessageWarning(apWindow, "Editar Compra", "Seleccione un producto para editarlo.");
        }
    }

    private void onViewRemove() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
//            short confirmation = Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Compras", "¿Esta seguro de quitar el artículo?", true);
//            if (confirmation == 1) {
//                ObservableList<SuministroTB> observableList, suministroTBs;
//                observableList = tvList.getItems();
//                suministroTBs = tvList.getSelectionModel().getSelectedItems();
//                suministroTBs.forEach(e -> {
//                    for (int i = 0; i < loteTBs.size(); i++) {
//                        if (loteTBs.get(i).getIdArticulo().equals(e.getIdSuministro())) {
//                            loteTBs.remove(i);
//                            i--;
//                        }
//                    }
//                    observableList.remove(e);
//                });
//                calculateTotals();
//            }
        } else {
            Tools.AlertMessageWarning(apWindow, "Editar Compra", "Seleccione un producto para removerlo.");
        }
    }

    private void openWindowProvedores() {
        try {
            URL url = getClass().getResource(FilesRouters.FX_PROVEEDORES_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxProveedorListaController controller = fXMLLoader.getController();
            controller.setInitComprasEditarController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Proveedor", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();

            stage.show();
            controller.fillCustomersTable("");
        } catch (IOException ex) {
            System.out.println("Controller compras" + ex.getLocalizedMessage());
        }
    }

    public void calculateTotals() {

//        tvList.getItems().forEach(e -> subImporte += e.getSubImporte());
//        lblSubTotal.setText(Tools.roundingValue(subImporte, 2));
//        subImporte = 0;
//
//        tvList.getItems().forEach(e -> descuento += e.getDescuentoSumado());
//        lblDescuento.setText((Tools.roundingValue(descuento * (-1), 2)));
//        descuento = 0;
//
//        tvList.getItems().forEach(e -> subTotalImporte += e.getTotalImporte());
//        lblSubTotalNuevo.setText(Tools.roundingValue(subTotalImporte, 2));
//        subTotalImporte = 0;
//
//        tvList.getItems().forEach(e -> totalImporte += e.getTotalImporte());
//        lblTotal.setText(Tools.roundingValue((totalImporte), 2));
//        totalImporte = 0;
    }

    public void disposeWindow() {
        Tools.Dispose(apWindow);
    }

    @FXML
    private void onKeyPressedRegister(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowRegister();
        }
    }

    @FXML
    private void onActionRegister(ActionEvent event) {
        openWindowRegister();
    }

    @FXML
    private void onKeyPressedAdd(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowSuministrasAdd();
        }
    }

    @FXML
    private void onActionAdd(ActionEvent event) {
        openWindowSuministrasAdd();
    }

    @FXML
    private void onKeyPressedEdit(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowArticulosEdit();
        }
    }

    @FXML
    private void onActionEdit(ActionEvent event) {
        openWindowArticulosEdit();
    }

    @FXML
    private void onKeyPressedRemover(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onViewRemove();
        }
    }

    @FXML
    private void onActionRemover(ActionEvent event) {
        onViewRemove();
    }

    @FXML
    private void onKeyPressedProviders(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowProvedores();
        }
    }

    @FXML
    private void onActionProviders(ActionEvent event) {
        openWindowProvedores();
    }

    @FXML
    private void onKeyTypedNumeracion(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c != '-')
                && (c != '/') && (c != '+') && (c != '*') && (c != '(') && (c != ')') && (c != '|')) {
            event.consume();
        }
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) {
        if (event.getClickCount() == 2) {
            openWindowArticulosEdit();
        }
    }

    @FXML
    private void onActionMoneda(ActionEvent event) {
        if (cbMoneda.getSelectionModel().getSelectedIndex() >= 1) {
            monedaSimbolo = cbMoneda.getSelectionModel().getSelectedItem().getSimbolo();
            lblMonedaSubTotal.setText(monedaSimbolo);
            lblMonedaSubTotalNuevo.setText(monedaSimbolo);
            lblMonedaDescuento.setText(monedaSimbolo);
            lblMonedaTotal.setText(monedaSimbolo);
            calculateTotals();
        }
    }

    public TableView<DetalleCompraTB> getTvList() {
        return tvList;
    }

    public ObservableList<LoteTB> getLoteTBs() {
        return loteTBs;
    }

    public HBox getHbAgregarImpuesto() {
        return hbAgregarImpuesto;
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
