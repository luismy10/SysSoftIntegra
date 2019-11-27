package controller.contactos.proveedores;

import controller.contactos.clientes.FxPerfilController;
import controller.tools.FilesRouters;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
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
import model.CiudadADO;
import model.CiudadTB;
import model.DetalleADO;
import model.DetalleTB;
import model.DistritoADO;
import model.DistritoTB;
import model.PaisADO;
import model.PaisTB;
import model.ProveedorADO;
import model.ProveedorTB;
import model.ProvinciaADO;
import model.ProvinciaTB;

public class FxProveedorProcesoController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private ComboBox<PaisTB> cbPais;
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
    private ComboBox<CiudadTB> cbCiudad;
    @FXML
    private ComboBox<ProvinciaTB> cbProvincia;
    @FXML
    private ComboBox<DistritoTB> cbDistrito;
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
    @FXML
    private Tab tab3;

    private String idProveedor;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
        idProveedor = "";
        DetalleADO.GetDetailIdName("2", "0003", "").forEach(e -> {
            cbDocumentTypeFactura.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        PaisADO.ListPais().forEach(e -> {
            cbPais.getItems().add(new PaisTB(e.getPaisCodigo(), e.getPaisNombre()));
        });
        DetalleADO.GetDetailIdName("2", "0005", "").forEach(e -> {
            cbAmbito.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        DetalleADO.GetDetailIdName("2", "0001", "").forEach(e -> {
            cbEstado.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        cbEstado.getSelectionModel().select(0);

        tab2.setText("");
        tab3.setText("");

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
            idProveedor = proveedorTB.getIdProveedor().get();
            ObservableList<DetalleTB> lstypefa = cbDocumentTypeFactura.getItems();
            for (int i = 0; i < lstypefa.size(); i++) {
                if (proveedorTB.getTipoDocumento() == lstypefa.get(i).getIdDetalle().get()) {
                    cbDocumentTypeFactura.getSelectionModel().select(i);
                    break;
                }
            }
            txtBusinessName.setText(proveedorTB.getRazonSocial().get());
            txtTradename.setText(proveedorTB.getNombreComercial().get());

            if (proveedorTB.getPais() != null || !proveedorTB.getPais().equals("")) {
                ObservableList<PaisTB> lspais = cbPais.getItems();
                for (int i = 0; i < lspais.size(); i++) {
                    if (proveedorTB.getPais().equals(lspais.get(i).getPaisCodigo())) {
                        cbPais.getSelectionModel().select(i);
                        CiudadADO.ListCiudad(cbPais.getSelectionModel().getSelectedItem().getPaisCodigo()).forEach(e -> {
                            cbCiudad.getItems().add(new CiudadTB(e.getIdCiudad(), e.getCiudadDistrito()));
                        });
                        break;
                    }
                }
            }

            if (proveedorTB.getCiudad() != 0) {
                ObservableList<CiudadTB> lsciudad = cbCiudad.getItems();
                for (int i = 0; i < lsciudad.size(); i++) {
                    if (proveedorTB.getCiudad() == lsciudad.get(i).getIdCiudad()) {
                        cbCiudad.getSelectionModel().select(i);
                        break;
                    }
                }
            }

            if (proveedorTB.getProvincia() != 0) {
                ObservableList<ProvinciaTB> lsprovin = cbProvincia.getItems();
                for (int i = 0; i < lsprovin.size(); i++) {
                    if (proveedorTB.getProvincia() == lsprovin.get(i).getIdProvincia()) {
                        cbProvincia.getSelectionModel().select(i);
                        DistritoADO.ListDistrito(cbProvincia.getSelectionModel().getSelectedItem().getIdProvincia()).forEach(e -> {
                            cbDistrito.getItems().add(new DistritoTB(e.getIdDistrito(), e.getDistrito()));
                        });
                        break;
                    }
                }
            }

            if (proveedorTB.getDistrito() != 0) {
                ObservableList<DistritoTB> lsdistrito = cbDistrito.getItems();
                for (int i = 0; i < lsdistrito.size(); i++) {
                    if (proveedorTB.getDistrito() == lsdistrito.get(i).getIdDistrito()) {
                        cbDistrito.getSelectionModel().select(i);
                        break;
                    }
                }
            }

            if (proveedorTB.getAmbito() != 0) {
                ObservableList<DetalleTB> lstamb = cbAmbito.getItems();
                for (int i = 0; i < lstamb.size(); i++) {
                    if (proveedorTB.getAmbito() == lstamb.get(i).getIdDetalle().get()) {
                        cbAmbito.getSelectionModel().select(i);
                        break;
                    }
                }
            }

            ObservableList<DetalleTB> lstest = cbEstado.getItems();
            for (int i = 0; i < lstest.size(); i++) {
                if (proveedorTB.getEstado() == lstest.get(i).getIdDetalle().get()) {
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
                proveedorTB.setTipoDocumento(cbDocumentTypeFactura.getSelectionModel().getSelectedItem().getIdDetalle().get());
                proveedorTB.setNumeroDocumento(txtDocumentNumberFactura.getText().trim());
                proveedorTB.setRazonSocial(txtBusinessName.getText().trim());
                proveedorTB.setNombreComercial(txtTradename.getText().trim());
                proveedorTB.setPais(cbPais.getSelectionModel().getSelectedIndex() >= 0
                        ? cbPais.getSelectionModel().getSelectedItem().getPaisCodigo()
                        : "");
                proveedorTB.setCiudad(cbCiudad.getSelectionModel().getSelectedIndex() >= 0
                        ? cbCiudad.getSelectionModel().getSelectedItem().getIdCiudad()
                        : 0);
                proveedorTB.setProvincia(cbProvincia.getSelectionModel().getSelectedIndex() >= 0
                        ? cbProvincia.getSelectionModel().getSelectedItem().getIdProvincia() : 0);
                proveedorTB.setDistrito(cbDistrito.getSelectionModel().getSelectedIndex() >= 0
                        ? cbDistrito.getSelectionModel().getSelectedItem().getIdDistrito() : 0);

                proveedorTB.setAmbito(cbAmbito.getSelectionModel().getSelectedIndex() >= 0
                        ? cbAmbito.getSelectionModel().getSelectedItem().getIdDetalle().get()
                        : 0);
                proveedorTB.setEstado(cbEstado.getSelectionModel().getSelectedItem().getIdDetalle().get());
                proveedorTB.setTelefono(txtTelefono.getText().trim());
                proveedorTB.setCelular(txtCelular.getText().trim());
                proveedorTB.setEmail(txtEmail.getText().trim());
                proveedorTB.setPaginaWeb(txtPaginaWeb.getText().trim());
                proveedorTB.setDireccion(txtDireccion.getText().trim());
                proveedorTB.setUsuarioRegistro(Session.USER_ID);
                proveedorTB.setFechaRegistro(LocalDateTime.now());
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

    @FXML
    private void onActionPais(ActionEvent event) {
        if (cbPais.getSelectionModel().getSelectedIndex() >= 0) {
            cbCiudad.getItems().clear();
            CiudadADO.ListCiudad(cbPais.getSelectionModel().getSelectedItem().getPaisCodigo()).forEach(e -> {
                cbCiudad.getItems().add(new CiudadTB(e.getIdCiudad(), e.getCiudadDistrito()));
            });
        }
    }

    @FXML
    private void onActionCiudad(ActionEvent event) {
        if (cbCiudad.getSelectionModel().getSelectedIndex() >= 0) {
            cbProvincia.getItems().clear();
            ProvinciaADO.ListProvincia(cbCiudad.getSelectionModel().getSelectedItem().getIdCiudad()).forEach(e -> {
                cbProvincia.getItems().add(new ProvinciaTB(e.getIdProvincia(), e.getProvincia()));
            });
        }
    }

    @FXML
    private void onActionProvincia(ActionEvent event) {
        if (cbProvincia.getSelectionModel().getSelectedIndex() >= 0) {
            cbDistrito.getItems().clear();
            DistritoADO.ListDistrito(cbProvincia.getSelectionModel().getSelectedItem().getIdProvincia()).forEach(e -> {
                cbDistrito.getItems().add(new DistritoTB(e.getIdDistrito(), e.getDistrito()));
            });
        }
    }

}
