package controller.egresos.compras;

import controller.contactos.proveedores.FxProveedorListaController;
import controller.produccion.suministros.FxSuministrosCompraController;
import controller.produccion.suministros.FxSuministrosListaController;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.LoteTB;
import model.MonedaADO;
import model.MonedaTB;
import model.PrivilegioTB;
import model.ProveedorADO;
import model.SuministroTB;
import model.TipoDocumentoADO;
import model.TipoDocumentoTB;

public class FxComprasController implements Initializable {

    @FXML
    private ScrollPane window;
    @FXML
    private TextField txtProveedor;
    @FXML
    private ComboBox<TipoDocumentoTB> cbComprobante;
    @FXML
    private TextField cbNumeracion;
    @FXML
    private DatePicker tpFechaCompra;
    @FXML
    private TableView<SuministroTB> tvList;
    @FXML
    private TableColumn<SuministroTB, String> tcArticulo;
    @FXML
    private TableColumn<SuministroTB, String> tcCantidad;
    @FXML
    private TableColumn<SuministroTB, String> tcCosto;
    @FXML
    private TableColumn<SuministroTB, String> tcDescuento;
    //private TableColumn<SuministroTB, String> tcImpuesto;
    @FXML
    private TableColumn<SuministroTB, String> tcImporte;
    @FXML
    private Text lblSubTotal;
    @FXML
    private Text lblDescuento;
    @FXML
    private Text lblSubTotalNuevo;
    @FXML
    private Text lblTotal;
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
    private HBox hbAgregarImpuesto;
    @FXML
    private TextField txtObservaciones;
    @FXML
    private TextField txtNotas;
    @FXML
    private HBox hbBotones;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnQuitar;
    @FXML
    private Button btnProveedor;

    private AnchorPane vbPrincipal;

    private String idProveedor;

    private double subImporte;

    private double descuento;

    private double subTotalImporte;

    private double totalImporte;

    private ArrayList<ImpuestoTB> arrayArticulosImpuesto;

    private ObservableList<LoteTB> loteTBs;

