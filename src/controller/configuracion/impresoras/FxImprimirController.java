package controller.configuracion.impresoras;

import controller.configuracion.tickets.FxTicketController;
import controller.tools.BillPrintable;
import controller.tools.PrinterService;
import controller.tools.TextFieldTicket;
import controller.tools.Tools;
import java.io.File;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

                for (int i = 0; i < ticketController.getHbEncabezado().getChildren().size(); i++) {
                    HBox box = ((HBox) ticketController.getHbEncabezado().getChildren().get(i));
                    for (int j = 0; j < box.getChildren().size(); j++) {
                        TextFieldTicket textFieldTicket = ((TextFieldTicket) box.getChildren().get(j));
                        System.out.println(textFieldTicket.getText());
                        System.out.println(textFieldTicket.getPrefWidth());
                    }
                }

                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.newDocument();
                    doc.setXmlStandalone(true);

                    Element rootElement = doc.createElement("jasperReport");
                    addAttr(doc, rootElement, "xmlns", "http://jasperreports.sourceforge.net/jasperreports");
                    addAttr(doc, rootElement, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
                    addAttr(doc, rootElement, "xsi:schemaLocation", "http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd");
                    addAttr(doc, rootElement, "name", "name");
                    addAttr(doc, rootElement, "pageWidth", "" + ((int) ticketController.getVbContenedor().getPrefWidth()));
                    addAttr(doc, rootElement, "pageHeight", "300");
                    addAttr(doc, rootElement, "columnWidth", "" + ((int) ticketController.getVbContenedor().getPrefWidth()));
                    addAttr(doc, rootElement, "leftMargin", "0");
                    addAttr(doc, rootElement, "rightMargin", "0");
                    addAttr(doc, rootElement, "topMargin", "0");
                    addAttr(doc, rootElement, "bottomMargin", "0");
                    doc.appendChild(rootElement);

                    Element propertyzoom = doc.createElement("property");
                    addAttr(doc, propertyzoom, "name", "ireport.zoom");
                    addAttr(doc, propertyzoom, "value", "1.0");
                    rootElement.appendChild(propertyzoom);

                    Element propertyx = doc.createElement("property");
                    addAttr(doc, propertyx, "name", "ireport.x");
                    addAttr(doc, propertyx, "value", "0");
                    rootElement.appendChild(propertyx);

                    Element propertyy = doc.createElement("property");
                    addAttr(doc, propertyy, "name", "ireport.y");
                    addAttr(doc, propertyy, "value", "0");
                    rootElement.appendChild(propertyy);

                    Element title = doc.createElement("title");
                    //band
                    Element bandtitle = doc.createElement("band");
                    addAttr(doc, bandtitle, "height", "" + 100);
                    addStaticText(doc, bandtitle, 0, 0, 313, 50);
//            addTextField(doc, bandtitle, 0, 50, 100, 50);
                    //create new elements

                    //add elements
                    title.appendChild(bandtitle);
                    rootElement.appendChild(title);
                    //Generate XML        
                    //""
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                    DOMSource source = new DOMSource(doc);
                    StreamResult result = new StreamResult(new File("./src/controller/configuracion/tickets/pruebajrxml.jrxml"));
                    transformer.transform(source, result);

                } catch (ParserConfigurationException | TransformerException | DOMException ex) {
                    System.out.println(ex.getLocalizedMessage());
                    System.out.println(ex);
                }

//                billPrintable.setSheetWidth(ticketController.getSheetWidth());
//                ArrayList<HBox> object = new ArrayList<>();
//                int rows = 0;
//                int lines = 0;
//                for (int i = 0; i < ticketController.getHbEncabezado().getChildren().size(); i++) {
//                    object.add((HBox) ticketController.getHbEncabezado().getChildren().get(i));
//                    HBox box = ((HBox) ticketController.getHbEncabezado().getChildren().get(i));
//                    rows++;
//                    for (int j = 0; j < box.getChildren().size(); j++) {
//                        lines += ((TextFieldTicket) box.getChildren().get(j)).getLines();
//                    }
//                }
//                
//                for (int i = 0; i < ticketController.getHbDetalleCabecera().getChildren().size(); i++) {
//                    object.add((HBox) ticketController.getHbDetalleCabecera().getChildren().get(i));
//                    HBox box = ((HBox) ticketController.getHbDetalleCabecera().getChildren().get(i));
//                    rows++;
//                    for (int j = 0; j < box.getChildren().size(); j++) {
//                        lines += ((TextFieldTicket) box.getChildren().get(j)).getLines();
//                    }
//                }
//
//                for (int i = 0; i < ticketController.getHbPie().getChildren().size(); i++) {                    
//                    object.add((HBox) ticketController.getHbPie().getChildren().get(i));
//                    HBox box = ((HBox) ticketController.getHbPie().getChildren().get(i));
//                    rows++;
//                    for (int j = 0; j < box.getChildren().size(); j++) {
//                        lines += ((TextFieldTicket) box.getChildren().get(j)).getLines();
//                    }
//                }
//                billPrintable.modelTicket(apWindow, rows + lines + 1 + 5, lines, object, "Imprimir", "Error al imprimir el ticket.", cbImpresoras.getSelectionModel().getSelectedItem(), cbCortarPapel.isSelected());
            }
        } else {
            Tools.AlertMessageWarning(apWindow, "Imprimir", "Seleccione un impresora para continuar.");
            cbImpresoras.requestFocus();
        }
    }

    private void addAttr(Document doc, Element element, String name, String value) {
        Attr attr = doc.createAttribute(name);
        attr.setValue(value);
        element.setAttributeNode(attr);
    }

    private void addStaticText(Document doc, Element element, int x, int y, int width, int height) {
        Element property = doc.createElement("staticText");

        Element reportElement = doc.createElement("reportElement");
        addAttr(doc, reportElement, "x", "" + x);
        addAttr(doc, reportElement, "y", "" + y);
        addAttr(doc, reportElement, "width", "" + width);
        addAttr(doc, reportElement, "height", "" + height);
        property.appendChild(reportElement);

        Element textElement = doc.createElement("textElement");
        addAttr(doc, textElement, "textAlignment", "Left");
        Element font = doc.createElement("font");
        addAttr(doc, font, "fontName", "Monospaced");
        addAttr(doc, font, "size", "12");
        textElement.appendChild(font);
        property.appendChild(textElement);

        Element text = doc.createElement("text");
        text.appendChild(doc.createTextNode("Static Text"));
        property.appendChild(text);

        element.appendChild(property);
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
