package controller.produccion.asignacion;

import controller.inventario.articulo.FxArticuloListaController;
import controller.produccion.suministros.FxSuministrosListaController;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.SuministroADO;
import model.SuministroTB;

public class FxAsignacionProcesoController implements Initializable {

    @FXML
    private HBox hbWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private Label lblArticulo;
    @FXML
    private TableView<SuministroTB> tvList;
    @FXML
    private TableColumn<SuministroTB, String> tcDescripcion;
    @FXML
    private TableColumn<SuministroTB, String> tcCantidad;
    @FXML
    private TableColumn<SuministroTB, String> tcMedida;
    @FXML
    private TableColumn<SuministroTB, String> tcCosto;
    @FXML
    private TableColumn<SuministroTB, String> tcPrecio;
    @FXML
    private TableColumn<SuministroTB, TextField> tcMovimiento;
    @FXML
    private TableColumn<SuministroTB, Label> tcEstado;
    @FXML
    private HBox hbAsignacion;
    @FXML
    private Button btnRegister;
    @FXML
    private Button btnProducto;
    @FXML
    private Label lblCantidad;
    @FXML
    private Label lblCostoPromedio;
    @FXML
    private Label lblPrecioPromedio;
    @FXML
    private Label lblCostoTotal;
    @FXML
    private Label lblPrecioTotal;
    @FXML
    private Label lblUtilidad;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    private FxAsignacionController asignacionController;

