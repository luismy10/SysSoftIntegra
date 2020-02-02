package controller.consultas.compras;

import controller.operaciones.compras.FxComprasCreditoController;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

public class FxAmortizarPagosController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private TextField txtMonto;
    @FXML
    private DatePicker dtFecha;
    @FXML
    private ComboBox<BancoTB> cbCuenta;

    private FxComprasCreditoController creditoController;

    private int idCompraCredito;

    private String idCompra;

    private double monto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
        Tools.actualDate(Tools.getDate(), dtFecha);
        cbCuenta.getItems().add(new BancoTB("0", "Seleccionar..."));
        BancoADO.GetBancoComboBox().forEach(e -> {
            cbCuenta.getItems().add(new BancoTB(e.getIdBanco(), e.getNombreCuenta()));
        });
        cbCuenta.getSelectionModel().select(0);
    }

    public void setInitValues(String idCompra, int idCompraCredito, double monto) {
        this.idCompra = idCompra;
        this.idCompraCredito = idCompraCredito;
        this.monto = monto;
        txtMonto.setText(Tools.roundingValue(monto, 2));
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
                CompraCreditoTB compraCreditoTB = new CompraCreditoTB();
                compraCreditoTB.setIdCompraCredito(idCompraCredito);
                compraCreditoTB.setFechaPago(Tools.getDatePicker(dtFecha));
                compraCreditoTB.setHoraPago(Tools.getHour());
                compraCreditoTB.setEstado(true);

                BancoHistorialTB bancoHistorialTB = new BancoHistorialTB();
                bancoHistorialTB.setIdBanco(cbCuenta.getSelectionModel().getSelectedItem().getIdBanco());
                bancoHistorialTB.setDescripcion("Salida de dinero por pago a proveedor");
                bancoHistorialTB.setFecha(Tools.getDate());
                bancoHistorialTB.setHora(Tools.getHour());
                bancoHistorialTB.setEntrada(0);
                bancoHistorialTB.setSalida(monto);

                String result = CompraADO.Registrar_Amortizacion(compraCreditoTB, bancoHistorialTB);
                if (result.equalsIgnoreCase("updated")) {
                    Tools.AlertMessageInformation(apWindow, "Generar Pago", "Se registro correctamente el pago.");
                    ObservableList<CompraCreditoTB> observableList = CompraADO.Listar_Compra_Credito(idCompra);
                    int validateTotal = 0;
                    int validateActual = 0;
                    for (CompraCreditoTB creditoTB : observableList) {
                        if (creditoTB.isEstado()) {
                            validateActual++;
                        }
                        validateTotal++;
                    }
                    if (validateTotal == validateActual) {
                        String completed = CompraADO.Actualizar_Compra_Estado(idCompra);
                        if (completed.equalsIgnoreCase("updated")) {
                            Tools.AlertMessageInformation(apWindow, "Generar Pago", "Se completo todos los pagos.");
                            creditoController.loadTableCompraCredito();
                        }
                    } else {
                        creditoController.loadTableCompraCredito();
                    }
                    Tools.Dispose(apWindow);
                } else if (result.equalsIgnoreCase("pagado")) {
                    Tools.AlertMessageWarning(apWindow, "Generar Pago", "La cuota seleccionada ya está cancelada.");
                    Tools.Dispose(apWindow);
                } else {
                    Tools.AlertMessageError(apWindow, "Generar Pago", result);
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

    public void setInitAmortizarPagosController(FxComprasCreditoController creditoController) {
        this.creditoController = creditoController;
    }

}
