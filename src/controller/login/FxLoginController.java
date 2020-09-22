package controller.login;

import controller.menus.FxPrincipalController;
import controller.tools.FilesRouters;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import model.EmpleadoADO;
import model.EmpleadoTB;

public class FxLoginController implements Initializable {

    @FXML
    private AnchorPane apLogin;
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtClave;
    @FXML
    private Label lblError;
    @FXML
    private Button lblEntrar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    private void eventEntrar() {
        if (Tools.isText(txtUsuario.getText())) {
            Tools.AlertMessageWarning(apLogin, "Iniciar Sesión", "Ingrese su usuario");
            txtUsuario.requestFocus();
        } else if (Tools.isText(txtClave.getText())) {
            Tools.AlertMessageWarning(apLogin, "Iniciar Sesión", "Ingrese su contraseña");
            txtClave.requestFocus();
        } else {

            ExecutorService executor = Executors.newCachedThreadPool((runnable) -> {
                Thread t = new Thread(runnable);
                t.setDaemon(true);
                return t;
            });

            Task<Short> task = new Task<Short>() {
                @Override
                protected Short call() throws Exception {

                    EmpleadoTB empleadoTB = EmpleadoADO.GetValidateUser(txtUsuario.getText().trim(), txtClave.getText().trim());
                    if (empleadoTB != null) {
                        if (empleadoTB.getIdEmpleado() != null) {

                            Session.USER_ROL = empleadoTB.getRol();
                            Session.USER_ID = empleadoTB.getIdEmpleado();
                            Session.USER_NAME = (empleadoTB.getApellidos().substring(0, 1).toUpperCase() + empleadoTB.getApellidos().substring(1).toLowerCase()) + " " + empleadoTB.getNombres().substring(0, 1).toUpperCase() + empleadoTB.getNombres().substring(1).toLowerCase();
                            Session.USER_PUESTO = empleadoTB.getRolName();

                            return 3;
                        } else {
                            return 2;
                        }
                    } else {
                        return 1;
                    }
                }
            };

            task.setOnScheduled(e -> {
                lblError.setText("Conectando al servidor...");
                lblEntrar.setDisable(true);
            });
            task.setOnRunning(e -> {
                lblError.setText("Iniciando la autentificación...");
            });
            task.setOnSucceeded(e -> {
                if (null != task.getValue()) {
                    switch (task.getValue()) {
                        case 1:
                            lblError.setText("Error: No se pudo conectar al servidor, revise su conexión e intente nuevamente.");
                            lblEntrar.setDisable(false);
                            break;
                        case 2:
                            lblError.setText("Los datos ingresados no son correctos.");
                            lblEntrar.setDisable(false);
                            break;
                        case 3:
                            lblError.setTextFill(Paint.valueOf("#ed3d1a"));
                            lblError.setText("Se completo la autentificación correctamente :D");
                            try {
                                Tools.Dispose(apLogin);
                                URL url = getClass().getResource(FilesRouters.FX_PRINCIPAL);
                                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                                Parent parent = fXMLLoader.load(url.openStream());
                                FxPrincipalController controller = fXMLLoader.getController();
                                Stage stage = new Stage();
                                Scene scene = new Scene(parent);
                                stage.getIcons().add(new Image(FilesRouters.IMAGE_ICON));
                                stage.setScene(scene);
                                stage.setTitle(FilesRouters.TITLE_APP);
                                stage.centerOnScreen();
                                stage.setMaximized(true);
                                stage.show();
                                stage.requestFocus();
                                controller.initLoadMenus();
                                controller.initInicioController();
                                controller.initWindowSize();
                                controller.initUserSession((Session.USER_PUESTO.substring(0, 1).toUpperCase() + Session.USER_PUESTO.substring(1).toLowerCase()));

                                Session.WIDTH_WINDOW = scene.getWidth();
                                Session.HEIGHT_WINDOW = scene.getHeight();
                            } catch (IOException exception) {
                                System.out.println("Error en la vista principal:" + exception.getLocalizedMessage());
                            }
                            lblEntrar.setDisable(false);
                            break;
                        default:
                            break;
                    }
                }
            });
            task.setOnFailed(e -> {
                lblError.setText("No se pudo completar la carga, revise su conexión e intente nuevamente.");
                lblEntrar.setDisable(false);
            });
            executor.execute(task);
            if (!executor.isShutdown()) {
                executor.shutdown();
            }
        }
    }

    @FXML
    private void onActionEntrar(ActionEvent event) {
        eventEntrar();
    }

    @FXML
    private void onKeyPressedEntrar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            eventEntrar();
        }
    }

    @FXML
    private void onActionUrlPrincipal(ActionEvent event) {
        try {
            Desktop d = Desktop.getDesktop();
            d.browse(new URI("www.syssoftintegra.com"));
        } catch (IOException | URISyntaxException ex) {
            Tools.println(ex.getMessage());
        }
    }

    @FXML
    private void onActionWhatsApp(ActionEvent event) {
        try {
            Desktop d = Desktop.getDesktop();
            d.browse(new URI("https://wa.link/64zbb1"));
        } catch (IOException | URISyntaxException ex) {
            Tools.println(ex.getMessage());
        }
    }

    public void initComponents() {
        txtUsuario.requestFocus();
    }

}
