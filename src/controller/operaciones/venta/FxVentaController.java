package controller.operaciones.venta;

import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import model.CajaADO;
import model.CajaTB;
import model.PrivilegioTB;

public class FxVentaController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private VBox hbContenedorVentas;
    @FXML
    private TabPane tbContenedor;
    @FXML
    private Tab tbVentaUno;
    @FXML
    private Button btnAgregarVenta;

    private FxVentaEstructuraController ventaEstructuraController;

//    private FxVentaEstructuraNuevoController ventaEstructuraNuevoController;
    private ObservableList<PrivilegioTB> privilegioTBs;

    private AnchorPane vbPrincipal;

    private boolean aperturaCaja;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        aperturaCaja = false;
        ventaEstructuraController = (FxVentaEstructuraController) addEstructura(tbVentaUno);
//        ventaEstructuraNuevoController = (FxVentaEstructuraNuevoController) addEstructura(tbVentaUno);
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
//            FXMLLoader fXMLSeleccionado = new FXMLLoader(getClass().getResource(FilesRouters.FX_VENTA_ESTRUCTURA_NUEVO));
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
//        ventaEstructuraNuevoController.setContent(vbPrincipal);
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

        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<CajaTB> task = new Task<CajaTB>() {
            @Override
            public CajaTB call() {
                return CajaADO.ValidarCreacionCaja(Session.USER_ID);
            }
        };

        task.setOnSucceeded(e -> {
//            vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            CajaTB cajaTB = task.getValue();
            if (cajaTB != null) {
                switch (cajaTB.getId()) {
                    case 1:
                        openWindowFondoInicial();
                        break;
                    case 2:
                        aperturaCaja = true;
                        hbContenedorVentas.setDisable(false);
                        Session.CAJA_ID = cajaTB.getIdCaja();
                        break;
                    case 3:
                        openWindowValidarCaja(cajaTB.getFechaApertura() + " " + cajaTB.getHoraApertura());
                        break;
                    default:
                        break;
                }
            } else {
                openWindowCajaNoRegistrada("No se pudo verificar el estado de su caja por problemas de red, intente nuevamente.");
            }
        });

        task.setOnFailed(e -> {
//            vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
        });

        task.setOnScheduled(e -> {
//            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);

        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
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

    private void openWindowValidarCaja(String dateTime) {
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
            controller.loadData(dateTime);
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
