package controller.inventario.valorinventario;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import controller.reporte.FxReportViewController;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.awt.HeadlessException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.DetalleADO;
import model.DetalleTB;
import model.SuministroADO;
import model.SuministroTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

public class FxValorInventarioController implements Initializable {

    @FXML
    private VBox vbWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private TextField txtProducto;
    @FXML
    private ComboBox<String> cbExistencia;
    @FXML
    private TableView<SuministroTB> tvList;
    @FXML
    private TableColumn<SuministroTB, Integer> tcNumero;
    @FXML
    private TableColumn<SuministroTB, String> tcDescripcion;
//    private TableColumn<SuministroTB, String> tcCantidad;
//    private TableColumn<SuministroTB, String> tcUnidad;
//    private TableColumn<SuministroTB, String> tcEstado;
    @FXML
    private TableColumn<SuministroTB, String> tcCostoPromedio;
    @FXML
    private TableColumn<SuministroTB, String> tcPrecio;
    @FXML
    private TableColumn<SuministroTB, Label> tcExistencia;
    @FXML
    private TableColumn<SuministroTB, String> tcInventario;
    @FXML
    private TableColumn<SuministroTB, String> tcCategoria;
    @FXML
    private TableColumn<SuministroTB, String> tcMarca;
    
//    private TableColumn<SuministroTB, String> tcInventarioMinimo;
//    private TableColumn<SuministroTB, String> tcInventarioMaximo;
//    private TableColumn<SuministroTB, String> tcCatMar;
//    private TableColumn<SuministroTB, String> tcTotal;
    @FXML
    private Label lblValoTotal;
    @FXML
    private TextField txtNameProduct;
    @FXML
    private ComboBox<HideableItem<DetalleTB>> cbCategoria;
    @FXML
    private ComboBox<HideableItem<DetalleTB>> cbMarca;

    private AnchorPane vbPrincipal;
    
    
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcNumero.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        tcDescripcion.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getClave() + "\n" + cellData.getValue().getNombreMarca()
        ));
//        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(
//                Tools.roundingValue(cellData.getValue().getCantidad(), 2)
//        ));
//        tcUnidad.setCellValueFactory(cellData -> Bindings.concat(
//                cellData.getValue().getUnidadCompraName()
//        ));
//        tcEstado.setCellValueFactory(cellData -> Bindings.concat(
//                cellData.getValue().getEstadoName().get()
//        ));
        tcCostoPromedio.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getCostoCompra(), 2)
        ));
        tcPrecio.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getPrecioVentaGeneral(), 2)));
        //tcExistencia.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getCantidad(), 2) + " " + cellData.getValue().getUnidadCompraName()));
        tcExistencia.setCellValueFactory(new PropertyValueFactory<>("lblCantidad"));
        tcInventario.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getStockMinimo(), 2)+" - "+ Tools.roundingValue(cellData.getValue().getStockMaximo(), 2)));
        tcCategoria.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getCategoriaName()));
        tcMarca.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMarcaName()));
        
//        tcTotal.setCellValueFactory(cellData -> Bindings.concat(
//                Tools.roundingValue(cellData.getValue().getTotalImporte(), 2)
//        ));

        tcNumero.prefWidthProperty().bind(tvList.widthProperty().multiply(0.04));
        tcDescripcion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.25));
        //tcCantidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        //tcUnidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        //tcEstado.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        tcCostoPromedio.prefWidthProperty().bind(tvList.widthProperty().multiply(0.10));
        tcPrecio.prefWidthProperty().bind(tvList.widthProperty().multiply(0.10));
        tcExistencia.prefWidthProperty().bind(tvList.widthProperty().multiply(0.16));
        tcInventario.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        tcCategoria.prefWidthProperty().bind(tvList.widthProperty().multiply(0.10));
        tcMarca.prefWidthProperty().bind(tvList.widthProperty().multiply(0.10));
        
