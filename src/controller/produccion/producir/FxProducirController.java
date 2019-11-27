package controller.produccion.producir;


import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class FxProducirController implements Initializable {

    @FXML
    private HBox window;
    @FXML
    private Label lblLoad;
    @FXML
    private TableView<?> tvList;
    @FXML
    private TableColumn<?, ?> tcNumeracion;
    @FXML
    private TableColumn<?, ?> tcClave;
    @FXML
    private TableColumn<?, ?> tcNombre;
    @FXML
    private TableColumn<?, ?> tcCosto;
    @FXML
    private TableColumn<?, ?> tcCantidad;
    @FXML
    private DatePicker dtFechaInicial;
    @FXML
    private DatePicker dtFechaFinal;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.actualDate(Tools.getDate(), dtFechaInicial);
        Tools.actualDate(Tools.getDate(), dtFechaFinal);

    }
    
    private void onViewProducirProceso() {
        try {
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(getClass().getResource(FilesRouters.FX_PRODUCIR_PROCESO));
            HBox node = fXMLLoader.load();
            //Controlller here
            FxProducirProcesoController controller = fXMLLoader.getController();
            controller.setInitControllerProducir(this, vbPrincipal, vbContent);
            //
            vbContent.getChildren().clear();
            AnchorPane.setLeftAnchor(node, 0d);
            AnchorPane.setTopAnchor(node, 0d);
            AnchorPane.setRightAnchor(node, 0d);
            AnchorPane.setBottomAnchor(node, 0d);
            vbContent.getChildren().add(node);
            controller.fillTablesProduccion();
            //

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }
    
    public HBox getWindow() {
        return window;
    }
    

    @FXML
    private void onKeyPressedAdd(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onViewProducirProceso();
        }
    }

    @FXML
    private void onActionAdd(ActionEvent event) throws IOException{
        onViewProducirProceso();
    }

    @FXML
    private void onKeyPressedEdit(KeyEvent event) {
    }

    @FXML
    private void onActionEdit(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedClone(KeyEvent event) {
    }

    @FXML
    private void onActionClone(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedReload(KeyEvent event) {
    }

    @FXML
    private void onActionReload(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedRemove(KeyEvent event) {
    }

    @FXML
    private void onActionRemove(ActionEvent event) {
    }

    public void setContent(AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
