package controller.produccion.suministros;

import controller.egresos.compras.FxComprasController;
import controller.egresos.compras.FxComprasEditarController;
import controller.ingresos.venta.FxVentaEstructuraController;
import controller.ingresos.venta.FxVentaUtilidadesController;
import controller.produccion.asignacion.FxAsignacionController;
import controller.produccion.asignacion.FxAsignacionProcesoController;
import controller.produccion.movimientos.FxMovimientosProcesoController;
import controller.produccion.producir.FxProducirProcesoController;
import controller.tools.FilesRouters;
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
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.ModeloGenerico;
import model.SuministroADO;
import model.SuministroTB;

public class FxSuministrosListaController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<SuministroTB> tvList;
    @FXML
    private TableColumn<SuministroTB, Integer> tcId;
    @FXML
    private TableColumn<SuministroTB, String> tcNombre;
    @FXML
    private TableColumn<SuministroTB, String> tcCantidad;
    @FXML
    private TableColumn<SuministroTB, String> tcCategoriaMarca;
    @FXML
    private TableColumn<SuministroTB, ImageView> tcTipoProducto;
    @FXML
    private TableColumn<SuministroTB, String> tcCosto;
    @FXML
    private TableColumn<SuministroTB, String> tcPrecio;
    @FXML
    private TextField txtCategoria;
    @FXML
    private TextField txtMarca;
    @FXML
    private TextField txtPresentacion;
    @FXML
    private TextField txtMedida;
    @FXML
    private ImageView ivPrincipal;
    @FXML
    private Label lblPaginaActual;
    @FXML
    private Label lblPaginaSiguiente;

    private FxMovimientosProcesoController movimientosProcesoController;

    private FxComprasController comprasController;

    private FxComprasEditarController comprasEditarController;

    private FxSuministrosKardexController suministrosKardexController;

    private FxProducirProcesoController producirProcesoController;

    private FxAsignacionController asignacionController;

    private FxAsignacionProcesoController asignacionProcesoController;

    private FxVentaEstructuraController ventaEstructuraController;

    private FxVentaUtilidadesController ventaUtilidadesController;

    private ArrayList<ImpuestoTB> arrayArticulosImpuesto;

    private boolean status;

    private int paginacion;

    private int totalPaginacion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);

        apWindow.setOnKeyReleased(event -> {
            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case F1:
                        txtSearch.requestFocus();
                        txtSearch.selectAll();
                        break;
                    case F2:
                        txtCategoria.requestFocus();
                        txtCategoria.selectAll();
                        break;
                    case F3:
                        txtMarca.requestFocus();
                        txtMarca.selectAll();
                        break;
                    case F4:
                        txtPresentacion.requestFocus();
                        txtPresentacion.selectAll();
                        break;
                    case F5:
                        txtMedida.requestFocus();
                        txtMedida.selectAll();
                        break;
                    case F6:
                        openWindowAddSuministro();
                        break;
                    case F7:
                        if (!status) {
                            paginacion = 1;
                            fillSuministrosTablePaginacion();
                        }
                        break;
                }
            }
        });

        tcId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        tcNombre.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getClave() + "\n" + cellData.getValue().getNombreMarca()));
        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getCantidad(), 2) + " " + cellData.getValue().getUnidadCompraName()));
        tcCategoriaMarca.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getCategoriaName() + "\n" + cellData.getValue().getMarcaName()));
        tcTipoProducto.setCellValueFactory(new PropertyValueFactory<>("imageValorInventario"));
        tcCosto.setCellValueFactory(cellData -> Bindings.concat(getTaxName(cellData.getValue().getImpuestoArticulo())));
        tcPrecio.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getPrecioVentaGeneral() + (cellData.getValue().getPrecioVentaGeneral() * (getTaxValue(cellData.getValue().getImpuestoArticulo()) / 100.00)), 2)));

        arrayArticulosImpuesto = new ArrayList<>();
        ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> {
            arrayArticulosImpuesto.add(new ImpuestoTB(e.getIdImpuesto(), e.getOperacion(),e.getNombreImpuesto(), e.getValor(), e.getPredeterminado()));
        });

        paginacion = 1;
        status = false;
    }

    private void fillSuministrosTable(short tipo, String value) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ObservableList<SuministroTB>> task = new Task<ObservableList<SuministroTB>>() {
            @Override
            public ObservableList<SuministroTB> call() {
                return SuministroADO.ListSuministrosListaView(tipo, value);
            }
        };

        task.setOnSucceeded((e) -> {
            tvList.setItems(task.getValue());
            status = false;
        });
        task.setOnFailed((e) -> {
            status = false;
        });
        task.setOnScheduled((e) -> {
            status = true;
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    public void fillSuministrosTablePaginacion() {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ModeloGenerico> task = new Task<ModeloGenerico>() {
            @Override
            public ModeloGenerico call() {
                int page = paginacion;
                int total = SuministroADO.GetAllSuministros();
                ModeloGenerico generico = new ModeloGenerico();
                generico.setObjectoE(SuministroADO.ListSuministroPaginacionView((page - 1) * 20));
                generico.setObjectoN(20);
                generico.setObjectoM(total);
                generico.setObjectoO((int) (Math.ceil((double) (total / 20.00))));
                generico.setObjectoP(page);
                return generico;
            }
        };

        task.setOnSucceeded((e) -> {
            ModeloGenerico generico = task.getValue();
            tvList.setItems((ObservableList<SuministroTB>) generico.getObjectoE());

            totalPaginacion = (int) generico.getObjectoO();
            lblPaginaActual.setText(paginacion + "");
            lblPaginaSiguiente.setText(totalPaginacion + "");
            status = false;
        });
        task.setOnFailed((e) -> {
            status = false;
        });
        task.setOnScheduled((e) -> {
            status = true;
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void openWindowCompra(short option) {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            if (tvList.getSelectionModel().getSelectedItem().isInventario()) {
                try {
                    URL url = getClass().getResource(FilesRouters.FX_SUMINISTROS_COMPRA);
                    FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                    Parent parent = fXMLLoader.load(url.openStream());
                    //Controlller here
                    FxSuministrosCompraController controller = fXMLLoader.getController();
                    if (option == 1) {
                        controller.setInitComprasController(comprasController);
                    } else {
                        controller.setInitComprasEditarController(comprasEditarController);
                    }
                    controller.setLoadData(new String[]{
                        tvList.getSelectionModel().getSelectedItem().getIdSuministro(),
                        tvList.getSelectionModel().getSelectedItem().getClave(),
                        tvList.getSelectionModel().getSelectedItem().getNombreMarca(),
                        "" + tvList.getSelectionModel().getSelectedItem().getCostoCompra(),
                        "" + tvList.getSelectionModel().getSelectedItem().getPrecioVentaGeneral(),
                        "" + tvList.getSelectionModel().getSelectedItem().getPrecioMargenGeneral(),
                        "" + tvList.getSelectionModel().getSelectedItem().getPrecioUtilidadGeneral(),
                        "" + tvList.getSelectionModel().getSelectedItem().getImpuestoArticulo()
                    },
                            tvList.getSelectionModel().getSelectedItem().getUnidadCompraName(),
                            tvList.getSelectionModel().getSelectedItem().isLote()
                    );
                    //
                    Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Producto", apWindow.getScene().getWindow());
                    stage.setResizable(false);
                    stage.sizeToScene();
                    stage.show();
                } catch (IOException ix) {
                    System.out.println("Error Producto Lista Controller:" + ix.getLocalizedMessage());
                }
            } else {
                Tools.AlertMessageWarning(apWindow, "Compra", "El producto no es inventariado, actualize sus campos.");
            }
        }
    }

    private void executeEvent() {
        if (ventaEstructuraController != null) {
            addArticuloToList();
        } else if (movimientosProcesoController != null) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                if (!validateStock(movimientosProcesoController.getTvList(), tvList.getSelectionModel().getSelectedItem())) {
                    movimientosProcesoController.addSuministroLista(tvList.getSelectionModel().getSelectedItem().getIdSuministro());
                    Tools.Dispose(apWindow);
                } else {
                    Tools.AlertMessageWarning(apWindow, "Movimiento", "Ya hay un producto con las mismas características.");
                }
            }
        } else if (comprasController != null) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                openWindowCompra((short) 1);
                txtSearch.requestFocus();
            }
        } else if (comprasEditarController != null) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                openWindowCompra((short) 0);
                txtSearch.requestFocus();
            }
        } else if (suministrosKardexController != null) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                suministrosKardexController.setLoadProducto(tvList.getSelectionModel().getSelectedItem().getClave() + " " + tvList.getSelectionModel().getSelectedItem().getNombreMarca());
                suministrosKardexController.fillKardexTable(tvList.getSelectionModel().getSelectedItem().getIdSuministro());
                Tools.Dispose(apWindow);
            }
        } else if (asignacionProcesoController != null) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                if (!validateStock(asignacionProcesoController.getTvList(), tvList.getSelectionModel().getSelectedItem())) {
                    asignacionProcesoController.addSuministroLista(tvList.getSelectionModel().getSelectedItem().getIdSuministro());
                    Tools.Dispose(apWindow);
                } else {
                    Tools.AlertMessageWarning(apWindow, "Asignación", "Ya hay un producto con las mismas características.");
                }
            }
        } else if (ventaUtilidadesController != null) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                ventaUtilidadesController.setIdSuministro(tvList.getSelectionModel().getSelectedItem().getIdSuministro());
                ventaUtilidadesController.getTxtProducto().setText(tvList.getSelectionModel().getSelectedItem().getNombreMarca());
                Tools.Dispose(apWindow);
            }
        }
    }

    private boolean validateStock(TableView<SuministroTB> view, SuministroTB suministroTB) {
        boolean ret = false;
        for (int i = 0; i < view.getItems().size(); i++) {
            if (view.getItems().get(i).getIdSuministro().equals(suministroTB.getIdSuministro())) {
                ret = true;
                break;
            }
        }
        return ret;
    }
    
    public int getTaxValueOperacion(int impuesto) {
        int valor = 0;
        for (int i = 0; i < arrayArticulosImpuesto.size(); i++) {
            if (arrayArticulosImpuesto.get(i).getIdImpuesto() == impuesto) {
                valor = arrayArticulosImpuesto.get(i).getOperacion();
                break;
            }
        }
        return valor;
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
                valor = arrayArticulosImpuesto.get(i).getNombreImpuesto();
                break;
            }
        }
        return valor;
    }

    private void addArticuloToList() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            SuministroTB suministroTB = new SuministroTB();
            suministroTB.setIdSuministro(tvList.getSelectionModel().getSelectedItem().getIdSuministro());
            suministroTB.setClave(tvList.getSelectionModel().getSelectedItem().getClave());
            suministroTB.setNombreMarca(tvList.getSelectionModel().getSelectedItem().getNombreMarca());
            suministroTB.setCantidad(1);
            suministroTB.setCostoCompra(tvList.getSelectionModel().getSelectedItem().getCostoCompra());

            suministroTB.setDescuento(0);
            suministroTB.setDescuentoCalculado(0);
            suministroTB.setDescuentoSumado(0);

            suministroTB.setPrecioVentaGeneralUnico(tvList.getSelectionModel().getSelectedItem().getPrecioVentaGeneral());
            suministroTB.setPrecioVentaGeneralReal(tvList.getSelectionModel().getSelectedItem().getPrecioVentaGeneral());
            suministroTB.setPrecioVentaGeneralAuxiliar(suministroTB.getPrecioVentaGeneralReal());

            suministroTB.setImpuestoOperacion(getTaxValueOperacion(tvList.getSelectionModel().getSelectedItem().getImpuestoArticulo()));
            suministroTB.setImpuestoArticulo(tvList.getSelectionModel().getSelectedItem().getImpuestoArticulo());
            suministroTB.setImpuestoArticuloName(getTaxName(tvList.getSelectionModel().getSelectedItem().getImpuestoArticulo()));
            suministroTB.setImpuestoValor(getTaxValue(tvList.getSelectionModel().getSelectedItem().getImpuestoArticulo()));
            suministroTB.setImpuestoSumado(suministroTB.getCantidad() * Tools.calculateTax(suministroTB.getImpuestoValor(), suministroTB.getPrecioVentaGeneralReal()));

            suministroTB.setPrecioVentaGeneral(suministroTB.getPrecioVentaGeneralReal() + suministroTB.getImpuestoSumado());

            suministroTB.setSubImporte(suministroTB.getPrecioVentaGeneralUnico() * suministroTB.getCantidad());
            suministroTB.setSubImporteDescuento(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
            suministroTB.setTotalImporte(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());

            suministroTB.setInventario(tvList.getSelectionModel().getSelectedItem().isInventario());
            suministroTB.setUnidadVenta(tvList.getSelectionModel().getSelectedItem().getUnidadVenta());
            suministroTB.setValorInventario(tvList.getSelectionModel().getSelectedItem().getValorInventario());

            Button button = new Button();
            button.getStyleClass().add("buttonBorder");
            ImageView view = new ImageView(new Image("/view/image/remove.png"));
            view.setFitWidth(24);
            view.setFitHeight(24);
            button.setGraphic(view);
            button.setOnAction(event -> {
                ventaEstructuraController.getTvList().getItems().remove(suministroTB);
                ventaEstructuraController.calculateTotales();
            });
            button.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    ventaEstructuraController.getTvList().getItems().remove(suministroTB);
                    ventaEstructuraController.calculateTotales();
                }
            });
            suministroTB.setRemover(button);

            Tools.Dispose(apWindow);

            ventaEstructuraController.getAddArticulo(suministroTB);

        }
    }

    private void openWindowAddSuministro() {
        try {
            URL url = getClass().getResource(FilesRouters.FX_SUMINISTROS_PROCESO_MODAL);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxSuministrosProcesoModalController controller = fXMLLoader.getController();
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Cliente", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.show();
            controller.setInitArticulo();
        } catch (IOException ex) {
            System.out.println("Error en suministro lista:" + ex.getLocalizedMessage());
        }
    }

    private void searchTable(KeyEvent event, short tipo, String value) {
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
            if (!status) {
                fillSuministrosTable(tipo, value);
            }
        }
    }

    private void selectTable(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!tvList.getItems().isEmpty()) {
                tvList.requestFocus();
                tvList.getSelectionModel().select(0);
                selectImage();
            }
        }
    }

    private void selectImage() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            ivPrincipal.setImage(new Image(tvList.getSelectionModel().getSelectedItem().getImagenTB().equalsIgnoreCase("")
                    ? "/view/image/no-image.png"
                    : new File(tvList.getSelectionModel().getSelectedItem().getImagenTB()).toURI().toString()));
        }
    }

    @FXML
    private void onKeyPressedAdd(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowAddSuministro();
        }
    }

    @FXML
    private void onActionAdd(ActionEvent event) {
        openWindowAddSuministro();
    }

    @FXML
    private void onKeyPressedReload(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!status) {
                paginacion = 1;
                fillSuministrosTablePaginacion();
            }
        }
    }

    @FXML
    private void onActionReload(ActionEvent event) {
        if (!status) {
            paginacion = 1;
            fillSuministrosTablePaginacion();
        }
    }

    @FXML
    private void onKeyPressedList(KeyEvent event) {
        if (null != event.getCode()) {
            switch (event.getCode()) {
                case ENTER:
                    executeEvent();
                    break;
                default:
                    break;
            }
        }
    }

    @FXML
    private void onKeyReleasedList(KeyEvent event) {
        if (null != event.getCode()) {
            switch (event.getCode()) {
                case UP:
                    selectImage();
                    break;
                case DOWN:
                    selectImage();
                    break;
                default:
                    break;
            }
        }
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) {
        if (event.getClickCount() == 1) {
            selectImage();
        } else if (event.getClickCount() == 2) {
            executeEvent();
        }
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            executeEvent();
        }
    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        executeEvent();
    }

    @FXML
    private void onKeyReleasedToSearch(KeyEvent event) {
        searchTable(event, (short) 1, txtSearch.getText().trim());
    }

    @FXML
    private void onKeyReleasedToCategoria(KeyEvent event) {
        searchTable(event, (short) 2, txtCategoria.getText().trim());
    }

    @FXML
    private void onKeyReleasedToMarca(KeyEvent event) {
        searchTable(event, (short) 3, txtMarca.getText().trim());
    }

    @FXML
    private void onKeyReleasedToPresentacion(KeyEvent event) {
        searchTable(event, (short) 4, txtPresentacion.getText().trim());
    }

    @FXML
    private void onKeyReleasedToMedida(KeyEvent event) {
        searchTable(event, (short) 5, txtMedida.getText().trim());
    }

    @FXML
    private void onKeyPressedToSearh(KeyEvent event) {
        selectTable(event);
    }

    @FXML
    private void onKeyPressedToCategoria(KeyEvent event) {
        selectTable(event);
    }

    @FXML
    private void onKeyPressedToMarca(KeyEvent event) {
        selectTable(event);
    }

    @FXML
    private void onKeyPressedToPresentacion(KeyEvent event) {
        selectTable(event);
    }

    @FXML
    private void onKeyPressedToMedida(KeyEvent event) {
        selectTable(event);
    }

    @FXML
    private void onKeyPressedAnterior(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!status) {
                if (paginacion > 1) {
                    paginacion--;
                    fillSuministrosTablePaginacion();
                }
            }
        }
    }

    @FXML
    private void onActionAnterior(ActionEvent event) {
        if (!status) {
            if (paginacion > 1) {
                paginacion--;
                fillSuministrosTablePaginacion();
            }
        }
    }

    @FXML
    private void onKeyPressedSiguiente(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!status) {
                if (paginacion < totalPaginacion) {
                    paginacion++;
                    fillSuministrosTablePaginacion();
                }
            }
        }
    }

    @FXML
    private void onActionSiguiente(ActionEvent event) {
        if (!status) {
            if (paginacion < totalPaginacion) {
                paginacion++;
                fillSuministrosTablePaginacion();
            }
        }
    }

    public void setInitMovimientoProcesoController(FxMovimientosProcesoController movimientosProcesoController) {
        this.movimientosProcesoController = movimientosProcesoController;
    }

    public void setInitComprasController(FxComprasController comprasController) {
        this.comprasController = comprasController;
    }

    public void setInitComprasEditarController(FxComprasEditarController comprasController) {
        this.comprasEditarController = comprasController;
    }

    public void setInitSuministrosKardexController(FxSuministrosKardexController suministrosKardexController) {
        this.suministrosKardexController = suministrosKardexController;
    }

    public void setInitProducirProcesoController(FxProducirProcesoController producirProcesoController) {
        this.producirProcesoController = producirProcesoController;
    }

    public void setInitAsignacionController(FxAsignacionController asignacionController) {
        this.asignacionController = asignacionController;
    }

    public void setInitAsignacionProcesoController(FxAsignacionProcesoController asignacionProcesoController) {
        this.asignacionProcesoController = asignacionProcesoController;
    }

    public void setInitVentaEstructuraController(FxVentaEstructuraController ventaEstructuraController) {
        this.ventaEstructuraController = ventaEstructuraController;
    }

    public void setInitVentaUtilidadesController(FxVentaUtilidadesController ventaUtilidadesController) {
        this.ventaUtilidadesController = ventaUtilidadesController;
    }

}
