package controller.egresos.compras;

import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.CajaADO;
import model.CajaTB;
import model.CompraADO;
import model.CompraTB;
import model.CuentasProveedorTB;
import model.EmpleadoADO;
import model.EmpleadoTB;
import model.LoteTB;
import model.PlazosADO;
import model.PlazosTB;
import model.SuministroTB;

public class FxComprasProcesoController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private Text lblTotal;
    @FXML
    private TextField txtEfectivo;
    @FXML
    private HBox hbPagoContado;
    @FXML
    private VBox vbPagoCredito;
    @FXML
    private HBox hbOtroMedioPago;
    @FXML
    private RadioButton rbContado;
    @FXML
    private RadioButton rbCredito;
    @FXML
    private RadioButton rbOtroMedioPago;
    @FXML
    private TextField txtProveedor;
    @FXML
    private ComboBox<PlazosTB> cbPlazos;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private TextField txtMonto;
    @FXML
    private TextField txtObservacion;
    @FXML
    private ComboBox<EmpleadoTB> cbCaja;
    @FXML
    private TextField txtObservacionCredito;
    
    private FxComprasController comprasController;

    private FxComprasEditarController comprasEditarController;

    private CompraTB compraTB = null;

    private TableView<SuministroTB> tvList;

    private ObservableList<LoteTB> loteTBs;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
        ToggleGroup group = new ToggleGroup();
        rbContado.setToggleGroup(group);
        rbCredito.setToggleGroup(group);
        rbOtroMedioPago.setToggleGroup(group);
        rbOtroMedioPago.setSelected(true);
        setInitializePlazos();
        setInitializeCaja();
    }

    private void setInitializeCaja() {
        cbCaja.getItems().clear();
        EmpleadoADO.Lista_Empleado_For_Compras().forEach(e -> {
            cbCaja.getItems().add(new EmpleadoTB(e.getIdEmpleado(), e.getApellidos(), e.getNombres()));
        });
    }

    public void setInitializePlazos() {
        cbPlazos.getItems().clear();
        PlazosADO.GetTipoPlazoCombBox().forEach(e -> {
            cbPlazos.getItems().add(new PlazosTB(e.getIdPlazos(), e.getNombre(), e.getDias(), e.getEstado(), e.getPredeterminado()));
        });
    }

    public void setLoadProcess(CompraTB compraTB, TableView<SuministroTB> tvList, ObservableList<LoteTB> loteTBs, String proveedor, String simboloMoneda) {
        this.compraTB = compraTB;
        this.tvList = tvList;
        this.loteTBs = loteTBs;
        lblTotal.setText(simboloMoneda + " " + Tools.roundingValue(compraTB.getTotal().get(), 2));
        txtProveedor.setText(proveedor);
        txtEfectivo.setText(Tools.roundingValue(compraTB.getTotal().get(), 2));
        txtMonto.setText(Tools.roundingValue(compraTB.getTotal().get(), 2));
    }

    private void executeCrud(int tipoCompra, int estadoCompra, String idCaja) {
        short option = Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Compra", "¿Está seguro de continuar?", true);
        if (option == 1) {
            compraTB.setTipo(tipoCompra);
            compraTB.setEstado(estadoCompra);

            if (rbContado.isSelected()) {
                String result = CompraADO.CrudCompra((short) 1, idCaja, compraTB, tvList, loteTBs, null);
                if (result.equalsIgnoreCase("register")) {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Compra", "Se registró correctamente la compra.", false);
                    Tools.Dispose(window);
                    if (comprasController != null) {
                        comprasController.clearComponents();
                    } else if (comprasEditarController != null) {
                        comprasEditarController.disposeWindow();
                    }
                } else {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Compra", result, false);
                }
            } else if (rbCredito.isSelected()) {
                compraTB.setObservaciones(txtObservacionCredito.getText().trim());
                
                CuentasProveedorTB cuentasProveedorTB = new CuentasProveedorTB();
                cuentasProveedorTB.setMontoTotal(Double.parseDouble(txtMonto.getText()));
                cuentasProveedorTB.setPlazos(cbPlazos.getSelectionModel().getSelectedItem().getIdPlazos());
                cuentasProveedorTB.setFechaPago(Tools.getDatePicker(dpFecha));
                cuentasProveedorTB.setFechaRegistro(Tools.getDate());

                String result = CompraADO.CrudCompra((short) 2, idCaja, compraTB, tvList, loteTBs, cuentasProveedorTB);
                if (result.equalsIgnoreCase("register")) {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Compra", "Se registró correctamente la compra.", false);
                    Tools.Dispose(window);
                    if (comprasController != null) {
                        comprasController.clearComponents();
                    } else if (comprasEditarController != null) {
                        comprasEditarController.disposeWindow();
                    }
                } else {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Compra", result, false);
                }
            } else if (rbOtroMedioPago.isSelected()) {
                compraTB.setObservaciones(txtObservacion.getText().trim());
                String result = CompraADO.CrudCompra((short) 3, idCaja, compraTB, tvList, loteTBs, null);
                if (result.equalsIgnoreCase("register")) {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Compra", "Se registró correctamente la compra.", false);
                    Tools.Dispose(window);
                    if (comprasController != null) {
                        comprasController.clearComponents();
                    } else if (comprasEditarController != null) {
                        comprasEditarController.disposeWindow();
                    }
                } else {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Compra", result, false);
                }
            }
        }
    }

    private void onEventProcess() {

        if (rbContado.isSelected()) {
            if (!Tools.isNumeric(txtEfectivo.getText())) {
                Tools.AlertMessageWarning(window, "Compra", "Complete el campo efectivo.");
                txtEfectivo.requestFocus();
            } else if (cbCaja.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(window, "Compra", "Seleccione la caja de salida de dinero.");
                cbCaja.requestFocus();
            } else {
                CajaTB cajaTB = CajaADO.ValidarAperturaCajaParaTrabajadores(cbCaja.getSelectionModel().getSelectedItem().getIdEmpleado());
                if (cajaTB != null) {
                    executeCrud(1, 1, cajaTB.getIdCaja());
                } else {
                    Tools.AlertMessageWarning(window, "Compra", "La caja seleccionada no está aperturada.");
                    cbCaja.requestFocus();
                }
            }
        } else if (rbCredito.isSelected()) {
            if (!Tools.isNumeric(txtMonto.getText())) {
                Tools.AlertMessageWarning(window, "Compra", "Complete el campo monto inicial.");
                txtMonto.requestFocus();
            } else if (cbPlazos.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(window, "Compra", "Seleccione los días de crédito.");
                cbPlazos.requestFocus();
            } else {
                if (Double.parseDouble(txtMonto.getText()) > 0) {
                    executeCrud(2, 2, "");
                } else {
                    executeCrud(2, 2, "");
                }
            }
        } else if (rbOtroMedioPago.isSelected()) {
            if (!Tools.isText(txtObservacion.getText().trim())) {
                executeCrud(1, 1, "");
            } else {
                Tools.AlertMessageWarning(window, "Compra", "Ingrese una observación de la compra.");
                txtObservacion.requestFocus();
            }
        }
    }

    public void openWindowAddPlazo() throws IOException {
        URL url = getClass().getResource(FilesRouters.FX_PLAZOS);
        FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
        Parent parent = fXMLLoader.load(url.openStream());
        //Controlller here
        FxPlazosController controller = fXMLLoader.getController();
        controller.setInitCompraVentaProcesoController(this, null);
        //
        Stage stage = WindowStage.StageLoaderModal(parent, "Agegar nuevo plazo", window.getScene().getWindow());
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
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
            Tools.Dispose(window);
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        Tools.Dispose(window);
    }

    @FXML
    private void onActionRbContado(ActionEvent event) {
        vbPagoCredito.setDisable(true);
        hbOtroMedioPago.setDisable(true);
        hbPagoContado.setDisable(false);
    }

    @FXML
    private void onActionRbCredito(ActionEvent event) {
        hbPagoContado.setDisable(true);
        hbOtroMedioPago.setDisable(true);
        vbPagoCredito.setDisable(false);
        txtMonto.requestFocus();
    }

    @FXML
    private void onActionRbOtro(ActionEvent event) {
        vbPagoCredito.setDisable(true);
        hbPagoContado.setDisable(true);
        hbOtroMedioPago.setDisable(false);
        txtObservacion.requestFocus();
    }

    @FXML
    private void OnMouseClickedPlazos(MouseEvent event) throws IOException {
        openWindowAddPlazo();
    }

    @FXML
    private void OnActionPlazos(ActionEvent event) {
        if (cbPlazos.getSelectionModel().getSelectedIndex() >= 0) {
            int dias = cbPlazos.getSelectionModel().getSelectedItem().getDias();
            LocalDate localDate = LocalDate.now();
            dpFecha.setValue(localDate.plusDays(dias));
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

    public void setInitComprasController(FxComprasController comprasController) {
        this.comprasController = comprasController;
    }

    void setInitComprasEditarController(FxComprasEditarController comprasEditarController) {
        this.comprasEditarController = comprasEditarController;
    }

}
