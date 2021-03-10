package controller.configuracion.impresoras;

import controller.consultas.pagar.FxCuentasPorCobrarVisualizarController;
import controller.consultas.pagar.FxCuentasPorPagarVisualizarController;
import controller.operaciones.cortecaja.FxCajaConsultasController;
import controller.operaciones.cortecaja.FxCajaController;
import controller.operaciones.cotizacion.FxCotizacionController;
import controller.operaciones.venta.FxVentaLlevarControllerHistorial;
import controller.reporte.FxReportViewController;
import controller.tools.BillPrintable;
import controller.tools.ConvertMonedaCadena;
import controller.tools.FilesRouters;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.awt.print.Book;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import model.CajaADO;
import model.CajaTB;
import model.CompraADO;
import model.CompraCreditoTB;
import model.CompraTB;
import model.CotizacionADO;
import model.CotizacionTB;
import model.DetalleVentaTB;
import model.HistorialSuministroSalidaTB;
import model.SuministroTB;
import model.VentaADO;
import model.VentaCreditoTB;
import model.VentaTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FxOpcionesImprimirController implements Initializable {

    @FXML
    private AnchorPane apWindow;

    private FxCuentasPorCobrarVisualizarController cuentasPorCobrarVisualizarController;

    private FxCuentasPorPagarVisualizarController cuentasPorPagarVisualizarController;

    private FxCajaConsultasController cajaConsultasController;

    private FxCajaController cajaController;

    private FxCotizacionController cotizacionController;

    private FxVentaLlevarControllerHistorial ventaLlevarControllerHistorial;

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

    private String idCotizacion;

    private String idSuministro;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadComponents();
    }

    public void loadComponents() {
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

    public void loadDataCotizacion(String idCotizacion) {
        this.idCotizacion = idCotizacion;
    }

    public void loadDataHistorialSuministroLlevar(String idVenta, String idSuministro) {
        this.idVenta = idVenta;
        this.idSuministro = idSuministro;
    }

    private void onEventAceptar() {
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

        } else if (cotizacionController != null) {
            Tools.Dispose(apWindow);
        } else if (ventaLlevarControllerHistorial != null) {
            Tools.println(apWindow);
        }
    }

    private void onEventTicket() {
        if (cuentasPorCobrarVisualizarController != null) {
            printEventCuentasPorCobrarTicket();
        } else if (cuentasPorPagarVisualizarController != null) {
            printEventCuentasPorPagar();
        } else if (cajaController != null) {
            printEventCorteCajaTicket();
            Tools.Dispose(apWindow);
            Tools.Dispose(cajaController.getVbPrincipal());
            cajaController.openWindowLogin();
        } else if (cajaConsultasController != null) {
            printEventCajaConsultas();
        } else if (cotizacionController != null) {
            printEventTicketCotizacion();
        } else if (ventaLlevarControllerHistorial != null) {
            printEventHistorialSuministroLlevarTicket();
        }
    }

    private void onEvent4a() {
        if (cotizacionController != null) {
            printEvent4aCotizacion();
        }
    }

    private void printEventCuentasPorCobrarTicket() {
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
        } else {
            Tools.AlertMessageWarning(apWindow, "Abono", "Error al validar el formato de impresión configure en la sección configuración/impresora.");
            Tools.Dispose(apWindow);
        }

    }

    private void printEventCuentasPorCobrar4a() {
        if (!Session.ESTADO_IMPRESORA_CUENTA_POR_COBRAR && Tools.isText(Session.NOMBRE_IMPRESORA_CUENTA_POR_COBRAR) && Tools.isText(Session.FORMATO_IMPRESORA_CUENTA_POR_COBRAR)) {
            Tools.AlertMessageWarning(apWindow, "Abono", "No esta configurado la ruta de impresión ve a la sección configuración/impresora.");
            Tools.Dispose(apWindow);
            return;
        }
        if (Session.FORMATO_IMPRESORA_CUENTA_POR_COBRAR.equalsIgnoreCase("a4")) {

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

    public void printEventCorteCajaTicket() {
        if (!Session.ESTADO_IMPRESORA_CORTE_CAJA && Tools.isText(Session.NOMBRE_IMPRESORA_CORTE_CAJA) && Tools.isText(Session.FORMATO_IMPRESORA_CORTE_CAJA)) {
            Tools.AlertMessageWarning(apWindow, "Abono", "No esta configurado la ruta de impresión ve a la sección configuración/impresora.");
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
            }
        } else if (Session.FORMATO_IMPRESORA_CORTE_CAJA.equalsIgnoreCase("a4")) {

        } else {
            Tools.AlertMessageWarning(apWindow, "Abono", "Error al validar el formato de impresión configure en la sección configuración/impresora.");
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

    private void printEventTicketCotizacion() {
        if (!Session.ESTADO_IMPRESORA_COTIZACION && Tools.isText(Session.NOMBRE_IMPRESORA_COTIZACION) && Tools.isText(Session.FORMATO_IMPRESORA_COTIZACION)) {
            Tools.AlertMessageWarning(apWindow, "Cotización", "No esta configurado la ruta de impresión ve a la sección configuración/impresora.");
            Tools.Dispose(apWindow);
            return;
        }
        if (Session.FORMATO_IMPRESORA_COTIZACION.equalsIgnoreCase("ticket")) {
            if (Session.TICKET_COTIZACION_ID == 0 && Session.TICKET_COTIZACION_RUTA.equalsIgnoreCase("")) {
                Tools.AlertMessageWarning(apWindow, "Cotización", "No hay un diseño predeterminado para la impresión configure su ticket en la sección configuración/tickets.");
                Tools.Dispose(apWindow);
            } else {
                executeProcessCotizacionTicket(
                        Session.DESING_IMPRESORA_COTIZACION,
                        Session.TICKET_COTIZACION_ID,
                        Session.TICKET_COTIZACION_RUTA,
                        Session.NOMBRE_IMPRESORA_COTIZACION,
                        Session.CORTAPAPEL_IMPRESORA_COTIZACION
                );
                Tools.Dispose(apWindow);
            }
        } else {
            Tools.AlertMessageWarning(apWindow, "Cotización", "Error al validar el formato de impresión configure en la sección configuración/impresora.");
            Tools.Dispose(apWindow);
        }
    }

    private void printEvent4aCotizacion() {
        executeProcessCotizacionReporte();
        Tools.Dispose(apWindow);
    }

    public void printEventHistorialSuministroLlevarTicket() {
        if (!Session.ESTADO_IMPRESORA_HISTORIA_SALIDA_PRODUCTOS && Tools.isText(Session.NOMBRE_IMPRESORA_HISTORIA_SALIDA_PRODUCTOS) && Tools.isText(Session.FORMATO_IMPRESORA_HISTORIA_SALIDA_PRODUCTOS)) {
            Tools.AlertMessageWarning(apWindow, "Salida del Producto", "No esta configurado la ruta de impresión ve a la sección configuración/impresora.");
            return;
        }
        if (Session.FORMATO_IMPRESORA_HISTORIA_SALIDA_PRODUCTOS.equalsIgnoreCase("ticket")) {
            if (Session.TICKET_HISTORIAL_SALIDA_PRODUCTOS_ID == 0 && Session.TICKET_HISTORIAL_SALIDA_PRODUCTOS_RUTA.equalsIgnoreCase("")) {
                Tools.AlertMessageWarning(apWindow, "Salida del Producto", "No hay un diseño predeterminado para la impresión configure su ticket en la sección configuración/tickets.");
            } else {
                executeProcessHistorialSalidaProducto(
                        Session.DESING_IMPRESORA_HISTORIA_SALIDA_PRODUCTOS,
                        Session.TICKET_HISTORIAL_SALIDA_PRODUCTOS_ID,
                        Session.TICKET_HISTORIAL_SALIDA_PRODUCTOS_RUTA,
                        Session.NOMBRE_IMPRESORA_HISTORIA_SALIDA_PRODUCTOS,
                        Session.CORTAPAPEL_IMPRESORA_HISTORIA_SALIDA_PRODUCTOS
                );
            }
        } else {
            Tools.AlertMessageWarning(apWindow, "Salida del Producto", "Error al validar el formato de impresión configure en la sección configuración/impresora.");
        }
    }

    /**
     * CUENTAS POR COBRAR
     */
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
                    Tools.roundingValue(ventaCreditoTB.getVentaTB().getMontoRestante(), 2),
                    "",
                    "0",
                    "0",
                    "0",
                    "0",
                    "0");
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
                    "0.00",
                    "0.00",
                    "0.00",
                    ventaCreditoTB.getVentaTB().getClienteTB().getNumeroDocumento(),
                    ventaCreditoTB.getVentaTB().getClienteTB().getInformacion(),
                    "",
                    ventaCreditoTB.getVentaTB().getClienteTB().getCelular(),
                    "",
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
                    Tools.roundingValue(ventaCreditoTB.getVentaTB().getMontoRestante(), 2),
                    "",
                    "0",
                    "0",
                    "0",
                    "0",
                    "0");
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
                    "0.00",
                    "0.00",
                    "0.00",
                    ventaCreditoTB.getVentaTB().getClienteTB().getNumeroDocumento(),
                    ventaCreditoTB.getVentaTB().getClienteTB().getInformacion(),
                    "",
                    ventaCreditoTB.getVentaTB().getClienteTB().getCelular(),
                    "",
                    "",
                    "",
                    "",
                    "",
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
                    Tools.roundingValue(ventaTB.getMontoRestante(), 2),
                    "",
                    "0",
                    "0",
                    "0",
                    "0",
                    "0");
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
                    "0.00",
                    "0.00",
                    "0.00",
                    ventaTB.getClienteTB().getNumeroDocumento(),
                    ventaTB.getClienteTB().getInformacion(),
                    "",
                    ventaTB.getClienteTB().getCelular(),
                    "",
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
                    Tools.roundingValue(ventaTB.getMontoRestante(), 2),
                    "",
                    "0",
                    "0",
                    "0",
                    "0",
                    "0");
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
                    "0.00",
                    "0.00",
                    "0.00",
                    ventaTB.getClienteTB().getNumeroDocumento(),
                    ventaTB.getClienteTB().getInformacion(),
                    "",
                    ventaTB.getClienteTB().getCelular(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "");
        }
        return billPrintable.modelTicket(rows + lines + 1 + 10, lines, object, nombreImpresora, cortaPapel);
    }

    /**
     * CUENTAS POR PAGAR
     */
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
                } else if (!Tools.isText(idCompra)) {
                    CompraTB compraTB = CompraADO.Listar_Compra_Credito(idCompra);
                    if (compraTB != null) {
                        try {
                            if (desing.equalsIgnoreCase("withdesing")) {
                                return printTicketWithDesingCuentaPagar(compraTB, ticketId, ticketRuta, nombreImpresora, cortaPapel);
                            } else {
                                billPrintable.loadEstructuraTicket(ticketId, ticketRuta, hbEncabezado, hbDetalleCabecera, hbPie);
                                return printTicketNoDesingCuentaPagar(compraTB, nombreImpresora, cortaPapel);
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
                    compraCreditoTB.getCompraTB().getProveedorTB().getNumeroDocumento(),
                    compraCreditoTB.getCompraTB().getProveedorTB().getRazonSocial(),
                    compraCreditoTB.getCompraTB().getProveedorTB().getCelular(),
                    compraCreditoTB.getCompraTB().getProveedorTB().getDireccion(),
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
                    Tools.roundingValue(compraCreditoTB.getCompraTB().getMontoTotal(), 2),
                    Tools.roundingValue(compraCreditoTB.getCompraTB().getMontoPagado(), 2),
                    Tools.roundingValue(compraCreditoTB.getCompraTB().getMontoRestante(), 2),
                    "",
                    "0",
                    "0",
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
                    "0.00",
                    "0.00",
                    "0.00",
                    compraCreditoTB.getCompraTB().getProveedorTB().getNumeroDocumento(),
                    compraCreditoTB.getCompraTB().getProveedorTB().getRazonSocial(),
                    "",
                    compraCreditoTB.getCompraTB().getProveedorTB().getCelular(),
                    "",
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
                    "0",
                    "",
                    "0",
                    "0",
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
                    "0.00",
                    "0.00",
                    "0.00",
                    compraCreditoTB.getProveedorTB().getNumeroDocumento(),
                    compraCreditoTB.getProveedorTB().getRazonSocial(),
                    "",
                    compraCreditoTB.getProveedorTB().getCelular(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "");
        }
        return billPrintable.modelTicket(rows + lines + 1 + 10, lines, object, nombreImpresora, cortaPapel);
    }

    private String printTicketWithDesingCuentaPagar(CompraTB compraTB, int ticketId, String ticketRuta, String nombreImpresora, boolean cortaPapel) throws PrinterException, PrintException, IOException {
        billPrintable.loadEstructuraTicket(ticketId, ticketRuta, hbEncabezado, hbDetalleCabecera, hbPie);

        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
            billPrintable.hbEncebezado(box,
                    "AMORTIZAR",
                    "CP",
                    compraTB.getProveedorTB().getNumeroDocumento(),
                    compraTB.getProveedorTB().getRazonSocial(),
                    compraTB.getProveedorTB().getCelular(),
                    compraTB.getProveedorTB().getDireccion(),
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
                    Tools.roundingValue(compraTB.getMontoTotal(), 2),
                    Tools.roundingValue(compraTB.getMontoPagado(), 2),
                    Tools.roundingValue(compraTB.getMontoRestante(), 2),
                    "",
                    "0",
                    "0",
                    "0",
                    "0",
                    "0");
        }

        AnchorPane hbDetalle = new AnchorPane();
        ObservableList<CompraCreditoTB> arrList = FXCollections.observableArrayList(compraTB.getCompraCreditoTBs());
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
                    "0.00",
                    "0.00",
                    "0.00",
                    compraTB.getProveedorTB().getNumeroDocumento(),
                    compraTB.getProveedorTB().getRazonSocial(),
                    "",
                    compraTB.getProveedorTB().getCelular(),
                    "",
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

    private String printTicketNoDesingCuentaPagar(CompraTB compraTB, String nombreImpresora, boolean cortaPapel) {
        ArrayList<HBox> object = new ArrayList<>();
        int rows = 0;
        int lines = 0;
        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
            object.add((HBox) hbEncabezado.getChildren().get(i));
            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
            rows++;
            lines += billPrintable.hbEncebezado(box,
                    "AMORTIZAR",
                    "CP",
                    compraTB.getProveedorTB().getNumeroDocumento(),
                    compraTB.getProveedorTB().getRazonSocial(),
                    compraTB.getProveedorTB().getCelular(),
                    compraTB.getProveedorTB().getDireccion(),
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
                    "0",
                    "",
                    "0",
                    "0",
                    "0",
                    "0",
                    "0");
        }

        ObservableList<CompraCreditoTB> arrList = FXCollections.observableArrayList(compraTB.getCompraCreditoTBs());
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
                    "0.00",
                    "0.00",
                    "0.00",
                    compraTB.getProveedorTB().getNumeroDocumento(),
                    compraTB.getProveedorTB().getRazonSocial(),
                    "",
                    compraTB.getProveedorTB().getCelular(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "");
        }
        return billPrintable.modelTicket(rows + lines + 1 + 10, lines, object, nombreImpresora, cortaPapel);
    }

    /**
     * CORTE DE CAJA
     */
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
                    Object arrayList = CajaADO.ListarMovimientoPorById(idCaja);
                    if (arrayList instanceof Object[]) {
                        try {
                            Object object[] = (Object[]) arrayList;
                            CajaTB cajaTB = (CajaTB) object[0];
                            if (desing.equalsIgnoreCase("withdesing")) {
                                return printTicketWithDesingCorteCaja(cajaTB, (ArrayList<Double>) object[1], ticketId, ticketRuta, nombreImpresora, cortaPapel);
                            } else {
                                billPrintable.loadEstructuraTicket(ticketId, ticketRuta, hbEncabezado, hbDetalleCabecera, hbPie);
                                return printTicketNoDesingCorteCaja(cajaTB, (ArrayList<Double>) object[1], nombreImpresora, cortaPapel);
                            }
                        } catch (PrinterException | IOException | PrintException ex) {
                            return "Error en imprimir: " + ex.getLocalizedMessage();
                        }
                    } else if (arrayList instanceof String) {
                        return (String) arrayList;
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

    private String printTicketWithDesingCorteCaja(CajaTB cajaTB, ArrayList<Double> movimientoCajaTBs, int ticketId, String ticketRuta, String nombreImpresora, boolean cortaPapel) throws PrinterException, PrintException, IOException {
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
                    "",
                    "",
                    Tools.roundingValue(movimientoCajaTBs.get(0), 2),
                    Tools.roundingValue(movimientoCajaTBs.get(1), 2),
                    Tools.roundingValue(movimientoCajaTBs.get(2), 2),
                    Tools.roundingValue(movimientoCajaTBs.get(3), 2),
                    Tools.roundingValue(movimientoCajaTBs.get(4), 2)
            );
        }

        AnchorPane hbDetalle = new AnchorPane();
