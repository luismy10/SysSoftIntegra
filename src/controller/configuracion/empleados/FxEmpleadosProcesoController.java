package controller.configuracion.empleados;

import controller.tools.Tools;
import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import model.CiudadADO;
import model.CiudadTB;
import model.DetalleADO;
import model.DetalleTB;
import model.DistritoADO;
import model.DistritoTB;
import model.EmpleadoADO;
import model.EmpleadoTB;
import model.PaisADO;
import model.PaisTB;
import model.ProvinciaADO;
import model.ProvinciaTB;
import model.RolADO;
import model.RolTB;

public class FxEmpleadosProcesoController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private ComboBox<DetalleTB> cbTipoDocumento;
    @FXML
    private TextField txtNumeroDocumento;
    @FXML
    private TextField txtApellidos;
    @FXML
    private TextField txtNombres;
    @FXML
    private ComboBox<DetalleTB> cbSexo;
    @FXML
    private DatePicker dpFechaNacimiento;
    @FXML
    private ComboBox<DetalleTB> cbPuesto;
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
    private ComboBox<PaisTB> cbPais;
    @FXML
    private ComboBox<CiudadTB> cbCiudad;
    @FXML
    private ComboBox<ProvinciaTB> cbProvincia;
    @FXML
    private ComboBox<DistritoTB> cbDistrito;
    @FXML
    private TextField txtUsuario;
    @FXML
    private TextField txtClave;
    @FXML
    private ComboBox<RolTB> cbRol;
    @FXML
    private ImageView ivPerfil;
    @FXML
    private Button btnRegister;

    private String idEmpleado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
        DetalleADO.GetDetailIdName("1", "0003", "RUC").forEach(e -> {
            cbTipoDocumento.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        DetalleADO.GetDetailId("0004").forEach(e -> {
            cbSexo.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        DetalleADO.GetDetailIdName("2", "0012", "").forEach(e -> {
            cbPuesto.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        RolADO.RolList().forEach(e -> {
            cbRol.getItems().add(new RolTB(e.getIdRol(), e.getNombre(),e.isSistema()));
        });
        DetalleADO.GetDetailIdName("2", "0001", "").forEach(e -> {
            cbEstado.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        cbEstado.getSelectionModel().select(0);
        PaisADO.ListPais().forEach(e -> {
            cbPais.getItems().add(new PaisTB(e.getPaisCodigo(), e.getPaisNombre()));
        });
        idEmpleado = "";
    }

    public void setEditEmpleado(String value) {
        btnRegister.setText("Actualizar");
        btnRegister.getStyleClass().add("buttonLightWarning");
        EmpleadoTB empleadoTB = EmpleadoADO.GetByIdEmpleados(value);
        if (empleadoTB != null) {

            idEmpleado = value;

            ObservableList<DetalleTB> lstype = cbTipoDocumento.getItems();
            for (int i = 0; i < lstype.size(); i++) {
                if (empleadoTB.getTipoDocumento() == lstype.get(i).getIdDetalle().get()) {
                    cbTipoDocumento.getSelectionModel().select(i);
                    break;
                }
            }

            txtNumeroDocumento.setText(empleadoTB.getNumeroDocumento());
            txtApellidos.setText(empleadoTB.getApellidos());
            txtNombres.setText(empleadoTB.getNombres());

            if (empleadoTB.getSexo() != 0) {
                ObservableList<DetalleTB> lstsex = cbSexo.getItems();
                for (int i = 0; i < lstsex.size(); i++) {
                    if (empleadoTB.getSexo() == lstsex.get(i).getIdDetalle().get()) {
                        cbSexo.getSelectionModel().select(i);
                        break;
                    }
                }
            }

            if (empleadoTB.getFechaNacimiento() != null) {
                Tools.actualDate(empleadoTB.getFechaNacimiento().toString(), dpFechaNacimiento);
            }

            if (empleadoTB.getPuesto() != 0) {
                ObservableList<DetalleTB> lstpus = cbPuesto.getItems();
                for (int i = 0; i < lstpus.size(); i++) {
                    if (empleadoTB.getPuesto() == lstpus.get(i).getIdDetalle().get()) {
                        cbPuesto.getSelectionModel().select(i);
                        break;
                    }
                }
            }

            if (empleadoTB.getEstado() != 0) {
                ObservableList<DetalleTB> lstest = cbEstado.getItems();
                for (int i = 0; i < lstest.size(); i++) {
                    if (empleadoTB.getEstado() == lstest.get(i).getIdDetalle().get()) {
                        cbEstado.getSelectionModel().select(i);
                        break;
                    }
                }
            }

            txtTelefono.setText(empleadoTB.getTelefono());
            txtCelular.setText(empleadoTB.getCelular());
            txtEmail.setText(empleadoTB.getEmail());
            txtDireccion.setText(empleadoTB.getDireccion());

            if (empleadoTB.getPais() != null) {
                ObservableList<PaisTB> lspais = cbPais.getItems();
                for (int i = 0; i < lspais.size(); i++) {
                    if (empleadoTB.getPais().equals(lspais.get(i).getPaisCodigo())) {
                        cbPais.getSelectionModel().select(i);
                        CiudadADO.ListCiudad(cbPais.getSelectionModel().getSelectedItem().getPaisCodigo()).forEach(e -> {
                            cbCiudad.getItems().add(new CiudadTB(e.getIdCiudad(), e.getCiudadDistrito()));
                        });
                        break;
                    }
                }
            }

            if (empleadoTB.getCiudad() != 0) {
                ObservableList<CiudadTB> lsciudad = cbCiudad.getItems();
                for (int i = 0; i < lsciudad.size(); i++) {
                    if (empleadoTB.getCiudad() == lsciudad.get(i).getIdCiudad()) {
                        cbCiudad.getSelectionModel().select(i);
                        ProvinciaADO.ListProvincia(cbCiudad.getSelectionModel().getSelectedItem().getIdCiudad()).forEach(e -> {
                            cbProvincia.getItems().add(new ProvinciaTB(e.getIdProvincia(), e.getProvincia()));
                        });
                        break;
                    }
                }
            }

            if (empleadoTB.getProvincia() != 0) {
                ObservableList<ProvinciaTB> lsprovin = cbProvincia.getItems();
                for (int i = 0; i < lsprovin.size(); i++) {
                    if (empleadoTB.getProvincia() == lsprovin.get(i).getIdProvincia()) {
                        cbProvincia.getSelectionModel().select(i);
                        DistritoADO.ListDistrito(cbProvincia.getSelectionModel().getSelectedItem().getIdProvincia()).forEach(e -> {
                            cbDistrito.getItems().add(new DistritoTB(e.getIdDistrito(), e.getDistrito()));
                        });
                        break;
                    }
                }
            }

            if (empleadoTB.getDistrito() != 0) {
                ObservableList<DistritoTB> lsdistrito = cbDistrito.getItems();
                for (int i = 0; i < lsdistrito.size(); i++) {
                    if (empleadoTB.getDistrito() == lsdistrito.get(i).getIdDistrito()) {
                        cbDistrito.getSelectionModel().select(i);
                        break;
                    }
                }
            }

            txtUsuario.setText(empleadoTB.getUsuario());
            txtClave.setText(empleadoTB.getClave());

            if (empleadoTB.getRol() != 0) {
                ObservableList<RolTB> lsrol = cbRol.getItems();
                for (int i = 0; i < lsrol.size(); i++) {
                    if (empleadoTB.getRol() == lsrol.get(i).getIdRol()) {
                        cbRol.getSelectionModel().select(i);
                        break;
                    }
                }
            }
            
        }
    }

    private void onActionProcessCrud() throws ParseException {
        if (cbTipoDocumento.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Empleado", "Seleccione el tipo de documento del empleado", false);
            cbTipoDocumento.requestFocus();
        } else if (Tools.isText(txtNumeroDocumento.getText())) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Empleado", "Ingrese el número del documento del empleado", false);
            txtNumeroDocumento.requestFocus();
        } else if (Tools.isText(txtApellidos.getText())) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Empleado", "Ingrese los apellidos del empleado", false);
            txtApellidos.requestFocus();
        } else if (Tools.isText(txtNombres.getText())) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Empleado", "Ingrese los nombres del empleado", false);
            txtNombres.requestFocus();
        } else if (cbPuesto.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Empleado", "Seleccione el puesto del empleado", false);
            cbPuesto.requestFocus();
        } else if (cbEstado.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Empleado", "Seleccione el estado del empleado", false);
            cbEstado.requestFocus();
        } else {
            short confirmation = Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Empleado", "¿Esta seguro de continuar?", true);
            if (confirmation == 1) {
                EmpleadoTB empleadoTB = new EmpleadoTB();
                empleadoTB.setIdEmpleado(idEmpleado);
                empleadoTB.setTipoDocumento(cbTipoDocumento.getSelectionModel().getSelectedIndex() >= 0
                        ? cbTipoDocumento.getSelectionModel().getSelectedItem().getIdDetalle().get()
                        : 0);
                empleadoTB.setNumeroDocumento(txtNumeroDocumento.getText().trim());
                empleadoTB.setApellidos(txtApellidos.getText().trim().toUpperCase());
                empleadoTB.setNombres(txtNombres.getText().trim().toUpperCase());
                empleadoTB.setSexo(cbSexo.getSelectionModel().getSelectedIndex() >= 0
                        ? cbSexo.getSelectionModel().getSelectedItem().getIdDetalle().get()
                        : 0);

                if (dpFechaNacimiento.getValue() != null) {
                    empleadoTB.setFechaNacimiento(new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(Tools.getDatePicker(dpFechaNacimiento)).getTime()));
                } else {
                    empleadoTB.setFechaNacimiento(null);
                }

                empleadoTB.setPuesto(cbPuesto.getSelectionModel().getSelectedIndex() >= 0
                        ? cbPuesto.getSelectionModel().getSelectedItem().getIdDetalle().get()
                        : 0);
                empleadoTB.setEstado(cbEstado.getSelectionModel().getSelectedIndex() >= 0
                        ? cbEstado.getSelectionModel().getSelectedItem().getIdDetalle().get()
                        : 0);
                empleadoTB.setTelefono(txtTelefono.getText().trim());
                empleadoTB.setCelular(txtCelular.getText().trim());
                empleadoTB.setEmail(txtEmail.getText().trim().toUpperCase());
                empleadoTB.setDireccion(txtDireccion.getText().trim().toUpperCase());
                empleadoTB.setPais(cbPais.getSelectionModel().getSelectedIndex() >= 0
                        ? cbPais.getSelectionModel().getSelectedItem().getPaisCodigo()
                        : "");
                empleadoTB.setCiudad(cbCiudad.getSelectionModel().getSelectedIndex() >= 0
                        ? cbCiudad.getSelectionModel().getSelectedItem().getIdCiudad()
                        : 0);
                empleadoTB.setProvincia(cbProvincia.getSelectionModel().getSelectedIndex() >= 0
                        ? cbProvincia.getSelectionModel().getSelectedItem().getIdProvincia()
                        : 0);
                empleadoTB.setDistrito(cbDistrito.getSelectionModel().getSelectedIndex() >= 0
                        ? cbDistrito.getSelectionModel().getSelectedItem().getIdDistrito()
                        : 0);
                empleadoTB.setUsuario(txtUsuario.getText().trim());
                empleadoTB.setClave(txtClave.getText().trim());
                empleadoTB.setRol(cbRol.getSelectionModel().getSelectedIndex() >= 0
                        ? cbRol.getSelectionModel().getSelectedItem().getIdRol()
                        : 0);
                if (idEmpleado.equalsIgnoreCase("")) {
                    String result = EmpleadoADO.InsertEmpleado(empleadoTB);
                    switch (result) {
                        case "register":
                            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Empleado", "Registrado correctamente el empleado.", false);
                            Tools.Dispose(window);
                            break;
                        default:
                            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Empleado", result, false);
                            break;
                    }
                } else {
                    String result = EmpleadoADO.UpdateEmpleado(empleadoTB);
                    switch (result) {
                        case "update":
                            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Empleado", "Actualizado correctamente el empleado.", false);
                            break;
                        default:
                            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Empleado", result, false);
                            break;
                    }
                }

            }
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

    @FXML
    private void onKeyPressedToRegister(KeyEvent event) throws ParseException {
        if (event.getCode() == KeyCode.ENTER) {
            onActionProcessCrud();

        }
    }

    @FXML
    private void onActionToRegister(ActionEvent event) throws ParseException {
        onActionProcessCrud();
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
    private void onMouseClickedImage(MouseEvent event) {
        if (event.getClickCount() == 2) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Importar una imagen");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Elija una imagen", "*.png", "*.jpg", "*.jpeg"));
            File file = fileChooser.showOpenDialog(window.getScene().getWindow());
            if (file != null) {
                file = new File(file.getAbsolutePath());
                if (file.getName().endsWith("png") || file.getName().endsWith("jpg") || file.getName().endsWith("jpeg")) {
                    ivPerfil.setImage(new Image(file.toURI().toString()));

                }
            }
        }
    }

}
