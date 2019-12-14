package controller.configuracion.impresoras;

import controller.configuracion.tickets.FxTicketController;
import controller.tools.BillPrintable;
import controller.tools.PrinterService;
import controller.tools.TextFieldTicket;
import controller.tools.Tools;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

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
                ArrayList<HBox> object = new ArrayList<>();
                int rows = 0;
                int lines = 0;
                for (int i = 0; i < ticketController.getHbEncabezado().getChildren().size(); i++) {
                    object.add((HBox) ticketController.getHbEncabezado().getChildren().get(i));
                    HBox box = ((HBox) ticketController.getHbEncabezado().getChildren().get(i));
                    rows++;
                    for (int j = 0; j < box.getChildren().size(); j++) {
                        lines += ((TextFieldTicket) box.getChildren().get(j)).getLines();
                    }
                }
                for (int i = 0; i < ticketController.getHbDetalleCabecera().getChildren().size(); i++) {
                    object.add((HBox) ticketController.getHbDetalleCabecera().getChildren().get(i));
                    HBox box = ((HBox) ticketController.getHbDetalleCabecera().getChildren().get(i));
                    rows++;
                    for (int j = 0; j < box.getChildren().size(); j++) {
                        lines += ((TextFieldTicket) box.getChildren().get(j)).getLines();
                    }
                }

                for (int i = 0; i < ticketController.getHbPie().getChildren().size(); i++) {
                    object.add((HBox) ticketController.getHbPie().getChildren().get(i));
                    HBox box = ((HBox) ticketController.getHbPie().getChildren().get(i));
                    rows++;
                    for (int j = 0; j < box.getChildren().size(); j++) {
                        lines += ((TextFieldTicket) box.getChildren().get(j)).getLines();
                    }
                }
                billPrintable.modelTicket(apWindow, rows + lines + 1 + 5, lines, object, "Imprimir", "Error al imprimir el ticket.",cbImpresoras.getSelectionModel().getSelectedItem(),cbCortarPapel.isSelected());
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
