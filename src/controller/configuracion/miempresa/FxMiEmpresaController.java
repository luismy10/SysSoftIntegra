package controller.configuracion.miempresa;

import controller.tools.Session;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
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

    private AnchorPane vbPrincipal;

    private boolean validate;

    private int idEmpresa;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DetalleADO.GetDetailIdName("3", "0011", "").forEach(e -> {
            cbGiroComercial.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        PaisADO.ListPais().forEach(e -> {
            cbPais.getItems().add(new PaisTB(e.getPaisCodigo(), e.getPaisNombre()));
        });
        DetalleADO.GetDetailIdName("0", "0003", "RUC").forEach(e -> {
            cbTipoDocumento.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        EmpresaTB list = EmpresaADO.GetEmpresa();
        if (list != null) {
            validate = true;
            idEmpresa = list.getIdEmpresa();

            ObservableList<DetalleTB> lsgiro = cbGiroComercial.getItems();
            if (list.getGiroComerial() != 0) {
                for (int i = 0; i < lsgiro.size(); i++) {
                    if (list.getGiroComerial() == lsgiro.get(i).getIdDetalle().get()) {
                        cbGiroComercial.getSelectionModel().select(i);
                        break;
                    }
                }
            }

            txtRepresentante.setText(list.getNombre());
            txtTelefono.setText(list.getTelefono());
            txtCelular.setText(list.getCelular());
            txtPaginasWeb.setText(list.getPaginaWeb());
            txtEmail.setText(list.getEmail());
            txtDomicilio.setText(list.getDomicilio());

            ObservableList<DetalleTB> lsdoc = cbTipoDocumento.getItems();
            if (list.getTipoDocumento() != 0) {
                for (int i = 0; i < lsdoc.size(); i++) {
                    if (list.getTipoDocumento() == lsdoc.get(i).getIdDetalle().get()) {
                        cbTipoDocumento.getSelectionModel().select(i);
                        break;
                    }
                }
            }

            txtNumeroDocumento.setText(list.getNumeroDocumento());
            txtRazonSocial.setText(list.getRazonSocial());
            txtNombreComercial.setText(list.getNombreComercial());

            ObservableList<PaisTB> lspais = cbPais.getItems();
            if (list.getPais() != null || !list.getPais().equals("")) {
                for (int i = 0; i < lspais.size(); i++) {
                    if (list.getPais().equals(lspais.get(i).getPaisCodigo())) {
                        cbPais.getSelectionModel().select(i);
                        CiudadADO.ListCiudad(cbPais.getSelectionModel().getSelectedItem().getPaisCodigo()).forEach(e -> {
                            cbCiudad.getItems().add(new CiudadTB(e.getIdCiudad(), e.getCiudadDistrito()));
                        });
                        break;
                    }
                }
            }

            ObservableList<CiudadTB> lsciudad = cbCiudad.getItems();
            if (list.getCiudad() != 0) {
                for (int i = 0; i < lsciudad.size(); i++) {
                    if (list.getCiudad() == lsciudad.get(i).getIdCiudad()) {
                        cbCiudad.getSelectionModel().select(i);
                        ProvinciaADO.ListProvincia(cbCiudad.getSelectionModel().getSelectedItem().getIdCiudad()).forEach(e -> {
                            cbProvincia.getItems().add(new ProvinciaTB(e.getIdProvincia(), e.getProvincia()));
                        });
                        break;
                    }
                }
            }

            ObservableList<ProvinciaTB> lsprovin = cbProvincia.getItems();
            if (list.getProvincia() != 0) {
                for (int i = 0; i < lsprovin.size(); i++) {
                    if (list.getProvincia() == lsprovin.get(i).getIdProvincia()) {
                        cbProvincia.getSelectionModel().select(i);
                        DistritoADO.ListDistrito(cbProvincia.getSelectionModel().getSelectedItem().getIdProvincia()).forEach(e -> {
                            cbCiudadDistrito.getItems().add(new DistritoTB(e.getIdDistrito(), e.getDistrito()));
                        });
                        break;
                    }
                }
            }

            ObservableList<DistritoTB> lsdistrito = cbCiudadDistrito.getItems();
            if (list.getDistrito() != 0) {
                for (int i = 0; i < lsdistrito.size(); i++) {
                    if (list.getDistrito() == lsdistrito.get(i).getIdDistrito()) {
                        cbCiudadDistrito.getSelectionModel().select(i);
                        break;
                    }
                }
            }

        } else {
            validate = false;
        }

    }

    private void aValidityProcess() {
        if (cbGiroComercial.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Mi Empresa", "Seleccione el giro comercial, por favor.", false);
            cbGiroComercial.requestFocus();
        } else if (txtRepresentante.getText().equalsIgnoreCase("")) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Mi Empresa", "Ingrese el nombre del representante, por favor.", false);
            txtRepresentante.requestFocus();
        } else if (txtDomicilio.getText().isEmpty()) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Mi Empresa", "Ingrese la dirección fiscal de la empresa, por favor.", false);
            txtDomicilio.requestFocus();
        } else if (cbTipoDocumento.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Mi Empresa", "Seleccione el tipo de documento, por favor.", false);
            cbTipoDocumento.requestFocus();
        } else if (txtNumeroDocumento.getText().isEmpty()) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Mi Empresa", "Ingrese el número del documento, por favor.", false);
            txtNumeroDocumento.requestFocus();
        } else if (txtRazonSocial.getText().isEmpty()) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Mi Empresa", "Ingrese la razón social, por favor.", false);
            txtRazonSocial.requestFocus();
        } else {
            short confirmation = Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Mi Empresa", "¿Esta seguro de continuar?", true);
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
                String result = EmpresaADO.CrudEntity(empresaTB);
                switch (result) {
                    case "registered":
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Mi Empresa", "Registrado correctamente.", false);
                        Session.COMPANY_REPRESENTANTE = txtRepresentante.getText();
                        Session.COMPANY_RAZON_SOCIAL = txtRazonSocial.getText();
                        Session.COMPANY_NOMBRE_COMERCIAL = txtNombreComercial.getText();
                        Session.COMPANY_NUM_DOCUMENTO = txtNumeroDocumento.getText();
                        Session.COMPANY_TELEFONO = txtTelefono.getText();
                        Session.COMPANY_CELULAR = txtCelular.getText();
                        Session.COMPANY_PAGINAWEB = txtPaginasWeb.getText();
                        Session.COMPANY_EMAIL = txtEmail.getText();
                        Session.COMPANY_DOMICILIO = txtDomicilio.getText();
                        break;
                    case "updated":
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Mi Empresa", "Actualizado correctamente.", false);
                        Session.COMPANY_REPRESENTANTE = txtRepresentante.getText();
                        Session.COMPANY_RAZON_SOCIAL = txtRazonSocial.getText();
                        Session.COMPANY_NOMBRE_COMERCIAL = txtNombreComercial.getText();
                        Session.COMPANY_NUM_DOCUMENTO = txtNumeroDocumento.getText();
                        Session.COMPANY_TELEFONO = txtTelefono.getText();
                        Session.COMPANY_CELULAR = txtCelular.getText();
                        Session.COMPANY_PAGINAWEB = txtPaginasWeb.getText();
                        Session.COMPANY_EMAIL = txtEmail.getText();
                        Session.COMPANY_DOMICILIO = txtDomicilio.getText();
                        break;
                    case "error":
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Mi Empresa", "No se puedo completar la ejecución.", false);
                        break;
                    default:
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Mi Empresa", result, false);
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

}
