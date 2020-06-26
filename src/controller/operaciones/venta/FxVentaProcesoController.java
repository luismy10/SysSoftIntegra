package controller.operaciones.venta;

import controller.tools.ConvertMonedaCadena;
import controller.tools.SearchComboBox;
import controller.tools.Session;
import controller.tools.Tools;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.BancoADO;
import model.BancoHistorialTB;
import model.ClienteADO;
import model.ClienteTB;
import model.CuentasClienteTB;
import model.FormaPagoTB;
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
    private TextField txtObservacion;

    private FxVentaEstructuraController ventaEstructuraController;

    private TableView<SuministroTB> tvList;

    private ConvertMonedaCadena monedaCadena;

    private VentaTB ventaTB;

    private String moneda_simbolo;

    private SearchComboBox<ClienteTB> searchComboBox;

    private double vuelto;

    private boolean estado = false;

    private double tota_venta;

    private boolean state_view_pago;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_PRESSED);
        state_view_pago = false;
        tota_venta = 0;
        vuelto = 0.00;
        monedaCadena = new ConvertMonedaCadena();
        lblVueltoNombre.setText("Su cambio: ");
//        searchComboBox = new SearchComboBox<>(cbCliente);
//        searchComboBox.setFilter((item, text) -> item.getInformacion().toLowerCase().contains(text.toLowerCase()) || item.getNumeroDocumento().toLowerCase().contains(text.toLowerCase()));

    }

    public void setInitComponents(VentaTB ventaTB, TableView<SuministroTB> tvList, double total) {
        this.ventaTB = ventaTB;
        this.tvList = tvList;
        moneda_simbolo = ventaTB.getMonedaName();
        Session.TICKET_SIMBOLOMONEDA = moneda_simbolo;
        lblComprobante.setText(ventaTB.getComprobanteName());
        tota_venta = total;
        lblTotal.setText(moneda_simbolo + " " + Tools.roundingValue(total, 2));
        lblVuelto.setText(moneda_simbolo + " " + Tools.roundingValue(vuelto, 2));
        lblMonedaLetras.setText(monedaCadena.Convertir(Tools.roundingValue(total, 2), true, ventaEstructuraController.getMonedaNombre()));

        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                ArrayList<Object> objects = new ArrayList<>();
                objects.add(ClienteADO.GetSearchComboBoxCliente());
                objects.add(BancoADO.ValidarBanco(Session.ID_CUENTA_EFECTIVO, Session.NOMBRE_CUENTA_EFECTIVO));
                return objects;
            }
        };
        task.setOnSucceeded(e -> {
            ArrayList<Object> objects = task.getValue();
            if (objects.get(0) == null && objects.get(1) == null) {
                hbContenido.setDisable(true);
                Tools.AlertMessageError(window, "Venta", "Habrá nuevamente la ventana, se produjo un problema de conexión al traer los datos.");
                return;
            }
            List<ClienteTB> clienteTBset = (List<ClienteTB>) objects.get(0);
            searchComboBox.getComboBox().getItems().addAll(clienteTBset);
            boolean validate = (boolean) objects.get(1);
            if (!validate) {
                hbContenido.setDisable(true);
                Tools.AlertMessageWarning(window, "Venta", "Su caja no esta registrada en la base de datos o se modifico, dirijase al modulo CAJA/BANCO para configurar una nueva su caja.");
            }
            txtEfectivo.requestFocus();
        });
        task.setOnFailed(e -> {
            hbContenido.setDisable(true);
            Tools.AlertMessageError(window, "Venta", "Habrá nuevamente la ventana, se produjo un problema de conexión al traer los datos.");

        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }

    }

//    public void loadSearchProveedores() {
//        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
//            Thread t = new Thread(runnable);
//            t.setDaemon(true);
//            return t;
//        });
//
//        Task<List<ClienteTB>> task = new Task<List<ClienteTB>>() {
//            @Override
//            public List<ClienteTB> call() {
//                return ClienteADO.GetSearchComboBoxCliente();
//            }
//        };
//
//        task.setOnSucceeded((e) -> {
//            searchComboBox.getComboBox().getItems().clear();
//            List<ClienteTB> clienteTBs = task.getValue();
//            searchComboBox.getComboBox().getItems().addAll(clienteTBs);
////            cbCliente.requestFocus();
//        });
//
//        exec.execute(task);
//        if (!exec.isShutdown()) {
//            exec.shutdown();
//        }
//    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        if (state_view_pago) {

            ventaTB.setTipo(2);
            ventaTB.setEstado(2);
            ventaTB.setEfectivo(0);
            ventaTB.setVuelto(0);
            ventaTB.setObservaciones(txtObservacion.getText().trim());

            //  ventaTB.setCliente(cbCliente.getSelectionModel().getSelectedItem().getIdCliente());
            CuentasClienteTB cuentasCliente = new CuentasClienteTB();
