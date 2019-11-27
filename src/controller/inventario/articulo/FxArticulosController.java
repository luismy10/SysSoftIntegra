package controller.inventario.articulo;

import controller.configuracion.etiquetas.FxEtiquetasBusquedaController;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.ArticuloADO;
import model.ArticuloTB;
import model.DetalleADO;
import model.DetalleTB;
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.ModeloGenerico;

public class FxArticulosController implements Initializable {

    @FXML
    private VBox window;
    @FXML
    private Label lblLoad;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<ArticuloTB> tvList;
    @FXML
    private TableColumn<ArticuloTB, Integer> tcId;
    @FXML
    private TableColumn<ArticuloTB, String> tcDocument;
    @FXML
    private TableColumn<ArticuloTB, String> tcMarca;
    @FXML
    private TableColumn<ArticuloTB, String> tcUnidadVenta;
    @FXML
    private TableColumn<ArticuloTB, String> tcCategoria;
    @FXML
    private TableColumn<ArticuloTB, String> tcEstado;
    @FXML
    private VBox vbOpciones;
    @FXML
    private ComboBox<DetalleTB> cbCategoria;
    @FXML
    private TextField txtSearchCode;
    @FXML
    private Label lblPaginaActual;
    @FXML
    private Label lblPaginaSiguiente;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    private boolean status;

    private FxArticuloSeleccionadoController seleccionadoController;

    private ArrayList<ImpuestoTB> arrayArticulosImpuesto;

    private int paginacion;

