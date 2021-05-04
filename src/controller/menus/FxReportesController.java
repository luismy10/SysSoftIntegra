package controller.menus;

import controller.reporte.FxCompraReporteController;
import controller.reporte.FxGlobalReporteController;
import controller.reporte.FxProductoReporteController;
import controller.reporte.FxVentaReporteController;
import controller.reporte.FxVentaUtilidadesController;
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
    private FxPrincipalController fxPrincipalController;
    /*
    Controller global
     */
    private FXMLLoader fXMLGlobal;

    private VBox nodeGlobal;

    private FxGlobalReporteController controllerReporteGlobal;

    /*
    Controller ventas
     */
    private FXMLLoader fXMLVenta;

    private VBox nodeVenta;

    private FxVentaReporteController controllerReporteVenta;
    /*
    Controller compras
     */
    private FXMLLoader fXMLCompra;

    private VBox nodeCompra;

    private FxCompraReporteController controllerReporteCompra;
    /*
    Controller producto
     */
    private FXMLLoader fXMLProducto;

    private VBox nodeProducto;

    private FxProductoReporteController controllerReporteProducto;

    /*
    Controller utilidades
     */
    private FXMLLoader fXMLUtilidades;

    private VBox nodeUtilidades;

    private FxVentaUtilidadesController controllerUtilidades;
 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            fXMLGlobal = new FXMLLoader(getClass().getResource(FilesRouters.FX_GLOBAL_REPORTE));
            nodeGlobal = fXMLGlobal.load();
            controllerReporteGlobal = fXMLGlobal.getController();

            fXMLVenta = new FXMLLoader(getClass().getResource(FilesRouters.FX_VENTA_REPORTE));
            nodeVenta = fXMLVenta.load();
            controllerReporteVenta = fXMLVenta.getController();

            fXMLCompra = new FXMLLoader(getClass().getResource(FilesRouters.FX_COMPRA_REPORTE));
            nodeCompra = fXMLCompra.load();
            controllerReporteCompra = fXMLCompra.getController();

            fXMLProducto = new FXMLLoader(getClass().getResource(FilesRouters.FX_PRODUCTO_REPORTE));
            nodeProducto = fXMLProducto.load();
            controllerReporteProducto = fXMLProducto.getController();

            fXMLUtilidades = new FXMLLoader(getClass().getResource(FilesRouters.FX_VENTA_UTILIDADES));
            nodeUtilidades = fXMLUtilidades.load();
            controllerUtilidades = fXMLUtilidades.getController();

        } catch (IOException ex) {
            System.out.println("Error en Ingresos Controller:" + ex.getLocalizedMessage());
        }
    }

    private void openWindowGlobal() {
        controllerReporteGlobal.setContent(fxPrincipalController);
        fxPrincipalController.getVbContent().getChildren().clear(); 
        AnchorPane.setLeftAnchor(nodeGlobal, 0d);
        AnchorPane.setTopAnchor(nodeGlobal, 0d);
        AnchorPane.setRightAnchor(nodeGlobal, 0d);
        AnchorPane.setBottomAnchor(nodeGlobal, 0d);
        fxPrincipalController.getVbContent().getChildren().add(nodeGlobal);
    }

    private void openWindowVentas() {
        controllerReporteVenta.setContent(fxPrincipalController);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(nodeVenta, 0d);
        AnchorPane.setTopAnchor(nodeVenta, 0d);
        AnchorPane.setRightAnchor(nodeVenta, 0d);
        AnchorPane.setBottomAnchor(nodeVenta, 0d);
        fxPrincipalController.getVbContent().getChildren().add(nodeVenta);
    }

    private void openWindowCompras() {
        controllerReporteCompra.setContent(fxPrincipalController);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(nodeCompra, 0d);
        AnchorPane.setTopAnchor(nodeCompra, 0d);
        AnchorPane.setRightAnchor(nodeCompra, 0d);
        AnchorPane.setBottomAnchor(nodeCompra, 0d);
        fxPrincipalController.getVbContent().getChildren().add(nodeCompra);
    }

    private void openWindowProductos() {
        controllerReporteProducto.setContent(fxPrincipalController);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(nodeProducto, 0d);
        AnchorPane.setTopAnchor(nodeProducto, 0d);
        AnchorPane.setRightAnchor(nodeProducto, 0d);
        AnchorPane.setBottomAnchor(nodeProducto, 0d);
        fxPrincipalController.getVbContent().getChildren().add(nodeProducto);
    }

    private void openWindowUtilidades() {
        controllerUtilidades.setContent(fxPrincipalController);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(nodeUtilidades, 0d);
        AnchorPane.setTopAnchor(nodeUtilidades, 0d);
        AnchorPane.setRightAnchor(nodeUtilidades, 0d);
        AnchorPane.setBottomAnchor(nodeUtilidades, 0d);
        fxPrincipalController.getVbContent().getChildren().add(nodeUtilidades);
    }

    @FXML
    private void onKeyPressedGlobal(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowGlobal();
        }
    }

    @FXML
    private void onActionGlobal(ActionEvent event) {
        openWindowGlobal();
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
    private void onKeyPressedCompras(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowCompras();
        }
    }

    @FXML
    private void onActionCompras(ActionEvent event) {
        openWindowCompras();
    }

    @FXML
    private void onKeyPressedProductos(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowProductos();
        }
    }

    @FXML
    private void onActionProductos(ActionEvent event) {
        openWindowProductos();
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
  
    public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }

}
