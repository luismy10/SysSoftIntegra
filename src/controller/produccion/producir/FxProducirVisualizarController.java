package controller.produccion.producir;

import controller.menus.FxPrincipalController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class FxProducirVisualizarController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private ScrollPane spBody;
    @FXML
    private Label lblTitulo;
    @FXML
    private GridPane gpList;
    @FXML
    private Label lblValorVenta;
    @FXML
    private Label lblValorVenta1;
    @FXML
    private Label lblValorVenta11;
    @FXML
    private Label lblValorVenta111;
    @FXML
    private HBox hbLoad;
    @FXML
    private Label lblMessageLoad;
    @FXML
    private Button btnAceptarLoad;

    private FxProducirController fxProducirController;

    private FxPrincipalController fxPrincipalController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setInitComponents(String idProducir) {

    }

    private void closeWindow() {
        fxPrincipalController.getVbContent().getChildren().remove(apWindow);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(fxProducirController.getWindow(), 0d);
        AnchorPane.setTopAnchor(fxProducirController.getWindow(), 0d);
        AnchorPane.setRightAnchor(fxProducirController.getWindow(), 0d);
        AnchorPane.setBottomAnchor(fxProducirController.getWindow(), 0d);
        fxPrincipalController.getVbContent().getChildren().add(fxProducirController.getWindow());
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        closeWindow();
    }

    public void setInitProducirController(FxProducirController fxProducirController, FxPrincipalController fxPrincipalController) {
        this.fxProducirController = fxProducirController;
        this.fxPrincipalController = fxPrincipalController;
    }

}
