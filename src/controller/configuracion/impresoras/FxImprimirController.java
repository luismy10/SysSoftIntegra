package controller.configuracion.impresoras;

import controller.configuracion.tickets.FxTicketController;
import controller.tools.BillPrintable;
import controller.tools.PrinterService;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class FxImprimirController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private ComboBox<String> cbImpresoras;
    @FXML
    private CheckBox cbCortarPapel;

    private FxTicketController ticketController;

    private PrinterService printerService;

    private BillPrintable billPrintable;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        printerService = new PrinterService();
        printerService.getPrinters().forEach(e -> {
            cbImpresoras.getItems().add(e);
        });
        billPrintable = new BillPrintable();
    }

    private void executeImprimir() {
        if (cbImpresoras.getSelectionModel().getSelectedIndex() >= 0) {
            if (ticketController != null) {
                billPrintable.setSheetWidth(ticketController.getSheetWidth());
                billPrintable.generatePDFPrint(ticketController.getHbEncabezado(), ticketController.getHbDetalleCabecera(), ticketController.getHbPie(), cbImpresoras.getSelectionModel().getSelectedItem(), cbCortarPapel.isSelected());
            }
        } else {
            Tools.AlertMessageWarning(apWindow, "Imprimir", "Seleccione un impresora para continuar.");
            cbImpresoras.requestFocus();
        }
    }

    @FXML
    private void onKeyPressedImprimir(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            executeImprimir();
        }
    }

    @FXML
    private void onActionImprimir(ActionEvent event) {
        executeImprimir();
    }

    public void setInitTicketController(FxTicketController ticketController) {
        this.ticketController = ticketController;
    }

}
