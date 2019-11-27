package controller.configuracion.etiquetas;

import controller.tools.PrinterService;
import controller.tools.Tools;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javax.print.PrintService;

public class FxImpresoraEtiquetaController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private ComboBox<String> cbImpresoras;
    @FXML
    private Spinner<Integer> spCopias;

    private PrinterService printerService;

    private Printable printable;

    private double widthEtiquetaMM;

    private double heightEtiquetaMM;

    private int orientacionEtiqueta;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
        printerService = new PrinterService();
        printerService.getPrinters().forEach(e -> {
            cbImpresoras.getItems().add(e);
        });

        spCopias.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));
        spCopias.setOnMousePressed((event) -> {
            if (event.getButton() == MouseButton.PRIMARY) {

            }
        });
        spCopias.getEditor().setOnAction((event) -> {

        });
        spCopias.getEditor().setOnKeyTyped((event) -> {
            char c = event.getCharacter().charAt(0);
            if ((c < '0' || c > '9') && (c != '\b')) {
                event.consume();
            }
        });
    }

    public void loadImpresoraEtiqueta(Printable printable, double widthEtiquetaMM, double heightEtiquetaMM, int orientacionEtiqueta) {
        this.printable = printable;
        this.widthEtiquetaMM = widthEtiquetaMM;
        this.heightEtiquetaMM = heightEtiquetaMM;
        this.orientacionEtiqueta = orientacionEtiqueta;
    }

    private void eventImprimir() {
        if (cbImpresoras.getSelectionModel().getSelectedIndex() >= 0) {
            PrinterJob pj = PrinterJob.getPrinterJob();
            pj.setCopies((int) spCopias.getValue());
            Book book = new Book();
            book.append(printable, getPageFormat(pj));
            pj.setPageable(book);
            for (PrintService printService : PrinterJob.lookupPrintServices()) {
                if (cbImpresoras.getSelectionModel().getSelectedItem().equals(printService.getName())) {
                    try {
                        pj.setPrintService(printService);
                        pj.print();
                    } catch (PrinterException ex) {
                    }
                }
            }
        }else{
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Ventana de impresión", "Seleccione una impresa.", false);
            cbImpresoras.requestFocus();
        }
    }

    public PageFormat getPageFormat(PrinterJob pj) {
        PageFormat pf = pj.defaultPage();
        Paper paper = pf.getPaper();
        paper.setSize(converMmToPoint(widthEtiquetaMM), converMmToPoint(heightEtiquetaMM));
        paper.setImageableArea(0, 0, pf.getWidth(), pf.getHeight());
        pf.setOrientation(orientacionEtiqueta);
        pf.setPaper(paper);
        return pf;
    }

    public double converMmToPoint(double mm) {
        return (mm * 2.83465) / 1;
    }

    @FXML
    private void onKeyPressedImprimir(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            eventImprimir();
        }
    }

    @FXML
    private void onActionImprimir(ActionEvent event) {
        eventImprimir();
    }

}
