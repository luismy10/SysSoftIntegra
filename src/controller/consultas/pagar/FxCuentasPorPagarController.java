package controller.consultas.pagar;

import controller.reporte.FxReportViewController;
import controller.tools.FilesRouters;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.awt.HeadlessException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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
import javafx.stage.Stage;
import model.CompraADO;
import model.CompraTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

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

    private AnchorPane vbContent;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcNumero.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcFechaRegistro.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getFechaCompra() + "\n" + cellData.getValue().getHoraCompra()));
        tcProveedor.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getProveedorTB().getNumeroDocumento() + "\n" + cellData.getValue().getProveedorTB().getRazonSocial()));
        tcComprobante.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getSerie() + "-" + cellData.getValue().getNumeracion()));
        tcEstado.setCellValueFactory(new PropertyValueFactory<>("estadoLabel"));
        tcTotal.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMonedaNombre() + " " + Tools.roundingValue(cellData.getValue().getTotal(), 2)));
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
                    onEventVisualizar(f.getIdCompra(), f.getTotal());

                });
                btnVisualizar.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        onEventVisualizar(f.getIdCompra(), f.getTotal());
                    }
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

    private void onEventVisualizar(String idCompra, double total) {
        try {
            FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(FilesRouters.FX_CUENTAS_POR_PAGAR_VISUALIZAR));
            ScrollPane node = fXMLLoader.load();
            //Controlller here
            FxCuentasPorPagarVisualizarController controller = fXMLLoader.getController();
            controller.loadData(idCompra, total);
            controller.setInitCuentasPorPagar(vbPrincipal, vbContent, this);
            //
            vbContent.getChildren().clear();
            AnchorPane.setLeftAnchor(node, 0d);
            AnchorPane.setTopAnchor(node, 0d);
            AnchorPane.setRightAnchor(node, 0d);
            AnchorPane.setBottomAnchor(node, 0d);
            vbContent.getChildren().add(node);
        } catch (IOException ex) {
            Tools.println("Error en la vista Generar pago:" + ex.getLocalizedMessage());
        }
    }

    private void onEventReporte() {
        try {

            if (tvList.getItems().isEmpty()) {
                Tools.AlertMessageWarning(vbWindow, "Reporte Cuentas por Pagar", "No hay páginas para mostrar en el reporte.");
                return;
            }
            
            InputStream imgInputStream = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            if(Session.COMPANY_IMAGE != null){
                imgInputStream = new ByteArrayInputStream(Session.COMPANY_IMAGE);
            }
            
            InputStream dir = getClass().getResourceAsStream("/report/CuentasPorPagar.jasper");

           
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(dir);
            Map map = new HashMap();
            map.put("LOGO", imgInputStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JRBeanCollectionDataSource(tvList.getItems()));

            URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxReportViewController controller = fXMLLoader.getController();
            controller.setJasperPrint(jasperPrint);
            controller.show();
            Stage stage = WindowStage.StageLoader(parent, "Cuentas por pagar");
            stage.setResizable(true);
            stage.show();
            stage.requestFocus();

        } catch (HeadlessException | JRException | IOException ex) {
            Tools.AlertMessageError(vbWindow, "Reporte de Inventario", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    private void onEventRecargar() {
        fillPurchasesTable("", "", "", (short) 0);
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

    @FXML
    private void onKeyPressedVisualizar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                onEventVisualizar(tvList.getSelectionModel().getSelectedItem().getIdCompra(), tvList.getSelectionModel().getSelectedItem().getTotal());
            } else {
                Tools.AlertMessage(vbWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Compra", "Debe seleccionar una compra de la lista", false);
            }
        }
    }

    @FXML
    private void onActionVisualizar(ActionEvent event) {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            onEventVisualizar(tvList.getSelectionModel().getSelectedItem().getIdCompra(), tvList.getSelectionModel().getSelectedItem().getTotal());
        } else {
            Tools.AlertMessage(vbWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Compra", "Debe seleccionar una compra de la lista", false);
        }
    }

    @FXML
    private void onKeyPressedReporte(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventReporte();
        }
    }

    @FXML
    private void onActionReporte(ActionEvent event) {
        onEventReporte();
    }

    @FXML
    private void onKeyPressedRecargar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventRecargar();
        }
    }

    @FXML
    private void onActionRecargar(ActionEvent event) {
        onEventRecargar();
    }

    @FXML
    private void onMouseClickList(MouseEvent event) {
        if (event.getClickCount() == 2) {
            if (!tvList.getItems().isEmpty()) {
                onEventVisualizar(tvList.getSelectionModel().getSelectedItem().getIdCompra(), tvList.getSelectionModel().getSelectedItem().getTotal());
            }
        }
    }

    public VBox getVbWindow() {
        return vbWindow;
    }

    public void setContent(AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
