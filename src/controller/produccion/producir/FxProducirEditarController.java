
package controller.produccion.producir;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;


public class FxProducirEditarController implements Initializable {

    @FXML
    private ScrollPane spWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private DatePicker txtFechaRegistro;
    @FXML
    private DatePicker txtFechaInicio;
    @FXML
    private DatePicker txtTermino;
    @FXML
    private TextField txtProductoFabricar;
    @FXML
    private Button btnProducirFabricar;
    @FXML
    private RadioButton cbInterno;
    @FXML
    private RadioButton cbExterno;
    @FXML
    private Label lblCostoPromedio;
    @FXML
    private Label lblPrecioPromedio;
    @FXML
    private Label lblUtilidadPromedio;
    @FXML
    private TableView<?> tvListInsumos;
    @FXML
    private TableColumn<?, ?> tcOpcion;
    @FXML
    private TableColumn<?, ?> tcCodigo;
    @FXML
    private TableColumn<?, ?> tcDescripcion;
    @FXML
    private TableColumn<?, ?> tcCantidad;
    @FXML
    private TableColumn<?, ?> tcMedida;
    @FXML
    private TableColumn<?, ?> tcCostoPromedio;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
   
    }    

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
    }

    @FXML
    private void onActionAgregar(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedLimpiar(KeyEvent event) {
    }

    @FXML
    private void onActionLimpiar(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedFabricar(KeyEvent event) {
    }

    @FXML
    private void onActionBuscarProductoFabricar(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedBuscar(KeyEvent event) {
    }

    @FXML
    private void onActionBuscar(ActionEvent event) {
    }
    
}
