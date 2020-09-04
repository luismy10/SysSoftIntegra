package controller.configuracion.impresoras;

import controller.tools.FontsPersonalize;
import controller.tools.PrinterService;
import controller.tools.Session;
import controller.tools.Tools;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;

public class FxImpresoraController implements Initializable {

    @FXML
    private VBox vbWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private ComboBox<String> cbImpresoras;
    @FXML
    private RadioButton rbTicket;
    @FXML
    private RadioButton rbDocumentos;
    @FXML
    private CheckBox cbPaperCut;

    private PrinterService printerService;

    private AnchorPane vbPrincipal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ToggleGroup group = new ToggleGroup();
        rbTicket.setToggleGroup(group);
        rbDocumentos.setToggleGroup(group);
        printerService = new PrinterService();
        loadPrinters();
    }

    private void loadPrinters() {
        printerService.getPrinters().forEach(e -> cbImpresoras.getItems().add(e));
    }

    private PrintService findPrintService(String printerName, PrintService[] services) {
        for (PrintService service : services) {
            if (service.getName().equalsIgnoreCase(printerName)) {
                return service;
            }
        }
        return null;
    }

    public PageFormat getPageFormat(PrinterJob pj) {
        PageFormat pf = pj.defaultPage();
        Paper paper = pf.getPaper();
        paper.setImageableArea(0, 0, 100, paper.getHeight());
        pf.setOrientation(PageFormat.PORTRAIT);
        pf.setPaper(paper);
        return pf;
    }

    private void printTicket(String printName) {
        try {
            DocPrintJob job = findPrintService(printName, PrinterJob.lookupPrintServices()).createPrintJob();
            PrinterJob pj = PrinterJob.getPrinterJob();
            pj.setPrintService(job.getPrintService());
            pj.setJobName(printName);
            Book book = new Book();
            book.append(new Printable() {
                @Override
                public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
                    if (pageIndex == 0) {
                        Graphics2D g2d = (Graphics2D) graphics;
                        g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
                        g2d.setPaint(Color.black);
                        g2d.setFont(new Font(FontsPersonalize.FONT_ROBOTO, Font.PLAIN, 12));
                        g2d.drawString("Prueba de impresión de ticket.", 10, 10);
                        graphics.dispose();
                        return PAGE_EXISTS;
                    } else {
                        return NO_SUCH_PAGE;
                    }
                }
            }, getPageFormat(pj));
            pj.setPageable(book);
            pj.print();
        } catch (PrinterException ex) {
            Tools.AlertMessageWarning(vbWindow, "Impresora", "Error al imprimir");
        }
    }

    private void printDocument4A(String printName) {
        try {

            int width = 595;
            int height = 842;
            int margin = 20;
            int columnwidth = 555;
            int columnspace = 0;

            Map param = new HashMap();
            JasperDesign jasperDesign = getJasperDesign(width, height, margin, columnwidth, columnspace);
            JasperReport report = JasperCompileManager.compileReport(jasperDesign);
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, param, new JREmptyDataSource());
            PrintReportToPrinter(jasperPrint, printName);

        } catch (JRException | PrintException | IOException er) {

        }
    }

    private JasperDesign getJasperDesign(int width, int height, int margin, int columnWidth, int columnSpace) throws JRException {

        JasperDesign jasperDesign = new JasperDesign();
        jasperDesign.setName("Impresión");
        jasperDesign.setPageWidth(width);
        jasperDesign.setPageHeight(height);

        jasperDesign.setColumnWidth(columnWidth);
        jasperDesign.setColumnSpacing(columnSpace);

        jasperDesign.setLeftMargin(margin);
        jasperDesign.setRightMargin(margin);
        jasperDesign.setTopMargin(margin);
        jasperDesign.setBottomMargin(margin);

        JRDesignBand band = new JRDesignBand();
        band.setHeight(100);
        JRDesignStaticText staticText = new JRDesignStaticText();
        staticText.setX(0);
        staticText.setY(0);
        staticText.setWidth(width);
        staticText.setHeight(50);
        staticText.setHorizontalTextAlign(HorizontalTextAlignEnum.LEFT);
        staticText.setText("Diseño de impresión 4a");
        band.addElement(staticText);

        jasperDesign.setTitle(band);
        return jasperDesign;
    }

    private void PrintReportToPrinter(JasperPrint jp, String printName) throws JRException, PrintException, IOException {
        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        printRequestAttributeSet.add(new Copies(1));

        PrinterName printerName = new PrinterName(printName, null);

        PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
        printServiceAttributeSet.add(printerName);

        JRPrintServiceExporter exporter = new JRPrintServiceExporter();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceAttributeSet);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
        exporter.exportReport();
    }

    private void onEventProbarImpresion() {
        if (rbTicket.isSelected()) {
            if (cbImpresoras.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(vbWindow, "Impresora", "Seleccione una impresora para continuar.");
                cbImpresoras.requestFocus();
            } else {
                printTicket(cbImpresoras.getSelectionModel().getSelectedItem());
            }
        } else {
            if (cbImpresoras.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(vbWindow, "Impresora", "Seleccione una impresora para continuar.");
                cbImpresoras.requestFocus();
            } else {
                printDocument4A(cbImpresoras.getSelectionModel().getSelectedItem());
            }
        }
    }

    private void onEventSaveConfiguration() {
        if (cbImpresoras.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(vbWindow, "Impresora", "Seleccione una impresora para continuar.");
            cbImpresoras.requestFocus();
        } else {
            String ruta = "./archivos/printSetting.properties";
            boolean state = false;
            try (OutputStream output = new FileOutputStream(ruta)) {
                Properties prop = new Properties();
                prop.setProperty("printername", cbImpresoras.getSelectionModel().getSelectedItem());
                prop.setProperty("printercutpaper", cbPaperCut.isSelected() + "");
                prop.setProperty("printertype", rbTicket.isSelected() + "");
                prop.store(output, "Ruta de configuración de la impresora");
                state = true;
                Tools.AlertMessageInformation(vbWindow, "Impresora", "Se guardo la configuración correctamente.");
            } catch (IOException io) {
                state = false;
                Tools.AlertMessageError(vbWindow, "Impresora", "Error al crear el archivo: " + io.getLocalizedMessage());
            } finally {
                if (state) {
                    iniciarRutasImpresion();
                }
            }
        }
    }

    private void iniciarRutasImpresion() {
        String ruta = "./archivos/printSetting.properties";
        try (InputStream input = new FileInputStream(ruta)) {

            Properties prop = new Properties();
            prop.load(input);

            Session.ESTADO_IMPRESORA = true;
            Session.NOMBRE_IMPRESORA = prop.getProperty("printername");
            Session.CORTAPAPEL_IMPRESORA = Boolean.parseBoolean(prop.getProperty("printercutpaper"));
            Session.TIPO_IMPRESORA = prop.getProperty("printertype");
            Tools.AlertMessageInformation(vbWindow, "Impresora", "Se ha creado una ruta de impresión con la impresora " + Session.NOMBRE_IMPRESORA + ", por defecto " + (Session.CORTAPAPEL_IMPRESORA ? "corta papel" : "no corta papel") + ".");
        } catch (IOException ex) {
            Tools.AlertMessageError(vbWindow, "Impresora", "Se ha producido un error al crear la ruta de impresión, " + ex.getLocalizedMessage());
            Session.ESTADO_IMPRESORA = false;
        }
    }

    @FXML
    private void onActionOpcionRadioButton(ActionEvent event) {
        cbPaperCut.setDisable(!rbTicket.isSelected());
    }

    @FXML
    private void onKeyPressedReloadPrinters(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            loadPrinters();
        }
    }

    @FXML
    private void onActionReloadPrinters(ActionEvent event) {
        loadPrinters();
    }

    @FXML
    private void onKeyPressedProbarImpresion(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventProbarImpresion();
        }
    }

    @FXML
    private void onActionProbarImpresion(ActionEvent event) {
        onEventProbarImpresion();
    }

    @FXML
    private void onKeyPressedGuardarConfiguracion(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventSaveConfiguration();
        }
    }

    @FXML
    private void onActionGuardarConfiguracion(ActionEvent event) {
        onEventSaveConfiguration();
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
