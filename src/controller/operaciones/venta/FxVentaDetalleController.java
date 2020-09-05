package controller.operaciones.venta;

import controller.reporte.FxReportViewController;
import controller.tools.BillPrintable;
import controller.tools.ConvertMonedaCadena;
import controller.tools.FilesRouters;
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
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
import model.EmpleadoTB;
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.SuministroTB;
import model.VentaADO;
import model.VentaTB;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import org.controlsfx.control.Notifications;

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
    private Button btnGuiaRemision;
    @FXML
    private Label lblEfectivo;
    @FXML
    private Label lblTarjeta;
    @FXML
    private Label lblVuelto;
    @FXML
    private Label lblValor;
    @FXML
    private Text lbClienteInformacion;

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
            task.setOnSucceeded(e -> {
                ArrayList<Object> objects = task.getValue();
                if (!objects.isEmpty()) {
                    ventaTB = (VentaTB) objects.get(0);
                    EmpleadoTB empleadoTB = (EmpleadoTB) objects.get(1);
                    ObservableList<SuministroTB> empList = (ObservableList<SuministroTB>) objects.get(2);
                    if (ventaTB != null) {
                        lblFechaVenta.setText(ventaTB.getFechaVenta() + " " + ventaTB.getHoraVenta());
                        lblCliente.setText(ventaTB.getClienteTB().getInformacion() + " ");
                        lbClienteInformacion.setText(ventaTB.getClienteTB().getDireccion() + " - " + ventaTB.getClienteTB().getCelular());
                        lblComprobante.setText(ventaTB.getComprobanteName());
                        lblSerie.setText(ventaTB.getSerie() + "-" + ventaTB.getNumeracion());
                        lblObservaciones.setText(ventaTB.getObservaciones());
                        lblTipo.setText(ventaTB.getTipoName() + "-" + ventaTB.getEstadoName());
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
                    btnGuiaRemision.setDisable(true);
                }

                lblLoad.setVisible(false);
            });
            task.setOnScheduled(e -> {
                lblLoad.setVisible(true);
            });
            task.setOnRunning(e -> {
                lblLoad.setVisible(true);
            });
            task.setOnFailed(e -> {
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

//    private void openWindowAbonos() {
//        if (lblTipo.getText().equalsIgnoreCase("credito")) {
//            try {
//                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
//                URL url = getClass().getResource(FilesRouters.FX_VENTA_ABONO);
//                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
//                Parent parent = fXMLLoader.load(url.openStream());
//
//                FxVentaAbonoController controller = fXMLLoader.getController();
//                controller.setInitVentaAbonoController(this);
//
//                Stage stage = WindowStage.StageLoaderModal(parent, "Historial de abonos", window.getScene().getWindow());
//                stage.setResizable(false);
//                stage.sizeToScene();
//                stage.setOnHiding(w -> {
//                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
//                });
//                stage.show();
//                controller.loadInitData(idVenta, ventaTB.getMonedaTB().getSimbolo());
//
//            } catch (IOException ex) {
//                System.out.println(ex.getLocalizedMessage());
//            }
//        } else {
//            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Detalle de Venta", "La venta se realizó al contado.", false);
//        }
//    }
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

            URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxReportViewController controller = fXMLLoader.getController();
            controller.setJasperPrint(reportA4(list));
            controller.show();
            Stage stage = WindowStage.StageLoader(parent, "Venta realizada");
            stage.setResizable(true);
            stage.show();
            stage.requestFocus();

        } catch (IOException | JRException ex) {
            Tools.AlertMessageError(window, "Reporte de Ventas", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    private JasperPrint reportA4(ArrayList<SuministroTB> list) throws JRException {

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
        map.put("CONDICIONPAGO", lblTipo.getText());

        map.put("SIMBOLO", ventaTB.getMonedaTB().getSimbolo());
        map.put("VALORSOLES", monedaCadena.Convertir(Tools.roundingValue(totalVenta, 2), true, ventaTB.getMonedaTB().getNombre()));

        map.put("VALOR_VENTA", lblValorVenta.getText());
        map.put("DESCUENTO", lblDescuento.getText());
        map.put("SUB_IMPORTE", lblSubTotal.getText());
//            map.put("CALCULAR_TOTALES", new JRBeanCollectionDataSource(list_totales));
////            map.put("SUBREPORT_DIR", "VentaRealizadaDetalle.jasper");
        map.put("IMPORTE_TOTAL", lblTotal.getText());

        JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JRBeanCollectionDataSource(list));
//            JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JREmptyDataSource());
        return jasperPrint;
    }

    private void onEventImprimirVenta() {

        if (!Session.ESTADO_IMPRESORA && Session.NOMBRE_IMPRESORA == null && Session.TIPO_IMPRESORA == null) {
            Tools.AlertMessageWarning(window, "Venta", "No esta configurado la ruta de impresión, ve a la sección configuración/impresora.");
            return;
        }

        if (Session.TIPO_IMPRESORA.equalsIgnoreCase("ticket")) {
            if (Session.TICKET_VENTA_ID == 0 && Session.TICKET_VENTA_RUTA.equalsIgnoreCase("")) {
                Tools.AlertMessageWarning(window, "Venta", "No hay un diseño predeterminado para la impresión, configure su ticket en la sección configuración/tickets.");
            } else {
                executeProcessPrinter();
            }
        } else if (Session.TIPO_IMPRESORA.equalsIgnoreCase("a4")) {
            executeProcessPrinter();
        } else {
            Tools.AlertMessageWarning(window, "Venta", "Error al validar el tipo de impresión, cofigure nuevamente la impresora.");
        }
    }

    private void executeProcessPrinter() {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<String> task = new Task<String>() {
            @Override
            public String call() {
                try {

                    if (Session.TIPO_IMPRESORA.equalsIgnoreCase("a4")) {

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

                        if (list.isEmpty()) {
                            return "empty";
                        } else {

                            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
                            printRequestAttributeSet.add(new Copies(1));

                            PrinterName printerName = new PrinterName(Session.NOMBRE_IMPRESORA, null);

                            PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
                            printServiceAttributeSet.add(printerName);

                            JRPrintServiceExporter exporter = new JRPrintServiceExporter();

                            exporter.setParameter(JRExporterParameter.JASPER_PRINT, reportA4(list));
                            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
                            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceAttributeSet);
                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
                            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
                            exporter.exportReport();
                            return "completed";
                        }
                    } else if (Session.TIPO_IMPRESORA.equalsIgnoreCase("ticket")) {

                        billPrintable.loadEstructuraTicket(Session.TICKET_VENTA_ID, Session.TICKET_VENTA_RUTA, hbEncabezado, hbDetalleCabecera, hbPie);

                        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
                            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
                            billPrintable.hbEncebezado(box,
                                    ventaTB.getComprobanteName(),
                                    lblSerie.getText(),
                                    ventaTB.getClienteTB().getNumeroDocumento(),
                                    ventaTB.getClienteTB().getInformacion(),
                                    ventaTB.getClienteTB().getCelular(),
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
                                    ventaTB.getClienteTB().getInformacion(), ventaTB.getCodigo(),
                                    ventaTB.getClienteTB().getCelular());
                        }

                        billPrintable.generatePDFPrint(hbEncabezado, hbDetalle, hbPie);

                        DocPrintJob job = billPrintable.findPrintService(Session.NOMBRE_IMPRESORA, PrinterJob.lookupPrintServices()).createPrintJob();

                        if (job != null) {
                            PrinterJob pj = PrinterJob.getPrinterJob();
                            pj.setPrintService(job.getPrintService());
                            pj.setJobName(Session.NOMBRE_IMPRESORA);
                            Book book = new Book();
                            book.append(billPrintable, billPrintable.getPageFormat(pj));
                            pj.setPageable(book);
                            pj.print();
                            if (Session.CORTAPAPEL_IMPRESORA) {
                                billPrintable.printCortarPapel(Session.NOMBRE_IMPRESORA);
                            }
                            return "completed";
                        } else {
                            return "error_name";
                        }
                    } else {
                        return "no_config";
                    }

                } catch (PrinterException | IOException | PrintException | JRException ex) {
                    return "Error en imprimir: " + ex.getLocalizedMessage();
                }
            }
        };

        task.setOnSucceeded(w -> {

            String result = task.getValue();
            if (result.equalsIgnoreCase("completed")) {
                showAlertNotification("/view/image/information_large.png",
                        "Envío de impresión",
                        "Se completo el proceso de impresión correctamente.",
                        Duration.seconds(5),
                        Pos.BOTTOM_RIGHT);
            } else if (result.equalsIgnoreCase("error_name")) {
                showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "Error en encontrar el nombre de la impresión por problemas de puerto o driver.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else if (result.equalsIgnoreCase("no_config")) {
                showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "Error al validar el tipo de impresión, cofigure nuevamente la impresora.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else if (result.equalsIgnoreCase("empty")) {
                showAlertNotification("/view/image/warning_large.png",
                        "Envío de impresión",
                        "No hay registros para mostrar en el reporte.",
                        Duration.seconds(10),
                        Pos.CENTER);
            } else {
                showAlertNotification("/view/image/error_large.png",
                        "Envío de impresión",
                        "Error en la configuración de su impresora: " + result,
                        Duration.seconds(10),
                        Pos.CENTER);
            }
        });
        task.setOnFailed(w -> {
            showAlertNotification("/view/image/warning_large.png",
                    "Envío de impresión",
                    "Se produjo un problema en el proceso de envío, \n intente nuevamente o comuníquese con su proveedor del sistema.",
                    Duration.seconds(10),
                    Pos.BOTTOM_RIGHT);
        });

        task.setOnScheduled(w -> {
            showAlertNotification("/view/image/print.png",
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

    private void showAlertNotification(String url, String title, String message, Duration duration, Pos pos) {
        Image image = new Image(url);
        Notifications notifications = Notifications.create()
                .title(title)
                .text(message)
                .graphic(new ImageView(image))
                .hideAfter(duration)
                .position(pos)
                .onAction(n -> {

                });
        notifications.darkStyle();
        notifications.show();
    }

    private void reportGuiaRemision() {
        try {
            InputStream imgInputStreamIcon = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            InputStream imgInputStream = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            if (Session.COMPANY_IMAGE != null) {
                imgInputStream = new ByteArrayInputStream(Session.COMPANY_IMAGE);
            }
            InputStream dir = getClass().getResourceAsStream("/report/GuiadeRemision.jasper");
            Map map = new HashMap();
            map.put("LOGO", imgInputStream);
            map.put("ICON", imgInputStreamIcon);
            map.put("RUC_EMPRESA", Session.COMPANY_NUMERO_DOCUMENTO);
            map.put("NOMBRE_EMPRESA", Session.COMPANY_RAZON_SOCIAL);
            map.put("DIRECCION_EMPRESA", Session.COMPANY_DOMICILIO);
            map.put("TELEFONO_EMPRESA", Session.COMPANY_TELEFONO);
            map.put("CELULAR_EMPRESA", Session.COMPANY_CELULAR);
            map.put("EMAIL_EMPRESA", Session.COMPANY_EMAIL);

            JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JREmptyDataSource());
            URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxReportViewController controller = fXMLLoader.getController();
            controller.setJasperPrint(jasperPrint);
            controller.show();
            Stage stage = WindowStage.StageLoader(parent, "Guia de remisión");
            stage.setResizable(true);
            stage.show();
            stage.requestFocus();
        } catch (JRException | IOException ex) {
            Tools.AlertMessageError(window, "Reporte de Ventas", "Error al generar el reporte : " + ex.getLocalizedMessage());
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
            onEventImprimirVenta();
        }
    }

    @FXML
    private void onActionImprimir(ActionEvent event) {
        onEventImprimirVenta();
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
    private void onKeyPressedReporte(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowReporte();
        }
    }

    @FXML
    private void onActionReporte(ActionEvent event) {
        openWindowReporte();
    }

    @FXML
    private void onKeyPressedGuiaRemision(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            reportGuiaRemision();
        }
    }

    @FXML
    private void onActionGuiaRemision(ActionEvent event) {
        reportGuiaRemision();
    }

    public void setInitVentasController(FxVentaRealizadasController ventaRealizadasController, AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.ventaRealizadasController = ventaRealizadasController;
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }
}
