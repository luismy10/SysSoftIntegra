package controller.produccion.insumos;

import controller.menus.FxPrincipalController;
import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.InsumoADO;
import model.InsumoTB;

public class FxInsumoController implements Initializable {

    @FXML
    private HBox hbWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private TableView<InsumoTB> tvList;
    @FXML
    private TableColumn<InsumoTB, Integer> tcNumero;
    @FXML
    private TableColumn<InsumoTB, String> tcDescripcion;
    @FXML
    private TableColumn<InsumoTB, String> tcCosto;
    @FXML
    private TableColumn<InsumoTB, String> tcCantidad;
    @FXML
    private TableColumn<InsumoTB, String> tcUnidad;
    @FXML
    private TableColumn<InsumoTB, String> tcCategoria;
    @FXML
    private TextField txtBuscar;

    private FxPrincipalController fxPrincipalController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hbWindow.setOnKeyReleased((KeyEvent event) -> {
            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case F1:

                        break;
                    case F5:

                        break;
                }
            }
        });

        tcNumero.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        tcDescripcion.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getClave() + "\n"
                + cellData.getValue().getNombreMarca()
        ));
        tcCosto.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getCosto(), 2)
        ));
        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getCantidad(), 2)
        ));
        tcUnidad.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getMedidaName()
        ));
        tcCategoria.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getCategoriaName()
        ));

        tcNumero.prefWidthProperty().bind(tvList.widthProperty().multiply(0.04));
        tcDescripcion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.34));
        tcCosto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));
        tcCantidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));
        tcUnidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));
        tcCategoria.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));

    }

    public void loadInitComponents() {
        if (!lblLoad.isVisible()) {
            fillTableInsumos("");
        }
    }

    private void fillTableInsumos(String busqueda) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ObservableList<InsumoTB>> task = new Task<ObservableList<InsumoTB>>() {
            @Override
            public ObservableList<InsumoTB> call() {
                return InsumoADO.ListarInsumos(busqueda);
            }
        };
        task.setOnSucceeded(w -> {
            tvList.setItems(task.getValue());
            lblLoad.setVisible(false);
        });
        task.setOnFailed(w -> lblLoad.setVisible(false));
        task.setOnScheduled(w -> lblLoad.setVisible(true));
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void openWindowInsumo() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_INSUMO_PROCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxInsumoProcesoController controller = fXMLLoader.getController();
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Registrar Insumo", hbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
        } catch (IOException ex) {
            System.out.println("openWindowInsumo():" + ex.getLocalizedMessage());
        }
    }

    private void openWindowInsumo(String idInsumo) {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_INSUMO_PROCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxInsumoProcesoController controller = fXMLLoader.getController();
            controller.loadInsumo();
            controller.editarInsumo(idInsumo);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Modificar Insumo", hbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
        } catch (IOException ex) {
            System.out.println("openWindowInsumo():" + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onKeyPressedAgregar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowInsumo();
        }
    }

    @FXML
    private void onActionAgregar(ActionEvent event) {
        openWindowInsumo();
    }

    @FXML
    private void onKeyPressedEditar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                openWindowInsumo(tvList.getSelectionModel().getSelectedItem().getIdInsumo());
            }
        }
    }

    @FXML
    private void onActionEditar(ActionEvent event) {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            openWindowInsumo(tvList.getSelectionModel().getSelectedItem().getIdInsumo());
        }
    }

    @FXML
    private void onKeyPressedRecargar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            loadInitComponents();
        }
    }

    @FXML
    private void onKeyPressedEliminar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                short value = Tools.AlertMessageConfirmation(hbWindow, "Insumo", "¿Está seguro de eliminar el insumo?");
                if (value == 1) {
                    String result = InsumoADO.EliminarInsumo(tvList.getSelectionModel().getSelectedItem().getIdInsumo());
                    if (result.equalsIgnoreCase("deleted")) {
                        Tools.AlertMessageInformation(hbWindow, "Insumo", "Se eliminó correctamten el insumo.");
                        loadInitComponents();
                    } else {
                        Tools.AlertMessageError(hbWindow, "Insumo", result);
                    }
                }
            }
        }
    }

    @FXML
    private void onActionElimar(ActionEvent event) {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            short value = Tools.AlertMessageConfirmation(hbWindow, "Insumo", "¿Está seguro de eliminar el insumo?");
            if (value == 1) {
                String result = InsumoADO.EliminarInsumo(tvList.getSelectionModel().getSelectedItem().getIdInsumo());
                if (result.equalsIgnoreCase("deleted")) {
                    Tools.AlertMessageInformation(hbWindow, "Insumo", "Se eliminó correctamten el insumo.");
                    loadInitComponents();
                } else {
                    Tools.AlertMessageError(hbWindow, "Insumo", result);
                }
            }
        }
    }

    @FXML
    private void onActionRecargar(ActionEvent event) {
        loadInitComponents();
    }

    @FXML
    private void onActionBuscar(ActionEvent event) {
        if (!tvList.getItems().isEmpty()) {
            tvList.getSelectionModel().select(0);
            tvList.requestFocus();
        }
    }

    @FXML
    private void onKeyReleasedBuscar(KeyEvent event) {
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
                && event.getCode() != KeyCode.PAUSE
                && event.getCode() != KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                fillTableInsumos(txtBuscar.getText().trim());
            }
        }
    }

    @FXML
    private void onMouseClickList(MouseEvent event) {
        if (event.getClickCount() == 2) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                openWindowInsumo(tvList.getSelectionModel().getSelectedItem().getIdInsumo());
            }
        }
    }

      public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }

}
