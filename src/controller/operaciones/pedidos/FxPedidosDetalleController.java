package controller.operaciones.pedidos;

import controller.menus.FxPrincipalController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class FxPedidosDetalleController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private ScrollPane spBody;
    @FXML
    private Label lblLoad;
    @FXML
    private HBox hbLoad;
    @FXML
    private Label lblMessageLoad;
    @FXML
    private Button btnAceptarLoad;

    private FxPrincipalController fxPrincipalController;
    
    private FxPedidosRealizadosController pedidosRealizadosController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
    
    public void loadComponents(String idPedido){
        
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        fxPrincipalController.getVbContent().getChildren().remove(apWindow);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(pedidosRealizadosController.getVbWindow(), 0d);
        AnchorPane.setTopAnchor(pedidosRealizadosController.getVbWindow(), 0d);
        AnchorPane.setRightAnchor(pedidosRealizadosController.getVbWindow(), 0d);
        AnchorPane.setBottomAnchor(pedidosRealizadosController.getVbWindow(), 0d);
        fxPrincipalController.getVbContent().getChildren().add(pedidosRealizadosController.getVbWindow());
    }

    @FXML
    private void onKeyPressedAceptarLoad(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionAceptarLoad(ActionEvent event) {

    }

    public void setInitPedidosRealizadasController(FxPedidosRealizadosController pedidosRealizadosController, FxPrincipalController fxPrincipalController) {
        this.pedidosRealizadosController = pedidosRealizadosController;
        this.fxPrincipalController = fxPrincipalController;
    }

}
