package controller.inventario.articulo;

import controller.configuracion.tablasbasicas.FxDetalleListaController;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.ArticuloADO;
import model.ArticuloTB;

public class FxArticuloProcesoController implements Initializable {

    @FXML
    private ScrollPane spWindow;
    @FXML
    private TextField txtClave;
    @FXML
    private TextField txtClaveAlterna;
    @FXML
    private TextField txtNombreMarca;
    @FXML
    private TextField txtNombreGenerico;
    @FXML
    private TextField txtMarca;
    @FXML
    private ImageView lnPrincipal;
    @FXML
    private Button btnRegister;
    @FXML
    private TextField txtStockMinimo;
    @FXML
    private TextField txtStockMaximo;
    @FXML
    private Text lblTitle;

    private String idArticulo;

    private File selectFile;

    private int idCategoria;

    private int idMarca;

    private FxArticulosController articulosController;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idArticulo = "";
        idCategoria = 0;
        idMarca = 0;
    }

    public void setValueEdit(String value) {
        lblTitle.setText("Editar datos del Producto");
        btnRegister.setText("Actualizar");
        btnRegister.getStyleClass().add("buttonLightWarning");
        txtClave.requestFocus();

        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                ArrayList<Object> arrayList = new ArrayList<>();
               // arrayList.add(ArticuloADO.GetArticulosById(value));
                return arrayList;
            }
        };

        task.setOnSucceeded(w -> {
            ArrayList<Object> objects = task.getValue();
            ArticuloTB articuloTB = (ArticuloTB) objects.get(0);
            if (articuloTB != null) {
                idArticulo = articuloTB.getIdArticulo();
                txtClave.setText(articuloTB.getClave());
                txtClaveAlterna.setText(articuloTB.getClaveAlterna());
                txtNombreMarca.setText(articuloTB.getNombreMarca());
                txtNombreGenerico.setText(articuloTB.getNombreGenerico());

                if (articuloTB.getCategoria() != 0) {
                    idCategoria = articuloTB.getCategoria();
                }

                if (articuloTB.getMarcar() != 0) {
                    idMarca = articuloTB.getMarcar();
                    txtMarca.setText(articuloTB.getMarcaName());
                }

                txtStockMinimo.setText(Tools.roundingValue(articuloTB.getStockMinimo(), 2));
                txtStockMaximo.setText(Tools.roundingValue(articuloTB.getStockMaximo(), 2));

                if (articuloTB.getImagenTB().equalsIgnoreCase("")) {
                    lnPrincipal.setImage(new Image("/view/image/no-image.png"));
                } else {
                    lnPrincipal.setImage(new Image(new File("" + articuloTB.getImagenTB()).toURI().toString()));
                }

            }
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void openWindowDetalle(String title, String idDetalle, boolean valor) {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_DETALLE_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxDetalleListaController controller = fXMLLoader.getController();
            controller.setControllerArticulo(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, title, spWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding((WindowEvent WindowEvent) -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();

            if (valor == true) {
                controller.initListNameImpuesto(idDetalle);
            } else {
                controller.initListDetalle(idDetalle, "");
            }
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }

    }

    private void aValidityProcess() {
        //primera validacion
        if (txtClave.getText().isEmpty()) {
            Tools.AlertMessage(spWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Articulo", "Ingrese la clave del artículo, por favor.", false);
            txtClave.requestFocus();
        } else if (txtNombreMarca.getText().isEmpty()) {
            Tools.AlertMessage(spWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Articulo", "Ingrese el nombre del artículo, por favor.", false);
            txtNombreMarca.requestFocus();
        }//segunda validacion
        else {
            crudArticulo();
        }

    }

    private void crudArticulo() {
        short confirmation = Tools.AlertMessage(spWindow.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Articulo", "¿Esta seguro de continuar?", true);
        if (confirmation == 1) {
            ArticuloTB articuloTB = new ArticuloTB();
            articuloTB.setIdArticulo(idArticulo);

            articuloTB.setClave(txtClave.getText().trim());
            articuloTB.setClaveAlterna(txtClaveAlterna.getText().trim());
            articuloTB.setNombreMarca(txtNombreMarca.getText().trim());
            articuloTB.setCategoria(idCategoria != 0
                    ? idCategoria
                    : 0);
            articuloTB.setStockMinimo(Tools.isNumeric(txtStockMinimo.getText())
                    ? Double.parseDouble(txtStockMinimo.getText().trim())
                    : 0);
            articuloTB.setStockMaximo(Tools.isNumeric(txtStockMaximo.getText())
                    ? Double.parseDouble(txtStockMaximo.getText().trim())
                    : 0);

            //Informacion adicional
            articuloTB.setImagenTB(selectFile != null
                    ? "./img/" + selectFile.getName()
                    : "");
            articuloTB.setNombreGenerico(txtNombreGenerico.getText().trim());
            articuloTB.setMarcar(idMarca != 0
                    ? idMarca
                    : 0);

//            String result = ArticuloADO.CrudArticulo(articuloTB);
//            switch (result) {
//                case "updated":
//                    Tools.AlertMessage(spWindow.getScene().getWindow(), Alert.AlertType.INFORMATION, "Articulo", "Actualizado correctamente el artículo.", false);
//                    closeWindow();
//                    articulosController.getTxtSearch().requestFocus();
//                    break;
//                case "duplicate":
//                    Tools.AlertMessage(spWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Articulo", "No se puede haber 2 artículos con la misma clave.", false);
//                    txtClave.requestFocus();
//                    break;
//                case "duplicatename":
//                    Tools.AlertMessage(spWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Articulo", "No se puede haber 2 artículos con el mismo nombre.", false);
//                    txtNombreMarca.requestFocus();
//                    break;
//                default:
//                    Tools.AlertMessage(spWindow.getScene().getWindow(), Alert.AlertType.ERROR, "Articulo", result, false);
//                    break;
//            }
        }
    }

    private void closeWindow() {
        vbContent.getChildren().remove(spWindow);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(articulosController.getWindow(), 0d);
        AnchorPane.setTopAnchor(articulosController.getWindow(), 0d);
        AnchorPane.setRightAnchor(articulosController.getWindow(), 0d);
        AnchorPane.setBottomAnchor(articulosController.getWindow(), 0d);
        vbContent.getChildren().add(articulosController.getWindow());
    }

    private void selectFileImage() {
        if (selectFile.getName().endsWith("png") || selectFile.getName().endsWith("jpg") || selectFile.getName().endsWith("jpeg") || selectFile.getName().endsWith(".gif")) {
            lnPrincipal.setImage(new Image(selectFile.toURI().toString()));
            FileInputStream inputStream = null;
            byte[] buffer = new byte[1024];
            try (FileOutputStream outputStream = new FileOutputStream("." + File.separator + "img" + File.separator + selectFile.getName())) {
                inputStream = new FileInputStream(selectFile.getAbsolutePath());
                int byteRead;
                while ((byteRead = inputStream.read(buffer)) != 1) {
                    outputStream.write(buffer, 0, byteRead);
                }
            } catch (Exception e) {
                if (e.getMessage() != null) {
                    Tools.AlertMessage(spWindow.getScene().getWindow(), Alert.AlertType.ERROR, "Artículo", e.getLocalizedMessage() + ": Consulte con el proveedor del sistema del problema :D", false);
                }
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();

                    } catch (IOException ex) {
                        Logger.getLogger(FxArticuloProcesoController.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    @FXML
    private void onActionToRegister(ActionEvent event) {
        aValidityProcess();
    }

    @FXML
    private void onKeyPressedToRegister(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            aValidityProcess();
        }
    }

    @FXML
    private void onKeyPressedToCancel(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            closeWindow();
        }
    }

    @FXML
    private void onActionToCancel(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void onActionPhoto(ActionEvent event) {
        //idArticulo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar una imagen");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Elija una imagen", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        selectFile = fileChooser.showOpenDialog(spWindow.getScene().getWindow());
        if (selectFile != null) {
            selectFile = new File(selectFile.getAbsolutePath());
            File fcom = new File("./img/" + selectFile.getName());
            if (fcom.exists()) {
                fcom.delete();
                selectFileImage();
            } else {
                selectFileImage();
            }
        }
    }

    @FXML
    private void onActionRemovePhoto(ActionEvent event) {
        lnPrincipal.setImage(new Image("/view/image/no-image.png"));
        selectFile = null;
    }

    @FXML
    private void onKeyTypedClave(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedClaveAlterna(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedMinimo(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtStockMinimo.getText().contains(".") || c == '-' && txtStockMinimo.getText().contains("-")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedMaxino(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtStockMaximo.getText().contains(".") || c == '-' && txtStockMaximo.getText().contains("-")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedDetalle(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if (c != '\b') {
            event.consume();
        }
    }

    @FXML
    private void onKeyReleasedMarca(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            openWindowDetalle("Agregar Marca", "0007", false);
        }
    }

    @FXML
    private void onMouseClickedMarca(MouseEvent event) {
        if (event.getClickCount() == 2) {
            openWindowDetalle("Agregar Marca", "0007", false);
        }
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        closeWindow();
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

    public TextField getTxtMarca() {
        return txtMarca;
    }

    public TextField getTxtClave() {
        return txtClave;
    }

    public void setInitControllerArticulos(FxArticulosController articulosController, AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.articulosController = articulosController;
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
