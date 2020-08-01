package controller.inventario.suministros;

import controller.operaciones.compras.FxComprasController;
import controller.consultas.compras.FxComprasEditarController;
import controller.inventario.lote.FxLoteProcesoController;
import controller.tools.FilesRouters;
import controller.tools.RadioButtonModel;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.DetalleCompraTB;
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.LoteTB;
import model.PreciosADO;
import model.PreciosTB;
import model.SuministroADO;
import model.SuministroTB;

public class FxSuministrosCompraController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private Text lblDescripcion;
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
    @FXML
    private TextField txtCostoCalculado;
    @FXML
    private ComboBox<ImpuestoTB> cbImpuesto;
    @FXML
    private TextField txtPrecioNeto1;
    @FXML
    private TextField txtPrecioNeto2;
    @FXML
    private TextField txtPrecioNeto3;
    @FXML
    private TextField txtPrecio1;
    @FXML
    private TextField txtPrecio2;
    @FXML
    private TextField txtPrecio3;

    private String clave;

    private String descripcion;

    private FxComprasController comprasController;

    private FxComprasEditarController comprasEditarController;

    private boolean editarSuministros;

    private String idSuminisitro;

    private boolean loteSuministro;

    private int indexcompra;

    private ObservableList<LoteTB> loteTBs;
    
    private ArrayList<PreciosTB> tvPreciosNormal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
        editarSuministros = false;
        loteSuministro = false;
        idSuminisitro = "";
        indexcompra = 0;
         tvPreciosNormal = new ArrayList();
        cargarComboBox();
    }

    public void cargarComboBox() {
        vbImpuestos.getChildren().clear();
        ToggleGroup toggleGroup = new ToggleGroup();
        ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> {
            RadioButtonModel radioButtonModel = new RadioButtonModel(e.getNombreImpuesto());
            radioButtonModel.setId(e.getIdImpuesto() + "");
            radioButtonModel.setValor(e.getValor());
            radioButtonModel.setToggleGroup(toggleGroup);
            radioButtonModel.setSelected(e.isSistema());
            radioButtonModel.setOnAction(event -> {
                onActionImpuesto();
            });
            vbImpuestos.getChildren().add(radioButtonModel);
        });

        if (!vbImpuestos.getChildren().isEmpty()) {
            ((RadioButtonModel) vbImpuestos.getChildren().get(0)).setSelected(true);
        }

        cbImpuesto.getItems().clear();
        ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> {
            cbImpuesto.getItems().add(new ImpuestoTB(e.getIdImpuesto(), e.getNombreImpuesto(), e.getValor(), e.getPredeterminado()));
        });

    }

    public void setLoadData(String idSuministro, boolean lote) {
        this.idSuminisitro = idSuministro;
        SuministroTB suministroTB = SuministroADO.GetSuministroById(idSuministro);
        this.clave = suministroTB.getClave();
        descripcion = suministroTB.getNombreMarca();
        lblDescripcion.setText(clave + " - " + descripcion);
        txtCosto.setText(Tools.roundingValue(suministroTB.getCostoCompra(), 8));
        lblMedida.setText(suministroTB.getUnidadCompraName());
        onActionImpuesto();
        loteSuministro = lote;
        for (ImpuestoTB impuestoTB : cbImpuesto.getItems()) {
            if (suministroTB.getImpuestoArticulo() == impuestoTB.getIdImpuesto()) {
                cbImpuesto.getSelectionModel().select(impuestoTB);
                break;
            }
        }
        if (suministroTB.isTipoPrecio()) {
            txtPrecio1.setText(Tools.roundingValue(suministroTB.getPrecioVentaGeneral(), 8));
            calculateForPrecio(txtPrecio1, txtPrecioNeto1);
            ObservableList<PreciosTB> preciosTBs = PreciosADO.Get_Lista_Precios_By_IdSuministro(idSuministro);
            if (!preciosTBs.isEmpty()) {
                if (((PreciosTB) preciosTBs.get(0)) != null) {
                    txtPrecio2.setId("" + ((PreciosTB) preciosTBs.get(0)).getIdPrecios());
                    txtPrecio2.setText(Tools.roundingValue(((PreciosTB) preciosTBs.get(0)).getValor(), 8));
                    calculateForPrecio(txtPrecio2, txtPrecioNeto2);
                }
                if (((PreciosTB) preciosTBs.get(1)) != null) {
                    txtPrecio3.setId("" + ((PreciosTB) preciosTBs.get(1)).getIdPrecios());
                    txtPrecio3.setText(Tools.roundingValue(((PreciosTB) preciosTBs.get(1)).getValor(), 8));
                    calculateForPrecio(txtPrecio3, txtPrecioNeto3);
                }
            }
        }
    }

    public void setLoadEdit(DetalleCompraTB detalleCompraTB, int index, ObservableList<LoteTB> loteTBs) {
        idSuminisitro = detalleCompraTB.getIdArticulo();
        clave = detalleCompraTB.getSuministroTB().getClave();
        descripcion = detalleCompraTB.getSuministroTB().getNombreMarca();
        lblDescripcion.setText(detalleCompraTB.getSuministroTB().getClave() + " - " + detalleCompraTB.getSuministroTB().getNombreMarca());
        txtCantidad.setText("" + detalleCompraTB.getCantidad());
        txtDescuento.setText(Tools.roundingValue(detalleCompraTB.getDescuento(), 0));
        txtCosto.setText(Tools.roundingValue(detalleCompraTB.getPrecioCompra(), 8));

        int impuesto = detalleCompraTB.getIdImpuesto();
        if (impuesto != 0) {
            if (!vbImpuestos.getChildren().isEmpty()) {
                for (int i = 0; i < vbImpuestos.getChildren().size(); i++) {
                    if (Integer.parseInt(((RadioButtonModel) vbImpuestos.getChildren().get(i)).getId()) == impuesto) {
                        ((RadioButtonModel) vbImpuestos.getChildren().get(i)).setSelected(true);
                        break;
                    }
                }
            }
        } else {
            if (!vbImpuestos.getChildren().isEmpty()) {
                ((RadioButtonModel) vbImpuestos.getChildren().get(0)).setSelected(true);
            }
        }
        onActionImpuesto();

        editarSuministros = true;
        indexcompra = index;
        this.loteTBs = loteTBs;

        for (ImpuestoTB impuestoTB : cbImpuesto.getItems()) {
            if (detalleCompraTB.getSuministroTB().getImpuestoArticulo() == impuestoTB.getIdImpuesto()) {
                cbImpuesto.getSelectionModel().select(impuestoTB);
                break;
            }
        }

        if (detalleCompraTB.getSuministroTB().isTipoPrecio()) {
            txtPrecio1.setText(Tools.roundingValue(detalleCompraTB.getSuministroTB().getPrecioVentaGeneral(), 8));
            calculateForPrecio(txtPrecio1, txtPrecioNeto1);
        }

    }

    private boolean validateStock(TableView<DetalleCompraTB> view, String clave) {
        boolean ret = false;
        for (int i = 0; i < view.getItems().size(); i++) {
            if (view.getItems().get(i).getSuministroTB().getClave().equals(clave)) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public void addSuministrosList() throws IOException {
        int validate = 0;
        RadioButtonModel radioButtonModel = new RadioButtonModel();
        for (int i = 0; i < vbImpuestos.getChildren().size(); i++) {
            if (((RadioButtonModel) vbImpuestos.getChildren().get(i)).isSelected()) {
                radioButtonModel.setId(((RadioButtonModel) vbImpuestos.getChildren().get(i)).getId());
                radioButtonModel.setText(((RadioButtonModel) vbImpuestos.getChildren().get(i)).getText());
                radioButtonModel.setValor(((RadioButtonModel) vbImpuestos.getChildren().get(i)).getValor());
                validate++;
            }
        }

        if (!Tools.isNumeric(txtCantidad.getText())) {
            Tools.AlertMessageWarning(apWindow, "Compra", "Ingrese un valor numérico en la cantidad");
            txtCantidad.requestFocus();
        } else if (Double.parseDouble(txtCantidad.getText()) <= 0) {
            Tools.AlertMessageWarning(apWindow, "Compra", "La cantidad no puede ser menor o igual a 0");
            txtCantidad.requestFocus();
        } else if (validate <= 0) {
            Tools.AlertMessageWarning(apWindow, "Compra", "Seleccione un impuesto.");
        } else if (!Tools.isNumeric(txtCosto.getText())) {
            Tools.AlertMessageWarning(apWindow, "Compra", "Ingrese un valor numerico en el costo");
            txtCosto.requestFocus();
        } else if (Double.parseDouble(txtCosto.getText()) <= 0) {
            Tools.AlertMessageWarning(apWindow, "Compra", "El costo no puede ser menor o igual a 0");
            txtCosto.requestFocus();
        } else if (cbImpuesto.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(apWindow, "Compra", "Seleccione el impuesto para el precio de venta");
            cbImpuesto.requestFocus();
        } else if (!Tools.isNumeric(txtPrecio1.getText().trim())) {
            Tools.AlertMessageWarning(apWindow, "Compra", "Ingrese un valor numérico en la cantidad");
            txtPrecio1.requestFocus();
        } else if (Double.parseDouble(txtPrecio1.getText()) <= 0) {
            Tools.AlertMessageWarning(apWindow, "Compra", "El precio no puede ser menor o igual a 0");
            txtPrecio1.requestFocus();
        } else {
            addSuministros(Integer.parseInt(radioButtonModel.getId()), radioButtonModel.getText(), radioButtonModel.getValor());
        }
    }

    private void addSuministros(int idImpuesto, String nombreImpuesto, double valorImpuesto) {
        DetalleCompraTB detalleCompraTB = new DetalleCompraTB();
        detalleCompraTB.setId(editarSuministros ? indexcompra + 1 : comprasController.getTvList().getItems().size() + 1);
        detalleCompraTB.setIdArticulo(idSuminisitro);
        //
        SuministroTB suministrosTB = new SuministroTB();
        suministrosTB.setClave(clave);
        suministrosTB.setNombreMarca(descripcion);
        suministrosTB.setPrecioVentaGeneral(Tools.isNumeric(txtPrecio1.getText().trim()) ? Double.parseDouble(txtPrecio1.getText().trim()) : 0);

        double porcentaje = ((Tools.isNumeric(txtPrecio1.getText().trim()) ? Double.parseDouble(txtPrecio1.getText().trim()) : 0) * 100.00) / Double.parseDouble(txtCosto.getText());
        int recalculado = (int) Math.abs(100 - Double.parseDouble(Tools.roundingValue(Double.parseDouble(Tools.roundingValue(porcentaje, 8)), 0)));

        suministrosTB.setPrecioMargenGeneral((short) recalculado);
        suministrosTB.setPrecioUtilidadGeneral(Tools.isNumeric(txtPrecio1.getText().trim()) ? (Double.parseDouble(txtPrecio1.getText().trim()) - Double.parseDouble(txtCosto.getText())) : 0);
        suministrosTB.setImpuestoArticulo(cbImpuesto.getSelectionModel().getSelectedItem().getIdImpuesto());
        suministrosTB.setTipoPrecio(true);
        detalleCompraTB.setSuministroTB(suministrosTB);
        //        
        detalleCompraTB.setCantidad(Double.parseDouble(txtCantidad.getText()));
        detalleCompraTB.setPrecioCompra(Double.parseDouble(txtCosto.getText()));
        detalleCompraTB.setDescuento(!Tools.isNumeric(txtDescuento.getText()) ? 0 : Double.parseDouble(txtDescuento.getText()));
        detalleCompraTB.setIdImpuesto(idImpuesto);
        detalleCompraTB.setNombreImpuesto(nombreImpuesto);
        detalleCompraTB.setValorImpuesto(valorImpuesto);
        detalleCompraTB.setDescripcion("");
        double totalDescuento = detalleCompraTB.getPrecioCompra() * (detalleCompraTB.getDescuento() / 100.00);
        double nuevoPrecioCompra = detalleCompraTB.getPrecioCompra() - totalDescuento;
        double totalImpuesto = Tools.calculateTax(detalleCompraTB.getValorImpuesto(), nuevoPrecioCompra);
        detalleCompraTB.setPrecioCompraCalculado((totalDescuento == 0 ? 0 : -1 * totalDescuento));
        detalleCompraTB.setImpuestoSumado(detalleCompraTB.getCantidad() * totalImpuesto);
        detalleCompraTB.setImporte(detalleCompraTB.getCantidad() * (nuevoPrecioCompra + totalImpuesto));
        detalleCompraTB.setLote(loteSuministro);
        Button btnRemove = new Button();
        btnRemove.setId(detalleCompraTB.getIdArticulo());
        btnRemove.getStyleClass().add("buttonDark");
        ImageView view = new ImageView(new Image("/view/image/remove.png"));
        view.setFitWidth(24);
        view.setFitHeight(24);
        btnRemove.setGraphic(view);
        detalleCompraTB.setRemove(btnRemove);

        if (comprasController != null) {
            if (!validateStock(comprasController.getTvList(), detalleCompraTB.getSuministroTB().getClave()) && !editarSuministros) {
                if (loteSuministro) {
                    openWindowLote(suministrosTB);
                } else {
                    comprasController.addSuministroToTable(detalleCompraTB);
                    comprasController.calculateTotals();
                    Tools.Dispose(apWindow);
                }
            } else if (editarSuministros) {
                if (loteSuministro) {
                    openWindowLote(suministrosTB);
                } else {
                    comprasController.editSuministroToTable(indexcompra, detalleCompraTB);
                    comprasController.calculateTotals();
                    Tools.Dispose(apWindow);
                }
            } else {
                Tools.AlertMessageWarning(apWindow, "Compra", "Ya hay un producto con las mismas características.");
            }
        } else if (comprasEditarController != null) {
//            if (!validateStock(comprasEditarController.getTvList(), suministrosTB) && !editarSuministros) {
//                if (validarlote && cantidadinicial != Double.parseDouble(txtCantidad.getText())) {
//                    openWindowLote(suministrosTB);
//                } else {
//                    comprasEditarController.getTvList().getItems().add(suministrosTB);
//                    comprasEditarController.calculateTotals();
//                    Tools.Dispose(apWindow);
//                }
//            } else if (editarSuministros) {
//                if (validarlote && cantidadinicial != Double.parseDouble(txtCantidad.getText())) {
//                    openWindowLote(suministrosTB);
//                } else {
//                    comprasEditarController.getTvList().getItems().set(indexcompra, suministrosTB);
//                    comprasEditarController.calculateTotals();
//                    Tools.Dispose(apWindow);
//                }
//            } else {
//                Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Compra", "Ya hay un producto con las mismas características.", false);
//            }
        }

    }

    private void onActionImpuesto() {
        if (Tools.isNumeric(txtCosto.getText())) {
            if (!vbImpuestos.getChildren().isEmpty()) {
                double costo = Double.parseDouble(txtCosto.getText());

                double totalImpuesto = 0;
                for (int i = 0; i < vbImpuestos.getChildren().size(); i++) {
                    totalImpuesto += Tools.calculateTax(((RadioButtonModel) vbImpuestos.getChildren().get(i)).isSelected()
                            ? ((RadioButtonModel) vbImpuestos.getChildren().get(i)).getValor()
                            : 0, costo);
                }

                double precioimpuesto = (costo + totalImpuesto);
                txtCostoCalculado.setText(Tools.roundingValue(precioimpuesto, 8));
            }
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
//            if (!loteedit) {
//                controller.setLoadData(suministroTB.getIdSuministro(), suministroTB.getClave(),
//                        suministroTB.getNombreMarca(),
//                        String.valueOf(suministroTB.getCantidad()));
//            } else {
//                controller.setEditData(new String[]{suministroTB.getIdSuministro(), suministroTB.getClave(),
//                    suministroTB.getNombreMarca(),
//                    String.valueOf(suministroTB.getCantidad())},
//                        loteTBs);
//            }
        } catch (IOException ex) {
            System.out.println("Suministros controller" + ex.getLocalizedMessage());

        }

    }

    private void calculateForPrecio(TextField txtPrecio, TextField txtPrecioCalculado) {
        if (cbImpuesto.getSelectionModel().getSelectedIndex() >= 0 && Tools.isNumeric(txtPrecio.getText())) {
            double precio = Double.parseDouble(txtPrecio.getText());
            double impuesto = Tools.calculateTax(
                    cbImpuesto.getSelectionModel().getSelectedIndex() >= 0
                    ? cbImpuesto.getSelectionModel().getSelectedItem().getValor()
                    : 0,
                    precio);

            double precioimpuesto = (precio + impuesto);
            txtPrecioCalculado.setText(Tools.roundingValue(precioimpuesto, 8));
        }
    }

    @FXML
    private void onActionImpuestoPrecio(ActionEvent event) {
        calculateForPrecio(txtPrecio1, txtPrecioNeto1);
        calculateForPrecio(txtPrecio2, txtPrecioNeto2);
        calculateForPrecio(txtPrecio3, txtPrecioNeto3);
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
    private void onKeyRealesedPrecio1(KeyEvent event) {
        if (Tools.isNumeric(txtPrecio1.getText())) {
            double precio = Double.parseDouble(txtPrecio1.getText());
            double impuesto = Tools.calculateTax(
                    cbImpuesto.getSelectionModel().getSelectedIndex() >= 0
                    ? cbImpuesto.getSelectionModel().getSelectedItem().getValor()
                    : 0,
                    precio);

            double precioimpuesto = (precio + impuesto);
            txtPrecioNeto1.setText(Tools.roundingValue(precioimpuesto, 8));
        }
    }

    @FXML
    private void onKeyRealesedPrecio2(KeyEvent event) {
        if (Tools.isNumeric(txtPrecio2.getText())) {
            double precio = Double.parseDouble(txtPrecio2.getText());
            double impuesto = Tools.calculateTax(
                    cbImpuesto.getSelectionModel().getSelectedIndex() >= 0
                    ? cbImpuesto.getSelectionModel().getSelectedItem().getValor()
                    : 0,
                    precio);

            double precioimpuesto = (precio + impuesto);
            txtPrecioNeto2.setText(Tools.roundingValue(precioimpuesto, 8));
        }
    }

    @FXML
    private void onKeyRealesedPrecio3(KeyEvent event) {
        if (Tools.isNumeric(txtPrecio3.getText())) {
            double precio = Double.parseDouble(txtPrecio3.getText());
            double impuesto = Tools.calculateTax(
                    cbImpuesto.getSelectionModel().getSelectedIndex() >= 0
                    ? cbImpuesto.getSelectionModel().getSelectedItem().getValor()
                    : 0,
                    precio);

            double precioimpuesto = (precio + impuesto);
            txtPrecioNeto3.setText(Tools.roundingValue(precioimpuesto, 8));
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
    private void onKeyTypedPrecio1(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtPrecio1.getText().contains(".") || c == '-' && txtPrecio1.getText().contains("-")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedPrecio2(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtPrecio2.getText().contains(".") || c == '-' && txtPrecio2.getText().contains("-")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedPrecio3(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtPrecio3.getText().contains(".") || c == '-' && txtPrecio3.getText().contains("-")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyReleasedCosto(KeyEvent event) {
        onActionImpuesto();
    }

    public void setValidarlote(boolean loteSuministro) {
        this.loteSuministro = loteSuministro;
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
