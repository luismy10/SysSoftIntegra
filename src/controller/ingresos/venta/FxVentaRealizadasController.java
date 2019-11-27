package controller.ingresos.venta;

import controller.configuracion.empleados.FxEmpleadosListaController;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.DetalleADO;
import model.DetalleTB;
import model.PrivilegioTB;
import model.TipoDocumentoADO;
import model.TipoDocumentoTB;
import model.VentaADO;
import model.VentaTB;

public class FxVentaRealizadasController implements Initializable {

    @FXML
    private VBox window;
    @FXML
    private DatePicker dtFechaInicial;
    @FXML
    private DatePicker dtFechaFinal;
    @FXML
    private Label lblLoad;
    @FXML
    private TableView<VentaTB> tvList;
    @FXML
    private TableColumn<VentaTB, Integer> tcId;
    @FXML
    private TableColumn<VentaTB, String> tcFechaVenta;
    @FXML
    private TableColumn<VentaTB, String> tcCliente;
    @FXML
    private TableColumn<VentaTB, String> tcTipo;
    @FXML
    private TableColumn<Label, String> tcEstado;
    @FXML
    private TableColumn<VentaTB, String> tcSerie;
    @FXML
    private TableColumn<VentaTB, String> tcTotal;
    @FXML
    private ComboBox<DetalleTB> cbEstado;
    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<TipoDocumentoTB> cbComprobante;
    @FXML
    private HBox hbVendedor;
    @FXML
    private TextField txtVendedor;
    @FXML
    private Button btnMostrar;
    @FXML
    private Button btnRecargar;
    @FXML
    private Button btnVendedor;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    private String idEmpleado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        tcFechaVenta.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getFechaVenta() + "\n"
                + cellData.getValue().getHoraVenta()
        ));
        tcCliente.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getCliente()));
        tcTipo.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getTipoName()));
        tcEstado.setCellValueFactory(new PropertyValueFactory<>("estadoLabel"));
        tcSerie.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getComprobanteName() + "\n" + cellData.getValue().getSerie() + "-" + cellData.getValue().getNumeracion()));
        tcTotal.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMonedaName() + " " + Tools.roundingValue(cellData.getValue().getTotal(), 2)));

        tcId.prefWidthProperty().bind(tvList.widthProperty().multiply(0.06));
        tcFechaVenta.prefWidthProperty().bind(tvList.widthProperty().multiply(0.17));
        tcSerie.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        tcCliente.prefWidthProperty().bind(tvList.widthProperty().multiply(0.23));
        tcTipo.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        tcEstado.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        tcTotal.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));

        cbEstado.getItems().add(new DetalleTB(new SimpleIntegerProperty(0), new SimpleStringProperty("TODOS")));
        DetalleADO.GetDetailIdName("2", "0009", "").forEach(e -> {
            cbEstado.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        cbEstado.getSelectionModel().select(0);

        cbComprobante.getItems().add(new TipoDocumentoTB(0, "TODOS"));
        TipoDocumentoADO.GetDocumentoCombBox().forEach(e -> {
            cbComprobante.getItems().add(new TipoDocumentoTB(e.getIdTipoDocumento(), e.getNombre()));
        });
        cbComprobante.getSelectionModel().select(0);

        Tools.actualDate(Tools.getDate(), dtFechaInicial);
        Tools.actualDate(Tools.getDate(), dtFechaFinal);
        
        idEmpleado = Session.USER_ID;
        txtVendedor.setText(Session.USER_NAME.toUpperCase());        
        
    }

    public void loadPrivilegios(ObservableList<PrivilegioTB> privilegioTBs) {
        if (privilegioTBs.get(0).getIdPrivilegio() != 0 && !privilegioTBs.get(0).isEstado()) {
            btnMostrar.setDisable(true);
        }
        if (privilegioTBs.get(1).getIdPrivilegio() != 0 && !privilegioTBs.get(1).isEstado()) {
            btnRecargar.setDisable(true);
        }
        if (privilegioTBs.get(2).getIdPrivilegio() != 0 && !privilegioTBs.get(2).isEstado()) {
            dtFechaInicial.setDisable(true);
        }
        if (privilegioTBs.get(3).getIdPrivilegio() != 0 && !privilegioTBs.get(3).isEstado()) {
            dtFechaFinal.setDisable(true);
        }
        if (privilegioTBs.get(4).getIdPrivilegio() != 0 && !privilegioTBs.get(4).isEstado()) {
            cbComprobante.setDisable(true);
        }
        if (privilegioTBs.get(5).getIdPrivilegio() != 0 && !privilegioTBs.get(5).isEstado()) {
            cbEstado.setDisable(true);
        }
        if (privilegioTBs.get(6).getIdPrivilegio() != 0 && !privilegioTBs.get(6).isEstado()) {
            hbVendedor.setDisable(true);
        }
        if (privilegioTBs.get(7).getIdPrivilegio() != 0 && !privilegioTBs.get(7).isEstado()) {
            txtSearch.setDisable(true);
        }
    }

    public void loadInit() {
        if (dtFechaInicial.getValue() != null && dtFechaFinal.getValue() != null) {
            fillVentasTable((short) 0, "", Tools.getDatePicker(dtFechaInicial), Tools.getDatePicker(dtFechaFinal),
                    cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento(),
                    cbEstado.getSelectionModel().getSelectedItem().getIdDetalle().get(),
                   idEmpleado);
        }
    }

    private void fillVentasTable(short opcion, String value, String fechaInicial, String fechaFinal, int comprobante, int estado, String usuario) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<ObservableList<VentaTB>> task = new Task<ObservableList<VentaTB>>() {
            @Override
            public ObservableList<VentaTB> call() {
                return VentaADO.ListVentas(opcion, value, fechaInicial, fechaFinal, comprobante, estado, usuario);
            }
        };

        task.setOnSucceeded((WorkerStateEvent e) -> {
            tvList.setItems(task.getValue());
            lblLoad.setVisible(false);
        });
        task.setOnFailed((WorkerStateEvent event) -> {
            lblLoad.setVisible(false);
        });

        task.setOnScheduled((WorkerStateEvent event) -> {
            lblLoad.setVisible(true);
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void openWindowDetalleVenta() throws IOException {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(FilesRouters.FX_VENTA_DETALLE));
            ScrollPane node = fXMLLoader.load();
            //Controlller here
            FxVentaDetalleController controller = fXMLLoader.getController();
            controller.setInitVentasController(this, vbPrincipal, vbContent);
            controller.setInitComponents(tvList.getSelectionModel().getSelectedItem().getIdVenta());
            //
            vbContent.getChildren().clear();
            AnchorPane.setLeftAnchor(node, 0d);
            AnchorPane.setTopAnchor(node, 0d);
            AnchorPane.setRightAnchor(node, 0d);
            AnchorPane.setBottomAnchor(node, 0d);
            vbContent.getChildren().add(node);
        } else {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Compra", "Debe seleccionar una compra de la lista", false);
        }
    }

    private void openWindowVendedores() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_EMPLEADO_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxEmpleadosListaController controller = fXMLLoader.getController();
            controller.setInitVentaRealizadasController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Elija un vendedor", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
            controller.fillEmpleadosTable("");
        } catch (IOException ex) {
            System.out.println("Venta reporte controller:" + ex.getLocalizedMessage());
        }
    }
    
    private void openWindowValidarAcceso() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_VALIDAR_ACCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxEmpleadosListaController controller = fXMLLoader.getController();
            controller.setInitVentaRealizadasController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Elija un vendedor", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
            controller.fillEmpleadosTable("");
        } catch (IOException ex) {
            System.out.println("Venta reporte controller:" + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onKeyPressedSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!tvList.getItems().isEmpty()) {
                tvList.requestFocus();
                tvList.getSelectionModel().select(0);
            }
        }
    }

    @FXML
    private void onKeyRelasedSearch(KeyEvent event) {
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
            if (!lblLoad.isVisible()) {
                fillVentasTable((short) 1, txtSearch.getText().trim(), "", "", 0, 0, idEmpleado);
            }
        }
    }

    @FXML
    private void onActionFechaInicial(ActionEvent event) {
        if (dtFechaInicial.getValue() != null && dtFechaFinal.getValue() != null) {
            if (!lblLoad.isVisible()) {
                fillVentasTable((short) 0, "", Tools.getDatePicker(dtFechaInicial), Tools.getDatePicker(dtFechaFinal),
                        cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento(),
                        cbEstado.getSelectionModel().getSelectedItem().getIdDetalle().get(), idEmpleado);
            }
        }
    }

    @FXML
    private void onActionFechaFinal(ActionEvent event) {
        if (dtFechaInicial.getValue() != null && dtFechaFinal.getValue() != null) {
            if (!lblLoad.isVisible()) {
                fillVentasTable((short) 0, "", Tools.getDatePicker(dtFechaInicial), Tools.getDatePicker(dtFechaFinal),
                        cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento(),
                        cbEstado.getSelectionModel().getSelectedItem().getIdDetalle().get(), idEmpleado);
            }
        }
    }

    @FXML
    private void onKeyPressedList(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            if (btnMostrar.isDisable()) {
                return;
            }
            openWindowDetalleVenta();
        }
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) throws IOException {
        if (event.getClickCount() == 2) {
            if (btnMostrar.isDisable()) {
                return;
            }
            openWindowDetalleVenta();
        }
    }

    @FXML
    private void onKeyPressedMostar(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowDetalleVenta();
        }
    }

    @FXML
    private void onActionMostrar(ActionEvent event) throws IOException {
        openWindowDetalleVenta();
    }

    @FXML
    private void onKeyPressedRecargar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                cbEstado.getSelectionModel().select(0);
                Tools.actualDate(Tools.getDate(), dtFechaInicial);
                Tools.actualDate(Tools.getDate(), dtFechaFinal);
                if (dtFechaInicial.getValue() != null && dtFechaFinal.getValue() != null) {
                    fillVentasTable((short) 0, "", Tools.getDatePicker(dtFechaInicial), Tools.getDatePicker(dtFechaFinal),
                            cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento(),
                            cbEstado.getSelectionModel().getSelectedItem().getIdDetalle().get(), idEmpleado);
                }
            }
        }
    }

    @FXML
    private void onActionRecargar(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            cbEstado.getSelectionModel().select(0);
            Tools.actualDate(Tools.getDate(), dtFechaInicial);
            Tools.actualDate(Tools.getDate(), dtFechaFinal);
            if (dtFechaInicial.getValue() != null && dtFechaFinal.getValue() != null) {
                fillVentasTable((short) 0, "", Tools.getDatePicker(dtFechaInicial), Tools.getDatePicker(dtFechaFinal),
                        cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento(),
                        cbEstado.getSelectionModel().getSelectedItem().getIdDetalle().get(), idEmpleado);
            }
        }
    }

    @FXML
    private void onActionComprobante(ActionEvent event) {
        if (dtFechaInicial.getValue() != null && dtFechaFinal.getValue() != null) {
            if (!lblLoad.isVisible()) {
                fillVentasTable((short) 0, "", Tools.getDatePicker(dtFechaInicial), Tools.getDatePicker(dtFechaFinal),
                        cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento(),
                        cbEstado.getSelectionModel().getSelectedItem().getIdDetalle().get(), idEmpleado);
            }
        }
    }

    @FXML
    private void onActionEstado(ActionEvent event) {
        if (dtFechaInicial.getValue() != null && dtFechaFinal.getValue() != null) {
            if (!lblLoad.isVisible()) {
                fillVentasTable((short) 0, "", Tools.getDatePicker(dtFechaInicial), Tools.getDatePicker(dtFechaFinal),
                        cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento(),
                        cbEstado.getSelectionModel().getSelectedItem().getIdDetalle().get(), idEmpleado);
            }
        }
    }

    @FXML
    private void onKeyPressedVendedor(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowVendedores();
        }
    }

    @FXML
    private void onActionVendedor(ActionEvent event) {
        openWindowVendedores();
    }

    public VBox getWindow() {
        return window;
    }

    public TextField getTxtVendedor() {
        return txtVendedor;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public void setContent(AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
