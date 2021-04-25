package controller.operaciones.cotizacion;

import controller.configuracion.impresoras.FxOpcionesImprimirController;
import controller.contactos.clientes.FxClienteProcesoController;
import controller.inventario.suministros.FxSuministrosListaController;
import controller.menus.FxPrincipalController;
import controller.tools.ConvertMonedaCadena;
import controller.tools.FilesRouters;
import controller.tools.SearchComboBox;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.Parent;
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
import model.ClienteADO;
import model.ClienteTB;
import model.CotizacionADO;
import model.CotizacionTB;
import model.DetalleCotizacionTB;
import model.MonedaADO;
import model.MonedaTB;
import model.SuministroADO;
import model.SuministroTB;

public class FxCotizacionController implements Initializable {

    @FXML
    private AnchorPane apWindow;
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
    @FXML
    private HBox hbBody;
    @FXML
    private HBox hbLoad;
    @FXML
    private Label lblMessageLoad;
    @FXML
    private Button btnAceptarLoad;

    private FxPrincipalController fxPrincipalController;

    private String idCotizacion;

    private ConvertMonedaCadena monedaCadena;

    private boolean stateSearch;

    private double importeBruto;

    private double descuentoBruto;

    private double subImporteNeto;

    private double impuestoNeto;

    private double importeNeto;

