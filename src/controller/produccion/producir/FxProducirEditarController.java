package controller.produccion.producir;

import controller.menus.FxPrincipalController;
import controller.tools.FilesRouters;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.ProduccionADO;
import model.ProduccionTB;
import model.SuministroTB;

public class FxProducirEditarController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private HBox hbLoad;
    @FXML
    private Label lblMessageLoad;
    @FXML
    private Button btnAceptarLoad;

    private FxPrincipalController fxPrincipalController;

    private FxProducirController producirController;

    private String idProduccion;

    private ArrayList<SuministroTB> suministroTBs;
    @FXML
    private VBox vbBody;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        suministroTBs = new ArrayList();

    }

    public void closeWindow() {
        fxPrincipalController.getVbContent().getChildren().remove(apWindow);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(producirController.getWindow(), 0d);
        AnchorPane.setTopAnchor(producirController.getWindow(), 0d);
        AnchorPane.setRightAnchor(producirController.getWindow(), 0d);
        AnchorPane.setBottomAnchor(producirController.getWindow(), 0d);
        fxPrincipalController.getVbContent().getChildren().add(producirController.getWindow());
    }

    public void editarProduccion(String idProduccion) {
        this.idProduccion = idProduccion;
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<Object> task = new Task<Object>() {
            @Override
            protected Object call() {
                return ProduccionADO.Obtener_Produccion_ById(idProduccion);
            }
        };

        task.setOnScheduled(w -> {
            hbLoad.setVisible(true);
//            vbBody.setDisable(true);
        });
        task.setOnFailed(w -> {
            lblMessageLoad.setGraphic(null);
            lblMessageLoad.setText(task.getMessage());
            lblMessageLoad.setTextFill(Color.web("ea4242"));
        });

        task.setOnSucceeded(w -> {
            Object object = task.getValue();
            if (object instanceof ProduccionTB) {

            } else {
                lblMessageLoad.setGraphic(null);
                lblMessageLoad.setText((String) object);
                lblMessageLoad.setTextFill(Color.web("ea4242"));
            }
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    public void modalEstado(ProduccionTB produccionTB) {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_MODAL_ESTADO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());

            FXModalEstadoController controller = fXMLLoader.getController();
            controller.setInitControllerProducirEditar(this);
            controller.setProduccionTB(produccionTB);

            Stage stage = WindowStage.StageLoaderModal(parent, "Confirmacion de Producción", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();

        } catch (IOException ex) {
            System.out.println("Controller modal estado" + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        closeWindow();
    }

    @FXML
    private void onKeyPressedEditar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionEditar(ActionEvent event) {

    }

    public void setInitControllerProducir(FxProducirController producirController) {
        this.producirController = producirController;
    }

    public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }

}
