package controller.configuracion.impresoras;

import controller.operaciones.venta.FxVentaAbonoProcesoController;
import controller.tools.Session;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class FxOpcionesImprimirController implements Initializable {

    @FXML
    private AnchorPane apWindow;

    private FxVentaAbonoProcesoController ventaAbonoProcesoController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    private void onEventAceptar() {
        if (ventaAbonoProcesoController != null) {
            Tools.Dispose(apWindow);
            Tools.Dispose(ventaAbonoProcesoController.getWindow());
        }
    }

    private void onEventTicket() {
        if (ventaAbonoProcesoController != null) {
            if (!Session.ESTADO_IMPRESORA_CUENTA_POR_COBRAR && Tools.isText(Session.NOMBRE_IMPRESORA_CUENTA_POR_COBRAR) && Tools.isText(Session.FORMATO_IMPRESORA_CUENTA_POR_COBRAR)) {
                Tools.AlertMessageWarning(apWindow, "Abono", "No esta configurado la ruta de impresión ve a la sección configuración/impresora.");
                Tools.Dispose(apWindow);
                Tools.Dispose(ventaAbonoProcesoController.getWindow());
                return;
            }

            if (Session.FORMATO_IMPRESORA_CUENTA_POR_COBRAR.equalsIgnoreCase("ticket")) {
                if (Session.TICKET_CUENTA_POR_COBRAR_ID == 0 && Session.TICKET_CUENTA_POR_COBRAR_RUTA.equalsIgnoreCase("")) {
                    Tools.AlertMessageWarning(apWindow, "Abono", "No hay un diseño predeterminado para la impresión configure su ticket en la sección configuración/tickets.");
                    Tools.Dispose(apWindow);
                    Tools.Dispose(ventaAbonoProcesoController.getWindow());
                } else {

                }
            } else if (Session.FORMATO_IMPRESORA_CUENTA_POR_COBRAR.equalsIgnoreCase("a4")) {

            } else {
                Tools.AlertMessageWarning(apWindow, "Abono", "Error al validar el formato de impresión configure en la sección configuración/impresora.");
                Tools.Dispose(apWindow);
                Tools.Dispose(ventaAbonoProcesoController.getWindow());
            }

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
    private void onKeyPressedImprimirA4(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionImprimirA4(ActionEvent event) {

    }

    @FXML
    private void onKeyPressedImprimirTicket(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventTicket();
        }
    }

    @FXML
    private void onActionImprimirTicket(ActionEvent event) {
        onEventTicket();
    }

    public void setInitOpcionesImprimirController(FxVentaAbonoProcesoController ventaAbonoProcesoController) {
        this.ventaAbonoProcesoController = ventaAbonoProcesoController;
    }

}
