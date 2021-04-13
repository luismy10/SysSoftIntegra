package controller.tools;

import controller.inventario.suministros.FxSuministrosListaController;
import controller.menus.FxPrincipalController;
import controller.operaciones.compras.FxComprasController;
import controller.produccion.compras.FxComprasInsumosController;
import controller.produccion.insumos.FxInsumosListaController;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class WindowStage {

    public static FXMLLoader LoaderWindow(URL ruta) {
        FXMLLoader fXMLLoader = new FXMLLoader();
        URL url = ruta;
        fXMLLoader.setLocation(url);
        fXMLLoader.setBuilderFactory(new JavaFXBuilderFactory());
        return fXMLLoader;
    }

    public static Stage StageLoaderModal(Parent parent, String title, Window window) {
        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        stage.getIcons().add(new Image(FilesRouters.IMAGE_ICON));
        stage.setScene(scene);
        stage.setTitle(title);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(window);
        return stage;
    }

    public static Stage StageLoader(Parent parent, String title) {
        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        stage.getIcons().add(new Image(FilesRouters.IMAGE_ICON));
        stage.setScene(scene);
        stage.setTitle(title);
        return stage;
    }

    public static void openWindowSuministrasAdd(FxPrincipalController fxPrincipalController, ScrollPane scrollPane, FxComprasController comprasController) {
        try {
            fxPrincipalController.openFondoModal();
            URL url = WindowStage.class.getClassLoader().getClass().getResource(FilesRouters.FX_SUMINISTROS_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxSuministrosListaController controller = fXMLLoader.getController();
            controller.setInitComprasController(comprasController);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Suministros", scrollPane.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
            controller.fillSuministrosTable((short)0,""); 
        } catch (IOException ex) {
            System.out.println("Controller compras" + ex.getLocalizedMessage());
        }
    }
    
      public static void openWindowInsumosAdd(FxPrincipalController fxPrincipalController, ScrollPane scrollPane, FxComprasInsumosController comprasInsumosController) {
        try {
            fxPrincipalController.openFondoModal();
            URL url = WindowStage.class.getClassLoader().getClass().getResource(FilesRouters.FX_INSUMO_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxInsumosListaController controller = fXMLLoader.getController();
            controller.setInitComprasController(comprasInsumosController);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Insumo", scrollPane.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
             stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
            controller.fillTableInsumos((short) 0, "");
        } catch (IOException ex) {
            System.out.println("Controller compras" + ex.getLocalizedMessage());
        }
    }


}
