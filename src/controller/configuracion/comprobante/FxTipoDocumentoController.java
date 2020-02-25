package controller.configuracion.comprobante;

import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
    //ese ojecto TipoDocumentoTB
    //cuando tu consultas cualquier file de la tabla consultas al objecto en si
    private TableColumn<TipoDocumentoTB, String> tcNumero;
    @FXML
    private TableColumn<TipoDocumentoTB, String> tcTipoComprobante;
    @FXML
    private TableColumn<TipoDocumentoTB, String> tcSerie;
    @FXML
    private TableColumn<TipoDocumentoTB, String> tcNombreImpresion;
    @FXML
    private TableColumn<TipoDocumentoTB, ImageView> tcPredeterminado;

    private boolean stateUpdate;

    private AnchorPane vbPrincipal;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcNumero.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcTipoComprobante.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getNombre()));
        tcSerie.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getSerie()));
        tcNombreImpresion.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getNombreDocumento()));
        tcPredeterminado.setCellValueFactory(new PropertyValueFactory<>("imagePredeterminado"));
        
        tcNumero.prefWidthProperty().bind(tvList.widthProperty().multiply(0.05));
        tcTipoComprobante.prefWidthProperty().bind(tvList.widthProperty().multiply(0.25));
        tcSerie.prefWidthProperty().bind(tvList.widthProperty().multiply(0.23));
        tcNombreImpresion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.30));
        tcPredeterminado.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));
        stateUpdate = false;
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
            if (stateUpdate) {
                List<TipoDocumentoTB> list = TipoDocumentoADO.GetDocumentoCombBox();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isPredeterminado() == true) {
                        Session.DEFAULT_COMPROBANTE = i;
                        break;
                    }
                }
                stateUpdate = false;
            }

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
        //
        Stage stage = WindowStage.StageLoaderModal(parent, "Nuevo comprobante", window.getScene().getWindow());
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setOnHiding((WindowEvent WindowEvent) -> {
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
            //debes enviar mas parametros cuando editar falta la serie no llega
            //recuenda  ah vrda ah pos ya me olvide ajjajaja
            //todo lo que guarda la lista o la tabla son una coleccion de objectos que tipos hay tu defines
            FxTipoDocumentoProcesoController controller = fXMLLoader.getController();
//            controller.setInitMoneyController(this);
//hay esta muchacho vas progresando  gracia maestro :c
//una ves que lo aggaras todo lo demas es facil ahora que sigue
//hya tiene que apareces primero has la funcion 
            controller.initUpdate(tvList.getSelectionModel().getSelectedItem().getIdTipoDocumento(),
                    tvList.getSelectionModel().getSelectedItem().getNombre(),
                    tvList.getSelectionModel().getSelectedItem().getSerie(),
                    tvList.getSelectionModel().getSelectedItem().getNombreDocumento()
                    );
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Actualizar el comprobante", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding((WindowEvent WindowEvent) -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();
        } else {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Tipo de comprobante", "Seleccione un elemento de la lista.", false);
        }
    }

    private void onEventPredeterminado() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            String result = TipoDocumentoADO.ChangeDefaultState(true, tvList.getSelectionModel().getSelectedItem().getIdTipoDocumento());
            if (result.equalsIgnoreCase("updated")) {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Tipo de comprobante", "Se cambio el estado correctamente.", false);
                stateUpdate = true;
                fillTabletTipoDocumento();
            } else {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Tipo de comprobante", "Error: " + result, false);
            }
        } else {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Tipo de comprobante", "Seleccione un elemento de la lista.", false);
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
    private void onMouseClickedList(MouseEvent event) {

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
