package controller.produccion.movimientos;

import controller.contactos.proveedores.FxProveedorListaController;
import controller.inventario.articulo.FxArticuloListaController;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.ArticuloADO;
import model.ArticuloTB;
import model.MovimientoInventarioADO;
import model.MovimientoInventarioTB;
import model.TipoMovimientoADO;
import model.TipoMovimientoTB;

public class FxMovimientosProcesoArticuloController implements Initializable {

    @FXML
    private HBox hbWindow;
    @FXML
    private ComboBox<TipoMovimientoTB> cbAjuste;
    @FXML
    private TextField txtObservacion;
    @FXML
    private TableView<ArticuloTB> tvList;
    @FXML
    private TableColumn<ArticuloTB, String> tcClave;
    @FXML
    private TableColumn<ArticuloTB, String> tcCantidad;
    @FXML
    private TableColumn<ArticuloTB, String> tcCosto;
    @FXML
    private TableColumn<ArticuloTB, String> tcPrecio;
    @FXML
    private TableColumn<ArticuloTB, TextField> tcMovimiento;
    @FXML
    private TableColumn<ArticuloTB, CheckBox> tcOpcion;
    @FXML
    private RadioButton rbIncremento;
    @FXML
    private RadioButton rbDecremento;
    @FXML
    private TextField txtProveedor;
    @FXML
    private RadioButton rbCompletado;
    @FXML
    private RadioButton rbProceso;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    private FxMovimientosController movimientosController;

    private String idProveedor;

