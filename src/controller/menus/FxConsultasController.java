package controller.menus;

import controller.egresos.compras.FxComprasRealizadasController;
import controller.ingresos.cortecaja.FxCajaConsultasController;
import controller.ingresos.venta.FxVentaRealizadasController;
import controller.inventario.kardex.FxArticuloKardexController;
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

public class FxConsultasController implements Initializable {

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
    private VBox btnKardex;
    /*
    Objectos de la ventana principal y venta que agrega al os hijos
     */
    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    /*
    Controller ventas realizadas
     */
    private FXMLLoader fXMLVentaRealizadas;

    private VBox nodeVentaRealizadas;

    private FxVentaRealizadasController ventaRealizadasController;

    /*
    Controller kardex
     */
    private FXMLLoader fXMLKardex;

    private HBox nodeKardex;

    private FxArticuloKardexController controllerKardex;

    /*
    Controller compras realizadas
     */
    private FXMLLoader fXMLComprasRealizadas;

    private VBox nodeComprasRealizadas;

    private FxComprasRealizadasController controllerComprasRealizadas;
    /*
    Controller caja consultas
     */
    private FXMLLoader fXMLCajaConsultas;

    private ScrollPane nodeCajaConsultas;

    private FxCajaConsultasController controlleCajaConsultas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            fXMLVentaRealizadas = new FXMLLoader(getClass().getResource(FilesRouters.FX_VENTA_REALIZADAS));
            nodeVentaRealizadas = fXMLVentaRealizadas.load();
            ventaRealizadasController = fXMLVentaRealizadas.getController();

            fXMLKardex = new FXMLLoader(getClass().getResource(FilesRouters.FX_ARTICULO_KARDEX));
            nodeKardex = fXMLKardex.load();
            controllerKardex = fXMLKardex.getController();

            fXMLComprasRealizadas = new FXMLLoader(getClass().getResource(FilesRouters.FX_COMPRAS_REALIZADAS));
            nodeComprasRealizadas = fXMLComprasRealizadas.load();
            controllerComprasRealizadas = fXMLComprasRealizadas.getController();

            fXMLCajaConsultas = new FXMLLoader(getClass().getResource(FilesRouters.FX_CAJA_CONSULTA));
            nodeCajaConsultas = fXMLCajaConsultas.load();
            controlleCajaConsultas = fXMLCajaConsultas.getController();

        } catch (IOException ex) {
            System.out.println("Error en Inventario Controller:" + ex.getLocalizedMessage());
        }
    }

    public void loadSubMenus(ObservableList<SubMenusTB> subMenusTBs) {
        if (subMenusTBs.get(0).getIdSubMenu() != 0 && !subMenusTBs.get(0).isEstado()) {
            hbOperacionesUno.getChildren().remove(btnVentas);
        } else {
            ObservableList<PrivilegioTB> privilegioTBs = MenuADO.GetPrivilegios(Session.USER_ROL, subMenusTBs.get(0).getIdSubMenu());
            ventaRealizadasController.loadPrivilegios(privilegioTBs);
        }

        if (subMenusTBs.get(1).getIdSubMenu() != 0 && !subMenusTBs.get(1).isEstado()) {
            hbOperacionesUno.getChildren().remove(btnCompras);
        } else {

        }

        if (subMenusTBs.get(2).getIdSubMenu() != 0 && !subMenusTBs.get(2).isEstado()) {
            hbOperacionesDos.getChildren().remove(btnKardex);
        } else {

        }

        if (subMenusTBs.get(3).getIdSubMenu() != 0 && !subMenusTBs.get(3).isEstado()) {
            hbOperacionesUno.getChildren().remove(btnCorteCaja);
        } else {

        }
    }

    private void openWindowVentaRealizadas() {
        ventaRealizadasController.setContent(vbPrincipal, vbContent);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeVentaRealizadas, 0d);
        AnchorPane.setTopAnchor(nodeVentaRealizadas, 0d);
        AnchorPane.setRightAnchor(nodeVentaRealizadas, 0d);
        AnchorPane.setBottomAnchor(nodeVentaRealizadas, 0d);
        vbContent.getChildren().add(nodeVentaRealizadas);
        ventaRealizadasController.loadInit();
    }

    private void openWindowPurchasesMade() {
        controllerComprasRealizadas.setContent(vbPrincipal, vbContent);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeComprasRealizadas, 0d);
        AnchorPane.setTopAnchor(nodeComprasRealizadas, 0d);
        AnchorPane.setRightAnchor(nodeComprasRealizadas, 0d);
        AnchorPane.setBottomAnchor(nodeComprasRealizadas, 0d);
        vbContent.getChildren().add(nodeComprasRealizadas);
        controllerComprasRealizadas.fillPurchasesTable((short) 0, "", Tools.getDate(), Tools.getDate(), 0);
    }

    private void openWindowKardex() {
        controllerKardex.setContent(vbPrincipal);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeKardex, 0d);
        AnchorPane.setTopAnchor(nodeKardex, 0d);
        AnchorPane.setRightAnchor(nodeKardex, 0d);
        AnchorPane.setBottomAnchor(nodeKardex, 0d);
        vbContent.getChildren().add(nodeKardex);
    }

    private void openWindowCortesCaja() {
        controlleCajaConsultas.setContent(vbPrincipal);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeCajaConsultas, 0d);
        AnchorPane.setTopAnchor(nodeCajaConsultas, 0d);
        AnchorPane.setRightAnchor(nodeCajaConsultas, 0d);
        AnchorPane.setBottomAnchor(nodeCajaConsultas, 0d);
        vbContent.getChildren().add(nodeCajaConsultas);
    }

    @FXML
    private void onKeyPressedComprasRealizadas(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowPurchasesMade();
        }
    }

    @FXML
    private void onActionComprasRealizadas(ActionEvent event) {
        openWindowPurchasesMade();
    }

    @FXML
    private void onKeyPressedVentasRealizadas(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowVentaRealizadas();
        }
    }

    @FXML
    private void onActionVentasRealizadas(ActionEvent event) {
        openWindowVentaRealizadas();
    }

    @FXML
    private void onKeyPressedKardex(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowKardex();
        }
    }

    @FXML
    private void onActionKardex(ActionEvent event) {
        openWindowKardex();
    }

    @FXML
    private void onKeyPressedCorteCaja(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowCortesCaja();
        }
    }

    @FXML
    private void onActionCorteCaja(ActionEvent event) {
        openWindowCortesCaja();
    }

    public void setContent(AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
