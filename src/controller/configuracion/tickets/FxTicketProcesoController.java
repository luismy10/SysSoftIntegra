package controller.configuracion.tickets;

import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.TicketADO;
import model.TicketTB;

public class FxTicketProcesoController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtMilimetros;
    @FXML
    private ComboBox<TicketTB> cbTpo;
    @FXML
    private Button btnSave;

    private FxTicketController ticketController;

    private boolean opcion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
        cbTpo.getItems().addAll(TicketADO.ListTipoTicket());
        opcion = false;
    }

    public void editarTicket(String name, double column) {
        opcion = true;
        btnSave.setText("Editar");
        btnSave.getStyleClass().add("buttonLightWarning");
        txtNombre.setText(name);
        txtMilimetros.setText(column + "");
    }

    private void addTicket() {
        if (!Tools.isNumeric(txtMilimetros.getText().trim())) {
            Tools.AlertMessageWarning(window, "Ticket", "El valor en el campo milímetro no es un número");
            txtMilimetros.requestFocus();
        } else if (Double.parseDouble(txtMilimetros.getText()) <= 0) {
            Tools.AlertMessageWarning(window, "Ticket", "El valor en el campo milímetro es menor que 0");
            txtMilimetros.requestFocus();
        } else {
            if (opcion) {
                ticketController.editarTicket(txtNombre.getText().trim(), Short.parseShort(txtMilimetros.getText()));
                Tools.Dispose(window);
            } else {
                ticketController.agregarTicket(txtNombre.getText().trim(), convertMMtoPX(Double.parseDouble(txtMilimetros.getText())),Double.parseDouble(txtMilimetros.getText()));
                Tools.Dispose(window);
            }
        }
    }

    private int convertMMtoPX(double milimetro) {
        return (int) (Double.parseDouble(Tools.roundingValue(2.83465 * milimetro, 0)) * 1.3333333333333333);
    }

    @FXML
    private void onActionAdd(ActionEvent event) {
        addTicket();
    }

    @FXML
    private void onKeyPressedAdd(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            addTicket();
        }
    }

    @FXML
    private void onKeyPressedCancelar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(window);
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        Tools.Dispose(window);
    }

    @FXML
    private void onKeyTypedColumnas(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
            event.consume();
        }
        if (c == '.' && txtMilimetros.getText().equalsIgnoreCase(".")) {
            event.consume();
        }
    }

    public void setInitTicketController(FxTicketController ticketController) {
        this.ticketController = ticketController;
    }

}
