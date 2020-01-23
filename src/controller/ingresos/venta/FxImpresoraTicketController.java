package controller.ingresos.venta;

import controller.tools.PrinterService;
import controller.tools.Session;
import controller.tools.Tools;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class FxImpresoraTicketController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private ComboBox<String> cbImpresoras;
    @FXML
    private CheckBox cbCortarPapel;

    private FxVentaEstructuraController ventaEstructuraController;

    private PrinterService printerService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        printerService = new PrinterService();
        printerService.getPrinters().forEach(e -> {
            cbImpresoras.getItems().add(e);
        });

    }

    public void loadConfigurationDefauld() {
        for (int i = 0; i < cbImpresoras.getItems().size(); i++) {
            if (cbImpresoras.getItems().get(i).equalsIgnoreCase(Session.NOMBRE_IMPRESORA)) {
                cbImpresoras.getSelectionModel().select(i);
                break;
            }
        }
        cbCortarPapel.setSelected(Session.CORTAPAPEL_IMPRESORA);
    }

    private void eventGuardarImpresora() {
        if (cbImpresoras.getSelectionModel().getSelectedIndex() >= 0) {
            String ruta = "./archivos/printSetting.properties";
            boolean state = false;

            try (OutputStream output = new FileOutputStream(ruta)) {

                Properties prop = new Properties();
                prop.setProperty("printername", cbImpresoras.getSelectionModel().getSelectedItem());
                prop.setProperty("printcutspaper", cbCortarPapel.isSelected() + "");

                prop.store(output, "Ruta de configuración de la impresora");
                state = true;
                Tools.AlertMessageInformation(window, "Impresora", "Se guardo la configuración correctamente.");
            } catch (IOException io) {
                state = false;
                Tools.AlertMessageError(window, "Impresora", "Error al crear el archivo: " + io.getLocalizedMessage());
            } finally {
                if (state) {
                    iniciarRutasImpresion();
                }
            }
        } else {
            Tools.AlertMessageWarning(window, "Impresora de ticket", "Seleccione una impresora");
        }
    }

    private void iniciarRutasImpresion() {
        String ruta = "./archivos/printSetting.properties";
        try (InputStream input = new FileInputStream(ruta)) {

            Properties prop = new Properties();
            prop.load(input);

            Session.ESTADO_IMPRESORA = true;
            Session.NOMBRE_IMPRESORA = prop.getProperty("printername");
            Session.CORTAPAPEL_IMPRESORA = Boolean.parseBoolean(prop.getProperty("printcutspaper"));
            Tools.AlertMessageInformation(window, "Impresora de ticket", "Se ha creado una ruta de impresión con la impresora " + Session.NOMBRE_IMPRESORA + ", por defecto " + (Session.CORTAPAPEL_IMPRESORA ? "corta papel" : "no corta papel") + ".");
            Tools.Dispose(window);
        } catch (IOException ex) {
            Tools.AlertMessageError(window, "Impresora de ticket", "Se ha producido un error al crear la ruta de impresión, "+ex.getLocalizedMessage());
            Session.ESTADO_IMPRESORA = false;            
        }
    }

    private void eventImprimirPrueba() {
        if (cbImpresoras.getSelectionModel().getSelectedIndex() >= 0) {
            if (ventaEstructuraController != null) {
                String text = "Impresora " + cbImpresoras.getSelectionModel().getSelectedItem()
                        + "\nPara uso de todo tipo de tickets"
                        + "\nCorta papel"
                        + "\n\n\n\n\n\n\n\n\n\n";
                printerService.printString(cbImpresoras.getSelectionModel().getSelectedItem(), text, cbCortarPapel.isSelected());
                ventaEstructuraController.imprimirPrueba(cbImpresoras.getSelectionModel().getSelectedItem(), cbCortarPapel.isSelected(),"Impresion de prueba", new TableView<>(), "00.00", "00.00", "00.00", "00.00", 0.00, 0.00, "0000-00000000", "789456123654987123659","00000000","PUBLICO GENERAL");
            }
        } else {
            Tools.AlertMessageWarning(window, "Impresora de ticket", "Seleccione una impresora");
        }
    }

    @FXML
    private void onKeyPressedGuardar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            eventGuardarImpresora();
        }
    }

    @FXML
    private void onActionGuardar(ActionEvent event) {
        eventGuardarImpresora();
    }

    @FXML
    private void onKeyPressedProbar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            eventImprimirPrueba();
        }
    }

    @FXML
    private void onActionProbar(ActionEvent event) {
        eventImprimirPrueba();
    }

    public void setInitVentaEstructuraController(FxVentaEstructuraController ventaEstructuraController) {
        this.ventaEstructuraController = ventaEstructuraController;
    }

}
