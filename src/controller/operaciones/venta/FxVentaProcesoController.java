package controller.operaciones.venta;

import controller.tools.ConvertMonedaCadena;
import controller.tools.Tools;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.ResultTransaction;
import model.SuministroTB;
import model.VentaADO;
import model.VentaTB;

public class FxVentaProcesoController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private HBox hbContenido;
    @FXML
    private Label lblTotal;
    @FXML
    private TextField txtEfectivo;
    @FXML
    private TextField txtTarjeta;
    @FXML
    private Label lblVuelto;
    @FXML
    private VBox vbEfectivo;
    @FXML
    private VBox vbCredito;
    @FXML
    private VBox vbViewEfectivo;
    @FXML
    private VBox vbViewCredito;
    @FXML
    private Label lblEfectivo;
    @FXML
    private Label lblCredito;
    @FXML
    private Label lblMonedaLetras;    
    @FXML
    private Label lblVueltoNombre;
    @FXML
    private DatePicker txtFechaVencimiento;

    private FxVentaEstructuraController ventaEstructuraController;

    private ArrayList<SuministroTB> tvList;

    private ConvertMonedaCadena monedaCadena;

    private VentaTB ventaTB;

    private String moneda_simbolo;

    private double vuelto;

    private boolean estado = false;

    private double tota_venta;

    private boolean state_view_pago;

    private boolean provilegios;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_PRESSED);
        state_view_pago = false;
        tota_venta = 0;
        vuelto = 0.00;
        monedaCadena = new ConvertMonedaCadena();
        lblVueltoNombre.setText("Su cambio: ");
    }

    public void setInitComponents(VentaTB ventaTB, ArrayList<SuministroTB> tvList, boolean provilegios) {
        this.ventaTB = ventaTB;
        this.tvList = tvList;
        moneda_simbolo = ventaTB.getMonedaName();
        tota_venta = Double.parseDouble(Tools.roundingValue(ventaTB.getTotal(), 2));
        lblTotal.setText("TOTAL A PAGAR: " + moneda_simbolo + " " + Tools.roundingValue(tota_venta, 2));
        lblVuelto.setText(moneda_simbolo + " " + Tools.roundingValue(vuelto, 2));
        lblMonedaLetras.setText(monedaCadena.Convertir(Tools.roundingValue(tota_venta, 2), true, ""));
        hbContenido.setDisable(false);
        this.provilegios = provilegios;
    }

    private void TotalAPagar() {
        if (txtEfectivo.getText().isEmpty() && txtTarjeta.getText().isEmpty()) {
            lblVuelto.setText(moneda_simbolo + " 0.00");
            lblVueltoNombre.setText("POR PAGAR: ");
            estado = false;
        } else if (txtEfectivo.getText().isEmpty()) {
            if (Double.parseDouble(txtTarjeta.getText()) >= tota_venta) {
                vuelto = Double.parseDouble(txtTarjeta.getText()) - tota_venta;
                lblVueltoNombre.setText("SU CAMBIO ES: ");
                estado = true;
            } else {
                vuelto = tota_venta - Double.parseDouble(txtTarjeta.getText());
                lblVueltoNombre.setText("POR PAGAR: ");
                estado = false;
            }

        } else if (txtTarjeta.getText().isEmpty()) {
            if (Double.parseDouble(txtEfectivo.getText()) >= tota_venta) {
                vuelto = Double.parseDouble(txtEfectivo.getText()) - tota_venta;
                lblVueltoNombre.setText("SU CAMBIO ES: ");
                estado = true;
            } else {
                vuelto = tota_venta - Double.parseDouble(txtEfectivo.getText());
                lblVueltoNombre.setText("POR PAGAR: ");
                estado = false;
            }
        } else {
            double suma = (Double.parseDouble(txtEfectivo.getText())) + (Double.parseDouble(txtTarjeta.getText()));
            if (suma >= tota_venta) {
                vuelto = suma - tota_venta;
                lblVueltoNombre.setText("SU CAMBIO ES: ");
                estado = true;
            } else {
                vuelto = tota_venta - suma;
                lblVueltoNombre.setText("POR PAGAR: ");
                estado = false;
            }
        }

        lblVuelto.setText(moneda_simbolo + " " + Tools.roundingValue(vuelto, 2));
    }

    private void onEventAceptar() {
        if (state_view_pago) {
            if (txtFechaVencimiento.getValue() == null) {
                Tools.AlertMessageWarning(window, "Venta", "Ingrese la fecha de vencimiento.");
                txtFechaVencimiento.requestFocus();
            } else {
                ventaTB.setTipo(2);
                ventaTB.setEstado(2);
                ventaTB.setVuelto(0);
                ventaTB.setEfectivo(0);
                ventaTB.setTarjeta(0);
                ventaTB.setObservaciones("");
                ventaTB.setFechaVencimiento(Tools.getDatePicker(txtFechaVencimiento));
                ventaTB.setHoraVencimiento(Tools.getHour());
                short confirmation = Tools.AlertMessageConfirmation(window, "Venta", "¿Está seguro de continuar?");
                if (confirmation == 1) {
                    ResultTransaction result = VentaADO.registrarVentaCredito(ventaTB, tvList, ventaEstructuraController.getIdTipoComprobante(), provilegios);
                    switch (result.getCode()) {
                        case "register":
                            short value = Tools.AlertMessage(window.getScene().getWindow(), "Venta", "Se realizó la venta con éxito, ¿Desea imprimir el comprobante?");
                            if (value == 1) {
                                ventaEstructuraController.resetVenta();
                                ventaEstructuraController.imprimirVenta(result.getResult());
                                Tools.Dispose(window);
                            } else {
                                ventaEstructuraController.resetVenta();
                                Tools.Dispose(window);
                            }
                            break;
                        case "nocantidades":
                            Tools.AlertDialogMessage(window, Alert.AlertType.WARNING, "Venta", "No se puede completar la venta por que hay productos con stock inferior.", result.toStringArrayResult());
                            break;
                        case "error":
                            Tools.AlertMessageError(window, "Venta", result.getResult());
                            break;
                        default:
                            Tools.AlertMessageError(window, "Venta", result.getResult());
                            break;
                    }
                }

            }
        } else {
            if (!estado) {
                Tools.AlertMessageWarning(window, "Venta", "El monto es menor que el total.");
                txtEfectivo.requestFocus();
            } else {
                ventaTB.setTipo(1);
                ventaTB.setEstado(1);
                ventaTB.setVuelto(vuelto);
                ventaTB.setObservaciones("");

                ventaTB.setEfectivo(0);
                ventaTB.setTarjeta(0);

                if (Tools.isNumeric(txtEfectivo.getText()) && Double.parseDouble(txtEfectivo.getText()) > 0) {
                    ventaTB.setEfectivo(Double.parseDouble(txtEfectivo.getText()));
                }

                if (Tools.isNumeric(txtTarjeta.getText()) && Double.parseDouble(txtTarjeta.getText()) > 0) {
                    ventaTB.setTarjeta(Double.parseDouble(txtTarjeta.getText()));
                }

                if (Tools.isNumeric(txtEfectivo.getText()) && Tools.isNumeric(txtTarjeta.getText())) {
                    if ((Double.parseDouble(txtEfectivo.getText())) >= tota_venta) {
                        Tools.AlertMessageWarning(window, "Venta", "Los valores ingresados no son correctos!!");
                        return;
                    }
                    double efectivo = Double.parseDouble(txtEfectivo.getText());
                    double tarjeta = Double.parseDouble(txtTarjeta.getText());
                    if ((efectivo + tarjeta) != tota_venta) {
                        Tools.AlertMessageWarning(window, "Venta", " El monto a pagar no debe ser mayor al total!!");
                        return;
                    }
                }

                if (!Tools.isNumeric(txtEfectivo.getText()) && Tools.isNumeric(txtTarjeta.getText())) {
                    if ((Double.parseDouble(txtTarjeta.getText())) > tota_venta) {
                        Tools.AlertMessageWarning(window, "Venta", "El pago con tarjeta no debe ser mayor al total!!");
                        return;
                    }
                }

                short confirmation = Tools.AlertMessageConfirmation(window, "Venta", "¿Esta seguro de continuar?");
                if (confirmation == 1) {
                    ResultTransaction result = VentaADO.registrarVentaContado(ventaTB, tvList, ventaEstructuraController.getIdTipoComprobante(), provilegios);
                    switch (result.getCode()) {
                        case "register":
                            short value = Tools.AlertMessage(window.getScene().getWindow(), "Venta", "Se realizó la venta con éxito, ¿Desea imprimir el comprobante?");
                            if (value == 1) {
                                ventaEstructuraController.resetVenta();
                                ventaEstructuraController.imprimirVenta(result.getResult());
                                Tools.Dispose(window);
                            } else {
                                ventaEstructuraController.resetVenta();
                                Tools.Dispose(window);
                            }
                            break;
                        case "nocantidades":
                            Tools.AlertDialogMessage(window, Alert.AlertType.WARNING, "Venta", "No se puede completar la venta por que hay productos con stock inferior.", result.toStringArrayResult());
                            break;
                        case "error":
                            Tools.AlertMessageError(window, "Venta", result.getResult());
                            break;
                        default:
                            Tools.AlertMessageError(window, "Venta", result.getResult());
                            break;
                    }
                }

            }
        }
    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        onEventAceptar();
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventAceptar();
        }
    }

    @FXML
    private void onKeyReleasedEfectivo(KeyEvent event) {
        if (txtEfectivo.getText().isEmpty()) {
            vuelto = tota_venta;
            TotalAPagar();
            return;
        }
        if (Tools.isNumeric(txtEfectivo.getText())) {
            TotalAPagar();
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

    @FXML
    private void OnKeyReleasedTarjeta(KeyEvent event) {
        if (txtTarjeta.getText().isEmpty()) {
            vuelto = tota_venta;
            TotalAPagar();
            return;
        }
        if (Tools.isNumeric(txtTarjeta.getText())) {
            TotalAPagar();
        }
    }

    @FXML
    private void OnKeyTypedTarjeta(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
            event.consume();
        }
        if (c == '.' && txtTarjeta.getText().contains(".")) {
            event.consume();
        }
    }

    @FXML
    private void onMouseClickedEfectivo(MouseEvent event) {
        if (state_view_pago) {
            vbCredito.setStyle("-fx-background-color: white;-fx-cursor:hand;-fx-padding:  0.5em;");
            vbEfectivo.setStyle("-fx-background-color:  #007bff;-fx-cursor:hand;-fx-padding:  0.5em");

            lblCredito.setStyle("-fx-text-fill:#1a2226;");
            lblEfectivo.setStyle("-fx-text-fill:white;");

            vbViewCredito.setVisible(false);
            vbViewEfectivo.setVisible(true);

            txtEfectivo.requestFocus();

            state_view_pago = false;
        }
    }

    @FXML
    private void onMouseClickedCredito(MouseEvent event) {
        if (!state_view_pago) {
            vbEfectivo.setStyle("-fx-background-color: white;-fx-cursor:hand;-fx-padding:  0.5em;");
            vbCredito.setStyle("-fx-background-color: #007bff;-fx-cursor:hand;-fx-padding:  0.5em");

            lblEfectivo.setStyle("-fx-text-fill:#1a2226;");
            lblCredito.setStyle("-fx-text-fill:white;");

            vbViewEfectivo.setVisible(false);
            vbViewCredito.setVisible(true);
            state_view_pago = true;
        }
    }

    public void setInitVentaEstructuraController(FxVentaEstructuraController ventaEstructuraController) {
        this.ventaEstructuraController = ventaEstructuraController;
    }

}
