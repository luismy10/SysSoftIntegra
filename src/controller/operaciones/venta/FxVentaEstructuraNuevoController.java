package controller.operaciones.venta;

import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.MonedaADO;
import model.MonedaTB;
import model.SuministroADO;
import model.SuministroTB;

public class FxVentaEstructuraNuevoController implements Initializable {

    @FXML
    private VBox vbWindow;
    @FXML
    private FlowPane fpProductos;
    @FXML
    private ListView<BbItemProducto> lvProductoAgregados;
    @FXML
    private Label lblPaginaActual;
    @FXML
    private Label lblPaginaSiguiente;
    @FXML
    private TextField txtSearch;
    @FXML
    private Label lblTotal;
    @FXML
    private ComboBox<MonedaTB> cbMoneda;

    private AnchorPane vbPrincipal;

    private ObservableList<SuministroTB> tvList;

    private ArrayList<ImpuestoTB> arrayArticulosImpuesto;

    private String monedaSimbolo;

    private int totalPaginacion;

    private int paginacion;

    private short opcion;

    private boolean state;

    private double subTotal;

    private double descuento;

    private double subTotalImporte;

    private double totalImporte;

    private double total;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        arrayArticulosImpuesto = new ArrayList<>();
        paginacion = 1;
        opcion = 0;
        state = false;
        monedaSimbolo = "M";
        arrayArticulosImpuesto.clear();
        ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> {
            arrayArticulosImpuesto.add(new ImpuestoTB(e.getIdImpuesto(), e.getNombreImpuesto(), e.getValor(), e.getPredeterminado()));
        });

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
    }

    public void onEventPaginacion() {
        switch (opcion) {
            case 0:
                fillSuministrosTable((short) 0, "");
                break;
            case 1:
                fillSuministrosTable((short) 1, txtSearch.getText().trim());
                break;
        }
    }

    public void fillSuministrosTable(short tipo, String value) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                return SuministroADO.ListSuministrosListaView(tipo, value, (paginacion - 1) * 15, 15);
            }
        };

        task.setOnSucceeded(e -> {
            ArrayList<Object> objects = task.getValue();
            if (!objects.isEmpty()) {
                fpProductos.setAlignment(Pos.TOP_CENTER);
                tvList = (ObservableList<SuministroTB>) objects.get(0);
                tvList.stream().map((tvList1) -> {
                    VBox vBox = new VBox();
                    vBox.setOnMouseClicked(m -> {

                        SuministroTB suministroTB = new SuministroTB();
                        suministroTB.setIdSuministro(tvList1.getIdSuministro());
                        suministroTB.setClave(tvList1.getClave());
                        suministroTB.setNombreMarca(tvList1.getNombreMarca());
                        suministroTB.setCantidad(1);
                        suministroTB.setCostoCompra(tvList1.getCostoCompra());

                        suministroTB.setDescuento(0);
                        suministroTB.setDescuentoCalculado(0);
                        suministroTB.setDescuentoSumado(0);

                        suministroTB.setPrecioVentaGeneralUnico(tvList1.getPrecioVentaGeneral());
                        suministroTB.setPrecioVentaGeneralReal(tvList1.getPrecioVentaGeneral());
                        suministroTB.setPrecioVentaGeneralAuxiliar(suministroTB.getPrecioVentaGeneralReal());

                        suministroTB.setImpuestoOperacion(getTaxValueOperacion(tvList1.getImpuestoArticulo()));
                        suministroTB.setImpuestoArticulo(tvList1.getImpuestoArticulo());
                        suministroTB.setImpuestoArticuloName(getTaxName(tvList1.getImpuestoArticulo()));
                        suministroTB.setImpuestoValor(getTaxValue(tvList1.getImpuestoArticulo()));
                        suministroTB.setImpuestoSumado(suministroTB.getCantidad() * Tools.calculateTax(suministroTB.getImpuestoValor(), suministroTB.getPrecioVentaGeneralReal()));

                        suministroTB.setPrecioVentaGeneral(suministroTB.getPrecioVentaGeneralReal() + suministroTB.getImpuestoSumado());

                        suministroTB.setSubImporte(suministroTB.getPrecioVentaGeneralUnico() * suministroTB.getCantidad());
                        suministroTB.setSubImporteDescuento(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
                        suministroTB.setTotalImporte(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());

                        suministroTB.setInventario(tvList1.isInventario());
                        suministroTB.setUnidadVenta(tvList1.getUnidadVenta());
                        suministroTB.setValorInventario(tvList1.getValorInventario());

                        if (validateDuplicateArticulo(lvProductoAgregados, suministroTB)) {
                            for (int i = 0; i < lvProductoAgregados.getItems().size(); i++) {
                                if (lvProductoAgregados.getItems().get(i).getSuministroTB().getIdSuministro().equalsIgnoreCase(suministroTB.getIdSuministro())) {
                                    BbItemProducto bbItemProducto = lvProductoAgregados.getItems().get(i);
                                    bbItemProducto.getSuministroTB().setCantidad(bbItemProducto.getSuministroTB().getCantidad() + 1);
                                    double porcentajeRestante = bbItemProducto.getSuministroTB().getPrecioVentaGeneralUnico() * (bbItemProducto.getSuministroTB().getDescuento() / 100.00);

                                    bbItemProducto.getSuministroTB().setDescuentoSumado(porcentajeRestante * bbItemProducto.getSuministroTB().getCantidad());
                                    bbItemProducto.getSuministroTB().setImpuestoSumado(bbItemProducto.getSuministroTB().getCantidad() * (bbItemProducto.getSuministroTB().getPrecioVentaGeneralReal() * (bbItemProducto.getSuministroTB().getImpuestoValor() / 100.00)));

                                    bbItemProducto.getSuministroTB().setSubImporte(bbItemProducto.getSuministroTB().getPrecioVentaGeneralUnico() * bbItemProducto.getSuministroTB().getCantidad());
                                    bbItemProducto.getSuministroTB().setSubImporteDescuento(bbItemProducto.getSuministroTB().getCantidad() * bbItemProducto.getSuministroTB().getPrecioVentaGeneralReal());
                                    bbItemProducto.getSuministroTB().setTotalImporte(bbItemProducto.getSuministroTB().getCantidad() * bbItemProducto.getSuministroTB().getPrecioVentaGeneralReal());
                                    bbItemProducto.getChildren().clear();
                                    bbItemProducto.addElementListView();

                                    lvProductoAgregados.getItems().set(i, bbItemProducto);
                                    calculateTotales();
                                }
                            }
                        } else {
                            BbItemProducto bbItemProducto = new BbItemProducto(suministroTB);
                            bbItemProducto.addElementListView();
                            lvProductoAgregados.getItems().add(bbItemProducto);
                            calculateTotales();
                        }
                    });
                    vBox.setMinWidth(Control.USE_PREF_SIZE);
                    vBox.setPrefWidth(300);
                    vBox.maxWidth(Control.USE_PREF_SIZE);
                    vBox.setStyle("-fx-spacing: 0.4166666666666667em;-fx-padding: 0.4166666666666667em;-fx-border-color: #999999;-fx-cursor:hand;");
                    vBox.setAlignment(Pos.TOP_CENTER);
                    File fileImage = new File(tvList1.getImagenTB());
                    ImageView imageView = new ImageView(new Image(fileImage.exists() ? new File(tvList1.getImagenTB()).toURI().toString() : "/view/image/no-image.png"));
                    imageView.setFitWidth(160);
                    imageView.setFitHeight(160);
                    vBox.getChildren().add(imageView);

                    Label lblCodigo = new Label(tvList1.getClave());
                    lblCodigo.getStyleClass().add("labelOpenSansRegular14");
                    lblCodigo.setTextFill(Color.web("#1a2226"));
                    vBox.getChildren().add(lblCodigo);

                    Label lblProducto = new Label(tvList1.getNombreMarca());
                    lblProducto.getStyleClass().add("labelRobotoBold14");
                    lblProducto.setTextFill(Color.web("#1a2226"));
                    lblProducto.setWrapText(true);
                    lblProducto.setTextAlignment(TextAlignment.CENTER);
                    vBox.getChildren().add(lblProducto);

                    Label lblMarca = new Label(tvList1.getMarcaName());
                    lblMarca.getStyleClass().add("labelOpenSansRegular14");
                    lblMarca.setTextFill(Color.web("#1a2226"));
                    vBox.getChildren().add(lblMarca);

                    Label lblTotal = new Label(Tools.roundingValue(tvList1.getPrecioVentaGeneral(), 2));
                    lblTotal.getStyleClass().add("labelRobotoBold18");
                    lblTotal.setTextFill(Color.web("#0478b2"));
                    lblTotal.maxWidth(Double.MAX_VALUE);
                    vBox.getChildren().add(lblTotal);
                    return vBox;
                }).forEachOrdered((vBox) -> {
                    fpProductos.getChildren().add(vBox);
                });
                int integer = (int) (Math.ceil((double) (((Integer) objects.get(1)) / 15.00)));
                totalPaginacion = integer;
                lblPaginaActual.setText(paginacion + "");
                lblPaginaSiguiente.setText(totalPaginacion + "");
            }
            state = false;
        });
        task.setOnFailed(e -> {
            state = false;
        });
        task.setOnScheduled(e -> {
            state = true;
            fpProductos.getChildren().clear();
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private boolean validateDuplicateArticulo(ListView<BbItemProducto> view, SuministroTB suministroTB) {
        boolean ret = false;
        for (int i = 0; i < view.getItems().size(); i++) {
            if (view.getItems().get(i).getSuministroTB().getClave().equals(suministroTB.getClave())) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    private void searchTable(KeyEvent event, short tipo, String value) {
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
            if (!state) {
                fillSuministrosTable(tipo, value);
            }
        }
    }

    public int getTaxValueOperacion(int impuesto) {
        int valor = 0;
        for (int i = 0; i < arrayArticulosImpuesto.size(); i++) {
            if (arrayArticulosImpuesto.get(i).getIdImpuesto() == impuesto) {
                valor = arrayArticulosImpuesto.get(i).getOperacion();
                break;
            }
        }
        return valor;
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

    public void calculateTotales() {

        subTotal = 0;
        lvProductoAgregados.getItems().forEach(e -> subTotal += e.getSuministroTB().getSubImporte());
//        lblValorVenta.setText(monedaSimbolo + " " + Tools.roundingValue(subTotal, 2));

        descuento = 0;
        lvProductoAgregados.getItems().forEach(e -> descuento += e.getSuministroTB().getDescuentoSumado());
//        lblDescuento.setText(monedaSimbolo + " " + (Tools.roundingValue(descuento * (-1), 2)));

        subTotalImporte = 0;
        lvProductoAgregados.getItems().forEach(e -> subTotalImporte += e.getSuministroTB().getSubImporteDescuento());
//        lblSubImporte.setText(monedaSimbolo + " " + Tools.roundingValue(subTotalImporte, 2));

//        gpTotales.getChildren().clear();
//
        boolean addElement = false;
        double sumaElement = 0;
        double totalImpuestos = 0;
        if (!lvProductoAgregados.getItems().isEmpty()) {
            for (int k = 0; k < arrayArticulosImpuesto.size(); k++) {
                for (int i = 0; i < lvProductoAgregados.getItems().size(); i++) {
                    if (arrayArticulosImpuesto.get(k).getIdImpuesto() == lvProductoAgregados.getItems().get(i).getSuministroTB().getImpuestoArticulo()) {
                        addElement = true;
                        sumaElement += lvProductoAgregados.getItems().get(i).getSuministroTB().getImpuestoSumado();
                    }
                }
                if (addElement) {
//                    gpTotales.add(addLabelTitle(arrayArticulosImpuesto.get(k).getNombreImpuesto().substring(0, 1).toUpperCase()
//                            + "" + arrayArticulosImpuesto.get(k).getNombreImpuesto().substring(1, arrayArticulosImpuesto.get(k).getNombreImpuesto().length()).toLowerCase(),
//                            Pos.CENTER_LEFT), 0, k + 1);
//                    gpTotales.add(addLabelTotal(monedaSimbolo + " " + Tools.roundingValue(sumaElement, 2), Pos.CENTER_RIGHT), 1, k + 1);
                    totalImpuestos += sumaElement;

                    addElement = false;
                    sumaElement = 0;
                }
            }
        }

        totalImporte = 0;
        total = 0;
        lvProductoAgregados.getItems().forEach(e -> totalImporte += e.getSuministroTB().getTotalImporte());
        total = totalImporte + totalImpuestos;
        //lblImporteTotal.setText(monedaSimbolo + " " + Tools.roundingValue(total, 2));
        lblTotal.setText(monedaSimbolo + " " + Tools.roundingValue(Double.parseDouble(Tools.roundingValue(total, 1)), 2));

    }

    private void openWindowDetalleProducto(String producto) {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_VENTA_DETALLE_PRODUCTO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxVentaDetalleProductoController controller = fXMLLoader.getController();
            controller.loadData(producto);
            controller.setInitVentaEstructuraNuevoController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Detalle producto", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();
        } catch (IOException ex) {
            System.out.println("openWindowImpresora():" + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onKeyReleasedToSearch(KeyEvent event) {
        paginacion = 1;
        searchTable(event, (short) 1, txtSearch.getText().trim());
        opcion = 1;
    }

    @FXML
    private void onKeyPressReload(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!state) {
                paginacion = 1;
                fillSuministrosTable((short) 0, "");
                opcion = 0;
            }
        }
    }

    @FXML
    private void onActionReload(ActionEvent event) {
        if (!state) {
            paginacion = 1;
            fillSuministrosTable((short) 0, "");
            opcion = 0;
        }
    }

    @FXML
    private void onKeyPressedAnterior(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!state) {
                if (paginacion > 1) {
                    paginacion--;
                    onEventPaginacion();
                }
            }
        }
    }

    @FXML
    private void onActionAnterior(ActionEvent event) {
        if (!state) {
            if (paginacion > 1) {
                paginacion--;
                onEventPaginacion();
            }
        }
    }

    @FXML
    private void onKeyPressedSiguiente(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!state) {
                if (paginacion < totalPaginacion) {
                    paginacion++;
                    onEventPaginacion();
                }
            }
        }
    }

    @FXML
    private void onActionSiguiente(ActionEvent event) {
        if (!state) {
            if (paginacion < totalPaginacion) {
                paginacion++;
                onEventPaginacion();
            }
        }
    }

    @FXML
    private void onActionMoneda(ActionEvent event) {

    }

    @FXML
    private void onMouseClickedProductosAgregados(MouseEvent event) {
        if (lvProductoAgregados.getSelectionModel().getSelectedIndex() >= 0) {
            openWindowDetalleProducto(lvProductoAgregados.getSelectionModel().getSelectedItem().getSuministroTB().getNombreMarca());
            event.consume();
        }
    }

    @FXML
    private void onKeyReleasedProductosAgregados(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (lvProductoAgregados.getSelectionModel().getSelectedIndex() >= 0) {
                openWindowDetalleProducto(lvProductoAgregados.getSelectionModel().getSelectedItem().getSuministroTB().getNombreMarca());
            }
        }
    }

    class BbItemProducto extends HBox {

        private SuministroTB suministroTB;

        public BbItemProducto(SuministroTB suministroTB) {
            this.suministroTB = suministroTB;
        }

        public void addElementListView() {
            setMinWidth(Control.USE_PREF_SIZE);
            setPrefWidth(Control.USE_COMPUTED_SIZE);
            maxWidth(Control.USE_PREF_SIZE);

            VBox vbLeft = new VBox();
            HBox.setHgrow(vbLeft, Priority.ALWAYS);
            vbLeft.setAlignment(Pos.CENTER_LEFT);
            vbLeft.setMaxWidth(Control.USE_PREF_SIZE);
            Label lblProducto = new Label(suministroTB.getNombreMarca());
            lblProducto.getStyleClass().add("labelRoboto14");
            lblProducto.setTextFill(Color.web("#1a2226"));
            lblProducto.setWrapText(true);
            lblProducto.setPrefWidth(200);
            vbLeft.getChildren().add(lblProducto);
            Label lblPrecio = new Label(Tools.roundingValue(suministroTB.getPrecioVentaGeneral(), 2));
            lblPrecio.getStyleClass().add("labelRobotoBold14");
            lblPrecio.setTextFill(Color.web("#1a2226"));
            vbLeft.getChildren().add(lblPrecio);

            HBox hbCenter = new HBox();
//            hbCenter.setPrefWidth(150);
            hbCenter.setStyle("-fx-spacing: 0.4166666666666667em;");
            hbCenter.setAlignment(Pos.CENTER);
            HBox.setHgrow(hbCenter, Priority.ALWAYS);
            Button btnMenos = new Button();
            ImageView ivMenos = new ImageView(new Image("/view/image/remove-item.png"));
            ivMenos.setFitWidth(24);
            ivMenos.setFitHeight(24);
            btnMenos.setGraphic(ivMenos);
            btnMenos.getStyleClass().add("buttonBorder");
            Label lblCantidad = new Label(Tools.roundingValue(suministroTB.getCantidad(), 2));
            lblCantidad.getStyleClass().add("labelRoboto14");
            lblCantidad.setTextFill(Color.web("#1a2226"));
            Button btnMas = new Button();
            ImageView ivMas = new ImageView(new Image("/view/image/plus.png"));
            ivMas.setFitWidth(24);
            ivMas.setFitHeight(24);
            btnMas.setGraphic(ivMas);
            btnMas.getStyleClass().add("buttonBorder");
            hbCenter.getChildren().addAll(btnMenos, lblCantidad, btnMas);

            VBox vbRight = new VBox();
//            vbRight.setPrefWidth(165);
            HBox.setHgrow(vbRight, Priority.ALWAYS);
            vbRight.setStyle("-fx-spacing: 0.4166666666666667em;");
            vbRight.setAlignment(Pos.CENTER_RIGHT);
            Label lblImporte = new Label(Tools.roundingValue(suministroTB.getPrecioVentaGeneral() * suministroTB.getCantidad(), 2));
            lblImporte.getStyleClass().add("labelRoboto14");
            lblImporte.setTextFill(Color.web("#1a2226"));
            Button btnRemoved = new Button();
            ImageView ivRemoved = new ImageView(new Image("/view/image/remove.png"));
            ivRemoved.setFitWidth(24);
            ivRemoved.setFitHeight(24);
            btnRemoved.setGraphic(ivRemoved);
            btnRemoved.getStyleClass().add("buttonDark");
            btnRemoved.setOnAction((ActionEvent a) -> {
                lvProductoAgregados.getItems().remove(this);
                calculateTotales();
            });
            btnRemoved.setOnKeyPressed(a -> {
                if (a.getCode() == KeyCode.ENTER) {
                    lvProductoAgregados.getItems().remove(this);
                    calculateTotales();
                }
            });
            vbRight.getChildren().addAll(lblImporte, btnRemoved);

            getChildren().add(vbLeft);
            getChildren().add(hbCenter);
            getChildren().add(vbRight);
        }

        public SuministroTB getSuministroTB() {
            return suministroTB;
        }

        public void setSuministroTB(SuministroTB suministroTB) {
            this.suministroTB = suministroTB;
        }

    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
