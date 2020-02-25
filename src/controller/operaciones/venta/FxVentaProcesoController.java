package controller.operaciones.venta;

import controller.contactos.clientes.FxClienteListaController;
import controller.operaciones.compras.FxPlazosController;
import controller.tools.ConvertMonedaCadena;
import controller.tools.FilesRouters;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;//call me
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.ClienteADO;
import model.ClienteTB;
import model.CuentasClienteTB;
import model.MonedaTB;
import model.PlazosADO;
import model.PlazosTB;
import model.SuministroTB;
import model.VentaADO;
import model.VentaTB;

public class FxVentaProcesoController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private Label lblTotal;
    @FXML
    private TextField txtEfectivo;
    @FXML
    private Label lblVuelto;
    @FXML
    private Text lblComprobante;
    @FXML
    private TextField txtObservacion;
    @FXML
    private VBox vbEfectivo;
    @FXML
    private VBox vbCredito;
    private VBox vbTarjeta;
    @FXML
    private VBox vbViewEfectivo;
    @FXML
    private VBox vbViewCredito;
    @FXML
    private Label lblEfectivo;
    @FXML
    private Label lblCredito;
    private Label lblTarjeta;
    @FXML
    private ComboBox<PlazosTB> cbPlazos;
    @FXML
    private DatePicker dtVencimiento;
    // Cliente
    @FXML
    private VBox vbCliente;
    @FXML
    private TextField txtNumeroDocumento;
    @FXML
    private TextField txtDatos;
    @FXML
    private TextField txtDireccion;
    @FXML
    private Label lblMonedaLetras;

    private FxVentaEstructuraController ventaEstructuraController;

    private TableView<SuministroTB> tvList;

    private ConvertMonedaCadena monedaCadena;

    private VentaTB ventaTB;

    private String moneda_simbolo;

    private ComboBox<MonedaTB> moneda;

    private double vuelto;

    private double vueltoo;
