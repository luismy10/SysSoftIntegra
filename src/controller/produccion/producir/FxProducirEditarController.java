package controller.produccion.producir;

import controller.menus.FxPrincipalController;
import controller.tools.FilesRouters;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.ProduccionADO;
import model.ProduccionTB;
import model.SuministroADO;
import model.SuministroTB;

public class FxProducirEditarController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private Text lblCantidadProducir;
    @FXML
    private Text lblProducto;
    @FXML
    private Text lblEncargado;
    @FXML
    private Text lblFechaCreacion;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private Button btnAgregar;
    @FXML
    private GridPane gpList;
    @FXML
    private VBox vbBody;
    @FXML
    private HBox hbLoad;
    @FXML
    private Label lblMessageLoad;
    @FXML
    private Button btnAceptarLoad;
    @FXML
    private TextField txtDias;
    @FXML
    private TextField txtHoras;
    @FXML
    private TextField txtMinutos;

    private FxPrincipalController fxPrincipalController;

    private FxProducirController producirController;

    private String idProduccion;

    private ArrayList<SuministroTB> suministroTBs;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        suministroTBs = new ArrayList();
        addElementPaneHead();
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
                return ProduccionADO.Obtener_Produccion_Editor_ById(idProduccion);
            }
        };

        task.setOnScheduled(w -> {
            vbBody.setDisable(true);
            hbLoad.setVisible(true);
            lblMessageLoad.setText("Cargando datos...");
            lblMessageLoad.setTextFill(Color.web("#ffffff"));
        });
        task.setOnFailed(w -> {
            lblMessageLoad.setText(task.getMessage());
            lblMessageLoad.setTextFill(Color.web("ea4242"));
            btnAceptarLoad.setOnAction(event -> {
                closeWindow();
            });
            btnAceptarLoad.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    closeWindow();
                }
                event.consume();
            });
        });

        task.setOnSucceeded(w -> {
            Object object = task.getValue();
            if (object instanceof ProduccionTB) {
                ProduccionTB produccionTB = (ProduccionTB) object;
                lblProducto.setText(produccionTB.getSuministroTB().getClave() + " - " + produccionTB.getSuministroTB().getNombreMarca());
                lblCantidadProducir.setText(Tools.roundingValue(produccionTB.getCantidad(), 2) + " " + produccionTB.getSuministroTB().getUnidadCompraName());
                lblEncargado.setText(produccionTB.getEmpleadoTB().getApellidos() + " " + produccionTB.getEmpleadoTB().getNombres());
                lblFechaCreacion.setText(produccionTB.getFechaRegistro() + " -" + produccionTB.getHoraRegistro());
                for (SuministroTB suministroTB : produccionTB.getSuministroInsumos()) {
                    suministroTB.getBtnRemove().setOnAction(event -> {
                        suministroTBs.remove(suministroTB);
                        addElementPaneHead();
                        addElementPaneBody();
                    });
                    suministroTB.getBtnRemove().setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.ENTER) {
                            suministroTBs.remove(suministroTB);
                            addElementPaneHead();
                            addElementPaneBody();
                        }
                    });
                    suministroTBs.add(suministroTB);
                }
                addElementPaneHead();
                addElementPaneBody();
                vbBody.setDisable(false);
                hbLoad.setVisible(false);
            } else {
                lblMessageLoad.setText((String) object);
                lblMessageLoad.setTextFill(Color.web("ea4242"));
                btnAceptarLoad.setOnAction(event -> {
                    closeWindow();
                });
                btnAceptarLoad.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        closeWindow();
                    }
                    event.consume();
                });
            }
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void addElementsTableSuministro() {
        SuministroTB suministroTB = new SuministroTB();
        suministroTB.setCantidad(0);
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
                List<SuministroTB> suministroTBs = SuministroADO.getSearchComboBoxSuministros(searchComboBox.getSearchComboBoxSkin().getSearchBox().getText().trim(), false);
                suministroTBs.forEach(p -> suministroTB.getCbSuministro().getItems().add(p));
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

        gpList.getColumnConstraints().get(5).setMinWidth(10);
        gpList.getColumnConstraints().get(5).setPrefWidth(60);
        gpList.getColumnConstraints().get(5).setHgrow(Priority.SOMETIMES);
        gpList.getColumnConstraints().get(5).setHalignment(HPos.CENTER);

        gpList.add(addElementGridPaneLabel("l01", "N°"), 0, 0);
        gpList.add(addElementGridPaneLabel("l02", "Insumo"), 1, 0);
        gpList.add(addElementGridPaneLabel("l03", "Cantidad Utilizada"), 2, 0);
        gpList.add(addElementGridPaneLabel("l04", "Cantidad a Utilizar"), 3, 0);
        gpList.add(addElementGridPaneLabel("l05", "Medida"), 4, 0);
        gpList.add(addElementGridPaneLabel("l06", "Quitar"), 5, 0);
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
            gpList.add(addElementGridPaneLabel("l3", Tools.roundingValue(suministroTBs.get(i).getCantidad(), 2), Pos.CENTER), 2, (i + 1));
            gpList.add(suministroTBs.get(i).getTxtCantidad(), 3, (i + 1));
            gpList.add(addElementGridPaneLabel("l6" + (i + 1), "MEDIDA", Pos.CENTER), 4, (i + 1));
            gpList.add(suministroTBs.get(i).getBtnRemove(), 5, (i + 1));
        }
    }

    private void modalEstado(ProduccionTB produccionTB) {
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

    private void onEventEditar() {
        if (suministroTBs.isEmpty()) {
            Tools.AlertMessageWarning(apWindow, "Producción", "No hay matería prima para producir.");
            btnAgregar.requestFocus();
        } else {
            int cantidad = 0;
            int producto = 0;
            for (SuministroTB suministroTB : suministroTBs) {
                if (Tools.isNumeric(suministroTB.getTxtCantidad().getText()) && Double.parseDouble(suministroTB.getTxtCantidad().getText()) > 0) {
                    cantidad += 0;
                } else {
                    cantidad += 1;
                }
                if (suministroTB.getCbSuministro().getSelectionModel().getSelectedIndex() >= 0) {
                    producto += 0;
                } else {
                    producto += 1;
                }
            }

            if (cantidad > 0) {
                Tools.AlertMessageWarning(apWindow, "Producción", "Hay cantidades en 0 en la lista de insumos.");
            } else if (producto > 0) {
                Tools.AlertMessageWarning(apWindow, "Producción", "No hay insumos seleccionados en la lista.");
            } else {
                int duplicate = 0;
                ArrayList<SuministroTB> newSuministroTBs = new ArrayList();
                for (SuministroTB suministroTB : suministroTBs) {
                    if (validateDuplicateInsumo(newSuministroTBs, suministroTB)) {
                        duplicate += 1;
                    } else {
                        newSuministroTBs.add(suministroTB);
                        duplicate += 0;
                    }
                }
                if (duplicate > 0) {
                    Tools.AlertMessageWarning(apWindow, "Producción", "Hay insumos duplicados en la lista.");
                } else {
                    ProduccionTB produccionTB = new ProduccionTB();
                    produccionTB.setIdProduccion(idProduccion);
                    produccionTB.setFechaRegistro(Tools.getDate());
                    produccionTB.setHoraRegistro(Tools.getTime());
                    produccionTB.setDias(Tools.isNumericInteger(txtDias.getText()) ? Integer.parseInt(txtDias.getText()) : 0);
                    produccionTB.setHoras(Tools.isNumericInteger(txtHoras.getText()) ? Integer.parseInt(txtHoras.getText()) : 0);
                    produccionTB.setMinutos(Tools.isNumericInteger(txtMinutos.getText()) ? Integer.parseInt(txtMinutos.getText()) : 0);
                    produccionTB.setDescripcion(txtDescripcion.getText().trim());
                    produccionTB.setSuministroInsumos(newSuministroTBs);
                    modalEstado(produccionTB);
                }
            }

        }
    }

    public void executeEdicion(ProduccionTB produccionTB) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<String> task = new Task<String>() {
            @Override
            protected String call() {
                return ProduccionADO.Crud_Produccion(produccionTB);
            }
        };
        task.setOnScheduled(w -> {
            vbBody.setDisable(true);
            hbLoad.setVisible(true);
            btnAceptarLoad.setVisible(false);
            lblMessageLoad.setText("Procesando información...");
            lblMessageLoad.setTextFill(Color.web("#ffffff"));
        });
        task.setOnFailed(w -> {
            btnAceptarLoad.setVisible(true);
            btnAceptarLoad.setOnAction(event -> {
                closeWindow();
            });
            btnAceptarLoad.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    closeWindow();
                }
            });
            lblMessageLoad.setText(task.getException().getLocalizedMessage());
            lblMessageLoad.setTextFill(Color.web("#ff6d6d"));
        });
        task.setOnSucceeded(w -> {
            String result = task.getValue();
            if (result.equalsIgnoreCase("updated")) {
                lblMessageLoad.setText("Se actualizó correctamente la producción.");
                lblMessageLoad.setTextFill(Color.web("#ffffff"));
                btnAceptarLoad.setVisible(true);
                btnAceptarLoad.setOnAction(event -> {
                    closeWindow();
                });
                btnAceptarLoad.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        closeWindow();
                    }
                });
            } else if (result.equalsIgnoreCase("state")) {
                lblMessageLoad.setText("La producción se encuentra anulada o terminada.");
                lblMessageLoad.setTextFill(Color.web("#ff6d6d"));
                btnAceptarLoad.setVisible(true);
                btnAceptarLoad.setOnAction(event -> {
                    closeWindow();
                });
                btnAceptarLoad.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        closeWindow();
                    }
                });
            } else {
                lblMessageLoad.setText(result);
                lblMessageLoad.setTextFill(Color.web("#ff6d6d"));
                btnAceptarLoad.setVisible(true);
                btnAceptarLoad.setOnAction(event -> {
                    closeWindow();
                });
                btnAceptarLoad.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        closeWindow();
                    }
                });
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

    private boolean validateDuplicateInsumo(ArrayList<SuministroTB> view, SuministroTB suministroTB) {
        boolean ret = false;
        for (SuministroTB sm : view) {
            if (sm.getCbSuministro().getSelectionModel().getSelectedItem().getIdSuministro()
                    .equals(suministroTB.getCbSuministro().getSelectionModel().getSelectedItem().getIdSuministro())) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        closeWindow();
    }

    @FXML
    private void onKeyPressedEditar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventEditar();
        }
    }

    @FXML
    private void onActionEditar(ActionEvent event) {
        onEventEditar();
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
