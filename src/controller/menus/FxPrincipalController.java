package controller.menus;

import controller.banco.FxBancosController;
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
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.MenuADO;
import model.MenuTB;
import model.SubMenusTB;

public class FxPrincipalController implements Initializable {

    @FXML
    private ScrollPane spWindow;
    @FXML
    private AnchorPane vbPrincipal;
    @FXML
    private AnchorPane vbContent;
    @FXML
    private Label lblPuesto;
    @FXML
    private ImageView imState;
    @FXML
    private Text lblEstado;
    @FXML
    private VBox hbMenus;
    @FXML
    private HBox btnInicio;
    @FXML
    private HBox btnOperaciones;
    @FXML
    private HBox btnConsultas;
    @FXML
    private HBox btnBancos;
    @FXML
    private HBox btnInventario;
    @FXML
    private HBox btnProduccion;
    @FXML
    private HBox btnContactos;
    @FXML
    private HBox btnReportes;
    @FXML
    private HBox btnConfiguracion;
    @FXML
    private Text lblDatos;
    @FXML
    private VBox vbSiderBar;

    private HBox hbReferent;

    private ScrollPane fxInicio;

    private HBox fxOperaciones;

    private FxOperacionesController operacionesController;

    private HBox fxConsultas;

    private FxConsultasController consultasController;

    private HBox fxBancos;

    private FxBancosController bancosController;

    private HBox fxInventario;

    private FxInventarioController inventarioController;

    private HBox fxProduccion;

    private HBox fxContactos;

    private FxContactosController contactosController;

    private HBox fxReportes;

    private HBox fxConfiguracion;

    private FxConfiguracionController configuracionController;

    private boolean isExpand = true;

