package controller.menus;

import controller.operaciones.compras.FxComprasController;
import controller.operaciones.cotizacion.FxCotizacionController;
import controller.operaciones.guiaremision.FxGuiaRemisionController;
import controller.operaciones.notacredito.FxNotaCreditoController;
import controller.operaciones.pedidos.FxPedidosController;
import controller.operaciones.venta.FxVentaController;
import controller.tools.FilesRouters;
import controller.tools.Session;
import controller.tools.Tools;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.MenuADO;
import model.PrivilegioTB;
import model.SubMenusTB;

public class FxOperacionesController implements Initializable {

    @FXML
    private TextField txtSearch;
    @FXML
    private HBox hbOperacionesUno;
    @FXML
    private HBox hbOperacionesDos;
    @FXML
    private VBox btnVentas;
    @FXML
    private VBox btnCompras;
    @FXML
    private VBox btnCotizacion;
    @FXML
    private VBox btnGuiaRemision;
    @FXML
    private VBox btnNotaCredito;
    @FXML
    private VBox btnPedido;
    /*
    Objectos de la ventana principal y venta que agrega a los hijos
     */
    private FxPrincipalController fxPrincipalController;

    /*
    Controller ventas
     */
    private FXMLLoader fXMLVentaOld;

    private AnchorPane nodeVentaOld;

    private FxVentaController controllerVentaOld;

    /*
    Controller ventas nueva
     */
    private FXMLLoader fXMLVentaNew;

    private AnchorPane nodeVentaNew;

    private FxVentaController controllerVentaNew;

    /*
    Controller compra
     */
    private FXMLLoader fXMLCompra;

    private ScrollPane nodeCompra;

    private FxComprasController controllerCompras;

    /*
    Controller compra
     */
    private FXMLLoader fXMLCotizacion;

    private AnchorPane nodeCotizacion;

    private FxCotizacionController controllerCotizacion;

    /*
    Controller guia remision
     */
    private FXMLLoader fXMLGuiaRemision;

    private ScrollPane nodeGuiaRemision;

    private FxGuiaRemisionController controllerGuiaRemision;

    /*
    Controller corte de caja
     */
    private FXMLLoader fXMLNotaCredito;

    private AnchorPane nodeNotaCredito;

    private FxNotaCreditoController controllerNotaCredito;

    /*
    Controller pedidos
     */
    private FXMLLoader fXMLPedido;

    private AnchorPane nodePedido;

    private FxPedidosController controllerPedido;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            fXMLVentaOld = new FXMLLoader(getClass().getResource(FilesRouters.FX_VENTA));
            nodeVentaOld = fXMLVentaOld.load();
            controllerVentaOld = fXMLVentaOld.getController();
            controllerVentaOld.setTipoVenta((short) 1);
            controllerVentaOld.loadComponents();

            fXMLVentaNew = new FXMLLoader(getClass().getResource(FilesRouters.FX_VENTA));
            nodeVentaNew = fXMLVentaNew.load();
            controllerVentaNew = fXMLVentaNew.getController();
            controllerVentaNew.setTipoVenta((short) 2);
            controllerVentaNew.loadComponents();

            fXMLCompra = new FXMLLoader(getClass().getResource(FilesRouters.FX_COMPRAS));
            nodeCompra = fXMLCompra.load();
            controllerCompras = fXMLCompra.getController();

            fXMLCotizacion = new FXMLLoader(getClass().getResource(FilesRouters.FX_COTIZACION));
            nodeCotizacion = fXMLCotizacion.load();
            controllerCotizacion = fXMLCotizacion.getController();

            fXMLGuiaRemision = new FXMLLoader(getClass().getResource(FilesRouters.FX_GUIA_REMISION));
            nodeGuiaRemision = fXMLGuiaRemision.load();
            controllerGuiaRemision = fXMLGuiaRemision.getController();

            fXMLNotaCredito = new FXMLLoader(getClass().getResource(FilesRouters.FX_NOTA_CREDITO));
            nodeNotaCredito = fXMLNotaCredito.load();
            controllerNotaCredito = fXMLNotaCredito.getController();

