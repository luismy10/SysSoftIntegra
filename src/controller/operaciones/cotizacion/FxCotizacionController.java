package controller.operaciones.cotizacion;

import controller.contactos.clientes.FxClienteProcesoController;
import controller.inventario.suministros.FxSuministrosListaController;
import controller.reporte.FxReportViewController;
import controller.tools.ConvertMonedaCadena;
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
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.ClienteADO;
import model.ClienteTB;
import model.CotizacionADO;
import model.CotizacionTB;
import model.DetalleCotizacionTB;
import model.MonedaADO;
import model.MonedaTB;
import model.SuministroADO;
import model.SuministroTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FxCotizacionController implements Initializable {

    @FXML
    private HBox hbWindow;
    @FXML
    private ComboBox<ClienteTB> cbCliente;
    @FXML
    private ComboBox<MonedaTB> cbMoneda;
    @FXML
    private DatePicker dtFechaCotizacion;
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
    private Label lblSubImporte;
    @FXML
    private Label lblDescuento;
    @FXML
    private Label lblValorVenta;
    @FXML
    private Label lblImpuesto;
    @FXML
    private Label lblImporteTotal;
    @FXML
    private TextField txtObservacion;
    @FXML
    private Label lblProceso;

    private AnchorPane vbPrincipal;

    private String idCotizacion;

    private ConvertMonedaCadena monedaCadena;

    private boolean stateSearch;

    private double subTotal;

    private double descuentoTotal;

    private double subTotalImporte;

    private double totalImporte;

    private double totalImpuesto;

    private double total;

    private String monedaSimbolo;

    private Alert alert = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        monedaSimbolo = "M";
        idCotizacion = "";
        stateSearch = false;
        monedaCadena = new ConvertMonedaCadena();
        Tools.actualDate(Tools.getDate(), dtFechaCotizacion);
        loadTableView();
        loadComboBoxCliente();
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
    }

    private void loadTableView() {
        tcOpcion.setCellValueFactory(new PropertyValueFactory<>("remover"));
        tcArticulo.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getClave() + "\n" + cellData.getValue().getNombreMarca()));
        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getCantidad(), 2)));
        tcPrecio.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getPrecioVentaGeneral(), 2)));
        tcDescuento.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getDescuento(), 0) + "%"));
        tcImpuesto.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getImpuestoNombre()));
        tcImporte.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getPrecioVentaGeneral() * cellData.getValue().getCantidad(), 2)));

        tcCantidad.setCellFactory(TextFieldTableCell.forTableColumn());
        tcCantidad.setOnEditCommit(data -> {
            final Double value = Tools.isNumeric(data.getNewValue())
                    ? (Double.parseDouble(data.getNewValue()) <= 0 ? Double.parseDouble(data.getOldValue()) : Double.parseDouble(data.getNewValue()))
                    : Double.parseDouble(data.getOldValue());
            SuministroTB suministroTB = data.getTableView().getItems().get(data.getTablePosition().getRow());

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
        });

        tcPrecio.setCellFactory(TextFieldTableCell.forTableColumn());
        tcPrecio.setOnEditCommit(data -> {
            final Double value = Tools.isNumeric(data.getNewValue())
                    ? (Double.parseDouble(data.getNewValue()) <= 0 ? Double.parseDouble(data.getOldValue()) : Double.parseDouble(data.getNewValue()))
                    : Double.parseDouble(data.getOldValue());
            SuministroTB suministroTB = data.getTableView().getItems().get(data.getTablePosition().getRow());

            double valor_sin_impuesto = value / ((suministroTB.getImpuestoValor() / 100.00) + 1);
            double impuesto_generado = valor_sin_impuesto * (suministroTB.getImpuestoValor() / 100.00);

            double descuento = suministroTB.getDescuento();
            double porcentajeRestante = valor_sin_impuesto * (descuento / 100.00);
            double preciocalculado = valor_sin_impuesto - porcentajeRestante;

            suministroTB.setDescuento(descuento);
            suministroTB.setDescuentoCalculado(porcentajeRestante);
            suministroTB.setDescuentoSumado(porcentajeRestante * suministroTB.getCantidad());

            suministroTB.setPrecioVentaGeneralUnico(valor_sin_impuesto);
            suministroTB.setPrecioVentaGeneralReal(preciocalculado);

            double impuesto = Tools.calculateTax(suministroTB.getImpuestoValor(), suministroTB.getPrecioVentaGeneralReal());

            suministroTB.setImpuestoSumado(suministroTB.getCantidad() * impuesto);
            suministroTB.setPrecioVentaGeneral(suministroTB.getPrecioVentaGeneralReal() + impuesto);

            suministroTB.setSubImporte(suministroTB.getPrecioVentaGeneralUnico() * suministroTB.getCantidad());
            suministroTB.setSubImporteDescuento(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
            suministroTB.setTotalImporte(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());

            tvList.refresh();
            calculateTotales();
        });

        tcOpcion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.08));
        tcArticulo.prefWidthProperty().bind(tvList.widthProperty().multiply(0.30));
        tcCantidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcPrecio.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcDescuento.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcImpuesto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
        tcImporte.prefWidthProperty().bind(tvList.widthProperty().multiply(0.12));
    }

    private void loadComboBoxCliente() {
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
            clienteTBs.forEach(e -> cbCliente.getItems().add(e));
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
    }

    private void reportA4(CotizacionTB cotizacionTB, ArrayList<SuministroTB> suministroTBs) {
        try {

            double importeBruto = 0;
            double descuento = 0;
            double importeDescontado = 0;
            double impuestoGenerado = 0;
            double importeNeto = 0;

            for (SuministroTB sm : suministroTBs) {
                importeBruto += sm.getSubImporte();
                descuento += sm.getDescuentoSumado();
                importeDescontado += sm.getSubImporteDescuento();
                impuestoGenerado += sm.getImpuestoSumado();
                importeNeto += sm.getTotalImporte() + sm.getImpuestoSumado();
            }

            InputStream imgInputStreamIcon = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            InputStream imgInputStream = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            if (Session.COMPANY_IMAGE != null) {
                imgInputStream = new ByteArrayInputStream(Session.COMPANY_IMAGE);
            }

            InputStream dir = getClass().getResourceAsStream("/report/Cotizacion.jasper");

            Map map = new HashMap();
            map.put("LOGO", imgInputStream);
            map.put("ICON", imgInputStreamIcon);
            map.put("EMPRESA", Session.COMPANY_RAZON_SOCIAL);
            map.put("DIRECCION", Session.COMPANY_DOMICILIO);
            map.put("TELEFONOCELULAR", "TELÉFONO: " + Session.COMPANY_TELEFONO + " CELULAR: " + Session.COMPANY_CELULAR);
            map.put("EMAIL", "EMAIL: " + Session.COMPANY_EMAIL);

            map.put("DOCUMENTOEMPRESA", "R.U.C " + Session.COMPANY_NUMERO_DOCUMENTO);
            map.put("NOMBREDOCUMENTO", "COTIZACIÓN");
            map.put("NUMERODOCUMENTO", "N° " + cotizacionTB.getIdCotizacion());

            map.put("DATOSCLIENTE", cotizacionTB.getClienteTB().getInformacion());
            map.put("DOCUMENTOCLIENTE", "");
            map.put("NUMERODOCUMENTOCLIENTE", cotizacionTB.getClienteTB().getNumeroDocumento());
            map.put("CELULARCLIENTE", cotizacionTB.getClienteTB().getCelular());
            map.put("EMAILCLIENTE", cotizacionTB.getClienteTB().getEmail());
            map.put("DIRECCIONCLIENTE", cotizacionTB.getClienteTB().getDireccion());

            map.put("FECHAEMISION", cotizacionTB.getFechaCotizacion());
            map.put("MONEDA", cotizacionTB.getMonedaTB().getNombre());
            map.put("CONDICIONPAGO", "");

            map.put("SIMBOLO", cotizacionTB.getMonedaTB().getSimbolo());
            map.put("VALORSOLES", monedaCadena.Convertir(Tools.roundingValue(importeNeto, 2), true, cotizacionTB.getMonedaTB().getNombre()));

            map.put("VALOR_VENTA", Tools.roundingValue(importeBruto, 2));
            map.put("DESCUENTO", Tools.roundingValue(descuento, 2));
            map.put("SUB_IMPORTE", Tools.roundingValue(importeDescontado, 2));
            map.put("IMPUESTO_TOTAL", Tools.roundingValue(impuestoGenerado, 2));
            map.put("IMPORTE_TOTAL", Tools.roundingValue(importeNeto, 2));
            map.put("OBSERVACION", cotizacionTB.getObservaciones());

            JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JRBeanCollectionDataSource(suministroTBs));

            URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxReportViewController controller = fXMLLoader.getController();
            controller.setJasperPrint(jasperPrint);
            controller.show();
            Stage stage = WindowStage.StageLoader(parent, "Cotizacion realizada");
            stage.setResizable(true);
            stage.show();
            stage.requestFocus();

        } catch (IOException | JRException ex) {
            Tools.AlertMessageError(hbWindow, "Reporte de Cotizacion", "Error al generar el reporte : " + ex.getLocalizedMessage());
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
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Cliente", hbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
            controller.setValueAdd();
        } catch (IOException ex) {
            System.out.println("Cliente controller en openWindowAddCliente()" + ex.getLocalizedMessage());
        }
    }

    private void openWindowSuministro() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_SUMINISTROS_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxSuministrosListaController controller = fXMLLoader.getController();
            controller.setInitCotizacionEstructuraController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Producto", hbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
            controller.fillSuministrosTable((short) 0, "");
        } catch (IOException ex) {
            System.out.println("openWindowArticulos():" + ex.getLocalizedMessage());
        }
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
            suministroTB.setImpuestoId(a.getImpuestoId());
            suministroTB.setImpuestoNombre(a.getImpuestoNombre());
            suministroTB.setImpuestoValor(a.getImpuestoValor());
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

    public void getAddArticulo(SuministroTB suministro) {
        if (validateDuplicateArticulo(tvList, suministro)) {
            for (int i = 0; i < tvList.getItems().size(); i++) {
                if (tvList.getItems().get(i).getIdSuministro().equalsIgnoreCase(suministro.getIdSuministro())) {
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
                }
            }
        } else {
            tvList.getItems().add(suministro);
            int index = tvList.getItems().size() - 1;
            tvList.getSelectionModel().select(index);
            calculateTotales();
            txtSearch.requestFocus();
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

    public void calculateTotales() {
        subTotal = 0;
        tvList.getItems().forEach(e -> subTotal += e.getSubImporte());
        lblValorVenta.setText(monedaSimbolo + " " + Tools.roundingValue(subTotal, 2));

        descuentoTotal = 0;
        tvList.getItems().forEach(e -> descuentoTotal += e.getDescuentoSumado());
        lblDescuento.setText(monedaSimbolo + " " + (Tools.roundingValue(descuentoTotal * (-1), 2)));

        subTotalImporte = 0;
        tvList.getItems().forEach(e -> subTotalImporte += e.getSubImporteDescuento());
        lblSubImporte.setText(monedaSimbolo + " " + Tools.roundingValue(subTotalImporte, 2));

        double totalImpuestos = 0;

        if (!tvList.getItems().isEmpty()) {
            for (int i = 0; i < tvList.getItems().size(); i++) {
                totalImpuestos += tvList.getItems().get(i).getImpuestoSumado();
            }
        }

        totalImporte = 0;
        totalImpuesto = 0;
        total = 0;
        tvList.getItems().forEach(e -> totalImporte += e.getTotalImporte());
        totalImpuesto = totalImpuestos;
        total = totalImporte + totalImpuesto;

        lblImpuesto.setText(monedaSimbolo + " " + Tools.roundingValue(totalImpuesto, 2));
        lblImporteTotal.setText(monedaSimbolo + " " + Tools.roundingValue(total, 2));
    }

    private void cancelarVenta() {
        short value = Tools.AlertMessageConfirmation(hbWindow, "Cotización", "¿Está seguro de limpiar la cotización?");
        if (value == 1) {
            resetVenta();
        }
    }

    public void resetVenta() {
        tvList.getItems().clear();
        txtSearch.setText("");
        txtSearch.requestFocus();
        cbCliente.getItems().clear();
        Tools.actualDate(Tools.getDate(), dtFechaCotizacion);
        txtObservacion.setText("");
        idCotizacion = "";
        lblProceso.setText("Cotización en proceso de registrar");
        lblProceso.setTextFill(Color.web("#0060e8"));
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
        calculateTotales();
    }

    private void onExecuteImpresion(String idCotizacion) {
        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                return CotizacionADO.CargarCotizacionReporte(idCotizacion);
            }
        };
        task.setOnScheduled(w -> {
            Tools.showAlertNotification("/view/image/information_large.png",
                    "Generando reporte",
                    "Se envió los datos para generar\n el reporte.",
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
            ArrayList<Object> objects = task.getValue();
            if (!objects.isEmpty()) {
                if (objects.get(0) != null && objects.get(1) != null) {
                    CotizacionTB cotizacionTB = (CotizacionTB) objects.get(0);
                    ObservableList<SuministroTB> cotizacionTBs = (ObservableList<SuministroTB>) objects.get(1);
                    ArrayList<SuministroTB> list = new ArrayList(cotizacionTBs);
                    reportA4(cotizacionTB, list);
                    Tools.showAlertNotification("/view/image/succes_large.png",
                            "Generando reporte",
                            "Se genero correctamente el reporte.",
                            Duration.seconds(5),
                            Pos.BOTTOM_RIGHT);
                }
            } else {
                Tools.showAlertNotification("/view/image/error_large.png",
                        "Generando reporte",
                        "Se producto al obtenener los datos.",
                        Duration.seconds(10),
                        Pos.CENTER);
            }
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    public void loadCotizacion(String idCotizacion) {
        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                return CotizacionADO.CargarCotizacionVenta(idCotizacion);
            }
        };
        task.setOnSucceeded(w -> {
            if (!task.isRunning()) {
                if (alert != null) {
                    ((Stage) (alert.getDialogPane().getScene().getWindow())).close();
                }
            }
            ArrayList<Object> objects = task.getValue();
            if (!objects.isEmpty()) {
                if (objects.get(0) != null) {
                    CotizacionTB cotizacionTB = (CotizacionTB) objects.get(0);
                    this.idCotizacion = idCotizacion;
                    for (MonedaTB monedaTB : cbMoneda.getItems()) {
                        if (monedaTB.getIdMoneda() == cotizacionTB.getIdMoneda()) {
                            cbMoneda.getSelectionModel().select(monedaTB);
                            monedaSimbolo = cbMoneda.getSelectionModel().getSelectedItem().getSimbolo();
                            break;
                        }
                    }

                    cbCliente.getItems().clear();
                    cbCliente.getItems().add(new ClienteTB(cotizacionTB.getClienteTB().getIdCliente(), cotizacionTB.getClienteTB().getNumeroDocumento(), cotizacionTB.getClienteTB().getInformacion(), cotizacionTB.getClienteTB().getCelular(), cotizacionTB.getClienteTB().getEmail(), cotizacionTB.getClienteTB().getDireccion()));
                    cbCliente.getSelectionModel().select(0);

                    lblProceso.setText("Cotización en proceso de actualizar");
                    lblProceso.setTextFill(Color.web("#c52700"));
                }
                if (objects.get(1) != null) {
                    ObservableList<SuministroTB> cotizacionTBs = (ObservableList<SuministroTB>) objects.get(1);
                    for (int i = 0; i < cotizacionTBs.size(); i++) {
                        SuministroTB suministroTB = cotizacionTBs.get(i);
                        suministroTB.getRemover().setOnAction(e -> {
                            tvList.getItems().remove(suministroTB);
                            calculateTotales();
                        });
                        suministroTB.getRemover().setOnKeyPressed(e -> {
                            if (e.getCode() == KeyCode.ENTER) {
                                tvList.getItems().remove(suministroTB);
                                calculateTotales();
                            }
                        });
                    }
                    tvList.setItems(cotizacionTBs);
                    calculateTotales();
                }
                Tools.AlertMessageInformation(hbWindow, "Ventas", "Los datos se cargaron correctamente.");
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            } else {
                Tools.AlertMessageWarning(hbWindow, "Ventas", "Se produjo un problema intente nuevamente.");
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            }

        });
        task.setOnFailed(w -> {
            if (alert != null) {
                ((Stage) (alert.getDialogPane().getScene().getWindow())).close();
            }
            Tools.AlertMessageError(hbWindow, "Venta", "Error en la ejecución, intente nuevamente.");
            vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
        });
        task.setOnScheduled(w -> {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            alert = Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.NONE, "Procesando Información...");

        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void onEventGuardar() {
        if (cbCliente.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(hbWindow, "Cotización", "Seleccione un cliente.");
            cbCliente.requestFocus();
        } else if (dtFechaCotizacion.getValue() == null) {
            Tools.AlertMessageWarning(hbWindow, "Cotización", "Ingrese un fecha valida.");
            dtFechaCotizacion.requestFocus();
        } else if (tvList.getItems().isEmpty()) {
            Tools.AlertMessageWarning(hbWindow, "Cotización", "Ingrese productos a la lista.");
            txtSearch.requestFocus();
        } else if (cbMoneda.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(hbWindow, "Cotización", "Seleccione la moneda a usar.");
            cbMoneda.requestFocus();
        } else {
            short value = Tools.AlertMessageConfirmation(hbWindow, "Cotización", "¿Está seguro de continuar?");
            if (value == 1) {
                CotizacionTB cotizacionTB = new CotizacionTB();
                cotizacionTB.setIdCotizacion(idCotizacion);
                cotizacionTB.setIdCliente(cbCliente.getSelectionModel().getSelectedItem().getIdCliente());
                cotizacionTB.setIdVendedor(Session.USER_ID);
                cotizacionTB.setIdMoneda(cbMoneda.getSelectionModel().getSelectedItem().getIdMoneda());
                cotizacionTB.setFechaCotizacion(Tools.getDatePicker(dtFechaCotizacion));
                cotizacionTB.setHoraCotizacion(Tools.getHour());
                cotizacionTB.setFechaVencimiento(Tools.getDate());
                cotizacionTB.setHoraVencimiento(Tools.getHour());
                cotizacionTB.setSubTotal(subTotal);
                cotizacionTB.setDescuento(descuentoTotal);
                cotizacionTB.setImpuesto(totalImpuesto);
                cotizacionTB.setTotal(total);
                cotizacionTB.setEstado((short) 1);
                cotizacionTB.setObservaciones(txtObservacion.getText().trim());

                ArrayList<DetalleCotizacionTB> detalleCotizacionTBs = new ArrayList();
                tvList.getItems().stream().map((suministroTB) -> {
                    DetalleCotizacionTB detalleCotizacionTB = new DetalleCotizacionTB();
                    detalleCotizacionTB.setIdSuministros(suministroTB.getIdSuministro());
                    detalleCotizacionTB.setCantidad(suministroTB.getCantidad());
                    detalleCotizacionTB.setPrecio(suministroTB.getPrecioVentaGeneralUnico());
                    detalleCotizacionTB.setDescuento(suministroTB.getDescuento());
                    detalleCotizacionTB.setIdImpuesto(suministroTB.getImpuestoId());
                    return detalleCotizacionTB;
                }).forEachOrdered((detalleCotizacionTB) -> {
                    detalleCotizacionTBs.add(detalleCotizacionTB);
                });
                cotizacionTB.setDetalleCotizacionTBs(detalleCotizacionTBs);

                String result[] = CotizacionADO.GuardarCotizacion(cotizacionTB);
                switch (result[0]) {
                    case "0":
                        Tools.AlertMessageInformation(hbWindow, "Cotización", "Se registro corectamente la cotización.");
                        resetVenta();
                        onExecuteImpresion(result[1]);
                        break;
                    case "1":
                        Tools.AlertMessageInformation(hbWindow, "Cotización", "Se actualizo corectamente la cotización.");
                        resetVenta();
                        onExecuteImpresion(result[1]);
                        break;
                    case "2":
                        Tools.AlertMessageError(hbWindow, "Cotización", result[1]);
                        break;
                    case "3":
                        Tools.AlertMessageError(hbWindow, "Cotización", result[1]);
                        break;
                    default:
                        Tools.AlertMessageError(hbWindow, "Cotización", "Problemas interno al momento de abrir el reporte.");
                        break;
                }
            }

        }
    }

    public void openWindowCotizaciones() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_COTIZACION_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxCotizacionListaController controller = fXMLLoader.getController();
            controller.setInitCotizacionListaController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Mostrar Cotizaciones", hbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
            controller.loadTable((short) 1, "", Tools.getDate(), Tools.getDate());
        } catch (IOException ex) {
            Tools.println("Error en la funcioón openWindowCotizaciones():" + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onKeyPressedCliente(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventCliente();
        }
    }

    @FXML
    private void onActionCliente(ActionEvent event) {
        onEventCliente();
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
    private void onKeyPressedGuardar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventGuardar();
        }
    }

    @FXML
    private void onActionGuardar(ActionEvent event) {
        onEventGuardar();

    }

    @FXML
    private void onKeyPressedArticulo(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowSuministro();
        }
    }

    @FXML
    private void onActionArticulo(ActionEvent event) {
        openWindowSuministro();
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
    private void onActionMoneda(ActionEvent event) {

    }

    @FXML
    private void onKeyPressedCotizaciones(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowCotizaciones();
        }
    }

    @FXML
    private void onActionCotizaciones(ActionEvent event) {
        openWindowCotizaciones();
    }

    public TableView<SuministroTB> getTvList() {
        return tvList;
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
