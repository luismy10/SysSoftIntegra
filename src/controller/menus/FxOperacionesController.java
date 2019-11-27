package controller.menus;

import controller.egresos.compras.FxComprasController;
import controller.ingresos.cortecaja.FxCajaController;
import controller.ingresos.venta.FxVentaController;
import controller.inventario.articulo.FxArticulosController;
import controller.produccion.movimientos.FxMovimientosController;
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
    private VBox btnCorteCaja;
    @FXML
    private VBox btnMovimiento;
    @FXML
    private VBox btnArticulo;

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

    private FxVentaController controllerVenta;

    /*
    Controller articulos
     */
    private FXMLLoader fXMLArticulo;

    private VBox nodeArticulo;

    private FxArticulosController controllerArticulo;

    /*
    Controller movimiento     
     */
    private FXMLLoader fXMLMovimiento;

    private HBox nodeMovimiento;

    private FxMovimientosController controllerMovimiento;

    /*
    Controller compra
     */
    private FXMLLoader fXMLCompra;

    private ScrollPane nodeCompra;

    private FxComprasController controllerCompras;
    /*
    Controller corte de caja
     */
    private FXMLLoader fXMLCorteCaja;

    private VBox nodeCorteCaja;

    private FxCajaController controllerCorteCaja;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            fXMLVenta = new FXMLLoader(getClass().getResource(FilesRouters.FX_VENTA));
            nodeVenta = fXMLVenta.load();
            controllerVenta = fXMLVenta.getController();

            fXMLArticulo = new FXMLLoader(getClass().getResource(FilesRouters.FX_ARTICULOS));
            nodeArticulo = fXMLArticulo.load();
            controllerArticulo = fXMLArticulo.getController();

            fXMLMovimiento = new FXMLLoader(getClass().getResource(FilesRouters.FX_MOVIMIENTOS));
            nodeMovimiento = fXMLMovimiento.load();
            controllerMovimiento = fXMLMovimiento.getController();

            fXMLCompra = new FXMLLoader(getClass().getResource(FilesRouters.FX_COMPRAS));
            nodeCompra = fXMLCompra.load();
            controllerCompras = fXMLCompra.getController();

            fXMLCorteCaja = new FXMLLoader(getClass().getResource(FilesRouters.FX_CAJA));
            nodeCorteCaja = fXMLCorteCaja.load();
            controllerCorteCaja = fXMLCorteCaja.getController();

        } catch (IOException ex) {
            System.out.println("Error en Ingresos Controller:" + ex.getLocalizedMessage());
        }
    }

    public void loadSubMenus(ObservableList<SubMenusTB> subMenusTBs) {

        if (subMenusTBs.get(0).getIdSubMenu() != 0 && !subMenusTBs.get(0).isEstado()) {
            hbOperacionesUno.getChildren().remove(btnVentas);
        } else {
            ObservableList<PrivilegioTB> privilegioTBs = MenuADO.GetPrivilegios(Session.USER_ROL, subMenusTBs.get(0).getIdSubMenu());
            controllerVenta.loadPrivilegios(privilegioTBs);
        }

        if (subMenusTBs.get(1).getIdSubMenu() != 0 && !subMenusTBs.get(1).isEstado()) {
            hbOperacionesDos.getChildren().remove(btnArticulo);
        }

        if (subMenusTBs.get(2).getIdSubMenu() != 0 && !subMenusTBs.get(2).isEstado()) {
            hbOperacionesUno.getChildren().remove(btnCompras);
        } else {
            ObservableList<PrivilegioTB> privilegioTBs = MenuADO.GetPrivilegios(Session.USER_ROL, subMenusTBs.get(2).getIdSubMenu());
            controllerCompras.loadPrivilegios(privilegioTBs);
        }

        if (subMenusTBs.get(3).getIdSubMenu() != 0 && !subMenusTBs.get(3).isEstado()) {
            hbOperacionesUno.getChildren().remove(btnCorteCaja);
        } else {
            ObservableList<PrivilegioTB> privilegioTBs = MenuADO.GetPrivilegios(Session.USER_ROL, subMenusTBs.get(3).getIdSubMenu());
            controllerCorteCaja.loadPrivilegios(privilegioTBs);
        }

        if (subMenusTBs.get(4).getIdSubMenu() != 0 && !subMenusTBs.get(4).isEstado()) {
            hbOperacionesDos.getChildren().remove(btnMovimiento);
        }

    }

    private void openWindowVenta() {
        controllerVenta.setContent(vbPrincipal);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeVenta, 0d);
        AnchorPane.setTopAnchor(nodeVenta, 0d);
        AnchorPane.setRightAnchor(nodeVenta, 0d);
        AnchorPane.setBottomAnchor(nodeVenta, 0d);
        vbContent.getChildren().add(nodeVenta);
        controllerVenta.loadValidarCaja();
        controllerVenta.loadElements();
    }

    private void openWindowArticulos() {
        controllerArticulo.setContent(vbPrincipal, vbContent);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeArticulo, 0d);
        AnchorPane.setTopAnchor(nodeArticulo, 0d);
        AnchorPane.setRightAnchor(nodeArticulo, 0d);
        AnchorPane.setBottomAnchor(nodeArticulo, 0d);
        vbContent.getChildren().add(nodeArticulo);
        if (controllerArticulo.getTvList().getItems().isEmpty()) {
            controllerArticulo.fillArticlesTablePaginacion();
        }
    }

    private void openWindowCompra() {
        controllerCompras.setContent(vbPrincipal);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeCompra, 0d);
        AnchorPane.setTopAnchor(nodeCompra, 0d);
        AnchorPane.setRightAnchor(nodeCompra, 0d);
        AnchorPane.setBottomAnchor(nodeCompra, 0d);
        vbContent.getChildren().add(nodeCompra);
    }

    private void openWindowCorteCaja() {
        controllerCorteCaja.setContent(vbPrincipal, vbContent);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeCorteCaja, 0d);
        AnchorPane.setTopAnchor(nodeCorteCaja, 0d);
        AnchorPane.setRightAnchor(nodeCorteCaja, 0d);
        AnchorPane.setBottomAnchor(nodeCorteCaja, 0d);
        vbContent.getChildren().add(nodeCorteCaja);

    }

    private void openWindowMovimientos() {
        controllerMovimiento.setContent(vbPrincipal, vbContent);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeMovimiento, 0d);
        AnchorPane.setTopAnchor(nodeMovimiento, 0d);
        AnchorPane.setRightAnchor(nodeMovimiento, 0d);
        AnchorPane.setBottomAnchor(nodeMovimiento, 0d);
        vbContent.getChildren().add(nodeMovimiento);
        if (controllerMovimiento.getTvList().getItems().isEmpty()) {
            controllerMovimiento.setOpcion((short) 2);
            controllerMovimiento.fillTableMovimiento(false, (short) 2, 0, Tools.getDate(), Tools.getDate());
        }
    }

    private void openWindowPrecios() {

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
    private void onKeyPressedCorteCaja(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowCorteCaja();
        }
    }

    @FXML
    private void onActionCorteCaja(ActionEvent event) {
        openWindowCorteCaja();
    }

    @FXML
    private void onKeyPressedArticulos(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowArticulos();
        }
    }

    @FXML
    private void onActionArticulos(ActionEvent event) {
        openWindowArticulos();
    }

    @FXML
    private void onKeyPressedMovimientos(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowMovimientos();
        }
    }

    @FXML
    private void onActionMovimientos(ActionEvent event) {
        openWindowMovimientos();
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

    private void onKeyPressedPrecios(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowPrecios();
        }
    }

    private void onActionPrecios(ActionEvent event) {
        openWindowPrecios();
    }

    public void setContent(AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
