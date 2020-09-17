package controller.operaciones.guiaremision;

import controller.contactos.clientes.FxClienteProcesoController;
import controller.reporte.FxReportViewController;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.SearchComboBox;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.ClienteADO;
import model.ClienteTB;
import model.DetalleADO;
import model.DetalleTB;
import model.GuiaRemisionTB;
import model.TipoDocumentoADO;
import model.TipoDocumentoTB;
import model.UbigeoADO;
import model.UbigeoTB;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

public class FxGuiaRemisionController implements Initializable {

    @FXML
    private ScrollPane spWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private ComboBox<ClienteTB> cbCliente;
    @FXML
    private TextField txtEmail;
    @FXML
    private ComboBox<DetalleTB> cbMotivoTraslado;
    @FXML
    private ComboBox<DetalleTB> cbModalidadTraslado;
    @FXML
    private DatePicker dtFechaTraslado;
    @FXML
    private TextField txtPesoBruto;
    @FXML
    private TextField txtNumeroBultos;
    @FXML
    private ComboBox<DetalleTB> cbTipoDocumento;
    @FXML
    private TextField txtNumeroDocumento;
    @FXML
    private TextField txtNombreConducto;
    @FXML
    private TextField txtNumeroPlacaVehiculo;
    @FXML
    private TextField txtDireccionPartida;
    @FXML
    private ComboBox<UbigeoTB> cbUbigeoPartida;
    @FXML
    private TextField txtDireccionLlegada;
    @FXML
    private ComboBox<UbigeoTB> cbUbigeoLlegada;
    @FXML
    private ComboBox<TipoDocumentoTB> cbTipoComprobante;
    @FXML
    private TextField txtSerie;
    @FXML
    private TextField txtNumeracion;
    @FXML
    private TableView<GuiaRemisionTB> tvList;
    @FXML
    private TableColumn<GuiaRemisionTB, String> tcNumero;
    @FXML
    private TableColumn<GuiaRemisionTB, String> tcCodigo;
    @FXML
    private TableColumn<GuiaRemisionTB, String> tcDescripcion;
    @FXML
    private TableColumn<GuiaRemisionTB, String> tcMedida;
    @FXML
    private TableColumn<GuiaRemisionTB, String> tcCantidad;
    @FXML
    private TableColumn<GuiaRemisionTB, String> tcPeso;
    @FXML
    private TableColumn<GuiaRemisionTB, HBox> tcOpcion;

