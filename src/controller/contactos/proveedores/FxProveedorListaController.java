package controller.contactos.proveedores;

import controller.operaciones.compras.FxComprasController;
import controller.consultas.compras.FxComprasEditarController;
import controller.inventario.movimientos.FxMovimientosProcesoController;
import controller.reporte.FxCompraReporteController;
import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.ProveedorADO;
import model.ProveedorTB;

public class FxProveedorListaController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<ProveedorTB> tvList;
    @FXML
    private TableColumn<ProveedorTB, Integer> tcId;
    @FXML
    private TableColumn<ProveedorTB, String> tcDocument;
    @FXML
    private TableColumn<ProveedorTB, String> tcRepresentative;
    @FXML
    private TableColumn<ProveedorTB, String> tcMovil;

    private FxComprasController comprasController;

    private FxMovimientosProcesoController movimientosProcesoController;

    private FxComprasEditarController comprasEditarController;

    private FxCompraReporteController compraReporteController;

    private boolean status;

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
                        openWindowAddProveedor();
                        break;
                    case F3:
                        if (!status) {
                            fillCustomersTable("");
                        }
                        break;

                }
            }
        });

        tcId.setCellValueFactory(cellData -> cellData.getValue().getId().asObject());
        tcDocument.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getTipoDocumentoName() + ": " + cellData.getValue().getNumeroDocumento())
        );
        tcRepresentative.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getRazonSocial()
        ));
        tcMovil.setCellValueFactory(cellData -> Bindings.concat(
                (cellData.getValue().getTelefono().equals("") ? "Sin N° de Teléfono" : cellData.getValue().getTelefono()).toUpperCase()
                + "\n"
                + (cellData.getValue().getCelular().equals("") ? "Sin N° de Celular" : cellData.getValue().getCelular()).toUpperCase()
        ));
    }

    public void fillCustomersTable(String value) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<List<ProveedorTB>> task = new Task<List<ProveedorTB>>() {
            @Override
            public ObservableList<ProveedorTB> call() {
                return ProveedorADO.ListProveedor(value);
            }
        };

        task.setOnSucceeded((e) -> {
            tvList.setItems((ObservableList<ProveedorTB>) task.getValue());
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

    private void openWindowAddProveedor() {
        try {
            URL url = getClass().getResource(FilesRouters.FX_PROVEEDORES_PROCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxProveedorProcesoController controller = fXMLLoader.getController();
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Proveedor", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.show();
            controller.setValueAdd();
        } catch (IOException ex) {
            System.out.println("openWindowAddProveedor():" + ex.getLocalizedMessage());
        }
    }

    private void executeEvent() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            if (comprasController != null) {
                comprasController.setLoadProveedor(tvList.getSelectionModel().getSelectedItem().getIdProveedor());
                Tools.Dispose(apWindow);
            } else if (movimientosProcesoController != null) {
                movimientosProcesoController.setInitProveedor(tvList.getSelectionModel().getSelectedItem().getIdProveedor(),
                        tvList.getSelectionModel().getSelectedItem().getRazonSocial());
                Tools.Dispose(apWindow);
            } else if (comprasEditarController != null) {
                comprasEditarController.setInitComprasValue(tvList.getSelectionModel().getSelectedItem().getNumeroDocumento(),
                        tvList.getSelectionModel().getSelectedItem().getRazonSocial());
                Tools.Dispose(apWindow);
            } else if (compraReporteController != null) {
                compraReporteController.setInitCompraReporteValue(tvList.getSelectionModel().getSelectedItem().getIdProveedor(),
                        tvList.getSelectionModel().getSelectedItem().getRazonSocial());
                Tools.Dispose(apWindow);
            }
        }
    }

    @FXML
    private void onKeyReleasedToSearch(KeyEvent event) {
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
                fillCustomersTable(txtSearch.getText());
            }
        }
    }

    @FXML
    private void onKeyPressedToSearh(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            tvList.requestFocus();
        }
    }

    @FXML
    private void onKeyPressedToRegister(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowAddProveedor();
        }
    }

    @FXML
    private void onActionToRegister(ActionEvent event) throws IOException {
        openWindowAddProveedor();
    }

    @FXML
    private void onKeyPressedToReload(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!status) {
                fillCustomersTable("");
            }
        }
    }

    @FXML
    private void onActionToReload(ActionEvent event) {
        if (!status) {
            fillCustomersTable("");
        }
    }

    @FXML
    private void onKeyPressedList(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            executeEvent();
        }
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) {
        if (event.getClickCount() == 2) {
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

    public void setInitComprasController(FxComprasController comprasController) {
        this.comprasController = comprasController;
    }

    public void setInitMovimientoProcesoController(FxMovimientosProcesoController movimientosProcesoController) {
        this.movimientosProcesoController = movimientosProcesoController;
    }

    public void setInitComprasEditarController(FxComprasEditarController comprasEditarController) {
        this.comprasEditarController = comprasEditarController;
    }

    public void setInitComprasReporteController(FxCompraReporteController compraReporteController) {
        this.compraReporteController = compraReporteController;
    }

}
