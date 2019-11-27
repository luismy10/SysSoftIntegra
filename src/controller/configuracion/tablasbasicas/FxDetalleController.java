package controller.configuracion.tablasbasicas;

import com.sun.javafx.scene.control.skin.TextAreaSkin;
import controller.tools.Session;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.DetalleADO;
import model.DetalleTB;

public class FxDetalleController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private TextField txtCode;
    @FXML
    private TextField txtName;
    @FXML
    private Label lblTitle;
    @FXML
    public TextArea txtDescripcion;
    @FXML
    public ComboBox<Estado> cbEstado;
    @FXML
    private Button btnToAction;
    @FXML
    private TextField txtCodigoAuxiliar;
    
    private int idDetalle;

    private FxDetalleMantenimientoController detalleMantenimientoController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
        cbEstado.getItems().add(new Estado("1", "Habilitado"));
        cbEstado.getItems().add(new Estado("0", "Inhabilitado"));
        cbEstado.setConverter(new javafx.util.StringConverter<Estado>() {
            @Override
            public String toString(Estado object) {
                return object.getNombre();
            }

            @Override
            public Estado fromString(String string) {
                return cbEstado.getItems().stream().filter(ap
                        -> ap.getNombre().equals(string)).findFirst().orElse(null);
            }
        });
        cbEstado.setValue(new Estado("1", "Habilitado"));

    }

    public void setValueAdd(String... values) {
        lblTitle.setText("Detalle del mantenimiento del " + values[0].toLowerCase());
        txtCode.setText(values[1]);
        idDetalle = Integer.parseInt(values[2]);
    }

    public void setValueUpdate(String... values) {
        lblTitle.setText("Detalle del mantenimiento del " + values[0].toLowerCase());
        txtCode.setText(values[1]);
        idDetalle = Integer.parseInt(values[2]);
        btnToAction.setText("Actualizar");
        btnToAction.getStyleClass().add("buttonLightWarning");
        txtCodigoAuxiliar.setText(values[3]);
        txtName.setText(values[4]);
        txtDescripcion.setText(values[5]);
        cbEstado.setValue(values[6].equals("1") ? new Estado("1", "Habilitado") : new Estado("0", "Inhabilitado"));
    }

    private void aValidityProcess() {
        if (txtCode.getText().equalsIgnoreCase("")) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Detalle", "Ingrese el código, por favor.", false);
            txtCode.requestFocus();
        } else if (txtName.getText().equalsIgnoreCase("")) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Detalle", "Ingrese el nombre, por favor.", false);
            txtName.requestFocus();
        } else {

            short confirmation = Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Mantenimiento", "¿Esta seguro de continuar?", true);
            if (confirmation == 1) {
                DetalleTB detalleTB = new DetalleTB();
                detalleTB.setIdDetalle(idDetalle);
                detalleTB.setIdMantenimiento(txtCode.getText());
                detalleTB.setIdAuxiliar(txtCodigoAuxiliar.getText().trim());
                detalleTB.setNombre(txtName.getText().trim());
                detalleTB.setDescripcion(txtDescripcion.getText().trim());
                detalleTB.setEstado(cbEstado.getValue().getId());
                detalleTB.setUsuarioRegistro(Session.USER_ID);
                String result = DetalleADO.CrudEntity(detalleTB);
                if (result.equalsIgnoreCase("registered")) {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Detalle", "Registrado correctamente.", false);

                } else if (result.equalsIgnoreCase("updated")) {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Detalle", "Actualizado correctamente.", false);
                    Tools.Dispose(window);
                    detalleMantenimientoController.initDetail(txtCode.getText(),"");
                } else if (result.equalsIgnoreCase("duplicate")) {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Detalle", "No se puede haber 2 detalles con el mismo nombre.", false);
                    txtName.requestFocus();
                } else if (result.equalsIgnoreCase("error")) {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Detalle", "No se puedo completar la ejecución.", false);
                } else {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Detalle", result, false);
                }

            } else {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Detalle", "No hay conexión al servidor.", false);
            }
        }
    }

    @FXML
    private void onKeyPressedToRegister(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            aValidityProcess();
        }
    }

    @FXML
    private void onActionToRegister(ActionEvent event) {
        aValidityProcess();
    }

    @FXML
    private void onPressedToCancel(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(window);
        }
    }

    @FXML
    private void onActionToCancel(ActionEvent event) {
        Tools.Dispose(window);
    }

    @FXML
    public void OnKeyPressedDescripcion(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.TAB) {
            Node node = (Node) keyEvent.getSource();
            if (node instanceof TextArea) {
                TextAreaSkin skin = (TextAreaSkin) ((TextArea) node).getSkin();
                if (keyEvent.isShiftDown()) {
                    skin.getBehavior().traversePrevious();
                } else {
                    skin.getBehavior().traverseNext();
                }
            }
            keyEvent.consume();
        }
    }

    void initConfiguracion(FxDetalleMantenimientoController detalleMantenimientoController) {
        this.detalleMantenimientoController = detalleMantenimientoController;
    }

    public class Estado {

        private String Id;
        private String Nombre;

        public Estado() {

        }

        public Estado(String Id, String Nombre) {
            this.Id = Id;
            this.Nombre = Nombre;
        }

        public String getId() {
            return Id;
        }

        public void setId(String Id) {
            this.Id = Id;
        }

        public String getNombre() {
            return Nombre;
        }

        public void setNombre(String Nombre) {
            this.Nombre = Nombre;
        }

    }
}