//    private double vueltoefectivo;
//    private double vueltotarjeta;

    private double tota_venta;

    private boolean state_view_pago;

    private String documento;

    private double subTotal, descuento, importeTotal;

    private String idCliente;
    @FXML
    private VBox vbViewCredito1;
    @FXML
    private TextField txtTarjeta;
    @FXML
    private Label lblVueltoNombre;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_PRESSED);
        state_view_pago = false;
        tota_venta = 0;
        monedaCadena = new ConvertMonedaCadena();
        setInitializePlazosVentas();
        lblVueltoNombre.setText("Su cambio: ");
    }

    public void setInitializePlazosVentas() {
        cbPlazos.getItems().clear();
        PlazosADO.GetTipoPlazoCombBox().forEach(e -> {
            this.cbPlazos.getItems().add(new PlazosTB(e.getIdPlazos(), e.getNombre(), e.getDias(), e.getEstado(), e.getPredeterminado()));
        });
        cbPlazos.getSelectionModel().select(0);
        if (cbPlazos.getSelectionModel().getSelectedIndex() >= 0) {
            dtVencimiento.setValue(LocalDate.now().plusDays(cbPlazos.getSelectionModel().getSelectedItem().getDias()));
        }
    }

    public void setInitComponents(VentaTB ventaTB, String documento, TableView<SuministroTB> tvList, String subTotal, String descuento, String importeTotal, String total) {
        this.ventaTB = ventaTB;
        this.tvList = tvList;
        this.documento = documento;
        moneda_simbolo = ventaTB.getMonedaName();
        Session.TICKET_SIMBOLOMONEDA = moneda_simbolo;
        lblComprobante.setText(ventaTB.getComprobanteName());
        lblTotal.setText(moneda_simbolo + " " + total);
        lblVuelto.setText(moneda_simbolo + " " + Tools.roundingValue(0, 2));
        tota_venta = Double.parseDouble(total);//es double
        this.subTotal = Double.parseDouble(subTotal);
        this.descuento = Double.parseDouble(descuento);
        this.importeTotal = Double.parseDouble(importeTotal);
        lblMonedaLetras.setText(monedaCadena.Convertir(total, true, ventaEstructuraController.getMonedaNombre()));
        setClienteProcesoVenta(Session.IDCLIENTE, Session.DATOSCLIENTE, Session.N_DOCUMENTO_CLIENTE, Session.DIRECCION_CLIENTE);
        txtEfectivo.requestFocus();
    }

    private void openWindowAddPlazoVenta() throws IOException {
        URL url = getClass().getResource(FilesRouters.FX_PLAZOS);
        FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
        Parent parent = fXMLLoader.load(url.openStream());
        //Controlller here
        FxPlazosController controller = fXMLLoader.getController();
        controller.setInitCompraVentaProcesoController(null, this);
        //
        Stage stage = WindowStage.StageLoaderModal(parent, "Agegar nuevo plazo", window.getScene().getWindow());
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    public void setClienteProcesoVenta(String id, String datos, String numeroDocumento, String direccion) {
        idCliente = !id.equalsIgnoreCase("") ? id : Session.IDCLIENTE;
        txtNumeroDocumento.setText(numeroDocumento.equalsIgnoreCase("")
                ? Session.N_DOCUMENTO_CLIENTE
                : numeroDocumento);

        txtDatos.setText(datos.equalsIgnoreCase("")
                ? Session.DATOSCLIENTE
                : datos);

        txtDireccion.setText(direccion.equalsIgnoreCase("")
                ? Session.DIRECCION_CLIENTE
                : direccion);

        ventaTB.setCliente(idCliente);
    }

    private void openWindowCliente() {
        try {
            URL url = getClass().getResource(FilesRouters.FX_CLIENTE_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxClienteListaController controller = fXMLLoader.getController();

            controller.setInitVentaProcesoController(this);
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Cliente", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.show();
//            stage.setOnHiding(w -> {
//                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
//            });
            controller.fillCustomersTable("");
        } catch (IOException ex) {
            System.out.println("Error en suministro lista:" + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        if (state_view_pago) {
            if (txtNumeroDocumento.getText().trim().isEmpty()) {
                Tools.AlertMessageWarning(window, "Venta", "Ingrese el número de documento del cliente.");
                txtNumeroDocumento.requestFocus();
            } else if (txtDatos.getText().trim().isEmpty()) {
                Tools.AlertMessageWarning(window, "Venta", "Ingrese los datos del cliente.");
                txtDatos.requestFocus();
            } else if (cbPlazos.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(window, "Venta", "Seleccionar el plazo.");
                cbPlazos.requestFocus();
            } else if (dtVencimiento.getValue() == null) {
                Tools.AlertMessageWarning(window, "Venta", "El formato de la fecha no es correcto.");
                dtVencimiento.requestFocus();
            } else {
                ventaTB.setObservaciones(txtObservacion.getText().trim());
                ventaTB.setTipo(2);
                ventaTB.setEstado(2);
                ventaTB.setEfectivo(0);
                ventaTB.setVuelto(0);
                ventaTB.setCliente(idCliente);

                CuentasClienteTB cuentasCliente = new CuentasClienteTB();
                cuentasCliente.setPlazos(cbPlazos.getSelectionModel().getSelectedItem().getIdPlazos());
                cuentasCliente.setFechaVencimiento(LocalDateTime.of(dtVencimiento.getValue(), LocalTime.now()));
                short confirmation = Tools.AlertMessageConfirmation(window, "Venta", "¿Esta seguro de continuar?");
                if (confirmation == 1) {

                    String[] result = VentaADO.CrudVenta(ventaTB, tvList, ventaEstructuraController.getIdTipoComprobante(), cuentasCliente).split("/");
                    switch (result[0]) {
                        case "register":
                            Tools.AlertMessageInformation(window, "Venta", "Se guardo correctamente la venta al crédito.");
                            ventaEstructuraController.resetVenta();
                            Tools.Dispose(window);
                            break;
                        default:
                            Tools.AlertMessageError(window, "Venta", result[0]);
                            break;
                    }
                }
            }

        } else {
            if (txtNumeroDocumento.getText().trim().isEmpty()) {
                Tools.AlertMessageWarning(window, "Venta", "Ingrese el número de documento del cliente.");
                txtNumeroDocumento.requestFocus();
            } else if (txtDatos.getText().trim().isEmpty()) {
                Tools.AlertMessageWarning(window, "Venta", "Ingrese los datos del cleinte.");
                txtDatos.requestFocus();
            } else if (Tools.isNumeric(txtEfectivo.getText().trim())) {
                ventaTB.setObservaciones(txtObservacion.getText().trim());
                ventaTB.setTipo(1);
                ventaTB.setEstado(1);
                ventaTB.setEfectivo(Double.parseDouble(txtEfectivo.getText()));
                ventaTB.setVuelto(vuelto);

                ventaTB.setCliente(idCliente);

                if (vuelto < 0) {
                    Tools.AlertMessageWarning(window, "Venta", "Su cambio no puede ser negativo.");
                } else {
                    short confirmation = Tools.AlertMessageInformation(window, "Venta", "¿Esta seguro de continuar?");
                    if (confirmation == 1) {
                        String[] result = VentaADO.CrudVenta(ventaTB, tvList, ventaEstructuraController.getIdTipoComprobante(), new CuentasClienteTB()).split("/");
                        switch (result[0]) {
                            case "register":
                                short value = Tools.AlertMessageConfirmation(window, "Venta", "Se realiazo la venta con éxito, ¿Desea imprimir el comprobante?");
                                if (value == 1) {
                                    ventaEstructuraController.imprimirVenta(documento, tvList, Tools.roundingValue(subTotal, 2), Tools.roundingValue(descuento, 2), Tools.roundingValue(importeTotal, 2), Tools.roundingValue(tota_venta, 2), Double.parseDouble(txtEfectivo.getText()), vuelto, result[1], result[2], txtNumeroDocumento.getText().trim(), txtDatos.getText().trim());
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
    }

    private void TotalAPagar() {
        if(txtEfectivo.getText().isEmpty() && txtTarjeta.getText().isEmpty()){
            lblVuelto.setText(moneda_simbolo + " 0.00");
             NombreVuelto();
        }else if(txtEfectivo.getText().isEmpty()){
            if(Double.parseDouble(txtTarjeta.getText()) <= tota_venta ){
                vueltoo = tota_venta - Double.parseDouble(txtTarjeta.getText());
            }else{
                vueltoo = Double.parseDouble(txtTarjeta.getText()) - tota_venta;
            }
             NombreVuelto();
        }else if (txtTarjeta.getText().isEmpty()){
            if(Double.parseDouble(txtEfectivo.getText()) <= tota_venta ){
                vueltoo = tota_venta - Double.parseDouble(txtEfectivo.getText());
            }else{
                vueltoo = Double.parseDouble(txtEfectivo.getText()) - tota_venta;
            }
             NombreVuelto();
        }
        else{
            double suma = 0;
            suma = (Double.parseDouble(txtEfectivo.getText())) + (Double.parseDouble(txtTarjeta.getText()));
            if(suma >= tota_venta){
                vueltoo = suma - tota_venta;
            }
            else{
                vueltoo = tota_venta - suma;
            }
             NombreVuelto();
        }
        
        lblVuelto.setText(moneda_simbolo + " " + Tools.roundingValue(vueltoo, 2));
      
    }
    
    private void NombreVuelto(){
        
            lblVueltoNombre.setText( (tota_venta >= vueltoo ? "Por pagar: ":"Su cambio es: ") );
             
    }

    @FXML
    private void onKeyReleasedEfectivo(KeyEvent event) {
        if(txtEfectivo.getText().isEmpty()){
                vueltoo = tota_venta;
            }
        TotalAPagar();
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
        if(txtTarjeta.getText().isEmpty()){
                vueltoo = tota_venta;
            }
        TotalAPagar();
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

    private void onMouseClickedTarjeta(MouseEvent event) {
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
    private void onActionPlazos(ActionEvent event) {
        if (cbPlazos.getSelectionModel().getSelectedIndex() >= 0) {
            dtVencimiento.setValue(LocalDate.now().plusDays(cbPlazos.getSelectionModel().getSelectedItem().getDias()));
        }
    }

    @FXML
    private void OnMouseClickedPlazos(MouseEvent event) throws IOException {
        openWindowAddPlazoVenta();
    }

    @FXML
    private void onKeyPressedCliente(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowCliente();
        }
    }

    @FXML
    private void onActionCliente(ActionEvent event) {
        openWindowCliente();
    }

    @FXML
    private void onActionNumeroDocumentoSearch(ActionEvent event) {
        ClienteTB clienteTB = ClienteADO.GetByIdClienteVenta(txtNumeroDocumento.getText().trim());
        if (clienteTB != null) {
            idCliente = clienteTB.getIdCliente();
            txtDatos.setText(clienteTB.getInformacion());
            txtDireccion.setText(clienteTB.getDireccion());
        } else {
            idCliente = Session.IDCLIENTE;
            txtNumeroDocumento.setText(Session.N_DOCUMENTO_CLIENTE);
            txtDatos.setText(Session.DATOSCLIENTE);
            txtDireccion.setText(Session.DIRECCION_CLIENTE);
        }
    }

    @FXML
    private void onKeyTypedDocumento(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b')) {
            event.consume();
        }
    }

    public void setInitVentaEstructuraController(FxVentaEstructuraController ventaEstructuraController) {
        this.ventaEstructuraController = ventaEstructuraController;
    }

}
