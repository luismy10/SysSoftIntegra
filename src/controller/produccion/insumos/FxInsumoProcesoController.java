package controller.produccion.insumos;

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

    private String idInsumo;

    private int idMedida;

    private int idCategoria;

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
                txtCosto.setText(""+insumoTB.getCosto());
                txtInventarioMinimo.setText(""+insumoTB.getStockMinimo());
                txtInventarioMaximo.setText(""+insumoTB.getStockMaximo());
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
                insumoTB.setClaveAlterna(txtClaveAlterna.getText().trim());
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

}
