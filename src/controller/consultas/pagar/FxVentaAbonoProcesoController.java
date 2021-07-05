package controller.consultas.pagar;

import controller.tools.Session;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import model.IngresoTB;
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
    private RadioButton rbBancos;
    @FXML
    private RadioButton rbIngreso;
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
        BancoADO.GetBancoComboBox().forEach(e -> cbCuenta.getItems().add(new BancoTB(e.getIdBanco(), e.getNombreCuenta())));
        ToggleGroup toggleGroup = new ToggleGroup();
        rbBancos.setToggleGroup(toggleGroup);
        rbIngreso.setToggleGroup(toggleGroup);
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
                        ventaCreditoTB.setFechaPago(Tools.getDate());
                        ventaCreditoTB.setHoraPago(Tools.getTime());
                        ventaCreditoTB.setEstado((short) 1);
                        ventaCreditoTB.setIdUsuario(Session.USER_ID);
                        ventaCreditoTB.setObservacion(txtObservacion.getText().trim());

                        BancoHistorialTB bancoHistorialTB = new BancoHistorialTB();
                        bancoHistorialTB.setIdBanco(cbCuenta.getSelectionModel().getSelectedItem().getIdBanco());
                        bancoHistorialTB.setDescripcion("INGRESO DE DINERO POR CUENTAS POR COBRAR");
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
                    VentaCreditoTB ventaCreditoTB = new VentaCreditoTB();
                    ventaCreditoTB.setIdVenta(idVenta);
                    ventaCreditoTB.setMonto(Double.parseDouble(txtMonto.getText()));
                    ventaCreditoTB.setFechaPago(Tools.getDate());
                    ventaCreditoTB.setHoraPago(Tools.getTime());
                    ventaCreditoTB.setEstado((short) 1);
                    ventaCreditoTB.setIdUsuario(Session.USER_ID);
                    ventaCreditoTB.setObservacion(txtObservacion.getText().trim());

                    /*
                    procedencia
                    1 = venta
                    2 = compra
                    
                    3 = ingreso libre
                    4 = salida libre
                    
                    5 = ingreso cuentas por cobrar
                    6 = salida cuentas por pagar
                     */

 /*
                    forma
                    1 = efectivo
                    2 = tarjeta
                     */
                    IngresoTB ingresoTB = new IngresoTB();
                    ingresoTB.setIdProcedencia("");
                    ingresoTB.setIdUsuario(Session.USER_ID);
                    ingresoTB.setDetalle("INGRESO DE DINERO POR CUENTAS POR COBRAR");
                    ingresoTB.setProcedencia(5);
                    ingresoTB.setFecha(Tools.getDate());
                    ingresoTB.setHora(Tools.getTime());
                    ingresoTB.setForma(1);
                    ingresoTB.setMonto(Double.parseDouble(txtMonto.getText()));

                    ModeloObject result = VentaADO.RegistrarAbono(ventaCreditoTB, null, ingresoTB);
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
    private void onActionIngreso(ActionEvent event) {
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
