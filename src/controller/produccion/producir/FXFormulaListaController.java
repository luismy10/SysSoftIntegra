
package controller.produccion.producir;

import controller.tools.Tools;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.FormulaADO;
import model.FormulaTB;


public class FXFormulaListaController implements Initializable {

    @FXML
    private ListView<FormulaTB> lvList;
    @FXML
    private AnchorPane window;

    private FxProducirAgregarController producirAgregarController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void initListFormulas(String idProducto, String nombre) {        
        lvList.getItems().clear();
        Object object = FormulaADO.Obtener_Formula_ByIdProducto(idProducto);
        if (object instanceof List) {
            List<FormulaTB> formulaTBs = (List<FormulaTB>) object;
            formulaTBs.forEach(e -> {
                lvList.getItems().add(e);
            });
        } else if (object instanceof String) {
            String error = (String) object;

        } else {

        }
    }

   

    @FXML
    private void onKeyPressedList(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (lvList.getSelectionModel().getSelectedIndex() >= 0 && lvList.isFocused()) {
//                producirAgregarController.selectFormula(lvList.getSelectionModel().getSelectedItem().getIdFormula());
                Tools.Dispose(window);
            }
        }
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) {
        if (lvList.getSelectionModel().getSelectedIndex() >= 0 && lvList.isFocused()) {
            if (event.getClickCount() == 2) {
//                producirAgregarController.selectFormula(lvList.getSelectionModel().getSelectedItem().getIdFormula());
                Tools.Dispose(window);
            }
        }
    }

    public void setProducirAgregarController(FxProducirAgregarController producirAgregarController) {
        this.producirAgregarController = producirAgregarController;
    }
}
