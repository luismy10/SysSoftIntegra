package controller.produccion.producir;

import controller.menus.FxPrincipalController;
import controller.tools.SearchComboBox;
import controller.tools.Tools;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.EmpleadoTB;
import model.FormulaADO;
import model.FormulaTB;
import model.SuministroADO;
import model.SuministroTB;

public class FxProducirAgregarController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private ComboBox<SuministroTB> cbProducto;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TextField txtProyecto;
    @FXML
    private ComboBox<FormulaTB> cbFormula;
    @FXML
    private VBox vbPrimero;
    @FXML
    private VBox vbSegundo;
    @FXML
    private RadioButton cbInterno;
    @FXML
    private RadioButton cbExterno;
    @FXML
    private ComboBox<EmpleadoTB> cbPersonaEncargada;
    @FXML
    private GridPane gpList;

    private FxProducirController producirController;

    private FxPrincipalController fxPrincipalController;

    private ArrayList<SuministroTB> suministroTBs;

    private SearchComboBox<FormulaTB> searchComboBoxFormula;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        suministroTBs = new ArrayList();
        searchComboBoxFormula = new SearchComboBox<>(cbFormula, false);
        ToggleGroup toggleGroup = new ToggleGroup();
        cbInterno.setToggleGroup(toggleGroup);
        cbExterno.setToggleGroup(toggleGroup);
        comboBoxProductos();
        comboBoxFormulas();
        comboBoxEmpleados();
    }

    private void comboBoxProductos() {
        SearchComboBox<SuministroTB> searchComboBoxSuministro = new SearchComboBox<>(cbProducto, false);
        searchComboBoxSuministro.getSearchComboBoxSkin().getSearchBox().setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                if (!searchComboBoxSuministro.getSearchComboBoxSkin().getItemView().getItems().isEmpty()) {
                    searchComboBoxSuministro.getSearchComboBoxSkin().getItemView().getSelectionModel().select(0);
                    searchComboBoxSuministro.getSearchComboBoxSkin().getItemView().requestFocus();
                }
            } else if (t.getCode() == KeyCode.ESCAPE) {
                searchComboBoxSuministro.getComboBox().hide();
            }
        });
        searchComboBoxSuministro.getSearchComboBoxSkin().getSearchBox().setOnKeyReleased(t -> {
            if (!Tools.isText(searchComboBoxSuministro.getSearchComboBoxSkin().getSearchBox().getText())) {
                searchComboBoxSuministro.getComboBox().getItems().clear();
                List<SuministroTB> suministroTBs = SuministroADO.getSearchComboBoxSuministros(searchComboBoxSuministro.getSearchComboBoxSkin().getSearchBox().getText().trim());
                suministroTBs.forEach(p -> cbProducto.getItems().add(p));
            }
        });
        searchComboBoxSuministro.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
            switch (t.getCode()) {
                case ENTER:
                case SPACE:
                case ESCAPE:
                    searchComboBoxSuministro.getComboBox().hide();
                    break;
                case UP:
                case DOWN:
                case LEFT:
                case RIGHT:
                    break;
                default:
                    searchComboBoxSuministro.getSearchComboBoxSkin().getSearchBox().requestFocus();
                    searchComboBoxSuministro.getSearchComboBoxSkin().getSearchBox().selectAll();
                    break;
            }
        });
        searchComboBoxSuministro.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                searchComboBoxSuministro.getComboBox().getSelectionModel().select(item);
                if (searchComboBoxSuministro.getSearchComboBoxSkin().isClickSelection()) {
                    searchComboBoxSuministro.getComboBox().hide();
                }
            }
        });
    }

    private void comboBoxFormulas() {
        searchComboBoxFormula.getSearchComboBoxSkin().getSearchBox().setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                if (!searchComboBoxFormula.getSearchComboBoxSkin().getItemView().getItems().isEmpty()) {
                    searchComboBoxFormula.getSearchComboBoxSkin().getItemView().getSelectionModel().select(0);
                    searchComboBoxFormula.getSearchComboBoxSkin().getItemView().requestFocus();
                }
            } else if (t.getCode() == KeyCode.ESCAPE) {
                searchComboBoxFormula.getComboBox().hide();
            }
        });
        searchComboBoxFormula.getSearchComboBoxSkin().getSearchBox().setOnKeyReleased(t -> {
            if (!Tools.isText(searchComboBoxFormula.getSearchComboBoxSkin().getSearchBox().getText())) {
                searchComboBoxFormula.getComboBox().getItems().clear();
                List<FormulaTB> formulaTBs = FormulaADO.getSearchComboBoxFormulas(cbProducto.getSelectionModel().getSelectedItem().getIdSuministro(), searchComboBoxFormula.getSearchComboBoxSkin().getSearchBox().getText().trim());
                formulaTBs.forEach(p -> cbFormula.getItems().add(p));
            }
        });
        searchComboBoxFormula.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
            switch (t.getCode()) {
                case ENTER:
                case SPACE:
                case ESCAPE:
                    searchComboBoxFormula.getComboBox().hide();
                    break;
                case UP:
                case DOWN:
                case LEFT:
                case RIGHT:
                    break;
                default:
                    searchComboBoxFormula.getSearchComboBoxSkin().getSearchBox().requestFocus();
                    searchComboBoxFormula.getSearchComboBoxSkin().getSearchBox().selectAll();
                    break;
            }
        });
        searchComboBoxFormula.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                searchComboBoxFormula.getComboBox().getSelectionModel().select(item);
                if (searchComboBoxFormula.getSearchComboBoxSkin().isClickSelection()) {
                    searchComboBoxFormula.getComboBox().hide();
                }
            }
        });
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

    private void onEventSiguiente() {
        if (Tools.isText(txtProyecto.getText())) {
            Tools.AlertMessageWarning(apWindow, "Producción", "Ingrese el nombre del proyecto.");
            txtProyecto.requestFocus();
        } else if (!Tools.isNumeric(txtCantidad.getText())) {
            Tools.AlertMessageWarning(apWindow, "Producción", "Ingrese la cantidad a producir.");
            txtCantidad.requestFocus();
        } else if (Double.parseDouble(txtCantidad.getText()) <= 0) {
            Tools.AlertMessageWarning(apWindow, "Producción", "Ingrese una cantidad mayor a 0");
            txtCantidad.requestFocus();
        } else {
            vbPrimero.setVisible(false);
            vbSegundo.setVisible(true);

        }
    }

    private void closeWindow() {
        fxPrincipalController.getVbContent().getChildren().remove(apWindow);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(producirController.getWindow(), 0d);
        AnchorPane.setTopAnchor(producirController.getWindow(), 0d);
        AnchorPane.setRightAnchor(producirController.getWindow(), 0d);
        AnchorPane.setBottomAnchor(producirController.getWindow(), 0d);
        fxPrincipalController.getVbContent().getChildren().add(producirController.getWindow());
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        closeWindow();
    }

    @FXML
    private void onKeyPressedSiguiente(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventSiguiente();
        }
    }

    @FXML
    private void onActionSiguiente(ActionEvent event) {
        onEventSiguiente();
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

    public void setInitControllerProducir(FxProducirController producirController, FxPrincipalController fxPrincipalController) {
        this.producirController = producirController;
        this.fxPrincipalController = fxPrincipalController;
    }

    @FXML
    private void onKeyPressedAgregar(KeyEvent event) {
    }

    @FXML
    private void onActonAgregar(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedGuardar(KeyEvent event) {
    }

    @FXML
    private void onActionGuardar(ActionEvent event) {
    }

}
