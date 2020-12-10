package controller.operaciones.compras;

import controller.consultas.pagar.FxCuentasPorPagarVisualizarController;
import controller.tools.Session;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.BancoADO;
import model.BancoHistorialTB;
import model.BancoTB;
import model.CompraADO;
import model.CompraCreditoTB;
import model.ModeloObject;
import model.TransaccionTB;

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

    private FxCuentasPorPagarVisualizarController cuentasPorPagarVisualizarController;

    private ObservableList<CompraCreditoTB> tvList;

    private String idCompra;

    private String comprobante;

    private double montoPagar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
        Tools.actualDate(Tools.getDate(), dtFecha);
        cbCuenta.getItems().add(new BancoTB("0", "Seleccionar..."));
        BancoADO.GetBancoComboBox().forEach(e -> cbCuenta.getItems().add(new BancoTB(e.getIdBanco(), e.getNombreCuenta())));
        cbCuenta.getSelectionModel().select(0);
    }

    public void setInitValues(String idCompra, String comprobante, ObservableList<CompraCreditoTB> tvList) {
        this.idCompra = idCompra;
        this.comprobante = comprobante;
        this.tvList = tvList;
        tvList.forEach((cctb) -> montoPagar += cctb.getCbSeleccion().isSelected() && !cctb.getCbSeleccion().isDisable() ? cctb.getMonto() : 0);
        txtMonto.setText(Tools.roundingValue(montoPagar, 2));
    }

    private void eventGuardar() {
        if (dtFecha.getValue() == null) {
            Tools.AlertMessageWarning(apWindow, "Generar Pago", "Ingrese la fecha de abono");
            dtFecha.requestFocus();
        } else if (cbCuenta.getSelectionModel().getSelectedIndex() <= 0) {
            Tools.AlertMessageWarning(apWindow, "Generar Pago", "Seleccione el banco o caja");
            cbCuenta.requestFocus();
        } else {
            short value = Tools.AlertMessageConfirmation(apWindow, "Generar Pago", "¿Está seguro de continuar?");
            if (value == 1) {
                btnGuardar.setDisable(true);
                btnCancelar.setDisable(true);
                for (int i = 0; i < tvList.size(); i++) {
                    if (tvList.get(i).getCbSeleccion().isSelected() && !tvList.get(i).getCbSeleccion().isDisable()) {
                        tvList.get(i).setFechaPago(Tools.getDatePicker(dtFecha));
                        tvList.get(i).setHoraPago(Tools.getHour());
                        tvList.get(i).setEstado(true);
                    }
                }

                BancoHistorialTB bancoHistorialTB = new BancoHistorialTB();
                bancoHistorialTB.setIdBanco(cbCuenta.getSelectionModel().getSelectedItem().getIdBanco());
                bancoHistorialTB.setDescripcion("Salida de dinero por pago a proveedor".toUpperCase());
                bancoHistorialTB.setFecha(Tools.getDate());
                bancoHistorialTB.setHora(Tools.getHour());
                bancoHistorialTB.setEntrada(0);
                bancoHistorialTB.setSalida(montoPagar);

                /*
                1=ingreso
                2=salida
                 */
                TransaccionTB transaccionTB = new TransaccionTB();
                transaccionTB.setFecha(Tools.getDate());
                transaccionTB.setHora(Tools.getHour());
                transaccionTB.setDescripcion("SALIDA DE DINERO POR PAGO DEL COMPROBANTE " + comprobante.toUpperCase());
                transaccionTB.setTipoTransaccion((short) 2);
                transaccionTB.setMonto(montoPagar);
                transaccionTB.setUsuario(Session.USER_ID);

                ModeloObject result = CompraADO.Registrar_Amortizacion(idCompra, tvList, bancoHistorialTB, transaccionTB);
                if (result.getState().equalsIgnoreCase("updated")) {
                    Tools.AlertMessageInformation(apWindow, "Generar Pago", "Se registro correctamente el pago.");
                    Tools.Dispose(apWindow);
                    cuentasPorPagarVisualizarController.validarEstadoCompra();
                    cuentasPorPagarVisualizarController.loadTableCompraCredito(idCompra);
                    cuentasPorPagarVisualizarController.openWindowReport(result.getMessage());
                } else if (result.getState().equalsIgnoreCase("pagado")) {
                    Tools.AlertMessageWarning(apWindow, "Generar Pago", "La cuota seleccionada ya está cancelada.");
                    Tools.Dispose(apWindow);
                } else if (result.getState().equalsIgnoreCase("error")) {
                    Tools.AlertMessageError(apWindow, "Generar Pago", result.getMessage());
                    btnGuardar.setDisable(false);
                    btnCancelar.setDisable(false);
                }

            }
        }
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
