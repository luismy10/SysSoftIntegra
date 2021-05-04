package controller.reporte;

import controller.menus.FxPrincipalController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class FxProductoReporteController implements Initializable {

    private FxPrincipalController fxPrincipalController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }

}
