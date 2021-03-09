package controller.operaciones.notacredito;

import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.NotaCreditoADO;
import model.NotaCreditoTB;

public class FxNotaCreditoProcesoController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private RadioButton rbGuardarUno;
    @FXML
    private RadioButton rbGuardarDos;

    private FxNotaCreditoController notaCreditoController;

    private NotaCreditoTB notaCreditoTB;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
        ToggleGroup toggleGroup = new ToggleGroup();
        rbGuardarUno.setToggleGroup(toggleGroup);
        rbGuardarDos.setToggleGroup(toggleGroup);
    }

    public void loadDataComponents(NotaCreditoTB notaCreditoTB) {
        this.notaCreditoTB = notaCreditoTB;
    }

    private void executeRegistrar() {
        short value = Tools.AlertMessageConfirmation(apWindow, "Nota de Crédito", "¿Está seguro de continuar?");
        if (value == 1) {
            String result = NotaCreditoADO.Registrar_NotaCredito(notaCreditoTB);
            if (result.equalsIgnoreCase("registrado")) {
                Tools.AlertMessageInformation(apWindow, "Nota de Crédito", "Se registró correctamente la nota de crédito.");
                Tools.Dispose(apWindow);
                notaCreditoController.clearElements();
                notaCreditoController.getHbLoad().setVisible(false);
                notaCreditoController.getSpBody().setDisable(false);
            } else {
                Tools.AlertMessageError(apWindow, "Nota de Crédito", result);
            }
        }
    }

    @FXML
    private void onKeyPressedRegistrar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            executeRegistrar();
        }
    }

    @FXML
    private void onActionRegistrar(ActionEvent event) {
        executeRegistrar();
    }

    @FXML
    private void onKeyPressedCerrar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(apWindow);
        }
    }

    @FXML
    private void onActionCerrar(ActionEvent event) {
        Tools.Dispose(apWindow);
    }

    public void setInitNotaCreditoController(FxNotaCreditoController notaCreditoController) {
        this.notaCreditoController = notaCreditoController;
    }

}