    private String monedaSimbolo;

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
                "-" + Tools.roundingValue(cellData.getValue().getDescuento(), 2)));
        tcImpuesto.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getImpuestoNombre()));
        tcImporte.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getImporteNeto(), 2)));

        tcPrecio.setCellFactory(TextFieldTableCell.forTableColumn());
        tcPrecio.setOnEditCommit(data -> {
//            final Double value = Tools.isNumeric(data.getNewValue())
//                    ? (Double.parseDouble(data.getNewValue()) <= 0 ? Double.parseDouble(data.getOldValue()) : Double.parseDouble(data.getNewValue()))
//                    : Double.parseDouble(data.getOldValue());
//            SuministroTB suministroTB = data.getTableView().getItems().get(data.getTablePosition().getRow());
//
//            double valor_sin_impuesto = value / ((suministroTB.getImpuestoValor() / 100.00) + 1);
//            double impuesto_generado = valor_sin_impuesto * (suministroTB.getImpuestoValor() / 100.00);
//
//            double descuento = suministroTB.getDescuento();
//            double porcentajeRestante = valor_sin_impuesto * (descuento / 100.00);
//            double preciocalculado = valor_sin_impuesto - porcentajeRestante;
//
//            suministroTB.setDescuento(descuento);
//            suministroTB.setDescuentoCalculado(porcentajeRestante);
//            suministroTB.setDescuentoSumado(porcentajeRestante * suministroTB.getCantidad());
//
//            suministroTB.setPrecioVentaGeneralUnico(valor_sin_impuesto);
//            suministroTB.setPrecioVentaGeneralReal(preciocalculado);
//
//            double impuesto = Tools.calculateTax(suministroTB.getImpuestoValor(), suministroTB.getPrecioVentaGeneralReal());
//
//            suministroTB.setImpuestoSumado(suministroTB.getCantidad() * impuesto);
//            suministroTB.setPrecioVentaGeneral(suministroTB.getPrecioVentaGeneralReal() + impuesto);
//
//            suministroTB.setImporteBruto(suministroTB.getPrecioVentaGeneralUnico() * suministroTB.getCantidad());
//            suministroTB.setSubImporteNeto(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
//            suministroTB.setImporteNeto(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneral());
//
//            tvList.refresh();
//            calculateTotales();
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

    private void onEventCliente() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_CLIENTE_PROCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxClienteProcesoController controller = fXMLLoader.getController();
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Cliente", apWindow.getScene().getWindow());
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
            controller.setInitCotizacionEstructuraController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Producto", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.setOnShown(w -> controller.getTxtSearch().requestFocus());
            stage.show();
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

            suministroTB.setImporteBruto(suministroTB.getPrecioVentaGeneralUnico() * suministroTB.getCantidad());
            suministroTB.setSubImporteNeto(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
            suministroTB.setImporteNeto(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneral());

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
        if (validateDuplicateArticulo(suministro)) {
            for (int i = 0; i < tvList.getItems().size(); i++) {
                if (tvList.getItems().get(i).getIdSuministro().equalsIgnoreCase(suministro.getIdSuministro())) {
                    SuministroTB suministroTB = tvList.getItems().get(i);
                    suministroTB.setPrecioVentaGeneral(suministro.getPrecioVentaGeneral());
                    suministroTB.setCantidad(suministro.getCantidad());
                    double descuento = suministroTB.getDescuento();
                    double precioDescuento = suministroTB.getPrecioVentaGeneral() - descuento;
                    suministroTB.setImporteNeto(suministroTB.getCantidad() * precioDescuento);
                    tvList.refresh();
                    tvList.getSelectionModel().select(i);
                    calculateTotales();
                    break;
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

    public boolean validateDuplicateArticulo(SuministroTB suministroTB) {
        boolean ret = false;
        for (int i = 0; i < tvList.getItems().size(); i++) {
            if (tvList.getItems().get(i).getClave().equals(suministroTB.getClave())) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public void calculateTotales() {
        importeBruto = 0;
        tvList.getItems().forEach(e -> {
            double descuento = e.getDescuento();
            double precioDescuento = e.getPrecioVentaGeneral() - descuento;
            double subPrecio = Tools.calculateTaxBruto(e.getImpuestoValor(), precioDescuento);
            double precioBruto = subPrecio + descuento;
            importeBruto += precioBruto * e.getCantidad();
        });
        lblValorVenta.setText(monedaSimbolo + " " + Tools.roundingValue(importeBruto, 2));

        descuentoBruto = 0;
        tvList.getItems().forEach(e -> {
            double descuento = e.getDescuento();
            descuentoBruto += e.getCantidad() * descuento;
        });
        lblDescuento.setText(monedaSimbolo + " " + (Tools.roundingValue(descuentoBruto * (-1), 2)));

        subImporteNeto = 0;
        tvList.getItems().forEach(e -> {
            double descuento = e.getDescuento();
            double costoDescuento = e.getPrecioVentaGeneral() - descuento;
            double subCosto = Tools.calculateTaxBruto(e.getImpuestoValor(), costoDescuento);
            subImporteNeto += e.getCantidad() * subCosto;
        });
        lblSubImporte.setText(monedaSimbolo + " " + Tools.roundingValue(subImporteNeto, 2));

        impuestoNeto = 0;
        tvList.getItems().forEach(e -> {
            double descuento = e.getDescuento();
            double costoDescuento = e.getPrecioVentaGeneral() - descuento;
            double subCosto = Tools.calculateTaxBruto(e.getImpuestoValor(), costoDescuento);
            double impuesto = Tools.calculateTax(e.getImpuestoValor(), subCosto);
            impuestoNeto += e.getCantidad() * impuesto;
        });

        importeNeto = 0;
        importeNeto = subImporteNeto + impuestoNeto;
        lblImpuesto.setText(monedaSimbolo + " " + Tools.roundingValue(impuestoNeto, 2));
        lblImporteTotal.setText(monedaSimbolo + " " + Tools.roundingValue(importeNeto, 2));
    }

    private void cancelarVenta() {
        short value = Tools.AlertMessageConfirmation(apWindow, "Cotización", "¿Está seguro de limpiar la cotización?");
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
        for (int i = 0; i < cbMoneda.getItems().size(); i++) {
            if (cbMoneda.getItems().get(i).getPredeterminado()) {
                cbMoneda.getSelectionModel().select(i);
                monedaSimbolo = cbMoneda.getItems().get(i).getSimbolo();
                break;
            }
        }

        calculateTotales();
    }

    public void loadCotizacion(String idCotizacion) {
        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<Object> task = new Task<Object>() {
            @Override
            public Object call() {
                return CotizacionADO.CargarCotizacionVenta(idCotizacion);
            }
        };
        task.setOnSucceeded(w -> {
            Object object = task.getValue();
            if (object instanceof CotizacionTB) {
                CotizacionTB cotizacionTB = (CotizacionTB) object;
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

                ObservableList<SuministroTB> cotizacionTBs = cotizacionTB.getDetalleSuministroTBs();
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
                    tvList.setItems(cotizacionTBs);
                }
                calculateTotales();
                Tools.AlertMessageInformation(apWindow, "Ventas", "Los datos se cargaron correctamente.");
                fxPrincipalController.closeFondoModal();
            } else {
                Tools.AlertMessageWarning(apWindow, "Ventas", (String) object);
                fxPrincipalController.closeFondoModal();
            }

        });
        task.setOnFailed(w -> {
            Tools.AlertMessageError(apWindow, "Venta", "Error en la ejecución, intente nuevamente.");
            fxPrincipalController.closeFondoModal();
        });
        task.setOnScheduled(w -> {
            fxPrincipalController.openFondoModal();
        });
        exec.execute(task);

        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    public void openModalImpresion(String idCotizacion) {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_OPCIONES_IMPRIMIR);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxOpcionesImprimirController controller = fXMLLoader.getController();
            controller.loadDataCotizacion(idCotizacion);
            controller.setInitOpcionesImprimirCotizacion(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Imprimir", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
        } catch (IOException ex) {
            System.out.println("Controller Modal Impresión: " + ex.getLocalizedMessage());
        }
    }

    private void onEventGuardar() {
        if (cbCliente.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(apWindow, "Cotización", "Seleccione un cliente.");
            cbCliente.requestFocus();
        } else if (dtFechaCotizacion.getValue() == null) {
            Tools.AlertMessageWarning(apWindow, "Cotización", "Ingrese un fecha valida.");
            dtFechaCotizacion.requestFocus();
        } else if (tvList.getItems().isEmpty()) {
            Tools.AlertMessageWarning(apWindow, "Cotización", "Ingrese productos a la lista.");
            txtSearch.requestFocus();
        } else if (cbMoneda.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(apWindow, "Cotización", "Seleccione la moneda a usar.");
            cbMoneda.requestFocus();
        } else {
            short value = Tools.AlertMessageConfirmation(apWindow, "Cotización", "¿Está seguro de continuar?");
            if (value == 1) {
                ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
                    Thread t = new Thread(runnable);
                    t.setDaemon(true);
                    return t;
                });

                Task<Object> task = new Task<Object>() {
                    @Override
                    protected Object call() {
                        CotizacionTB cotizacionTB = new CotizacionTB();
                        cotizacionTB.setIdCotizacion(idCotizacion);
                        cotizacionTB.setIdCliente(cbCliente.getSelectionModel().getSelectedItem().getIdCliente());
                        cotizacionTB.setIdVendedor(Session.USER_ID);
                        cotizacionTB.setIdMoneda(cbMoneda.getSelectionModel().getSelectedItem().getIdMoneda());
                        cotizacionTB.setFechaCotizacion(Tools.getDatePicker(dtFechaCotizacion));
                        cotizacionTB.setHoraCotizacion(Tools.getHour());
                        cotizacionTB.setFechaVencimiento(Tools.getDate());
                        cotizacionTB.setHoraVencimiento(Tools.getHour());
                        cotizacionTB.setSubTotal(importeBruto);
                        cotizacionTB.setDescuento(descuentoBruto);
                        cotizacionTB.setImpuesto(impuestoNeto);
                        cotizacionTB.setTotal(importeNeto);
                        cotizacionTB.setEstado((short) 1);
                        cotizacionTB.setObservaciones(txtObservacion.getText().trim());

                        ArrayList<DetalleCotizacionTB> detalleCotizacionTBs = new ArrayList();
                        tvList.getItems().stream().map((suministroTB) -> {
                            DetalleCotizacionTB detalleCotizacionTB = new DetalleCotizacionTB();
                            detalleCotizacionTB.setIdSuministros(suministroTB.getIdSuministro());
                            detalleCotizacionTB.setCantidad(suministroTB.getCantidad());
                            detalleCotizacionTB.setPrecio(suministroTB.getPrecioVentaGeneral());
                            detalleCotizacionTB.setDescuento(suministroTB.getDescuento());
                            detalleCotizacionTB.setIdImpuesto(suministroTB.getImpuestoId());
                            return detalleCotizacionTB;
                        }).forEachOrdered((detalleCotizacionTB) -> {
                            detalleCotizacionTBs.add(detalleCotizacionTB);
                        });
                        cotizacionTB.setDetalleCotizacionTBs(detalleCotizacionTBs);

                        return CotizacionADO.GuardarCotizacion(cotizacionTB);
                    }
                };

                task.setOnScheduled(w -> {
                    hbBody.setDisable(true);
                    hbLoad.setVisible(true);
                    lblMessageLoad.setText("Procesando información...");
                    lblMessageLoad.setTextFill(Color.web("#ffffff"));
                });
                task.setOnFailed(w -> {
                    btnAceptarLoad.setVisible(true);
                    lblMessageLoad.setText(task.getException().getLocalizedMessage());
                    lblMessageLoad.setTextFill(Color.web("#ff6d6d"));
                });
                task.setOnSucceeded(w -> {
                    Object object = task.getValue();
                    if (object instanceof String[]) {
                        String result[] = (String[]) object;
                        if (result[0].equalsIgnoreCase("1")) {
                            lblMessageLoad.setText("Se actualizó correctamente la cotización.");
                            lblMessageLoad.setTextFill(Color.web("#ffffff"));
                            btnAceptarLoad.setVisible(true);
                            openModalImpresion(result[1]);
                        } else {
                            lblMessageLoad.setText("Se registro corectamente la cotización.");
                            lblMessageLoad.setTextFill(Color.web("#ffffff"));
                            btnAceptarLoad.setVisible(true);
                            openModalImpresion(result[1]);
                        }
                    } else {
                        lblMessageLoad.setText((String) object);
                        lblMessageLoad.setTextFill(Color.web("#ff6d6d"));
                        btnAceptarLoad.setVisible(true);
                    }
                });

                exec.execute(task);

                if (!exec.isShutdown()) {
                    exec.shutdown();
                }
            }

        }
    }

    public void openWindowCotizaciones() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_COTIZACION_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxCotizacionListaController controller = fXMLLoader.getController();
            controller.setInitCotizacionListaController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Mostrar Cotizaciones", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.setOnShown(w -> controller.initLoad());
            stage.show();
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

    @FXML
    private void onKeyReleasedWindow(KeyEvent event) {
        if (null != event.getCode()) {
            switch (event.getCode()) {
                case F1:
                    onEventGuardar();
                    break;
                case F2:
                    openWindowSuministro();
                    break;
                case F3:
                    openWindowCotizaciones();
                    break;
                case F4:
                    cancelarVenta();
                    break;
                default:
                    break;
            }
        }
    }

    @FXML
    private void onKeyPressedAceptarLoad(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            hbBody.setDisable(false);
            hbLoad.setVisible(false);
            resetVenta();
        }
    }

    @FXML
    private void onActionAceptarLoad(ActionEvent event) {
        hbBody.setDisable(false);
        hbLoad.setVisible(false);
        resetVenta();
    }

    public TableView<SuministroTB> getTvList() {
        return tvList;
    }

    public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }

}
