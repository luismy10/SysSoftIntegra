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
import javafx.collections.ObservableList;
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
import model.InsumoADO;
import model.InsumoTB;
import report.ProduccionADO;
import report.ProduccionTB;

public class FxProducirController implements Initializable {

    @FXML
    private HBox window;
    @FXML
    private Label lblLoad;
    @FXML
    private DatePicker dtFechaInicial;
    @FXML
    private DatePicker dtFechaFinal;
    @FXML
    private TableView<ProduccionTB> tvList;
    @FXML
    private TableColumn<ProduccionTB, Integer> tcNumero;
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

    private ScrollPane node;

    private FxProducirProcesoController controller;

    private AnchorPane vbContent;

    private AnchorPane vbPrincipal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(getClass().getResource(FilesRouters.FX_PRODUCIR_PROCESO));
            node = fXMLLoader.load();
            controller = fXMLLoader.getController();
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }

        Tools.actualDate(Tools.getDate(), dtFechaInicial);
        Tools.actualDate(Tools.getDate(), dtFechaFinal);

        tcNumero.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        tcNumeroOrden.setCellValueFactory(cellData -> Bindings.concat(
                "Nrm. " + Tools.Generador0(cellData.getValue().getNumeroOrden())
        ));
        tcFecha.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getFechaProduccion() + "\n"
                + cellData.getValue().getHoraProduccion()
        ));
        tcProduccion.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getFechaInicio() + "\n"
                + cellData.getValue().getFechaTermino()
        ));
        tcProducto.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getSuministroTB().getClave() + "\n"
                + cellData.getValue().getSuministroTB().getNombreMarca()
        ));
        tcTipoOrden.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().isTipoOrden() ? "INTERNO" : "EXTERNO"
        ));

    }

    public void loadInitComponents() {
        if (!lblLoad.isVisible()) {
            fillTableInsumos("");
        }
    }

    private void fillTableInsumos(String busqueda) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<ObservableList<ProduccionTB>> task = new Task<ObservableList<ProduccionTB>>() {
            @Override
            public ObservableList<ProduccionTB> call() {
                return ProduccionADO.ListarProduccion();
            }
        };
        task.setOnSucceeded(w -> {
            tvList.setItems(task.getValue());
            lblLoad.setVisible(false);
        });
        task.setOnFailed(w -> lblLoad.setVisible(false));
        task.setOnScheduled(w -> lblLoad.setVisible(true));
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

        //
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
            loadInitComponents();
        }
    }

    @FXML
    private void onActionRecargar(ActionEvent event) {
        loadInitComponents();
    }

    public HBox getWindow() {
        return window;
    }

    public void setContent(AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
