package controller.operaciones.venta;

import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.SuministroTB;

public class FxVentaGranelController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private Label lblTitle;
    @FXML
    private TextField txtImporte;
    @FXML
    private Label lblArticulo;

    private FxVentaEstructuraController ventaEstructuraController;

    private SuministroTB suministroTB;

    private double oldPrecio;

    private boolean opcion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
    }

    public void initComponents(String value, SuministroTB suministroTB, boolean opcion) {
        lblTitle.setText(value);
        this.suministroTB = suministroTB;
        this.opcion = opcion;
        lblArticulo.setText(suministroTB.getNombreMarca());
    }

    private void executeEventAceptar() {
        double importe = Tools.isNumeric(txtImporte.getText())
                ? (Double.parseDouble(txtImporte.getText()) <= 0 ? oldPrecio : Double.parseDouble(txtImporte.getText()))
                : oldPrecio;

        double value = opcion ? importe + suministroTB.getPrecioVentaGeneralUnico(): importe;

        double porcentajeRestante = value * (suministroTB.getDescuento() / 100.00);
        double preciocalculado = value - porcentajeRestante;

        suministroTB.setDescuentoCalculado(porcentajeRestante);
        suministroTB.setDescuentoSumado(porcentajeRestante * suministroTB.getCantidad());

        suministroTB.setPrecioVentaGeneralUnico(value);
        suministroTB.setPrecioVentaGeneralReal(preciocalculado);

        double impuesto = Tools.calculateTax(suministroTB.getImpuestoValor(), suministroTB.getPrecioVentaGeneralReal());

        suministroTB.setImpuestoSumado(suministroTB.getCantidad() * impuesto);
        suministroTB.setPrecioVentaGeneral(suministroTB.getPrecioVentaGeneralReal() + impuesto);

        suministroTB.setSubImporte(suministroTB.getPrecioVentaGeneralUnico() * suministroTB.getCantidad());
        suministroTB.setSubImporteDescuento(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
        suministroTB.setTotalImporte(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());

        ventaEstructuraController.getTvList().refresh();
        ventaEstructuraController.calculateTotales();
        Tools.Dispose(window);
        ventaEstructuraController.getTxtSearch().requestFocus();
        ventaEstructuraController.getTxtSearch().clear();
    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        executeEventAceptar();
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            executeEventAceptar();
        }
    }

    @FXML
    private void onKeyPressedCancelar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(window);
            ventaEstructuraController.getTxtSearch().requestFocus();
            ventaEstructuraController.getTxtSearch().clear();
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        Tools.Dispose(window);
        ventaEstructuraController.getTxtSearch().requestFocus();
        ventaEstructuraController.getTxtSearch().clear();
    }

    @FXML
    private void onKeyTypedImporte(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtImporte.getText().contains(".") || c == '-' && txtImporte.getText().contains("-")) {
            event.consume();
        }
    }

    public void setInitVentaEstructuraController(FxVentaEstructuraController ventaEstructuraController) {
        this.ventaEstructuraController = ventaEstructuraController;
    }

}
