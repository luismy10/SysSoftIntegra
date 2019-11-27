package controller.inventario.articulo;

import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class FxReporteOpcionController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private RadioButton rbLista;
    @FXML
    private RadioButton rbColumna2;
    @FXML
    private RadioButton rbColumna3;

    private FxArticuloReportesController articuloReportesController;

    private short option;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
        ToggleGroup group = new ToggleGroup();
        rbLista.setToggleGroup(group);
        rbColumna2.setToggleGroup(group);
        rbColumna3.setToggleGroup(group);
        option = 1;
    }

    @FXML
    private void onKeyPressedGenerar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!articuloReportesController.getTvList().getItems().isEmpty()) {
                articuloReportesController.openWindowReporte(true, option);
            } else {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Reporte", "La lista está vacía", false);
                Tools.Dispose(window);
            }
        }
    }

    @FXML
    private void onActionGenerar(ActionEvent event) {
        if (!articuloReportesController.getTvList().getItems().isEmpty()) {
            articuloReportesController.openWindowReporte(true, option);
        } else {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Reporte", "La lista está vacía", false);
            Tools.Dispose(window);
        }
    }

    public void setInitReporteArticuloController(FxArticuloReportesController articuloReportesController) {
        this.articuloReportesController = articuloReportesController;
    }

    @FXML
    private void onActionRbLista(ActionEvent event) {
        option = 1;
    }

    @FXML
    private void onActionRbLista2(ActionEvent event) {
        option = 2;
    }

    @FXML
    private void onActionRbList3(ActionEvent event) {
        option = 3;
    }

}
