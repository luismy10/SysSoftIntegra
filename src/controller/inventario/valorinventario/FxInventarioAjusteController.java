package controller.inventario.valorinventario;

import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.SuministroADO;

public class FxInventarioAjusteController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblClave;
    @FXML
    private Label lblProducto;
    @FXML
    private TextField txtStockMinimo;
    @FXML
    private TextField stockMaximo;
    
    private FxValorInventarioController valorInventarioController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
    }
    
    private void onExecuteGuardar(){
        short option = Tools.AlertMessageConfirmation(apWindow, "Ajuste de inventario", "¿Esta seguro de continuar?");
        if(option == 1){
            return;
        }
//        String value = SuministroADO.UpdatedInventarioStockMM(txtStockMinimo.getText().trim(),stockMaximo.getText().trim());
//        if(value.equalsIgnoreCase("updated")){
//            
//        }else{
//            
//        }
    }

    @FXML
    private void onActionGuardar(ActionEvent event) {
        
    }

    @FXML
    private void onKeyPressedGuardar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onKeyPressedCancelar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(apWindow);
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        Tools.Dispose(apWindow);
    }

    public void setInitValorInventarioController(FxValorInventarioController valorInventarioController) {
        this.valorInventarioController = valorInventarioController;
    }

}