            fXMLPedido = new FXMLLoader(getClass().getResource(FilesRouters.FX_PEDIDOS));
            nodePedido = fXMLPedido.load();
            controllerPedido = fXMLPedido.getController();

        } catch (IOException ex) {
            System.out.println("Error en Ingresos Controller:" + ex.getLocalizedMessage());
        }
    }

    public void loadSubMenus(ObservableList<SubMenusTB> subMenusTBs) {

        if (subMenusTBs.get(0).getIdSubMenu() != 0 && !subMenusTBs.get(0).isEstado()) {
            hbOperacionesUno.getChildren().remove(btnVentas);
        } else {
            ObservableList<PrivilegioTB> privilegioTBs = MenuADO.GetPrivilegios(Session.USER_ROL, subMenusTBs.get(0).getIdSubMenu());
            controllerVentaOld.loadPrivilegios(privilegioTBs);
            controllerVentaNew.loadPrivilegios(privilegioTBs);
        }

        if (subMenusTBs.get(1).getIdSubMenu() != 0 && !subMenusTBs.get(1).isEstado()) {
            hbOperacionesUno.getChildren().remove(btnCompras);
        } else {
            ObservableList<PrivilegioTB> privilegioTBs = MenuADO.GetPrivilegios(Session.USER_ROL, subMenusTBs.get(1).getIdSubMenu());
            controllerCompras.loadPrivilegios(privilegioTBs);
        }

        if (subMenusTBs.get(2).getIdSubMenu() != 0 && !subMenusTBs.get(2).isEstado()) {
            hbOperacionesUno.getChildren().remove(btnCotizacion);
        }

        if (subMenusTBs.get(3).getIdSubMenu() != 0 && !subMenusTBs.get(3).isEstado()) {
            hbOperacionesUno.getChildren().remove(btnGuiaRemision);
        }

        if (subMenusTBs.get(4).getIdSubMenu() != 0 && !subMenusTBs.get(4).isEstado()) {
            hbOperacionesDos.getChildren().remove(btnNotaCredito);
        } else {

        }

        if (subMenusTBs.get(5).getIdSubMenu() != 0 && !subMenusTBs.get(5).isEstado()) {
            hbOperacionesDos.getChildren().remove(btnPedido);
        } else {

        }
    }

    private void openWindowVenta() {
        controllerVentaOld.setContent(fxPrincipalController);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(nodeVentaOld, 0d);
        AnchorPane.setTopAnchor(nodeVentaOld, 0d);
        AnchorPane.setRightAnchor(nodeVentaOld, 0d);
        AnchorPane.setBottomAnchor(nodeVentaOld, 0d);
        fxPrincipalController.getVbContent().getChildren().add(nodeVentaOld);
        controllerVentaOld.loadElements();
    }

    private void openWindowVentaNueva() {
        controllerVentaNew.setContent(fxPrincipalController);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(nodeVentaNew, 0d);
        AnchorPane.setTopAnchor(nodeVentaNew, 0d);
        AnchorPane.setRightAnchor(nodeVentaNew, 0d);
        AnchorPane.setBottomAnchor(nodeVentaNew, 0d);
        fxPrincipalController.getVbContent().getChildren().add(nodeVentaNew);
        controllerVentaNew.loadElements();
    }

    private void openWindowCompra() {
        controllerCompras.setContent(fxPrincipalController);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(nodeCompra, 0d);
        AnchorPane.setTopAnchor(nodeCompra, 0d);
        AnchorPane.setRightAnchor(nodeCompra, 0d);
        AnchorPane.setBottomAnchor(nodeCompra, 0d);
        fxPrincipalController.getVbContent().getChildren().add(nodeCompra);
    }

    private void openWindowCotizacion() {
        controllerCotizacion.setContent(fxPrincipalController);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(nodeCotizacion, 0d);
        AnchorPane.setTopAnchor(nodeCotizacion, 0d);
        AnchorPane.setRightAnchor(nodeCotizacion, 0d);
        AnchorPane.setBottomAnchor(nodeCotizacion, 0d);
        fxPrincipalController.getVbContent().getChildren().add(nodeCotizacion);
    }

    private void openWindowGuiaRemision() {
        controllerGuiaRemision.setContent(fxPrincipalController);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(nodeGuiaRemision, 0d);
        AnchorPane.setTopAnchor(nodeGuiaRemision, 0d);
        AnchorPane.setRightAnchor(nodeGuiaRemision, 0d);
        AnchorPane.setBottomAnchor(nodeGuiaRemision, 0d);
        fxPrincipalController.getVbContent().getChildren().add(nodeGuiaRemision);
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

    private void openWindowPedidos() {
        controllerPedido.setContent(fxPrincipalController);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(nodePedido, 0d);
        AnchorPane.setTopAnchor(nodePedido, 0d);
        AnchorPane.setRightAnchor(nodePedido, 0d);
        AnchorPane.setBottomAnchor(nodePedido, 0d);
        fxPrincipalController.getVbContent().getChildren().add(nodePedido);
    }

    @FXML
    private void onKeyPressedVentas(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowVenta();
        }
    }

    @FXML
    private void onActionVentas(ActionEvent event) {
        openWindowVenta();
    }

    @FXML
    public void onActionVentasNueva(ActionEvent event) {
        openWindowVentaNueva();
    }

    @FXML
    public void onKeyPressedVentasNueva(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            openWindowVentaNueva();
        }
    }

    @FXML
    private void onKeyPressedGuiaRemision(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowGuiaRemision();
        }
    }

    @FXML
    private void onActionGuiaRemision(ActionEvent event) {
        openWindowGuiaRemision();
    }

    @FXML
    private void onKeyPressedCompras(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowCompra();
        }
    }

    @FXML
    private void onActionCompras(ActionEvent event) {
        openWindowCompra();
    }

    @FXML
    private void onKeyPressedCotizacion(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowCotizacion();
        }
    }

    @FXML
    private void onActionCotizacion(ActionEvent event) {
        openWindowCotizacion();
    }

    @FXML
    private void onKeyPresseNotaCredito(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowNotaCredito();
        }
    }

    @FXML
    private void onActioNotaCredito(ActionEvent event) {
        openWindowNotaCredito();
    }

    @FXML
    private void onKeyPressedPedidos(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowPedidos();
        }
    }

    @FXML
    private void onActioPedidos(ActionEvent event) {
        openWindowPedidos();
    }

    public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }

}
