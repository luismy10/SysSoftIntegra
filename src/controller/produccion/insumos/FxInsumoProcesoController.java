package controller.produccion.insumos;

import controller.configuracion.tablasbasicas.FxDetalleClasesListaController;
import controller.configuracion.tablasbasicas.FxDetalleController;
import controller.configuracion.tablasbasicas.FxDetalleListaController;
import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.DetalleADO;
import model.InsumoADO;
import model.InsumoTB;

public class FxInsumoProcesoController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private TextField txtClave;
    @FXML
    private TextField txtDescripcion;
    @FXML
    private Button btnRegistro;
    @FXML
    private TextField txtMedida;
    @FXML
    private TextField txtCategoria;
    @FXML
    private TextField txtClaveAlterna;
    @FXML
    private TextField txtCosto;
    @FXML
    private TextField txtInventarioMinimo;
    @FXML
    private TextField txtInventarioMaximo;
    @FXML
    private TextField txtClase;
    @FXML
    private TextField txtSubClase;
    @FXML
    private TextField txtSubSubClase;

    private String idInsumo;

    private int idMedida;

    private int idCategoria;
    
    private String idClase;
    private String idSubClase;
    private String idSubSubClase;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
    }

    public void loadInsumo() {
        btnRegistro.setText("Actualizar");
        btnRegistro.getStyleClass().add("buttonLightWarning");
    }

    public void editarInsumo(String id) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<InsumoTB> task = new Task<InsumoTB>() {
            @Override
            public InsumoTB call() {
                return InsumoADO.GetInsumoById(id);
            }
        };
        task.setOnSucceeded(w -> {
            InsumoTB insumoTB = task.getValue();
            if (insumoTB != null) {
                idInsumo = insumoTB.getIdInsumo();
                txtClave.setText(insumoTB.getClave());
                txtClaveAlterna.setText(insumoTB.getClaveAlterna());
                txtDescripcion.setText(insumoTB.getNombreMarca());
                txtMedida.setText(insumoTB.getMedidaName());
                idMedida = insumoTB.getMedida();
                txtCategoria.setText(insumoTB.getCategoriaName());
                idCategoria = insumoTB.getCategoria();
                txtClase.setText(insumoTB.getNombreClase());
                idSubClase = insumoTB.getIdSubClase();
                txtSubClase.setText(insumoTB.getNombreSubClase());
                idSubSubClase = insumoTB.getIdSubSubClase();
                txtSubSubClase.setText(insumoTB.getNombreSubSubClase());
                idClase = insumoTB.getIdClase();
                txtCosto.setText("" + insumoTB.getCosto());
                txtInventarioMinimo.setText("" + insumoTB.getStockMinimo());
                txtInventarioMaximo.setText("" + insumoTB.getStockMaximo());
            }
        });
        task.setOnFailed(w -> {
        });
        task.setOnScheduled(w -> {
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void openWindowDetalle(String title, String idDetalle, boolean valor) {
        try {
            URL url = getClass().getResource(FilesRouters.FX_DETALLE_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxDetalleListaController controller = fXMLLoader.getController();
            controller.setControllerInsumoModal(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, title, apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
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

    private void validateRegistro() {
        if (Tools.isText(txtClave.getText())) {
            Tools.AlertMessageWarning(apWindow, "Insumo", "Ingrese una clave del insumo.");
            txtClave.requestFocus();
        } else if (Tools.isText(txtDescripcion.getText())) {
            Tools.AlertMessageWarning(apWindow, "Insumo", "Ingrese una descripción al insumo.");
            txtDescripcion.requestFocus();
        } else if (!Tools.isNumeric(txtCosto.getText())) {
            Tools.AlertMessageWarning(apWindow, "Insumo", "Ingrese el costo del insumo, por favor.");
            txtCosto.requestFocus();
        } else {
            short value = Tools.AlertMessageConfirmation(apWindow, "Insumo", "¿Está seguro de continuar?");
            if (value == 1) {
                InsumoTB insumoTB = new InsumoTB();
                insumoTB.setIdInsumo(idInsumo);
                insumoTB.setClave(txtClave.getText().trim());
                insumoTB.setClaveAlterna(txtClaveAlterna.getText() == null ? "" : txtClaveAlterna.getText().trim());
                insumoTB.setNombreMarca(txtDescripcion.getText().trim().toUpperCase());
                insumoTB.setMedida(idMedida != 0
                        ? idMedida
                        : 0);
                insumoTB.setCategoria(idCategoria != 0
                        ? idCategoria
                        : 0);
                insumoTB.setCosto(Double.parseDouble(txtCosto.getText().trim()));
                insumoTB.setStockMinimo(Tools.isNumeric(txtInventarioMinimo.getText())
                        ? Double.parseDouble(txtInventarioMinimo.getText().trim())
                        : 0);
                insumoTB.setStockMaximo(Tools.isNumeric(txtInventarioMaximo.getText())
                        ? Double.parseDouble(txtInventarioMaximo.getText().trim())
                        : 0);
                insumoTB.setIdClase(idClase);
                insumoTB.setIdSubClase(idSubClase);
                insumoTB.setIdSubSubClase(idSubSubClase);

                String result = InsumoADO.CrudInsumo(insumoTB);
                if (result.equalsIgnoreCase("inserted")) {
                    Tools.AlertMessageInformation(apWindow, "Insumo", "Se registró correctamente el insumo.");
                    Tools.Dispose(apWindow);
                } else if (result.equalsIgnoreCase("updated")) {
                    Tools.AlertMessageInformation(apWindow, "Insumo", "Se actualizó correctamente el insumo.");
                    Tools.Dispose(apWindow);
                } else if (result.equalsIgnoreCase("clave")) {
                    Tools.AlertMessageWarning(apWindow, "Insumo", "Ya existe un insumo con la misma clave.");
                } else if (result.equalsIgnoreCase("nombre")) {
                    Tools.AlertMessageWarning(apWindow, "Insumo", "Ya existe un insumo con la misma descripción.");
                } else {
                    Tools.AlertMessageError(apWindow, "Insumo", result);
                }
            }
        }
    }

    private void openWindowClases(String titulo) {
        try {
            URL url = getClass().getResource(FilesRouters.FX_DETALLE_CLASES_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            
            FxDetalleClasesListaController controller = fXMLLoader.getController();
            controller.setControllerInsumoModal(this);
            controller.initListClassDetail(titulo);
            
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar "+titulo+" al nuevo insumo", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onMouseClickedCategoria(MouseEvent event) {
        if (event.getClickCount() == 2) {
            openWindowDetalle("Agregar Categoría", "0006", false);
        }
    }

    @FXML
    private void onKeyReleasedCategoria(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            openWindowDetalle("Agregar Categoría", "0006", false);
        }
    }

    @FXML
    private void onMouseClickedMedida(MouseEvent event) {
        if (event.getClickCount() == 2) {
            openWindowDetalle("Agregar Unidade de Medida", "0013", false);
        }
    }

    @FXML
    private void onKeyReleasedMedida(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            openWindowDetalle("Agregar Unidade de Medida", "0013", false);
        }
    }

    @FXML
    private void onKeyTypedCategoria(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if (c != '\b') {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedMedida(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if (c != '\b') {
            event.consume();
        }
    }

    @FXML
    private void onKeyPressedRegistrar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            validateRegistro();
        }
    }

    @FXML
    private void onActionRegistrar(ActionEvent event) {
        validateRegistro();
    }

    @FXML
    private void onKeyPressedCancelar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(apWindow);
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        Tools.Dispose(apWindow);
    }

    @FXML
    private void onActionAddClase(ActionEvent event) throws IOException {
        try {
            String IdClase = DetalleADO.ListarClases(1);
//            ObjectGlobal.InitializationTransparentBackground(apWindow);
            URL url = getClass().getResource(FilesRouters.FX_DETALLE);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxDetalleController controller = fXMLLoader.getController();
            controller.setAddClase("Clase", IdClase);

            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar clase", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.show();
        } catch (IOException ex) {
            System.out.println("openWindowInsumo():" + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onMouseClickedClase(MouseEvent event) {
        if (event.getClickCount() == 2) {
            openWindowClases("Clase");
        }
    }

    @FXML
    private void onKeyReleasedClase(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            openWindowClases("Clase");
        }
    }

    @FXML
    private void onKeyTypedClase(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if (c != '\b') {
            event.consume();
        }
    }

    @FXML
    private void onKeyPressedClase(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                String IdClase = DetalleADO.ListarClases(1);
//            ObjectGlobal.InitializationTransparentBackground(apWindow);
                URL url = getClass().getResource(FilesRouters.FX_DETALLE);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxDetalleController controller = fXMLLoader.getController();
                controller.setAddClase("Clase", IdClase);

                Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Clase", apWindow.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.show();
            } catch (IOException ex) {
                System.out.println("openWindowInsumo():" + ex.getLocalizedMessage());
            }
        }
    }

    @FXML
    private void onActionAddSubClase(ActionEvent event) {
        try {
            String IdClase = DetalleADO.ListarClases(2);
//            ObjectGlobal.InitializationTransparentBackground(apWindow);
            URL url = getClass().getResource(FilesRouters.FX_DETALLE);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxDetalleController controller = fXMLLoader.getController();
            controller.setAddClase("Sub-Clase", IdClase);

            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Sub-Clase", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.show();
        } catch (IOException ex) {
            System.out.println("openWindowInsumo():" + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onMouseClickedSubClase(MouseEvent event) {
        if (event.getClickCount() == 2) {
            openWindowClases("Sub-Clase");
        }
    }

    @FXML
    private void onKeyReleasedSubClase(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            openWindowClases("Sub-Clase");
        }
    }

    @FXML
    private void onKeyTypedSubClase(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if (c != '\b') {
            event.consume();
        }
    }

    @FXML
    private void onKeyPressedSubClase(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                String IdClase = DetalleADO.ListarClases(2);
//            ObjectGlobal.InitializationTransparentBackground(apWindow);
                URL url = getClass().getResource(FilesRouters.FX_DETALLE);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxDetalleController controller = fXMLLoader.getController();
                controller.setAddClase("Sub-Clase", IdClase);

                Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Sub-Clase", apWindow.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.show();
            } catch (IOException ex) {
                System.out.println("openWindowInsumo():" + ex.getLocalizedMessage());
            }
        }
    }

    @FXML
    private void onActionAddSubSubClase(ActionEvent event) {
        try {
            String IdClase = DetalleADO.ListarClases(3);
//            ObjectGlobal.InitializationTransparentBackground(apWindow);
            URL url = getClass().getResource(FilesRouters.FX_DETALLE);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxDetalleController controller = fXMLLoader.getController();
            controller.setAddClase("Sub-Sub-Clase", IdClase);

            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Sub-Sub-Clase", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.show();
        } catch (IOException ex) {
            System.out.println("openWindowInsumo():" + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onMouseClickedSubSubClase(MouseEvent event) {
        if (event.getClickCount() == 2) {
            openWindowClases("Sub-Sub-Clase");
        }
    }

    @FXML
    private void onKeyReleasedSubSubClase(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            openWindowClases("Sub-Sub-Clase");
        }
    }

    @FXML
    private void onKeyTypedSubSubClase(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if (c != '\b') {
            event.consume();
        }
    }

    @FXML
    private void onKeyPressedSubSubClase(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                String IdClase = DetalleADO.ListarClases(3);
//            ObjectGlobal.InitializationTransparentBackground(apWindow);
                URL url = getClass().getResource(FilesRouters.FX_DETALLE);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxDetalleController controller = fXMLLoader.getController();
                controller.setAddClase("Sub-Sub-Clase", IdClase);

                Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Sub-Sub-Clase", apWindow.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.show();
            } catch (IOException ex) {
                System.out.println("openWindowInsumo():" + ex.getLocalizedMessage());
            }
        }
    }

    public void setIdMedida(int idMedida) {
        this.idMedida = idMedida;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public TextField getTxtCategoria() {
        return txtCategoria;
    }

    public TextField getTxtMedida() {
        return txtMedida;
    }

    public void setIdClase(String idClase) {
        this.idClase = idClase;
    }

    public TextField getTxtClase() {
        return txtClase;
    }

    public void setIdSubClase(String idSubClase) {
        this.idSubClase = idSubClase;
    }

    public TextField getTxtSubClase() {
        return txtSubClase;
    }

    public void setIdSubSubClase(String idSubSubClase) {
        this.idSubSubClase = idSubSubClase;
    }

    public TextField getTxtSubSubClase() {
        return txtSubSubClase;
    }
}