//        tcInventarioMinimo.prefWidthProperty().bind(tvList.widthProperty().multiply(0.10));
//        tcInventarioMaximo.prefWidthProperty().bind(tvList.widthProperty().multiply(0.10));
        
        //tcTotal.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));

        cbExistencia.getItems().addAll("Todas las Existencias", "Existencias negativas", "Existencias positivas", "Existencias Intermedias");
        cbExistencia.getSelectionModel().select(0);
        filtercbCategoria();
    }

    public void fillInventarioTable(String producto, short tipoExistencia, String nameProduct, short opcion, int categoria, int marca) {
        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<ObservableList<SuministroTB>> task = new Task<ObservableList<SuministroTB>>() {
            @Override
            public ObservableList<SuministroTB> call() {
                return SuministroADO.ListInventario(producto, tipoExistencia, nameProduct, opcion, categoria, marca);
            }
        };
        task.setOnSucceeded((WorkerStateEvent e) -> {
            tvList.setItems(task.getValue());
            double total = 0;
            total = tvList.getItems().stream().map((l) -> l.getTotalImporte()).reduce(total, (accumulator, _item) -> accumulator + _item);
            lblValoTotal.setText(Session.MONEDA_SIMBOLO + Tools.roundingValue(total, 4));
            lblLoad.setVisible(false);
        });
        task.setOnFailed((WorkerStateEvent event) -> {
            lblLoad.setVisible(false);
        });
        task.setOnScheduled((WorkerStateEvent event) -> {
            lblLoad.setVisible(true);
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }
    
    private void filtercbCategoria() {
        createComboBoxWithAutoCompletionSupport(cbCategoria, DetalleADO.GetDetailId("0006"));
        createComboBoxWithAutoCompletionSupport(cbMarca, DetalleADO.GetDetailId("0007"));
    }

    private void openWindowAjuste() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            try {
                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                URL url = getClass().getResource(FilesRouters.FX_INVENTARIO_AJUSTE);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxInventarioAjusteController controller = fXMLLoader.getController();
                controller.setInitValorInventarioController(this);
                controller.setLoadComponents(tvList.getSelectionModel().getSelectedItem().getIdSuministro(), tvList.getSelectionModel().getSelectedItem().getClave(), tvList.getSelectionModel().getSelectedItem().getNombreMarca());
                //
                Stage stage = WindowStage.StageLoaderModal(parent, "Ajuste de inventario", vbWindow.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.setOnHiding((w) -> {
                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                });
                stage.show();
            } catch (IOException ex) {
                System.out.println(ex.getLocalizedMessage());
            }
        }
    }

    private void generarReporte() {
        if (tvList.getItems().isEmpty()) {
            Tools.AlertMessageWarning(vbWindow, "Reporte Inventario", "No hay datos en la lista para mostrar en el reporte");
            return;
        }
        try {
            InputStream dir = getClass().getResourceAsStream("/report/InventarioActual.jasper");

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(dir);
            Map map = new HashMap();
            map.put("OPCIONES_EXISTENCIA", cbExistencia.getSelectionModel().getSelectedItem());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JRBeanCollectionDataSource(tvList.getItems()));

            URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxReportViewController controller = fXMLLoader.getController();
            controller.setJasperPrint(jasperPrint);
            controller.show();
            Stage stage = WindowStage.StageLoader(parent, "Inventario General");
            stage.setResizable(true);
            stage.show();
            stage.requestFocus();

        } catch (HeadlessException | JRException | IOException ex) {
            Tools.AlertMessageError(vbWindow, "Reporte de Inventario", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onKeyReleasedProducto(KeyEvent event) {
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
                && event.getCode() != KeyCode.PAUSE) {
            if (!lblLoad.isVisible()) {
                fillInventarioTable(txtProducto.getText().trim(), (short) 0, "", (short) 1, 0, 0);
            }
        }
    }

    @FXML
    private void onKeyPressedAjuste(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowAjuste();
        }
    }

    @FXML
    private void onActionAjuste(ActionEvent event) {
        openWindowAjuste();
    }

    @FXML
    private void onKeyPressedRecargar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                cbExistencia.getSelectionModel().select(0);
                fillInventarioTable("", (short) 0, "", (short) 0,0, 0);
            }
        }
    }

    @FXML
    private void onActionRecargar(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            cbExistencia.getSelectionModel().select(0);
            fillInventarioTable("", (short) 0, "", (short) 0, 0, 0);
        }
    }

    @FXML
    private void onKeyPressedReporte(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            generarReporte();
        }
    }

    @FXML
    private void onActionReporte(ActionEvent event) {
        generarReporte();
    }

    @FXML
    private void onActionExistencia(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            fillInventarioTable("", (short) cbExistencia.getSelectionModel().getSelectedIndex(), "", (short) 3, 0, 0);
        }
    }

    @FXML
    private void onKeyReleasedNameProduct(KeyEvent event) {
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
                && event.getCode() != KeyCode.PAUSE) {
            if (!lblLoad.isVisible()) {
                fillInventarioTable("", (short) 0, txtNameProduct.getText().trim(), (short) 2, 0, 0);
            }
        }
    }
    
    @FXML
    private void onActionCategoria(ActionEvent event) {
        if (cbCategoria.getSelectionModel().getSelectedIndex() >= 0) {
            if (!lblLoad.isVisible()) {
                fillInventarioTable("", (short) 0, "", (short) 4, cbCategoria.getSelectionModel().getSelectedItem().getObject().getIdDetalle().get(), 0);
            }
        }
    }

    @FXML
    private void onActionMarca(ActionEvent event) {
        if (cbCategoria.getSelectionModel().getSelectedIndex() >= 0) {
            if (!lblLoad.isVisible()) {
                fillInventarioTable("", (short) 0, "", (short) 5, 0, cbMarca.getSelectionModel().getSelectedItem().getObject().getIdDetalle().get());
            }
        }
    }

    public ComboBox<String> getCbExistencia() {
        return cbExistencia;
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

    
    
    private void createComboBoxWithAutoCompletionSupport(ComboBox<HideableItem<DetalleTB>> comboBox, ObservableList<DetalleTB> items) {
        ObservableList<HideableItem<DetalleTB>> hideableHideableItems = FXCollections.observableArrayList(hideableItem -> new Observable[]{hideableItem.hiddenProperty()});

        items.forEach(item
                -> {
            HideableItem<DetalleTB> hideableItem = new HideableItem<>(item);
            hideableHideableItems.add(hideableItem);
        });

        FilteredList<HideableItem<DetalleTB>> filteredHideableItems = new FilteredList<>(hideableHideableItems, t -> !t.isHidden());

        comboBox.setItems(filteredHideableItems);

        @SuppressWarnings("unchecked")
        HideableItem<DetalleTB>[] selectedItem = (HideableItem<DetalleTB>[]) new HideableItem[1];

        comboBox.addEventHandler(KeyEvent.KEY_PRESSED, event
                -> {
            if (!comboBox.isShowing()) {
                return;
            }

            comboBox.setEditable(true);
            comboBox.getEditor().clear();
        });

        comboBox.showingProperty().addListener((observable, oldValue, newValue)
                -> {
            if (newValue) {
                @SuppressWarnings("unchecked")
                ListView<HideableItem> lv = ((ComboBoxListViewSkin<HideableItem>) comboBox.getSkin()).getListView();
                lv.scrollTo(comboBox.getValue());
            } else {
                HideableItem<DetalleTB> value = comboBox.getValue();
                if (value != null) {
                    selectedItem[0] = value;
                }
//
                comboBox.setEditable(false);
//
                Platform.runLater(()
                        -> {
                    comboBox.getSelectionModel().select(selectedItem[0]);
                    comboBox.setValue(selectedItem[0]);
                });
            }

        });

        comboBox.setOnHidden(event -> hideableHideableItems.forEach(item -> {
            item.setHidden(false);

        }));

        comboBox.getEditor().textProperty().addListener((obs, oldValue, newValue)
                -> {

            if (!comboBox.isShowing()) {
                return;
            }

            Platform.runLater(()
                    -> {
                if (comboBox.getSelectionModel().getSelectedItem() == null) {
                    hideableHideableItems.forEach(item -> item.setHidden(!item.getObject().toString().toLowerCase().contains(newValue.toLowerCase())));
                } else {
                    boolean validText = false;

                    for (HideableItem hideableItem : hideableHideableItems) {
                        if (hideableItem.getObject().toString().equals(newValue)) {
                            validText = true;
                            break;
                        }
                    }

                    if (!validText) {
                        comboBox.getSelectionModel().select(null);
                    }
                }
            });
        });

    }
    
    public class HideableItem<T> {

        private final ObjectProperty<T> object = new SimpleObjectProperty<>();
        private final BooleanProperty hidden = new SimpleBooleanProperty();

        private HideableItem(T object) {
            setObject(object);
        }

        private ObjectProperty<T> objectProperty() {
            return this.object;
        }

        private T getObject() {
            return this.objectProperty().get();
        }

        private void setObject(T object) {
            this.objectProperty().set(object);
        }

        private BooleanProperty hiddenProperty() {
            return this.hidden;
        }

        private boolean isHidden() {
            return this.hiddenProperty().get();
        }

        private void setHidden(boolean hidden) {
            this.hiddenProperty().set(hidden);
        }

        @Override
        public String toString() {
            return getObject() == null ? null : getObject().toString();
        }
    }



}
