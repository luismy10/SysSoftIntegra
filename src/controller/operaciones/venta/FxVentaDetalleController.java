package controller.operaciones.venta;

import controller.reporte.FxReportViewController;
import controller.tools.BillPrintable;
import controller.tools.ConvertMonedaCadena;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.awt.HeadlessException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.EmpleadoTB;
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.SuministroTB;
import model.VentaADO;
import model.VentaTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FxVentaDetalleController implements Initializable {

    @FXML
    private ScrollPane window;
    @FXML
    private Label lblLoad;
    @FXML
    private Text lblFechaVenta;
    @FXML
    private Text lblComprobante;
    @FXML
    private Label lblTotal;
    @FXML
    private Text lblCliente;
    @FXML
    private Text lblTipo;
    @FXML
    private Text lblObservaciones;
    @FXML
    private Text lblVendedor;
    @FXML
    private Text lblSerie;
    @FXML
    private GridPane gpList;
    @FXML
    private Label lblValorVenta;
    @FXML
    private Label lblDescuento;
    @FXML
    private Label lblSubTotal;
    @FXML
    private Text lblTotalVenta;
    @FXML
    private Button btnCancelarVenta;
    @FXML
    private Text lblValorVentaLetra;
    @FXML
    private GridPane gpOperaciones;
    @FXML
    private GridPane gpImpuestos;
    @FXML
    private Button btnReporte;
    @FXML
    private Button btnImprimir;
    @FXML
    private Button btnAbonos;
    @FXML
    private Label lblEfectivo;
    @FXML
    private Label lblTarjeta;
    @FXML
    private Label lblVuelto;
    @FXML
    private Label lblValor;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    private String idVenta;

    private double subImporte;

    private double descuento;

    private double subTotalImporte;

    private double totalImporte;

    private double total;

    private FxVentaRealizadasController ventaRealizadasController;

    private ObservableList<SuministroTB> arrList = null;

    private ArrayList<ImpuestoTB> arrayArticulos;

    private BillPrintable billPrintable;

    private ConvertMonedaCadena monedaCadena;

    private VentaTB ventaTB;

    private AnchorPane hbEncabezado;

    private AnchorPane hbDetalleCabecera;

    private AnchorPane hbPie;

    private double totalVenta;

    private double efectivo, tarjeta, vuelto;

    private Alert alert = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        arrayArticulos = new ArrayList<>();
        billPrintable = new BillPrintable();
        hbEncabezado = new AnchorPane();
        hbDetalleCabecera = new AnchorPane();
        hbPie = new AnchorPane();
        monedaCadena = new ConvertMonedaCadena();
    }

    public void setInitComponents(String idVenta) {
        this.idVenta = idVenta;
        try {

            ExecutorService executor = Executors.newCachedThreadPool((runnable) -> {
                Thread t = new Thread(runnable);
                t.setDaemon(true);
                return t;
            });

            Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
                @Override
                protected ArrayList<Object> call() {
                    arrayArticulos.clear();
                    ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> {
                        arrayArticulos.add(new ImpuestoTB(e.getIdImpuesto(), e.getNombreOperacion(), e.getNombreImpuesto(), e.getValor(), e.getPredeterminado()));
                    });
                    ArrayList<Object> objects = VentaADO.ListCompletaVentasDetalle(idVenta);
                    return objects;
                }
            };

            task.setOnScheduled(e -> {
                lblLoad.setVisible(true);
            });
            task.setOnRunning(e -> {
                lblLoad.setVisible(true);
            });
            task.setOnFailed(e -> {
                lblLoad.setVisible(false);
            });
            task.setOnSucceeded(e -> {

                ArrayList<Object> objects = task.getValue();

                if (!objects.isEmpty()) {
                    ventaTB = (VentaTB) objects.get(0);
                    EmpleadoTB empleadoTB = (EmpleadoTB) objects.get(1);
                    ObservableList<SuministroTB> empList = (ObservableList<SuministroTB>) objects.get(2);
                    if (ventaTB != null) {
                        lblFechaVenta.setText(ventaTB.getFechaVenta() + " " + ventaTB.getHoraVenta());
                        lblCliente.setText(ventaTB.getClienteTB().getInformacion());
                        lblComprobante.setText(ventaTB.getComprobanteName());
                        lblSerie.setText(ventaTB.getSerie() + "-" + ventaTB.getNumeracion());
                        lblObservaciones.setText(ventaTB.getObservaciones());
                        lblTipo.setText(ventaTB.getTipoName() + " " + ventaTB.getEstadoName());
                        btnCancelarVenta.setDisable(ventaTB.getEstadoName().equalsIgnoreCase("ANULADO"));
                        lblTotalVenta.setText(ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(ventaTB.getTotal(), 2));
                        efectivo = ventaTB.getEfectivo();
                        tarjeta = ventaTB.getTarjeta();
                        vuelto = ventaTB.getVuelto();
                        totalVenta = ventaTB.getTotal();
                        lblValorVentaLetra.setText(monedaCadena.Convertir(Tools.roundingValue(totalVenta, 2), true, ventaTB.getMonedaTB().getNombre()));

                        lblEfectivo.setText(Tools.roundingValue(efectivo, 2));
                        lblTarjeta.setText(Tools.roundingValue(tarjeta, 2));
                        lblValor.setText(Tools.roundingValue(totalVenta, 2));
                        lblVuelto.setText(Tools.roundingValue(vuelto, 2));
                    }

                    if (empleadoTB != null) {
                        lblVendedor.setText(empleadoTB.getApellidos() + " " + empleadoTB.getNombres());
                    }
                    fillVentasDetalleTable(empList);

                } else {
                    btnReporte.setDisable(true);
                    btnCancelarVenta.setDisable(true);
                    btnImprimir.setDisable(true);
                    btnAbonos.setDisable(true);
                }

                lblLoad.setVisible(false);
            });

            executor.execute(task);
            if (!executor.isShutdown()) {
                executor.shutdown();
            }
        } catch (Exception ex) {
            System.out.println(ex.getLocalizedMessage());
            lblLoad.setVisible(false);
        }
    }

    private void fillVentasDetalleTable(ObservableList<SuministroTB> empList) {
        arrList = empList;
        for (int i = 0; i < arrList.size(); i++) {
            gpList.add(addElementGridPane("l1" + (i + 1), arrList.get(i).getId() + "", Pos.CENTER), 0, (i + 1));
            gpList.add(addElementGridPane("l2" + (i + 1), arrList.get(i).getClave() + "\n" + arrList.get(i).getNombreMarca(), Pos.CENTER_LEFT), 1, (i + 1));
            gpList.add(addElementGridPane("l3" + (i + 1), Tools.roundingValue(arrList.get(i).getCantidad(), 2), Pos.CENTER_RIGHT), 2, (i + 1));
            gpList.add(addElementGridPane("l4" + (i + 1), arrList.get(i).getUnidadCompraName(), Pos.CENTER_LEFT), 3, (i + 1));
            gpList.add(addElementGridPane("l5" + (i + 1), arrList.get(i).getImpuestoArticuloName(), Pos.CENTER_RIGHT), 4, (i + 1));
            gpList.add(addElementGridPane("l6" + (i + 1), ventaTB.getMonedaTB().getSimbolo() + "" + Tools.roundingValue(arrList.get(i).getPrecioVentaGeneral(), 2), Pos.CENTER_RIGHT), 5, (i + 1));
            gpList.add(addElementGridPane("l7" + (i + 1), Tools.roundingValue(arrList.get(i).getDescuento(), 2) + "%", Pos.CENTER_RIGHT), 6, (i + 1));
            gpList.add(addElementGridPane("l8" + (i + 1), ventaTB.getMonedaTB().getSimbolo() + "" + Tools.roundingValue(arrList.get(i).getPrecioVentaGeneral() * arrList.get(i).getCantidad(), 2), Pos.CENTER_RIGHT), 7, (i + 1));
        }
        calcularTotales();
    }

    private void openAlertMessageWarning(String message) {
        ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Ventas", message, false);
        vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
    }

    private void onEventCancelar() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_VENTA_DEVOLUCION);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxVentaDevolucionController controller = fXMLLoader.getController();
            controller.setInitVentaDetalle(this);
            controller.setLoadVentaDevolucion(idVenta, arrList, lblComprobante.getText(), lblTotalVenta.getText(), totalVenta);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Cancelar la venta", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();
        } catch (IOException ex) {
            System.out.println("Error en venta detalle: " + ex.getLocalizedMessage());
        }
    }

    private void openWindowAbonos() {
        if (lblTipo.getText().equalsIgnoreCase("credito")) {
            try {
                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                URL url = getClass().getResource(FilesRouters.FX_VENTA_ABONO);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());

                FxVentaAbonoController controller = fXMLLoader.getController();
                controller.setInitVentaAbonoController(this);

                Stage stage = WindowStage.StageLoaderModal(parent, "Historial de abonos", window.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.setOnHiding(w -> {
                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                });
                stage.show();
                controller.loadInitData(idVenta, ventaTB.getMonedaTB().getSimbolo());

            } catch (IOException ex) {
                System.out.println(ex.getLocalizedMessage());
            }
        } else {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Detalle de Venta", "La venta se realizó al contado.", false);
        }

    }

    private void calcularTotales() {
        if (arrList != null) {

            subImporte = 0;
            arrList.forEach(e -> subImporte += e.getSubImporte());
            lblValorVenta.setText(ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(subImporte, 2));

            descuento = 0;
            arrList.forEach(e -> descuento += e.getDescuentoSumado());
            lblDescuento.setText(ventaTB.getMonedaTB().getSimbolo() + " -" + Tools.roundingValue(descuento, 2));

            subTotalImporte = 0;
            arrList.forEach(e -> subTotalImporte += e.getSubImporteDescuento());
            lblSubTotal.setText(ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(subTotalImporte, 2));

            gpOperaciones.getChildren().clear();
            gpImpuestos.getChildren().clear();

            boolean addOperacion = false;
            double sumaOperacion = 0;

            boolean addImpuesto = false;
            double sumaImpuesto = 0;
            double totalImpuestos = 0;

            for (int k = 0; k < arrayArticulos.size(); k++) {
                for (int i = 0; i < arrList.size(); i++) {
                    if (arrayArticulos.get(k).getIdImpuesto() == arrList.get(i).getImpuestoArticulo()) {
                        addOperacion = true;
                        sumaOperacion += arrList.get(i).getSubImporteDescuento();
                    }
                }
                if (addOperacion) {
                    gpOperaciones.add(addLabelTitle(arrayArticulos.get(k).getNombreOperacion().toLowerCase().substring(0, 1).toUpperCase()
                            + "" + arrayArticulos.get(k).getNombreOperacion().substring(1, arrayArticulos.get(k).getNombreOperacion().length()).toLowerCase(),
                            Pos.CENTER_LEFT), 0, k + 1);
                    gpOperaciones.add(addLabelTotal(ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(sumaOperacion, 2), Pos.CENTER_RIGHT), 1, k + 1);
                    addOperacion = false;
                    sumaOperacion = 0;
                }
            }

            for (int k = 0; k < arrayArticulos.size(); k++) {
                for (int i = 0; i < arrList.size(); i++) {
                    if (arrayArticulos.get(k).getIdImpuesto() == arrList.get(i).getImpuestoArticulo()) {
                        addImpuesto = true;
                        sumaImpuesto += arrList.get(i).getImpuestoSumado();
                    }
                }
                if (addImpuesto) {
                    gpImpuestos.add(addLabelTitle(arrayArticulos.get(k).getNombreImpuesto(), Pos.CENTER_LEFT), 0, k + 1);
                    gpImpuestos.add(addLabelTotal(ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(sumaImpuesto, 2), Pos.CENTER_RIGHT), 1, k + 1);
                    totalImpuestos += sumaImpuesto;
                    addImpuesto = false;
                    sumaImpuesto = 0;
                }
            }

            totalImporte = 0;
            arrList.forEach(e -> totalImporte += e.getTotalImporte());

            total = totalImporte + totalImpuestos;
            lblTotal.setText(ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(total, 2));
        }

    }

    private Label addElementGridPane(String id, String nombre, Pos pos) {
        Label label = new Label(nombre);
        label.setId(id);
        label.setStyle("-fx-text-fill:#020203;-fx-background-color: #dddddd;-fx-padding: 0.4166666666666667em 0.8333333333333334em 0.4166666666666667em 0.8333333333333334em;");
        label.getStyleClass().add("labelRoboto14");
        label.setAlignment(pos);
        label.setWrapText(true);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        return label;
    }

    private Label addLabelTitle(String nombre, Pos pos) {
        Label label = new Label(nombre);
        label.setStyle("-fx-text-fill:#000000;-fx-padding: 0.4166666666666667em 0em  0.4166666666666667em 0em;");
        label.getStyleClass().add("labelRoboto14");
        label.setAlignment(pos);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Control.USE_COMPUTED_SIZE);
        return label;
    }

    private Label addLabelTotal(String nombre, Pos pos) {
        Label label = new Label(nombre);
        label.setStyle("-fx-text-fill:#0771d3;");
        label.getStyleClass().add("labelRobotoMedium16");
        label.setAlignment(pos);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Control.USE_COMPUTED_SIZE);
        return label;
    }

    private void openWindowReporte() {
        try {

            ArrayList<SuministroTB> list = new ArrayList();
            arrList.stream().map((suministroTB) -> {
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

            boolean addOperacion = false;
            double sumaOperacion = 0;

            boolean addImpuesto = false;
            double sumaImpuesto = 0;

            ArrayList<SuministroTB> list_totales = new ArrayList();

            for (int k = 0; k < arrayArticulos.size(); k++) {
                for (int i = 0; i < arrList.size(); i++) {
                    if (arrayArticulos.get(k).getIdImpuesto() == arrList.get(i).getImpuestoArticulo()) {
                        addOperacion = true;
                        sumaOperacion += arrList.get(i).getSubImporteDescuento();
                    }
                }
                if (addOperacion) {
                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setImpuestoArticuloName(arrayArticulos.get(k).getNombreOperacion().toLowerCase().substring(0, 1).toUpperCase() + arrayArticulos.get(k).getNombreOperacion().toLowerCase().substring(1, arrayArticulos.get(k).getNombreOperacion().length()).toLowerCase() + ":");
                    suministroTB.setImpuestoValor(sumaOperacion);
                    list_totales.add(suministroTB);
                    addOperacion = false;
                    sumaOperacion = 0;
                }
            }

            for (int k = 0; k < arrayArticulos.size(); k++) {
                for (int i = 0; i < arrList.size(); i++) {
                    if (arrayArticulos.get(k).getIdImpuesto() == arrList.get(i).getImpuestoArticulo()) {
                        addImpuesto = true;
                        sumaImpuesto += arrList.get(i).getImpuestoSumado();
                    }
                }
                if (addImpuesto) {
                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setImpuestoArticuloName(arrayArticulos.get(k).getNombreImpuesto() + ":");
                    suministroTB.setImpuestoValor(sumaImpuesto);
                    list_totales.add(suministroTB);
                    addImpuesto = false;
                    sumaImpuesto = 0;
                }
            }

            if (list.isEmpty()) {
                Tools.AlertMessageWarning(window, "Venta realizada", "No hay registros para mostrar en el reporte.");
                return;
            }
            InputStream imgInputStream
                    = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            InputStream dir = getClass().getResourceAsStream("/report/VentaRealizada.jasper");

            Map map = new HashMap();
            map.put("LOGO", imgInputStream);
            map.put("EMPRESA", Session.COMPANY_RAZON_SOCIAL);
            map.put("DIRECCION", Session.COMPANY_DOMICILIO);
            map.put("TELEFONOCELULAR", "Tel.: " + Session.COMPANY_TELEFONO + " Cel.: " + Session.COMPANY_CELULAR);
            map.put("EMAIL", "Email: " + Session.COMPANY_EMAIL);
            map.put("DOCUMENTOEMPRESA", "R.U.C " + Session.COMPANY_NUM_DOCUMENTO);

            map.put("NOMBREDOCUMENTO", ventaTB.getComprobanteName());
            map.put("NUMERODOCUMENTO", ventaTB.getSerie() + "-" + ventaTB.getNumeracion());
            map.put("DATOSCLIENTE", ventaTB.getClienteTB().getInformacion());
            map.put("DOCUMENTOCLIENTE", ventaTB.getClienteTB().getTipoDocumentoName() + " N°:");
            map.put("NUMERODOCUMENTOCLIENTE", ventaTB.getClienteTB().getNumeroDocumento());
            map.put("DIRECCIONCLIENTE", ventaTB.getClienteTB().getDireccion());

            map.put("FECHAEMISION", ventaTB.getFechaVenta());
            map.put("MONEDA", ventaTB.getMonedaTB().getAbreviado());

            map.put("VALOR_VENTA", lblValorVenta.getText());
            map.put("DESCUENTO", lblDescuento.getText());
            map.put("SUB_TOTAL", lblSubTotal.getText());
            map.put("CALCULAR_TOTALES", new JRBeanCollectionDataSource(list_totales));
//            map.put("SUBREPORT_DIR", "VentaRealizadaDetalle.jasper");
            map.put("TOTAL", lblTotal.getText());
            map.put("SIMBOLO", ventaTB.getMonedaTB().getSimbolo());
            map.put("VALORSOLES", monedaCadena.Convertir(Tools.roundingValue(totalVenta, 2), true, ventaTB.getMonedaTB().getNombre()));

            JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JRBeanCollectionDataSource(list));

            URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxReportViewController controller = fXMLLoader.getController();
            controller.setJasperPrint(jasperPrint);
            controller.show();
            Stage stage = WindowStage.StageLoader(parent, "Venta realizada");
            stage.setResizable(true);
            stage.show();
            stage.requestFocus();

        } catch (HeadlessException | JRException | IOException ex) {
            Tools.AlertMessageError(window, "Reporte de Ventas", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    private void onEventimprimirVenta() {
        if (Session.TICKET_VENTA_ID == 0 && Session.TICKET_VENTA_RUTA.equalsIgnoreCase("")) {
            Tools.AlertMessageWarning(window, "Venta", "No hay un diseño predeterminado para la impresión, configure su ticket en la sección configuración/tickets.");
            return;
        }

        if (!Session.ESTADO_IMPRESORA && Session.NOMBRE_IMPRESORA == null) {
            Tools.AlertMessageWarning(window, "Venta", "No hay ruta de impresión, presione F8 o has un click en la opción impresora del mismo formulario actual, para configurar la ruta de impresión..");
            return;
        }

        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        try {

            Task<String> task = new Task<String>() {
                @Override
                public String call() {

                    billPrintable.loadEstructuraTicket(Session.TICKET_VENTA_ID, Session.TICKET_VENTA_RUTA, hbEncabezado, hbDetalleCabecera, hbPie);

                    for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
                        HBox box = ((HBox) hbEncabezado.getChildren().get(i));
                        billPrintable.hbEncebezado(box,
                                ventaTB.getComproabanteNameImpresion(),
                                lblSerie.getText(),
                                ventaTB.getClienteTB().getNumeroDocumento(),
                                ventaTB.getClienteTB().getInformacion(), 
                                ventaTB.getClienteTB().getDireccion(),
                                ventaTB.getCodigo());
                    }

                    AnchorPane hbDetalle = new AnchorPane();
                    for (int m = 0; m < arrList.size(); m++) {
                        for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                            HBox hBox = new HBox();
                            hBox.setId("dc_" + m + "" + i);
                            HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                            billPrintable.hbDetalle(hBox, box, arrList, m);
                            hbDetalle.getChildren().add(hBox);
                        }
                    }

                    for (int i = 0; i < hbPie.getChildren().size(); i++) {
                        HBox box = ((HBox) hbPie.getChildren().get(i));
                        billPrintable.hbPie(box, ventaTB.getMonedaTB().getSimbolo(),
                                Tools.roundingValue(subImporte, 2),
                                "-" + Tools.roundingValue(descuento, 2),
                                Tools.roundingValue(subTotalImporte, 2),
                                Tools.roundingValue(total, 2),
                                Tools.roundingValue(efectivo, 2),
                                Tools.roundingValue(vuelto, 2),
                                ventaTB.getClienteTB().getNumeroDocumento(),
                                ventaTB.getClienteTB().getInformacion(), ventaTB.getCodigo());
                    }

                    return billPrintable.generatePDFPrint(hbEncabezado, hbDetalle, hbPie, Session.NOMBRE_IMPRESORA, Session.CORTAPAPEL_IMPRESORA);
                }
            };

            task.setOnSucceeded(w -> {
                if (!task.isRunning()) {
                    if (alert != null) {
                        ((Stage) (alert.getDialogPane().getScene().getWindow())).close();
                    }
                }
                String result = task.getValue();
                if (result.equalsIgnoreCase("completed")) {
                    Tools.AlertMessageInformation(window, "Ventas", "Se completo el proceso de impresión correctamente.");
                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                } else {
                    Tools.AlertMessageError(window, "Ventas", result);
                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                }
            });
            task.setOnFailed(w -> {
                if (alert != null) {
                    ((Stage) (alert.getDialogPane().getScene().getWindow())).close();
                }
                Tools.AlertMessageWarning(window, "Ventas", "Se produjo un problema en el proceso de envío, intente nuevamente.");
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });

            task.setOnScheduled(w -> {
                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                alert = Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.NONE, "Se envió la impresión a la cola, este proceso puede tomar unos segundos.");
            });
            exec.execute(task);

        } catch (Exception ex) {
        } finally {
            exec.shutdown();
        }

    }

    @FXML
    private void onKeyPressedCancelar(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            onEventCancelar();
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) throws IOException {
        onEventCancelar();
    }

    @FXML
    private void onKeyPressedImprimir(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventimprimirVenta();
        }
    }

    @FXML
    private void onActionImprimir(ActionEvent event) {
        onEventimprimirVenta();
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) throws IOException {
        vbContent.getChildren().remove(window);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(ventaRealizadasController.getWindow(), 0d);
        AnchorPane.setTopAnchor(ventaRealizadasController.getWindow(), 0d);
        AnchorPane.setRightAnchor(ventaRealizadasController.getWindow(), 0d);
        AnchorPane.setBottomAnchor(ventaRealizadasController.getWindow(), 0d);
        vbContent.getChildren().add(ventaRealizadasController.getWindow());

    }

    @FXML
    private void onKeyPressedHistorialPagos(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowAbonos();
        }
    }

    @FXML
    private void onActionHistorialPagos(ActionEvent event) {
        openWindowAbonos();
    }

    @FXML
    private void onKeyPressedReporte(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowReporte();
        }
    }

    @FXML
    private void onActionReporte(ActionEvent event) {
        openWindowReporte();
    }

    public void setInitVentasController(FxVentaRealizadasController ventaRealizadasController, AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.ventaRealizadasController = ventaRealizadasController;
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
