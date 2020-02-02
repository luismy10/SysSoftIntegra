package controller.produccion.asignacion;

import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.SuministroTB;

public class FxAsignacionController implements Initializable {

    @FXML
    private HBox hbWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<SuministroTB> tvList;
    @FXML
    private TableColumn<SuministroTB, Integer> tcNumero;
    @FXML
    private TableColumn<SuministroTB, String> tcFecha;
    @FXML
    private TableColumn<SuministroTB, String> tcDescripcion;
    @FXML
    private TableColumn<SuministroTB, String> tcCantidad;
    @FXML
    private TableColumn<SuministroTB, String> tcCosto;
    @FXML
    private TableColumn<SuministroTB, String> tcPrecio;
    @FXML
    private DatePicker dtFechaInicio;
    @FXML
    private DatePicker dtFechaFinal;
    /*
    Objectos de la ventana principal y venta que agrega al os hijos
     */
    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    /*
    Controller suministros     
     */
    private FXMLLoader fXMLAsignacionProceso;

    private HBox nodeAsignacionProceso;

    private FxAsignacionProcesoController asignacionProcesoController;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            fXMLAsignacionProceso = new FXMLLoader(getClass().getResource(FilesRouters.FX_ASIGNACION_PROCESO));
            nodeAsignacionProceso = fXMLAsignacionProceso.load();
            asignacionProcesoController = fXMLAsignacionProceso.getController();
        } catch (IOException ex) {
            System.out.println("Error en Asignacion Controller:" + ex.getLocalizedMessage());
        }

        hbWindow.setOnKeyReleased((KeyEvent event) -> {
            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case F1:
                        openWindowAdd();
                        break;
                    case F5:
                        reloadTable();
                        break;
                }
            }
        });

        tcNumero.setCellValueFactory(null);
        tcFecha.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue()));
        tcDescripcion.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue()));
        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue()));
        tcCosto.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue()));
        tcPrecio.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getPrecioVentaGeneral()));

        tcNumero.prefWidthProperty().bind(tvList.widthProperty().multiply(0.05));
        tcFecha.prefWidthProperty().bind(tvList.widthProperty().multiply(0.18));
        tcDescripcion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.3));
        tcCantidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));
        tcCosto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));
        tcPrecio.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));
    }

    private void openWindowAdd() {
        asignacionProcesoController.setInitControllerAsignacion(this, vbPrincipal, vbContent);
        //
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeAsignacionProceso, 0d);
        AnchorPane.setTopAnchor(nodeAsignacionProceso, 0d);
        AnchorPane.setRightAnchor(nodeAsignacionProceso, 0d);
        AnchorPane.setBottomAnchor(nodeAsignacionProceso, 0d);
        vbContent.getChildren().add(nodeAsignacionProceso);
        //

    }

    private void reloadTable() {

    }

    private void openWindowArticulos() {
//        try {
//            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
//            URL url = getClass().getResource(FilesRouters.FX_ARTICULO_LISTA);
//            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
//            Parent parent = fXMLLoader.load(url.openStream());
//            //Controlller here
//            FxArticuloListaController controller = fXMLLoader.getController();
//            controller.setInitAsignacionController(this);
//            //
//            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Artículo", hbWindow.getScene().getWindow());
//            stage.setResizable(false);
//            stage.sizeToScene();
//            stage.setOnHiding((w) -> {
//                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
//            });
//            stage.show();
//            controller.loadElements((short) 2, "");
//        } catch (IOException ex) {
//            System.out.println("Error en la vista ajustes:" + ex.getLocalizedMessage());
//        }
    }

    @FXML
    private void onKeyPressedSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowArticulos();
        }
    }

    @FXML
    private void onActionSearch(ActionEvent event) {
        openWindowArticulos();
    }

    @FXML
    private void onKeyPressedNuevaAsignacion(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowAdd();
        }
    }

    @FXML
    private void onActionNuevaAsignacion(ActionEvent event) {
        openWindowAdd();
    }

    @FXML
    private void onKeyPressedReload(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionReload(ActionEvent event) {

    }

    public HBox getHbWindow() {
        return hbWindow;
    }

    public void setContent(AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
