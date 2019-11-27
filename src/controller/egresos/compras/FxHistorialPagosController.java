package controller.egresos.compras;

import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import model.CuentasHistorialProveedorTB;
import model.CuentasProveedorADO;
import model.CuentasProveedorTB;

public class FxHistorialPagosController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private Label lblFechaInicial;
    @FXML
    private Label lblFechaFinal;
    @FXML
    private Label lblCuotaActual;
    @FXML
    private Label lblPlazo;
    @FXML
    private Label lblPagado;
    @FXML
    private Label lblPendiente;
    @FXML
    private Label lblTotal;
    @FXML
    private TableView<CuentasHistorialProveedorTB> tvList;
    @FXML
    private TableColumn<CuentasHistorialProveedorTB, String> tcNumeroRegistro;
    @FXML
    private TableColumn<CuentasHistorialProveedorTB, String> tcFecha;
    @FXML
    private TableColumn<CuentasHistorialProveedorTB, String> tcCuota;
    @FXML
    private TableColumn<CuentasHistorialProveedorTB, String> tcObservacion;
    @FXML
    private TableColumn<CuentasHistorialProveedorTB, String> tcMonto;
    @FXML
    private Button btnAmortizar;
    @FXML
    private Button btnCancelar;

    private FxComprasDetalleController comprasDetalleController;

    private String idCompra;

    private int idCuentasProveedor;

    private double pagado;

    private double total;

    private String simboloMoneda;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
        tcNumeroRegistro.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcFecha.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getFecha() + "\n" + cellData.getValue().getHora()));
        tcCuota.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getCuota()));
        tcObservacion.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getObservacion()));
        tcMonto.setCellValueFactory(cellData -> Bindings.concat(simboloMoneda + Tools.roundingValue(cellData.getValue().getMonto(), 2)));
    }

    public void initListHistorialPagos() {
        ExecutorService executor = Executors.newCachedThreadPool((runnable) -> { 
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<List<Object>> task = new Task<List<Object>>() {
            @Override
            protected List<Object> call() {
                List<Object> objects = CuentasProveedorADO.Lista_Completa_Historial_Pago_Por_IdCompra(idCompra);

                return objects;
            }
        };

        task.setOnScheduled(e -> {
            btnCancelar.setDisable(true);
            btnAmortizar.setDisable(true);
        });
        task.setOnRunning(e -> {

        });
        task.setOnFailed(e -> {

        });
        task.setOnSucceeded(e -> {
            pagado = 0;
            List<Object> objects = task.getValue();
            if (!objects.isEmpty()) {
                CuentasProveedorTB cuentasProveedorTB = (CuentasProveedorTB) objects.get(0);
                if (cuentasProveedorTB != null) {
                    idCuentasProveedor = cuentasProveedorTB.getIdCuentasProveedor();
                    lblFechaInicial.setText(cuentasProveedorTB.getFechaRegistro());
                    lblFechaFinal.setText(cuentasProveedorTB.getFechaPago());

                    lblCuotaActual.setText("0");
                    lblPlazo.setText(cuentasProveedorTB.getPlazosName());

                }
                ObservableList<CuentasHistorialProveedorTB> empList = (ObservableList<CuentasHistorialProveedorTB>) objects.get(1);
                if (!empList.isEmpty()) {
                    tvList.setItems(empList);
                    tvList.getItems().forEach(tv -> pagado += tv.getMonto());
                }

                lblPagado.setText(simboloMoneda + " " + Tools.roundingValue(pagado, 2));
                lblPendiente.setText(simboloMoneda + " " + Tools.roundingValue(total - pagado, 2));
                lblTotal.setText(simboloMoneda + " " + Tools.roundingValue(total, 2));

                btnCancelar.setDisable(false);
                btnAmortizar.setDisable(false);
            }

        });

        executor.execute(task);
        if (!executor.isShutdown()) {
            executor.shutdown();
        }

        this.btnAmortizar.requestFocus();
    }

    private void openWindowAmortizarPagos() throws IOException {
        URL url = getClass().getResource(FilesRouters.FX_AMARTIZAR_PAGOS);
        FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
        Parent parent = fXMLLoader.load(url.openStream());

        FxAmortizarPagosController controller = fXMLLoader.getController();
        controller.setInitAmortizarPagosController(this);

        Stage stage = WindowStage.StageLoaderModal(parent, "Amortizar deuda", window.getScene().getWindow());
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setOnHiding((w) -> {

        });
        stage.show();

        controller.setInitValues(simboloMoneda, pagado, total, idCuentasProveedor, idCompra);
    }

    private void eliminarPagoCuota() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            short value = Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Historial de pagos", "Está seguro de cancelar el registro de pago", true);
            if (value == 1) {
                String result = CuentasProveedorADO.EliminarPagoCuota(
                        tvList.getSelectionModel().getSelectedItem().getIdCuentasHistorialProveedor(),
                        idCompra,
                        "se canceló el pago realizado de".toUpperCase() +" "+ tvList.getSelectionModel().getSelectedItem().getMonto());
                if (result.equalsIgnoreCase("removed")) {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Historial de pagos", "Se canceló la cuota correctamente.", false);
                    initListHistorialPagos(); 
                } else {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Historial de pagos", result, false);
                }
            }
        } else {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Historial de pagos", "Seleccione una cuota para cancelarlo.", false);
            tvList.requestFocus();
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        eliminarPagoCuota();

    }

    @FXML
    private void onKeyPressedCancelar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            eliminarPagoCuota();
        }

    }

    @FXML
    private void onActionAmortizar(ActionEvent event) throws IOException {
        openWindowAmortizarPagos();
    }

    @FXML
    private void onKeyPressedAmortizar(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowAmortizarPagos();
        }
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

    public void setInitHistorialPagosController(FxComprasDetalleController comprasDetalleController, String idCompra, double total, String simboloMoneda) {
        this.comprasDetalleController = comprasDetalleController;
        this.idCompra = idCompra;
        this.total = total;
        this.simboloMoneda = simboloMoneda;
    }

}
