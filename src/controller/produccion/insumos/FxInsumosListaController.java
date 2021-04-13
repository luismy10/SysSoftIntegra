package controller.produccion.insumos;

import controller.produccion.compras.FxComprasInsumosController;
import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
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

    private FxComprasInsumosController comprasInsumosController;

    private int paginacion;

    private boolean stateBusqueda;

    private int totalPaginacion;

    private short opcion;

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

        paginacion = 1;
        stateBusqueda = false;
        opcion = 0;

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTextInput(1, (short) 1, (short) 1, oldValue, newValue);
        });
    }

    public void loadInitComponents() {
        if (!stateBusqueda) {
            paginacion = 1;
            fillTableInsumos((short) 0, "");
            opcion = 0;
        }
    }

    private void filterTextInput(int paginacion, short opcion, short tipo, String oldValue, String newValue) {
        if (!newValue.trim().equalsIgnoreCase("")) {
            this.paginacion = paginacion;
            fillTableInsumos(tipo, newValue.trim());
            this.opcion = opcion;
        } else {
            if (oldValue.trim().length() > 0) {
                this.paginacion = 0;
                tvList.getItems().clear();
                this.opcion = -1;
                totalPaginacion = 0;
                lblPaginaActual.setText(this.paginacion + "");
                lblPaginaSiguiente.setText(totalPaginacion + "");
            }
        }
    }

    public void fillTableInsumos(short tipo, String busqueda) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                return InsumoADO.ListarInsumosListaView(tipo, busqueda, (paginacion - 1) * 10, 10);
            }
        };

        task.setOnSucceeded(e -> {
            ArrayList<Object> objects = task.getValue();
            if (!objects.isEmpty()) {
                tvList.setItems((ObservableList<InsumoTB>) objects.get(0));
                if (!tvList.getItems().isEmpty()) {
                    tvList.getSelectionModel().select(0);
                }
                int integer = (int) (Math.ceil(((Integer) objects.get(1)) / 10.00));
                totalPaginacion = integer;
                lblPaginaActual.setText(paginacion + "");
                lblPaginaSiguiente.setText(totalPaginacion + "");
                stateBusqueda = false;
            } else {
                tvList.getItems().clear();
                stateBusqueda = false;
            }
        });
        task.setOnFailed(e -> {
            stateBusqueda = false;
        });
        task.setOnScheduled(e -> {
            stateBusqueda = true;
        });
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
        if (comprasInsumosController != null) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                try {
                    URL url = WindowStage.class.getClassLoader().getClass().getResource(FilesRouters.FX_INSUMOS_COMPRA);
                    FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                    Parent parent = fXMLLoader.load(url.openStream());
                    //Controlller here
                    FxInsumosCompraController controller = fXMLLoader.getController();
                    controller.setInitComprasInsumosController(comprasInsumosController);
                    controller.loadComponents(tvList.getSelectionModel().getSelectedItem());
                    //
                    Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Producto", window.getScene().getWindow());
                    stage.setResizable(false);
                    stage.sizeToScene();
                    stage.show();
                } catch (IOException ix) {
                    System.out.println("Error Producto Lista Controller:" + ix.getLocalizedMessage());
                }
            }
        }

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
                fillTableInsumos((short) 1, txtSearch.getText().trim());
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
        if (null != event.getCode()) {
            switch (event.getCode()) {
                case ENTER:
                    addInsumoProduccion();
                    break;
                default:
                    break;
            }
        }
    }

    @FXML
    private void onKeyReleasedList(KeyEvent event) {
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

    public void setInitComprasController(FxComprasInsumosController comprasInsumosController) {
        this.comprasInsumosController = comprasInsumosController;
    }

}
