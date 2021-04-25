package controller.operaciones.pedidos;

import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import model.PedidoTB;

public class FxPedidosRealizadosController implements Initializable {

    @FXML
    private VBox vbWindow;
    @FXML
    private Button btnMostrar;
    @FXML
    private Button btnRecargar;
    @FXML
    private TextField txtSearch;
    @FXML
    private DatePicker dtFechaInicial;
    @FXML
    private DatePicker dtFechaFinal;
    @FXML
    private TableView<PedidoTB> tvList;
    @FXML
    private TableColumn<PedidoTB, String> tcNumero;
    @FXML
    private TableColumn<PedidoTB, String> tcVendedor;
    @FXML
    private TableColumn<PedidoTB, String> tcCotizacion;
    @FXML
    private TableColumn<PedidoTB, String> tcFecha;
    @FXML
    private TableColumn<PedidoTB, String> tcCliente;
    @FXML
    private TableColumn<PedidoTB, String> tcTotal;
    @FXML
    private Label lblPaginaActual;
    @FXML
    private Label lblPaginaSiguiente;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.actualDate(Tools.getDate(), dtFechaInicial);
        Tools.actualDate(Tools.getDate(), dtFechaFinal);
    }

    public void loadComponents() {

    }

    public void fillTablePedidos() {
        ExecutorService executorService = Executors.newCachedThreadPool(run -> {
            Thread thread = new Thread(run); 
            return thread;
        });
         
        if(executorService.isShutdown()){
            executorService.shutdown();
            
        }
    }

    @FXML
    private void onKeyPressedMostar(KeyEvent event) {
    }

    @FXML
    private void onActionMostrar(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedRecargar(KeyEvent event) {
    }

    @FXML
    private void onActionRecargar(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedSearch(KeyEvent event) {
    }

    @FXML
    private void onKeyRelasedSearch(KeyEvent event) {
    }

    @FXML
    private void onActionFechaInicial(ActionEvent event) {
    }

    @FXML
    private void onActionFechaFinal(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedList(KeyEvent event) {
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) {
    }

    @FXML
    private void onKeyPressedAnterior(KeyEvent event) {
    }

    @FXML
    private void onActionAnterior(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedSiguiente(KeyEvent event) {
    }

    @FXML
    private void onActionSiguiente(ActionEvent event) {
    }

}
