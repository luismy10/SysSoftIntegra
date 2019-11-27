package controller.contactos.clientes;

import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.DirectorioADO;
import model.DirectorioTB;
import model.ImageADO;
import model.ImagenTB;

public class FxPerfilController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private VBox vbList;
    @FXML
    private Text lblInformation;
    @FXML
    private ImageView ivPerfil;

    private String idPersona;

    private long idImagen;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idPersona = "";
        idImagen = 0;
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
    }

    private void onViewAsignacion() throws IOException {
        URL url = getClass().getResource(FilesRouters.FX_CLIENTE_ASIGNAR);
        FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
        Parent parent = fXMLLoader.load(url.openStream());
        //Controlller here
        FxAsignacionController controller = fXMLLoader.getController();
        //
        Stage stage = WindowStage.StageLoaderModal(parent, "Asignar información", window.getScene().getWindow());
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
        controller.setViewAdd(idPersona);
    }

    public void setLoadView(String idPersona, String information) {
        this.idPersona = idPersona;
        lblInformation.setText(information);
        loadViewImage(idPersona);
        loadViewUpdate(idPersona);
    }

    public void loadViewUpdate(String idPersona) {
        try {
            ArrayList<DirectorioTB> arrayList = DirectorioADO.GetIdDirectorio(idPersona);
            for (int i = 0; i < arrayList.size(); i++) {
                FXMLLoader fXMLLoader = new FXMLLoader(getClass().getResource(FilesRouters.FX_CARD));
                HBox node = fXMLLoader.load();
                FxCardController controller = fXMLLoader.getController();
                controller.setIdDirectorio(arrayList.get(i).getIdDirectorio());
                controller.setIdAtributo(arrayList.get(i).getAtributo());
                controller.getLblSubTitle().setText(arrayList.get(i).getNombre());
                controller.getLblTitle().setText(arrayList.get(i).getValor());
                vbList.getChildren().addAll(node);
            }

        } catch (IOException ex) {
            System.out.println(ex);
        }

    }

    private void loadViewImage(String idRepresentante) {
//        ImagenTB imagenTB = ImageADO.GetImage(idRepresentante,true);
//        if (imagenTB.getIdImage() != 0) {
//            idImagen = imagenTB.getIdImage();
//            ivPerfil.setImage(imagenTB.getImagen());
//        }
    }

    @FXML
    private void onKeyPressedToRegister(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            onViewAsignacion();
        }
    }

    @FXML
    private void onActionToRegister(ActionEvent event) throws IOException {
        onViewAsignacion();
    }

    @FXML
    private void onKeyPressedCerrar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(window);
        }
    }

    @FXML
    private void onActionCerrar(ActionEvent event) {
        Tools.Dispose(window);
    }

    @FXML
    private void onKeyPressedToReload(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            vbList.getChildren().clear();
            loadViewUpdate(idPersona);
        }
    }

    @FXML
    private void onActionToReload(ActionEvent event) {
        vbList.getChildren().clear();
        loadViewUpdate(idPersona);
    }

    @FXML
    private void onMouseClickedImage(MouseEvent event) throws FileNotFoundException {
        if (event.getClickCount() == 2) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Importar una imagen");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Elija una imagen", "*.png", "*.jpg", "*.jpeg"));
            File file = fileChooser.showOpenDialog(window.getScene().getWindow());
            if (file != null) {
                file = new File(file.getAbsolutePath());
                if (file.getName().endsWith("png") || file.getName().endsWith("jpg") || file.getName().endsWith("jpeg")) {
                    ivPerfil.setImage(new Image(file.toURI().toString()));
                    try (InputStream inputStream = new FileInputStream(file)) {
                        ImagenTB imagenTB = new ImagenTB();
                        imagenTB.setIdImage(idImagen);
                        imagenTB.setFile(inputStream);
                        imagenTB.setIdRelacionado(idPersona);
                        String result = ImageADO.CrudImage(imagenTB);
                        if (result.equalsIgnoreCase("insert")) {
                            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Perfil", "Registrado correctamente.", false);
                        } else if (result.equalsIgnoreCase("update")) {
                            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Perfil", "Actualizado correctamente.", false);
                        } else if (result.equalsIgnoreCase("error")) {
                            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Perfil", "No se puedo completar la ejecución.", false);
                        } else {
                            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Perfil", result, false);
                        }
                    } catch (Exception ex) {

                    }
                }
            }
        }
    }

}
