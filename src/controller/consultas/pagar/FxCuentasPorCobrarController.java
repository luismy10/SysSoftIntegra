package controller.consultas.pagar;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.VentaADO;
import model.VentaTB;

public class FxCuentasPorCobrarController implements Initializable {

    @FXML
    private VBox vbWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private DatePicker dpFechaInicial;
    @FXML
    private DatePicker dpFechaFinal;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<VentaTB> tvList;
    @FXML
    private TableColumn<VentaTB, String> tcNumero;
    @FXML
    private TableColumn<VentaTB, String> tcFechaRegistro;
    @FXML
    private TableColumn<VentaTB, String> tcProveedor;
    @FXML
    private TableColumn<VentaTB, String> tcComprobante;
    @FXML
    private TableColumn<VentaTB, Label> tcEstado;
    @FXML
    private TableColumn<VentaTB, String> tcTotal;
    @FXML
    private TableColumn<VentaTB, HBox> tcOpciones;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcNumero.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcFechaRegistro.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getFechaVenta() + "\n" + cellData.getValue().getHoraVenta()));
        tcProveedor.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getClienteTB().getNumeroDocumento() + "\n" + cellData.getValue().getClienteTB().getInformacion()));
        tcComprobante.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getSerie() + "-" + cellData.getValue().getNumeracion()));
        tcEstado.setCellValueFactory(new PropertyValueFactory<>("estadoLabel"));
        tcTotal.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMonedaName() + " " + Tools.roundingValue(cellData.getValue().getTotal(), 2)));
        tcOpciones.setCellValueFactory(new PropertyValueFactory<>("hbOpciones"));

        tcNumero.prefWidthProperty().bind(tvList.widthProperty().multiply(0.05));
        tcFechaRegistro.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcProveedor.prefWidthProperty().bind(tvList.widthProperty().multiply(0.25));
        tcComprobante.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcEstado.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcTotal.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcOpciones.prefWidthProperty().bind(tvList.widthProperty().multiply(0.11));

        Tools.actualDate(Tools.getDate(), dpFechaInicial);
        Tools.actualDate(Tools.getDate(), dpFechaFinal);
    }

    public void loadTableCuentasPorCobrar() {
        fillPurchasesTable();
    }

    public void fillPurchasesTable() {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ObservableList<VentaTB>> task = new Task<ObservableList<VentaTB>>() {
            @Override
            public ObservableList<VentaTB> call() {
                return VentaADO.ListarVentasCredito();
            }
        };
        task.setOnSucceeded(w -> {
            ObservableList<VentaTB> ventaTBs = task.getValue();
            ventaTBs.forEach(e -> {
                Button btnVisualizar = (Button) e.getHbOpciones().getChildren().get(0);
                btnVisualizar.setOnAction(event -> {
                    onEventVisualizar(e.getIdVenta());
                });
                btnVisualizar.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        onEventVisualizar(e.getIdVenta());
                    }
                });
            });
            tvList.setItems(ventaTBs);
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

    private void onEventVisualizar(String idVenta) {
        try {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(FilesRouters.FX_CUENTAS_POR_COBRAR_VISUALIZAR));
            ScrollPane node = fXMLLoader.load();
            //Controlller here
            FxCuentasPorCobrarVisualizarController controller = fXMLLoader.getController();
            controller.loadData(idVenta);
            controller.setInitCuentasPorCobrar(vbPrincipal, vbContent, this);
            //
            vbContent.getChildren().clear();
            AnchorPane.setLeftAnchor(node, 0d);
            AnchorPane.setTopAnchor(node, 0d);
            AnchorPane.setRightAnchor(node, 0d);
            AnchorPane.setBottomAnchor(node, 0d);
            vbContent.getChildren().add(node);
        } catch (IOException ex) {
            Tools.println("Error en la vista Generar el cobro:" + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onKeyPressedVisualizar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                onEventVisualizar(tvList.getSelectionModel().getSelectedItem().getIdVenta());
            } else {
                Tools.AlertMessage(vbWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Venta", "Debe seleccionar una venta de la lista", false);
            }
        }
    }

    @FXML
    private void onActionVisualizar(ActionEvent event) {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            onEventVisualizar(tvList.getSelectionModel().getSelectedItem().getIdVenta());
        } else {
            Tools.AlertMessage(vbWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Venta", "Debe seleccionar una venta de la lista", false);
        }
    }

    @FXML
    private void onKeyPressedReporte(KeyEvent event) {
    }

    @FXML
    private void onActionReporte(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedRecargar(KeyEvent event) {
    }

    @FXML
    private void onActionRecargar(ActionEvent event) {
    }

    @FXML
    private void onActionFechaInicial(ActionEvent event) {
    }

    @FXML
    private void onActionFechaFinal(ActionEvent event) {
    }

    @FXML
    private void onActionSearch(ActionEvent event) {
    }

    @FXML
    private void onKeyReleasedSeach(KeyEvent event) {
    }

    @FXML
    private void onMouseClickList(MouseEvent event) {
    }

    public void setContent(AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }
}
