package controller.configuracion.impresoras;

import controller.consultas.pagar.FxCuentasPorCobrarVisualizarController;
import controller.consultas.pagar.FxCuentasPorPagarVisualizarController;
import controller.operaciones.cortecaja.FxCajaConsultasController;
import controller.operaciones.cortecaja.FxCajaController;
import controller.tools.BillPrintable;
import controller.tools.ConvertMonedaCadena;
import controller.tools.Session;
import controller.tools.Tools;
import java.awt.print.Book;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import model.CajaADO;
import model.CajaTB;
import model.CompraADO;
import model.CompraCreditoTB;
import model.MovimientoCajaTB;
import model.VentaADO;
import model.VentaCreditoTB;
import model.VentaTB;

public class FxOpcionesImprimirController implements Initializable {

    @FXML
    private AnchorPane apWindow;

    private FxCuentasPorCobrarVisualizarController cuentasPorCobrarVisualizarController;

    private FxCuentasPorPagarVisualizarController cuentasPorPagarVisualizarController;

    private FxCajaConsultasController cajaConsultasController;

    private FxCajaController cajaController;

    private ConvertMonedaCadena monedaCadena;

    private BillPrintable billPrintable;

    private AnchorPane hbEncabezado;

    private AnchorPane hbDetalleCabecera;

    private AnchorPane hbPie;

    private String idVenta;

    private String idVentaCredito;

    private String idCompra;

    private String idCompraCredito;

