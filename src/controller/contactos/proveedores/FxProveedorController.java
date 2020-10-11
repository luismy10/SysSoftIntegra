package controller.contactos.proveedores;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.ProveedorADO;
import model.ProveedorTB;

public class FxProveedorController implements Initializable {

    @FXML
    private VBox window;
    @FXML
    private TextField txtSearch;
    @FXML
    private Label lblLoad;
    @FXML
    private TableView<ProveedorTB> tvList;
    @FXML
    private TableColumn<ProveedorTB, Integer> tcId;
    @FXML
    private TableColumn<ProveedorTB, String> tcDocumentType;
    @FXML
    private TableColumn<ProveedorTB, String> tcDocument;
    @FXML
    private TableColumn<ProveedorTB, String> tcBusinessName;
    @FXML
    private TableColumn<ProveedorTB, String> tcContacto;
    @FXML
    private TableColumn<ProveedorTB, String> tcState;
    @FXML
    private TableColumn<ProveedorTB, String> tcRepresentante;

    private AnchorPane vbPrincipal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        window.setOnKeyReleased((KeyEvent event) -> {
            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case F1:
                        openInsertWindow();
                        event.consume();
                        break;
                    case F2:
                        openUpdateWindow();
                        event.consume();
                        break;
                    case F5:
                        fillCustomersTable("");
                        break;
                    case DELETE:

                        event.consume();
                        break;
                    default:

                        break;
                }
            }
        });

        tcId.setCellValueFactory(cellData -> cellData.getValue().getId().asObject());
        tcDocumentType.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getTipoDocumentoName()));
        tcDocument.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getNumeroDocumento()));
        tcBusinessName.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getRazonSocial() // + "\n" + cellData.getValue().getNombreComercial().get()
        ));
        tcContacto.setCellValueFactory(cellData
                -> Bindings.concat(
                        !Tools.isText(cellData.getValue().getTelefono())
                        ? "TEL: " + cellData.getValue().getTelefono() + "\n" + "CEL: " + cellData.getValue().getCelular()
                        : "CEL: " + cellData.getValue().getCelular()
                )
        );
        tcRepresentante.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getRepresentante() + ""));
        tcState.setCellValueFactory(cellData -> cellData.getValue().getEstadoName());

        tcId.prefWidthProperty().bind(tvList.widthProperty().multiply(0.05));
        tcDocumentType.prefWidthProperty().bind(tvList.widthProperty().multiply(0.10));
        tcDocument.prefWidthProperty().bind(tvList.widthProperty().multiply(0.16));
        tcBusinessName.prefWidthProperty().bind(tvList.widthProperty().multiply(0.21));
        tcContacto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.17));
        tcRepresentante.prefWidthProperty().bind(tvList.widthProperty().multiply(0.20));
        tcState.prefWidthProperty().bind(tvList.widthProperty().multiply(0.09));

    }

    public void fillCustomersTable(String value) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<List<ProveedorTB>> task = new Task<List<ProveedorTB>>() {
            @Override
            public ObservableList<ProveedorTB> call() {
                return ProveedorADO.ListProveedor(value);
            }
        };

        task.setOnSucceeded((WorkerStateEvent e) -> {
            tvList.setItems((ObservableList<ProveedorTB>) task.getValue());
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

    private void openInsertWindow() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_PROVEEDORES_PROCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxProveedorProcesoController controller = fXMLLoader.getController();
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Proveedor", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
            controller.setValueAdd();
        } catch (IOException ix) {
            System.out.println("Error en Proveedor Controller:" + ix.getLocalizedMessage());
        }
    }

    private void openUpdateWindow() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            try {
                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                URL url = getClass().getResource(FilesRouters.FX_PROVEEDORES_PROCESO);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxProveedorProcesoController controller = fXMLLoader.getController();
                //
                Stage stage = WindowStage.StageLoaderModal(parent, "Editr Proveedor", window.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
                stage.show();
                controller.setValueUpdate(tvList.getSelectionModel().getSelectedItem().getNumeroDocumento());

            } catch (IOException ix) {
                System.out.println("Error en Proveedor Controller:" + ix.getLocalizedMessage());
            }
        } else {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Proveedor", "Debe seleccionar un proveedor para editarlo", false);
            tvList.requestFocus();
            if (!tvList.getItems().isEmpty()) {
                tvList.getSelectionModel().select(0);
            }
        }
    }

    private void executeEventRemove() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            short confirmation = Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Proveedor", "¿Esta seguro de eliminar al proveedor?", true);
            if (confirmation == 1) {
                String result = ProveedorADO.RemoveProveedor(tvList.getSelectionModel().getSelectedItem().getIdProveedor());
                if (result.equalsIgnoreCase("removed")) {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Proveedor", "Eliminado correctamente.", false);
                    fillCustomersTable("");
                } else if (result.equalsIgnoreCase("exists")) {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Proveedor", "No se puede eliminar el proveedor ya que está ligado a una compra.", false);
                } else {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Proveedor", result, false);
                }
            }
        } else {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Proveedor", "Debe seleccionar un proveedor para eliminarlo", false);
            tvList.requestFocus();
            if (!tvList.getItems().isEmpty()) {
                tvList.getSelectionModel().select(0);
            }
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
    private void onKeyReleasedSearch(KeyEvent event) {
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
                fillCustomersTable(txtSearch.getText());
            }
        }
    }

    @FXML
    private void onActionAdd(ActionEvent event) throws IOException {
        openInsertWindow();
    }

    @FXML
    private void onKeyPressedAdd(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openInsertWindow();
        }
    }

    @FXML
    private void onActionEdit(ActionEvent event) throws IOException {
        openUpdateWindow();
    }

    @FXML
    private void onKeyPressedEdit(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openUpdateWindow();
        }
    }

    @FXML
    private void onKeyPressedRemove(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            executeEventRemove();
        }
    }

    @FXML
    private void onActionRemove(ActionEvent event) {
        executeEventRemove();
    }

    @FXML
    private void onKeyPressedList(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openUpdateWindow();
        }
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) {
        if (event.getClickCount() == 2) {
            openUpdateWindow();
        }
    }

    @FXML
    private void onActionReload(ActionEvent event) {
        fillCustomersTable("");
    }

    @FXML
    private void onKeyPressedReload(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            fillCustomersTable("");
        }
    }

    public TableView<ProveedorTB> getTvList() {
        return tvList;
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
