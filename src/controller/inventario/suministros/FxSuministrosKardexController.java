package controller.inventario.suministros;

import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
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
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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
import javafx.stage.Stage;
import model.KardexADO;
import model.KardexTB;
import model.SuministroADO;
import model.SuministroTB;

public class FxSuministrosKardexController implements Initializable {

    @FXML
    private HBox hbWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private TextField txtSearch;
    @FXML
    private Label lblProducto;
    @FXML
    private TableView<KardexTB> tvList;
    @FXML
    private TableColumn<KardexTB, String> tcNumero;
    @FXML
    private TableColumn<KardexTB, String> tcFecha;
    @FXML
    private TableColumn<KardexTB, String> tcDetalle;
    //private TableColumn<KardexTB, Label> tcInicial;
    @FXML
    private TableColumn<KardexTB, Label> tcCantidadEntrada;
    @FXML
    private TableColumn<KardexTB, Label> tcCostoEntrada;
    @FXML
    private TableColumn<KardexTB, Label> tcTotalEntrada;
    @FXML
    private TableColumn<KardexTB, Label> tcCantidadSalida;
    @FXML
    private TableColumn<KardexTB, Label> tcCostoSalida;
    @FXML
    private TableColumn<KardexTB, Label> tcTotalSalida;
    @FXML
    private TableColumn<KardexTB, Label> tcCantidadSaldo;
    @FXML
    private TableColumn<KardexTB, Label> tcCostoSaldo;
    @FXML
    private TableColumn<KardexTB, Label> tcTotalSaldo;
    @FXML
    private Label lblCantidadTotal;
    @FXML
    private DatePicker dtFechaInicial;
    @FXML
    private DatePicker dtFechaFinal;

    private String idSuministros;

    private AnchorPane vbPrincipal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcNumero.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcFecha.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getFecha() + "\n" + cellData.getValue().getHora()));
        tcDetalle.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMovimientoName() + "\n" + cellData.getValue().getDetalle().toUpperCase()));

        tcCantidadEntrada.setCellValueFactory(new PropertyValueFactory<>("lblEntrada"));
        tcCostoEntrada.setCellValueFactory(new PropertyValueFactory<>("lblEntrada"));
        tcTotalEntrada.setCellValueFactory(new PropertyValueFactory<>("lblEntrada"));

        tcCantidadSalida.setCellValueFactory(new PropertyValueFactory<>("lblSalida"));
        tcCostoSalida.setCellValueFactory(new PropertyValueFactory<>("lblSalida"));
        tcTotalSalida.setCellValueFactory(new PropertyValueFactory<>("lblSalida"));

        tcCantidadSaldo.setCellValueFactory(new PropertyValueFactory<>("lblSaldo"));
        tcCostoSaldo.setCellValueFactory(new PropertyValueFactory<>("lblSaldo"));
        tcTotalSaldo.setCellValueFactory(new PropertyValueFactory<>("lblSaldo"));
        //tcInicial.setCellValueFactory(new PropertyValueFactory<>("lblInicial"));
