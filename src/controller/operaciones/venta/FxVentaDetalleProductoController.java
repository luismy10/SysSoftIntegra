package controller.operaciones.venta;

import controller.tools.BbItemProducto;
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

public class FxVentaDetalleProductoController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TextField txtDescuento;
    @FXML
    private Label lblProducto;
    @FXML
    private Label lblSubTotal;
    @FXML
    private Label lblDescuento;
    @FXML
    private Label lblTotal;

    private FxVentaEstructuraNuevoController ventaEstructuraNuevoController;

    private BbItemProducto bbItemProducto;

    private int index;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
    }

    public void loadData(int index, BbItemProducto bbItemProducto) {
        this.index = index;
        this.bbItemProducto = bbItemProducto;
        lblProducto.setText(bbItemProducto.getSuministroTB().getNombreMarca());
        txtPrecio.setText(Tools.roundingValue(bbItemProducto.getSuministroTB().getPrecioVentaGeneralUnico(), 2));
        txtCantidad.setText(Tools.roundingValue(bbItemProducto.getSuministroTB().getCantidad(), 2));
        txtDescuento.setText(Tools.roundingValue(bbItemProducto.getSuministroTB().getDescuento(), 2));

        lblSubTotal.setText(Tools.roundingValue(bbItemProducto.getSuministroTB().getSubImporte(), 2));
        lblDescuento.setText(Tools.roundingValue(bbItemProducto.getSuministroTB().getDescuentoSumado(), 2));
        lblTotal.setText(Tools.roundingValue(bbItemProducto.getSuministroTB().getTotalImporte(), 2));
    }

    @FXML
    private void onKeyReleasedPrecio(KeyEvent event) {
        if (Tools.isNumeric(txtPrecio.getText()) && Tools.isNumeric(txtCantidad.getText()) && Tools.isNumeric(txtDescuento.getText())) {
            double importe = Tools.isNumeric(txtPrecio.getText()) ? Double.parseDouble(txtPrecio.getText()) : 0;

            double porcentajeRestante = importe * (Double.parseDouble(txtDescuento.getText()) / 100.00);
            double preciocalculado = importe - porcentajeRestante;

            bbItemProducto.getSuministroTB().setDescuentoCalculado(porcentajeRestante);
            bbItemProducto.getSuministroTB().setDescuentoSumado(porcentajeRestante * Double.parseDouble(txtCantidad.getText()));

            bbItemProducto.getSuministroTB().setPrecioVentaGeneralUnico(importe);
            bbItemProducto.getSuministroTB().setPrecioVentaGeneralReal(preciocalculado);

            double impuesto = Tools.calculateTax(bbItemProducto.getSuministroTB().getImpuestoValor(), bbItemProducto.getSuministroTB().getPrecioVentaGeneralReal());

            bbItemProducto.getSuministroTB().setImpuestoSumado(Double.parseDouble(txtCantidad.getText()) * impuesto);
            bbItemProducto.getSuministroTB().setPrecioVentaGeneral(bbItemProducto.getSuministroTB().getPrecioVentaGeneralReal() + impuesto);

            bbItemProducto.getSuministroTB().setSubImporte(bbItemProducto.getSuministroTB().getPrecioVentaGeneralUnico() * Double.parseDouble(txtCantidad.getText()));
            bbItemProducto.getSuministroTB().setSubImporteDescuento(Double.parseDouble(txtCantidad.getText()) * bbItemProducto.getSuministroTB().getPrecioVentaGeneralReal());
            bbItemProducto.getSuministroTB().setTotalImporte(Double.parseDouble(txtCantidad.getText()) * bbItemProducto.getSuministroTB().getPrecioVentaGeneralReal());

            lblSubTotal.setText(Tools.roundingValue(bbItemProducto.getSuministroTB().getSubImporte(), 2));
            lblDescuento.setText(Tools.roundingValue(bbItemProducto.getSuministroTB().getDescuentoSumado(), 2));
            lblTotal.setText(Tools.roundingValue(bbItemProducto.getSuministroTB().getSubImporteDescuento(), 2));
        }
    }

    @FXML
    private void onKeyReleasedCantidad(KeyEvent event) {
        if (Tools.isNumeric(txtPrecio.getText()) && Tools.isNumeric(txtCantidad.getText()) && Tools.isNumeric(txtDescuento.getText())) {
            double cantidad = Double.parseDouble(txtCantidad.getText());
            bbItemProducto.getSuministroTB().setCantidad(cantidad);
            double porcentajeRestante = bbItemProducto.getSuministroTB().getPrecioVentaGeneralUnico() * (Double.parseDouble(txtDescuento.getText()) / 100.00);

            bbItemProducto.getSuministroTB().setDescuentoCalculado(porcentajeRestante);
            bbItemProducto.getSuministroTB().setDescuentoSumado(porcentajeRestante * Double.parseDouble(txtCantidad.getText()));

            double impuesto = Tools.calculateTax(bbItemProducto.getSuministroTB().getImpuestoValor(), Double.parseDouble(txtPrecio.getText()));
            bbItemProducto.getSuministroTB().setImpuestoSumado(Double.parseDouble(txtCantidad.getText()) * impuesto);

            bbItemProducto.getSuministroTB().setSubImporte(bbItemProducto.getSuministroTB().getPrecioVentaGeneralUnico() * Double.parseDouble(txtCantidad.getText()));
            bbItemProducto.getSuministroTB().setSubImporteDescuento(Double.parseDouble(txtCantidad.getText()) * Double.parseDouble(txtPrecio.getText()));
            bbItemProducto.getSuministroTB().setTotalImporte(Double.parseDouble(txtCantidad.getText()) * Double.parseDouble(txtPrecio.getText()));

            lblSubTotal.setText(Tools.roundingValue(bbItemProducto.getSuministroTB().getSubImporte(), 2));
            lblDescuento.setText(Tools.roundingValue(bbItemProducto.getSuministroTB().getDescuentoSumado(), 2));
            lblTotal.setText(Tools.roundingValue(bbItemProducto.getSuministroTB().getSubImporteDescuento(), 2));
        }
    }

    @FXML
    private void onKeyReleasedDescuento(KeyEvent event) {
        if (Tools.isNumeric(txtPrecio.getText()) && Tools.isNumeric(txtCantidad.getText()) && Tools.isNumeric(txtDescuento.getText())) {
            double precio = Double.parseDouble(txtPrecio.getText());

            double descuento = Double.parseDouble(txtDescuento.getText());
            double porcentajeRestante = precio * (descuento / 100.00);
            double preciocalculado = precio - porcentajeRestante;

            bbItemProducto.getSuministroTB().setDescuento(descuento);
            bbItemProducto.getSuministroTB().setDescuentoCalculado(porcentajeRestante);
            bbItemProducto.getSuministroTB().setDescuentoSumado(porcentajeRestante * Double.parseDouble(txtCantidad.getText()));

            bbItemProducto.getSuministroTB().setPrecioVentaGeneralUnico(precio);
            bbItemProducto.getSuministroTB().setPrecioVentaGeneralReal(preciocalculado);

            double impuesto = Tools.calculateTax(bbItemProducto.getSuministroTB().getImpuestoValor(), bbItemProducto.getSuministroTB().getPrecioVentaGeneralReal());

            bbItemProducto.getSuministroTB().setImpuestoSumado(Double.parseDouble(txtCantidad.getText()) * impuesto);
            bbItemProducto.getSuministroTB().setPrecioVentaGeneral(bbItemProducto.getSuministroTB().getPrecioVentaGeneralReal() + impuesto);

            bbItemProducto.getSuministroTB().setSubImporte(bbItemProducto.getSuministroTB().getPrecioVentaGeneralUnico() * Double.parseDouble(txtCantidad.getText()));
            bbItemProducto.getSuministroTB().setSubImporteDescuento(Double.parseDouble(txtCantidad.getText()) * bbItemProducto.getSuministroTB().getPrecioVentaGeneralReal());
            bbItemProducto.getSuministroTB().setTotalImporte(Double.parseDouble(txtCantidad.getText()) * bbItemProducto.getSuministroTB().getPrecioVentaGeneralReal());

            lblSubTotal.setText(Tools.roundingValue(bbItemProducto.getSuministroTB().getSubImporte(), 2));
            lblDescuento.setText(Tools.roundingValue(bbItemProducto.getSuministroTB().getDescuentoSumado(), 2));
            lblTotal.setText(Tools.roundingValue(bbItemProducto.getSuministroTB().getSubImporteDescuento(), 2));
        }
    }

    @FXML
    private void onKeyTypedPrecio(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtPrecio.getText().contains(".") || c == '-' && txtPrecio.getText().contains("-")) {
            event.consume();
        }
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

    @FXML
    private void onKeyTypedPorcentajeDescuento(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
            event.consume();
        }
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            bbItemProducto.getChildren().clear();
            bbItemProducto.addElementListView();
            ventaEstructuraNuevoController.getLvProductoAgregados().getItems().set(index, bbItemProducto);
            ventaEstructuraNuevoController.calculateTotales();
            Tools.Dispose(apWindow);
        }
    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        bbItemProducto.getChildren().clear();
        bbItemProducto.addElementListView();
        ventaEstructuraNuevoController.getLvProductoAgregados().getItems().set(index, bbItemProducto);
        ventaEstructuraNuevoController.calculateTotales();
        Tools.Dispose(apWindow);
    }

    @FXML
    private void onKeyPressedCancelar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(apWindow);
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        Tools.Dispose(apWindow);
    }

    public void setInitVentaEstructuraNuevoController(FxVentaEstructuraNuevoController ventaEstructuraNuevoController) {
        this.ventaEstructuraNuevoController = ventaEstructuraNuevoController;
    }

}
