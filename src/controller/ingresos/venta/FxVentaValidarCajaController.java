package controller.ingresos.venta;

import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class FxVentaValidarCajaController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private TextField txtFecha;

    private FxVentaController ventaController;

    private String idCaja;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
    }

    public void loadData(String idCaja, String dateTime) {
        this.idCaja = idCaja;
        txtFecha.setText(dateTime);
    }

    @FXML
    private void onKeyPressedOk(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(apWindow);
        }
    }

    @FXML
    private void onActionOk(ActionEvent event) {
        Tools.Dispose(apWindow);
    }

    public void setInitVentaController(FxVentaController ventaController) {
        this.ventaController = ventaController;
    }

}
