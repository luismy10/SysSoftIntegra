package controller.inventario.articulo;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class FxArticuloSeleccionadoController implements Initializable {

    @FXML
    private ImageView ivPrincipal;
    @FXML
    private Text lblName;
    @FXML
    private Text lblPrice;
    @FXML
    private Text lblQuantity;
    @FXML
    private Text lblPriceBruto;
    @FXML
    private Text lblImpuesto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public ImageView getIvPrincipal() {
        return ivPrincipal;
    }

    public Text getLblName() {
        return lblName;
    }

    public Text getLblPrice() {
        return lblPrice;
    }

    public Text getLblQuantity() {
        return lblQuantity;
    }

    public Text getLblPriceBruto() {
        return lblPriceBruto;
    }

    public Text getLblImpuesto() {
        return lblImpuesto;
    }

}
