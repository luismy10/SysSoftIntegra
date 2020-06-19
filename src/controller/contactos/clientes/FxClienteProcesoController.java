package controller.contactos.clientes;

import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.ClienteADO;
import model.ClienteTB;
import model.DetalleADO;
import model.DetalleTB;

public class FxClienteProcesoController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private ComboBox<DetalleTB> cbDocumentType;
    @FXML
    private TextField txtDocumentNumber;
    @FXML
    private TextField txtInformacion;
    @FXML
    private Button btnRegister;
    @FXML
    private ComboBox<DetalleTB> cbEstado;
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
        DetalleADO.GetDetailIdName("2", "0001", "").forEach(e -> {
            cbEstado.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        cbEstado.getSelectionModel().select(0);

    }

    public void setValueAdd() {
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
            txtInformacion.setText(clienteTB.getInformacion());

            information = clienteTB.getInformacion();

            ObservableList<DetalleTB> lsest = cbEstado.getItems();
            for (int i = 0; i < lsest.size(); i++) {
                if (clienteTB.getEstado() == lsest.get(i).getIdDetalle().get()) {
                    cbEstado.getSelectionModel().select(i);
                    break;
                }
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

    void aValidityProcess() throws ParseException {
        if (cbDocumentType.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageInformation(window, "Persona", "Seleccione el tipo de documento, por favor.");

            cbDocumentType.requestFocus();
        } else if (txtDocumentNumber.getText().equalsIgnoreCase("")) {
            Tools.AlertMessageWarning(window, "Persona", "Ingrese el documento de identificación, por favor.");

            txtDocumentNumber.requestFocus();
        } else if (txtInformacion.getText().equalsIgnoreCase("")) {
            Tools.AlertMessageWarning(window, "Persona", "Ingrese la información del cliente, por favor.");
            txtInformacion.requestFocus();
        } else if (cbEstado.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(window, "Persona", "Seleccione el estado, por favor.");
            cbEstado.requestFocus();
        } else {

            short confirmation = Tools.AlertMessageConfirmation(window, "Mantenimiento", "¿Esta seguro de continuar?");
            if (confirmation == 1) {

                ClienteTB clienteTB = new ClienteTB();
                clienteTB.setIdCliente(idCliente);
                clienteTB.setTipoDocumento(cbDocumentType.getSelectionModel().getSelectedItem().getIdDetalle().get());
                clienteTB.setInformacion(txtInformacion.getText().trim().toUpperCase());
                clienteTB.setNumeroDocumento(txtDocumentNumber.getText().trim().toUpperCase());
//              clienteTB.setFechaNacimiento(new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(Tools.getDatePicker(dpBirthdate)).getTime()));
                clienteTB.setTelefono(txtTelefono.getText().trim());
                clienteTB.setCelular(txtCelular.getText().trim());
                clienteTB.setEmail(txtEmail.getText().trim());
                clienteTB.setDireccion(txtDireccion.getText().trim().toUpperCase());
                clienteTB.setRepresentante(txtRepresentante.getText().trim().toUpperCase());
                clienteTB.setEstado(cbEstado.getSelectionModel().getSelectedItem().getIdDetalle().get());
                clienteTB.setPredeterminado(false);
                clienteTB.setSistema(false);

                String result = ClienteADO.CrudCliente(clienteTB);
                switch (result) {
                    case "registered":
                        Tools.AlertMessageInformation(window, "Persona", "Registrado correctamente.");
                        Tools.Dispose(window);
                        break;
                    case "updated":
                        Tools.AlertMessageInformation(window, "Persona", "Actualizado correctamente.");
                        Tools.Dispose(window);
                        break;
                    case "duplicate":
                        Tools.AlertMessageWarning(window, "Persona", "No se puede haber 2 personas con el mismo documento de identidad.");
                        txtDocumentNumber.requestFocus();
                        break;
                    default:
                        Tools.AlertMessageError(window, "Persona", result);
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

}