//                cuentasCliente.setPlazos(cbPlazos.getSelectionModel().getSelectedItem().getIdPlazos());
//                cuentasCliente.setFechaVencimiento(LocalDateTime.of(dtVencimiento.getValue(), LocalTime.now()));
            short confirmation = Tools.AlertMessageConfirmation(window, "Venta", "¿Esta seguro de continuar?");
            if (confirmation == 1) {

//                    String[] result = VentaADO.CrudVenta(ventaTB, tvList, ventaEstructuraController.getIdTipoComprobante(), cuentasCliente).split("/");
//                    switch (result[0]) {
//                        case "register":
//                            Tools.AlertMessageInformation(window, "Venta", "Se guardo correctamente la venta al crédito.");
//                            ventaEstructuraController.resetVenta();
//                            Tools.Dispose(window);
//                            break;
//                        default:
//                            Tools.AlertMessageError(window, "Venta", result[0]);
//                            break;
//                    }
            }

        } else {
//            if (cbCliente.getSelectionModel().getSelectedIndex() < 0) {
//                Tools.AlertMessageWarning(window, "Venta", "Seleccione su cliente.");
//                cbCliente.requestFocus();
//            } else 
            if (estado == false) {
                Tools.AlertMessageWarning(window, "Venta", "El monto es menor que el total.");
            } else {

                ventaTB.setTipo(1);
                ventaTB.setEstado(1);
                ventaTB.setEfectivo(Tools.isNumeric(txtEfectivo.getText()) ? Double.parseDouble(txtEfectivo.getText()) : 0);
                ventaTB.setVuelto(vuelto);
                ventaTB.setObservaciones(txtObservacion.getText().trim());
//                ventaTB.setCliente(cbCliente.getSelectionModel().getSelectedItem().getIdCliente());
                ArrayList<FormaPagoTB> formaPagoTBs = new ArrayList();

                if (Tools.isNumeric(txtEfectivo.getText()) && Double.parseDouble(txtEfectivo.getText()) > 0) {
                    FormaPagoTB formaPagoTB = new FormaPagoTB();
                    formaPagoTB.setNombre("EFECTIVO");
                    formaPagoTB.setMonto(Double.parseDouble(txtEfectivo.getText()));
                    formaPagoTBs.add(formaPagoTB);
                }

                if (Tools.isNumeric(txtTarjeta.getText()) && Double.parseDouble(txtTarjeta.getText()) > 0) {
                    FormaPagoTB formaPagoTB = new FormaPagoTB();
                    formaPagoTB.setNombre("TARJETA");
                    formaPagoTB.setMonto(Double.parseDouble(txtTarjeta.getText()));
                    formaPagoTBs.add(formaPagoTB);
                }

                BancoHistorialTB bancoHistorialEfectivo = null;
                if (Tools.isNumeric(txtEfectivo.getText())) {
                    if (Session.ID_CUENTA_EFECTIVO == null || Session.ID_CUENTA_EFECTIVO.equalsIgnoreCase("")) {
                        Tools.AlertMessageWarning(window, "Venta", "No tiene un cuenta en efectivo aperturada!!");
                        return;
                    }
                    bancoHistorialEfectivo = new BancoHistorialTB();
                    bancoHistorialEfectivo.setIdBanco(Session.ID_CUENTA_EFECTIVO);
                    bancoHistorialEfectivo.setIdEmpleado(Session.USER_ID);
                    bancoHistorialEfectivo.setDescripcion("Venta Efectivo");
                    bancoHistorialEfectivo.setFecha(Tools.getDate());
                    bancoHistorialEfectivo.setHora(Tools.getHour());
                    bancoHistorialEfectivo.setEntrada((Double.parseDouble(txtEfectivo.getText())) > tota_venta ? tota_venta : (Double.parseDouble(txtEfectivo.getText())));
                }

                BancoHistorialTB bancoHistorialBancaria = null;
                if (Tools.isNumeric(txtTarjeta.getText())) {
                    if (Session.ID_CUENTA_BANCARIA == null || Session.ID_CUENTA_BANCARIA.equalsIgnoreCase("")) {
                        Tools.AlertMessageWarning(window, "Venta", "No tiene un cuenta bancaria aperturada!!");
                        return;
                    }
                    bancoHistorialBancaria = new BancoHistorialTB();
                    bancoHistorialBancaria.setIdBanco(Session.ID_CUENTA_BANCARIA);
                    bancoHistorialBancaria.setIdEmpleado(Session.USER_ID);
                    bancoHistorialBancaria.setDescripcion("Venta con Tarjeta");
                    bancoHistorialBancaria.setFecha(Tools.getDate());
                    bancoHistorialBancaria.setHora(Tools.getHour());
                    bancoHistorialBancaria.setEntrada(Double.parseDouble(txtTarjeta.getText()));
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
                    String[] result = VentaADO.registrarVentaContado(ventaTB, bancoHistorialEfectivo, bancoHistorialBancaria, formaPagoTBs, tvList, ventaEstructuraController.getIdTipoComprobante(), new CuentasClienteTB()).split("/");
                    switch (result[0]) {
                        case "register":
                            short value = Tools.AlertMessage(window.getScene().getWindow(), "Venta", "Se realizo la venta con éxito, ¿Desea imprimir el comprobante?");
                            if (value == 1) {
                                ventaEstructuraController.imprimirVenta(
                                        ventaEstructuraController.obtenerTipoComprobante(),
                                        tvList,
                                        Tools.roundingValue(ventaTB.getSubTotal(), 2),
                                        Tools.roundingValue(ventaTB.getDescuento(), 2),
                                        Tools.roundingValue(ventaTB.getSubImporte(), 2),
                                        Tools.roundingValue(tota_venta, 2),
                                        Double.parseDouble(txtEfectivo.getText()),
                                        vuelto,
                                        result[1],
                                        result[2],
                                        //                                        cbCliente.getSelectionModel().getSelectedItem().getNumeroDocumento(),
                                        //                                        cbCliente.getSelectionModel().getSelectedItem().getInformacion());
                                        "",
                                        "");
                                ventaEstructuraController.resetVenta();
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

    private void TotalAPagar() {

        if (txtEfectivo.getText().isEmpty() && txtTarjeta.getText().isEmpty()) {
            lblVuelto.setText(moneda_simbolo + " 0.00");
            lblVueltoNombre.setText("Por pagar: ");
            estado = false;
        } else if (txtEfectivo.getText().isEmpty()) {
            if (Double.parseDouble(txtTarjeta.getText()) >= tota_venta) {
                vuelto = Double.parseDouble(txtTarjeta.getText()) - tota_venta;
                lblVueltoNombre.setText("Su cambio es: ");
                estado = true;
            } else {
                vuelto = tota_venta - Double.parseDouble(txtTarjeta.getText());
                lblVueltoNombre.setText("Por pagar: ");
                estado = false;
            }

        } else if (txtTarjeta.getText().isEmpty()) {
            if (Double.parseDouble(txtEfectivo.getText()) >= tota_venta) {
                vuelto = Double.parseDouble(txtEfectivo.getText()) - tota_venta;
                lblVueltoNombre.setText("Su cambio es: ");
                estado = true;
            } else {
                vuelto = tota_venta - Double.parseDouble(txtEfectivo.getText());
                lblVueltoNombre.setText("Por pagar: ");
                estado = false;
            }
        } else {
            double suma = (Double.parseDouble(txtEfectivo.getText())) + (Double.parseDouble(txtTarjeta.getText()));
            if (suma >= tota_venta) {
                vuelto = suma - tota_venta;
                lblVueltoNombre.setText("Su cambio es: ");
                estado = true;
            } else {
                vuelto = tota_venta - suma;
                lblVueltoNombre.setText("Por pagar: ");
                estado = false;
            }
        }

        lblVuelto.setText(moneda_simbolo + " " + Tools.roundingValue(vuelto, 2));

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

    private void onKeyPressedSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    public void setInitVentaEstructuraController(FxVentaEstructuraController ventaEstructuraController) {
        this.ventaEstructuraController = ventaEstructuraController;
    }

}
