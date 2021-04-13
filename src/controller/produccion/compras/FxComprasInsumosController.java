package controller.produccion.compras;

import controller.contactos.proveedores.FxProveedorListaController;
import controller.menus.FxPrincipalController;
import controller.operaciones.compras.FxComprasProcesoController;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.SearchComboBox;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.CompraTB;
import model.DetalleCompraTB;
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.LoteTB;
import model.ProveedorADO;
import model.ProveedorTB;

public class FxComprasInsumosController implements Initializable {

    @FXML
    private ScrollPane spWindow;
    @FXML
    private TableView<DetalleCompraTB> tvList;
    @FXML
    private TableColumn<DetalleCompraTB, Button> tcAccion;
    @FXML
    private TableColumn<DetalleCompraTB, String> tcCantidad;
    @FXML
    private TableColumn<DetalleCompraTB, String> tcArticulo;
    @FXML
    private TableColumn<DetalleCompraTB, String> tcCosto;
    @FXML
    private TableColumn<DetalleCompraTB, String> tcDescuento;
    @FXML
    private TableColumn<DetalleCompraTB, String> tcImpuesto;
    @FXML
    private TableColumn<DetalleCompraTB, String> tcImporte;
    @FXML
    private ComboBox<ImpuestoTB> cbImpuestos;
    @FXML
    private Label lblTotalBruto;
    @FXML
    private Label lblDescuento;
    @FXML
    private Label lblSubTotal;
    @FXML
    private Label lblImpuesto;
    @FXML
    private Label lblTotalNeto;
    @FXML
    private ComboBox<ProveedorTB> cbProveedor;
    @FXML
    private DatePicker tpFechaCompra;
    @FXML
    private TextArea txtNotas;
    @FXML
    private TextArea txtObservaciones;
    @FXML
    private TextField cbSerie;
    @FXML
    private TextField cbNumeracion;
    @FXML
    private Button btnInsumos;
    @FXML
    private Button btnQuitar;

    private FxPrincipalController fxPrincipalController;

    private double totalBruto;

    private double descuentoTotal;

    private double subTotal;

    private double impuestoTotal;

    private double totalNeto;

    private boolean loadData;

