
package controller.consultas.pagar;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;


public class FxCuentasPorCobrarController implements Initializable {
    
     @FXML
    private VBox vbWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private DatePicker dpFechaInicial;
    @FXML
    private DatePicker dpFechaFinal;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<?> tvList;
    @FXML
    private TableColumn<?, ?> tcNumero;
    @FXML
    private TableColumn<?, ?> tcFechaRegistro;
    @FXML
    private TableColumn<?, ?> tcProveedor;
    @FXML
    private TableColumn<?, ?> tcComprobante;
    @FXML
    private TableColumn<?, ?> tcEstado;
    @FXML
    private TableColumn<?, ?> tcTotal;
    @FXML
    private TableColumn<?, ?> tcOpciones;
   

    @Override
    public void initialize(URL url, ResourceBundle rb) {
     
    }    

    @FXML
    private void onKeyPressedVisualizar(KeyEvent event) {
    }

    @FXML
    private void onActionVisualizar(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedReporte(KeyEvent event) {
    }

    @FXML
    private void onActionReporte(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedRecargar(KeyEvent event) {
    }

    @FXML
    private void onActionRecargar(ActionEvent event) {
    }

    @FXML
    private void onActionFechaInicial(ActionEvent event) {
    }

    @FXML
    private void onActionFechaFinal(ActionEvent event) {
    }

    @FXML
    private void onActionSearch(ActionEvent event) {
    }

    @FXML
    private void onKeyReleasedSeach(KeyEvent event) {
    }

    @FXML
    private void onMouseClickList(MouseEvent event) {
    }
    
}
