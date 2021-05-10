package controller.operaciones.venta;

import controller.consultas.pagar.FxCuentasPorCobrarVisualizarController;
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
import model.CajaADO;
import model.CajaTB;
import model.ModeloObject;
import model.MovimientoCajaTB;
import model.VentaADO;
import model.VentaCreditoTB;

public class FxVentaAbonoProcesoController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private TextField txtMonto;
    @FXML
    private DatePicker dtFecha;
    @FXML
    private RadioButton rbBancos;
    @FXML
    private RadioButton rbCaja;
    @FXML
    private ComboBox<BancoTB> cbCuenta;
    @FXML
    private Button btnAceptar;
    @FXML
    private TextField txtObservacion;

    private FxCuentasPorCobrarVisualizarController cuentasPorCobrarVisualizarController;

    private String idVenta;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
        Tools.actualDate(Tools.getDate(), dtFecha);
        BancoADO.GetBancoComboBox().forEach(e -> cbCuenta.getItems().add(new BancoTB(e.getIdBanco(), e.getNombreCuenta())));
        ToggleGroup toggleGroup = new ToggleGroup();
        rbBancos.setToggleGroup(toggleGroup);
        rbCaja.setToggleGroup(toggleGroup);
    }

    public void setInitLoadVentaAbono(String idVenta) {
        this.idVenta = idVenta;
    }

    private void saveAbono() {
        if (!Tools.isNumeric(txtMonto.getText().trim())) {
            Tools.AlertMessageWarning(window, "Abonar", "Ingrese el abono.");
            txtMonto.requestFocus();
        } else if (Double.parseDouble(txtMonto.getText().trim()) <= 0) {
            Tools.AlertMessageWarning(window, "Abonar", "El abono no puede ser menor a 0.");
            txtMonto.requestFocus();
        } else if (Tools.isText(txtObservacion.getText())) {
            Tools.AlertMessageWarning(window, "Abonar", "Ingrese alguna observación del abono.");
            txtObservacion.requestFocus();
        } else {
            if (rbBancos.isSelected()) {
                if (cbCuenta.getSelectionModel().getSelectedIndex() < 0) {
                    Tools.AlertMessageWarning(window, "Abonar", "Seleccione la cuenta.");
                    cbCuenta.requestFocus();
                } else {
                    short value = Tools.AlertMessageConfirmation(window, "Abonor", "¿Está seguro de continuar?");
                    if (value == 1) {
                        btnAceptar.setDisable(true);
                        VentaCreditoTB ventaCreditoTB = new VentaCreditoTB();
                        ventaCreditoTB.setIdVenta(idVenta);
                        ventaCreditoTB.setMonto(Double.parseDouble(txtMonto.getText()));
                        ventaCreditoTB.setFechaPago(Tools.getDatePicker(dtFecha));
                        ventaCreditoTB.setHoraPago(Tools.getTime());
                        ventaCreditoTB.setEstado((short) 1);
                        ventaCreditoTB.setIdUsuario(Session.USER_ID);
                        ventaCreditoTB.setObservacion(txtObservacion.getText().trim());

                        BancoHistorialTB bancoHistorialTB = new BancoHistorialTB();
                        bancoHistorialTB.setIdBanco(cbCuenta.getSelectionModel().getSelectedItem().getIdBanco());
                        bancoHistorialTB.setDescripcion("INGRESO DE DINERO POR CUENTAS POR COBRAR".toUpperCase());
                        bancoHistorialTB.setFecha(Tools.getDate());
                        bancoHistorialTB.setHora(Tools.getTime());
                        bancoHistorialTB.setEntrada(Double.parseDouble(txtMonto.getText()));
                        bancoHistorialTB.setSalida(0);

                        ModeloObject result = VentaADO.RegistrarAbono(ventaCreditoTB, bancoHistorialTB, null);
                        if (result.getState().equalsIgnoreCase("inserted")) {
                            Tools.Dispose(window);
                            cuentasPorCobrarVisualizarController.openModalImpresion(idVenta, result.getIdResult());
                            cuentasPorCobrarVisualizarController.loadData(idVenta);
                        } else if (result.getState().equalsIgnoreCase("error")) {
                            Tools.AlertMessageWarning(window, "Abonar", result.getMessage());
                        } else {
                            Tools.AlertMessageError(window, "Abonar", "No se completo el proceso por problemas de conexión.");
                            btnAceptar.setDisable(false);
                        }
                    }
                }
            } else {
                short value = Tools.AlertMessageConfirmation(window, "Abonor", "¿Está seguro de continuar?");
                if (value == 1) {
                    btnAceptar.setDisable(true);
                    CajaTB cajaTB = CajaADO.ValidarCreacionCaja(Session.USER_ID);
                    if (cajaTB.getId() == 2) {
                        VentaCreditoTB ventaCreditoTB = new VentaCreditoTB();
                        ventaCreditoTB.setIdVenta(idVenta);
                        ventaCreditoTB.setMonto(Double.parseDouble(txtMonto.getText()));
                        ventaCreditoTB.setFechaPago(Tools.getDatePicker(dtFecha));
                        ventaCreditoTB.setHoraPago(Tools.getTime());
                        ventaCreditoTB.setEstado((short) 1);
                        ventaCreditoTB.setIdUsuario(Session.USER_ID);
                        ventaCreditoTB.setObservacion(txtObservacion.getText().trim());

                        MovimientoCajaTB movimientoCajaTB = new MovimientoCajaTB();
                        movimientoCajaTB.setIdCaja(cajaTB.getIdCaja());
                        movimientoCajaTB.setFechaMovimiento(Tools.getDate());
                        movimientoCajaTB.setHoraMovimiento(Tools.getTime());
                        movimientoCajaTB.setComentario("INGRESO DE DINERO DE LA CUENTA POR COBRAR");
                        movimientoCajaTB.setTipoMovimiento((short)2);
                        movimientoCajaTB.setMonto(Double.parseDouble(txtMonto.getText()));

                        ModeloObject result = VentaADO.RegistrarAbono(ventaCreditoTB, null, movimientoCajaTB);
                        if (result.getState().equalsIgnoreCase("inserted")) {
                            Tools.Dispose(window);
                            cuentasPorCobrarVisualizarController.openModalImpresion(idVenta, result.getIdResult());
                            cuentasPorCobrarVisualizarController.loadData(idVenta);
                        } else if (result.getState().equalsIgnoreCase("error")) {
                            Tools.AlertMessageWarning(window, "Abonar", result.getMessage());
                        } else {
                            Tools.AlertMessageError(window, "Abonar", "No se completo el proceso por problemas de conexión.");
                            btnAceptar.setDisable(false);
                        }
                    } else {
                        Tools.AlertMessageWarning(window, "Abonar", "No se tiene una caja apertura para realizar el cobro.");
                        btnAceptar.setDisable(false);
                    }
                }
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
    private void onActionAceptar(ActionEvent event) {
        saveAbono();
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            saveAbono();
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

    public AnchorPane getWindow() {
        return window;
    }

    public void setInitVentaAbonarController(FxCuentasPorCobrarVisualizarController cuentasPorCobrarVisualizarController) {
        this.cuentasPorCobrarVisualizarController = cuentasPorCobrarVisualizarController;
    }

}
