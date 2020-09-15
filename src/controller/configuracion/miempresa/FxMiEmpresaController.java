package controller.configuracion.miempresa;

import controller.tools.Session;
import controller.tools.Tools;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import model.CiudadADO;
import model.CiudadTB;
import model.DetalleADO;
import model.DetalleTB;
import model.DistritoADO;
import model.DistritoTB;
import model.EmpresaADO;
import model.EmpresaTB;
import model.PaisADO;
import model.PaisTB;
import model.ProvinciaADO;
import model.ProvinciaTB;

public class FxMiEmpresaController implements Initializable {

    @FXML
    private ScrollPane window;
    @FXML
    private ComboBox<DetalleTB> cbGiroComercial;
    @FXML
    private TextField txtRepresentante;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtCelular;
    @FXML
    private TextField txtPaginasWeb;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtDomicilio;
    @FXML
    private ComboBox<DetalleTB> cbTipoDocumento;
    @FXML
    private TextField txtNumeroDocumento;
    @FXML
    private TextField txtRazonSocial;
    @FXML
    private TextField txtNombreComercial;
    @FXML
    private ComboBox<PaisTB> cbPais;
    @FXML
    private ComboBox<CiudadTB> cbCiudad;
    @FXML
    private ComboBox<ProvinciaTB> cbProvincia;
    @FXML
    private ComboBox<DistritoTB> cbCiudadDistrito;
    @FXML
    private ImageView lnPrincipal;

    private AnchorPane vbPrincipal;

    private boolean validate;

    private byte[] imageBytes;

    private int idEmpresa;

