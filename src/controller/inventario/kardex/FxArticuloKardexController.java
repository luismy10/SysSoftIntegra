package controller.inventario.kardex;

import controller.inventario.articulo.FxArticuloListaController;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.ArticuloADO;
import model.ArticuloTB;
import model.KardexADO;
import model.KardexTB;

public class FxArticuloKardexController implements Initializable {

    @FXML
    private HBox hbWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private TextField txtSearch;
    @FXML
    private Label lblArticulo;
    @FXML
    private TableView<KardexTB> tvList;
    @FXML
    private TableColumn<KardexTB, String> tcFecha;
    @FXML
    private TableColumn<KardexTB, String> tcDetalle;
    @FXML
    private TableColumn<KardexTB, String> tcC1;
    @FXML
    private TableColumn<KardexTB, String> tcCo1;
    @FXML
    private TableColumn<KardexTB, String> tcCe1;
    @FXML
    private TableColumn<KardexTB, String> tcC2;
    @FXML
    private TableColumn<KardexTB, String> tcCo2;
    @FXML
    private TableColumn<KardexTB, String> tcCe2;
    @FXML
    private TableColumn<KardexTB, String> tcC3;
    @FXML
    private TableColumn<KardexTB, String> tcCo3;
    @FXML
    private TableColumn<KardexTB, String> tcCe3;
    @FXML
    private Label lblCantidadTotal;
    @FXML
    private Label lblCostoPromedio;
    @FXML
    private Label lblCostoTotal;

    private AnchorPane vbPrincipal;

    private boolean validationSearch;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcFecha.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getFecha() + "\n" + cellData.getValue().getHora()));
        tcDetalle.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMovimientoName() + "\n" + cellData.getValue().getDetalle().toUpperCase()));

        tcC1.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getTipo() == 1 ? Tools.roundingValue(cellData.getValue().getCantidad(), 2) : "")
        );
        tcCo1.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getTipo() == 1 ? Tools.roundingValue(cellData.getValue().getcUnitario(), 2) : "")
        );
        tcCe1.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getTipo() == 1 ? Tools.roundingValue(cellData.getValue().getcTotal(), 2) : "")
        );

        tcC2.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getTipo() == 1 ? "" : Tools.roundingValue(cellData.getValue().getCantidad(), 2))
        );
        tcCo2.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getTipo() == 1 ? "" : Tools.roundingValue(cellData.getValue().getcUnitario(), 2))
        );
        tcCe2.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getTipo() == 1 ? "" : Tools.roundingValue(cellData.getValue().getcTotal(), 2))
        );

        tcC3.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getCantidadTotal(), 2)));
        tcCo3.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getcUnictarioTotal(), 2)));
        tcCe3.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getcTotalTotal(), 2)));

        tcFecha.prefWidthProperty().bind(tvList.widthProperty().multiply(0.10));
        tcDetalle.prefWidthProperty().bind(tvList.widthProperty().multiply(0.16));

        tcC1.prefWidthProperty().bind(tvList.widthProperty().multiply(0.08));
        tcCo1.prefWidthProperty().bind(tvList.widthProperty().multiply(0.08));
        tcCe1.prefWidthProperty().bind(tvList.widthProperty().multiply(0.08));

        tcC2.prefWidthProperty().bind(tvList.widthProperty().multiply(0.08));
        tcCo2.prefWidthProperty().bind(tvList.widthProperty().multiply(0.08));
        tcCe2.prefWidthProperty().bind(tvList.widthProperty().multiply(0.08));

        tcC3.prefWidthProperty().bind(tvList.widthProperty().multiply(0.08));
        tcCo3.prefWidthProperty().bind(tvList.widthProperty().multiply(0.08));
        tcCe3.prefWidthProperty().bind(tvList.widthProperty().multiply(0.08));

        validationSearch = false;

    }

    private void openWindowArticulos() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_ARTICULO_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxArticuloListaController controller = fXMLLoader.getController();
            controller.setInitArticuloKardexController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Artículo", hbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding((WindowEvent WindowEvent) -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();
            controller.loadElements((short)1,"");
        } catch (IOException ex) {
            System.out.println("Error en la vista ajustes:" + ex.getLocalizedMessage());
        }
    }

    public void fillKardexTable(String value) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<ObservableList<KardexTB>> task = new Task<ObservableList<KardexTB>>() {
            @Override
            public ObservableList<KardexTB> call() {
                return KardexADO.List_Kardex_By_Id_Articulo(value);
            }
        };

        task.setOnSucceeded((WorkerStateEvent e) -> {
            tvList.setItems(task.getValue());
            lblLoad.setVisible(false);
            validationSearch = false;
            if (!tvList.getItems().isEmpty()) {
                lblCantidadTotal.setText(Tools.roundingValue(tvList.getItems().get(tvList.getItems().size() - 1).getCantidadTotal(), 2));
                lblCostoPromedio.setText(Tools.roundingValue(tvList.getItems().get(tvList.getItems().size() - 1).getcUnictarioTotal(), 2));
                lblCostoTotal.setText(Tools.roundingValue(tvList.getItems().get(tvList.getItems().size() - 1).getcTotalTotal(), 2));
            }
        });
        task.setOnFailed((WorkerStateEvent event) -> {
            lblLoad.setVisible(false);
            validationSearch = false;
        });
        task.setOnScheduled((WorkerStateEvent event) -> {
            lblLoad.setVisible(true);
            validationSearch = true;
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    @FXML
    private void onActionSearch(ActionEvent event) {
        if (!validationSearch) {
//            ArticuloTB articuloTB = ArticuloADO.GetArticulosById(txtSearch.getText().trim());
//            if (articuloTB != null) {
//                setLoadArticulo(articuloTB.getClave() + " " + articuloTB.getNombreMarca());
//                fillKardexTable(txtSearch.getText().trim());
//            }
        }
    }

    @FXML
    private void onKeyPressedSearchArticulo(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowArticulos();
        }
    }

    @FXML
    private void onActionSearchArticulo(ActionEvent event) {
        openWindowArticulos();
    }

    public void setLoadArticulo(String value) {
        lblArticulo.setText(value);
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
