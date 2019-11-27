package controller.configuracion.etiquetas;

import controller.inventario.articulo.FxArticulosController;
import controller.produccion.suministros.FxSuministrosController;
import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.ArticuloTB;
import model.EtiquetaADO;
import model.EtiquetaTB;
import model.SuministroTB;

public class FxEtiquetasBusquedaController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private VBox vbEtiquetas;

    private SelectecElement selectecElement;

    private VBoxContent etiquetaReferent;

    private FxEtiquetasController etiquetasController;

//    private FxArticulosController articulosController;
    private FxSuministrosController suministrosController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
        selectecElement = new SelectecElement();
    }

    public void onLoadEtiquetas(int type) {
        addElement(type);
    }

    private void addElement(int type) {
        ArrayList<EtiquetaTB> etbs = EtiquetaADO.ListEtiquetas(type);
        HBox hBox = new HBox();
        hBox.setStyle("-fx-spacing:0.8333333333333334em;-fx-padding:0.8333333333333334em;");
        //
        for (int i = 0; i < etbs.size(); i++) {
            Group group = new Group();
            group.setStyle("-fx-cursor:hand;");
            VBoxContent vBox = new VBoxContent();
            vBox.setId("" + etbs.get(i).getIdEtiqueta());
            vBox.setIdContent(etbs.get(i).getIdEtiqueta());
            vBox.setRuta(etbs.get(i).getRuta());
            vBox.setAlignment(Pos.CENTER);
            vBox.setStyle("-fx-background-color:white;-fx-spacing:0.4166666666666667em;-fx-padding: 0.4166666666666667em;-fx-border-color: #cccccc;");
            group.setOnMousePressed((MouseEvent event) -> {
                selectecElement.clear();
                selectecElement.add(group, vBox.getWidth(), vBox.getHeight());
                etiquetaReferent = vBox;
                event.consume();
            });

            ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(etbs.get(i).getImagen())));
            imageView.setFitWidth(210);
            imageView.setFitHeight(150);

            Label nombre = new Label(etbs.get(i).getNombre());
            nombre.getStyleClass().add("labelOpenSansRegular14");

            Label tipo = new Label(etbs.get(i).getNombreTipo());
            tipo.getStyleClass().add("labelRobotoBold14");

            Label medida = new Label(etbs.get(i).getMedida());
            medida.getStyleClass().add("labelOpenSansRegular16");

            vBox.getChildren().addAll(imageView, nombre, tipo, medida);
            group.getChildren().add(vBox);

            hBox.getChildren().add(group);
        }
        //
        vbEtiquetas.getChildren().add(hBox);
    }

    private void eventPreview(String ruta, SuministroTB suministroTB) {
        try {

            URL url = getClass().getResource(FilesRouters.FX_ETIQUETA_PREVISUALIZADOR);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxEtiquetasPrevisualizadorController controller = fXMLLoader.getController();
            controller.loadEtiqueta(ruta, suministroTB);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Vista previa", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.show();

        } catch (IOException exception) {
            System.out.println(exception);
        }
    }

    private void eventSelect() {
        if (etiquetaReferent != null) {
            if (etiquetasController != null) {
                etiquetasController.loadEtiqueta(etiquetaReferent.getIdContent(), etiquetaReferent.getRuta());
                Tools.Dispose(window);
            }
            if (suministrosController != null) {
                eventPreview(etiquetaReferent.getRuta(), suministrosController.getTvList().getSelectionModel().getSelectedItem());
            }
//            } else if (articulosController != null) {
//                eventPreview(etiquetaReferent.getRuta(), articulosController.getTvList().getSelectionModel().getSelectedItem());
//            }

        } else {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Etiqueta", "Seleccione una etiqueta para editarlo.", false);
        }

    }

    @FXML
    private void onKeyPressedSelect(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            eventSelect();
        }
    }

    @FXML
    private void onActionSelect(ActionEvent event) {
        eventSelect();
    }

    @FXML
    private void onKeyPressedCancelar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(window);
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        Tools.Dispose(window);
    }

    public void setInitEtiquetasController(FxEtiquetasController etiquetasController) {
        this.etiquetasController = etiquetasController;
    }

    public void setInitArticuloController(FxArticulosController articulosController) {
//        this.articulosController = articulosController;
    }

    public void setInitSuministroController(FxSuministrosController suministrosController) {
        this.suministrosController = suministrosController;
    }

    class VBoxContent extends VBox {

        private int idContent;

        private String ruta;

        public VBoxContent() {

        }

        public int getIdContent() {
            return idContent;
        }

        public void setIdContent(int idContent) {
            this.idContent = idContent;
        }

        public String getRuta() {
            return ruta;
        }

        public void setRuta(String ruta) {
            this.ruta = ruta;
        }

    }

    class SelectecElement {

        private final List<Node> selection = new ArrayList<>();

        public SelectecElement() {

        }

        public void clear() {
            for (int i = 0; i < selection.size(); i++) {
                Group group = (Group) selection.get(i);
                for (int j = 0; j < group.getChildren().size(); j++) {
                    if (group.getChildren().get(j) instanceof Region) {
                        Region region = (Region) group.getChildren().get(j);
                        if (region.getId().equalsIgnoreCase("rcon")) {
                            group.getChildren().remove(region);
                        }
                    }
                }
            }
        }

        public void add(Node node, double width, double height) {
            Group group = (Group) node;
            Region region = new Region();
            region.setId("rcon");
            region.setPrefSize(width, height);
            region.setStyle("-fx-border-color:green;-fx-border-width:2;");

            if (selection.contains(node)) {
                group.getChildren().add(region);
            } else {
                group.getChildren().add(region);
                selection.add(group);
            }

        }

    }

}
