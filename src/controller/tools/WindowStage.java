package controller.tools;

import controller.inventario.suministros.FxSuministrosCompraController;
import controller.inventario.suministros.FxSuministrosListaController;
import controller.operaciones.compras.FxComprasController;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.SuministroTB;

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

    public static void openWindowSuministrasAdd(AnchorPane vbPrincipal, ScrollPane scrollPane, FxComprasController comprasController) {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
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
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
            controller.fillSuministrosTable((short)0,""); 
        } catch (IOException ex) {
            System.out.println("Controller compras" + ex.getLocalizedMessage());
        }
    }

     public static void openWindowSuministroCompra(boolean background,AnchorPane vbPrincipal,FxComprasController comprasController, SuministroTB suministroTB,Window window) {
        try {
            if(background)ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = WindowStage.class.getClassLoader().getClass().getResource(FilesRouters.FX_SUMINISTROS_COMPRA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxSuministrosCompraController controller = fXMLLoader.getController();
//                    if (option == 1) {
            controller.setInitComprasController(comprasController);
            controller.setLoadData(suministroTB.getIdSuministro(),false);
//                    } else {
//                        controller.setInitComprasEditarController(comprasEditarController);
//                    }
//            controller.setLoadData(
//                    tvList.getSelectionModel().getSelectedItem().getIdSuministro(),
//                    tvList.getSelectionModel().getSelectedItem().getClave(),
//                    tvList.getSelectionModel().getSelectedItem().getNombreMarca(),
//                    Tools.roundingValue(tvList.getSelectionModel().getSelectedItem().getCostoCompra(), 8),
//                    tvList.getSelectionModel().getSelectedItem().getUnidadCompraName(),
//                    tvList.getSelectionModel().getSelectedItem().isLote()
//            );
            //

            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Producto", window);
            stage.setResizable(false);
            stage.sizeToScene();
            if(background)stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
            controller.getTxtCantidad().requestFocus();
            
        } catch (IOException ix) {
            System.out.println("Error Producto Lista Controller:" + ix.getLocalizedMessage());
        }
    }
     
     

}
