package controller.operaciones.guiaremision;

import controller.contactos.clientes.FxClienteProcesoController;
import controller.inventario.suministros.FxSuministrosListaController;
import controller.menus.FxPrincipalController;
import controller.operaciones.venta.FxVentaListaController;
import controller.reporte.FxReportViewController;
import controller.tools.FilesRouters;
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
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.ClienteADO;
import model.ClienteTB;
import model.DetalleADO;
import model.DetalleTB;
import model.GuiaRemisionADO;
import model.GuiaRemisionDetalleTB;
import model.GuiaRemisionTB;
import model.SuministroTB;
import model.TipoDocumentoADO;
import model.TipoDocumentoTB;
import model.UbigeoADO;
import model.UbigeoTB;
import model.VentaADO;
import model.VentaTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FxGuiaRemisionController implements Initializable {

    @FXML
    private ScrollPane spWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private ComboBox<TipoDocumentoTB> cbDocumentoGuia;
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
    private TextField txtTelefonoCelular;
    @FXML
    private TextField txtNumeroPlacaVehiculo;
    @FXML
    private TextField txtMarcaVehiculo;
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
    private TextField txtSerieFactura;
    @FXML
    private TextField txtNumeracionFactura;
    @FXML
    private TableView<GuiaRemisionDetalleTB> tvList;
    @FXML
    private TableColumn<GuiaRemisionDetalleTB, TextField> tcCodigo;
    @FXML
    private TableColumn<GuiaRemisionDetalleTB, TextField> tcDescripcion;
    @FXML
    private TableColumn<GuiaRemisionDetalleTB, TextField> tcMedida;
    @FXML
    private TableColumn<GuiaRemisionDetalleTB, TextField> tcCantidad;
    @FXML
    private TableColumn<GuiaRemisionDetalleTB, TextField> tcPeso;
    @FXML
    private TableColumn<GuiaRemisionDetalleTB, Button> tcOpcion;

    private FxPrincipalController fxPrincipalController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcCodigo.setCellValueFactory(new PropertyValueFactory<>("txtCodigo"));
        tcDescripcion.setCellValueFactory(new PropertyValueFactory<>("txtDescripcion"));
        tcMedida.setCellValueFactory(new PropertyValueFactory<>("txtUnidad"));
        tcCantidad.setCellValueFactory(new PropertyValueFactory<>("txtCantidad"));
        tcPeso.setCellValueFactory(new PropertyValueFactory<>("txtPeso"));
        tcOpcion.setCellValueFactory(new PropertyValueFactory<>("btnRemover"));

        tcCodigo.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        tcDescripcion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.25));
        tcMedida.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));
        tcCantidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));
        tcPeso.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));
        tcOpcion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));

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
        txtDireccionPartida.setText(Session.COMPANY_DOMICILIO);
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
                objects.add(TipoDocumentoADO.GetDocumentoCombBoxVentas());
                objects.add(TipoDocumentoADO.GetTipoDocumentoGuiaRemision());
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
                if (objects.get(4) != null) {
                    ArrayList<TipoDocumentoTB> documentoTBs = (ArrayList<TipoDocumentoTB>) objects.get(4);
                    documentoTBs.forEach(cbDocumentoGuia.getItems()::add);
                    if (cbDocumentoGuia.getItems().size() == 1) {
                        cbDocumentoGuia.getSelectionModel().select(0);
                    }
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

    public void eventAgregar(SuministroTB suministroTB) {
        GuiaRemisionDetalleTB guiaRemisionDetalleTB = new GuiaRemisionDetalleTB();
        guiaRemisionDetalleTB.setIdSuministro(suministroTB.getIdSuministro());
        guiaRemisionDetalleTB.setCodigo(suministroTB.getClave());
        guiaRemisionDetalleTB.setCantidad(suministroTB.getCantidad());
        guiaRemisionDetalleTB.setDescripcion(suministroTB.getNombreMarca());
        guiaRemisionDetalleTB.setUnidad(suministroTB.getUnidadCompraName());
        guiaRemisionDetalleTB.setPeso(0);

        TextField txtCodigo = new TextField(guiaRemisionDetalleTB.getCodigo());
        txtCodigo.getStyleClass().add("text-field-normal");

        TextField txtDescripcion = new TextField(guiaRemisionDetalleTB.getDescripcion());
        txtDescripcion.getStyleClass().add("text-field-normal");

        TextField txtUnidad = new TextField(guiaRemisionDetalleTB.getUnidad());
        txtUnidad.getStyleClass().add("text-field-normal");

        TextField txtCantidad = new TextField(Tools.roundingValue(guiaRemisionDetalleTB.getCantidad(), 0));
        txtCantidad.getStyleClass().add("text-field-normal");
        txtCantidad.setOnKeyTyped(event -> {
            char c = event.getCharacter().charAt(0);
            if ((c < '0' || c > '9') && (c != '\b')) {
                event.consume();
            }
        });

        TextField txtPeso = new TextField(Tools.roundingValue(guiaRemisionDetalleTB.getPeso(), 2));
        txtPeso.getStyleClass().add("text-field-normal");
        txtPeso.setOnKeyTyped(event -> {
            char c = event.getCharacter().charAt(0);
            if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
                event.consume();
            }
            if (c == '.' && txtPeso.getText().contains(".")) {
                event.consume();
            }
        });

        Button btnRemover = new Button();
        btnRemover.getStyleClass().add("buttonBorder");
        ImageView view = new ImageView(new Image("/view/image/remove-black.png"));
        view.setFitWidth(18);
        view.setFitHeight(18);
        btnRemover.setGraphic(view);
        btnRemover.setOnAction(event -> {
            tvList.getItems().remove(guiaRemisionDetalleTB);
        });
        btnRemover.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                tvList.getItems().remove(guiaRemisionDetalleTB);
            }
        });

        guiaRemisionDetalleTB.setTxtCodigo(txtCodigo);
        guiaRemisionDetalleTB.setTxtDescripcion(txtDescripcion);
        guiaRemisionDetalleTB.setTxtUnidad(txtUnidad);
        guiaRemisionDetalleTB.setTxtCantidad(txtCantidad);
        guiaRemisionDetalleTB.setTxtPeso(txtPeso);
        guiaRemisionDetalleTB.setBtnRemover(btnRemover);

        tvList.getItems().add(guiaRemisionDetalleTB);

    }

    private void reportGuiaRemision(GuiaRemisionTB guiaRemisionTB) {
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

            map.put("NUMERACION_GUIA_REMISION", guiaRemisionTB.getSerie() + "-" + guiaRemisionTB.getNumeracion());
            map.put("FECHA_TRASLADO", guiaRemisionTB.getFechaTraslado());
            map.put("NUMERO_FACTURA", guiaRemisionTB.getSerieFactura() + "-" + guiaRemisionTB.getNumeracionFactura());

            map.put("DIRECCION_PARTIDA", guiaRemisionTB.getDireccionPartida());
            map.put("UBIGEO_PARTIDA", guiaRemisionTB.getUbigeoPartidaDescripcion());

            map.put("DIRECCION_LLEGAGA", guiaRemisionTB.getDireccionLlegada());
            map.put("UBIGEO_LLEGADA", guiaRemisionTB.getUbigeoLlegadaDescripcion());

            map.put("NOMBRE_COMERCIAL", Session.COMPANY_NOMBRE_COMERCIAL);
            map.put("RUC_EMPRESA", Session.COMPANY_NUMERO_DOCUMENTO);

            map.put("NOMBRE_CLIENTE", guiaRemisionTB.getClienteTB().getInformacion());
            map.put("RUC_CLIENTE", guiaRemisionTB.getClienteTB().getNumeroDocumento());

            map.put("DATOS_TRANSPORTISTA", guiaRemisionTB.getNombreConductor());
            map.put("DOCUMENTO_TRANSPORTISTA", guiaRemisionTB.getNumeroConductor());
            map.put("MARCA_VEHICULO", guiaRemisionTB.getMarcaVehiculo());
            map.put("NUMERO_PLACA", guiaRemisionTB.getNumeroPlaca());

            map.put("MOTIVO_TRANSPORTE", guiaRemisionTB.getMotivoTrasladoDescripcion());

            JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JRBeanCollectionDataSource(guiaRemisionTB.getListGuiaRemisionDetalle()));
            URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxReportViewController controller = fXMLLoader.getController();
            controller.setJasperPrint(jasperPrint);
            controller.show();
            Stage stage = WindowStage.StageLoader(parent, "Guia de remisi??n");
            stage.setResizable(true);
            stage.show();
            stage.requestFocus();
        } catch (JRException | IOException ex) {
            Tools.AlertMessageError(spWindow, "Reporte de Ventas", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    private void onEventCliente() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_CLIENTE_PROCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxClienteProcesoController controller = fXMLLoader.getController();
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Cliente", spWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
            controller.setValueAdd();
        } catch (IOException ex) {
            System.out.println("Cliente controller en openWindowAddCliente()" + ex.getLocalizedMessage());
        }
    }

    private void openWindowSuministro() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_SUMINISTROS_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxSuministrosListaController controller = fXMLLoader.getController();
            controller.setInitGuiaRemisionController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Producto", spWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
            controller.fillSuministrosTable((short) 0, "");
        } catch (IOException ex) {
            System.out.println("openWindowArticulos():" + ex.getLocalizedMessage());
        }
    }

    private void openWindowVentas() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_VENTA_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxVentaListaController controller = fXMLLoader.getController();
            controller.setInitGuiaRemisionController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione una venta", spWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
            controller.loadInit();
        } catch (IOException ex) {
            System.out.println("openWindowArticulos():" + ex.getLocalizedMessage());
        }
    }

    private void onExecuteImpresion(String idGuiaRemision) {
        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<GuiaRemisionTB> task = new Task<GuiaRemisionTB>() {
            @Override
            public GuiaRemisionTB call() {
                return GuiaRemisionADO.CargarGuiaRemisionReporte(idGuiaRemision);
            }
        };
        task.setOnScheduled(w -> {
            Tools.showAlertNotification("/view/image/information_large.png",
                    "Generando reporte",
                    "Se envi?? los datos para generar\n el reporte.",
                    Duration.seconds(5),
                    Pos.BOTTOM_RIGHT);
        });
        task.setOnFailed(w -> {
            Tools.showAlertNotification("/view/image/warning_large.png",
                    "Generando reporte",
                    "Se produjo un problema al generar.",
                    Duration.seconds(10),
                    Pos.BOTTOM_RIGHT);
        });
        task.setOnSucceeded(w -> {
            GuiaRemisionTB remisionTB = task.getValue();
            if (remisionTB != null) {
                reportGuiaRemision(remisionTB);
                Tools.showAlertNotification("/view/image/succes_large.png",
                        "Generando reporte",
                        "Se genero correctamente el reporte.",
                        Duration.seconds(5),
                        Pos.BOTTOM_RIGHT);

            } else {
                Tools.println("dentroo..");
                Tools.showAlertNotification("/view/image/error_large.png",
                        "Generando reporte",
                        "Se produjo al obtenener los datos.",
                        Duration.seconds(10),
                        Pos.CENTER);
            }
        }
        );
        exec.execute(task);

        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void onExecuteGuardar() {
        if (cbDocumentoGuia.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(spWindow, "Gu??a de Remisi??n", "Seleccione el documento gu??a usar.");
            cbDocumentoGuia.requestFocus();
        } else if (cbCliente.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(spWindow, "Gu??a de Remisi??n", "Seleccione un cliente.");
            cbCliente.requestFocus();
        } else if (cbMotivoTraslado.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(spWindow, "Gu??a de Remisi??n", "Seleccione el motivo del traslado.");
            cbMotivoTraslado.requestFocus();
        } else if (cbModalidadTraslado.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(spWindow, "Gu??a de Remisi??n", "Seleccione la modalidad de traslado.");
            cbModalidadTraslado.requestFocus();
        } else if (dtFechaTraslado.getValue() == null) {
            Tools.AlertMessageWarning(spWindow, "Gu??a de Remisi??n", "Ingrese la fecha de traslado.");
            dtFechaTraslado.requestFocus();
        } else if (Tools.isText(txtPesoBruto.getText())) {
            Tools.AlertMessageWarning(spWindow, "Gu??a de Remisi??n", "Ingrese el peso de los bultos a trasaladar.");
            txtPesoBruto.requestFocus();
        } else if (Tools.isText(txtNumeroBultos.getText())) {
            Tools.AlertMessageWarning(spWindow, "Gu??a de Remisi??n", "Ingrese el n??mero de bultos a trasladar.");
            txtNumeroBultos.requestFocus();
        } else if (cbTipoDocumento.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(spWindow, "Gu??a de Remisi??n", "Seleccione el tipo de documento del conducto.");
            cbTipoDocumento.requestFocus();
        } else if (Tools.isText(txtNumeroDocumento.getText())) {
            Tools.AlertMessageWarning(spWindow, "Gu??a de Remisi??n", "Ingrese el n??mero de documento del conductor.");
            txtNumeroDocumento.requestFocus();
        } else if (Tools.isText(txtNombreConducto.getText())) {
            Tools.AlertMessageWarning(spWindow, "Gu??a de Remisi??n", "Ingrese los datos completos del conducto");
            txtNombreConducto.requestFocus();
        } else if (Tools.isText(txtTelefonoCelular.getText())) {
            Tools.AlertMessageWarning(spWindow, "Gu??a de Remisi??n", "Ingrese el tel??fono o celular del conducto");
            txtTelefonoCelular.requestFocus();
        } else if (Tools.isText(txtNumeroPlacaVehiculo.getText())) {
            Tools.AlertMessageWarning(spWindow, "Gu??a de Remisi??n", "Ingrese el n??mero de placa del veh??culo");
            txtNumeroPlacaVehiculo.requestFocus();
        } else if (Tools.isText(txtMarcaVehiculo.getText())) {
            Tools.AlertMessageWarning(spWindow, "Gu??a de Remisi??n", "Ingrese la marca del veh??culo.");
            txtMarcaVehiculo.requestFocus();
        } else if (Tools.isText(txtDireccionPartida.getText())) {
            Tools.AlertMessageWarning(spWindow, "Gu??a de Remisi??n", "Ingrese la direcci??n de partida.");
            txtDireccionPartida.requestFocus();
        } else if (cbUbigeoPartida.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(spWindow, "Gu??a de Remisi??n", "Seleccione el ubigeo de partida.");
            cbUbigeoPartida.requestFocus();
        } else if (Tools.isText(txtDireccionLlegada.getText())) {
            Tools.AlertMessageWarning(spWindow, "Gu??a de Remisi??n", "Ingrese la direcci??n de llegada.");
            txtDireccionLlegada.requestFocus();
        } else if (cbUbigeoLlegada.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(spWindow, "Gu??a de Remisi??n", "Seleccione el ubigeo de llegada.");
            cbUbigeoLlegada.requestFocus();
        } else if (tvList.getItems().isEmpty()) {
            Tools.AlertMessageWarning(spWindow, "Gu??a de Remisi??n", "Agregar productos a lista.");
            tvList.requestFocus();
        } else {

            short value = Tools.AlertMessageConfirmation(spWindow, "Guia Remisi??n", "??Est?? seguro de continuar?");
            if (value == 1) {
                GuiaRemisionTB guiaRemisionTB = new GuiaRemisionTB();
                guiaRemisionTB.setIdCliente(cbCliente.getSelectionModel().getSelectedItem().getIdCliente());
                guiaRemisionTB.setIdVendedor(Session.USER_ID);
                guiaRemisionTB.setIdComprobante(cbDocumentoGuia.getSelectionModel().getSelectedItem().getIdTipoDocumento());
                guiaRemisionTB.setEmail(txtEmail.getText().trim());
                guiaRemisionTB.setIdMotivoTraslado(cbMotivoTraslado.getSelectionModel().getSelectedItem().getIdDetalle());
                guiaRemisionTB.setIdModalidadTraslado(cbModalidadTraslado.getSelectionModel().getSelectedItem().getIdDetalle());
                guiaRemisionTB.setFechaTraslado(Tools.getDatePicker(dtFechaTraslado));
                guiaRemisionTB.setPesoBruto(Double.parseDouble(txtPesoBruto.getText()));
                guiaRemisionTB.setNumeroBultos(Integer.parseInt(txtNumeroBultos.getText()));
                guiaRemisionTB.setTipoDocumentoConducto(cbTipoDocumento.getSelectionModel().getSelectedItem().getIdDetalle());
                guiaRemisionTB.setNumeroConductor(txtNumeroDocumento.getText().trim());
                guiaRemisionTB.setNombreConductor(txtNombreConducto.getText().trim());
                guiaRemisionTB.setTelefonoCelularConducto(txtTelefonoCelular.getText());
                guiaRemisionTB.setNumeroPlaca(txtNumeroPlacaVehiculo.getText().trim());
                guiaRemisionTB.setMarcaVehiculo(txtMarcaVehiculo.getText().trim());
                guiaRemisionTB.setDireccionPartida(txtDireccionPartida.getText().trim());
                guiaRemisionTB.setIdUbigeoPartida(cbUbigeoPartida.getSelectionModel().getSelectedItem().getIdUbigeo());
                guiaRemisionTB.setDireccionLlegada(txtDireccionLlegada.getText().trim());
                guiaRemisionTB.setIdUbigeoLlegada(cbUbigeoLlegada.getSelectionModel().getSelectedItem().getIdUbigeo());
                guiaRemisionTB.setIdTipoComprobanteFactura(cbTipoComprobante.getSelectionModel().getSelectedIndex() >= 0 ? cbTipoComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento() : 0);
                guiaRemisionTB.setSerieFactura(txtSerieFactura.getText().trim());
                guiaRemisionTB.setNumeracionFactura(txtNumeracionFactura.getText().trim());
                guiaRemisionTB.setListGuiaRemisionDetalle(tvList.getItems());

                String[] result = GuiaRemisionADO.InsertarGuiaRemision(guiaRemisionTB).split("/");
                if (result[0].equalsIgnoreCase("register")) {
                    Tools.AlertMessageInformation(spWindow, "Guia Remisi??n", "Se registro correctamente la gu??a de remesi??n.");
                    reset();
                    onExecuteImpresion(result[1]);
                } else {
                    Tools.AlertMessageError(spWindow, "Guia Remisi??n", result[0]);
                }
            }

        }
    }

    private void reset() {
        cbCliente.getItems().clear();
        txtEmail.clear();
        cbMotivoTraslado.getSelectionModel().select(null);
        cbModalidadTraslado.getSelectionModel().select(null);
        Tools.actualDate(Tools.getDate(), dtFechaTraslado);
        txtPesoBruto.clear();
        txtNumeroBultos.clear();
        cbTipoDocumento.getSelectionModel().select(null);
        txtNumeroDocumento.clear();
        txtNombreConducto.clear();
        txtTelefonoCelular.clear();
        txtNumeroPlacaVehiculo.clear();
        txtMarcaVehiculo.clear();
        txtDireccionPartida.clear();
        txtDireccionPartida.setText(Session.COMPANY_DOMICILIO);
        cbUbigeoPartida.getSelectionModel().select(null);
        txtDireccionLlegada.clear();
        cbUbigeoLlegada.getSelectionModel().select(null);
        cbTipoComprobante.getSelectionModel().select(null);
        txtSerieFactura.clear();
        txtNumeracionFactura.clear();
        tvList.getItems().clear();
    }

    public void loadVentaById(String idVenta) {

        ExecutorService executor = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<Object> task = new Task<Object>() {
            @Override
            protected Object call() {
                return VentaADO.ListCompletaVentasDetalle(idVenta);
            }
        };
        task.setOnSucceeded(e -> {
            Object object = task.getValue();
            if (object instanceof VentaTB) {
                VentaTB ventaTB = (VentaTB) object;
                ArrayList<SuministroTB> empList = ventaTB.getSuministroTBs();

                for (int i = 0; i < cbTipoComprobante.getItems().size(); i++) {
                    if (cbTipoComprobante.getItems().get(i).getIdTipoDocumento() == ventaTB.getIdComprobante()) {
                        cbTipoComprobante.getSelectionModel().select(i);
                        break;
                    }
                }
                txtSerieFactura.setText(ventaTB.getSerie());
                txtNumeracionFactura.setText(ventaTB.getNumeracion());
                cbCliente.getItems().clear();
                cbCliente.getItems().add(new ClienteTB(
                        ventaTB.getClienteTB().getIdCliente(),
                        ventaTB.getClienteTB().getNumeroDocumento(),
                        ventaTB.getClienteTB().getInformacion(),
                        ventaTB.getClienteTB().getCelular(),
                        ventaTB.getClienteTB().getEmail(),
                        ventaTB.getClienteTB().getDireccion()));
                if (!cbCliente.getItems().isEmpty()) {
                    cbCliente.getSelectionModel().select(0);
                }

                tvList.getItems().clear();
                empList.forEach((suministro) -> {
                    eventAgregar(suministro);
                });
                txtNumeroBultos.setText("" + empList.size());
                lblLoad.setVisible(false);
            } else {
                lblLoad.setVisible(false);
            }
        });
        task.setOnScheduled(e -> {
            lblLoad.setVisible(true);
        });
        task.setOnFailed(e -> {
            lblLoad.setVisible(false);
        });
        executor.execute(task);
        if (!executor.isShutdown()) {
            executor.shutdown();
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
            onExecuteGuardar();
        }
    }

    @FXML
    private void onActionToRegister(ActionEvent event) {
        onExecuteGuardar();
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
    private void onKeyPressedToReload(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            loadComponents();
        }
    }

    @FXML
    private void onActionToReload(ActionEvent event) {
        loadComponents();
    }

    @FXML
    private void onKeyPressedAgregar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowSuministro();
        }
    }

    @FXML
    private void onActionAgregar(ActionEvent event) {
        openWindowSuministro();
    }

    @FXML
    private void onKeyPressedVentas(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowVentas();
        }
    }

    @FXML
    private void onActionVentas(ActionEvent event) {
        openWindowVentas();
    }

    public TableView<GuiaRemisionDetalleTB> getTvList() {
        return tvList;
    }

    public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }
}
