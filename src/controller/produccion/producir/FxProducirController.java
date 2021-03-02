package controller.produccion.producir;

import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.ProduccionADO;
import model.ProduccionTB;

public class FxProducirController implements Initializable {

    @FXML
    private HBox window;
    @FXML
    private Label lblLoad;
    @FXML
    private TableView<ProduccionTB> tvList;
    @FXML
    private TableColumn<ProduccionTB, String> tcNumero;
    @FXML
    private TableColumn<ProduccionTB, String> tcEncargado;
    @FXML
    private TableColumn<ProduccionTB, String> tcFechaInicio;
    @FXML
    private TableColumn<ProduccionTB, String> tcDuracion;
    @FXML
    private TableColumn<ProduccionTB, String> tcProductoFabricar;
    @FXML
    private TableColumn<ProduccionTB, String> tcCantidad;
    @FXML
    private TableColumn<ProduccionTB, Label> tcEstado;
    @FXML
    private Label lblPaginaActual;
    @FXML
    private Label lblPaginaSiguiente;
    @FXML
    private DatePicker dtFechaInicial;
    @FXML
    private DatePicker dtFechaFinal;
    @FXML
    private TextField txtSearch;

    private AnchorPane node;

    private FxProducirAgregarController controller;

    private AnchorPane vbContent;

    private AnchorPane vbPrincipal;

    private int paginacion;

    private int totalPaginacion;

    private short opcion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(getClass().getResource(FilesRouters.FX_PRODUCIR_AGREGAR));
            node = fXMLLoader.load();
            controller = fXMLLoader.getController();

            paginacion = 1;
            opcion = 0;
            loadTableComponents();
            initLoadTable();

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void loadTableComponents() {
        tcNumero.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcEncargado.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getEmpleadoTB().getNumeroDocumento() + "\n"
                + cellData.getValue().getEmpleadoTB().getApellidos() + " " + cellData.getValue().getEmpleadoTB().getNombres()
        ));
        tcFechaInicio.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getFechaInicio() + "\n"
                + cellData.getValue().getHoraInicio()
        ));
        tcDuracion.setCellValueFactory(cellData -> Bindings.concat(
                (cellData.getValue().getDias() == 1 ? cellData.getValue().getDias() + " día" : cellData.getValue().getDias() + " días ") + cellData.getValue().getHoras() + ":" + cellData.getValue().getMinutos() + ":00" + "\n"
                + cellData.getValue().getFechaInicio()
        ));
        tcProductoFabricar.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getSuministroTB().getClave() + "\n"
                + cellData.getValue().getSuministroTB().getNombreMarca()
        ));
        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getCantidad(), 2) + " " + cellData.getValue().getSuministroTB().getUnidadCompraName()
        ));

        tcEstado.setCellValueFactory(new PropertyValueFactory<>("lblEstado"));
        tcNumero.prefWidthProperty().bind(tvList.widthProperty().multiply(0.05));
        tcEncargado.prefWidthProperty().bind(tvList.widthProperty().multiply(0.19));
        tcFechaInicio.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcDuracion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcProductoFabricar.prefWidthProperty().bind(tvList.widthProperty().multiply(0.22));
        tcCantidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcEstado.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tvList.setPlaceholder(Tools.placeHolderTableView("No hay datos para mostrar.", "-fx-text-fill:#020203;", false));

    }

    private void initLoadTable() {
        Tools.actualDate(Tools.getDate(), dtFechaInicial);
        Tools.actualDate(Tools.getDate(), dtFechaFinal);
        txtSearch.requestFocus();
        txtSearch.selectAll();
        if (dtFechaInicial.getValue() != null && dtFechaFinal.getValue() != null) {
            if (!lblLoad.isVisible()) {
                paginacion = 1;
                fillProduccionTable(0, Tools.getDatePicker(dtFechaInicial), Tools.getDatePicker(dtFechaFinal), "");
                opcion = 0;
            }
        }
    }

    private void fillProduccionTable(int tipo, String fechaInicio, String fechaFinal, String buscar) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<Object> task = new Task<Object>() {
            @Override
            public Object call() {
                return ProduccionADO.ListarProduccion(tipo, fechaInicio, fechaFinal, buscar, (paginacion - 1) * 20, 20);
            }
        };

        task.setOnScheduled(w -> {
            lblLoad.setVisible(true);
            tvList.getItems().clear();
            tvList.setPlaceholder(Tools.placeHolderTableView("Cargando información...", "-fx-text-fill:#020203;", true));
            totalPaginacion = 0;
        });

        task.setOnFailed(w -> {
            lblLoad.setVisible(false);
            tvList.setPlaceholder(Tools.placeHolderTableView(task.getMessage(), "-fx-text-fill:#a70820;", false));

        });

        task.setOnSucceeded(w -> {
            Object object = task.getValue();
            if (object instanceof Object[]) {
                Object[] objects = (Object[]) object;
                ObservableList<ProduccionTB> produccionTBs = (ObservableList<ProduccionTB>) objects[0];
                if (!produccionTBs.isEmpty()) {
                    tvList.setItems(produccionTBs);
                    totalPaginacion = (int) (Math.ceil(((Integer) objects[1]) / 20.00));
                    lblPaginaActual.setText(paginacion + "");
                    lblPaginaSiguiente.setText(totalPaginacion + "");
                } else {
                    tvList.setPlaceholder(Tools.placeHolderTableView("No hay datos para mostrar.", "-fx-text-fill:#020203;", false));
                    lblPaginaActual.setText("0");
                    lblPaginaSiguiente.setText("0");
                }

            } else if (object instanceof String) {
                tvList.setPlaceholder(Tools.placeHolderTableView((String) object, "-fx-text-fill:#a70820;", false));
            } else {
                tvList.setPlaceholder(Tools.placeHolderTableView("Error en traer los datos, intente nuevamente.", "-fx-text-fill:#a70820;", false));
            }
            lblLoad.setVisible(false);
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    public void onEventPaginacion() {
        switch (opcion) {
            case 0:
                fillProduccionTable(0, Tools.getDatePicker(dtFechaInicial), Tools.getDatePicker(dtFechaFinal), "");
                break;
            case 1:
                fillProduccionTable(1, "", "", txtSearch.getText().trim());
                break;
        }
    }

    private void onViewProducirProceso() {
        controller.setInitControllerProducir(this, vbPrincipal, vbContent);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(node, 0d);
        AnchorPane.setTopAnchor(node, 0d);
        AnchorPane.setRightAnchor(node, 0d);
        AnchorPane.setBottomAnchor(node, 0d);
        vbContent.getChildren().add(node);
    }

    @FXML
    private void onKeyPressedAgregar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onViewProducirProceso();
        }
    }

    @FXML
    private void onActionAgregar(ActionEvent event) {
        onViewProducirProceso();
    }

    @FXML
    private void onKeyPressedRecargar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            initLoadTable();
        }
    }

    @FXML
    private void onActionRecargar(ActionEvent event) {
        initLoadTable();
    }

    @FXML
    private void onKeyReleasedSearch(KeyEvent event) {
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
            if (!Tools.isText(txtSearch.getText())) {
                if (!lblLoad.isVisible()) {
                    paginacion = 1;
                    fillProduccionTable(1, "", "", txtSearch.getText().trim());
                    opcion = 1;
                }
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

    public HBox getWindow() {
        return window;
    }

    public void setContent(AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
