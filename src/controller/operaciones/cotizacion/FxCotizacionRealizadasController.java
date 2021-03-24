package controller.operaciones.cotizacion;

import controller.menus.FxPrincipalController;
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
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.CotizacionADO;
import model.CotizacionTB;

public class FxCotizacionRealizadasController implements Initializable {

    @FXML
    private VBox hbWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private Button btnMostrar;
    @FXML
    private Button btnRecargar;
    @FXML
    private TextField txtSearch;
    @FXML
    private DatePicker dtFechaInicial;
    @FXML
    private DatePicker dtFechaFinal;
    @FXML
    private TableView<CotizacionTB> tvList;
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
    @FXML
    private Label lblPaginaActual;
    @FXML
    private Label lblPaginaSiguiente;

    private FxPrincipalController fxPrincipalController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcNumero.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcVendedor.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getEmpleadoTB().getApellidos() + "\n" + cellData.getValue().getEmpleadoTB().getNombres()));
        tcCotizacion.setCellValueFactory(cellData -> Bindings.concat("COTIZACIÓN N° " + cellData.getValue().getIdCotizacion()));
        tcFecha.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getFechaCotizacion() + "\n" + cellData.getValue().getHoraCotizacion()));
        tcCliente.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getClienteTB().getInformacion()));
        tcTotal.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMonedaTB().getSimbolo() + " " + Tools.roundingValue(cellData.getValue().getTotal(), 2)));

        tcNumero.prefWidthProperty().bind(tvList.widthProperty().multiply(0.05));
        tcVendedor.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));
        tcCotizacion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.18));
        tcFecha.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));
        tcCliente.prefWidthProperty().bind(tvList.widthProperty().multiply(0.30));
        tcTotal.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));

        Tools.actualDate(Tools.getDate(), dtFechaInicial);
        Tools.actualDate(Tools.getDate(), dtFechaFinal);
    }

    public void loadInit() {
        loadTable((short) 1, "", "", "");
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
                tvList.setItems(cotizacionTBs);
                lblLoad.setVisible(false);
            } else {
                tvList.getItems().clear();
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

    private void openWindowDetalleCotizacion() throws IOException {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(FilesRouters.FX_COTIZACION_DETALLE));
            ScrollPane node = fXMLLoader.load();
            //Controlller here
            FxCotizacionDetalleController controller = fXMLLoader.getController();  
            controller.setInitCotizacionesRealizadasController(this, fxPrincipalController);
            //
            fxPrincipalController.getVbContent().getChildren().clear();
            AnchorPane.setLeftAnchor(node, 0d);
            AnchorPane.setTopAnchor(node, 0d);
            AnchorPane.setRightAnchor(node, 0d);
            AnchorPane.setBottomAnchor(node, 0d);
            fxPrincipalController.getVbContent().getChildren().add(node);
            controller.setInitComponents(tvList.getSelectionModel().getSelectedItem().getIdCotizacion());
        } else {
            Tools.AlertMessageWarning(hbWindow, "Cotizaciones", "Debe seleccionar una compra de la lista");
        }
    }

    @FXML
    private void onKeyPressedMostar(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowDetalleCotizacion();
        }
    }

    @FXML
    private void onActionMostrar(ActionEvent event) throws IOException {
        openWindowDetalleCotizacion();
    }

    @FXML
    private void onKeyPressedRecargar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                txtSearch.clear();
                Tools.actualDate(Tools.getDate(), dtFechaInicial);
                Tools.actualDate(Tools.getDate(), dtFechaFinal);
                loadTable((short) 1, "", "", "");
            }
        }
    }

    @FXML
    private void onActionRecargar(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            txtSearch.clear();
            Tools.actualDate(Tools.getDate(), dtFechaInicial);
            Tools.actualDate(Tools.getDate(), dtFechaFinal);
            loadTable((short) 1, "", "", "");
        }
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
            loadTable((short) 2, txtSearch.getText().trim(), "", "");
        }
    }

    @FXML
    private void onActionFechaInicial(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (dtFechaInicial.getValue() != null && dtFechaFinal.getValue() != null) {
                loadTable((short) 3, "", Tools.getDatePicker(dtFechaInicial), Tools.getDatePicker(dtFechaFinal));
            }
        }
    }

    @FXML
    private void onActionFechaFinal(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (dtFechaInicial.getValue() != null && dtFechaFinal.getValue() != null) {
                loadTable((short) 3, "", Tools.getDatePicker(dtFechaInicial), Tools.getDatePicker(dtFechaFinal));
            }
        }
    }

    @FXML
    private void onKeyPressedList(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            if (!tvList.getItems().isEmpty()) {
                openWindowDetalleCotizacion(); 
            }
        }
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) throws IOException {
        if (event.getClickCount() == 2) {
            if (!tvList.getItems().isEmpty()) {
                openWindowDetalleCotizacion();
            }
        }
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

    public VBox getHbWindow() {
        return hbWindow;
    }

   
    public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController=fxPrincipalController;
    }

}
