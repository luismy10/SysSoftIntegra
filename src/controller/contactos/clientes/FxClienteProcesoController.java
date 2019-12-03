package controller.contactos.clientes;

import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.CiudadADO;
import model.CiudadTB;
import model.ClienteADO;
import model.ClienteTB;
import model.DetalleADO;
import model.DetalleTB;
import model.DistritoADO;
import model.DistritoTB;
import model.PaisADO;
import model.PaisTB;
import model.ProvinciaADO;
import model.ProvinciaTB;

public class FxClienteProcesoController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private ComboBox<DetalleTB> cbDocumentType;
    @FXML
    private TextField txtDocumentNumber;
    @FXML
    private TextField txtLastName;
    @FXML
    private TextField txtFirstName;
    @FXML
    private ComboBox<DetalleTB> cbSex;
    @FXML
    private DatePicker dpBirthdate;
    @FXML
    private Button btnDirectory;
    @FXML
    private Button btnRegister;
    @FXML
    private Label lblDirectory;
    @FXML
    private ComboBox<DetalleTB> cbEstado;
    @FXML
    private ComboBox<PaisTB> cbPais;
    @FXML
    private ComboBox<CiudadTB> cbDepartamento;
    @FXML
    private ComboBox<ProvinciaTB> cbProvincia;
    @FXML
    private ComboBox<DistritoTB> cbDistrito;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtCelular;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtDireccion;
    @FXML
    private TextField txtRepresentante;

    private String idCliente;

    private String information;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
        idCliente = "";
        DetalleADO.GetDetailId("0003").forEach(e -> {
            cbDocumentType.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        DetalleADO.GetDetailId("0004").forEach(e -> {
            cbSex.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        DetalleADO.GetDetailIdName("2", "0001", "").forEach(e -> {
            cbEstado.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        cbEstado.getSelectionModel().select(0);
        PaisADO.ListPais().forEach(e -> {
            cbPais.getItems().add(new PaisTB(e.getPaisCodigo(), e.getPaisNombre()));
        });

    }

    public void setValueAdd() {
        lblDirectory.setVisible(false);
        btnDirectory.setVisible(false);
        cbDocumentType.requestFocus();
    }

    public void setValueUpdate(String value) {
        btnRegister.setText("Actualizar");
        btnRegister.getStyleClass().add("buttonLightWarning");
        cbDocumentType.requestFocus();
        ClienteTB clienteTB = ClienteADO.GetByIdCliente(value);
        if (clienteTB != null) {
            idCliente = clienteTB.getIdCliente();
            ObservableList<DetalleTB> lstype = cbDocumentType.getItems();
            for (int i = 0; i < lstype.size(); i++) {
                if (clienteTB.getTipoDocumento() == lstype.get(i).getIdDetalle().get()) {
                    cbDocumentType.getSelectionModel().select(i);
                    break;
                }
            }

            txtDocumentNumber.setText(clienteTB.getNumeroDocumento());
            txtDocumentNumber.setDisable(clienteTB.getNumeroDocumento().equals("00000000"));
            txtLastName.setText(clienteTB.getApellidos());
            txtFirstName.setText(clienteTB.getNombres());

            information = clienteTB.getApellidos() + " " + clienteTB.getNombres();

            ObservableList<DetalleTB> lsest = cbEstado.getItems();
            for (int i = 0; i < lsest.size(); i++) {
                if (clienteTB.getEstado() == lsest.get(i).getIdDetalle().get()) {
                    cbEstado.getSelectionModel().select(i);
                    break;
                }
            }
            if (clienteTB.getSexo() != 0) {
                ObservableList<DetalleTB> lssex = cbSex.getItems();
                for (int i = 0; i < lssex.size(); i++) {
                    if (clienteTB.getSexo() == lssex.get(i).getIdDetalle().get()) {
                        cbSex.getSelectionModel().select(i);
                        break;
                    }
                }
            }

            if (clienteTB.getFechaNacimiento() != null) {
                Tools.actualDate(clienteTB.getFechaNacimiento().toString(), dpBirthdate);
            }
            txtTelefono.setText(clienteTB.getTelefono());
            txtCelular.setText(clienteTB.getCelular());
            txtEmail.setText(clienteTB.getEmail());
            txtDireccion.setText(clienteTB.getDireccion());

//            if (clienteTB.getFacturacionTB().getTipoDocumentoFacturacion() != 0) {
//                ObservableList<DetalleTB> lstypefacturacion = cbDocumentTypeFactura.getItems();
//                for (int i = 0; i < lstypefacturacion.size(); i++) {
//                    if (clienteTB.getFacturacionTB().getTipoDocumentoFacturacion() == lstypefacturacion.get(i).getIdDetalle().get()) {
//                        cbDocumentTypeFactura.getSelectionModel().select(i);
//                        break;
//                    }
//                }
//            }
//            txtDocumentNumberFactura.setText(clienteTB.getFacturacionTB().getNumeroDocumentoFacturacion());
//            txtBusinessName.setText(clienteTB.getFacturacionTB().getRazonSocial());
//            txtTradename.setText(clienteTB.getFacturacionTB().getNombreComercial());
//            if (clienteTB.getFacturacionTB().getPais() != null) {
//                ObservableList<PaisTB> lspais = cbPais.getItems();
//                for (int i = 0; i < lspais.size(); i++) {
//                    if (clienteTB.getFacturacionTB().getPais().equals(lspais.get(i).getPaisCodigo())) {
//                        cbPais.getSelectionModel().select(i);
//                        CiudadADO.ListCiudad(cbPais.getSelectionModel().getSelectedItem().getPaisCodigo()).forEach(e -> {
//                            cbDepartamento.getItems().add(new CiudadTB(e.getIdCiudad(), e.getCiudadDistrito()));
//                        });
//                        break;
//                    }
//                }
//            }
//            if (clienteTB.getFacturacionTB().getDepartamento() != 0) {
//                ObservableList<CiudadTB> lsciudad = cbDepartamento.getItems();
//                for (int i = 0; i < lsciudad.size(); i++) {
//                    if (clienteTB.getFacturacionTB().getDepartamento() == lsciudad.get(i).getIdCiudad()) {
//                        cbDepartamento.getSelectionModel().select(i);
//                        ProvinciaADO.ListProvincia(cbDepartamento.getSelectionModel().getSelectedItem().getIdCiudad()).forEach(e -> {
//                            cbProvincia.getItems().add(new ProvinciaTB(e.getIdProvincia(), e.getProvincia()));
//                        });
//                        break;
//                    }
//                }
//            }
//            if (clienteTB.getFacturacionTB().getProvincia() != 0) {
//                ObservableList<ProvinciaTB> lsprovin = cbProvincia.getItems();
//                for (int i = 0; i < lsprovin.size(); i++) {
//                    if (clienteTB.getFacturacionTB().getProvincia() == lsprovin.get(i).getIdProvincia()) {
//                        cbProvincia.getSelectionModel().select(i);
//                        DistritoADO.ListDistrito(cbProvincia.getSelectionModel().getSelectedItem().getIdProvincia()).forEach(e -> {
//                            cbDistrito.getItems().add(new DistritoTB(e.getIdDistrito(), e.getDistrito()));
//                        });
//                        break;
//                    }
//                }
//            }
//            if (clienteTB.getFacturacionTB().getDistrito() != 0) {
//                ObservableList<DistritoTB> lsdistrito = cbDistrito.getItems();
//                for (int i = 0; i < lsdistrito.size(); i++) {
//                    if (clienteTB.getFacturacionTB().getDistrito() == lsdistrito.get(i).getIdDistrito()) {
//                        cbDistrito.getSelectionModel().select(i);
//                        break;
//                    }
//                }
//            }
        }

    }

    private void onViewPerfil() throws IOException {
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
        controller.setLoadView(idCliente, information);
    }

    @FXML
    private void onKeyPressedToDirectory(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            onViewPerfil();
        }
    }

    @FXML
    private void onActionToDirectory(ActionEvent event) throws IOException {
        onViewPerfil();
    }

    void aValidityProcess() throws ParseException {
        if (cbDocumentType.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Persona", "Seleccione el tipo de documento, por favor.", false);

            cbDocumentType.requestFocus();
        } else if (txtDocumentNumber.getText().equalsIgnoreCase("")) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Persona", "Ingrese el documento de identificación, por favor.", false);

            txtDocumentNumber.requestFocus();
        } else if (txtLastName.getText().equalsIgnoreCase("")) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Persona", "Ingrese el apellido paterno, por favor.", false);

            txtLastName.requestFocus();
        } else if (txtFirstName.getText().equalsIgnoreCase("")) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Persona", "Ingrese el primero nombre, por favor.", false);

            txtFirstName.requestFocus();
        } else if (cbEstado.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Persona", "Seleccione el estado, por favor.", false);
            cbEstado.requestFocus();
        } else {

            short confirmation = Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Mantenimiento", "¿Esta seguro de continuar?", true);
            if (confirmation == 1) {

                ClienteTB clienteTB = new ClienteTB();
                clienteTB.setIdCliente(idCliente);
                clienteTB.setTipoDocumento(cbDocumentType.getSelectionModel().getSelectedItem().getIdDetalle().get());
                clienteTB.setApellidos(txtLastName.getText().trim());
                clienteTB.setNombres(txtFirstName.getText().trim());
                clienteTB.setNumeroDocumento(txtDocumentNumber.getText().trim());
                clienteTB.setSexo(cbSex.getSelectionModel().getSelectedIndex() >= 0
                        ? cbSex.getSelectionModel().getSelectedItem().getIdDetalle().get()
                        : 0);
                if (dpBirthdate.getValue() != null) {
                    clienteTB.setFechaNacimiento(new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(Tools.getDatePicker(dpBirthdate)).getTime()));
                } else {
                    clienteTB.setFechaNacimiento(null);
                }
                clienteTB.setTelefono(txtTelefono.getText().trim());
                clienteTB.setCelular(txtCelular.getText().trim());
                clienteTB.setEmail(txtEmail.getText().trim());
                clienteTB.setDireccion(txtDireccion.getText().trim());
                clienteTB.setRepresentante(txtRepresentante.getText().trim());
                clienteTB.setEstado(cbEstado.getSelectionModel().getSelectedItem().getIdDetalle().get());

                String result = ClienteADO.CrudCliente(clienteTB);

                switch (result) {
                    case "registered":
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Persona", "Registrado correctamente.", false);
                        Tools.Dispose(window);
                        break;
                    case "updated":
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Persona", "Actualizado correctamente.", false);
                        Tools.Dispose(window);
                        break;
                    case "duplicate":
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Persona", "No se puede haber 2 personas con el mismo documento de identidad.", false);
                        txtDocumentNumber.requestFocus();
                        break;
                    case "error":
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Persona", "No se puedo completar la ejecución.", false);

                        break;
                    default:
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Persona", result, false);

                        break;
                }

            } 
        }
    }

    @FXML
    private void onKeyPressedToRegister(KeyEvent event) throws ParseException {
        if (event.getCode() == KeyCode.ENTER) {
            aValidityProcess();
        }
    }

    @FXML
    private void onActionToRegister(ActionEvent event) throws ParseException {
        aValidityProcess();
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
            cbDepartamento.getItems().clear();
            CiudadADO.ListCiudad(cbPais.getSelectionModel().getSelectedItem().getPaisCodigo()).forEach(e -> {
                cbDepartamento.getItems().add(new CiudadTB(e.getIdCiudad(), e.getCiudadDistrito()));
            });
        }
    }

    @FXML
    private void onActionDepartamento(ActionEvent event) {
        if (cbDepartamento.getSelectionModel().getSelectedIndex() >= 0) {
            cbProvincia.getItems().clear();
            ProvinciaADO.ListProvincia(cbDepartamento.getSelectionModel().getSelectedItem().getIdCiudad()).forEach(e -> {
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
