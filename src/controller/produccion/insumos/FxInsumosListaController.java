package controller.produccion.insumos;

import controller.produccion.producir.FxProducirAgregarController;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.InsumoADO;
import model.InsumoTB;

public class FxInsumosListaController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<InsumoTB> tvList;
    @FXML
    private TableColumn<InsumoTB, Integer> tcId;
    @FXML
    private TableColumn<InsumoTB, String> tcNombre;
    @FXML
    private TableColumn<InsumoTB, String> tcCategoriaMarca;
    @FXML
    private TableColumn<InsumoTB, String> tcCantidad;
    @FXML
    private TableColumn<InsumoTB, String> tcPrecio;
    @FXML
    private Label lblPaginaActual;
    @FXML
    private Label lblPaginaSiguiente;

    private FxProducirAgregarController producirAgregarController;

    private boolean stateBusqueda;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);

        tcId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        tcNombre.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getClave() + "\n"
                + cellData.getValue().getNombreMarca()
        ));
        tcPrecio.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getCosto(), 2)
        ));
        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getCantidad(), 2) + " " + cellData.getValue().getMedidaName()
        ));
        tcCategoriaMarca.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getCategoriaName()
        ));
        stateBusqueda = false;
    }

    public void loadInitComponents() {
        if (!stateBusqueda) {
            fillTableInsumos("");
        }
    }

    private void fillTableInsumos(String busqueda) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<ObservableList<InsumoTB>> task = new Task<ObservableList<InsumoTB>>() {
            @Override
            public ObservableList<InsumoTB> call() {
                return InsumoADO.ListarInsumos(busqueda);
            }
        };
        task.setOnSucceeded(w -> {
            tvList.setItems(task.getValue());
            stateBusqueda = false;
        });
        task.setOnFailed(w -> stateBusqueda = false);
        task.setOnScheduled(w -> stateBusqueda = true);
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private boolean validateDuplicate(TableView<InsumoTB> view, InsumoTB insumoTB) {
        boolean ret = false;
        for (InsumoTB value : view.getItems()) {
            if (value.getIdInsumo().equalsIgnoreCase(insumoTB.getIdInsumo())) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    private void addInsumoProduccion() {
//        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
//            if (!validateDuplicate(producirAgregarController.getTvListInsumos(), tvList.getSelectionModel().getSelectedItem())) {
//                producirAgregarController.addElementsTableInsumo(tvList.getSelectionModel().getSelectedItem());
//                Tools.Dispose(window);
//            } else {
//                Tools.AlertMessageWarning(window, "Insumos", "Ya existe un insumos en la lista con las misma caracteristicas.");
//            }
//        }
    }

    @FXML
    private void onKeyPressedToSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!tvList.getItems().isEmpty()) {
                tvList.getSelectionModel().select(0);
                tvList.requestFocus();
            }
        }
    }

    @FXML
    private void onKeyReleasedToSearh(KeyEvent event) {
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
                && event.getCode() != KeyCode.PAUSE
                && event.getCode() != KeyCode.ENTER) {
            if (!stateBusqueda) {
                fillTableInsumos(txtSearch.getText().trim());
            }
        }
    }

    @FXML
    private void onKeyPressedReload(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            loadInitComponents();
        }
    }

    @FXML
    private void onActionReload(ActionEvent event) {
        loadInitComponents();
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) {
        if (event.getClickCount() == 2) {
            addInsumoProduccion();
        }
    }

    @FXML
    private void onKeyPressedList(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            addInsumoProduccion();
        }
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            addInsumoProduccion();
        }
    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        addInsumoProduccion();
    }

    @FXML
    private void onKeyPressedAnterior(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionAnterior(ActionEvent event) {

    }

    @FXML
    private void onKeyPressedSiguiente(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionSiguiente(ActionEvent event) {

    }

    public void setInitProducirProcesoController(FxProducirAgregarController producirAgregarController) {
        this.producirAgregarController = producirAgregarController;
    }

}
