
package controller.operaciones.compras;

import controller.operaciones.venta.FxVentaProcesoController;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.PlazosADO;
import model.PlazosTB;


public class FxPlazosController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private Label lblTitle;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtDias;
    @FXML
    private Button btnAceptar;
    @FXML
    private Button btnCancelar;

    private FxComprasProcesoController compraProcesoController;
    
    private FxVentaProcesoController ventaProcesoController;    

   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    
    }

    private void onAddPlazo() {
        if (this.txtNombre.getText().isEmpty()) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Plazos", "Ingrese el nombre del plazo, por favor.", false);
            txtNombre.requestFocus();
        } else if (this.txtDias.getText().trim().isEmpty()) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Plazos", "Ingrese los dias de intervalo del plazo, por favor.", false);
            txtDias.requestFocus();
        } else {
            short confirmation = Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Plazos", "¿Esta seguro de continuar?", true);
            if (confirmation == 1) {
                PlazosTB plazosTB = new PlazosTB();

                plazosTB.setNombre(txtNombre.getText().toUpperCase());
                plazosTB.setDias(Integer.parseInt(txtDias.getText()));
                plazosTB.setEstado(true);
                plazosTB.setPredeterminado(true);

                String result = PlazosADO.crudPlazos(plazosTB);
                if (result.equalsIgnoreCase("register")) {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Plazos", "Registrado correctamente el plazo.", false);
                    Tools.Dispose(window);
                    
                    if(compraProcesoController != null){
//                        compraProcesoController.setInitializePlazos();
                    }
                    if(ventaProcesoController != null){
                        //ventaProcesoController.setInitializePlazosVentas();
                    }
                    
                } else {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Plazos", result, false);
                }
            }
        }
    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        onAddPlazo();
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        Tools.Dispose(window);
    }

    @FXML
    private void onKeyTypedDias(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtDias.getText().contains(".") || c == '-' && txtDias.getText().contains("-")) {
            event.consume();
        }
    }

    public void setInitCompraVentaProcesoController(FxComprasProcesoController compraProcesoController, FxVentaProcesoController ventaProcesoController) {
        this.compraProcesoController = compraProcesoController;
        this.ventaProcesoController = ventaProcesoController;
    }
    

}
