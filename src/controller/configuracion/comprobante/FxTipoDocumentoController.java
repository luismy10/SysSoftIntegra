package controller.configuracion.comprobante;

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
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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
import model.TipoDocumentoADO;
import model.TipoDocumentoTB;

public class FxTipoDocumentoController implements Initializable {

    @FXML
    private VBox window;
    @FXML
    private Label lblLoad;
    @FXML
    private TableView<TipoDocumentoTB> tvList;
    @FXML
    private TableColumn<TipoDocumentoTB, String> tcNumero;
    @FXML
    private TableColumn<TipoDocumentoTB, String> tcTipoComprobante;
    @FXML
    private TableColumn<TipoDocumentoTB, String> tcSerie;
    @FXML
    private TableColumn<TipoDocumentoTB, String> tcCodigoAlterno;
    @FXML
    private TableColumn<TipoDocumentoTB, ImageView> tcPredeterminado;

    private AnchorPane vbPrincipal;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcNumero.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcTipoComprobante.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getNombre()));
        tcSerie.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getSerie()));
        tcCodigoAlterno.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getCodigoAlterno()));
        tcPredeterminado.setCellValueFactory(new PropertyValueFactory<>("imagePredeterminado"));

        tcNumero.prefWidthProperty().bind(tvList.widthProperty().multiply(0.07));
        tcTipoComprobante.prefWidthProperty().bind(tvList.widthProperty().multiply(0.30));
        tcSerie.prefWidthProperty().bind(tvList.widthProperty().multiply(0.20));
        tcCodigoAlterno.prefWidthProperty().bind(tvList.widthProperty().multiply(0.20));
        tcPredeterminado.prefWidthProperty().bind(tvList.widthProperty().multiply(0.20));
    }

    public void fillTabletTipoDocumento() {
        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ObservableList<TipoDocumentoTB>> task = new Task<ObservableList<TipoDocumentoTB>>() {
            @Override
            public ObservableList<TipoDocumentoTB> call() {
                return TipoDocumentoADO.ListTipoDocumento();
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

    private void openWindowAdd() throws IOException {
        ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
        URL url = getClass().getResource(FilesRouters.FX_TIPO_DOCUMENTO_PROCESO);
        FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
        Parent parent = fXMLLoader.load(url.openStream());
        //Controlller here
        FxTipoDocumentoProcesoController controller = fXMLLoader.getController();
        controller.setTipoDocumentoController(this);
        //
        Stage stage = WindowStage.StageLoaderModal(parent, "Nuevo comprobante", window.getScene().getWindow());
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setOnHiding(w -> {
            vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
        });
        stage.show();
    }

    private void openWindowEdit() throws IOException {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_TIPO_DOCUMENTO_PROCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxTipoDocumentoProcesoController controller = fXMLLoader.getController();
            controller.setTipoDocumentoController(this);
            controller.initUpdate(tvList.getSelectionModel().getSelectedItem().getIdTipoDocumento(),
                    tvList.getSelectionModel().getSelectedItem().getNombre(),
                    tvList.getSelectionModel().getSelectedItem().getSerie(),
                    tvList.getSelectionModel().getSelectedItem().getCodigoAlterno());
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Actualizar el comprobante", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();
        } else {
            Tools.AlertMessageWarning(window, "Tipo de comprobante", "Seleccione un elemento de la lista.");
        }
    }

    private void onEventPredeterminado() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            String result = TipoDocumentoADO.ChangeDefaultState(true, tvList.getSelectionModel().getSelectedItem().getIdTipoDocumento());
            if (result.equalsIgnoreCase("updated")) {
                Tools.AlertMessageInformation(window, "Tipo de comprobante", "Se cambio el estado correctamente.");
                fillTabletTipoDocumento();
            } else {
                Tools.AlertMessageError(window, "Tipo de comprobante", "Error: " + result);
            }
        } else {
            Tools.AlertMessageWarning(window, "Tipo de comprobante", "Seleccione un elemento de la lista.");
        }
    }

    private void onEventRemove() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            short value = Tools.AlertMessageConfirmation(window, "Tipo de comprobante", "¿Esta seguro de eliminar el tipo de comprobante?");
            if (value == 1) {
                String result = TipoDocumentoADO.EliminarTipoDocumento(tvList.getSelectionModel().getSelectedItem().getIdTipoDocumento());
                if (result.equalsIgnoreCase("removed")) {
                    Tools.AlertMessageInformation(window,  "Tipo de comprobante", "Se elimino correctamente el tipo de documento.");
                    fillTabletTipoDocumento();
                } else if (result.equalsIgnoreCase("venta")) {
                    Tools.AlertMessageWarning(window,  "Tipo de comprobante", "El tipo de documento esta ligado a una venta.");
                } else if(result.equalsIgnoreCase("sistema")){
                    Tools.AlertMessageWarning(window,  "Tipo de comprobante", "El tipo de documento no se puede eliminar porque es del sistema.");                    
                } 
                else {
                    Tools.AlertMessageError(window,  "Tipo de comprobante", result);
                }
            }
        } else {
            Tools.AlertMessageWarning(window, "Tipo de comprobante", "Seleccione un elemento de la lista.");
        }
    }

    @FXML
    private void onKeyPressedAdd(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowAdd();
        }
    }

    @FXML
    private void onActionAdd(ActionEvent event) throws IOException {
        openWindowAdd();
    }

    @FXML
    private void onKeyPressedEdit(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowEdit();
        }
    }

    @FXML
    private void onActionEdit(ActionEvent event) throws IOException {
        openWindowEdit();
    }

    @FXML
    private void onKeyPressedRemove(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventRemove();
        }
    }

    @FXML
    private void onActionRemove(ActionEvent event) {
        onEventRemove();
    }

    @FXML
    private void onKeyPressedPredetermined(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventPredeterminado();
        }
    }

    @FXML
    private void onActionPredetermined(ActionEvent event) {
        onEventPredeterminado();
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) throws IOException {
        if(event.getClickCount() == 2){
            openWindowEdit();
        }
    }

    @FXML
    private void onKeyPressedReload(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            fillTabletTipoDocumento();
        }
    }

    @FXML
    private void onActionReload(ActionEvent event) {
        fillTabletTipoDocumento();
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
