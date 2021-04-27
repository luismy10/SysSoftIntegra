package controller.operaciones.pedidos;

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
import model.PedidoADO;
import model.PedidoTB;

public class FxPedidosListaController implements Initializable {

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
    private TableView<PedidoTB> tvList;
    @FXML
    private TableColumn<PedidoTB, String> tcNumero;
    @FXML
    private TableColumn<PedidoTB, String> tcVendedor;
    @FXML
    private TableColumn<PedidoTB, String> tcCotizacion;
    @FXML
    private TableColumn<PedidoTB, String> tcFecha;
    @FXML
    private TableColumn<PedidoTB, String> tcProveedor;
    @FXML
    private TableColumn<PedidoTB, String> tcTotal;
    @FXML
    private Label lblPaginaActual;
    @FXML
    private Label lblPaginaSiguiente;

    private FxPedidosController pedidosController;

    private int paginacion;

    private int totalPaginacion;

    private short opcion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);

        tcNumero.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcVendedor.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getEmpleadoTB().getApellidos() + "\n" + cellData.getValue().getEmpleadoTB().getNombres()));
        tcCotizacion.setCellValueFactory(cellData -> Bindings.concat("COTIZACIÓN N° " + cellData.getValue().getIdPedido()));
        tcFecha.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getFechaEmision() + "\n" + cellData.getValue().getHoraEmision()));
        tcProveedor.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getProveedorTB().getRazonSocial()));
        tcTotal.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMonedaTB().getSimbolo() + " " + Tools.roundingValue(cellData.getValue().getImporteNeto(), 2)));
        tvList.setPlaceholder(Tools.placeHolderTableView("No hay datos para mostrar.", "-fx-text-fill:#020203;", false));

        Tools.actualDate(Tools.getDate(), txtFechaInicio);
        Tools.actualDate(Tools.getDate(), txtFechaFinal);
    }

    public void initLoad() {
        Tools.actualDate(Tools.getDate(), txtFechaInicio);
        Tools.actualDate(Tools.getDate(), txtFechaFinal);
        if (!lblLoad.isVisible()) {
            paginacion = 1;
            fillTablePedido(0, "", "", "");
            opcion = 0;
        }
    }

    private void fillTablePedido(int opcion, String search, String fechaInicio, String fechaFinal) {
        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<Object> task = new Task<Object>() {
            @Override
            public Object call() {
                return PedidoADO.ListarPedidos(opcion, search, fechaInicio, fechaFinal, (paginacion - 1) * 10, 10);
            }
        };
        task.setOnSucceeded(w -> {
            Object object = task.getValue();
            if (object instanceof Object[]) {
                Object[] objects = (Object[]) object;
                ObservableList<PedidoTB> pedidoTBs = (ObservableList<PedidoTB>) objects[0];
                if (!pedidoTBs.isEmpty()) {
                    tvList.setItems(pedidoTBs);
                    totalPaginacion = (int) (Math.ceil(((Integer) objects[1]) / 10.00));
                    lblPaginaActual.setText(paginacion + "");
                    lblPaginaSiguiente.setText(totalPaginacion + "");
                } else {
                    tvList.setPlaceholder(Tools.placeHolderTableView("No hay datos para mostrar.", "-fx-text-fill:#020203;", false));
                    lblPaginaActual.setText("0");
                    lblPaginaSiguiente.setText("0");
                }
            } else {
                tvList.setPlaceholder(Tools.placeHolderTableView((String) object, "-fx-text-fill:#a70820;", false));
            }
            lblLoad.setVisible(false);
        });
        task.setOnFailed(w -> {
            lblLoad.setVisible(false);
            tvList.setPlaceholder(Tools.placeHolderTableView(task.getMessage(), "-fx-text-fill:#a70820;", false));
        });
        task.setOnScheduled(w -> {
            lblLoad.setVisible(true);
            tvList.getItems().clear();
            tvList.setPlaceholder(Tools.placeHolderTableView("Cargando información...", "-fx-text-fill:#020203;", true));
            totalPaginacion = 0;
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void onEventAceptar() {
        if (pedidosController != null) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                Tools.Dispose(apWindow);
                pedidosController.loadPedido(tvList.getSelectionModel().getSelectedItem().getIdPedido());
            }
        }
    }

    private void onEventPaginacion() {
        switch (opcion) {
            case 0:
                fillTablePedido(0, "", "", "");
                break;
            case 1:
                fillTablePedido(1, txtBuscar.getText().trim(), "", "");
                break;
            case 2:
                fillTablePedido(2, "", Tools.getDatePicker(txtFechaInicio), Tools.getDatePicker(txtFechaFinal));
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
        if (!lblLoad.isVisible()) {
            if (!Tools.isText(txtBuscar.getText())) {
                paginacion = 1;
                fillTablePedido(1, txtBuscar.getText().trim(), "", "");
                opcion = 1;
            }
        }
    }

    @FXML
    private void onActionFechaInicio(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (txtFechaInicio.getValue() != null && txtFechaFinal.getValue() != null) {
                paginacion = 1;
                fillTablePedido(2, "", Tools.getDatePicker(txtFechaInicio), Tools.getDatePicker(txtFechaFinal));
                opcion = 2;
            }
        }
    }

    @FXML
    private void onActionFechaFinal(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (txtFechaInicio.getValue() != null && txtFechaFinal.getValue() != null) {
                paginacion = 1;
                fillTablePedido(2, "", Tools.getDatePicker(txtFechaInicio), Tools.getDatePicker(txtFechaFinal));
                opcion = 2;
            }
        }
    }

    @FXML
    private void onkeyPressedRecargar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                initLoad();
            }
        }
    }

    @FXML
    private void onActionRecargar(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            initLoad();
        }
    }

    @FXML
    private void onKeyPressedList(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!tvList.getItems().isEmpty()) {
                onEventAceptar();
            }
        }
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) {
        if (!tvList.getItems().isEmpty()) {
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

    public void setInitPedidoListaController(FxPedidosController pedidosController) {
        this.pedidosController = pedidosController;
    }

}
