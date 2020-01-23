package controller.tools;

import br.com.adilson.util.PrinterMatrix;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import model.SuministroTB;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class BillPrintable implements Printable {

    private String tickt;

    private int sheetWidth;

    public BillPrintable() {
        sheetWidth = 0;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

        if (pageIndex == 0) {
            Graphics2D g2d = (Graphics2D) graphics;
            double width = pageFormat.getImageableWidth();

            g2d.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
            ////////// code by alqama//////////////
            FontMetrics metrics = g2d.getFontMetrics(new Font("Arial", Font.PLAIN, 9));

            int y = 20;
            int yShift = 12;
            ///////////////// Product price Get ///////////
            Date date = new Date();
            SimpleDateFormat fecha = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat hora = new SimpleDateFormat("hh:mm:ss aa");

//            g2d.setFont(new Font("Arial", Font.PLAIN, 9));
//            g2d.setPaint(Color.BLACK);
//            g2d.drawString(Session.COMPANY_RAZON_SOCIAL, centerText(width, metrics, Session.COMPANY_RAZON_SOCIAL), y);
//            
//                y += yShift;
//                g2d.drawString(Session.DIRECCION, centerText(width, metrics, Session.DIRECCION), y);
//                y += yShift;
//                g2d.drawString("TEL: " + Session.TELEFONO + " CEL:" + Session.CELULAR, centerText(width, metrics, "TEL: " + Session.TELEFONO + " CEL:" + Session.CELULAR), y);
//                y += yShift;
//                g2d.drawString("RUC " + Session.RUC, centerText(width, metrics, "RUC " + Session.RUC), y);
//            y += yShift;
//            g2d.drawString("N° Ticket: " + tickt, centerText(width, metrics, "N° Ticket: " + tickt), y);
//            y += yShift;
//            g2d.drawString("\n", 0, y);
//            y += yShift;
//            g2d.drawString("CANT ARTICULO", 0, y);
//            g2d.drawString("IMPORTE", (int) (width - metrics.stringWidth("IMPORTE")), y);
//            for (int i = 0; i < tvList.getItems().size(); i++) {
//                y += yShift;
//                g2d.drawString(
//                        (tvList.getItems().get(i).getCantidad() + " " + tvList.getItems().get(i).getNombreMarca()).length() >= 25
//                        ? (tvList.getItems().get(i).getCantidad() + " " + tvList.getItems().get(i).getNombreMarca()).substring(0, 25)
//                        : (tvList.getItems().get(i).getCantidad() + " " + tvList.getItems().get(i).getNombreMarca()),
//                        0, y);
//                g2d.drawString(getText(tvList.getItems().get(i).getTotalImporte()), rightText(width, metrics, getText(tvList.getItems().get(i).getTotalImporte())), y);
//            }
//            y += yShift;
//            g2d.drawString("\n", 0, y);
//            y += yShift;
//            g2d.drawString("TOTAL DESCUENTO", 0, y);
//            g2d.drawString(getText(descuento), rightText(width, metrics, getText(descuento)), y);
//            y += yShift;
//            g2d.drawString("SUBTOTAL", 0, y);
//            g2d.drawString(getText(subTotal), rightText(width, metrics, getText(subTotal)), y);
//            y += yShift;
//            g2d.drawString("\n", 0, y);
//                y += yShift;
//                g2d.drawString("OP. GRAVADA", 0, y);
//                g2d.drawString(getText(gravada), rightText(width, metrics, getText(gravada)), y);
//                y += yShift;
//                g2d.drawString("I.G.V", 0, y);
//                g2d.drawString(getText(igv), rightText(width, metrics, getText(igv)), y);
//            y += yShift;
//            g2d.drawString("IMPORTE TOTAL", 0, y);
//            g2d.drawString(getText(total), rightText(width, metrics, getText(total)), y);
//            y += yShift;
//            g2d.drawString("\n", 0, y);
//            y += yShift;
//            g2d.drawString("EFECTIVO", 0, y);
//            g2d.drawString(getText(efectivo), rightText(width, metrics, getText(efectivo)), y);
//            y += yShift;
//            g2d.drawString("VUELTO", 0, y);
//            g2d.drawString(getText(vuelto), rightText(width, metrics, getText(vuelto)), y);
//                y += yShift;
//                g2d.drawString("Son: CIENTO CUARENTA Y NUEVE SOLES", 0, y);
//                y += yShift;
//                g2d.drawString("\n", 0, y);
//                y += yShift;
//                g2d.drawString("¡Que tengas un buen dia!", centerText(width, metrics, "¡Que tengas un buen dia!"), y);
//            y += yShift;
//            g2d.drawString("\n", 0, y);
//            y += yShift;
//            g2d.drawString("Fecha: " + fecha.format(date), 0, y);
//            g2d.drawString("Hora: " + hora.format(date), rightText(width, metrics, "Hora: " + hora.format(date)), y);
            g2d.dispose();
            return (PAGE_EXISTS);
        } else {
            return (NO_SUCH_PAGE);
        }

    }

    public int centerText(double width, FontMetrics metrics, String text) {
        return (int) ((width - metrics.stringWidth(text)) / 2);
    }

    public int rightText(double width, FontMetrics metrics, String text) {
        return (int) (width - metrics.stringWidth(text));
    }

    private String getText(double value) {
        return Tools.roundingValue(value, 2);
    }

    public int hbEncebezado(HBox box, String nombreTicketImpresion, String ticket, String numCliente, String infoCliente) {
        int lines = 0;
        for (int j = 0; j < box.getChildren().size(); j++) {
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
                fieldTicket.setText(nombreTicketImpresion);
            } else if (fieldTicket.getVariable().equalsIgnoreCase("numventa")) {
                fieldTicket.setText(ticket);
            } else if (fieldTicket.getVariable().equalsIgnoreCase("codigo")) {
                fieldTicket.setText(Session.TICKET_CODIGOVENTA);
            } else if (fieldTicket.getVariable().equalsIgnoreCase("numcliente")) {
                fieldTicket.setText(numCliente);
            } else if (fieldTicket.getVariable().equalsIgnoreCase("infocliente")) {
                fieldTicket.setText(infoCliente);
            }
            lines = fieldTicket.getLines();
        }
        return lines;
    }

    public int hbDetalle(HBox hBox, HBox box, ObservableList<SuministroTB> arrList, int m) {
        int lines = 0;
        for (int j = 0; j < box.getChildren().size(); j++) {
            TextFieldTicket fieldTicket = ((TextFieldTicket) box.getChildren().get(j));
            if (fieldTicket.getVariable().equalsIgnoreCase("codbarrasarticulo")) {
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
            hBox.getChildren().add(addElementTextField("iu", fieldTicket.getText(), fieldTicket.isMultilineas(), fieldTicket.getLines(), fieldTicket.getColumnWidth(), fieldTicket.getAlignment(), fieldTicket.isEditable(), fieldTicket.getVariable()));
            lines = fieldTicket.getLines();
        }
        return lines;
    }

    public int hbPie(HBox box, String valorVenta, String descuento, String subTotal, String total, double efectivo, double vuelto, String numCliente, String infoCliente) {
        int lines = 0;
        for (int j = 0; j < box.getChildren().size(); j++) {
            TextFieldTicket fieldTicket = ((TextFieldTicket) box.getChildren().get(j));
            if (fieldTicket.getVariable().equalsIgnoreCase("fchactual")) {
                fieldTicket.setText(Tools.getDate("dd/MM/yyyy"));
            } else if (fieldTicket.getVariable().equalsIgnoreCase("horactual")) {
                fieldTicket.setText(Tools.getHour("hh:mm:ss aa"));
            } else if (fieldTicket.getVariable().equalsIgnoreCase("subtotal")) {
                fieldTicket.setText(Session.TICKET_SIMBOLOMONEDA + " " + valorVenta);
            } else if (fieldTicket.getVariable().equalsIgnoreCase("dscttotal")) {
                fieldTicket.setText(Session.TICKET_SIMBOLOMONEDA + " " + descuento);
            } else if (fieldTicket.getVariable().equalsIgnoreCase("imptotal")) {
                fieldTicket.setText(Session.TICKET_SIMBOLOMONEDA + " " + subTotal);
            } else if (fieldTicket.getVariable().equalsIgnoreCase("totalpagar")) {
                fieldTicket.setText(Session.TICKET_SIMBOLOMONEDA + " " + total);
            } else if (fieldTicket.getVariable().equalsIgnoreCase("efectivo")) {
                fieldTicket.setText(Session.TICKET_SIMBOLOMONEDA + " " + Tools.roundingValue(efectivo, 2));
            } else if (fieldTicket.getVariable().equalsIgnoreCase("vuelto")) {
                fieldTicket.setText(Session.TICKET_SIMBOLOMONEDA + " " + Tools.roundingValue(vuelto, 2));
            } else if (fieldTicket.getVariable().equalsIgnoreCase("codigo")) {
                fieldTicket.setText(Session.TICKET_CODIGOVENTA);
            } else if (fieldTicket.getVariable().equalsIgnoreCase("numcliente")) {
                fieldTicket.setText(numCliente);
            } else if (fieldTicket.getVariable().equalsIgnoreCase("infocliente")) {
                fieldTicket.setText(infoCliente);
            }
            lines = fieldTicket.getLines();
        }
        return lines;
    }

    public void modelTicket(Node window, int rows, int lines, ArrayList<HBox> object, String messageClassTitle, String messageClassContent, String nombreimpresora, boolean cortar) {
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

    private TextFieldTicket addElementTextField(String id, String titulo, boolean multilinea, short lines, short widthColumn, Pos align, boolean editable, String variable) {
        TextFieldTicket field = new TextFieldTicket(titulo, id);
        field.setMultilineas(multilinea);
        field.setLines(lines);
        field.setColumnWidth(widthColumn);
        field.setVariable(variable);
        field.setEditable(editable);
        field.getStyleClass().add("text-field-ticket");
        field.setAlignment(align);
        return field;
    }

    public void loadEstructuraTicket(String file, VBox hbEncabezado, VBox hbDetalleCabecera, VBox hbPie) {
        JSONObject jSONObject = Json.obtenerObjetoJSON(file);
        hbEncabezado.getChildren().clear();
        hbDetalleCabecera.getChildren().clear();
        hbPie.getChildren().clear();

        sheetWidth = jSONObject.get("column") != null ? Short.parseShort(jSONObject.get("column").toString()) : (short) 40;

        if (jSONObject.get("cabecera") != null) {
            JSONObject cabeceraObjects = Json.obtenerObjetoJSON(jSONObject.get("cabecera").toString());
            for (int i = 0; i < cabeceraObjects.size(); i++) {
                HBox box = generateElement(hbEncabezado, "cb");
                JSONObject objectObtener = Json.obtenerObjetoJSON(cabeceraObjects.get("cb_" + (i + 1)).toString());
                if (objectObtener.get("text") != null) {
                    JSONObject object = Json.obtenerObjetoJSON(objectObtener.get("text").toString());
                    TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Short.parseShort(object.get("lines").toString()), Short.parseShort(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()));
                    box.getChildren().add(field);
                } else if (objectObtener.get("list") != null) {
                    JSONArray array = Json.obtenerArrayJSON(objectObtener.get("list").toString());
                    Iterator it = array.iterator();
                    while (it.hasNext()) {
                        JSONObject object = Json.obtenerObjetoJSON(it.next().toString());
                        TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Short.parseShort(object.get("lines").toString()), Short.parseShort(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()));
                        box.getChildren().add(field);
                    }
                }
            }
        }
        if (jSONObject.get("detalle") != null) {
            JSONObject detalleObjects = Json.obtenerObjetoJSON(jSONObject.get("detalle").toString());
            for (int i = 0; i < detalleObjects.size(); i++) {
                HBox box = generateElement(hbDetalleCabecera, "dr");
                JSONObject objectObtener = Json.obtenerObjetoJSON(detalleObjects.get("dr_" + (i + 1)).toString());
                if (objectObtener.get("text") != null) {
                    JSONObject object = Json.obtenerObjetoJSON(objectObtener.get("text").toString());
                    TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Short.parseShort(object.get("lines").toString()), Short.parseShort(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()));
                    box.getChildren().add(field);
                } else if (objectObtener.get("list") != null) {
                    JSONArray array = Json.obtenerArrayJSON(objectObtener.get("list").toString());
                    Iterator it = array.iterator();
                    while (it.hasNext()) {
                        JSONObject object = Json.obtenerObjetoJSON(it.next().toString());
                        TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Short.parseShort(object.get("lines").toString()), Short.parseShort(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()));
                        box.getChildren().add(field);
                    }
                }
            }
        }

        if (jSONObject.get("pie") != null) {
            JSONObject pieObjects = Json.obtenerObjetoJSON(jSONObject.get("pie").toString());
            for (int i = 0; i < pieObjects.size(); i++) {
                HBox box = generateElement(hbPie, "cp");
                JSONObject objectObtener = Json.obtenerObjetoJSON(pieObjects.get("cp_" + (i + 1)).toString());
                if (objectObtener.get("text") != null) {
                    JSONObject object = Json.obtenerObjetoJSON(objectObtener.get("text").toString());
                    TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Short.parseShort(object.get("lines").toString()), Short.parseShort(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()));
                    box.getChildren().add(field);
                } else if (objectObtener.get("list") != null) {
                    JSONArray array = Json.obtenerArrayJSON(objectObtener.get("list").toString());
                    Iterator it = array.iterator();
                    while (it.hasNext()) {
                        JSONObject object = Json.obtenerObjetoJSON(it.next().toString());
                        TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Short.parseShort(object.get("lines").toString()), Short.parseShort(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()));
                        box.getChildren().add(field);
                    }

                }

            }
        }

    }

    private HBox generateElement(VBox contenedor, String id) {
        if (contenedor.getChildren().isEmpty()) {
            return addElement(contenedor, id + "1");
        } else {
            HBox hBox = (HBox) contenedor.getChildren().get(contenedor.getChildren().size() - 1);
            String idGenerate = hBox.getId();
            String codigo = idGenerate.substring(2);
            int valor = Integer.parseInt(codigo) + 1;
            String newCodigo = id + valor;
            return addElement(contenedor, newCodigo);
        }
    }

    private HBox addElement(VBox contenedor, String id) {
        HBox hBox = new HBox();
        hBox.setId(id);
        hBox.setPrefHeight(30);
        contenedor.getChildren().add(hBox);
        return hBox;
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
