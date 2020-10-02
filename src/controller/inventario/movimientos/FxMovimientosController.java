package controller.inventario.movimientos;

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
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.MovimientoInventarioADO;
import model.MovimientoInventarioTB;
import model.PrivilegioTB;
import model.TipoMovimientoADO;
import model.TipoMovimientoTB;

public class FxMovimientosController implements Initializable {

    @FXML
    private HBox hbWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private ComboBox<TipoMovimientoTB> cbMovimiento;
    @FXML
    private TableView<MovimientoInventarioTB> tvList;
    @FXML
    private TableColumn<MovimientoInventarioTB, String> tcNumero;
    @FXML
    private TableColumn<MovimientoInventarioTB, String> tcTipoMovimiento;
    @FXML
    private TableColumn<MovimientoInventarioTB, String> tcFecha;
    @FXML
    private TableColumn<MovimientoInventarioTB, String> tcObservacion;
    @FXML
    private TableColumn<MovimientoInventarioTB, String> tcInformacion;
    @FXML
    private TableColumn<MovimientoInventarioTB, Label> tcEstado;
    @FXML
    private TableColumn<MovimientoInventarioTB, Button> tcOpcion;
    @FXML
    private DatePicker dtFechaInicio;
    @FXML
    private DatePicker dtFechaFinal;
    @FXML
    private Button btnRealizarMovimiento;

    private FXMLLoader fXMLLoaderMovimientoDetalle;

    private HBox nodeMovimientoDetalle;

    private FxMovimientosProcesoController controllerMovimientoDetalle;

