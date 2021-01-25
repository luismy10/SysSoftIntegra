package controller.inventario.movimientos;

import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
import model.AjusteInventarioADO;
import model.AjusteInventarioTB;
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
    private TableView<AjusteInventarioTB> tvList;
    @FXML
    private TableColumn<AjusteInventarioTB, String> tcNumero;
    @FXML
    private TableColumn<AjusteInventarioTB, String> tcTipoMovimiento;
    @FXML
    private TableColumn<AjusteInventarioTB, String> tcFecha;
    @FXML
    private TableColumn<AjusteInventarioTB, String> tcObservacion;
    @FXML
    private TableColumn<AjusteInventarioTB, String> tcInformacion;
    @FXML
    private TableColumn<AjusteInventarioTB, Label> tcEstado;
    @FXML
    private TableColumn<AjusteInventarioTB, Button> tcOpcion;
    @FXML
    private DatePicker dtFechaInicio;
    @FXML
    private DatePicker dtFechaFinal;
    @FXML
    private Button btnRealizarMovimiento;
    @FXML
    private Label lblPaginaActual;
    @FXML
    private Label lblPaginaSiguiente;

    private FXMLLoader fXMLLoaderMovimientoDetalle;

    private HBox nodeMovimientoDetalle;

    private FxMovimientosProcesoController controllerMovimientoDetalle;

    private ObservableList<PrivilegioTB> privilegioTBs;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    private short opcion;

    private int paginacion;

    private int totalPaginacion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcNumero.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcTipoMovimiento.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getTipoMovimientoName()));
        tcFecha.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getFecha() + " " + cellData.getValue().getHora()));
        tcObservacion.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getObservacion()));
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

    public void loadInitTable() {
        paginacion = 1;
        fillTableMovimiento(false, 0, Tools.getDate(), Tools.getDate());
        opcion = 0;
    }

    private void fillTableMovimiento(boolean init, int movimiento, String fechaInicial, String fechaFinal) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                return AjusteInventarioADO.ListMovimientoInventario(init, (short) 1, movimiento, fechaInicial, fechaFinal, (paginacion - 1) * 10, 10);
            }
        };

        task.setOnScheduled((WorkerStateEvent e) -> {
            lblLoad.setVisible(true);
        });
        task.setOnSucceeded((WorkerStateEvent e) -> {
            ArrayList<Object> objects = task.getValue();
            if (!objects.isEmpty()) {
                ObservableList<AjusteInventarioTB> inventarioTBs = (ObservableList<AjusteInventarioTB>) objects.get(0);
                for (AjusteInventarioTB mi : inventarioTBs) {
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

                int integer = (int) (Math.ceil(((Integer) objects.get(1)) / 10.00));
                totalPaginacion = integer;
                lblPaginaActual.setText(paginacion + "");
                lblPaginaSiguiente.setText(totalPaginacion + "");
                lblLoad.setVisible(false);
            } else {
                tvList.getItems().clear();
                lblLoad.setVisible(false);
            }
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
    }

    public void onEventPaginacion() {
        switch (opcion) {
            case 0:
                fillTableMovimiento(false, 0, "", "");
                break;
            case 1:
                fillTableMovimiento(true, cbMovimiento.getSelectionModel().getSelectedItem().getIdTipoMovimiento(), Tools.getDatePicker(dtFechaInicio), Tools.getDatePicker(dtFechaFinal));
                break;
        }
    }

    @FXML
    private void onActionRelizarMovimiento(ActionEvent event) {
        openWindowRealizarMovimientoProducto();

    }

    @FXML
    private void onKeyPressedRealizarMovimiento(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowRealizarMovimientoProducto();
        }
    }

    @FXML
    private void onKeyPressedReload(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                paginacion = 1;
                loadInitTable();
                opcion = 0;
            }
        }
    }

    @FXML
    private void onActionReload(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            paginacion = 1;
            loadInitTable();
            opcion = 0;
        }
    }

    @FXML
    private void onActionTipoMovimiento(ActionEvent event) {
        if (cbMovimiento.getSelectionModel().getSelectedIndex() >= 0) {
            if (dtFechaInicio.getValue() != null && dtFechaFinal.getValue() != null) {
                if (!lblLoad.isVisible()) {
                    paginacion = 1;
                    fillTableMovimiento(true, cbMovimiento.getSelectionModel().getSelectedItem().getIdTipoMovimiento(), Tools.getDatePicker(dtFechaInicio), Tools.getDatePicker(dtFechaFinal));
                    opcion = 1;
                }
            }
        }
    }

    @FXML
    private void onActionFecha(ActionEvent event) {
        if (dtFechaInicio.getValue() != null && dtFechaFinal.getValue() != null) {
            if (!lblLoad.isVisible()) {
                paginacion = 1;
                fillTableMovimiento(true, cbMovimiento.getSelectionModel().getSelectedItem().getIdTipoMovimiento(), Tools.getDatePicker(dtFechaInicio), Tools.getDatePicker(dtFechaFinal));
                opcion = 1;
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

    public void setOpcion(short opcion) {
        this.opcion = opcion;
    }

    public HBox getHbWindow() {
        return hbWindow;
    }

    public TableView<AjusteInventarioTB> getTvList() {
        return tvList;
    }

    public void setContent(AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
