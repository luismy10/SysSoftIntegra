package controller.produccion.suministros;

import controller.configuracion.tablasbasicas.FxDetalleListaController;
import controller.inventario.articulo.FxCodigoBarrasController;
import controller.tools.CheckBoxModel;
import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.DetalleADO;
import model.DetalleTB;
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.PreciosTB;
import model.SuministroADO;
import model.SuministroTB;

public class FxSuministrosProcesoModalController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private ComboBox<DetalleTB> cbOrigen;
    @FXML
    private TextField txtClave;
    @FXML
    private TextField txtClaveAlterna;
    @FXML
    private TextField txtNombreMarca;
    @FXML
    private TextField txtNombreGenerico;
    @FXML
    private TextField txtCategoria;
    @FXML
    private TextField txtMarca;
    @FXML
    private TextField txtPresentacion;
    @FXML
    private ComboBox<DetalleTB> cbEstado;
    @FXML
    private ImageView lnPrincipal;
    @FXML
    private Button btnRegister;
    @FXML
    private TextField txtStockMinimo;
    @FXML
    private TextField txtStockMaximo;
    @FXML
    private TextField txtCosto;
    @FXML
    private TextField txtCostoPromedio;
    @FXML
    private CheckBox cbLote;
    @FXML
    private CheckBox cbInventario;
    @FXML
    private RadioButton rbUnidad;
    @FXML
    private RadioButton rbMedida;
    @FXML
    private RadioButton rbGranel;
    @FXML
    private TextField txtMedida;
    @FXML
    private TextField txtPrecioVentaNeto1;
    @FXML
    private TextField txtPrecioVentaNeto2;
    @FXML
    private TextField txtPrecioVentaNeto3;
    @FXML
    private TextField txtPrecio1;
    @FXML
    private TextField txtPrecio2;
    @FXML
    private TextField txtPrecio3;
    @FXML
    private TextField txtMargen1;
    @FXML
    private TextField txtMargen2;
    @FXML
    private TextField txtMargen3;
    @FXML
    private TextField txtUtilidad1;
    @FXML
    private TextField txtUtilidad2;
    @FXML
    private TextField txtUtilidad3;
    @FXML
    private ComboBox<ImpuestoTB> cbImpuesto;
    @FXML
    private VBox vbInventario;
    @FXML
    private RadioButton rbValorUnidad;
    @FXML
//    private RadioButton rbValorCosto;
    private CheckBox cbAceptar;
    @FXML
    private Text lblTitle;
    @FXML
    private TextField txtClaveSat;
    @FXML
    private VBox vbImpuestos;
    @FXML
    private TableView<PreciosTB> tvPrecios;
    @FXML
    private TableColumn<PreciosTB, TextField> tcNombre;
    @FXML
    private TableColumn<PreciosTB, TextField> tcMonto;
    @FXML
    private TableColumn<PreciosTB, TextField> tcFactor;
    @FXML
    private TableColumn<PreciosTB, Button> tcOpcion;
    @FXML
    private RadioButton rbPrecioNormal;
    @FXML
    private RadioButton rbPrecioPersonalizado;
    @FXML
    private VBox vbContenedorPrecioNormal;
    @FXML
    private VBox vbContenedorPreciosPersonalizado;
    @FXML
    private HBox hbPrecioNormal;
    @FXML
    private VBox vbPrecioPersonalizado;
    @FXML
    private TextField txtPrecioVentaNetoPersonalizado;
    @FXML
    private TextField txtPrecioVentaBrutoPersonalizado;
    @FXML
    private TextField txtMargenPersonalizado;
    @FXML
    private TextField txtUtilidadPersonalizado;
    @FXML
    private TabPane tpContenedor;

    private String idSuministro;

    private File selectFile;

    private int idPresentacion;

    private int idCategoria;

    private int idMarca;

    private int idMedida;

//    private boolean articulo;

    private boolean estadoOrigen;

    private ObservableList<PreciosTB> tvPreciosNormal;

    private SingleSelectionModel<Tab> selectionModel = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
        selectionModel = tpContenedor.getSelectionModel();
        ToggleGroup groupVende = new ToggleGroup();
        ToggleGroup groupValor = new ToggleGroup();
        ToggleGroup groupPrecio = new ToggleGroup();
        rbUnidad.setToggleGroup(groupVende);
        rbMedida.setToggleGroup(groupVende);
        rbGranel.setToggleGroup(groupVende);
        rbValorUnidad.setToggleGroup(groupValor);