    private Alert alert = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcClave.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getClave() + "\n" + cellData.getValue().getNombreMarca()));
        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getCantidad(), 2) + " " + cellData.getValue().getUnidadCompraName()));
        tcCosto.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getCostoCompra(), 2)));
        tcPrecio.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getPrecioVentaGeneral(), 2)));
        tcMovimiento.setCellValueFactory(new PropertyValueFactory<>("movimiento"));
        tcOpcion.setCellValueFactory(new PropertyValueFactory<>("validar"));

        tcClave.prefWidthProperty().bind(tvList.widthProperty().multiply(0.29));
        tcCantidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcCosto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcPrecio.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcMovimiento.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcOpcion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));

        TipoMovimientoADO.Get_list_Tipo_Movimiento(rbIncremento.isSelected(), false).forEach(e -> {
            cbAjuste.getItems().add(new TipoMovimientoTB(e.getIdTipoMovimiento(), e.getNombre(), e.isAjuste()));
        });
        
        ToggleGroup groupAjuste = new ToggleGroup();
        rbIncremento.setToggleGroup(groupAjuste);
        rbDecremento.setToggleGroup(groupAjuste);

        ToggleGroup groupEstado = new ToggleGroup();
        rbCompletado.setToggleGroup(groupEstado);
        rbProceso.setToggleGroup(groupEstado);
    }

    private void openAlertMessageWarning(String message) {
        ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
        Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Productos", message, false);
        vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
    }

    private void registrarMovimiento() {
        if (!tvList.getItems().isEmpty()) {
            if (cbAjuste.getSelectionModel().getSelectedIndex() < 0) {
                openAlertMessageWarning("Seleccione un tipo de ajuste, por favor.");
                cbAjuste.requestFocus();
            } else if (Tools.isText(txtObservacion.getText().trim())) {
                openAlertMessageWarning("Ingrese una observación, por favor.");
                txtObservacion.requestFocus();
            } else {
                short validate = Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Suministros", "¿Está seguro de continuar?", true);
                if (validate == 1) {
                    MovimientoInventarioTB inventarioTB = new MovimientoInventarioTB();
                    inventarioTB.setFecha(Tools.getDate());
                    inventarioTB.setHora(Tools.getHour());
                    inventarioTB.setTipoAjuste(rbIncremento.isSelected());
                    inventarioTB.setTipoMovimiento(cbAjuste.getSelectionModel().getSelectedItem().getIdTipoMovimiento());
                    inventarioTB.setTipoMovimientoName(cbAjuste.getSelectionModel().getSelectedItem().getNombre());
                    inventarioTB.setObservacion(txtObservacion.getText().trim());
                    inventarioTB.setSuministro(false);
                    inventarioTB.setArticulo(true);
                    inventarioTB.setProveedor(idProveedor);
                    inventarioTB.setEstado(rbCompletado.isSelected() ? (short) 1 : (short) 0);
                    ejecutarConsulta(inventarioTB);
                } else {
                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                }
            }
        } else {
            openAlertMessageWarning("La tabla no tiene campos para continuar con el proceso.");
        }
    }

    private void ejecutarConsulta(MovimientoInventarioTB inventarioTB) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        try {
            Task<String> task = new Task<String>() {
                @Override
                public String call() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                    }
                    return MovimientoInventarioADO.Crud_Movimiento_Inventario_Articulo(inventarioTB, tvList);
                }
            };
            task.setOnScheduled(t -> {
                alert = Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.NONE, "Procesando Información...");
            });
            task.setOnFailed(t -> {
                if (alert != null) {
                    ((Stage) (alert.getDialogPane().getScene().getWindow())).close();
                }
                openAlertMessageWarning("Error en la ejecución, intente nuevamente.");
            });
            task.setOnSucceeded(t -> {
                if (!task.isRunning()) {
                    if (alert != null) {
                        ((Stage) (alert.getDialogPane().getScene().getWindow())).close();
                    }
                }
                String result = task.getValue();
                if (result.equalsIgnoreCase("registered")) {
                    Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.INFORMATION, "Proceso", "Se completo el registro correctamente.", false);
                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                    rbIncremento.setSelected(false);
                    rbIncremento.setDisable(false);
                    rbDecremento.setDisable(false);
                    cbAjuste.setDisable(false);
                    cbAjuste.getItems().clear();
                    TipoMovimientoADO.Get_list_Tipo_Movimiento(rbIncremento.isSelected(), false).forEach(e -> {
                        cbAjuste.getItems().add(new TipoMovimientoTB(e.getIdTipoMovimiento(), e.getNombre(), e.isAjuste()));
                    });
                    txtObservacion.clear();
                    tvList.getItems().clear();
                } else {
                    Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.ERROR, "Proceso", result, false);
                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                }
            });

            exec.execute(task);

        } catch (Exception ex) {
        } finally {
            exec.shutdown();
        }

    }

    public void addArticuloLista(String idSuministro) {
        System.out.println(idSuministro);
//        ObservableList<ArticuloTB> list = ArticuloADO.List_Articulo_Movimiento(idSuministro);
//        if (!list.isEmpty()) {
//            ArticuloTB articuloTB = list.get(0);
//            tvList.getItems().add(articuloTB);
//        }
    }

    private void openWindowArticulos() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_ARTICULO_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxArticuloListaController controller = fXMLLoader.getController();
            controller.setInitMovimientoProcesoArticuloController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Artículo", hbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding((WindowEvent WindowEvent) -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();
            controller.loadElements((short) 1, "");
        } catch (IOException ex) {
            System.out.println("Error en la vista ajustes:" + ex.getLocalizedMessage());
        }
    }

    private void openWindowProveedor() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_PROVEEDORES_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxProveedorListaController controller = fXMLLoader.getController();
            controller.setInitMovimientoProcesoArticuloController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Proveedor", hbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding((w) -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();
            controller.fillCustomersTable("");
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void executeEventRomever() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            short confirmation = Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Compras", "¿Esta seguro de quitar el suministro?", true);
            if (confirmation == 1) {
                ObservableList<ArticuloTB> observableList, suministroTBs;
                observableList = tvList.getItems();
                suministroTBs = tvList.getSelectionModel().getSelectedItems();
                suministroTBs.forEach(e -> {
                    observableList.remove(e);
                });
            }
        } else {
            openAlertMessageWarning("Seleccione un artículo para removerlo.");
        }
    }

    private void closeWindow() {
        vbContent.getChildren().remove(hbWindow);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(movimientosController.getHbWindow(), 0d);
        AnchorPane.setTopAnchor(movimientosController.getHbWindow(), 0d);
        AnchorPane.setRightAnchor(movimientosController.getHbWindow(), 0d);
        AnchorPane.setBottomAnchor(movimientosController.getHbWindow(), 0d);
        vbContent.getChildren().add(movimientosController.getHbWindow());
    }

    @FXML
    private void onActionRelizarMovimiento(ActionEvent event) {
        registrarMovimiento();
    }

    @FXML
    private void onActionTipo(ActionEvent event) {
        cbAjuste.getItems().clear();
        TipoMovimientoADO.Get_list_Tipo_Movimiento(rbIncremento.isSelected(), false).forEach(e -> {
            cbAjuste.getItems().add(new TipoMovimientoTB(e.getIdTipoMovimiento(), e.getNombre(), e.isAjuste()));
        });
    }

    @FXML
    private void onKeyPressedTipo(KeyEvent event) {
        if (null != event.getCode()) {
            switch (event.getCode()) {
                case UP:
                    cbAjuste.getItems().clear();
                    TipoMovimientoADO.Get_list_Tipo_Movimiento(!rbIncremento.isSelected(), false).forEach(e -> {
                        cbAjuste.getItems().add(new TipoMovimientoTB(e.getIdTipoMovimiento(), e.getNombre(), e.isAjuste()));
                    });
                    break;
                case DOWN:
                    cbAjuste.getItems().clear();
                    TipoMovimientoADO.Get_list_Tipo_Movimiento(!rbIncremento.isSelected(), false).forEach(e -> {
                        cbAjuste.getItems().add(new TipoMovimientoTB(e.getIdTipoMovimiento(), e.getNombre(), e.isAjuste()));
                    });
                    break;
                case LEFT:
                    cbAjuste.getItems().clear();
                    TipoMovimientoADO.Get_list_Tipo_Movimiento(!rbIncremento.isSelected(), false).forEach(e -> {
                        cbAjuste.getItems().add(new TipoMovimientoTB(e.getIdTipoMovimiento(), e.getNombre(), e.isAjuste()));
                    });
                    break;
                case RIGHT:
                    cbAjuste.getItems().clear();
                    TipoMovimientoADO.Get_list_Tipo_Movimiento(!rbIncremento.isSelected(), false).forEach(e -> {
                        cbAjuste.getItems().add(new TipoMovimientoTB(e.getIdTipoMovimiento(), e.getNombre(), e.isAjuste()));
                    });
                    break;
                default:
                    break;
            }
        }
    }

    private void onMouseClickedBehind(MouseEvent event) {
        closeWindow();
    }

    @FXML
    private void onKeyPressedProveedor(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowProveedor();
        }
    }

    @FXML
    private void onActionProveedor(ActionEvent event) {
        openWindowProveedor();
    }

    @FXML
    private void onKeyPressedArticulo(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowArticulos();
        }
    }

    @FXML
    private void onActionArticulo(ActionEvent event) {
        openWindowArticulos();
    }

    @FXML
    private void onKeyPressedRemover(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            executeEventRomever();
        }
    }

    @FXML
    private void onActionRemover(ActionEvent event) {
        executeEventRomever();
    }

    public TableView<ArticuloTB> getTvList() {
        return tvList;
    }

    public void setInitProveedor(String idProveedor, String datos) {
        this.idProveedor = idProveedor;
        txtProveedor.setText(datos);
    }

    public void setContent(FxMovimientosController movimientosController, AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.movimientosController = movimientosController;
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
