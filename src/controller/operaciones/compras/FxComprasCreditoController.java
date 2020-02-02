package controller.operaciones.compras;

import controller.consultas.compras.FxAmortizarPagosController;
import controller.consultas.compras.FxComprasRealizadasController;
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
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.CompraADO;
import model.CompraCreditoTB;

public class FxComprasCreditoController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private Label lblProveedor;
    @FXML
    private TableView<CompraCreditoTB> tvList;
    @FXML
    private TableColumn<CompraCreditoTB, String> tcNumero;
    @FXML
    private TableColumn<CompraCreditoTB, String> tcCouta;
    @FXML
    private TableColumn<CompraCreditoTB, String> tcFecha;
    @FXML
    private TableColumn<CompraCreditoTB, Text> tcEstado;
    @FXML
    private TableColumn<CompraCreditoTB, String> tcMonto;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblMontoPagado;

    private FxComprasRealizadasController realizadasController;

    private String idCompra;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
        tcNumero.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcCouta.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getCuota()));
        tcFecha.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getFechaRegistro()));
        tcEstado.setCellValueFactory(new PropertyValueFactory<>("txtEstado"));
        tcMonto.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getMonto(), 2)));
    }

    public void loadData(String idCompra, String proveedor, double total) {
        this.idCompra = idCompra;
        lblProveedor.setText(proveedor);
        lblTotal.setText(Tools.roundingValue(total, 2));
        loadTableCompraCredito();
    }

    public void loadTableCompraCredito() {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ObservableList<CompraCreditoTB>> task = new Task<ObservableList<CompraCreditoTB>>() {
            @Override
            public ObservableList<CompraCreditoTB> call() {
                return CompraADO.Listar_Compra_Credito(idCompra);
            }
        };
        task.setOnScheduled(w -> {
            lblLoad.setVisible(true);
        });
        task.setOnFailed(w -> {
            lblLoad.setVisible(false);
        });
        task.setOnSucceeded(w -> {
            double montoPagado = 0;
            ObservableList<CompraCreditoTB> observableList = task.getValue();
            montoPagado = observableList.stream().filter((creditoTB) -> (creditoTB.isEstado())).map((creditoTB) -> creditoTB.getMonto()).reduce(montoPagado, (accumulator, _item) -> accumulator + _item);
            lblMontoPagado.setText(Tools.roundingValue(montoPagado, 2));
            tvList.setItems(observableList);
            lblLoad.setVisible(false);
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void eventAmortizar() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            try {
                URL url = getClass().getResource(FilesRouters.FX_AMARTIZAR_PAGOS);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxAmortizarPagosController controller = fXMLLoader.getController();
                controller.setInitValues(idCompra, tvList.getSelectionModel().getSelectedItem().getIdCompraCredito(), tvList.getSelectionModel().getSelectedItem().getMonto());
                controller.setInitAmortizarPagosController(this);
                //
                Stage stage = WindowStage.StageLoaderModal(parent, "Generar Pago", apWindow.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.show();

            } catch (IOException ex) {
                System.out.println("Controller banco" + ex.getLocalizedMessage());
            }
        } else {
            Tools.AlertMessageWarning(apWindow, "Compra al crédito", "Debe seleccionar una compra de la lista");
        }
    }

    private void eventEditar() {

    }

    @FXML
    private void onKeyPressedEditar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            eventEditar();
        }
    }

    @FXML
    private void onActionEditar(ActionEvent event) {
        eventEditar();
    }

    @FXML
    private void onKeyPressedAmortizar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            eventAmortizar();
        }
    }

    @FXML
    private void onActionAmortizar(ActionEvent event) {
        eventAmortizar();
    }

    public void setInitControllerComprasRealizadas(FxComprasRealizadasController realizadasController) {
        this.realizadasController = realizadasController;
    }

}
