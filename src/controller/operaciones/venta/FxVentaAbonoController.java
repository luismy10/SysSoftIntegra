package controller.operaciones.venta;

import controller.tools.FilesRouters;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.CajaADO;
import model.CajaTB;
import model.CuentasClienteADO;
import model.CuentasClienteTB;
import model.CuentasHistorialClienteADO;
import model.CuentasHistorialClienteTB;
import model.MovimientoCajaTB;

public class FxVentaAbonoController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblPagado;
    @FXML
    private Label lblPendiente;
    @FXML
    private Label lblMontoCobrar;
    @FXML
    private Label lblPlazos;
    @FXML
    private Label lblFechaVencimiento;
    @FXML
    private TableView<CuentasHistorialClienteTB> tvList;
    @FXML
    private TableColumn<CuentasHistorialClienteTB, String> tcFecha;
    @FXML
    private TableColumn<CuentasHistorialClienteTB, String> tcMonto;
    @FXML
    private TableColumn<CuentasHistorialClienteTB, String> tcReferencia;
    @FXML
    private Button btnCancelar;

    private FxVentaDetalleController ventaDetalleController;

    private String idVenta;

    private String simboloMoneda;

    private int idCuentasCliente;

    private double cobrar;

    private double pagado;

    private double pagadoTotal;

    private double pendiente;

    private double total;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
        simboloMoneda = "M";
        tcFecha.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getFechaAbono().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))));
        tcMonto.setCellValueFactory(cellData -> Bindings.concat(simboloMoneda + " " + Tools.roundingValue(cellData.getValue().getAbono(), 2)));
        tcReferencia.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getReferencia()));
    }

    public void loadInitData(String idVenta, String simboloMoneda) {
        this.idVenta = idVenta;
        this.simboloMoneda = simboloMoneda;
        CuentasClienteTB cuentasClienteTB = CuentasClienteADO.Get_CuentasCliente_ById(idVenta);
        if (cuentasClienteTB != null) {
            cobrar = cuentasClienteTB.getMontoInicial();
            idCuentasCliente = cuentasClienteTB.getIdCuentasCliente();
            lblPlazos.setText(cuentasClienteTB.getPlazosName());
            lblFechaVencimiento.setText(cuentasClienteTB.getFechaVencimiento().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
            lblMontoCobrar.setText(simboloMoneda + " " + Tools.roundingValue(cobrar, 2));
            loadTableAbono();
            tvList.getItems().forEach(e -> pagado += e.getAbono());
            lblPagado.setText(simboloMoneda + " " + Tools.roundingValue(pagado, 2));
            pendiente = cobrar - pagado;
            lblPendiente.setText(simboloMoneda + " " + Tools.roundingValue(pendiente, 2));
            total = pagado + pendiente;
            lblTotal.setText(simboloMoneda + " " + Tools.roundingValue(total, 2));
            pagadoTotal = pagado;
            pagado = 0;
        }
    }

    public void loadTableAbono() {
        tvList.setItems(CuentasHistorialClienteADO.ListarAbonos(idCuentasCliente));
    }

    private void openWindowAbono() {
        try {
            URL url = getClass().getResource(FilesRouters.FX_VENTA_ABONO_PROCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //
            FxVentaAbonoProcesoController controller = fXMLLoader.getController();
            controller.setInitLoadVentaAbono(idVenta, idCuentasCliente, total, pagadoTotal);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Abono", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding((w) -> {
                loadInitData(idVenta, simboloMoneda);
                ventaDetalleController.setInitComponents(idVenta);
            });
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void deleteAbono() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
//            short option = Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Abono", "¿Está segudo de cancelar el abono?", true);
//            if (option == 1) {
//
//                CajaTB cajaTB = CajaADO.ValidarCreacionCaja(Session.USER_ID);
//                if (cajaTB.getId() == 1) {
//                } else if (cajaTB.getId() == 2) {//listo
//                    Session.CAJA_ID = cajaTB.getIdCaja();
//                } else if (cajaTB.getId() == 3) {
//                    Session.CAJA_ID = cajaTB.getIdCaja();
//                }
//
//                if (Session.CAJA_ID == null) {
//                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Detalle de venta", "No existe ninguna caja aperturada, para realizar la devolución.", false);
//                } else {
//                    MovimientoCajaTB movimientoCaja = new MovimientoCajaTB();
//                    movimientoCaja.setIdCaja(Session.CAJA_ID);
//                    movimientoCaja.setIdUsuario(Session.USER_ID);
//                    movimientoCaja.setFechaMovimiento(Tools.getDate());
//                    movimientoCaja.setComentario("Eliminado el abono");
//                    movimientoCaja.setMovimiento("ABON");
//                    movimientoCaja.setEntrada(0);
//                    movimientoCaja.setSalidas(tvList.getSelectionModel().getSelectedItem().getAbono());
//                    movimientoCaja.setSaldo(tvList.getSelectionModel().getSelectedItem().getAbono());
//
//                    String result = CuentasHistorialClienteADO.Delete_CuentasHistorialCliente(idVenta,tvList.getSelectionModel().getSelectedItem().getIdCuentasHistorialCliente(),
//                            "se canceló el abono realizado de".toUpperCase() + " " + tvList.getSelectionModel().getSelectedItem().getAbono(),
//                            movimientoCaja);
//                    if (result.equalsIgnoreCase("deleted")) {
//                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Abonar", "Se elimino el abono correctamente.", false);
//                        loadTableAbono();
//                    } else {
//                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Abonar", result, false);
//                    }
//                }
//            }
        } else {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Abonar", "Seleccione una abono para cancelarlo.", false);
            tvList.requestFocus();
        }
    }

    @FXML
    private void onKeyPressedAbono(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowAbono();
        }
    }

    @FXML
    private void onActionAbono(ActionEvent event) {
        openWindowAbono();
    }

    @FXML
    private void onKeyPressedCancelar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            deleteAbono();
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        deleteAbono();
    }

    @FXML
    private void onKeyPressedCerrar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(window);
        }
    }

    @FXML
    private void onActionCerrar(ActionEvent event) {
        Tools.Dispose(window);
    }

    public void setInitVentaAbonoController(FxVentaDetalleController ventaDetalleController) {
        this.ventaDetalleController = ventaDetalleController;
    }

}
