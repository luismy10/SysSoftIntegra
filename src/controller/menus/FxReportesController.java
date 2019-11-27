package controller.menus;

import controller.ingresos.venta.FxVentaReporteController;
import controller.ingresos.venta.FxVentaUtilidadesController;
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
import javafx.scene.layout.VBox;

public class FxReportesController implements Initializable {

    @FXML
    private HBox hbWindow;
    @FXML
    private TextField txtSearch;
    /*
    Objectos de la ventana principal y venta que agrega al os hijos
     */
    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;
    /*
    Controller ventas
     */
    private FXMLLoader fXMLVenta;

    private VBox nodeVenta;

    private FxVentaReporteController controllerReporteVenta;

    /*
    Controller utilidades
     */
    private FXMLLoader fXMLUtilidades;

    private VBox nodeUtilidades;

    private FxVentaUtilidadesController controllerUtilidades;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            fXMLVenta = new FXMLLoader(getClass().getResource(FilesRouters.FX_VENTA_REPORTE));
            nodeVenta = fXMLVenta.load();
            controllerReporteVenta = fXMLVenta.getController();

            fXMLUtilidades = new FXMLLoader(getClass().getResource(FilesRouters.FX_VENTA_UTILIDADES));
            nodeUtilidades = fXMLUtilidades.load();
            controllerUtilidades = fXMLUtilidades.getController();

        } catch (IOException ex) {
            System.out.println("Error en Ingresos Controller:" + ex.getLocalizedMessage());
        }
    }

    private void openWindowVentas() {
        controllerReporteVenta.setContent(vbPrincipal);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeVenta, 0d);
        AnchorPane.setTopAnchor(nodeVenta, 0d);
        AnchorPane.setRightAnchor(nodeVenta, 0d);
        AnchorPane.setBottomAnchor(nodeVenta, 0d);
        vbContent.getChildren().add(nodeVenta);
    }

    private void openWindowUtilidades() {
        controllerUtilidades.setContent(vbPrincipal);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeUtilidades, 0d);
        AnchorPane.setTopAnchor(nodeUtilidades, 0d);
        AnchorPane.setRightAnchor(nodeUtilidades, 0d);
        AnchorPane.setBottomAnchor(nodeUtilidades, 0d);
        vbContent.getChildren().add(nodeUtilidades);
    }

    @FXML
    private void onKeyPressedVentas(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowVentas();
        }
    }

    @FXML
    private void onActionVentas(ActionEvent event) {
        openWindowVentas();
    }

    @FXML
    private void onKeyPressedUtilidades(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowUtilidades();
        }
    }

    @FXML
    private void onActionUtilidades(ActionEvent event) {
        openWindowUtilidades();
    }

    public void setContent(AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
