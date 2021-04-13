package controller.produccion.insumos;

import controller.produccion.compras.FxComprasInsumosController;
import controller.tools.RadioButtonModel;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.DetalleCompraTB;
import model.ImpuestoADO;
import model.InsumoTB;
import model.SuministroTB;

public class FxInsumosCompraController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private Label lblDescripcion;
    @FXML
    private VBox vbImpuestos;
    @FXML
    private TextField txtCantidad;
    @FXML
    private Label lblMedida;
    @FXML
    private TextField txtCosto;
    @FXML
    private TextField txtDescuento;

    private FxComprasInsumosController comprasInsumosController;

    private InsumoTB insumoTB;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarComboBox();
    }

    public void loadComponents(InsumoTB insumoTB) {
        this.insumoTB = insumoTB;
        lblDescripcion.setText(insumoTB.getClave() + " - " + insumoTB.getNombreMarca());
//        InsumoTB insumoTB = new InsumoTB();
        insumoTB.setClave(insumoTB.getClave());
        insumoTB.setNombreMarca(insumoTB.getNombreMarca());
        txtCosto.setText(String.valueOf(insumoTB.getCosto()));
    }

    public void cargarComboBox() {
        vbImpuestos.getChildren().clear();
        ToggleGroup toggleGroup = new ToggleGroup();
        ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> {
            RadioButtonModel radioButtonModel = new RadioButtonModel(e.getNombre());
            radioButtonModel.setId(e.getIdImpuesto() + "");
            radioButtonModel.setValor(e.getValor());
            radioButtonModel.setToggleGroup(toggleGroup);
            radioButtonModel.setSelected(e.isSistema());

            vbImpuestos.getChildren().add(radioButtonModel);
        });

        if (!vbImpuestos.getChildren().isEmpty()) {
            ((RadioButtonModel) vbImpuestos.getChildren().get(0)).setSelected(true);
        }
    }

    private void addInsumosList() {

        RadioButtonModel radioButtonModel = new RadioButtonModel();
        for (int i = 0; i < vbImpuestos.getChildren().size(); i++) {
            if (((RadioButtonModel) vbImpuestos.getChildren().get(i)).isSelected()) {
                radioButtonModel.setId(((RadioButtonModel) vbImpuestos.getChildren().get(i)).getId());
                radioButtonModel.setText(((RadioButtonModel) vbImpuestos.getChildren().get(i)).getText());
                radioButtonModel.setValor(((RadioButtonModel) vbImpuestos.getChildren().get(i)).getValor());
            }
        }

        if (!Tools.isNumeric(txtCantidad.getText())) {
            Tools.AlertMessageWarning(apWindow, "Compra", "Ingrese un valor numérico en la cantidad");
            txtCantidad.requestFocus();
        } else if (Double.parseDouble(txtCantidad.getText()) <= 0) {
            Tools.AlertMessageWarning(apWindow, "Compra", "La cantidad no puede ser menor o igual a 0");
            txtCantidad.requestFocus();
        } else if (!Tools.isNumeric(txtCosto.getText())) {
            Tools.AlertMessageWarning(apWindow, "Compra", "Ingrese un valor numerico en el costo");
            txtCosto.requestFocus();
        } else if (Double.parseDouble(txtCosto.getText()) <= 0) {
            Tools.AlertMessageWarning(apWindow, "Compra", "El costo no puede ser menor o igual a 0");
            txtCosto.requestFocus();
        } else {
            DetalleCompraTB detalleCompraTB = new DetalleCompraTB();
            detalleCompraTB.setIdArticulo(insumoTB.getIdInsumo());            
            detalleCompraTB.setDescripcion(insumoTB.getClave() + "\n" + insumoTB.getNombreMarca());
            detalleCompraTB.setCantidad(Double.parseDouble(txtCantidad.getText()));
            detalleCompraTB.setPrecioCompra(Double.parseDouble(txtCosto.getText()));
            detalleCompraTB.setIdImpuesto(Integer.parseInt(radioButtonModel.getId()));
            detalleCompraTB.setNombreImpuesto(radioButtonModel.getText());
            detalleCompraTB.setValorImpuesto(radioButtonModel.getValor());

            double valor_sin_impuesto = Double.parseDouble(txtCosto.getText()) / ((detalleCompraTB.getValorImpuesto() / 100.00) + 1);
            double descuento = !Tools.isNumeric(txtDescuento.getText()) ? 0 : Double.parseDouble(txtDescuento.getText());
            double porcentajeRestante = valor_sin_impuesto * (descuento / 100.00);
            double preciocalculado = valor_sin_impuesto - porcentajeRestante;

            detalleCompraTB.setDescuento(descuento);
            detalleCompraTB.setDescuentoSumado(descuento * detalleCompraTB.getCantidad());

            detalleCompraTB.setPrecioCompraUnico(valor_sin_impuesto);
            detalleCompraTB.setPrecioCompraReal(preciocalculado);

            double impuesto = Tools.calculateTax(detalleCompraTB.getValorImpuesto(), detalleCompraTB.getPrecioCompraReal());
            detalleCompraTB.setImpuestoSumado(detalleCompraTB.getCantidad() * impuesto);
            detalleCompraTB.setPrecioCompra(detalleCompraTB.getPrecioCompraReal() + impuesto);

            detalleCompraTB.setImporte(detalleCompraTB.getPrecioCompra() * detalleCompraTB.getCantidad());

            Button btnRemove = new Button();
            btnRemove.setId(detalleCompraTB.getIdArticulo());
            btnRemove.getStyleClass().add("buttonDark");
            ImageView view = new ImageView(new Image("/view/image/remove.png"));
            view.setFitWidth(22);
            view.setFitHeight(22);
            btnRemove.setGraphic(view);
            detalleCompraTB.setRemove(btnRemove);

            comprasInsumosController.getTvList().getItems().add(detalleCompraTB);
            comprasInsumosController.calculateTotals();
            Tools.Dispose(apWindow);

        }
    }

    @FXML
    private void onActionAdd(ActionEvent event) {
        addInsumosList();
    }

    @FXML
    private void onKeyPressedAdd(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            addInsumosList();
        }
    }

    @FXML
    private void onActionCancel(ActionEvent event) {
        Tools.Dispose(apWindow);
    }

    @FXML
    private void onKeyPressedCancel(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(apWindow);
        }
    }

    public void setInitComprasInsumosController(FxComprasInsumosController comprasInsumosController) {
        this.comprasInsumosController = comprasInsumosController;
    }

}
