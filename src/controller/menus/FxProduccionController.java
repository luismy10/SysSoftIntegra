package controller.menus;

import controller.produccion.insumos.FxInsumoController;
import controller.produccion.producir.FxFormulaController;
import controller.produccion.producir.FxProducirController;
import controller.tools.FilesRouters;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class FxProduccionController implements Initializable {

    @FXML
    private HBox hbWindow;
    @FXML
    private TextField txtSearch;

    /*
    Objectos de la ventana principal y venta que agrega a los hijos
     */
    private FxPrincipalController fxPrincipalController;

    /*
    Controller producir     
     */
    private FXMLLoader fXMLProducir;

    private HBox nodeProducir;

    private FxProducirController controllerProducir;

    /*
    Controller insumos     
     */
    private FXMLLoader fXMLInsumo;

    private HBox nodeInsumo;

    private FxInsumoController controllerInsumo;
    
      /*
    Controller formula     
     */
    private FXMLLoader fXMLFormula;

    private HBox nodeFormula;

    private FxFormulaController controllerFormula;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            fXMLProducir = new FXMLLoader(getClass().getResource(FilesRouters.FX_PRODUCIR));
            nodeProducir = fXMLProducir.load();
            controllerProducir = fXMLProducir.getController();

            fXMLInsumo = new FXMLLoader(getClass().getResource(FilesRouters.FX_INSUMOS));
            nodeInsumo = fXMLInsumo.load();
            controllerInsumo = fXMLInsumo.getController();
            
            fXMLFormula = new FXMLLoader(getClass().getResource(FilesRouters.FX_FORMULA));
            nodeFormula = fXMLFormula.load();
            controllerFormula = fXMLFormula.getController();
        } catch (IOException ex) {
            System.out.println("Error en Producción Controller:" + ex.getLocalizedMessage());
        }
    }

    private void openWindowProducir() {
        controllerProducir.setContent(fxPrincipalController);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(nodeProducir, 0d);
        AnchorPane.setTopAnchor(nodeProducir, 0d);
        AnchorPane.setRightAnchor(nodeProducir, 0d);
        AnchorPane.setBottomAnchor(nodeProducir, 0d);
        fxPrincipalController.getVbContent().getChildren().add(nodeProducir);    
    }

    private void openWindowInsumo() {
        controllerInsumo.setContent(fxPrincipalController);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(nodeInsumo, 0d);
        AnchorPane.setTopAnchor(nodeInsumo, 0d);
        AnchorPane.setRightAnchor(nodeInsumo, 0d);
        AnchorPane.setBottomAnchor(nodeInsumo, 0d);
        fxPrincipalController.getVbContent().getChildren().add(nodeInsumo);
        controllerInsumo.loadInitComponents();
    }

    private void openWindowFormula() {
        controllerFormula.setContent(fxPrincipalController);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(nodeFormula, 0d);
        AnchorPane.setTopAnchor(nodeFormula, 0d);
        AnchorPane.setRightAnchor(nodeFormula, 0d);
        AnchorPane.setBottomAnchor(nodeFormula, 0d);
        fxPrincipalController.getVbContent().getChildren().add(nodeFormula);        
    }

    @FXML
    private void onKeyPressedProducir(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowProducir();
        }
    }

    @FXML
    private void onActionProducir(ActionEvent event) {
        openWindowProducir();
    }

    @FXML
    private void onKeyPressedInsumos(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowInsumo();
        }
    }

    @FXML
    private void onActionInsumos(ActionEvent event) {
        openWindowInsumo();
    }

    @FXML
    private void onKeyPressedFormula(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowFormula();
        }
    }

    @FXML
    private void onActionFormula(ActionEvent event) {
        openWindowFormula();
    }   

     public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }

}
