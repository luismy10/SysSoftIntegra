package controller.operaciones.venta;

import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.MovimientoCajaTB;
import model.SuministroTB;
import model.VentaADO;

public class FxVentaDevolucionController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private Label lblComprobante;
    @FXML
    private Label lblTotal;
    @FXML
    private TextField txtEfectivo;
    @FXML
    private TextField txtObservacion;
    @FXML
    private Button btnEjecutar;

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
            short validate = Tools.AlertMessageConfirmation(window, "Detalle de ventas", "¿Está seguro de anular la venta?");
            if (validate == 1) {

                ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
                    Thread t = new Thread(runnable);
                    t.setDaemon(true);
                    return t;
                });

                Task<String> task = new Task<String>() {
                    @Override
                    public String call() {
                        MovimientoCajaTB movimientoCajaTB = new MovimientoCajaTB();
                        movimientoCajaTB.setFechaMovimiento(Tools.getDate());
                        movimientoCajaTB.setHoraMovimiento(Tools.getHour());
                        movimientoCajaTB.setComentario(txtObservacion.getText());
                        movimientoCajaTB.setTipoMovimiento((short) 5);
                        movimientoCajaTB.setMonto(totalVenta);
                        return VentaADO.CancelTheSale(idVenta, arrList, movimientoCajaTB);
                    }
                };

                task.setOnSucceeded(e -> {
                    String result = task.getValue();
                    if (result.equalsIgnoreCase("updated")) {
                        Tools.AlertMessageInformation(window, "Detalle de ventas", "Se anuló correctamente.");
                        Tools.Dispose(window);
                    } else if (result.equalsIgnoreCase("scrambled")) {
                        Tools.AlertMessageWarning(window, "Detalle de venta", "Ya está anulada la venta.");
                    } else if (result.equalsIgnoreCase("nocaja")) {
                        Tools.AlertMessageWarning(window, "Detalle de venta", "No tienes aperturado ninguna caja para completar la operación.");
                    } else {
                        Tools.AlertMessageError(window, "Detalle de ventas", result);
                    }
                    btnEjecutar.setDisable(false);
                });

                task.setOnFailed(e -> {
                    btnEjecutar.setDisable(false);
                });

                task.setOnScheduled(e -> {
                    btnEjecutar.setDisable(true);
                });

                exec.execute(task);
                if (!exec.isShutdown()) {
                    exec.shutdown();
                }

            }
        }
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