    private AnchorPane vbPrincipal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcNumero.prefWidthProperty().bind(tvList.widthProperty().multiply(0.06));
        tcCodigo.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        tcDescripcion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.23));
        tcMedida.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcCantidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcPeso.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcOpcion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));

        SearchComboBox<ClienteTB> searchComboBoxCliente = new SearchComboBox<>(cbCliente, false);
        searchComboBoxCliente.getSearchComboBoxSkin().getSearchBox().setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                if (!searchComboBoxCliente.getSearchComboBoxSkin().getItemView().getItems().isEmpty()) {
                    searchComboBoxCliente.getSearchComboBoxSkin().getItemView().getSelectionModel().select(0);
                    searchComboBoxCliente.getSearchComboBoxSkin().getItemView().requestFocus();
                }
            } else if (t.getCode() == KeyCode.ESCAPE) {
                searchComboBoxCliente.getComboBox().hide();
            }
        });
        searchComboBoxCliente.getSearchComboBoxSkin().getSearchBox().setOnKeyReleased(t -> {
            searchComboBoxCliente.getComboBox().getItems().clear();
            List<ClienteTB> clienteTBs = ClienteADO.GetSearchComboBoxCliente((short) 4, searchComboBoxCliente.getSearchComboBoxSkin().getSearchBox().getText().trim());
            clienteTBs.forEach(e -> searchComboBoxCliente.getComboBox().getItems().add(e));
        });
        searchComboBoxCliente.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
            if (null == t.getCode()) {
                searchComboBoxCliente.getSearchComboBoxSkin().getSearchBox().requestFocus();
                searchComboBoxCliente.getSearchComboBoxSkin().getSearchBox().selectAll();
            } else {
                switch (t.getCode()) {
                    case ENTER:
                    case SPACE:
                    case ESCAPE:
                        searchComboBoxCliente.getComboBox().hide();
                        break;
                    case UP:
                    case DOWN:
                    case LEFT:
                    case RIGHT:
                        break;
                    default:
                        searchComboBoxCliente.getSearchComboBoxSkin().getSearchBox().requestFocus();
                        searchComboBoxCliente.getSearchComboBoxSkin().getSearchBox().selectAll();
                        break;
                }
            }
        });
        searchComboBoxCliente.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                searchComboBoxCliente.getComboBox().getSelectionModel().select(item);
                if (searchComboBoxCliente.getSearchComboBoxSkin().isClickSelection()) {
                    searchComboBoxCliente.getComboBox().hide();
                }
            }
        });

        Tools.actualDate(Tools.getDate(), dtFechaTraslado);
        loadUbigeoPartida();
        loadUbigeoLlegada();
        loadComponents();
    }

    private void loadUbigeoPartida() {
        SearchComboBox<UbigeoTB> searchComboBoxUbigeoPartida = new SearchComboBox<>(cbUbigeoPartida, false);
        searchComboBoxUbigeoPartida.getSearchComboBoxSkin().getSearchBox().setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                if (!searchComboBoxUbigeoPartida.getSearchComboBoxSkin().getItemView().getItems().isEmpty()) {
                    searchComboBoxUbigeoPartida.getSearchComboBoxSkin().getItemView().getSelectionModel().select(0);
                    searchComboBoxUbigeoPartida.getSearchComboBoxSkin().getItemView().requestFocus();
                }
            } else if (t.getCode() == KeyCode.ESCAPE) {
                searchComboBoxUbigeoPartida.getComboBox().hide();
            }
        });
        searchComboBoxUbigeoPartida.getSearchComboBoxSkin().getSearchBox().setOnKeyReleased(t -> {
            searchComboBoxUbigeoPartida.getComboBox().getItems().clear();
            List<UbigeoTB> ubigeoTBs = UbigeoADO.GetSearchComboBoxUbigeo(searchComboBoxUbigeoPartida.getSearchComboBoxSkin().getSearchBox().getText().trim());
            ubigeoTBs.forEach(e -> {
                searchComboBoxUbigeoPartida.getComboBox().getItems().add(e);
            });
        });
        searchComboBoxUbigeoPartida.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
            if (null == t.getCode()) {
                searchComboBoxUbigeoPartida.getSearchComboBoxSkin().getSearchBox().requestFocus();
                searchComboBoxUbigeoPartida.getSearchComboBoxSkin().getSearchBox().selectAll();
            } else {
                switch (t.getCode()) {
                    case ENTER:
                    case SPACE:
                    case ESCAPE:
                        searchComboBoxUbigeoPartida.getComboBox().hide();
                        break;
                    case UP:
                    case DOWN:
                    case LEFT:
                    case RIGHT:
                        break;
                    default:
                        searchComboBoxUbigeoPartida.getSearchComboBoxSkin().getSearchBox().requestFocus();
                        searchComboBoxUbigeoPartida.getSearchComboBoxSkin().getSearchBox().selectAll();
                        break;
                }
            }
        });
        searchComboBoxUbigeoPartida.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                searchComboBoxUbigeoPartida.getComboBox().getSelectionModel().select(item);
                if (searchComboBoxUbigeoPartida.getSearchComboBoxSkin().isClickSelection()) {
                    searchComboBoxUbigeoPartida.getComboBox().hide();
                }
            }
        });
    }

    private void loadUbigeoLlegada() {
        SearchComboBox<UbigeoTB> searchComboBoxUbigeoLlegada = new SearchComboBox<>(cbUbigeoLlegada, false);
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

    private void loadComponents() {
        ExecutorService executor = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            protected ArrayList<Object> call() {
                ArrayList<Object> objects = new ArrayList();
                objects.add(DetalleADO.GetDetailId("0017"));
                objects.add(DetalleADO.GetDetailId("0018"));
                objects.add(DetalleADO.GetDetailId("0003"));
                objects.add(TipoDocumentoADO.GetDocumentoCombBox());

                return objects;
            }
        };

        task.setOnScheduled(e -> {
            lblLoad.setVisible(true);
        });

        task.setOnFailed(e -> {
            lblLoad.setVisible(false);
        });

        task.setOnSucceeded(e -> {
            ArrayList<Object> objects = task.getValue();
            if (!objects.isEmpty()) {
                if (objects.get(0) != null) {
                    cbMotivoTraslado.getItems().clear();
                    ObservableList<DetalleTB> motivoTraslado = (ObservableList<DetalleTB>) objects.get(0);
                    motivoTraslado.forEach(d -> {
                        cbMotivoTraslado.getItems().add(new DetalleTB(d.getIdDetalle(), d.getNombre()));
                    });
                }

                if (objects.get(1) != null) {
                    cbModalidadTraslado.getItems().clear();
                    ObservableList<DetalleTB> motivoTransporte = (ObservableList<DetalleTB>) objects.get(1);
                    motivoTransporte.forEach(d -> {
                        cbModalidadTraslado.getItems().add(new DetalleTB(d.getIdDetalle(), d.getNombre()));
                    });
                }
                if (objects.get(2) != null) {
                    cbTipoDocumento.getItems().clear();
                    ObservableList<DetalleTB> tipoDocumento = (ObservableList<DetalleTB>) objects.get(2);
                    tipoDocumento.forEach(d -> {
                        cbTipoDocumento.getItems().add(new DetalleTB(d.getIdDetalle(), d.getNombre()));
                    });
                }
                if (objects.get(3) != null) {
                    cbTipoComprobante.getItems().clear();
                    List<TipoDocumentoTB> tipoComprobante = (List<TipoDocumentoTB>) objects.get(3);
                    tipoComprobante.forEach(cbTipoComprobante.getItems()::add);
                }
                lblLoad.setVisible(false);
            } else {
                lblLoad.setVisible(false);
            }
        });
        executor.execute(task);
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
    }

    private void reportGuiaRemision() {
        try {
            InputStream imgInputStreamIcon = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            InputStream imgInputStream = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            if (Session.COMPANY_IMAGE != null) {
                imgInputStream = new ByteArrayInputStream(Session.COMPANY_IMAGE);
            }
            InputStream dir = getClass().getResourceAsStream("/report/GuiadeRemision.jasper");
            Map map = new HashMap();
            map.put("LOGO", imgInputStream);
            map.put("ICON", imgInputStreamIcon);
            map.put("RUC_EMPRESA", Session.COMPANY_NUMERO_DOCUMENTO);
            map.put("NOMBRE_EMPRESA", Session.COMPANY_RAZON_SOCIAL);
            map.put("DIRECCION_EMPRESA", Session.COMPANY_DOMICILIO);
            map.put("TELEFONO_EMPRESA", Session.COMPANY_TELEFONO);
            map.put("CELULAR_EMPRESA", Session.COMPANY_CELULAR);
            map.put("EMAIL_EMPRESA", Session.COMPANY_EMAIL);

            JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JREmptyDataSource());
            URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxReportViewController controller = fXMLLoader.getController();
            controller.setJasperPrint(jasperPrint);
            controller.show();
            Stage stage = WindowStage.StageLoader(parent, "Guia de remisión");
            stage.setResizable(true);
            stage.show();
            stage.requestFocus();
        } catch (JRException | IOException ex) {
            Tools.AlertMessageError(spWindow, "Reporte de Ventas", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    private void onEventCliente() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_CLIENTE_PROCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxClienteProcesoController controller = fXMLLoader.getController();
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Cliente", spWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
            controller.setValueAdd();
        } catch (IOException ex) {
            System.out.println("Cliente controller en openWindowAddCliente()" + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onKeyTypedPesoBruto(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
            event.consume();
        }
        if (c == '.' && txtPesoBruto.getText().contains(".")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedNumeroBultos(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
            event.consume();
        }
        if (c == '.' && txtNumeroBultos.getText().contains(".")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedNumeroDocumento(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
            event.consume();
        }
    }

    @FXML
    private void onKeyTypedNumeracion(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
            event.consume();
        }
    }

    @FXML
    private void onKeyPressedToRegister(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionToRegister(ActionEvent event) {

    }

    @FXML
    private void onKeyPressedPrevisualizar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            reportGuiaRemision();
        }
    }

    @FXML
    private void onActionPrevisualizar(ActionEvent event) {
        reportGuiaRemision();
    }

    @FXML
    private void onKeyPressedToClient(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventCliente();
        }
    }

    @FXML
    private void onActionToClient(ActionEvent event) {
        onEventCliente();
    }

    @FXML
    private void onKeyPressedBuscarVenta(KeyEvent event) {
    }

    @FXML
    private void onActionBuscarVenta(ActionEvent event) {
    }

    @FXML
    private void onKeyPressedToReload(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            loadComponents();
        }
    }

    @FXML
    private void onActionToReload(ActionEvent event) {
        loadComponents();
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

    @FXML
    private void onKeyPressedAgregar(KeyEvent event) {
    }

    @FXML
    private void onActionAgregar(ActionEvent event) {
    }

}
