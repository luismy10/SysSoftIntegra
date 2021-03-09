package controller.operaciones.cotizacion;

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
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import model.CotizacionADO;
import model.CotizacionTB;
import model.SuministroTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FxCotizacionDetalleController implements Initializable {

    @FXML
    private ScrollPane spWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private Button btnReporte;
    @FXML
    private Text lblNumero;
    @FXML
    private Text lblFechaVenta;
    @FXML
    private Text lblCliente;
    @FXML
    private Text lbClienteInformacion;
    @FXML
    private Text lbCorreoElectronico;
    @FXML
    private Text lbDireccion;
    @FXML
    private Text lblObservaciones;
    @FXML
    private Text lblVendedor;
    @FXML
    private GridPane gpList;
    @FXML
    private Label lblValorVenta;
    @FXML
    private Label lblDescuento;
    @FXML
    private Label lblSubTotal;
    @FXML
    private GridPane gpImpuestos;
    @FXML
    private Label lblTotal;

    private FxCotizacionRealizadasController cotizacionRealizadasController;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    private CotizacionTB cotizacionTB;

    private ObservableList<SuministroTB> arrList = null;

    private ConvertMonedaCadena monedaCadena;

    private BillPrintable billPrintable;

    private AnchorPane hbEncabezado;

    private AnchorPane hbDetalleCabecera;

    private AnchorPane hbPie;

    private double importeBruto = 0;

    private double descuentoBruto = 0;

    private double subImporteNeto = 0;

    private double impuestoNeto = 0;

    private double importeTotal = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        monedaCadena = new ConvertMonedaCadena();
        billPrintable = new BillPrintable();
        hbEncabezado = new AnchorPane();
        hbDetalleCabecera = new AnchorPane();
        hbPie = new AnchorPane();
    }

    public void setInitComponents(String idCotizacion) {

        ExecutorService executor = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<CotizacionTB> task = new Task<CotizacionTB>() {
            @Override
            protected CotizacionTB call() {
                return CotizacionADO.CargarCotizacionReporte(idCotizacion);
            }
        };
        task.setOnSucceeded(e -> {
            cotizacionTB = task.getValue();
            if (cotizacionTB != null) {

                lblNumero.setText("N° " + cotizacionTB.getIdCotizacion());
                lblFechaVenta.setText(cotizacionTB.getFechaCotizacion());
                lblCliente.setText(cotizacionTB.getClienteTB().getInformacion());
                lbClienteInformacion.setText("TEL.: " + cotizacionTB.getClienteTB().getTelefono() + " CEL.: " + cotizacionTB.getClienteTB().getCelular());
                lbCorreoElectronico.setText(cotizacionTB.getClienteTB().getEmail());
                lbDireccion.setText(cotizacionTB.getClienteTB().getDireccion());
                lblObservaciones.setText(cotizacionTB.getObservaciones());
                lblVendedor.setText(cotizacionTB.getEmpleadoTB().getApellidos() + " " + cotizacionTB.getEmpleadoTB().getNombres());

                ObservableList<SuministroTB> cotizacionTBs = cotizacionTB.getDetalleSuministroTBs();
                fillVentasDetalleTable(cotizacionTBs, cotizacionTB.getMonedaTB().getSimbolo());

                lblLoad.setVisible(false);
            } else {
                btnReporte.setDisable(true);
                lblLoad.setVisible(false);
            }
        });
        task.setOnScheduled(e -> {
            lblLoad.setVisible(true);
        });
        task.setOnFailed(e -> {
            lblLoad.setVisible(false);
        });
        executor.execute(task);
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
    }

    private void fillVentasDetalleTable(ObservableList<SuministroTB> empList, String simbolo) {
        arrList = empList;
        for (int i = 0; i < arrList.size(); i++) {
            gpList.add(addElementGridPane("l1" + (i + 1), arrList.get(i).getId() + "", Pos.CENTER), 0, (i + 1));
            gpList.add(addElementGridPane("l2" + (i + 1), arrList.get(i).getClave() + "\n" + arrList.get(i).getNombreMarca(), Pos.CENTER_LEFT), 1, (i + 1));
            gpList.add(addElementGridPane("l3" + (i + 1), Tools.roundingValue(arrList.get(i).getCantidad(), 2), Pos.CENTER_RIGHT), 2, (i + 1));
            gpList.add(addElementGridPane("l4" + (i + 1), arrList.get(i).getUnidadCompraName(), Pos.CENTER_LEFT), 3, (i + 1));
            gpList.add(addElementGridPane("l5" + (i + 1), arrList.get(i).getImpuestoNombre(), Pos.CENTER_RIGHT), 4, (i + 1));
            gpList.add(addElementGridPane("l6" + (i + 1), simbolo + "" + Tools.roundingValue(arrList.get(i).getPrecioVentaGeneral(), 2), Pos.CENTER_RIGHT), 5, (i + 1));
            gpList.add(addElementGridPane("l7" + (i + 1), Tools.roundingValue(arrList.get(i).getDescuento(), 2) + "%", Pos.CENTER_RIGHT), 6, (i + 1));
            gpList.add(addElementGridPane("l8" + (i + 1), simbolo + "" + Tools.roundingValue(arrList.get(i).getPrecioVentaGeneral() * arrList.get(i).getCantidad(), 2), Pos.CENTER_RIGHT), 7, (i + 1));
        }
        calculateTotales(simbolo);
    }

    private Label addElementGridPane(String id, String nombre, Pos pos) {
        Label label = new Label(nombre);
        label.setId(id);
        label.setStyle("-fx-text-fill:#020203;-fx-background-color: #dddddd;-fx-padding: 0.4166666666666667em 0.8333333333333334em 0.4166666666666667em 0.8333333333333334em;");
        label.getStyleClass().add("labelRoboto13");
        label.setAlignment(pos);
        label.setWrapText(true);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        return label;
    }

    private Label addLabelTitle(String nombre, Pos pos) {
        Label label = new Label(nombre);
        label.setStyle("-fx-text-fill: #020203;-fx-padding:  0.4166666666666667em 0em  0.4166666666666667em 0em;");
        label.getStyleClass().add("labelRoboto13");
        label.setAlignment(pos);
        label.setMinWidth(Control.USE_COMPUTED_SIZE);
        label.setMinHeight(Control.USE_COMPUTED_SIZE);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Control.USE_COMPUTED_SIZE);
        return label;
    }

    private Label addLabelTotal(String nombre, Pos pos) {
        Label label = new Label(nombre);
        label.setStyle("-fx-text-fill:#0771d3;");
        label.getStyleClass().add("labelRobotoMedium15");
        label.setAlignment(pos);
        label.setMinWidth(Control.USE_COMPUTED_SIZE);
        label.setMinHeight(Control.USE_COMPUTED_SIZE);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Control.USE_COMPUTED_SIZE);
        return label;
    }

    public void calculateTotales(String simbolo) {

        importeBruto = 0;
        arrList.forEach(e -> importeBruto += e.getImporteBruto());
        lblValorVenta.setText(simbolo + " " + Tools.roundingValue(importeBruto, 2));

        descuentoBruto = 0;
        arrList.forEach(e -> descuentoBruto += e.getDescuentoSumado());
        lblDescuento.setText(simbolo + " " + (Tools.roundingValue(descuentoBruto * (-1), 2)));

        subImporteNeto = 0;
        arrList.forEach(e -> subImporteNeto += e.getSubImporteNeto());
        lblSubTotal.setText(simbolo + " " + Tools.roundingValue(subImporteNeto, 2));

        gpImpuestos.getChildren().clear();
        impuestoNeto = 0;
        arrList.forEach(e -> impuestoNeto += e.getImpuestoSumado());

        gpImpuestos.add(addLabelTitle("IMPUESTO GENERADO:", Pos.CENTER_LEFT), 0, 0 + 1);
        gpImpuestos.add(addLabelTotal(simbolo + " " + Tools.roundingValue(impuestoNeto, 2), Pos.CENTER_RIGHT), 1, 0 + 1);

        importeTotal = 0;
        importeTotal = subImporteNeto + impuestoNeto;
        lblTotal.setText(simbolo + " " + Tools.roundingValue(importeTotal, 2));
    }

    private void ticket() {
        if (!Session.ESTADO_IMPRESORA_COTIZACION && Tools.isText(Session.NOMBRE_IMPRESORA_COTIZACION) && Tools.isText(Session.FORMATO_IMPRESORA_VENTA)) {
            Tools.AlertMessageWarning(spWindow, "Venta", "No esta configurado la ruta de impresión ve a la sección configuración/impresora.");
            return;
        }

        if (Session.FORMATO_IMPRESORA_COTIZACION.equalsIgnoreCase("ticket")) {
            if (Session.TICKET_COTIZACION_ID == 0 && Session.TICKET_COTIZACION_RUTA.equalsIgnoreCase("")) {
                Tools.AlertMessageWarning(spWindow, "Venta", "No hay un diseño predeterminado para la impresión configure su ticket en la sección configuración/tickets.");
            } else {
                ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
                    Thread t = new Thread(runnable);
                    t.setDaemon(true);
                    return t;
                });

                Task<String> task = new Task<String>() {
                    @Override
                    public String call() {
                        try {
                            if (Session.DESING_IMPRESORA_COTIZACION.equalsIgnoreCase("withdesing")) {
                                billPrintable.loadEstructuraTicket(Session.TICKET_COTIZACION_ID, Session.TICKET_COTIZACION_RUTA, hbEncabezado, hbDetalleCabecera, hbPie);

                                for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
                                    HBox box = ((HBox) hbEncabezado.getChildren().get(i));
                                    billPrintable.hbEncebezado(box,
                                            "COTIZACIÓN",
                                            "N° " + cotizacionTB.getIdCotizacion(),
                                            cotizacionTB.getClienteTB().getNumeroDocumento(),
                                            cotizacionTB.getClienteTB().getInformacion(),
                                            cotizacionTB.getClienteTB().getCelular(),
                                            cotizacionTB.getClienteTB().getDireccion(),
                                            "---",
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
                                for (int m = 0; m < arrList.size(); m++) {
                                    for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                                        HBox hBox = new HBox();
                                        hBox.setId("dc_" + m + "" + i);
                                        HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                                        billPrintable.hbDetalle(hBox, box, arrList, m);
                                        hbDetalle.getChildren().add(hBox);
                                    }
                                }

                                for (int i = 0; i < hbPie.getChildren().size(); i++) {
                                    HBox box = ((HBox) hbPie.getChildren().get(i));
                                    billPrintable.hbPie(box, cotizacionTB.getMonedaTB().getSimbolo(),
                                            Tools.roundingValue(importeBruto, 2),
                                            "-" + Tools.roundingValue(descuentoBruto, 2),
                                            Tools.roundingValue(subImporteNeto, 2),
                                            Tools.roundingValue(impuestoNeto, 2),
                                            Tools.roundingValue(subImporteNeto, 2),
                                            Tools.roundingValue(importeTotal, 2),
                                            Tools.roundingValue(0, 2),
                                            Tools.roundingValue(0, 2),
                                            cotizacionTB.getClienteTB().getNumeroDocumento(),
                                            cotizacionTB.getClienteTB().getInformacion(),
                                            "---",
                                            cotizacionTB.getClienteTB().getCelular(), "",
                                            "",
                                            "",
                                            "",
                                            "");
                                }

                                billPrintable.generatePDFPrint(hbEncabezado, hbDetalle, hbPie);

                                DocPrintJob job = billPrintable.findPrintService(Session.NOMBRE_IMPRESORA_COTIZACION, PrinterJob.lookupPrintServices()).createPrintJob();

                                if (job != null) {
                                    PrinterJob pj = PrinterJob.getPrinterJob();
                                    pj.setPrintService(job.getPrintService());
                                    pj.setJobName(Session.NOMBRE_IMPRESORA_COTIZACION);
                                    Book book = new Book();
                                    book.append(billPrintable, billPrintable.getPageFormat(pj));
                                    pj.setPageable(book);
                                    pj.print();
                                    if (Session.CORTAPAPEL_IMPRESORA_COTIZACION) {
                                        billPrintable.printCortarPapel(Session.NOMBRE_IMPRESORA_COTIZACION);
                                    }
                                    return "completed";
                                } else {
                                    return "error_name";
                                }
                            } else {
                                billPrintable.loadEstructuraTicket(Session.TICKET_COTIZACION_ID, Session.TICKET_COTIZACION_RUTA, hbEncabezado, hbDetalleCabecera, hbPie);
                                return imprimirVenta(Session.NOMBRE_IMPRESORA_COTIZACION, Session.CORTAPAPEL_IMPRESORA_COTIZACION);
                            }

                        } catch (PrinterException | PrintException | IOException ex) {
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
        } else {
            Tools.AlertMessageWarning(spWindow, "Venta", "Error al validar el formato de impresión configure en la sección configuración/impresora.");
        }
    }

    private String imprimirVenta(String printerName, boolean printerCut) {
        ArrayList<HBox> object = new ArrayList<>();
        int rows = 0;
        int lines = 0;
        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
            object.add((HBox) hbEncabezado.getChildren().get(i));
            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
            rows++;
            lines += billPrintable.hbEncebezado(box,
                    "COTIZACIÓN",
                    "N° " + cotizacionTB.getIdCotizacion(),
                    cotizacionTB.getClienteTB().getNumeroDocumento(),
                    cotizacionTB.getClienteTB().getInformacion(),
                    cotizacionTB.getClienteTB().getCelular(),
                    cotizacionTB.getClienteTB().getDireccion(),
                    "---",
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
                    "",
                    "",
                    "",
                    "0",
                    "0",
                    "0",
                    "0",
                    "0");
        }

        for (int m = 0; m < arrList.size(); m++) {
            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                HBox hBox = new HBox();
                hBox.setId("dc_" + m + "" + i);
                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                rows++;
                lines += billPrintable.hbDetalle(hBox, box, arrList, m);
                object.add(hBox);
            }
        }

        for (int i = 0; i < hbPie.getChildren().size(); i++) {
            object.add((HBox) hbPie.getChildren().get(i));
            HBox box = ((HBox) hbPie.getChildren().get(i));
            rows++;
            lines += billPrintable.hbPie(box, cotizacionTB.getMonedaTB().getSimbolo(),
                    Tools.roundingValue(importeBruto, 2),
                    "-" + Tools.roundingValue(descuentoBruto, 2),
                    Tools.roundingValue(subImporteNeto, 2),
                    Tools.roundingValue(impuestoNeto, 2),
                    Tools.roundingValue(subImporteNeto, 2),
                    Tools.roundingValue(importeTotal, 2),
                    Tools.roundingValue(0, 2),
                    Tools.roundingValue(0, 2),
                    cotizacionTB.getClienteTB().getNumeroDocumento(),
                    cotizacionTB.getClienteTB().getInformacion(),
                    "---",
                    cotizacionTB.getClienteTB().getCelular(), "",
                    "",
                    "",
                    "",
                    "");
        }
        return billPrintable.modelTicket(rows + lines + 1 + 5, lines, object, printerName, printerCut);
    }

    private void reportA4() {
        try {
            ArrayList<SuministroTB> list = new ArrayList();
            for (int i = 0; i < arrList.size(); i++) {
                SuministroTB stb = new SuministroTB();
                stb.setId(i + 1);
                stb.setClave(arrList.get(i).getClave());
                stb.setNombreMarca(arrList.get(i).getNombreMarca());
                stb.setCantidad(arrList.get(i).getCantidad());
                stb.setUnidadCompraName(arrList.get(i).getUnidadCompraName());
                stb.setImporteBruto(arrList.get(i).getImporteBruto());
                stb.setDescuentoSumado(arrList.get(i).getDescuentoSumado());
                stb.setSubImporteNeto(arrList.get(i).getSubImporteNeto());
                stb.setImpuestoSumado(arrList.get(i).getImpuestoSumado());
                stb.setPrecioVentaGeneral(arrList.get(i).getPrecioVentaGeneral());
                stb.setDescuento(arrList.get(i).getDescuento());
                stb.setImporteNeto(arrList.get(i).getCantidad() * arrList.get(i).getPrecioVentaGeneral());
                list.add(stb);
            }

            if (list.isEmpty()) {
                Tools.AlertMessageWarning(spWindow, "Venta realizada", "No hay registros para mostrar en el reporte.");
                return;
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

            map.put("DOCUMENTOCLIENTE", "");
            map.put("DATOSCLIENTE", cotizacionTB.getClienteTB().getInformacion());
            map.put("NUMERODOCUMENTOCLIENTE", cotizacionTB.getClienteTB().getNumeroDocumento());
            map.put("CELULARCLIENTE", cotizacionTB.getClienteTB().getCelular());
            map.put("EMAILCLIENTE", cotizacionTB.getClienteTB().getEmail());
            map.put("DIRECCIONCLIENTE", cotizacionTB.getClienteTB().getDireccion());

            map.put("FECHAEMISION", cotizacionTB.getFechaCotizacion());
            map.put("MONEDA", cotizacionTB.getMonedaTB().getNombre());
            map.put("CONDICIONPAGO", "");

            map.put("SIMBOLO", cotizacionTB.getMonedaTB().getSimbolo());
            map.put("VALORSOLES", monedaCadena.Convertir(Tools.roundingValue(importeTotal, 2), true, cotizacionTB.getMonedaTB().getNombre()));

            map.put("VALOR_VENTA", Tools.roundingValue(importeBruto, 2));
            map.put("DESCUENTO", "-" + Tools.roundingValue(descuentoBruto, 2));
            map.put("SUB_IMPORTE", Tools.roundingValue(subImporteNeto, 2));
            map.put("IMPUESTO_TOTAL", Tools.roundingValue(impuestoNeto, 2));
            map.put("IMPORTE_TOTAL", Tools.roundingValue(importeTotal, 2));
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
            Stage stage = WindowStage.StageLoader(parent, "Cotizacion realizada");
            stage.setResizable(true);
            stage.show();
            stage.requestFocus();

        } catch (IOException | JRException ex) {
            Tools.AlertMessageError(spWindow, "Reporte de Cotizacion", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        vbContent.getChildren().remove(spWindow);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(cotizacionRealizadasController.getHbWindow(), 0d);
        AnchorPane.setTopAnchor(cotizacionRealizadasController.getHbWindow(), 0d);
        AnchorPane.setRightAnchor(cotizacionRealizadasController.getHbWindow(), 0d);
        AnchorPane.setBottomAnchor(cotizacionRealizadasController.getHbWindow(), 0d);
        vbContent.getChildren().add(cotizacionRealizadasController.getHbWindow());
    }

    @FXML
    private void onKeyPressedReporte(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (cotizacionTB != null) {
                reportA4();
            } else {
                Tools.AlertMessageWarning(spWindow, "Cotización", "No se puede generar el reporte por problemas en cargar los datos.");
            }
        }
    }

    @FXML
    private void onActionReporte(ActionEvent event) {
        if (cotizacionTB != null) {
            reportA4();
        } else {
            Tools.AlertMessageWarning(spWindow, "Cotización", "No se puede generar el reporte por problemas en cargar los datos.");
        }
    }

    @FXML
    private void onKeyPressedTicket(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (cotizacionTB != null) {
                ticket();
            } else {
                Tools.AlertMessageWarning(spWindow, "Cotización", "No se puede imprimir el ticket por problemas en cargar los datos.");
            }
        }
    }

    @FXML
    private void onActionTicket(ActionEvent event) {
        if (cotizacionTB != null) {
            ticket();
        } else {
            Tools.AlertMessageWarning(spWindow, "Cotización", "No se puede imprimir el ticket por problemas en cargar los datos.");
        }
    }

    public void setInitCotizacionesRealizadasController(FxCotizacionRealizadasController cotizacionRealizadasController, AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.cotizacionRealizadasController = cotizacionRealizadasController;
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