    private String idCaja;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        billPrintable = new BillPrintable();
        hbEncabezado = new AnchorPane();
        hbDetalleCabecera = new AnchorPane();
        hbPie = new AnchorPane();
        monedaCadena = new ConvertMonedaCadena();
    }

    public void loadDataCuentaPorCobrar(String idVenta, String idVentaCredito) {
        this.idVenta = idVenta;
        this.idVentaCredito = idVentaCredito;
    }

    public void loadDataCuentaPorPagar(String idCompra, String idCompraCredito) {
        this.idCompra = idCompra;
        this.idCompraCredito = idCompraCredito;
    }

    public void loadDataCorteCaja(String idCaja) {
        this.idCaja = idCaja;
    }

    private void onEventAceptar() throws IOException {
        if (cuentasPorCobrarVisualizarController != null) {
            Tools.Dispose(apWindow);
        } else if (cuentasPorPagarVisualizarController != null) {
            Tools.Dispose(apWindow);
        } else if (cajaController != null) {
            Tools.Dispose(apWindow);
            Tools.Dispose(cajaController.getVbPrincipal());
            cajaController.openWindowLogin();
        } else if (cajaConsultasController != null) {
            Tools.Dispose(apWindow);
        }
    }

    private void onEventTicket() {
        if (cuentasPorCobrarVisualizarController != null) {
            printEventCuentasPorCobrar();
        } else if (cuentasPorPagarVisualizarController != null) {
            printEventCuentasPorPagar();
        } else if (cajaController != null) {
            printEventCorteCaja();
        } else if (cajaConsultasController != null) {
            printEventCajaConsultas();
        }
    }

    private void printEventCuentasPorCobrar() {
        if (!Session.ESTADO_IMPRESORA_CUENTA_POR_COBRAR && Tools.isText(Session.NOMBRE_IMPRESORA_CUENTA_POR_COBRAR) && Tools.isText(Session.FORMATO_IMPRESORA_CUENTA_POR_COBRAR)) {
            Tools.AlertMessageWarning(apWindow, "Abono", "No esta configurado la ruta de impresión ve a la sección configuración/impresora.");
            Tools.Dispose(apWindow);
            return;
        }

        if (Session.FORMATO_IMPRESORA_CUENTA_POR_COBRAR.equalsIgnoreCase("ticket")) {
            if (Session.TICKET_CUENTA_POR_COBRAR_ID == 0 && Session.TICKET_CUENTA_POR_COBRAR_RUTA.equalsIgnoreCase("")) {
                Tools.AlertMessageWarning(apWindow, "Abono", "No hay un diseño predeterminado para la impresión configure su ticket en la sección configuración/tickets.");
                Tools.Dispose(apWindow);
            } else {
                Tools.Dispose(apWindow);
                executeProcessPrinterCuentaPorCobrar(
                        Session.DESING_IMPRESORA_CUENTA_POR_COBRAR,
                        Session.TICKET_CUENTA_POR_COBRAR_ID,
                        Session.TICKET_CUENTA_POR_COBRAR_RUTA,
                        Session.NOMBRE_IMPRESORA_CUENTA_POR_COBRAR,
                        Session.CORTAPAPEL_IMPRESORA_CUENTA_POR_COBRAR
                );
            }
        } else if (Session.FORMATO_IMPRESORA_CUENTA_POR_COBRAR.equalsIgnoreCase("a4")) {

        } else {
            Tools.AlertMessageWarning(apWindow, "Abono", "Error al validar el formato de impresión configure en la sección configuración/impresora.");
            Tools.Dispose(apWindow);
        }
    }

    private void printEventCuentasPorPagar() {
        if (!Session.ESTADO_IMPRESORA_CUENTA_POR_PAGAR && Tools.isText(Session.NOMBRE_IMPRESORA_CUENTA_POR_PAGAR) && Tools.isText(Session.FORMATO_IMPRESORA_CUENTA_POR_PAGAR)) {
            Tools.AlertMessageWarning(apWindow, "Abono", "No esta configurado la ruta de impresión ve a la sección configuración/impresora.");
            Tools.Dispose(apWindow);
            return;
        }

        if (Session.FORMATO_IMPRESORA_CUENTA_POR_PAGAR.equalsIgnoreCase("ticket")) {
            if (Session.TICKET_CUENTA_POR_PAGAR_ID == 0 && Session.TICKET_CUENTA_POR_PAGAR_RUTA.equalsIgnoreCase("")) {
                Tools.AlertMessageWarning(apWindow, "Abono", "No hay un diseño predeterminado para la impresión configure su ticket en la sección configuración/tickets.");
                Tools.Dispose(apWindow);
            } else {
                Tools.Dispose(apWindow);
                executeProcessPrinterCuentaPorPagar(
                        Session.DESING_IMPRESORA_CUENTA_POR_PAGAR,
                        Session.TICKET_CUENTA_POR_PAGAR_ID,
                        Session.TICKET_CUENTA_POR_PAGAR_RUTA,
                        Session.NOMBRE_IMPRESORA_CUENTA_POR_PAGAR,
                        Session.CORTAPAPEL_IMPRESORA_CUENTA_POR_PAGAR
                );
            }
        } else if (Session.FORMATO_IMPRESORA_CUENTA_POR_PAGAR.equalsIgnoreCase("a4")) {

        } else {
            Tools.AlertMessageWarning(apWindow, "Abono", "Error al validar el formato de impresión configure en la sección configuración/impresora.");
            Tools.Dispose(apWindow);
        }
    }

    private void printEventCorteCaja() {
        if (!Session.ESTADO_IMPRESORA_CORTE_CAJA && Tools.isText(Session.NOMBRE_IMPRESORA_CORTE_CAJA) && Tools.isText(Session.FORMATO_IMPRESORA_CORTE_CAJA)) {
            Tools.AlertMessageWarning(apWindow, "Abono", "No esta configurado la ruta de impresión ve a la sección configuración/impresora.");
            Tools.Dispose(apWindow);
            Tools.Dispose(cajaController.getVbPrincipal());
            cajaController.openWindowLogin();
            return;
        }
        if (Session.FORMATO_IMPRESORA_CORTE_CAJA.equalsIgnoreCase("ticket")) {
            if (Session.TICKET_CORTE_CAJA_ID == 0 && Session.TICKET_CORTE_CAJA_RUTA.equalsIgnoreCase("")) {
                Tools.AlertMessageWarning(apWindow, "Abono", "No hay un diseño predeterminado para la impresión configure su ticket en la sección configuración/tickets.");
                Tools.Dispose(apWindow);
                Tools.Dispose(cajaController.getVbPrincipal());
                cajaController.openWindowLogin();
            } else {
                executeProcessPrinterCorteCaja(
                        Session.DESING_IMPRESORA_CORTE_CAJA,
                        Session.TICKET_CORTE_CAJA_ID,
                        Session.TICKET_CORTE_CAJA_RUTA,
                        Session.NOMBRE_IMPRESORA_CORTE_CAJA,
                        Session.CORTAPAPEL_IMPRESORA_CORTE_CAJA
                );
                Tools.Dispose(apWindow);
                Tools.Dispose(cajaController.getVbPrincipal());
                cajaController.openWindowLogin();
            }
        } else if (Session.FORMATO_IMPRESORA_CORTE_CAJA.equalsIgnoreCase("a4")) {
            Tools.Dispose(apWindow);
            Tools.Dispose(cajaController.getVbPrincipal());
            cajaController.openWindowLogin();
        } else {
            Tools.AlertMessageWarning(apWindow, "Abono", "Error al validar el formato de impresión configure en la sección configuración/impresora.");
            Tools.Dispose(apWindow);
            Tools.Dispose(cajaController.getVbPrincipal());
            cajaController.openWindowLogin();
        }
    }

    private void printEventCajaConsultas() {
        if (!Session.ESTADO_IMPRESORA_CORTE_CAJA && Tools.isText(Session.NOMBRE_IMPRESORA_CORTE_CAJA) && Tools.isText(Session.FORMATO_IMPRESORA_CORTE_CAJA)) {
            Tools.AlertMessageWarning(apWindow, "Abono", "No esta configurado la ruta de impresión ve a la sección configuración/impresora.");
            Tools.Dispose(apWindow);
            return;
        }
        if (Session.FORMATO_IMPRESORA_CORTE_CAJA.equalsIgnoreCase("ticket")) {
            if (Session.TICKET_CORTE_CAJA_ID == 0 && Session.TICKET_CORTE_CAJA_RUTA.equalsIgnoreCase("")) {
                Tools.AlertMessageWarning(apWindow, "Abono", "No hay un diseño predeterminado para la impresión configure su ticket en la sección configuración/tickets.");
                Tools.Dispose(apWindow);
            } else {
                executeProcessPrinterCorteCaja(
                        Session.DESING_IMPRESORA_CORTE_CAJA,
                        Session.TICKET_CORTE_CAJA_ID,
                        Session.TICKET_CORTE_CAJA_RUTA,
                        Session.NOMBRE_IMPRESORA_CORTE_CAJA,
                        Session.CORTAPAPEL_IMPRESORA_CORTE_CAJA
                );
                Tools.Dispose(apWindow);
            }
        } else if (Session.FORMATO_IMPRESORA_CORTE_CAJA.equalsIgnoreCase("a4")) {

        } else {
            Tools.AlertMessageWarning(apWindow, "Abono", "Error al validar el formato de impresión configure en la sección configuración/impresora.");
            Tools.Dispose(apWindow);
        }
    }

    /*cuenta por cobrar*/
    private void executeProcessPrinterCuentaPorCobrar(String desing, int ticketId, String ticketRuta, String nombreImpresora, boolean cortaPapel) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<String> task = new Task<String>() {
            @Override
            public String call() {
                if (!Tools.isText(idVenta) && !Tools.isText(idVentaCredito)) {
                    VentaCreditoTB ventaCreditoTB = VentaADO.ImprimirVetanCreditoById(idVenta, idVentaCredito);
                    if (ventaCreditoTB != null && ventaCreditoTB.getVentaTB() != null) {
                        try {
                            if (desing.equalsIgnoreCase("withdesing")) {
                                return printTicketWithDesingCuentaCobrarUnico(ventaCreditoTB, ticketId, ticketRuta, nombreImpresora, cortaPapel);
                            } else {
                                billPrintable.loadEstructuraTicket(ticketId, ticketRuta, hbEncabezado, hbDetalleCabecera, hbPie);
                                return printTicketNoDesingCuentaCobrarUnico(ventaCreditoTB, nombreImpresora, cortaPapel);
                            }
                        } catch (PrinterException | IOException | PrintException ex) {
                            return "Error en imprimir: " + ex.getLocalizedMessage();
                        }
                    } else {
                        return "empty";
                    }
                } else if (!Tools.isText(idVenta)) {
                    VentaTB ventaTB = VentaADO.ListarVentasDetalleCredito(idVenta);
                    if (ventaTB != null) {
                        try {
                            if (desing.equalsIgnoreCase("withdesing")) {
                                return printTicketWithDesingCuentaCobrar(ventaTB, ticketId, ticketRuta, nombreImpresora, cortaPapel);
                            } else {
                                billPrintable.loadEstructuraTicket(ticketId, ticketRuta, hbEncabezado, hbDetalleCabecera, hbPie);
                                return printTicketNoDesingCuentaCobrar(ventaTB, nombreImpresora, cortaPapel);
                            }
                        } catch (PrinterException | IOException | PrintException ex) {
                            return "Error en imprimir: " + ex.getLocalizedMessage();
                        }
                    } else {
                        return "empty";
                    }
                } else {
                    return "empty";
                }
            }
        };

        task.setOnSucceeded(w -> {
            String result = task.getValue();
            if (result.equalsIgnoreCase("completed")) {
                Tools.showAlertNotification("/view/image/information_large.png",
                        "Envío de impresión",
                        "Se completo el proceso de impresión correctamente.",
                        Duration.seconds(5),
                        Pos.BOTTOM_RIGHT);
            } else if (result.equalsIgnoreCase("error_name")) {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "Error en encontrar el nombre de la impresión por problemas de puerto o driver.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else if (result.equalsIgnoreCase("empty")) {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "No hay registros para mostrar en el reporte.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else {
                Tools.showAlertNotification("/view/image/error_large.png",
                        "Envío de impresión",
                        "Se producto un problema de la impresora\n" + result,
                        Duration.seconds(10),
                        Pos.CENTER);
            }
        });
        task.setOnFailed(w -> {
            Tools.showAlertNotification("/view/image/warning_large.png",
                    "Envío de impresión",
                    "Se produjo un problema en el proceso de envío, \n intente nuevamente o comuníquese con su proveedor del sistema.",
                    Duration.seconds(10),
                    Pos.BOTTOM_RIGHT);
        });

        task.setOnScheduled(w -> {
            Tools.showAlertNotification("/view/image/print.png",
                    "Envío de impresión",
                    "Se envió la impresión a la cola, este\n proceso puede tomar unos segundos.",
                    Duration.seconds(5),
                    Pos.BOTTOM_RIGHT);
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }

    }

    private String printTicketWithDesingCuentaCobrarUnico(VentaCreditoTB ventaCreditoTB, int ticketId, String ticketRuta, String nombreImpresora, boolean cortaPapel) throws PrinterException, PrintException, IOException {
        billPrintable.loadEstructuraTicket(ticketId, ticketRuta, hbEncabezado, hbDetalleCabecera, hbPie);

        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
            billPrintable.hbEncebezado(box,
                    "ABONO",
                    ventaCreditoTB.getIdVentaCredito(),
                    ventaCreditoTB.getVentaTB().getClienteTB().getNumeroDocumento(),
                    ventaCreditoTB.getVentaTB().getClienteTB().getInformacion(),
                    ventaCreditoTB.getVentaTB().getClienteTB().getCelular(),
                    ventaCreditoTB.getVentaTB().getClienteTB().getDireccion(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    Tools.roundingValue(ventaCreditoTB.getVentaTB().getMontoTotal(), 2),
                    Tools.roundingValue(ventaCreditoTB.getVentaTB().getMontoCobrado(), 2),
                    Tools.roundingValue(ventaCreditoTB.getVentaTB().getMontoRestante(), 2));
        }

        AnchorPane hbDetalle = new AnchorPane();
        ObservableList<VentaCreditoTB> arrList = FXCollections.observableArrayList(ventaCreditoTB);
        for (int m = 0; m < arrList.size(); m++) {
            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                HBox hBox = new HBox();
                hBox.setId("dc_" + m + "" + i);
                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                billPrintable.hbDetalleCuentaCobrar(hBox, box, arrList, m);
                hbDetalle.getChildren().add(hBox);
            }
        }

        for (int i = 0; i < hbPie.getChildren().size(); i++) {
            HBox box = ((HBox) hbPie.getChildren().get(i));
            billPrintable.hbPie(box,
                    "M",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    ventaCreditoTB.getVentaTB().getClienteTB().getNumeroDocumento(),
                    ventaCreditoTB.getVentaTB().getClienteTB().getInformacion(),
                    "",
                    ventaCreditoTB.getVentaTB().getClienteTB().getCelular(),
                    "");
        }

        billPrintable.generatePDFPrint(hbEncabezado, hbDetalle, hbPie);

        DocPrintJob job = billPrintable.findPrintService(nombreImpresora, PrinterJob.lookupPrintServices()).createPrintJob();

        if (job != null) {
            PrinterJob pj = PrinterJob.getPrinterJob();
            pj.setPrintService(job.getPrintService());
            pj.setJobName(nombreImpresora);
            Book book = new Book();
            book.append(billPrintable, billPrintable.getPageFormat(pj));
            pj.setPageable(book);
            pj.print();
            if (cortaPapel) {
                billPrintable.printCortarPapel(nombreImpresora);
            }
            return "completed";
        } else {
            return "error_name";
        }
    }

    private String printTicketNoDesingCuentaCobrarUnico(VentaCreditoTB ventaCreditoTB, String nombreImpresora, boolean cortaPapel) {
        ArrayList<HBox> object = new ArrayList<>();
        int rows = 0;
        int lines = 0;
        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
            object.add((HBox) hbEncabezado.getChildren().get(i));
            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
            rows++;
            lines += billPrintable.hbEncebezado(box,
                    "ABONO",
                    ventaCreditoTB.getIdVentaCredito(),
                    ventaCreditoTB.getVentaTB().getClienteTB().getNumeroDocumento(),
                    ventaCreditoTB.getVentaTB().getClienteTB().getInformacion(),
                    ventaCreditoTB.getVentaTB().getClienteTB().getCelular(),
                    ventaCreditoTB.getVentaTB().getClienteTB().getDireccion(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    Tools.roundingValue(ventaCreditoTB.getVentaTB().getMontoTotal(), 2),
                    Tools.roundingValue(ventaCreditoTB.getVentaTB().getMontoCobrado(), 2),
                    Tools.roundingValue(ventaCreditoTB.getVentaTB().getMontoRestante(), 2));
        }

        ObservableList<VentaCreditoTB> arrList = FXCollections.observableArrayList(ventaCreditoTB);
        for (int m = 0; m < arrList.size(); m++) {
            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                HBox hBox = new HBox();
                hBox.setId("dc_" + m + "" + i);
                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                rows++;
                lines += billPrintable.hbDetalleCuentaCobrar(hBox, box, arrList, m);
                object.add(hBox);
            }
        }

        for (int i = 0; i < hbPie.getChildren().size(); i++) {
            object.add((HBox) hbPie.getChildren().get(i));
            HBox box = ((HBox) hbPie.getChildren().get(i));
            rows++;
            lines += billPrintable.hbPie(box,
                    "M",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    ventaCreditoTB.getVentaTB().getClienteTB().getNumeroDocumento(),
                    ventaCreditoTB.getVentaTB().getClienteTB().getInformacion(),
                    "",
                    ventaCreditoTB.getVentaTB().getClienteTB().getCelular(),
                    "");
        }
        return billPrintable.modelTicket(rows + lines + 1 + 10, lines, object, nombreImpresora, cortaPapel);
    }

    private String printTicketWithDesingCuentaCobrar(VentaTB ventaTB, int ticketId, String ticketRuta, String nombreImpresora, boolean cortaPapel) throws PrinterException, PrintException, IOException {
        billPrintable.loadEstructuraTicket(ticketId, ticketRuta, hbEncabezado, hbDetalleCabecera, hbPie);

        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
            billPrintable.hbEncebezado(box,
                    "ABONO",
                    "VC",
                    ventaTB.getClienteTB().getNumeroDocumento(),
                    ventaTB.getClienteTB().getInformacion(),
                    ventaTB.getClienteTB().getCelular(),
                    ventaTB.getClienteTB().getDireccion(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    Tools.roundingValue(ventaTB.getMontoTotal(), 2),
                    Tools.roundingValue(ventaTB.getMontoCobrado(), 2),
                    Tools.roundingValue(ventaTB.getMontoRestante(), 2));
        }

        AnchorPane hbDetalle = new AnchorPane();
        ObservableList<VentaCreditoTB> arrList = FXCollections.observableArrayList(ventaTB.getVentaCreditoTBs());
        for (int m = 0; m < arrList.size(); m++) {
            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                HBox hBox = new HBox();
                hBox.setId("dc_" + m + "" + i);
                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                billPrintable.hbDetalleCuentaCobrar(hBox, box, arrList, m);
                hbDetalle.getChildren().add(hBox);
            }
        }

        for (int i = 0; i < hbPie.getChildren().size(); i++) {
            HBox box = ((HBox) hbPie.getChildren().get(i));
            billPrintable.hbPie(box,
                    "M",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    ventaTB.getClienteTB().getNumeroDocumento(),
                    ventaTB.getClienteTB().getInformacion(),
                    "",
                    ventaTB.getClienteTB().getCelular(),
                    "");
        }

        billPrintable.generatePDFPrint(hbEncabezado, hbDetalle, hbPie);

        DocPrintJob job = billPrintable.findPrintService(nombreImpresora, PrinterJob.lookupPrintServices()).createPrintJob();

        if (job != null) {
            PrinterJob pj = PrinterJob.getPrinterJob();
            pj.setPrintService(job.getPrintService());
            pj.setJobName(nombreImpresora);
            Book book = new Book();
            book.append(billPrintable, billPrintable.getPageFormat(pj));
            pj.setPageable(book);
            pj.print();
            if (cortaPapel) {
                billPrintable.printCortarPapel(nombreImpresora);
            }
            return "completed";
        } else {
            return "error_name";
        }
    }

    private String printTicketNoDesingCuentaCobrar(VentaTB ventaTB, String nombreImpresora, boolean cortaPapel) {
        ArrayList<HBox> object = new ArrayList<>();
        int rows = 0;
        int lines = 0;
        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
            object.add((HBox) hbEncabezado.getChildren().get(i));
            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
            rows++;
            lines += billPrintable.hbEncebezado(box,
                    "ABONO",
                    "VC",
                    ventaTB.getClienteTB().getNumeroDocumento(),
                    ventaTB.getClienteTB().getInformacion(),
                    ventaTB.getClienteTB().getCelular(),
                    ventaTB.getClienteTB().getDireccion(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    Tools.roundingValue(ventaTB.getMontoTotal(), 2),
                    Tools.roundingValue(ventaTB.getMontoCobrado(), 2),
                    Tools.roundingValue(ventaTB.getMontoRestante(), 2));
        }

        ObservableList<VentaCreditoTB> arrList = FXCollections.observableArrayList(ventaTB.getVentaCreditoTBs());
        for (int m = 0; m < arrList.size(); m++) {
            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                HBox hBox = new HBox();
                hBox.setId("dc_" + m + "" + i);
                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                rows++;
                lines += billPrintable.hbDetalleCuentaCobrar(hBox, box, arrList, m);
                object.add(hBox);
            }
        }

        for (int i = 0; i < hbPie.getChildren().size(); i++) {
            object.add((HBox) hbPie.getChildren().get(i));
            HBox box = ((HBox) hbPie.getChildren().get(i));
            rows++;
            lines += billPrintable.hbPie(box,
                    "M",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    ventaTB.getClienteTB().getNumeroDocumento(),
                    ventaTB.getClienteTB().getInformacion(),
                    "",
                    ventaTB.getClienteTB().getCelular(),
                    "");
        }
        return billPrintable.modelTicket(rows + lines + 1 + 10, lines, object, nombreImpresora, cortaPapel);
    }

    /*cuenta por pagar*/
    private void executeProcessPrinterCuentaPorPagar(String desing, int ticketId, String ticketRuta, String nombreImpresora, boolean cortaPapel) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<String> task = new Task<String>() {
            @Override
            public String call() {
                if (!Tools.isText(idCompra) && !Tools.isText(idCompraCredito)) {
                    CompraCreditoTB compraCreditoTB = CompraADO.ImprimirCompraCreditoById(idCompra, idCompraCredito);
                    if (compraCreditoTB != null && compraCreditoTB.getCompraTB() != null) {
                        try {
                            if (desing.equalsIgnoreCase("withdesing")) {
                                return printTicketWithDesingCuentaPagarUnico(compraCreditoTB, ticketId, ticketRuta, nombreImpresora, cortaPapel);
                            } else {
                                billPrintable.loadEstructuraTicket(ticketId, ticketRuta, hbEncabezado, hbDetalleCabecera, hbPie);
                                return printTicketNoDesingCuentaPagarUnico(compraCreditoTB, nombreImpresora, cortaPapel);
                            }
                        } catch (PrinterException | IOException | PrintException ex) {
                            return "Error en imprimir: " + ex.getLocalizedMessage();
                        }
                    } else {
                        return "empty";
                    }
                } else {
                    return "empty";
                }
            }
        };

        task.setOnSucceeded(w -> {
            String result = task.getValue();
            if (result.equalsIgnoreCase("completed")) {
                Tools.showAlertNotification("/view/image/information_large.png",
                        "Envío de impresión",
                        "Se completo el proceso de impresión correctamente.",
                        Duration.seconds(5),
                        Pos.BOTTOM_RIGHT);
            } else if (result.equalsIgnoreCase("error_name")) {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "Error en encontrar el nombre de la impresión por problemas de puerto o driver.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else if (result.equalsIgnoreCase("empty")) {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "No hay registros para mostrar en el reporte.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else {
                Tools.showAlertNotification("/view/image/error_large.png",
                        "Envío de impresión",
                        "Se producto un problema de la impresora\n" + result,
                        Duration.seconds(10),
                        Pos.CENTER);
            }
        });
        task.setOnFailed(w -> {
            Tools.showAlertNotification("/view/image/warning_large.png",
                    "Envío de impresión",
                    "Se produjo un problema en el proceso de envío, \n intente nuevamente o comuníquese con su proveedor del sistema.",
                    Duration.seconds(10),
                    Pos.BOTTOM_RIGHT);
        });

        task.setOnScheduled(w -> {
            Tools.showAlertNotification("/view/image/print.png",
                    "Envío de impresión",
                    "Se envió la impresión a la cola, este\n proceso puede tomar unos segundos.",
                    Duration.seconds(5),
                    Pos.BOTTOM_RIGHT);
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }

    }

    private String printTicketWithDesingCuentaPagarUnico(CompraCreditoTB compraCreditoTB, int ticketId, String ticketRuta, String nombreImpresora, boolean cortaPapel) throws PrinterException, PrintException, IOException {
        billPrintable.loadEstructuraTicket(ticketId, ticketRuta, hbEncabezado, hbDetalleCabecera, hbPie);

        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
            billPrintable.hbEncebezado(box,
                    "AMORTIZAR",
                    compraCreditoTB.getIdCompraCredito(),
                    compraCreditoTB.getProveedorTB().getNumeroDocumento(),
                    compraCreditoTB.getProveedorTB().getRazonSocial(),
                    compraCreditoTB.getProveedorTB().getCelular(),
                    compraCreditoTB.getProveedorTB().getDireccion(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "0",
                    "0",
                    "0");
        }

        AnchorPane hbDetalle = new AnchorPane();
        ObservableList<CompraCreditoTB> arrList = FXCollections.observableArrayList(compraCreditoTB);
        for (int m = 0; m < arrList.size(); m++) {
            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                HBox hBox = new HBox();
                hBox.setId("dc_" + m + "" + i);
                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                billPrintable.hbDetalleCuentaPagar(hBox, box, arrList, m);
                hbDetalle.getChildren().add(hBox);
            }
        }

        for (int i = 0; i < hbPie.getChildren().size(); i++) {
            HBox box = ((HBox) hbPie.getChildren().get(i));
            billPrintable.hbPie(box,
                    "M",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    compraCreditoTB.getProveedorTB().getNumeroDocumento(),
                    compraCreditoTB.getProveedorTB().getRazonSocial(),
                    "",
                    compraCreditoTB.getProveedorTB().getCelular(),
                    "");
        }

        billPrintable.generatePDFPrint(hbEncabezado, hbDetalle, hbPie);

        DocPrintJob job = billPrintable.findPrintService(nombreImpresora, PrinterJob.lookupPrintServices()).createPrintJob();

        if (job != null) {
            PrinterJob pj = PrinterJob.getPrinterJob();
            pj.setPrintService(job.getPrintService());
            pj.setJobName(nombreImpresora);
            Book book = new Book();
            book.append(billPrintable, billPrintable.getPageFormat(pj));
            pj.setPageable(book);
            pj.print();
            if (cortaPapel) {
                billPrintable.printCortarPapel(nombreImpresora);
            }
            return "completed";
        } else {
            return "error_name";
        }
    }

    private String printTicketNoDesingCuentaPagarUnico(CompraCreditoTB compraCreditoTB, String nombreImpresora, boolean cortaPapel) {
        ArrayList<HBox> object = new ArrayList<>();
        int rows = 0;
        int lines = 0;
        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
            object.add((HBox) hbEncabezado.getChildren().get(i));
            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
            rows++;
            lines += billPrintable.hbEncebezado(box,
                    "AMORTIZAR",
                    compraCreditoTB.getIdCompraCredito(),
                    compraCreditoTB.getProveedorTB().getNumeroDocumento(),
                    compraCreditoTB.getProveedorTB().getRazonSocial(),
                    compraCreditoTB.getProveedorTB().getCelular(),
                    compraCreditoTB.getProveedorTB().getDireccion(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "0",
                    "0",
                    "0");
        }

        ObservableList<CompraCreditoTB> arrList = FXCollections.observableArrayList(compraCreditoTB);
        for (int m = 0; m < arrList.size(); m++) {
            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                HBox hBox = new HBox();
                hBox.setId("dc_" + m + "" + i);
                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                rows++;
                lines += billPrintable.hbDetalleCuentaPagar(hBox, box, arrList, m);
                object.add(hBox);
            }
        }

        for (int i = 0; i < hbPie.getChildren().size(); i++) {
            object.add((HBox) hbPie.getChildren().get(i));
            HBox box = ((HBox) hbPie.getChildren().get(i));
            rows++;
            lines += billPrintable.hbPie(box,
                    "M",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    compraCreditoTB.getProveedorTB().getNumeroDocumento(),
                    compraCreditoTB.getProveedorTB().getRazonSocial(),
                    "",
                    compraCreditoTB.getProveedorTB().getCelular(),
                    "");
        }
        return billPrintable.modelTicket(rows + lines + 1 + 10, lines, object, nombreImpresora, cortaPapel);
    }

    /*corte de caja*/
    private void executeProcessPrinterCorteCaja(String desing, int ticketId, String ticketRuta, String nombreImpresora, boolean cortaPapel) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<String> task = new Task<String>() {
            @Override
            public String call() {
                if (!Tools.isText(idCaja)) {
                    ArrayList<Object> arrayList = CajaADO.ListarMovimientoPorById(idCaja);
                    if (!arrayList.isEmpty() && arrayList.get(0) != null && arrayList.get(1) != null && arrayList.get(2) != null) {
                        try {
                            CajaTB cajaTB = (CajaTB) arrayList.get(0);
                            if (desing.equalsIgnoreCase("withdesing")) {
                                return printTicketWithDesingCorteCaja(cajaTB, (ArrayList<MovimientoCajaTB>) arrayList.get(2), ticketId, ticketRuta, nombreImpresora, cortaPapel);
                            } else {
                                billPrintable.loadEstructuraTicket(ticketId, ticketRuta, hbEncabezado, hbDetalleCabecera, hbPie);
                                return printTicketNoDesingCorteCaja(cajaTB, (ArrayList<MovimientoCajaTB>) arrayList.get(2), nombreImpresora, cortaPapel);
                            }
                        } catch (PrinterException | IOException | PrintException ex) {
                            return "Error en imprimir: " + ex.getLocalizedMessage();
                        }
                    } else {
                        return "empty";
                    }

                } else {
                    return "empty";
                }
            }
        };

        task.setOnSucceeded(w -> {
            String result = task.getValue();
            if (result.equalsIgnoreCase("completed")) {
                Tools.showAlertNotification("/view/image/information_large.png",
                        "Envío de impresión",
                        "Se completo el proceso de impresión correctamente.",
                        Duration.seconds(5),
                        Pos.BOTTOM_RIGHT);
            } else if (result.equalsIgnoreCase("error_name")) {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "Error en encontrar el nombre de la impresión por problemas de puerto o driver.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else if (result.equalsIgnoreCase("empty")) {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "No hay registros para mostrar en el reporte.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else {
                Tools.showAlertNotification("/view/image/error_large.png",
                        "Envío de impresión",
                        "Se producto un problema de la impresora\n" + result,
                        Duration.seconds(10),
                        Pos.CENTER);
            }
        });
        task.setOnFailed(w -> {
            Tools.showAlertNotification("/view/image/warning_large.png",
                    "Envío de impresión",
                    "Se produjo un problema en el proceso de envío, \n intente nuevamente o comuníquese con su proveedor del sistema.",
                    Duration.seconds(10),
                    Pos.BOTTOM_RIGHT);
        });

        task.setOnScheduled(w -> {
            Tools.showAlertNotification("/view/image/print.png",
                    "Envío de impresión",
                    "Se envió la impresión a la cola, este\n proceso puede tomar unos segundos.",
                    Duration.seconds(5),
                    Pos.BOTTOM_RIGHT);
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private String printTicketWithDesingCorteCaja(CajaTB cajaTB, ArrayList<MovimientoCajaTB> movimientoCajaTBs, int ticketId, String ticketRuta, String nombreImpresora, boolean cortaPapel) throws PrinterException, PrintException, IOException {
        billPrintable.loadEstructuraTicket(ticketId, ticketRuta, hbEncabezado, hbDetalleCabecera, hbPie);

        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
            billPrintable.hbEncebezado(box,
                    "CORTE DE CAJA",
                    cajaTB.getIdCaja(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    cajaTB.getFechaApertura(),
                    cajaTB.getHoraApertura(),
                    cajaTB.getFechaCierre(),
                    cajaTB.getHoraCierre(),
                    Tools.roundingValue(cajaTB.getCalculado(), 2),
                    Tools.roundingValue(cajaTB.getContado(), 2),
                    Tools.roundingValue(cajaTB.getDiferencia(), 2),
                    cajaTB.getEmpleadoTB().getNumeroDocumento(),
                    cajaTB.getEmpleadoTB().getApellidos(),
                    cajaTB.getEmpleadoTB().getCelular(),
                    cajaTB.getEmpleadoTB().getDireccion(),
                    "",
                    "",
                    "");
        }

        AnchorPane hbDetalle = new AnchorPane();
        ObservableList<MovimientoCajaTB> arrList = FXCollections.observableArrayList(movimientoCajaTBs);
        for (int m = 0; m < arrList.size(); m++) {
            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                HBox hBox = new HBox();
                hBox.setId("dc_" + m + "" + i);
                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                billPrintable.hbDetalleCorteCaja(hBox, box, arrList, m);
                hbDetalle.getChildren().add(hBox);
            }
        }

        for (int i = 0; i < hbPie.getChildren().size(); i++) {
            HBox box = ((HBox) hbPie.getChildren().get(i));
            billPrintable.hbPie(box,
                    "M",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    "",
                    "",
                    "",
                    "",
                    "");
        }

        billPrintable.generatePDFPrint(hbEncabezado, hbDetalle, hbPie);

        DocPrintJob job = billPrintable.findPrintService(nombreImpresora, PrinterJob.lookupPrintServices()).createPrintJob();

        if (job != null) {
            PrinterJob pj = PrinterJob.getPrinterJob();
            pj.setPrintService(job.getPrintService());
            pj.setJobName(nombreImpresora);
            Book book = new Book();
            book.append(billPrintable, billPrintable.getPageFormat(pj));
            pj.setPageable(book);
            pj.print();
            if (cortaPapel) {
                billPrintable.printCortarPapel(nombreImpresora);
            }
            return "completed";
        } else {
            return "error_name";
        }
    }

    private String printTicketNoDesingCorteCaja(CajaTB cajaTB, ArrayList<MovimientoCajaTB> movimientoCajaTBs, String nombreImpresora, boolean cortaPapel) {
        ArrayList<HBox> object = new ArrayList<>();
        int rows = 0;
        int lines = 0;
        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
            object.add((HBox) hbEncabezado.getChildren().get(i));
            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
            rows++;
            lines += billPrintable.hbEncebezado(box,
                    "CORTE DE CAJA",
                    cajaTB.getIdCaja(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    cajaTB.getFechaApertura(),
                    cajaTB.getHoraApertura(),
                    cajaTB.getFechaCierre(),
                    cajaTB.getHoraCierre(),
                    Tools.roundingValue(cajaTB.getCalculado(), 2),
                    Tools.roundingValue(cajaTB.getContado(), 2),
                    Tools.roundingValue(cajaTB.getDiferencia(), 2),
                    cajaTB.getEmpleadoTB().getNumeroDocumento(),
                    cajaTB.getEmpleadoTB().getApellidos(),
                    cajaTB.getEmpleadoTB().getCelular(),
                    cajaTB.getEmpleadoTB().getDireccion(),
                    "",
                    "",
                    "");
        }

        ObservableList<MovimientoCajaTB> arrList = FXCollections.observableArrayList(movimientoCajaTBs);
        for (int m = 0; m < arrList.size(); m++) {
            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                HBox hBox = new HBox();
                hBox.setId("dc_" + m + "" + i);
                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                rows++;
                lines += billPrintable.hbDetalleCorteCaja(hBox, box, arrList, m);
                object.add(hBox);
            }
        }

        for (int i = 0; i < hbPie.getChildren().size(); i++) {
            object.add((HBox) hbPie.getChildren().get(i));
            HBox box = ((HBox) hbPie.getChildren().get(i));
            rows++;
            lines += billPrintable.hbPie(box,
                    "M",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    "0.00",
                    "",
                    "",
                    "",
                    "",
                    "");
        }
        return billPrintable.modelTicket(rows + lines + 1 + 10, lines, object, nombreImpresora, cortaPapel);
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            onEventAceptar();
        }
    }

    @FXML
    private void onActionAceptar(ActionEvent event) throws IOException {
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

    public void setInitOpcionesImprimirCuentasPorCobrar(FxCuentasPorCobrarVisualizarController cuentasPorCobrarVisualizarController) {
        this.cuentasPorCobrarVisualizarController = cuentasPorCobrarVisualizarController;
    }

    public void setInitOpcionesImprimirCuentasPorPagar(FxCuentasPorPagarVisualizarController cuentasPorPagarVisualizarController) {
        this.cuentasPorPagarVisualizarController = cuentasPorPagarVisualizarController;
    }

    public void setInitOpcionesImprimirCajaConsultas(FxCajaConsultasController cajaConsultasController) {
        this.cajaConsultasController = cajaConsultasController;
    }

    public void setInitOpcionesImprimirCorteCaja(FxCajaController cajaController) {
        this.cajaController = cajaController;
    }

}
