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
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    private TableColumn<ProduccionTB, String> tcNumeroOrden;
    @FXML
    private TableColumn<ProduccionTB, String> tcFecha;
    @FXML
    private TableColumn<ProduccionTB, String> tcProduccion;
    @FXML
    private TableColumn<ProduccionTB, String> tcProducto;
    @FXML
    private TableColumn<ProduccionTB, String> tcPersonaEncargada;
    @FXML
    private TableColumn<ProduccionTB, String> tcAreaProduccion;
    @FXML
    private TableColumn<ProduccionTB, String> tcTipoOrden;
    @FXML
    private TableColumn<ProduccionTB, String> tcCantidad;
    @FXML
    private Label lblPaginaActual;
    @FXML
    private Label lblPaginaSiguiente;
    @FXML
    private DatePicker dtFechaInicial;
    @FXML
    private DatePicker dtFechaFinal;

    private ScrollPane node;

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
//        tcNumeroOrden.setCellValueFactory(cellData -> Bindings.concat(
//                "Nrm. " + Tools.Generador0(cellData.getValue().getNumeroOrden())
//        ));
//        tcFecha.setCellValueFactory(cellData -> Bindings.concat(
//                cellData.getValue().getFechaProduccion() + "\n"
//                + cellData.getValue().getHoraProduccion()
//        ));
//        tcProduccion.setCellValueFactory(cellData -> Bindings.concat(
//                cellData.getValue().getFechaInicio() + "\n"
//                + cellData.getValue().getFechaTermino()
//        ));
//        tcProducto.setCellValueFactory(cellData -> Bindings.concat(
//                cellData.getValue().getSuministroTB().getClave() + "\n"
//                + cellData.getValue().getSuministroTB().getNombreMarca()
//        ));
//        tcTipoOrden.setCellValueFactory(cellData -> Bindings.concat(
//                cellData.getValue().isTipoOrden() ? "INTERNO" : "EXTERNO"
//        ));
        tvList.setPlaceholder(Tools.placeHolderTableView("No hay datos para mostrar.", "-fx-text-fill:#020203;", false));

    }

    private void initLoadTable() {
        Tools.actualDate(Tools.getDate(), dtFechaInicial);
        Tools.actualDate(Tools.getDate(), dtFechaFinal);
        if (dtFechaInicial.getValue() != null && dtFechaFinal.getValue() != null) {
            paginacion = 1;
            fillProduccionTable(0, Tools.getDatePicker(dtFechaInicial), Tools.getDatePicker(dtFechaFinal), "");
            opcion = 0;
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

    public HBox getWindow() {
        return window;
    }

    public void setContent(AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
