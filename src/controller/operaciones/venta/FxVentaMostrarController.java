package controller.operaciones.venta;

import controller.tools.BillPrintable;
import controller.tools.ConvertMonedaCadena;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.Tools;
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
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;
import model.SuministroTB;
import model.VentaADO;
import model.VentaTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

public class FxVentaMostrarController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private TextField txtSearch;
    @FXML
    private Label lblLoad;
    @FXML
    private TableView<VentaTB> tvList;
    @FXML
    private TableColumn<VentaTB, String> tcNumero;
    @FXML
    private TableColumn<VentaTB, String> tcCliente;
    @FXML
    private TableColumn<VentaTB, String> tcDocumento;
    @FXML
    private TableColumn<VentaTB, String> tcFechaHora;
    @FXML
    private TableColumn<VentaTB, String> tcTotal;
    @FXML
    private TableColumn<VentaTB, Button> tcImprimir;
    @FXML
    private TableColumn<VentaTB, Button> tcAgregarVenta;
    @FXML
    private TableColumn<VentaTB, Button> tcSumarVenta;

    private FxVentaEstructuraController ventaEstructuraController;

    private BillPrintable billPrintable;

    private ConvertMonedaCadena monedaCadena;

    private AnchorPane hbEncabezado;

    private AnchorPane hbDetalleCabecera;

    private AnchorPane hbPie;

    private int paginacion;

    private int totalPaginacion;

    private short opcion;
    @FXML
    private Label lblPaginaActual;
    @FXML
    private Label lblPaginaSiguiente;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
        tcNumero.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcCliente.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getClienteTB().getInformacion()));
        tcFechaHora.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getFechaVenta() + "\n"
                + cellData.getValue().getHoraVenta()
        ));
        tcDocumento.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getSerie() + "-" + cellData.getValue().getNumeracion()
        ));
        tcTotal.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMonedaName() + " " + Tools.roundingValue(cellData.getValue().getTotal(), 2)));
        tcImprimir.setCellValueFactory(new PropertyValueFactory<>("btnImprimir"));
        tcAgregarVenta.setCellValueFactory(new PropertyValueFactory<>("btnAgregar"));
        tcSumarVenta.setCellValueFactory(new PropertyValueFactory<>("btnSumar"));
        tvList.setPlaceholder(Tools.placeHolderTableView("Ingrese la información a buscar.", "-fx-text-fill:#020203;", false));

        billPrintable = new BillPrintable();
        monedaCadena = new ConvertMonedaCadena();
        hbEncabezado = new AnchorPane();
        hbDetalleCabecera = new AnchorPane();
        hbPie = new AnchorPane();

        paginacion = 1;
        opcion = 0;
    }

    private void fillVentasTable(int opcion, String value) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<Object> task = new Task<Object>() {
            @Override
            public Object call() {
                return VentaADO.ListVentasMostrar(opcion, value, (paginacion - 1) * 10, 10);
            }
        };

        task.setOnSucceeded(e -> {
            Object result = task.getValue();
            if (result instanceof Object[]) {
                Object[] object = (Object[]) result;
                ObservableList<VentaTB> ventaTBs = (ObservableList<VentaTB>) object[0];
                if (!ventaTBs.isEmpty()) {
                    ventaTBs.forEach(f -> {
                        f.getBtnImprimir().setOnAction(event -> {
                            imprimirVenta(f.getIdVenta());
                        });
                        f.getBtnImprimir().setOnKeyPressed(event -> {
                            if (event.getCode() == KeyCode.ENTER) {
                                imprimirVenta(f.getIdVenta());
                            }
                            event.consume();
                        });
                        f.getBtnAgregar().setOnAction(event -> {
                            addVenta(f.getIdVenta());
                        });
                        f.getBtnAgregar().setOnKeyPressed(event -> {
                            if (event.getCode() == KeyCode.ENTER) {
                                addVenta(f.getIdVenta());
                            }
                            event.consume();
                        });

                        f.getBtnSumar().setOnAction(event -> {
                            plusVenta(f.getIdVenta());
                        });
                        f.getBtnSumar().setOnKeyPressed(event -> {
                            if (event.getCode() == KeyCode.ENTER) {
                                plusVenta(f.getIdVenta());
                            }
                        });
                    });
                    tvList.setItems(ventaTBs);
                    totalPaginacion = (int) (Math.ceil(((Integer) object[1]) / 10.00));
                    lblPaginaActual.setText(paginacion + "");
                    lblPaginaSiguiente.setText(totalPaginacion + "");
                } else {
                    tvList.setPlaceholder(Tools.placeHolderTableView("No hay datos para mostrar.", "-fx-text-fill:#020203;", false));
                    lblPaginaActual.setText("0");
                    lblPaginaSiguiente.setText("0");
                }
            } else {
                tvList.setPlaceholder(Tools.placeHolderTableView((String) result, "-fx-text-fill:#a70820;", false));
            }
            lblLoad.setVisible(false);
        });
        task.setOnFailed(e -> {
            lblLoad.setVisible(false);
            tvList.setPlaceholder(Tools.placeHolderTableView(task.getException().getLocalizedMessage(), "-fx-text-fill:#a70820;", false));
        });

        task.setOnScheduled(e -> {
            lblLoad.setVisible(true);
            tvList.getItems().clear();
            tvList.setPlaceholder(Tools.placeHolderTableView("Cargando información...", "-fx-text-fill:#020203;", true));
            totalPaginacion = 0;
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    public void addVenta(String idVenta) {
        if (ventaEstructuraController != null) {
            ventaEstructuraController.resetVenta();
            Tools.Dispose(apWindow);
            ventaEstructuraController.loadAddVenta(idVenta);
        }
    }

    public void plusVenta(String idVenta) {
        if (ventaEstructuraController != null) {
            Tools.Dispose(apWindow);
            ventaEstructuraController.loadPlusVenta(idVenta);
        }
    }

    public void imprimirVenta(String idVenta) {
        if (!Session.ESTADO_IMPRESORA_VENTA && Tools.isText(Session.NOMBRE_IMPRESORA_VENTA) && Tools.isText(Session.FORMATO_IMPRESORA_VENTA)) {
            Tools.AlertMessageWarning(apWindow, "Venta", "No esta configurado la ruta de impresión, ve a la sección configuración/impresora.");
            return;
        }

        if (Session.FORMATO_IMPRESORA_VENTA.equalsIgnoreCase("ticket")) {
            if (Session.TICKET_VENTA_ID == 0 && Tools.isText(Session.TICKET_VENTA_RUTA)) {
                Tools.AlertMessageWarning(apWindow, "Venta", "No hay un diseño predeterminado para la impresión, configure su ticket en la sección configuración/tickets.");
            } else {
                executeProcessPrinterVenta(idVenta, Session.NOMBRE_IMPRESORA_VENTA, Session.CORTAPAPEL_IMPRESORA_VENTA, Session.FORMATO_IMPRESORA_VENTA, Session.DESING_IMPRESORA_VENTA);
            }
        } else if (Session.FORMATO_IMPRESORA_VENTA.equalsIgnoreCase("a4")) {
            executeProcessPrinterVenta(idVenta, Session.NOMBRE_IMPRESORA_VENTA, Session.CORTAPAPEL_IMPRESORA_VENTA, Session.FORMATO_IMPRESORA_VENTA, Session.DESING_IMPRESORA_VENTA);
        } else {
            Tools.AlertMessageWarning(apWindow, "Venta", "Error al validar el formato de impresión, configure en la sección configuración/impresora.");
        }
    }

    private JasperPrint reportA4(VentaTB ventaTB, ArrayList<SuministroTB> list) throws JRException {

        InputStream imgInputStreamIcon = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

        InputStream imgInputStream = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

        if (Session.COMPANY_IMAGE != null) {
            imgInputStream = new ByteArrayInputStream(Session.COMPANY_IMAGE);
        }

        InputStream dir = getClass().getResourceAsStream("/report/VentaRealizada.jasper");

        Map map = new HashMap();
        map.put("LOGO", imgInputStream);
        map.put("ICON", imgInputStreamIcon);
        map.put("EMPRESA", Session.COMPANY_RAZON_SOCIAL);
        map.put("DIRECCION", Session.COMPANY_DOMICILIO);
        map.put("TELEFONOCELULAR", "TELÉFONO: " + Session.COMPANY_TELEFONO + " CELULAR: " + Session.COMPANY_CELULAR);
        map.put("EMAIL", "EMAIL: " + Session.COMPANY_EMAIL);

        map.put("DOCUMENTOEMPRESA", "R.U.C " + Session.COMPANY_NUMERO_DOCUMENTO);
        map.put("NOMBREDOCUMENTO", ventaTB.getComprobanteName());
        map.put("NUMERODOCUMENTO", ventaTB.getSerie() + "-" + ventaTB.getNumeracion());

        map.put("DATOSCLIENTE", ventaTB.getClienteTB().getInformacion());
        map.put("DOCUMENTOCLIENTE", ventaTB.getClienteTB().getTipoDocumentoName() + " N°:");
        map.put("NUMERODOCUMENTOCLIENTE", ventaTB.getClienteTB().getNumeroDocumento());
        map.put("CELULARCLIENTE", ventaTB.getClienteTB().getCelular().equals("") ? "---" : ventaTB.getClienteTB().getCelular());
        map.put("EMAILCLIENTE", ventaTB.getClienteTB().getEmail().equals("") ? "---" : ventaTB.getClienteTB().getEmail());
        map.put("DIRECCIONCLIENTE", ventaTB.getClienteTB().getDireccion().equals("") ? "---" : ventaTB.getClienteTB().getDireccion());

        map.put("FECHAEMISION", ventaTB.getFechaVenta());
        map.put("MONEDA", ventaTB.getMonedaTB().getNombre() + "-" + ventaTB.getMonedaTB().getAbreviado());
        map.put("CONDICIONPAGO", ventaTB.getTipoName() + "-" + ventaTB.getEstadoName());

        map.put("SIMBOLO", ventaTB.getMonedaTB().getSimbolo());
        map.put("VALORSOLES", monedaCadena.Convertir(Tools.roundingValue(ventaTB.getTotal(), 2), true, ventaTB.getMonedaTB().getNombre()));

        map.put("VALOR_VENTA", ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(ventaTB.getSubTotal(), 2));
        map.put("DESCUENTO", ventaTB.getMonedaTB().getSimbolo() + " -" + Tools.roundingValue(ventaTB.getDescuento(), 2));
        map.put("SUB_IMPORTE", ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(ventaTB.getSubImporte(), 2));
        map.put("IMPUESTO_TOTAL", Tools.roundingValue(ventaTB.getImpuesto(), 2));
        map.put("IMPORTE_TOTAL", ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(ventaTB.getTotal(), 2));
        map.put("QRDATA", Session.COMPANY_NUMERO_DOCUMENTO + "|" + ventaTB.getCodigoAlterno() + "|" + ventaTB.getSerie() + "|" + ventaTB.getNumeracion() + "|" + Tools.roundingValue(ventaTB.getImpuesto(), 2) + "|" + Tools.roundingValue(ventaTB.getTotal(), 2) + "|" + ventaTB.getFechaVenta() + "|" + ventaTB.getClienteTB().getIdAuxiliar() + "|" + ventaTB.getClienteTB().getNumeroDocumento() + "|");

        JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JRBeanCollectionDataSource(list));
        return jasperPrint;
    }

    private void executeProcessPrinterVenta(String idVenta, String printerName, boolean printerCut, String format, String desing) {
        if (Tools.isText(idVenta)) {
            return;
        }
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<String> task = new Task<String>() {
            @Override
            public String call() {
                Object object = VentaADO.ListCompletaVentasDetalle(idVenta);
                try {
                    if (object instanceof VentaTB) {
                        VentaTB ventaTB = (VentaTB) object;
                        ObservableList<SuministroTB> suministroTBs = FXCollections.observableArrayList(ventaTB.getSuministroTBs());
                        if (format.equalsIgnoreCase("a4")) {
                            ArrayList<SuministroTB> list = new ArrayList();
                            for (int i = 0; i < suministroTBs.size(); i++) {
                                SuministroTB stb = new SuministroTB();
                                stb.setId(i + 1);
                                stb.setClave(suministroTBs.get(i).getClave());
                                stb.setNombreMarca(suministroTBs.get(i).getNombreMarca());
                                stb.setCantidad(suministroTBs.get(i).getCantidad());
                                stb.setUnidadCompraName(suministroTBs.get(i).getUnidadCompraName());
                                stb.setPrecioVentaGeneral(suministroTBs.get(i).getPrecioVentaGeneral());
                                stb.setDescuento(suministroTBs.get(i).getDescuento());
                                stb.setImporteNeto(suministroTBs.get(i).getCantidad() * +suministroTBs.get(i).getPrecioVentaGeneral());
                                list.add(stb);
                            }

                            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
                            printRequestAttributeSet.add(new Copies(1));

                            PrinterName pn = new PrinterName(printerName, null);

                            PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
                            printServiceAttributeSet.add(pn);

                            JRPrintServiceExporter exporter = new JRPrintServiceExporter();

                            exporter.setParameter(JRExporterParameter.JASPER_PRINT, reportA4(ventaTB, list));
                            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
                            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceAttributeSet);
                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
                            exporter.exportReport();
                            return "completed";
                        } else {
                            if (desing.equalsIgnoreCase("withdesing")) {
                                billPrintable.loadEstructuraTicket(Session.TICKET_VENTA_ID, Session.TICKET_VENTA_RUTA, hbEncabezado, hbDetalleCabecera, hbPie);
                                ObjectGlobal.QR_PERU_DATA = "|" + Session.COMPANY_NUMERO_DOCUMENTO + "|" + ventaTB.getCodigoAlterno() + "|" + ventaTB.getSerie() + "|" + ventaTB.getNumeracion() + "|" + Tools.roundingValue(ventaTB.getImpuesto(), 2) + "|" + Tools.roundingValue(ventaTB.getTotal(), 2) + "|" + ventaTB.getFechaVenta() + "|" + ventaTB.getClienteTB().getIdAuxiliar() + "|" + ventaTB.getClienteTB().getNumeroDocumento() + "|";

                                for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
                                    HBox box = ((HBox) hbEncabezado.getChildren().get(i));
                                    billPrintable.hbEncebezado(box,
                                            ventaTB.getComprobanteName(),
                                            ventaTB.getSerie() + "-" + ventaTB.getNumeracion(),
                                            ventaTB.getClienteTB().getNumeroDocumento(),
                                            ventaTB.getClienteTB().getInformacion(),
                                            ventaTB.getClienteTB().getCelular(),
                                            ventaTB.getClienteTB().getDireccion(),
                                            ventaTB.getCodigo(),
                                            monedaCadena.Convertir(Tools.roundingValue(ventaTB.getTotal(), 2), true, ventaTB.getMonedaTB().getNombre()),
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
                                for (int m = 0; m < suministroTBs.size(); m++) {
                                    for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                                        HBox hBox = new HBox();
                                        hBox.setId("dc_" + m + "" + i);
                                        HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                                        billPrintable.hbDetalle(hBox, box, suministroTBs, m);
                                        hbDetalle.getChildren().add(hBox);
                                    }
                                }

                                for (int i = 0; i < hbPie.getChildren().size(); i++) {
                                    HBox box = ((HBox) hbPie.getChildren().get(i));
                                    billPrintable.hbPie(box, ventaTB.getMonedaTB().getSimbolo(),
                                            Tools.roundingValue(ventaTB.getSubImporte(), 2),
                                            "-" + Tools.roundingValue(ventaTB.getDescuento(), 2),
                                            Tools.roundingValue(ventaTB.getSubImporte(), 2),
                                            Tools.roundingValue(ventaTB.getImpuesto(), 2),
                                            Tools.roundingValue(ventaTB.getSubImporte(), 2),
                                            Tools.roundingValue(ventaTB.getTotal(), 2),
                                            Tools.roundingValue(ventaTB.getTarjeta(), 2),
                                            Tools.roundingValue(ventaTB.getEfectivo(), 2),
                                            Tools.roundingValue(ventaTB.getVuelto(), 2),
                                            ventaTB.getClienteTB().getNumeroDocumento(),
                                            ventaTB.getClienteTB().getInformacion(), ventaTB.getCodigo(),
                                            ventaTB.getClienteTB().getCelular(),
                                            monedaCadena.Convertir(Tools.roundingValue(ventaTB.getTotal(), 2), true, ventaTB.getMonedaTB().getNombre()),
                                            "",
                                            "",
                                            "",
                                            "",
                                            "");
                                }

                                billPrintable.generatePDFPrint(hbEncabezado, hbDetalle, hbPie);

                                DocPrintJob job = billPrintable.findPrintService(printerName, PrinterJob.lookupPrintServices()).createPrintJob();

                                if (job != null) {
                                    PrinterJob pj = PrinterJob.getPrinterJob();
                                    pj.setPrintService(job.getPrintService());
                                    pj.setJobName(printerName);
                                    Book book = new Book();
                                    book.append(billPrintable, billPrintable.getPageFormat(pj));
                                    pj.setPageable(book);
                                    pj.print();
                                    if (printerCut) {
                                        billPrintable.printCortarPapel(printerName);
                                    }
                                    return "completed";
                                } else {
                                    return "error_name";
                                }
                            } else {
                                billPrintable.loadEstructuraTicket(Session.TICKET_VENTA_ID, Session.TICKET_VENTA_RUTA, hbEncabezado, hbDetalleCabecera, hbPie);
                                return imprimirVenta(ventaTB, suministroTBs, printerName, printerCut);
                            }
                        }
                    } else {
                        return (String) object;
                    }
                } catch (PrinterException | IOException | PrintException | JRException ex) {
                    return "Error en imprimir: " + ex.getLocalizedMessage();
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
            } else if (result.equalsIgnoreCase("no_config")) {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "Error al validar el tipo de impresión, cofigure nuevamente la impresora.",
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
                        "Se producto un problema por problemas de la impresora\n" + result,
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

    private String imprimirVenta(VentaTB ventaTB, ObservableList<SuministroTB> suministroTBs, String printerName, boolean printerCut) {
        ArrayList<HBox> object = new ArrayList<>();
        int rows = 0;
        int lines = 0;
        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
            object.add((HBox) hbEncabezado.getChildren().get(i));
            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
            rows++;
            lines += billPrintable.hbEncebezado(box,
                    ventaTB.getComprobanteName(),
                    ventaTB.getSerie() + "-" + ventaTB.getNumeracion(),
                    ventaTB.getClienteTB().getNumeroDocumento(),
                    ventaTB.getClienteTB().getInformacion(),
                    ventaTB.getClienteTB().getCelular(),
                    ventaTB.getClienteTB().getDireccion(),
                    ventaTB.getCodigo(),
                    monedaCadena.Convertir(Tools.roundingValue(ventaTB.getTotal(), 2), true, ventaTB.getMonedaTB().getNombre()),
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

        for (int m = 0; m < suministroTBs.size(); m++) {
            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                HBox hBox = new HBox();
                hBox.setId("dc_" + m + "" + i);
                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                rows++;
                lines += billPrintable.hbDetalle(hBox, box, suministroTBs, m);
                object.add(hBox);
            }
        }

        for (int i = 0; i < hbPie.getChildren().size(); i++) {
            object.add((HBox) hbPie.getChildren().get(i));
            HBox box = ((HBox) hbPie.getChildren().get(i));
            rows++;
            lines += billPrintable.hbPie(box, ventaTB.getMonedaTB().getSimbolo(),
                    Tools.roundingValue(ventaTB.getSubImporte(), 2),
                    "-" + Tools.roundingValue(ventaTB.getDescuento(), 2),
                    Tools.roundingValue(ventaTB.getSubImporte(), 2),
                    Tools.roundingValue(ventaTB.getImpuesto(), 2),
                    Tools.roundingValue(ventaTB.getSubImporte(), 2),
                    Tools.roundingValue(ventaTB.getTotal(), 2),
                    Tools.roundingValue(ventaTB.getTarjeta(), 2),
                    Tools.roundingValue(ventaTB.getEfectivo(), 2),
                    Tools.roundingValue(ventaTB.getVuelto(), 2),
                    ventaTB.getClienteTB().getNumeroDocumento(),
                    ventaTB.getClienteTB().getInformacion(), ventaTB.getCodigo(),
                    ventaTB.getClienteTB().getCelular(),
                    monedaCadena.Convertir(Tools.roundingValue(ventaTB.getTotal(), 2), true, ventaTB.getMonedaTB().getNombre()),
                    "",
                    "",
                    "",
                    "",
                    "");
        }
        return billPrintable.modelTicket(rows + lines + 1 + 5, lines, object, printerName, printerCut);
    }

    private void onEventPaginacion() {
        switch (opcion) {
            case 0:
                fillVentasTable(0, "");
                break;
            case 1:
                fillVentasTable(1, txtSearch.getText().trim());
                break;
        }
    }

    @FXML
    private void onKeyPressed10PrimerasVentas(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                paginacion = 1;
                fillVentasTable(1, txtSearch.getText().trim());
                opcion = 1;
            }
        }
    }

    @FXML
    private void onAction10PrimerasVentas(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            paginacion = 1;
            fillVentasTable(1, txtSearch.getText().trim());
            opcion = 1;
        }
    }

    @FXML
    private void onKeyReleasedSearch(KeyEvent event) {
        if (event.getCode() != KeyCode.ESCAPE
                && event.getCode() != KeyCode.F1
                && event.getCode() != KeyCode.F2
                && event.getCode() != KeyCode.F3
                && event.getCode() != KeyCode.F4
                && event.getCode() != KeyCode.F5
                && event.getCode() != KeyCode.F6
                && event.getCode() != KeyCode.F7
                && event.getCode() != KeyCode.F8
                && event.getCode() != KeyCode.F9
                && event.getCode() != KeyCode.F10
                && event.getCode() != KeyCode.F11
                && event.getCode() != KeyCode.F12
                && event.getCode() != KeyCode.ALT
                && event.getCode() != KeyCode.CONTROL
                && event.getCode() != KeyCode.UP
                && event.getCode() != KeyCode.DOWN
                && event.getCode() != KeyCode.RIGHT
                && event.getCode() != KeyCode.LEFT
                && event.getCode() != KeyCode.TAB
                && event.getCode() != KeyCode.CAPS
                && event.getCode() != KeyCode.SHIFT
                && event.getCode() != KeyCode.HOME
                && event.getCode() != KeyCode.WINDOWS
                && event.getCode() != KeyCode.ALT_GRAPH
                && event.getCode() != KeyCode.CONTEXT_MENU
                && event.getCode() != KeyCode.END
                && event.getCode() != KeyCode.INSERT
                && event.getCode() != KeyCode.PAGE_UP
                && event.getCode() != KeyCode.PAGE_DOWN
                && event.getCode() != KeyCode.NUM_LOCK
                && event.getCode() != KeyCode.PRINTSCREEN
                && event.getCode() != KeyCode.SCROLL_LOCK
                && event.getCode() != KeyCode.PAUSE) {
            if (!Tools.isText(txtSearch.getText())) {
                if (!lblLoad.isVisible()) {
                    paginacion = 1;
                    fillVentasTable(0, txtSearch.getText().trim());
                    opcion = 0;
                }
            }
        }
    }

    @FXML
    private void onKeyPressedAnterior(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                if (paginacion > 1) {
                    paginacion--;
                    onEventPaginacion();
                }
            }
        }
    }

    @FXML
    private void onActionAnterior(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (paginacion > 1) {
                paginacion--;
                onEventPaginacion();
            }
        }
    }

    @FXML
    private void onKeyPressedSiguiente(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                if (paginacion < totalPaginacion) {
                    paginacion++;
                    onEventPaginacion();
                }
            }
        }
    }

    @FXML
    private void onActionSiguiente(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (paginacion < totalPaginacion) {
                paginacion++;
                onEventPaginacion();
            }
        }
    }

    public TextField getTxtSearch() {
        return txtSearch;
    }

    public void setInitControllerVentaEstructura(FxVentaEstructuraController ventaEstructuraController) {
        this.ventaEstructuraController = ventaEstructuraController;
    }

}