    private String idArticulo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcDescripcion.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getClave() + "\n" + cellData.getValue().getNombreMarca()));
        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getCantidad(), 2)));
        tcMedida.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getUnidadCompraName()));
        tcCosto.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getCostoCompra(), 2)));
        tcPrecio.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getPrecioVentaGeneral(), 2)));
        tcMovimiento.setCellValueFactory(new PropertyValueFactory<>("movimiento"));
        tcEstado.setCellValueFactory(new PropertyValueFactory<>("estadoAsignacion"));

        tcDescripcion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.27));
        tcCantidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.11));
        tcMedida.prefWidthProperty().bind(tvList.widthProperty().multiply(0.11));
        tcCosto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.11));
        tcPrecio.prefWidthProperty().bind(tvList.widthProperty().multiply(0.11));
        tcMovimiento.prefWidthProperty().bind(tvList.widthProperty().multiply(0.11));
        tcEstado.prefWidthProperty().bind(tvList.widthProperty().multiply(0.16));
    }

    public void setCargarArticulo(String datos, String idArticulo) {
        lblArticulo.setText(datos);
        this.idArticulo = idArticulo;
        hbAsignacion.setDisable(false);
    }

    public void addSuministroLista(String idSuministro) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<SuministroTB> task = new Task<SuministroTB>() {
            @Override
            public SuministroTB call() {
                return SuministroADO.Get_Suministros_For_Asignacion_By_Id(idSuministro);
            }
        };

        task.setOnScheduled(e -> {
            btnProducto.setDisable(true);
            lblLoad.setVisible(true);
        });

        task.setOnFailed(e -> {
            btnProducto.setDisable(false);
            lblLoad.setVisible(false);
        });

        task.setOnSucceeded(e -> {
            SuministroTB suministroTB = task.getValue();
            tvList.getItems().add(suministroTB);
            btnProducto.setDisable(false);
            lblLoad.setVisible(false);
            calculateTotals();
            btnProducto.requestFocus();
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void executeRegister() {
        if (Tools.isText(idArticulo)) {
            Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Asignación", "Seleccione un artículo para continuar.", false);
        } else if (tvList.getItems().isEmpty()) {
            Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Asignación", "La lista no puede estar vacía.", false);
            btnProducto.requestFocus();
        } else {
            short option = Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Asignación", "¿Está seguro de continuar?", true);
            if (option == 1) {

                ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
                    Thread t = new Thread(runnable);
                    t.setDaemon(true);
                    return t;
                });

                Task<String> task = new Task<String>() {
                    @Override
                    public String call() {
//                        return SuministroADO.Registrar_Asignacion_Productos_Articulo(idArticulo, tvList);
                          return null;
                    }
                };

                task.setOnScheduled(e -> {
                    btnRegister.setDisable(true);
                });

                task.setOnFailed(e -> {
                    btnRegister.setDisable(false);
                });

                task.setOnSucceeded(e -> {
                    String result = task.getValue();
                    if (result.equalsIgnoreCase("inserted")) {
                        hbAsignacion.setDisable(true);
                        btnRegister.setDisable(false);
                        lblArticulo.setText("Artículo elegido");
                        idArticulo = "";
                        tvList.getItems().clear();
                        calculateTotals();
                        Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.INFORMATION, "Asignación", "Se registro los cambios correctamente.", false);
                    } else {
                        Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.ERROR, "Asignación", result, false);
                    }
                });

                exec.execute(task);
                if (!exec.isShutdown()) {
                    exec.shutdown();
                }

            }
        }
    }

    private void openWindowSuministros() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_SUMINISTROS_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxSuministrosListaController controller = fXMLLoader.getController();
            controller.setInitAsignacionProcesoController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Producto", hbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();
            controller.fillSuministrosTablePaginacion();
        } catch (IOException ex) {
            System.out.println("Error en la vista productos lista:" + ex.getLocalizedMessage());
        }
    }

    private void openWindowArticulos() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_ARTICULO_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxArticuloListaController controller = fXMLLoader.getController();
            controller.setInitAsignacionProcesoController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Artículo", hbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding((w) -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();
            controller.loadElements((short) 2, "");
        } catch (IOException ex) {
            System.out.println("Error en la vista ajustes:" + ex.getLocalizedMessage());
        }
    }

    private void removeProducto() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            short confirmation = Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Asignación", "¿Esta seguro de quitar el producto?", true);
            if (confirmation == 1) {
                ObservableList<SuministroTB> observableList, suministroTBs;
                observableList = tvList.getItems();
                suministroTBs = tvList.getSelectionModel().getSelectedItems();
                suministroTBs.forEach(e -> {
                    observableList.remove(e);
                });
                calculateTotals();
            }
        } else {
            Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Asignación", "Seleccione un artículo para removerlo", false);
        }
    }

    private void recalculate() {
        calculateTotals();
    }

    private void calculateTotals() {
        if (!tvList.getItems().isEmpty()) {
            double cantidad = 0;
            double costoPromedio = 0;
            double precioPromedio = 0;
            double costoTotal;
            double precioTotal;
            double utilidad;
            int rows = tvList.getItems().size();
            for (int i = 0; i < tvList.getItems().size(); i++) {
                cantidad += tvList.getItems().get(i).getMovimiento();
                costoPromedio += tvList.getItems().get(i).getCostoCompra();
                precioPromedio += tvList.getItems().get(i).getPrecioVentaGeneral();
            }
            costoTotal = (costoPromedio / rows) * cantidad;
            precioTotal = (precioPromedio / rows) * cantidad;
            utilidad = precioTotal - costoTotal;
            lblCantidad.setText(Tools.roundingValue(cantidad, 2));
            lblCostoPromedio.setText(Tools.roundingValue(costoPromedio / rows, 2));
            lblPrecioPromedio.setText(Tools.roundingValue(precioPromedio / rows, 2));
            lblCostoTotal.setText(Tools.roundingValue(costoTotal, 2));
            lblPrecioTotal.setText(Tools.roundingValue(precioTotal, 2));
            lblUtilidad.setText(Tools.roundingValue(utilidad, 2));
        } else {
            lblCantidad.setText(Tools.roundingValue(0, 2));
            lblCostoPromedio.setText(Tools.roundingValue(0, 2));
            lblPrecioPromedio.setText(Tools.roundingValue(0, 2));
            lblCostoTotal.setText(Tools.roundingValue(0, 2));
            lblPrecioTotal.setText(Tools.roundingValue(0, 2));
            lblUtilidad.setText(Tools.roundingValue(0, 2));
        }
    }

    private void closeWindow() {
        vbContent.getChildren().remove(hbWindow);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(asignacionController.getHbWindow(), 0d);
        AnchorPane.setTopAnchor(asignacionController.getHbWindow(), 0d);
        AnchorPane.setRightAnchor(asignacionController.getHbWindow(), 0d);
        AnchorPane.setBottomAnchor(asignacionController.getHbWindow(), 0d);
        vbContent.getChildren().add(asignacionController.getHbWindow());
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        closeWindow();
    }

    @FXML
    private void onKeyPressedRegister(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            executeRegister();
        }
    }

    @FXML
    private void onActionRegister(ActionEvent event) {
        executeRegister();
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
    private void onKeyPressedProducto(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowSuministros();
        }
    }

    @FXML
    private void onActionProducto(ActionEvent event) {
        openWindowSuministros();
    }

    @FXML
    private void onKeyPressedRecalcular(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            recalculate();
        }
    }

    @FXML
    private void onActionRecalcular(ActionEvent event) {
        recalculate();
    }

    @FXML
    private void onKeyPressedRemover(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            removeProducto();
        }
    }

    @FXML
    private void onActionRemover(ActionEvent event) {
        removeProducto();
    }

    public TableView<SuministroTB> getTvList() {
        return tvList;
    }

    public void setInitControllerAsignacion(FxAsignacionController asignacionController, AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.asignacionController = asignacionController;
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