    private double width_siderbar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        lblEstado.setText(Session.CONNECTION_SESSION == true ? "Conectado" : "Desconectado");
        imState.setImage(Session.CONNECTION_SESSION == true ? new Image("/view/image/connected.png") : new Image("/view/image/disconnected.png"));
        hbReferent = btnInicio;
        hbReferent.getStyleClass().add("buttonMenuActivate");
        ((Label) btnInicio.getChildren().get(0)).getStyleClass().add("buttonMenuActivateText");
        try {
            FXMLLoader fXMLInicio = new FXMLLoader(getClass().getResource(FilesRouters.FX_INICIO));
            fxInicio = fXMLInicio.load();

            FXMLLoader fXMLOperaciones = new FXMLLoader(getClass().getResource(FilesRouters.FX_OPERACIONES));
            fxOperaciones = fXMLOperaciones.load();
            operacionesController = fXMLOperaciones.getController();
            operacionesController.setContent(vbPrincipal, vbContent);

            FXMLLoader fXMLConsultas = new FXMLLoader(getClass().getResource(FilesRouters.FX_CONSULTAS));
            fxConsultas = fXMLConsultas.load();
            consultasController = fXMLConsultas.getController();
            consultasController.setContent(vbPrincipal, vbContent);

            FXMLLoader fXMLBancos = new FXMLLoader(getClass().getResource(FilesRouters.FX_BANCOS));
            fxBancos = fXMLBancos.load();
            bancosController = fXMLBancos.getController();
            bancosController.setContent(vbPrincipal, vbContent);
            bancosController.loadTableViewBanco("");

            FXMLLoader fXMLInventario = new FXMLLoader(getClass().getResource(FilesRouters.FX_INVENTARIO));
            fxInventario = fXMLInventario.load();
            inventarioController = fXMLInventario.getController();
            inventarioController.setContent(vbPrincipal, vbContent);

            FXMLLoader fXMLProduccion = new FXMLLoader(getClass().getResource(FilesRouters.FX_PRODUCCION));
            fxProduccion = fXMLProduccion.load();
            FxProduccionController produccionController = fXMLProduccion.getController();
            produccionController.setContent(vbPrincipal, vbContent);

            FXMLLoader fXMLContactos = new FXMLLoader(getClass().getResource(FilesRouters.FX_CONTACTOS));
            fxContactos = fXMLContactos.load();
            contactosController = fXMLContactos.getController();
            contactosController.setContent(vbPrincipal, vbContent);

            FXMLLoader fXMLReportes = new FXMLLoader(getClass().getResource(FilesRouters.FX_REPORTES));
            fxReportes = fXMLReportes.load();
            FxReportesController controllerReportes = fXMLReportes.getController();
            controllerReportes.setContent(vbPrincipal, vbContent);

            FXMLLoader fXMLConfiguracion = new FXMLLoader(getClass().getResource(FilesRouters.FX_CONFIGURACION));
            fxConfiguracion = fXMLConfiguracion.load();
            configuracionController = fXMLConfiguracion.getController();
            configuracionController.setContent(vbPrincipal, vbContent);
        } catch (IOException ex) {
            System.out.println("Error en controller principal:" + ex.getLocalizedMessage());
        }
    }

    public void initLoadMenus() {
        try {

            ExecutorService executor = Executors.newCachedThreadPool((runnable) -> {
                Thread t = new Thread(runnable);
                t.setDaemon(true);
                return t;
            });

            Task<ObservableList<MenuTB>> task = new Task<ObservableList<MenuTB>>() {
                @Override
                protected ObservableList<MenuTB> call() throws Exception {
                    Thread.sleep(1000);
                    btnInicio.setOnMouseClicked((event) -> onMouseClickedMenus(event, fxInicio, btnInicio));
                    btnOperaciones.setOnMouseClicked((event) -> onMouseClickedMenus(event, fxOperaciones, btnOperaciones));
                    btnConsultas.setOnMouseClicked((event) -> onMouseClickedMenus(event, fxConsultas, btnConsultas));
                    btnBancos.setOnMouseClicked((event) -> onMouseClickedMenus(event, fxBancos, btnBancos));
                    btnInventario.setOnMouseClicked((event) -> onMouseClickedMenus(event, fxInventario, btnInventario));
                    btnProduccion.setOnMouseClicked((event) -> onMouseClickedMenus(event, fxProduccion, btnProduccion));
                    btnContactos.setOnMouseClicked((event) -> onMouseClickedMenus(event, fxContactos, btnContactos));
                    btnReportes.setOnMouseClicked((event) -> onMouseClickedMenus(event, fxReportes, btnReportes));
                    btnConfiguracion.setOnMouseClicked((event) -> onMouseClickedMenus(event, fxConfiguracion, btnConfiguracion));

                    return MenuADO.GetMenus(Session.USER_ROL);
                }
            };

            task.setOnScheduled(e -> {
                ObjectGlobal.InitializationTransparentBackgroundPrincipal(vbPrincipal);
            });
            task.setOnRunning(e -> {

            });
            task.setOnSucceeded(e -> {
                ObservableList<MenuTB> menuTBs = task.getValue();
                if (menuTBs.get(0).getIdMenu() != 0 && !menuTBs.get(0).isEstado()) {
                    hbMenus.getChildren().remove(btnInicio);
                }
                if (menuTBs.get(1).getIdMenu() != 0 && !menuTBs.get(1).isEstado()) {
                    hbMenus.getChildren().remove(btnOperaciones);
                } else {
                    ObservableList<SubMenusTB> subMenusTBs = MenuADO.GetSubMenus(Session.USER_ROL, menuTBs.get(1).getIdMenu());
                    operacionesController.loadSubMenus(subMenusTBs);
                }

                if (menuTBs.get(2).getIdMenu() != 0 && !menuTBs.get(2).isEstado()) {
                    hbMenus.getChildren().remove(btnConsultas);
                } else {
                    ObservableList<SubMenusTB> subMenusTBs = MenuADO.GetSubMenus(Session.USER_ROL, menuTBs.get(2).getIdMenu());
                    consultasController.loadSubMenus(subMenusTBs);
                }

                if (menuTBs.get(3).getIdMenu() != 0 && !menuTBs.get(3).isEstado()) {
                    hbMenus.getChildren().remove(btnInventario);
                } else {
                    ObservableList<SubMenusTB> subMenusTBs = MenuADO.GetSubMenus(Session.USER_ROL, menuTBs.get(3).getIdMenu());
                    inventarioController.loadSubMenus(subMenusTBs);
                }

                if (menuTBs.get(4).getIdMenu() != 0 && !menuTBs.get(4).isEstado()) {
                    hbMenus.getChildren().remove(btnProduccion);
                }

                if (menuTBs.get(5).getIdMenu() != 0 && !menuTBs.get(5).isEstado()) {
                    hbMenus.getChildren().remove(btnContactos);
                } else {
                    ObservableList<SubMenusTB> subMenusTBs = MenuADO.GetSubMenus(Session.USER_ROL, menuTBs.get(3).getIdMenu());
                    contactosController.loadSubMenus(subMenusTBs);
                }

                if (menuTBs.get(6).getIdMenu() != 0 && !menuTBs.get(6).isEstado()) {
                    hbMenus.getChildren().remove(btnReportes);
                }

                if (menuTBs.get(7).getIdMenu() != 0 && !menuTBs.get(7).isEstado()) {

                }

                if (menuTBs.get(8).getIdMenu() != 0 && !menuTBs.get(8).isEstado()) {
                    hbMenus.getChildren().remove(btnConfiguracion);
                } else {
                    ObservableList<SubMenusTB> subMenusTBs = MenuADO.GetSubMenus(Session.USER_ROL, menuTBs.get(8).getIdMenu());
                    configuracionController.loadSubMenus(subMenusTBs);
                }

                ObjectGlobal.PANE_PRINCIPAL.getChildren().clear();
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE_PRINCIPAL);
            });

            task.setOnFailed(e -> {
                ObjectGlobal.PANE_PRINCIPAL.getChildren().clear();
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE_PRINCIPAL);
            });

            executor.execute(task);
            if (!executor.isShutdown()) {
                executor.shutdown();
            }

        } catch (Exception ex) {
            ObjectGlobal.PANE_PRINCIPAL.getChildren().clear();
            vbPrincipal.getChildren().remove(ObjectGlobal.PANE_PRINCIPAL);
        }
    }

    public void initInicioController() {
        Stage stage = (Stage) spWindow.getScene().getWindow();
        stage.setOnCloseRequest(c -> {
            try {
                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                short option = Tools.AlertMessageConfirmation(spWindow, "SysSoft Integra", "¿Está seguro de cerrar la aplicación?");
                if (option == 1) {
                    System.exit(0);
                } else {
                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                    c.consume();
                }
            } catch (Exception ex) {
                System.out.println("Close window:" + ex.getLocalizedMessage());
            }
        });

        width_siderbar = vbSiderBar.getPrefWidth();

        setNode(fxInicio);

    }

    public void initWindowSize() {
        vbPrincipal.widthProperty().addListener((ObservableValue<? extends Number> ob, Number ol, Number newValue) -> {
            Session.WIDTH_WINDOW = (double) newValue;
            ObjectGlobal.PANE.setPrefWidth(Session.WIDTH_WINDOW);
        });

        vbPrincipal.heightProperty().addListener((ObservableValue<? extends Number> ob, Number ol, Number newValue) -> {
            Session.HEIGHT_WINDOW = (double) newValue;
            ObjectGlobal.PANE.setPrefHeight(Session.HEIGHT_WINDOW);
        });
    }

    public void initUserSession(String value) {
        lblPuesto.setText(value);
        lblDatos.setText(Session.USER_NAME);
    }

    private void setNode(Node node) {
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(node, 0d);
        AnchorPane.setTopAnchor(node, 0d);
        AnchorPane.setRightAnchor(node, 0d);
        AnchorPane.setBottomAnchor(node, 0d);
        vbContent.getChildren().add(node);
    }

    private void selectFocust(HBox hbButton) {
        hbReferent.getStyleClass().remove("buttonMenuActivate");
        ((Label) hbReferent.getChildren().get(0)).getStyleClass().remove("buttonMenuActivateText");

        hbReferent = hbButton;
        hbReferent.getStyleClass().add("buttonMenuActivate");
        ((Label) hbReferent.getChildren().get(0)).getStyleClass().add("buttonMenuActivateText");
    }

    private void onMouseClickedMenus(MouseEvent event, Node vista, HBox menu) {
        if (event.getSource() == menu) {
            setNode(vista);
            selectFocust(menu);
            event.consume();
        }
    }

    @FXML
    private void onMouseClickedSiderBar(MouseEvent event) {
        if (isExpand) {
            vbSiderBar.setPrefWidth(0);
            isExpand = false;
        } else {
            vbSiderBar.setPrefWidth(width_siderbar);
            isExpand = true;
        }
        event.consume();
    }

    @FXML
    private void onMouseClickedCerrar(MouseEvent event) throws IOException {
        Tools.Dispose(vbPrincipal);
        URL urllogin = getClass().getResource(FilesRouters.FX_LOGIN);
        FXMLLoader fXMLLoaderLogin = WindowStage.LoaderWindow(urllogin);
        Parent parent = fXMLLoaderLogin.load(urllogin.openStream());
        Scene scene = new Scene(parent);
        Stage primaryStage = new Stage();
        primaryStage.getIcons().add(new Image(FilesRouters.IMAGE_ICON));
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setTitle(FilesRouters.TITLE_APP);
        primaryStage.centerOnScreen();
        primaryStage.setMaximized(true);
        primaryStage.show();
        primaryStage.requestFocus();
    }

}
