
package controller.menus;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class FxPosTerminalController implements Initializable {

    @FXML
    private TextField txtSearch;
    @FXML
    private HBox hbOperacionesUno;
    @FXML
    private VBox btnVentas;
    @FXML
    private VBox btnCorteCaja;
    @FXML
    private VBox btnVentas1;

   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
      
    }    

    @FXML
    private void onKeyPressedVentas(KeyEvent event) {
    }

    @FXML
    private void onActionVentas(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedVentasNueva(KeyEvent event) {
    }

    @FXML
    private void onActionVentasNueva(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedCorteCaja(KeyEvent event) {
    }

    @FXML
    private void onActionCorteCaja(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedVentasRealizadas(KeyEvent event) {
    }

    @FXML
    private void onActionVentasRealizadas(ActionEvent event) {
    }
    
}
