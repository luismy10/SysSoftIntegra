package controller.operaciones.venta;

import controller.operaciones.guiaremision.FxGuiaRemisionController;
import controller.tools.Tools;
import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.VentaADO;
import model.VentaTB;

public class FxVentaListaController implements Initializable {

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
    private TableView<VentaTB> tvList;
    @FXML
    private TableColumn<VentaTB, String> tcNumero;
    @FXML
    private TableColumn<VentaTB, String> tcFecha;
    @FXML
    private TableColumn<VentaTB, String> tcComprobante;
    @FXML
    private TableColumn<VentaTB, String> tcCliente;
    @FXML
    private TableColumn<VentaTB, Label> tcEstado;
    @FXML
    private TableColumn<VentaTB, String> tcTotal;
    @FXML
    private Label lblPaginaActual;
    @FXML
    private Label lblPaginaSiguiente;

    private FxGuiaRemisionController guiaRemisionController;

    private int paginacion;

    private int totalPaginacion;

    private short opcion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);

        tcNumero.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcFecha.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getFechaVenta() + "\n" + cellData.getValue().getHoraVenta()));
        tcComprobante.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getComprobanteName() + "\n" + cellData.getValue().getSerie() + "-" + cellData.getValue().getNumeracion()));
        tcCliente.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getIdCliente()));
        tcEstado.setCellValueFactory(new PropertyValueFactory<>("estadoLabel"));
        tcTotal.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMonedaName() + " " + Tools.roundingValue(cellData.getValue().getTotal(), 2)));

        Tools.actualDate(Tools.getDate(), txtFechaInicio);
        Tools.actualDate(Tools.getDate(), txtFechaFinal);

        paginacion = 1;
        opcion = 0;
    }

    public void loadInit() {
        if (txtFechaInicio.getValue() != null && txtFechaFinal.getValue() != null) {
            paginacion = 1;
            fillVentasTable((short) 3, "", Tools.getDatePicker(txtFechaInicio), Tools.getDatePicker(txtFechaFinal), 0, 0, "");
            opcion = 0;
        }
    }

    private void fillVentasTable(short opcion, String value, String fechaInicial, String fechaFinal, int comprobante, int estado, String usuario) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                return VentaADO.ListVentas(opcion, value, fechaInicial, fechaFinal, comprobante, estado, usuario, (paginacion - 1) * 20, 20);
            }
        };
        task.setOnSucceeded(w -> {
            ArrayList<Object> objects = task.getValue();
            if (!objects.isEmpty()) {
                tvList.setItems((ObservableList<VentaTB>) objects.get(0));
                if (tvList.getItems().isEmpty()) {
                    tvList.getSelectionModel().select(0);
                }
                totalPaginacion = (int) (Math.ceil(((Integer) objects.get(1)) / 20.00));
                lblPaginaActual.setText(paginacion + "");
                lblPaginaSiguiente.setText(totalPaginacion + "");
                lblLoad.setVisible(false);
            } else {
                lblLoad.setVisible(false);
            }
        });
        task.setOnFailed(w -> lblLoad.setVisible(false));
        task.setOnScheduled(w -> lblLoad.setVisible(true));
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void onEventPaginacion() {
        switch (opcion) {
            case 0:
                fillVentasTable((short) 3, "", Tools.getDatePicker(txtFechaInicio), Tools.getDatePicker(txtFechaFinal),
                        0,
                        0, "");
                break;
            case 1:
                fillVentasTable((short) 2, txtBuscar.getText().trim(), "", "", 0, 0, "");
                break;
        }
    }

    @FXML
    private void onKeyPressedBuscar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!tvList.getItems().isEmpty()) {
                tvList.requestFocus();
                tvList.getSelectionModel().select(0);
            }
        }
    }

    @FXML
    private void onKeyReleasedBuscar(KeyEvent event) {
        if (event.getCode() != KeyCode.ESCAPE
                && event.getCode() != KeyCode.F1
                && event.getCode() != KeyCode.F2
                && event.getCode() != KeyCode.F3
                && event.getCode() != KeyCode.F4
                && event.getCode() != KeyCode.F5
                && event.getCode() != KeyCode.F6
                && event.getCode() != KeyCode.F7
                && event.getCode() != KeyCode.F8
                && event.getCode() != KeyCode.F9
                && event.getCode() != KeyCode.F10
                && event.getCode() != KeyCode.F11
                && event.getCode() != KeyCode.F12
                && event.getCode() != KeyCode.ALT
                && event.getCode() != KeyCode.CONTROL
                && event.getCode() != KeyCode.UP
                && event.getCode() != KeyCode.DOWN
                && event.getCode() != KeyCode.RIGHT
                && event.getCode() != KeyCode.LEFT
                && event.getCode() != KeyCode.TAB
                && event.getCode() != KeyCode.CAPS
                && event.getCode() != KeyCode.SHIFT
                && event.getCode() != KeyCode.HOME
                && event.getCode() != KeyCode.WINDOWS
                && event.getCode() != KeyCode.ALT_GRAPH
                && event.getCode() != KeyCode.CONTEXT_MENU
                && event.getCode() != KeyCode.END
                && event.getCode() != KeyCode.INSERT
                && event.getCode() != KeyCode.PAGE_UP
                && event.getCode() != KeyCode.PAGE_DOWN
                && event.getCode() != KeyCode.NUM_LOCK
                && event.getCode() != KeyCode.PRINTSCREEN
                && event.getCode() != KeyCode.SCROLL_LOCK
                && event.getCode() != KeyCode.PAUSE) {
            if (!lblLoad.isVisible()) {
                paginacion = 1;
                fillVentasTable((short) 2, txtBuscar.getText().trim(), "", "", 0, 0, "");
                opcion = 1;
            }
        }
    }

    @FXML
    private void onActionFechaInicio(ActionEvent event) {
        if (txtFechaInicio.getValue() != null && txtFechaFinal.getValue() != null) {
            if (!lblLoad.isVisible()) {
                paginacion = 1;
                fillVentasTable((short) 3, "", Tools.getDatePicker(txtFechaInicio), Tools.getDatePicker(txtFechaFinal),
                        0,
                        0, "");
                opcion = 0;
            }
        }
    }

    @FXML
    private void onActionFechaFinal(ActionEvent event) {
        if (txtFechaInicio.getValue() != null && txtFechaFinal.getValue() != null) {
            if (!lblLoad.isVisible()) {
                paginacion = 1;
                fillVentasTable((short) 3, "", Tools.getDatePicker(txtFechaInicio), Tools.getDatePicker(txtFechaFinal),
                        0,
                        0, "");
                opcion = 0;
            }
        }
    }

    @FXML
    private void onkeyPressedRecargar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                Tools.actualDate(Tools.getDate(), txtFechaInicio);
                Tools.actualDate(Tools.getDate(), txtFechaFinal);
                if (txtFechaInicio.getValue() != null && txtFechaFinal.getValue() != null) {
                    paginacion = 1;
                    fillVentasTable((short) 3, "", Tools.getDatePicker(txtFechaInicio), Tools.getDatePicker(txtFechaFinal),
                            0,
                            0, "");
                    opcion = 0;
                }
            }
        }
    }

    @FXML
    private void onActionRecargar(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            Tools.actualDate(Tools.getDate(), txtFechaInicio);
            Tools.actualDate(Tools.getDate(), txtFechaFinal);
            if (txtFechaInicio.getValue() != null && txtFechaFinal.getValue() != null) {
                paginacion = 1;
                fillVentasTable((short) 0, "", Tools.getDatePicker(txtFechaInicio), Tools.getDatePicker(txtFechaFinal),
                        0,
                        0, "");
                opcion = 0;
            }
        }
    }

    @FXML
    private void onKeyPressedList(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                guiaRemisionController.loadVentaById(tvList.getSelectionModel().getSelectedItem().getIdVenta());
                Tools.Dispose(apWindow);
            }
        }
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) {
        if (event.getClickCount() == 2) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                guiaRemisionController.loadVentaById(tvList.getSelectionModel().getSelectedItem().getIdVenta());
                Tools.Dispose(apWindow);
            }
        }
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                guiaRemisionController.loadVentaById(tvList.getSelectionModel().getSelectedItem().getIdVenta());
                Tools.Dispose(apWindow);
            }
        }
    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            guiaRemisionController.loadVentaById(tvList.getSelectionModel().getSelectedItem().getIdVenta());
            Tools.Dispose(apWindow);
        }
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

    @FXML
    private void onKeyPressedAnterior(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                if (paginacion > 1) {
                    paginacion--;
                    onEventPaginacion();
                }
            }
        }
    }

    @FXML
    private void onActionAnterior(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (paginacion > 1) {
                paginacion--;
                onEventPaginacion();
            }
        }
    }

    @FXML
    private void onKeyPressedSiguiente(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                if (paginacion < totalPaginacion) {
                    paginacion++;
                    onEventPaginacion();
                }
            }
        }
    }

    @FXML
    private void onActionSiguiente(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (paginacion < totalPaginacion) {
                paginacion++;
                onEventPaginacion();
            }
        }
    }

    public void setInitGuiaRemisionController(FxGuiaRemisionController guiaRemisionController) {
        this.guiaRemisionController = guiaRemisionController;
    }

}
