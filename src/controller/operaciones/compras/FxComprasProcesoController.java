package controller.operaciones.compras;

import controller.consultas.compras.FxComprasEditarController;
import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.BancoADO;
import model.BancoHistorialTB;
import model.BancoTB;
import model.CompraADO;
import model.CompraCreditoTB;
import model.CompraTB;
import model.DetalleCompraTB;
import model.LoteTB;

public class FxComprasProcesoController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private Text lblTotal;
    @FXML
    private TextField txtEfectivo;
    @FXML
    private HBox hbPagoContado;
    @FXML
    private VBox vbPagoCredito;
    @FXML
    private RadioButton rbContado;
    @FXML
    private RadioButton rbCredito;
    @FXML
    private ComboBox<BancoTB> cbCuentas;
    @FXML
    private TableView<CompraCreditoTB> tvListaCredito;
    @FXML
    private TableColumn<CompraCreditoTB, TextField> tcCredito;
    @FXML
    private TableColumn<CompraCreditoTB, DatePicker> tcFecha;
    @FXML
    private TableColumn<CompraCreditoTB, Button> tcOpciones;
    @FXML
    private Label lblMontoTotal;
    @FXML
    private Label lblMontoPagar;

    private FxComprasController comprasController;

    private FxComprasEditarController comprasEditarController;

    private CompraTB compraTB = null;

    private TableView<DetalleCompraTB> tvList;

    private ObservableList<LoteTB> loteTBs;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
        ToggleGroup group = new ToggleGroup();
        rbContado.setToggleGroup(group);
        rbCredito.setToggleGroup(group);

        tcCredito.setCellValueFactory(new PropertyValueFactory<>("txtCredito"));
        tcFecha.setCellValueFactory(new PropertyValueFactory<>("dpFecha"));
        tcOpciones.setCellValueFactory(new PropertyValueFactory<>("btnRemove"));

        setInitializeCaja();
    }

    private void setInitializeCaja() {
        cbCuentas.getItems().clear();
        cbCuentas.getItems().add(new BancoTB("0", "Seleccione una cuenta..."));
        cbCuentas.getItems().addAll(BancoADO.GetBancoComboBox());
        cbCuentas.getSelectionModel().select(0);
    }

    public void setLoadProcess(CompraTB compraTB, TableView<DetalleCompraTB> tvList, ObservableList<LoteTB> loteTBs, String simboloMoneda) {
        this.compraTB = compraTB;
        this.tvList = tvList;
        this.loteTBs = loteTBs;
        lblTotal.setText(simboloMoneda + " " + Tools.roundingValue(compraTB.getTotal(), 2));
        lblMontoTotal.setText(Tools.roundingValue(compraTB.getTotal(), 2));
        txtEfectivo.setText(Tools.roundingValue(compraTB.getTotal(), 2));
    }

    private void onEventProcess() {
        if (rbContado.isSelected()) {
            if (cbCuentas.getSelectionModel().getSelectedIndex() <= 0) {
                Tools.AlertMessageWarning(apWindow, "Compra", "Seleccione una cuenta.");
                cbCuentas.requestFocus();
            } else if (!Tools.isNumeric(txtEfectivo.getText())) {
                Tools.AlertMessageWarning(apWindow, "Compra", "Ingrese el valor recibido.");
                txtEfectivo.requestFocus();
            } else {
                compraTB.setTipo(1);
                compraTB.setEstado(1);
                compraTB.setFechaVencimiento(compraTB.getFechaCompra());
                compraTB.setHoraVencimiento(compraTB.getHoraCompra());

                BancoHistorialTB bancoHistorialTB = new BancoHistorialTB();
                bancoHistorialTB.setIdBanco(cbCuentas.getSelectionModel().getSelectedItem().getIdBanco());
                bancoHistorialTB.setDescripcion("Salida de dinero por compra");
                bancoHistorialTB.setFecha(Tools.getDate());
                bancoHistorialTB.setHora(Tools.getHour());
                bancoHistorialTB.setEntrada(0);
                bancoHistorialTB.setSalida(Double.parseDouble(txtEfectivo.getText()));

                String result = CompraADO.Compra_Contado(bancoHistorialTB, compraTB, tvList, loteTBs);
                if (result.equalsIgnoreCase("register")) {
                    Tools.AlertMessageInformation(apWindow, "Compra", "Se registró correctamente la compra.");
                    Tools.Dispose(apWindow);
                    if (comprasController != null) {
                        comprasController.clearComponents();
                    } else if (comprasEditarController != null) {
                        comprasEditarController.disposeWindow();
                    }
                } else {
                    Tools.AlertMessageError(apWindow, "Compra", result);
                }
            }
        } else if (rbCredito.isSelected()) {
            int validateMonto = 0;
            int validateFecha = 0;
            for (CompraCreditoTB creditoTB : tvListaCredito.getItems()) {
                validateMonto += !Tools.isNumeric(creditoTB.getTxtCredito().getText()) ? 1 : 0;
                validateFecha += creditoTB.getDpFecha().getValue() == null ? 1 : 0;
            }

            if (validateMonto > 0) {
                Tools.AlertMessageWarning(apWindow, "Compra", "Hay montos sin ingresar en la tabla.");
                tvListaCredito.requestFocus();
            } else if (validateFecha > 0) {
                Tools.AlertMessageWarning(apWindow, "Compra", "Hay fechas sin ingresar en la tabla.");
                tvListaCredito.requestFocus();
            } else if (Double.parseDouble(lblMontoTotal.getText()) != Double.parseDouble(lblMontoPagar.getText())) {
                Tools.AlertMessageWarning(apWindow, "Compra", "El monto total y el monto a pagar no son iquales.");
                tvListaCredito.requestFocus();
            } else {
                compraTB.setTipo(2);
                compraTB.setEstado(2);
                compraTB.setFechaVencimiento(tvListaCredito.getItems().get(tvListaCredito.getItems().size() - 1).getFechaRegistro());
                compraTB.setHoraVencimiento(compraTB.getHoraCompra());

                String result = CompraADO.Compra_Credito(compraTB, tvList, loteTBs, tvListaCredito);
                if (result.equalsIgnoreCase("register")) {
                    Tools.AlertMessageInformation(apWindow, "Compra", "Se registró correctamente la compra.");
                    Tools.Dispose(apWindow);
                    if (comprasController != null) {
                        comprasController.clearComponents();
                    } else if (comprasEditarController != null) {
                        comprasEditarController.disposeWindow();
                    }
                } else {
                    Tools.AlertMessageError(apWindow, "Compra", result);
                }

            }
        }
    }

    public void openWindowAddPlazo() throws IOException {
        URL url = getClass().getResource(FilesRouters.FX_PLAZOS);
        FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
        Parent parent = fXMLLoader.load(url.openStream());
        //Controlller here
        FxPlazosController controller = fXMLLoader.getController();
        controller.setInitCompraVentaProcesoController(this, null);
        //
        Stage stage = WindowStage.StageLoaderModal(parent, "Agegar nuevo plazo", apWindow.getScene().getWindow());
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    private void eventAgregarCredito() {
        CompraCreditoTB compraCreditoTB = new CompraCreditoTB();
        compraCreditoTB.setHoraRegistro(Tools.getHour());
        compraCreditoTB.setEstado(false);

        TextField txtCredito = new TextField("0.00");
        txtCredito.getStyleClass().add("text-field-normal");
        txtCredito.focusedProperty().addListener((obs, oldVal, newVal) -> {
            double sumaMontos = 0;
            if (!newVal) {
                if (!Tools.isNumeric(txtCredito.getText())) {
                    txtCredito.setText("0.00");
                    compraCreditoTB.setMonto(Double.parseDouble(txtCredito.getText()));
                }
                sumaMontos = tvListaCredito.getItems().stream().map(
                        (cctb) -> Double.parseDouble(cctb.getTxtCredito().getText()))
                        .reduce(sumaMontos, (accumulator, _item) -> accumulator + _item);
                lblMontoPagar.setText(Tools.roundingValue(sumaMontos, 2));
            }
        });
        txtCredito.setOnKeyReleased(event -> {
            if (Tools.isNumeric(txtCredito.getText())) {
                compraCreditoTB.setMonto(Double.parseDouble(txtCredito.getText()));
            }
        });
        txtCredito.setOnKeyTyped(event -> {
            char c = event.getCharacter().charAt(0);
            if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
                event.consume();
            }
            if (c == '.' && txtCredito.getText().contains(".")) {
                event.consume();
            }
        });
        compraCreditoTB.setTxtCredito(txtCredito);

        DatePicker dpFecha = new DatePicker();
        dpFecha.setEditable(false);
        dpFecha.setOnAction(event -> {
            if (dpFecha.getValue() != null) {
                compraCreditoTB.setFechaRegistro(dpFecha.getValue().toString());
            }
        });
        compraCreditoTB.setDpFecha(dpFecha);

        Button btnRemover = new Button();
        btnRemover.getStyleClass().add("buttonBorder");
        ImageView view = new ImageView(new Image("/view/image/remove-black.png"));
        view.setFitWidth(24);
        view.setFitHeight(24);
        btnRemover.setGraphic(view);
        btnRemover.setOnAction(event -> {
            tvListaCredito.getItems().remove(compraCreditoTB);
        });
        btnRemover.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                tvListaCredito.getItems().remove(compraCreditoTB);
            }
        });
        compraCreditoTB.setBtnRemove(btnRemover);

        tvListaCredito.getItems().add(compraCreditoTB);
    }

    @FXML
    private void onKeyTypedEfectivo(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtEfectivo.getText().contains(".") || c == '-' && txtEfectivo.getText().contains("-")) {
            event.consume();
        }
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventProcess();
        }
    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        onEventProcess();
    }

    @FXML
    private void onKeyPressedCancelar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(apWindow);
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        Tools.Dispose(apWindow);
    }

    @FXML
    private void onActionRbContado(ActionEvent event) {
        vbPagoCredito.setDisable(true);
        hbPagoContado.setDisable(false);
    }

    @FXML
    private void onActionRbCredito(ActionEvent event) {
        hbPagoContado.setDisable(true);
        vbPagoCredito.setDisable(false);
    }

    @FXML
    private void onKeyPressedCredito(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            eventAgregarCredito();
        }
    }

    @FXML
    private void oActionAgregarCredito(ActionEvent event) {
        eventAgregarCredito();
    }

    public void setInitComprasController(FxComprasController comprasController) {
        this.comprasController = comprasController;
    }

    public void setInitComprasEditarController(FxComprasEditarController comprasEditarController) {
        this.comprasEditarController = comprasEditarController;
    }

}
