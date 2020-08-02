package controller.inventario.suministros;

import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
    @FXML
    private TableColumn<KardexTB, Label> tcEntrada;
    @FXML
    private TableColumn<KardexTB, Label> tcSalida;
    @FXML
    private TableColumn<KardexTB, String> tcExistencia;
    @FXML
    private TableColumn<KardexTB, String> tcCostoPromedio;
    @FXML
    private TableColumn<KardexTB, String> tcDebe;
    @FXML
    private TableColumn<KardexTB, String> tcHaber;
    @FXML
    private TableColumn<KardexTB, String> tcSaldo;
    @FXML
    private Label lblCantidadActual;
    @FXML
    private Label lblValorActual;
    @FXML
    private DatePicker dtFechaInicial;
    @FXML
    private DatePicker dtFechaFinal;
    @FXML
    private Label lblPaginaActual;
    @FXML
    private Label lblPaginaSiguiente;

    private String idSuministro;

    private AnchorPane vbPrincipal;

    private int paginacion;

    private int totalPaginacion;

    private boolean status;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcNumero.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcFecha.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getFecha() + "\n" + cellData.getValue().getHora()));
        tcDetalle.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMovimientoName() + "\n" + cellData.getValue().getDetalle().toUpperCase()));

        tcEntrada.setCellValueFactory(new PropertyValueFactory<>("lblEntreda"));
        tcSalida.setCellValueFactory(new PropertyValueFactory<>("lblSalida"));
        tcExistencia.setCellValueFactory(new PropertyValueFactory<>("lblExistencia"));

        tcCostoPromedio.setCellValueFactory(new PropertyValueFactory<>("lblCosto"));

        tcDebe.setCellValueFactory(new PropertyValueFactory<>("lblDebe"));
        tcHaber.setCellValueFactory(new PropertyValueFactory<>("lblHaber"));
        tcSaldo.setCellValueFactory(new PropertyValueFactory<>("lblSaldo"));

        //
        tcNumero.prefWidthProperty().bind(tvList.widthProperty().multiply(0.06));
        tcFecha.prefWidthProperty().bind(tvList.widthProperty().multiply(0.10));
        tcDetalle.prefWidthProperty().bind(tvList.widthProperty().multiply(0.19));

        tcEntrada.prefWidthProperty().bind(tvList.widthProperty().multiply(0.09));
        tcSalida.prefWidthProperty().bind(tvList.widthProperty().multiply(0.09));
        tcExistencia.prefWidthProperty().bind(tvList.widthProperty().multiply(0.09));

        tcCostoPromedio.prefWidthProperty().bind(tvList.widthProperty().multiply(0.09));

        tcDebe.prefWidthProperty().bind(tvList.widthProperty().multiply(0.09));
        tcHaber.prefWidthProperty().bind(tvList.widthProperty().multiply(0.09));
        tcSaldo.prefWidthProperty().bind(tvList.widthProperty().multiply(0.09));

        idSuministro = "";
        paginacion = 1;
        status = false;
    }

    public void fillKardexTable(short opcion, String value, String fechaInicial, String fechaFinal) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                return KardexADO.List_Kardex_By_Id_Suministro(opcion, value, fechaInicial, fechaFinal, (paginacion - 1) * 10, 10);
            }
        };
        task.setOnSucceeded(w -> {
            ArrayList<Object> objects = task.getValue();
            if (!objects.isEmpty()) {

                tvList.setItems((ObservableList<KardexTB>) objects.get(0));

                lblCantidadActual.setText((objects.get(1) != null ? Tools.roundingValue((double) objects.get(1), 2) : "0.00"));
                lblValorActual.setText((objects.get(2) != null ? Tools.roundingValue((double) objects.get(2), 2) : "0.00"));
//                int integer = (int) (Math.ceil((double) (((Integer) objects.get(1)) / 10.00)));
//
//                totalPaginacion = integer;
//                lblPaginaActual.setText(paginacion + "");
//                lblPaginaSiguiente.setText(totalPaginacion + "");
//                
//                cantidad += (double) objects.get(2);
//                total += (double) objects.get(3);
                //System.out.println("Cantidad: "+cantidad );
                lblLoad.setVisible(false);
            } else {
                tvList.getItems().clear();
                lblCantidadActual.setText("0");
                lblValorActual.setText("0");
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
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
            controller.fillSuministrosTable((short) 0, "");
        } catch (IOException ex) {
            System.out.println("Error en la vista suministros lista:" + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onActionSearch(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            SuministroTB suministroTB = SuministroADO.GetSuministroById(txtSearch.getText().trim());
            if (suministroTB != null) {
                setLoadProducto(suministroTB.getIdSuministro(), suministroTB.getClave() + " " + suministroTB.getNombreMarca());
                fillKardexTable((short) 0, idSuministro, "", "");
            } else {
                setLoadProducto("", "");
                fillKardexTable((short) 0, idSuministro, "", "");
            }
        }
    }

    @FXML
    private void onKeyPressedSearchSuministro(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowSuministros();
        }
    }

    private void onActionFechaInicial(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (dtFechaInicial.getValue() != null && dtFechaFinal.getValue() != null) {
                fillKardexTable((short) 1, idSuministro, Tools.getDatePicker(dtFechaInicial), Tools.getDatePicker(dtFechaFinal));
            }
        }
    }

    @FXML
    private void onActionFechaFinal(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (dtFechaInicial.getValue() != null && dtFechaFinal.getValue() != null) {
                fillKardexTable((short) 1, idSuministro, Tools.getDatePicker(dtFechaInicial), Tools.getDatePicker(dtFechaFinal));
            }
        }
    }

    @FXML
    private void onActionSearchSuministro(ActionEvent event) {
        openWindowSuministros();
    }

    @FXML
    private void onKeyPressedAnterior(KeyEvent event) {
    }

    @FXML
    private void onActionAnterior(ActionEvent event) {
        if (!status) {
            if (paginacion > 1) {
                paginacion--;
                fillKardexTable((short) 0, idSuministro, "", "");
            }
        }
    }

    @FXML
    private void onKeyPressedSiguiente(KeyEvent event) {
    }

    @FXML
    private void onActionSiguiente(ActionEvent event) {
        if (!status) {
            if (paginacion < totalPaginacion) {
                paginacion++;
                fillKardexTable((short) 0, idSuministro, "", "");
            }
        }
    }

    public void setLoadProducto(String idSuministro, String value) {
        this.idSuministro = idSuministro;
        lblProducto.setText(value);
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
