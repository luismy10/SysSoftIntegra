package controller.produccion.producir;

import controller.tools.SearchComboBox;
import controller.tools.Tools;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import model.EmpleadoTB;
import model.FormulaTB;
import model.ProduccionADO;
import model.ProduccionTB;
import model.SuministroADO;
import model.SuministroTB;

public class FxProducirAgregarController implements Initializable {

    @FXML
    private ScrollPane spWindow;
    @FXML
    private RadioButton cbInterno;
    @FXML
    private RadioButton cbExterno;
    @FXML
    private ComboBox<SuministroTB> cbProducto;
    @FXML
    private ComboBox<FormulaTB> cbFormula;
    @FXML
    private ComboBox<EmpleadoTB> cbPersonaEncargada;
    @FXML
    private TextArea txtGlosaDescriptiva;
    @FXML
    private DatePicker txtFechaRegistro;

    private FxProducirController producirController;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;
    @FXML
    private TextField txtDias;
    @FXML
    private TextField txtHoras;
    @FXML
    private TextField txtMinutos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ToggleGroup toggleGroup = new ToggleGroup();
        cbInterno.setToggleGroup(toggleGroup);
        cbExterno.setToggleGroup(toggleGroup);
        comboBoxProductos();
        comboBoxEmpleados();
    }

    private void comboBoxProductos() {
        SearchComboBox<SuministroTB> searchComboBox = new SearchComboBox<>(cbProducto, false);
        searchComboBox.getSearchComboBoxSkin().getSearchBox().setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                if (!searchComboBox.getSearchComboBoxSkin().getItemView().getItems().isEmpty()) {
                    searchComboBox.getSearchComboBoxSkin().getItemView().getSelectionModel().select(0);
                    searchComboBox.getSearchComboBoxSkin().getItemView().requestFocus();
                }
            } else if (t.getCode() == KeyCode.ESCAPE) {
                searchComboBox.getComboBox().hide();
            }
        });
        searchComboBox.getSearchComboBoxSkin().getSearchBox().setOnKeyReleased(t -> {
            if (!Tools.isText(searchComboBox.getSearchComboBoxSkin().getSearchBox().getText())) {
                searchComboBox.getComboBox().getItems().clear();
                List<SuministroTB> suministroTBs = SuministroADO.getSearchComboBoxSuministros(searchComboBox.getSearchComboBoxSkin().getSearchBox().getText().trim());
                suministroTBs.forEach(p -> cbProducto.getItems().add(p));
            }
        });
        searchComboBox.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
            switch (t.getCode()) {
                case ENTER:
                case SPACE:
                case ESCAPE:
                    searchComboBox.getComboBox().hide();
                    break;
                case UP:
                case DOWN:
                case LEFT:
                case RIGHT:
                    break;
                default:
                    searchComboBox.getSearchComboBoxSkin().getSearchBox().requestFocus();
                    searchComboBox.getSearchComboBoxSkin().getSearchBox().selectAll();
                    break;
            }
        });
        searchComboBox.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                searchComboBox.getComboBox().getSelectionModel().select(item);
                if (searchComboBox.getSearchComboBoxSkin().isClickSelection()) {
                    searchComboBox.getComboBox().hide();
                }
            }
        });
    }

    private void comboBoxFormula() {

    }

    private void comboBoxEmpleados() {
        SearchComboBox<EmpleadoTB> searchComboBox = new SearchComboBox<>(cbPersonaEncargada, false);
        searchComboBox.getSearchComboBoxSkin().getSearchBox().setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                if (!searchComboBox.getSearchComboBoxSkin().getItemView().getItems().isEmpty()) {
                    searchComboBox.getSearchComboBoxSkin().getItemView().getSelectionModel().select(0);
                    searchComboBox.getSearchComboBoxSkin().getItemView().requestFocus();
                }
            } else if (t.getCode() == KeyCode.ESCAPE) {
                searchComboBox.getComboBox().hide();
            }
        });
        searchComboBox.getSearchComboBoxSkin().getSearchBox().setOnKeyReleased(t -> {
            if (!Tools.isText(searchComboBox.getSearchComboBoxSkin().getSearchBox().getText())) {
                searchComboBox.getComboBox().getItems().clear();
                List<EmpleadoTB> empleadoTBs = EmpleadoTB.getSearchComboBoxEmpleados(searchComboBox.getSearchComboBoxSkin().getSearchBox().getText().trim());
                empleadoTBs.forEach(p -> cbPersonaEncargada.getItems().add(p));
            }
        });
        searchComboBox.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
            switch (t.getCode()) {
                case ENTER:
                case SPACE:
                case ESCAPE:
                    searchComboBox.getComboBox().hide();
                    break;
                case UP:
                case DOWN:
                case LEFT:
                case RIGHT:
                    break;
                default:
                    searchComboBox.getSearchComboBoxSkin().getSearchBox().requestFocus();
                    searchComboBox.getSearchComboBoxSkin().getSearchBox().selectAll();
                    break;
            }
        });
        searchComboBox.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                searchComboBox.getComboBox().getSelectionModel().select(item);
                if (searchComboBox.getSearchComboBoxSkin().isClickSelection()) {
                    searchComboBox.getComboBox().hide();
                }
            }
        });
    }

    private void clearComponentes() {
        cbProducto.getItems().clear();
        cbProducto.getSelectionModel().select(null);
        cbFormula.getItems().clear();
        cbFormula.getSelectionModel().select(null);
        cbPersonaEncargada.getItems().clear();
        cbPersonaEncargada.getSelectionModel().select(null);
        cbInterno.setSelected(true);
        txtGlosaDescriptiva.clear();
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

    private void registrarProduccion() {
        if (cbProducto.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(spWindow, "Producción", "Seleccione el producto a fabricar.");
            cbProducto.requestFocus();
        } else if (cbPersonaEncargada.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(spWindow, "Producción", "Seleccione el encargado de la producción.");
            cbPersonaEncargada.requestFocus();
        } else {
            ProduccionTB produccionTB = new ProduccionTB();
            produccionTB.setIdProducto(cbProducto.getSelectionModel().getSelectedItem().getIdSuministro());
            produccionTB.setTipoOrden(true);
            produccionTB.setIdEncargado(cbPersonaEncargada.getSelectionModel().getSelectedItem().getIdEmpleado());
            produccionTB.setDescripcion(txtGlosaDescriptiva.getText());
            produccionTB.setFechaRegistro(Tools.getDate());
            produccionTB.setHoraRegistro(Tools.getHour());

            short value = Tools.AlertMessageConfirmation(spWindow, "Producción", "¿Está seguro de continuar?");
            if (value == 1) {
                String result = ProduccionADO.RegistrarProduccion(produccionTB);
                if (result.equalsIgnoreCase("registrado")) {
                    Tools.AlertMessageInformation(spWindow, "Producción", "Se registró correctamente la produccón.");
                    clearComponentes();
                    closeWindow();
                } else {
                    Tools.AlertMessageWarning(spWindow, "Producción", result);
                }
            }

        }
    }    

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        closeWindow();
    }

    @FXML
    private void onKeyPressedGuardar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            registrarProduccion();
        }
    }

    @FXML
    private void onActionGuardar(ActionEvent event) {
        registrarProduccion();
    }

    @FXML
    private void onKeyPressedLimpiar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionLimpiar(ActionEvent event) {

    }

    private void onKeyPressedPersonaEncargada(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    public void setInitControllerProducir(FxProducirController producirController, AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.producirController = producirController;
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
