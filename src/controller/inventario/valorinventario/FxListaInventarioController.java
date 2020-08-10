package controller.inventario.valorinventario;

import controller.tools.Tools;
import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.SuministroADO;
import model.SuministroTB;

public class FxListaInventarioController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblLoad;
    @FXML
    private TextField txtSearch;
    @FXML
    private Label lblPaginaActual;
    @FXML
    private Label lblPaginaSiguiente;
    @FXML
    private TableView<SuministroTB> tvLista;
    @FXML
    private TableColumn<SuministroTB, Integer> tcNumero;
    @FXML
    private TableColumn<SuministroTB, String> tcDescripcion;
    @FXML
    private TableColumn<SuministroTB, String> tcMarca;
    @FXML
    private TableColumn<SuministroTB, String> tcCategoria;
    @FXML
    private TableColumn<SuministroTB, Label> tcCantidad;
    @FXML
    private TableColumn<SuministroTB, String> tcStock;

    private int paginacion;

    private int totalPaginacion;

    private short opcion;

    private short existencia;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
        tcNumero.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        tcDescripcion.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getClave() + (cellData.getValue().getClaveAlterna().isEmpty() ? "" : " - ") + cellData.getValue().getClaveAlterna()
                + "\n" + cellData.getValue().getNombreMarca()));
        tcCantidad.setCellValueFactory(new PropertyValueFactory<>("lblCantidad"));
        tcStock.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getStockMinimo(), 2) + " - " + Tools.roundingValue(cellData.getValue().getStockMaximo(), 2)));
        tcCategoria.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getCategoriaName()));
        tcMarca.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMarcaName()));

        tcNumero.prefWidthProperty().bind(tvLista.widthProperty().multiply(0.06));
        tcDescripcion.prefWidthProperty().bind(tvLista.widthProperty().multiply(0.29));
        tcCantidad.prefWidthProperty().bind(tvLista.widthProperty().multiply(0.18));
        tcStock.prefWidthProperty().bind(tvLista.widthProperty().multiply(0.15));
        tcCategoria.prefWidthProperty().bind(tvLista.widthProperty().multiply(0.15));
        tcMarca.prefWidthProperty().bind(tvLista.widthProperty().multiply(0.15));

        //falta 0.20
        paginacion = 1;
        totalPaginacion = 0;
        opcion = 0;
        existencia = 0;
    }

    //controllerValorInventario.fillInventarioTable("",(short)0,"",(short)0,0,0);
    public void loadData(short tipoExistencia) {
        existencia = tipoExistencia;
        paginacion = 1;
        fillInventarioTable("", existencia, "", (short) 3, 0, 0);
    }

    private void fillInventarioTable(String producto, short tipoExistencia, String nameProduct, short opcion, int categoria, int marca) {
        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                return SuministroADO.ListInventario(producto, tipoExistencia, nameProduct, opcion, categoria, marca, (paginacion - 1) * 10, 10);
            }
        };
        task.setOnSucceeded(w -> {
            ArrayList<Object> objects = task.getValue();
            if (!objects.isEmpty()) {
                tvLista.setItems((ObservableList<SuministroTB>) objects.get(0));
                int integer = (int) (Math.ceil((double) (((Integer) objects.get(1)) / 10.00)));
                totalPaginacion = integer;
                lblPaginaActual.setText(paginacion + "");
                lblPaginaSiguiente.setText(totalPaginacion + "");
            }
            lblLoad.setVisible(false);
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

    public void onEventPaginacion() {
        switch (opcion) {
            case 0:
                fillInventarioTable("", existencia, "", (short) 3, 0, 0);
                break;
            case 2:
                fillInventarioTable("", existencia, txtSearch.getText().trim(), (short) 3, 0, 0);
                break;
        }
    }

    @FXML
    private void onKeyPressedReleased(KeyEvent event) {
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
                fillInventarioTable("", existencia, txtSearch.getText().trim(), (short) 3, 0, 0);
                opcion = 2;
            }
        }
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

    @FXML
    private void onKeyPressedRecargar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                paginacion = 1;
                fillInventarioTable("", existencia, "", (short) 3, 0, 0);
                paginacion = 0;
            }
        }
    }

    @FXML
    private void onActionRecargar(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            paginacion = 1;
            fillInventarioTable("", existencia, "", (short) 3, 0, 0);
            opcion = 0;
        }
    }

    public Label getLblTitle() {
        return lblTitle;
    }

}
