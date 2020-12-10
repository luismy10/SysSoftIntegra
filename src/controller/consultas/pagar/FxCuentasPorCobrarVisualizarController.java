package controller.consultas.pagar;

import controller.configuracion.impresoras.FxOpcionesImprimirController;
import controller.operaciones.compras.FxAmortizarPagosController;
import controller.operaciones.venta.FxVentaAbonoProcesoController;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.CompraCreditoTB;
import model.VentaADO;
import model.VentaCreditoTB;
import model.VentaTB;

public class FxCuentasPorCobrarVisualizarController implements Initializable {

    @FXML
    private ScrollPane spWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private Label lblCliente;
    @FXML
    private Label lblComprobante;
    @FXML
    private Label lblEstado;
    @FXML
    private Label lblTelefonoCelular;
    @FXML
    private Label lblDireccion;
    @FXML
    private Label lblEmail;
    @FXML
    private GridPane gpList;
    @FXML
    private Label lblMontoTotal;
    @FXML
    private Label lblMontoPagado;
    @FXML
    private Label lblDiferencia;
    @FXML
    private Label lblObservacion;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    private FxCuentasPorCobrarController cuentasPorCobrarController;

    private VentaTB ventaTB;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void loadData(String idVenta) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<VentaTB> task = new Task<VentaTB>() {
            @Override
            public VentaTB call() {
                return VentaADO.ListarVentasDetalleCredito(idVenta);
            }
        };
        task.setOnSucceeded(w -> {
            ventaTB = task.getValue();
            if (ventaTB != null) {
                lblCliente.setText(ventaTB.getClienteTB().getNumeroDocumento() + " - " + ventaTB.getClienteTB().getInformacion());
                lblTelefonoCelular.setText(ventaTB.getClienteTB().getCelular());
                lblDireccion.setText(ventaTB.getClienteTB().getDireccion());
                lblEmail.setText(ventaTB.getClienteTB().getEmail());
                lblComprobante.setText(ventaTB.getSerie() + " - " + ventaTB.getNumeracion());
                lblEstado.setText(ventaTB.getEstadoName());
                lblEstado.setTextFill(ventaTB.getEstado() == 3 ? Color.web("#ee4637") : ventaTB.getEstado() == 2 ? Color.web("#eab120") : Color.web("#42bf59"));
                lblMontoTotal.setText(Tools.roundingValue(ventaTB.getTotal(), 2));
                fillVentasDetalleTable();
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

    private void fillVentasDetalleTable() {
        double montoPagado = 0;
        for (int i = 0; i < ventaTB.getVentaCreditoTBs().size(); i++) {
            gpList.add(addElementGridPane("l1" + (i + 1), ventaTB.getVentaCreditoTBs().get(i).getId() + "", Pos.CENTER, null), 0, (i + 1));
            gpList.add(addElementGridPane("l2" + (i + 1), ventaTB.getVentaCreditoTBs().get(i).getIdVentaCredito(), Pos.CENTER, null), 1, (i + 1));
            gpList.add(addElementGridPane("l3" + (i + 1), ventaTB.getVentaCreditoTBs().get(i).getFechaPago(), Pos.CENTER, null), 2, (i + 1));
            gpList.add(addElementGridPane("l4" + (i + 1), ventaTB.getVentaCreditoTBs().get(i).getEstado() + "", Pos.CENTER, null), 3, (i + 1));
            gpList.add(addElementGridPane("l5" + (i + 1), Tools.roundingValue(ventaTB.getVentaCreditoTBs().get(i).getMonto(), 2), Pos.CENTER, null), 4, (i + 1));
            gpList.add(addElementGridPane("l6" + (i + 1), ventaTB.getVentaCreditoTBs().get(i).getObservacion(), Pos.CENTER, null), 5, (i + 1));
            gpList.add(addElementGridPane("l7" + (i + 1), "", Pos.CENTER, ventaTB.getVentaCreditoTBs().get(i).getBtnImprimir()), 6, (i + 1));
            montoPagado = ventaTB.getVentaCreditoTBs().get(i).getMonto();            
        }        
        lblMontoPagado.setText(Tools.roundingValue(montoPagado, 2));
        lblDiferencia.setText(Tools.roundingValue(ventaTB.getTotal()-montoPagado, 2));
    }

    private Label addElementGridPane(String id, String nombre, Pos pos, Node node) {
        Label label = new Label(nombre);
        label.setId(id);
        label.setGraphic(node);
        label.setStyle("-fx-text-fill:#020203;-fx-background-color: #dddddd;-fx-padding: 0.4166666666666667em 0.8333333333333334em 0.4166666666666667em 0.8333333333333334em;");
        label.getStyleClass().add("labelRoboto13");
        label.setAlignment(pos);
        label.setWrapText(true);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        return label;
    }

    private void onEventAmortizar() {
        if (ventaTB != null) {
            try {
                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                URL url = getClass().getResource(FilesRouters.FX_VENTA_ABONO_PROCESO);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxVentaAbonoProcesoController controller = fXMLLoader.getController();
                controller.setInitLoadVentaAbono(ventaTB.getIdVenta());
                controller.setInitVentaAbonarController(this);
                //
                Stage stage = WindowStage.StageLoaderModal(parent, "Generar Cobro", spWindow.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
                stage.show();
            } catch (IOException ex) {
                System.out.println("Controller banco" + ex.getLocalizedMessage());
            }
        } else {
            Tools.AlertMessageWarning(spWindow, "Generar Cobro", "No se puede habrir el modal por error en carga de datos.");
        }
    }

    public void openModalImpresion(String idVenta,String idVentaCredito) {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_OPCIONES_IMPRIMIR);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxOpcionesImprimirController controller = fXMLLoader.getController();
            controller.loadDataCuentaPorCobrar(idVenta, idVentaCredito);
            controller.setInitOpcionesImprimirController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Imprimir", spWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
        } catch (IOException ex) {
            System.out.println("Controller banco" + ex.getLocalizedMessage());
        }
    }
    
    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        vbContent.getChildren().remove(spWindow);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(cuentasPorCobrarController.getVbWindow(), 0d);
        AnchorPane.setTopAnchor(cuentasPorCobrarController.getVbWindow(), 0d);
        AnchorPane.setRightAnchor(cuentasPorCobrarController.getVbWindow(), 0d);
        AnchorPane.setBottomAnchor(cuentasPorCobrarController.getVbWindow(), 0d);
        vbContent.getChildren().add(cuentasPorCobrarController.getVbWindow());
    }

    @FXML
    private void onKeyPressedCobrar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventAmortizar();
        }
    }

    @FXML
    private void onActionCobrar(ActionEvent event) {
        onEventAmortizar();
    }

    public void setInitCuentasPorCobrar(AnchorPane vbPrincipal, AnchorPane vbContent, FxCuentasPorCobrarController cuentasPorCobrarController) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
        this.cuentasPorCobrarController = cuentasPorCobrarController;
    }

}
