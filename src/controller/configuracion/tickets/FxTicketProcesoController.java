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
    private TextField txtColumnas;
    @FXML
    private ComboBox<TicketTB> cbTpo;
    @FXML
    private Button btnSave;

    private FxTicketController ticketController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
        cbTpo.getItems().addAll(TicketADO.ListTipoTicket());
    }

    public void editarTicket(String name, short column) {
        btnSave.setText("Editar");
        btnSave.getStyleClass().add("buttonLightWarning");
        txtNombre.setText(name);
        txtColumnas.setText(column + "");
    }

    private void addTicket() {
        if (!Tools.isNumericInteger(txtColumnas.getText().trim())) {
            Tools.AlertMessageWarning(window, "Ticket", "El valor en el campo columna no es un número");
            txtColumnas.requestFocus();
        } else if (Short.parseShort(txtColumnas.getText()) <= 0) {
            Tools.AlertMessageWarning(window, "Ticket", "El valor en el campo columna es menor que 0");
            txtColumnas.requestFocus();
        } else {
            ticketController.editarTcket(txtNombre.getText().trim(), Short.parseShort(txtColumnas.getText()));
            Tools.Dispose(window);
        }
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
        if ((c < '0' || c > '9') && (c != '\b')) {
            event.consume();
        }
    }

    public void setInitTicketController(FxTicketController ticketController) {
        this.ticketController = ticketController;
    }

}