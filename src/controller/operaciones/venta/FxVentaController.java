package controller.operaciones.venta;

import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.BancoADO;
import model.CajaADO;
import model.CajaTB;
import model.PrivilegioTB;

public class FxVentaController implements Initializable {

    @FXML
    private VBox window;
    @FXML
    private VBox hbContenedorVentas;
    @FXML
    private TabPane tbContenedor;
    @FXML
    private Tab tbVentaUno;
    @FXML
    private Button btnAgregarVenta;

    private AnchorPane vbPrincipal;

    private FxVentaEstructuraController ventaEstructuraController;

    private ObservableList<PrivilegioTB> privilegioTBs;

    private boolean aperturaCaja;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        aperturaCaja = false;
        ventaEstructuraController = (FxVentaEstructuraController) addEstructura(tbVentaUno);
    }

    public void loadPrivilegios(ObservableList<PrivilegioTB> privilegioTBs) {
        this.privilegioTBs = privilegioTBs;
        if (privilegioTBs.get(0).getIdPrivilegio() != 0 && !privilegioTBs.get(0).isEstado()) {
            btnAgregarVenta.setDisable(true);
        }
        ventaEstructuraController.loadPrivilegios(privilegioTBs);
    }

    private Object addEstructura(Tab tab) {
        Object object = null;
        try {
            FXMLLoader fXMLSeleccionado = new FXMLLoader(getClass().getResource(FilesRouters.FX_VENTA_ESTRUCTURA));
            VBox seleccionado = fXMLSeleccionado.load();
            object = fXMLSeleccionado.getController();
            tab.setContent(seleccionado);
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
        return object;
    }

    public void loadElements() {
        ventaEstructuraController.setContent(vbPrincipal);
        ventaEstructuraController.getTxtSearch().requestFocus();
    }

    private void addTabVentaEstructura() {
        Tab tab = new Tab("Venta " + (tbContenedor.getTabs().size() + 1));
        tbContenedor.getTabs().add(tab);
        FxVentaEstructuraController controller = (FxVentaEstructuraController) addEstructura(tab);
        controller.setContent(vbPrincipal);
        controller.getTxtSearch().requestFocus();
        controller.loadPrivilegios(privilegioTBs);
    }

    public void loadValidarCaja() {

        if ("".equals(Session.ID_CUENTA_EFECTIVO) && "".equals(Session.NOMBRE_CUENTA_EFECTIVO)) {
            openWindowCajaNoRegistrada("Su caja no esta configurada para este punto de venta, dirijase al modulo CAJA/BANCO para configurar una nueva caja");
        } else {
            boolean validate = BancoADO.ValidarBanco(Session.ID_CUENTA_EFECTIVO, Session.NOMBRE_CUENTA_EFECTIVO);
            if (validate) {
                CajaTB cajaTB = CajaADO.ValidarCreacionCaja(Session.USER_ID);
                switch (cajaTB.getId()) {
                    case 1:
                        openWindowFondoInicial();
                        break;
                    case 2:
                         aperturaCaja = true;
                        hbContenedorVentas.setDisable(false);
                        break;
                    case 3:
                        openWindowValidarCaja(cajaTB.getIdCaja(), cajaTB.getFechaApertura() + " " + cajaTB.getHoraApertura());
                        break;
                    default:
                        break;
                }
            } else {
                openWindowCajaNoRegistrada("Su caja no esta registrada en la base de datos o se modifico, dirijase al modulo CAJA/BANCO para configurar una nueva caja.");
            }
        }

    }

    public void openWindowCajaNoRegistrada(String mensaje) {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_CAJA_NO_REGISTRADA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());

            FxCajaNoRegistrada controller = fXMLLoader.getController();
            controller.initElements(mensaje);

            Stage stage = WindowStage.StageLoaderModal(parent, "Caja no registrada", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                 if (aperturaCaja) {
                    aperturaCaja = false;
                    hbContenedorVentas.setDisable(true);
                }
            });
            stage.show();
        } catch (IOException ex) {
        }
    }

    public void openWindowFondoInicial() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_VENTA_FONDO_INICIAL);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxVentaFondoInicialController controller = fXMLLoader.getController();
            controller.setInitVentaController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Fondo incial", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                if (!aperturaCaja) {
                    hbContenedorVentas.setDisable(true);
                } else {
                    hbContenedorVentas.setDisable(false);
                }
            });
            stage.show();
        } catch (IOException ex) {
            System.out.println("controller.ingresos.venta.FxVentaController.openWindowFondoInicial():" + ex.getLocalizedMessage());
        }
    }

    private void openWindowValidarCaja(String idCaja, String dateTime) {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_VENTA_VALIDAR_CAJA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxVentaValidarCajaController controller = fXMLLoader.getController();
            controller.setInitVentaController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Validar apertura y cierre de caja", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                if (aperturaCaja) {
                    aperturaCaja = false;
                    hbContenedorVentas.setDisable(true);
                }
            });
            stage.show();
            controller.loadData(idCaja, dateTime);
        } catch (IOException ex) {

        }

    }

    @FXML
    private void onKeyPressedAgregarVenta(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            addTabVentaEstructura();
        }
    }

    @FXML
    private void onActionAgregarVenta(ActionEvent event) {
        addTabVentaEstructura();
    }

    public void setAperturaCaja(boolean aperturaCaja) {
        this.aperturaCaja = aperturaCaja;
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
