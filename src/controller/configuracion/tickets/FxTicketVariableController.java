package controller.configuracion.tickets;

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
            listCabecera.add(new TicketTB("Representante de la empresa", "REPRESENTANTE", "repeempresa"));
            listCabecera.add(new TicketTB("Telefono de la empresa", "TELEFONO", "telempresa"));
            listCabecera.add(new TicketTB("Celular de la empresa", "CELULAR", "celempresa"));
            listCabecera.add(new TicketTB("Pagina web de la empresa", "WWW.COMPANY.COM", "pagwempresa"));
            listCabecera.add(new TicketTB("Email de la empresa", "COMPANY@EMAIL.COM", "emailempresa"));
            listCabecera.add(new TicketTB("Direcci??n de la empresa", "DIRECCION DE LA EMPRESA", "direcempresa"));
            listCabecera.add(new TicketTB("Ruc de la empresa", "56232665", "rucempresa"));
            listCabecera.add(new TicketTB("Raz??n social de la empresa", "RAZON SOCIAL", "razoempresa"));
            listCabecera.add(new TicketTB("Nombre comercial de la empresa", "NOMBRE COMERCIAL", "nomcomempresa"));
            listCabecera.add(new TicketTB("Fecha actual", Tools.getDate("dd/MM/yyyy"), "fchactual"));
            listCabecera.add(new TicketTB("Hora actual", Tools.getTime("hh:mm:ss aa"), "horactual"));
            listCabecera.add(new TicketTB("Nombre del documento de emitido", "NOMBRE DEL DOCUMENTO EMITIDO", "docventa"));
            listCabecera.add(new TicketTB("Numeraci??n del documento de emitido", "V000-00000000", "numventa"));
            listCabecera.add(new TicketTB("N??mero del documento del cliente", "NUMERO DOCUMENTO CLIENTE", "numcliente"));
            listCabecera.add(new TicketTB("Informaci??n del cliente", "DATOS DEL CLIENTE", "infocliente"));
            listCabecera.add(new TicketTB("Celular del cliente", "CELULAR DEL CLIENTE", "celcliente"));
            listCabecera.add(new TicketTB("Direcci??n del cliente", "DIRECCION DEL CLIENTE", "direcliente"));
            listCabecera.add(new TicketTB("Codigo de venta", "CODIGO UNICO DE VENTA", "codigo"));
            listCabecera.add(new TicketTB("Importe Total en Letras", "0.00", "importetotalletras"));
            listCabecera.add(new TicketTB("Monto Total", "0.00", "montotal"));
            listCabecera.add(new TicketTB("Monto Pagado/Cobrado", "0.00", "montopagacobra"));
            listCabecera.add(new TicketTB("Monto Restante", "0.00", "montorestanten"));
            listCabecera.add(new TicketTB("Fecha de inicio de la Operaci??n", "dd/MM/yyyy", "finiciooperacion"));
            listCabecera.add(new TicketTB("Hora de inicio de la Operaci??n", "HH:mm:ss a", "hiniciooperacion"));
            listCabecera.add(new TicketTB("Fecha de termino de la Operaci??n", "dd/MM/yyyy", "fterminooperacion"));
            listCabecera.add(new TicketTB("Hora de termino de la Operaci??n", "HH:mm:ss a", "hterminooperacion"));
            listCabecera.add(new TicketTB("Contado para corte de caja", "0.00", "contado"));
            listCabecera.add(new TicketTB("Calculado para corte de caja", "0.00", "calculado"));
            listCabecera.add(new TicketTB("Diferencia para corte de caja", "0.00", "diferencia"));
            listCabecera.add(new TicketTB("Apertura de caja", "0.00", "aperturacaja"));
            listCabecera.add(new TicketTB("Ventas con efectivo de caja", "0.00", "ventasefectivocaja"));
            listCabecera.add(new TicketTB("Ventas con tarjeta de caja", "0.00", "ventastarjetacaja"));
            listCabecera.add(new TicketTB("Ingresos en efectivo de caja", "0.00", "ingresosefectivocaja"));
            listCabecera.add(new TicketTB("Egresos en efectivo de caja", "0.00", "egresosefectivocaja"));
            listCabecera.add(new TicketTB("Numero documento Empleado", "NUMERO DE DOCUMENTO EMPLEADO", "numempleado"));
            listCabecera.add(new TicketTB("Informaci??n del Empleado", "INFORMACION DEL EMPLEADO", "infoempleado"));
            listCabecera.add(new TicketTB("Celular del Empleado", "CELULAR DEL EMPLEADO", "celempleado"));
            listCabecera.add(new TicketTB("Direcci??n del Empleado", "DIRECCION DEL EMPLEADO", "direcempleado"));
            listCabecera.add(new TicketTB("Observaci??n Generada en los documentos", "OBSERVACION", "observacion"));
            lvLista.getItems().addAll(listCabecera);
        } else if (hBox.getId().substring(0, 2).equalsIgnoreCase("dr")) {
            listDetalleCuerpo.add(new TicketTB("Numeraci??n de las filas", "1", "numfilas"));
            listDetalleCuerpo.add(new TicketTB("C??digo alterno del producto", "456123789", "codalternoarticulo"));
            listDetalleCuerpo.add(new TicketTB("C??digo de barras del producto", "789456123789", "codbarrasarticulo"));
            listDetalleCuerpo.add(new TicketTB("Descripci??n del producto", "DESCRIPCION DEL PRODUCTO", "nombretarticulo"));
            listDetalleCuerpo.add(new TicketTB("Cantidad por producto", "1000", "cantarticulo"));
            listDetalleCuerpo.add(new TicketTB("Costo unitario por producto", "0000.00", "costarticulo"));
            listDetalleCuerpo.add(new TicketTB("Precio unitario por producto", "0000.00", "precarticulo"));
            listDetalleCuerpo.add(new TicketTB("Descuento por producto", "00.00", "descarticulo"));
            listDetalleCuerpo.add(new TicketTB("Importe por producto", "00.00", "impoarticulo"));
            listDetalleCuerpo.add(new TicketTB("Fecha de la operaci??n", "FECHA DE LA OPERACION", "fechadetalle"));
            listDetalleCuerpo.add(new TicketTB("Hora de la operaci??n", "HORA DE LA OPERACION", "horadetalle"));
            listDetalleCuerpo.add(new TicketTB("Tipo de Operaci??n", "TIPO DE OPERACION", "tipomovimiento"));
            listDetalleCuerpo.add(new TicketTB("Monto total de la operaci??n", "0000.00", "montooperacion"));
            listDetalleCuerpo.add(new TicketTB("Descripci??n del la operaci??n", "INFORACION REFERENTE A LA OPERACION", "observacion"));
            lvLista.getItems().addAll(listDetalleCuerpo);
        } else if (hBox.getId().substring(0, 2).equalsIgnoreCase("cp")) {
            listPie.add(new TicketTB("Fecha actual", Tools.getDate("dd/MM/yyyy"), "fchactual"));
            listPie.add(new TicketTB("Hora actual", Tools.getTime("hh:mm:ss aa"), "horactual"));
            listPie.add(new TicketTB("Importe Bruto", "M 00.00", "imptotal"));
            listPie.add(new TicketTB("Sub Importe", "M 00.00", "subtotal"));
            listPie.add(new TicketTB("Descuento Total", "M 00.00", "dscttotal"));
            listPie.add(new TicketTB("Nombre de la Operaci??n", "M 00.00", "nameoperacion"));
            listPie.add(new TicketTB("Valor de la Operaci??n", "M 00.00", "valoroperacion"));
            listPie.add(new TicketTB("Nombre del Impuesto", "M 00.00", "nameimpustos"));
            listPie.add(new TicketTB("Valor del Impuesto", "M 00.00", "valorimpustos"));
            listPie.add(new TicketTB("Importe Neto", "M 00.00", "totalpagar"));
            listPie.add(new TicketTB("Tarjeta", "M 00.00", "tarjeta"));
            listPie.add(new TicketTB("Efectivo", "M 00.00", "efectivo"));
            listPie.add(new TicketTB("Vuelto", "M 00.00", "vuelto"));
            listPie.add(new TicketTB("N??mero del documento del cliente", "NUMERO DE DOCUMENTO CLIENTE", "numcliente"));
            listPie.add(new TicketTB("Informaci??n del cliente", "DATOS DEL CLIENTE", "infocliente"));
            listPie.add(new TicketTB("Celular del cliente", "CELULAR DEL CLIENTE", "celcliente"));
            listPie.add(new TicketTB("Direcci??n del cliente", "DIRECCION DEL CLIENTE", "direcliente"));
            listPie.add(new TicketTB("Codigo de venta", "CODIGO UNICO DE VENTA", "codigo"));
            listPie.add(new TicketTB("Importe Total en Letras", "SON CERO 0/0 SOLES", "importetotalletras"));
            listPie.add(new TicketTB("Numero documento Empleado", "NUMERO DE DOCUMENTO EMPLEADO", "numempleado"));
            listPie.add(new TicketTB("Informaci??n del Empleado", "INFORMACION DEL EMPLEADO", "infoempleado"));
            listPie.add(new TicketTB("Celular del Empleado", "CELULAR DEL EMPLEADO", "celempleado"));
            listPie.add(new TicketTB("Direcci??n del Empleado", "DIRECCION DEL EMPLEADO", "direcempleado"));
            listPie.add(new TicketTB("Observaci??n de la operaci??n", "OBSERVACI??/DIRECCI??N DE LA OPERACION", "observacion"));
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
            if (lvLista.getSelectionModel().getSelectedIndex() >= 0) {
                txtContenido.setText(lvLista.getSelectionModel().getSelectedItem().getVariable().toString());
            }
        }
    }

    public void setInitTicketController(FxTicketController ticketController) {
        this.ticketController = ticketController;
    }

}
