package controller.produccion.producir;

import controller.menus.FxPrincipalController;
import controller.tools.SearchComboBox;
import controller.tools.Tools;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
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
    private DatePicker dtFechaProduccion;
    @FXML
    private TextField txtDias;
    @FXML
    private TextField txtHoras;
    @FXML
    private TextField txtMinutos;
    @FXML
    private RadioButton cbInterno;
    @FXML
    private RadioButton cbExterno;
    @FXML
    private ComboBox<EmpleadoTB> cbPersonaEncargada;
    @FXML
    private TextArea txtDescripcion;
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
        Tools.actualDate(Tools.getDate(), dtFechaProduccion);
        ToggleGroup toggleGroup = new ToggleGroup();
        cbInterno.setToggleGroup(toggleGroup);
        cbExterno.setToggleGroup(toggleGroup);
        addElementPaneHead();
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

    private void addElementsTableSuministro() {
        SuministroTB suministroTB = new SuministroTB();
        ComboBox<SuministroTB> comboBox = new ComboBox();
        comboBox.setPromptText("-- Selecionar --");
        comboBox.setPrefWidth(220);
        comboBox.setPrefHeight(30);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        suministroTB.setCbSuministro(comboBox);

        SearchComboBox<SuministroTB> searchComboBox = new SearchComboBox<>(suministroTB.getCbSuministro(), false);
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
        suministroTB.setSearchComboBox(searchComboBox);

        TextField textField = new TextField(Tools.roundingValue(1, 2));
        textField.setPromptText("0.00");
        textField.getStyleClass().add("text-field-normal");
        textField.setPrefWidth(220);
        textField.setPrefHeight(30);
        textField.setOnKeyTyped(event -> {
            char c = event.getCharacter().charAt(0);
            if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
                event.consume();
            }
            if (c == '.' && textField.getText().contains(".")) {
                event.consume();
            }
        });
        suministroTB.setTxtCantidad(textField);

        Button button = new Button();
        button.getStyleClass().add("buttonLightError");
        button.setAlignment(Pos.CENTER);
        button.setPrefWidth(Control.USE_COMPUTED_SIZE);
        button.setPrefHeight(Control.USE_COMPUTED_SIZE);
        ImageView imageView = new ImageView(new Image("/view/image/remove-gray.png"));
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);
        button.setGraphic(imageView);

        button.setOnAction(event -> {
            suministroTBs.remove(suministroTB);
            addElementPaneHead();
            addElementPaneBody();
        });
        button.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                suministroTBs.remove(suministroTB);
                addElementPaneHead();
                addElementPaneBody();
            }
        });
        suministroTB.setBtnRemove(button);
        suministroTBs.add(suministroTB);
        addElementPaneHead();
        addElementPaneBody();
    }

    private void addElementPaneHead() {
        gpList.getChildren().clear();
        gpList.getColumnConstraints().get(0).setMinWidth(10);
        gpList.getColumnConstraints().get(0).setPrefWidth(40);
        gpList.getColumnConstraints().get(0).setHgrow(Priority.SOMETIMES);

        gpList.getColumnConstraints().get(1).setMinWidth(10);
        gpList.getColumnConstraints().get(1).setPrefWidth(320);
        gpList.getColumnConstraints().get(1).setHgrow(Priority.SOMETIMES);

        gpList.getColumnConstraints().get(2).setMinWidth(10);
        gpList.getColumnConstraints().get(2).setPrefWidth(60);
        gpList.getColumnConstraints().get(2).setHgrow(Priority.SOMETIMES);

        gpList.getColumnConstraints().get(3).setMinWidth(10);
        gpList.getColumnConstraints().get(3).setPrefWidth(60);
        gpList.getColumnConstraints().get(3).setHgrow(Priority.SOMETIMES);

        gpList.getColumnConstraints().get(4).setMinWidth(10);
        gpList.getColumnConstraints().get(4).setPrefWidth(60);
        gpList.getColumnConstraints().get(4).setHgrow(Priority.SOMETIMES);
        gpList.getColumnConstraints().get(4).setHalignment(HPos.CENTER);

        gpList.add(addElementGridPaneLabel("l01", "N°"), 0, 0);
        gpList.add(addElementGridPaneLabel("l02", "Insumo"), 1, 0);
        gpList.add(addElementGridPaneLabel("l03", "Cantidad"), 2, 0);
        gpList.add(addElementGridPaneLabel("l04", "Medida"), 3, 0);
        gpList.add(addElementGridPaneLabel("l05", "Quitar"), 4, 0);
    }

    private Label addElementGridPaneLabel(String id, String nombre) {
        Label label = new Label(nombre);
        label.setId(id);
        label.setStyle("-fx-background-color: #020203;-fx-text-fill:#ffffff;-fx-padding: 0.6666666666666666em 0.16666666666666666em 0.6666666666666666em 0.16666666666666666em;-fx-font-weight: 100");
        label.getStyleClass().add("labelOpenSansRegular13");
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        return label;
    }

    private Label addElementGridPaneLabel(String id, String nombre, Pos pos) {
        Label label = new Label(nombre);
        label.setId(id);
        label.setStyle("-fx-text-fill:#020203;-fx-padding: 0.4166666666666667em 0.8333333333333334em 0.4166666666666667em 0.8333333333333334em;");
        label.getStyleClass().add("labelRoboto13");
        label.setAlignment(pos);
        label.setWrapText(true);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        return label;
    }

    private void addElementPaneBody() {
        for (int i = 0; i < suministroTBs.size(); i++) {
            gpList.add(addElementGridPaneLabel("l1" + (i + 1), (i + 1) + "", Pos.CENTER), 0, (i + 1));
            gpList.add(suministroTBs.get(i).getCbSuministro(), 1, (i + 1));
            gpList.add(suministroTBs.get(i).getTxtCantidad(), 2, (i + 1));
            gpList.add(addElementGridPaneLabel("l4" + (i + 1), "MEDIDA", Pos.CENTER), 3, (i + 1));
            gpList.add(suministroTBs.get(i).getBtnRemove(), 4, (i + 1));
        }
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
        } else if (cbProducto.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(apWindow, "Producción", "Seleccione un producto a producir.");
            cbProducto.requestFocus();
        } else {
            vbPrimero.setVisible(false);
            vbSegundo.setVisible(true);
            if (cbFormula.getSelectionModel().getSelectedIndex() >= 0) {
                editFormulaProceso(cbFormula.getSelectionModel().getSelectedItem().getIdFormula());
            } else {

            }

        }
    }

    public void editFormulaProceso(String idFormula) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<Object> task = new Task<Object>() {
            @Override
            protected Object call() {
                return FormulaADO.Obtener_Formula_ById(idFormula);
            }
        };

        task.setOnScheduled(w -> {

        });
        task.setOnFailed(w -> {

        });
        task.setOnSucceeded(w -> {
            Object object = task.getValue();
            if (object instanceof FormulaTB) {
                FormulaTB formulaTB = (FormulaTB) object;
                for (SuministroTB suministroTB : formulaTB.getSuministroTBs()) {
                    ComboBox<SuministroTB> comboBox = new ComboBox();
                    comboBox.setPromptText("-- Selecionar --");
                    comboBox.setPrefWidth(220);
                    comboBox.setPrefHeight(30);
                    comboBox.setMaxWidth(Double.MAX_VALUE);
                    suministroTB.setCbSuministro(comboBox);

                    SearchComboBox<SuministroTB> searchComboBox = new SearchComboBox<>(suministroTB.getCbSuministro(), false);
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
                    suministroTB.setSearchComboBox(searchComboBox);
                    suministroTB.getCbSuministro().getItems().add(new SuministroTB(suministroTB.getIdSuministro(), suministroTB.getClave(), suministroTB.getNombreMarca()));
                    suministroTB.getCbSuministro().getSelectionModel().select(0);

                    TextField textField = new TextField(Tools.roundingValue(Double.parseDouble(txtCantidad.getText()), 2));
                    textField.setPromptText("0.00");
                    textField.getStyleClass().add("text-field-normal");
                    textField.setPrefWidth(220);
                    textField.setPrefHeight(30);
                    textField.setOnKeyTyped(event -> {
                        char c = event.getCharacter().charAt(0);
                        if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
                            event.consume();
                        }
                        if (c == '.' && textField.getText().contains(".")) {
                            event.consume();
                        }
                    });
                    suministroTB.setTxtCantidad(textField);

                    Button button = new Button();
                    button.getStyleClass().add("buttonLightError");
                    button.setAlignment(Pos.CENTER);
                    button.setPrefWidth(Control.USE_COMPUTED_SIZE);
                    button.setPrefHeight(Control.USE_COMPUTED_SIZE);
                    ImageView imageView = new ImageView(new Image("/view/image/remove-gray.png"));
                    imageView.setFitWidth(20);
                    imageView.setFitHeight(20);
                    button.setGraphic(imageView);

                    button.setOnAction(event -> {
                        suministroTBs.remove(suministroTB);
                        addElementPaneHead();
                        addElementPaneBody();
                    });
                    button.setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.ENTER) {
                            suministroTBs.remove(suministroTB);
                            addElementPaneHead();
                            addElementPaneBody();
                        }
                    });
                    suministroTB.setBtnRemove(button);
                    suministroTBs.add(suministroTB);
                    addElementPaneHead();
                    addElementPaneBody();
                }

            } else {
                Tools.println((String) object);
            }
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
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

    private void onEventCancelar() {
        vbPrimero.setVisible(true);
        vbSegundo.setVisible(false);
        txtProyecto.clear();
        txtCantidad.clear();
        cbProducto.getItems().clear();
        cbProducto.getSelectionModel().select(null);
        cbFormula.getItems().clear();
        cbFormula.getSelectionModel().select(null);
        Tools.actualDate(Tools.getDate(), dtFechaProduccion);
        txtDias.clear();
        txtHoras.clear();
        txtMinutos.clear();
        txtDescripcion.clear();
        cbInterno.setSelected(true);
        cbPersonaEncargada.getItems().clear();
        cbPersonaEncargada.getSelectionModel().select(null);
        suministroTBs.clear();
        addElementPaneHead();
        addElementPaneBody();
        txtProyecto.requestFocus();
    }

    private void onEventGuardar() {
        if (dtFechaProduccion.getValue() == null) {

        }
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
    private void onKeyPressedAgregar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            addElementsTableSuministro();
        }
    }

    @FXML
    private void onActonAgregar(ActionEvent event) {
        addElementsTableSuministro();
    }

    @FXML
    private void onKeyPressedGuardar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventGuardar();
        }
    }

    @FXML
    private void onActionGuardar(ActionEvent event) {
        onEventGuardar();
    }

    @FXML
    private void onKeyPressedCancelar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventCancelar();
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        onEventCancelar();
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
    private void onKeyTypedDias(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b')) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedHoras(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b')) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedMinutos(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b')) {
            event.consume();
        }
    }

    public void setInitControllerProducir(FxProducirController producirController, FxPrincipalController fxPrincipalController) {
        this.producirController = producirController;
        this.fxPrincipalController = fxPrincipalController;
    }

}
