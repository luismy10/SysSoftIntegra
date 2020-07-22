package controller.inventario.valorinventario;

import controller.configuracion.tickets.FxTicketBusquedaController;
import controller.tools.BillPrintable;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.SearchComboBox;
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
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.DetalleADO;
import model.DetalleTB;
import model.SuministroADO;
import model.SuministroTB;

public class FxValorInventarioController implements Initializable {

    @FXML
    private VBox vbWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private TextField txtProducto;
    @FXML
    private ComboBox<String> cbExistencia;
    @FXML
    private TableView<SuministroTB> tvList;
    @FXML
    private TableColumn<SuministroTB, Integer> tcNumero;
    @FXML
    private TableColumn<SuministroTB, String> tcDescripcion;
    @FXML
    private TableColumn<SuministroTB, String> tcCostoPromedio;
    @FXML
    private TableColumn<SuministroTB, String> tcPrecio;
    @FXML
    private TableColumn<SuministroTB, Label> tcExistencia;
    @FXML
    private TableColumn<SuministroTB, String> tcInventario;
    @FXML
    private TableColumn<SuministroTB, String> tcCategoria;
    @FXML
    private TableColumn<SuministroTB, String> tcMarca;
    @FXML
    private Label lblValoTotal;
    @FXML
    private TextField txtNameProduct;
    @FXML
    private ComboBox<DetalleTB> cbCategoria;
    @FXML
    private ComboBox<DetalleTB> cbMarca;
    @FXML
    private Label lblPaginaActual;
    @FXML
    private Label lblPaginaSiguiente;

    private AnchorPane vbPrincipal;

    private AnchorPane apEncabezado;

    private AnchorPane apDetalleCabecera;

    private AnchorPane apPie;

    private SearchComboBox<String> searchComboBoxExistencias;

    private SearchComboBox<DetalleTB> searchComboBoxCategoria;

    private SearchComboBox<DetalleTB> searchComboBoxMarca;

    private BillPrintable billPrintable;

    private int paginacion;

    private int totalPaginacion;