    private String monedaSimbolo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        window.setOnKeyReleased((KeyEvent event) -> {

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
        TipoDocumentoADO.GetDocumentoCombBox().forEach(e -> {
            cbComprobante.getItems().add(new TipoDocumentoTB(e.getIdTipoDocumento(), e.getNombre(), e.isPredeterminado()));
        });
        if (!cbComprobante.getItems().isEmpty()) {
            for (int i = 0; i < cbComprobante.getItems().size(); i++) {
                if (cbComprobante.getItems().get(i).isPredeterminado() == true) {
                    cbComprobante.getSelectionModel().select(i);
                    Session.DEFAULT_COMPROBANTE = i;
                    break;
                }
            }
        }

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

        tcArticulo.prefWidthProperty().bind(tvList.widthProperty().multiply(0.38));
        tcCantidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcCosto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.16));
        tcDescuento.prefWidthProperty().bind(tvList.widthProperty().multiply(0.16));
        //tcImpuesto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcImporte.prefWidthProperty().bind(tvList.widthProperty().multiply(0.16));

    }

    public void loadPrivilegios(ObservableList<PrivilegioTB> privilegioTBs) {
        if (privilegioTBs.get(0).getIdPrivilegio() != 0 && !privilegioTBs.get(0).isEstado()) {
            hbBotones.getChildren().remove(btnRegistrar);
        }
        if (privilegioTBs.get(1).getIdPrivilegio() != 0 && !privilegioTBs.get(1).isEstado()) {
            hbBotones.getChildren().remove(btnArticulo);
        }
        if (privilegioTBs.get(2).getIdPrivilegio() != 0 && !privilegioTBs.get(2).isEstado()) {
            hbBotones.getChildren().remove(btnEditar);
        }
        if (privilegioTBs.get(3).getIdPrivilegio() != 0 && !privilegioTBs.get(3).isEstado()) {
            hbBotones.getChildren().remove(btnQuitar);
        }
        if (privilegioTBs.get(4).getIdPrivilegio() != 0 && !privilegioTBs.get(4).isEstado()) {
            hbBotones.getChildren().remove(btnProveedor);
        }
        if (privilegioTBs.get(5).getIdPrivilegio() != 0 && !privilegioTBs.get(5).isEstado()) {
            tpFechaCompra.setDisable(true);
        }
        if (privilegioTBs.get(6).getIdPrivilegio() != 0 && !privilegioTBs.get(6).isEstado()) {
            cbComprobante.setDisable(true);
        }
        if (privilegioTBs.get(7).getIdPrivilegio() != 0 && !privilegioTBs.get(7).isEstado()) {
            cbNumeracion.setDisable(true);
        }
        if (privilegioTBs.get(8).getIdPrivilegio() != 0 && !privilegioTBs.get(8).isEstado()) {
            cbMoneda.setDisable(true);
        }
        if (privilegioTBs.get(9).getIdPrivilegio() != 0 && !privilegioTBs.get(9).isEstado()) {
            txtObservaciones.setDisable(true);
        }
        if (privilegioTBs.get(10).getIdPrivilegio() != 0 && !privilegioTBs.get(10).isEstado()) {
            txtNotas.setDisable(true);
        }
    }

    private void initTable() {
        tcArticulo.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getClave() + "\n" + cellData.getValue().getNombreMarca()
        ));
        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getCantidad(), 2)));
        tcCosto.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getCostoCompraReal(), 2)));
        tcDescuento.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getDescuento(), 0) + "%"
        ));
        //tcImpuesto.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getImpuestoArticuloName()));
        tcImporte.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getTotalImporte(), 2)));

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
        lblSubTotal.setText("0.00");
        lblDescuento.setText("0.00");
        lblSubTotalNuevo.setText("0.00");
        lblTotal.setText("0.00");
        txtObservaciones.clear();
        txtNotas.clear();
        hbAgregarImpuesto.getChildren().clear();
    }

    private void openAlertMessageWarning(String message) {
        ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Compras", message, false);
        vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
    }

    private void openWindowRegister() {
        try {
            if (txtProveedor.getText().isEmpty() && idProveedor.equalsIgnoreCase("")) {
                openAlertMessageWarning("Ingrese un proveedor, por favor.");
                txtProveedor.requestFocus();
            } else if (cbComprobante.getSelectionModel().getSelectedIndex() < 0) {
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
                compraTB.setComprobante(cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento());
                compraTB.setNumeracion(cbNumeracion.getText().trim());
                compraTB.setTipoMoneda(cbMoneda.getSelectionModel().getSelectedIndex() >= 1
                        ? cbMoneda.getSelectionModel().getSelectedItem().getIdMoneda() : 0);
                compraTB.setFecha(Tools.getDatePicker(tpFechaCompra));
                compraTB.setHora(Tools.getHour());
                compraTB.setSubTotal(Double.parseDouble(lblSubTotal.getText()));
                compraTB.setDescuento(Double.parseDouble(lblDescuento.getText()));
                compraTB.setTotal(Double.parseDouble(lblTotal.getText()));
                compraTB.setObservaciones(txtObservaciones.getText().isEmpty() ? "" : txtObservaciones.getText());
                compraTB.setNotas(txtNotas.getText().isEmpty() ? "" : txtNotas.getText());
                compraTB.setUsuario(Session.USER_ID);

                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                URL url = getClass().getResource(FilesRouters.FX_COMPRAS_PROCESO);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
//            Controlller here
                FxComprasProcesoController controller = fXMLLoader.getController();
                controller.setInitComprasController(this);
                controller.setLoadProcess(compraTB, tvList, loteTBs, txtProveedor.getText(), monedaSimbolo);

                Stage stage = WindowStage.StageLoaderModal(parent, "Pago de la compra", window.getScene().getWindow());
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
            controller.setInitComprasController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Suministros", window.getScene().getWindow());
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
            ObservableList<SuministroTB> suministroTBs;
            suministroTBs = tvList.getSelectionModel().getSelectedItems();
            suministroTBs.forEach(e -> {
                try {
                    URL url = getClass().getResource(FilesRouters.FX_SUMINISTROS_COMPRA);
                    FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                    Parent parent = fXMLLoader.load(url.openStream());
                    //Controlller here
                    FxSuministrosCompraController controller = fXMLLoader.getController();
                    controller.setInitComprasController(this);
                    //
                    Stage stage = WindowStage.StageLoaderModal(parent, "Editar suministro", window.getScene().getWindow());
                    stage.setResizable(false);
                    stage.show();

                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setIdSuministro(e.getIdSuministro());
                    suministroTB.setClave(e.getClave());
                    suministroTB.setNombreMarca(e.getNombreMarca());
                    suministroTB.setCantidad(e.getCantidad());
                    suministroTB.setCostoCompra(e.getCostoCompra());
                    suministroTB.setCostoCompraReal(e.getCostoCompraReal());

                    suministroTB.setPrecioVentaGeneral(e.getPrecioVentaGeneral());
                    suministroTB.setPrecioMargenGeneral(e.getPrecioMargenGeneral());
                    suministroTB.setPrecioUtilidadGeneral(e.getPrecioUtilidadGeneral());

                    suministroTB.setDescuento(e.getDescuento());
                    suministroTB.setTotalImporte(e.getTotalImporte());
                    suministroTB.setImpuestoArticulo(e.getImpuestoArticulo());
                    suministroTB.setLote(e.isLote());
                    suministroTB.setUnidadVenta(e.getUnidadVenta());
                    suministroTB.setDescripcion(e.getDescripcion());
                    controller.setLoadEdit(suministroTB, tvList.getSelectionModel().getSelectedIndex(), loteTBs);
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
            short confirmation = Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Compras", "¿Esta seguro de quitar el artículo?", true);
            if (confirmation == 1) {
                ObservableList<SuministroTB> observableList, suministroTBs;
                observableList = tvList.getItems();
                suministroTBs = tvList.getSelectionModel().getSelectedItems();
                suministroTBs.forEach(e -> {
                    for (int i = 0; i < loteTBs.size(); i++) {
                        if (loteTBs.get(i).getIdArticulo().equals(e.getIdSuministro())) {
                            loteTBs.remove(i);
                            i--;
                        }
                    }
                    observableList.remove(e);
                });
                calculateTotals();
            }
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
            controller.setInitComprasController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Proveedor", window.getScene().getWindow());
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

        tvList.getItems().forEach(e -> subImporte += e.getSubImporte());
        lblSubTotal.setText(Tools.roundingValue(subImporte, 2));
        subImporte = 0;

        tvList.getItems().forEach(e -> descuento += e.getDescuentoSumado());
        lblDescuento.setText((Tools.roundingValue(descuento * (-1), 2)));
        descuento = 0;

        tvList.getItems().forEach(e -> subTotalImporte += e.getTotalImporte());
        lblSubTotalNuevo.setText(Tools.roundingValue(subTotalImporte, 2));
        subTotalImporte = 0;

//        hbAgregarImpuesto.getChildren().clear();
//        boolean addElement = false;
//        double sumaElement = 0;
//        double totalImpuestos = 0;
//        for (int k = 0; k < arrayArticulosImpuesto.size(); k++) {
//            for (int i = 0; i < tvList.getItems().size(); i++) {
//                if (arrayArticulosImpuesto.get(k).getIdImpuesto() == tvList.getItems().get(i).getImpuestoArticulo()) {
//                    addElement = true;
//                    sumaElement += tvList.getItems().get(i).getImpuestoSumado();
//                }
//            }
//            if (addElement) {
//                addElementImpuesto(arrayArticulosImpuesto.get(k).getIdImpuesto() + "", arrayArticulosImpuesto.get(k).getNombre(), monedaSimbolo, Tools.roundingValue(sumaElement, 2));
//                addElement = false;
//                totalImpuestos += sumaElement;
//                sumaElement = 0;
//            }
//
//        }
        tvList.getItems().forEach(e -> totalImporte += e.getTotalImporte());
//        lblTotal.setText(Tools.roundingValue((totalImporte + totalImpuestos), 2));
        lblTotal.setText(Tools.roundingValue((totalImporte), 2));
        totalImporte = 0;
    }

    private void addElementImpuesto(String id, String titulo, String moneda, String total) {
        Label label = new Label(titulo);
        label.setStyle("-fx-text-fill:#1a2226;");
        label.getStyleClass().add("labelRoboto14");

        Text text = new Text(moneda);
        text.getStyleClass().add("labelRobotoMedium16");
        Text text1 = new Text(total);
        text1.getStyleClass().add("labelRobotoMedium16");

        HBox hBox = new HBox(text, text1);
        hBox.setSpacing(5);
        VBox vBox = new VBox(label, hBox);
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

    public TableView<SuministroTB> getTvList() {
        return tvList;
    }

    public ObservableList<LoteTB> getLoteTBs() {
        return loteTBs;
    }

    public HBox getHbAgregarImpuesto() {
        return hbAgregarImpuesto;
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
