package controller.operaciones.pedidos;

import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.PedidoDetalleTB;
import model.SuministroTB;

public class FxPedidosModalController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private Label lblArticulo;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TextField txtCosto;
    @FXML
    private TextField txtDescuento;
    @FXML
    private ComboBox<ImpuestoTB> cbImpuesto;

    private SuministroTB suministroTB;

    private FxPedidosController pedidosController;

    private double oldCantidad;

    private double oldCosto;

    private double oldDescuento;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
        cbImpuesto.getItems().clear();
        ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> {
            cbImpuesto.getItems().add(new ImpuestoTB(e.getIdImpuesto(), e.getNombre(), e.getValor(), e.isPredeterminado()));
        });
        for (int i = 0; i < cbImpuesto.getItems().size(); i++) {
            if (cbImpuesto.getItems().get(i).isPredeterminado()) {
                cbImpuesto.getSelectionModel().select(i);
                break;
            }
        }
    }

    public void initComponents(SuministroTB suministroTB) {
        this.suministroTB = suministroTB;
        this.oldCantidad = 1;
        this.oldCosto = suministroTB.getCostoCompra();
        this.oldDescuento = 0;
        txtCantidad.setText(Tools.roundingValue(oldCantidad, 2));
        txtCosto.setText(Tools.roundingValue(oldCosto, 2));
        txtDescuento.setText(Tools.roundingValue(oldDescuento, 2));
        lblArticulo.setText(suministroTB.getNombreMarca());
    }

    private void onEventAceptar() {
        if (cbImpuesto.getSelectionModel().getSelectedIndex() >= 0) {
            double cantidadActual = Tools.isNumeric(txtCantidad.getText().trim())
                    ? (Double.parseDouble(txtCantidad.getText()) <= 0 ? oldCantidad : Double.parseDouble(txtCantidad.getText()))
                    : oldCantidad;

            double costoActual = Tools.isNumeric(txtCosto.getText().trim())
                    ? (Double.parseDouble(txtCosto.getText()) <= 0 ? oldCosto : Double.parseDouble(txtCosto.getText()))
                    : oldCosto;

            double descuentoActual = Tools.isNumeric(txtDescuento.getText().trim())
                    ? (Double.parseDouble(txtDescuento.getText()) < 0 ? oldDescuento : Double.parseDouble(txtDescuento.getText()))
                    : oldDescuento;

            PedidoDetalleTB pedidoDetalleTB = new PedidoDetalleTB();
            pedidoDetalleTB.setIdSuministro(suministroTB.getIdSuministro());
            pedidoDetalleTB.setSuministroTB(new SuministroTB(suministroTB.getClave(), suministroTB.getNombreMarca()));
            pedidoDetalleTB.setExistencia(suministroTB.getCantidad());
            pedidoDetalleTB.setStock(suministroTB.getStockMinimo() + "/" + suministroTB.getStockMaximo());
            pedidoDetalleTB.setCantidad(cantidadActual);
            pedidoDetalleTB.setCosto(costoActual);
            pedidoDetalleTB.setDescuento(descuentoActual);
            pedidoDetalleTB.setIdImpuesto(cbImpuesto.getSelectionModel().getSelectedItem().getIdImpuesto());
            pedidoDetalleTB.setImpuesto(cbImpuesto.getSelectionModel().getSelectedItem().getValor());
            double descuento = pedidoDetalleTB.getDescuento();
            double costoDescuento = pedidoDetalleTB.getCosto() - descuento;
            pedidoDetalleTB.setImporte(costoDescuento * pedidoDetalleTB.getCantidad());

            Button button = new Button("X");
            button.getStyleClass().add("buttonDark");
            button.setOnAction(e -> {
                pedidosController.getTvList().getItems().remove(pedidoDetalleTB);
                pedidosController.calculateTotales();
            });
            button.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    pedidosController.getTvList().getItems().remove(pedidoDetalleTB);
                    pedidosController.calculateTotales();
                }
                e.consume();
            });

            pedidoDetalleTB.setBtnQuitar(button);
            pedidosController.getAddArticulo(pedidoDetalleTB);
            Tools.Dispose(apWindow);
        } else {
            Tools.AlertMessageWarning(apWindow, "Producto", "Seleccione un impuesto para continuar.");
        }
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventAceptar();
        }
    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        onEventAceptar();
    }

    @FXML
    private void onKeyPressedCerrar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(apWindow);
        }
    }

    @FXML
    private void onActionCerrar(ActionEvent event) {
        Tools.Dispose(apWindow);
    }

    @FXML
    private void onKeyTypedCantidad(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
            event.consume();
        }
        if (c == '.' && txtCantidad.getText().contains(".")) {
            event.consume();
        }
    }

    public TextField getTxtCantidad() {
        return txtCantidad;
    }

    public void setInitPedidosController(FxPedidosController pedidosController) {
        this.pedidosController = pedidosController;
    }

}