//        ObservableList<Double> arrList = FXCollections.observableArrayList(movimientoCajaTBs);
//        for (int m = 0; m < arrList.size(); m++) {
//            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
//                HBox hBox = new HBox();
//                hBox.setId("dc_" + m + "" + i);
//                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
//                billPrintable.hbDetalleCorteCaja(hBox, box, arrList, m);
//                hbDetalle.getChildren().add(hBox);
//            }
//        }

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
                    "0.00",
                    "0.00",
                    "0.00",
                    "",
                    "",
                    "",
                    "",
                    "",
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

    private String printTicketNoDesingCorteCaja(CajaTB cajaTB, ArrayList<Double> movimientoCajaTBs, String nombreImpresora, boolean cortaPapel) {
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
                    "",
                    "",
                    Tools.roundingValue(movimientoCajaTBs.get(0), 2),
                    Tools.roundingValue(movimientoCajaTBs.get(1), 2),
                    Tools.roundingValue(movimientoCajaTBs.get(2), 2),
                    Tools.roundingValue(movimientoCajaTBs.get(3), 2),
                    Tools.roundingValue(movimientoCajaTBs.get(4), 2)
            );
        }

//        ObservableList<Double> arrList = FXCollections.observableArrayList(movimientoCajaTBs);
//        for (int m = 0; m < arrList.size(); m++) {
//            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
//                HBox hBox = new HBox();
//                hBox.setId("dc_" + m + "" + i);
//                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
//                rows++;
//                lines += billPrintable.hbDetalleCorteCaja(hBox, box, arrList, m);
//                object.add(hBox);
//            }
//        }
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
                    "0.00",
                    "0.00",
                    "0.00",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "");
        }
        return billPrintable.modelTicket(rows + lines + 1 + 10, lines, object, nombreImpresora, cortaPapel);
    }

    /**
     * COTIZACION
     */
    private void executeProcessCotizacionTicket(String desing, int ticketId, String ticketRuta, String nombreImpresora, boolean cortaPapel) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<String> task = new Task<String>() {
            @Override
            public String call() {
                Object object = CotizacionADO.CargarCotizacionReporte(idCotizacion);
                if (object instanceof CotizacionTB) {
                    try {
                        CotizacionTB cotizacionTB = (CotizacionTB) object;
                        if (desing.equalsIgnoreCase("withdesing")) {
                            return printTicketWithDesingCotizacion(cotizacionTB, ticketId, ticketRuta, nombreImpresora, cortaPapel);
                        } else {
                            return "empty";
                        }
                    } catch (PrinterException | IOException | PrintException ex) {
                        return "Error en imprimir: " + ex.getLocalizedMessage();
                    }
                } else {
                    return (String) object;
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

    private String printTicketWithDesingCotizacion(CotizacionTB cotizacionTB, int ticketId, String ticketRuta, String nombreImpresora, boolean cortaPapel) throws PrinterException, PrintException, IOException {
        billPrintable.loadEstructuraTicket(ticketId, ticketRuta, hbEncabezado, hbDetalleCabecera, hbPie);

        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
            billPrintable.hbEncebezado(box,
                    "COTIZACIÓN",
                    "N° " + cotizacionTB.getIdCotizacion(),
                    cotizacionTB.getClienteTB().getNumeroDocumento(),
                    cotizacionTB.getClienteTB().getInformacion(),
                    cotizacionTB.getClienteTB().getCelular(),
                    cotizacionTB.getClienteTB().getDireccion(),
                    "CODIGO PROCESO",
                    "IMPORTE EN LETRAS",
                    cotizacionTB.getFechaCotizacion(),
                    cotizacionTB.getHoraCotizacion(),
                    cotizacionTB.getFechaCotizacion(),
                    cotizacionTB.getHoraCotizacion(),
                    "0",
                    "0",
                    "0",
                    "-",
                    cotizacionTB.getEmpleadoTB().getApellidos() + " " + cotizacionTB.getEmpleadoTB().getNombres(),
                    "-",
                    "-",
                    "",
                    "",
                    "",
                    "",
                    "0",
                    "0",
                    "0",
                    "0",
                    "0");
        }

        AnchorPane hbDetalle = new AnchorPane();
        ObservableList<SuministroTB> arrList = FXCollections.observableArrayList(cotizacionTB.getDetalleSuministroTBs());
        for (int m = 0; m < arrList.size(); m++) {
            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                HBox hBox = new HBox();
                hBox.setId("dc_" + m + "" + i);
                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                billPrintable.hbDetalle(hBox, box, arrList, m);
                hbDetalle.getChildren().add(hBox);
            }
        }

        double totalBruto = 0;
        double totalDescuento = 0;
        double totalSubTotal = 0;
        double totalImpuesto = 0;
        double totalNeto = 0;

        for (SuministroTB suministroTB : arrList) {
            totalBruto += suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralUnico();
            totalDescuento += suministroTB.getDescuentoSumado();
            totalSubTotal += suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal();
            totalImpuesto += suministroTB.getImpuestoSumado();
            totalNeto += suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneral();
        }

        for (int i = 0; i < hbPie.getChildren().size(); i++) {
            HBox box = ((HBox) hbPie.getChildren().get(i));
            billPrintable.hbPie(box,
                    cotizacionTB.getMonedaTB().getSimbolo(),
                    Tools.roundingValue(totalBruto, 2),
                    Tools.roundingValue(totalDescuento, 2),
                    Tools.roundingValue(totalSubTotal, 2),
                    Tools.roundingValue(totalImpuesto, 2),
                    Tools.roundingValue(totalSubTotal, 2),
                    Tools.roundingValue(totalNeto, 2),
                    "TARJETA",
                    "EFECTIVO",
                    "VUELTO",
                    cotizacionTB.getClienteTB().getNumeroDocumento(),
                    cotizacionTB.getClienteTB().getInformacion(),
                    "CODIGO VENTA",
                    cotizacionTB.getClienteTB().getCelular(),
                    "IMPORTE EN LETRAS",
                    "DOCUMENTO EMPLEADO",
                    cotizacionTB.getEmpleadoTB().getApellidos() + " " + cotizacionTB.getEmpleadoTB().getNombres(),
                    "CELULAR EMPLEADO",
                    "DIRECCION EMPLEADO",
                    cotizacionTB.getObservaciones());
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

    private void executeProcessCotizacionReporte() {
        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<Object> task = new Task<Object>() {
            @Override
            public Object call() {
                return CotizacionADO.CargarCotizacionReporte(idCotizacion);
            }
        };
        task.setOnScheduled(w -> {
            Tools.showAlertNotification("/view/image/information_large.png",
                    "Generando reporte",
                    "Se envió los datos para generar\n el reporte.",
                    Duration.seconds(5),
                    Pos.BOTTOM_RIGHT);
        });
        task.setOnFailed(w -> {
            Tools.showAlertNotification("/view/image/warning_large.png",
                    "Generando reporte",
                    "Se produjo un problema al generar.",
                    Duration.seconds(10),
                    Pos.BOTTOM_RIGHT);
        });
        task.setOnSucceeded(w -> {
            Object object = task.getValue();
            if (object instanceof CotizacionTB) {
                CotizacionTB cotizacionTB = (CotizacionTB) object;
                printA4WithDesingCotizacion(cotizacionTB);
                Tools.showAlertNotification("/view/image/succes_large.png",
                        "Generando reporte",
                        "Se genero correctamente el reporte.",
                        Duration.seconds(5),
                        Pos.BOTTOM_RIGHT);

            } else {
                Tools.showAlertNotification("/view/image/error_large.png",
                        "Generando reporte",
                        (String)object,
                        Duration.seconds(10),
                        Pos.CENTER);
            }
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void printA4WithDesingCotizacion(CotizacionTB cotizacionTB) {
        try {
            double subTotalReporte = 0;
            double descuentoTotalReporte = 0;
            double subTotalImporteReporte = 0;
            double totalImpuestoReporte = 0;
            double totalImporteReporte = 0;
            int count = 0;

            ArrayList<SuministroTB> list = new ArrayList();
            for (SuministroTB suministroTB : cotizacionTB.getDetalleSuministroTBs()) {
                subTotalReporte += suministroTB.getImporteBruto();
                descuentoTotalReporte += suministroTB.getDescuentoSumado();
                subTotalImporteReporte += suministroTB.getSubImporteNeto();
                totalImpuestoReporte += suministroTB.getImpuestoSumado();
                totalImporteReporte += suministroTB.getImporteNeto();

                count++;
                SuministroTB stb = new SuministroTB();
                stb.setId(count + 1);
                stb.setClave(suministroTB.getClave());
                stb.setNombreMarca(suministroTB.getNombreMarca());
                stb.setCantidad(suministroTB.getCantidad());
                stb.setUnidadCompraName(suministroTB.getUnidadCompraName());
                stb.setImporteBruto(suministroTB.getImporteBruto());
                stb.setDescuentoSumado(suministroTB.getDescuentoSumado());
                stb.setSubImporteNeto(suministroTB.getSubImporteNeto());
                stb.setImpuestoSumado(suministroTB.getImpuestoSumado());
                stb.setPrecioVentaGeneral(suministroTB.getPrecioVentaGeneral());
                stb.setDescuento(suministroTB.getDescuento());
                stb.setImporteNeto(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneral());
                list.add(stb);
            }

            InputStream imgInputStreamIcon = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            InputStream imgInputStream = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            if (Session.COMPANY_IMAGE != null) {
                imgInputStream = new ByteArrayInputStream(Session.COMPANY_IMAGE);
            }

            InputStream dir = getClass().getResourceAsStream("/report/Cotizacion.jasper");

            Map map = new HashMap();
            map.put("LOGO", imgInputStream);
            map.put("ICON", imgInputStreamIcon);
            map.put("EMPRESA", Session.COMPANY_RAZON_SOCIAL);
            map.put("DIRECCION", Session.COMPANY_DOMICILIO);
            map.put("TELEFONOCELULAR", "TELÉFONO: " + Session.COMPANY_TELEFONO + " CELULAR: " + Session.COMPANY_CELULAR);
            map.put("EMAIL", "EMAIL: " + Session.COMPANY_EMAIL);

            map.put("DOCUMENTOEMPRESA", "R.U.C " + Session.COMPANY_NUMERO_DOCUMENTO);
            map.put("NOMBREDOCUMENTO", "COTIZACIÓN");
            map.put("NUMERODOCUMENTO", "N° " + cotizacionTB.getIdCotizacion());

            map.put("DATOSCLIENTE", cotizacionTB.getClienteTB().getInformacion());
            map.put("DOCUMENTOCLIENTE", "");
            map.put("NUMERODOCUMENTOCLIENTE", cotizacionTB.getClienteTB().getNumeroDocumento());
            map.put("CELULARCLIENTE", cotizacionTB.getClienteTB().getCelular());
            map.put("EMAILCLIENTE", cotizacionTB.getClienteTB().getEmail());
            map.put("DIRECCIONCLIENTE", cotizacionTB.getClienteTB().getDireccion());

            map.put("FECHAEMISION", cotizacionTB.getFechaCotizacion());
            map.put("MONEDA", cotizacionTB.getMonedaTB().getNombre());
            map.put("CONDICIONPAGO", "");

            map.put("SIMBOLO", cotizacionTB.getMonedaTB().getSimbolo());
            map.put("VALORSOLES", monedaCadena.Convertir(Tools.roundingValue(totalImporteReporte, 2), true, cotizacionTB.getMonedaTB().getNombre()));

            map.put("VALOR_VENTA", Tools.roundingValue(subTotalReporte, 2));
            map.put("DESCUENTO", Tools.roundingValue(descuentoTotalReporte, 2));
            map.put("SUB_IMPORTE", Tools.roundingValue(subTotalImporteReporte, 2));
            map.put("IMPUESTO_TOTAL", Tools.roundingValue(totalImpuestoReporte, 2));
            map.put("IMPORTE_TOTAL", Tools.roundingValue(totalImporteReporte, 2));
            map.put("OBSERVACION", cotizacionTB.getObservaciones());

            JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JRBeanCollectionDataSource(list));

            URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxReportViewController controller = fXMLLoader.getController();
            controller.setFileName("COTIZACION N° " + cotizacionTB.getIdCotizacion());
            controller.setJasperPrint(jasperPrint);
            controller.show();
            Stage stage = WindowStage.StageLoader(parent, "Cotizacion");
            stage.setResizable(true);
            stage.show();
            stage.requestFocus();

        } catch (IOException | JRException ex) {
            Tools.showAlertNotification("/view/image/error_large.png",
                    "Reporte de Cotización",
                    "Error al generar el reporte : " + ex.getLocalizedMessage(),
                    Duration.seconds(10),
                    Pos.CENTER);
        }
    }

    /**
     * HISTORIAL DE SALIDA DEL PRODUCTO
     */
    private void executeProcessHistorialSalidaProducto(String desing, int ticketId, String ticketRuta, String nombreImpresora, boolean cortaPapel) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<String> task = new Task<String>() {
            @Override
            public String call() {
                if (!Tools.isText(idVenta) && !Tools.isText(idSuministro)) {
                    Object object = VentaADO.ListarHistorialSuministroLlevar(idVenta, idSuministro);

                    if (object instanceof Object[]) {
                        Object[] objects = (Object[]) object;

                        if (objects[0] != null && objects[1] != null) {
                            try {
                                VentaTB ventaTB = (VentaTB) objects[0];
                                DetalleVentaTB detalleVentaTB = (DetalleVentaTB) objects[1];
                                ArrayList<HistorialSuministroSalidaTB> suministroSalidas = (ArrayList<HistorialSuministroSalidaTB>) objects[2];
                                if (desing.equalsIgnoreCase("withdesing")) {
                                    return printTicketWithDesingHistorialSalidaProducto(ventaTB, detalleVentaTB, suministroSalidas, ticketId, ticketRuta, nombreImpresora, cortaPapel);
                                } else {
                                    billPrintable.loadEstructuraTicket(ticketId, ticketRuta, hbEncabezado, hbDetalleCabecera, hbPie);
                                    return printTicketNoDesingHistorialSalidaProducto(ventaTB, detalleVentaTB, suministroSalidas, nombreImpresora, cortaPapel);
                                }
                            } catch (PrinterException | IOException | PrintException ex) {
                                return "Error en imprimir: " + ex.getLocalizedMessage();
                            }
                        } else {
                            return "hay valores nulos en traer los datos";
                        }
                    } else if (object instanceof String) {
                        return (String) object;
                    } else {
                        return "no se puedo cargar los datos";
                    }
                } else {
                    return "id no inicializados";
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

    private String printTicketWithDesingHistorialSalidaProducto(VentaTB ventaTB, DetalleVentaTB detalleVentaTB, ArrayList<HistorialSuministroSalidaTB> suministroSalidas, int ticketId, String ticketRuta, String nombreImpresora, boolean cortaPapel) throws PrinterException, PrintException, IOException {
        billPrintable.loadEstructuraTicket(ticketId, ticketRuta, hbEncabezado, hbDetalleCabecera, hbPie);

        double cantidadActual = 0;
        cantidadActual = suministroSalidas.stream().map((hs) -> hs.getCantidad()).reduce(cantidadActual, (accumulator, _item) -> accumulator + _item);

        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
            billPrintable.hbEncebezado(box,
                    "HISTORIAL DE SALIDA",
                    ventaTB.getSerie() + "-" + ventaTB.getNumeracion(),
                    ventaTB.getClienteTB().getNumeroDocumento(),
                    ventaTB.getClienteTB().getInformacion(),
                    ventaTB.getClienteTB().getCelular(),
                    ventaTB.getClienteTB().getDireccion(),
                    "",
                    "",
                    ventaTB.getFechaVenta(),
                    ventaTB.getHoraVenta(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    Tools.roundingValue(detalleVentaTB.getCantidad(), 2),
                    Tools.roundingValue(cantidadActual, 2),
                    Tools.roundingValue(detalleVentaTB.getCantidad() - cantidadActual, 2),
                    detalleVentaTB.getSuministroTB().getNombreMarca(),
                    "0",
                    "0",
                    "0",
                    "0",
                    "0"
            );
        }

        AnchorPane hbDetalle = new AnchorPane();
        ArrayList<HistorialSuministroSalidaTB> arrList = suministroSalidas;
        for (int m = 0; m < arrList.size(); m++) {
            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                HBox hBox = new HBox();
                hBox.setId("dc_" + m + "" + i);
                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                billPrintable.hbDetalleHistorialSumistroSalida(hBox, box, arrList, m);
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
                    "0.00",
                    "0.00",
                    "0.00",
                    ventaTB.getClienteTB().getNumeroDocumento(),
                    ventaTB.getClienteTB().getInformacion(),
                    "",
                    ventaTB.getClienteTB().getCelular(),
                    "",
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

    private String printTicketNoDesingHistorialSalidaProducto(VentaTB ventaTB, DetalleVentaTB detalleVentaTB, ArrayList<HistorialSuministroSalidaTB> suministroSalidas, String nombreImpresora, boolean cortaPapel) {
        ArrayList<HBox> object = new ArrayList<>();

        double cantidadActual = 0;
        cantidadActual = suministroSalidas.stream().map((hs) -> hs.getCantidad()).reduce(cantidadActual, (accumulator, _item) -> accumulator + _item);

        int rows = 0;
        int lines = 0;
        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
            object.add((HBox) hbEncabezado.getChildren().get(i));
            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
            rows++;
            lines += billPrintable.hbEncebezado(box,
                    "HISTORIAL DE SALIDA",
                    ventaTB.getSerie() + "-" + ventaTB.getNumeracion(),
                    ventaTB.getClienteTB().getNumeroDocumento(),
                    ventaTB.getClienteTB().getInformacion(),
                    ventaTB.getClienteTB().getCelular(),
                    ventaTB.getClienteTB().getDireccion(),
                    "",
                    "",
                    ventaTB.getFechaVenta(),
                    ventaTB.getHoraVenta(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    Tools.roundingValue(detalleVentaTB.getCantidad(), 2),
                    Tools.roundingValue(cantidadActual, 2),
                    Tools.roundingValue(detalleVentaTB.getCantidad() - cantidadActual, 2),
                    detalleVentaTB.getSuministroTB().getNombreMarca(),
                    "0",
                    "0",
                    "0",
                    "0",
                    "0"
            );
        }

        ArrayList<HistorialSuministroSalidaTB> arrList = suministroSalidas;
        for (int m = 0; m < arrList.size(); m++) {
            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                HBox hBox = new HBox();
                hBox.setId("dc_" + m + "" + i);
                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                rows++;
                lines += billPrintable.hbDetalleHistorialSumistroSalida(hBox, box, arrList, m);
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
                    "0.00",
                    "0.00",
                    "0.00",
                    ventaTB.getClienteTB().getNumeroDocumento(),
                    ventaTB.getClienteTB().getInformacion(),
                    "",
                    ventaTB.getClienteTB().getCelular(),
                    "",
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
    private void onKeyPressedImprimirTicket(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventTicket();
        }
    }

    @FXML
    private void onActionImprimirTicket(ActionEvent event) {
        onEventTicket();
    }

    @FXML
    private void onKeyPressedImprimirA4(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEvent4a();
        }
    }

    @FXML
    private void onActionImprimirA4(ActionEvent event) {
        onEvent4a();
    }

    public void setInitOpcionesImprimirCuentasPorCobrar(FxCuentasPorCobrarVisualizarController cuentasPorCobrarVisualizarController) {
        this.cuentasPorCobrarVisualizarController = cuentasPorCobrarVisualizarController;
    }

    public void setInitOpcionesImprimirCuentasPorPagar(FxCuentasPorPagarVisualizarController cuentasPorPagarVisualizarController) {
        this.cuentasPorPagarVisualizarController = cuentasPorPagarVisualizarController;
    }

    public void setInitOpcionesImprimirCorteCaja(FxCajaController cajaController) {
        this.cajaController = cajaController;
    }

    public void setInitOpcionesImprimirCorteCaja(FxCajaConsultasController cajaConsultasController) {
        this.cajaConsultasController = cajaConsultasController;
    }

    public void setInitOpcionesImprimirCotizacion(FxCotizacionController cotizacionController) {
        this.cotizacionController = cotizacionController;
    }

    public void setInitOpcionesVentaLlevar(FxVentaLlevarControllerHistorial ventaLlevarControllerHistorial) {
        this.ventaLlevarControllerHistorial = ventaLlevarControllerHistorial;
    }

}
