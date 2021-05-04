package controller.operaciones.cotizacion;

import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.SuministroTB;

public class FxCotizacionModalController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private Label lblArticulo;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtDescuento;

    private FxCotizacionController cotizacionController;

    private SuministroTB suministroTB;

    private double oldCantidad;

    private double oldPrecio;

    private double oldDescuento;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
    }

    public void initComponents(SuministroTB suministroTB, boolean exists) {
        this.suministroTB = suministroTB;
        this.oldCantidad = exists ? suministroTB.getCantidad() : 1;
        this.oldPrecio = suministroTB.getPrecioVentaGeneral();
        this.oldDescuento = exists ? suministroTB.getDescuento() : 0;
        txtCantidad.setText(Tools.roundingValue(oldCantidad, 2));
        txtPrecio.setText(Tools.roundingValue(oldPrecio, 2));
        txtDescuento.setText(Tools.roundingValue(oldDescuento, 2));
        lblArticulo.setText(suministroTB.getNombreMarca());
    }

    private void onEventAceptar() {
        double cantidadActual = Tools.isNumeric(txtCantidad.getText().trim())
                ? (Double.parseDouble(txtCantidad.getText()) <= 0 ? oldCantidad : Double.parseDouble(txtCantidad.getText()))
                : oldCantidad;

        double precioActual = Tools.isNumeric(txtPrecio.getText().trim())
                ? (Double.parseDouble(txtPrecio.getText()) <= 0 ? oldPrecio : Double.parseDouble(txtPrecio.getText()))
                : oldPrecio;

        double descuentoActual = Tools.isNumeric(txtDescuento.getText().trim())
                ? (Double.parseDouble(txtDescuento.getText()) < 0 ? oldDescuento : Double.parseDouble(txtDescuento.getText()))
                : oldDescuento;

        SuministroTB newSuministro = new SuministroTB();
        newSuministro.setIdSuministro(suministroTB.getIdSuministro());
        newSuministro.setClave(suministroTB.getClave());
        newSuministro.setNombreMarca(suministroTB.getNombreMarca());
        newSuministro.setCantidad(cantidadActual);
        newSuministro.setCostoCompra(suministroTB.getCostoCompra());
        newSuministro.setDescuento(descuentoActual);
        newSuministro.setPrecioVentaGeneral(precioActual);
        double descuento = newSuministro.getDescuento();
        double precioDescuento = newSuministro.getPrecioVentaGeneral() - descuento;
        newSuministro.setImporteNeto(newSuministro.getCantidad() * precioDescuento);

        newSuministro.setImpuestoOperacion(suministroTB.getImpuestoOperacion());
        newSuministro.setImpuestoId(suministroTB.getImpuestoId());
        newSuministro.setImpuestoNombre(suministroTB.getImpuestoNombre());
        newSuministro.setImpuestoValor(suministroTB.getImpuestoValor());

        newSuministro.setInventario(suministroTB.isInventario());
        newSuministro.setUnidadVenta(suministroTB.getUnidadVenta());
        newSuministro.setValorInventario(suministroTB.getValorInventario());

        Button button = new Button("X");
        button.getStyleClass().add("buttonDark");
        button.setOnAction(e -> {
            cotizacionController.getTvList().getItems().remove(newSuministro);
            cotizacionController.calculateTotales();
        });
        button.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                cotizacionController.getTvList().getItems().remove(newSuministro);
                cotizacionController.calculateTotales();
            }
        });
        newSuministro.setRemover(button);
        Tools.Dispose(apWindow);
        cotizacionController.getAddArticulo(newSuministro);
    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        onEventAceptar();
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventAceptar();
        }
    }

    @FXML
    private void onKeyPressedCerrar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(apWindow);
        }
    }

    @FXML
    private void onActionCerrar(ActionEvent event) {
        Tools.Dispose(apWindow);
    }

    @FXML
    private void onKeyTypedCantidad(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
            event.consume();
        }
        if (c == '.' && txtCantidad.getText().contains(".")) {
            event.consume();
        }
    }

    public TextField getTxtCantidad() {
        return txtCantidad;
    }

    public void setInitCotizacionController(FxCotizacionController cotizacionController) {
        this.cotizacionController = cotizacionController;
    }

}
