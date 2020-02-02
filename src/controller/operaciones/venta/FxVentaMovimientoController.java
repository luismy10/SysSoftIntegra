package controller.operaciones.venta;

import com.sun.javafx.scene.control.skin.TextAreaSkin;
import controller.tools.Session;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.MovimientoCajaADO;
import model.MovimientoCajaTB;

public class FxVentaMovimientoController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private ComboBox<String> cbMovimiento;
    @FXML
    private TextField txtMonto;
    @FXML
    private TextArea txtComentario;

    private FxVentaEstructuraController ventaEstructuraController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
        cbMovimiento.getItems().addAll("Entrada", "Salida");
    }

    private void saveMovimiento() {
        if (cbMovimiento.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(window, "Ventas", "Seleccione el movimiento a realizar.");
            cbMovimiento.requestFocus();
        } else if (!Tools.isNumeric(txtMonto.getText().trim())) {
            Tools.AlertMessageWarning(window, "Ventas", "El monto ingreso no tiene el formato correcto.");
            txtMonto.requestFocus();
        } else if (txtComentario.getText().isEmpty()) {
            Tools.AlertMessageWarning(window, "Ventas", "Ingrese un comentario.");
            txtComentario.requestFocus();
        } else {
            MovimientoCajaTB movimientoCaja = new MovimientoCajaTB();
            movimientoCaja.setIdCaja(Session.CAJA_ID);
            movimientoCaja.setIdUsuario(Session.USER_ID);
            movimientoCaja.setFechaMovimiento(Tools.getDate());
            movimientoCaja.setComentario(txtComentario.getText().trim());
            movimientoCaja.setMovimiento(cbMovimiento.getSelectionModel().getSelectedIndex() == 0 ? "ENTR" : "SALI");
            movimientoCaja.setEntrada(cbMovimiento.getSelectionModel().getSelectedIndex() == 0
                    ? Double.parseDouble(txtMonto.getText()) : 0);
            movimientoCaja.setSalidas(cbMovimiento.getSelectionModel().getSelectedIndex() == 0
                    ? 0 : Double.parseDouble(txtMonto.getText()));
            movimientoCaja.setSaldo(Double.parseDouble(txtMonto.getText()));

            String result = MovimientoCajaADO.Registrar_Movimiento(movimientoCaja);
            if (result.equalsIgnoreCase("registrado")) {
                Tools.AlertMessageInformation(window, "Ventas", "Se registro correctamente el movimiento de caja.");
                Tools.Dispose(window);
                ventaEstructuraController.getTxtSearch().requestFocus();
                ventaEstructuraController.getTxtSearch().selectAll();
            } else {
                Tools.AlertMessageWarning(window, "Ventas", result);
            }
        }
    }

    @FXML
    private void onKeyPressedComentario(KeyEvent event) {
        if (event.getCode() == KeyCode.TAB) {
            Node node = (Node) event.getSource();
            if (node instanceof TextArea) {
                TextAreaSkin skin = (TextAreaSkin) ((TextArea) node).getSkin();
                if (!event.isControlDown()) {
                    if (event.isShiftDown()) {
                        skin.getBehavior().traversePrevious();
                    } else {
                        skin.getBehavior().traverseNext();
                    }
                } else {
                    TextArea textA = (TextArea) node;
                    textA.replaceSelection("\t");
                }
                event.consume();
            }
        }
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            saveMovimiento();
        }
    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        saveMovimiento();
    }

    @FXML
    private void onKeyPressedCancelar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(window);
        }
    }

    @FXML
    private void onKeyTypedMonto(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtMonto.getText().contains(".") || c == '-' && txtMonto.getText().contains("-")) {
            event.consume();
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        Tools.Dispose(window);
    }

    public void setInitVentaEstructuraController(FxVentaEstructuraController ventaEstructuraController) {
        this.ventaEstructuraController = ventaEstructuraController;
    }

}
