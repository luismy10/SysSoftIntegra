package controller.contactos.clientes;

import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.ClienteADO;
import model.ClienteTB;

public class FxClienteController implements Initializable {

    @FXML
    private VBox window;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<ClienteTB> tvList;
    @FXML
    private TableColumn<ClienteTB, Integer> tcId;
    @FXML
    private TableColumn<ClienteTB, String> tcDocumento;
    @FXML
    private TableColumn<ClienteTB, String> tcPersona;
    @FXML
    private TableColumn<ClienteTB, String> tcContacto;
    @FXML
    private TableColumn<ClienteTB, String> tcDirección;
    @FXML
    private TableColumn<ClienteTB, String> tcRepresentante;
    @FXML
    private TableColumn<ClienteTB, ImageView> tcPredeterminado;
    @FXML
    private Label lblLoad;

    private AnchorPane vbPrincipal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcId.setCellValueFactory(cellData -> cellData.getValue().getId().asObject());
        tcDocumento.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getNumeroDocumento()));
        tcPersona.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getInformacion()));
        tcContacto.setCellValueFactory(cellData -> Bindings.concat(
                (!Tools.isText(cellData.getValue().getTelefono()) ? "TEL: " + cellData.getValue().getTelefono() : "Sin número telefónico")
                + "\n"
                + (!Tools.isText(cellData.getValue().getCelular()) ? "CEL: " + cellData.getValue().getCelular() : "Sin número de celular")
        )
        );
        tcDirección.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getDireccion()));
        tcRepresentante.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getRepresentante()));
        tcPredeterminado.setCellValueFactory(new PropertyValueFactory<>("imagePredeterminado"));

        tcId.prefWidthProperty().bind(tvList.widthProperty().multiply(0.05));
        tcDocumento.prefWidthProperty().bind(tvList.widthProperty().multiply(0.11));
        tcPersona.prefWidthProperty().bind(tvList.widthProperty().multiply(0.26));
        tcContacto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.16));
        tcDirección.prefWidthProperty().bind(tvList.widthProperty().multiply(0.16));
        tcRepresentante.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcPredeterminado.prefWidthProperty().bind(tvList.widthProperty().multiply(0.10));

    }

    public void fillCustomersTable(String value) {

        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ObservableList<ClienteTB>> task = new Task<ObservableList<ClienteTB>>() {
            @Override
            public ObservableList<ClienteTB> call() {
                return ClienteADO.ListCliente(value);
            }
        };

        task.setOnSucceeded(e -> {
            tvList.setItems(task.getValue());
            lblLoad.setVisible(false);
        });
        task.setOnFailed(e -> {
            lblLoad.setVisible(false);
        });

        task.setOnScheduled(e -> {
            lblLoad.setVisible(true);
        });
        exec.execute(task);

        if (!exec.isShutdown()) {
            exec.shutdown();
        }

    }

    private void openWindowAddCliente() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_CLIENTE_PROCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxClienteProcesoController controller = fXMLLoader.getController();
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Cliente", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();
            controller.setValueAdd();
        } catch (IOException ex) {
            System.out.println("Cliente controller en openWindowAddCliente()" + ex.getLocalizedMessage());
        }
    }

    private void openWindowEditCliente() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_CLIENTE_PROCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxClienteProcesoController controller = fXMLLoader.getController();
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Editar cliente", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();
            controller.setValueUpdate(tvList.getSelectionModel().getSelectedItem().getIdCliente());
        } catch (IOException ex) {
            System.out.println("Cliente controller en openWindowEditCliente()" + ex.getLocalizedMessage());
        }
    }

    private void onEventProdeteminado() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            if (!lblLoad.isVisible()) {
                String result = ClienteADO.ChangeDefaultState(true, tvList.getSelectionModel().getSelectedItem().getIdCliente());
                if (result.equalsIgnoreCase("updated")) {
                    Tools.AlertMessageInformation(window, "Cliente", "Se cambió el cliente a predeterminado.");
                    fillCustomersTable("");

                } else {
                    Tools.AlertMessageError(window, "Cliente", "Error: " + result);
                }
            }
        } else {
            Tools.AlertMessageWarning(window, "Cliente", "Seleccione un elemento de la lista.");
        }
    }

    private void openWindowRemoveCliente() {
        short value = Tools.AlertMessageConfirmation(window, "Eliminar cliente", "¿Está seguro de eliminar al cliente?");
        if (value == 1) {
            String result = ClienteADO.RemoveCliente(tvList.getSelectionModel().getSelectedItem().getIdCliente());
            if (result.equalsIgnoreCase("deleted")) {
                Tools.AlertMessageInformation(window, "Eliminar cliente", "Se elimino correctamente el cliente.");
                fillCustomersTable("");
            } else if (result.equalsIgnoreCase("sistema")) {
                Tools.AlertMessageWarning(window, "Eliminar cliente", "No se puede eliminar el cliente porque es propio del sistema.");
            } else if (result.equalsIgnoreCase("venta")) {
                Tools.AlertMessageWarning(window, "Eliminar cliente", "No se puede eliminar al cliente porque tiene asignado compras.");
            } else {
                Tools.AlertMessageError(window, "Eliminar cliente", result);
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
    private void onKeyPressedAdd(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowAddCliente();
        }
    }

    @FXML
    private void onActionAdd(ActionEvent event) {
        openWindowAddCliente();
    }

    @FXML
    private void onKeyPressedEdit(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                openWindowEditCliente();
            } else {
                Tools.AlertMessageWarning(window, "Clientes", "Seleccione un cliente para actualizar.");
                tvList.requestFocus();
            }
        }
    }

    @FXML
    private void onActionEdit(ActionEvent event) {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            openWindowEditCliente();
        } else {
            Tools.AlertMessageWarning(window, "Clientes", "Seleccione un cliente para actualizar.");
            tvList.requestFocus();
        }
    }

    @FXML
    private void onKeyPressedRemove(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                openWindowRemoveCliente();
            } else {
                Tools.AlertMessageWarning(window, "Clientes", "Seleccione un cliente para actualizar.");
                tvList.requestFocus();
            }
        }
    }

    @FXML
    private void onActionRemove(ActionEvent event) {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            openWindowRemoveCliente();
        } else {
            Tools.AlertMessageWarning(window, "Clientes", "Seleccione un cliente para actualizar.");
            tvList.requestFocus();
        }
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) {
        if (event.getClickCount() == 2) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                openWindowEditCliente();
            }
        }
    }

    @FXML
    private void onKeyPressedReload(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                fillCustomersTable("");
            }
        }
    }

    @FXML
    private void onActionReload(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            fillCustomersTable("");
        }
    }

    @FXML
    private void onKeyPressedPredeterminado(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventProdeteminado();
        }
    }

    @FXML
    private void onActionPredeterminado(ActionEvent event) {
        onEventProdeteminado();
    }

    public TableView<ClienteTB> getTvList() {
        return tvList;
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
