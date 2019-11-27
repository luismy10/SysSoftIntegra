package controller.egresos.compras;

import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.CompraADO;
import model.SuministroTB;

public class FxComprasCancelarController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private CheckBox cbOpcionOne;
    @FXML
    private CheckBox cbOpcionDos;
    @FXML
    private TextField txtEfectivo;
    @FXML
    private HBox hbDevolucion;

    private String idCompra;
    
    private ObservableList<SuministroTB> arrList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
    }

    private void eventAplicarCancelacion(String idCompra) {
        short value = Tools.AlertMessageConfirmation(apWindow, "Detalle de la compra", "¿Está seguro de continuar?");
        if (value == 1) {
            String result = CompraADO.cancelarVenta(idCompra,arrList,cbOpcionOne.isSelected(),cbOpcionDos.isSelected());
            if (result.equalsIgnoreCase("cancel")) {
                Tools.AlertMessageWarning(apWindow, "Detalle de la compra", "La compra ya se encuentra cancelada o anulada.");
                Tools.Dispose(apWindow);
            } else if (result.equalsIgnoreCase("updated")) {
                Tools.AlertMessageInformation(apWindow, "Detalle de la compra", "Se completo correctamente los cambios.");
                Tools.Dispose(apWindow);
            } else {
                Tools.AlertMessageError(apWindow, "Detalle de la compra", result);
            }
        }

    }

    @FXML
    private void onActionAplicar(ActionEvent event) {
        eventAplicarCancelacion(idCompra);
    }

    @FXML
    private void onKeyPressedAplicar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            eventAplicarCancelacion(idCompra);
        }
    }
    
     @FXML
    private void onActionOpcionDos(ActionEvent event) {
        if(cbOpcionDos.isSelected()){
            hbDevolucion.setDisable(false);
        }else{
            hbDevolucion.setDisable(true);
        }
    }
    
    public void setIdCompra(String idCompra) {
        this.idCompra = idCompra;
    }

    public void setTableList(ObservableList<SuministroTB> arrList) {
        this.arrList = arrList;
    }

   

}
