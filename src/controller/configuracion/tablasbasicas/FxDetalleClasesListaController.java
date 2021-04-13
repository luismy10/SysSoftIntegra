package controller.configuracion.tablasbasicas;

import controller.produccion.insumos.FxInsumoProcesoController;
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
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.ClasesTB;
import model.DetalleADO;

public class FxDetalleClasesListaController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private TextField txtSearch;
    @FXML
    private ListView<ClasesTB> lvList;

    private String titulo;

    private FxInsumoProcesoController insumoProcesoController;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    public void initListClassDetail(String titulo) {
        this.titulo = titulo.toLowerCase();
        
        FillListClass();
    }
    
    public void FillListClass(){
        lvList.getItems().clear();
        DetalleADO.GetDetailClass(titulo).forEach(e -> {
            lvList.getItems().add(new ClasesTB(e.getIdClase(), e.getNombreClase(), e.getCodigoAuxiliar(), e.getDescripcion(), e.getEstado()));
        });
    }

    private void selectDetailLisClass() {
        if (insumoProcesoController != null) {
            if (titulo.equalsIgnoreCase("clase")) {
                insumoProcesoController.setIdClase(lvList.getSelectionModel().getSelectedItem().getIdClase());
                insumoProcesoController.getTxtClase().setText(lvList.getSelectionModel().getSelectedItem().getNombreClase().get());
                Tools.Dispose(window);
            } else if (titulo.equalsIgnoreCase("sub-clase")) {
                insumoProcesoController.setIdSubClase(lvList.getSelectionModel().getSelectedItem().getIdClase());
                insumoProcesoController.getTxtSubClase().setText(lvList.getSelectionModel().getSelectedItem().getNombreClase().get());
                Tools.Dispose(window);
            } else if (titulo.equalsIgnoreCase("sub-sub-clase")) {
                insumoProcesoController.setIdSubSubClase(lvList.getSelectionModel().getSelectedItem().getIdClase());
                insumoProcesoController.getTxtSubSubClase().setText(lvList.getSelectionModel().getSelectedItem().getNombreClase().get());
                Tools.Dispose(window);
            }
        }
    }

    private void openWindowEditar() throws IOException {
        if (lvList.getSelectionModel().getSelectedIndex() >= 0) {
            URL url = getClass().getResource(FilesRouters.FX_DETALLE);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());

            FxDetalleController controller = fXMLLoader.getController();
            controller.setInitiDetalleClaseListaController(this);
            controller.updateDetalle(lvList.getSelectionModel().getSelectedItem(), titulo);

            Stage stage = WindowStage.StageLoaderModal(parent, titulo+" Editar", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.show();
//            controller.setInitComponents(lvList.getSelectionModel().getSelectedItem().getIdDetalle().get(),idMantenimiento);
        } else {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Detalle lista", "Seleccione un elemento de la lista para editarlo", false);
            lvList.requestFocus();
        }
    }


    @FXML
    private void onKeyPressedToSearh(KeyEvent event) {
    }

    @FXML
    private void onKeyReleasedToSearch(KeyEvent event) {
    }

    @FXML
    private void onKeyPressedEditar(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowEditar();
        }
    }

    @FXML
    private void onActionEditar(ActionEvent event) throws IOException {
        openWindowEditar();
    }

    @FXML
    private void onKeyPressedList(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            selectDetailLisClass();
        }
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) {
        if (lvList.getSelectionModel().getSelectedIndex() >= 0 && lvList.isFocused()) {
            if (event.getClickCount() == 2) {
                selectDetailLisClass();
            }
        }

    }

    public void setControllerInsumoModal(FxInsumoProcesoController insumoProcesoController) {
        this.insumoProcesoController = insumoProcesoController;
    }

}
