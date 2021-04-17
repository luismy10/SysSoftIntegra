package controller.operaciones.pedidos;

import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.PedidoDetalleTB;
import model.SuministroTB;

public class FxPedidosModalCantidadController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private Label lblArticulo;
    @FXML
    private TextField txtCantidad;

    private SuministroTB suministroTB;

    private FxPedidosController pedidosController;

    private double oldCantidad;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
    }

    public void initComponents(SuministroTB suministroTB) {
        this.suministroTB = suministroTB;
        this.oldCantidad = 1;
        lblArticulo.setText(suministroTB.getNombreMarca());
    }

    private void onEventAceptar() {
        double cantidadActual = Tools.isNumeric(txtCantidad.getText().trim())
                ? (Double.parseDouble(txtCantidad.getText()) <= 0 ? oldCantidad : Double.parseDouble(txtCantidad.getText()))
                : oldCantidad;

        PedidoDetalleTB pedidoDetalleTB = new PedidoDetalleTB();
        pedidoDetalleTB.setIdSuministro(suministroTB.getIdSuministro());
        pedidoDetalleTB.setSuministroTB(new SuministroTB(suministroTB.getClave(), suministroTB.getNombreMarca()));
        pedidoDetalleTB.setExistencia(suministroTB.getCantidad());
        pedidoDetalleTB.setStock(suministroTB.getStockMinimo() + "/" + suministroTB.getStockMaximo());
        pedidoDetalleTB.setCosto(suministroTB.getCostoCompra());

        Button button = new Button();
        button.getStyleClass().add("buttonDark");
        ImageView view = new ImageView(new Image("/view/image/remove.png"));
        view.setFitWidth(22);
        view.setFitHeight(22);
        button.setGraphic(view);
        pedidoDetalleTB.setBtnQuitar(button);

        TextField textField = new TextField(String.valueOf(cantidadActual));
        textField.getStyleClass().add("text-field-normal");
        textField.setPromptText("0.00");
        textField.setPrefWidth(220);
        textField.setPrefHeight(30);
        textField.setOnKeyReleased(event -> {
            if (Tools.isNumeric(textField.getText().trim())) {
                double cantidad = Double.parseDouble(textField.getText().trim());
                double importe = cantidad * pedidoDetalleTB.getCosto();
                pedidoDetalleTB.setImporte(importe);
                pedidosController.getTvList().refresh();
            }
        });
        textField.setOnKeyTyped(event -> {
            char c = event.getCharacter().charAt(0);
            if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
                event.consume();
            }
            if (c == '.' && textField.getText().contains(".")) {
                event.consume();
            }
        });

        pedidoDetalleTB.setTxtCantidad(textField);

        double importe = Tools.isNumeric(textField.getText().trim()) ? Double.parseDouble(textField.getText().trim()) * pedidoDetalleTB.getCosto() : 0;

        pedidoDetalleTB.setImporte(importe);
        pedidosController.getAddArticulo(pedidoDetalleTB);
        Tools.Dispose(apWindow);
    }

    @FXML
    private void onKeyPressedMas(KeyEvent event) {
    }

    @FXML
    private void onActionMas(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedMenos(KeyEvent event) {
    }

    @FXML
    private void onActionMenos(ActionEvent event) {
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
