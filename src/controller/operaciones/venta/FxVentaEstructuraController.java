package controller.operaciones.venta;

import controller.inventario.suministros.FxSuministrosListaController;
import controller.tools.ApiPeru;
import controller.tools.BillPrintable;
import controller.tools.ConvertMonedaCadena;
import controller.tools.FilesRouters;
import controller.tools.Json;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.awt.print.Book;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;
import model.ClienteADO;
import model.ClienteTB;
import model.ComprobanteADO;
import model.DetalleADO;
import model.DetalleTB;
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.MonedaADO;
import model.MonedaTB;
import model.PrivilegioTB;
import model.SuministroADO;
import model.SuministroTB;
import model.TipoDocumentoADO;
import model.TipoDocumentoTB;
import model.VentaADO;
import model.VentaTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import org.json.simple.JSONObject;

public class FxVentaEstructuraController implements Initializable {

    @FXML
    private VBox window;
    @FXML
    private HBox hbBotonesSuperior;
    @FXML
    private Button btnCobrar;
    @FXML
    private Button btnArticulo;
    @FXML
    private Button btnListaPrecio;
    @FXML
    private Button btnCantidad;
    @FXML
    private Button btnCambiarPrecio;
    @FXML
    private Button btnDescuento;
    @FXML
    private Button btnSumarPrecio;
    @FXML
    private TextField txtSearch;
    @FXML
    private TableView<SuministroTB> tvList;
    @FXML
    private TableColumn<SuministroTB, Button> tcOpcion;
    @FXML
    private TableColumn<SuministroTB, String> tcCantidad;
    @FXML
    private TableColumn<SuministroTB, String> tcArticulo;
    @FXML
    private TableColumn<SuministroTB, String> tcImpuesto;
    @FXML
    private TableColumn<SuministroTB, String> tcPrecio;
    @FXML
    private TableColumn<SuministroTB, String> tcDescuento;
    @FXML
    private TableColumn<SuministroTB, String> tcImporte;
    @FXML
    private Text lblTotal;
    @FXML
    private Text lblSerie;
    @FXML
    private Text lblNumeracion;
    @FXML
    private ComboBox<MonedaTB> cbMoneda;
    @FXML
    private ComboBox<TipoDocumentoTB> cbComprobante;
    @FXML
    private TextField txtNumeroDocumento;
    @FXML
    private TextField txtDatosCliente;
    @FXML
    private TextField txtDireccionCliente;
    @FXML
    private TextField txtCelularCliente;
    @FXML
    private Label lblValorVenta;
    @FXML
    private Label lblDescuento;
    @FXML
    private Label lblSubImporte;
    @FXML
    private Label lblImporteTotal;
    @FXML
    private Label lblTotalPagar;
    @FXML
    private Button btnMovimiento;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnVentasPorDia;
    @FXML
    private GridPane gpTotales;
    @FXML
    private Button btnBuscarCliente;
    @FXML
    private ComboBox<DetalleTB> cbTipoDocumento;
    @FXML
    private Button btnBuscarSunat;
    @FXML
    private Button btnBuscarReniec;

    private AnchorPane vbPrincipal;

    private String monedaSimbolo;

    private String idCliente;

    private ArrayList<ImpuestoTB> arrayArticulosImpuesto;

    private BillPrintable billPrintable;

    private ConvertMonedaCadena monedaCadena;

    private AnchorPane hbEncabezado;

    private AnchorPane hbDetalleCabecera;

    private AnchorPane hbPie;

    private boolean unidades_cambio_cantidades;

    private boolean unidades_cambio_precio;

    private boolean unidades_cambio_descuento;

    private boolean valormonetario_cambio_cantidades;

    private boolean valormonetario_cambio_precio;

    private boolean valormonetario_cambio_decuento;

    private boolean medida_cambio_cantidades;

    private boolean medida_cambio_precio;

    private boolean medida_cambio_decuento;

    private boolean stateSearch;

    private double subTotal;

    private double descuento;

    private double subTotalImporte;

    private double totalImporte;

    private double totalImpuesto;

    private double total;
    @FXML
    private TextField txtCorreoElectronico;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        hbEncabezado = new AnchorPane();
        hbDetalleCabecera = new AnchorPane();
        hbPie = new AnchorPane();

