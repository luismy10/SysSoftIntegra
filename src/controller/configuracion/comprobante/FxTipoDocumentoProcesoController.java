package controller.configuracion.comprobante;

import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.TipoDocumentoADO;
import model.TipoDocumentoTB;

public class FxTipoDocumentoProcesoController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtSerie;
    @FXML
    private Button btnGuardar;
    
    private FxTipoDocumentoController tipoDocumentoController;
    
    private int idTipoDocumento;    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
    }

    public void initUpdate(int codigo,String nombre,String serie) {
        idTipoDocumento = codigo;
        txtNombre.setText(nombre);
        txtSerie.setText(serie);
        btnGuardar.setText("Actualizar");
        btnGuardar.getStyleClass().add("buttonLightWarning");
    }

    private void saveTipoImpuesto() {
        if (txtNombre.getText().trim().isEmpty()) {
            Tools.AlertMessageWarning(window, "Tipo de documento", "Ingrese el nombre del comprobante.");
            txtNombre.requestFocus();
        }else if(txtSerie.getText().trim().isEmpty()){
            Tools.AlertMessageWarning(window,"Tipo de documento","Ingrese serie del comprobante.");
            txtSerie.requestFocus();
        }else {
            TipoDocumentoTB documentoTB = new TipoDocumentoTB();
            documentoTB.setIdTipoDocumento(idTipoDocumento);
            documentoTB.setNombre(txtNombre.getText().toUpperCase().trim());
            documentoTB.setSerie(txtSerie.getText().trim());
            documentoTB.setPredeterminado(false);
            
            String result = TipoDocumentoADO.CrudTipoDocumento(documentoTB);
            if (result.equalsIgnoreCase("updated")) {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Tipo de documento", "Se actualizado correctamente", false);
                Tools.Dispose(window);
                tipoDocumentoController.fillTabletTipoDocumento();
            } else if (result.equalsIgnoreCase("duplicate")) {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Tipo de documento", "Ya existe comprobante con el mismo nombre", false);
                txtNombre.requestFocus();
            } else if(result.equalsIgnoreCase("inserted")){
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Tipo de documento", "Se ha insertado correctamente", false);
                Tools.Dispose(window);
                tipoDocumentoController.fillTabletTipoDocumento();
            }else {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Tipo de documento", result, false);
            }
        }
    }

    @FXML
    private void onActionGuardar(ActionEvent event) {
        saveTipoImpuesto();
    }

    @FXML
    private void onKeyPressedGuardar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            saveTipoImpuesto();
        }
    }

    @FXML
    private void onKeyCancelar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(window);
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        Tools.Dispose(window);
    }
    
    public void setTipoDocumentoController(FxTipoDocumentoController tipoDocumentoController){
        this.tipoDocumentoController = tipoDocumentoController;
    }
    
}
