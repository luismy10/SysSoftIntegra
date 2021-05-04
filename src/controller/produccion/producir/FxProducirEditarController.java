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
import model.ProduccionADO;
import model.ProduccionTB;
import model.SuministroADO;
import model.SuministroTB;

public class FxProducirEditarController implements Initializable {

    @FXML
    private DatePicker txtFechaInicio;
    @FXML
    private RadioButton cbInterno;
    @FXML
    private RadioButton cbExterno;
    @FXML
    private AnchorPane apWindow;
    @FXML
    private VBox vbBody;
    @FXML
    private TextField txtDias;
    @FXML
    private TextField txtHoras;
    @FXML
    private TextField txtMinutos;
    @FXML
    private TabPane tpContenedor;
    @FXML
    private ComboBox<EmpleadoTB> cbPersonaEncargada;
    @FXML
    private TextArea txtGlosaDescriptiva;
    @FXML
    private ComboBox<SuministroTB> cbProducto;
    @FXML
    private TextField txtCantidad;
    @FXML
    private GridPane gpList;
    @FXML
    private HBox hbLoad;
    @FXML
    private Label lblMessageLoad;
    @FXML
    private Button btnAceptarLoad;

    private FxPrincipalController fxPrincipalController;

    private FxProducirController producirController;

    private String idProduccion;

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

