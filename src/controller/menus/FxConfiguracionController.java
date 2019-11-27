package controller.menus;

import controller.configuracion.comprobante.FxTipoDocumentoController;
import controller.configuracion.empleados.FxEmpleadosController;
import controller.configuracion.etiquetas.FxEtiquetasController;
import controller.configuracion.impuestos.FxImpuestoController;
import controller.configuracion.miempresa.FxMiEmpresaController;
import controller.configuracion.moneda.FxMonedaController;
import controller.configuracion.roles.FxRolesController;
import controller.configuracion.tablasbasicas.FxDetalleMantenimientoController;
import controller.configuracion.tickets.FxTicketController;
import controller.tools.FilesRouters;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.SubMenusTB;

public class FxConfiguracionController implements Initializable {

    @FXML
    private HBox hbWindow;
    @FXML
    private HBox hbOperacionesUno;
    @FXML
    private VBox btnEmpresa;
    @FXML
    private VBox btnTablasBasicas;
    @FXML
    private VBox btnRoles;
    @FXML
    private VBox btnEmpleados;
    @FXML
    private VBox btnMoneda;
    @FXML
    private VBox btnComprobante;
    @FXML
    private HBox hbOperacionesDos;
    @FXML
    private VBox btnImpuesto;
    @FXML
    private VBox btnTickets;
    @FXML
    private VBox btnEtiqueta;
    /*
    Objectos de la ventana principal y venta que agrega al os hijos
     */
    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;
    /*
    Controller ticket
     */
    private FXMLLoader fXMLTicket;

    private VBox nodeTicketa;

    private FxTicketController controllerTicket;
    /*
    Controller etiquetas
     */
    private FXMLLoader fXMLEtiquetas;

    private VBox nodeEtiqueta;

    private FxEtiquetasController controllerEtiqueta;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            fXMLTicket = new FXMLLoader(getClass().getResource(FilesRouters.FX_TICKET));
            nodeTicketa = fXMLTicket.load();
            controllerTicket = fXMLTicket.getController();