    private short opcion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcNumero.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        tcDescripcion.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getClave() + (cellData.getValue().getClaveAlterna().isEmpty() ? "" : " - ") + cellData.getValue().getClaveAlterna()
                + "\n" + cellData.getValue().getNombreMarca()));
        tcCostoPromedio.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getCostoCompra(), 2)));
        tcPrecio.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getPrecioVentaGeneral(), 2)));
        tcExistencia.setCellValueFactory(new PropertyValueFactory<>("lblCantidad"));
        tcInventario.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getStockMinimo(), 2) + " - " + Tools.roundingValue(cellData.getValue().getStockMaximo(), 2)));
        tcCategoria.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getCategoriaName()));
        tcMarca.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMarcaName()));

        tcNumero.prefWidthProperty().bind(tvList.widthProperty().multiply(0.04));
        tcDescripcion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.25));
        tcCostoPromedio.prefWidthProperty().bind(tvList.widthProperty().multiply(0.10));
        tcPrecio.prefWidthProperty().bind(tvList.widthProperty().multiply(0.10));
        tcExistencia.prefWidthProperty().bind(tvList.widthProperty().multiply(0.16));
        tcInventario.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        tcCategoria.prefWidthProperty().bind(tvList.widthProperty().multiply(0.10));
        tcMarca.prefWidthProperty().bind(tvList.widthProperty().multiply(0.10));

        billPrintable = new BillPrintable();
        apEncabezado = new AnchorPane();
        apDetalleCabecera = new AnchorPane();
        apPie = new AnchorPane();

        paginacion = 1;
        filtercbCategoria();
    }

    public void fillInventarioTable(String producto, short tipoExistencia, String nameProduct, short opcion, int categoria, int marca) {
        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                return SuministroADO.ListInventario(producto, tipoExistencia, nameProduct, opcion, categoria, marca, (paginacion - 1) * 20, 20);
            }
        };
        task.setOnSucceeded(w -> {
            ArrayList<Object> objects = task.getValue();
            if (!objects.isEmpty()) {
                tvList.setItems((ObservableList<SuministroTB>) objects.get(0));
                double total = 0;
                total = tvList.getItems().stream().map((l) -> l.getTotalImporte()).reduce(total, (accumulator, _item) -> accumulator + _item);
                lblValoTotal.setText(Session.MONEDA_SIMBOLO + Tools.roundingValue(total, 4));

                int integer = (int) (Math.ceil((double) (((Integer) objects.get(1)) / 20.00)));
                totalPaginacion = integer;
                lblPaginaActual.setText(paginacion + "");
                lblPaginaSiguiente.setText(totalPaginacion + "");                
            }
            lblLoad.setVisible(false);
        });
        task.setOnFailed(w -> {
            lblLoad.setVisible(false);
        });
        task.setOnScheduled(w -> {
            lblLoad.setVisible(true);
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void filtercbCategoria() {

        searchComboBoxExistencias = new SearchComboBox<>(cbExistencia);
        searchComboBoxExistencias.setFilter((item, text) -> item.toLowerCase().contains(text.toLowerCase()));
        searchComboBoxExistencias.getComboBox().getItems().addAll("Todas las Cantidades", "Cantidades negativas", "Cantidades intermedias", "Cantidades necesaria", "Cantidades excedentes");
        searchComboBoxExistencias.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
            if (null == t.getCode()) {
                searchComboBoxExistencias.getSearchComboBoxSkin().getSearchBox().requestFocus();
                searchComboBoxExistencias.getSearchComboBoxSkin().getSearchBox().selectAll();
            } else {
                switch (t.getCode()) {
                    case ENTER:
                    case SPACE:
                    case ESCAPE:
                        if (!lblLoad.isVisible()) {
                            paginacion = 1;
                            fillInventarioTable("", (short) searchComboBoxExistencias.getComboBox().getSelectionModel().getSelectedIndex(), "", (short) 3, 0, 0);
                            opcion = 3;
                        }
                        searchComboBoxExistencias.getComboBox().hide();
                        break;
                    case UP:
                    case DOWN:
                    case LEFT:
                    case RIGHT:
                        break;
                    default:
                        searchComboBoxExistencias.getSearchComboBoxSkin().getSearchBox().requestFocus();
                        searchComboBoxExistencias.getSearchComboBoxSkin().getSearchBox().selectAll();
                        break;
                }
            }
        });
        searchComboBoxExistencias.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                searchComboBoxExistencias.getComboBox().getSelectionModel().select(item);
                if (searchComboBoxExistencias.getSearchComboBoxSkin().isClickSelection()) {
                    if (!lblLoad.isVisible()) {
                        paginacion = 1;
                        fillInventarioTable("", (short) searchComboBoxExistencias.getComboBox().getSelectionModel().getSelectedIndex(), "", (short) 3, 0, 0);
                        opcion = 3;
                    }
                    searchComboBoxExistencias.getComboBox().hide();
                }
            }
        });

        searchComboBoxCategoria = new SearchComboBox<>(cbCategoria);
        searchComboBoxCategoria.setFilter((item, text) -> item.getNombre().get().toLowerCase().contains(text.toLowerCase()));
        searchComboBoxCategoria.getComboBox().getItems().addAll(DetalleADO.GetDetailId("0006"));
        searchComboBoxCategoria.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
            if (null == t.getCode()) {
                searchComboBoxCategoria.getSearchComboBoxSkin().getSearchBox().requestFocus();
                searchComboBoxCategoria.getSearchComboBoxSkin().getSearchBox().selectAll();
            } else {
                switch (t.getCode()) {
                    case ENTER:
                    case SPACE:
                    case ESCAPE:
                        if (!lblLoad.isVisible()) {
                            paginacion = 1;
                            fillInventarioTable("", (short) 0, "", (short) 4, ((DetalleTB) searchComboBoxCategoria.getComboBox().getSelectionModel().getSelectedItem()).getIdDetalle().get(), 0);
                            opcion = 4;
                        }
                        searchComboBoxCategoria.getComboBox().hide();
                        break;
                    case UP:
                    case DOWN:
                    case LEFT:
                    case RIGHT:
                        break;
                    default:
                        searchComboBoxCategoria.getSearchComboBoxSkin().getSearchBox().requestFocus();
                        searchComboBoxCategoria.getSearchComboBoxSkin().getSearchBox().selectAll();
                        break;
                }
            }
        });

        searchComboBoxCategoria.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                searchComboBoxCategoria.getComboBox().getSelectionModel().select(item);
                if (searchComboBoxCategoria.getSearchComboBoxSkin().isClickSelection()) {
                    if (!lblLoad.isVisible()) {
                        paginacion = 1;
                        fillInventarioTable("", (short) 0, "", (short) 4, ((DetalleTB) searchComboBoxCategoria.getComboBox().getSelectionModel().getSelectedItem()).getIdDetalle().get(), 0);
                        opcion = 4;
                    }
                    searchComboBoxCategoria.getComboBox().hide();
                }
            }
        });

        searchComboBoxMarca = new SearchComboBox<>(cbMarca);
        searchComboBoxMarca.setFilter((item, text) -> item.getNombre().get().toLowerCase().contains(text.toLowerCase()));
        searchComboBoxMarca.getComboBox().getItems().addAll(DetalleADO.GetDetailId("0007"));
        searchComboBoxMarca.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
            if (null == t.getCode()) {
                searchComboBoxMarca.getSearchComboBoxSkin().getSearchBox().requestFocus();
                searchComboBoxMarca.getSearchComboBoxSkin().getSearchBox().selectAll();
            } else {
                switch (t.getCode()) {
                    case ENTER:
                    case SPACE:
                    case ESCAPE:
                        if (!lblLoad.isVisible()) {
                            paginacion = 1;
                            fillInventarioTable("", (short) 0, "", (short) 5, 0, ((DetalleTB) searchComboBoxMarca.getComboBox().getSelectionModel().getSelectedItem()).getIdDetalle().get());
                            opcion = 5;
                        }
                        searchComboBoxMarca.getComboBox().hide();
                        break;
                    case UP:
                    case DOWN:
                    case LEFT:
                    case RIGHT:
                        break;
                    default:
                        searchComboBoxMarca.getSearchComboBoxSkin().getSearchBox().requestFocus();
                        searchComboBoxMarca.getSearchComboBoxSkin().getSearchBox().selectAll();
                        break;
                }
            }
        });

        searchComboBoxMarca.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                searchComboBoxMarca.getComboBox().getSelectionModel().select(item);
                // ocultar popup cuando el item fue seleccionado mediante un click
                if (searchComboBoxMarca.getSearchComboBoxSkin().isClickSelection()) {
                    if (!lblLoad.isVisible()) {
                        paginacion = 1;
                        fillInventarioTable("", (short) 0, "", (short) 5, 0, ((DetalleTB) searchComboBoxMarca.getComboBox().getSelectionModel().getSelectedItem()).getIdDetalle().get());
                        opcion = 5;
                    }
                    searchComboBoxMarca.getComboBox().hide();
                }
            }
        });
    }

    private void openWindowAjuste() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            try {
                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                URL url = getClass().getResource(FilesRouters.FX_INVENTARIO_AJUSTE);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxInventarioAjusteController controller = fXMLLoader.getController();
                controller.setInitValorInventarioController(this);
                controller.setLoadComponents(tvList.getSelectionModel().getSelectedItem().getIdSuministro(),
                        tvList.getSelectionModel().getSelectedItem().getClave(),
                        tvList.getSelectionModel().getSelectedItem().getNombreMarca(),
                        tvList.getSelectionModel().getSelectedItem().getStockMinimo(),
                        tvList.getSelectionModel().getSelectedItem().getStockMaximo());
                //
                Stage stage = WindowStage.StageLoaderModal(parent, "Inventario general", vbWindow.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.setOnHiding((w) -> {
                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                });
                stage.show();
            } catch (IOException ex) {
                System.out.println(ex.getLocalizedMessage());
            }
        }
    }

    private void generarReporte() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_REPORTE_OPCIONES_INVENTARIO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxReporteOpcionesInventarioController controller = fXMLLoader.getController();
            controller.setInitValorInventarioController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Inventario general", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding((w) -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    public void onEventPaginacion() {
        switch (opcion) {
            case 0:
                fillInventarioTable("", (short) 0, "", (short) 0, 0, 0);
                break;
            case 1:
                fillInventarioTable(txtProducto.getText().trim(), (short) 0, "", (short) 1, 0, 0);
                break;
            case 2:
                fillInventarioTable("", (short) 0, txtNameProduct.getText().trim(), (short) 2, 0, 0);
                break;
            case 3:
                fillInventarioTable("", (short) searchComboBoxExistencias.getComboBox().getSelectionModel().getSelectedIndex(), "", (short) 3, 0, 0);
                break;
            case 4:
                fillInventarioTable("", (short) 0, "", (short) 4, ((DetalleTB) searchComboBoxCategoria.getComboBox().getSelectionModel().getSelectedItem()).getIdDetalle().get(), 0);
                break;
            default:
                fillInventarioTable("", (short) 0, "", (short) 5, 0, ((DetalleTB) searchComboBoxMarca.getComboBox().getSelectionModel().getSelectedItem()).getIdDetalle().get());
                break;
        }
    }

    private void onEventTicketBusqueda() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_TICKET_BUSQUEDA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxTicketBusquedaController controller = fXMLLoader.getController();
            controller.setInitValorInventarioController(this);
            controller.loadComponents(3, false);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccionar formato", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    public void setGenerarTicket(int idTicket, String rutaTicket) {

        if (!Session.ESTADO_IMPRESORA && Session.NOMBRE_IMPRESORA == null) {
            Tools.AlertMessageWarning(vbWindow, "Inventario general", "No hay ruta de impresión, no se ha configurado la ruta de impresión");
            return;
        }

        billPrintable.loadEstructuraTicket(idTicket, rutaTicket, apEncabezado, apDetalleCabecera, apPie);

        for (int i = 0; i < apEncabezado.getChildren().size(); i++) {
            HBox box = ((HBox) apEncabezado.getChildren().get(i));
            billPrintable.hbEncebezado(box,
                    "TICKET",
                    "00000000",
                    "SIN DOCUMENTO",
                    "PUBLICO GENERAL",
                    "SIN CELULAR",
                    "SIN DIRECCION",
                    "SIN CODIGO");
        }

        AnchorPane hbDetalle = new AnchorPane();
        for (int m = 0; m < tvList.getItems().size(); m++) {
            for (int i = 0; i < apDetalleCabecera.getChildren().size(); i++) {
                HBox hBox = new HBox();
                hBox.setId("dc_" + m + "" + i);
                HBox box = ((HBox) apDetalleCabecera.getChildren().get(i));
                billPrintable.hbDetalle(hBox, box, tvList.getItems(), m);
                hbDetalle.getChildren().add(hBox);
            }
        }

        for (int i = 0; i < apPie.getChildren().size(); i++) {
            HBox box = ((HBox) apPie.getChildren().get(i));
            billPrintable.hbPie(box, "",
                    Tools.roundingValue(0, 2),
                    "-" + Tools.roundingValue(0, 2),
                    Tools.roundingValue(0, 2),
                    Tools.roundingValue(0, 2),
                    Tools.roundingValue(0, 2),
                    Tools.roundingValue(0, 2),
                    "SIN DOCUMENTO",
                    "PUBLICO GENERAL", "SIN CODIGO", "SIN CELULAR");
        }

        billPrintable.generatePDFPrint(apEncabezado, hbDetalle, apPie, Session.NOMBRE_IMPRESORA, Session.CORTAPAPEL_IMPRESORA);

    }

    @FXML
    private void onKeyReleasedProducto(KeyEvent event) {
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
            if (!lblLoad.isVisible()) {
                paginacion = 1;
                fillInventarioTable(txtProducto.getText().trim(), (short) 0, "", (short) 1, 0, 0);
                opcion = 1;
            }
        }
    }

    @FXML
    private void onKeyPressedAjuste(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowAjuste();
        }
    }

    @FXML
    private void onActionAjuste(ActionEvent event) {
        openWindowAjuste();
    }

    @FXML
    private void onKeyPressedRecargar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                paginacion = 1;
                fillInventarioTable("", (short) 0, "", (short) 0, 0, 0);
                opcion = 0;
            }
        }
    }

    @FXML
    private void onActionRecargar(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            paginacion = 1;
            fillInventarioTable("", (short) 0, "", (short) 0, 0, 0);
            opcion = 0;
        }
    }

    @FXML
    private void onKeyPressedReporte(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            generarReporte();
        }
    }

    @FXML
    private void onActionReporte(ActionEvent event) {
        generarReporte();
    }

    @FXML
    private void onKeyReleasedNameProduct(KeyEvent event) {
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
            if (!lblLoad.isVisible()) {
                paginacion = 1;
                fillInventarioTable("", (short) 0, txtNameProduct.getText().trim(), (short) 2, 0, 0);
                opcion = 2;
            }
        }
    }

    @FXML
    private void onKeyPressedTicket(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventTicketBusqueda();
        }
    }

    @FXML
    private void onActionTicket(ActionEvent event) {
        onEventTicketBusqueda();
    }

    @FXML
    private void onKeyPressedAnterior(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                if (paginacion > 1) {
                    paginacion--;
                    onEventPaginacion();
                }
            }
        }
    }

    @FXML
    private void onActionAnterior(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (paginacion > 1) {
                paginacion--;
                onEventPaginacion();
            }
        }
    }

    @FXML
    private void onKeyPressedSiguiente(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                if (paginacion < totalPaginacion) {
                    paginacion++;
                    onEventPaginacion();
                }
            }
        }
    }

    @FXML
    private void onActionSiguiente(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (paginacion < totalPaginacion) {
                paginacion++;
                onEventPaginacion();
            }
        }
    }

    public TableView<SuministroTB> getTvList() {
        return tvList;
    }

    public TableColumn<SuministroTB, Label> getTcExistencia() {
        return tcExistencia;
    }

    public SearchComboBox<String> getSearchComboBoxExistencias() {
        return searchComboBoxExistencias;
    }

    public AnchorPane getApEncabezado() {
        return apEncabezado;
    }

    public AnchorPane getApDetalleCabecera() {
        return apDetalleCabecera;
    }

    public AnchorPane getApPie() {
        return apPie;
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
