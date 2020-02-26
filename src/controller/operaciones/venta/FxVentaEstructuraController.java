package controller.operaciones.venta;

import controller.inventario.suministros.FxSuministrosListaController;
import controller.tools.BillPrintable;
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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.ComprobanteADO;
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.MonedaADO;
import model.MonedaTB;
import model.PrivilegioTB;
import model.SuministroADO;
import model.SuministroTB;
import model.TipoDocumentoADO;
import model.TipoDocumentoTB;
import model.VentaTB;

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
    private Text lblMoneda;
    @FXML
    private Text lblTotal;
    @FXML
    private Text lblSerie;
    @FXML
    private Text lblNumeracion;
    @FXML
    private ComboBox<TipoDocumentoTB> cbComprobante;
    @FXML
    private ComboBox<MonedaTB> cbMoneda;
    @FXML
    private Text lblSubTotalMoneda;
    @FXML
    private Text lblValorVenta;
    @FXML
    private Text lblDescuentoMoneda;
    @FXML
    private Text lblDescuento;
    @FXML
    private Text lblSubImporteMoneda;
    @FXML
    private Text lblSubImporte;
    @FXML
    private VBox vbImpuestos;
    @FXML
    private Text lblImporteTotalMoneda;
    @FXML
    private Text lblImporteTotal;
    @FXML
    private Text lblTotalPagarMoneda;
    @FXML
    private Text lblTotalPagar;
    @FXML
    private HBox hbBotonesInferior;
    @FXML
    private Button btnQuitar;
    @FXML
    private Button btnMovimiento;
    @FXML
    private Button btnImpresora;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnVentasPorDia;
    @FXML
    private TextField txtObservacion;
    //-----------------------------------------------
    private AnchorPane vbPrincipal;

    private double subTotal;

    private double descuento;

    private double subTotalImporte;

    private double totalImporte;

    private String monedaSimbolo;

    private ArrayList<ImpuestoTB> arrayArticulosImpuesto;

    private BillPrintable billPrintable;

    private VBox hbEncabezado;

    private VBox hbDetalleCabecera;

    private VBox hbPie;

    private boolean unidades_cambio_cantidades;

    private boolean unidades_cambio_precio;

    private boolean unidades_cambio_descuento;

    private boolean valormonetario_cambio_cantidades;

    private boolean valormonetario_cambio_precio;

    private boolean valormonetario_cambio_decuento;

    private boolean medida_cambio_cantidades;

    private boolean medida_cambio_precio;

    private boolean medida_cambio_decuento;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hbEncabezado = new VBox();
        hbDetalleCabecera = new VBox();
        hbPie = new VBox();

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
                    openWindowListaPrecios("Lista de precios");
                    event.consume();
                } else if (event.getCode() == KeyCode.F9) {
                    short value = Tools.AlertMessageConfirmation(window, "Venta", "¿Está seguro de borrar la venta?");
                    if (value == 1) {
                        resetVenta();
                        event.consume();
                    }
                    event.consume();
                } else if (event.getCode() == KeyCode.F10) {
                    resetVenta();
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
        monedaSimbolo = "M";
        initTable();
        loadWindow();
    }

    private void loadWindow() {
        cbComprobante.getItems().clear();
        TipoDocumentoADO.GetDocumentoCombBox().forEach(e -> {
            cbComprobante.getItems().add(new TipoDocumentoTB(e.getIdTipoDocumento(), e.getNombre(), e.isPredeterminado(), e.getNombreDocumento()));
        });
        if (!cbComprobante.getItems().isEmpty()) {
            for (int i = 0; i < cbComprobante.getItems().size(); i++) {
                if (cbComprobante.getItems().get(i).isPredeterminado() == true) {
                    cbComprobante.getSelectionModel().select(i);
                    Session.DEFAULT_COMPROBANTE = i;
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

        Session.TICKET_SIMBOLOMONEDA = monedaSimbolo;
        lblMoneda.setText(monedaSimbolo);
        lblSubTotalMoneda.setText(monedaSimbolo);
        lblDescuentoMoneda.setText(monedaSimbolo);
        lblSubImporteMoneda.setText(monedaSimbolo);
        lblImporteTotalMoneda.setText(monedaSimbolo);
        lblTotalPagarMoneda.setText(monedaSimbolo);

        arrayArticulosImpuesto = new ArrayList<>();
        ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> {
            arrayArticulosImpuesto.add(new ImpuestoTB(e.getIdImpuesto(), e.getNombreImpuesto(), e.getValor(), e.getPredeterminado()));
        });

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
            hbBotonesInferior.getChildren().remove(btnQuitar);
        }
        if (privilegioTBs.get(9).getIdPrivilegio() != 0 && !privilegioTBs.get(9).isEstado()) {
            hbBotonesInferior.getChildren().remove(btnMovimiento);
        }
        if (privilegioTBs.get(10).getIdPrivilegio() != 0 && !privilegioTBs.get(10).isEstado()) {
            hbBotonesInferior.getChildren().remove(btnImpresora);
        }
        if (privilegioTBs.get(11).getIdPrivilegio() != 0 && !privilegioTBs.get(11).isEstado()) {
            hbBotonesInferior.getChildren().remove(btnCancelar);
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
            hbBotonesInferior.getChildren().remove(btnVentasPorDia);
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
            stage.setOnHiding(w -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();
            controller.fillSuministrosTablePaginacion();
        } catch (IOException ex) {
            System.out.println("openWindowArticulos():" + ex.getLocalizedMessage());
        }
    }

    private void openWindowVentaProceso() {
        try {
            if (tvList.getItems().isEmpty()) {
                Tools.AlertMessageWarning(window, "Ventas", "Debes agregar artículos a la venta");
            } else if (cbComprobante.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(window, "Ventas", "Seleccione el tipo de documento");
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
                stage.setOnHiding(w -> {
                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                });
                stage.show();
                VentaTB ventaTB = new VentaTB();
                //ventaTB.setCliente(idCliente);
                ventaTB.setVendedor(Session.USER_ID);
                ventaTB.setComprobante(cbComprobante.getSelectionModel().getSelectedIndex() >= 0
                        ? cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento()
                        : 0
                );
                ventaTB.setComprobanteName(cbComprobante.getSelectionModel().getSelectedIndex() >= 0
                        ? cbComprobante.getSelectionModel().getSelectedItem().getNombre()
                        : "");
                ventaTB.setMoneda(cbMoneda.getSelectionModel().getSelectedIndex() >= 0 ? cbMoneda.getSelectionModel().getSelectedItem().getIdMoneda() : 0);
                ventaTB.setMonedaName(monedaSimbolo);
                ventaTB.setSerie(lblSerie.getText());
                ventaTB.setNumeracion(lblNumeracion.getText());
                ventaTB.setFechaVenta(Tools.getDate());
                ventaTB.setHoraVenta(Tools.getHour());
                ventaTB.setSubTotal(Double.parseDouble(lblValorVenta.getText()));
                ventaTB.setDescuento(Double.parseDouble(lblDescuento.getText()));
                ventaTB.setSubImporte(Double.parseDouble(lblSubImporte.getText()));
                ventaTB.setTotal(Double.parseDouble(lblImporteTotal.getText()));
                ventaTB.setObservaciones(txtObservacion.getText().trim());
                //ire a comprar algo para levantarme vale ok
                controller.setInitComponents(ventaTB, tvList, lblTotalPagar.getText());
            }
        } catch (IOException ex) {
            System.out.println("openWindowVentaProceso():" + ex.getLocalizedMessage());
        }
    }

    private void openWindowImpresora() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_IMPRESORA_TICKET);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxImpresoraTicketController controller = fXMLLoader.getController();
            controller.setInitVentaEstructuraController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Configurar impresora", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            controller.loadConfigurationDefauld();
            stage.show();
        } catch (IOException ex) {
            System.out.println("openWindowImpresora():" + ex.getLocalizedMessage());
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
                    stage.setOnHiding(w -> {
                        vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                    });
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

    private void openWindowListaPrecios(String title) {
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
                Stage stage = WindowStage.StageLoaderModal(parent, title, window.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.setOnHiding(w -> {
                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                    calculateTotales();
                });
//                stage.setOnHidden(W -> {
//                    openWindowCantidad(factor);
//                });
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
            controller.setInitVentaEstructuraController(this);
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
                    controller.initComponents(tvList.getSelectionModel().getSelectedItem());
                    //
                    Stage stage = WindowStage.StageLoaderModal(parent, "Modificar cantidades", window.getScene().getWindow());
                    stage.setResizable(false);
                    stage.sizeToScene();
                    stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
                    stage.show();
                    controller.getTxtCantidad().requestFocus();
                } catch (IOException ex) {

                }
            }
        } else {
            tvList.requestFocus();
            if (!tvList.getItems().isEmpty()) {
                tvList.getSelectionModel().select(0);
            }
        }

    }

    public void openWindowMostrarVentas() {

        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_VENTA_MOSTRAR);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxVentaMostrarController controller = fXMLLoader.getController();

            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Mostrar ventas", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();

        } catch (IOException ex) {

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
                    break;
                }
                case 2: {
                    tvList.getItems().add(suministro);
                    int index = tvList.getItems().size() - 1;
                    tvList.requestFocus();
                    tvList.getSelectionModel().select(index);
                    openWindowCambiarPrecio("Cambiar precio al Artículo", false);
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
        short value = Tools.AlertMessageConfirmation(window, "Venta", "¿Está seguro de borrar la venta?");
        if (value == 1) {
            resetVenta();
        }
    }

    public void calculateTotales() {

        tvList.getItems().forEach(e -> subTotal += e.getSubImporte());
        lblValorVenta.setText(Tools.roundingValue(subTotal, 2));
        subTotal = 0;

        tvList.getItems().forEach(e -> descuento += e.getDescuentoSumado());
        lblDescuento.setText((Tools.roundingValue(descuento * (-1), 2)));
        descuento = 0;

        tvList.getItems().forEach(e -> subTotalImporte += e.getSubImporteDescuento());
        lblSubImporte.setText(Tools.roundingValue(subTotalImporte, 2));
        subTotalImporte = 0;

        vbImpuestos.getChildren().clear();
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
                    addElementImpuesto(arrayArticulosImpuesto.get(k).getIdImpuesto() + "", arrayArticulosImpuesto.get(k).getNombreImpuesto(), monedaSimbolo, Tools.roundingValue(sumaElement, 2));
                    totalImpuestos += sumaElement;
                    addElement = false;
                    sumaElement = 0;
                }
            }
        }

        tvList.getItems().forEach(e -> totalImporte += e.getTotalImporte());
        lblImporteTotal.setText(Tools.roundingValue(totalImporte + totalImpuestos, 2));
        lblTotalPagar.setText(Tools.roundingValue(Double.parseDouble(Tools.roundingValue(totalImporte + totalImpuestos, 1)), 2));
        lblTotal.setText(Tools.roundingValue(Double.parseDouble(Tools.roundingValue(totalImporte + totalImpuestos, 1)), 2));
        totalImporte = 0;

    }

    public void resetVenta() {
        tvList.getItems().clear();
        lblMoneda.setText(monedaSimbolo);
        lblSubTotalMoneda.setText(monedaSimbolo);
        lblDescuentoMoneda.setText(monedaSimbolo);
        lblSubImporteMoneda.setText(monedaSimbolo);
        lblImporteTotalMoneda.setText(monedaSimbolo);
        lblTotalPagarMoneda.setText(monedaSimbolo);

        lblTotal.setText("0.00");
        lblValorVenta.setText("0.00");
        lblDescuento.setText("0.00");
        lblImporteTotal.setText("0.00");
        lblTotalPagar.setText("0.00");

        cbComprobante.getItems().clear();
        TipoDocumentoADO.GetDocumentoCombBox().forEach(e -> {
            cbComprobante.getItems().add(new TipoDocumentoTB(e.getIdTipoDocumento(), e.getNombre(), e.isPredeterminado(), e.getNombreDocumento()));
        });
        if (!cbComprobante.getItems().isEmpty()) {
            for (int i = 0; i < cbComprobante.getItems().size(); i++) {
                if (cbComprobante.getItems().get(i).isPredeterminado() == true) {
                    cbComprobante.getSelectionModel().select(i);
                    Session.DEFAULT_COMPROBANTE = i;
                    break;
                }
            }

            if (cbComprobante.getSelectionModel().getSelectedIndex() >= 0) {
                String[] array = ComprobanteADO.GetSerieNumeracionEspecifico(cbComprobante.getSelectionModel().getSelectedItem().getIdTipoDocumento()).split("-");
                lblSerie.setText(array[0]);
                lblNumeracion.setText(array[1]);
            }

        }
        cbComprobante.getSelectionModel().select(Session.DEFAULT_COMPROBANTE);

        cbMoneda.getItems().clear();
        MonedaADO.GetMonedasCombBox().forEach(e -> {
            cbMoneda.getItems().add(new MonedaTB(e.getIdMoneda(), e.getNombre(), e.getSimbolo(), e.getPredeterminado()));
        });
        if (!cbMoneda.getItems().isEmpty()) {
            for (int i = 0; i < cbMoneda.getItems().size(); i++) {
                if (cbMoneda.getItems().get(i).getPredeterminado() == true) {
                    cbMoneda.getSelectionModel().select(i);
                    break;
                }
            }
        }

        arrayArticulosImpuesto.clear();
        ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> {
            arrayArticulosImpuesto.add(new ImpuestoTB(e.getIdImpuesto(), e.getNombreImpuesto(), e.getValor(), e.getPredeterminado()));
        });

        txtSearch.requestFocus();

        calculateTotales();
    }

    private void loadTicket() {
        billPrintable.loadEstructuraTicket(Session.RUTA_TICKET_VENTA, hbEncabezado, hbDetalleCabecera, hbPie);
    }

    public void imprimirVenta(String documento, TableView<SuministroTB> tvList, String subTotal, String descuento, String importeTotal, String total, double efec, double vuel, String ticket, String codigoVenta, String numCliente, String infoCliente) {
        if (Session.ESTADO_IMPRESORA && Session.NOMBRE_IMPRESORA != null) {
            loadEstructura(Session.NOMBRE_IMPRESORA, Session.CORTAPAPEL_IMPRESORA, documento, tvList, subTotal, descuento, importeTotal, total, efec, vuel, ticket, codigoVenta, numCliente, infoCliente);
        } else {
            Tools.AlertMessageWarning(window, "Venta", "No esta configurado la impresora :D");
        }
    }

    public void imprimirPrueba(String nombre_impresora, boolean costar, String documento, TableView<SuministroTB> tvList, String subTotal, String descuento, String importeTotal, String total, double efec, double vuel, String ticket, String codigoVenta, String numCliente, String infoCliente) {
        loadEstructura(nombre_impresora, costar, documento, tvList, subTotal, descuento, importeTotal, total, efec, vuel, ticket, codigoVenta, numCliente, infoCliente);
    }

    private void loadEstructura(String nombre_impresora, boolean cortar, String documento, TableView<SuministroTB> tvList, String subTotal, String descuento, String importeTotal, String total, double efec, double vuel, String ticket, String codigoVenta, String numCliente, String infoCliente) {
        loadTicket();
        ArrayList<HBox> object = new ArrayList<>();
        int rows = 0;
        int lines = 0;
        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
            object.add((HBox) hbEncabezado.getChildren().get(i));
            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
            rows++;
            lines += billPrintable.hbEncebezado(box, documento, ticket, numCliente, infoCliente);
        }

        for (int m = 0; m < tvList.getItems().size(); m++) {
            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                HBox hBox = new HBox();
                hBox.setId("dc_" + m + "" + i);
                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                rows++;
                lines += billPrintable.hbDetalle(hBox, box, tvList.getItems(), m);
                object.add(hBox);
            }
        }
        Session.TICKET_CODIGOVENTA = codigoVenta;
        for (int i = 0; i < hbPie.getChildren().size(); i++) {
            object.add((HBox) hbPie.getChildren().get(i));
            HBox box = ((HBox) hbPie.getChildren().get(i));
            rows++;
            lines += billPrintable.hbPie(box, subTotal, descuento, importeTotal, total, efec, vuel, numCliente, infoCliente);
        }
        billPrintable.modelTicket(window, rows + lines + 1 + 5, lines, object, "Ticket", "Error el imprimir el ticket.", nombre_impresora, cortar);

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

    public double getTaxValue(int impuesto) {
        double valor = 0;
        for (int i = 0; i < arrayArticulosImpuesto.size(); i++) {
            if (arrayArticulosImpuesto.get(i).getIdImpuesto() == impuesto) {
                valor = arrayArticulosImpuesto.get(i).getValor();
                break;
            }
        }
        return valor;
    }

    public String getTaxName(int impuesto) {
        String valor = "";
        for (int i = 0; i < arrayArticulosImpuesto.size(); i++) {
            if (arrayArticulosImpuesto.get(i).getIdImpuesto() == impuesto) {
                valor = arrayArticulosImpuesto.get(i).getNombreImpuesto();
                break;
            }
        }
        return valor;
    }

    private void addElementImpuesto(String id, String titulo, String moneda, String total) {

        Label label = new Label(titulo);
        label.setStyle("-fx-text-fill:#1a2226;");
        label.getStyleClass().add("labelRoboto14");

        Text text = new Text(moneda);
        text.setStyle("-fx-fill:#1a2226");
        text.getStyleClass().add("labelRobotoMedium16");
        Text text1 = new Text(total);
        text1.setStyle("-fx-fill:#1a2226");
        text1.getStyleClass().add("labelRobotoMedium16");

        HBox box = new HBox(text, text1);
        box.setStyle("-fx-spacing: 0.4166666666666667em;");

        HBox hBox = new HBox(label, box);
        hBox.setId(id);
        hBox.setStyle("-fx-spacing: 0.8333333333333334em;");

        vbImpuestos.getChildren().add(hBox);
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
            openWindowListaPrecios("Lista de precios");
        }
    }

    @FXML
    private void onActionListaPrecios(ActionEvent event) {
        openWindowListaPrecios("Lista de precios");
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
            SuministroTB a = SuministroADO.Get_Suministro_By_Search(txtSearch.getText().trim());
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

                Button button = new Button();
                button.getStyleClass().add("buttonBorder");
                ImageView view = new ImageView(new Image("/view/image/remove.png"));
                view.setFitWidth(24);
                view.setFitHeight(24);
                button.setGraphic(view);
                button.setOnAction(buttonEvent -> {
                    tvList.getItems().remove(suministroTB);
                    calculateTotales();
                });
                button.setOnKeyPressed(buttonEvent -> {
                    if (buttonEvent.getCode() == KeyCode.ENTER) {
                        tvList.getItems().remove(suministroTB);
                        calculateTotales();
                    }
                });
                suministroTB.setRemover(button);

                getAddArticulo(suministroTB);
                txtSearch.clear();
                txtSearch.requestFocus();

            } else {

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
            lblMoneda.setText(monedaSimbolo);
            lblSubTotalMoneda.setText(monedaSimbolo);
            lblDescuentoMoneda.setText(monedaSimbolo);
            lblSubImporteMoneda.setText(monedaSimbolo);
            lblImporteTotalMoneda.setText(monedaSimbolo);
            lblTotalPagarMoneda.setText(monedaSimbolo);
            calculateTotales();
        }
    }

    @FXML
    private void onKeyPressedRemover(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            removeArticulo();
        }
    }

    @FXML
    private void onActionRemover(ActionEvent event) {
        removeArticulo();
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
    private void onKeyPressedImprimir(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowImpresora();
        }
    }

    @FXML
    private void onActionImprimir(ActionEvent event) {
        openWindowImpresora();
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

    public String obtenerTipoComprobante() {
        return cbComprobante.getSelectionModel().getSelectedItem().getNombre();
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