            fXMLEtiquetas = new FXMLLoader(getClass().getResource(FilesRouters.FX_ETIQUETA));
            nodeEtiqueta = fXMLEtiquetas.load();
            controllerEtiqueta = fXMLEtiquetas.getController();
        } catch (IOException ex) {
            System.out.println("Error en Configuración Controller:" + ex.getLocalizedMessage());
        }
    }

    public void loadSubMenus(ObservableList<SubMenusTB> subMenusTBs) {
        if (subMenusTBs.get(0).getIdSubMenu() != 0 && !subMenusTBs.get(0).isEstado()) {
            hbOperacionesUno.getChildren().remove(btnEmpresa);
        } else {

        }
        if (subMenusTBs.get(1).getIdSubMenu() != 0 && !subMenusTBs.get(1).isEstado()) {
            hbOperacionesUno.getChildren().remove(btnTablasBasicas);
        } else {

        }
        if (subMenusTBs.get(2).getIdSubMenu() != 0 && !subMenusTBs.get(2).isEstado()) {
            hbOperacionesUno.getChildren().remove(btnRoles);
        } else {

        }
        if (subMenusTBs.get(3).getIdSubMenu() != 0 && !subMenusTBs.get(3).isEstado()) {
            hbOperacionesUno.getChildren().remove(btnEmpleados);
        } else {

        }
        if (subMenusTBs.get(4).getIdSubMenu() != 0 && !subMenusTBs.get(4).isEstado()) {
            hbOperacionesUno.getChildren().remove(btnMoneda);
        } else {

        }
        if (subMenusTBs.get(5).getIdSubMenu() != 0 && !subMenusTBs.get(5).isEstado()) {
            hbOperacionesUno.getChildren().remove(btnComprobante);
        } else {

        }
        if (subMenusTBs.get(6).getIdSubMenu() != 0 && !subMenusTBs.get(6).isEstado()) {
            hbOperacionesDos.getChildren().remove(btnImpuesto);
        } else {

        }
        if (subMenusTBs.get(7).getIdSubMenu() != 0 && !subMenusTBs.get(7).isEstado()) {
            hbOperacionesDos.getChildren().remove(btnTickets);
        } else {

        }
        if (subMenusTBs.get(8).getIdSubMenu() != 0 && !subMenusTBs.get(8).isEstado()) {
            hbOperacionesDos.getChildren().remove(btnEtiqueta);
        } else {

        }
    }

    private void openWindowTablasBasicas() {
        try {
            FXMLLoader fXMLPrincipal = new FXMLLoader(getClass().getResource(FilesRouters.FX_DETALLE_MANTENIMIENTO));
            VBox node = fXMLPrincipal.load();
            FxDetalleMantenimientoController controller = fXMLPrincipal.getController();
            controller.setContent(vbPrincipal);
            vbContent.getChildren().clear();
            AnchorPane.setLeftAnchor(node, 0d);
            AnchorPane.setTopAnchor(node, 0d);
            AnchorPane.setRightAnchor(node, 0d);
            AnchorPane.setBottomAnchor(node, 0d);
            vbContent.getChildren().add(node);
            controller.reloadListView();
        } catch (IOException ex) {
            System.out.println("Error en la view configuración:" + ex.getLocalizedMessage());
        }
    }

    private void openWindowCompany() {
        try {
            FXMLLoader fXMLPrincipal = new FXMLLoader(getClass().getResource(FilesRouters.FX_MI_EMPRESA));
            ScrollPane node = fXMLPrincipal.load();
            FxMiEmpresaController controller = fXMLPrincipal.getController();
            controller.setContent(vbPrincipal);
            vbContent.getChildren().clear();
            AnchorPane.setLeftAnchor(node, 0d);
            AnchorPane.setTopAnchor(node, 0d);
            AnchorPane.setRightAnchor(node, 0d);
            AnchorPane.setBottomAnchor(node, 0d);
            vbContent.getChildren().add(node);
        } catch (IOException ex) {
            System.out.println("Error en la view configuración:" + ex.getLocalizedMessage());
        }
    }

    private void openWindowPrivileges() {
        try {
            FXMLLoader fXMLPrincipal = new FXMLLoader(getClass().getResource(FilesRouters.FX_ROLES));
            VBox node = fXMLPrincipal.load();
            FxRolesController controller = fXMLPrincipal.getController();
            controller.setContent(vbPrincipal);
            vbContent.getChildren().clear();
            AnchorPane.setLeftAnchor(node, 0d);
            AnchorPane.setTopAnchor(node, 0d);
            AnchorPane.setRightAnchor(node, 0d);
            AnchorPane.setBottomAnchor(node, 0d);
            vbContent.getChildren().add(node);
        } catch (IOException ex) {
            System.out.println("Error en la view configuración:" + ex.getLocalizedMessage());
        }
    }

    private void openWindowEmployes() {
        try {
            FXMLLoader fXMLPrincipal = new FXMLLoader(getClass().getResource(FilesRouters.FX_EMPLEADO));
            VBox node = fXMLPrincipal.load();
            FxEmpleadosController controller = fXMLPrincipal.getController();
            controller.setContent(vbPrincipal);
            vbContent.getChildren().clear();
            AnchorPane.setLeftAnchor(node, 0d);
            AnchorPane.setTopAnchor(node, 0d);
            AnchorPane.setRightAnchor(node, 0d);
            AnchorPane.setBottomAnchor(node, 0d);
            vbContent.getChildren().add(node);
            controller.fillEmpleadosTable("");
        } catch (IOException ex) {
            System.out.println("Error en la view configuración:" + ex.getLocalizedMessage());
        }
    }

    private void openWindowMoney() {
        try {
            FXMLLoader fXMLPrincipal = new FXMLLoader(getClass().getResource(FilesRouters.FX_MONEDA));
            VBox node = fXMLPrincipal.load();
            FxMonedaController controller = fXMLPrincipal.getController();
            controller.setContent(vbPrincipal);
            vbContent.getChildren().clear();
            AnchorPane.setLeftAnchor(node, 0d);
            AnchorPane.setTopAnchor(node, 0d);
            AnchorPane.setRightAnchor(node, 0d);
            AnchorPane.setBottomAnchor(node, 0d);
            vbContent.getChildren().add(node);
            controller.fillTableMonedas();
        } catch (IOException ex) {
            System.out.println("Error en la view configuración:" + ex.getLocalizedMessage());
        }
    }

    private void openWindowVoucher() {
        try {
            FXMLLoader fXMLPrincipal = new FXMLLoader(getClass().getResource(FilesRouters.FX_TIPO_DOCUMENTO));
            VBox node = fXMLPrincipal.load();
            FxTipoDocumentoController controller = fXMLPrincipal.getController();
            controller.setContent(vbPrincipal);
            vbContent.getChildren().clear();
            AnchorPane.setLeftAnchor(node, 0d);
            AnchorPane.setTopAnchor(node, 0d);
            AnchorPane.setRightAnchor(node, 0d);
            AnchorPane.setBottomAnchor(node, 0d);
            vbContent.getChildren().add(node);
            controller.fillTabletTipoDocumento();
        } catch (IOException ex) {
            System.out.println("Error en la view configuración:" + ex.getLocalizedMessage());
        }
    }

    private void openWindowTex() {
        try {
            FXMLLoader fXMLPrincipal = new FXMLLoader(getClass().getResource(FilesRouters.FX_IMPUESTO));
            VBox node = fXMLPrincipal.load();
            FxImpuestoController controller = fXMLPrincipal.getController();
            controller.setContent(vbPrincipal);
            vbContent.getChildren().clear();
            AnchorPane.setLeftAnchor(node, 0d);
            AnchorPane.setTopAnchor(node, 0d);
            AnchorPane.setRightAnchor(node, 0d);
            AnchorPane.setBottomAnchor(node, 0d);
            vbContent.getChildren().add(node);
            controller.fillTabletTax();
        } catch (IOException ex) {
            System.out.println("Error en la view configuración:" + ex.getLocalizedMessage());
        }
    }

    private void openWindowTickets() {
        controllerTicket.setContent(vbPrincipal);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeTicketa, 0d);
        AnchorPane.setTopAnchor(nodeTicketa, 0d);
        AnchorPane.setRightAnchor(nodeTicketa, 0d);
        AnchorPane.setBottomAnchor(nodeTicketa, 0d);
        vbContent.getChildren().add(nodeTicketa);
    }

    private void openWindowEtiquetas() {
        controllerEtiqueta.setContent(vbPrincipal);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeEtiqueta, 0d);
        AnchorPane.setTopAnchor(nodeEtiqueta, 0d);
        AnchorPane.setRightAnchor(nodeEtiqueta, 0d);
        AnchorPane.setBottomAnchor(nodeEtiqueta, 0d);
        vbContent.getChildren().add(nodeEtiqueta);
        //controllerEtiqueta.loadEtiqueta(0, new File("./archivos/etiqueta.json").getAbsolutePath());
    }

    @FXML
    private void onKeyPressedCompany(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowCompany();
        }
    }

    @FXML
    private void onActionCompany(ActionEvent event) {
        openWindowCompany();
    }

    @FXML
    private void onKeyPressedTablasBasicas(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowTablasBasicas();
        }
    }

    @FXML
    private void onActionTablasBasicas(ActionEvent event) {
        openWindowTablasBasicas();
    }

    @FXML
    private void onKeyPressedPrivileges(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowPrivileges();
        }
    }

    @FXML
    private void onActionPrivileges(ActionEvent event) {
        openWindowPrivileges();
    }

    @FXML
    private void onKeyPressedEmployes(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowEmployes();
        }
    }

    @FXML
    private void onActionEmployes(ActionEvent event) {
        openWindowEmployes();
    }

    @FXML
    private void onKeyPressedMoney(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowMoney();
        }
    }

    @FXML
    private void onActionMoney(ActionEvent event) {
        openWindowMoney();
    }

    @FXML
    private void onKeyPressedVoucher(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowVoucher();
        }
    }

    @FXML
    private void onActionVoucher(ActionEvent event) {
        openWindowVoucher();
    }

    @FXML
    private void onKeyPressedTax(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowTex();
        }
    }

    @FXML
    private void onActionTax(ActionEvent event) {
        openWindowTex();
    }

    @FXML
    private void onKeyPressedTickets(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowTickets();
        }
    }

    @FXML
    private void onActionTickets(ActionEvent event) {
        openWindowTickets();
    }

    @FXML
    private void onKeyPressedEtiquetas(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowEtiquetas();
        }
    }

    @FXML
    private void onActionEtiquetas(ActionEvent event) {
        openWindowEtiquetas();
    }

    public void setContent(AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
