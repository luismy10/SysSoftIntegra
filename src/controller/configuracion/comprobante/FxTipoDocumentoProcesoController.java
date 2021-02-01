package controller.configuracion.comprobante;

import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
    private TextField txtNumeracion;
    @FXML
    private TextField txtCodigoAlterno;
    @FXML
    private Button btnGuardar;
    @FXML
    private CheckBox cbGuia;
    @FXML
    private CheckBox cbFacturado;
    @FXML
    private CheckBox cbNotaCredito;

    private FxTipoDocumentoController tipoDocumentoController;

    private int idTipoDocumento;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
    }

    public void initUpdate(int codigo, String nombre, String serie, int numeracion, String codigoAlterno, boolean guia, boolean factura, boolean notaCredito) {
        idTipoDocumento = codigo;
        txtNombre.setText(nombre);
        txtSerie.setText(serie);
        txtNumeracion.setText("" + numeracion);
        txtCodigoAlterno.setText(codigoAlterno);
        btnGuardar.setText("Actualizar");
        btnGuardar.getStyleClass().add("buttonLightWarning");
        cbGuia.setSelected(guia);
        cbGuia.setText(guia ? "Si" : "No");
        cbFacturado.setSelected(factura);
        cbFacturado.setText(factura ? "Si" : "No");
        cbNotaCredito.setSelected(notaCredito);
        cbNotaCredito.setText(notaCredito ? "Si" : "No");
    }

    private void saveTipoImpuesto() {
        if (txtNombre.getText().trim().isEmpty()) {
            Tools.AlertMessageWarning(window, "Tipo de documento", "Ingrese el nombre del comprobante.");
            txtNombre.requestFocus();
        } else if (txtSerie.getText().trim().isEmpty()) {
            Tools.AlertMessageWarning(window, "Tipo de documento", "Ingrese la serie del comprobante.");
            txtSerie.requestFocus();
        } else if (!Tools.isNumericInteger(txtNumeracion.getText())) {
            Tools.AlertMessageWarning(window, "Tipo de documento", "Ingrese la numeración del comprobante.");
            txtNumeracion.requestFocus();
        } else if (Integer.parseInt(txtNumeracion.getText().trim()) <= 0) {
            Tools.AlertMessageWarning(window, "Tipo de documento", "La numeración no puede ser menor que 1.");
            txtNumeracion.requestFocus();
        } else {
            TipoDocumentoTB documentoTB = new TipoDocumentoTB();
            documentoTB.setIdTipoDocumento(idTipoDocumento);
            documentoTB.setNombre(txtNombre.getText().toUpperCase().trim());
            documentoTB.setSerie(txtSerie.getText().trim().toUpperCase());
            documentoTB.setNumeracion(Integer.parseInt(txtNumeracion.getText().trim()));
            documentoTB.setCodigoAlterno(txtCodigoAlterno.getText().trim());
            documentoTB.setPredeterminado(false);
            documentoTB.setGuia(cbGuia.isSelected());
            documentoTB.setFactura(cbFacturado.isSelected());
            documentoTB.setNotaCredito(cbNotaCredito.isSelected());

            String result = TipoDocumentoADO.CrudTipoDocumento(documentoTB);
            if (result.equalsIgnoreCase("updated")) {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Tipo de documento", "Se actualizado correctamente", false);
                Tools.Dispose(window);
                tipoDocumentoController.initLoad();
            } else if (result.equalsIgnoreCase("duplicate")) {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Tipo de documento", "Ya existe comprobante con el mismo nombre", false);
                txtNombre.requestFocus();
            } else if (result.equalsIgnoreCase("inserted")) {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Tipo de documento", "Se ha insertado correctamente", false);
                Tools.Dispose(window);
                tipoDocumentoController.initLoad();
            } else {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Tipo de documento", result, false);
            }

        }
    }

    @FXML
    private void onKeyTypedNumeracion(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b')) {
            event.consume();
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

    @FXML
    private void onActionGuia(ActionEvent event) {
        cbGuia.setText(cbGuia.isSelected() ? "Si" : "No");
    }

    @FXML
    private void onActionFactura(ActionEvent event) {
        cbFacturado.setText(cbFacturado.isSelected() ? "Si" : "No");
    }

    @FXML
    private void onActionNotaCredito(ActionEvent event) {
        cbNotaCredito.setText(cbNotaCredito.isSelected() ? "Si" : "No");
    }

    public void setTipoDocumentoController(FxTipoDocumentoController tipoDocumentoController) {
        this.tipoDocumentoController = tipoDocumentoController;
    }

}
