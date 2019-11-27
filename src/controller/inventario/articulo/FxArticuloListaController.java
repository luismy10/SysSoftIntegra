package controller.inventario.articulo;

import controller.inventario.kardex.FxArticuloKardexController;
import controller.ingresos.venta.FxVentaController;
import controller.produccion.asignacion.FxAsignacionController;
import controller.produccion.asignacion.FxAsignacionProcesoController;
import controller.produccion.movimientos.FxMovimientosProcesoArticuloController;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.ArticuloADO;
import model.ArticuloTB;

public class FxArticuloListaController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<ArticuloTB> tvList;
    @FXML
    private TableColumn<ArticuloTB, Integer> tcId;
    @FXML
    private TableColumn<ArticuloTB, String> tcArticulo;
    @FXML
    private TableColumn<ArticuloTB, String> tcMarca;
    @FXML
    private TableColumn<ArticuloTB, String> tcExistencias;
    @FXML
    private TableColumn<ArticuloTB, String> tcPrecio;
    @FXML
    private TableColumn<ArticuloTB, String> tcOrigen;

    private boolean stateRequest;

    private FxVentaController ventaController;

    private FxArticuloReportesController articuloReportesController;

    private FxArticuloKardexController articuloKardexController;

    private FxMovimientosProcesoArticuloController movimientosProcesoArticuloController;

    private FxAsignacionController asignacionController;

    private FxAsignacionProcesoController asignacionProcesoController;

    private short opcion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);

        tcId.setCellValueFactory(cellData ->new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        tcArticulo.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getClave() + "\n" + cellData.getValue().getNombreMarca()
        )
        );
        tcMarca.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMarcaName()));
        tcExistencias.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getCantidad(), 2) + " " + cellData.getValue().getUnidadCompraName()
        ));
        tcPrecio.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getPrecioVentaGeneral(), 2)));
        tcOrigen.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getOrigenName()
        ));
        stateRequest = false;
    }

    public void loadElements(short opcion, String value) {
        this.opcion = opcion;
        fillProvidersTable(opcion, value);
    }

    private void fillProvidersTable(short opcion, String value) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ObservableList<ArticuloTB>> task = new Task<ObservableList<ArticuloTB>>() {
            @Override
            public ObservableList<ArticuloTB> call() {
                //return ArticuloADO.ListArticulosListaView(opcion, value);
                return null;
            }
        };

        task.setOnSucceeded((WorkerStateEvent e) -> {
            tvList.setItems(task.getValue());
            stateRequest = true;
        });

        task.setOnFailed((WorkerStateEvent e) -> {
            stateRequest = false;
        });
        task.setOnScheduled((WorkerStateEvent e) -> {
            stateRequest = false;
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void addArticuloToList() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
//            ArticuloTB articuloTB = new ArticuloTB();
//            articuloTB.setIdArticulo(tvList.getSelectionModel().getSelectedItem().getIdArticulo());
//            articuloTB.setClave(tvList.getSelectionModel().getSelectedItem().getClave());
//            articuloTB.setNombreMarca(tvList.getSelectionModel().getSelectedItem().getNombreMarca());
//            articuloTB.setCantidad(1);
//            articuloTB.setCostoCompra(tvList.getSelectionModel().getSelectedItem().getCostoCompra());
//
//            articuloTB.setDescuento(0);
//            articuloTB.setDescuentoSumado(0);
//
//            articuloTB.setPrecioVentaGeneralReal(tvList.getSelectionModel().getSelectedItem().getPrecioVentaGeneral());
//            articuloTB.setPrecioVentaGeneral(tvList.getSelectionModel().getSelectedItem().getPrecioVentaGeneral());
//            articuloTB.setPrecioVentaAuxiliar(tvList.getSelectionModel().getSelectedItem().getPrecioVentaGeneral());
//            articuloTB.setPrecioVentaUnico(tvList.getSelectionModel().getSelectedItem().getPrecioVentaGeneral());
//
//            articuloTB.setSubImporte(1 * tvList.getSelectionModel().getSelectedItem().getPrecioVentaGeneral());
//            articuloTB.setTotalImporte(1 * tvList.getSelectionModel().getSelectedItem().getPrecioVentaGeneral());
//
//            articuloTB.setInventario(tvList.getSelectionModel().getSelectedItem().isInventario());
//            articuloTB.setUnidadVenta(tvList.getSelectionModel().getSelectedItem().getUnidadVenta());
//            articuloTB.setValorInventario(tvList.getSelectionModel().getSelectedItem().isValorInventario());
//
//            articuloTB.setImpuestoArticulo(tvList.getSelectionModel().getSelectedItem().getImpuestoArticulo());
//            articuloTB.setImpuestoArticuloName(ventaController.getTaxName(tvList.getSelectionModel().getSelectedItem().getImpuestoArticulo()));
//            articuloTB.setImpuestoValor(ventaController.getTaxValue(tvList.getSelectionModel().getSelectedItem().getImpuestoArticulo()));
//            articuloTB.setImpuestoSumado(articuloTB.getCantidad() * (articuloTB.getPrecioVentaGeneral() * (articuloTB.getImpuestoValor() / 100.00)));
//
//            articuloTB.setSubImporteDescuento(articuloTB.getSubImporte() - articuloTB.getDescuentoSumado());
//            Tools.Dispose(window);
//            ventaController.getAddArticulo(articuloTB);
        }
    }

    private void executeEventObject() {
        if (ventaController != null) {
            addArticuloToList();
        } else if (articuloReportesController != null) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                ArticuloTB articuloTB = new ArticuloTB();
                articuloTB.setClave(tvList.getSelectionModel().getSelectedItem().getClave());
                articuloTB.setNombreMarca(tvList.getSelectionModel().getSelectedItem().getNombreMarca());
                articuloTB.setUnidadVenta(tvList.getSelectionModel().getSelectedItem().getUnidadVenta());
                articuloReportesController.getTvList().getItems().add(articuloTB);
                Tools.Dispose(window);
            }

        } else if (articuloKardexController != null) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                articuloKardexController.fillKardexTable(tvList.getSelectionModel().getSelectedItem().getClave());
                Tools.Dispose(window);
            }
        } else if (movimientosProcesoArticuloController != null) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                if (!validateStock(movimientosProcesoArticuloController.getTvList(), tvList.getSelectionModel().getSelectedItem())) {
                    movimientosProcesoArticuloController.addArticuloLista(tvList.getSelectionModel().getSelectedItem().getIdArticulo());
                    Tools.Dispose(window);
                } else {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Movimiento", "Ya hay un artículo con las mismas características.", false);
                }
            }
        } else if (asignacionProcesoController != null) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                asignacionProcesoController.setCargarArticulo(tvList.getSelectionModel().getSelectedItem().getClave() + " " + tvList.getSelectionModel().getSelectedItem().getNombreMarca(),
                        tvList.getSelectionModel().getSelectedItem().getIdArticulo());
                Tools.Dispose(window);
            }
        }
    }

    private boolean validateStock(TableView<ArticuloTB> view, ArticuloTB articuloTB) {
        boolean ret = false;
        for (int i = 0; i < view.getItems().size(); i++) {
            if (view.getItems().get(i).getIdArticulo().equals(articuloTB.getIdArticulo())) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) {
        if (event.getClickCount() == 2) {
            executeEventObject();
        }
    }

    @FXML
    private void onKeyPressedList(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            executeEventObject();
        }
    }

    @FXML
    private void onKeyReleasedToSearch(KeyEvent event) {
        if (stateRequest) {
            fillProvidersTable(opcion, txtSearch.getText());
        }
    }

    @FXML
    private void onKeyPressedToSearh(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!tvList.getItems().isEmpty()) {
                tvList.requestFocus();
                tvList.getSelectionModel().select(0);
            }
        }
    }

    @FXML
    private void onKeyPressedReload(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (stateRequest) {
                fillProvidersTable(opcion, "");
            }
        }
    }

    @FXML
    private void onActionReload(ActionEvent event) {
        if (stateRequest) {
            fillProvidersTable(opcion, "");
        }
    }

    public void setInitVentasController(FxVentaController ventaController) {
        this.ventaController = ventaController;
    }

    public void setInitReporteArticuloController(FxArticuloReportesController articuloReportesController) {
        this.articuloReportesController = articuloReportesController;
    }

    public void setInitArticuloKardexController(FxArticuloKardexController articuloKardexController) {
        this.articuloKardexController = articuloKardexController;
    }

    public void setInitMovimientoProcesoArticuloController(FxMovimientosProcesoArticuloController movimientosProcesoArticuloController) {
        this.movimientosProcesoArticuloController = movimientosProcesoArticuloController;
    }

    public void setInitAsignacionController(FxAsignacionController asignacionController) {
        this.asignacionController = asignacionController;
    }

    public void setInitAsignacionProcesoController(FxAsignacionProcesoController asignacionProcesoController) {
        this.asignacionProcesoController = asignacionProcesoController;
    }

}
