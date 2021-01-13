package controller.menus;

import controller.banco.FxBancosController;
import controller.consultas.compras.FxComprasRealizadasController;
import controller.consultas.pagar.FxCuentasPorCobrarController;
import controller.consultas.pagar.FxCuentasPorPagarController;
import controller.operaciones.cortecaja.FxCajaConsultasController;
import controller.operaciones.cotizacion.FxCotizacionRealizadasController;
import controller.operaciones.guiaremision.FxGuiaRemisionRealizadasController;
import controller.operaciones.venta.FxVentaRealizadasController;
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
    private VBox btnCuentasPorCobrar;
    @FXML
    private VBox btnCuentasPorPagar;
    @FXML
    private VBox btnBancos;
    @FXML
    private VBox btnCotizaciones;
    @FXML
    private VBox btnGuiaRemision;
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
    Controller compras realizadas
     */
    private FXMLLoader fXMLComprasRealizadas;

    private VBox nodeComprasRealizadas;

    private FxComprasRealizadasController controllerComprasRealizadas;

    /*
    Controller cotizaciones realizadas
     */
    private FXMLLoader fXMLCotizaciones;

    private VBox nodeCotizaciones;

    private FxCotizacionRealizadasController controllerCotizaciones;

    /*
    Controller guia remision realizadas
     */
    private FXMLLoader fXMLGuiaRemision;

    private VBox nodeGuiaRemision;

    private FxGuiaRemisionRealizadasController controllerGuiaRemision;

    /*
    Controller caja consultas
     */
    private FXMLLoader fXMLCajaConsultas;

    private ScrollPane nodeCajaConsultas;

    private FxCajaConsultasController controlleCajaConsultas;

    /*
    Controller cuentas por pagar
     */
    private FXMLLoader fXMLCuentasPorCobrar;

    private VBox nodeCuentasPorCobrar;

    private FxCuentasPorCobrarController controlleCuentasPorCobrar;

    /*
    Controller cuentas por pagar
     */
    private FXMLLoader fXMLCuentasPorPagar;

    private VBox nodeCuentasPorPagar;

    private FxCuentasPorPagarController controlleCuentasPorPagar;

    /*
    Controller caja consultas
     */
    private FXMLLoader fXMLBancos;

    private HBox nodeBancos;

    private FxBancosController bancosController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            fXMLVentaRealizadas = new FXMLLoader(getClass().getResource(FilesRouters.FX_VENTA_REALIZADAS));
            nodeVentaRealizadas = fXMLVentaRealizadas.load();
            ventaRealizadasController = fXMLVentaRealizadas.getController();

            fXMLComprasRealizadas = new FXMLLoader(getClass().getResource(FilesRouters.FX_COMPRAS_REALIZADAS));
            nodeComprasRealizadas = fXMLComprasRealizadas.load();
            controllerComprasRealizadas = fXMLComprasRealizadas.getController();

            fXMLCotizaciones = new FXMLLoader(getClass().getResource(FilesRouters.FX_COTIZACION_REALIZADAS));
            nodeCotizaciones = fXMLCotizaciones.load();
            controllerCotizaciones = fXMLCotizaciones.getController();

            fXMLGuiaRemision = new FXMLLoader(getClass().getResource(FilesRouters.FX_GUIA_REMISION_REALIZADAS));
            nodeGuiaRemision = fXMLGuiaRemision.load();
            controllerGuiaRemision = fXMLGuiaRemision.getController();

            fXMLCajaConsultas = new FXMLLoader(getClass().getResource(FilesRouters.FX_CAJA_CONSULTA));
            nodeCajaConsultas = fXMLCajaConsultas.load();
            controlleCajaConsultas = fXMLCajaConsultas.getController();

            fXMLCuentasPorCobrar = new FXMLLoader(getClass().getResource(FilesRouters.FX_CUENTAS_POR_COBRAR));
            nodeCuentasPorCobrar = fXMLCuentasPorCobrar.load();
            controlleCuentasPorCobrar = fXMLCuentasPorCobrar.getController();

            fXMLCuentasPorPagar = new FXMLLoader(getClass().getResource(FilesRouters.FX_CUENTAS_POR_PAGAR));
            nodeCuentasPorPagar = fXMLCuentasPorPagar.load();
            controlleCuentasPorPagar = fXMLCuentasPorPagar.getController();

            fXMLBancos = new FXMLLoader(getClass().getResource(FilesRouters.FX_BANCOS));
            nodeBancos = fXMLBancos.load();
            bancosController = fXMLBancos.getController();

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
        }

        if (subMenusTBs.get(2).getIdSubMenu() != 0 && !subMenusTBs.get(2).isEstado()) {
            hbOperacionesUno.getChildren().remove(btnCorteCaja);
        }

        if (subMenusTBs.get(3).getIdSubMenu() != 0 && !subMenusTBs.get(3).isEstado()) {
            hbOperacionesUno.getChildren().remove(btnCuentasPorCobrar);
        }

        if (subMenusTBs.get(4).getIdSubMenu() != 0 && !subMenusTBs.get(4).isEstado()) {
            hbOperacionesUno.getChildren().remove(btnCuentasPorPagar);
        }

        if (subMenusTBs.get(5).getIdSubMenu() != 0 && !subMenusTBs.get(5).isEstado()) {
            hbOperacionesDos.getChildren().remove(btnBancos);
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

    private void openWindowCotizaciones() {
        controllerCotizaciones.setContent(vbPrincipal, vbContent);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeCotizaciones, 0d);
        AnchorPane.setTopAnchor(nodeCotizaciones, 0d);
        AnchorPane.setRightAnchor(nodeCotizaciones, 0d);
        AnchorPane.setBottomAnchor(nodeCotizaciones, 0d);
        vbContent.getChildren().add(nodeCotizaciones);
        controllerCotizaciones.loadInit();
    }

    private void openWindowGuiaRemision() {
        controllerGuiaRemision.setContent(vbPrincipal, vbContent);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeGuiaRemision, 0d);
        AnchorPane.setTopAnchor(nodeGuiaRemision, 0d);
        AnchorPane.setRightAnchor(nodeGuiaRemision, 0d);
        AnchorPane.setBottomAnchor(nodeGuiaRemision, 0d);
        vbContent.getChildren().add(nodeGuiaRemision);
        controllerGuiaRemision.loadInit();
    }

    private void openWindowPurchasesMade() {
        controllerComprasRealizadas.setContent(vbPrincipal, vbContent);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeComprasRealizadas, 0d);
        AnchorPane.setTopAnchor(nodeComprasRealizadas, 0d);
        AnchorPane.setRightAnchor(nodeComprasRealizadas, 0d);
        AnchorPane.setBottomAnchor(nodeComprasRealizadas, 0d);
        vbContent.getChildren().add(nodeComprasRealizadas);
        if (controllerComprasRealizadas.getTvList().getItems().isEmpty()) {
            controllerComprasRealizadas.fillPurchasesTable((short) 0, "", Tools.getDate(), Tools.getDate(), 0);
        }
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

    private void openWindowCuentasPorCobrar() {
        controlleCuentasPorCobrar.setContent(vbPrincipal, vbContent);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeCuentasPorCobrar, 0d);
        AnchorPane.setTopAnchor(nodeCuentasPorCobrar, 0d);
        AnchorPane.setRightAnchor(nodeCuentasPorCobrar, 0d);
        AnchorPane.setBottomAnchor(nodeCuentasPorCobrar, 0d);
        vbContent.getChildren().add(nodeCuentasPorCobrar);
        controlleCuentasPorCobrar.loadTableCuentasPorCobrar();
    }

    private void openWindowCuentasPorPagar() {
        controlleCuentasPorPagar.setContent(vbPrincipal, vbContent);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeCuentasPorPagar, 0d);
        AnchorPane.setTopAnchor(nodeCuentasPorPagar, 0d);
        AnchorPane.setRightAnchor(nodeCuentasPorPagar, 0d);
        AnchorPane.setBottomAnchor(nodeCuentasPorPagar, 0d);
        vbContent.getChildren().add(nodeCuentasPorPagar);
        controlleCuentasPorPagar.loadTableCuentasPorPagar();
    }

    private void openWindowBancos() {
        bancosController.setContent(vbPrincipal, vbContent);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeBancos, 0d);
        AnchorPane.setTopAnchor(nodeBancos, 0d);
        AnchorPane.setRightAnchor(nodeBancos, 0d);
        AnchorPane.setBottomAnchor(nodeBancos, 0d);
        vbContent.getChildren().add(nodeBancos);
        bancosController.loadTableViewBanco("");
    }

    @FXML
    private void onKeyPressedCuentasCobrar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowCuentasPorCobrar();
        }
    }

    @FXML
    private void onActionCuentasCobrar(ActionEvent event) {
        openWindowCuentasPorCobrar();
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
    private void onKeyPressedCotizaciones(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowCotizaciones();
        }
    }

    @FXML
    private void onActionCotizaciones(ActionEvent event) {
        openWindowCotizaciones();
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
    private void onKeyPressedCorteCaja(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowCortesCaja();
        }
    }

    @FXML
    private void onActionCorteCaja(ActionEvent event) {
        openWindowCortesCaja();
    }

    @FXML
    private void onKeyPressedCuentasPagar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowCuentasPorPagar();
        }
    }

    @FXML
    private void onActionCuentasPagar(ActionEvent event) {
        openWindowCuentasPorPagar();
    }

    @FXML
    private void onKeyPressedBanco(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowBancos();
        }
    }

    @FXML
    private void onActionBanco(ActionEvent event) {
        openWindowBancos();
    }

    public void setContent(AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
