package controller.configuracion.tickets;

import controller.tools.Session;
import controller.tools.TextFieldTicket;
import controller.tools.Tools;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.TicketTB;

public class FxTicketVariableController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private ListView<TicketTB> lvLista;
    @FXML
    private TextField txtContenido;

    private FxTicketController ticketController;

    private ArrayList<TicketTB> listCabecera;

    private ArrayList<TicketTB> listDetalleCuerpo;

    private ArrayList<TicketTB> listPie;

    private HBox hBox;

    private int sheetWidth;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
        listCabecera = new ArrayList<>();
        listDetalleCuerpo = new ArrayList<>();
        listPie = new ArrayList<>();
    }

    public void setLoadComponent(HBox hBox, int sheetWidth) {
        this.hBox = hBox;
        this.sheetWidth = sheetWidth;
        if (hBox.getId().substring(0, 2).equalsIgnoreCase("cb")) {
            listCabecera.add(new TicketTB("Representante de la empresa", Session.COMPANY_REPRESENTANTE, "repeempresa"));
            listCabecera.add(new TicketTB("Telefono de la empresa", Session.COMPANY_TELEFONO.isEmpty() ? "TELEFONO" : Session.COMPANY_TELEFONO, "telempresa"));
            listCabecera.add(new TicketTB("Celular de la empresa", Session.COMPANY_CELULAR.isEmpty() ? "CELULAR" : Session.COMPANY_CELULAR, "celempresa"));
            listCabecera.add(new TicketTB("Pagina web de la empresa", Session.COMPANY_PAGINAWEB.isEmpty() ? "WWW.COMPANY.COM" : Session.COMPANY_PAGINAWEB, "pagwempresa"));
            listCabecera.add(new TicketTB("Email de la empresa", Session.COMPANY_EMAIL.isEmpty() ? "COMPANY@EMAIL.COM" : Session.COMPANY_EMAIL, "emailempresa"));
            listCabecera.add(new TicketTB("Dirección de la empresa", Session.COMPANY_DOMICILIO.isEmpty() ? "DIRECCION" : Session.COMPANY_DOMICILIO, "direcempresa"));
            listCabecera.add(new TicketTB("Ruc de la empresa", Session.COMPANY_NUM_DOCUMENTO.isEmpty() ? "56232665" : Session.COMPANY_NUM_DOCUMENTO, "rucempresa"));
            listCabecera.add(new TicketTB("Razón social de la empresa", Session.COMPANY_RAZON_SOCIAL.isEmpty() ? "COMPANY" : Session.COMPANY_RAZON_SOCIAL, "razoempresa"));
            listCabecera.add(new TicketTB("Nombre comercial de la empresa", Session.COMPANY_NOMBRE_COMERCIAL.isEmpty() ? "COMPANY" : Session.COMPANY_NOMBRE_COMERCIAL, "nomcomempresa"));
            listCabecera.add(new TicketTB("Fecha actual", Tools.getDate("dd/MM/yyyy"), "fchactual"));
            listCabecera.add(new TicketTB("Hora actual", Tools.getHour("hh:mm:ss aa"), "horactual"));
            listCabecera.add(new TicketTB("Nombre del documento de venta", "NOMBRE DEL DOCUMENTO DE VENTA", "docventa"));
            listCabecera.add(new TicketTB("Numeración del documento de venta", "V000-00000000", "numventa"));
            listCabecera.add(new TicketTB("Numero del documento del cliente", "NUMERO DOCUMENTO CLIENTE", "numcliente"));
            listCabecera.add(new TicketTB("Información del cliente", "DATOS DEL CLIENTE", "infocliente"));
            listCabecera.add(new TicketTB("Celular del cliente", "CELULAR DEL CLIENTE", "celcliente"));
            listCabecera.add(new TicketTB("Dirección del cliente", "DIRECCION DEL CLIENTE", "direcliente"));
            listCabecera.add(new TicketTB("Codigo de venta", "CODIGO UNICO DE VENTA", "codigo"));
            lvLista.getItems().addAll(listCabecera);
        } else if (hBox.getId().substring(0, 2).equalsIgnoreCase("dr")) {
            listDetalleCuerpo.add(new TicketTB("Numeración de las filas", "1", "numfilas"));
            listDetalleCuerpo.add(new TicketTB("Código de barras", "789456123789", "codbarrasarticulo"));
            listDetalleCuerpo.add(new TicketTB("Descripción del árticulo", "Descripcion del articulo para la venta", "nombretarticulo"));
            listDetalleCuerpo.add(new TicketTB("Cantidad por árticulo", "1000", "cantarticulo"));
            listDetalleCuerpo.add(new TicketTB("Precio unitario por árticulo", "0000.00", "precarticulo"));
            listDetalleCuerpo.add(new TicketTB("Descuento por árticulo", "0000.00", "descarticulo"));
            listDetalleCuerpo.add(new TicketTB("Importe por árticulo", "0000.00", "impoarticulo"));
            lvLista.getItems().addAll(listDetalleCuerpo);
        } else if (hBox.getId().substring(0, 2).equalsIgnoreCase("cp")) {
            listPie.add(new TicketTB("Fecha actual", Tools.getDate("dd/MM/yyyy"), "fchactual"));
            listPie.add(new TicketTB("Hora actual", Tools.getHour("hh:mm:ss aa"), "horactual"));
            listPie.add(new TicketTB("Importe total", "M 00.00", "imptotal"));
            listPie.add(new TicketTB("Sub total", "M 00.00", "subtotal"));
            listPie.add(new TicketTB("Descuento total", "M 00.00", "dscttotal"));
            listPie.add(new TicketTB("Nombre de la Operación", "Lista -> M 00.00", "nameoperacion"));
            listPie.add(new TicketTB("Valor de la Operación", "Lista -> M 00.00", "valoroperacion"));
            listPie.add(new TicketTB("Nombre del Impuesto", "Lista -> M 00.00", "nameimpustos"));
            listPie.add(new TicketTB("Valor del Impuesto", "Lista -> M 00.00", "valorimpustos"));
            listPie.add(new TicketTB("Total a pagar", "M 00.00", "totalpagar"));
            listPie.add(new TicketTB("Efectivo", "M 00.00", "efectivo"));
            listPie.add(new TicketTB("Vuelto", "M 00.00", "vuelto"));
            listPie.add(new TicketTB("Numero del documento del cliente", "NUMERO DOCUMENTO CLIENTE", "numcliente"));
            listPie.add(new TicketTB("Información del cliente", "DATOS DEL CLIENTE", "infocliente"));
            listPie.add(new TicketTB("Celular del cliente", "CELULAR DEL CLIENTE", "celcliente"));
            listPie.add(new TicketTB("Dirección del cliente", "DIRECCION DEL CLIENTE", "direcliente"));
            listPie.add(new TicketTB("Codigo de venta", "CODIGO UNICO DE VENTA", "codigo"));
            lvLista.getItems().addAll(listPie);
        }
    }

    private void addTextVariable() {
        if (lvLista.getSelectionModel().getSelectedIndex() >= 0) {
            short widthContent = 0;
            for (short i = 0; i < hBox.getChildren().size(); i++) {
                widthContent += ((TextFieldTicket) hBox.getChildren().get(i)).getColumnWidth();
            }
            if (widthContent <= sheetWidth) {
                short widthNew = (short) (sheetWidth - widthContent);
                if (widthNew <= 0 || widthNew > sheetWidth) {
                    Tools.AlertMessageWarning(window, "Ticket", "No hay espacio suficiente en la fila.");
                } else {
                    TextFieldTicket field = ticketController.addElementTextField("iu", lvLista.getSelectionModel().getSelectedItem().getVariable().toString(), false, (short) 0, widthNew, Pos.CENTER_LEFT, false, lvLista.getSelectionModel().getSelectedItem().getIdVariable(), "Consola", 12.5f);
                    hBox.getChildren().add(field);
                    Tools.Dispose(window);
                }
            }
        } else {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Ticket", "Seleccione un item de la lista.", false);
        }
    }

    @FXML
    private void onKeyPressedAdd(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            addTextVariable();
        }
    }

    @FXML
    private void onActionAdd(ActionEvent event) {
        addTextVariable();
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
    private void onMouseClickedList(MouseEvent event) {
        if (event.getClickCount() == 2) {
            addTextVariable();
        } else if (event.getClickCount() == 1) {
            if (!lvLista.getItems().isEmpty()) {
                txtContenido.setText(lvLista.getSelectionModel().getSelectedItem().getVariable().toString());
            }
        }
    }

    public void setInitTicketController(FxTicketController ticketController) {
        this.ticketController = ticketController;
    }

}
