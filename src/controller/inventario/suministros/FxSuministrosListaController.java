package controller.inventario.suministros;

import controller.operaciones.compras.FxComprasController;
import controller.inventario.lote.FxLoteCambiarController;
import controller.operaciones.venta.FxVentaEstructuraController;
import controller.reporte.FxVentaUtilidadesController;
import controller.inventario.movimientos.FxMovimientosProcesoController;
import controller.operaciones.cotizacion.FxCotizacionController;
import controller.operaciones.cotizacion.FxCotizacionModalController;
import controller.operaciones.guiaremision.FxGuiaRemisionController;
import controller.operaciones.pedidos.FxPedidosController;
import controller.operaciones.pedidos.FxPedidosModalController;
import controller.posterminal.venta.FxPostVentaEstructuraController;
import controller.produccion.producir.FxProducirAgregarController;
import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.ByteArrayInputStream;
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
import model.GuiaRemisionDetalleTB;
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
    private TableColumn<SuministroTB, String> tcImpuesto;
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

    private FxSuministrosKardexController suministrosKardexController;

    private FxVentaEstructuraController ventaEstructuraController;

    private FxPostVentaEstructuraController postVentaEstructuraController;

    private FxVentaUtilidadesController ventaUtilidadesController;

    private FxCotizacionController cotizacionController;

    private FxGuiaRemisionController guiaRemisionController;

    private FxLoteCambiarController loteCambiarController;

    private FxProducirAgregarController producirAgregarController;

    private FxPedidosController pedidosController;

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
        tcImpuesto.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getImpuestoNombre()));
        tcPrecio.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getPrecioVentaGeneral(), 2)));
        tvList.setPlaceholder(Tools.placeHolderTableView("Ingrese datos para iniciar la busqueda.", "-fx-text-fill:#020203;", false));

        paginacion = 1;
        opcion = 0;
        status = false;

        txtSearch.setOnKeyReleased(e -> {
            if (e.getCode() != KeyCode.ESCAPE
                    && e.getCode() != KeyCode.F1
                    && e.getCode() != KeyCode.F2
                    && e.getCode() != KeyCode.F3
                    && e.getCode() != KeyCode.F4
                    && e.getCode() != KeyCode.F5
                    && e.getCode() != KeyCode.F6
                    && e.getCode() != KeyCode.F7
                    && e.getCode() != KeyCode.F8
                    && e.getCode() != KeyCode.F9
                    && e.getCode() != KeyCode.F10
                    && e.getCode() != KeyCode.F11
                    && e.getCode() != KeyCode.F12
                    && e.getCode() != KeyCode.ALT
                    && e.getCode() != KeyCode.CONTROL
                    && e.getCode() != KeyCode.UP
                    && e.getCode() != KeyCode.DOWN
                    && e.getCode() != KeyCode.RIGHT
                    && e.getCode() != KeyCode.LEFT
                    && e.getCode() != KeyCode.TAB
                    && e.getCode() != KeyCode.CAPS
                    && e.getCode() != KeyCode.SHIFT
                    && e.getCode() != KeyCode.HOME
                    && e.getCode() != KeyCode.WINDOWS
                    && e.getCode() != KeyCode.ALT_GRAPH
                    && e.getCode() != KeyCode.CONTEXT_MENU
                    && e.getCode() != KeyCode.END
                    && e.getCode() != KeyCode.INSERT
                    && e.getCode() != KeyCode.PAGE_UP
                    && e.getCode() != KeyCode.PAGE_DOWN
                    && e.getCode() != KeyCode.NUM_LOCK
                    && e.getCode() != KeyCode.PRINTSCREEN
                    && e.getCode() != KeyCode.SCROLL_LOCK
                    && e.getCode() != KeyCode.PAUSE
                    && e.getCode() != KeyCode.ENTER) {
                if (!status) {
                    if (!Tools.isText(txtSearch.getText())) {
                        paginacion = 1;
                        fillSuministrosTable((short) 1, txtSearch.getText().trim());
                        opcion = 1;
                    }
                }
            }
        });

        txtCategoria.setOnKeyReleased(e -> {
            if (!status) {
                if (!Tools.isText(txtCategoria.getText())) {
                    paginacion = 1;
                    fillSuministrosTable((short) 2, txtCategoria.getText().trim());
                    opcion = 2;
                }
            }
        });

        txtMarca.setOnKeyReleased(e -> {
            if (!status) {
                if (!Tools.isText(txtMarca.getText())) {
                    paginacion = 1;
                    fillSuministrosTable((short) 3, txtMarca.getText().trim());
                    opcion = 3;
                }
            }
        });

        txtPresentacion.setOnKeyReleased(e -> {
            if (!status) {
                if (!Tools.isText(txtPresentacion.getText())) {
                    paginacion = 1;
                    fillSuministrosTable((short) 4, txtPresentacion.getText().trim());
                    opcion = 4;
                }
            }
        });

        txtMedida.setOnKeyReleased(e -> {
            if (!status) {
                if (!Tools.isText(txtMedida.getText())) {
                    paginacion = 1;
                    fillSuministrosTable((short) 5, txtMedida.getText().trim());
                    opcion = 5;
                }
            }
        });

    }

    public void fillSuministrosTable(short tipo, String value) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<Object> task = new Task<Object>() {
            @Override
            public Object call() {
                return SuministroADO.ListSuministrosListaView(tipo, value, (paginacion - 1) * 10, 10);
            }
        };

        task.setOnSucceeded(w -> {
            Object object = task.getValue();
            if (object instanceof Object[]) {
                Object[] objects = (Object[]) object;
                ObservableList<SuministroTB> suministroTBs = (ObservableList<SuministroTB>) objects[0];
                if (!suministroTBs.isEmpty()) {
                    tvList.setItems(suministroTBs);
                    if (!tvList.getItems().isEmpty()) {
                        tvList.getSelectionModel().select(0);
                    }
                    int integer = (int) (Math.ceil(((Integer) objects[1]) / 10.00));
                    totalPaginacion = integer;
                    lblPaginaActual.setText(paginacion + "");
                    lblPaginaSiguiente.setText(totalPaginacion + "");
                } else {
                    tvList.setPlaceholder(Tools.placeHolderTableView("No hay datos para mostrar.", "-fx-text-fill:#020203;", false));
                    lblPaginaActual.setText("0");
                    lblPaginaSiguiente.setText("0");
                }
            } else if (object instanceof String) {
                tvList.setPlaceholder(Tools.placeHolderTableView((String) object, "-fx-text-fill:#a70820;", false));
            } else {
                tvList.setPlaceholder(Tools.placeHolderTableView("Error en traer los datos, intente nuevamente.", "-fx-text-fill:#a70820;", false));
            }
            status = false;
        });
        task.setOnFailed(w -> {
            status = false;
            tvList.setPlaceholder(Tools.placeHolderTableView(task.getMessage(), "-fx-text-fill:#a70820;", false));
        });
        task.setOnScheduled(w -> {
            status = true;
            tvList.getItems().clear();
            tvList.setPlaceholder(Tools.placeHolderTableView("Cargando informaci??n...", "-fx-text-fill:#020203;", true));
            totalPaginacion = 0;
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

    private void openWindowCompra() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            if (tvList.getSelectionModel().getSelectedItem().isInventario()) {
                try {
                    URL url = WindowStage.class.getClassLoader().getClass().getResource(FilesRouters.FX_SUMINISTROS_COMPRA);
                    FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                    Parent parent = fXMLLoader.load(url.openStream());
                    //Controlller here
                    FxSuministrosCompraController controller = fXMLLoader.getController();
                    controller.setInitComprasController(comprasController);
                    controller.setLoadData(tvList.getSelectionModel().getSelectedItem().getIdSuministro(), false);
                    //
                    Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Producto", apWindow.getScene().getWindow());
                    stage.setResizable(false);
                    stage.setOnShown(e -> controller.getTxtCantidad().requestFocus());
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

    private void openWindowPedido() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            if (tvList.getSelectionModel().getSelectedItem().isInventario()) {
                try {
                    URL url = WindowStage.class.getClassLoader().getClass().getResource(FilesRouters.FX_PEDIDO_MODAL);
                    FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                    Parent parent = fXMLLoader.load(url.openStream());
                    //Controlller here
                    FxPedidosModalController controller = fXMLLoader.getController();
                    controller.setInitPedidosController(pedidosController);
                    controller.initComponents(tvList.getSelectionModel().getSelectedItem());
                    //
                    Stage stage = WindowStage.StageLoaderModal(parent, "Cambiar datos", apWindow.getScene().getWindow());
                    stage.setResizable(false);
                    stage.sizeToScene();
                    stage.setOnShown(w -> controller.getTxtCantidad().requestFocus());
                    stage.show();
                } catch (IOException ix) {
                    System.out.println("Error Producto Lista Controller:" + ix.getLocalizedMessage());
                }
            } else {
                Tools.AlertMessageWarning(apWindow, "Compra", "El producto no es inventariado, actualize sus campos.");
            }
        }
    }

    private void addCotizacion() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            if (cotizacionController.validateDuplicateArticulo(tvList.getSelectionModel().getSelectedItem())) {
                for (int i = 0; i < cotizacionController.getTvList().getItems().size(); i++) {
                    if (cotizacionController.getTvList().getItems().get(i).getIdSuministro().equalsIgnoreCase(tvList.getSelectionModel().getSelectedItem().getIdSuministro())) {
                        openWindowCotizacion(cotizacionController.getTvList().getItems().get(i), true);
                        break;
                    }
                }
            } else {
                openWindowCotizacion(tvList.getSelectionModel().getSelectedItem(), false);
            }
        }
    }

    private void openWindowCotizacion(SuministroTB suministroTB, boolean exists) {
        try {
            URL url = WindowStage.class.getClassLoader().getClass().getResource(FilesRouters.FX_COTIZACION_MODAL);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxCotizacionModalController controller = fXMLLoader.getController();
            controller.setInitCotizacionController(cotizacionController);
            controller.initComponents(suministroTB, exists);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Cambiar datos", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnShown(w -> controller.getTxtCantidad().requestFocus());
            stage.show();
        } catch (IOException ix) {
            System.out.println("Error Producto Lista Controller:" + ix.getLocalizedMessage());
        }
    }

    private void executeEvent() {
        if (ventaEstructuraController != null) {
            addVentaToList();
        } else if (postVentaEstructuraController != null) {
            addPostVentaToList();
        } else if (cotizacionController != null) {
            addCotizacion();
            txtSearch.requestFocus();
        } else if (guiaRemisionController != null) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                if (!validateDuplicateGuiaRemision(guiaRemisionController.getTvList(), tvList.getSelectionModel().getSelectedItem())) {
                    guiaRemisionController.eventAgregar(tvList.getSelectionModel().getSelectedItem());
                    Tools.Dispose(apWindow);
                } else {
                    Tools.AlertMessageWarning(apWindow, "Gu??a de remisi??n", "Ya hay un producto con las mismas caracter??sticas.");
                }
            }
        } else if (movimientosProcesoController != null) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                if (!validateDuplicate(movimientosProcesoController.getTvList(), tvList.getSelectionModel().getSelectedItem())) {
                    movimientosProcesoController.addSuministroLista(tvList.getSelectionModel().getSelectedItem().getIdSuministro());
                    Tools.Dispose(apWindow);
                } else {
                    Tools.AlertMessageWarning(apWindow, "Movimiento", "Ya hay un producto con las mismas caracter??sticas.");
                }
            }
        } else if (comprasController != null) {
            openWindowCompra();
            txtSearch.requestFocus();
        } else if (suministrosKardexController != null) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                suministrosKardexController.setLoadProducto(tvList.getSelectionModel().getSelectedItem().getIdSuministro(), tvList.getSelectionModel().getSelectedItem().getClave() + " " + tvList.getSelectionModel().getSelectedItem().getNombreMarca());
                suministrosKardexController.fillKardexTable((short) 0, tvList.getSelectionModel().getSelectedItem().getIdSuministro(), "", "");
                Tools.Dispose(apWindow);
            }
        } else if (ventaUtilidadesController != null) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                ventaUtilidadesController.setIdSuministro(tvList.getSelectionModel().getSelectedItem().getIdSuministro());
                ventaUtilidadesController.getTxtProducto().setText(tvList.getSelectionModel().getSelectedItem().getNombreMarca());
                Tools.Dispose(apWindow);
            }
        } else if (loteCambiarController != null) {
            loteCambiarController.getTxtArticulo().setText(tvList.getSelectionModel().getSelectedItem().getNombreMarca());
            loteCambiarController.getTxtCantidad().setText("" + tvList.getSelectionModel().getSelectedItem().getCantidad());
            Tools.Dispose(apWindow);
        } else if (producirAgregarController != null) {
//            producirAgregarController.setIdSuministro(tvList.getSelectionModel().getSelectedItem().getIdSuministro());
//            producirAgregarController.getTxtProductoFabricar().setText(tvList.getSelectionModel().getSelectedItem().getNombreMarca());
//            Tools.Dispose(apWindow);
        } else if (pedidosController != null) {
            openWindowPedido();
            txtSearch.requestFocus();
        }
    }

    private boolean validateDuplicate(TableView<SuministroTB> view, SuministroTB suministroTB) {
        boolean ret = false;
        for (int i = 0; i < view.getItems().size(); i++) {
            if (view.getItems().get(i).getIdSuministro().equals(suministroTB.getIdSuministro())) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    private boolean validateDuplicateGuiaRemision(TableView<GuiaRemisionDetalleTB> view, SuministroTB suministroTB) {
        boolean ret = false;
        for (int i = 0; i < view.getItems().size(); i++) {
            if (view.getItems().get(i).getIdSuministro().equals(suministroTB.getIdSuministro())) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    private void addVentaToList() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            if (ventaEstructuraController.isVender_con_cantidades_negativas() && tvList.getSelectionModel().getSelectedItem().getCantidad() <= 0) {
                Tools.AlertMessageWarning(apWindow, "Venta", "No puede agregar el producto ya que tiene la cantidad <= 0.");
                return;
            }
            SuministroTB suministroTB = new SuministroTB();
            suministroTB.setIdSuministro(tvList.getSelectionModel().getSelectedItem().getIdSuministro());
            suministroTB.setClave(tvList.getSelectionModel().getSelectedItem().getClave());
            suministroTB.setNombreMarca(tvList.getSelectionModel().getSelectedItem().getNombreMarca());
            suministroTB.setCantidad(1);
            suministroTB.setCostoCompra(tvList.getSelectionModel().getSelectedItem().getCostoCompra());
            suministroTB.setBonificacion(0);

            double valor_sin_impuesto = tvList.getSelectionModel().getSelectedItem().getPrecioVentaGeneral() / ((tvList.getSelectionModel().getSelectedItem().getImpuestoValor() / 100.00) + 1);
            double descuento = suministroTB.getDescuento();
            double porcentajeRestante = valor_sin_impuesto * (descuento / 100.00);
            double preciocalculado = valor_sin_impuesto - porcentajeRestante;

            suministroTB.setDescuento(0);
            suministroTB.setDescuentoCalculado(0);
            suministroTB.setDescuentoSumado(0);

            suministroTB.setPrecioVentaGeneralUnico(valor_sin_impuesto);
            suministroTB.setPrecioVentaGeneralReal(preciocalculado);

            suministroTB.setImpuestoOperacion(tvList.getSelectionModel().getSelectedItem().getImpuestoOperacion());
            suministroTB.setImpuestoId(tvList.getSelectionModel().getSelectedItem().getImpuestoId());
            suministroTB.setImpuestoNombre(tvList.getSelectionModel().getSelectedItem().getImpuestoNombre());
            suministroTB.setImpuestoValor(tvList.getSelectionModel().getSelectedItem().getImpuestoValor());

            double impuesto = Tools.calculateTax(suministroTB.getImpuestoValor(), suministroTB.getPrecioVentaGeneralReal());
            suministroTB.setImpuestoSumado(suministroTB.getCantidad() * impuesto);
            suministroTB.setPrecioVentaGeneral(suministroTB.getPrecioVentaGeneralReal() + impuesto);
            suministroTB.setPrecioVentaGeneralAuxiliar(suministroTB.getPrecioVentaGeneral());

            suministroTB.setImporteBruto(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralUnico());
            suministroTB.setSubImporteNeto(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
            suministroTB.setImporteNeto(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneral());

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
            suministroTB.setBtnRemove(button);
            if (ventaEstructuraController.isCerar_modal_agregar_item_lista()) {
                Tools.Dispose(apWindow);
                ventaEstructuraController.getAddArticulo(suministroTB, ventaEstructuraController.getWindow().getScene().getWindow());
            } else {
                txtSearch.selectAll();
                txtSearch.requestFocus();
                ventaEstructuraController.getAddArticulo(suministroTB, apWindow.getScene().getWindow());
            }

        }
    }

    private void addPostVentaToList() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            if (postVentaEstructuraController.isVender_con_cantidades_negativas() && tvList.getSelectionModel().getSelectedItem().getCantidad() <= 0) {
                Tools.AlertMessageWarning(apWindow, "Venta", "No puede agregar el producto ya que tiene la cantidad <= 0.");
                return;
            }
            SuministroTB suministroTB = new SuministroTB();
            suministroTB.setIdSuministro(tvList.getSelectionModel().getSelectedItem().getIdSuministro());
            suministroTB.setClave(tvList.getSelectionModel().getSelectedItem().getClave());
            suministroTB.setNombreMarca(tvList.getSelectionModel().getSelectedItem().getNombreMarca());
            suministroTB.setCantidad(1);
            suministroTB.setCostoCompra(tvList.getSelectionModel().getSelectedItem().getCostoCompra());
            suministroTB.setBonificacion(0);

            double valor_sin_impuesto = tvList.getSelectionModel().getSelectedItem().getPrecioVentaGeneral() / ((tvList.getSelectionModel().getSelectedItem().getImpuestoValor() / 100.00) + 1);
            double descuento = suministroTB.getDescuento();
            double porcentajeRestante = valor_sin_impuesto * (descuento / 100.00);
            double preciocalculado = valor_sin_impuesto - porcentajeRestante;

            suministroTB.setDescuento(0);
            suministroTB.setDescuentoCalculado(0);
            suministroTB.setDescuentoSumado(0);

            suministroTB.setPrecioVentaGeneralUnico(valor_sin_impuesto);
            suministroTB.setPrecioVentaGeneralReal(preciocalculado);

            suministroTB.setImpuestoOperacion(tvList.getSelectionModel().getSelectedItem().getImpuestoOperacion());
            suministroTB.setImpuestoId(tvList.getSelectionModel().getSelectedItem().getImpuestoId());
            suministroTB.setImpuestoNombre(tvList.getSelectionModel().getSelectedItem().getImpuestoNombre());
            suministroTB.setImpuestoValor(tvList.getSelectionModel().getSelectedItem().getImpuestoValor());

            double impuesto = Tools.calculateTax(suministroTB.getImpuestoValor(), suministroTB.getPrecioVentaGeneralReal());
            suministroTB.setImpuestoSumado(suministroTB.getCantidad() * impuesto);
            suministroTB.setPrecioVentaGeneral(suministroTB.getPrecioVentaGeneralReal() + impuesto);
            suministroTB.setPrecioVentaGeneralAuxiliar(suministroTB.getPrecioVentaGeneral());

            suministroTB.setImporteBruto(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralUnico());
            suministroTB.setSubImporteNeto(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
            suministroTB.setImporteNeto(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneral());

            suministroTB.setInventario(tvList.getSelectionModel().getSelectedItem().isInventario());
            suministroTB.setUnidadVenta(tvList.getSelectionModel().getSelectedItem().getUnidadVenta());
            suministroTB.setValorInventario(tvList.getSelectionModel().getSelectedItem().getValorInventario());

            Button button = new Button("X");
            button.getStyleClass().add("buttonDark");
            button.setOnAction(e -> {
                postVentaEstructuraController.getTvList().getItems().remove(suministroTB);
                postVentaEstructuraController.calculateTotales();
            });
            button.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    postVentaEstructuraController.getTvList().getItems().remove(suministroTB);
                    postVentaEstructuraController.calculateTotales();
                }
            });
            suministroTB.setBtnRemove(button);
            if (postVentaEstructuraController.isCerar_modal_agregar_item_lista()) {
                Tools.Dispose(apWindow);
                postVentaEstructuraController.getAddArticulo(suministroTB, postVentaEstructuraController.getWindow().getScene().getWindow());
            } else {
                txtSearch.selectAll();
                txtSearch.requestFocus();
                postVentaEstructuraController.getAddArticulo(suministroTB, apWindow.getScene().getWindow());
            }

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
            if (tvList.getSelectionModel().getSelectedItem().getNuevaImagen() == null) {
                // File fileImage = new File(tvList.getSelectionModel().getSelectedItem().getImagenTB());
                ivPrincipal.setImage(new Image("/view/image/no-image.png"));
            } else {
                ivPrincipal.setImage(new Image(new ByteArrayInputStream(tvList.getSelectionModel().getSelectedItem().getNuevaImagen())));
            }

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

    public TextField getTxtSearch() {
        return txtSearch;
    }

    public void setInitMovimientoProcesoController(FxMovimientosProcesoController movimientosProcesoController) {
        this.movimientosProcesoController = movimientosProcesoController;
    }

    public void setInitComprasController(FxComprasController comprasController) {
        this.comprasController = comprasController;
    }

    public void setInitSuministrosKardexController(FxSuministrosKardexController suministrosKardexController) {
        this.suministrosKardexController = suministrosKardexController;
    }

    public void setInitVentaEstructuraController(FxVentaEstructuraController ventaEstructuraController) {
        this.ventaEstructuraController = ventaEstructuraController;
    }

    public void setInitPostVentaEstructuraController(FxPostVentaEstructuraController postVentaEstructuraController) {
        this.postVentaEstructuraController = postVentaEstructuraController;
    }

    public void setInitVentaUtilidadesController(FxVentaUtilidadesController ventaUtilidadesController) {
        this.ventaUtilidadesController = ventaUtilidadesController;
    }

    public void setInitCotizacionEstructuraController(FxCotizacionController cotizacionController) {
        this.cotizacionController = cotizacionController;
    }

    public void setInitGuiaRemisionController(FxGuiaRemisionController guiaRemisionController) {
        this.guiaRemisionController = guiaRemisionController;
    }

    public void setInitLoteController(FxLoteCambiarController loteCambiarController) {
        this.loteCambiarController = loteCambiarController;
    }

    public void setInitProducirProcesoController(FxProducirAgregarController producirAgregarController) {
        this.producirAgregarController = producirAgregarController;
    }

    public void setInitPedidosController(FxPedidosController pedidosController) {
        this.pedidosController = pedidosController;
    }

}
