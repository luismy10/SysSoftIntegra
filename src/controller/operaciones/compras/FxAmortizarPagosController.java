package controller.operaciones.compras;

import controller.consultas.pagar.FxCuentasPorPagarVisualizarController;
import controller.tools.Session;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.BancoADO;
import model.BancoHistorialTB;
import model.BancoTB;
import model.CompraADO;
import model.CompraCreditoTB;
import model.ModeloObject;
import model.VentaADO;
import model.VentaCreditoTB;

public class FxAmortizarPagosController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private TextField txtMonto;
    @FXML
    private DatePicker dtFecha;
    @FXML
    private ComboBox<BancoTB> cbCuenta;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    @FXML
    private RadioButton rbBancos;
    @FXML
    private RadioButton rbCaja;
    @FXML
    private TextField txtObservacion;

    private FxCuentasPorPagarVisualizarController cuentasPorPagarVisualizarController;

    private String idCompra;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
        Tools.actualDate(Tools.getDate(), dtFecha);
        BancoADO.GetBancoComboBox().forEach(e -> cbCuenta.getItems().add(new BancoTB(e.getIdBanco(), e.getNombreCuenta())));
        ToggleGroup toggleGroup = new ToggleGroup();
        rbBancos.setToggleGroup(toggleGroup);
        rbCaja.setToggleGroup(toggleGroup);
    }

    public void setInitValues(String idCompra) {
        this.idCompra = idCompra;
    }

    private void eventGuardar() {
        if (!Tools.isNumeric(txtMonto.getText().trim())) {
            Tools.AlertMessageWarning(apWindow, "Abonar", "Ingrese el abono.");
            txtMonto.requestFocus();
        } else if (Double.parseDouble(txtMonto.getText().trim()) <= 0) {
            Tools.AlertMessageWarning(apWindow, "Abonar", "El abono no puede ser menor a 0.");
            txtMonto.requestFocus();
        } else if (Tools.isText(txtObservacion.getText())) {
            Tools.AlertMessageWarning(apWindow, "Abonar", "Ingrese alguna observación del abono.");
            txtObservacion.requestFocus();
        } else {
            if (rbBancos.isSelected()) {
                if (cbCuenta.getSelectionModel().getSelectedIndex() < 0) {
                    Tools.AlertMessageWarning(apWindow, "Abonar", "Seleccione la cuenta.");
                    cbCuenta.requestFocus();
                } else {
                    short value = Tools.AlertMessageConfirmation(apWindow, "Abonor", "¿Está seguro de continuar?");
                    if (value == 1) {
                        btnGuardar.setDisable(true);
                        CompraCreditoTB compraCreditoTB = new CompraCreditoTB();
                        compraCreditoTB.setIdCompra(idCompra);
                        compraCreditoTB.setMonto(Double.parseDouble(txtMonto.getText()));
                        compraCreditoTB.setFechaPago(Tools.getDatePicker(dtFecha));
                        compraCreditoTB.setHoraPago(Tools.getHour());
                        compraCreditoTB.setEstado(true);
                        compraCreditoTB.setIdEmpleado(Session.USER_ID);
                        compraCreditoTB.setObservacion(txtObservacion.getText().trim());

                        BancoHistorialTB bancoHistorialTB = new BancoHistorialTB();
                        bancoHistorialTB.setIdBanco(cbCuenta.getSelectionModel().getSelectedItem().getIdBanco());
                        bancoHistorialTB.setDescripcion("SALIDA DE DINERO POR CUENTAS POR PAGAR".toUpperCase());
                        bancoHistorialTB.setFecha(Tools.getDate());
                        bancoHistorialTB.setHora(Tools.getHour());
                        bancoHistorialTB.setEntrada(0);
                        bancoHistorialTB.setSalida(Double.parseDouble(txtMonto.getText()));

                        ModeloObject result = CompraADO.Registrar_Amortizacion(compraCreditoTB, bancoHistorialTB, null);
                        if (result.getState().equalsIgnoreCase("inserted")) {
                            Tools.Dispose(apWindow);
                            cuentasPorPagarVisualizarController.openModalImpresion(idCompra, result.getIdResult());
                            cuentasPorPagarVisualizarController.loadTableCompraCredito(idCompra);
                        } else if (result.getState().equalsIgnoreCase("error")) {
                            Tools.AlertMessageWarning(apWindow, "Abonar", result.getMessage());
                            btnGuardar.setDisable(false);
                        } else {
                            Tools.AlertMessageError(apWindow, "Abonar", "No se completo el proceso por problemas de conexión.");
                            btnGuardar.setDisable(false);
                        }
                    }
                }
            } else {

            }
        }
    }

    @FXML
    private void onKeyTypedMonto(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
            event.consume();
        }
        if (c == '.' && txtMonto.getText().contains(".")) {
            event.consume();
        }
    }

    @FXML
    private void onActionBancos(ActionEvent event) {
        cbCuenta.setDisable(false);
    }

    @FXML
    private void onActionCaja(ActionEvent event) {
        cbCuenta.setDisable(true);
    }

    @FXML
    private void onKeyPressedGuardar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            eventGuardar();
        }
    }

    @FXML
    private void onActionGuardar(ActionEvent event) {
        eventGuardar();
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

    public void setInitAmortizarPagosController(FxCuentasPorPagarVisualizarController cuentasPorPagarVisualizarController) {
        this.cuentasPorPagarVisualizarController = cuentasPorPagarVisualizarController;
    }

}
