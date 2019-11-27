package controller.menus;

import controller.produccion.producir.FxProducirController;
import controller.tools.FilesRouters;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class FxProduccionController implements Initializable {

    @FXML
    private HBox hbWindow;
    @FXML
    private TextField txtSearch;

    /*
    Objectos de la ventana principal y venta que agrega a los hijos
     */
    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    /*
    Controller producir     
     */
    private FXMLLoader fXMLProducir;

    private HBox nodeProducir;

    private FxProducirController controllerProducir;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {

            fXMLProducir = new FXMLLoader(getClass().getResource(FilesRouters.FX_PRODUCIR));
            nodeProducir = fXMLProducir.load();
            controllerProducir = fXMLProducir.getController();
           

        } catch (IOException ex) {
            System.out.println("Error en Producción Controller:" + ex.getLocalizedMessage());
        }
    }

    private void openWindowProducir() {
        controllerProducir.setContent(vbPrincipal, vbContent);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeProducir, 0d);
        AnchorPane.setTopAnchor(nodeProducir, 0d);
        AnchorPane.setRightAnchor(nodeProducir, 0d);
        AnchorPane.setBottomAnchor(nodeProducir, 0d);
        vbContent.getChildren().add(nodeProducir);
    }

   

    @FXML
    private void onKeyPressedProducir(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowProducir();
        }
    }

    @FXML
    private void onActionProducir(ActionEvent event) {
        openWindowProducir();
    }

    @FXML
    private void onKeyPressedEquipos(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionEquipos(ActionEvent event) {

    }

    public void setContent(AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
