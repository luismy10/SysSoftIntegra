package controller.operaciones.pedidos;

import controller.menus.FxPrincipalController;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.PedidoADO;
import model.PedidoTB;

public class FxPedidosDetalleController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private ScrollPane spBody;
    @FXML
    private Text lblNumero;
    @FXML
    private Text lblFechaVenta;
    @FXML
    private Text lblProveedor;
    @FXML
    private Text lblCelularTelefono;
    @FXML
    private Text lbCorreoElectronico;
    @FXML
    private Text lblDireccion;
    @FXML
    private Text lblObservaciones;
    @FXML
    private Text lblVendedor;
    @FXML
    private Label lblLoad;
    @FXML
    private HBox hbLoad;
    @FXML
    private Label lblMessageLoad;
    @FXML
    private Button btnAceptarLoad;

    private FxPrincipalController fxPrincipalController;

    private FxPedidosRealizadosController pedidosRealizadosController;

    private PedidoTB pedidoTB;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void loadComponents(String idPedido) {
        ExecutorService executor = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<Object> task = new Task<Object>() {
            @Override
            protected Object call() {
                return PedidoADO.CargarPedidoById(idPedido);
            }
        };
        task.setOnScheduled(e -> {
            lblLoad.setVisible(true);
            spBody.setDisable(true);
            hbLoad.setVisible(true);
            lblMessageLoad.setText("Cargando datos...");
            lblMessageLoad.setTextFill(Color.web("#ffffff"));
            btnAceptarLoad.setVisible(false);
        });
        task.setOnFailed(e -> {
            lblLoad.setVisible(false);
            lblMessageLoad.setText(task.getException().getLocalizedMessage());
            lblMessageLoad.setTextFill(Color.web("#ff6d6d"));
            btnAceptarLoad.setVisible(true);
        });
        task.setOnSucceeded(e -> {
            Object object = task.getValue();
            if (object instanceof PedidoTB) {
                pedidoTB = (PedidoTB) object;
                lblNumero.setText(idPedido);
                lblFechaVenta.setText(idPedido);
                lblProveedor.setText(idPedido);
                lblCelularTelefono.setText(idPedido);
                lbCorreoElectronico.setText(idPedido);
                lblDireccion.setText(idPedido);
                lblObservaciones.setText(idPedido);
                lblVendedor.setText(idPedido);
                
                spBody.setDisable(false);
                hbLoad.setVisible(false);
            } else {
                lblMessageLoad.setText((String) object);
                lblMessageLoad.setTextFill(Color.web("#ff6d6d"));
                btnAceptarLoad.setVisible(true);
            }
            lblLoad.setVisible(false);
        });
        executor.execute(task);
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        fxPrincipalController.getVbContent().getChildren().remove(apWindow);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(pedidosRealizadosController.getVbWindow(), 0d);
        AnchorPane.setTopAnchor(pedidosRealizadosController.getVbWindow(), 0d);
        AnchorPane.setRightAnchor(pedidosRealizadosController.getVbWindow(), 0d);
        AnchorPane.setBottomAnchor(pedidosRealizadosController.getVbWindow(), 0d);
        fxPrincipalController.getVbContent().getChildren().add(pedidosRealizadosController.getVbWindow());
    }

    @FXML
    private void onKeyPressedReporte(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionReporte(ActionEvent event) {

    }

    @FXML
    private void onKeyPressedTicket(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionTicket(ActionEvent event) {

    }

    @FXML
    private void onKeyPressedAceptarLoad(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionAceptarLoad(ActionEvent event) {

    }

    public void setInitPedidosRealizadasController(FxPedidosRealizadosController pedidosRealizadosController, FxPrincipalController fxPrincipalController) {
        this.pedidosRealizadosController = pedidosRealizadosController;
        this.fxPrincipalController = fxPrincipalController;
    }

}
