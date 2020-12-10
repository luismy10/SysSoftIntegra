package controller.consultas.pagar;

import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
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
    private Label lblTotal;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    private FxCuentasPorCobrarController cuentasPorCobrarController;

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
            VentaTB ventaTB = task.getValue();
            if (ventaTB != null) {
                lblCliente.setText(ventaTB.getClienteTB().getNumeroDocumento() + " - " + ventaTB.getClienteTB().getInformacion());
                lblTelefonoCelular.setText(ventaTB.getClienteTB().getCelular());
                lblDireccion.setText(ventaTB.getClienteTB().getDireccion());
                lblEmail.setText(ventaTB.getClienteTB().getEmail());
                lblComprobante.setText(ventaTB.getSerie() + " - " + ventaTB.getNumeracion());
                lblEstado.setText(ventaTB.getEstadoName());
                lblEstado.setTextFill(ventaTB.getEstado()==3?Color.web("#F1948A"):ventaTB.getEstado()==2?Color.web("#F9E434"):Color.web("#c6e2cc"));
                lblTotal.setText(Tools.roundingValue(ventaTB.getTotal(), 2));
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

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
    }

    public void setInitCuentasPorCobrar(AnchorPane vbPrincipal, AnchorPane vbContent, FxCuentasPorCobrarController cuentasPorCobrarController) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
        this.cuentasPorCobrarController = cuentasPorCobrarController;
    }

}
