package controller.inventario.movimientos;

import controller.tools.Tools;
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
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.MovimientoInventarioADO;
import model.MovimientoInventarioDetalleTB;
import model.MovimientoInventarioTB;

public class FxMovimientosDetalleController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private Label lblTIpoMovimiento;
    @FXML
    private Label lblHoraFecha;
    @FXML
    private Label lblObservacion;
    @FXML
    private Label lblProveedor;
    @FXML
    private Label lblEstado;
    @FXML
    private Label lblVerificar;
    @FXML
    private TableView<MovimientoInventarioDetalleTB> tvList;
    @FXML
    private TableColumn<MovimientoInventarioDetalleTB, Integer> tcNumero;
    @FXML
    private TableColumn<MovimientoInventarioDetalleTB, String> tcDescripcion;
    @FXML
    private TableColumn<MovimientoInventarioDetalleTB, String> tcCantidad;
    @FXML
    private TableColumn<MovimientoInventarioDetalleTB, CheckBox> tcVerificar;
    @FXML
    private TableColumn<MovimientoInventarioDetalleTB, CheckBox> tcPrecio;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Button btnVerificar;

    private int countVerificar;

    private String idMovimientoInventario;
    
    private short tipo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);

        tcNumero.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        tcDescripcion.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getSuministroTB().getClave() + "\n" + cellData.getValue().getSuministroTB().getNombreMarca()
        ));
        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getCantidad(), 2)));
        tcVerificar.setCellValueFactory(new PropertyValueFactory<>("verificar"));
        tcPrecio.setCellValueFactory(new PropertyValueFactory<>("actualizarPrecio"));
    }

    public void setIniciarCarga(short type,String value) {
        tipo = type;
        idMovimientoInventario = value;
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                return MovimientoInventarioADO.Obtener_Movimiento_Inventario_By_Id(type,value);
            }
        };

        task.setOnScheduled((e) -> {
            lblLoad.setVisible(true);
        });

        task.setOnFailed((e) -> {
            lblLoad.setVisible(false);
        });

        task.setOnSucceeded((e) -> {
            ArrayList<Object> objects = task.getValue();
            if (!objects.isEmpty()) {

                MovimientoInventarioTB inventarioTB = (MovimientoInventarioTB) objects.get(0);
                ObservableList<MovimientoInventarioDetalleTB> detalleTBs = (ObservableList<MovimientoInventarioDetalleTB>) objects.get(1);

                if (inventarioTB != null) {
                    lblTIpoMovimiento.setText(inventarioTB.getTipoMovimientoName());
                    lblHoraFecha.setText(inventarioTB.getFecha() + " " + inventarioTB.getHora());
                    lblObservacion.setText(inventarioTB.getObservacion());
                    lblProveedor.setText(inventarioTB.getProveedor());
                    lblEstado.setText(inventarioTB.getEstadoName());
                    if (inventarioTB.getEstadoName().equalsIgnoreCase("COMPLETADO")) {
                        btnRegistrar.setDisable(true);
                        btnVerificar.setDisable(true);
                    } else if (inventarioTB.getEstadoName().equalsIgnoreCase("EN PROCESO")) {

                    } else if (inventarioTB.getEstadoName().equalsIgnoreCase("CANCELADO")) {
                        btnRegistrar.setDisable(true);
                        btnVerificar.setDisable(true);
                    }
                }
                tvList.setItems(detalleTBs);
            }
            lblLoad.setVisible(false);
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void executeVerificar() {
//        for (int i = 0; i < tvList.getItems().size(); i++) {
//            ArticuloTB atb = ArticuloADO.Get_Articulo_Verificar_Movimiento(tvList.getItems().get(i).getSuministroTB().getClave());
//            if (atb != null) {
//                tvList.getItems().get(i).getVerificar().setSelected(true);
//                tvList.getItems().get(i).getActualizarPrecio().setSelected(true);
//                countVerificar++;
//            }
//        }
//        if (countVerificar == tvList.getItems().size()) {
//            lblVerificar.setTextFill(Paint.valueOf("#1880ee"));
//            lblVerificar.setText("Se verificó con satisfacción");
//            btnRegistrar.setDisable(false);
//        } else {
//            lblVerificar.setTextFill(Paint.valueOf("#ed3d1a"));
//            lblVerificar.setText("Hay un artículo que no coincide con la lista");
//        }
    }

    private void executeRegistrar() {
//        short option = Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Movimiento", "¿Esta seguro de continuar?", true);
//        if (option == 1) {
//
//            ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
//                Thread t = new Thread(runnable);
//                t.setDaemon(true);
//                return t;
//            });
//
//            Task<String> task = new Task<String>() {
//                @Override
//                public String call() {
//                    return ArticuloADO.Registrar_Articulo_Movimiento(idMovimientoInventario, lblObservacion.getText(), tvList);
//                }
//            };
//
//            task.setOnScheduled((e) -> {
//                btnRegistrar.setDisable(true);
//            });
//
//            task.setOnFailed((e) -> {
//                btnRegistrar.setDisable(false);
//            });
//
//            task.setOnSucceeded((e) -> {
//                String result = task.getValue();
//                if (result.equalsIgnoreCase("registrado")) {
//                    Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.INFORMATION, "Movimiento", "Se registraron los cambios correctamente.", false);
//                    Tools.Dispose(apWindow);
//                } else if (result.equalsIgnoreCase("error")) {
//                    Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.INFORMATION, "Movimiento", "La verificación de los artículos no se completo.", false);
//                } else {
//                    Tools.AlertMessage(apWindow.getScene().getWindow(), Alert.AlertType.ERROR, "Movimiento", result, false);
//                }
//            });
//
//            exec.execute(task);
//            if (!exec.isShutdown()) {
//                exec.shutdown();
//            }
//
//        }
    }

    @FXML
    private void onKeyPressedReporte(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionReporte(ActionEvent event) {

    }

    @FXML
    private void onKeyPressedVerificar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            executeVerificar();
        }
    }

    @FXML
    private void onActionVerificar(ActionEvent event) {
        executeVerificar();
    }

    @FXML
    private void onKeyPressedRegistrar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            executeRegistrar();
        }
    }

    @FXML
    private void onActionRegistrar(ActionEvent event) {
        executeRegistrar();
    }

}
