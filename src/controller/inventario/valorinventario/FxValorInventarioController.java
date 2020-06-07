package controller.inventario.valorinventario;

import controller.tools.Session;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.SuministroADO;
import model.SuministroTB;

public class FxValorInventarioController implements Initializable {

    @FXML
    private Label lblLoad;
    @FXML
    private TextField txtProducto;
    @FXML
    private ComboBox<String> cbExistencia;
    @FXML
    private TableView<SuministroTB> tvList;
    @FXML
    private TableColumn<SuministroTB, Integer> tcNumero;
    @FXML
    private TableColumn<SuministroTB, String> tcDescripcion;
////    private TableColumn<SuministroTB, String> tcCantidad;
//    private TableColumn<SuministroTB, String> tcUnidad;
//    private TableColumn<SuministroTB, String> tcEstado;
    @FXML
    private TableColumn<SuministroTB, String> tcCostoPromedio;
    @FXML
    private TableColumn<SuministroTB, String> tcPrecio;
    @FXML
    private TableColumn<SuministroTB, Label> tcExistencia;
    @FXML
    private TableColumn<SuministroTB, String> tcInventarioMinimo;
    @FXML
    private TableColumn<SuministroTB, String> tcInventarioMaximo;
//    private TableColumn<SuministroTB, String> tcTotal;
    @FXML
    private Label lblValoTotal;

    private AnchorPane vbPrincipal;    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcNumero.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        tcDescripcion.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getClave() + "\n" + cellData.getValue().getNombreMarca()
        ));
//        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(
//                Tools.roundingValue(cellData.getValue().getCantidad(), 2)
//        ));
//        tcUnidad.setCellValueFactory(cellData -> Bindings.concat(
//                cellData.getValue().getUnidadCompraName()
//        ));
//        tcEstado.setCellValueFactory(cellData -> Bindings.concat(
//                cellData.getValue().getEstadoName().get()
//        ));
        tcCostoPromedio.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getCostoCompra(), 2)
        ));
        tcPrecio.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getPrecioVentaGeneral(), 2)));
        //tcExistencia.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getCantidad(), 2) + " " + cellData.getValue().getUnidadCompraName()));
        tcExistencia.setCellValueFactory(new PropertyValueFactory<>("lblCantidad"));
        tcInventarioMinimo.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getStockMinimo(), 2)));
        tcInventarioMaximo.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getStockMaximo(), 2)));
//        tcTotal.setCellValueFactory(cellData -> Bindings.concat(
//                Tools.roundingValue(cellData.getValue().getTotalImporte(), 2)
//        ));

        tcNumero.prefWidthProperty().bind(tvList.widthProperty().multiply(0.05));
        tcDescripcion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.28));
        //tcCantidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        //tcUnidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        //tcEstado.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        tcCostoPromedio.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        tcPrecio.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        tcExistencia.prefWidthProperty().bind(tvList.widthProperty().multiply(0.19));
        tcInventarioMinimo.prefWidthProperty().bind(tvList.widthProperty().multiply(0.10));
        tcInventarioMaximo.prefWidthProperty().bind(tvList.widthProperty().multiply(0.10));
        //tcTotal.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
    
        cbExistencia.getItems().addAll("Todas las Existencias","Existencia negativas","Existencia positivas","Existencia Intermedias");
        cbExistencia.getSelectionModel().select(0);
    }

    public void fillInventarioTable(String producto,short tipoExistencia) {
        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<ObservableList<SuministroTB>> task = new Task<ObservableList<SuministroTB>>() {
            @Override
            public ObservableList<SuministroTB> call() {
                return SuministroADO.ListInventario(producto,tipoExistencia);
            }
        };
        task.setOnSucceeded((WorkerStateEvent e) -> {
            tvList.setItems(task.getValue());
            double total = 0;
            total = tvList.getItems().stream().map((l) -> l.getTotalImporte()).reduce(total, (accumulator, _item) -> accumulator + _item);
            lblValoTotal.setText(Session.MONEDA + Tools.roundingValue(total, 4));
            lblLoad.setVisible(false);
        });
        task.setOnFailed((WorkerStateEvent event) -> {
            lblLoad.setVisible(false);
        });
        task.setOnScheduled((WorkerStateEvent event) -> {
            lblLoad.setVisible(true);
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }
    
    @FXML
    private void onKeyReleasedProducto(KeyEvent event) {
    
    }
    
    
    @FXML
    private void onKeyPressedAjuste(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER){
            
        }
    }

    @FXML
    private void onActionAjuste(ActionEvent event) {
    
    }

    @FXML
    private void onKeyPressedRecargar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                cbExistencia.getSelectionModel().select(0);
                fillInventarioTable(txtProducto.getText().trim(),(short)cbExistencia.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    private void onActionRecargar(ActionEvent event) {
        if (!lblLoad.isVisible()) {
             cbExistencia.getSelectionModel().select(0);
            fillInventarioTable(txtProducto.getText().trim(),(short)cbExistencia.getSelectionModel().getSelectedIndex());
        }
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
    private void onKeyPressedModificar(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER){
            
        }
    }

    @FXML
    private void onActionModificar(ActionEvent event) {
    
    }
    
    @FXML
    private void onActionExistencia(ActionEvent event) {
        fillInventarioTable("",(short)cbExistencia.getSelectionModel().getSelectedIndex());
    }
    
    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
