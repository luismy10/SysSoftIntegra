package controller.inventario.valorinventario;

import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    private Label lblClave;
    @FXML
    private Label lblProducto;
    @FXML
    private TextField txtStockMinimo;
    @FXML
    private TextField stockMaximo;

    private FxValorInventarioController valorInventarioController;

    private String idSuministro;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
    }
      
    public void setLoadComponents(String idSuministro, String clave, String producto) {
        this.idSuministro = idSuministro;
        lblClave.setText(clave);
        lblProducto.setText(producto);
        txtStockMinimo.setText("0");
        stockMaximo.setText("0");
    }

    private void onExecuteGuardar() {
        if (!Tools.isNumeric(txtStockMinimo.getText().trim())) {
            Tools.AlertMessageWarning(apWindow, "Ajuste de inventario", "El valor ingresado debe ser numerico.");
            txtStockMinimo.requestFocus();
        } else if (!Tools.isNumeric(stockMaximo.getText().trim())) {
            Tools.AlertMessageWarning(apWindow, "Ajuste de inventario", "El valor ingresado debe ser numerico.");
            stockMaximo.requestFocus();
        } else {
            short option = Tools.AlertMessageConfirmation(apWindow, "Ajuste de inventario", "¿Esta seguro de continuar?");
            if (option == 1) {
                String value = SuministroADO.UpdatedInventarioStockMM(idSuministro, txtStockMinimo.getText().trim(), stockMaximo.getText().trim());
                if (value.equalsIgnoreCase("updated")) {
                    valorInventarioController.fillInventarioTable("", (short) 0, "",(short) 0,0,0);
                    Tools.Dispose(apWindow);
                    Tools.AlertMessageInformation(apWindow, "Ajuste de inventario", "Se realizo los cambios correctamenten.");
                } else {
                    Tools.AlertMessageError(apWindow, "Ajuste de inventario", value);
                }
            }
        }
    }

    @FXML
    private void onActionGuardar(ActionEvent event) {
        onExecuteGuardar();
    }

    @FXML
    private void onKeyPressedGuardar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onExecuteGuardar();
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

    @FXML
    private void onKeyTypedMinimo(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtStockMinimo.getText().contains(".") || c == '-' && txtStockMinimo.getText().contains("-")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedMaxino(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && stockMaximo.getText().contains(".") || c == '-' && stockMaximo.getText().contains("-")) {
            event.consume();
        }
    }

    public void setInitValorInventarioController(FxValorInventarioController valorInventarioController) {
        this.valorInventarioController = valorInventarioController;
    }

}
