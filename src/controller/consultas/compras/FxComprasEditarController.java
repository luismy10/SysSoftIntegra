package controller.consultas.compras;

import controller.contactos.proveedores.FxProveedorListaController;
import controller.inventario.suministros.FxSuministrosCompraController;
import controller.inventario.suministros.FxSuministrosListaController;
import controller.operaciones.compras.FxComprasProcesoController;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.CompraTB;
import model.DetalleCompraTB;
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.LoteTB;
import model.MonedaADO;
import model.MonedaTB;
import model.ProveedorADO;
import model.SuministroTB;
import model.TipoDocumentoADO;
import model.TipoDocumentoTB;

public class FxComprasEditarController implements Initializable {

    @FXML
    private ScrollPane spWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private TextField txtProveedor;
    @FXML
    private ComboBox<TipoDocumentoTB> cbComprobante;
    @FXML
    private TextField cbNumeracion;
    @FXML
    private DatePicker tpFechaCompra;
    @FXML
    private TableView<DetalleCompraTB> tvList;
    @FXML
    private TableColumn<DetalleCompraTB, String> tcItem;
    @FXML
    private TableColumn<DetalleCompraTB, String> tcArticulo;
    @FXML
    private TableColumn<DetalleCompraTB, String> tcCantidad;
    @FXML
    private TableColumn<DetalleCompraTB, String> tcCosto;
    @FXML
    private TableColumn<DetalleCompraTB, String> tcDescuento;
    @FXML
    private TableColumn<DetalleCompraTB, String> tcImpuesto;
    @FXML
    private TableColumn<DetalleCompraTB, String> tcImporte;
    @FXML
    private Text lblSubTotal;
    @FXML
    private Text lblTotalBruto;
    @FXML
    private Text lblDescuento;
    @FXML
    private Text lblTotalNeto;
    @FXML
    private Text lblMonedaSubTotal;
    @FXML
    private Text lblMonedaDescuento;
    @FXML
    private Text lblMonedaSubTotalNuevo;
    @FXML
    private Text lblMonedaTotal;
    @FXML
    private Button btnArticulo;
    @FXML
    private ComboBox<MonedaTB> cbMoneda;
    @FXML
    private VBox hbAgregarImpuesto;
    @FXML
    private TextArea txtObservaciones;
    @FXML
    private TextArea txtNotas;

    private FxComprasDetalleController comprasDetalleController;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    private String idProveedor;

    private double totalBruto;

    private double descuento;

    private double subTotal;

    private double totalNeto;

    private ArrayList<ImpuestoTB> arrayArticulosImpuesto;

    private ObservableList<LoteTB> loteTBs;

