package controller.consultas.pagar;

import controller.operaciones.compras.FxComprasCreditoController;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.CompraADO;
import model.CompraTB;

public class FxCuentasPorPagarController implements Initializable {

    @FXML
    private VBox vbWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private TableView<CompraTB> tvList;
    @FXML
    private TableColumn<CompraTB, String> tcNumero;
    @FXML
    private TableColumn<CompraTB, String> tcFechaRegistro;
    @FXML
    private TableColumn<CompraTB, String> tcProveedor;
    @FXML
    private TableColumn<CompraTB, String> tcComprobante;
    @FXML
    private TableColumn<CompraTB, String> tcTotal;
    @FXML
    private TableColumn<CompraTB, Label> tcEstado;
    @FXML
    private TableColumn<CompraTB, HBox> tcOpciones;
    @FXML
    private TextField txtSearch;
    @FXML
    private DatePicker dpFechaInicial;
    @FXML
    private DatePicker dpFechaFinal;

    private AnchorPane vbPrincipal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcNumero.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcFechaRegistro.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getFechaCompra() + "\n" + cellData.getValue().getHoraCompra()));
        tcProveedor.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getProveedorTB().getNumeroDocumento() + "\n" + cellData.getValue().getProveedorTB().getRazonSocial()));
        tcComprobante.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getSerie() + "-" + cellData.getValue().getNumeracion()));
        tcEstado.setCellValueFactory(new PropertyValueFactory<>("estadoLabel"));
        tcTotal.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMonedaNombre() + " " + Tools.roundingValue(cellData.getValue().getTotal(), 2)));
        tcOpciones.setCellValueFactory(new PropertyValueFactory<>("hbOpciones"));

        tcNumero.prefWidthProperty().bind(tvList.widthProperty().multiply(0.06));
        tcFechaRegistro.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcProveedor.prefWidthProperty().bind(tvList.widthProperty().multiply(0.25));
        tcComprobante.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcEstado.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcTotal.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcOpciones.prefWidthProperty().bind(tvList.widthProperty().multiply(0.11));

        Tools.actualDate(Tools.getDate(), dpFechaInicial);
        Tools.actualDate(Tools.getDate(), dpFechaFinal);
    }

    public void fillPurchasesTable(String search, String fechaInicio, String fechaFinal, short opcion) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ObservableList<CompraTB>> task = new Task<ObservableList<CompraTB>>() {
            @Override
            public ObservableList<CompraTB> call() {
                return CompraADO.ListComprasCredito(search, fechaInicio, fechaFinal, opcion);
            }
        };
        task.setOnSucceeded(w -> {
            ObservableList<CompraTB> observableList = task.getValue();
            observableList.forEach(f -> {
                Button btnVisualizar = (Button) f.getHbOpciones().getChildren().get(0);
                btnVisualizar.setOnAction(event -> {

                });
                btnVisualizar.setOnKeyPressed(event -> {

                });
                Button btnAbonar = (Button) f.getHbOpciones().getChildren().get(1);
                btnAbonar.setOnAction(event -> {
                    openWindowGenerarPago(f.getIdCompra(), f.getProveedorTB().getRazonSocial(), f.getTotal());
                });
                btnAbonar.setOnKeyPressed(event -> {
                    openWindowGenerarPago(f.getIdCompra(), f.getProveedorTB().getRazonSocial(), f.getTotal());
                });
            });
            tvList.setItems(observableList);
            if (!tvList.getItems().isEmpty()) {
                tvList.getSelectionModel().select(0);
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

    private void openWindowGenerarPago(String idCompra, String nombreProveedor, double total) {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_COMPRAS_CREDITO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxComprasCreditoController controller = fXMLLoader.getController();
            controller.loadData(idCompra, nombreProveedor, total);
            controller.setInitControllerComprasCredito(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Cuenta por pagar", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();

        } catch (IOException ex) {
            System.out.println("Controller banco" + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onActionSearch(ActionEvent event) {
        if (!tvList.getItems().isEmpty()) {
            tvList.getSelectionModel().select(0);
            tvList.requestFocus();
        }
    }

    @FXML
    private void onKeyReleasedSeach(KeyEvent event) {
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
            if (!lblLoad.isVisible()) {
                fillPurchasesTable(txtSearch.getText().trim(), "", "", (short) 0);
            }
        }
    }

    @FXML
    private void onKeyPressedAbonar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                openWindowGenerarPago(tvList.getSelectionModel().getSelectedItem().getIdCompra(), tvList.getSelectionModel().getSelectedItem().getProveedorTB().getRazonSocial(), tvList.getSelectionModel().getSelectedItem().getTotal());
            } else {
                Tools.AlertMessage(vbWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Compra", "Debe seleccionar una compra de la lista", false);
            }
        }
    }

    @FXML
    private void onActionAbonar(ActionEvent event) {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            openWindowGenerarPago(tvList.getSelectionModel().getSelectedItem().getIdCompra(), tvList.getSelectionModel().getSelectedItem().getProveedorTB().getRazonSocial(), tvList.getSelectionModel().getSelectedItem().getTotal());
        } else {
            Tools.AlertMessage(vbWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Compra", "Debe seleccionar una compra de la lista", false);
        }
    }

    @FXML
    private void onActionFechaInicial(ActionEvent event) {
        if (dpFechaInicial.getValue() != null && dpFechaFinal.getValue() != null) {
            if (!lblLoad.isVisible()) {
                fillPurchasesTable("", Tools.getDatePicker(dpFechaInicial), Tools.getDatePicker(dpFechaFinal), (short) 1);
            }
        }
    }

    @FXML
    private void onActionFechaFinal(ActionEvent event) {
        if (dpFechaInicial.getValue() != null && dpFechaFinal.getValue() != null) {
            if (!lblLoad.isVisible()) {
                fillPurchasesTable("", Tools.getDatePicker(dpFechaInicial), Tools.getDatePicker(dpFechaFinal), (short) 1);
            }
        }
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
