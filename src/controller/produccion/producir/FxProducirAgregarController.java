package controller.produccion.producir;

import controller.menus.FxPrincipalController;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.SearchComboBox;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TabPane;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.EmpleadoTB;
import model.FormulaADO;
import model.FormulaTB;
import model.ProduccionTB;
import model.SuministroADO;
import model.SuministroTB;

public class FxProducirAgregarController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private RadioButton cbInterno;
    @FXML
    private RadioButton cbExterno;
    @FXML
    private ComboBox<SuministroTB> cbProducto;
    @FXML
    private ComboBox<EmpleadoTB> cbPersonaEncargada;
    @FXML
    private TextArea txtGlosaDescriptiva;
    @FXML
    private TextField txtDias;
    @FXML
    private TextField txtHoras;
    @FXML
    private TextField txtMinutos;
    @FXML
    private DatePicker txtFechaInicio;
    @FXML
    private GridPane gpList;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TabPane tpContenedor;
    @FXML
    private VBox vbBody;
    @FXML
    private HBox hbLoad;
    @FXML
    private Label lblMessageLoad;
    @FXML
    private Button btnAceptarLoad;

    private FxProducirController producirController;

    private FxPrincipalController fxPrincipalController;

    private ArrayList<SuministroTB> suministroTBs;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        suministroTBs = new ArrayList();
        ToggleGroup toggleGroup = new ToggleGroup();
        cbInterno.setToggleGroup(toggleGroup);
        cbExterno.setToggleGroup(toggleGroup);
        addElementPaneHead();
        comboBoxProductos();
        comboBoxEmpleados();
    }

    private void addElementPaneHead() {
        gpList.getChildren().clear();
        gpList.getColumnConstraints().get(0).setMinWidth(10);
        gpList.getColumnConstraints().get(0).setPrefWidth(40);
        gpList.getColumnConstraints().get(0).setHgrow(Priority.SOMETIMES);

        gpList.getColumnConstraints().get(1).setMinWidth(10);
        gpList.getColumnConstraints().get(1).setPrefWidth(340);
        gpList.getColumnConstraints().get(1).setHgrow(Priority.SOMETIMES);

        gpList.getColumnConstraints().get(2).setMinWidth(10);
        gpList.getColumnConstraints().get(2).setPrefWidth(260);
        gpList.getColumnConstraints().get(2).setHgrow(Priority.SOMETIMES);

        gpList.getColumnConstraints().get(3).setMinWidth(10);
        gpList.getColumnConstraints().get(3).setPrefWidth(180);
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

    private void addElementPaneBody() {
//        for (int i = 0; i < insumoTBs.size(); i++) {
//            gpList.add(addElementGridPaneLabel("l1" + (i + 1), (i + 1) + "", Pos.CENTER), 0, (i + 1));
//            gpList.add(insumoTBs.get(i).getComboBox(), 1, (i + 1));
//            gpList.add(insumoTBs.get(i).getTxtCantidad(), 2, (i + 1));
//            gpList.add(addElementGridPaneLabel("l4" + (i + 1), "UNIDAD", Pos.CENTER), 3, (i + 1));
//            gpList.add(insumoTBs.get(i).getBtnRemove(), 4, (i + 1));
//        }
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

    private void addElementsTableInsumo() {
//        InsumoTB insumoTB = new InsumoTB();
//        ComboBox<InsumoTB> comboBox = new ComboBox();
//        comboBox.setPromptText("-- Selecionar --");
//        comboBox.setPrefWidth(220);
//        comboBox.setPrefHeight(30);
//        comboBox.setMaxWidth(Double.MAX_VALUE);
//        insumoTB.setComboBox(comboBox);
//
//        SearchComboBox<InsumoTB> searchComboBox = new SearchComboBox<>(insumoTB.getComboBox(), false);
//        searchComboBox.getSearchComboBoxSkin().getSearchBox().setOnKeyPressed(t -> {
//            if (t.getCode() == KeyCode.ENTER) {
//                if (!searchComboBox.getSearchComboBoxSkin().getItemView().getItems().isEmpty()) {
//                    searchComboBox.getSearchComboBoxSkin().getItemView().getSelectionModel().select(0);
//                    searchComboBox.getSearchComboBoxSkin().getItemView().requestFocus();
//                }
//            } else if (t.getCode() == KeyCode.ESCAPE) {
//                searchComboBox.getComboBox().hide();
//            }
//        });
//        searchComboBox.getSearchComboBoxSkin().getSearchBox().setOnKeyReleased(t -> {
//            if (!Tools.isText(searchComboBox.getSearchComboBoxSkin().getSearchBox().getText())) {
//                searchComboBox.getComboBox().getItems().clear();
//                List<InsumoTB> insumoTBss = InsumoADO.getSearchComboBoxInsumos(searchComboBox.getSearchComboBoxSkin().getSearchBox().getText().trim());
//                insumoTBss.forEach(p -> searchComboBox.getComboBox().getItems().add(p));
//            }
//        });
//        searchComboBox.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
//            switch (t.getCode()) {
//                case ENTER:
//                case SPACE:
//                case ESCAPE:
//                    searchComboBox.getComboBox().hide();
//                    break;
//                case UP:
//                case DOWN:
//                case LEFT:
//                case RIGHT:
//                    break;
//                default:
//                    searchComboBox.getSearchComboBoxSkin().getSearchBox().requestFocus();
//                    searchComboBox.getSearchComboBoxSkin().getSearchBox().selectAll();
//                    break;
//            }
//        });
//        searchComboBox.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
//            if (item != null) {
//                searchComboBox.getComboBox().getSelectionModel().select(item);
//                if (searchComboBox.getSearchComboBoxSkin().isClickSelection()) {
//                    searchComboBox.getComboBox().hide();
//                }
//            }
//        });
//        insumoTB.setSearchComboBox(searchComboBox);
//
//        TextField textField = new TextField(Tools.roundingValue(Double.parseDouble(txtCantidad.getText()), 2));
//        textField.setPromptText("0.00");
//        textField.getStyleClass().add("text-field-normal");
//        textField.setPrefWidth(220);
//        textField.setPrefHeight(30);
//        textField.setOnKeyTyped(event -> {
//            char c = event.getCharacter().charAt(0);
//            if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
//                event.consume();
//            }
//            if (c == '.' && textField.getText().contains(".")) {
//                event.consume();
//            }
//        });
//        insumoTB.setTxtCantidad(textField);
//
//        Button button = new Button();
//        button.getStyleClass().add("buttonLightError");
//        button.setAlignment(Pos.CENTER);
//        button.setPrefWidth(Control.USE_COMPUTED_SIZE);
//        button.setPrefHeight(Control.USE_COMPUTED_SIZE);
//        ImageView imageView = new ImageView(new Image("/view/image/remove-gray.png"));
//        imageView.setFitWidth(20);
//        imageView.setFitHeight(20);
//        button.setGraphic(imageView);
//        button.setOnAction(event -> {
//            insumoTBs.remove(insumoTB);
//            addElementPaneHead();
//            addElementPaneBody();
//        });
//        button.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.ENTER) {
//                insumoTBs.remove(insumoTB);
//                addElementPaneHead();
//                addElementPaneBody();
//            }
//        });
//        insumoTB.setBtnRemove(button);
//        insumoTBs.add(insumoTB);
//        addElementPaneHead();
//        addElementPaneBody();
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

    public void clearComponentes() {
        Tools.actualDate(Tools.getDate(), txtFechaInicio);
        suministroTBs.clear();
        addElementPaneHead();
        addElementPaneBody();
        txtDias.clear();
        txtHoras.clear();
        txtMinutos.clear();
        cbProducto.getItems().clear();
        cbProducto.getSelectionModel().select(null);
        cbInterno.setSelected(true);
        cbPersonaEncargada.getItems().clear();
        cbPersonaEncargada.getSelectionModel().select(null);
        txtGlosaDescriptiva.clear();
        txtCantidad.clear();
    }

    public void closeWindow() {
        fxPrincipalController.getVbContent().getChildren().remove(apWindow);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(producirController.getWindow(), 0d);
        AnchorPane.setTopAnchor(producirController.getWindow(), 0d);
        AnchorPane.setRightAnchor(producirController.getWindow(), 0d);
        AnchorPane.setBottomAnchor(producirController.getWindow(), 0d);
        fxPrincipalController.getVbContent().getChildren().add(producirController.getWindow());
    }

    private void modalEstado(ProduccionTB produccionTB) {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_MODAL_ESTADO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());

            FXModalEstadoController controller = fXMLLoader.getController();
            controller.setInitControllerProducirAgregar(this);
            controller.setProduccionTB(produccionTB);

            Stage stage = WindowStage.StageLoaderModal(parent, "Confirmacion de Producción", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();

        } catch (IOException ex) {
            System.out.println("Controller modal estado" + ex.getLocalizedMessage());
        }
    }

    private void registrarProduccion() {
        if (txtFechaInicio.getValue() == null) {
            Tools.AlertMessageWarning(apWindow, "Producción", "Ingrese la fecha de inicio.");
            tpContenedor.getSelectionModel().select(0);
            txtFechaInicio.requestFocus();
        } else if (cbPersonaEncargada.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(apWindow, "Producción", "Seleccione el encargado de la producción.");
            tpContenedor.getSelectionModel().select(0);
            cbPersonaEncargada.requestFocus();
        } else if (cbProducto.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(apWindow, "Producción", "Seleccione el producto a fabricar.");
            tpContenedor.getSelectionModel().select(1);
            cbProducto.requestFocus();
        } else if (!Tools.isNumeric(txtCantidad.getText())) {
            Tools.AlertMessageWarning(apWindow, "Producción", "Ingrese la cantidad a producir.");
            tpContenedor.getSelectionModel().select(1);
            txtCantidad.requestFocus();
        } else {
            int cantidad = 0;
            int insumo = 0;
//            for (InsumoTB insumoTB : insumoTBs) {
//                if (Tools.isNumeric(insumoTB.getTxtCantidad().getText()) && Double.parseDouble(insumoTB.getTxtCantidad().getText()) > 0) {
//                    cantidad += 0;
//                } else {
//                    cantidad += 1;
//                }
//
//                if (insumoTB.getSearchComboBox().getComboBox().getSelectionModel().getSelectedIndex() >= 0) {
//                    insumo += 0;
//                } else {
//                    insumo += 1;
//                }
//
//            }

            if (cantidad > 0) {
                Tools.AlertMessageWarning(apWindow, "Producción", "Hay cantidades en 0 en la lista de insumos.");
                tpContenedor.getSelectionModel().select(1);
            } else if (insumo > 0) {
                Tools.AlertMessageWarning(apWindow, "Producción", "No hay insumos seleccionados en la lista.");
                tpContenedor.getSelectionModel().select(1);
            } else {
                ProduccionTB produccionTB = new ProduccionTB();
                produccionTB.setFechaInicio(Tools.getDatePicker(txtFechaInicio));
                produccionTB.setHoraInicio(Tools.getHour());
                produccionTB.setDias(!Tools.isNumericInteger(txtDias.getText()) ? 1 : Integer.parseInt(txtDias.getText()));
                produccionTB.setHoras(!Tools.isNumericInteger(txtHoras.getText()) ? 0 : Integer.parseInt(txtHoras.getText()));
                produccionTB.setMinutos(!Tools.isNumericInteger(txtMinutos.getText()) ? 0 : Integer.parseInt(txtMinutos.getText()));
                produccionTB.setIdProducto(cbProducto.getSelectionModel().getSelectedItem().getIdSuministro());
                produccionTB.setTipoOrden(cbInterno.isSelected());
                produccionTB.setIdEncargado(cbPersonaEncargada.getSelectionModel().getSelectedItem().getIdEmpleado());
                produccionTB.setDescripcion(txtGlosaDescriptiva.getText());
                produccionTB.setFechaRegistro(Tools.getDate());
                produccionTB.setHoraRegistro(Tools.getHour());
                produccionTB.setCantidad(Double.parseDouble(txtCantidad.getText()));
//                produccionTB.setEstado(1);
//                produccionTB.setInsumoTBs(insumoTBs);

                modalEstado(produccionTB);

            }
        }
    }

    public void selectFormula(String idFormula) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<Object> task = new Task<Object>() {
            @Override
            public Object call() {
                return FormulaADO.Obtener_Formula_ById(idFormula, Double.parseDouble(txtCantidad.getText()));
            }
        };
        task.setOnSucceeded(w -> {
            Object object = task.getValue();

            if (object instanceof FormulaTB) {
                FormulaTB formulaTB = (FormulaTB) object;
//                insumoTBs.addAll(formulaTB.getInsumoTBs());
//                for (InsumoTB insumoTB : insumoTBs) {
//                    insumoTB.getBtnRemove().setOnAction(event -> {
//                        insumoTBs.remove(insumoTB);
//                        addElementPaneHead();
//                        addElementPaneBody();
//                    });
//                    insumoTB.getBtnRemove().setOnKeyPressed(event -> {
//                        if (event.getCode() == KeyCode.ENTER) {
//                            insumoTBs.remove(insumoTB);
//                            addElementPaneHead();
//                            addElementPaneBody();
//                        }
//                    });
//                }
                addElementPaneHead();
                addElementPaneBody();
                hbLoad.setVisible(false);
                vbBody.setDisable(false);
            } else if (object instanceof String) {
                lblMessageLoad.setGraphic(null);
                lblMessageLoad.setText((String) object);
                lblMessageLoad.setTextFill(Color.web("#ff6d6d"));
                btnAceptarLoad.setVisible(true);
            } else {
                lblMessageLoad.setGraphic(null);
                lblMessageLoad.setText("Se produjo un error interno, comuníquese con su proveedor del sistema.");
                lblMessageLoad.setTextFill(Color.web("#ff6d6d"));
                btnAceptarLoad.setVisible(true);
            }
        });
        task.setOnFailed(w -> {
            lblMessageLoad.setGraphic(null);
            lblMessageLoad.setText(task.getMessage());
            lblMessageLoad.setTextFill(Color.web("#ff6d6d"));
            btnAceptarLoad.setVisible(true);
        });
        task.setOnScheduled(w -> {
            hbLoad.setVisible(true);
            vbBody.setDisable(true);
            btnAceptarLoad.setVisible(false);
            lblMessageLoad.setText("Cargando datos...");
            lblMessageLoad.setTextFill(Color.web("#ffffff"));
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void openWindowFormulas(String title, String idProducto, boolean valor) {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_FORMULA_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FXFormulaListaController controller = fXMLLoader.getController();
            controller.setProducirAgregarController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, title, apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
            if (valor == false) {
                controller.initListFormulas(idProducto, "");
            }
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
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
            clearComponentes();
        }
    }

    @FXML
    private void onActionLimpiar(ActionEvent event) {
        clearComponentes();
    }

    @FXML
    private void onKeyPressedAgregar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (cbProducto.getSelectionModel().getSelectedIndex() >= 0) {
                if (!txtCantidad.getText().equalsIgnoreCase("") && Double.parseDouble(txtCantidad.getText()) > 0) {
                    addElementsTableInsumo();
                } else {
                    Tools.AlertMessageWarning(apWindow, "Formula", "Ingrese la cantidad de productos a producir y que sea mayor a 0");
                    txtCantidad.requestFocus();
                }
            } else {
                Tools.AlertMessageWarning(apWindow, "Formula", "Seleccione un producto para añadir insumos a la producción");
                cbProducto.requestFocus();
            }
        }
    }

    @FXML
    private void onActonAgregar(ActionEvent event) {
        if (cbProducto.getSelectionModel().getSelectedIndex() >= 0) {
            if (!txtCantidad.getText().equalsIgnoreCase("") && Double.parseDouble(txtCantidad.getText()) > 0) {
                addElementsTableInsumo();
            } else {
                Tools.AlertMessageWarning(apWindow, "Formula", "Ingrese la cantidad de productos a producir y que sea mayor a 0");
                txtCantidad.requestFocus();
            }
        } else {
            Tools.AlertMessageWarning(apWindow, "Formula", "Seleccione un producto para añadir insumos a la producción");
            cbProducto.requestFocus();
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
    private void onMouseClickedFormula(MouseEvent event) {
        if (event.getClickCount() == 1) {
            if (cbProducto.getSelectionModel().getSelectedIndex() >= 0) {
                if (!txtCantidad.getText().equalsIgnoreCase("") && Double.parseDouble(txtCantidad.getText()) > 0) {
                    String idSuministro = cbProducto.getSelectionModel().getSelectedItem().getIdSuministro();
                    openWindowFormulas("Agregar Formula", idSuministro, false);
                } else {
                    Tools.AlertMessageWarning(apWindow, "Formula", "Ingrese la cantidad de productos a producir y que sea mayor a 0");
                    txtCantidad.requestFocus();
                }
            } else {
                Tools.AlertMessageWarning(apWindow, "Formula", "Seleccione un producto para asociar una formula");
                cbProducto.requestFocus();
            }
        }
    }

    @FXML
    private void onKeyPressedAceptarLoad(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            hbLoad.setVisible(false);
            vbBody.setDisable(false);
        }
    }

    @FXML
    private void onActionAceptarLoad(ActionEvent event) {
        hbLoad.setVisible(false);
        vbBody.setDisable(false);
    }

    public void setInitControllerProducir(FxProducirController producirController, FxPrincipalController fxPrincipalController) {
        this.producirController = producirController;
        this.fxPrincipalController = fxPrincipalController;
    }

}