    private File selectFile;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DetalleADO.GetDetailIdName("3", "0011", "").forEach(e -> {
            cbGiroComercial.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        PaisADO.ListPais().forEach(e -> {
            cbPais.getItems().add(new PaisTB(e.getPaisCodigo(), e.getPaisNombre()));
        });
        DetalleADO.GetDetailIdName("3", "0003", "").forEach(e -> {
            cbTipoDocumento.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        EmpresaTB empresaTb = EmpresaADO.GetEmpresa();
        if (empresaTb != null) {
            validate = true;
            idEmpresa = empresaTb.getIdEmpresa();

            ObservableList<DetalleTB> lsgiro = cbGiroComercial.getItems();
            if (empresaTb.getGiroComerial() != 0) {
                for (int i = 0; i < lsgiro.size(); i++) {
                    if (empresaTb.getGiroComerial() == lsgiro.get(i).getIdDetalle().get()) {
                        cbGiroComercial.getSelectionModel().select(i);
                        break;
                    }
                }
            }

            txtRepresentante.setText(empresaTb.getNombre());
            txtTelefono.setText(empresaTb.getTelefono());
            txtCelular.setText(empresaTb.getCelular());
            txtPaginasWeb.setText(empresaTb.getPaginaWeb());
            txtEmail.setText(empresaTb.getEmail());
            txtDomicilio.setText(empresaTb.getDomicilio());

            ObservableList<DetalleTB> lsdoc = cbTipoDocumento.getItems();
            if (empresaTb.getTipoDocumento() != 0) {
                for (int i = 0; i < lsdoc.size(); i++) {
                    if (empresaTb.getTipoDocumento() == lsdoc.get(i).getIdDetalle().get()) {
                        cbTipoDocumento.getSelectionModel().select(i);
                        break;
                    }
                }
            }

            txtNumeroDocumento.setText(empresaTb.getNumeroDocumento());
            txtRazonSocial.setText(empresaTb.getRazonSocial());
            txtNombreComercial.setText(empresaTb.getNombreComercial());

            ObservableList<PaisTB> lspais = cbPais.getItems();
            if (empresaTb.getPais() != null || !empresaTb.getPais().equals("")) {
                for (int i = 0; i < lspais.size(); i++) {
                    if (empresaTb.getPais().equals(lspais.get(i).getPaisCodigo())) {
                        cbPais.getSelectionModel().select(i);
                        CiudadADO.ListCiudad(cbPais.getSelectionModel().getSelectedItem().getPaisCodigo()).forEach(e -> {
                            cbCiudad.getItems().add(new CiudadTB(e.getIdCiudad(), e.getCiudadDistrito()));
                        });
                        break;
                    }
                }
            }

            ObservableList<CiudadTB> lsciudad = cbCiudad.getItems();
            if (empresaTb.getCiudad() != 0) {
                for (int i = 0; i < lsciudad.size(); i++) {
                    if (empresaTb.getCiudad() == lsciudad.get(i).getIdCiudad()) {
                        cbCiudad.getSelectionModel().select(i);
                        ProvinciaADO.ListProvincia(cbCiudad.getSelectionModel().getSelectedItem().getIdCiudad()).forEach(e -> {
                            cbProvincia.getItems().add(new ProvinciaTB(e.getIdProvincia(), e.getProvincia()));
                        });
                        break;
                    }
                }
            }

            ObservableList<ProvinciaTB> lsprovin = cbProvincia.getItems();
            if (empresaTb.getProvincia() != 0) {
                for (int i = 0; i < lsprovin.size(); i++) {
                    if (empresaTb.getProvincia() == lsprovin.get(i).getIdProvincia()) {
                        cbProvincia.getSelectionModel().select(i);
                        DistritoADO.ListDistrito(cbProvincia.getSelectionModel().getSelectedItem().getIdProvincia()).forEach(e -> {
                            cbCiudadDistrito.getItems().add(new DistritoTB(e.getIdDistrito(), e.getDistrito()));
                        });
                        break;
                    }
                }
            }

            ObservableList<DistritoTB> lsdistrito = cbCiudadDistrito.getItems();
            if (empresaTb.getDistrito() != 0) {
                for (int i = 0; i < lsdistrito.size(); i++) {
                    if (empresaTb.getDistrito() == lsdistrito.get(i).getIdDistrito()) {
                        cbCiudadDistrito.getSelectionModel().select(i);
                        break;
                    }
                }
            }

            if (empresaTb.getImage() != null) {
                imageBytes = empresaTb.getImage();
                lnPrincipal.setImage(new Image(new ByteArrayInputStream(empresaTb.getImage())));
            } else {
                imageBytes = null;
                lnPrincipal.setImage(new Image("/view/image/no-image.png"));
            }

        } else {
            validate = false;
        }

    }

    private void openWindowFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar una imagen");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Elija una imagen", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        selectFile = fileChooser.showOpenDialog(window.getScene().getWindow());
        if (selectFile != null) {
            selectFile = new File(selectFile.getAbsolutePath());
            if (selectFile.getName().endsWith("png") || selectFile.getName().endsWith("jpg") || selectFile.getName().endsWith("jpeg") || selectFile.getName().endsWith("gif")) {
                Image image = new Image(selectFile.toURI().toString(), 200, 200, false, true);
                lnPrincipal.setSmooth(true);
                lnPrincipal.setPreserveRatio(false);
                lnPrincipal.setImage(image);
                imageBytes = null;
            } else {
                Tools.AlertMessageWarning(window, "Mi Empresa", "No seleccionó un formato correcto de imagen.");
            }
        }
    }

    private void clearImage() {
        lnPrincipal.setImage(new Image("/view/image/no-image.png"));
        selectFile = null;
        imageBytes = null;
    }

    private void aValidityProcess() {
        if (cbGiroComercial.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(window, "Mi Empresa", "Seleccione el giro comercial, por favor.");
            cbGiroComercial.requestFocus();
        } else if (txtRepresentante.getText().equalsIgnoreCase("")) {
            Tools.AlertMessageWarning(window, "Mi Empresa", "Ingrese el nombre del representante, por favor.");
            txtRepresentante.requestFocus();
        } else if (txtDomicilio.getText().isEmpty()) {
            Tools.AlertMessageWarning(window, "Mi Empresa", "Ingrese la dirección fiscal de la empresa, por favor.");
            txtDomicilio.requestFocus();
        } else if (cbTipoDocumento.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(window, "Mi Empresa", "Seleccione el tipo de documento, por favor.");
            cbTipoDocumento.requestFocus();
        } else if (txtNumeroDocumento.getText().isEmpty()) {
            Tools.AlertMessageWarning(window, "Mi Empresa", "Ingrese el número del documento, por favor.");
            txtNumeroDocumento.requestFocus();
        } else if (txtRazonSocial.getText().isEmpty()) {
            Tools.AlertMessageWarning(window, "Mi Empresa", "Ingrese la razón social, por favor.");
            txtRazonSocial.requestFocus();
        } else {
            short confirmation = Tools.AlertMessageConfirmation(window, "Mi Empresa", "¿Esta seguro de continuar?");
            if (confirmation == 1) {
                EmpresaTB empresaTB = new EmpresaTB();
                empresaTB.setIdEmpresa(validate == true ? idEmpresa : 0);
                empresaTB.setGiroComerial(cbGiroComercial.getSelectionModel().getSelectedItem().getIdDetalle().get());
                empresaTB.setNombre(txtRepresentante.getText().trim());
                empresaTB.setTelefono(txtTelefono.getText().trim().isEmpty() ? "0000000" : txtTelefono.getText().trim());
                empresaTB.setCelular(txtCelular.getText().trim().isEmpty() ? "000000000" : txtCelular.getText().trim());
                empresaTB.setPaginaWeb(txtPaginasWeb.getText().trim());
                empresaTB.setEmail(txtEmail.getText().trim());
                empresaTB.setDomicilio(txtDomicilio.getText().trim());
                empresaTB.setTipoDocumento(cbTipoDocumento.getSelectionModel().getSelectedIndex() >= 0
                        ? cbTipoDocumento.getSelectionModel().getSelectedItem().getIdDetalle().get()
                        : 0);
                empresaTB.setNumeroDocumento(txtNumeroDocumento.getText().trim().isEmpty() ? "000000000000" : txtNumeroDocumento.getText().trim());
                empresaTB.setRazonSocial(txtRazonSocial.getText().trim().isEmpty() ? txtRepresentante.getText().trim() : txtRazonSocial.getText().trim());
                empresaTB.setNombreComercial(txtNombreComercial.getText().trim());
                empresaTB.setPais(cbPais.getSelectionModel().getSelectedIndex() >= 0
                        ? cbPais.getSelectionModel().getSelectedItem().getPaisCodigo()
                        : "");
                empresaTB.setCiudad(cbCiudad.getSelectionModel().getSelectedIndex() >= 0
                        ? cbCiudad.getSelectionModel().getSelectedItem().getIdCiudad()
                        : 0);
                empresaTB.setProvincia(cbProvincia.getSelectionModel().getSelectedIndex() >= 0
                        ? cbProvincia.getSelectionModel().getSelectedItem().getIdProvincia() : 0);
                empresaTB.setDistrito(cbCiudadDistrito.getSelectionModel().getSelectedIndex() >= 0
                        ? cbCiudadDistrito.getSelectionModel().getSelectedItem().getIdDistrito() : 0);

                empresaTB.setImage(
                        imageBytes != null ? imageBytes
                                : selectFile == null
                                        ? null
                                        : Tools.getImageBytes(selectFile)
                );

                String result = EmpresaADO.CrudEntity(empresaTB);
                switch (result) {
                    case "registered":
                        Tools.AlertMessageInformation(window, "Mi Empresa", "Registrado correctamente.");
                        Session.COMPANY_REPRESENTANTE = txtRepresentante.getText();
                        Session.COMPANY_RAZON_SOCIAL = txtRazonSocial.getText();
                        Session.COMPANY_NOMBRE_COMERCIAL = txtNombreComercial.getText();
                        Session.COMPANY_NUMERO_DOCUMENTO = txtNumeroDocumento.getText();
                        Session.COMPANY_TELEFONO = txtTelefono.getText();
                        Session.COMPANY_CELULAR = txtCelular.getText();
                        Session.COMPANY_PAGINAWEB = txtPaginasWeb.getText();
                        Session.COMPANY_EMAIL = txtEmail.getText();
                        Session.COMPANY_DOMICILIO = txtDomicilio.getText();
                        Session.COMPANY_IMAGE = imageBytes != null ? imageBytes : selectFile == null
                                ? null
                                : Tools.getImageBytes(selectFile);
                        break;
                    case "updated":
                        Tools.AlertMessageInformation(window, "Mi Empresa", "Actualizado correctamente.");
                        Session.COMPANY_REPRESENTANTE = txtRepresentante.getText();
                        Session.COMPANY_RAZON_SOCIAL = txtRazonSocial.getText();
                        Session.COMPANY_NOMBRE_COMERCIAL = txtNombreComercial.getText();
                        Session.COMPANY_NUMERO_DOCUMENTO = txtNumeroDocumento.getText();
                        Session.COMPANY_TELEFONO = txtTelefono.getText();
                        Session.COMPANY_CELULAR = txtCelular.getText();
                        Session.COMPANY_PAGINAWEB = txtPaginasWeb.getText();
                        Session.COMPANY_EMAIL = txtEmail.getText();
                        Session.COMPANY_DOMICILIO = txtDomicilio.getText();
                        Session.COMPANY_IMAGE = imageBytes != null ? imageBytes : selectFile == null
                                ? null
                                : Tools.getImageBytes(selectFile);
                        break;
                    default:
                        Tools.AlertMessageError(window, "Mi Empresa", result);
                        break;
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
    private void onActionPais(ActionEvent event) {
        if (cbPais.getSelectionModel().getSelectedIndex() >= 0) {
            cbCiudad.getItems().clear();
            CiudadADO.ListCiudad(cbPais.getSelectionModel().getSelectedItem().getPaisCodigo()).forEach(e -> {
                cbCiudad.getItems().add(new CiudadTB(e.getIdCiudad(), e.getCiudadDistrito()));
            });
        }
    }

    @FXML
    private void onActionDepartamento(ActionEvent event) {
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
            cbCiudadDistrito.getItems().clear();
            DistritoADO.ListDistrito(cbProvincia.getSelectionModel().getSelectedItem().getIdProvincia()).forEach(e -> {
                cbCiudadDistrito.getItems().add(new DistritoTB(e.getIdDistrito(), e.getDistrito()));
            });
        }
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

    @FXML
    private void onKeyPressedPhoto(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowFile();
        }
    }

    @FXML
    private void onActionPhoto(ActionEvent event) {
        openWindowFile();

    }

    @FXML
    private void onKeyPressedRemovePhoto(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            clearImage();
        }
    }

    @FXML
    private void onActionRemovePhoto(ActionEvent event) {
        clearImage();
    }

}
