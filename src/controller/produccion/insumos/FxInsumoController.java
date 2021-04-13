package controller.produccion.insumos;

import controller.menus.FxPrincipalController;
import controller.tools.FilesRouters;
import controller.tools.SearchComboBox;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.ClasesTB;
import model.DetalleADO;
import model.DetalleTB;
import model.InsumoADO;
import model.InsumoTB;

public class FxInsumoController implements Initializable {

    @FXML
    private HBox hbWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private TableView<InsumoTB> tvList;
    @FXML
    private TableColumn<InsumoTB, Integer> tcNumero;
    @FXML
    private TableColumn<InsumoTB, String> tcDescripcion;
    @FXML
    private TableColumn<InsumoTB, String> tcCosto;
    @FXML
    private TableColumn<InsumoTB, String> tcClase;
    @FXML
    private TableColumn<InsumoTB, String> tcSubClase;
    @FXML
    private TableColumn<InsumoTB, String> tcSubSubClase;
    @FXML
    private TableColumn<InsumoTB, String> tcCantidad;
    @FXML
    private TableColumn<InsumoTB, String> tcUnidad;
    @FXML
    private TableColumn<InsumoTB, String> tcCategoria;
    @FXML
    private TextField txtBuscar;
    @FXML
    private Label lblPaginaActual;
    @FXML
    private Label lblPaginaSiguiente;
    @FXML
    private ComboBox<DetalleTB> cbCategoria;
    @FXML
    private ComboBox<ClasesTB> cbClase;
    @FXML
    private ComboBox<ClasesTB> cbSubClase;
    @FXML
    private ComboBox<ClasesTB> cbSubSubClase;

    private FxPrincipalController fxPrincipalController;

    private int paginacion;

    private int totalPaginacion;

