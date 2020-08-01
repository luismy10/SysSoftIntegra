package controller.inventario.suministros;

import controller.operaciones.compras.FxComprasController;
import controller.consultas.compras.FxComprasEditarController;
import controller.operaciones.venta.FxVentaEstructuraController;
import controller.reporte.FxVentaUtilidadesController;
import controller.produccion.asignacion.FxAsignacionController;
import controller.produccion.asignacion.FxAsignacionProcesoController;
import controller.inventario.movimientos.FxMovimientosProcesoController;
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

    private short opcion;

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
                            fillSuministrosTable((short) 0, "");
                            opcion = 0;
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
        tcPrecio.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getPrecioVentaGeneral() + (cellData.getValue().getPrecioVentaGeneral() * (getTaxValue(cellData.getValue().getImpuestoArticulo()) / 100.00)), 4)));

        arrayArticulosImpuesto = new ArrayList<>();
        ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> {
            arrayArticulosImpuesto.add(new ImpuestoTB(e.getIdImpuesto(), e.getOperacion(), e.getNombreImpuesto(), e.getValor(), e.getPredeterminado()));
        });

        paginacion = 1;
        opcion = 0;
        status = false;
    }

    public void fillSuministrosTable(short tipo, String value) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                return SuministroADO.ListSuministrosListaView(tipo, value, (paginacion - 1) * 10, 10);
            }
        };

        task.setOnSucceeded(e -> {
            ArrayList<Object> objects = task.getValue();
            if (!objects.isEmpty()) {
                tvList.setItems((ObservableList<SuministroTB>) objects.get(0));
                if (!tvList.getItems().isEmpty()) {
                    tvList.getSelectionModel().select(0);
                }
                int integer = (int) (Math.ceil((double) (((Integer) objects.get(1)) / 10.00)));
                totalPaginacion = integer;
                lblPaginaActual.setText(paginacion + "");
                lblPaginaSiguiente.setText(totalPaginacion + "");
            }
            status = false;
        });
        task.setOnFailed(e -> {
            status = false;
        });
        task.setOnScheduled(e -> {
            status = true;
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    public void onEventPaginacion() {
        switch (opcion) {
            case 0:
                fillSuministrosTable((short) 0, "");
                break;
            case 1:
                fillSuministrosTable((short) 1, txtSearch.getText().trim());
                break;
            case 2:
                fillSuministrosTable((short) 2, txtCategoria.getText().trim());
                break;
            case 3:
                fillSuministrosTable((short) 3, txtMarca.getText().trim());
                break;
            case 4:
                fillSuministrosTable((short) 4, txtPresentacion.getText().trim());
                break;
            default:
                fillSuministrosTable((short) 5, txtMedida.getText().trim());
                break;
        }
    }

    private void openWindowCompra(short option) {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            if (tvList.getSelectionModel().getSelectedItem().isInventario()) {
                WindowStage.openWindowSuministroCompra(false, null, comprasController, tvList.getSelectionModel().getSelectedItem(), apWindow.getScene().getWindow());
//                try {
//                    URL url = getClass().getResource(FilesRouters.FX_SUMINISTROS_COMPRA);
//                    FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
//                    Parent parent = fXMLLoader.load(url.openStream());
//                    //Controlller here
//                    FxSuministrosCompraController controller = fXMLLoader.getController();
//                    if (option == 1) {
//                        controller.setInitComprasController(comprasController);
//                    } else {
//                        controller.setInitComprasEditarController(comprasEditarController);
//                    }
//                    controller.setLoadData(
//                            tvList.getSelectionModel().getSelectedItem().getIdSuministro(),
//                            tvList.getSelectionModel().getSelectedItem().getClave(),
//                            tvList.getSelectionModel().getSelectedItem().getNombreMarca(),
//                            Tools.roundingValue(tvList.getSelectionModel().getSelectedItem().getCostoCompra(), 8),
//                            tvList.getSelectionModel().getSelectedItem().getUnidadCompraName(),
//                            tvList.getSelectionModel().getSelectedItem().isLote()
//                    );
//                    //
//                    Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Producto", apWindow.getScene().getWindow());
//                    stage.setResizable(false);
//                    stage.sizeToScene();
//                    stage.show();
//                } catch (IOException ix) {
//                    System.out.println("Error Producto Lista Controller:" + ix.getLocalizedMessage());
//                }
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
                suministrosKardexController.setLoadProducto(tvList.getSelectionModel().getSelectedItem().getIdSuministro(), tvList.getSelectionModel().getSelectedItem().getClave() + " " + tvList.getSelectionModel().getSelectedItem().getNombreMarca());
                suministrosKardexController.fillKardexTable((short)0,tvList.getSelectionModel().getSelectedItem().getIdSuministro(), "", "");
                System.out.println(tvList.getSelectionModel().getSelectedItem().getIdSuministro());
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

            Button button = new Button("X");
            button.getStyleClass().add("buttonDark");
            button.setOnAction(e -> {
                ventaEstructuraController.getTvList().getItems().remove(suministroTB);
                ventaEstructuraController.calculateTotales();
            });
            button.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    ventaEstructuraController.getTvList().getItems().remove(suministroTB);
                    ventaEstructuraController.calculateTotales();
                }
            });
            suministroTB.setRemover(button);
            txtSearch.selectAll();
            txtSearch.requestFocus();
            //Tools.Dispose(apWindow);
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
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Suministro", apWindow.getScene().getWindow());
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
            File fileImage = new File(tvList.getSelectionModel().getSelectedItem().getImagenTB());
            ivPrincipal.setImage(new Image(fileImage.exists() ? fileImage.toURI().toString() : "/view/image/no-image.png"));
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
                fillSuministrosTable((short) 0, "");
                opcion = 0;
            }
        }
    }

    @FXML
    private void onActionReload(ActionEvent event) {
        if (!status) {
            paginacion = 1;
            fillSuministrosTable((short) 0, "");
            opcion = 0;
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
        paginacion = 1;
        searchTable(event, (short) 1, txtSearch.getText().trim());
        opcion = 1;
    }

    @FXML
    private void onKeyReleasedToCategoria(KeyEvent event) {
        paginacion = 1;
        searchTable(event, (short) 2, txtCategoria.getText().trim());
        opcion = 2;
    }

    @FXML
    private void onKeyReleasedToMarca(KeyEvent event) {
        paginacion = 1;
        searchTable(event, (short) 3, txtMarca.getText().trim());
        opcion = 3;
    }

    @FXML
    private void onKeyReleasedToPresentacion(KeyEvent event) {
        paginacion = 1;
        searchTable(event, (short) 4, txtPresentacion.getText().trim());
        opcion = 4;
    }

    @FXML
    private void onKeyReleasedToMedida(KeyEvent event) {
        paginacion = 1;
        searchTable(event, (short) 5, txtMedida.getText().trim());
        opcion = 5;
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
                    onEventPaginacion();
                }
            }
        }
    }

    @FXML
    private void onActionAnterior(ActionEvent event) {
        if (!status) {
            if (paginacion > 1) {
                paginacion--;
                onEventPaginacion();
            }
        }
    }

    @FXML
    private void onKeyPressedSiguiente(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!status) {
                if (paginacion < totalPaginacion) {
                    paginacion++;
                    onEventPaginacion();
                }
            }
        }
    }

    @FXML
    private void onActionSiguiente(ActionEvent event) {
        if (!status) {
            if (paginacion < totalPaginacion) {
                paginacion++;
                onEventPaginacion();
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
