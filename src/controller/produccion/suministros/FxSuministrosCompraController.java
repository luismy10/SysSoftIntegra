package controller.produccion.suministros;

import controller.egresos.compras.FxComprasController;
import controller.egresos.compras.FxComprasEditarController;
import controller.inventario.lote.FxLoteProcesoController;
import controller.tools.CheckBoxModel;
import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.LoteTB;
import model.SuministroTB;

public class FxSuministrosCompraController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TextField txtCosto;
    @FXML
    private TextField txtCostoPromedio;
    @FXML
    private Text lblClave;
    @FXML
    private Text lblDescripcion;
    @FXML
    private TextField txtDescuento;
    @FXML
    private ComboBox<ImpuestoTB> cbImpuesto;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtMargen;
    @FXML
    private TextField txtUtilidad;
    @FXML
    private TextField txtObservacion;
    @FXML
    private TextField txtPrecioNeto;
    @FXML
    private VBox vbImpuestos;
    @FXML
    private Label lblMedida;

    private FxComprasController comprasController;

    private FxComprasEditarController comprasEditarController;

    private boolean editarSuministros;

    private String idSuminisitro;

    private boolean lote;

    private boolean validarlote;

    private boolean loteedit;

    private int indexcompra;

    private ObservableList<LoteTB> loteTBs;

    private double cantidadinicial;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
        editarSuministros = false;
        lote = loteedit = false;
        validarlote = false;
        txtMargen.setText("30");
        idSuminisitro = "";
        indexcompra = 0;
        cantidadinicial = 0;
        cargarComboBox();
    }

    public void cargarComboBox() {
        cbImpuesto.getItems().clear();
        ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> {
            this.cbImpuesto.getItems().add(new ImpuestoTB(e.getIdImpuesto(), e.getNombre(), e.getValor(), e.getPredeterminado()));
        });
        vbImpuestos.getChildren().clear();
        ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> {
            CheckBoxModel checkBox = new CheckBoxModel(e.getNombre());
            checkBox.setValor(e.getValor());
            checkBox.getStyleClass().add("check-box-contenido");
            vbImpuestos.getChildren().add(checkBox);
        });
    }

    public void setLoadData(String value[], String medida, boolean lote) {
        idSuminisitro = value[0];
        lblClave.setText(value[1]);
        lblDescripcion.setText(value[2]);
        txtCosto.setText("" + value[3]);
        txtCostoPromedio.setText("" + value[3]);
        txtPrecio.setText("" + value[4]);
        txtMargen.setText("" + value[5]);
        txtUtilidad.setText("" + value[6]);
        lblMedida.setText(medida);
        int impuesto = Integer.parseInt(value[7]);
        if (impuesto != 0) {
            for (int i = 0; i < cbImpuesto.getItems().size(); i++) {
                if (cbImpuesto.getItems().get(i).getIdImpuesto() == impuesto) {
                    cbImpuesto.getSelectionModel().select(i);
                    break;
                }
            }
        } else {
            cbImpuesto.getSelectionModel().select(0);
        }

        validarlote = lote;
        this.lote = lote;
        calculateForPrecio(txtPrecio, txtCostoPromedio, txtMargen, txtUtilidad, txtPrecioNeto);
    }

    public void setLoadEdit(SuministroTB suministroTB, int index, ObservableList<LoteTB> loteTBs) {
        idSuminisitro = suministroTB.getIdSuministro();
        lblClave.setText(suministroTB.getClave());
        lblDescripcion.setText(suministroTB.getNombreMarca());
        txtCantidad.setText("" + suministroTB.getCantidad());
        txtCostoPromedio.setText(Tools.roundingValue(suministroTB.getCostoCompraReal(), 4));
        txtDescuento.setText(Tools.roundingValue(suministroTB.getDescuento(), 4));

        txtPrecio.setText(Tools.roundingValue(suministroTB.getPrecioVentaGeneral(), 4));
        txtMargen.setText("" + suministroTB.getPrecioMargenGeneral());
        txtUtilidad.setText(Tools.roundingValue(suministroTB.getPrecioUtilidadGeneral(), 4));

        int impuesto = suministroTB.getImpuestoArticulo();
        if (impuesto != 0) {
            for (int i = 0; i < cbImpuesto.getItems().size(); i++) {
                if (cbImpuesto.getItems().get(i).getIdImpuesto() == impuesto) {
                    cbImpuesto.getSelectionModel().select(i);
                    break;
                }
            }
        } else {
            cbImpuesto.getSelectionModel().select(0);
        }
        editarSuministros = true;
        validarlote = suministroTB.isLote();
        lote = suministroTB.isLote();
        indexcompra = index;
        loteedit = true;
        this.loteTBs = loteTBs;
        cantidadinicial = suministroTB.getCantidad();
        txtDescuento.setText(suministroTB.getDescripcion());
        calculateForPrecio(txtPrecio, txtCostoPromedio, txtMargen, txtUtilidad, txtPrecioNeto);
    }

    private void calculateForPrecio(TextField pre, TextField cos, TextField mar, TextField uti, TextField preneto) {
        if (Tools.isNumeric(pre.getText()) && Tools.isNumeric(cos.getText())) {
            if (Double.parseDouble(cos.getText()) <= 0) {
                return;
            }
            double costo = Double.parseDouble(cos.getText());
            double precio = Double.parseDouble(pre.getText());

            double porcentaje = (precio * 100.00) / costo;

            int recalculado = (int) Math.abs(100
                    - Double.parseDouble(
                            Tools.roundingValue(Double.parseDouble(
                                    Tools.roundingValue(porcentaje, 4)), 0)));

            double impuesto = Tools.calculateTax(
                    cbImpuesto.getSelectionModel().getSelectedIndex() >= 0
                    ? cbImpuesto.getSelectionModel().getSelectedItem().getValor()
                    : 0,
                    precio);

            double precioimpuesto = (precio + impuesto);

            mar.setText(String.valueOf(recalculado));
            uti.setText(Tools.roundingValue((precio - costo), 4));
            preneto.setText(Tools.roundingValue(precioimpuesto, 4));
        }
    }

    private void addSuministros(double costo) {
        SuministroTB suministrosTB = new SuministroTB();
        suministrosTB.setIdSuministro(idSuminisitro);
        suministrosTB.setClave(lblClave.getText());
        suministrosTB.setNombreMarca(lblDescripcion.getText());
        suministrosTB.setCantidad(Double.parseDouble(txtCantidad.getText()));

        suministrosTB.setDescuento(!Tools.isNumeric(txtDescuento.getText()) ? 0
                : Double.parseDouble(txtDescuento.getText()));

        double porcentajeDecimal = suministrosTB.getDescuento() / 100.00;
        double porcentajeRestante = costo * porcentajeDecimal;

        suministrosTB.setDescuentoSumado(porcentajeRestante * suministrosTB.getCantidad());

        suministrosTB.setCostoCompra(costo - porcentajeRestante);
        suministrosTB.setCostoCompraReal(costo);

        suministrosTB.setSubImporte(suministrosTB.getCantidad() * suministrosTB.getCostoCompraReal());
        suministrosTB.setTotalImporte(suministrosTB.getCantidad() * suministrosTB.getCostoCompra());

        suministrosTB.setImpuestoArticulo(cbImpuesto.getSelectionModel().getSelectedItem().getIdImpuesto());
        suministrosTB.setImpuestoArticuloName(cbImpuesto.getSelectionModel().getSelectedItem().getNombre());
        suministrosTB.setImpuestoValor(cbImpuesto.getSelectionModel().getSelectedItem().getValor());
        suministrosTB.setImpuestoSumado(suministrosTB.getCantidad() * (suministrosTB.getCostoCompra() * (suministrosTB.getImpuestoValor() / 100.00)));

        suministrosTB.setPrecioVentaGeneral(Double.parseDouble(txtPrecio.getText()));
        suministrosTB.setPrecioMargenGeneral(Short.parseShort(txtMargen.getText()));
        suministrosTB.setPrecioUtilidadGeneral(Double.parseDouble(txtUtilidad.getText()));

        suministrosTB.setLote(lote);
        suministrosTB.setDescripcion(txtObservacion.getText().isEmpty() ? "" : txtObservacion.getText());
        if (comprasController != null) {
            if (!validateStock(comprasController.getTvList(), suministrosTB) && !editarSuministros) {
                if (validarlote && cantidadinicial != Double.parseDouble(txtCantidad.getText())) {
                    openWindowLote(suministrosTB);
                } else {
                    comprasController.getTvList().getItems().add(suministrosTB);
                    comprasController.calculateTotals();
                    Tools.Dispose(apWindow);
                }
            } else if (editarSuministros) {
                if (validarlote && cantidadinicial != Double.parseDouble(txtCantidad.getText())) {
                    openWindowLote(suministrosTB);
                } else {
                    comprasController.getTvList().getItems().set(indexcompra, suministrosTB);
                    comprasController.calculateTotals();
                    Tools.Dispose(apWindow);
                }
            } else {
                Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Compra", "Ya hay un producto con las mismas características.", false);
            }
        } else if (comprasEditarController != null) {
            if (!validateStock(comprasEditarController.getTvList(), suministrosTB) && !editarSuministros) {
                if (validarlote && cantidadinicial != Double.parseDouble(txtCantidad.getText())) {
                    openWindowLote(suministrosTB);
                } else {
                    comprasEditarController.getTvList().getItems().add(suministrosTB);
                    comprasEditarController.calculateTotals();
                    Tools.Dispose(apWindow);
                }
            } else if (editarSuministros) {
                if (validarlote && cantidadinicial != Double.parseDouble(txtCantidad.getText())) {
                    openWindowLote(suministrosTB);
                } else {
                    comprasEditarController.getTvList().getItems().set(indexcompra, suministrosTB);
                    comprasEditarController.calculateTotals();
                    Tools.Dispose(apWindow);
                }
            } else {
                Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Compra", "Ya hay un producto con las mismas características.", false);
            }
        }

    }

    private boolean validateStock(TableView<SuministroTB> view, SuministroTB suministroTB) {
        boolean ret = false;
        for (int i = 0; i < view.getItems().size(); i++) {
            if (view.getItems().get(i).getClave().equals(suministroTB.getClave())) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public void addSuministrosList() throws IOException {
        if (!Tools.isNumeric(txtCantidad.getText())) {
            Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Compra", "Ingrese un valor numerico en la cantidad", false);
            txtCantidad.requestFocus();
        } else if (Double.parseDouble(txtCantidad.getText()) <= 0) {
            Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Compra", "La cantidad no puede ser menor o igual a 0", false);
            txtCantidad.requestFocus();
        } else if (cbImpuesto.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Compra", "Seleccione el impuesto", false);
            cbImpuesto.requestFocus();
        } else if (!Tools.isNumeric(txtCostoPromedio.getText())) {
            Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Compra", "Ingrese un valor numerico en el costo", false);
            txtCostoPromedio.requestFocus();
        } else if (Double.parseDouble(txtCostoPromedio.getText()) <= 0) {
            Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Compra", "El costo no puede ser menor o igual a 0", false);
            txtCostoPromedio.requestFocus();
        } else if (!Tools.isNumeric(txtPrecio.getText())) {
            Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Compra", "Ingrese un valor numerico en el precio 1", false);
            txtPrecio.requestFocus();
        } else if (Double.parseDouble(txtPrecio.getText()) <= 0) {
            Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Compra", "El precio de venta 1 no puede ser menor o igual a 0", false);
            txtPrecio.requestFocus();
        } else {
            addSuministros(Double.parseDouble(txtCostoPromedio.getText()));
        }
    }

    private void openWindowLote(SuministroTB suministroTB) {
        try {
            URL url = getClass().getResource(FilesRouters.FX_LOTE_PROCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxLoteProcesoController controller = fXMLLoader.getController();
            controller.setSuministrosLoteController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Lote", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.show();
            if (!loteedit) {
                controller.setLoadData(suministroTB.getIdSuministro(), suministroTB.getClave(),
                        suministroTB.getNombreMarca(),
                        String.valueOf(suministroTB.getCantidad()));
            } else {
                controller.setEditData(new String[]{suministroTB.getIdSuministro(), suministroTB.getClave(),
                    suministroTB.getNombreMarca(),
                    String.valueOf(suministroTB.getCantidad())},
                        loteTBs);
            }
        } catch (IOException ex) {
            System.out.println("Suministros controller" + ex.getLocalizedMessage());

        }

    }

    @FXML
    private void onActionAdd(ActionEvent event) throws IOException {
        addSuministrosList();
    }

    @FXML
    private void onKeyPressedAdd(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            addSuministrosList();
        }
    }

    private void onKeyPressedPreviousPrices(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

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

    @FXML
    private void onKeyTypedCantidad(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtCantidad.getText().contains(".") || c == '-' && txtCantidad.getText().contains("-")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedCosto(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtCosto.getText().contains(".") || c == '-' && txtCosto.getText().contains("-")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedDescuento(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
            event.consume();
        }
        if (c == '.' && txtDescuento.getText().contains(".")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedMargen(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b')) {
            event.consume();
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
    private void onKeyTypedPrecioNeto(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtPrecioNeto.getText().contains(".") || c == '-' && txtPrecioNeto.getText().contains("-")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyReleasedCosto(KeyEvent event) {
        if (Tools.isNumeric(txtCosto.getText())) {
            if (!vbImpuestos.getChildren().isEmpty()) {
                double totalImpuesto = 0;
                for (int i = 0; i < vbImpuestos.getChildren().size(); i++) {
                    totalImpuesto += ((CheckBoxModel) vbImpuestos.getChildren().get(i)).isSelected()
                            ? (Double.parseDouble(txtCosto.getText()) * ((double) ((CheckBoxModel) vbImpuestos.getChildren().get(i)).getValor() / 100.00))
                            : 0;
                }
                double valorCalculado = Double.parseDouble(txtCosto.getText()) + totalImpuesto;
                txtCostoPromedio.setText(Tools.roundingValue(valorCalculado, 4));

//                if (Tools.isNumeric(txtPrecioNeto.getText())) {
//                    double costo = Double.parseDouble(txtCostoPromedio.getText());
//                    double impuesto = cbImpuesto.getSelectionModel().getSelectedIndex() >= 0 ? cbImpuesto.getSelectionModel().getSelectedItem().getValor() : 0;
//                    double precioNeto = Double.parseDouble(txtPrecioNeto.getText());
//
//                    double precio = Tools.calculateValueBruto(impuesto, precioNeto);
//
//                    double porcentaje = (precio * 100.00) / costo;
//
//                    int recalculado = (int) Math.abs(100
//                            - Double.parseDouble(
//                                    Tools.roundingValue(Double.parseDouble(
//                                            Tools.roundingValue(porcentaje, 2)), 0)));
//
//                    txtPrecio.setText(Tools.roundingValue(precio, 2));
//                    txtMargen.setText(String.valueOf(recalculado));
//                    txtUtilidad.setText(Tools.roundingValue((precio - costo), 2));
//                }
            } else {
                //toma el valor del impuesto del combo box
                double valorCalculado = Double.parseDouble(txtCosto.getText());
                txtCostoPromedio.setText(Tools.roundingValue(valorCalculado, 4));

//                if (Tools.isNumeric(txtPrecioNeto.getText())) {
//                    double costo = Double.parseDouble(txtCostoPromedio.getText());
//                    double impuesto = cbImpuesto.getSelectionModel().getSelectedIndex() >= 0 ? cbImpuesto.getSelectionModel().getSelectedItem().getValor() : 0;
//                    double precioNeto = Double.parseDouble(txtPrecioNeto.getText());
//
//                    double precio = Tools.calculateValueBruto(impuesto, precioNeto);
//
//                    double porcentaje = (precio * 100.00) / costo;
//
//                    int recalculado = (int) Math.abs(100
//                            - Double.parseDouble(
//                                    Tools.roundingValue(Double.parseDouble(
//                                            Tools.roundingValue(porcentaje, 2)), 0)));
//
//                    txtPrecio.setText(Tools.roundingValue(precio, 2));
//                    txtMargen.setText(String.valueOf(recalculado));
//                    txtUtilidad.setText(Tools.roundingValue((precio - costo), 2));
//                }
            }
        }
    }

    @FXML
    private void onActionImpuesto(ActionEvent event) {
        if (Tools.isNumeric(txtCostoPromedio.getText()) && Tools.isNumeric(txtPrecioNeto.getText())) {
            if (Double.parseDouble(txtCostoPromedio.getText()) <= 0) {
                return;
            }
            double impuesto = cbImpuesto.getSelectionModel().getSelectedIndex() >= 0 ? cbImpuesto.getSelectionModel().getSelectedItem().getValor() : 0;
            double costo = Double.parseDouble(txtCostoPromedio.getText());
            double precioNeto = Double.parseDouble(txtPrecioNeto.getText());

            double precio = Tools.calculateValueNeto(impuesto, precioNeto);

            double porcentaje = (precio * 100.00) / costo;

            int recalculado = (int) Math.abs(100 - Double.parseDouble(Tools.roundingValue(Double.parseDouble(Tools.roundingValue(porcentaje, 4)), 0)));

            txtPrecio.setText(Tools.roundingValue(precio, 4));
            txtMargen.setText(String.valueOf(recalculado));
            txtUtilidad.setText(Tools.roundingValue((precio - costo), 4));

        }
    }

    @FXML
    private void onKeyRealesdPrecio(KeyEvent event) {
        if (Tools.isNumeric(txtPrecio.getText()) && Tools.isNumeric(txtCosto.getText())) {
            double costo = Double.parseDouble(txtCosto.getText());
            double precio = Double.parseDouble(txtPrecio.getText());

            double porcentaje = (precio * 100.00) / costo;

            int recalculado = (int) Math.abs(100
                    - Double.parseDouble(
                            Tools.roundingValue(Double.parseDouble(
                                    Tools.roundingValue(porcentaje, 4)), 0)));

            double impuesto = Tools.calculateTax(
                    cbImpuesto.getSelectionModel().getSelectedIndex() >= 0
                    ? cbImpuesto.getSelectionModel().getSelectedItem().getValor()
                    : 0,
                    precio);

            double precioimpuesto = (precio + impuesto);

            txtMargen.setText(String.valueOf(recalculado));
            txtUtilidad.setText(Tools.roundingValue((precio - costo), 4));
            txtPrecioNeto.setText(Tools.roundingValue(precioimpuesto, 4));
        }
    }

    @FXML
    private void onKeyRealesdPrecioNeto(KeyEvent event) {
        if (Tools.isNumeric(txtPrecioNeto.getText()) && Tools.isNumeric(txtCostoPromedio.getText())) {
            if (Double.parseDouble(txtCostoPromedio.getText()) <= 0) {
                return;
            }

            double costo = Double.parseDouble(txtCostoPromedio.getText());
            double impuesto = cbImpuesto.getSelectionModel().getSelectedIndex() >= 0 ? cbImpuesto.getSelectionModel().getSelectedItem().getValor() : 0;
            double precioNeto = Double.parseDouble(txtPrecioNeto.getText());

            double precio = Tools.calculateValueNeto(impuesto, precioNeto);

            double porcentaje = (precio * 100.00) / costo;

            int recalculado = (int) Math.abs(100
                    - Double.parseDouble(
                            Tools.roundingValue(Double.parseDouble(
                                    Tools.roundingValue(porcentaje, 4)), 0)));

            txtPrecio.setText(Tools.roundingValue(precio, 4));
            txtMargen.setText(String.valueOf(recalculado));
            txtUtilidad.setText(Tools.roundingValue((precio - costo), 4));
        }
    }

    @FXML
    private void onKeyReleasedMargen(KeyEvent event) {
        if (Tools.isNumeric(txtMargen.getText()) && Tools.isNumeric(txtCostoPromedio.getText())) {
            //toma el valor del impuesto del combo box
            double costo = Double.parseDouble(txtCostoPromedio.getText());
            int margen = Integer.parseInt(txtMargen.getText());

            double precio = Tools.calculateAumento(margen, costo);

            double impuesto = Tools.calculateTax(
                    cbImpuesto.getSelectionModel().getSelectedIndex() >= 0
                    ? cbImpuesto.getSelectionModel().getSelectedItem().getValor()
                    : 0,
                    precio);
            double precioimpuesto = (precio + impuesto);

            txtPrecio.setText(Tools.roundingValue(precio, 2));
            txtUtilidad.setText(Tools.roundingValue((precio - costo), 4));
            txtPrecioNeto.setText(Tools.roundingValue(precioimpuesto, 4));
        }
    }

    public void setValidarlote(boolean validarlote) {
        this.validarlote = validarlote;
    }

    public void setCantidadInicial(double cantidadinicial) {
        this.cantidadinicial = cantidadinicial;
    }

    public void setInitComprasController(FxComprasController comprasController) {
        this.comprasController = comprasController;
    }

    public void setInitComprasEditarController(FxComprasEditarController comprasEditarController) {
        this.comprasEditarController = comprasEditarController;
    }

    public FxComprasController getComprasController() {
        return comprasController;
    }

}
