package controller.operaciones.venta;

import controller.tools.ConvertMonedaCadena;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.SuministroTB;
import model.VentaADO;
import model.VentaCreditoTB;
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
    private Label lblVuelto;
    @FXML
    private Text lblComprobante;
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
    private TextField txtTarjeta;
    @FXML
    private Label lblVueltoNombre;
    @FXML
    private TableView<VentaCreditoTB> tvPlazos;
    @FXML
    private TableColumn<VentaCreditoTB, TextField> tcMonto;
    @FXML
    private TableColumn<VentaCreditoTB, DatePicker> tcFecha;
    @FXML
    private TableColumn<VentaCreditoTB, CheckBox> tcMontoInicial;
    @FXML
    private TableColumn<VentaCreditoTB, Button> tcOpcion;
//    private TextField txtObservacion;

    private FxVentaEstructuraController ventaEstructuraController;

    private TableView<SuministroTB> tvList;

    private ConvertMonedaCadena monedaCadena;

    private VentaTB ventaTB;

    private String moneda_simbolo;

    private double vuelto;

    private boolean estado = false;

    private double tota_venta;

    private boolean state_view_pago;
    @FXML
    private Label lblMontoPagar;
    @FXML
    private Label lblMontoTotal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_PRESSED);
        state_view_pago = false;
        tota_venta = 0;
        vuelto = 0.00;
        monedaCadena = new ConvertMonedaCadena();
        lblVueltoNombre.setText("Su cambio: ");
        tcMonto.setCellValueFactory(new PropertyValueFactory<>("tfMonto"));
        tcFecha.setCellValueFactory(new PropertyValueFactory<>("dpFecha"));
        tcMontoInicial.setCellValueFactory(new PropertyValueFactory<>("cbMontoInicial"));
        tcOpcion.setCellValueFactory(new PropertyValueFactory<>("btnQuitar"));
    }

    public void setInitComponents(VentaTB ventaTB, TableView<SuministroTB> tvList) {
        this.ventaTB = ventaTB;
        this.tvList = tvList;
        moneda_simbolo = ventaTB.getMonedaName();
        lblComprobante.setText(ventaTB.getComprobanteName());
        tota_venta = ventaTB.getTotal();
        lblTotal.setText("TOTAL A PAGAR: " + moneda_simbolo + " " + Tools.roundingValue(ventaTB.getTotal(), 2));
        lblMontoTotal.setText("MONTO TOTAL: " + Tools.roundingValue(ventaTB.getTotal(), 2));
        lblVuelto.setText(moneda_simbolo + " " + Tools.roundingValue(vuelto, 2));
        lblMonedaLetras.setText(monedaCadena.Convertir(Tools.roundingValue(ventaTB.getTotal(), 2), true, ventaEstructuraController.getMonedaNombre()));
        hbContenido.setDisable(false);
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

            int validateMonto = 0;
            int validateFecha = 0;
            double montoPagar = 0;
            for (VentaCreditoTB creditoTB : tvPlazos.getItems()) {
                validateMonto += !Tools.isNumeric(creditoTB.getTfMonto().getText()) ? 1 : 0;
                validateFecha += creditoTB.getDpFecha().getValue() == null ? 1 : 0;
                montoPagar += creditoTB.getMonto();
            }

            if (validateMonto > 0) {
                Tools.AlertMessageWarning(window, "Venta", "Hay montos sin ingresar en la tabla.");
                tvPlazos.requestFocus();
            } else if (validateFecha > 0) {
                Tools.AlertMessageWarning(window, "Venta", "Hay fechas sin ingresar en la tabla.");
                tvPlazos.requestFocus();
            } else if (ventaTB.getTotal() != montoPagar) {
                Tools.AlertMessageWarning(window, "Venta", "El monto total y el monto a pagar no son iguales.");
                tvPlazos.requestFocus();
            } else {

                ventaTB.setTipo(2);
                ventaTB.setEstado(2);
                ventaTB.setVuelto(0);
                ventaTB.setEfectivo(0);
                ventaTB.setTarjeta(0);
                ventaTB.setObservaciones("");
                short confirmation = Tools.AlertMessageConfirmation(window, "Venta", "¿Esta seguro de continuar?");
                if (confirmation == 1) {

                }

            }
        } else {
            if (estado == false) {
                Tools.AlertMessageWarning(window, "Venta", "El monto es menor que el total.");
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
                    String result[] = VentaADO.registrarVentaContado(
                            ventaTB,
                            tvList,
                            ventaEstructuraController.getIdTipoComprobante()).split("/");
                    switch (result[0]) {
                        case "register":
                            short value = Tools.AlertMessage(window.getScene().getWindow(), "Venta", "Se realizó la venta con éxito, ¿Desea imprimir el comprobante?");
                            if (value == 1) {
                                ventaEstructuraController.imprimirVenta(result[1], result[2], txtEfectivo.getText(), Tools.roundingValue(vuelto, 2), true);
                                Tools.Dispose(window);
                            } else {
                                ventaEstructuraController.resetVenta();
                                Tools.Dispose(window);
                            }
                            break;
                        default:
                            Tools.AlertMessageError(window, "Venta", result[0]);
                            break;
                    }
                }

            }
        }
    }

    private void addElementPlazos() {
        VentaCreditoTB vc = new VentaCreditoTB();

        TextField textField = new TextField();
        textField.setPromptText("0.00");
        textField.setAlignment(Pos.CENTER);
        textField.getStyleClass().add("text-field-normal");
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            double sumaMontos = 0;
            if (!newVal) {
                if (!Tools.isNumeric(textField.getText())) {
                    textField.setText("0.00");
                    vc.setMonto(Double.parseDouble(textField.getText()));
                }
                sumaMontos = tvPlazos.getItems().stream().map(
                        (cctb) -> cctb.getMonto())
                        .reduce(sumaMontos, (accumulator, _item) -> accumulator + _item);
                lblMontoPagar.setText("MONTO A PAGAR: " + Tools.roundingValue(sumaMontos, 2));
            }
        });
        textField.setOnKeyReleased(event -> {
            if (Tools.isNumeric(textField.getText())) {
                vc.setMonto(Double.parseDouble(textField.getText()));
            }
        });
        textField.setOnKeyTyped(event -> {
            char c = event.getCharacter().charAt(0);
            if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
                event.consume();
            }
            if (c == '.' && textField.getText().contains(".")) {
                event.consume();
            }
        });
        vc.setTfMonto(textField);

        DatePicker datePicker = new DatePicker();
        datePicker.getStyleClass().add("");
        datePicker.setPromptText("00/00/0000");
        datePicker.setEditable(false);
        datePicker.setOnAction(event -> {
            if (datePicker.getValue() != null) {
                vc.setFechaPago(datePicker.getValue().toString());
            }
        });
        vc.setDpFecha(datePicker);

        CheckBox checkBox = new CheckBox();
        checkBox.getStyleClass().add("check-box-contenido");
        vc.setCbMontoInicial(checkBox);

        Button button = new Button("X");
        button.getStyleClass().add("buttonDark");
        button.setOnAction(event -> {
            tvPlazos.getItems().remove(vc);
        });
        button.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                tvPlazos.getItems().remove(vc);
            }
        });
        vc.setBtnQuitar(button);

        tvPlazos.getItems().add(vc);

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
            vbCredito.setStyle("-fx-background-color: white;-fx-cursor:hand;-fx-padding: 0.8333333333333334em;");
            vbEfectivo.setStyle("-fx-background-color: #265B7C;-fx-cursor:hand;-fx-padding: 0.8333333333333334em;");

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
            vbEfectivo.setStyle("-fx-background-color: white;-fx-cursor:hand;-fx-padding: 0.8333333333333334em;");
            vbCredito.setStyle("-fx-background-color: #265B7C;-fx-cursor:hand;-fx-padding: 0.8333333333333334em;");

            lblEfectivo.setStyle("-fx-text-fill:#1a2226;");
            lblCredito.setStyle("-fx-text-fill:white;");

            vbViewEfectivo.setVisible(false);
            vbViewCredito.setVisible(true);
            state_view_pago = true;
        }
    }

    @FXML
    private void onActionAgregarPlazos(ActionEvent event) {
        addElementPlazos();
    }

    @FXML
    private void onKeyPressedPlazos(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            addElementPlazos();
        }
    }

    public void setInitVentaEstructuraController(FxVentaEstructuraController ventaEstructuraController) {
        this.ventaEstructuraController = ventaEstructuraController;
    }

}
