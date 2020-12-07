package controller.produccion.producir;

import controller.inventario.suministros.FxSuministrosListaController;
import controller.produccion.insumos.FxInsumosListaController;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
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
import javafx.stage.Stage;
import model.InsumoTB;
import report.ProduccionADO;
import report.ProduccionTB;

public class FxProducirProcesoController implements Initializable {

    @FXML
    private ScrollPane spWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private TableView<InsumoTB> tvListInsumos;
    @FXML
    private TableColumn<InsumoTB, Button> tcOpcion;
    @FXML
    private TableColumn<InsumoTB, String> tcCodigo;
    @FXML
    private TableColumn<InsumoTB, String> tcDescripcion;
    @FXML
    private TableColumn<InsumoTB, TextField> tcCantidad;
    @FXML
    private TableColumn<InsumoTB, String> tcMedida;
    @FXML
    private TableColumn<InsumoTB, String> tcCostoPromedio;
    @FXML
    private TextField txtProductoFabricar;
    @FXML
    private RadioButton cbInterno;
    @FXML
    private RadioButton cbExterno;
    @FXML
    private Button btnProducirFabricar;
    @FXML
    private DatePicker txtFechaRegistro;
    @FXML
    private DatePicker txtFechaInicio;
    @FXML
    private DatePicker txtTermino;

