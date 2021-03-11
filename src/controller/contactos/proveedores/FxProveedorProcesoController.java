package controller.contactos.proveedores;

import controller.contactos.clientes.FxPerfilController;
import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.DetalleADO;
import model.DetalleTB;
import model.ProveedorADO;
import model.ProveedorTB;

public class FxProveedorProcesoController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private Label lblDirectory;
    @FXML
    private Button btnDirectory;
    @FXML
    private ComboBox<DetalleTB> cbDocumentTypeFactura;
    @FXML
    private Button btnRegister;
    @FXML
    private TextField txtBusinessName;
    @FXML
    private TextField txtTradename;
    @FXML
    private TextField txtDocumentNumberFactura;
    @FXML
    private ComboBox<DetalleTB> cbAmbito;
    @FXML
    private ComboBox<DetalleTB> cbEstado;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtCelular;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPaginaWeb;
    @FXML
    private TextField txtDireccion;
    @FXML
    private TextField txtRepresentante;
    @FXML
    private Tab tab1;
    @FXML
    private Tab tab2;

    private String idProveedor;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
        idProveedor = "";
        DetalleADO.GetDetailIdName("2", "0003", "").forEach(e -> {
            cbDocumentTypeFactura.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        DetalleADO.GetDetailIdName("2", "0005", "").forEach(e -> {
            cbAmbito.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        DetalleADO.GetDetailIdName("2", "0001", "").forEach(e -> {
            cbEstado.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        cbEstado.getSelectionModel().select(0);

        tab2.setText("");

    }

    public void setValueAdd(String... value) {
        lblDirectory.setVisible(false);
        btnDirectory.setVisible(false);
        cbDocumentTypeFactura.requestFocus();
    }

    public void setValueUpdate(String... value) {
        btnRegister.setText("Actualizar");
        btnRegister.getStyleClass().add("buttonLightWarning");
        cbDocumentTypeFactura.requestFocus();
        txtDocumentNumberFactura.setText(value[0]);
        ProveedorTB proveedorTB = ProveedorADO.GetIdLisProveedor(value[0]);
        if (proveedorTB != null) {
            idProveedor = proveedorTB.getIdProveedor();
            ObservableList<DetalleTB> lstypefa = cbDocumentTypeFactura.getItems();
            for (int i = 0; i < lstypefa.size(); i++) {
                if (proveedorTB.getTipoDocumento() == lstypefa.get(i).getIdDetalle()) {
                    cbDocumentTypeFactura.getSelectionModel().select(i);
                    break;
                }
            }
            txtBusinessName.setText(proveedorTB.getRazonSocial());
            txtTradename.setText(proveedorTB.getNombreComercial());

            if (proveedorTB.getAmbito() != 0) {
                ObservableList<DetalleTB> lstamb = cbAmbito.getItems();
                for (int i = 0; i < lstamb.size(); i++) {
                    if (proveedorTB.getAmbito() == lstamb.get(i).getIdDetalle()) {
                        cbAmbito.getSelectionModel().select(i);
                        break;
                    }
                }
            }

            ObservableList<DetalleTB> lstest = cbEstado.getItems();
            for (int i = 0; i < lstest.size(); i++) {
                if (proveedorTB.getEstado() == lstest.get(i).getIdDetalle()) {
                    cbEstado.getSelectionModel().select(i);
                    break;
                }
            }

            txtTelefono.setText(proveedorTB.getTelefono());
            txtCelular.setText(proveedorTB.getCelular());
            txtEmail.setText(proveedorTB.getEmail());
            txtPaginaWeb.setText(proveedorTB.getPaginaWeb());
            txtDireccion.setText(proveedorTB.getDireccion());
            txtRepresentante.setText(proveedorTB.getRepresentante());

        }
    }

    private void toCrudProvider() {
        if (cbDocumentTypeFactura.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Proveedor", "Seleccione el tipo de documento, por favor.", false);

            cbDocumentTypeFactura.requestFocus();
        } else if (txtDocumentNumberFactura.getText().equalsIgnoreCase("")) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Proveedor", "Ingrese el número del documento, por favor.", false);

            txtDocumentNumberFactura.requestFocus();
        } else if (txtBusinessName.getText().equalsIgnoreCase("")) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Proveedor", "Ingrese la razón social o datos correspondientes, por favor.", false);

            txtBusinessName.requestFocus();
        } else if (cbEstado.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Proveedor", "Seleccione el estado, por favor.", false);

            cbEstado.requestFocus();
        } else {
            short confirmation = Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Proveedor", "¿Esta seguro de continuar?", true);
            if (confirmation == 1) {
                ProveedorTB proveedorTB = new ProveedorTB();
                proveedorTB.setIdProveedor(idProveedor);
                proveedorTB.setTipoDocumento(cbDocumentTypeFactura.getSelectionModel().getSelectedItem().getIdDetalle());
                proveedorTB.setNumeroDocumento(txtDocumentNumberFactura.getText().trim());
                proveedorTB.setRazonSocial(txtBusinessName.getText().trim());
                proveedorTB.setNombreComercial(txtTradename.getText().trim());
                proveedorTB.setAmbito(cbAmbito.getSelectionModel().getSelectedIndex() >= 0
                        ? cbAmbito.getSelectionModel().getSelectedItem().getIdDetalle()
                        : 0);
                proveedorTB.setEstado(cbEstado.getSelectionModel().getSelectedItem().getIdDetalle());
                proveedorTB.setTelefono(txtTelefono.getText().trim());
                proveedorTB.setCelular(txtCelular.getText().trim());
                proveedorTB.setEmail(txtEmail.getText().trim());
                proveedorTB.setPaginaWeb(txtPaginaWeb.getText().trim());
                proveedorTB.setDireccion(txtDireccion.getText().trim());
//                proveedorTB.setUsuarioRegistro(Session.USER_ID);
//                proveedorTB.setFechaRegistro(LocalDateTime.now());
                proveedorTB.setRepresentante(txtRepresentante.getText().trim());

                String result = ProveedorADO.CrudEntity(proveedorTB);
                switch (result) {
                    case "registered":
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Proveedor", "Registrado correctamente.", false);
                        Tools.Dispose(window);
                        break;
                    case "updated":
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Proveedor", "Actualizado correctamente.", false);
                        Tools.Dispose(window);
                        break;
                    case "duplicate":
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Proveedor", "No se puede haber 2 proveedores con el mismo número de documento.", false);
                        txtDocumentNumberFactura.requestFocus();
                        break;
                    default:
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Proveedor", result, false);
                        break;
                }

            }
        }
    }

    private void onViewPerfil(String id, String value) throws IOException {
        URL url = getClass().getResource(FilesRouters.FX_PERFIL);
        FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
        Parent parent = fXMLLoader.load(url.openStream());
        //Controlller here
        FxPerfilController controller = fXMLLoader.getController();
        //
        Stage stage = WindowStage.StageLoaderModal(parent, "Perfil", window.getScene().getWindow());
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
        controller.setLoadView(id, value);
    }

    @FXML
    private void onActionToRegister(ActionEvent event) {
        toCrudProvider();
    }

    @FXML
    private void onKeyPressedToRegister(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            toCrudProvider();
        }
    }

    @FXML
    private void onKeyPressedToDirectory(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            onViewPerfil(idProveedor, txtBusinessName.getText());
        }
    }

    @FXML
    private void onActionToDirectory(ActionEvent event) throws IOException {
        onViewPerfil(idProveedor, txtBusinessName.getText());
    }

    @FXML
    private void onKeyPressedToCancel(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(window);
        }
    }

    @FXML
    private void onActionToCancel(ActionEvent event) {
        Tools.Dispose(window);
    }

}
