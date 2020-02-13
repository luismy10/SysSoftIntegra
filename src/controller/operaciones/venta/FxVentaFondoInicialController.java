package controller.operaciones.venta;

import controller.tools.Session;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.CajaTB;
import model.MovimientoCajaADO;
import model.MovimientoCajaTB;

public class FxVentaFondoInicialController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private TextField txtImporte;

    private FxVentaController ventaController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
    }

    private void eventAceptar() {
        if (Tools.isNumeric(txtImporte.getText().trim())) {            
  
            CajaTB cajaTB = new CajaTB();
            cajaTB.setFechaApertura(Tools.getDate());
            cajaTB.setHoraApertura(Tools.getHour("HH:mm:ss"));
            cajaTB.setIdUsuario(Session.USER_ID);
            cajaTB.setEstado(true);
            cajaTB.setContado(0);
            cajaTB.setCalculado(0);
            cajaTB.setDiferencia(0);
            cajaTB.setFechaRegistro(Tools.getDate());
            cajaTB.setHoraRegistro(Tools.getHour());

            MovimientoCajaTB movimientoCaja = new MovimientoCajaTB(); 
            movimientoCaja.setIdUsuario(Session.USER_ID);
            movimientoCaja.setFechaMovimiento(Tools.getDate());
            movimientoCaja.setHoraMovimiento(Tools.getHour());
            movimientoCaja.setComentario("Apertura de caja");
            movimientoCaja.setMovimiento("FONC");
            movimientoCaja.setEntrada(Double.parseDouble(txtImporte.getText()));
            movimientoCaja.setSalidas(0);
            movimientoCaja.setSaldo(Double.parseDouble(txtImporte.getText()));

            String result = MovimientoCajaADO.AperturarCaja_Movimiento(cajaTB, movimientoCaja);
            if (result.equalsIgnoreCase("registrado")) {
                Tools.AlertMessageInformation(window, "Ventas", "Se aperturo correctamento la caja.");
                ventaController.setAperturaCaja(true);
                Tools.Dispose(window);
            } else {
                Tools.AlertMessageError(window, "Ventas", result);
                ventaController.setAperturaCaja(false);                
            }
        }
    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        eventAceptar();
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            eventAceptar();
        }
    }

    @FXML
    private void onKeyTypedImporte(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtImporte.getText().contains(".") || c == '-' && txtImporte.getText().contains("-")) {
            event.consume();
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

    public void setInitVentaController(FxVentaController ventaController) {
        this.ventaController = ventaController;
    }

}