    private FxProducirController producirController;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    private String idSuministro;
    @FXML
    private Label lblCostoPromedio;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcOpcion.setCellValueFactory(new PropertyValueFactory<>("btnRemove"));
        tcCodigo.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getClave()));
        tcDescripcion.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getNombreMarca()));
        tcCantidad.setCellValueFactory(new PropertyValueFactory<>("txtCantidad"));
        tcMedida.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMedidaName()));
        tcCostoPromedio.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getCosto()));

        ToggleGroup toggleGroup = new ToggleGroup();
        cbInterno.setToggleGroup(toggleGroup);
        cbExterno.setToggleGroup(toggleGroup);

        Tools.actualDate(Tools.getDate(), txtFechaRegistro);
        Tools.actualDate(Tools.getDate(), txtFechaInicio);

    }

    public void addElementsTableInsumo(InsumoTB ins) {
        InsumoTB insumoTB = new InsumoTB();
        insumoTB.setIdInsumo(ins.getIdInsumo());
        Button button = new Button();
        button.getStyleClass().add("buttonLightError");
        ImageView view = new ImageView(new Image("/view/image/remove-black.png"));
        view.setFitWidth(22);
        view.setFitHeight(22);
        button.setGraphic(view);
        button.setOnAction(event -> {
            executeEventRomeverPrice(insumoTB);
        });
        button.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                executeEventRomeverPrice(insumoTB);
            }
        });
        insumoTB.setBtnRemove(button);
        insumoTB.setClave(ins.getClave());
        insumoTB.setNombreMarca(ins.getNombreMarca());
        insumoTB.setCosto(ins.getCosto());

        TextField tfCantidad = new TextField("0.00");
        tfCantidad.setPrefWidth(220);
        tfCantidad.setPrefHeight(30);
        tfCantidad.getStyleClass().add("text-field-normal");
        tfCantidad.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                double costoCreado = !Tools.isNumeric(tfCantidad.getText().trim()) ? (0 * ins.getCosto()) : (Double.parseDouble(tfCantidad.getText()) * insumoTB.getCosto());
                insumoTB.setCosto(costoCreado);
                tvListInsumos.refresh();
            }
        });
        tfCantidad.setOnKeyTyped(event -> {
            char c = event.getCharacter().charAt(0);
            if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
                event.consume();
            }
            if (c == '.' && tfCantidad.getText().contains(".")) {
                event.consume();
            }
        });
        insumoTB.setTxtCantidad(tfCantidad);
        insumoTB.setMedidaName(ins.getMedidaName());
        tvListInsumos.getItems().add(insumoTB);              
    }

    private void executeEventRomeverPrice(InsumoTB insumoTB) {
        short confirmation = Tools.AlertMessageConfirmation(spWindow, "Producir", "¿Esta seguro de quitar el insumo?");
        if (confirmation == 1) {
            ObservableList<InsumoTB> observableList;
            observableList = tvListInsumos.getItems();
            observableList.remove(insumoTB);
            tvListInsumos.requestFocus();
        }
    }

    private void closeWindow() {
        vbContent.getChildren().remove(spWindow);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(producirController.getWindow(), 0d);
        AnchorPane.setTopAnchor(producirController.getWindow(), 0d);
        AnchorPane.setRightAnchor(producirController.getWindow(), 0d);
        AnchorPane.setBottomAnchor(producirController.getWindow(), 0d);
        vbContent.getChildren().add(producirController.getWindow());
    }

    private void openWindowInsumos() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_INSUMO_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxInsumosListaController controller = fXMLLoader.getController();
            controller.setInitProducirProcesoController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Producto", spWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
            controller.loadInitComponents();
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void openWindowSuministros() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_SUMINISTROS_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxSuministrosListaController controller = fXMLLoader.getController();
            controller.setInitProducirProcesoController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Producto", spWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
            controller.fillSuministrosTable((short) 0, "");
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void registrarProduccion() {
        if (idSuministro == null || idSuministro.equalsIgnoreCase("")) {
            Tools.AlertMessageWarning(spWindow, "Produccón", "Ingrese el producto a producir.");
            btnProducirFabricar.requestFocus();
        } else if (txtFechaInicio.getValue() == null) {
            Tools.AlertMessageWarning(spWindow, "Produccón", "Ingrese la fecha de inicio de la producción.");
            txtFechaInicio.requestFocus();
        } else if (txtTermino.getValue() == null) {
            Tools.AlertMessageWarning(spWindow, "Produccón", "Ingrese la fecha de finalización de la producción.");
            txtTermino.requestFocus();
        } else {
            short value = Tools.AlertMessageConfirmation(spWindow, "Producción", "¿Está seguro de continuar?");
            if (value == 1) {
                ProduccionTB produccionTB = new ProduccionTB();
                produccionTB.setFechaProduccion(Tools.getDatePicker(txtFechaRegistro));
                produccionTB.setHoraProduccion(Tools.getHour());
                produccionTB.setFechaInicio(Tools.getDatePicker(txtFechaInicio));
                produccionTB.setFechaTermino(Tools.getDatePicker(txtTermino));
                produccionTB.setIdSuministro(idSuministro);
                produccionTB.setEstado((short) 2);
                produccionTB.setTipoOrden(cbInterno.isSelected());
                String result = ProduccionADO.RegistrarProduccion(produccionTB);
                if (result.equalsIgnoreCase("registrado")) {
                    Tools.AlertMessageInformation(spWindow, "Producción", "Se registro correctamente la orden de producción.");
                    clearComponents();
                } else {
                    Tools.AlertMessageError(spWindow, "Producción", result);
                }
            }
        }
    }

    private void clearComponents() {
        idSuministro = "";
        txtProductoFabricar.clear();

    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        closeWindow();
    }

    @FXML
    private void onKeyPressedBuscar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowInsumos();
        }
    }

    @FXML
    private void onActionBuscar(ActionEvent event) {
        openWindowInsumos();
    }

    public TableView<InsumoTB> getTvListInsumos() {
        return tvListInsumos;
    }

    private void onKeyPressedAgregar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            registrarProduccion();
        }
    }

    @FXML
    private void onActionAgregar(ActionEvent event) {
        registrarProduccion();
    }

    @FXML
    private void onKeyPressedFabricar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowSuministros();
        }
    }

    @FXML
    private void onActionBuscarProductoFabricar(ActionEvent event) {
        openWindowSuministros();
    }

    @FXML
    private void onKeyPressedLimpiar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            clearComponents();
        }
    }

    @FXML
    private void onActionLimpiar(ActionEvent event) {
        clearComponents();
    }

    public String getIdSuministro() {
        return idSuministro;
    }

    public void setIdSuministro(String idSuministro) {
        this.idSuministro = idSuministro;
    }

    public TextField getTxtProductoFabricar() {
        return txtProductoFabricar;
    }

    public void setInitControllerProducir(FxProducirController producirController, AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.producirController = producirController;
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
