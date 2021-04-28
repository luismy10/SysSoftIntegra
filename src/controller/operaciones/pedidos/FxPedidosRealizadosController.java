package controller.operaciones.pedidos;

import controller.menus.FxPrincipalController;
import controller.operaciones.cotizacion.FxCotizacionDetalleController;
import controller.tools.FilesRouters;
import controller.tools.Tools;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.PedidoADO;
import model.PedidoTB;

public class FxPedidosRealizadosController implements Initializable {

    @FXML
    private VBox vbWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private TextField txtSearch;
    @FXML
    private DatePicker dtFechaInicial;
    @FXML
    private DatePicker dtFechaFinal;
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

    private FxPrincipalController fxPrincipalController;

    private int paginacion;

    private int totalPaginacion;

    private short opcion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.actualDate(Tools.getDate(), dtFechaInicial);
        Tools.actualDate(Tools.getDate(), dtFechaFinal);

        tcNumero.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcVendedor.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getEmpleadoTB().getApellidos() + "\n" + cellData.getValue().getEmpleadoTB().getNombres()));
        tcCotizacion.setCellValueFactory(cellData -> Bindings.concat("COTIZACIÓN N° " + cellData.getValue().getIdPedido()));
        tcFecha.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getFechaEmision() + "\n" + cellData.getValue().getHoraEmision()));
        tcProveedor.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getProveedorTB().getRazonSocial()));
        tcTotal.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMonedaTB().getSimbolo() + " " + Tools.roundingValue(cellData.getValue().getImporteNeto(), 2)));
        tvList.setPlaceholder(Tools.placeHolderTableView("No hay datos para mostrar.", "-fx-text-fill:#020203;", false));

    }

    public void loadInit() {
        if (!lblLoad.isVisible()) {
            paginacion = 1;
            fillTablePedidos((short) 1, "", "", "");
            opcion = 0;
        }
    }

    public void fillTablePedidos(int opcion, String search, String fechaInicio, String fechaFinal) {
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

    private void onEventPaginacion() {
        switch (opcion) {
            case 0:
                fillTablePedidos(0, "", "", "");
                break;
            case 1:
                fillTablePedidos(1, txtSearch.getText().trim(), "", "");
                break;
            case 2:
                fillTablePedidos(2, "", Tools.getDatePicker(dtFechaInicial), Tools.getDatePicker(dtFechaFinal));
                break;
        }
    }

    private void openWindowDetallePedido() throws IOException {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(FilesRouters.FX_PEDIDO_DETALLE));
            AnchorPane node = fXMLLoader.load();
            //Controlller here
            FxPedidosDetalleController controller = fXMLLoader.getController();
            controller.setInitPedidosRealizadasController(this, fxPrincipalController);
            //
            fxPrincipalController.getVbContent().getChildren().clear();
            AnchorPane.setLeftAnchor(node, 0d);
            AnchorPane.setTopAnchor(node, 0d);
            AnchorPane.setRightAnchor(node, 0d);
            AnchorPane.setBottomAnchor(node, 0d);
            fxPrincipalController.getVbContent().getChildren().add(node);
            controller.loadComponents(tvList.getSelectionModel().getSelectedItem().getIdPedido());
        } else {
            Tools.AlertMessageWarning(vbWindow, "Pedidos", "Debe seleccionar una compra de la lista");
        }
    }

    @FXML
    private void onKeyPressedMostrar(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowDetallePedido();
        }
    }

    @FXML
    private void onActionMostrar(ActionEvent event) throws IOException {
        openWindowDetallePedido();
    }

    @FXML
    private void onKeyPressedRecargar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            loadInit();
        }
    }

    @FXML
    private void onActionRecargar(ActionEvent event) {
        loadInit();
    }

    @FXML
    private void onKeyPressedSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!tvList.getItems().isEmpty()) {
                tvList.requestFocus();
                tvList.getSelectionModel().select(0);
            }
        }
    }

    @FXML
    private void onKeyRelasedSearch(KeyEvent event) {
        if (!lblLoad.isVisible()) {
            if (!Tools.isText(txtSearch.getText())) {
                paginacion = 1;
                fillTablePedidos(1, txtSearch.getText().trim(), "", "");
                opcion = 1;
            }
        }
    }

    @FXML
    private void onActionFechaInicio(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (dtFechaInicial.getValue() != null && dtFechaFinal.getValue() != null) {
                paginacion = 1;
                fillTablePedidos(2, "", Tools.getDatePicker(dtFechaInicial), Tools.getDatePicker(dtFechaFinal));
                opcion = 2;
            }
        }
    }

    @FXML
    private void onActionFechaFinal(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (dtFechaInicial.getValue() != null && dtFechaFinal.getValue() != null) {
                paginacion = 1;
                fillTablePedidos(2, "", Tools.getDatePicker(dtFechaInicial), Tools.getDatePicker(dtFechaFinal));
                opcion = 2;
            }
        }
    }

    @FXML
    private void onKeyPressedList(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowDetallePedido();
        }
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) throws IOException {
        openWindowDetallePedido();
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

    public VBox getVbWindow() {
        return vbWindow;
    }

    public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }

}
