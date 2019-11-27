package controller.configuracion.impuestos;

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
import model.ImpuestoADO;
import model.ImpuestoTB;

public class FxImpuestoProcesoController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtValor;
    @FXML
    private TextField txtCodigoAlterno;
    @FXML
    private Button btnGuardar;

    private FxImpuestoController impuestoController;

    private int idImpuesto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idImpuesto = 0;

    }

    public void setUpdateImpuesto(int idImpuesto, String nombre, String valor, String codigoAlterno) {
        this.idImpuesto = idImpuesto;
        txtNombre.setText(nombre);
        txtValor.setText(valor);
        txtCodigoAlterno.setText(codigoAlterno);
        btnGuardar.setText("Actualizar");
        btnGuardar.getStyleClass().add("buttonLightWarning");
    }

    @FXML
    private void onKeyTypedValor(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
            event.consume();
        }
        if (c == '.' && txtValor.getText().contains(".")) {
            event.consume();
        }
    }

    private void onProccesImpuesto() {
        if (txtNombre.getText().isEmpty()) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Impuesto", "Ingrese el nombre del impuesto.", false);
            txtNombre.requestFocus();
        } else if (!Tools.isNumeric(txtValor.getText())) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Impuesto", "Ingrese el valor del impuesto.", false);
            txtValor.requestFocus();
        } else {
            ImpuestoTB impuestoTB = new ImpuestoTB();
            impuestoTB.setIdImpuesto(idImpuesto);
            impuestoTB.setNombre(txtNombre.getText().trim());
            impuestoTB.setValor(Double.parseDouble(txtValor.getText()));
            impuestoTB.setPredeterminado(false);
            impuestoTB.setCodigoAlterno(txtCodigoAlterno.getText().isEmpty() ? "0" : txtCodigoAlterno.getText().trim());
            impuestoTB.setSistema(false);
            String result = ImpuestoADO.CrudImpuesto(impuestoTB);
            if (result.equalsIgnoreCase("duplicated")) {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Impuesto", "Hay un impuesto con el mismo nombre.", false);
                txtNombre.requestFocus();
            } else if (result.equalsIgnoreCase("updated")) {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Impuesto", "Se actualizo correctamente.", false);
                impuestoController.fillTabletTax();
                Tools.Dispose(window);
            } else if (result.equalsIgnoreCase("inserted")) {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Impuesto", "Se ingreso correctamente.", false);
                impuestoController.fillTabletTax();
                Tools.Dispose(window);
            } else {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Impuesto", result, false);

            }
        }
    }

    @FXML
    private void onActionGuardar(ActionEvent event) {
        onProccesImpuesto();
    }

    @FXML
    private void onKeyPressedGuardar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onProccesImpuesto();
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

    public void setInitImpuestoController(FxImpuestoController impuestoController) {
        this.impuestoController = impuestoController;
    }

}
