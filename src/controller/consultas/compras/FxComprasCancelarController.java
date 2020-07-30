package controller.consultas.compras;

import controller.tools.Session;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.BancoADO;
import model.BancoHistorialTB;
import model.BancoTB;
import model.CompraADO;

public class FxComprasCancelarController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private CheckBox cbIngresoDinero;
    @FXML
    private HBox hbIngresoDinero;
    @FXML
    private Label lblTotal;
    @FXML
    private ComboBox<BancoTB> cbCuentas;
    @FXML
    private TextField txtEfectivo;
    @FXML
    private TextField txtObservacion;

    private FxComprasDetalleController comprasDetalleController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
    }

    public void loadComponents() {
        lblTotal.setText(comprasDetalleController.getCompraTB().getMonedaNombre() + " " + Tools.roundingValue(comprasDetalleController.getCompraTB().getTotal(), 2));
        txtEfectivo.setText(Tools.roundingValue(comprasDetalleController.getCompraTB().getTotal(), 2));
        cbCuentas.getItems().addAll(BancoADO.GetBancoComboBox());
    }

    private void executeAceptar() {
        if (cbIngresoDinero.isSelected()) {
            if (cbCuentas.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(apWindow, "Compra", "Seleccione una cuenta.");
                cbCuentas.requestFocus();
            } else if (txtObservacion.getText().trim().isEmpty()) {
                Tools.AlertMessageWarning(apWindow, "Detalle de venta", "Ingrese un comentario.");
                txtObservacion.requestFocus();
            } else {
                short value = Tools.AlertMessageConfirmation(apWindow, "Detalle de la compra", "¿Está seguro de continuar?");
                if (value == 1) {
                    BancoHistorialTB bancoHistorialBancaria = new BancoHistorialTB();
                    bancoHistorialBancaria.setIdBanco(cbCuentas.getSelectionModel().getSelectedItem().getIdBanco());
                    bancoHistorialBancaria.setIdEmpleado(Session.USER_ID);
                    bancoHistorialBancaria.setDescripcion("INGRESO POR COMPRA ANULADA");
                    bancoHistorialBancaria.setFecha(Tools.getDate());
                    bancoHistorialBancaria.setHora(Tools.getHour());
                    bancoHistorialBancaria.setEntrada(comprasDetalleController.getCompraTB().getTotal());
                    bancoHistorialBancaria.setSalida(0);
                    String result = CompraADO.cancelarCompraTotal(comprasDetalleController.getCompraTB().getIdCompra(), comprasDetalleController.getArrList(), bancoHistorialBancaria);
                    if (result.equalsIgnoreCase("scrambled")) {
                        Tools.AlertMessageWarning(apWindow, "Detalle de la compra", "La compra ya se encuentra anulada.");
                        Tools.Dispose(apWindow);
                    } else if (result.equalsIgnoreCase("historial")) {
                        Tools.AlertMessageWarning(apWindow, "Detalle de la compra", "La compra tiene un historial de pago, no se puede anular.");
                    } else if (result.equalsIgnoreCase("updated")) {
                        Tools.AlertMessageInformation(apWindow, "Detalle de la compra", "Se completo correctamente los cambios.");
                        Tools.Dispose(apWindow);
                        comprasDetalleController.setLoadDetalle(comprasDetalleController.getCompraTB().getIdCompra());
                    } else {
                        Tools.AlertMessageError(apWindow, "Detalle de la compra", result);
                    }
                }
            }

        } else {

            short value = Tools.AlertMessageConfirmation(apWindow, "Detalle de la compra", "¿Está seguro de continuar?");
            if (value == 1) {
                String result = CompraADO.cancelarCompraProducto(comprasDetalleController.getCompraTB().getIdCompra(), comprasDetalleController.getArrList());
                if (result.equalsIgnoreCase("cancel")) {
                    Tools.AlertMessageWarning(apWindow, "Detalle de la compra", "La compra ya se encuentra anulada.");
                    Tools.Dispose(apWindow);
                } else if (result.equalsIgnoreCase("updated")) {
                    Tools.AlertMessageInformation(apWindow, "Detalle de la compra", "Se completo correctamente los cambios.");
                    Tools.Dispose(apWindow);
                    comprasDetalleController.setLoadDetalle(comprasDetalleController.getCompraTB().getIdCompra());
                } else {
                    Tools.AlertMessageError(apWindow, "Detalle de la compra", result);
                }
            }

        }

    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        executeAceptar();
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            executeAceptar();
        }
    }

    @FXML
    private void onActionIngresoDinero(ActionEvent event) {
        hbIngresoDinero.setDisable(!cbIngresoDinero.isSelected());
    }

    public void setComprasDetalleController(FxComprasDetalleController comprasDetalleController) {
        this.comprasDetalleController = comprasDetalleController;
    }

}
