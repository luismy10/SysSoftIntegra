package controller.produccion.producir;

import controller.inventario.suministros.FxSuministrosListaController;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.EmpleadoADO;
import model.EmpleadoTB;
import model.SuministroADO;
import model.SuministroTB;

public class FxProducirProcesoController implements Initializable {

    @FXML
    private HBox hbWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private DatePicker dtFechaInicial;
    @FXML
    private DatePicker dtFechaFinal;
    @FXML
    private ComboBox<?> cbxLote;

    //Empleado
    @FXML
    private TextField txtSearchEmpleado;
    @FXML
    private ComboBox<?> cbxRegistroEmpleado;
    @FXML
    private TableView<EmpleadoTB> tvListEm;
    @FXML
    private TableColumn<EmpleadoTB, String> tcNumeracionEm;
    @FXML
    private TableColumn<EmpleadoTB, String> tcDocumento;
    @FXML
    private TableColumn<EmpleadoTB, String> tcDatosCompletos;
    @FXML
    private TableColumn<EmpleadoTB, String> tcTelefonoCelular;
    @FXML
    private TableColumn<EmpleadoTB, String> tcPuesto;
    @FXML
    private TableColumn<EmpleadoTB, String> tcEstado;
    @FXML
    private TableColumn<EmpleadoTB, CheckBox> tcOpcionEm;

    // Suministro 
    @FXML
    private TextField txtSearchSuministro;
    @FXML
    private ComboBox<?> cbxRegistroSuministro;
    @FXML
    private TableView<SuministroTB> tvListS;
    @FXML
    private TableColumn<SuministroTB, String> tcNumeracionS;
    @FXML
    private TableColumn<SuministroTB, String> tcClave;
    @FXML
    private TableColumn<SuministroTB, String> tcNombre;
    @FXML
    private TableColumn<SuministroTB, String> tcCosto;
    @FXML
    private TableColumn<SuministroTB, String> tcCantidad;
    @FXML
    private TableColumn<SuministroTB, CheckBox> tcOpcionS;

    // Equipo
    @FXML
    private TextField txtSearchEquipo;
    @FXML
    private ComboBox<?> cbxRegistroEquipo;
    @FXML
    private TableView<?> tvListE;
    @FXML
    private TableColumn<?, ?> tcNumeracion2;
    @FXML
    private TableColumn<?, ?> tcNombre2;
    @FXML
    private TableColumn<?, ?> tcCosto2;
    @FXML
    private TableColumn<?, ?> tcCantidad2;

    private FxProducirController producirController;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    private boolean empleadoLoad;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Empleados
        tcNumeracionEm.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcDocumento.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getNumeroDocumento()));
        tcDatosCompletos.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getApellidos() + " " + cellData.getValue().getNombres()));
        tcTelefonoCelular.setCellValueFactory(cellData -> Bindings.concat(
                !Tools.isText(cellData.getValue().getTelefono())
                ? "TEL: " + cellData.getValue().getTelefono() + "\n" + "CEL: " + cellData.getValue().getCelular()
                : "CEL: " + cellData.getValue().getCelular()
        ));
        tcPuesto.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getPuestoName()));
        tcEstado.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getEstadoName()));
        tcOpcionEm.setCellValueFactory(new PropertyValueFactory<>("validarEm"));

        // Suministros
        tcNumeracionS.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcClave.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getClave()));
        tcNombre.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getNombreMarca()));
        tcCosto.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getCostoCompra(), 2)));
        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getCantidad(), 2) + " " + cellData.getValue().getUnidadCompraName()));
        tcOpcionS.setCellValueFactory(new PropertyValueFactory<>("validarS"));

        Tools.actualDate(Tools.getDate(), dtFechaInicial);
        Tools.actualDate(Tools.getDate(), dtFechaFinal);

        empleadoLoad = false;
    }

    private void closeWindow() {
        vbContent.getChildren().remove(hbWindow);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(producirController.getWindow(), 0d);
        AnchorPane.setTopAnchor(producirController.getWindow(), 0d);
        AnchorPane.setRightAnchor(producirController.getWindow(), 0d);
        AnchorPane.setBottomAnchor(producirController.getWindow(), 0d);
        vbContent.getChildren().add(producirController.getWindow());
    }

    public void fillTablesProduccion() {
        fillTableEmployeeInProduction("");
        if (!empleadoLoad) {
            fillTableSuministrosInProduction((short) 0, "", "");
        }
    }

    public void fillTableEmployeeInProduction(String value) {

        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<List<EmpleadoTB>> task = new Task<List<EmpleadoTB>>() {
            @Override
            public ObservableList<EmpleadoTB> call() {
                return EmpleadoADO.ListEmployeeInProProduction(value);
            }
        };

        task.setOnSucceeded((WorkerStateEvent e) -> {
            tvListEm.setItems((ObservableList<EmpleadoTB>) task.getValue());
            lblLoad.setVisible(false);
            empleadoLoad = false;
        });
        task.setOnFailed((WorkerStateEvent event) -> {
            lblLoad.setVisible(false);
            empleadoLoad = false;
        });

        task.setOnScheduled((WorkerStateEvent event) -> {
            lblLoad.setVisible(true);
            empleadoLoad = true;
        });
        exec.execute(task);

        if (!exec.isShutdown()) {
            exec.shutdown();
        }

    }

    public void fillTableSuministrosInProduction(short opcion, String clave, String nombre) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ObservableList<SuministroTB>> task = new Task<ObservableList<SuministroTB>>() {
            @Override
            public ObservableList<SuministroTB> call() {
                return SuministroADO.List_Suministros_In_Produccion(opcion, clave, nombre);
            }
        };

        task.setOnSucceeded((WorkerStateEvent e) -> {
            tvListS.setItems(task.getValue());
            lblLoad.setVisible(false);
        });

        task.setOnFailed((WorkerStateEvent e) -> {
            lblLoad.setVisible(false);
        });
        task.setOnScheduled((WorkerStateEvent e) -> {
            lblLoad.setVisible(true);
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void openWindowSuministros() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_SUMINISTROS_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxSuministrosListaController controller = fXMLLoader.getController();
            controller.setInitProducirProcesoController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Producto", hbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding((w) -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();
            controller.fillSuministrosTablePaginacion();
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onKeyPressedAdd(KeyEvent event) {
    }

    @FXML
    private void onActionAdd(ActionEvent event) {
    }

    @FXML
    private void onActionSearchEmpleado(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedEmpleado(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            fillTableEmployeeInProduction(txtSearchEmpleado.getText());
        }
    }

    @FXML
    private void onActionEmpleado(ActionEvent event) {
        fillTableEmployeeInProduction(txtSearchEmpleado.getText());
    }

    @FXML
    private void onKeyPressedReloadEmpleado(KeyEvent event) {
    }

    @FXML
    private void onActionReloadEmpleado(ActionEvent event) {
    }

    @FXML
    private void onActionSearchSuministro(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedSuministro(KeyEvent event) {
    }

    @FXML
    private void onActionSuministro(ActionEvent event) {
        openWindowSuministros();
    }

    @FXML
    private void onKeyPressedReloadSuministro(KeyEvent event) {
    }

    @FXML
    private void onActionReloadSuministro(ActionEvent event) {
    }

    @FXML
    private void onActionSearchEquipo(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedEquipo(KeyEvent event) {
    }

    @FXML
    private void onActionEquipo(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedReloadEquipo(KeyEvent event) {
    }

    @FXML
    private void onActionReloadEquipo(ActionEvent event) {
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        closeWindow();
    }

    public void setInitControllerProducir(FxProducirController producirController, AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.producirController = producirController;
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