        window.setOnKeyReleased((KeyEvent event) -> {
            if (null != event.getCode()) {
                if (event.getCode() == KeyCode.F5) {
                    openWindowCambiarPrecio("Cambiar precio al Artículo", false);
                    event.consume();
                } else if (event.getCode() == KeyCode.F6) {
                    openWindowDescuento();
                    event.consume();
                } else if (event.getCode() == KeyCode.F7) {
                    openWindowCambiarPrecio("Sumar precio al Artículo", true);
                    event.consume();
                } else if (event.getCode() == KeyCode.F1) {
                    openWindowVentaProceso();
                    event.consume();
                } else if (event.getCode() == KeyCode.F2) {
                    openWindowArticulos();
                    event.consume();
                } else if (event.getCode() == KeyCode.F3) {
                    openWindowListaPrecios();
                    event.consume();
                } else if (event.getCode() == KeyCode.F8) {
                    openWindowCashMovement();
                    event.consume();
                } else if (event.getCode() == KeyCode.F9) {
//                    imprimirVenta("LISTA DE PEDIDO", "000000000", "00", "00", false);
                    event.consume();
                } else if (event.getCode() == KeyCode.F10) {
                    short value = Tools.AlertMessageConfirmation(window, "Venta", "¿Está seguro de limpiar la venta?");
                    if (value == 1) {
                        resetVenta();
                        event.consume();
                    } else {
                        event.consume();
                    }
                } else if (event.getCode() == KeyCode.F11) {
                    openWindowMostrarVentas();
                    event.consume();
                } else if (event.getCode() == KeyCode.DELETE) {
                    removeArticulo();
                    event.consume();
                } else if (event.isControlDown() && event.getCode() == KeyCode.D) {
                    txtSearch.requestFocus();
                    txtSearch.selectAll();
                    event.consume();
                } else if (event.isControlDown() && event.getCode() == KeyCode.W) {
                    tvList.requestFocus();
                    if (!tvList.getItems().isEmpty()) {
                        tvList.getSelectionModel().select(0);
                    }
                    event.consume();
                } else if (event.getCode() == KeyCode.F4) {
                    openWindowCantidad();
                    event.consume();
                }

            }
        });
        billPrintable = new BillPrintable();
        monedaCadena = new ConvertMonedaCadena();
        arrayArticulosImpuesto = new ArrayList<>();
        monedaSimbolo = "M";
        idCliente = "";
        stateSearch = false;
        initTable();
        loadDataComponent();
    }

    private void loadDataComponent() {
        cbComprobante.getItems().clear();
        TipoDocumentoADO.GetDocumentoCombBox().forEach(cbComprobante.getItems()::add);
        if (!cbComprobante.getItems().isEmpty()) {
            for (int i = 0; i < cbComprobante.getItems().size(); i++) {
                if (cbComprobante.getItems().get(i).isPredeterminado()) {
                    cbComprobante.getSelectionModel().select(i);
                    break;
                }
            }

            if (cbComprobante.getSelectionModel().getSelectedIndex() >= 0) {
                String[] array = ComprobanteADO.GetSerieNumeracionEspecifico(cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento()).split("-");
                lblSerie.setText(array[0]);
                lblNumeracion.setText(array[1]);
            }
        }
        cbMoneda.getItems().clear();
        MonedaADO.GetMonedasCombBox().forEach(e -> cbMoneda.getItems().add(new MonedaTB(e.getIdMoneda(), e.getNombre(), e.getSimbolo(), e.getPredeterminado())));

        if (!cbMoneda.getItems().isEmpty()) {
            for (int i = 0; i < cbMoneda.getItems().size(); i++) {
                if (cbMoneda.getItems().get(i).getPredeterminado()) {
                    cbMoneda.getSelectionModel().select(i);
                    monedaSimbolo = cbMoneda.getItems().get(i).getSimbolo();
                    break;
                }
            }
        }

        arrayArticulosImpuesto.clear();
        ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> arrayArticulosImpuesto.add(new ImpuestoTB(e.getIdImpuesto(), e.getNombre(), e.getValor(), e.getPredeterminado())));

        cbTipoDocumento.getItems().clear();
        DetalleADO.GetDetailId("0003").forEach(e -> cbTipoDocumento.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre())));

        idCliente = Session.CLIENTE_ID;
        txtNumeroDocumento.setText(Session.CLIENTE_NUMERO_DOCUMENTO);
        txtDatosCliente.setText(Session.CLIENTE_DATOS);
        txtDireccionCliente.setText(Session.CLIENTE_DIRECCION);
        txtCelularCliente.setText("");

        if (!cbTipoDocumento.getItems().isEmpty()) {
            for (DetalleTB detalleTB : cbTipoDocumento.getItems()) {
                if (detalleTB.getIdDetalle().get() == Session.CLIENTE_TIPO_DOCUMENTO) {
                    cbTipoDocumento.getSelectionModel().select(detalleTB);
                    break;
                }
            }
        }

    }

    private void initTable() {
        tcOpcion.setCellValueFactory(new PropertyValueFactory<>("remover"));
        tcArticulo.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getClave() + "\n" + cellData.getValue().getNombreMarca()));
        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getCantidad(), 2)));
        tcPrecio.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getPrecioVentaGeneral(), 2)));
        tcDescuento.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getDescuento(), 0) + "%"));
        tcImpuesto.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getImpuestoArticuloName()));
        tcImporte.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getPrecioVentaGeneral() * cellData.getValue().getCantidad(), 2)));

    }

    public void loadPrivilegios(ObservableList<PrivilegioTB> privilegioTBs) {

        if (privilegioTBs.get(1).getIdPrivilegio() != 0 && !privilegioTBs.get(1).isEstado()) {
            hbBotonesSuperior.getChildren().remove(btnCobrar);
        }
        if (privilegioTBs.get(2).getIdPrivilegio() != 0 && !privilegioTBs.get(2).isEstado()) {
            hbBotonesSuperior.getChildren().remove(btnArticulo);
        }
        if (privilegioTBs.get(3).getIdPrivilegio() != 0 && !privilegioTBs.get(3).isEstado()) {
            hbBotonesSuperior.getChildren().remove(btnListaPrecio);
        }
        if (privilegioTBs.get(4).getIdPrivilegio() != 0 && !privilegioTBs.get(4).isEstado()) {
            hbBotonesSuperior.getChildren().remove(btnCantidad);
        }
        if (privilegioTBs.get(5).getIdPrivilegio() != 0 && !privilegioTBs.get(5).isEstado()) {
            hbBotonesSuperior.getChildren().remove(btnCambiarPrecio);
        }
        if (privilegioTBs.get(6).getIdPrivilegio() != 0 && !privilegioTBs.get(6).isEstado()) {
            hbBotonesSuperior.getChildren().remove(btnDescuento);
        }
        if (privilegioTBs.get(7).getIdPrivilegio() != 0 && !privilegioTBs.get(7).isEstado()) {
            hbBotonesSuperior.getChildren().remove(btnSumarPrecio);
        }
        if (privilegioTBs.get(8).getIdPrivilegio() != 0 && !privilegioTBs.get(8).isEstado()) {
//            hbBotonesInferior.getChildren().remove(btnQuitar);
        }
        if (privilegioTBs.get(9).getIdPrivilegio() != 0 && !privilegioTBs.get(9).isEstado()) {
            hbBotonesSuperior.getChildren().remove(btnMovimiento);
        }
        if (privilegioTBs.get(10).getIdPrivilegio() != 0 && !privilegioTBs.get(10).isEstado()) {
//            hbBotonesInferior.getChildren().remove(btnImpresora);
        }
        if (privilegioTBs.get(11).getIdPrivilegio() != 0 && !privilegioTBs.get(11).isEstado()) {
            hbBotonesSuperior.getChildren().remove(btnCancelar);
        }
        if (privilegioTBs.get(12).getIdPrivilegio() != 0 && !privilegioTBs.get(12).isEstado()) {
            // btnCliente.setDisable(true);
        }
        if (privilegioTBs.get(13).getIdPrivilegio() != 0 && !privilegioTBs.get(13).isEstado()) {
            cbComprobante.setDisable(true);
        }
        if (privilegioTBs.get(14).getIdPrivilegio() != 0 && !privilegioTBs.get(14).isEstado()) {
            cbMoneda.setDisable(true);
        }
        if (privilegioTBs.get(15).getIdPrivilegio() != 0 && !privilegioTBs.get(15).isEstado()) {
            //hbBotonesInferior.getChildren().remove(btnVentasPorDia);
        }
        if (privilegioTBs.get(16).getIdPrivilegio() != 0 && !privilegioTBs.get(16).isEstado()) {

        } else {
            tcCantidad.setCellFactory(TextFieldTableCell.forTableColumn());

            tcCantidad.setOnEditCommit(data -> {

                final Double value = Tools.isNumeric(data.getNewValue())
                        ? (Double.parseDouble(data.getNewValue()) <= 0 ? Double.parseDouble(data.getOldValue()) : Double.parseDouble(data.getNewValue()))
                        : Double.parseDouble(data.getOldValue());
                SuministroTB suministroTB = data.getTableView().getItems().get(data.getTablePosition().getRow());

                if (unidades_cambio_cantidades && suministroTB.getUnidadVenta() == 1
                        || valormonetario_cambio_cantidades && suministroTB.getUnidadVenta() == 2
                        || medida_cambio_cantidades && suministroTB.getUnidadVenta() == 3) {

                    suministroTB.setCantidad(value);

                    double porcentajeRestante = suministroTB.getPrecioVentaGeneralUnico() * (suministroTB.getDescuento() / 100.00);

                    suministroTB.setDescuentoCalculado(porcentajeRestante);
                    suministroTB.setDescuentoSumado(porcentajeRestante * suministroTB.getCantidad());

                    double impuesto = Tools.calculateTax(suministroTB.getImpuestoValor(), suministroTB.getPrecioVentaGeneralReal());
                    suministroTB.setImpuestoSumado(suministroTB.getCantidad() * impuesto);

                    suministroTB.setSubImporte(suministroTB.getPrecioVentaGeneralUnico() * suministroTB.getCantidad());
                    suministroTB.setSubImporteDescuento(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
                    suministroTB.setTotalImporte(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());

                    tvList.refresh();
                    calculateTotales();
                } else {
                    suministroTB.setCantidad(Double.parseDouble(data.getOldValue()));
                    tvList.refresh();
                    calculateTotales();
                }
            });
        }
        if (privilegioTBs.get(17).getIdPrivilegio() != 0 && !privilegioTBs.get(17).isEstado()) {

        } else {
            tcPrecio.setCellFactory(TextFieldTableCell.forTableColumn());

            tcPrecio.setOnEditCommit(data -> {
                final Double value = Tools.isNumeric(data.getNewValue())
                        ? (Double.parseDouble(data.getNewValue()) <= 0 ? Double.parseDouble(data.getOldValue()) : Double.parseDouble(data.getNewValue()))
                        : Double.parseDouble(data.getOldValue());
                SuministroTB suministroTB = data.getTableView().getItems().get(data.getTablePosition().getRow());

                if (!unidades_cambio_precio && suministroTB.getUnidadVenta() == 1
                        || !valormonetario_cambio_precio && suministroTB.getUnidadVenta() == 2
                        || !medida_cambio_precio && suministroTB.getUnidadVenta() == 3) {
                    tvList.refresh();
                    calculateTotales();
                } else {
                    double porcentajeRestante = value * (suministroTB.getDescuento() / 100.00);
                    double preciocalculado = value - porcentajeRestante;

                    suministroTB.setDescuentoCalculado(porcentajeRestante);
                    suministroTB.setDescuentoSumado(porcentajeRestante * suministroTB.getCantidad());

                    suministroTB.setPrecioVentaGeneralUnico(value);
                    suministroTB.setPrecioVentaGeneralReal(preciocalculado);

                    double impuesto = Tools.calculateTax(suministroTB.getImpuestoValor(), suministroTB.getPrecioVentaGeneralReal());

                    suministroTB.setImpuestoSumado(suministroTB.getCantidad() * impuesto);
                    suministroTB.setPrecioVentaGeneral(suministroTB.getPrecioVentaGeneralReal() + impuesto);

                    suministroTB.setSubImporte(suministroTB.getPrecioVentaGeneralUnico() * suministroTB.getCantidad());
                    suministroTB.setSubImporteDescuento(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
                    suministroTB.setTotalImporte(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());

                    tvList.refresh();
                    calculateTotales();
                }
            });
        }
        unidades_cambio_cantidades = !(privilegioTBs.get(18).getIdPrivilegio() != 0 && !privilegioTBs.get(18).isEstado());
        unidades_cambio_precio = !(privilegioTBs.get(19).getIdPrivilegio() != 0 && !privilegioTBs.get(19).isEstado());
        unidades_cambio_descuento = !(privilegioTBs.get(20).getIdPrivilegio() != 0 && !privilegioTBs.get(20).isEstado());

        valormonetario_cambio_cantidades = !(privilegioTBs.get(21).getIdPrivilegio() != 0 && !privilegioTBs.get(21).isEstado());
        valormonetario_cambio_precio = !(privilegioTBs.get(22).getIdPrivilegio() != 0 && !privilegioTBs.get(22).isEstado());
        valormonetario_cambio_decuento = !(privilegioTBs.get(23).getIdPrivilegio() != 0 && !privilegioTBs.get(23).isEstado());

        medida_cambio_cantidades = !(privilegioTBs.get(24).getIdPrivilegio() != 0 && !privilegioTBs.get(24).isEstado());
        medida_cambio_precio = !(privilegioTBs.get(25).getIdPrivilegio() != 0 && !privilegioTBs.get(25).isEstado());
        medida_cambio_decuento = !(privilegioTBs.get(26).getIdPrivilegio() != 0 && !privilegioTBs.get(26).isEstado());

        tcOpcion.setVisible(!(privilegioTBs.get(27).getIdPrivilegio() != 0 && !privilegioTBs.get(27).isEstado()));
        tcCantidad.setVisible(!(privilegioTBs.get(28).getIdPrivilegio() != 0 && !privilegioTBs.get(28).isEstado()));
        tcArticulo.setVisible(!(privilegioTBs.get(29).getIdPrivilegio() != 0 && !privilegioTBs.get(29).isEstado()));
        tcImpuesto.setVisible(!(privilegioTBs.get(30).getIdPrivilegio() != 0 && !privilegioTBs.get(30).isEstado()));
        tcPrecio.setVisible(!(privilegioTBs.get(31).getIdPrivilegio() != 0 && !privilegioTBs.get(31).isEstado()));
        tcDescuento.setVisible(!(privilegioTBs.get(32).getIdPrivilegio() != 0 && !privilegioTBs.get(32).isEstado()));
        tcImporte.setVisible(!(privilegioTBs.get(33).getIdPrivilegio() != 0 && !privilegioTBs.get(33).isEstado()));

        tcOpcion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.08));
        tcArticulo.prefWidthProperty().bind(tvList.widthProperty().multiply(0.30));
        tcCantidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcPrecio.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcDescuento.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcImpuesto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcImporte.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
    }

    private void filterSuministro(String search) {

        SuministroTB a = SuministroADO.Get_Suministro_By_Search(search);
        if (a != null) {
            SuministroTB suministroTB = new SuministroTB();
            suministroTB.setIdSuministro(a.getIdSuministro());
            suministroTB.setClave(a.getClave());
            suministroTB.setNombreMarca(a.getNombreMarca());
            suministroTB.setCantidad(1);
            suministroTB.setCostoCompra(a.getCostoCompra());

            suministroTB.setDescuento(0);
            suministroTB.setDescuentoCalculado(0);
            suministroTB.setDescuentoSumado(0);

            suministroTB.setPrecioVentaGeneralUnico(a.getPrecioVentaGeneral());
            suministroTB.setPrecioVentaGeneralReal(a.getPrecioVentaGeneral());
            suministroTB.setPrecioVentaGeneralAuxiliar(suministroTB.getPrecioVentaGeneralReal());

            suministroTB.setImpuestoOperacion(a.getImpuestoOperacion());
            suministroTB.setImpuestoArticulo(a.getImpuestoArticulo());
            suministroTB.setImpuestoArticuloName(getTaxName(a.getImpuestoArticulo()));
            suministroTB.setImpuestoValor(getTaxValue(a.getImpuestoArticulo()));
            suministroTB.setImpuestoSumado(suministroTB.getCantidad() * Tools.calculateTax(suministroTB.getImpuestoValor(), suministroTB.getPrecioVentaGeneralReal()));

            suministroTB.setPrecioVentaGeneral(suministroTB.getPrecioVentaGeneralReal() + suministroTB.getImpuestoSumado());

            suministroTB.setSubImporte(suministroTB.getPrecioVentaGeneralUnico() * suministroTB.getCantidad());
            suministroTB.setSubImporteDescuento(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
            suministroTB.setTotalImporte(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());

            suministroTB.setInventario(a.isInventario());
            suministroTB.setUnidadVenta(a.getUnidadVenta());
            suministroTB.setValorInventario(a.getValorInventario());

            Button button = new Button("X");
            button.getStyleClass().add("buttonDark");
            button.setOnAction(b -> {
                tvList.getItems().remove(suministroTB);
                calculateTotales();
            });
            button.setOnKeyPressed(b -> {
                if (b.getCode() == KeyCode.ENTER) {
                    tvList.getItems().remove(suministroTB);
                    calculateTotales();
                }
            });
            suministroTB.setRemover(button);

            getAddArticulo(suministroTB);
            txtSearch.clear();
            txtSearch.requestFocus();

        }

    }

    private void openWindowArticulos() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_SUMINISTROS_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxSuministrosListaController controller = fXMLLoader.getController();
            controller.setInitVentaEstructuraController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Producto", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
            controller.fillSuministrosTable((short) 0, "");
        } catch (IOException ex) {
            System.out.println("openWindowArticulos():" + ex.getLocalizedMessage());
        }
    }

    private void openWindowVentaProceso() {
        try {
            if (tvList.getItems().isEmpty()) {
                Tools.AlertMessageWarning(window, "Ventas", "Debes agregar artículos a la venta");
            } else if (txtNumeroDocumento.getText().trim().equalsIgnoreCase("")) {
                Tools.AlertMessageWarning(window, "Ventas", "Ingrese el número del documento del cliente.");
                txtNumeroDocumento.requestFocus();
            } else if (txtDatosCliente.getText().trim().equalsIgnoreCase("")) {
                Tools.AlertMessageWarning(window, "Ventas", "Ingrese los datos del cliente.");
                txtDatosCliente.requestFocus();
            } else if (cbComprobante.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(window, "Ventas", "Seleccione el tipo de documento");
                cbComprobante.requestFocus();
            } else if (cbMoneda.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(window, "Ventas", "Seleccione un moneda.");
                cbMoneda.requestFocus();
            } else if (total <= 0) {
                Tools.AlertMessageWarning(window, "Ventas", "El total de la venta no puede ser menor que 0.");
            } else {
                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                URL url = getClass().getResource(FilesRouters.FX_VENTA_PROCESO);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxVentaProcesoController controller = fXMLLoader.getController();
                controller.setInitVentaEstructuraController(this);
                //
                Stage stage = WindowStage.StageLoaderModal(parent, "Completar la venta", window.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
                stage.show();

                ClienteTB clienteTB = new ClienteTB();
                clienteTB.setIdCliente(idCliente);
                clienteTB.setTipoDocumento(cbTipoDocumento.getSelectionModel().getSelectedItem().getIdDetalle().get());
                clienteTB.setNumeroDocumento(txtNumeroDocumento.getText().trim().toUpperCase());
                clienteTB.setInformacion(txtDatosCliente.getText().trim().toUpperCase());
                clienteTB.setCelular(txtCelularCliente.getText().trim().toUpperCase());
                clienteTB.setEmail(txtCorreoElectronico.getText().trim().toUpperCase());
                clienteTB.setDireccion(txtDireccionCliente.getText().trim().toUpperCase());
                

                VentaTB ventaTB = new VentaTB();
                ventaTB.setVendedor(Session.USER_ID);
                ventaTB.setComprobante(cbComprobante.getSelectionModel().getSelectedIndex() >= 0
                        ? cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento()
                        : 0);
                ventaTB.setComprobanteName(cbComprobante.getSelectionModel().getSelectedIndex() >= 0
                        ? cbComprobante.getSelectionModel().getSelectedItem().getNombre()
                        : "");
                ventaTB.setMoneda(cbMoneda.getSelectionModel().getSelectedIndex() >= 0 ? cbMoneda.getSelectionModel().getSelectedItem().getIdMoneda() : 0);
                ventaTB.setMonedaName(monedaSimbolo);
                ventaTB.setSerie(lblSerie.getText());
                ventaTB.setNumeracion(lblNumeracion.getText());
                ventaTB.setFechaVenta(Tools.getDate());
                ventaTB.setHoraVenta(Tools.getHour());
                ventaTB.setSubTotal(subTotal);
                ventaTB.setDescuento(descuento);
                ventaTB.setSubImporte(subTotalImporte);
                ventaTB.setImpuesto(totalImpuesto);
                ventaTB.setTotal(total);
                ventaTB.setClienteTB(clienteTB);
                ArrayList<SuministroTB> suministroTBs = new ArrayList<>(tvList.getItems());
                controller.setInitComponents(ventaTB, suministroTBs);
            }
        } catch (IOException ex) {
            System.out.println("openWindowVentaProceso():" + ex.getLocalizedMessage());
        }
    }

    private void openWindowDescuento() {
        try {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                if (!unidades_cambio_descuento && tvList.getSelectionModel().getSelectedItem().getValorInventario() == 1
                        || !valormonetario_cambio_decuento && tvList.getSelectionModel().getSelectedItem().getValorInventario() == 2
                        || !medida_cambio_decuento && tvList.getSelectionModel().getSelectedItem().getValorInventario() == 3) {
                    Tools.AlertMessageWarning(window, "Ventas", "No se puede aplicar descuento a este producto.");
                } else {
                    ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                    URL url = getClass().getResource(FilesRouters.FX_VENTA_DESCUENTO);
                    FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                    Parent parent = fXMLLoader.load(url.openStream());
                    //Controlller here
                    FxVentaDescuentoController controller = fXMLLoader.getController();
                    controller.setInitVentaEstructuraController(this);
                    //
                    Stage stage = WindowStage.StageLoaderModal(parent, "Descuento del Artículo", window.getScene().getWindow());
                    stage.setResizable(false);
                    stage.sizeToScene();
                    stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
                    stage.show();
                    controller.initComponents(tvList.getSelectionModel().getSelectedItem());
                }
            } else {
                tvList.requestFocus();
            }
        } catch (IOException ex) {
            System.out.println("openWindowDescuento():" + ex.getLocalizedMessage());
        }
    }

    private void openWindowCambiarPrecio(String title, boolean opcion) {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            if (!unidades_cambio_precio && tvList.getSelectionModel().getSelectedItem().getValorInventario() == 1
                    || !valormonetario_cambio_precio && tvList.getSelectionModel().getSelectedItem().getValorInventario() == 2
                    || !medida_cambio_precio && tvList.getSelectionModel().getSelectedItem().getValorInventario() == 3) {
                Tools.AlertMessageWarning(window, "Ventas", "No se puede cambiar precio a este producto.");
            } else {
                try {
                    ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                    URL url = getClass().getResource(FilesRouters.FX_VENTA_GRANEL);
                    FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                    Parent parent = fXMLLoader.load(url.openStream());
                    //Controlller here
                    FxVentaGranelController controller = fXMLLoader.getController();
                    controller.setInitVentaEstructuraController(this);
                    //
                    Stage stage = WindowStage.StageLoaderModal(parent, title, window.getScene().getWindow());
                    stage.setResizable(false);
                    stage.sizeToScene();
                    stage.setOnHiding(w -> {
                        vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                        calculateTotales();
                    });
                    stage.show();
                    controller.initComponents(title, tvList.getSelectionModel().getSelectedItem(), opcion);
                } catch (IOException ie) {
                    System.out.println("openWindowGranel():" + ie.getLocalizedMessage());
                }
            }
        } else {
            tvList.requestFocus();
        }

    }

    private void openWindowListaPrecios() {
        try {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                URL url = getClass().getResource(FilesRouters.FX_LISTA_PRECIOS);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxListaPreciosController controller = fXMLLoader.getController();
                controller.setInitVentaEstructuraController(this);
                controller.loadDataView(tvList.getSelectionModel().getSelectedItem());
                //
                Stage stage = WindowStage.StageLoaderModal(parent, "Lista de precios", window.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.setOnHiding(w -> {
                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                    calculateTotales();
                });
                stage.show();

            } else {
                tvList.requestFocus();
            }
        } catch (IOException ex) {
            System.out.println("openWindowListaPrecios():" + ex.getLocalizedMessage());
        }
    }

    private void openWindowCashMovement() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_VENTA_MOVIMIENTO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxVentaMovimientoController controller = fXMLLoader.getController();
