package controller.operaciones.cotizacion;

import controller.operaciones.venta.FxVentaEstructuraController;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.CotizacionADO;
import model.CotizacionTB;

public class FxCotizacionListaController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private TextField txtBuscar;
    @FXML
    private DatePicker txtFechaInicio;
    @FXML
    private DatePicker txtFechaFinal;
    @FXML
    private TableView<CotizacionTB> tvLista;
    @FXML
    private TableColumn<CotizacionTB, String> tcNumero;
    @FXML
    private TableColumn<CotizacionTB, String> tcVendedor;
    @FXML
    private TableColumn<CotizacionTB, String> tcCotizacion;
    @FXML
    private TableColumn<CotizacionTB, String> tcFecha;
    @FXML
    private TableColumn<CotizacionTB, String> tcCliente;
    @FXML
    private TableColumn<CotizacionTB, String> tcTotal;

    private FxVentaEstructuraController ventaEstructuraController;

    private FxCotizacionController cotizacionController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
        tcNumero.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcVendedor.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getEmpleadoTB().getApellidos() + "\n" + cellData.getValue().getEmpleadoTB().getNombres()));
        tcCotizacion.setCellValueFactory(cellData -> Bindings.concat("COTIZACIÓN N° " + cellData.getValue().getIdCotizacion()));
        tcFecha.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getFechaCotizacion() + "\n" + cellData.getValue().getHoraCotizacion()));
        tcCliente.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getClienteTB().getInformacion()));
        tcTotal.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMonedaTB().getSimbolo() + " " + Tools.roundingValue(cellData.getValue().getTotal(), 2)));
        Tools.actualDate(Tools.getDate(), txtFechaInicio);
        Tools.actualDate(Tools.getDate(), txtFechaFinal);
    }

    public void loadTable(short opcion, String buscar, String fechaInicio, String fechaFinal) {
        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<ObservableList<CotizacionTB>> task = new Task<ObservableList<CotizacionTB>>() {
            @Override
            public ObservableList<CotizacionTB> call() {
                return CotizacionADO.CargarCotizacion(opcion, buscar, fechaInicio, fechaFinal);
            }
        };
        task.setOnSucceeded(w -> {
            ObservableList<CotizacionTB> cotizacionTBs = task.getValue();
            if (!cotizacionTBs.isEmpty()) {
                tvLista.setItems(cotizacionTBs);
                lblLoad.setVisible(false);
            } else {
                tvLista.getItems().clear();
                lblLoad.setVisible(false);
            }
        });
        task.setOnFailed(w -> {
            lblLoad.setVisible(false);
        });
        task.setOnScheduled(w -> {
            lblLoad.setVisible(true);
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void onEventAceptar() {
        if (ventaEstructuraController != null) {
            if (tvLista.getSelectionModel().getSelectedIndex() >= 0) {
                ventaEstructuraController.resetVenta();
                Tools.Dispose(apWindow);
                ventaEstructuraController.loadCotizacion(tvLista.getSelectionModel().getSelectedItem().getIdCotizacion());
            }
        } else if (cotizacionController != null) {
            if (tvLista.getSelectionModel().getSelectedIndex() >= 0) {
                cotizacionController.resetVenta();
                Tools.Dispose(apWindow);
                cotizacionController.loadCotizacion(tvLista.getSelectionModel().getSelectedItem().getIdCotizacion());
            }
        }
    }

    @FXML
    private void onKeyPressedBuscar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!tvLista.getItems().isEmpty()) {
                tvLista.requestFocus();
                tvLista.getSelectionModel().select(0);
            }
        }
    }

    @FXML
    private void onKeyReleasedBuscar(KeyEvent event) {
        if (!lblLoad.isVisible()) {
            loadTable((short) 2, txtBuscar.getText().trim(), "", "");
        }
    }

    @FXML
    private void onActionFechaInicio(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (txtFechaInicio.getValue() != null && txtFechaFinal.getValue() != null) {
                loadTable((short) 3, "", Tools.getDatePicker(txtFechaInicio), Tools.getDatePicker(txtFechaFinal));
            }
        }
    }

    @FXML
    private void onActionFechaFinal(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (txtFechaInicio.getValue() != null && txtFechaFinal.getValue() != null) {
                loadTable((short) 3, "", Tools.getDatePicker(txtFechaInicio), Tools.getDatePicker(txtFechaFinal));
            }
        }
    }

    @FXML
    private void onkeyPressedRecargar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                txtBuscar.clear();
                Tools.actualDate(Tools.getDate(), txtFechaInicio);
                Tools.actualDate(Tools.getDate(), txtFechaFinal);
                if (txtFechaInicio.getValue() != null && txtFechaFinal.getValue() != null) {
                    loadTable((short) 1, "", Tools.getDatePicker(txtFechaInicio), Tools.getDatePicker(txtFechaFinal));
                }
            }
        }
    }

    @FXML
    private void onActionRecargar(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            txtBuscar.clear();
            Tools.actualDate(Tools.getDate(), txtFechaInicio);
            Tools.actualDate(Tools.getDate(), txtFechaFinal);
            if (txtFechaInicio.getValue() != null && txtFechaFinal.getValue() != null) {
                loadTable((short) 1, "", Tools.getDatePicker(txtFechaInicio), Tools.getDatePicker(txtFechaFinal));
            }
        }
    }

    @FXML
    private void onKeyPressedList(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!tvLista.getItems().isEmpty()) {
                onEventAceptar();
            }
        }
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) {
        if (!tvLista.getItems().isEmpty()) {
            if (event.getClickCount() == 2) {
                onEventAceptar();
            }
        }
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventAceptar();
        }
    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        onEventAceptar();
    }

    @FXML
    private void onKeyPressedCerrar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(apWindow);
        }
    }

    @FXML
    private void onActionCerrar(ActionEvent event) {
        Tools.Dispose(apWindow);
    }

    public void setInitVentaEstructuraController(FxVentaEstructuraController ventaEstructuraController) {
        this.ventaEstructuraController = ventaEstructuraController;
    }

    public void setInitCotizacionListaController(FxCotizacionController cotizacionController) {
        this.cotizacionController = cotizacionController;
    }

}
