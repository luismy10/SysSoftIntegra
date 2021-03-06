package controller.preloader;

import controller.tools.SearchComboBox;
import controller.tools.Tools;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.ClienteTB;
import model.DetalleADO;
import model.DetalleTB;
import model.EmpleadoTB;
import model.EmpresaTB;
import model.GlobalADO;
import model.ImpuestoTB;
import model.MonedaTB;
import model.TipoDocumentoTB;
import model.UbigeoADO;
import model.UbigeoTB;

public class FxBienvenidaController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private VBox vbPrimero;
    @FXML
    private VBox vbSegundo;
    @FXML
    private VBox vbTercero;
    @FXML
    private VBox vbCuarto;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnAnterior;
    @FXML
    private Button btnTerminar;
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
    private TextField txtNombreMoneda;
    @FXML
    private TextField txtSimboloMoneda;
    @FXML
    private TextField txtTipoCambioMoneda;
    @FXML
    private TextField txtUsuario;
    @FXML
    private TextField txtClave;
    @FXML
    private ImageView lnPrincipal;
    @FXML
    private ComboBox<UbigeoTB> cbUbigeo;

    private File selectFile;

    private byte[] imageBytes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DetalleADO.GetDetailIdName("3", "0011", "").forEach(e -> {
            cbGiroComercial.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });

        DetalleADO.GetDetailIdName("0", "0003", "RUC").forEach(e -> {
            cbTipoDocumento.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });

        loadUbigeo();
    }

    private void loadUbigeo() {
        SearchComboBox<UbigeoTB> searchComboBoxUbigeoLlegada = new SearchComboBox<>(cbUbigeo, false);
        searchComboBoxUbigeoLlegada.getSearchComboBoxSkin().getSearchBox().setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                if (!searchComboBoxUbigeoLlegada.getSearchComboBoxSkin().getItemView().getItems().isEmpty()) {
                    searchComboBoxUbigeoLlegada.getSearchComboBoxSkin().getItemView().getSelectionModel().select(0);
                    searchComboBoxUbigeoLlegada.getSearchComboBoxSkin().getItemView().requestFocus();
                }
            } else if (t.getCode() == KeyCode.ESCAPE) {
                searchComboBoxUbigeoLlegada.getComboBox().hide();
            }
        });
        searchComboBoxUbigeoLlegada.getSearchComboBoxSkin().getSearchBox().setOnKeyReleased(t -> {
            searchComboBoxUbigeoLlegada.getComboBox().getItems().clear();
            List<UbigeoTB> ubigeoTBs = UbigeoADO.GetSearchComboBoxUbigeo(searchComboBoxUbigeoLlegada.getSearchComboBoxSkin().getSearchBox().getText().trim());
            ubigeoTBs.forEach(e -> {
                searchComboBoxUbigeoLlegada.getComboBox().getItems().add(e);
            });
        });
        searchComboBoxUbigeoLlegada.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
            if (null == t.getCode()) {
                searchComboBoxUbigeoLlegada.getSearchComboBoxSkin().getSearchBox().requestFocus();
                searchComboBoxUbigeoLlegada.getSearchComboBoxSkin().getSearchBox().selectAll();
            } else {
                switch (t.getCode()) {
                    case ENTER:
                    case SPACE:
                    case ESCAPE:
                        searchComboBoxUbigeoLlegada.getComboBox().hide();
                        break;
                    case UP:
                    case DOWN:
                    case LEFT:
                    case RIGHT:
                        break;
                    default:
                        searchComboBoxUbigeoLlegada.getSearchComboBoxSkin().getSearchBox().requestFocus();
                        searchComboBoxUbigeoLlegada.getSearchComboBoxSkin().getSearchBox().selectAll();
                        break;
                }
            }
        });
        searchComboBoxUbigeoLlegada.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                searchComboBoxUbigeoLlegada.getComboBox().getSelectionModel().select(item);
                if (searchComboBoxUbigeoLlegada.getSearchComboBoxSkin().isClickSelection()) {
                    searchComboBoxUbigeoLlegada.getComboBox().hide();
                }
            }
        });
    }

    private void openWindowFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar una imagen");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Elija una imagen", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        selectFile = fileChooser.showOpenDialog(apWindow.getScene().getWindow());
        if (selectFile != null) {
            selectFile = new File(selectFile.getAbsolutePath());
            if (selectFile.getName().endsWith("png") || selectFile.getName().endsWith("jpg") || selectFile.getName().endsWith("jpeg") || selectFile.getName().endsWith("gif")) {
                Image image = new Image(selectFile.toURI().toString(), 200, 200, false, true);
                lnPrincipal.setSmooth(true);
                lnPrincipal.setPreserveRatio(false);
                lnPrincipal.setImage(image);
                imageBytes = null;
            } else {
                Tools.AlertMessageWarning(apWindow, "SysSoft Integra", "No seleccion?? un formato correcto de imagen.");
            }
        }
    }

    private void clearImage() {
        lnPrincipal.setImage(new Image("/view/image/no-image.png"));
        selectFile = null;
        imageBytes = null;
    }

    private void openWindowNextVisible() {
        if (vbPrimero.isVisible()) {
            vbPrimero.setVisible(false);
            vbSegundo.setVisible(true);
        } else if (vbSegundo.isVisible()) {
            vbSegundo.setVisible(false);
            vbTercero.setVisible(true);
        } else if (vbTercero.isVisible()) {
            vbTercero.setVisible(false);
            vbCuarto.setVisible(true);
        }
    }

    private void openWindowBackVisible() {
        if (vbCuarto.isVisible()) {
            vbCuarto.setVisible(false);
            vbTercero.setVisible(true);
        } else if (vbTercero.isVisible()) {
            vbTercero.setVisible(false);
            vbSegundo.setVisible(true);
        } else if (vbSegundo.isVisible()) {
            vbSegundo.setVisible(false);
            vbPrimero.setVisible(true);
        }
    }

    private void openWindowTerminar() {

        if (cbGiroComercial.getSelectionModel().getSelectedIndex() < 0) {
            if (!vbSegundo.isVisible()) {
                vbPrimero.setVisible(false);
                vbTercero.setVisible(false);
                vbCuarto.setVisible(false);
                vbSegundo.setVisible(true);
            }
            cbGiroComercial.requestFocus();
        } else if (txtRepresentante.getText().trim().isEmpty()) {
            if (!vbSegundo.isVisible()) {
                vbPrimero.setVisible(false);
                vbTercero.setVisible(false);
                vbCuarto.setVisible(false);
                vbSegundo.setVisible(true);
            }
            txtRepresentante.requestFocus();
        } else if (txtCelular.getText().trim().isEmpty()) {
            if (!vbSegundo.isVisible()) {
                vbPrimero.setVisible(false);
                vbTercero.setVisible(false);
                vbCuarto.setVisible(false);
                vbSegundo.setVisible(true);
            }
            txtCelular.requestFocus();
        } else if (txtDomicilio.getText().trim().isEmpty()) {
            if (vbSegundo.isVisible()) {
                vbPrimero.setVisible(false);
                vbTercero.setVisible(false);
                vbCuarto.setVisible(false);
                vbSegundo.setVisible(true);
            }
            txtDomicilio.requestFocus();
        } else if (cbTipoDocumento.getSelectionModel().getSelectedIndex() < 0) {
            if (!vbSegundo.isVisible()) {
                vbPrimero.setVisible(false);
                vbTercero.setVisible(false);
                vbCuarto.setVisible(false);
                vbSegundo.setVisible(true);
            }
            cbTipoDocumento.requestFocus();
        } else if (txtNumeroDocumento.getText().trim().isEmpty()) {
            if (!vbSegundo.isVisible()) {
                vbPrimero.setVisible(false);
                vbTercero.setVisible(false);
                vbCuarto.setVisible(false);
                vbSegundo.setVisible(true);
            }
            txtNumeroDocumento.requestFocus();
        } else if (txtRazonSocial.getText().trim().isEmpty()) {
            if (!vbSegundo.isVisible()) {
                vbPrimero.setVisible(false);
                vbTercero.setVisible(false);
                vbCuarto.setVisible(false);
                vbSegundo.setVisible(true);
            }
            txtRazonSocial.requestFocus();
        } else if (txtNombreMoneda.getText().trim().isEmpty()) {
            if (!vbTercero.isVisible()) {
                vbPrimero.setVisible(false);
                vbCuarto.setVisible(false);
                vbSegundo.setVisible(false);
                vbTercero.setVisible(true);
            }
            txtNombreMoneda.requestFocus();
        } else if (txtSimboloMoneda.getText().trim().isEmpty()) {
            if (!vbTercero.isVisible()) {
                vbPrimero.setVisible(false);
                vbCuarto.setVisible(false);
                vbSegundo.setVisible(false);
                vbTercero.setVisible(true);
            }
            txtSimboloMoneda.requestFocus();
        } else if (!Tools.isNumeric(txtTipoCambioMoneda.getText().trim())) {
            if (!vbTercero.isVisible()) {
                vbPrimero.setVisible(false);
                vbCuarto.setVisible(false);
                vbSegundo.setVisible(false);
                vbTercero.setVisible(true);
            }
            txtTipoCambioMoneda.requestFocus();
        } else if (txtUsuario.getText().trim().isEmpty()) {
            if (!vbTercero.isVisible()) {
                vbPrimero.setVisible(false);
                vbCuarto.setVisible(false);
                vbSegundo.setVisible(false);
                vbTercero.setVisible(true);
            }
            txtUsuario.requestFocus();
        } else if (txtClave.getText().trim().isEmpty()) {
            if (!vbTercero.isVisible()) {
                vbPrimero.setVisible(false);
                vbCuarto.setVisible(false);
                vbSegundo.setVisible(false);
                vbTercero.setVisible(true);
            }
            txtClave.requestFocus();
        } else {

            EmpresaTB empresaTB = new EmpresaTB();
            empresaTB.setGiroComerial(cbGiroComercial.getSelectionModel().getSelectedItem().getIdDetalle());
            empresaTB.setNombre(txtRepresentante.getText());
            empresaTB.setTelefono(txtTelefono.getText());
            empresaTB.setCelular(txtCelular.getText());
            empresaTB.setPaginaWeb(txtPaginasWeb.getText());
            empresaTB.setEmail(txtEmail.getText());
            empresaTB.setDomicilio(txtDomicilio.getText());
            empresaTB.setTipoDocumento(cbTipoDocumento.getSelectionModel().getSelectedItem().getIdDetalle());
            empresaTB.setNumeroDocumento(txtNumeroDocumento.getText());
            empresaTB.setRazonSocial(txtRazonSocial.getText());
            empresaTB.setNombreComercial(txtNombreComercial.getText());
            empresaTB.setImage(
                    imageBytes != null ? imageBytes
                            : selectFile == null
                                    ? null
                                    : Tools.getImageBytes(selectFile)
            );
            empresaTB.setIdUbigeo(cbUbigeo.getSelectionModel().getSelectedIndex() >= 0
                    ? cbUbigeo.getSelectionModel().getSelectedItem().getIdUbigeo()
                    : 0);
            empresaTB.setUsuarioSol("");
            empresaTB.setClaveSol("");
            empresaTB.setCertificadoRuta("");
            empresaTB.setCertificadoClave("");

            MonedaTB monedaTB = new MonedaTB();
            monedaTB.setNombre(txtNombreMoneda.getText());
            monedaTB.setSimbolo(txtSimboloMoneda.getText());
            monedaTB.setAbreviado("");
            monedaTB.setTipoCambio(Double.parseDouble(txtTipoCambioMoneda.getText()));
            monedaTB.setPredeterminado(true);
            monedaTB.setSistema(true);

            EmpleadoTB empleadoTB = new EmpleadoTB();
            empleadoTB.setTipoDocumento(1);
            empleadoTB.setNumeroDocumento("00000000");
            empleadoTB.setApellidos("ADMINISTRADOR");
            empleadoTB.setNombres("GENERAL");
            empleadoTB.setUsuario(txtUsuario.getText());
            empleadoTB.setClave(txtClave.getText());

            ImpuestoTB impuestoTB = new ImpuestoTB();
            impuestoTB.setOperacion(2);
            impuestoTB.setNombre("NINGUNO(%)");
            impuestoTB.setValor(0);
            impuestoTB.setCodigo("");
            impuestoTB.setNumeracion("");
            impuestoTB.setNombreImpuesto("");
            impuestoTB.setLetra("");
            impuestoTB.setCategoria("");
            impuestoTB.setPredeterminado(true);
            impuestoTB.setSistema(true);

            TipoDocumentoTB tipoDocumentoTicket= new TipoDocumentoTB();
            tipoDocumentoTicket.setNombre("TICKET");
            tipoDocumentoTicket.setSerie("T001");
            tipoDocumentoTicket.setNumeracion(1);
            tipoDocumentoTicket.setCodigoAlterno("");
            tipoDocumentoTicket.setPredeterminado(true);
            tipoDocumentoTicket.setSistema(true);
            tipoDocumentoTicket.setGuia(false);

            ClienteTB clienteTB = new ClienteTB();
            clienteTB.setTipoDocumento(1);
            clienteTB.setNumeroDocumento("00000000");
            clienteTB.setInformacion("PUBLICO GENERAL");
            clienteTB.setTelefono("");
            clienteTB.setCelular("");
            clienteTB.setEmail("");
            clienteTB.setDireccion("");
            clienteTB.setRepresentante("");
            clienteTB.setEstado(1);
            clienteTB.setPredeterminado(true);
            clienteTB.setSistema(true);

            ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
                Thread t = new Thread(runnable);
                t.setDaemon(true);
                return t;
            });
            Task<String> task = new Task<String>() {
                @Override
                public String call() {
                    return GlobalADO.RegistrarInicioPrograma(empresaTB, monedaTB, empleadoTB, impuestoTB, tipoDocumentoTicket,clienteTB);
                }
            };
            task.setOnScheduled(w -> {
                btnAnterior.setDisable(true);
                btnTerminar.setDisable(true);
                btnCancelar.setDisable(true);
            });
            task.setOnFailed(w -> {
                Tools.AlertMessageError(apWindow, "SysSoftIntegra", task.getMessage());
                btnAnterior.setDisable(false);
                btnTerminar.setDisable(false);
                btnCancelar.setDisable(false);
            });
            task.setOnSucceeded(w -> {
                String result = task.getValue();
                if (result.equalsIgnoreCase("inserted")) {
                    Tools.AlertMessageInformation(apWindow, "SysSoftIntegra", "Se guardo los cambios correctamente, habra nuevamente la aplicaci??n son su usuario y clave creada.");
                    
                    System.exit(0);
                } else {
                    Tools.AlertMessageError(apWindow, "SysSoftIntegra", result);
                    btnAnterior.setDisable(false);
                    btnTerminar.setDisable(false);
                    btnCancelar.setDisable(false);
                }
            });
            exec.execute(task);
            if (!exec.isShutdown()) {
                exec.shutdown();
            }
        }
    }

    @FXML
    private void onKeyPressedSiguiente(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowNextVisible();
        }
    }

    @FXML
    private void onActionSiguiente(ActionEvent event) {
        openWindowNextVisible();
    }

    @FXML
    private void onKeyPressedAnteriorSegundo(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowBackVisible();
        }
    }

    @FXML
    private void onActionAnteriorSegundo(ActionEvent event) {
        openWindowBackVisible();
    }

    @FXML
    private void onKeyPressedSiguienteSegundo(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowNextVisible();
        }
    }

    @FXML
    private void onActionSiguienteSegundo(ActionEvent event) {
        openWindowNextVisible();
    }

    @FXML
    private void onKeyPressedAnteriorTercero(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowBackVisible();
        }
    }

    @FXML
    private void onActionAnteriorTercero(ActionEvent event) {
        openWindowBackVisible();
    }

    @FXML
    private void onKeyPressedSiguienteTercero(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowNextVisible();
        }
    }

    @FXML
    private void onActionSiguienteTercero(ActionEvent event) {
        openWindowNextVisible();
    }

    @FXML
    private void onKeyPressedAnteriorCuarto(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowBackVisible();
        }
    }

    @FXML
    private void onActionAnteriorCuarto(ActionEvent event) {
        openWindowBackVisible();
    }

    @FXML
    private void onKeyPressedTerminar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowTerminar();
        }
    }

    @FXML
    private void onActionTerminar(ActionEvent event) {
        openWindowTerminar();
    }

    @FXML
    private void onKeyPressedCancelar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            short value = Tools.AlertMessageConfirmation(apWindow, "SysSoftIntegra", "??Est?? seguro se cancelar el proceso?");
            if (value == 1) {
                Tools.Dispose(apWindow);
            }
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        short value = Tools.AlertMessageConfirmation(apWindow, "SysSoftIntegra", "??Est?? seguro se cancelar el proceso?");
        if (value == 1) {
            Tools.Dispose(apWindow);
        }
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

    @FXML
    private void onKeyTypedTipoCambio(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
            event.consume();
        }
        if (c == '.' && txtTipoCambioMoneda.getText().contains(".")) {
            event.consume();
        }
    }

}
