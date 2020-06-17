package controller.operaciones.venta;

import controller.tools.Session;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import model.BancoHistorialTB;
import model.SuministroTB;
import model.VentaADO;

public class FxVentaDevolucionController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private Text lblComprobante;
    @FXML
    private Label lblTotal;
    @FXML
    private RadioButton rbMovimiento;
    @FXML
    private TextField txtEfectivo;
    @FXML
    private TextField txtObservacion;

    private FxVentaDetalleController ventaDetalleController;

    private FxVentaMostrarController ventaMostrarController;

    private String idVenta;

    private ObservableList<SuministroTB> arrList;

    private double totalVenta;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setLoadVentaDevolucion(String idVenta, ObservableList<SuministroTB> arrList, String comprobante, String total, double totalVenta) {
        this.idVenta = idVenta;
        this.arrList = arrList;
        this.totalVenta = totalVenta;
        lblComprobante.setText(comprobante);
        lblTotal.setText(total);
        txtEfectivo.setText(Tools.roundingValue(totalVenta, 2));
    }

    private void eventAceptar() {

        if (txtObservacion.getText().trim().isEmpty()) {
            Tools.AlertMessageWarning(window, "Detalle de venta", "Ingrese un comentario.");
            txtObservacion.requestFocus();
        } else {
            short validate = Tools.AlertMessageConfirmation(window, "Detalle de ventas", "¿Está seguro de cancelar la venta?");
            if (validate == 1) {

                BancoHistorialTB bancoHistorialBancaria = new BancoHistorialTB();
                bancoHistorialBancaria.setIdBanco(Session.ID_CUENTA_BANCARIA);
                bancoHistorialBancaria.setIdEmpleado(Session.USER_ID);
                bancoHistorialBancaria.setDescripcion(txtObservacion.getText());
                bancoHistorialBancaria.setFecha(Tools.getDate());
                bancoHistorialBancaria.setHora(Tools.getHour());
                bancoHistorialBancaria.setEntrada(0);
                bancoHistorialBancaria.setSalida(totalVenta);

                String result = VentaADO.CancelTheSale(idVenta, arrList, bancoHistorialBancaria);
                if (result.equalsIgnoreCase("updated")) {
                    Tools.AlertMessageInformation(window, "Detalle de ventas", "Se anulo correctamente.");
                    Tools.Dispose(window);
                } else if (result.equalsIgnoreCase("scrambled")) {
                    Tools.AlertMessageWarning(window, "Detalle de venta", "Ya está anulada la venta.");
                } else {
                    Tools.AlertMessageError(window, "Detalle de ventas", result);
                }

//                    MovimientoCajaTB cajaTB = new MovimientoCajaTB();
//                    cajaTB.setIdCaja(Session.CAJA_ID);
//                    cajaTB.setIdUsuario(Session.USER_ID);
//                    cajaTB.setFechaMovimiento(Tools.getDate());
//                    cajaTB.setHoraMovimiento(Tools.getHour());
//                    cajaTB.setComentario(txtObservacion.getText().trim());
//                    cajaTB.setMovimiento("VENCAN");
//                    cajaTB.setSalidas(Double.parseDouble(txtEfectivo.getText()));
//                    cajaTB.setSaldo(Double.parseDouble(txtEfectivo.getText()));
//
//                    String result = VentaADO.CancelTheSale(idVenta, arrList, totalVenta, cajaTB);
//                    if (result.equalsIgnoreCase("update")) {
//                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Detalle de venta", "Se ha cancelado con éxito.", false);
//                        Tools.Dispose(window);
//                        if (ventaDetalleController != null) {
//                            ventaDetalleController.setInitComponents(idVenta);
//                        } else if (ventaMostrarController != null) {
//
//                        }
//                    } else if (result.equalsIgnoreCase("scrambled")) {
//                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Detalle de venta", "Ya está cancelada la venta!.", false);
//                    } else {
//                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Detalle de venta", result, false);
//                    }
            }
        }

    }

    @FXML
    private void onActionMovimiento(ActionEvent event) {
        txtEfectivo.setDisable(!rbMovimiento.isSelected());
    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        eventAceptar();
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            eventAceptar();
        }
    }

    @FXML
    private void onKeyTypedEfectivo(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
            event.consume();
        }
        if (c == '.' && txtEfectivo.getText().contains(".")) {
            event.consume();
        }
    }

    public void setInitVentaDetalle(FxVentaDetalleController ventaDetalleController) {
        this.ventaDetalleController = ventaDetalleController;
    }

    public void setInitVentaMostrar(FxVentaMostrarController ventaMostrarController) {
        this.ventaMostrarController = ventaMostrarController;
    }

}