//            controller.setInitVentaEstructuraController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Movimiento de caja", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();
        } catch (IOException ex) {

        }

    }

    public void openWindowCantidad() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            if (!unidades_cambio_cantidades && tvList.getSelectionModel().getSelectedItem().getValorInventario() == 1
                    || !valormonetario_cambio_cantidades && tvList.getSelectionModel().getSelectedItem().getValorInventario() == 2
                    || !medida_cambio_cantidades && tvList.getSelectionModel().getSelectedItem().getValorInventario() == 3) {
                Tools.AlertMessageWarning(window, "Ventas", "No se puede cambiar la cantidad a este producto.");
            } else {
                try {
                    ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                    URL url = getClass().getResource(FilesRouters.FX_VENTA_CANTIDADES);
                    FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                    Parent parent = fXMLLoader.load(url.openStream());
                    //Controlller here
                    FxVentaCantidadesController controller = fXMLLoader.getController();
                    controller.setInitVentaEstructuraController(this);
                    controller.initComponents(tvList.getSelectionModel().getSelectedItem(), false);
                    //
                    Stage stage = WindowStage.StageLoaderModal(parent, "Modificar cantidades", window.getScene().getWindow());
                    stage.setResizable(false);
                    stage.sizeToScene();
                    stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
                    stage.show();
                    controller.getTxtCantidad().requestFocus();
                } catch (IOException ex) {
                    Tools.println("Venta estructura openWindowCantidad:" + ex.getLocalizedMessage());
                }
            }
        } else {
            tvList.requestFocus();
            if (!tvList.getItems().isEmpty()) {
                tvList.getSelectionModel().select(0);
            }
        }

    }

    public void openWindowCantidadLista(SuministroTB suministroTB, Boolean tipoVenta) {
        try {

            URL url = getClass().getResource(FilesRouters.FX_VENTA_CANTIDADES);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxVentaCantidadesController controller = fXMLLoader.getController();
            controller.setInitVentaEstructuraController(this);
            controller.initComponents(suministroTB, tipoVenta);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Modificar cantidades", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.show();
            controller.getTxtCantidad().requestFocus();
        } catch (IOException ex) {
            Tools.println("Venta estructura openWindowCantidad:" + ex.getLocalizedMessage());
        }
    }

    public void openWindowMostrarVentas() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_VENTA_MOSTRAR);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            //  FxVentaMostrarController controller = fXMLLoader.getController();
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Mostrar ventas", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();

        } catch (IOException ex) {
            Tools.println("Venta estructura openWindowMostrarVentas: " + ex.getLocalizedMessage());
        }
    }

    public void getAddArticulo(SuministroTB suministro) {
        if (validateDuplicateArticulo(tvList, suministro)) {
            for (int i = 0; i < tvList.getItems().size(); i++) {
                if (tvList.getItems().get(i).getIdSuministro().equalsIgnoreCase(suministro.getIdSuministro())) {
                    switch (suministro.getUnidadVenta()) {
                        case 3:
                            tvList.requestFocus();
                            tvList.getSelectionModel().select(i);
                            openWindowCantidad();
                            break;
                        case 2:
                            tvList.requestFocus();
                            tvList.getSelectionModel().select(i);
                            openWindowCambiarPrecio("Cambiar precio al Artículo", false);
                            break;
                        default:
                            SuministroTB suministroTB = tvList.getItems().get(i);
                            suministroTB.setCantidad(suministroTB.getCantidad() + 1);
                            double porcentajeRestante = suministroTB.getPrecioVentaGeneralUnico() * (suministroTB.getDescuento() / 100.00);

                            suministroTB.setDescuentoSumado(porcentajeRestante * suministroTB.getCantidad());
                            suministroTB.setImpuestoSumado(suministroTB.getCantidad() * (suministroTB.getPrecioVentaGeneralReal() * (suministroTB.getImpuestoValor() / 100.00)));

                            suministroTB.setSubImporte(suministroTB.getPrecioVentaGeneralUnico() * suministroTB.getCantidad());
                            suministroTB.setSubImporteDescuento(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
                            suministroTB.setTotalImporte(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());

                            tvList.refresh();
                            tvList.getSelectionModel().select(i);
                            calculateTotales();
                            break;
                    }
                }
            }
        } else {
            switch (suministro.getUnidadVenta()) {
                case 3: {
                    tvList.getItems().add(suministro);
                    int index = tvList.getItems().size() - 1;
                    tvList.requestFocus();
                    tvList.getSelectionModel().select(index);
                    openWindowCantidad();
                    calculateTotales();
                    break;
                }
                case 2: {
                    tvList.getItems().add(suministro);
                    int index = tvList.getItems().size() - 1;
                    tvList.requestFocus();
                    tvList.getSelectionModel().select(index);
                    openWindowCambiarPrecio("Cambiar precio al Artículo", false);
                    calculateTotales();
                    break;
                }
                default: {
                    tvList.getItems().add(suministro);
                    int index = tvList.getItems().size() - 1;
                    tvList.getSelectionModel().select(index);
                    calculateTotales();
                    txtSearch.requestFocus();
                    break;
                }
            }
        }

    }

    private boolean validateDuplicateArticulo(TableView<SuministroTB> view, SuministroTB suministroTB) {
        boolean ret = false;
        for (int i = 0; i < view.getItems().size(); i++) {
            if (view.getItems().get(i).getClave().equals(suministroTB.getClave())) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    private void cancelarVenta() {
        short value = Tools.AlertMessageConfirmation(window, "Venta", "¿Está seguro de limpiar la venta?");
        if (value == 1) {
            resetVenta();
        }
    }

    public void calculateTotales() {

        subTotal = 0;
        tvList.getItems().forEach(e -> subTotal += e.getSubImporte());
        lblValorVenta.setText(monedaSimbolo + " " + Tools.roundingValue(subTotal, 2));

        descuento = 0;
        tvList.getItems().forEach(e -> descuento += e.getDescuentoSumado());
        lblDescuento.setText(monedaSimbolo + " " + (Tools.roundingValue(descuento * (-1), 2)));

        subTotalImporte = 0;
        tvList.getItems().forEach(e -> subTotalImporte += e.getSubImporteDescuento());
        lblSubImporte.setText(monedaSimbolo + " " + Tools.roundingValue(subTotalImporte, 2));

        gpTotales.getChildren().clear();

        boolean addElement = false;
        double sumaElement = 0;
        double totalImpuestos = 0;
        if (!tvList.getItems().isEmpty()) {
            for (int k = 0; k < arrayArticulosImpuesto.size(); k++) {
                for (int i = 0; i < tvList.getItems().size(); i++) {
                    if (arrayArticulosImpuesto.get(k).getIdImpuesto() == tvList.getItems().get(i).getImpuestoArticulo()) {
                        addElement = true;
                        sumaElement += tvList.getItems().get(i).getImpuestoSumado();
                    }
                }
                if (addElement) {
                    gpTotales.add(addLabelTitle(arrayArticulosImpuesto.get(k).getNombre().substring(0, 1).toUpperCase()
                            + "" + arrayArticulosImpuesto.get(k).getNombre().substring(1, arrayArticulosImpuesto.get(k).getNombre().length()).toLowerCase(),
                            Pos.CENTER_LEFT), 0, k + 1);
                    gpTotales.add(addLabelTotal(monedaSimbolo + " " + Tools.roundingValue(sumaElement, 2), Pos.CENTER_RIGHT), 1, k + 1);
                    totalImpuestos += sumaElement;

                    addElement = false;
                    sumaElement = 0;
                }
            }
        }

        totalImporte = 0;
        totalImpuesto = 0;
        total = 0;
        tvList.getItems().forEach(e -> totalImporte += e.getTotalImporte());
        totalImpuesto = totalImpuestos;
        total = totalImporte + totalImpuesto;
        lblImporteTotal.setText(monedaSimbolo + " " + Tools.roundingValue(total, 2));
        lblTotalPagar.setText(monedaSimbolo + " " + Tools.roundingValue(Double.parseDouble(Tools.roundingValue(total, 1)), 2));
        lblTotal.setText(monedaSimbolo + " " + Tools.roundingValue(Double.parseDouble(Tools.roundingValue(total, 1)), 2));

    }

    private Label addLabelTitle(String nombre, Pos pos) {
        Label label = new Label(nombre);
        label.setStyle("-fx-text-fill:#020203;-fx-padding: 0.4166666666666667em 0em  0.4166666666666667em 0em;");
        label.getStyleClass().add("labelRoboto13");
        label.setAlignment(pos);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Control.USE_COMPUTED_SIZE);
        label.setMaxHeight(Control.USE_COMPUTED_SIZE);
        return label;
    }

    private Label addLabelTotal(String nombre, Pos pos) {
        Label label = new Label(nombre);
        label.setStyle("-fx-text-fill:#020203;");
        label.getStyleClass().add("labelRobotoMedium15");
        label.setAlignment(pos);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        return label;
    }

    public void resetVenta() {
        tvList.getItems().clear();

        loadDataComponent();

        lblTotal.setText(monedaSimbolo + " " + "0.00");
        lblValorVenta.setText(monedaSimbolo + " " + "0.00");
        lblDescuento.setText(monedaSimbolo + " " + "0.00");
        lblSubImporte.setText(monedaSimbolo + " " + "0.00");
        lblImporteTotal.setText(monedaSimbolo + " " + "0.00");
        lblTotalPagar.setText(monedaSimbolo + " " + "0.00");

        txtSearch.requestFocus();

        calculateTotales();
    }

    private void removeArticulo() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            short confirmation = Tools.AlertMessageConfirmation(window, "Venta", "¿Esta seguro de quitar el artículo?");
            if (confirmation == 1) {
                ObservableList<SuministroTB> articuloSelect, allArticulos;
                allArticulos = tvList.getItems();
                articuloSelect = tvList.getSelectionModel().getSelectedItems();
                articuloSelect.forEach(allArticulos::remove);
                calculateTotales();
            }
        } else {
            Tools.AlertMessageWarning(window, "Venta", "Seleccione un artículo para quitarlo");
        }
    }

    public int getTaxValueOperacion(int impuesto) {
        int valor = 0;
        for (ImpuestoTB impuestoTB : arrayArticulosImpuesto) {
            if (impuestoTB.getIdImpuesto() == impuesto) {
                valor = impuestoTB.getOperacion();
                break;
            }
        }
        return valor;
    }

    public double getTaxValue(int impuesto) {
        double valor = 0;
        for (ImpuestoTB impuestoTB : arrayArticulosImpuesto) {
            if (impuestoTB.getIdImpuesto() == impuesto) {
                valor = impuestoTB.getValor();
                break;
            }
        }
        return valor;
    }

    public String getTaxName(int impuesto) {
        String valor = "";
        for (ImpuestoTB impuestoTB : arrayArticulosImpuesto) {
            if (impuestoTB.getIdImpuesto() == impuesto) {
                valor = impuestoTB.getNombre();
                break;
            }
        }
        return valor;
    }

    private JasperPrint reportA4(VentaTB ventaTB, ArrayList<SuministroTB> list) throws JRException {

        InputStream imgInputStreamIcon = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

        InputStream imgInputStream = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

        if (Session.COMPANY_IMAGE != null) {
            imgInputStream = new ByteArrayInputStream(Session.COMPANY_IMAGE);
        }

        InputStream dir = getClass().getResourceAsStream("/report/VentaRealizada.jasper");

        Map map = new HashMap();
        map.put("LOGO", imgInputStream);
        map.put("ICON", imgInputStreamIcon);
        map.put("EMPRESA", Session.COMPANY_RAZON_SOCIAL);
        map.put("DIRECCION", Session.COMPANY_DOMICILIO);
        map.put("TELEFONOCELULAR", "TELÉFONO: " + Session.COMPANY_TELEFONO + " CELULAR: " + Session.COMPANY_CELULAR);
        map.put("EMAIL", "EMAIL: " + Session.COMPANY_EMAIL);

        map.put("DOCUMENTOEMPRESA", "R.U.C " + Session.COMPANY_NUMERO_DOCUMENTO);
        map.put("NOMBREDOCUMENTO", ventaTB.getComprobanteName());
        map.put("NUMERODOCUMENTO", ventaTB.getSerie() + "-" + ventaTB.getNumeracion());

        map.put("DATOSCLIENTE", ventaTB.getClienteTB().getInformacion());
        map.put("DOCUMENTOCLIENTE", ventaTB.getClienteTB().getTipoDocumentoName() + " N°:");
        map.put("NUMERODOCUMENTOCLIENTE", ventaTB.getClienteTB().getNumeroDocumento());
        map.put("CELULARCLIENTE", ventaTB.getClienteTB().getCelular().equals("") ? "---" : ventaTB.getClienteTB().getCelular());
        map.put("EMAILCLIENTE", ventaTB.getClienteTB().getEmail().equals("") ? "---" : ventaTB.getClienteTB().getEmail());
        map.put("DIRECCIONCLIENTE", ventaTB.getClienteTB().getDireccion().equals("") ? "---" : ventaTB.getClienteTB().getDireccion());

        map.put("FECHAEMISION", ventaTB.getFechaVenta());
        map.put("MONEDA", ventaTB.getMonedaTB().getNombre() + "-" + ventaTB.getMonedaTB().getAbreviado());
        map.put("CONDICIONPAGO", ventaTB.getTipoName() + "-" + ventaTB.getEstadoName());

        map.put("SIMBOLO", ventaTB.getMonedaTB().getSimbolo());
        map.put("VALORSOLES", monedaCadena.Convertir(Tools.roundingValue(ventaTB.getTotal(), 2), true, ventaTB.getMonedaTB().getNombre()));

        map.put("VALOR_VENTA", ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(ventaTB.getSubTotal(), 2));
        map.put("DESCUENTO", ventaTB.getMonedaTB().getSimbolo() + " -" + Tools.roundingValue(ventaTB.getDescuento(), 2));
        map.put("SUB_IMPORTE", ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(ventaTB.getSubImporte(), 2));
        map.put("IMPUESTO_TOTAL", Tools.roundingValue(ventaTB.getImpuesto(), 2));
        map.put("IMPORTE_TOTAL", ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(ventaTB.getTotal(), 2));

        JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JRBeanCollectionDataSource(list));
        return jasperPrint;
    }

    public void imprimirVenta(String idVenta) {
        if (!Session.ESTADO_IMPRESORA_VENTA && Tools.isText(Session.NOMBRE_IMPRESORA_VENTA) && Tools.isText(Session.FORMATO_IMPRESORA_VENTA)) {
            Tools.AlertMessageWarning(window, "Venta", "No esta configurado la ruta de impresión ve a la sección configuración/impresora.");
            return;
        }

        if (Session.FORMATO_IMPRESORA_VENTA.equalsIgnoreCase("ticket")) {
            if (Session.TICKET_VENTA_ID == 0 && Tools.isText(Session.TICKET_VENTA_RUTA)) {
                Tools.AlertMessageWarning(window, "Venta", "No hay un diseño predeterminado para la impresión configure su ticket en la sección configuración/tickets.");
            } else {
                executeProcessPrinterVenta(idVenta, Session.NOMBRE_IMPRESORA_VENTA, Session.CORTAPAPEL_IMPRESORA_VENTA, Session.FORMATO_IMPRESORA_VENTA);
            }
        } else if (Session.FORMATO_IMPRESORA_VENTA.equalsIgnoreCase("a4")) {
            executeProcessPrinterVenta(idVenta, Session.NOMBRE_IMPRESORA_VENTA, Session.CORTAPAPEL_IMPRESORA_VENTA, Session.FORMATO_IMPRESORA_VENTA);
        } else {
            Tools.AlertMessageWarning(window, "Venta", "Error al validar el formato de impresión, configure en la sección configuración/impresora.");
        }
    }

    private void executeProcessPrinterVenta(String idVenta, String printerName, boolean printerCut, String format) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<String> task = new Task<String>() {
            @Override
            public String call() {
                ArrayList<Object> objects = VentaADO.ListCompletaVentasDetalle(idVenta);
                try {
                    if (!objects.isEmpty()) {
                        VentaTB ventaTB = (VentaTB) objects.get(0);
//                        EmpleadoTB empleadoTB = (EmpleadoTB) objects.get(1);
                        ObservableList<SuministroTB> suministroTBs = (ObservableList<SuministroTB>) objects.get(2);

                        if (format.equalsIgnoreCase("a4")) {
                            ArrayList<SuministroTB> list = new ArrayList();
                            suministroTBs.stream().map((suministroTB) -> {
                                SuministroTB stb = new SuministroTB();
                                stb.setClave(suministroTB.getClave());
                                stb.setNombreMarca(suministroTB.getNombreMarca());
                                stb.setCantidad(suministroTB.getCantidad());
                                stb.setUnidadCompraName(suministroTB.getUnidadCompraName());
                                stb.setPrecioVentaGeneral(suministroTB.getPrecioVentaGeneral());
                                stb.setDescuento(suministroTB.getDescuento());
                                stb.setTotalImporte(suministroTB.getCantidad() * +suministroTB.getPrecioVentaGeneral());
                                return stb;
                            }).forEachOrdered((stb) -> {
                                list.add(stb);
                            });

                            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
                            printRequestAttributeSet.add(new Copies(1));

                            PrinterName pn = new PrinterName(printerName, null);

                            PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
                            printServiceAttributeSet.add(pn);

                            JRPrintServiceExporter exporter = new JRPrintServiceExporter();

                            exporter.setParameter(JRExporterParameter.JASPER_PRINT, reportA4(ventaTB, list));
                            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
                            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceAttributeSet);
                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
                            exporter.exportReport();
                            return "completed";
                        } else {

                            billPrintable.loadEstructuraTicket(Session.TICKET_VENTA_ID, Session.TICKET_VENTA_RUTA, hbEncabezado, hbDetalleCabecera, hbPie);

                            for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
                                HBox box = ((HBox) hbEncabezado.getChildren().get(i));
                                billPrintable.hbEncebezado(box,
                                        ventaTB.getComprobanteName(),
                                        ventaTB.getSerie() + "-" + ventaTB.getNumeracion(),
                                        ventaTB.getClienteTB().getNumeroDocumento(),
                                        ventaTB.getClienteTB().getInformacion(),
                                        ventaTB.getClienteTB().getCelular(),
                                        ventaTB.getClienteTB().getDireccion(),
                                        ventaTB.getCodigo());
                            }

                            AnchorPane hbDetalle = new AnchorPane();
                            for (int m = 0; m < suministroTBs.size(); m++) {
                                for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                                    HBox hBox = new HBox();
                                    hBox.setId("dc_" + m + "" + i);
                                    HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                                    billPrintable.hbDetalle(hBox, box, suministroTBs, m);
                                    hbDetalle.getChildren().add(hBox);
                                }
                            }

                            for (int i = 0; i < hbPie.getChildren().size(); i++) {
                                HBox box = ((HBox) hbPie.getChildren().get(i));
                                billPrintable.hbPie(box, ventaTB.getMonedaTB().getSimbolo(),
                                        Tools.roundingValue(ventaTB.getSubImporte(), 2),
                                        "-" + Tools.roundingValue(ventaTB.getDescuento(), 2),
                                        Tools.roundingValue(ventaTB.getSubImporte(), 2),
                                        Tools.roundingValue(ventaTB.getTotal(), 2),
                                        Tools.roundingValue(ventaTB.getEfectivo(), 2),
                                        Tools.roundingValue(ventaTB.getVuelto(), 2),
                                        ventaTB.getClienteTB().getNumeroDocumento(),
                                        ventaTB.getClienteTB().getInformacion(), ventaTB.getCodigo(),
                                        ventaTB.getClienteTB().getCelular());
                            }

                            billPrintable.generatePDFPrint(hbEncabezado, hbDetalle, hbPie);

                            DocPrintJob job = billPrintable.findPrintService(printerName, PrinterJob.lookupPrintServices()).createPrintJob();

                            if (job != null) {
                                PrinterJob pj = PrinterJob.getPrinterJob();
                                pj.setPrintService(job.getPrintService());
                                pj.setJobName(printerName);
                                Book book = new Book();
                                book.append(billPrintable, billPrintable.getPageFormat(pj));
                                pj.setPageable(book);
                                pj.print();
                                if (printerCut) {
                                    billPrintable.printCortarPapel(printerName);
                                }
                                return "completed";
                            } else {
                                return "error_name";
                            }
                        }
                    } else {
                        return "empty";
                    }
                } catch (PrinterException | IOException | PrintException | JRException ex) {
                    return "Error en imprimir: " + ex.getLocalizedMessage();
                }
            }
        };

        task.setOnSucceeded(w -> {
            String result = task.getValue();
            if (result.equalsIgnoreCase("completed")) {
                Tools.showAlertNotification("/view/image/information_large.png",
                        "Envío de impresión",
                        "Se completo el proceso de impresión correctamente.",
                        Duration.seconds(5),
                        Pos.BOTTOM_RIGHT);
            } else if (result.equalsIgnoreCase("error_name")) {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "Error en encontrar el nombre de la impresión por problemas de puerto o driver.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else if (result.equalsIgnoreCase("no_config")) {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "Error al validar el tipo de impresión, cofigure nuevamente la impresora.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else if (result.equalsIgnoreCase("empty")) {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "No hay registros para mostrar en el reporte.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else {
                Tools.showAlertNotification("/view/image/error_large.png",
                        "Envío de impresión",
                        "Se producto un problema por problemas de la impresora\n" + result,
                        Duration.seconds(10),
                        Pos.CENTER);
            }
        });

        task.setOnFailed(w -> {
            Tools.showAlertNotification("/view/image/warning_large.png",
                    "Envío de impresión",
                    "Se produjo un problema en el proceso de envío, \n intente nuevamente o comuníquese con su proveedor del sistema.",
                    Duration.seconds(10),
                    Pos.BOTTOM_RIGHT);
        });

        task.setOnScheduled(w -> {
            Tools.showAlertNotification("/view/image/print.png",
                    "Envío de impresión",
                    "Se envió la impresión a la cola, este\n proceso puede tomar unos segundos.",
                    Duration.seconds(5),
                    Pos.BOTTOM_RIGHT);
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void imprimirPreVenta() {
        if (!Session.ESTADO_IMPRESORA_PRE_VENTA && Tools.isText(Session.NOMBRE_IMPRESORA_PRE_VENTA) && Tools.isText(Session.FORMATO_IMPRESORA_PRE_VENTA)) {
            Tools.AlertMessageWarning(window, "Venta", "No esta configurado la ruta de impresión, ve a la sección configuración/impresora.");
            return;
        }

        if (Session.FORMATO_IMPRESORA_PRE_VENTA.equalsIgnoreCase("ticket")) {
            if (Session.TICKET_PRE_VENTA_ID == 0 && Tools.isText(Session.TICKET_PRE_VENTA_RUTA)) {
                Tools.AlertMessageWarning(window, "Venta", "No hay un diseño predeterminado para la impresión, configure su ticket en la sección configuración/tickets.");
            } else {
                executeProcessPrinterPreVenta(Session.NOMBRE_IMPRESORA_PRE_VENTA, Session.CORTAPAPEL_IMPRESORA_PRE_VENTA);
            }
        } else if (Session.FORMATO_IMPRESORA_PRE_VENTA.equalsIgnoreCase("a4")) {
//            executeProcessPrinterPreVenta(Session.NOMBRE_IMPRESORA_PRE_VENTA);
        } else {
            Tools.AlertMessageWarning(window, "Venta", "Error al validar el formato de impresión, configure en la sección configuración/impresora.");

        }
    }

    private void executeProcessPrinterPreVenta(String printerName, boolean printerCut) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<String> task = new Task<String>() {
            @Override
            public String call() throws PrintException {
                try {
                    billPrintable.loadEstructuraTicket(Session.TICKET_PRE_VENTA_ID, Session.TICKET_PRE_VENTA_RUTA, hbEncabezado, hbDetalleCabecera, hbPie);

                    for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
                        HBox box = ((HBox) hbEncabezado.getChildren().get(i));
                        billPrintable.hbEncebezado(box,
                                "PRE VENTA",
                                "-------",
                                txtNumeroDocumento.getText().trim().toUpperCase(),
                                txtDatosCliente.getText().trim().toUpperCase(),
                                txtCelularCliente.getText().trim().toUpperCase(),
                                txtDireccionCliente.getText().trim().toUpperCase(),
                                "00000000");
                    }

                    AnchorPane hbDetalle = new AnchorPane();
                    for (int m = 0; m < tvList.getItems().size(); m++) {
                        for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                            HBox hBox = new HBox();
                            hBox.setId("dc_" + m + "" + i);
                            HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                            billPrintable.hbDetalle(hBox, box, tvList.getItems(), m);
                            hbDetalle.getChildren().add(hBox);
                        }
                    }

                    for (int i = 0; i < hbPie.getChildren().size(); i++) {
                        HBox box = ((HBox) hbPie.getChildren().get(i));
                        billPrintable.hbPie(box, monedaSimbolo,
                                Tools.roundingValue(subTotal, 2),
                                "-" + Tools.roundingValue(descuento, 2),
                                Tools.roundingValue(subTotalImporte, 2),
                                Tools.roundingValue(total, 2),
                                "EFECTIVO",
                                "VUELTO",
                                txtNumeroDocumento.getText(),
                                txtDatosCliente.getText(), "CODIGO DE VENTA", txtCelularCliente.getText().trim());
                    }

                    billPrintable.generatePDFPrint(hbEncabezado, hbDetalle, hbPie);

                    DocPrintJob job = billPrintable.findPrintService(printerName, PrinterJob.lookupPrintServices()).createPrintJob();

                    if (job != null) {
                        PrinterJob pj = PrinterJob.getPrinterJob();
                        pj.setPrintService(job.getPrintService());
                        pj.setJobName(printerName);
                        Book book = new Book();
                        book.append(billPrintable, billPrintable.getPageFormat(pj));
                        pj.setPageable(book);
                        pj.print();
                        if (printerCut) {
                            billPrintable.printCortarPapel(printerName);
                        }
                        return "completed";
                    } else {
                        return "error_name";
                    }

                } catch (PrinterException | IOException | PrintException ex) {
                    return "Error en imprimir: " + ex.getLocalizedMessage();
                }
            }
        };

        task.setOnSucceeded(w -> {
            String result = task.getValue();
            if (result.equalsIgnoreCase("completed")) {
                Tools.showAlertNotification("/view/image/information_large.png",
                        "Envío de impresión",
                        "Se completo el proceso de impresión correctamente.",
                        Duration.seconds(5),
                        Pos.BOTTOM_RIGHT);
            } else if (result.equalsIgnoreCase("error_name")) {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "Error en encontrar el nombre de la impresión por problemas de puerto o driver.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else if (result.equalsIgnoreCase("no_config")) {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "Error al validar el tipo de impresión, cofigure nuevamente la impresora.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else if (result.equalsIgnoreCase("empty")) {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "No hay registros para mostrar en el reporte.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else {
                Tools.showAlertNotification("/view/image/error_large.png",
                        "Envío de impresión",
                        "Se producto un problema por problemas de la impresora\n" + result,
                        Duration.seconds(10),
                        Pos.CENTER);
            }
        });

        task.setOnFailed(w -> {
            Tools.showAlertNotification("/view/image/warning_large.png",
                    "Envío de impresión",
                    "Se produjo un problema en el proceso de envío, \n intente nuevamente o comuníquese con su proveedor del sistema.",
                    Duration.seconds(10),
                    Pos.BOTTOM_RIGHT);
        });

        task.setOnScheduled(w -> {
            Tools.showAlertNotification("/view/image/print.png",
                    "Envío de impresión",
                    "Se envió la impresión a la cola, este\n proceso puede tomar unos segundos.",
                    Duration.seconds(5),
                    Pos.BOTTOM_RIGHT);
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    public void onExecuteCliente(short opcion, String search) {

        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ClienteTB> task = new Task<ClienteTB>() {
            @Override
            public ClienteTB call() {
                return ClienteADO.GetSearchClienteNumeroDocumento(opcion, search);
            }
        };

        task.setOnScheduled(e -> {
            txtNumeroDocumento.setDisable(true);
            txtDatosCliente.setDisable(true);
            txtCelularCliente.setDisable(true);
            txtCorreoElectronico.setDisable(true);
            txtDireccionCliente.setDisable(true);
            btnBuscarCliente.setDisable(true);
            btnBuscarSunat.setDisable(true);
            btnBuscarReniec.setDisable(true);

            txtDatosCliente.setText("");
            txtCelularCliente.setText("");
            txtCorreoElectronico.setText("");
            txtDireccionCliente.setText("");
            Tools.showAlertNotification("/view/image/information_large.png",
                    "Buscando clíente",
                    "Se inicio el proceso de busqueda\n del cliente internamente.",
                    Duration.seconds(5),
                    Pos.TOP_RIGHT);
        });

        task.setOnFailed(e -> {
            Tools.showAlertNotification("/view/image/error_large.png",
                    "Buscando clíente",
                    "Se produjo un error al buscar al cliente intenten\n nuevamente, si persiste el problema comuniquese con su \nproveedor del sistema.",
                    Duration.seconds(10),
                    Pos.TOP_RIGHT);
            clearDataClient();
        });

        task.setOnSucceeded(e -> {
            ClienteTB clienteTB = task.getValue();
            if (clienteTB != null) {
                Tools.showAlertNotification("/view/image/succes_large.png",
                        "Buscando clíente",
                        "Se completo la busqueda con exito.",
                        Duration.seconds(5),
                        Pos.TOP_RIGHT);
                txtNumeroDocumento.setDisable(false);
                txtDatosCliente.setDisable(false);
                txtCelularCliente.setDisable(false);
                txtCorreoElectronico.setDisable(false);
                txtDireccionCliente.setDisable(false);
                btnBuscarCliente.setDisable(false);
                btnBuscarSunat.setDisable(false);
                btnBuscarReniec.setDisable(false);

                idCliente = clienteTB.getIdCliente();
                txtDatosCliente.setText(clienteTB.getInformacion());
                txtCelularCliente.setText(clienteTB.getCelular());
                txtCorreoElectronico.setText(clienteTB.getEmail());
                txtDireccionCliente.setText(clienteTB.getDireccion());
                for (int i = 0; i < cbTipoDocumento.getItems().size(); i++) {
                    if (cbTipoDocumento.getItems().get(i).getIdDetalle().get() == clienteTB.getTipoDocumento()) {
                        cbTipoDocumento.getSelectionModel().select(i);
                        break;
                    }
                }
            } else {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Buscando clíente",
                        "Hubo un problema en validar el resultado de\n datos intente nuevamente.",
                        Duration.seconds(5),
                        Pos.TOP_RIGHT);
                clearDataClient();
            }
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }

    }

    private void getApiSunat() {
        ApiPeru apiSunat = new ApiPeru();
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                ArrayList<Object> objects = new ArrayList();
                ClienteTB clienteTB = ClienteADO.GetSearchClienteNumeroDocumento((short) 2, txtNumeroDocumento.getText().trim());
                if (clienteTB == null) {
                    objects.add("client-no-exists");
                    objects.add("");
                    objects.add(apiSunat.getUrlSunatApisPeru(txtNumeroDocumento.getText().trim()));
                } else {
                    objects.add("client-exists");
                    objects.add(clienteTB);
                    objects.add(apiSunat.getUrlSunatApisPeru(txtNumeroDocumento.getText().trim()));
                }
                return objects;
            }
        };

        task.setOnScheduled(e -> {
            txtNumeroDocumento.setDisable(true);
            txtDatosCliente.setDisable(true);
            txtCelularCliente.setDisable(true);
            txtCorreoElectronico.setDisable(true);
            txtDireccionCliente.setDisable(true);
            btnBuscarCliente.setDisable(true);
            btnBuscarSunat.setDisable(true);
            btnBuscarReniec.setDisable(true);

            txtDatosCliente.setText("");
            txtCelularCliente.setText("");
            txtCorreoElectronico.setText("");
            txtDireccionCliente.setText("");
            
            Tools.showAlertNotification("/view/image/information_large.png",
                    "Buscando clíente",
                    "Se inicio el proceso de busqueda\n del cliente por su número de ruc.",
                    Duration.seconds(5),
                    Pos.TOP_RIGHT);
        });

        task.setOnFailed(e -> {
            Tools.showAlertNotification("/view/image/error_large.png",
                    "Buscando clíente",
                    "Se produjo un error al buscar al cliente intenten\n nuevamente, si persiste el problema comuniquese con su \nproveedor del sistema.",
                    Duration.seconds(10),
                    Pos.TOP_RIGHT);
            clearDataClient();
        });

        task.setOnSucceeded(e -> {

            ArrayList<Object> result = task.getValue();
            if (!result.isEmpty()) {
                String stateClient = (String) result.get(0);
                String api = (String) result.get(2);

                if (api.equalsIgnoreCase("200") && !Tools.isText(apiSunat.getJsonURL())) {

                    JSONObject sONObject = Json.obtenerObjetoJSON(apiSunat.getJsonURL());
                    if (sONObject == null) {
                        Tools.showAlertNotification("/view/image/warning_large.png",
                                "Buscando clíente",
                                "Hubo un problema en validar el resultado de\n datos intente nuevamente.",
                                Duration.seconds(5),
                                Pos.TOP_RIGHT);
                        clearDataClient();
                    } else {
                        Tools.showAlertNotification("/view/image/succes_large.png",
                                "Buscando clíente",
                                "Se completo la busqueda con exito.",
                                Duration.seconds(5),
                                Pos.TOP_RIGHT);
                        txtNumeroDocumento.setDisable(false);
                        txtDatosCliente.setDisable(false);
                        txtCelularCliente.setDisable(false);
                        txtCorreoElectronico.setDisable(false);
                        txtDireccionCliente.setDisable(false);
                        btnBuscarCliente.setDisable(false);
                        btnBuscarSunat.setDisable(false);
                        btnBuscarReniec.setDisable(false);
                        if (sONObject.get("ruc") != null) {
                            txtNumeroDocumento.setText(sONObject.get("ruc").toString());
                        }
                        if (sONObject.get("razonSocial") != null) {
                            txtDatosCliente.setText(sONObject.get("razonSocial").toString());
                        }
                        if (sONObject.get("direccion") != null) {
                            txtDireccionCliente.setText(sONObject.get("direccion").toString());
                        }

                        if (stateClient.equals("client-exists")) {
                            ClienteTB clienteTB = (ClienteTB) result.get(1);
                            txtCelularCliente.setText(clienteTB.getCelular());
                            txtCorreoElectronico.setText(clienteTB.getEmail());
                            for (int i = 0; i < cbTipoDocumento.getItems().size(); i++) {
                                if (cbTipoDocumento.getItems().get(i).getIdDetalle().get() == clienteTB.getTipoDocumento()) {
                                    cbTipoDocumento.getSelectionModel().select(i);
                                    break;
                                }
                            }
                        }
                    }

                } else {
                    Tools.showAlertNotification("/view/image/warning_large.png",
                            "Buscando clíente",
                            "Paso un problema en trear la información por\n problemas de conexión o error el número ruc.",
                            Duration.seconds(5),
                            Pos.TOP_RIGHT);
                    clearDataClient();
                }
            } else {

            }
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }

    }

    private void getApiReniec() {
        ApiPeru apiSunat = new ApiPeru();
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                ArrayList<Object> objects = new ArrayList();
                ClienteTB clienteTB = ClienteADO.GetSearchClienteNumeroDocumento((short) 2, txtNumeroDocumento.getText().trim());
                if (clienteTB == null) {
                    objects.add("client-no-exists");
                    objects.add("");
                    objects.add(apiSunat.getUrlReniecApisPeru(txtNumeroDocumento.getText().trim()));
                } else {
                    objects.add("client-exists");
                    objects.add(clienteTB);
                    objects.add(apiSunat.getUrlReniecApisPeru(txtNumeroDocumento.getText().trim()));
                }
                return objects;
            }
        };

        task.setOnScheduled(e -> {
            txtNumeroDocumento.setDisable(true);
            txtDatosCliente.setDisable(true);
            txtCelularCliente.setDisable(true);
            txtCorreoElectronico.setDisable(true);
            txtDireccionCliente.setDisable(true);
            btnBuscarCliente.setDisable(true);
            btnBuscarSunat.setDisable(true);
            btnBuscarReniec.setDisable(true);

            txtDatosCliente.setText("");
            txtCelularCliente.setText("");
            txtDireccionCliente.setText("");
            txtDireccionCliente.setText("");

            Tools.showAlertNotification("/view/image/information_large.png",
                    "Buscando clíente",
                    "Se inicio el proceso de busqueda\n del cliente por su número de dni.",
                    Duration.seconds(5),
                    Pos.TOP_RIGHT);
        });

        task.setOnFailed(e -> {
            Tools.showAlertNotification("/view/image/error_large.png",
                    "Buscando clíente",
                    "Se produjo un error al buscar al cliente intenten\n nuevamente, si persiste el problema comuniquese con su \nproveedor del sistema.",
                    Duration.seconds(10),
                    Pos.TOP_RIGHT);
            clearDataClient();
        });

        task.setOnSucceeded(e -> {
            ArrayList<Object> result = task.getValue();
            if (!result.isEmpty()) {
                String stateClient = (String) result.get(0);
                String api = (String) result.get(2);

                if (api.equalsIgnoreCase("200") && !Tools.isText(apiSunat.getJsonURL())) {
                    JSONObject sONObject = Json.obtenerObjetoJSON(apiSunat.getJsonURL());
                    if (sONObject == null) {
                        Tools.showAlertNotification("/view/image/warning_large.png",
                                "Buscando clíente",
                                "Hubo un problema en validar el resultado de\n datos intente nuevamente.",
                                Duration.seconds(5),
                                Pos.TOP_RIGHT);
                        clearDataClient();
                    } else {
                        Tools.showAlertNotification("/view/image/succes_large.png",
                                "Buscando clíente",
                                "Se completo la busqueda con exito.",
                                Duration.seconds(5),
                                Pos.TOP_RIGHT);
                        txtNumeroDocumento.setDisable(false);
                        txtDatosCliente.setDisable(false);
                        txtCelularCliente.setDisable(false);
                        txtCorreoElectronico.setDisable(false);
                        txtDireccionCliente.setDisable(false);
                        btnBuscarCliente.setDisable(false);
                        btnBuscarSunat.setDisable(false);
                        btnBuscarReniec.setDisable(false);
                        if (sONObject.get("dni") != null) {
                            txtNumeroDocumento.setText(sONObject.get("dni").toString());
                        }
                        if (sONObject.get("apellidoPaterno") != null && sONObject.get("apellidoMaterno") != null && sONObject.get("nombres") != null) {
                            txtDatosCliente.setText(sONObject.get("apellidoPaterno").toString() + " " + sONObject.get("apellidoMaterno").toString() + " " + sONObject.get("nombres").toString());
                        }
                        if (stateClient.equals("client-exists")) {
                            ClienteTB clienteTB = (ClienteTB) result.get(1);

                            txtCelularCliente.setText(clienteTB.getCelular());
                            txtCorreoElectronico.setText(clienteTB.getEmail());
                            txtDireccionCliente.setText(clienteTB.getDireccion());

                            for (int i = 0; i < cbTipoDocumento.getItems().size(); i++) {
                                if (cbTipoDocumento.getItems().get(i).getIdDetalle().get() == clienteTB.getTipoDocumento()) {
                                    cbTipoDocumento.getSelectionModel().select(i);
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    Tools.showAlertNotification("/view/image/warning_large.png",
                            "Buscando clíente",
                            "Paso un problema en trear la información por\n problemas de conexión o error el número del dni.",
                            Duration.seconds(5),
                            Pos.TOP_RIGHT);
                    clearDataClient();
                }

            } else {
                Tools.showAlertNotification("/view/image/warning_large.png",
                        "Buscando clíente",
                        "Hubo un problema interno, intente nuevamente.",
                        Duration.seconds(5),
                        Pos.TOP_RIGHT);
                clearDataClient();
            }
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void clearDataClient() {
        txtNumeroDocumento.setDisable(false);
        txtDatosCliente.setDisable(false);
        txtCelularCliente.setDisable(false);
        txtCorreoElectronico.setDisable(false);
        txtDireccionCliente.setDisable(false);
        btnBuscarCliente.setDisable(false);
        btnBuscarSunat.setDisable(false);
        btnBuscarReniec.setDisable(false);

        txtNumeroDocumento.setText("");
        txtDatosCliente.setText("");
        txtCelularCliente.setText("");
        txtCorreoElectronico.setText("");
        txtDireccionCliente.setText("");
    }

    @FXML
    private void onKeyPressedRegister(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowVentaProceso();
        }
    }

    @FXML
    private void onActionRegister(ActionEvent event) {
        openWindowVentaProceso();
    }

    @FXML
    private void onKeyPressedArticulo(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowArticulos();
        }
    }

    @FXML
    private void onActionArticulo(ActionEvent event) {
        openWindowArticulos();
    }

    @FXML
    private void onKeyPressedListaPrecios(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowListaPrecios();
        }
    }

    @FXML
    private void onActionListaPrecios(ActionEvent event) {
        openWindowListaPrecios();
    }

    @FXML
    private void onKeyPressedCantidad(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowCantidad();
        }
    }

    @FXML
    private void onActionCantidad(ActionEvent event) {
        openWindowCantidad();
    }

    @FXML
    private void onKeyPressedPrecio(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowCambiarPrecio("Cambiar precio al Artículo", false);
        }
    }

    @FXML
    private void onActionPrecio(ActionEvent event) {
        openWindowCambiarPrecio("Cambiar precio al Artículo", false);
    }

    @FXML
    private void onKeyPressedDescuento(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowDescuento();
        }
    }

    @FXML
    private void onActionDescuento(ActionEvent event) {
        openWindowDescuento();
    }

    @FXML
    private void onKeyPressedPrecioSumar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowCambiarPrecio("Sumar precio al Artículo", true);
        }
    }

    @FXML
    private void onActionPrecioSumar(ActionEvent event) {
        openWindowCambiarPrecio("Sumar precio al Artículo", true);
    }

    @FXML
    private void onKeyReleasedSearch(KeyEvent event) {
        if (event.getCode() != KeyCode.ESCAPE
                && event.getCode() != KeyCode.F1
                && event.getCode() != KeyCode.F2
                && event.getCode() != KeyCode.F3
                && event.getCode() != KeyCode.F4
                && event.getCode() != KeyCode.F5
                && event.getCode() != KeyCode.F6
                && event.getCode() != KeyCode.F7
                && event.getCode() != KeyCode.F8
                && event.getCode() != KeyCode.F9
                && event.getCode() != KeyCode.F10
                && event.getCode() != KeyCode.F11
                && event.getCode() != KeyCode.F12
                && event.getCode() != KeyCode.ALT
                && event.getCode() != KeyCode.CONTROL
                && event.getCode() != KeyCode.UP
                && event.getCode() != KeyCode.DOWN
                && event.getCode() != KeyCode.RIGHT
                && event.getCode() != KeyCode.LEFT
                && event.getCode() != KeyCode.TAB
                && event.getCode() != KeyCode.CAPS
                && event.getCode() != KeyCode.SHIFT
                && event.getCode() != KeyCode.HOME
                && event.getCode() != KeyCode.WINDOWS
                && event.getCode() != KeyCode.ALT_GRAPH
                && event.getCode() != KeyCode.CONTEXT_MENU
                && event.getCode() != KeyCode.END
                && event.getCode() != KeyCode.INSERT
                && event.getCode() != KeyCode.PAGE_UP
                && event.getCode() != KeyCode.PAGE_DOWN
                && event.getCode() != KeyCode.NUM_LOCK
                && event.getCode() != KeyCode.PRINTSCREEN
                && event.getCode() != KeyCode.SCROLL_LOCK
                && event.getCode() != KeyCode.PAUSE) {
            if (!stateSearch) {
                filterSuministro(txtSearch.getText().trim());
            }
        }
    }

    @FXML
    private void onKeyReleasedList(KeyEvent event) {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            if (event.getCode() == KeyCode.PLUS || event.getCode() == KeyCode.ADD) {
                int index = tvList.getSelectionModel().getSelectedIndex();
                SuministroTB suministroTB = tvList.getSelectionModel().getSelectedItem();
                if (!valormonetario_cambio_cantidades && suministroTB.getUnidadVenta() == 2) {
                    return;
                }
                suministroTB.setCantidad(suministroTB.getCantidad() + 1);
                double porcentajeRestante = suministroTB.getPrecioVentaGeneralUnico() * (suministroTB.getDescuento() / 100.00);

                suministroTB.setDescuentoCalculado(porcentajeRestante);
                suministroTB.setDescuentoSumado(porcentajeRestante * suministroTB.getCantidad());

                double impuesto = Tools.calculateTax(suministroTB.getImpuestoValor(), suministroTB.getPrecioVentaGeneralReal());
                suministroTB.setImpuestoSumado(suministroTB.getCantidad() * impuesto);

                suministroTB.setSubImporte(suministroTB.getPrecioVentaGeneralUnico() * suministroTB.getCantidad());
                suministroTB.setSubImporteDescuento(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
                suministroTB.setTotalImporte(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());

                tvList.refresh();
                tvList.getSelectionModel().select(index);
                calculateTotales();

            } else if (event.getCode() == KeyCode.MINUS || event.getCode() == KeyCode.SUBTRACT) {
                int index = tvList.getSelectionModel().getSelectedIndex();
                SuministroTB suministroTB = tvList.getSelectionModel().getSelectedItem();
                if (!valormonetario_cambio_cantidades && suministroTB.getUnidadVenta() == 2) {
                    return;
                }
                if (suministroTB.getCantidad() <= 1) {
                    return;
                }
                suministroTB.setCantidad(suministroTB.getCantidad() - 1);
                double porcentajeRestante = suministroTB.getPrecioVentaGeneralUnico() * (suministroTB.getDescuento() / 100.00);

                suministroTB.setDescuentoCalculado(porcentajeRestante);
                suministroTB.setDescuentoSumado(porcentajeRestante * suministroTB.getCantidad());

                double impuesto = Tools.calculateTax(suministroTB.getImpuestoValor(), suministroTB.getPrecioVentaGeneralReal());
                suministroTB.setImpuestoSumado(suministroTB.getCantidad() * impuesto);

                suministroTB.setSubImporte(suministroTB.getPrecioVentaGeneralUnico() * suministroTB.getCantidad());
                suministroTB.setSubImporteDescuento(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
                suministroTB.setTotalImporte(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());

                tvList.refresh();
                tvList.getSelectionModel().select(index);
                calculateTotales();
            }
        }
    }

    @FXML
    private void onActionComprobante(ActionEvent event) {
        if (cbComprobante.getSelectionModel().getSelectedIndex() >= 0) {
            String[] array = ComprobanteADO.GetSerieNumeracionEspecifico(cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento()).split("-");
            lblSerie.setText(array[0]);
            lblNumeracion.setText(array[1]);
        }
    }

    @FXML
    private void onActionMoneda(ActionEvent event) {
        if (cbMoneda.getSelectionModel().getSelectedIndex() >= 0) {
            monedaSimbolo = cbMoneda.getSelectionModel().getSelectedItem().getSimbolo();
            calculateTotales();
        }
    }

    @FXML
    private void onKeyPressedCashMovement(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowCashMovement();
        }
    }

    @FXML
    private void onActionCashMovement(ActionEvent event) {
        openWindowCashMovement();
    }

    @FXML
    private void onKeyPressedCancelar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            cancelarVenta();
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        cancelarVenta();
    }

    @FXML
    private void onKeyPressedVentasPorDia(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowMostrarVentas();
        }
    }

    @FXML
    private void onActionVentasPorDia(ActionEvent event) {
        openWindowMostrarVentas();
    }

    @FXML
    private void onKeyPressedCliente(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onExecuteCliente((short) 2, txtNumeroDocumento.getText().trim());
        }
    }

    @FXML
    private void onActionCliente(ActionEvent event) {
        onExecuteCliente((short) 2, txtNumeroDocumento.getText().trim());
    }

    @FXML
    private void onKeyPressedSunat(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            getApiSunat();
        }
    }

    @FXML
    private void onActionSunat(ActionEvent event) {
        getApiSunat();
    }

    @FXML
    private void onKeyPressedReniec(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            getApiReniec();
        }
    }

    @FXML
    private void onActionReniec(ActionEvent event) {
        getApiReniec();
    }

    @FXML
    private void onKeyPressedTicket(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            imprimirPreVenta();
        }
    }

    @FXML
    private void onActionTicket(ActionEvent event) {
        imprimirPreVenta();
    }

    @FXML
    private void onKeyTypedNumeroDocumento(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
            event.consume();
        }
    }

    public int getIdTipoComprobante() {
        return cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento();
    }

    public TextField getTxtSearch() {
        return txtSearch;
    }

    public TableView<SuministroTB> getTvList() {
        return tvList;
    }

    public String getMonedaNombre() {
        return cbMoneda.getSelectionModel().getSelectedItem().getNombre();
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