//        tcEntrada.setCellValueFactory(new PropertyValueFactory<>("lblEntrada"));
//        tcSalida.setCellValueFactory(new PropertyValueFactory<>("lblSalida"));
//        tcSaldo.setCellValueFactory(new PropertyValueFactory<>("lblSaldo"));
        //tcInicial.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getInicial(), 4)));
        //tcEntrada.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getTipo() == 1 ? Tools.roundingValue(cellData.getValue().getCantidad(), 4) : "0.0000"));
        //tcSalida.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getTipo() == 2 ? Tools.roundingValue(cellData.getValue().getCantidad() == 0 ?  cellData.getValue().getCantidad(): -cellData.getValue().getCantidad(), 4) : "0.0000"));
        // tcSaldo.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getCantidadTotal(), 4)));

        tcNumero.prefWidthProperty().bind(tvList.widthProperty().multiply(0.06));
        tcFecha.prefWidthProperty().bind(tvList.widthProperty().multiply(0.10));
        tcDetalle.prefWidthProperty().bind(tvList.widthProperty().multiply(0.19));

        tcCantidadEntrada.prefWidthProperty().bind(tvList.widthProperty().multiply(0.07));
        tcCostoEntrada.prefWidthProperty().bind(tvList.widthProperty().multiply(0.07));
        tcTotalEntrada.prefWidthProperty().bind(tvList.widthProperty().multiply(0.07));

        tcCantidadSalida.prefWidthProperty().bind(tvList.widthProperty().multiply(0.07));
        tcCostoSalida.prefWidthProperty().bind(tvList.widthProperty().multiply(0.07));
        tcTotalSalida.prefWidthProperty().bind(tvList.widthProperty().multiply(0.07));

        tcCantidadSaldo.prefWidthProperty().bind(tvList.widthProperty().multiply(0.07));
        tcCostoSaldo.prefWidthProperty().bind(tvList.widthProperty().multiply(0.07));
        tcTotalSaldo.prefWidthProperty().bind(tvList.widthProperty().multiply(0.07));
        //tcInicial.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
//        tcEntrada.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));
//        tcSalida.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));
//        tcSaldo.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));

        idSuministros = "";
    }

    public void fillKardexTable(String value, String fechaInicial, String fechaFinal) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<ObservableList<KardexTB>> task = new Task<ObservableList<KardexTB>>() {
            @Override
            public ObservableList<KardexTB> call() {
                return KardexADO.List_Kardex_By_Id_Suministro(value, fechaInicial, fechaFinal);
            }
        };

        task.setOnSucceeded((WorkerStateEvent e) -> {
            tvList.setItems(task.getValue());
            lblLoad.setVisible(false);
            if (!tvList.getItems().isEmpty()) {
                lblCantidadTotal.setText(Tools.roundingValue(tvList.getItems().get(tvList.getItems().size() - 1).getCantidadTotal(), 2));
            }
        });
        task.setOnFailed((WorkerStateEvent event) -> {
            lblLoad.setVisible(false);
        });
        task.setOnScheduled((WorkerStateEvent event) -> {
            lblLoad.setVisible(true);
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void openWindowSuministros() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_SUMINISTROS_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxSuministrosListaController controller = fXMLLoader.getController();
            controller.setInitSuministrosKardexController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Suministro", hbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();
            controller.fillSuministrosTablePaginacion();
        } catch (IOException ex) {
            System.out.println("Error en la vista suministros lista:" + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onActionSearch(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            SuministroTB suministroTB = SuministroADO.GetSuministroById(txtSearch.getText().trim());
            if (suministroTB != null) {
                setLoadProducto(suministroTB.getIdSuministro(),suministroTB.getClave() + " " + suministroTB.getNombreMarca());
                fillKardexTable(idSuministros, "", "");
            } else {
                setLoadProducto("","");
                fillKardexTable(idSuministros, "", "");
            }
        }
    }

    @FXML
    private void onKeyPressedSearchSuministro(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowSuministros();
        }
    }

    @FXML
    private void onActionFechaInicial(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (dtFechaInicial.getValue() != null && dtFechaFinal.getValue() != null) {
                fillKardexTable(idSuministros, Tools.getDatePicker(dtFechaInicial), Tools.getDatePicker(dtFechaFinal));
            }
        }
    }

    @FXML
    private void onActionFechaFinal(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (dtFechaInicial.getValue() != null && dtFechaFinal.getValue() != null) {
                fillKardexTable(idSuministros, Tools.getDatePicker(dtFechaInicial), Tools.getDatePicker(dtFechaFinal));
            }
        }
    }

    @FXML
    private void onActionSearchSuministro(ActionEvent event) {
        openWindowSuministros();
    }

    public void setLoadProducto(String idSuministro,String value) {
        idSuministros = idSuministro;
        lblProducto.setText(value);
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