    private short opcion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hbWindow.setOnKeyReleased((KeyEvent event) -> {
            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case F1:

                        break;
                    case F5:

                        break;
                }
            }
        });

        tcNumero.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        tcDescripcion.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getClave() + "\n"
                + cellData.getValue().getNombreMarca()
        ));
        tcCosto.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getCosto(), 2)
        ));
        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getCantidad(), 2)
        ));
        tcUnidad.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getMedidaName()
        ));
        tcCategoria.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getCategoriaName()
        ));
        tcClase.setCellValueFactory((cellData) -> Bindings.concat(
                cellData.getValue().getNombreClase()
        ));
        tcSubClase.setCellValueFactory((cellData) -> Bindings.concat(
                cellData.getValue().getNombreSubClase()
        ));
        tcSubSubClase.setCellValueFactory((cellData) -> Bindings.concat(
                cellData.getValue().getNombreSubSubClase()
        ));

        tcNumero.prefWidthProperty().bind(tvList.widthProperty().multiply(0.03));
        tcDescripcion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.21));
        tcCosto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.07));
        tcClase.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        tcSubClase.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        tcSubSubClase.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        tcCantidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.07));
        tcUnidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.07));
        tcCategoria.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        tvList.setPlaceholder(loadMessageTableView("No hay datos para mostrar", "-fx-text-fill:#020203;", "labelRoboto13", false));

        paginacion = 1;
        opcion = 0;
        //filtersComboBox();
    }

    public void loadInitComponents() {
        if (!lblLoad.isVisible()) {
            paginacion = 1;
            fillTableInsumos((short) 0, "", 0, "", "", "");
            opcion = 0;

        }
    }

    private void filtersComboBox() {

        SearchComboBox<DetalleTB> searchComboBoxCategoria = new SearchComboBox<>(cbCategoria, true);
        searchComboBoxCategoria.setFilter((item, text) -> item.getNombre().toLowerCase().contains(text.toLowerCase()));
        searchComboBoxCategoria.getComboBox().getItems().addAll(DetalleADO.GetDetailId("0006"));
        searchComboBoxCategoria.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
            if (null == t.getCode()) {
                searchComboBoxCategoria.getSearchComboBoxSkin().getSearchBox().requestFocus();
                searchComboBoxCategoria.getSearchComboBoxSkin().getSearchBox().selectAll();
            } else {
                switch (t.getCode()) {
                    case ENTER:
                    case SPACE:
                    case ESCAPE:
                        cbCategoria.getSelectionModel().select(null);
                        searchComboBoxCategoria.getComboBox().hide();
                        break;
                    case UP:
                    case DOWN:
                    case LEFT:
                    case RIGHT:
                        break;
                    default:
                        searchComboBoxCategoria.getSearchComboBoxSkin().getSearchBox().requestFocus();
                        searchComboBoxCategoria.getSearchComboBoxSkin().getSearchBox().selectAll();
                        break;
                }
            }
        });

        searchComboBoxCategoria.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                searchComboBoxCategoria.getComboBox().getSelectionModel().select(item);
                if (searchComboBoxCategoria.getSearchComboBoxSkin().isClickSelection()) {
                    if (!lblLoad.isVisible()) {
                        cbClase.getSelectionModel().select(null);
                        cbSubClase.getSelectionModel().select(null);
                        cbSubSubClase.getSelectionModel().select(null);
                        if (txtBuscar.getText().trim().equalsIgnoreCase("")) {
                            paginacion = 1;
                            fillTableInsumos((short) 2, "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), "", "", "");

                            opcion = 2;
                        } else {
                            paginacion = 1;
                            fillTableInsumos((short) 3, txtBuscar.getText().trim(), cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), "", "", "");
                            opcion = 3;
                        }
                    }
                    searchComboBoxCategoria.getComboBox().hide();
                }
            }
        });

        SearchComboBox<ClasesTB> searchComboBoxClases = new SearchComboBox<>(cbClase, true);
        searchComboBoxClases.setFilter((item, text) -> item.getNombreClase().get().toLowerCase().contains(text.toLowerCase()));
        searchComboBoxClases.getComboBox().getItems().addAll(DetalleADO.GetDetailClass("clase"));
        searchComboBoxClases.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
            if (null == t.getCode()) {
                searchComboBoxClases.getSearchComboBoxSkin().getSearchBox().requestFocus();
                searchComboBoxClases.getSearchComboBoxSkin().getSearchBox().selectAll();
            } else {
                switch (t.getCode()) {
                    case ENTER:
                    case SPACE:
                    case ESCAPE:
                        cbClase.getSelectionModel().select(null);
                        searchComboBoxCategoria.getComboBox().hide();
                        break;
                    case UP:
                    case DOWN:
                    case LEFT:
                    case RIGHT:
                        break;
                    default:
                        searchComboBoxClases.getSearchComboBoxSkin().getSearchBox().requestFocus();
                        searchComboBoxClases.getSearchComboBoxSkin().getSearchBox().selectAll();
                        break;
                }
            }
        });

        searchComboBoxClases.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                searchComboBoxClases.getComboBox().getSelectionModel().select(item);
                if (searchComboBoxClases.getSearchComboBoxSkin().isClickSelection()) {
                    if (!lblLoad.isVisible()) {
                        cbSubClase.getSelectionModel().select(null);
                        cbSubSubClase.getSelectionModel().select(null);
                        if (txtBuscar.getText().trim().equalsIgnoreCase("") && cbCategoria.getSelectionModel().getSelectedIndex() < 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 4, "", 0, cbClase.getSelectionModel().getSelectedItem().getIdClase(), "", "");
                            opcion = 4;
                        } else if (txtBuscar.getText().trim().equalsIgnoreCase("") && cbCategoria.getSelectionModel().getSelectedIndex() >= 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 5, "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), cbClase.getSelectionModel().getSelectedItem().getIdClase(), "", "");
                            opcion = 5;
                        } else if (!txtBuscar.getText().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() >= 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 6, txtBuscar.getText().trim(), cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), cbClase.getSelectionModel().getSelectedItem().getIdClase(), "", "");
                            opcion = 6;
                        } else {
                            paginacion = 1;
                            fillTableInsumos((short) 7, txtBuscar.getText().trim(), 0, cbClase.getSelectionModel().getSelectedItem().getIdClase(), "", "");
                            opcion = 7;
                        }
                    }
                    searchComboBoxClases.getComboBox().hide();
                }
            }
        });

        SearchComboBox<ClasesTB> searchComboBoxSubClases = new SearchComboBox<>(cbSubClase, true);
        searchComboBoxSubClases.setFilter((item, text) -> item.getNombreClase().get().toLowerCase().contains(text.toLowerCase()));
        searchComboBoxSubClases.getComboBox().getItems().addAll(DetalleADO.GetDetailClass("sub-clase"));
        searchComboBoxSubClases.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
            if (null == t.getCode()) {
                searchComboBoxSubClases.getSearchComboBoxSkin().getSearchBox().requestFocus();
                searchComboBoxSubClases.getSearchComboBoxSkin().getSearchBox().selectAll();
            } else {
                switch (t.getCode()) {
                    case ENTER:
                    case SPACE:
                    case ESCAPE:
                        cbSubClase.getSelectionModel().select(null);
                        searchComboBoxSubClases.getComboBox().hide();
                        break;
                    case UP:
                    case DOWN:
                    case LEFT:
                    case RIGHT:
                        break;
                    default:
                        searchComboBoxSubClases.getSearchComboBoxSkin().getSearchBox().requestFocus();
                        searchComboBoxSubClases.getSearchComboBoxSkin().getSearchBox().selectAll();
                        break;
                }
            }
        });

        searchComboBoxSubClases.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                searchComboBoxSubClases.getComboBox().getSelectionModel().select(item);
                if (searchComboBoxSubClases.getSearchComboBoxSkin().isClickSelection()) {
                    if (!lblLoad.isVisible()) {
                        cbSubSubClase.getSelectionModel().select(null);
                        if (txtBuscar.getText().trim().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() < 0 && cbClase.getSelectionModel().getSelectedIndex() < 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 8, "", 0, "", cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), "");
                            opcion = 8;
                        } else if (txtBuscar.getText().trim().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() < 0 && !cbClase.getSelectionModel().getSelectedItem().getIdClase().equalsIgnoreCase("")) {
                            paginacion = 1;
                            fillTableInsumos((short) 9, "", 0, cbClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), "");
                            opcion = 9;
                        } else if (txtBuscar.getText().trim().equalsIgnoreCase("") && (cbCategoria.getSelectionModel().getSelectedIndex() >= 0) && cbClase.getSelectionModel().getSelectedIndex() >= 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 10, "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), cbClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), "");
                            opcion = 10;
                        } else if (!txtBuscar.getText().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() >= 0 && cbClase.getSelectionModel().getSelectedIndex() >= 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 11, txtBuscar.getText().trim(), cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), cbClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), "");
                            opcion = 11;
                        } else if (txtBuscar.getText().trim().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() >= 0 && cbClase.getSelectionModel().getSelectedIndex() < 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 12, "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), "", cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), "");
                            opcion = 12;
                        } else if (!txtBuscar.getText().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() >= 0 && cbClase.getSelectionModel().getSelectedIndex() < 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 13, txtBuscar.getText().trim(), cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), "", cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), "");
                            opcion = 13;
                        } else if (!txtBuscar.getText().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() < 0 && cbClase.getSelectionModel().getSelectedIndex() >= 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 14, txtBuscar.getText().trim(), 0, cbClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), "");
                            opcion = 14;
                        } else /*if (!txtBuscar.getText().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() < 0 && cbClase.getSelectionModel().getSelectedIndex()<0)*/ {
                            paginacion = 1;
                            fillTableInsumos((short) 15, txtBuscar.getText().trim(), 0, "", cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), "");
                            opcion = 15;
                        }
                    }
                    searchComboBoxSubClases.getComboBox().hide();
                }
            }
        });

        SearchComboBox<ClasesTB> searchComboBoxSubSubClases = new SearchComboBox<>(cbSubSubClase, true);
        searchComboBoxSubSubClases.setFilter((item, text) -> item.getNombreClase().get().toLowerCase().contains(text.toLowerCase()));
        searchComboBoxSubSubClases.getComboBox().getItems().addAll(DetalleADO.GetDetailClass("sub-sub-clase"));
        searchComboBoxSubSubClases.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
            if (null == t.getCode()) {
                searchComboBoxSubSubClases.getSearchComboBoxSkin().getSearchBox().requestFocus();
                searchComboBoxSubSubClases.getSearchComboBoxSkin().getSearchBox().selectAll();
            } else {
                switch (t.getCode()) {
                    case ENTER:
                    case SPACE:
                    case ESCAPE:
                        cbSubSubClase.getSelectionModel().select(null);
                        searchComboBoxSubSubClases.getComboBox().hide();
                        break;
                    case UP:
                    case DOWN:
                    case LEFT:
                    case RIGHT:
                        break;
                    default:
                        searchComboBoxSubSubClases.getSearchComboBoxSkin().getSearchBox().requestFocus();
                        searchComboBoxSubSubClases.getSearchComboBoxSkin().getSearchBox().selectAll();
                        break;
                }
            }
        });

        searchComboBoxSubSubClases.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                searchComboBoxSubSubClases.getComboBox().getSelectionModel().select(item);
                if (searchComboBoxSubSubClases.getSearchComboBoxSkin().isClickSelection()) {
                    if (!lblLoad.isVisible()) {
                        if (txtBuscar.getText().trim().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() < 0 && cbClase.getSelectionModel().getSelectedIndex() < 0 && cbSubClase.getSelectionModel().getSelectedIndex() < 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 16, "", 0, "", "", cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                            opcion = 16;
                        } else if (txtBuscar.getText().trim().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() < 0 && cbClase.getSelectionModel().getSelectedIndex() < 0 && cbSubClase.getSelectionModel().getSelectedIndex() >= 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 17, "", 0, "", cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                            opcion = 17;
                        } else if (txtBuscar.getText().trim().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() < 0 && cbClase.getSelectionModel().getSelectedIndex() >= 0 && cbSubClase.getSelectionModel().getSelectedIndex() >= 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 18, "", 0, cbClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                            opcion = 18;
                        } else if (txtBuscar.getText().trim().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() >= 0 && cbClase.getSelectionModel().getSelectedIndex() >= 0 && cbSubClase.getSelectionModel().getSelectedIndex() >= 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 19, "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), cbClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                            opcion = 19;
                        } else if (txtBuscar.getText().trim().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() >= 0 && cbClase.getSelectionModel().getSelectedIndex() < 0 && cbSubClase.getSelectionModel().getSelectedIndex() >= 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 20, "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), "", cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                            opcion = 20;
                        } else if (txtBuscar.getText().trim().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() < 0 && cbClase.getSelectionModel().getSelectedIndex() >= 0 && cbSubClase.getSelectionModel().getSelectedIndex() < 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 21, "", 0, cbClase.getSelectionModel().getSelectedItem().getIdClase(), "", cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                            opcion = 21;
                        } else if (txtBuscar.getText().trim().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() >= 0 && cbClase.getSelectionModel().getSelectedIndex() >= 0 && cbSubClase.getSelectionModel().getSelectedIndex() < 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 22, "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), cbClase.getSelectionModel().getSelectedItem().getIdClase(), "", cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                            opcion = 22;
                        } else if (txtBuscar.getText().trim().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() >= 0 && cbClase.getSelectionModel().getSelectedIndex() < 0 && cbSubClase.getSelectionModel().getSelectedIndex() < 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 23, "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), "", "", cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                            opcion = 23;
                        } else if (!txtBuscar.getText().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() >= 0 && cbClase.getSelectionModel().getSelectedIndex() >= 0 && cbSubClase.getSelectionModel().getSelectedIndex() >= 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 24, txtBuscar.getText().trim(), cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), cbClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                            opcion = 24;
                        } else if (!txtBuscar.getText().trim().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() < 0 && cbClase.getSelectionModel().getSelectedIndex() < 0 && cbSubClase.getSelectionModel().getSelectedIndex() < 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 25, txtBuscar.getText().trim(), 0, "", "", cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                            opcion = 25;
                        } else if (!txtBuscar.getText().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() >= 0 && cbClase.getSelectionModel().getSelectedIndex() < 0 && cbSubClase.getSelectionModel().getSelectedIndex() < 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 26, txtBuscar.getText().trim(), cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), "", "", cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                            opcion = 26;
                        } else if (!txtBuscar.getText().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() >= 0 && cbClase.getSelectionModel().getSelectedIndex() >= 0 && cbSubClase.getSelectionModel().getSelectedIndex() < 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 27, txtBuscar.getText().trim(), cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), cbClase.getSelectionModel().getSelectedItem().getIdClase(), "", cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                            opcion = 27;
                        } else if (!txtBuscar.getText().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() < 0 && cbClase.getSelectionModel().getSelectedIndex() >= 0 && cbSubClase.getSelectionModel().getSelectedIndex() < 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 28, txtBuscar.getText().trim(), 0, cbClase.getSelectionModel().getSelectedItem().getIdClase(), "", cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                            opcion = 28;
                        } else if (!txtBuscar.getText().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() >= 0 && cbClase.getSelectionModel().getSelectedIndex() < 0 && cbSubClase.getSelectionModel().getSelectedIndex() >= 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 29, txtBuscar.getText().trim(), cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), "", cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                            opcion = 29;
                        } else if (!txtBuscar.getText().isEmpty() && cbCategoria.getSelectionModel().getSelectedIndex() < 0 && cbClase.getSelectionModel().getSelectedIndex() >= 0 && cbSubClase.getSelectionModel().getSelectedIndex() >= 0) {
                            paginacion = 1;
                            fillTableInsumos((short) 30, txtBuscar.getText().trim(), 0, cbClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                            opcion = 30;
                        } else {
                            paginacion = 1;
                            fillTableInsumos((short) 30, txtBuscar.getText().trim(), 0, "", cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                            opcion = 31;
                        }
                    }
                    searchComboBoxSubSubClases.getComboBox().hide();
                }
            }
        });
    }

    private void fillTableInsumos(short tipo, String busqueda, int categoria, String clase, String subClase, String subSubClase) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<Object> task = new Task<Object>() {
            @Override
            public Object call() {
                return InsumoADO.ListarInsumos(tipo, busqueda, categoria, clase, subClase, subSubClase, (paginacion - 1) * 20, 20);
            }
        };
        task.setOnSucceeded(w -> {
            Object object = task.getValue();
            if (object instanceof Object[]) {
                Object[] objects = (Object[]) object;
                ObservableList<InsumoTB> observableList = (ObservableList<InsumoTB>) objects[0];
                if (!observableList.isEmpty()) {
                    tvList.setItems(observableList);
                } else {
                    tvList.setPlaceholder(loadMessageTableView("Datos no encontrados.", "-fx-text-fill:#020203;", "labelRoboto13", false));
                }

                totalPaginacion = (int) (Math.ceil(((Integer) objects[1]) / 20.00));
                lblPaginaActual.setText(paginacion + "");
                lblPaginaSiguiente.setText(totalPaginacion + "");

            } else if (object instanceof String) {
                tvList.setPlaceholder(loadMessageTableView((String) object, "-fx-text-fill:#a70820;", "labelRoboto13", false));
            } else {
                tvList.setPlaceholder(loadMessageTableView("Error en traer los datos, intente nuevamente.", "-fx-text-fill:#a70820;", "labelRoboto13", false));
            }
            lblLoad.setVisible(false);
        });
        task.setOnFailed(w -> {
            lblLoad.setVisible(false);
            tvList.setPlaceholder(loadMessageTableView(task.getMessage(), "-fx-text-fill:#a70820;", "labelRoboto13", false));
        });
        task.setOnScheduled(w -> {
            lblLoad.setVisible(true);
            tvList.getItems().clear();
            tvList.setPlaceholder(loadMessageTableView("Cargando información...", "-fx-text-fill:#020203;", "labelRoboto13", true));
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    public void onEventPaginacion() {
        switch (opcion) {
            case 0:
                fillTableInsumos((short) 0, "", 0, "", "", "");
                break;
            case 1:
                fillTableInsumos((short) 1, txtBuscar.getText().trim(), 0, "", "", "");
                break;
            case 2:
                fillTableInsumos((short) 2, "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), "", "", "");
                break;
            case 3:
                fillTableInsumos((short) 3, txtBuscar.getText().trim(), cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), "", "", "");
                break;
            case 4:
                fillTableInsumos((short) 4, "", 0, cbClase.getSelectionModel().getSelectedItem().getIdClase(), "", "");
                break;
            case 5:
                fillTableInsumos((short) 5, "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), cbClase.getSelectionModel().getSelectedItem().getIdClase(), "", "");
                break;
            case 6:
                fillTableInsumos((short) 6, txtBuscar.getText().trim(), cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), cbClase.getSelectionModel().getSelectedItem().getIdClase(), "", "");
                break;
            case 7:
                fillTableInsumos((short) 7, txtBuscar.getText().trim(), 0, cbClase.getSelectionModel().getSelectedItem().getIdClase(), "", "");
                break;
            case 8:
                fillTableInsumos((short) 8, "", 0, "", cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), "");
                break;
            case 9:
                fillTableInsumos((short) 9, "", 0, cbClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), "");
                break;
            case 10:
                fillTableInsumos((short) 10, "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), cbClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), "");
                break;
            case 11:
                fillTableInsumos((short) 11, txtBuscar.getText().trim(), cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), cbClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), "");
                break;
            case 12:
                fillTableInsumos((short) 12, "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), "", cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), "");
                break;
            case 13:
                fillTableInsumos((short) 13, txtBuscar.getText().trim(), cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), "", cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), "");
                break;
            case 14:
                fillTableInsumos((short) 14, txtBuscar.getText().trim(), 0, cbClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), "");
                break;
            case 15:
                fillTableInsumos((short) 15, txtBuscar.getText().trim(), 0, "", cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), "");
                break;
            case 16:
                fillTableInsumos((short) 16, "", 0, "", "", cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                break;
            case 17:
                fillTableInsumos((short) 17, "", 0, "", cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                break;
            case 18:
                fillTableInsumos((short) 18, "", 0, cbClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                break;
            case 19:
                fillTableInsumos((short) 19, "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), cbClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                break;
            case 20:
                fillTableInsumos((short) 20, "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), "", cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                break;
            case 21:
                fillTableInsumos((short) 21, "", 0, cbClase.getSelectionModel().getSelectedItem().getIdClase(), "", cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                break;
            case 22:
                fillTableInsumos((short) 22, "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), cbClase.getSelectionModel().getSelectedItem().getIdClase(), "", cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                break;
            case 23:
                fillTableInsumos((short) 23, "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), "", "", cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                break;
            case 24:
                fillTableInsumos((short) 24, txtBuscar.getText().trim(), cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), cbClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                break;
            case 25:
                fillTableInsumos((short) 25, txtBuscar.getText().trim(), 0, "", "", cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                break;
            case 26:
                fillTableInsumos((short) 26, txtBuscar.getText().trim(), cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), "", "", cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                break;
            case 27:
                fillTableInsumos((short) 27, txtBuscar.getText().trim(), cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), cbClase.getSelectionModel().getSelectedItem().getIdClase(), "", cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                break;
            case 28:
                fillTableInsumos((short) 28, txtBuscar.getText().trim(), 0, cbClase.getSelectionModel().getSelectedItem().getIdClase(), "", cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                break;
            case 29:
                fillTableInsumos((short) 29, txtBuscar.getText().trim(), cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle(), "", cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                break;
            case 30:
                fillTableInsumos((short) 30, txtBuscar.getText().trim(), 0, cbClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                break;
            default:
                fillTableInsumos((short) 31, txtBuscar.getText().trim(), 0, "", cbSubClase.getSelectionModel().getSelectedItem().getIdClase(), cbSubSubClase.getSelectionModel().getSelectedItem().getIdClase());
                break;
        }
    }

    private Label loadMessageTableView(String message, String styleCss, String fontClass, boolean viewImage) {
        Label label = new Label(message);
        label.setStyle(styleCss);
        label.getStyleClass().add(fontClass);
        if (viewImage) {
            ImageView imageView = new ImageView(new Image("/view/image/load.gif"));
            imageView.setFitWidth(48);
            imageView.setFitHeight(48);
            label.setGraphic(imageView);
        }
        return label;
    }

    private void openWindowInsumo() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_INSUMO_PROCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxInsumoProcesoController controller = fXMLLoader.getController();
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Registrar Insumo", hbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
        } catch (IOException ex) {
            System.out.println("openWindowInsumo():" + ex.getLocalizedMessage());
        }
    }

    private void openWindowInsumo(String idInsumo) {
        try {
           fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_INSUMO_PROCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxInsumoProcesoController controller = fXMLLoader.getController();
            controller.loadInsumo();
            controller.editarInsumo(idInsumo);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Modificar Insumo", hbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
           stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
        } catch (IOException ex) {
            System.out.println("openWindowInsumo():" + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onKeyPressedAgregar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowInsumo();
        }
    }

    @FXML
    private void onActionAgregar(ActionEvent event) {
        openWindowInsumo();
    }

    @FXML
    private void onKeyPressedEditar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                openWindowInsumo(tvList.getSelectionModel().getSelectedItem().getIdInsumo());
            }
        }
    }

    @FXML
    private void onActionEditar(ActionEvent event) {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            openWindowInsumo(tvList.getSelectionModel().getSelectedItem().getIdInsumo());
        }
    }

    @FXML
    private void onKeyPressedRecargar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            txtBuscar.setText("");
            cbCategoria.getSelectionModel().select(null);
            cbClase.getSelectionModel().select(null);
            cbSubClase.getSelectionModel().select(null);
            cbSubSubClase.getSelectionModel().select(null);
            loadInitComponents();
        }
    }

    @FXML
    private void onActionRecargar(ActionEvent event) {
        txtBuscar.setText("");
        cbCategoria.getSelectionModel().select(null);
        cbClase.getSelectionModel().select(null);
        cbSubClase.getSelectionModel().select(null);
        cbSubSubClase.getSelectionModel().select(null);
        loadInitComponents();
    }

    @FXML
    private void onKeyPressedEliminar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                short value = Tools.AlertMessageConfirmation(hbWindow, "Insumo", "¿Está seguro de eliminar el insumo?");
                if (value == 1) {
                    String result = InsumoADO.EliminarInsumo(tvList.getSelectionModel().getSelectedItem().getIdInsumo());
                    if (result.equalsIgnoreCase("deleted")) {
                        Tools.AlertMessageInformation(hbWindow, "Insumo", "Se eliminó correctamten el insumo.");
                        loadInitComponents();
                    } else {
                        Tools.AlertMessageError(hbWindow, "Insumo", result);
                    }
                }
            }
        }
    }

    @FXML
    private void onActionElimar(ActionEvent event) {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            short value = Tools.AlertMessageConfirmation(hbWindow, "Insumo", "¿Está seguro de eliminar el insumo?");
            if (value == 1) {
                String result = InsumoADO.EliminarInsumo(tvList.getSelectionModel().getSelectedItem().getIdInsumo());
                if (result.equalsIgnoreCase("deleted")) {
                    Tools.AlertMessageInformation(hbWindow, "Insumo", "Se eliminó correctamten el insumo.");
                    loadInitComponents();
                } else {
                    Tools.AlertMessageError(hbWindow, "Insumo", result);
                }
            }
        }
    }

    @FXML
    private void onActionBuscar(ActionEvent event) {
        if (!tvList.getItems().isEmpty()) {
            tvList.getSelectionModel().select(null);
            tvList.requestFocus();
        }
    }

    @FXML
    private void onKeyPressedBuscar(KeyEvent event) {
        if ((event.getCode() == KeyCode.BACK_SPACE) || (event.getCode() == KeyCode.DELETE)) {
            cbCategoria.getSelectionModel().select(null);
            cbClase.getSelectionModel().select(null);
            cbSubClase.getSelectionModel().select(null);
            cbSubSubClase.getSelectionModel().select(null);
        }
    }

    @FXML
    private void onKeyReleasedBuscar(KeyEvent event) {
        if (event.getCode() != KeyCode.ESCAPE
                && event.getCode() != KeyCode.F1
                && event.getCode() != KeyCode.F2
                && event.getCode() != KeyCode.F3
                && event.getCode() != KeyCode.F4
                && event.getCode() != KeyCode.F5
                && event.getCode() != KeyCode.F6
                && event.getCode() != KeyCode.F7
                && event.getCode() != KeyCode.F8
                && event.getCode() != KeyCode.F9
                && event.getCode() != KeyCode.F10
                && event.getCode() != KeyCode.F11
                && event.getCode() != KeyCode.F12
                && event.getCode() != KeyCode.ALT
                && event.getCode() != KeyCode.CONTROL
                && event.getCode() != KeyCode.UP
                && event.getCode() != KeyCode.DOWN
                && event.getCode() != KeyCode.RIGHT
                && event.getCode() != KeyCode.LEFT
                && event.getCode() != KeyCode.TAB
                && event.getCode() != KeyCode.CAPS
                && event.getCode() != KeyCode.SHIFT
                && event.getCode() != KeyCode.HOME
                && event.getCode() != KeyCode.WINDOWS
                && event.getCode() != KeyCode.ALT_GRAPH
                && event.getCode() != KeyCode.CONTEXT_MENU
                && event.getCode() != KeyCode.END
                && event.getCode() != KeyCode.INSERT
                && event.getCode() != KeyCode.PAGE_UP
                && event.getCode() != KeyCode.PAGE_DOWN
                && event.getCode() != KeyCode.NUM_LOCK
                && event.getCode() != KeyCode.PRINTSCREEN
                && event.getCode() != KeyCode.SCROLL_LOCK
                && event.getCode() != KeyCode.PAUSE
                && event.getCode() != KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                if (!Tools.isText(txtBuscar.getText())) {
                    paginacion = 1;
                    fillTableInsumos((short) 1, txtBuscar.getText().trim(), 0, "", "", "");
                    opcion = 1;
                } else {
                    loadInitComponents();
                }
            }
        }
    }

    @FXML
    private void onMouseClickList(MouseEvent event) {
        if (event.getClickCount() == 2) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                openWindowInsumo(tvList.getSelectionModel().getSelectedItem().getIdInsumo());
            }
        }
    }

    @FXML
    private void onKeyPressedAnterior(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                if (paginacion > 1) {
                    paginacion--;
                    onEventPaginacion();
                }
            }
        }
    }

    @FXML
    private void onActionAnterior(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (paginacion > 1) {
                paginacion--;
                onEventPaginacion();
            }
        }
    }

    @FXML
    private void onKeyPressedSiguiente(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                if (paginacion < totalPaginacion) {
                    paginacion++;
                    onEventPaginacion();
                }
            }
        }
    }

    @FXML
    private void onActionSiguiente(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (paginacion < totalPaginacion) {
                paginacion++;
                onEventPaginacion();
            }
        }
    }

    public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }

}
