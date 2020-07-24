package controller.operaciones.venta;

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

public class FxVentaDetalleProductoController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TextField txtDescuento;
    @FXML
    private Label lblProducto;
    
    private FxVentaEstructuraNuevoController ventaEstructuraNuevoController;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
    }
    
    public void loadData(String producto){
        lblProducto.setText(producto);
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

    public void setInitVentaEstructuraNuevoController(FxVentaEstructuraNuevoController ventaEstructuraNuevoController) {
        this.ventaEstructuraNuevoController = ventaEstructuraNuevoController;
    }

}