    private String monedaSimbolo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        spWindow.setOnKeyReleased((KeyEvent event) -> {

            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case F1:
                        openWindowRegister();
                        break;
                    case F2:
                        openWindowSuministrasAdd();
                        break;
                    case F3:
                        openWindowArticulosEdit();
                        break;
                    case F4:
                        onViewRemove();
                        break;
                    case F5:
                        openWindowProvedores();
                        break;
                }
            }
        });
        idProveedor = "";
        monedaSimbolo = "M";
        loteTBs = FXCollections.observableArrayList();
        Tools.actualDate(Tools.getDate(), tpFechaCompra);

        arrayArticulosImpuesto = new ArrayList<>();
        ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> {
            arrayArticulosImpuesto.add(new ImpuestoTB(e.getIdImpuesto(), e.getNombreImpuesto(), e.getValor(), e.getPredeterminado()));
        });

        cbComprobante.getItems().clear();
        cbComprobante.getItems().add(new TipoDocumentoTB(0, "Seleccione un tipo de documento", false));
        TipoDocumentoADO.GetDocumentoCombBox().forEach(e -> {
            cbComprobante.getItems().add(new TipoDocumentoTB(e.getIdTipoDocumento(), e.getNombre(), e.isPredeterminado()));
        });
        cbComprobante.getSelectionModel().select(0);

        cbMoneda.getItems().clear();
        cbMoneda.getItems().add(new MonedaTB(0, "Seleccione una moneda", "", false));
        MonedaADO.GetMonedasCombBox().forEach(e -> {
            cbMoneda.getItems().add(new MonedaTB(e.getIdMoneda(), e.getNombre(), e.getSimbolo(), e.getPredeterminado()));
        });

        if (!cbMoneda.getItems().isEmpty()) {
            for (int i = 0; i < cbMoneda.getItems().size(); i++) {
                if (cbMoneda.getItems().get(i).getPredeterminado() == true) {
                    cbMoneda.getSelectionModel().select(i);
                    monedaSimbolo = cbMoneda.getItems().get(i).getSimbolo();
                    break;
                }
            }
        }

        lblMonedaSubTotal.setText(monedaSimbolo);
        lblMonedaDescuento.setText(monedaSimbolo);
        lblMonedaSubTotalNuevo.setText(monedaSimbolo);
        lblMonedaTotal.setText(monedaSimbolo);

        initTable();

        tcItem.prefWidthProperty().bind(tvList.widthProperty().multiply(0.06));
        tcArticulo.prefWidthProperty().bind(tvList.widthProperty().multiply(0.30));
        tcCantidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcCosto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcDescuento.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcImpuesto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcImporte.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));

    }

    private void initTable() {
        tcItem.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcArticulo.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getSuministroTB().getClave() + "\n" + cellData.getValue().getSuministroTB().getNombreMarca()
        ));
        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getCantidad(), 2)));
        tcCosto.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getPrecioCompra(), 4)));
        tcDescuento.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getPrecioCompraCalculado(), 4) + "(" + Tools.roundingValue(cellData.getValue().getDescuento(), 0) + "%)"
        ));
        tcImpuesto.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getNombreImpuesto()));
        tcImporte.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getImporte(), 4)));

    }

    public void setInitComprasEditar(String idCompra) {
        ExecutorService executor = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        
        
    }

    public void setInitComprasValue(String... value) {
        idProveedor = ProveedorADO.GetProveedorId(value[0]);
        txtProveedor.setText(value[1]);
    }

    public void clearComponents() {
        idProveedor = "";
        txtProveedor.clear();
        cbNumeracion.clear();
        Tools.actualDate(Tools.getDate(), tpFechaCompra);
        tvList.getItems().clear();
        loteTBs.clear();
        initTable();
        lblTotalBruto.setText("0.00");
        lblSubTotal.setText("0.00");
        lblDescuento.setText("0.00");
        lblTotalNeto.setText("0.00");
        txtObservaciones.clear();
        txtNotas.clear();
        hbAgregarImpuesto.getChildren().clear();
    }

    private void openAlertMessageWarning(String message) {
        ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
        Tools.AlertMessageWarning(spWindow, "Compras", message);
        vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
    }

    private void openWindowRegister() {
        try {
            if (txtProveedor.getText().isEmpty() && idProveedor.equalsIgnoreCase("")) {
                openAlertMessageWarning("Ingrese un proveedor, por favor.");
                txtProveedor.requestFocus();
            } else if (cbComprobante.getSelectionModel().getSelectedIndex() <= 0) {
                openAlertMessageWarning("Seleccione tipo de comprobante, por favor.");
                cbComprobante.requestFocus();
            } else if (cbNumeracion.getText().isEmpty()) {
                openAlertMessageWarning("Ingrese la numeración del comprobante, por favor.");
                cbNumeracion.requestFocus();
            } else if (tpFechaCompra.getValue() == null) {
                openAlertMessageWarning("Ingrese la fecha de compra, por favor.");
                tpFechaCompra.requestFocus();
            } else if (tvList.getItems().isEmpty()) {
                openAlertMessageWarning("Ingrese algún producto para realizar la compra, por favor.");
                btnArticulo.requestFocus();
            } else {
                CompraTB compraTB = new CompraTB();
                compraTB.setProveedor(idProveedor);
//                compraTB.setComprobante(cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento());
                compraTB.setNumeracion(cbNumeracion.getText().trim());
                compraTB.setTipoMoneda(cbMoneda.getSelectionModel().getSelectedIndex() >= 1
                        ? cbMoneda.getSelectionModel().getSelectedItem().getIdMoneda() : 0);
                compraTB.setFechaCompra(Tools.getDatePicker(tpFechaCompra));
                compraTB.setHoraCompra(Tools.getHour());
                compraTB.setSubTotal(Double.parseDouble(lblSubTotal.getText()));
                compraTB.setDescuento(Double.parseDouble(lblDescuento.getText()));
                compraTB.setTotal(Double.parseDouble(lblTotalNeto.getText()));
                compraTB.setObservaciones(txtObservaciones.getText().trim().isEmpty() ? "" : txtObservaciones.getText().trim());
                compraTB.setNotas(txtNotas.getText().trim().isEmpty() ? "" : txtNotas.getText().trim());
                compraTB.setUsuario(Session.USER_ID);

                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                URL url = getClass().getResource(FilesRouters.FX_COMPRAS_PROCESO);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
//              Controlller here
                FxComprasProcesoController controller = fXMLLoader.getController();
                controller.setInitComprasEditarController(this);
                controller.setLoadProcess(compraTB, tvList, loteTBs);
//
                Stage stage = WindowStage.StageLoaderModal(parent, "Pago de la compra", spWindow.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.setOnHiding((w) -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
                stage.show();

            }
        } catch (IOException ex) {
            System.out.println("Controller compras" + ex.getLocalizedMessage());
        }
    }

    private void openWindowSuministrasAdd() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_SUMINISTROS_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxSuministrosListaController controller = fXMLLoader.getController();
            controller.setInitComprasEditarController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Suministros", spWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding((w) -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();
            controller.fillSuministrosTablePaginacion();
        } catch (IOException ex) {
            System.out.println("Controller compras" + ex.getLocalizedMessage());
        }

    }

    private void openWindowArticulosEdit() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            ObservableList<DetalleCompraTB> detalleCompraTBs;
            detalleCompraTBs = tvList.getSelectionModel().getSelectedItems();
            detalleCompraTBs.forEach(e -> {
                try {
                    URL url = getClass().getResource(FilesRouters.FX_SUMINISTROS_COMPRA);
                    FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                    Parent parent = fXMLLoader.load(url.openStream());
                    //Controlller here
                    FxSuministrosCompraController controller = fXMLLoader.getController();
                    controller.setInitComprasEditarController(this);
                    //
                    Stage stage = WindowStage.StageLoaderModal(parent, "Editar suministro", spWindow.getScene().getWindow());
                    stage.setResizable(false);
                    stage.show();

                    DetalleCompraTB detalleCompraTB = new DetalleCompraTB();
                    detalleCompraTB.setIdArticulo(e.getIdArticulo());
                    //
                    SuministroTB suministrosTB = new SuministroTB();
                    suministrosTB.setClave(e.getSuministroTB().getClave());
                    suministrosTB.setNombreMarca(e.getSuministroTB().getNombreMarca());
                    detalleCompraTB.setSuministroTB(suministrosTB);
                    detalleCompraTB.setCantidad(e.getCantidad());
                    detalleCompraTB.setPrecioCompra(e.getPrecioCompra());
                    detalleCompraTB.setDescuento(e.getDescuento());
                    detalleCompraTB.setIdImpuesto(e.getIdImpuesto());
                    detalleCompraTB.setDescripcion(e.getDescripcion());
                    controller.setLoadEdit(detalleCompraTB, tvList.getSelectionModel().getSelectedIndex(), loteTBs);
                } catch (IOException ex) {
                    System.out.println(ex.getLocalizedMessage());
                }
            });
        } else {
            openAlertMessageWarning("Seleccione un producto para editarlo.");
        }
    }

    private void onViewRemove() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            ObservableList<DetalleCompraTB> observableList, detalleCompraTBs;
            observableList = tvList.getItems();
            detalleCompraTBs = tvList.getSelectionModel().getSelectedItems();
            detalleCompraTBs.forEach(e -> {
                for (int i = 0; i < loteTBs.size(); i++) {
                    if (loteTBs.get(i).getIdArticulo().equals(e.getSuministroTB().getIdSuministro())) {
                        loteTBs.remove(i);
                        i--;
                    }
                }
                observableList.remove(e);
            });
            for (int i = 0; i < tvList.getItems().size(); i++) {
                tvList.getItems().get(i).setId(i + 1);
            }
            tvList.refresh();
            calculateTotals();
        } else {
            openAlertMessageWarning("Seleccione un producto para removerlo.");
        }
    }

    private void openWindowProvedores() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_PROVEEDORES_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxProveedorListaController controller = fXMLLoader.getController();
            controller.setInitComprasEditarController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Proveedor", spWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();
            controller.fillCustomersTable("");
        } catch (IOException ex) {
            System.out.println("Controller compras" + ex.getLocalizedMessage());
        }
    }

    public void calculateTotals() {

        tvList.getItems().forEach(e -> {
            totalBruto += (e.getCantidad() * e.getPrecioCompra());
        });
        lblTotalBruto.setText(Tools.roundingValue(totalBruto, 4));
        totalBruto = 0;

        tvList.getItems().forEach(e -> {
            descuento += e.getCantidad() * (e.getPrecioCompra() * (e.getDescuento() / 100.00));
        });
        lblDescuento.setText((Tools.roundingValue(descuento * (-1), 4)));
        descuento = 0;

        tvList.getItems().forEach(e -> {
            subTotal += (e.getCantidad() * e.getPrecioCompra()) - (e.getCantidad() * (e.getPrecioCompra() * (e.getDescuento() / 100.00)));
        });
        lblSubTotal.setText(Tools.roundingValue(subTotal, 4));
        subTotal = 0;

        hbAgregarImpuesto.getChildren().clear();
        boolean addElement = false;
        double sumaElement = 0;

        for (int k = 0; k < arrayArticulosImpuesto.size(); k++) {
            for (int i = 0; i < tvList.getItems().size(); i++) {
                if (arrayArticulosImpuesto.get(k).getIdImpuesto() == tvList.getItems().get(i).getIdImpuesto()) {
                    addElement = true;
                    sumaElement += tvList.getItems().get(i).getImpuestoSumado();
                }
            }
            if (addElement) {
                addElementImpuesto(arrayArticulosImpuesto.get(k).getIdImpuesto() + "", arrayArticulosImpuesto.get(k).getNombreImpuesto(), monedaSimbolo, Tools.roundingValue(sumaElement, 4));
                addElement = false;
                sumaElement = 0;
            }

        }

        tvList.getItems().forEach(e -> {
            totalNeto += e.getImporte();
        });
        lblTotalNeto.setText(Tools.roundingValue((totalNeto), 4));
        totalNeto = 0;
    }

    private void addElementImpuesto(String id, String titulo, String moneda, String total) {
        Label label = new Label(titulo);
        label.setStyle("-fx-text-fill:#1a2226;");
        label.getStyleClass().add("labelRoboto14");

        Label text = new Label(moneda);
        text.setStyle("-fx-text-fill:#1a2226;");
        text.getStyleClass().add("labelRobotoMedium16");
        Label text1 = new Label(total);
        text1.setStyle("-fx-text-fill:#1a2226;");
        text1.getStyleClass().add("labelRobotoMedium16");

        HBox hBox = new HBox(text, text1);
        hBox.setStyle("-fx-spacing: 0.8333333333333334em;");
        HBox vBox = new HBox(label, hBox);
        vBox.setStyle("-fx-spacing: 0.8333333333333334em;");
        vBox.setId(id);

        hbAgregarImpuesto.getChildren().add(vBox);
    }

    @FXML
    private void onKeyPressedRegister(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowRegister();
        }
    }

    @FXML
    private void onActionRegister(ActionEvent event) {
        openWindowRegister();
    }

    @FXML
    private void onKeyPressedAdd(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowSuministrasAdd();
        }
    }

    @FXML
    private void onActionAdd(ActionEvent event) {
        openWindowSuministrasAdd();
    }

    @FXML
    private void onKeyPressedEdit(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowArticulosEdit();
        }
    }

    @FXML
    private void onActionEdit(ActionEvent event) {
        openWindowArticulosEdit();
    }

    @FXML
    private void onKeyPressedRemover(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onViewRemove();
        }
    }

    @FXML
    private void onActionRemover(ActionEvent event) {
        onViewRemove();
    }

    @FXML
    private void onKeyPressedProviders(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowProvedores();
        }
    }

    @FXML
    private void onActionProviders(ActionEvent event) {
        openWindowProvedores();
    }

    @FXML
    private void onKeyTypedNumeracion(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c != '-')
                && (c != '/') && (c != '+') && (c != '*') && (c != '(') && (c != ')') && (c != '|')) {
            event.consume();
        }
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) {
        if (event.getClickCount() == 2) {
            openWindowArticulosEdit();
        }
    }

    @FXML
    private void onActionMoneda(ActionEvent event) {
        if (cbMoneda.getSelectionModel().getSelectedIndex() >= 1) {
            monedaSimbolo = cbMoneda.getSelectionModel().getSelectedItem().getSimbolo();
            lblMonedaSubTotal.setText(monedaSimbolo);
            lblMonedaSubTotalNuevo.setText(monedaSimbolo);
            lblMonedaDescuento.setText(monedaSimbolo);
            lblMonedaTotal.setText(monedaSimbolo);
            calculateTotals();
        }
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        vbContent.getChildren().remove(spWindow);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(comprasDetalleController.getCpWindow(), 0d);
        AnchorPane.setTopAnchor(comprasDetalleController.getCpWindow(), 0d);
        AnchorPane.setRightAnchor(comprasDetalleController.getCpWindow(), 0d);
        AnchorPane.setBottomAnchor(comprasDetalleController.getCpWindow(), 0d);
        vbContent.getChildren().add(comprasDetalleController.getCpWindow());
    }

    public TableView<DetalleCompraTB> getTvList() {
        return tvList;
    }

    public ObservableList<LoteTB> getLoteTBs() {
        return loteTBs;
    }

    public VBox getHbAgregarImpuesto() {
        return hbAgregarImpuesto;
    }

    public void setInitContentComprasEditar(FxComprasDetalleController comprasDetalleController, AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.comprasDetalleController = comprasDetalleController;
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
