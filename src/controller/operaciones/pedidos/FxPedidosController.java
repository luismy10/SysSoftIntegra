package controller.operaciones.pedidos;

import controller.configuracion.impresoras.FxOpcionesImprimirController;
import controller.contactos.proveedores.FxProveedorProcesoController;
import controller.inventario.suministros.FxSuministrosListaController;
import controller.menus.FxPrincipalController;
import controller.tools.FilesRouters;
import controller.tools.SearchComboBox;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.binding.Bindings;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.MonedaADO;
import model.MonedaTB;
import model.PedidoADO;
import model.PedidoDetalleTB;
import model.PedidoTB;
import model.ProveedorADO;
import model.ProveedorTB;

public class FxPedidosController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private VBox vbBody;
    @FXML
    private HBox hbLoad;
    @FXML
    private Label lblMessageLoad;
    @FXML
    private Button btnAceptarLoad;
    @FXML
    private ComboBox<ProveedorTB> cbProveedor;
    @FXML
    private DatePicker txtFechaEmision;
    @FXML
    private DatePicker txtFechaVencimiento;
    @FXML
    private TableView<PedidoDetalleTB> tvList;
    @FXML
    private TableColumn<PedidoDetalleTB, Button> tcQuitar;
    @FXML
    private TableColumn<PedidoDetalleTB, String> tcCantidad;
    @FXML
    private TableColumn<PedidoDetalleTB, String> tcExistencia;
    @FXML
    private TableColumn<PedidoDetalleTB, String> tcStock;
    @FXML
    private TableColumn<PedidoDetalleTB, String> tcProducto;
    @FXML
    private TableColumn<PedidoDetalleTB, String> tcCostoProveedor;
    @FXML
    private TableColumn<PedidoDetalleTB, String> txtDescuento;
    @FXML
    private TableColumn<PedidoDetalleTB, String> txtImpuesto;
    @FXML
    private TableColumn<PedidoDetalleTB, String> tcImporte;
    @FXML
    private ComboBox<MonedaTB> cbMoneda;
    @FXML
    private TextField txtObservacion;
    @FXML
    private Label lblSubImporte;
    @FXML
    private Label lblDescuentoTotal;
    @FXML
    private Label lblImporteBruto;
    @FXML
    private Label lblImpuesto;
    @FXML
    private Label lblImporteNeto;
    @FXML
    private Button btnProducto;
    @FXML
    private Label lblProceso;

    private FxPrincipalController fxPrincipalController;

    private String idPedido;

    private String monedaSimbolo;

    private double importeBruto;

    private double descuentoTotal;

    private double subImporte;

    private double impuestoGenerado;

    private double importeNeto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.actualDate(Tools.getDate(), txtFechaEmision);
        Tools.actualDate(Tools.getDate(), txtFechaVencimiento);
        loadComponentProveedor();
        loadComponentMoneda();
        initTable();
    }

    private void loadComponentProveedor() {
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

    private void loadComponentMoneda() {
        monedaSimbolo = "M";
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

        lblImporteBruto.setText(monedaSimbolo + " 0.00");
        lblDescuentoTotal.setText(monedaSimbolo + " 0.00");
        lblSubImporte.setText(monedaSimbolo + " 0.00");
        lblImpuesto.setText(monedaSimbolo + " 0.00");
        lblImporteNeto.setText(monedaSimbolo + " 0.00");
    }

    private void initTable() {
        tcQuitar.setCellValueFactory(new PropertyValueFactory<>("btnQuitar"));
        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getCantidad(), 2)));
        tcExistencia.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getExistencia(), 2)));
        tcStock.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getStock()));
        tcProducto.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getSuministroTB().getClave() + "\n" + cellData.getValue().getSuministroTB().getNombreMarca()));
        tcCostoProveedor.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getCosto(), 2)));
        txtDescuento.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getDescuento(), 2)));
        txtImpuesto.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getImpuesto(), 2) + " %"));
        tcImporte.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getImporte(), 2)));

        tcCantidad.setCellFactory(TextFieldTableCell.forTableColumn());
        tcCantidad.setOnEditCommit(data -> {
            final Double newCantidad = Tools.isNumeric(data.getNewValue())
                    ? (Double.parseDouble(data.getNewValue()) <= 0 ? Double.parseDouble(data.getOldValue()) : Double.parseDouble(data.getNewValue()))
                    : Double.parseDouble(data.getOldValue());

            PedidoDetalleTB pedidoDetalleTB = data.getTableView().getItems().get(data.getTablePosition().getRow());
            pedidoDetalleTB.setCantidad(newCantidad);
            double descuento = pedidoDetalleTB.getDescuento();
            double costoDescuento = pedidoDetalleTB.getCosto() - descuento;
            pedidoDetalleTB.setImporte(costoDescuento * pedidoDetalleTB.getCantidad());

            tvList.refresh();
            calculateTotales();
        });

        tcCostoProveedor.setCellFactory(TextFieldTableCell.forTableColumn());
        tcCostoProveedor.setOnEditCommit(data -> {
            final Double newCosto = Tools.isNumeric(data.getNewValue())
                    ? (Double.parseDouble(data.getNewValue()) <= 0 ? Double.parseDouble(data.getOldValue()) : Double.parseDouble(data.getNewValue()))
                    : Double.parseDouble(data.getOldValue());

            PedidoDetalleTB pedidoDetalleTB = data.getTableView().getItems().get(data.getTablePosition().getRow());
            pedidoDetalleTB.setCosto(newCosto);
            double descuento = pedidoDetalleTB.getDescuento();
            double costoDescuento = pedidoDetalleTB.getCosto() - descuento;
            pedidoDetalleTB.setImporte(costoDescuento * pedidoDetalleTB.getCantidad());

            tvList.refresh();
            calculateTotales();
        });

        txtDescuento.setCellFactory(TextFieldTableCell.forTableColumn());
        txtDescuento.setOnEditCommit(data -> {
            final Double newDescuento = Tools.isNumeric(data.getNewValue())
                    ? (Double.parseDouble(data.getNewValue()) < 0 ? Double.parseDouble(data.getOldValue()) : Double.parseDouble(data.getNewValue()))
                    : Double.parseDouble(data.getOldValue());

            PedidoDetalleTB pedidoDetalleTB = data.getTableView().getItems().get(data.getTablePosition().getRow());
            pedidoDetalleTB.setDescuento(newDescuento);
            double descuento = pedidoDetalleTB.getDescuento();
            double costoDescuento = pedidoDetalleTB.getCosto() - descuento;
            pedidoDetalleTB.setImporte(costoDescuento * pedidoDetalleTB.getCantidad());

            tvList.refresh();
            calculateTotales();
        });

        tcQuitar.prefWidthProperty().bind(tvList.widthProperty().multiply(0.06));
        tcCantidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.09));
        tcExistencia.prefWidthProperty().bind(tvList.widthProperty().multiply(0.09));
        tcStock.prefWidthProperty().bind(tvList.widthProperty().multiply(0.09));
        tcProducto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.21));
        tcCostoProveedor.prefWidthProperty().bind(tvList.widthProperty().multiply(0.11));
        txtDescuento.prefWidthProperty().bind(tvList.widthProperty().multiply(0.11));
        txtImpuesto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.11));
        tcImporte.prefWidthProperty().bind(tvList.widthProperty().multiply(0.11));
    }

    private void openWindowSuministro() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_SUMINISTROS_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxSuministrosListaController controller = fXMLLoader.getController();
            controller.setInitPedidosController(this);
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

    private void openWindowProveedor() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_PROVEEDORES_PROCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxProveedorProcesoController controller = fXMLLoader.getController();
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar Proveedor", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
            controller.setValueAdd();
        } catch (IOException ix) {
            System.out.println("Error en Proveedor Controller:" + ix.getLocalizedMessage());
        }
    }

    private void openModalImpresion(String idPedido) {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_OPCIONES_IMPRIMIR);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxOpcionesImprimirController controller = fXMLLoader.getController();
            controller.loadDataPedido(idPedido);
            controller.setInitOpcionesImprimirPedido(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Imprimir", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
        } catch (IOException ex) {
            System.out.println("Controller Modal Impresi??n: " + ex.getLocalizedMessage());
        }
    }

    private void openWindowPedido() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_PEDIDO_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxPedidosListaController controller = fXMLLoader.getController();
            controller.setInitPedidoListaController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Mostrar Pedidos", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.setOnShown(w -> controller.initLoad());
            stage.show();
        } catch (IOException ex) {
            Tools.println("Error en la funcio??n openWindowPedido():" + ex.getLocalizedMessage());
        }
    }

    public void getAddArticulo(PedidoDetalleTB pedidoDetalleTB) {
        if (validateDuplicateArticulo(tvList, pedidoDetalleTB)) {
            Tools.AlertMessageWarning(apWindow, "Pedido", "Ya existe un producto con los mismos datos.");
        } else {
            tvList.getItems().add(pedidoDetalleTB);
            calculateTotales();
        }
    }

    private boolean validateDuplicateArticulo(TableView<PedidoDetalleTB> view, PedidoDetalleTB pedidoDetalleTB) {
        boolean ret = false;
        for (int i = 0; i < view.getItems().size(); i++) {
            if (view.getItems().get(i).getIdSuministro().equals(pedidoDetalleTB.getIdSuministro())) {
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
            double costoDescuento = e.getCosto() - descuento;
            double subCosto = Tools.calculateTaxBruto(e.getImpuesto(), costoDescuento);
            double costoBruto = subCosto + descuento;
            importeBruto += costoBruto * e.getCantidad();
        });

        descuentoTotal = 0;
        tvList.getItems().forEach(e -> {
            double descuento = e.getDescuento();
            descuentoTotal += e.getCantidad() * descuento;
        });

        subImporte = 0;
        tvList.getItems().forEach(e -> {
            double descuento = e.getDescuento();
            double costoDescuento = e.getCosto() - descuento;
            double subCosto = Tools.calculateTaxBruto(e.getImpuesto(), costoDescuento);
            subImporte += e.getCantidad() * subCosto;
        });

        impuestoGenerado = 0;
        tvList.getItems().forEach(e -> {
            double descuento = e.getDescuento();
            double costoDescuento = e.getCosto() - descuento;
            double subCosto = Tools.calculateTaxBruto(e.getImpuesto(), costoDescuento);
            double impuesto = Tools.calculateTax(e.getImpuesto(), subCosto);
            impuestoGenerado += e.getCantidad() * impuesto;
        });

        importeNeto = subImporte + impuestoGenerado;

        lblImporteBruto.setText(monedaSimbolo + " " + Tools.roundingValue(importeBruto, 2));
        lblDescuentoTotal.setText(monedaSimbolo + " -" + Tools.roundingValue(descuentoTotal, 2));
        lblSubImporte.setText(monedaSimbolo + " " + Tools.roundingValue(subImporte, 2));
        lblImpuesto.setText(monedaSimbolo + " " + Tools.roundingValue(impuestoGenerado, 2));
        lblImporteNeto.setText(monedaSimbolo + " " + Tools.roundingValue(importeNeto, 2));
    }

    private void clearElements() {
        idPedido = "";
        lblProceso.setText("Pedido en proceso de registrar");
        lblProceso.setTextFill(Color.web("#0060e8"));
        cbProveedor.getItems().clear();
        Tools.actualDate(Tools.getDate(), txtFechaEmision);
        Tools.actualDate(Tools.getDate(), txtFechaVencimiento);
        txtObservacion.clear();
        loadComponentMoneda();
        tvList.getItems().clear();
        calculateTotales();
        lblMessageLoad.setText("");
        btnAceptarLoad.setVisible(false);
    }

    public void loadPedido(String idPedido) {
        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<Object> task = new Task<Object>() {
            @Override
            public Object call() {
                return PedidoADO.CargarPedidoEditar(idPedido);
            }
        };
        task.setOnScheduled(w -> {
            hbLoad.setVisible(true);
            vbBody.setDisable(true);
            btnAceptarLoad.setVisible(false);
            lblMessageLoad.setText("Cargando informaci??n...");
            lblMessageLoad.setTextFill(Color.web("#ffffff"));
        });
        task.setOnFailed(w -> {
            btnAceptarLoad.setVisible(true);
            btnAceptarLoad.setOnAction(event -> {
                hbLoad.setVisible(false);
                vbBody.setDisable(false);
                clearElements();
            });
            btnAceptarLoad.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    hbLoad.setVisible(false);
                    vbBody.setDisable(false);
                    clearElements();
                }
                event.consume();
            });
            lblMessageLoad.setText(task.getException().getLocalizedMessage());
            lblMessageLoad.setTextFill(Color.web("#ff6d6d"));
            Tools.println("failed");
        });
        task.setOnSucceeded(w -> {
            Object object = task.getValue();
            if (object instanceof PedidoTB) {
                PedidoTB pedidoTB = (PedidoTB) object;
                this.idPedido = pedidoTB.getIdPedido();
                txtFechaEmision.setValue(LocalDate.parse(pedidoTB.getFechaEmision()));
                txtFechaVencimiento.setValue(LocalDate.parse(pedidoTB.getFechaVencimiento()));
                for (int i = 0; i < cbMoneda.getItems().size(); i++) {
                    if (cbMoneda.getItems().get(i).getIdMoneda() == pedidoTB.getIdMoneda()) {
                        cbMoneda.getSelectionModel().select(i);
                        break;
                    }
                }

                txtObservacion.setText(pedidoTB.getObservacion());

                cbProveedor.getItems().add(pedidoTB.getProveedorTB());
                cbProveedor.getSelectionModel().select(0);

                for (PedidoDetalleTB pedidoDetalleTB : pedidoTB.getPedidoDetalleTBs()) {
                    pedidoDetalleTB.getBtnQuitar().setOnAction(event -> {
                        getTvList().getItems().remove(pedidoDetalleTB);
                        calculateTotales();
                    });
                    pedidoDetalleTB.getBtnQuitar().setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.ENTER) {
                            getTvList().getItems().remove(pedidoDetalleTB);
                            calculateTotales();
                        }
                        event.consume();
                    });
                }
                tvList.setItems(pedidoTB.getPedidoDetalleTBs());
                
                
                lblProceso.setText("Pedido en proceso de actualizar");
                lblProceso.setTextFill(Color.web("#c52700"));

                calculateTotales();
                hbLoad.setVisible(false);
                vbBody.setDisable(false);
            } else {
                lblMessageLoad.setText((String) object);
                lblMessageLoad.setTextFill(Color.web("#ff6d6d"));
                btnAceptarLoad.setVisible(true);
                btnAceptarLoad.setOnAction(event -> {
                    hbLoad.setVisible(false);
                    vbBody.setDisable(false);
                    clearElements();
                });
                btnAceptarLoad.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        hbLoad.setVisible(false);
                        vbBody.setDisable(false);
                        clearElements();
                    }
                    event.consume();
                });
            }
        });

        exec.execute(task);

        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void onEventRegistrar() {
        if (cbProveedor.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(apWindow, "Pedido", "Ingrese un proveedor para continuar.");
            cbProveedor.requestFocus();
        } else if (txtFechaEmision.getValue() == null) {
            Tools.AlertMessageWarning(apWindow, "Pedido", "Ingrese la fecha de emisi??n.");
            txtFechaEmision.requestFocus();
        } else if (txtFechaVencimiento.getValue() == null) {
            Tools.AlertMessageWarning(apWindow, "Pedido", "Ingrese la fecha de vencimiento.");
            txtFechaVencimiento.requestFocus();
        } else if (cbMoneda.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessageWarning(apWindow, "Pedido", "Selecciona una moneda.");
            cbMoneda.requestFocus();
        } else if (tvList.getItems().isEmpty()) {
            Tools.AlertMessageWarning(apWindow, "Pedido", "No hay items para completa el pedido.");
            btnProducto.requestFocus();
        } else {
            short value = Tools.AlertMessageConfirmation(apWindow, "Pedido", "??Est?? seguro de continuar?");
            if (value == 1) {

                ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
                    Thread t = new Thread(runnable);
                    t.setDaemon(true);
                    return t;
                });

                Task<Object> task = new Task<Object>() {
                    @Override
                    public Object call() {
                        PedidoTB pedidoTB = new PedidoTB();
                        pedidoTB.setIdPedido(idPedido);
                        pedidoTB.setIdProveedor(cbProveedor.getSelectionModel().getSelectedItem().getIdProveedor());
                        pedidoTB.setIdVendedor(Session.USER_ID);
                        pedidoTB.setFechaEmision(Tools.getDatePicker(txtFechaEmision));
                        pedidoTB.setHoraEmision(Tools.getTime());
                        pedidoTB.setFechaVencimiento(Tools.getDatePicker(txtFechaVencimiento));
                        pedidoTB.setHoraVencimiento(Tools.getTime());
                        pedidoTB.setIdMoneda(cbMoneda.getSelectionModel().getSelectedItem().getIdMoneda());
                        pedidoTB.setObservacion(txtObservacion.getText().trim());
                        pedidoTB.setPedidoDetalleTBs(tvList.getItems());
                        return PedidoADO.CrudPedido(pedidoTB);
                    }
                };
                task.setOnScheduled(w -> {
                    hbLoad.setVisible(true);
                    vbBody.setDisable(true);
                    btnAceptarLoad.setVisible(false);
//                    lblMessageLoad.setGraphic(new ImageView(new Image("/view/image/load.gif")));
                    lblMessageLoad.setText("Procesando petici??n...");
                    lblMessageLoad.setTextFill(Color.web("#ffffff"));
                });
                task.setOnFailed(w -> {
//                    lblMessageLoad.setGraphic(new ImageView(new Image("/view/image/error_large.png")));
                    btnAceptarLoad.setVisible(true);
                    btnAceptarLoad.setOnAction(event -> {
                        hbLoad.setVisible(false);
                        vbBody.setDisable(false);
                        clearElements();
                    });
                    btnAceptarLoad.setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.ENTER) {
                            hbLoad.setVisible(false);
                            vbBody.setDisable(false);
                            clearElements();
                        }
                        event.consume();
                    });
                    lblMessageLoad.setText(task.getException().getLocalizedMessage());
                    lblMessageLoad.setTextFill(Color.web("#ff6d6d"));
                });
                task.setOnSucceeded(w -> {
                    Object object = task.getValue();
                    if (object instanceof String[]) {
                        String[] result = (String[]) object;
                        if (result[0].equalsIgnoreCase("0")) {
                            lblMessageLoad.setText("Se registr?? correctamente la pedido.");
                            lblMessageLoad.setTextFill(Color.web("#ffffff"));
                            hbLoad.setVisible(false);
                            vbBody.setDisable(false);
                            openModalImpresion(result[1]);
                            clearElements();
                        } else {
                            lblMessageLoad.setText("Se actualiz?? correctamente la pedido.");
                            lblMessageLoad.setTextFill(Color.web("#ffffff"));
                            hbLoad.setVisible(false);
                            vbBody.setDisable(false);
                            openModalImpresion(result[1]);
                            clearElements();
                        }
                    } else {
//                        lblMessageLoad.setGraphic(new ImageView(new Image("/view/image/warning_large.png")));
                        lblMessageLoad.setText((String) object);
                        lblMessageLoad.setTextFill(Color.web("#ff6d6d"));
                        btnAceptarLoad.setVisible(true);
                        btnAceptarLoad.setOnAction(event -> {
                            hbLoad.setVisible(false);
                            vbBody.setDisable(false);
                            clearElements();
                        });
                        btnAceptarLoad.setOnKeyPressed(event -> {
                            if (event.getCode() == KeyCode.ENTER) {
                                hbLoad.setVisible(false);
                                vbBody.setDisable(false);
                                clearElements();
                            }
                            event.consume();
                        });
                    }
                });
                exec.execute(task);

                if (!exec.isShutdown()) {
                    exec.shutdown();
                }

            }
        }
    }

    @FXML
    private void onKeyPressedRegistrar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventRegistrar();
        }
    }

    @FXML
    private void onActionRegistrar(ActionEvent event) {
        onEventRegistrar();
    }

    @FXML
    private void onKeyPressedProducto(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowSuministro();
        }
    }

    @FXML
    private void onActionProducto(ActionEvent event) {
        openWindowSuministro();
    }

    @FXML
    private void onKeyPressedProveedor(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowProveedor();
        }
    }

    @FXML
    private void onActionProveedor(ActionEvent event) {
        openWindowProveedor();
    }

    @FXML
    private void onKeyPressedPedido(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowPedido();
        }
    }

    @FXML
    private void onActionPedido(ActionEvent event) {
        openWindowPedido();
    }

    @FXML
    private void onKeyPressedLimpiar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            clearElements();
        }
    }

    @FXML
    private void onActionLimpiar(ActionEvent event) {
        clearElements();
    }

    @FXML
    private void onKeyReleasedWindow(KeyEvent event) {
        if (null != event.getCode()) {
            switch (event.getCode()) {
                case F1:
                    onEventRegistrar();
                    break;
                case F2:
                    openWindowSuministro();
                    break;
                case F3:
                    openWindowProveedor();
                    break;
                case F4:

                    break;
                case F5:
                    clearElements();
                    break;
                default:
                    break;
            }
        }
    }

    public TableView<PedidoDetalleTB> getTvList() {
        return tvList;
    }

    public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }

}
