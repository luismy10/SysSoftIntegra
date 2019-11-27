package controller.egresos.compras;

import controller.tools.Session;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.CuentasHistorialProveedorTB;
import model.CuentasProveedorADO;

public class FxAmortizarPagosController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private TextField txtValorCuota;
    @FXML
    private TextField txtObservacion;
    @FXML
    private Label lblPagado;
    @FXML
    private Label lblPendiente;
    @FXML
    private Label lblTotal;

    private int idCuentasProveedor;

    private double pagado;

    private String simboloMoneda;

    private String idCompra;

    private double total;

    private FxHistorialPagosController historialPagosController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
    }

    public void setInitValues(String simboloMoneda, double pagado, double total, int idCuentasProveedor, String idCompra) {
        this.simboloMoneda = simboloMoneda;
        this.pagado = pagado;
        this.total = total;
        this.idCuentasProveedor = idCuentasProveedor;
        this.idCompra = idCompra;
        lblPagado.setText(simboloMoneda + " " + Tools.roundingValue(pagado, 2));
        lblPendiente.setText(simboloMoneda + " " + Tools.roundingValue(total - pagado, 2));
        lblTotal.setText(simboloMoneda + " " + Tools.roundingValue(total, 2));
    }

    private void registerPagosProveedores() {
        if (!Tools.isNumeric(txtValorCuota.getText())) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Amortizar Pagos", "Ingrese el monto de la cuota, por favor.", false);
            txtValorCuota.requestFocus();
        } else if (Double.parseDouble(txtValorCuota.getText()) <= 0) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Amortizar Pagos", "El monto no puede ser 0.", false);
            txtValorCuota.requestFocus();
        } else {
            if ((pagado + Double.parseDouble(txtValorCuota.getText())) > total) {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Amortizar Pagos", "El monto a pagar sobrepasa el valor a cobrar.", false);
                txtValorCuota.requestFocus();
            } else {
                CuentasHistorialProveedorTB cuentasHistorialProveedorTB = new CuentasHistorialProveedorTB();
                cuentasHistorialProveedorTB.setIdCuentasProveedor(idCuentasProveedor);
                cuentasHistorialProveedorTB.setMonto(Double.parseDouble(txtValorCuota.getText()));
                cuentasHistorialProveedorTB.setFecha(Tools.getDate());
                cuentasHistorialProveedorTB.setHora(Tools.getHour("HH:mm:ss"));
                cuentasHistorialProveedorTB.setObservacion(txtObservacion.getText().isEmpty() ? "ninguno".toUpperCase() : txtObservacion.getText().toUpperCase());
                cuentasHistorialProveedorTB.setEstado((short) 1);
                cuentasHistorialProveedorTB.setUsuario(Session.USER_ID);

                short option = Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Pago Proveedor", "¿Está seguro de continuar?", true);
                if (option == 1) {
                    String result = CuentasProveedorADO.crudPagoProveedores(cuentasHistorialProveedorTB, (total == (pagado + Double.parseDouble(txtValorCuota.getText()))), idCompra);
                    if (result.equalsIgnoreCase("register")) {
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Pago Proveedores", "Se registró correctamente el pago.", false);
                        historialPagosController.initListHistorialPagos();
                        Tools.Dispose(window);
                    } else if (result.equalsIgnoreCase("completed")) {
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Pago Proveedores", "Se completo todos los pagos.", false);
                        historialPagosController.initListHistorialPagos();
                        Tools.Dispose(window);
                    } else {
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Pago Proveedores", result, false);
                    }
                }
            }
        }
    }

    private void validarValorCuota() {
        if (Tools.isNumeric(txtValorCuota.getText())) {
            if ((Double.parseDouble(txtValorCuota.getText()) + pagado) <= total) {
                lblPagado.setText(simboloMoneda + " " + Tools.roundingValue((pagado + Double.parseDouble(txtValorCuota.getText())), 2));
                lblPendiente.setText(simboloMoneda + " " + Tools.roundingValue((total - (pagado + Double.parseDouble(txtValorCuota.getText()))), 2));
            }
        }
    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        registerPagosProveedores();
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            registerPagosProveedores();
        }
    }

    @FXML
    private void onKeyTypedValorCuotas(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
            event.consume();
        }
        if (c == '.' && txtValorCuota.getText().contains(".")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyPressedCancelar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(window);
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        Tools.Dispose(window);
    }

    @FXML
    private void onKeyReleasedValorCuota(KeyEvent event) {
        validarValorCuota();
    }

    public void setInitAmortizarPagosController(FxHistorialPagosController historialPagosController) {
        this.historialPagosController = historialPagosController;
    }

}