    private ObservableList<PrivilegioTB> privilegioTBs;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    private short opcion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcNumero.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcTipoMovimiento.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getTipoMovimientoName()));
        tcFecha.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getFecha() + " " + cellData.getValue().getHora()));
        tcObservacion.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getObservacion() + "\n" + cellData.getValue().getProveedor()));
        tcInformacion.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getInformacion()));
        tcEstado.setCellValueFactory(new PropertyValueFactory<>("lblEstado"));
        tcOpcion.setCellValueFactory(new PropertyValueFactory<>("validar"));

        tcNumero.prefWidthProperty().bind(tvList.widthProperty().multiply(0.06));
        tcTipoMovimiento.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        tcFecha.prefWidthProperty().bind(tvList.widthProperty().multiply(0.16));
        tcObservacion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.20));
        tcInformacion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.20));
        tcEstado.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        tcOpcion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.10));

        cbMovimiento.getItems().add(new TipoMovimientoTB(0, "--TODOS--", false));
        TipoMovimientoADO.Get_list_Tipo_Movimiento(true, true).forEach(e -> {
            cbMovimiento.getItems().add(new TipoMovimientoTB(e.getIdTipoMovimiento(), e.getNombre(), e.isAjuste()));
        });
        cbMovimiento.getSelectionModel().select(0);

        Tools.actualDate(Tools.getDate(), dtFechaInicio);
        Tools.actualDate(Tools.getDate(), dtFechaFinal);
        try {
            fXMLLoaderMovimientoDetalle = WindowStage.LoaderWindow(getClass().getResource(FilesRouters.FX_MOVIMIENTOS_PROCESO));
            nodeMovimientoDetalle = fXMLLoaderMovimientoDetalle.load();
            controllerMovimientoDetalle = fXMLLoaderMovimientoDetalle.getController();
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    public void loadPrivilegios(ObservableList<PrivilegioTB> privilegioTBs) {
        this.privilegioTBs = privilegioTBs;
        if (privilegioTBs.get(0).getIdPrivilegio() != 0 && !privilegioTBs.get(0).isEstado()) {
            btnRealizarMovimiento.setDisable(!privilegioTBs.get(0).isEstado());
        }
    }

    public void fillTableMovimiento(boolean init, short opcion, int movimiento, String fechaInicial, String fechaFinal) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ObservableList<MovimientoInventarioTB>> task = new Task<ObservableList<MovimientoInventarioTB>>() {
            @Override
            public ObservableList<MovimientoInventarioTB> call() {
                return MovimientoInventarioADO.ListMovimientoInventario(init, opcion, movimiento, fechaInicial, fechaFinal);
            }
        };

        task.setOnScheduled((WorkerStateEvent e) -> {
            lblLoad.setVisible(true);
        });
        task.setOnSucceeded((WorkerStateEvent e) -> {
            ObservableList<MovimientoInventarioTB> inventarioTBs = task.getValue();
            for (MovimientoInventarioTB mi : inventarioTBs) {
                mi.getValidar().setOnAction(event -> {
                    openWindowMovimientoDetalle(mi.getIdMovimientoInventario());
                });
                mi.getValidar().setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        openWindowMovimientoDetalle(mi.getIdMovimientoInventario());
                    }
                });
            }
            tvList.setItems(inventarioTBs);
            lblLoad.setVisible(false);
        });
        task.setOnFailed((WorkerStateEvent e) -> {
            lblLoad.setVisible(false);
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void executeReload() {

        Tools.actualDate(Tools.getDate(), dtFechaInicio);
        Tools.actualDate(Tools.getDate(), dtFechaFinal);

        cbMovimiento.getItems().clear();

        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ObservableList<MovimientoInventarioTB>> task = new Task<ObservableList<MovimientoInventarioTB>>() {
            @Override
            public ObservableList<MovimientoInventarioTB> call() {
                cbMovimiento.getItems().add(new TipoMovimientoTB(0, "--TODOS--", false));
                TipoMovimientoADO.Get_list_Tipo_Movimiento(true, true).forEach(e -> {
                    cbMovimiento.getItems().add(new TipoMovimientoTB(e.getIdTipoMovimiento(), e.getNombre(), e.isAjuste()));
                });

                return MovimientoInventarioADO.ListMovimientoInventario(false, opcion,
                        cbMovimiento.getSelectionModel().getSelectedIndex() >= 0
                        ? cbMovimiento.getSelectionModel().getSelectedItem().getIdTipoMovimiento()
                        : 0, Tools.getDatePicker(dtFechaInicio), Tools.getDatePicker(dtFechaFinal)
                );
            }
        };

        task.setOnScheduled((WorkerStateEvent e) -> {
            lblLoad.setVisible(true);
        });
        task.setOnSucceeded((WorkerStateEvent e) -> {
            ObservableList<MovimientoInventarioTB> inventarioTBs = task.getValue();
            for (MovimientoInventarioTB mi : inventarioTBs) {
                mi.getValidar().setOnAction(event -> {
                    openWindowMovimientoDetalle(mi.getIdMovimientoInventario());
                });
                mi.getValidar().setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        openWindowMovimientoDetalle(mi.getIdMovimientoInventario());
                    }
                });
            }
            tvList.setItems(inventarioTBs);
            lblLoad.setVisible(false);
        });
        task.setOnFailed((WorkerStateEvent e) -> {
            lblLoad.setVisible(false);
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void openWindowMovimientoDetalle(String idMovimiento) {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = FxMovimientosController.class.getClassLoader().getClass().getResource(FilesRouters.FX_MOVIMIENTOS_DETALLE);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxMovimientosDetalleController controller = fXMLLoader.getController();
            controller.setIniciarCarga(idMovimiento);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Detalle del movimiento", hbWindow.getScene().getWindow());
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

    private void openWindowRealizarMovimientoProducto() {
        controllerMovimientoDetalle.setContent(this, vbPrincipal, vbContent);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeMovimientoDetalle, 0d);
        AnchorPane.setTopAnchor(nodeMovimientoDetalle, 0d);
        AnchorPane.setRightAnchor(nodeMovimientoDetalle, 0d);
        AnchorPane.setBottomAnchor(nodeMovimientoDetalle, 0d);
        vbContent.getChildren().add(nodeMovimientoDetalle);
        //controller.setInitArticulo();
    }

    private void openWindowRealizarMovimientoArticulo() {
//        try {
//            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(getClass().getResource(FilesRouters.FX_MOVIMIENTOS_PROCESO_ARTICULO));
//            HBox node = fXMLLoader.load();
//            //Controlller here
//            FxMovimientosProcesoArticuloController controller = fXMLLoader.getController();
//            controller.setContent(this, vbPrincipal, vbContent);
//
//            //
//            vbContent.getChildren().clear();
//            AnchorPane.setLeftAnchor(node, 0d);
//            AnchorPane.setTopAnchor(node, 0d);
//            AnchorPane.setRightAnchor(node, 0d);
//            AnchorPane.setBottomAnchor(node, 0d);
//            vbContent.getChildren().add(node);
//            //
//            //controller.setInitArticulo();
//        } catch (IOException ex) {
//            System.out.println(ex.getLocalizedMessage());
//        }

    }

    @FXML
    private void onActionRelizarMovimiento(ActionEvent event) {
        if (opcion == 1) {
            openWindowRealizarMovimientoProducto();
        } else {
            openWindowRealizarMovimientoArticulo();
        }

    }

    @FXML
    private void onKeyPressedRealizarMovimiento(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (opcion == 1) {
                openWindowRealizarMovimientoProducto();
            } else {
                openWindowRealizarMovimientoArticulo();
            }
        }
    }

    @FXML
    private void onActionTipoMovimiento(ActionEvent event) {
        if (cbMovimiento.getSelectionModel().getSelectedIndex() >= 0) {
            if (dtFechaInicio.getValue() != null && dtFechaFinal.getValue() != null) {
                if (!lblLoad.isVisible()) {
                    fillTableMovimiento(true, opcion, cbMovimiento.getSelectionModel().getSelectedItem().getIdTipoMovimiento(), Tools.getDatePicker(dtFechaInicio), Tools.getDatePicker(dtFechaFinal));
                }
            }
        }
    }

    @FXML
    private void onKeyPressedReload(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                executeReload();
            }
        }
    }

    @FXML
    private void onActionReload(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            executeReload();
        }
    }

    @FXML
    private void onActionFecha(ActionEvent event) {
        if (dtFechaInicio.getValue() != null && dtFechaFinal.getValue() != null) {
            if (!lblLoad.isVisible()) {
                fillTableMovimiento(true, opcion, cbMovimiento.getSelectionModel().getSelectedIndex() >= 0
                        ? cbMovimiento.getSelectionModel().getSelectedItem().getIdTipoMovimiento()
                        : 0, Tools.getDatePicker(dtFechaInicio), Tools.getDatePicker(dtFechaFinal));
            }
        }
    }

    public void setOpcion(short opcion) {
        this.opcion = opcion;
    }

    public HBox getHbWindow() {
        return hbWindow;
    }

    public TableView<MovimientoInventarioTB> getTvList() {
        return tvList;
    }

    public void setContent(AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
