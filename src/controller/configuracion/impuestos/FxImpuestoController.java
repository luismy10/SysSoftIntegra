package controller.configuracion.impuestos;

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
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.ImpuestoADO;
import model.ImpuestoTB;

public class FxImpuestoController implements Initializable {

    @FXML
    private VBox window;
    @FXML
    private Label lblLoad;
    @FXML
    private TableView<ImpuestoTB> tvList;
    @FXML
    private TableColumn<ImpuestoTB, String> tcOperacion;
    @FXML
    private TableColumn<ImpuestoTB, String> tcNombre;
    @FXML
    private TableColumn<ImpuestoTB, String> tcValor;
    @FXML
    private TableColumn<ImpuestoTB, ImageView> tcPredeterminado;
    @FXML
    private TableColumn<ImpuestoTB, String> tcCodigoAlterno;

    private AnchorPane vbPrincipal;

    private boolean stateUpdate;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcOperacion.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getNombreOperacion()));
        tcNombre.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getNombreImpuesto()));
        tcValor.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getValor()));
        tcPredeterminado.setCellValueFactory(new PropertyValueFactory<>("imagePredeterminado"));
        tcCodigoAlterno.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getCodigoAlterno()));
        stateUpdate = false;
        
        
        tcOperacion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.20));
        tcNombre.prefWidthProperty().bind(tvList.widthProperty().multiply(0.20));
        tcValor.prefWidthProperty().bind(tvList.widthProperty().multiply(0.18));
        tcCodigoAlterno.prefWidthProperty().bind(tvList.widthProperty().multiply(0.20));
        tcPredeterminado.prefWidthProperty().bind(tvList.widthProperty().multiply(0.20));
    }

    public void fillTabletTax() {
        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ObservableList<ImpuestoTB>> task = new Task<ObservableList<ImpuestoTB>>() {
            @Override
            public ObservableList<ImpuestoTB> call() {
                return ImpuestoADO.ListImpuestos();
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

    private void openWindowImpuestoRegister() throws IOException {
        ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
        URL url = getClass().getResource(FilesRouters.FX_IMPUESTO_PROCESO);
        FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
        Parent parent = fXMLLoader.load(url.openStream());
        //Controlller here
        FxImpuestoProcesoController controller = fXMLLoader.getController();
        controller.setInitImpuestoController(this);
        //
        Stage stage = WindowStage.StageLoaderModal(parent, "Registre su impuesto", window.getScene().getWindow());
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setOnHiding(w -> {
            vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
        });
        stage.show();

    }

    private void openWindowImpuestoUpdate() {
        try {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                URL url = getClass().getResource(FilesRouters.FX_IMPUESTO_PROCESO);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxImpuestoProcesoController controller = fXMLLoader.getController();
                controller.setInitImpuestoController(this);
                controller.setUpdateImpuesto(tvList.getSelectionModel().getSelectedItem().getIdImpuesto());
                //
                Stage stage = WindowStage.StageLoaderModal(parent, "Actualizar su impuesto", window.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
                stage.show();
            } else {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Impuesto", "Seleccione un elemento de la lista.", false);
            }
        } catch (IOException ex) {
            System.out.println("openWindowImpuestoUpdate():" + ex.getLocalizedMessage());
        }

    }

    private void onEventProdeteminado() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            String result = ImpuestoADO.ChangeDefaultStateImpuesto(true, tvList.getSelectionModel().getSelectedItem().getIdImpuesto());
            if (result.equalsIgnoreCase("updated")) {
                System.out.println("Entre acaaaaa");
                Tools.AlertMessageInformation(window, "Impuesto", "Se cambio el estado correctamente.");
                stateUpdate = true;
                this.fillTableImpuesto();
            } else {
                Tools.AlertMessageError(window, "Impuesto", "Error: " + result);
            }
        } else {
            Tools.AlertMessageWarning(window, "Impuesto", "Seleccione un elemento de la lista.");
        }
    }

    private void onEventDelete() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            short confirmation = Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Impuesto", "¿Está seguro de eliminar el impuesto?", true);
            if (confirmation == 1) {
                String result = ImpuestoADO.DeleteImpuestoById(tvList.getSelectionModel().getSelectedItem().getIdImpuesto());
                if (result.equalsIgnoreCase("deleted")) {
                    Tools.AlertMessageInformation(window, "Impuesto", "Se eliminó el impuesto correctamente.");
                    fillTabletTax();
                } else if (result.equalsIgnoreCase("sistema")) {
                    Tools.AlertMessageWarning(window, "Impuesto", "No se puede eliminar el impuesto, ya que es del sistema.");
                } else if (result.equalsIgnoreCase("suministro")) {
                    Tools.AlertMessageWarning(window, "Impuesto", "No se puede eliminar el impuesto, está ligado a un producto.");
                } else {
                    Tools.AlertMessageError(window, "Impuesto", result);
                }
            }
        } else {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Impuesto", "Seleccione un elemento de la lista.", false);
        }
    }

    public void fillTableImpuesto() {
        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ObservableList<ImpuestoTB>> task = new Task<ObservableList<ImpuestoTB>>() {
            @Override
            public ObservableList<ImpuestoTB> call() {
                return ImpuestoADO.ListImpuestos();
            }
        };

        task.setOnSucceeded(e -> {
            tvList.setItems(task.getValue());
            lblLoad.setVisible(false);
            if (stateUpdate) {
                List<ImpuestoTB> list = ImpuestoADO.GetTipoImpuestoCombBox();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getPredeterminado() == true) {
                        Session.DEFAULT_IMPUESTO = i;
                        break;
                    }
                }
                stateUpdate = false;
            }
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

    @FXML
    private void onMouseClickedList(MouseEvent event) {
        if (event.getClickCount() == 2) {
            openWindowImpuestoUpdate();
        }
    }

    @FXML
    private void onKeyPressedAdd(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowImpuestoRegister();
        }
    }

    @FXML
    private void onActionAdd(ActionEvent event) throws IOException {
        openWindowImpuestoRegister();
    }

    @FXML
    private void onKeyPressedEdit(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowImpuestoUpdate();
        }
    }

    @FXML
    private void onActionEdit(ActionEvent event) {
        openWindowImpuestoUpdate();
    }

    @FXML
    private void onKeyPressedPredetermined(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventProdeteminado();
        }
    }

    @FXML
    private void onActionPredetermined(ActionEvent event) {
        onEventProdeteminado();
    }

    @FXML
    private void onKeyPressedDelete(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventDelete();
        }
    }

    @FXML
    private void onActionDelete(ActionEvent event) {
        onEventDelete();
    }

    @FXML
    private void onKeyPressedReload(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            fillTabletTax();
        }
    }

    @FXML
    private void onActionReload(ActionEvent event) {
        fillTabletTax();
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
