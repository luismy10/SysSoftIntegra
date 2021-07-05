package controller.menus;

import controller.reporte.FxCompraReporteController;
import controller.reporte.FxIngresosReporteController;
import controller.reporte.FxNotaCreditoReporteController;
import controller.reporte.FxPosReporteController;
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

    /*
    Controller ingresos
     */
    private FXMLLoader fXMLIngresos;

    private VBox nodeIngresos;

    private FxIngresosReporteController controllerIngresos;

    /*
    Controller nota credito
     */
    private FXMLLoader fXMLNotaCredito;

    private VBox nodeNotaCredito;

    private FxNotaCreditoReporteController controllerNotaCredito;

    /*
    Controller ventas pos
     */
    private FXMLLoader fXMLVentasPos;

    private VBox nodeVentasPos;

    private FxPosReporteController controllerVentasPos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
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

            fXMLIngresos = new FXMLLoader(getClass().getResource(FilesRouters.FX_INGRESOS_REPORTE));
            nodeIngresos = fXMLIngresos.load();
            controllerIngresos = fXMLIngresos.getController();

            fXMLNotaCredito = new FXMLLoader(getClass().getResource(FilesRouters.FX_NOTA_CREDITO_REPORTE));
            nodeNotaCredito = fXMLNotaCredito.load();
            controllerNotaCredito = fXMLNotaCredito.getController();

            fXMLVentasPos = new FXMLLoader(getClass().getResource(FilesRouters.FX_REPORTE_POS));
            nodeVentasPos = fXMLVentasPos.load();
            controllerVentasPos = fXMLVentasPos.getController();
        } catch (IOException ex) {
            System.out.println("Error en Ingresos Controller:" + ex.getLocalizedMessage());
        }
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

    private void openWindowPos() {
        controllerVentasPos.setContent(fxPrincipalController);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(nodeVentasPos, 0d);
        AnchorPane.setTopAnchor(nodeVentasPos, 0d);
        AnchorPane.setRightAnchor(nodeVentasPos, 0d);
        AnchorPane.setBottomAnchor(nodeVentasPos, 0d);
        fxPrincipalController.getVbContent().getChildren().add(nodeVentasPos);
    }

    private void openWindowIngresos() {
        controllerIngresos.setContent(fxPrincipalController);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(nodeIngresos, 0d);
        AnchorPane.setTopAnchor(nodeIngresos, 0d);
        AnchorPane.setRightAnchor(nodeIngresos, 0d);
        AnchorPane.setBottomAnchor(nodeIngresos, 0d);
        fxPrincipalController.getVbContent().getChildren().add(nodeIngresos);
    }

    private void openWindowNotaCredito() {
        controllerNotaCredito.setContent(fxPrincipalController);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(nodeNotaCredito, 0d);
        AnchorPane.setTopAnchor(nodeNotaCredito, 0d);
        AnchorPane.setRightAnchor(nodeNotaCredito, 0d);
        AnchorPane.setBottomAnchor(nodeNotaCredito, 0d);
        fxPrincipalController.getVbContent().getChildren().add(nodeNotaCredito);
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
    private void onKeyPressedPos(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowPos();
        }
    }

    @FXML
    private void onActionPos(ActionEvent event) {
        openWindowPos();
    }

    @FXML
    private void onKeyPressedIngresos(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowIngresos();
        }
    }

    @FXML
    private void onActionIngresos(ActionEvent event) {
        openWindowIngresos();
    }

    @FXML
    private void onKeyPressedNotaCredito(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowNotaCredito();
        }
    }

    @FXML
    private void onActionNotaCredito(ActionEvent event) {
        openWindowNotaCredito();
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
