package controller.tools;

import br.com.adilson.util.PrinterMatrix;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;
import model.ImageADO;
import model.ImagenTB;
import model.SuministroTB;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class BillPrintable {

    private int sheetWidth;

    private final double pointWidthSizeView;

    private final double pointWidthSizePaper;

    private final ArrayList<String> fileImages;

    public BillPrintable() {
        sheetWidth = 0;
        pointWidthSizeView = 8.10;
        pointWidthSizePaper = 5.20;
        fileImages = new ArrayList();
    }

    public void hbEncebezado(HBox box, String nombre_impresion_comprobante, String numeracion_serie_comprobante, String nummero_documento_cliente, String informacion_cliente, String direccion_cliente, String codigoVenta) {
        for (int j = 0; j < box.getChildren().size(); j++) {
            if (box.getChildren().get(j) instanceof TextFieldTicket) {
                TextFieldTicket fieldTicket = ((TextFieldTicket) box.getChildren().get(j));
                if (fieldTicket.getVariable().equalsIgnoreCase("repeempresa")) {
                    fieldTicket.setText(Session.COMPANY_REPRESENTANTE);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("telempresa")) {
                    fieldTicket.setText(Session.COMPANY_TELEFONO);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("celempresa")) {
                    fieldTicket.setText(Session.COMPANY_CELULAR);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("pagwempresa")) {
                    fieldTicket.setText(Session.COMPANY_PAGINAWEB);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("emailempresa")) {
                    fieldTicket.setText(Session.COMPANY_EMAIL);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("direcempresa")) {
                    fieldTicket.setText(Session.COMPANY_DOMICILIO);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("rucempresa")) {
                    fieldTicket.setText(Session.COMPANY_NUM_DOCUMENTO);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("razoempresa")) {
                    fieldTicket.setText(Session.COMPANY_RAZON_SOCIAL);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("nomcomempresa")) {
                    fieldTicket.setText(Session.COMPANY_NOMBRE_COMERCIAL);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("fchactual")) {
                    fieldTicket.setText(Tools.getDate("dd/MM/yyyy"));
                } else if (fieldTicket.getVariable().equalsIgnoreCase("horactual")) {
                    fieldTicket.setText(Tools.getHour("hh:mm:ss aa"));
                } else if (fieldTicket.getVariable().equalsIgnoreCase("docventa")) {
                    fieldTicket.setText(nombre_impresion_comprobante);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("numventa")) {
                    fieldTicket.setText(numeracion_serie_comprobante);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("codigo")) {
                    fieldTicket.setText(codigoVenta);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("numcliente")) {
                    fieldTicket.setText(nummero_documento_cliente);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("infocliente")) {
                    fieldTicket.setText(informacion_cliente);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("direccliente")) {
                    fieldTicket.setText(direccion_cliente);
                }
            }
        }
    }

    public void hbDetalle(HBox hBox, HBox box, ObservableList<SuministroTB> arrList, int m) {
        for (int j = 0; j < box.getChildren().size(); j++) {
            if (box.getChildren().get(j) instanceof TextFieldTicket) {
                TextFieldTicket fieldTicket = ((TextFieldTicket) box.getChildren().get(j));
                if (fieldTicket.getVariable().equalsIgnoreCase("numfilas")) {
                    fieldTicket.setText("" + (m + 1));
                } else if (fieldTicket.getVariable().equalsIgnoreCase("codbarrasarticulo")) {
                    fieldTicket.setText(arrList.get(m).getClave());
                } else if (fieldTicket.getVariable().equalsIgnoreCase("nombretarticulo")) {
                    fieldTicket.setText(arrList.get(m).getNombreMarca());
                } else if (fieldTicket.getVariable().equalsIgnoreCase("cantarticulo")) {
                    fieldTicket.setText(Tools.roundingValue(arrList.get(m).getCantidad(), 2));
                } else if (fieldTicket.getVariable().equalsIgnoreCase("precarticulo")) {
                    fieldTicket.setText(Tools.roundingValue(arrList.get(m).getPrecioVentaGeneral(), 2));
                } else if (fieldTicket.getVariable().equalsIgnoreCase("descarticulo")) {
                    fieldTicket.setText(Tools.roundingValue(arrList.get(m).getDescuento(), 0) + "%");
                } else if (fieldTicket.getVariable().equalsIgnoreCase("impoarticulo")) {
                    fieldTicket.setText(Tools.roundingValue(arrList.get(m).getTotalImporte(), 2));
                }
                hBox.getChildren().add(addElementTextField("iu", fieldTicket.getText(), fieldTicket.isMultilineas(), fieldTicket.getLines(), fieldTicket.getColumnWidth(), fieldTicket.getAlignment(), fieldTicket.isEditable(), fieldTicket.getVariable(),fieldTicket.getFontName(),fieldTicket.getFontSize()));
            }
        }
    }

    public void hbPie(HBox box, String moneda, String valorVenta, String descuento, String subTotal, String total, String efectivo, String vuelto, String numCliente, String infoCliente, String codigoVenta) {
        for (int j = 0; j < box.getChildren().size(); j++) {
            if (box.getChildren().get(j) instanceof TextFieldTicket) {
                TextFieldTicket fieldTicket = ((TextFieldTicket) box.getChildren().get(j));
                if (fieldTicket.getVariable().equalsIgnoreCase("fchactual")) {
                    fieldTicket.setText(Tools.getDate("dd/MM/yyyy"));
                } else if (fieldTicket.getVariable().equalsIgnoreCase("horactual")) {
                    fieldTicket.setText(Tools.getHour("hh:mm:ss aa"));
                } else if (fieldTicket.getVariable().equalsIgnoreCase("subtotal")) {
                    fieldTicket.setText(moneda + " " + valorVenta);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("dscttotal")) {
                    fieldTicket.setText(moneda + " " + descuento);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("imptotal")) {
                    fieldTicket.setText(moneda + " " + subTotal);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("totalpagar")) {
                    fieldTicket.setText(moneda + " " + total);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("efectivo")) {
                    fieldTicket.setText(moneda + " " + efectivo);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("vuelto")) {
                    fieldTicket.setText(moneda + " " + vuelto);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("codigo")) {
                    fieldTicket.setText(codigoVenta);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("numcliente")) {
                    fieldTicket.setText(numCliente);
                } else if (fieldTicket.getVariable().equalsIgnoreCase("infocliente")) {
                    fieldTicket.setText(infoCliente);
                }
            }
        }
    }

    @Deprecated
    private void modelTicket(Node window, int rows, int lines, ArrayList<HBox> object, String messageClassTitle, String messageClassContent, String nombreimpresora, boolean cortar) {
        int column = sheetWidth;
        try {
            PrinterMatrix p = new PrinterMatrix();

            p.setOutSize(rows, column);
            short linesbefore;
            short linescurrent;
            short linesafter = 0;
            int linescount;
            int rowscount = 0;
            for (int i = 0; i < object.size(); i++) {
                HBox hBox = object.get(i);
                rowscount += 1;
                linescount = rowscount + linesafter;
                linesafter = 0;
                linescurrent = 0;
                if (hBox.getChildren().size() > 1) {
                    int columnI;
                    int columnF;
                    int columnA = 0;
                    int countColumns = 0;
                    rowscount = linescount;
                    for (int v = 0; v < hBox.getChildren().size(); v++) {
                        TextFieldTicket field = (TextFieldTicket) hBox.getChildren().get(v);
                        columnI = columnA;
                        columnF = columnI + field.getColumnWidth();
                        columnA = columnF;
                        if (null != field.getAlignment()) {
                            switch (field.getAlignment()) {
                                case CENTER_LEFT:
                                    p.printTextWrap(linescount + linesafter, field.getLines(), columnI, columnF, field.getText());
                                    linesbefore = (short) field.getLines();
                                    linescurrent = (short) (linesbefore == 1 ? linesbefore : linescurrent);
                                    countColumns++;
                                    linesafter = hBox.getChildren().size() == countColumns ? linescurrent : 0;
                                    break;
                                case CENTER:
                                    p.printTextWrap(linescount + linesafter, field.getLines(), ((columnI + columnF) - field.getText().length()) / 2, columnF, field.getText());
                                    linesbefore = (short) field.getLines();
                                    linescurrent = (short) (linesbefore == 1 ? linesbefore : linescurrent);
                                    countColumns++;
                                    linesafter = hBox.getChildren().size() == countColumns ? linescurrent : 0;
                                    break;
                                case CENTER_RIGHT:
                                    p.printTextWrap(linescount + linesafter, field.getLines(), columnF - field.getText().length(), columnF, field.getText());
                                    linesbefore = (short) field.getLines();
                                    linescurrent = (short) (linesbefore == 1 ? linesbefore : linescurrent);
                                    countColumns++;
                                    linesafter = hBox.getChildren().size() == countColumns ? linescurrent : 0;
                                    break;
                                default:
                                    p.printTextWrap(linescount + linesafter, field.getLines(), columnI, columnF, field.getText());
                                    linesbefore = (short) field.getLines();
                                    linescurrent = (short) (linesbefore == 1 ? linesbefore : linescurrent);
                                    countColumns++;
                                    linesafter = hBox.getChildren().size() == countColumns ? linescurrent : 0;
                                    break;
                            }
                        }

                    }
                } else {
                    rowscount = linescount;
                    TextFieldTicket field = (TextFieldTicket) hBox.getChildren().get(0);
                    if (null != field.getAlignment()) {
                        switch (field.getAlignment()) {
                            case CENTER_LEFT:
                                p.printTextWrap(linescount + linesafter, field.getLines(), 0, field.getColumnWidth(), field.getText());
                                linesafter = (short) field.getLines();
                                break;
                            case CENTER:
                                p.printTextWrap(linescount + linesafter, field.getLines(), (field.getColumnWidth() - field.getText().length()) / 2, field.getColumnWidth(), field.getText());
                                linesafter = (short) field.getLines();
                                break;
                            case CENTER_RIGHT:
                                p.printTextWrap(linescount + linesafter, field.getLines(), field.getColumnWidth() - field.getText().length(), field.getColumnWidth(), field.getText());
                                linesafter = (short) field.getLines();
                                break;
                            default:
                                p.printTextWrap(linescount + linesafter, field.getLines(), 0, field.getColumnWidth(), field.getText());
                                linesafter = (short) field.getLines();
                                break;
                        }
                    }
                }
            }
            int salida = object.size() + lines;
            salida++;
            p.printTextWrap(salida, 0, 0, column, "\n");
            salida++;
            p.printTextWrap(salida, 0, 0, column, "\n");
            salida++;
            p.printTextWrap(salida, 0, 0, column, "\n");
            salida++;
            p.printTextWrap(salida, 0, 0, column, "\n");
            salida++;
            p.printTextWrap(salida, 0, 0, column, "\n");
            File archivo = new File("./archivos/modeloimpresoraticket.txt");
            if (archivo.exists()) {
                archivo.delete();
                p.toFile("./archivos/modeloimpresoraticket.txt");
                printDoc("./archivos/modeloimpresoraticket.txt", nombreimpresora, cortar);
                Tools.AlertMessageInformation(window, "Imprimir", "Se envío correctamente la impresión");
            } else {
                p.toFile("./archivos/modeloimpresoraticket.txt");
                printDoc("./archivos/modeloimpresoraticket.txt", nombreimpresora, cortar);
                Tools.AlertMessageInformation(window, "Imprimir", "Se envío correctamente la impresión");
            }
        } catch (Exception e) {
            Tools.AlertMessageError(window, messageClassTitle, messageClassContent + " " + e.getLocalizedMessage());
        }
    }

    public String generatePDFPrint(AnchorPane apEncabezado, AnchorPane apDetalle, AnchorPane apPie, String nombreImpresora, boolean cortar) {
        try {
            int width = (int) Math.ceil(sheetWidth * pointWidthSizePaper);
            Map param = new HashMap();
            JasperDesign jasperDesign = getJasperDesign(width, apEncabezado, apDetalle, apPie);
            JasperReport report = JasperCompileManager.compileReport(jasperDesign);
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, param, new JREmptyDataSource());
//            JasperExportManager.exportReportToPdfFile(jasperPrint, "./archivos/InventarioGeneral.pdf");
            PrintReportToPrinter(jasperPrint, nombreImpresora, cortar);
            return "completed";
        } catch (JRException | PrintException | IOException er) {
            return "Error en imprimir: " + er.getLocalizedMessage();
        } finally {
            fileImages.stream().map((fileImage) -> new File(fileImage)).filter((removed) -> (removed != new File("./archivos/no-image"))).forEachOrdered((removed) -> {
                removed.delete();
            });
        }
    }

    private JasperDesign getJasperDesign(int width, AnchorPane apEncabezado, AnchorPane apDetalle, AnchorPane pPie) throws JRException {
        JasperDesign jasperDesign = new JasperDesign();
        jasperDesign.setName("Formato de ticket");
        jasperDesign.setPageWidth(width);
        jasperDesign.setPageHeight(2000);
        jasperDesign.setColumnWidth(width);
        jasperDesign.setColumnSpacing(0);
        jasperDesign.setLeftMargin(0);
        jasperDesign.setRightMargin(0);
        jasperDesign.setTopMargin(0);
        jasperDesign.setBottomMargin(0);

//        JRDesignStyle normalStyle = new JRDesignStyle();
//        normalStyle.setName("Consolas");
//        normalStyle.setDefault(true);
//        // normalStyle.setFontName("Monospace");
//        normalStyle.setPdfFontName("src/view/style/Consolas.ttf");
//        normalStyle.setPdfEncoding("Identity-H");
//        normalStyle.setPdfEmbedded(true);
//        jasperDesign.addStyle(normalStyle);
        JRDesignBand band = new JRDesignBand();
        band.setHeight(2000);

        //font size 9f x 15 height
        int rows = 0;
        rows += createRow(apEncabezado, jasperDesign, band, width, rows);
        rows = createRow(apDetalle, jasperDesign, band, width, rows);
        createRow(pPie, jasperDesign, band, width, rows);

        jasperDesign.setTitle(band);
        return jasperDesign;
    }

    private int createRow(AnchorPane anchorPane, JasperDesign jasperDesign, JRDesignBand band, int width, int rows) {
        for (int i = 0; i < anchorPane.getChildren().size(); i++) {

            HBox box = ((HBox) anchorPane.getChildren().get(i));
            StringBuilder result = new StringBuilder();

            if (box.getChildren().size() > 1) {

                int columnI;
                int columnF;
                int columnA = 0;

                for (int j = 0; j < box.getChildren().size(); j++) {
                    TextFieldTicket field = (TextFieldTicket) box.getChildren().get(j);

                    columnI = columnA;
                    columnF = columnI + field.getColumnWidth();
                    columnA = columnF;

                    int totalWidth;
                    int length;

                    if (null != field.getAlignment()) {
                        switch (field.getAlignment()) {
                            case CENTER_LEFT:
                                totalWidth = columnF - columnI;
                                length = field.getText().length();
                                int posl = 0;
                                for (int ca = 0; ca < totalWidth; ca++) {
                                    if (ca >= 0 && ca <= (length - 1)) {
                                        char c = field.getText().charAt(posl);
                                        result.append(c);
                                        posl++;

                                    } else {
                                        result.append(" ");
                                    }
                                }
                                break;
                            case CENTER:
                                totalWidth = columnF - columnI;
                                length = field.getText().length();
                                int centro = (totalWidth - length) / 2;
                                int posc = 0;
                                for (int ca = 0; ca < totalWidth; ca++) {
                                    if (ca >= centro && ca <= (centro + (length - 1))) {
                                        char c = field.getText().charAt(posc);
                                        result.append(c);
                                        posc++;

                                    } else {
                                        result.append(" ");
                                    }
                                }

                                break;
                            case CENTER_RIGHT:
                                totalWidth = columnF - columnI;
                                length = field.getText().length();
                                int right = totalWidth - length;
                                int posr = 0;
                                for (int ca = 0; ca < totalWidth; ca++) {
                                    if (ca >= right && ca <= (right + (length - 1))) {
                                        char c = field.getText().charAt(posr);
                                        result.append(c);
                                        posr++;

                                    } else {
                                        result.append(" ");
                                    }
                                }
                                break;
                            default:
                                totalWidth = columnF - columnI;
                                length = field.getText().length();
                                int posd = 0;
                                for (int ca = 0; ca < totalWidth; ca++) {
                                    if (ca >= 0 && ca <= (length - 1)) {
                                        char c = field.getText().charAt(posd);
                                        result.append(c);
                                        posd++;

                                    } else {
                                        result.append(" ");
                                    }
                                }
                                break;
                        }
                    }
                }

                JRDesignStaticText staticText = new JRDesignStaticText();
                staticText.setX(0);
                staticText.setY(rows);
                staticText.setWidth(width);
                staticText.setHeight(15);
                staticText.setFontSize(9f);
                //staticText.setFontName(field.getFontName());
                staticText.setFontName("Consola");
                staticText.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
                staticText.setText(result.toString());
                band.addElement(staticText);
                rows += 15;
            } else {

                if (box.getChildren().get(0) instanceof TextFieldTicket) {

//                    TextFieldTicket field = (TextFieldTicket) box.getChildren().get(0);
//                    int columnI;
//                    int columnF;
//
//                    columnI = 0;
//                    columnF = columnI + field.getColumnWidth();
//
//                    int totalWidth;
//                    int length;
//
//                    if (null != field.getAlignment()) {
//                        switch (field.getAlignment()) {
//                            case CENTER_LEFT:
//                                totalWidth = columnF - columnI;
//                                length = field.getText().length();
//                                int posl = 0;
//                                for (int ca = 0; ca < totalWidth; ca++) {
//                                    if (ca >= 0 && ca <= (length - 1)) {
//                                        char c = field.getText().charAt(posl);
//                                        result.append(c);
//                                        posl++;
//                                    } else {
//                                        result.append(" ");
//                                    }
//                                }
//                                break;
//                            case CENTER:
//                                totalWidth = columnF - columnI;
//                                length = field.getText().length();
//                                int centro = (totalWidth - length) / 2;
//                                int posc = 0;
//                                for (int ca = 0; ca < totalWidth; ca++) {
//                                    if (ca >= centro && ca <= (centro + (length - 1))) {
//                                        char c = field.getText().charAt(posc);
//                                        result.append(c);
//                                        posc++;
//                                    } else {
//                                        result.append(" ");
//                                    }
//                                }
//
//                                break;
//                            case CENTER_RIGHT:
//                                totalWidth = columnF - columnI;
//                                length = field.getText().length();
//                                int right = totalWidth - length;
//                                int posr = 0;
//                                for (int ca = 0; ca < totalWidth; ca++) {
//                                    if (ca >= right && ca <= (right + (length - 1))) {
//                                        char c = field.getText().charAt(posr);
//                                        result.append(c);
//                                        posr++;
//                                    } else {
//                                        result.append(" ");
//                                    }
//                                }
//                                break;
//                            default:
//                                totalWidth = columnF - columnI;
//                                length = field.getText().length();
//                                int posd = 0;
//                                for (int ca = 0; ca < totalWidth; ca++) {
//                                    if (ca >= 0 && ca <= (length - 1)) {
//                                        char c = field.getText().charAt(posd);
//                                        result.append(c);
//                                        posd++;
//                                    } else {
//                                        result.append(" ");
//                                    }
//                                }
//                                break;
//                        }
//                    }
                    TextFieldTicket field = (TextFieldTicket) box.getChildren().get(0);
                    JRDesignStaticText staticText = new JRDesignStaticText();
                    staticText.setX(0);
                    staticText.setY(rows);
                    staticText.setWidth(width);
                    staticText.setHeight((int) (field.getFontSize()+2.5f));//15-9 17-11 19-13 21-15 23-17 25-19 27-21
                    staticText.setFontSize(field.getFontSize()-3.5f);
                    staticText.setFontName(
                            field.getFontName().equalsIgnoreCase("Consola")
                            ? "Consola"
                            : field.getFontName().equalsIgnoreCase("Roboto Regular")
                            ? "RobotoRegular"
                            : "RobotoBold");
                    staticText.setHorizontalTextAlign(
                            field.getAlignment() == Pos.CENTER_LEFT
                            ? HorizontalTextAlignEnum.LEFT
                            : field.getAlignment() == Pos.CENTER
                            ? HorizontalTextAlignEnum.CENTER
                            : HorizontalTextAlignEnum.RIGHT);
                    staticText.setVerticalTextAlign(VerticalTextAlignEnum.MIDDLE);
                    staticText.setText(field.getText());
//                    staticText.setText(result.toString());
                    band.addElement(staticText);
                    rows += staticText.getHeight();
                } else if (box.getChildren().get(0) instanceof ImageViewTicket) {
                    ImageViewTicket imageView = (ImageViewTicket) box.getChildren().get(0);
                    String idImage = "./archivos/no-image.png";
                    try {
                        ByteArrayInputStream bais = new ByteArrayInputStream(imageView.getUrl());
                        BufferedImage bufferedImage = ImageIO.read(bais);
                        String idGenerated = getIdGenerateImage();
                        boolean validateImage = true;
                        while (validateImage) {
                            File file = new File("./archivos/" + idGenerated + ".png");
                            if (file.exists()) {
                                idGenerated = getIdGenerateImage();
                            } else {
                                validateImage = false;
                                idGenerated = "./archivos/" + idGenerated + ".png";
                                idImage = idGenerated;
                            }
                        }
                        ImageIO.write(bufferedImage, "png", new File(idGenerated));
                    } catch (IOException ex1) {
                    }
                    JRDesignImage image = new JRDesignImage(jasperDesign);
                    image.setX(
                            box.getAlignment() == Pos.CENTER_LEFT ? 0
                            : box.getAlignment() == Pos.CENTER
                            ? (int) (width - imageView.getFitWidth()) / 2
                            : box.getAlignment() == Pos.CENTER_RIGHT ? (int) (width - imageView.getFitWidth()) : 0
                    );
                    image.setY(rows);
                    image.setWidth((int) imageView.getFitWidth());
                    image.setHeight((int) imageView.getFitHeight());
                    image.setScaleImage(ScaleImageEnum.FILL_FRAME);
                    JRDesignExpression expr = new JRDesignExpression();
                    expr.setText("\"" + idImage + "\"");
                    image.setExpression(expr);
                    band.addElement(image);
                    rows += imageView.getFitHeight();
                    fileImages.add(idImage);
                }
            }
        }
        return rows;
    }

    private void PrintReportToPrinter(JasperPrint jp, String printName, boolean cortar) throws JRException, PrintException, IOException {
        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        printRequestAttributeSet.add(new Copies(1));

        PrinterName printerName = new PrinterName(printName, null); //gets printer 

        PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
        printServiceAttributeSet.add(printerName);

        JRPrintServiceExporter exporter = new JRPrintServiceExporter();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceAttributeSet);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
        exporter.exportReport();
        printString(printName, cortar);
    }

    public void printString(String printerName, boolean cortar) throws PrintException, IOException {
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
        PrintService service = findPrintService(printerName, printService);
        DocPrintJob job = service.createPrintJob();
        byte[] bytes = "\n\n\n".getBytes("CP437");
        byte[] cutP = new byte[]{0x1d, 'V', 1};
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(bytes);
        if (cortar) {
            outputStream.write(cutP);
        }
        byte c[] = outputStream.toByteArray();
        Doc doc = new SimpleDoc(c, flavor, null);
        job.print(doc, null);
    }

    private String getIdGenerateImage() {
        Random rd = new Random();
        int dig5 = rd.nextInt(90000) + 10000;
        return "image_" + dig5;

    }

    @Deprecated
    private void printDoc(String ruta, String nombreimpresora, boolean cortar) {
        File file = new File(ruta);
        FileInputStream inputStream = null;
        try {
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException ex) {
            }
            if (inputStream == null) {
                return;
            }
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
            PrintService service = findPrintService(nombreimpresora, printService);
            DocPrintJob job = service.createPrintJob();

            byte[] bytes = readFileToByteArray(file);
            byte[] cutP = new byte[]{0x1d, 'V', 1};
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(bytes);
            if (cortar) {
                outputStream.write(cutP);
            }
            byte c[] = outputStream.toByteArray();
            Doc doc = new SimpleDoc(c, flavor, null);
            job.print(doc, null);
        } catch (IOException | PrintException e) {
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    private PrintService findPrintService(String printerName, PrintService[] services) {
        for (PrintService service : services) {
            if (service.getName().equalsIgnoreCase(printerName)) {
                return service;
            }
        }
        return null;
    }

    private static byte[] readFileToByteArray(File file) {
        byte[] bArray = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(bArray);
            fis.close();

        } catch (IOException ioExp) {
        }
        return bArray;
    }

    public void loadEstructuraTicket(int idTicket, String ruta, AnchorPane apEncabezado, AnchorPane apDetalleCabecera, AnchorPane apPie) {
        apEncabezado.getChildren().clear();
        apDetalleCabecera.getChildren().clear();
        apPie.getChildren().clear();
        JSONObject jSONObject = Json.obtenerObjetoJSON(ruta);

        ArrayList<ImagenTB> imagenTBs = ImageADO.ListaImagePorIdRelacionado(idTicket);

        sheetWidth = jSONObject.get("column") != null ? Short.parseShort(jSONObject.get("column").toString()) : (short) 40;

        if (jSONObject.get("cabecera") != null) {
            JSONObject cabeceraObjects = Json.obtenerObjetoJSON(jSONObject.get("cabecera").toString());
            for (int i = 0; i < cabeceraObjects.size(); i++) {
                HBox box = generateElement(apEncabezado, "cb");
                JSONObject objectObtener = Json.obtenerObjetoJSON(cabeceraObjects.get("cb_" + (i + 1)).toString());
                if (objectObtener.get("text") != null) {
                    JSONObject object = Json.obtenerObjetoJSON(objectObtener.get("text").toString());
                    TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Short.parseShort(object.get("lines").toString()), Short.parseShort(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()), String.valueOf(object.get("font").toString()), Float.valueOf(object.get("size").toString()));
                    box.getChildren().add(field);
                } else if (objectObtener.get("list") != null) {
                    JSONArray array = Json.obtenerArrayJSON(objectObtener.get("list").toString());
                    Iterator it = array.iterator();
                    while (it.hasNext()) {
                        JSONObject object = Json.obtenerObjetoJSON(it.next().toString());
                        TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Short.parseShort(object.get("lines").toString()), Short.parseShort(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()), String.valueOf(object.get("font").toString()), Float.valueOf(object.get("size").toString()));
                        box.getChildren().add(field);
                    }
                } else if (objectObtener.get("image") != null) {
                    JSONObject object = Json.obtenerObjetoJSON(objectObtener.get("image").toString());
                    ImageViewTicket imageView = addElementImageView("", Short.parseShort(object.get("width").toString()), 100, 86, false);
                    imageView.setId(String.valueOf(object.get("value").toString()));
                    box.setPrefWidth(imageView.getColumnWidth() * pointWidthSizeView);
                    box.setPrefHeight(imageView.getFitHeight());
                    box.setAlignment(getAlignment(object.get("align").toString()));
                    box.getChildren().add(imageView);
                }
            }
        }
        if (jSONObject.get("detalle") != null) {
            JSONObject detalleObjects = Json.obtenerObjetoJSON(jSONObject.get("detalle").toString());
            for (int i = 0; i < detalleObjects.size(); i++) {
                HBox box = generateElement(apDetalleCabecera, "dr");
                JSONObject objectObtener = Json.obtenerObjetoJSON(detalleObjects.get("dr_" + (i + 1)).toString());
                if (objectObtener.get("text") != null) {
                    JSONObject object = Json.obtenerObjetoJSON(objectObtener.get("text").toString());
                    TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Short.parseShort(object.get("lines").toString()), Short.parseShort(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()), String.valueOf(object.get("font").toString()), Float.valueOf(object.get("size").toString()));
                    box.getChildren().add(field);
                } else if (objectObtener.get("list") != null) {
                    JSONArray array = Json.obtenerArrayJSON(objectObtener.get("list").toString());
                    Iterator it = array.iterator();
                    while (it.hasNext()) {
                        JSONObject object = Json.obtenerObjetoJSON(it.next().toString());
                        TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Short.parseShort(object.get("lines").toString()), Short.parseShort(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()), String.valueOf(object.get("font").toString()), Float.valueOf(object.get("size").toString()));
                        box.getChildren().add(field);
                    }
                } else if (objectObtener.get("image") != null) {
                    JSONObject object = Json.obtenerObjetoJSON(objectObtener.get("image").toString());
                    ImageViewTicket imageView = addElementImageView("", Short.parseShort(object.get("width").toString()), 100, 86, false);
                    imageView.setId(String.valueOf(object.get("value").toString()));
                    box.setPrefWidth(imageView.getColumnWidth() * pointWidthSizeView);
                    box.setPrefHeight(imageView.getFitHeight());
                    box.setAlignment(getAlignment(object.get("align").toString()));
                    box.getChildren().add(imageView);
                }
            }
        }

        if (jSONObject.get("pie") != null) {
            JSONObject pieObjects = Json.obtenerObjetoJSON(jSONObject.get("pie").toString());
            for (int i = 0; i < pieObjects.size(); i++) {
                HBox box = generateElement(apPie, "cp");
                JSONObject objectObtener = Json.obtenerObjetoJSON(pieObjects.get("cp_" + (i + 1)).toString());
                if (objectObtener.get("text") != null) {
                    JSONObject object = Json.obtenerObjetoJSON(objectObtener.get("text").toString());
                    TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Short.parseShort(object.get("lines").toString()), Short.parseShort(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()), String.valueOf(object.get("font").toString()), Float.valueOf(object.get("size").toString()));
                    box.getChildren().add(field);
                } else if (objectObtener.get("list") != null) {
                    JSONArray array = Json.obtenerArrayJSON(objectObtener.get("list").toString());
                    Iterator it = array.iterator();
                    while (it.hasNext()) {
                        JSONObject object = Json.obtenerObjetoJSON(it.next().toString());
                        TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Short.parseShort(object.get("lines").toString()), Short.parseShort(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()), String.valueOf(object.get("font").toString()), Float.valueOf(object.get("size").toString()));
                        box.getChildren().add(field);
                    }
                } else if (objectObtener.get("image") != null) {
                    JSONObject object = Json.obtenerObjetoJSON(objectObtener.get("image").toString());
                    ImageViewTicket imageView = addElementImageView("", Short.parseShort(object.get("width").toString()), 100, 86, false);
                    imageView.setId(String.valueOf(object.get("value").toString()));
                    box.setPrefWidth(imageView.getColumnWidth() * pointWidthSizeView);
                    box.setPrefHeight(imageView.getFitHeight());
                    box.setAlignment(getAlignment(object.get("align").toString()));
                    box.getChildren().add(imageView);
                }
            }
        }

        for (int i = 0; i < imagenTBs.size(); i++) {
            for (int m = 0; m < apEncabezado.getChildren().size(); m++) {
                HBox hBox = (HBox) apEncabezado.getChildren().get(m);
                if (hBox.getChildren().size() == 1) {
                    if (hBox.getChildren().get(0) instanceof ImageViewTicket) {
                        ImageViewTicket imageViewTicket = (ImageViewTicket) hBox.getChildren().get(0);
                        if (imagenTBs.get(i).getIdSubRelacion().equalsIgnoreCase(imageViewTicket.getId())) {
                            imageViewTicket.setUrl(imagenTBs.get(i).getImagen());
                            imageViewTicket.setImage(new javafx.scene.image.Image(new ByteArrayInputStream(imagenTBs.get(i).getImagen())));
                        }
                    }
                }
            }
        }

        for (int i = 0; i < imagenTBs.size(); i++) {
            for (int m = 0; m < apDetalleCabecera.getChildren().size(); m++) {
                HBox hBox = (HBox) apDetalleCabecera.getChildren().get(m);
                if (hBox.getChildren().size() == 1) {
                    if (hBox.getChildren().get(0) instanceof ImageViewTicket) {
                        ImageViewTicket imageViewTicket = (ImageViewTicket) hBox.getChildren().get(0);
                        if (imagenTBs.get(i).getIdSubRelacion().equalsIgnoreCase(imageViewTicket.getId())) {
                            imageViewTicket.setUrl(imagenTBs.get(i).getImagen());
                            imageViewTicket.setImage(new javafx.scene.image.Image(new ByteArrayInputStream(imagenTBs.get(i).getImagen())));
                        }
                    }
                }
            }
        }

        for (int i = 0; i < imagenTBs.size(); i++) {
            for (int m = 0; m < apPie.getChildren().size(); m++) {
                HBox hBox = (HBox) apPie.getChildren().get(m);
                if (hBox.getChildren().size() == 1) {
                    if (hBox.getChildren().get(0) instanceof ImageViewTicket) {
                        ImageViewTicket imageViewTicket = (ImageViewTicket) hBox.getChildren().get(0);
                        if (imagenTBs.get(i).getIdSubRelacion().equalsIgnoreCase(imageViewTicket.getId())) {
                            imageViewTicket.setUrl(imagenTBs.get(i).getImagen());
                            imageViewTicket.setImage(new javafx.scene.image.Image(new ByteArrayInputStream(imagenTBs.get(i).getImagen())));
                        }
                    }
                }
            }
        }

    }

    private HBox generateElement(AnchorPane contenedor, String id) {
        if (contenedor.getChildren().isEmpty()) {
            return addElement(contenedor, id + "1", true);
        } else {
            HBox hBox = (HBox) contenedor.getChildren().get(contenedor.getChildren().size() - 1);
            String idGenerate = hBox.getId();
            String codigo = idGenerate.substring(2);
            int valor = Integer.parseInt(codigo) + 1;
            String newCodigo = id + valor;
            return addElement(contenedor, newCodigo, true);
        }
    }

    private HBox addElement(AnchorPane contenedor, String id, boolean useLayout) {
        double layoutY = 0;
        if (useLayout) {
            for (int i = 0; i < contenedor.getChildren().size(); i++) {
                layoutY += ((HBox) contenedor.getChildren().get(i)).getPrefHeight();
            }
        }

        HBox hBox = new HBox();
        hBox.setId(id);
        hBox.setLayoutX(0);
        hBox.setLayoutY(layoutY);
        hBox.setPrefWidth(sheetWidth * pointWidthSizeView);
        hBox.setPrefHeight(30);
        if (useLayout) {
            contenedor.getChildren().add(hBox);
        }
        return hBox;
    }

    private TextFieldTicket addElementTextField(String id, String titulo, boolean multilinea, short lines, short widthColumn, Pos align, boolean editable, String variable,String font,float size) {
        TextFieldTicket field = new TextFieldTicket(titulo, id);
        field.setMultilineas(multilinea);
        field.setLines(lines);
        field.setColumnWidth(widthColumn);
        field.setVariable(variable);
        field.setEditable(editable);
        field.getStyleClass().add("text-field-ticket");
        field.setAlignment(align);
         field.setFontName(font);
        field.setFontSize(size);
        return field;
    }

    private ImageViewTicket addElementImageView(String path, short widthColumn, double width, double height, boolean newImage) {
        ImageViewTicket imageView = new ImageViewTicket();
        imageView.setColumnWidth(widthColumn);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setSmooth(true);
        if (newImage) {
            imageView.setImage(new javafx.scene.image.Image(path));
            imageView.setUrl(Tools.getImageBytes(imageView.getImage(), Tools.getFileExtension(new File(path))));
        }
        return imageView;
    }

    private Pos getAlignment(String align) {
        switch (align) {
            case "CENTER":
                return Pos.CENTER;
            case "CENTER_LEFT":
                return Pos.CENTER_LEFT;
            case "CENTER_RIGHT":
                return Pos.CENTER_RIGHT;
            default:
                return Pos.CENTER_LEFT;
        }
    }

    public void setSheetWidth(int sheetWidth) {
        this.sheetWidth = sheetWidth;
    }

}
