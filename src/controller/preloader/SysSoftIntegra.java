package controller.preloader;

import com.sun.javafx.application.LauncherImpl;
import controller.login.FxLoginController;
import controller.tools.FilesRouters;
import controller.tools.WindowStage;
import controller.tools.ObjectGlobal;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SysSoftIntegra extends Application {
        
    private FxLoginController loginController;
    
    private Scene scene;

    @Override
    public void init() throws Exception {
        URL urllogin = getClass().getResource(FilesRouters.FX_LOGIN);
        FXMLLoader fXMLLoaderLogin = WindowStage.LoaderWindow(urllogin);
        Parent parent = fXMLLoaderLogin.load(urllogin.openStream());
        loginController = fXMLLoaderLogin.getController();
        ObjectGlobal.PANE = new AnchorPane();
        scene = new Scene(parent);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.getIcons().add(new Image(FilesRouters.IMAGE_ICON));
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setTitle(FilesRouters.TITLE_APP);
        primaryStage.centerOnScreen();
        primaryStage.setMaximized(true);
        primaryStage.show();
        primaryStage.requestFocus();
        loginController.initComponents();
    }

    public static void main(String[] args) {
        LauncherImpl.launchApplication(SysSoftIntegra.class, SplashScreen.class, args);
    }

}