    public void closeWindow() {
        fxPrincipalController.getVbContent().getChildren().remove(apWindow);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(producirController.getWindow(), 0d);
        AnchorPane.setTopAnchor(producirController.getWindow(), 0d);
        AnchorPane.setRightAnchor(producirController.getWindow(), 0d);
        AnchorPane.setBottomAnchor(producirController.getWindow(), 0d);
        fxPrincipalController.getVbContent().getChildren().add(producirController.getWindow());
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

    private void addElementPaneHead() {
        gpList.getChildren().clear();
        gpList.getColumnConstraints().get(0).setMinWidth(10);
        gpList.getColumnConstraints().get(0).setPrefWidth(40);
        gpList.getColumnConstraints().get(0).setHgrow(Priority.SOMETIMES);

        gpList.getColumnConstraints().get(1).setMinWidth(10);
        gpList.getColumnConstraints().get(1).setPrefWidth(340);
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

        gpList.getColumnConstraints().get(5).setMinWidth(10);
        gpList.getColumnConstraints().get(5).setPrefWidth(60);
        gpList.getColumnConstraints().get(5).setHgrow(Priority.SOMETIMES);
        gpList.getColumnConstraints().get(5).setHalignment(HPos.CENTER);

        gpList.getColumnConstraints().get(6).setMinWidth(10);
        gpList.getColumnConstraints().get(6).setPrefWidth(60);
        gpList.getColumnConstraints().get(6).setHgrow(Priority.SOMETIMES);
        gpList.getColumnConstraints().get(6).setHalignment(HPos.CENTER);

        gpList.getColumnConstraints().get(7).setMinWidth(10);
        gpList.getColumnConstraints().get(7).setPrefWidth(60);
        gpList.getColumnConstraints().get(7).setHgrow(Priority.SOMETIMES);
        gpList.getColumnConstraints().get(7).setHalignment(HPos.CENTER);

        gpList.add(addElementGridPaneLabel("l01", "N°"), 0, 0);
        gpList.add(addElementGridPaneLabel("l02", "Insumo"), 1, 0);
        gpList.add(addElementGridPaneLabel("l03", "Cantidad Formula"), 2, 0);
        gpList.add(addElementGridPaneLabel("l04", "Cantidad Utilizada"), 3, 0);
        gpList.add(addElementGridPaneLabel("l05", "Cantidad a Utilizar"), 4, 0);
        gpList.add(addElementGridPaneLabel("l06", "Merma (Sobrante)"), 5, 0);
        gpList.add(addElementGridPaneLabel("l06", "Medida"), 6, 0);
        gpList.add(addElementGridPaneLabel("l07", "Quitar"), 7, 0);
    }

    private void addElementPaneBody() {
        for (int i = 0; i < suministroTBs.size(); i++) {
//            gpList.add(addElementGridPaneLabel("l1" + (i + 1), (i + 1) + "", Pos.CENTER), 0, (i + 1));
//            gpList.add(insumoTBs.get(i).getComboBox(), 1, (i + 1));
//            gpList.add(insumoTBs.get(i).getTxtCantidad(), 2, (i + 1));
//            gpList.add(insumoTBs.get(i).getTxtCantidadUtilizada(), 3, (i + 1));
//            gpList.add(insumoTBs.get(i).getTxtCantidadAUtilizar(), 4, (i + 1));
//            gpList.add(insumoTBs.get(i).getTxtMerma(), 5, (i + 1));
//            gpList.add(addElementGridPaneLabel("l4" + (i + 1), "UNIDAD", Pos.CENTER), 6, (i + 1));
//            gpList.add(insumoTBs.get(i).getBtnRemove(), 7, (i + 1));
        }
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
//        insumoTB.setCosto(0);
//        insumoTB.setCantidad(1);
//        insumoTB.setCantidadAUtilizar(0);
//        insumoTB.setMerma(0);
//        insumoTB.setCantidadUtilizada(Double.parseDouble(txtCantidad.getText()));
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
//        TextField textField = new TextField(Tools.roundingValue(insumoTB.getCantidad(), 2));
//        textField.setDisable(true);
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
//        TextField textFieldCU = new TextField(Tools.roundingValue(insumoTB.getCantidadUtilizada(), 2));
//        textFieldCU.setDisable(true);
//        textFieldCU.setPromptText("0.00");
//        textFieldCU.getStyleClass().add("text-field-normal");
//        textFieldCU.setPrefWidth(220);
//        textFieldCU.setPrefHeight(30);
//        textFieldCU.setOnKeyTyped(event -> {
//            char c = event.getCharacter().charAt(0);
//            if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
//                event.consume();
//            }
//            if (c == '.' && textFieldCU.getText().contains(".")) {
//                event.consume();
//            }
//        });
//        insumoTB.setTxtCantidadUtilizada(textFieldCU);
//
//        TextField textFieldCaU = new TextField(Tools.roundingValue(insumoTB.getCantidadAUtilizar(), 2));
//        textFieldCaU.setPromptText("0.00");
//        textFieldCaU.getStyleClass().add("text-field-normal");
//        textFieldCaU.setPrefWidth(220);
//        textFieldCaU.setPrefHeight(30);
//        textFieldCaU.setOnKeyTyped(event -> {
//            char c = event.getCharacter().charAt(0);
//            if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
//                event.consume();
//            }
//            if (c == '.' && textFieldCaU.getText().contains(".") && (c != '.')) {
//                event.consume();
//            }
//        });
//        insumoTB.setTxtCantidadAUtilizar(textFieldCaU);
//
//        TextField textMerma = new TextField(Tools.roundingValue(insumoTB.getMerma(), 2));
//        textMerma.setPromptText("0.00");
//        textMerma.getStyleClass().add("text-field-normal");
//        textMerma.setPrefWidth(220);
//        textMerma.setPrefHeight(30);
//        textMerma.setOnKeyTyped(event -> {
//            char c = event.getCharacter().charAt(0);
//            if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
//                event.consume();
//            }
//            if (c == '.' && textMerma.getText().contains(".") && (c != '.')) {
//                event.consume();
//            }
//        });
//        insumoTB.setTxtMerma(textMerma);
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
//
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

    public void editarProduccion(String idProduccion) {
        this.idProduccion = idProduccion;
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<Object> task = new Task<Object>() {
            @Override
            protected Object call() {
                return ProduccionADO.Obtener_Produccion_ById(idProduccion);
            }
        };

        task.setOnScheduled(w -> {
            hbLoad.setVisible(true);
            vbBody.setDisable(true);
        });
        task.setOnFailed(w -> {
            lblMessageLoad.setGraphic(null);
            lblMessageLoad.setText(task.getMessage());
            lblMessageLoad.setTextFill(Color.web("ea4242"));
        });

        task.setOnSucceeded(w -> {
            Object object = task.getValue();
            if (object instanceof ProduccionTB) {
                ProduccionTB produccionTB = (ProduccionTB) object;

                Tools.actualDate(produccionTB.getFechaInicio(), txtFechaInicio);
                txtDias.setText(Integer.toString(produccionTB.getDias()));
                txtHoras.setText(Integer.toString(produccionTB.getHoras()));
                txtMinutos.setText(Integer.toString(produccionTB.getMinutos()));
                cbInterno.setSelected(produccionTB.isTipoOrden());
                cbProducto.getItems().add(produccionTB.getSuministroTB());
                cbProducto.getSelectionModel().select(produccionTB.getSuministroTB());
                cbProducto.setDisable(true);
                cbPersonaEncargada.getItems().add(produccionTB.getEmpleadoTB());
                cbPersonaEncargada.getSelectionModel().select(produccionTB.getEmpleadoTB());
                cbPersonaEncargada.setDisable(true);
                txtGlosaDescriptiva.setText(produccionTB.getDescripcion());
                txtCantidad.setText(Tools.roundingValue(produccionTB.getCantidad(), 2));
//                insumoTBs.addAll(produccionTB.getInsumoTBs());
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
            } else {
                lblMessageLoad.setGraphic(null);
                lblMessageLoad.setText((String) object);
                lblMessageLoad.setTextFill(Color.web("ea4242"));
            }
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    public void modalEstado(ProduccionTB produccionTB) {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_MODAL_ESTADO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());

            FXModalEstadoController controller = fXMLLoader.getController();
            controller.setInitControllerProducirEditar(this);
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

    public void registrarProduccion() {
        if (txtFechaInicio.getValue() == null) {
            Tools.AlertMessageWarning(apWindow, "Producción", "Ingrese la fecha de inicio.");
            tpContenedor.getSelectionModel().select(0);
            txtFechaInicio.requestFocus();
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
                produccionTB.setDescripcion(txtGlosaDescriptiva.getText());
                produccionTB.setCantidad(Double.parseDouble(txtCantidad.getText()));
                produccionTB.setIdProduccion(idProduccion);
//                produccionTB.setInsumoTBs(insumoTBs);

                modalEstado(produccionTB);
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
    private void onKeyPressedAgregar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            addElementsTableInsumo();
        }
    }

    @FXML
    private void onActonAgregar(ActionEvent event) {
        addElementsTableInsumo();
    }

    @FXML
    private void onKeyPressedAceptarLoad(KeyEvent event) {
    }

    @FXML
    private void onActionAceptarLoad(ActionEvent event) {
    }

    public void setInitControllerProducir(FxProducirController producirController) {
        this.producirController = producirController;
    }

    public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }

}