//        rbValorCosto.setToggleGroup(groupValor);
        rbPrecioNormal.setToggleGroup(groupPrecio);
        rbPrecioPersonalizado.setToggleGroup(groupPrecio);
        tvPreciosNormal = FXCollections.observableArrayList();
        txtPrecio2.setId("0");
        txtPrecio3.setId("0");
        initTablePrecios();
    }

    public void setInitArticulo() {
        clearElements();
        rbPrecioNormal.setSelected(true);
        vbContenedorPrecioNormal.getChildren().add(hbPrecioNormal);
        setIniciarCarga();
        txtClave.requestFocus();
        lblTitle.setText("Registrar datos del Producto");
    }

    public void clearElements() {
        idSuministro = "";
        tvPreciosNormal.clear();
        idMedida = 0;
        idPresentacion = 0;
        idCategoria = 0;
        idMarca = 0;
        selectFile = null;
        lnPrincipal.setImage(new Image("/view/image/no-image.png"));
        txtMargen1.setText("30");
        txtMargen2.setText("25");
        txtMargen3.setText("20");
        lblTitle.setText("Datos del producto");
        btnRegister.setText("Registrar");
        btnRegister.getStyleClass().clear();
        btnRegister.getStyleClass().add("buttonLightDefault");

        cbOrigen.getItems().clear();
        cbOrigen.setDisable(false);
        cbAceptar.setSelected(true);
        cbAceptar.setVisible(true);
        txtClave.clear();
        txtClaveAlterna.clear();
        txtNombreMarca.clear();
        txtMedida.clear();
        txtCategoria.clear();
        rbUnidad.setSelected(true);
        txtStockMinimo.clear();
        txtStockMaximo.clear();
        cbImpuesto.getItems().clear();
        txtPrecio1.clear();
        txtPrecio2.clear();
        txtPrecio3.clear();
        cbInventario.setSelected(false);
        vbInventario.setDisable(true);
        txtCosto.clear();
        txtCostoPromedio.clear();
        txtUtilidad1.clear();
        rbValorUnidad.setSelected(true);
        txtNombreGenerico.clear();
        cbEstado.getItems().clear();
        txtMarca.clear();
        txtPresentacion.clear();
        cbLote.setSelected(false);
        txtClaveSat.clear();
        txtPrecioVentaNetoPersonalizado.clear();
        txtPrecioVentaBrutoPersonalizado.clear();
        txtMargenPersonalizado.clear();
        txtUtilidadPersonalizado.clear();
        tvPrecios.getItems().clear();
        vbContenedorPrecioNormal.getChildren().remove(hbPrecioNormal);
        vbContenedorPreciosPersonalizado.getChildren().remove(vbPrecioPersonalizado);
    }

    private void initTablePrecios() {
        tcNombre.setCellValueFactory(new PropertyValueFactory<>("txtNombre"));
        tcMonto.setCellValueFactory(new PropertyValueFactory<>("txtValor"));
        tcFactor.setCellValueFactory(new PropertyValueFactory<>("txtFactor"));
        tcOpcion.setCellValueFactory(new PropertyValueFactory<>("btnOpcion"));
    }

    private void openAlertMessageWarning(String message) {
        Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Producto", message, false);
    }

    private void addElementsTablePrecios() {
        PreciosTB precios = new PreciosTB();
        precios.setId(tvPrecios.getItems().isEmpty() ? 1 : tvPrecios.getItems().size() + 1);
        precios.setNombre("Precio " + precios.getId());
        precios.setValor(Double.parseDouble("0.00"));

        precios.setFactor(Double.parseDouble("1.00"));

        TextField tfNombre = new TextField("Precio " + precios.getId());
        tfNombre.getStyleClass().add("text-field-normal");
        tfNombre.setOnKeyReleased(event -> {
            precios.setNombre(tfNombre.getText());
        });
        precios.setTxtNombre(tfNombre);

        TextField tfValor = new TextField("0.00");
        tfValor.getStyleClass().add("text-field-normal");
        tfValor.setOnKeyReleased(event -> {
            precios.setValor(!Tools.isNumeric(tfValor.getText()) ? 0 : Double.parseDouble(tfValor.getText()));
        });
        tfValor.setOnKeyTyped(event -> {
            char c = event.getCharacter().charAt(0);
            if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
                event.consume();
            }
            if (c == '.' && tfValor.getText().contains(".")) {
                event.consume();
            }
        });

        precios.setTxtValor(tfValor);

        TextField tfFactor = new TextField("1.00");
        tfFactor.getStyleClass().add("text-field-normal");
        tfFactor.setOnKeyReleased(event -> {
            precios.setFactor(!Tools.isNumeric(tfFactor.getText()) ? 1 : Double.parseDouble(tfFactor.getText()));
        });
        tfFactor.setOnKeyTyped(event -> {
            char c = event.getCharacter().charAt(0);
            if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
                event.consume();
            }
            if (c == '.' && tfFactor.getText().contains(".")) {
                event.consume();
            }
        });
        precios.setTxtFactor(tfFactor);

        Button button = new Button();
        button.getStyleClass().add("buttonLightWarning");
        ImageView view = new ImageView(new Image("/view/image/remove-black.png"));
        view.setFitWidth(24);
        view.setFitHeight(24);
        button.setGraphic(view);
        button.setOnAction(event -> {
            executeEventRomeverPrice(precios);
        });
        button.setOnKeyPressed(event -> {
            executeEventRomeverPrice(precios);
        });
        precios.setBtnOpcion(button);

        precios.setEstado(true);

        tvPrecios.getItems().add(precios);

    }

    private void executeEventRomeverPrice(PreciosTB preciosTB) {
        short confirmation = Tools.AlertMessageConfirmation(apWindow, "Precios", "¿Esta seguro de quitar el precio?");
        if (confirmation == 1) {
            ObservableList<PreciosTB> observableList;
            observableList = tvPrecios.getItems();
            observableList.remove(preciosTB);
            tvPrecios.requestFocus();
        }
    }

    private void loadEventos(ArrayList<Object> objects) {
        cbOrigen.getItems().clear();
        ObservableList<DetalleTB> list = (ObservableList<DetalleTB>) objects.get(0);
        list.forEach(e -> {
            cbOrigen.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        cbOrigen.getSelectionModel().select(0);

        cbImpuesto.getItems().clear();
        List<ImpuestoTB> list1 = (List<ImpuestoTB>) objects.get(1);
        list1.forEach(e -> {
            cbImpuesto.getItems().add(new ImpuestoTB(e.getIdImpuesto(), e.getNombre(), e.getValor(), e.getPredeterminado()));
        });

        vbImpuestos.getChildren().clear();
        list1.forEach(e -> {
            CheckBoxModel checkBox = new CheckBoxModel(e.getNombre());
            checkBox.setValor(e.getValor());
            checkBox.setOnAction(event -> {

            });
            checkBox.getStyleClass().add("check-box-contenido");
            vbImpuestos.getChildren().add(checkBox);
        });

        cbEstado.getItems().clear();
        ObservableList<DetalleTB> list2 = (ObservableList<DetalleTB>) objects.get(2);
        list2.forEach(e -> {
            cbEstado.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        cbEstado.getSelectionModel().select(0);
    }

    public void setIniciarCarga() {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                ArrayList<Object> arrayList = new ArrayList<>();
                arrayList.add(DetalleADO.GetDetailIdName("1", "0016", "SUB PRODUCTO(S)"));
                arrayList.add(ImpuestoADO.GetTipoImpuestoCombBox());
                arrayList.add(DetalleADO.GetDetailIdName("2", "0001", ""));
                return arrayList;
            }
        };

        task.setOnSucceeded(w -> {
            ArrayList<Object> objects = task.getValue();
            loadEventos(objects);
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void aValidityProcess() {
        //primera validacion
        if (cbOrigen.getSelectionModel().getSelectedIndex() < 0) {
            openAlertMessageWarning("Seleccione el origen, por favor.");
            if (!selectionModel.isSelected(0)) {
                selectionModel.select(0);
            }
            cbOrigen.requestFocus();
        } else if (txtClave.getText().isEmpty()) {
            openAlertMessageWarning("Ingrese la clave del producto, por favor.");
            if (!selectionModel.isSelected(0)) {
                selectionModel.select(0);
            }
            txtClave.requestFocus();
        } else if (txtNombreMarca.getText().isEmpty()) {
            openAlertMessageWarning("Ingrese el nombre del producto, por favor.");
            if (!selectionModel.isSelected(0)) {
                selectionModel.select(0);
            }
            txtNombreMarca.requestFocus();
        } else if (txtMedida.getText().isEmpty()) {
            openAlertMessageWarning("Ingrese el nombre de la unidad de medida, por favor.");
            if (!selectionModel.isSelected(0)) {
                selectionModel.select(0);
            }
            txtMedida.requestFocus();
        } else if (idMedida <= 0) {
            openAlertMessageWarning("Ingrese el nombre de la unidad de medida, por favor.");
            if (!selectionModel.isSelected(0)) {
                selectionModel.select(0);
            }
            txtMedida.requestFocus();
        } else if (txtCategoria.getText().isEmpty()) {
            openAlertMessageWarning("Ingrese el nombre de la categoría, por favor.");
            if (!selectionModel.isSelected(0)) {
                selectionModel.select(0);
            }
            txtCategoria.requestFocus();
        } else if (idCategoria <= 0) {
            openAlertMessageWarning("Ingrese el nombre de la categoría, por favor.");
            if (!selectionModel.isSelected(0)) {
                selectionModel.select(0);
            }
            txtCategoria.requestFocus();
        } else if (!estadoOrigen && cbImpuesto.getSelectionModel().getSelectedIndex() < 0) {
            openAlertMessageWarning("Seleccione el impuesto, por favor.");
            if (!selectionModel.isSelected(1)) {
                selectionModel.select(1);
            }
            cbImpuesto.requestFocus();
        }//segunda validacion de lista de precio normal 
        else if (!estadoOrigen && rbPrecioNormal.isSelected() && !Tools.isNumeric(txtPrecio1.getText())) {
            openAlertMessageWarning("Ingrese el primer precio de venta, por favor.");
            if (!selectionModel.isSelected(1)) {
                selectionModel.select(1);
            }
            txtPrecio1.requestFocus();
        } else if (!estadoOrigen && rbPrecioNormal.isSelected() && Double.parseDouble(txtPrecio1.getText()) <= 0) {
            openAlertMessageWarning("El precio de venta no puede ser menor o igual a 0, por favor.");
            if (!selectionModel.isSelected(1)) {
                selectionModel.select(1);
            }
            txtPrecio1.requestFocus();
        }//segunda validación de lista de precios personalizado 
        else if (!estadoOrigen && rbPrecioPersonalizado.isSelected() && !Tools.isNumeric(txtPrecioVentaNetoPersonalizado.getText())) {
            openAlertMessageWarning("Ingrese el precio de venta, por favor.");
            if (!selectionModel.isSelected(1)) {
                selectionModel.select(1);
            }
            txtPrecioVentaNetoPersonalizado.requestFocus();
        } else if (!estadoOrigen && rbPrecioPersonalizado.isSelected() && Double.parseDouble(txtPrecioVentaNetoPersonalizado.getText()) <= 0) {
            openAlertMessageWarning("El precio de venta no puede ser menor o igual a 0, por favor.");
            if (!selectionModel.isSelected(1)) {
                selectionModel.select(1);
            }
            txtPrecioVentaNetoPersonalizado.requestFocus();
        } else if (cbInventario.isSelected()) {
            if (!estadoOrigen && !Tools.isNumeric(txtCostoPromedio.getText())) {
                openAlertMessageWarning("Ingrese el costo del producto, por favor.");
                if (!selectionModel.isSelected(1)) {
                    selectionModel.select(1);
                }
                txtCostoPromedio.requestFocus();
            } else if (!estadoOrigen && Double.parseDouble(txtCostoPromedio.getText()) <= 0) {
                openAlertMessageWarning("El costo del producto no puede ser menor o igual a 0, por favor.");
                if (!selectionModel.isSelected(1)) {
                    selectionModel.select(1);
                }
                txtCostoPromedio.requestFocus();
            } else if (!estadoOrigen && rbPrecioNormal.isSelected() && !Tools.isNumeric(txtMargen1.getText())) {
                openAlertMessageWarning("Ingrese el margen, por favor.");
                if (!selectionModel.isSelected(1)) {
                    selectionModel.select(1);
                }
                txtMargen1.requestFocus();
            } else if (!estadoOrigen && rbPrecioPersonalizado.isSelected() && !Tools.isNumeric(txtMargenPersonalizado.getText())) {
                openAlertMessageWarning("Ingrese el margen, por favor.");
                if (!selectionModel.isSelected(1)) {
                    selectionModel.select(1);
                }
                txtMargenPersonalizado.requestFocus();
            } else if (cbEstado.getSelectionModel().getSelectedIndex() < 0) {
                openAlertMessageWarning("Selecciona el estado del producto, por favor.");
                if (!selectionModel.isSelected(2)) {
                    selectionModel.select(2);
                }
                cbEstado.requestFocus();
            } else {
                crudProducto();
            }
        } else if (cbEstado.getSelectionModel().getSelectedIndex() < 0) {
            openAlertMessageWarning("Selecciona el estado del producto, por favor.");
            if (!selectionModel.isSelected(2)) {
                selectionModel.select(2);
            }
            cbEstado.requestFocus();
        } else {
            crudProducto();
        }
    }

    private void crudProducto() {
        short confirmation = Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Movimiento", "¿Está seguro de continuar?", true);
        if (confirmation == 1) {
            SuministroTB suministroTB = new SuministroTB();
            suministroTB.setIdSuministro(idSuministro);
            suministroTB.setOrigen(cbOrigen.getSelectionModel().getSelectedIndex() >= 0
                    ? cbOrigen.getSelectionModel().getSelectedItem().getIdDetalle().get()
                    : 0);
            suministroTB.setClave(txtClave.getText().trim());
            suministroTB.setClaveAlterna(txtClaveAlterna.getText().trim());
            suministroTB.setNombreMarca(txtNombreMarca.getText().trim());
            suministroTB.setNombreGenerico(txtNombreGenerico.getText().trim());
            suministroTB.setImagenTB(selectFile != null
                    ? "./img/" + selectFile.getName()
                    : "");
            suministroTB.setCategoria(idCategoria != 0
                    ? idCategoria
                    : 0);
            suministroTB.setMarcar(idMarca != 0
                    ? idMarca
                    : 0);
            suministroTB.setUnidadCompra(idMedida != 0
                    ? idMedida
                    : 0);
            suministroTB.setPresentacion(idPresentacion != 0
                    ? idPresentacion
                    : 0);

            suministroTB.setStockMinimo(Tools.isNumeric(txtStockMinimo.getText())
                    ? Double.parseDouble(txtStockMinimo.getText().trim())
                    : 0);

            suministroTB.setStockMaximo(Tools.isNumeric(txtStockMaximo.getText())
                    ? Double.parseDouble(txtStockMaximo.getText().trim())
                    : 0);

            //agregar lista de precios
            suministroTB.setCostoCompra(Tools.isNumeric(txtCostoPromedio.getText())
                    ? Double.parseDouble(txtCostoPromedio.getText())
                    : 0);

            double precioValidado = rbPrecioNormal.isSelected()
                    ? Tools.isNumeric(txtPrecio1.getText()) ? Double.parseDouble(txtPrecio1.getText()) : 0
                    : Tools.isNumeric(txtPrecioVentaNetoPersonalizado.getText()) ? Double.parseDouble(txtPrecioVentaNetoPersonalizado.getText()) : 0;
            suministroTB.setPrecioVentaGeneral(precioValidado);

            short margenValidado = rbPrecioNormal.isSelected()
                    ? Tools.isNumericInteger(txtMargen1.getText()) ? Short.valueOf(txtMargen1.getText()) : 0
                    : Tools.isNumericInteger(txtMargenPersonalizado.getText()) ? Short.valueOf(txtMargenPersonalizado.getText()) : 0;
            suministroTB.setPrecioMargenGeneral(margenValidado);

            double utilidadValidado = rbPrecioNormal.isSelected()
                    ? Tools.isNumeric(txtUtilidad1.getText()) ? Double.parseDouble(txtUtilidad1.getText()) : 0
                    : Tools.isNumeric(txtUtilidadPersonalizado.getText()) ? Double.parseDouble(txtUtilidadPersonalizado.getText()) : 0;
            suministroTB.setPrecioUtilidadGeneral(utilidadValidado);

            suministroTB.setEstado(cbEstado.getSelectionModel().getSelectedIndex() >= 0
                    ? cbEstado.getSelectionModel().getSelectedItem().getIdDetalle().get()
                    : 0);

            int se_vende;

            if (rbUnidad.isSelected()) {
                se_vende = 1;
            } else if (rbGranel.isSelected()) {
                se_vende = 2;
            } else {
                se_vende = 3;
            }

            suministroTB.setUnidadVenta(se_vende);
            suministroTB.setLote(cbLote.isSelected());
            suministroTB.setInventario(cbInventario.isSelected());
            suministroTB.setValorInventario(rbValorUnidad.isSelected());
            suministroTB.setImpuestoArticulo(cbImpuesto.getSelectionModel().getSelectedIndex() >= 0 ? cbImpuesto.getSelectionModel().getSelectedItem().getIdImpuesto() : 0);
            suministroTB.setClaveSat(txtClaveSat.getText().trim());
            suministroTB.setTipoPrecio(rbPrecioNormal.isSelected());

            tvPreciosNormal.add(new PreciosTB(Integer.parseInt(txtPrecio2.getId()), "Precio de Venta 1", !Tools.isNumeric(txtPrecio2.getText()) ? 0 : Double.parseDouble(txtPrecio2.getText()), 1));
            tvPreciosNormal.add(new PreciosTB(Integer.parseInt(txtPrecio3.getId()), "Pricio de Venta 2", !Tools.isNumeric(txtPrecio3.getText()) ? 0 : Double.parseDouble(txtPrecio3.getText()), 1));

//            if (articulo) {
//                String result = SuministroADO.CrudSuministro_Articulo(suministroTB);
//                switch (result) {
//                    case "registered":
//                        Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.INFORMATION, "Producto", "Se clono correctamente el producto a artículo.", false);
//                        closeWindow();
//                        break;
//                    case "duplicate":
//                        Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Producto", "No se puede haber 2 artículos con las misma clave.", false);
//                        txtClave.requestFocus();
//                        break;
//                    case "duplicatename":
//                        Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Producto", "No se puede haber 2 producto con el mismo nombre.", false);
//                        txtNombreMarca.requestFocus();
//
//                    default:
//                        Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.ERROR, "Producto", result, false);
//                        break;
//                }
//            } else {
            String result = SuministroADO.CrudSuministro(suministroTB,
                    rbPrecioNormal.isSelected() ? tvPreciosNormal : tvPrecios.getItems());
            switch (result) {
                case "registered":
                    Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.INFORMATION, "Producto", "Registrado correctamente el producto.", false);
                    closeWindow();
                    break;
                case "updated":
                    Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.INFORMATION, "Producto", "Actualizado correctamente el producto.", false);
                    closeWindow();
                    break;
                case "duplicate":
                    Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Producto", "No se puede haber 2 producto con la misma clave.", false);
                    txtClave.requestFocus();
                    break;
                case "duplicatename":
                    Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Producto", "No se puede haber 2 producto con el mismo nombre.", false);
                    txtNombreMarca.requestFocus();
                    break;
                case "mayor":
                    Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Producto", "No se puede desleccionar el lote, ya que el producto cuenta con unidades.", false);
                    cbLote.requestFocus();
                    break;
                default:
                    Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.ERROR, "Producto", result, false);
                    break;
            }
//            }
        }
    }

    private void openWindowDetalle(String title, String idDetalle, boolean valor) {
        try {
            URL url = getClass().getResource(FilesRouters.FX_DETALLE_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxDetalleListaController controller = fXMLLoader.getController();
            controller.setControllerSuministroModal(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, title, apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();

            stage.show();
            if (valor == true) {
                controller.initListNameImpuesto(idDetalle);
            } else {
                controller.initListDetalle(idDetalle, "");
            }
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }

    }

    private void openWindowGerarCodigoBarras() {
        try {
            URL url = getClass().getResource(FilesRouters.FX_CODIGO_BARRAS);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxCodigoBarrasController controller = fXMLLoader.getController();
            controller.setControllerSuministroModal(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Generar codigo de barras", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();

            stage.show();
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void selectFileImage() {
        if (selectFile.getName().endsWith("png") || selectFile.getName().endsWith("jpg") || selectFile.getName().endsWith("jpeg") || selectFile.getName().endsWith(".gif")) {
            lnPrincipal.setImage(new Image(selectFile.toURI().toString()));
            FileInputStream inputStream = null;
            byte[] buffer = new byte[1024];
            try (FileOutputStream outputStream = new FileOutputStream("." + File.separator + "img" + File.separator + selectFile.getName())) {
                inputStream = new FileInputStream(selectFile.getAbsolutePath());
                int byteRead;
                while ((byteRead = inputStream.read(buffer)) != 1) {
                    outputStream.write(buffer, 0, byteRead);
                }
            } catch (Exception e) {
                if (e.getMessage() != null) {
                    Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.ERROR, "Producto", e.getLocalizedMessage() + ": Consulte con el proveedor del sistema del problema :D", false);
                }
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ex) {
                        System.out.println(ex.getLocalizedMessage());
                    }
                }
            }
        }
    }

    private void closeWindow() {
        Tools.Dispose(apWindow);
    }

    private void calculateForCosto(TextField cos) {
        if (Tools.isNumeric(cos.getText())) {
            if (!vbImpuestos.getChildren().isEmpty()) {
                double totalImpuesto = 0;
                for (int i = 0; i < vbImpuestos.getChildren().size(); i++) {
                    totalImpuesto += ((CheckBoxModel) vbImpuestos.getChildren().get(i)).isSelected()
                            ? (Double.parseDouble(cos.getText()) * ((double) ((CheckBoxModel) vbImpuestos.getChildren().get(i)).getValor() / 100.00))
                            : 0;
                }
                double valorCalculado = Double.parseDouble(cos.getText()) + totalImpuesto;
                txtCostoPromedio.setText(Tools.roundingValue(valorCalculado, 4));

//                double costo = Double.parseDouble(txtCostoPromedio.getText());
//                int margen = Integer.parseInt(mar.getText());
//
//                double precio = Tools.calculateAumento(margen, costo);
//
//                double impuesto = Tools.calculateTax(
//                        cbImpuesto.getSelectionModel().getSelectedIndex() >= 0
//                        ? cbImpuesto.getSelectionModel().getSelectedItem().getValor()
//                        : 0,
//                        precio);
//                double precioimpuesto = (precio + impuesto);
//                prec.setText(Tools.roundingValue(precio, 2));
//                uti.setText(Tools.roundingValue((precio - costo), 2));
//                precneto.setText(Tools.roundingValue(precioimpuesto, 2));
            } else {
                //toma el valor del impuesto del combo box
                double valorCalculado = Double.parseDouble(cos.getText());
                txtCostoPromedio.setText(Tools.roundingValue(valorCalculado, 4));
//                double costo = Double.parseDouble(txtCostoPromedio.getText());
//                int margen = Integer.parseInt(mar.getText());
//
//                double precio = Tools.calculateAumento(margen, costo);
//
//                double impuesto = Tools.calculateTax(
//                        cbImpuesto.getSelectionModel().getSelectedIndex() >= 0
//                        ? cbImpuesto.getSelectionModel().getSelectedItem().getValor()
//                        : 0,
//                        precio);
//                double precioimpuesto = (precio + impuesto);
//
//                prec.setText(Tools.roundingValue(precio, 2));
//                uti.setText(Tools.roundingValue((precio - costo), 2));
//                precneto.setText(Tools.roundingValue(precioimpuesto, 2));
            }
        }
    }

    private void calculateImpuesto(TextField cost, TextField mar, TextField util, TextField precneto, TextField prec) {
        if (cbImpuesto.getSelectionModel().getSelectedIndex() >= 0) {
            if (Tools.isNumeric(precneto.getText())) {
                if (cbInventario.isSelected()) {
                    if (Tools.isNumeric(cost.getText())) {
                        if (Double.parseDouble(cost.getText()) <= 0) {
                            return;
                        }
                        double impuesto = cbImpuesto.getSelectionModel().getSelectedIndex() >= 0 ? cbImpuesto.getSelectionModel().getSelectedItem().getValor() : 0;
                        double costo = Double.parseDouble(cost.getText());
                        double precioNeto = Double.parseDouble(precneto.getText());

                        double precio = Tools.calculateValueNeto(impuesto, precioNeto);

                        double porcentaje = (precio * 100.00) / costo;

                        int recalculado = (int) Math.abs(100 - Double.parseDouble(Tools.roundingValue(Double.parseDouble(Tools.roundingValue(porcentaje, 4)), 0)));

                        prec.setText(Tools.roundingValue(precio, 4));
                        mar.setText(String.valueOf(recalculado));
                        util.setText(Tools.roundingValue((precio - costo), 4));
                    }
                } else {
                    double impuesto = cbImpuesto.getSelectionModel().getSelectedIndex() >= 0 ? cbImpuesto.getSelectionModel().getSelectedItem().getValor() : 0;
                    double precioNeto = Double.parseDouble(precneto.getText());

                    double precio = Tools.calculateValueNeto(impuesto, precioNeto);

                    prec.setText(Tools.roundingValue(precio, 4));
                    mar.setText(String.valueOf(0));
                    util.setText(Tools.roundingValue((0), 4));
                }
            }
        }
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

    private void calculateForMargen(TextField mar, TextField cos, TextField pre, TextField uti, TextField preneto) {
        if (Tools.isNumeric(mar.getText()) && Tools.isNumeric(cos.getText())) {
            //toma el valor del impuesto del combo box
            double costo = Double.parseDouble(cos.getText());
            int margen = Integer.parseInt(mar.getText());

            double precio = Tools.calculateAumento(margen, costo);

            double impuesto = Tools.calculateTax(
                    cbImpuesto.getSelectionModel().getSelectedIndex() >= 0
                    ? cbImpuesto.getSelectionModel().getSelectedItem().getValor()
                    : 0,
                    precio);
            double precioimpuesto = (precio + impuesto);

            pre.setText(Tools.roundingValue(precio, 4));
            uti.setText(Tools.roundingValue((precio - costo), 4));
            preneto.setText(Tools.roundingValue(precioimpuesto, 4));
        }
    }

    private void calculateForPrecioNeto(TextField preneto, TextField cos, TextField pre, TextField mar, TextField uti) {
        if (Tools.isNumeric(preneto.getText())) {
            if (cbInventario.isSelected()) {
                if (Tools.isNumeric(cos.getText())) {
                    if (Double.parseDouble(cos.getText()) <= 0) {
                        return;
                    }

                    double costo = Double.parseDouble(cos.getText());
                    double impuesto = cbImpuesto.getSelectionModel().getSelectedIndex() >= 0 ? cbImpuesto.getSelectionModel().getSelectedItem().getValor() : 0;
                    double precioNeto = Double.parseDouble(preneto.getText());

                    double precio = Tools.calculateValueNeto(impuesto, precioNeto);

                    double porcentaje = (precio * 100.00) / costo;

                    int recalculado = (int) Math.abs(100
                            - Double.parseDouble(
                                    Tools.roundingValue(Double.parseDouble(
                                            Tools.roundingValue(porcentaje, 4)), 0)));

                    pre.setText(Tools.roundingValue(precio, 4));
                    mar.setText(String.valueOf(recalculado));
                    uti.setText(Tools.roundingValue((precio - costo), 4));
                }
            } else {
                double impuesto = cbImpuesto.getSelectionModel().getSelectedIndex() >= 0 ? cbImpuesto.getSelectionModel().getSelectedItem().getValor() : 0;
                double precioNeto = Double.parseDouble(preneto.getText());

                double precio = Tools.calculateValueNeto(impuesto, precioNeto);

                pre.setText(Tools.roundingValue(precio, 4));
                mar.setText(String.valueOf(0));
                uti.setText(Tools.roundingValue((0), 4));
            }
        }
    }

    @FXML
    private void onActionToRegister(ActionEvent event) {
        aValidityProcess();
    }

    @FXML
    private void onKeyPressedToRegister(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            aValidityProcess();
        }
    }

    @FXML
    private void onKeyPressedToCancel(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            closeWindow();
        }
    }

    @FXML
    private void onActionToCancel(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void onActionPhoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar una imagen");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Elija una imagen", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        selectFile = fileChooser.showOpenDialog(apWindow.getScene().getWindow());
        if (selectFile != null) {
            selectFile = new File(selectFile.getAbsolutePath());
            File fcom = new File("./img/" + selectFile.getName());
            if (fcom.exists()) {
                fcom.delete();
                selectFileImage();
            } else {
                selectFileImage();
            }
        }
    }

    @FXML
    private void onActionRemovePhoto(ActionEvent event) {
        lnPrincipal.setImage(new Image("/view/image/no-image.png"));
        selectFile = null;
    }

    @FXML
    private void onKeyTypedClave(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedClaveAlterna(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedMinimo(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtStockMinimo.getText().contains(".") || c == '-' && txtStockMinimo.getText().contains("-")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedMaxino(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtStockMaximo.getText().contains(".") || c == '-' && txtStockMaximo.getText().contains("-")) {
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
        if (c == '.' && txtPrecio1.getText().contains(".") || c == '-' && txtPrecio1.getText().contains("-")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedPrecio3(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtPrecio1.getText().contains(".") || c == '-' && txtPrecio1.getText().contains("-")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedPrecioNeto1(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtPrecioVentaNeto1.getText().contains(".") || c == '-' && txtPrecioVentaNeto1.getText().contains("-")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedPrecioNeto2(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtPrecioVentaNeto2.getText().contains(".") || c == '-' && txtPrecioVentaNeto2.getText().contains("-")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedPrecioNeto3(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtPrecioVentaNeto3.getText().contains(".") || c == '-' && txtPrecioVentaNeto3.getText().contains("-")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedDetalle(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if (c != '\b') {
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
    private void onActionGenerar(ActionEvent event) {
        openWindowGerarCodigoBarras();
    }

    @FXML
    private void onKeyReleasedCategoria(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            openWindowDetalle("Agregar Categoría", "0006", false);
        }
    }

    @FXML
    private void onKeyReleasedMarca(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            openWindowDetalle("Agregar Marca", "0007", false);
        }
    }

    @FXML
    private void onKeyReleasedPresentacion(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            openWindowDetalle("Agregar Presentación", "0008", false);
        }
    }

    @FXML
    private void onMouseClickedCategoria(MouseEvent event) {
        if (event.getClickCount() == 2) {
            openWindowDetalle("Agregar Categoría", "0006", false);
        }
    }

    @FXML
    private void onMouseClickedMarca(MouseEvent event) {
        if (event.getClickCount() == 2) {
            openWindowDetalle("Agregar Marca", "0007", false);
        }
    }

    @FXML
    private void onMouseClickedPresentacion(MouseEvent event) {
        if (event.getClickCount() == 2) {
            openWindowDetalle("Agregar Presentación", "0008", false);
        }
    }

    @FXML
    private void onMouseClickedMedida(MouseEvent event) {
        if (event.getClickCount() == 2) {
            openWindowDetalle("Agregar Departamento", "0013", false);
        }
    }

    @FXML
    private void onKeyReleasedMedida(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            openWindowDetalle("Agregar Departamento", "0013", false);
        }
    }

    private void onMouseClickedImpuesto(MouseEvent event) {
        if (event.getClickCount() == 2) {
            openWindowDetalle("Agregar Impuestoto", "0", true);
        }
    }

    private void onKeyReleasedImpuesto(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            openWindowDetalle("Agregar Impuesto", "0", true);
        }
    }

    @FXML
    private void onKeyTypedMedida(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if (c != '\b') {
            event.consume();
        }
    }

    private void onKeyTypedImpuesto(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if (c != '\b') {
            event.consume();
        }
    }

    @FXML
    private void onKeyReleasedCosto(KeyEvent event) {
        calculateForCosto(txtCosto);
    }

    @FXML
    private void onActionImpuesto(ActionEvent event) {
        calculateImpuesto(txtCostoPromedio, txtMargen1, txtUtilidad1, txtPrecioVentaNeto1, txtPrecio1);
        calculateImpuesto(txtCostoPromedio, txtMargen2, txtUtilidad2, txtPrecioVentaNeto2, txtPrecio2);
        calculateImpuesto(txtCostoPromedio, txtMargen3, txtUtilidad3, txtPrecioVentaNeto3, txtPrecio3);
    }

    @FXML
    private void onKeyRealesedPrecio1(KeyEvent event) {
        calculateForPrecio(txtPrecio1, txtCostoPromedio, txtMargen1, txtUtilidad1, txtPrecioVentaNeto1);
    }

    @FXML
    private void onKeyRealesedPrecio2(KeyEvent event) {
        calculateForPrecio(txtPrecio2, txtCostoPromedio, txtMargen2, txtUtilidad2, txtPrecioVentaNeto2);
    }

    @FXML
    private void onKeyRealesedPrecio3(KeyEvent event) {
        calculateForPrecio(txtPrecio3, txtCostoPromedio, txtMargen3, txtUtilidad3, txtPrecioVentaNeto3);
    }

    @FXML
    private void onKeyRealesedPrecioNeto1(KeyEvent event) {
        calculateForPrecioNeto(txtPrecioVentaNeto1, txtCostoPromedio, txtPrecio1, txtMargen1, txtUtilidad1);
    }

    @FXML
    private void onKeyRealesedPrecioNeto2(KeyEvent event) {
        calculateForPrecioNeto(txtPrecioVentaNeto2, txtCostoPromedio, txtPrecio2, txtMargen2, txtUtilidad2);
    }

    @FXML
    private void onKeyRealesedPrecioNeto3(KeyEvent event) {
        calculateForPrecioNeto(txtPrecioVentaNeto3, txtCostoPromedio, txtPrecio3, txtMargen3, txtUtilidad3);
    }

    @FXML
    private void onKeyReleasedMargen1(KeyEvent event) {
        calculateForMargen(txtMargen1, txtCostoPromedio, txtPrecio1, txtUtilidad1, txtPrecioVentaNeto1);
    }

    @FXML
    private void onKeyReleasedMargen2(KeyEvent event) {
        calculateForMargen(txtMargen2, txtCostoPromedio, txtPrecio2, txtUtilidad2, txtPrecioVentaNeto2);
    }

    @FXML
    private void onKeyReleasedMargen3(KeyEvent event) {
        calculateForMargen(txtMargen3, txtCostoPromedio, txtPrecio3, txtUtilidad3, txtPrecioVentaNeto3);
    }

    @FXML
    private void onActionInventario(ActionEvent event) {
        vbInventario.setDisable(!cbInventario.isSelected());
    }

    @FXML
    private void onActionRbListaPrecios(ActionEvent event) {
        if (rbPrecioNormal.isSelected()) {
            vbContenedorPrecioNormal.getChildren().add(hbPrecioNormal);
            vbContenedorPreciosPersonalizado.getChildren().remove(vbPrecioPersonalizado);
        } else {
            vbContenedorPreciosPersonalizado.getChildren().add(vbPrecioPersonalizado);
            vbContenedorPrecioNormal.getChildren().remove(hbPrecioNormal);
        }
    }

    @FXML
    private void onKeyRealesedPrecioNetoPerzonalizado(KeyEvent event) {
        calculateForPrecioNeto(txtPrecioVentaNetoPersonalizado, txtCostoPromedio, txtPrecioVentaBrutoPersonalizado, txtMargenPersonalizado, txtUtilidadPersonalizado);

    }

    @FXML
    private void onKeyTypedPrecioNetoPerzonalizado(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtPrecioVentaNetoPersonalizado.getText().contains(".") || c == '-' && txtPrecioVentaNetoPersonalizado.getText().contains("-")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyReleasedMargenPersonalizado(KeyEvent event) {
        calculateForMargen(txtMargenPersonalizado, txtCostoPromedio, txtPrecioVentaBrutoPersonalizado, txtUtilidadPersonalizado, txtPrecioVentaNetoPersonalizado);
    }

    @FXML
    private void onKeyPressedAdd(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            addElementsTablePrecios();
        }
    }

    @FXML
    private void onActionAdd(ActionEvent event) {
        addElementsTablePrecios();
    }

    @FXML
    private void onActionOrigen(ActionEvent event) {
        if (cbOrigen.getSelectionModel().getSelectedIndex() == 2) {
            estadoOrigen = true;
            rbPrecioNormal.setSelected(true);
            cbInventario.setSelected(false);
            vbInventario.setDisable(true);
            txtPrecioVentaNeto1.clear();
            txtPrecioVentaNeto2.clear();
            txtPrecioVentaNeto3.clear();
            txtPrecio1.clear();
            txtPrecio2.clear();
            txtPrecio3.clear();
            txtCosto.clear();
            txtCostoPromedio.clear();
            txtMargen1.setText("30");
            txtUtilidad1.clear();
            cbImpuesto.getSelectionModel().select(-1);
            txtPrecioVentaNetoPersonalizado.clear();
            txtPrecioVentaBrutoPersonalizado.clear();
            txtMargenPersonalizado.clear();
            txtUtilidadPersonalizado.clear();
            tvPrecios.getItems().clear();
            tpContenedor.getTabs().get(1).setDisable(true);
        } else {
            if (estadoOrigen) {
                estadoOrigen = false;
                tpContenedor.getTabs().get(1).setDisable(false);
            }
        }
    }

    public void setIdPresentacion(int idPresentacion) {
        this.idPresentacion = idPresentacion;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

    public void setIdMedida(int idMedida) {
        this.idMedida = idMedida;
    }

    public TextField getTxtPresentacion() {
        return txtPresentacion;
    }

    public TextField getTxtCategoria() {
        return txtCategoria;
    }

    public TextField getTxtMarca() {
        return txtMarca;
    }

    public TextField getTxtClave() {
        return txtClave;
    }

    public TextField getTxtMedida() {
        return txtMedida;
    }

}