    private ObservableList<LoteTB> loteTBs;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initTable();
        loteTBs = FXCollections.observableArrayList();
        Tools.actualDate(Tools.getDate(), tpFechaCompra);
    }

    private void initTable() {
        tcAccion.setCellValueFactory(new PropertyValueFactory<>("remove"));
        tcArticulo.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getDescripcion()));
        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getCantidad(), 2)));
        tcCosto.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getPrecioCompra(), 2)));
        tcDescuento.setCellValueFactory(cellData -> Bindings.concat(
                "- " + Tools.roundingValue(cellData.getValue().getDescuento(), 2)));
        tcImpuesto.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getNombreImpuesto()));
        tcImporte.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getPrecioCompra() * cellData.getValue().getCantidad(), 2)));

        tcAccion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.06));
        tcCantidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcArticulo.prefWidthProperty().bind(tvList.widthProperty().multiply(0.30));
        tcCosto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcDescuento.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcImpuesto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcImporte.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));

        cbImpuestos.getItems().clear();
        ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> {
           // cbImpuestos.getItems().add(new ImpuestoTB(e.getIdImpuesto(), e.getNombre(), e.getValor(), e.getPredeterminado()));
        });

        SearchComboBox<ProveedorTB> searchComboBox = new SearchComboBox<>(cbProveedor, false);

        searchComboBox.getSearchComboBoxSkin().getSearchBox().setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                if (!searchComboBox.getSearchComboBoxSkin().getItemView().getItems().isEmpty()) {
                    searchComboBox.getSearchComboBoxSkin().getItemView().getSelectionModel().select(0);
                    searchComboBox.getSearchComboBoxSkin().getItemView().requestFocus();
                }
            } else if (t.getCode() == KeyCode.ESCAPE) {
                searchComboBox.getComboBox().hide();
            }
        });
        searchComboBox.getSearchComboBoxSkin().getSearchBox().setOnKeyReleased(t -> {
            searchComboBox.getComboBox().getItems().clear();
            List<ProveedorTB> proveedorTBs = ProveedorADO.getSearchComboBoxProveedores(searchComboBox.getSearchComboBoxSkin().getSearchBox().getText().trim());
            proveedorTBs.forEach(p -> cbProveedor.getItems().add(p));
        });
        searchComboBox.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
            switch (t.getCode()) {
                case ENTER:
                case SPACE:
                case ESCAPE:
                    searchComboBox.getComboBox().hide();
                    break;
                case UP:
                case DOWN:
                case LEFT:
                case RIGHT:
                    break;
                default:
                    searchComboBox.getSearchComboBoxSkin().getSearchBox().requestFocus();
                    searchComboBox.getSearchComboBoxSkin().getSearchBox().selectAll();
                    break;
            }
        });
        searchComboBox.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                searchComboBox.getComboBox().getSelectionModel().select(item);
                if (searchComboBox.getSearchComboBoxSkin().isClickSelection()) {
                    searchComboBox.getComboBox().hide();
                }
            }
        });
    }

    private void openWindowProvedores() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_PROVEEDORES_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxProveedorListaController controller = fXMLLoader.getController();
            //controller.setInitComprasController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Proveedor", spWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.setOnShown(w -> {
                controller.initTable();
            });
            stage.show();

        } catch (IOException ex) {
            System.out.println("Controller compras" + ex.getLocalizedMessage());
        }
    }

    public void clearComponents() {
        cbSerie.clear();
        cbNumeracion.clear();
        tvList.getItems().clear();
        cbProveedor.getItems().clear();
        Tools.actualDate(Tools.getDate(), tpFechaCompra);
        lblTotalBruto.setText(Session.MONEDA_SIMBOLO + " " + "0.00");
        lblSubTotal.setText(Session.MONEDA_SIMBOLO + " " + "0.00");
        lblDescuento.setText(Session.MONEDA_SIMBOLO + " " + "0.00");
        lblImpuesto.setText(Session.MONEDA_SIMBOLO + " " + "0.00");
        lblTotalNeto.setText(Session.MONEDA_SIMBOLO + " " + "0.00");
        txtObservaciones.clear();
        txtNotas.clear();
        initTable();
        loteTBs.clear();
    }

    public void calculateTotals() {
        totalBruto = 0;
        tvList.getItems().forEach(e -> totalBruto += (e.getCantidad() * e.getPrecioCompraUnico()));
        lblTotalBruto.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(totalBruto, 2));

        descuentoTotal = 0;
        tvList.getItems().forEach(e -> descuentoTotal += e.getDescuentoSumado());
        lblDescuento.setText(Session.MONEDA_SIMBOLO + " " + (Tools.roundingValue(descuentoTotal * (-1), 2)));

        subTotal = 0;
        tvList.getItems().forEach(e -> subTotal += e.getPrecioCompraReal() * e.getCantidad());
        lblSubTotal.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(subTotal, 2));

        impuestoTotal = 0;
        tvList.getItems().forEach(e -> impuestoTotal += e.getImpuestoSumado());
        lblImpuesto.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(impuestoTotal, 2));

        totalNeto = 0;
        tvList.getItems().forEach(e -> totalNeto += e.getPrecioCompra() * e.getCantidad());
        lblTotalNeto.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue((totalNeto), 2));
    }

    private void openWindowRegister() {
        if (cbProveedor.getSelectionModel().getSelectedIndex() < 0) {
            openAlertMessageWarning("Seleccione un proveedor, por favor.");
            cbProveedor.requestFocus();
        } else if (cbSerie.getText().isEmpty()) {
            openAlertMessageWarning("Ingrese la serie del comprobante, por favor.");
            cbSerie.requestFocus();
        } else if (cbNumeracion.getText().isEmpty()) {
            openAlertMessageWarning("Ingrese la numeración del comprobante, por favor.");
            cbNumeracion.requestFocus();
        } else if (tpFechaCompra.getValue() == null) {
            openAlertMessageWarning("Ingrese la fecha de compra, por favor.");
            tpFechaCompra.requestFocus();
        } else if (tvList.getItems().isEmpty()) {
            openAlertMessageWarning("Ingrese algún producto para realizar la compra, por favor.");
            btnInsumos.requestFocus();
        } else {
            try {
                CompraTB compraTB = new CompraTB();
                compraTB.setIdProveedor(cbProveedor.getSelectionModel().getSelectedItem().getIdProveedor());
                compraTB.setSerie(cbSerie.getText().trim());
                compraTB.setNumeracion(cbNumeracion.getText().trim());
                compraTB.setIdMoneda(Session.MONEDA_ID);
                compraTB.setFechaCompra(Tools.getDatePicker(tpFechaCompra));
                compraTB.setHoraCompra(Tools.getHour());
                compraTB.setTotal(totalNeto);
                compraTB.setObservaciones(txtObservaciones.getText().trim().isEmpty() ? "" : txtObservaciones.getText().trim());
                compraTB.setNotas(txtNotas.getText().trim().isEmpty() ? "" : txtNotas.getText().trim());
                compraTB.setUsuario(Session.USER_ID);
                compraTB.setDestino("2");

                fxPrincipalController.openFondoModal();
                URL url = getClass().getResource(FilesRouters.FX_COMPRAS_INSUMOS_PROCESO);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
//              Controlller here
                FxComprasInsumosProcesoController controller = fXMLLoader.getController();
                controller.setInitComprasController(this);
                controller.setLoadProcess(compraTB, tvList, loteTBs);
//
                Stage stage = WindowStage.StageLoaderModal(parent, "Pago de la compra", spWindow.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
                stage.show();
            } catch (IOException ex) {
                System.out.println("Controller compras" + ex.getLocalizedMessage());
            }
        }
    }

    private void onViewRemove() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            ObservableList<DetalleCompraTB> observableList, detalleCompraTBs;
            observableList = tvList.getItems();
            detalleCompraTBs = tvList.getSelectionModel().getSelectedItems();
            detalleCompraTBs.forEach(e -> {
                for (int i = 0; i < loteTBs.size(); i++) {
                    if (loteTBs.get(i).getIdArticulo().equals(e.getInsumoTB().getIdInsumo())) {
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

    private void openAlertMessageWarning(String message) {
        fxPrincipalController.openFondoModal();
        Tools.AlertMessageWarning(spWindow, "Compras", message);
        fxPrincipalController.closeFondoModal();
    }

    private void sumarImpuestos() {
        if (cbImpuestos.getSelectionModel().getSelectedIndex() >= 0) {
            for (DetalleCompraTB dctb : tvList.getItems()) {
                dctb.setIdImpuesto(cbImpuestos.getSelectionModel().getSelectedItem().getIdImpuesto());
                dctb.setNombreImpuesto(cbImpuestos.getSelectionModel().getSelectedItem().getNombre());
                dctb.setValorImpuesto(cbImpuestos.getSelectionModel().getSelectedItem().getValor());

                dctb.setPrecioCompra(dctb.getPrecioCompra() + (dctb.getPrecioCompra() * (dctb.getValorImpuesto() / 100.00)));

                double costo = dctb.getPrecioCompra();
                double descuento = dctb.getDescuento();
                double preciocalculado = costo - descuento;
                double valor_sin_impuesto = preciocalculado / ((dctb.getValorImpuesto() / 100.00) + 1);

                dctb.setDescuento(descuento);
                dctb.setDescuentoSumado(descuento * dctb.getCantidad());

                dctb.setPrecioCompraUnico(costo);
                dctb.setPrecioCompraReal(valor_sin_impuesto);

                double impuesto = Tools.calculateTax(dctb.getValorImpuesto(), dctb.getPrecioCompraReal());
                dctb.setImpuestoSumado(dctb.getCantidad() * impuesto);
                dctb.setPrecioCompra(dctb.getPrecioCompraReal() + impuesto);
            }
            tvList.refresh();
            calculateTotals();
        } else {
            Tools.AlertMessageWarning(spWindow, "Compra", "Seleccione un impuesto");
            cbImpuestos.requestFocus();
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
    private void onMouseClickedList(MouseEvent event) {
        if (event.getClickCount() == 2) {
            if (!tvList.getItems().isEmpty()) {

            }
        }
    }

    @FXML
    private void onKeyPressedInsumos(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            WindowStage.openWindowInsumosAdd(fxPrincipalController, spWindow, this);
        }
    }

    @FXML
    private void onActionInsumos(ActionEvent event) {
        WindowStage.openWindowInsumosAdd(fxPrincipalController, spWindow, this);
    }

    @FXML
    private void onKeyPressedReacargar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            short value = Tools.AlertMessageConfirmation(spWindow, "Compras", "¿Está seguro de cancelar la venta?");
            if (value == 1) {
                clearComponents();
            }
        }
    }

    @FXML
    private void onActionRevargar(ActionEvent event) {
        short value = Tools.AlertMessageConfirmation(spWindow, "Compras", "¿Está seguro de cancelar la venta?");
        if (value == 1) {
            clearComponents();
        }
    }

    @FXML
    private void onKeyPressedProveedor(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowProvedores();
        }
    }

    @FXML
    private void onActionProveedor(ActionEvent event) {
        openWindowProvedores();
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
    private void onKeyPressedSumarImpuesto(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            sumarImpuestos();
        }
    }

    @FXML
    private void onActionSumarImpuesto(ActionEvent event) {
        sumarImpuestos();
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

    public TableView<DetalleCompraTB> getTvList() {
        return tvList;
    }

    public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }
}