    private int totalPaginacion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        window.setOnKeyReleased((KeyEvent event) -> {
            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case F2:
                        onViewArticuloEdit();
                        event.consume();
                        break;
                    case F5:
                        if (!status) {
                            paginacion = 1;
                            fillArticlesTablePaginacion();
                            if (!tvList.getItems().isEmpty()) {
                                tvList.getSelectionModel().select(0);
                            }
                        }
                        event.consume();
                        break;
                    case F6:
                        eventEtiqueta();
                        event.consume();
                        break;
                    case F7:
                        txtSearchCode.requestFocus();
                        txtSearchCode.selectAll();
                        event.consume();
                        break;
                    case F8:
                        txtSearch.requestFocus();
                        txtSearch.selectAll();
                        event.consume();
                        break;
                    case F9:
                        cbCategoria.requestFocus();
                        break;
                    default:

                        break;
                }
            }
        });

        tcId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        tcDocument.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getClaveAlterna() == null || cellData.getValue().getClaveAlterna().equalsIgnoreCase("")
                ? cellData.getValue().getClave() + "\n" + cellData.getValue().getNombreMarca()
                : cellData.getValue().getClave() + " - " + cellData.getValue().getClaveAlterna() + "\n" + cellData.getValue().getNombreMarca()
        ));
        tcMarca.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMarcaName()));
        tcUnidadVenta.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getUnidadVenta() == 1 ? "Por Unidad/Pza" : "A Granel(Peso)"
        ));
        tcCategoria.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getCategoriaName()));
        tcEstado.setCellValueFactory(cellData -> cellData.getValue().getEstadoName());

        tcId.prefWidthProperty().bind(tvList.widthProperty().multiply(0.06));
        tcDocument.prefWidthProperty().bind(tvList.widthProperty().multiply(0.28));
        tcMarca.prefWidthProperty().bind(tvList.widthProperty().multiply(0.16));
        tcUnidadVenta.prefWidthProperty().bind(tvList.widthProperty().multiply(0.16));
        tcCategoria.prefWidthProperty().bind(tvList.widthProperty().multiply(0.16));
        tcEstado.prefWidthProperty().bind(tvList.widthProperty().multiply(0.16));

        arrayArticulosImpuesto = new ArrayList<>();
        ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> {
            arrayArticulosImpuesto.add(new ImpuestoTB(e.getIdImpuesto(), e.getNombre(), e.getValor(), e.getPredeterminado()));
        });

        cbCategoria.getItems().add(new DetalleTB(new SimpleIntegerProperty(0), new SimpleStringProperty("-- Seleccione --")));
        DetalleADO.GetDetailId("0006").forEach(e -> {
            cbCategoria.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        cbCategoria.getSelectionModel().select(0);
        changeViewArticuloSeleccionado();

        paginacion = 1;
        status = false;
    }

    public void changeViewArticuloSeleccionado() {
        try {
            FXMLLoader fXMLSeleccionado = new FXMLLoader(getClass().getResource(FilesRouters.FX_ARTICULO_SELECCIONADO));
            VBox seleccionado = fXMLSeleccionado.load();
            VBox.setVgrow(seleccionado, Priority.SOMETIMES);
            seleccionadoController = fXMLSeleccionado.getController();

            vbOpciones.getChildren().clear();
            vbOpciones.getChildren().add(seleccionado);
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void fillArticlesTable(short option, String value, int categoria) {
        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ObservableList<ArticuloTB>> task = new Task<ObservableList<ArticuloTB>>() {
            @Override
            public ObservableList<ArticuloTB> call() {
               // return ArticuloADO.ListArticulos(option, value, categoria);
               return null;
            }
        };

        task.setOnSucceeded((WorkerStateEvent e) -> {
            tvList.setItems(task.getValue());
            if (!tvList.getItems().isEmpty()) {
                tvList.getSelectionModel().select(0);
                onViewDetailArticulo();
            }
            lblLoad.setVisible(false);
            status = false;
        });
        task.setOnFailed((WorkerStateEvent event) -> {
            lblLoad.setVisible(false);
            status = false;
        });

        task.setOnScheduled((WorkerStateEvent event) -> {
            lblLoad.setVisible(true);
            status = true;
        });
        exec.execute(task);

        if (!exec.isShutdown()) {
            exec.shutdown();
        }

    }

    private void onViewArticuloEdit() {
        try {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(getClass().getResource(FilesRouters.FX_ARTICULO_PROCESO));
                ScrollPane node = fXMLLoader.load();
                //Controlller here
                FxArticuloProcesoController controller = fXMLLoader.getController();
                controller.setInitControllerArticulos(this, vbPrincipal, vbContent);
                //
                vbContent.getChildren().clear();
                AnchorPane.setLeftAnchor(node, 0d);
                AnchorPane.setTopAnchor(node, 0d);
                AnchorPane.setRightAnchor(node, 0d);
                AnchorPane.setBottomAnchor(node, 0d);
                vbContent.getChildren().add(node);
                //
                controller.setValueEdit(tvList.getSelectionModel().getSelectedItem().getIdArticulo());
            } else {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Artículo", "Debe seleccionar un artículo para editarlo", false);
                tvList.requestFocus();
            }
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    public void fillArticlesTablePaginacion() {
        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ModeloGenerico> task = new Task<ModeloGenerico>() {
            @Override
            public ModeloGenerico call() {
                int page = paginacion;
               // int total = ArticuloADO.GetAllArticulos();
                ModeloGenerico generico = new ModeloGenerico();
//                generico.setObjectoE(ArticuloADO.ListArticulosPaginacion((page - 1) * 10));
//                generico.setObjectoN(10);
//                generico.setObjectoM(total); 
//                generico.setObjectoO((int) (Math.ceil((double) (total / 10.00))));
//                generico.setObjectoP(page);
                return generico;
            }
        };

        task.setOnSucceeded((WorkerStateEvent e) -> {
            ModeloGenerico generico = task.getValue();
            tvList.setItems((ObservableList<ArticuloTB>) generico.getObjectoE());
            if (!tvList.getItems().isEmpty()) {
                tvList.getSelectionModel().select(0);
                onViewDetailArticulo();
            }
            totalPaginacion = (int) generico.getObjectoO();
            lblPaginaActual.setText(paginacion + "");
            lblPaginaSiguiente.setText(totalPaginacion + "");

            lblLoad.setVisible(false);
            status = false;
        });
        task.setOnFailed((WorkerStateEvent event) -> {
            lblLoad.setVisible(false);
            status = false;
        });

        task.setOnScheduled((WorkerStateEvent event) -> {
            lblLoad.setVisible(true);
            status = true;
        });
        exec.execute(task);

        if (!exec.isShutdown()) {
            exec.shutdown();
        }

    }

    private void eventEtiqueta() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            try {
                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                URL url = getClass().getResource(FilesRouters.FX_ETIQUETA_BUSQUEDA);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxEtiquetasBusquedaController controller = fXMLLoader.getController();
                controller.setInitArticuloController(this);
                controller.onLoadEtiquetas(1);
                //
                Stage stage = WindowStage.StageLoaderModal(parent, "Buscar etiquetas", window.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.setOnHiding((WindowEvent WindowEvent) -> {
                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                });
                stage.show();
            } catch (IOException ex) {
                System.out.println(ex.getLocalizedMessage());
            }
        } else {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Artículo", "Seleccione un item para imprimir su etiqueta", false);
        }
    }

    public void onViewDetailArticulo() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            ArticuloTB articuloTB = tvList.getSelectionModel().getSelectedItem();
            seleccionadoController.getIvPrincipal().setImage(new Image(articuloTB.getImagenTB().equalsIgnoreCase("")
                    ? "/view/image/no-image.png"
                    : new File(articuloTB.getImagenTB()).toURI().toString()));
            seleccionadoController.getLblName().setText(articuloTB.getNombreMarca());
            seleccionadoController.getLblPrice().setText(
                    Session.MONEDA + " "
                    + Tools.roundingValue(articuloTB.getPrecioVentaGeneral() + (articuloTB.getPrecioVentaGeneral() * (getTaxValue(articuloTB.getImpuestoArticulo()) / 100.00)),
                            2)
            );
            seleccionadoController.getLblPriceBruto().setText(
                    "Precio Bruto: "
                    + Tools.roundingValue(articuloTB.getPrecioVentaGeneral(), 2));
            seleccionadoController.getLblImpuesto().setText(
                    "Impuesto: " + getTaxName(articuloTB.getImpuestoArticulo())
            );
            seleccionadoController.getLblQuantity().setText(Tools.roundingValue(articuloTB.getCantidad(), 2) + " " + articuloTB.getUnidadCompraName());

        }
    }

    public double getTaxValue(int impuesto) {
        double valor = 0;
        for (int i = 0; i < arrayArticulosImpuesto.size(); i++) {
            if (arrayArticulosImpuesto.get(i).getIdImpuesto() == impuesto) {
                valor = arrayArticulosImpuesto.get(i).getValor();
                break;
            }
        }
        return valor;
    }

    public String getTaxName(int impuesto) {
        String valor = "";
        for (int i = 0; i < arrayArticulosImpuesto.size(); i++) {
            if (arrayArticulosImpuesto.get(i).getIdImpuesto() == impuesto) {
                valor = arrayArticulosImpuesto.get(i).getNombre();
                break;
            }
        }
        return valor;
    }

    @FXML
    private void onActionEdit(ActionEvent event) {
        onViewArticuloEdit();
    }

    @FXML
    private void onKeyPressedEdit(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onViewArticuloEdit();
        }
    }

    @FXML
    private void onActionReload(ActionEvent event) {
        if (!status) {
            fillArticlesTable((short) 0, "", 0);
            if (!tvList.getItems().isEmpty()) {
                tvList.getSelectionModel().select(0);
            }
        }
    }

    @FXML
    private void onKeyPressedReload(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!status) {
                fillArticlesTable((short) 0, "", 0);
                if (!tvList.getItems().isEmpty()) {
                    tvList.getSelectionModel().select(0);
                }
            }
        }
    }

    @FXML
    private void onActionSearch(ActionEvent event) {
        if (!tvList.getItems().isEmpty()) {
            tvList.requestFocus();
            tvList.getSelectionModel().select(0);
        }
    }

    @FXML
    private void onKeyReleasedSearch(KeyEvent event) {
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
            if (!status) {
                fillArticlesTable((short) 2, txtSearch.getText().trim(), 0);
            }
        }
    }

    @FXML
    private void onKeyReleasedSearchCode(KeyEvent event) {
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
            if (!status) {
                fillArticlesTable((short) 3, txtSearchCode.getText().trim(), 0);
            }
        }
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            if (event.getClickCount() == 1) {
                onViewDetailArticulo();
            } else if (event.getClickCount() == 2) {
                onViewArticuloEdit();
            }
        }
    }

    @FXML
    private void onKeyPressedList(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onViewArticuloEdit();
        }
    }

    @FXML
    private void onKeyRelasedList(KeyEvent event) {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case UP:
                        onViewDetailArticulo();
                        break;
                    case DOWN:
                        onViewDetailArticulo();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @FXML
    private void onActionCategoria(ActionEvent event) {
        if (cbCategoria.getSelectionModel().getSelectedIndex() >= 1) {
            if (!status) {
                fillArticlesTable((short) 1, "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle().get());
            }
        }
    }

    @FXML
    private void onKeyPressedEtiquetas(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            eventEtiqueta();
        }
    }

    @FXML
    private void onActionEtiquetas(ActionEvent event) {
        eventEtiqueta();
    }

    @FXML
    private void onActionAnterior(ActionEvent event) {
        if (paginacion > 1) {
            paginacion--;
            fillArticlesTablePaginacion();
        }
    }

    @FXML
    private void onActionSiguiente(ActionEvent event) {
        if (paginacion < totalPaginacion) {
            paginacion++;
            fillArticlesTablePaginacion();
        }
    }

    public TableView<ArticuloTB> getTvList() {
        return tvList;
    }

    public TextField getTxtSearch() {
        return txtSearch;
    }

    public VBox getWindow() {
        return window;
    }

    public void setContent(AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
