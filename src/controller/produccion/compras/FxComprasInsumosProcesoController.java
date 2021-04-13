package controller.produccion.compras;

import controller.tools.Session;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.BancoADO;
import model.BancoHistorialTB;
import model.BancoTB;
import model.CompraADO;
import model.CompraTB;
import model.DetalleCompraTB;
import model.LoteTB;

/**
 * FXML Controller class
 *
 * @author RYZEN
 */
public class FxComprasInsumosProcesoController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private Label lblTotal;
    @FXML
    private RadioButton rbContado;
    @FXML
    private HBox hbPagoContado;
    @FXML
    private ComboBox<BancoTB> cbCuentas;
    @FXML
    private TextField txtEfectivo;
    @FXML
    private RadioButton rbCredito;
    @FXML
    private VBox vbPagoCredito;
    @FXML
    private DatePicker txtFechaVencimiento;
    @FXML
    private RadioButton rbBorrado;
    @FXML
    private VBox vbPagoBorrado;

    private FxComprasInsumosController comprasInsumosController;

    private CompraTB compraTB = null;

    private TableView<DetalleCompraTB> tvList;

    private ObservableList<LoteTB> loteTBs;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
        ToggleGroup group = new ToggleGroup();
        rbContado.setToggleGroup(group);
        rbCredito.setToggleGroup(group);
        rbBorrado.setToggleGroup(group);
        setInitializeCaja();
    }

    private void setInitializeCaja() {
        cbCuentas.getItems().clear();
        cbCuentas.getItems().add(new BancoTB("0", "Seleccione una cuenta..."));
        cbCuentas.getItems().addAll(BancoADO.GetBancoComboBox());
        cbCuentas.getSelectionModel().select(0);
    }

    public void setLoadProcess(CompraTB compraTB, TableView<DetalleCompraTB> tvList, ObservableList<LoteTB> loteTBs) {
        this.compraTB = compraTB;
        this.tvList = tvList;
        this.loteTBs = loteTBs;
        lblTotal.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(compraTB.getTotal(), 2));
        txtEfectivo.setText(Tools.roundingValue(compraTB.getTotal(), 2));
    }

    private void onEventProcess() {
        if (rbContado.isSelected()) {
            if (cbCuentas.getSelectionModel().getSelectedIndex() <= 0) {
                Tools.AlertMessageWarning(apWindow, "Compra", "Seleccione una cuenta.");
                cbCuentas.requestFocus();
            } else if (!Tools.isNumeric(txtEfectivo.getText())) {
                Tools.AlertMessageWarning(apWindow, "Compra", "Ingrese el valor recibido.");
                txtEfectivo.requestFocus();
            } else {
                compraTB.setTipoDocumento(0);
                compraTB.setTipo(1);
                compraTB.setEstado(1);
                compraTB.setFechaVencimiento(compraTB.getFechaCompra());
                compraTB.setHoraVencimiento(compraTB.getHoraCompra());

                BancoHistorialTB bancoHistorialTB = new BancoHistorialTB();
                bancoHistorialTB.setIdBanco(cbCuentas.getSelectionModel().getSelectedItem().getIdBanco());
                bancoHistorialTB.setIdEmpleado(Session.USER_ID);
                bancoHistorialTB.setDescripcion("Salida de dinero por compra");
                bancoHistorialTB.setFecha(Tools.getDate());
                bancoHistorialTB.setHora(Tools.getHour());
                bancoHistorialTB.setEntrada(0);
                bancoHistorialTB.setSalida(Double.parseDouble(txtEfectivo.getText()));

                String result = CompraADO.Compra_Contado_Insumos(bancoHistorialTB, compraTB, tvList, loteTBs);
                if (result.equalsIgnoreCase("register")) {
                    Tools.AlertMessageInformation(apWindow, "Compra", "Se registró correctamente la compra.");
                    Tools.Dispose(apWindow);
                    if (comprasInsumosController != null) {
                        comprasInsumosController.clearComponents();
                    } 
                } else {
                    Tools.AlertMessageError(apWindow, "Compra", result);
                }
            }
        } else if (rbCredito.isSelected()) {
            if (txtFechaVencimiento.getValue() == null) {
                Tools.AlertMessageWarning(apWindow, "Compra", "Ingrese la fecha vencimiento.");
                txtFechaVencimiento.requestFocus();
            } else {
                compraTB.setTipo(2);
                compraTB.setEstado(2);
                compraTB.setFechaVencimiento(Tools.getDatePicker(txtFechaVencimiento));
                compraTB.setHoraVencimiento(compraTB.getHoraCompra());

                String result = CompraADO.Compra_Credito(compraTB, tvList, loteTBs);
                if (result.equalsIgnoreCase("register")) {
                    Tools.AlertMessageInformation(apWindow, "Compra", "Se registró correctamente la compra.");
                    Tools.Dispose(apWindow);
                    if (comprasInsumosController != null) {
                        comprasInsumosController.clearComponents();
                    }
                } else {
                    Tools.AlertMessageError(apWindow, "Compra", result);
                }
            }
        } else if (rbBorrado.isSelected()) {
            compraTB.setTipo(3);
            compraTB.setEstado(4);
            compraTB.setFechaVencimiento(compraTB.getFechaCompra());
            compraTB.setHoraVencimiento(compraTB.getHoraCompra());

            String result = CompraADO.Compra_Borrado(compraTB, tvList, loteTBs);
            if (result.equalsIgnoreCase("register")) {
                Tools.AlertMessageInformation(apWindow, "Compra", "Se registró correctamente la compra como guardado.");
                Tools.Dispose(apWindow);
                if (comprasInsumosController != null) {
                    comprasInsumosController.clearComponents();
                } 
            } else {
                Tools.AlertMessageError(apWindow, "Compra", result);
            }
        }
    }

    @FXML
    private void onKeyTypedEfectivo(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtEfectivo.getText().contains(".") || c == '-' && txtEfectivo.getText().contains("-")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventProcess();
        }
    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        onEventProcess();
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

    @FXML
    private void onActionRbContado(ActionEvent event) {
        vbPagoCredito.setDisable(true);
        vbPagoBorrado.setDisable(true);
        hbPagoContado.setDisable(false);
    }

    @FXML
    private void onActionRbCredito(ActionEvent event) {
        hbPagoContado.setDisable(true);
        vbPagoBorrado.setDisable(true);
        vbPagoCredito.setDisable(false);
    }

    @FXML
    private void onActionRbBorrado(ActionEvent event) {
        hbPagoContado.setDisable(true);
        vbPagoCredito.setDisable(true);
        vbPagoBorrado.setDisable(false);
    }

    public void setInitComprasController(FxComprasInsumosController comprasInsumosController) {
        this.comprasInsumosController = comprasInsumosController;
    }

}
