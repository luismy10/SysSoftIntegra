package controller.operaciones.venta;

import controller.menus.FxPrincipalController;
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
import javafx.collections.FXCollections;
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
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

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
    private Text lbCorreoElectronico;
    @FXML
    private Text lbDireccion;
    @FXML
    private Text lblTipo;
    @FXML
    private Text lblObservaciones;
    @FXML
    private Text lblVendedor;
    @FXML
    private GridPane gpList;
    @FXML
    private Label lblValorVenta;
    @FXML
    private Label lblDescuento;
    @FXML
    private Label lblSubTotal;
    @FXML
    private Button btnCancelarVenta;
    @FXML
    private GridPane gpImpuestos;
    @FXML
    private Button btnReporte;
    @FXML
    private Button btnImprimir;
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

    private FxPrincipalController fxPrincipalController;

    private String idVenta;

    private double importeBruto;

    private double descuentoBruto;

    private double subImporteNeto;

    private double importeNeto;

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

        ExecutorService executor = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<Object> task = new Task<Object>() {
            @Override
            protected Object call() {
                arrayArticulos.clear();
                ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> {
                    arrayArticulos.add(new ImpuestoTB(e.getIdImpuesto(), e.getNombreOperacion(), e.getNombre(), e.getValor(), e.isPredeterminado()));
                });
                return VentaADO.ListCompletaVentasDetalle(idVenta);
            }
        };

        task.setOnScheduled(e -> {
            lblLoad.setVisible(true);
        });

        task.setOnSucceeded(e -> {
            Object result = task.getValue();
            if (result instanceof VentaTB) {
                ventaTB = (VentaTB) result;
                EmpleadoTB empleadoTB = ventaTB.getEmpleadoTB();
                ObservableList<SuministroTB> empList = FXCollections.observableArrayList(ventaTB.getSuministroTBs());
                lblFechaVenta.setText(ventaTB.getFechaVenta() + " " + ventaTB.getHoraVenta());
                lblCliente.setText(ventaTB.getClienteTB().getNumeroDocumento() + "-" + ventaTB.getClienteTB().getInformacion());
                lbClienteInformacion.setText(ventaTB.getClienteTB().getTelefono() + "-" + ventaTB.getClienteTB().getCelular());
                lbCorreoElectronico.setText(ventaTB.getClienteTB().getEmail());
                lbDireccion.setText(ventaTB.getClienteTB().getDireccion());
                lblComprobante.setText(ventaTB.getComprobanteName() + " " + ventaTB.getSerie() + "-" + ventaTB.getNumeracion());
                lblObservaciones.setText(ventaTB.getObservaciones());
                lblTipo.setText(ventaTB.getTipoName() + " - " + ventaTB.getEstadoName());
                btnCancelarVenta.setDisable(ventaTB.getEstado() == 3);
                efectivo = ventaTB.getEfectivo();
                tarjeta = ventaTB.getTarjeta();
                vuelto = ventaTB.getVuelto();
                totalVenta = ventaTB.getTotal();

                lblEfectivo.setText(Tools.roundingValue(efectivo, 2));
                lblTarjeta.setText(Tools.roundingValue(tarjeta, 2));
                lblValor.setText(Tools.roundingValue(totalVenta, 2));
                lblVuelto.setText(Tools.roundingValue(vuelto, 2));

                if (empleadoTB != null) {
                    lblVendedor.setText(empleadoTB.getApellidos() + " " + empleadoTB.getNombres());
                }
                fillVentasDetalleTable(empList);
                lblLoad.setVisible(false);
            } else {
                btnReporte.setDisable(true);
                btnCancelarVenta.setDisable(true);
                btnImprimir.setDisable(true);
                lblLoad.setVisible(false);
            }
        });
        task.setOnFailed(e -> {
            lblLoad.setVisible(false);
        });
        executor.execute(task);
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
    }

    private void fillVentasDetalleTable(ObservableList<SuministroTB> empList) {
        arrList = empList;
        for (int i = 0; i < arrList.size(); i++) {
            gpList.add(addElementGridPaneLabel("l1" + (i + 1), arrList.get(i).getId() + "", Pos.CENTER), 0, (i + 1));
            gpList.add(addElementGridPaneLabel("l2" + (i + 1), arrList.get(i).getClave() + "\n" + arrList.get(i).getNombreMarca(), Pos.CENTER_LEFT), 1, (i + 1));
            gpList.add(addElementGridPaneLabel("l3" + (i + 1), Tools.roundingValue(arrList.get(i).getCantidad(), 2), Pos.CENTER_RIGHT), 2, (i + 1));
            gpList.add(addElementGridPaneLabel("l4" + (i + 1), arrList.get(i).getUnidadCompraName(), Pos.CENTER_LEFT), 3, (i + 1));
            gpList.add(addElementGridPaneLabel("l5" + (i + 1), arrList.get(i).getImpuestoNombre(), Pos.CENTER_RIGHT), 4, (i + 1));
            gpList.add(addElementGridPaneLabel("l6" + (i + 1), ventaTB.getMonedaTB().getSimbolo() + "" + Tools.roundingValue(arrList.get(i).getPrecioVentaGeneral(), 2), Pos.CENTER_RIGHT), 5, (i + 1));
            gpList.add(addElementGridPaneLabel("l7" + (i + 1), Tools.roundingValue(arrList.get(i).getDescuento(), 2) + "%", Pos.CENTER_RIGHT), 6, (i + 1));
            gpList.add(addElementGridPaneLabel("l8" + (i + 1), ventaTB.getMonedaTB().getSimbolo() + "" + Tools.roundingValue(arrList.get(i).getPrecioVentaGeneral() * arrList.get(i).getCantidad(), 2), Pos.CENTER_RIGHT), 7, (i + 1));
            gpList.add(arrList.get(i).getEstadoName().equalsIgnoreCase("C")
                    ? addElementGridPaneLabel("l9" + (i + 1), "COMPLETADO", Pos.CENTER_LEFT)
                    : addElementGridPaneButtonLlevar("l9" + (i + 1), "LLEVAR \n" + Tools.roundingValue(arrList.get(i).getPorLlevar(), 2), arrList.get(i).getIdSuministro(), arrList.get(i).getCostoCompra(), Pos.CENTER_LEFT),
                    8, (i + 1));
            gpList.add(addElementGridPaneButtonHistorial("l10" + (i + 1), "HISTORIAL", arrList.get(i), Pos.CENTER_LEFT), 9, (i + 1));
        }
        calcularTotales();
    }

    private void onEventCancelar() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_VENTA_DEVOLUCION);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxVentaDevolucionController controller = fXMLLoader.getController();
            controller.setInitVentaDetalle(this);
            controller.setLoadVentaDevolucion(idVenta, arrList, ventaTB.getSerie() + "-" + ventaTB.getNumeracion(), Tools.roundingValue(importeNeto, 2));
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Cancelar la venta", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
        } catch (IOException ex) {
            System.out.println("Error en venta detalle: " + ex.getLocalizedMessage());
        }
    }

    private void calcularTotales() {
        if (arrList != null) {

            importeBruto = 0;
            arrList.forEach(e -> importeBruto += e.getImporteBruto());
            lblValorVenta.setText(ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(importeBruto, 2));

            descuentoBruto = 0;
            arrList.forEach(e -> descuentoBruto += e.getDescuentoSumado());
            lblDescuento.setText(ventaTB.getMonedaTB().getSimbolo() + " -" + Tools.roundingValue(descuentoBruto, 2));

            subImporteNeto = 0;
            arrList.forEach(e -> subImporteNeto += e.getSubImporteNeto());
            lblSubTotal.setText(ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(subImporteNeto, 2));

//            gpOperaciones.getChildren().clear();
            gpImpuestos.getChildren().clear();

//            boolean addOperacion = false;
//            double sumaOperacion = 0;
            boolean addImpuesto = false;
            double sumaImpuesto = 0;
            double totalImpuestos = 0;

//            for (int k = 0; k < arrayArticulos.size(); k++) {
//                for (int i = 0; i < arrList.size(); i++) {
//                    if (arrayArticulos.get(k).getIdImpuesto() == arrList.get(i).getImpuestoId()) {
//                        addOperacion = true;
//                        sumaOperacion += arrList.get(i).getSubImporteDescuento();
//                    }
//                }
//                if (addOperacion) {
//                    gpOperaciones.add(addLabelTitle(arrayArticulos.get(k).getNombreOperacion(), Pos.CENTER_LEFT), 0, k + 1);
//                    gpOperaciones.add(addLabelTotal(ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(sumaOperacion, 2), Pos.CENTER_RIGHT), 1, k + 1);
//                    addOperacion = false;
//                    sumaOperacion = 0;
//                }
//            }
            for (int k = 0; k < arrayArticulos.size(); k++) {
                for (int i = 0; i < arrList.size(); i++) {
                    if (arrayArticulos.get(k).getIdImpuesto() == arrList.get(i).getImpuestoId()) {
                        addImpuesto = true;
                        sumaImpuesto += arrList.get(i).getImpuestoSumado();
                    }
                }
                if (addImpuesto) {
                    gpImpuestos.add(addLabelTitle(arrayArticulos.get(k).getNombre(), Pos.CENTER_LEFT), 0, (k + 1) - 1);
                    gpImpuestos.add(addLabelTotal(ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(sumaImpuesto, 2), Pos.CENTER_RIGHT), 1, (k + 1) - 1);
                    totalImpuestos += sumaImpuesto;
                    addImpuesto = false;
                    sumaImpuesto = 0;
                }
            }

            importeNeto = subImporteNeto + totalImpuestos;
            lblTotal.setText(ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(importeNeto, 2));
        }

    }

    private Label addElementGridPaneLabel(String id, String nombre, Pos pos) {
        Label label = new Label(nombre);
        label.setId(id);
        label.setStyle("-fx-text-fill:#020203;-fx-background-color: #dddddd;-fx-padding: 0.4166666666666667em 0.8333333333333334em 0.4166666666666667em 0.8333333333333334em;");
        label.getStyleClass().add("labelRoboto13");
        label.setAlignment(pos);
        label.setWrapText(true);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        return label;
    }

    private Button addElementGridPaneButtonLlevar(String id, String nombre, String idSuministro, double costo, Pos pos) {
        Button button = new Button(nombre);
        button.setId(id);
        button.getStyleClass().add("buttonLightCancel");
        button.setAlignment(pos);
        button.setPrefWidth(Control.USE_COMPUTED_SIZE);
        button.setPrefHeight(Control.USE_COMPUTED_SIZE);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMaxHeight(Double.MAX_VALUE);
        ImageView imageView = new ImageView(new Image("/view/image/plus.png"));
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);
        button.setGraphic(imageView);
        button.setOnAction(event -> {
            openWindowLlevar(idSuministro, costo);
        });
        button.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                openWindowLlevar(idSuministro, costo);
            }
        });
        return button;
    }

    private Button addElementGridPaneButtonHistorial(String id, String nombre, SuministroTB suministroTB, Pos pos) {
        Button button = new Button(nombre);
        button.setId(id);
        button.getStyleClass().add("buttonLightCancel");
        button.setAlignment(pos);
        button.setPrefWidth(Control.USE_COMPUTED_SIZE);
        button.setPrefHeight(Control.USE_COMPUTED_SIZE);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMaxHeight(Double.MAX_VALUE);
        ImageView imageView = new ImageView(new Image("/view/image/asignacion.png"));
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);
        button.setGraphic(imageView);
        button.setOnAction(event -> {
            openWindowHistorial(suministroTB);
        });
        button.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                openWindowHistorial(suministroTB);
            }
        });
        return button;
    }

    private Label addLabelTitle(String nombre, Pos pos) {
        Label label = new Label(nombre);
        label.setStyle("-fx-text-fill: #020203;-fx-padding:  0.4166666666666667em 0em  0.4166666666666667em 0em");
        label.getStyleClass().add("labelRoboto13");
        label.setAlignment(pos);
        label.setMinWidth(Control.USE_COMPUTED_SIZE);
        label.setMinHeight(Control.USE_COMPUTED_SIZE);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Control.USE_COMPUTED_SIZE);
        return label;
    }

    private Label addLabelTotal(String nombre, Pos pos) {
        Label label = new Label(nombre);
        label.setStyle("-fx-text-fill:#0771d3;");
        label.getStyleClass().add("labelRobotoMedium15");
        label.setAlignment(pos);
        label.setMinWidth(Control.USE_COMPUTED_SIZE);
        label.setMinHeight(Control.USE_COMPUTED_SIZE);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Control.USE_COMPUTED_SIZE);
        return label;
    }

    private void openWindowLlevar(String idSuministro, double costo) {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_VENTA_LLEVAR);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxVentaLlevarController controller = fXMLLoader.getController();
            controller.setInitData(idVenta, idSuministro, lblComprobante.getText(), costo);
            controller.setInitVentaDetalleController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Producto a llevar", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();

        } catch (IOException ex) {
            Tools.println("Venta estructura openWindowLlevar: " + ex.getLocalizedMessage());
        }
    }

    private void openWindowHistorial(SuministroTB suministroTB) {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_VENTA_LLEVAR_HISTORIAL);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxVentaLlevarControllerHistorial controller = fXMLLoader.getController();
            controller.setInitVentaDetalleController(this);
            controller.loadData(ventaTB, suministroTB);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Historial de Salida", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();

        } catch (IOException ex) {
            Tools.println("Venta estructura openWindowHistorial: " + ex.getLocalizedMessage());
        }
    }

    private void openWindowReporte() {
        try {
            ArrayList<SuministroTB> list = new ArrayList();
            for (int i = 0; i < arrList.size(); i++) {
                SuministroTB stb = new SuministroTB();
                stb.setId(i + 1);
                stb.setClave(arrList.get(i).getClave());
                stb.setNombreMarca(arrList.get(i).getNombreMarca());
                stb.setCantidad(arrList.get(i).getCantidad());
                stb.setUnidadCompraName(arrList.get(i).getUnidadCompraName());
                stb.setPrecioVentaGeneral(arrList.get(i).getPrecioVentaGeneral());
                stb.setDescuento(arrList.get(i).getDescuento());
                stb.setImporteNeto(arrList.get(i).getCantidad() * +arrList.get(i).getPrecioVentaGeneral());
                list.add(stb);
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
            controller.setFileName(ventaTB.getComprobanteName().toUpperCase() + " " + ventaTB.getSerie() + "-" + ventaTB.getNumeracion());
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
        map.put("NOMBREDOCUMENTO", ventaTB.getComprobanteName().toUpperCase());
        map.put("NUMERODOCUMENTO", ventaTB.getSerie() + "-" + ventaTB.getNumeracion());

        map.put("DATOSCLIENTE", ventaTB.getClienteTB().getInformacion());
        map.put("DOCUMENTOCLIENTE", ventaTB.getClienteTB().getTipoDocumentoName().toUpperCase() + ":");
        map.put("NUMERODOCUMENTOCLIENTE", ventaTB.getClienteTB().getNumeroDocumento());
        map.put("CELULARCLIENTE", ventaTB.getClienteTB().getCelular());
        map.put("EMAILCLIENTE", ventaTB.getClienteTB().getEmail());
        map.put("DIRECCIONCLIENTE", ventaTB.getClienteTB().getDireccion());

        map.put("FECHAEMISION", ventaTB.getFechaVenta());
        map.put("MONEDA", ventaTB.getMonedaTB().getNombre() + "-" + ventaTB.getMonedaTB().getAbreviado());
        map.put("CONDICIONPAGO", lblTipo.getText());

        map.put("SIMBOLO", ventaTB.getMonedaTB().getSimbolo());
        map.put("VALORSOLES", monedaCadena.Convertir(Tools.roundingValue(totalVenta, 2), true, ventaTB.getMonedaTB().getNombre()));

        map.put("VALOR_VENTA", lblValorVenta.getText());
        map.put("DESCUENTO", lblDescuento.getText());
        map.put("SUB_IMPORTE", lblSubTotal.getText());
        map.put("IMPUESTO_TOTAL", Tools.roundingValue(ventaTB.getImpuesto(), 2));
        map.put("IMPORTE_TOTAL", lblTotal.getText());
        map.put("QRDATA", Session.COMPANY_NUMERO_DOCUMENTO + "|" + ventaTB.getCodigoAlterno() + "|" + ventaTB.getSerie() + "|" + ventaTB.getNumeracion() + "|" + Tools.roundingValue(ventaTB.getImpuesto(), 2) + "|" + Tools.roundingValue(importeNeto, 2) + "|" + ventaTB.getFechaVenta() + "|" + ventaTB.getClienteTB().getIdAuxiliar() + "|" + ventaTB.getClienteTB().getNumeroDocumento() + "|");

        JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JRBeanCollectionDataSource(list));
        return jasperPrint;
    }

    private void onEventImprimirVenta() {
        if (!Session.ESTADO_IMPRESORA_VENTA && Tools.isText(Session.NOMBRE_IMPRESORA_VENTA) && Tools.isText(Session.FORMATO_IMPRESORA_VENTA)) {
            Tools.AlertMessageWarning(window, "Venta", "No esta configurado la ruta de impresión ve a la sección configuración/impresora.");
            return;
        }

        if (Session.FORMATO_IMPRESORA_VENTA.equalsIgnoreCase("ticket")) {
            if (Session.TICKET_VENTA_ID == 0 && Session.TICKET_VENTA_RUTA.equalsIgnoreCase("")) {
                Tools.AlertMessageWarning(window, "Venta", "No hay un diseño predeterminado para la impresión configure su ticket en la sección configuración/tickets.");
            } else {
                executeProcessPrinter(Session.FORMATO_IMPRESORA_VENTA);
            }
        } else if (Session.FORMATO_IMPRESORA_VENTA.equalsIgnoreCase("a4")) {
            executeProcessPrinter(Session.FORMATO_IMPRESORA_VENTA);
        } else {
            Tools.AlertMessageWarning(window, "Venta", "Error al validar el formato de impresión configure en la sección configuración/impresora.");
        }
    }

    private void executeProcessPrinter(String format) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<String> task = new Task<String>() {
            @Override
            public String call() {
                try {

                    if (format.equalsIgnoreCase("a4")) {

                        ArrayList<SuministroTB> list = new ArrayList();
                        for (int i = 0; i < arrList.size(); i++) {
                            SuministroTB stb = new SuministroTB();
                            stb.setId(i + 1);
                            stb.setClave(arrList.get(i).getClave());
                            stb.setNombreMarca(arrList.get(i).getNombreMarca());
                            stb.setCantidad(arrList.get(i).getCantidad());
                            stb.setUnidadCompraName(arrList.get(i).getUnidadCompraName());
                            stb.setPrecioVentaGeneral(arrList.get(i).getPrecioVentaGeneral());
                            stb.setDescuento(arrList.get(i).getDescuento());
                            stb.setImporteNeto(arrList.get(i).getCantidad() * +arrList.get(i).getPrecioVentaGeneral());
                            list.add(stb);
                        }

                        if (list.isEmpty()) {
                            return "empty";
                        } else {

                            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
                            printRequestAttributeSet.add(new Copies(1));

                            PrinterName printerName = new PrinterName(Session.NOMBRE_IMPRESORA_VENTA, null);

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
                    } else {

                        if (Session.DESING_IMPRESORA_VENTA.equalsIgnoreCase("withdesing")) {
                            billPrintable.loadEstructuraTicket(Session.TICKET_VENTA_ID, Session.TICKET_VENTA_RUTA, hbEncabezado, hbDetalleCabecera, hbPie);
                            ObjectGlobal.QR_PERU_DATA = "|" + Session.COMPANY_NUMERO_DOCUMENTO + "|" + ventaTB.getCodigoAlterno() + "|" + ventaTB.getSerie() + "|" + ventaTB.getNumeracion() + "|" + Tools.roundingValue(ventaTB.getImpuesto(), 2) + "|" + Tools.roundingValue(ventaTB.getTotal(), 2) + "|" + ventaTB.getFechaVenta() + "|" + ventaTB.getClienteTB().getIdAuxiliar() + "|" + ventaTB.getClienteTB().getNumeroDocumento() + "|";

                            for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
                                HBox box = ((HBox) hbEncabezado.getChildren().get(i));
                                billPrintable.hbEncebezado(box,
                                        ventaTB.getComprobanteName(),
                                        ventaTB.getSerie() + "-" + ventaTB.getNumeracion(),
                                        ventaTB.getClienteTB().getNumeroDocumento(),
                                        ventaTB.getClienteTB().getInformacion(),
                                        ventaTB.getClienteTB().getCelular(),
                                        ventaTB.getClienteTB().getDireccion(),
                                        ventaTB.getCodigo(),
                                        monedaCadena.Convertir(Tools.roundingValue(totalVenta, 2), true, ventaTB.getMonedaTB().getNombre()),
                                        ventaTB.getFechaVenta(),
                                        ventaTB.getHoraVenta(),
                                        "fechaTerminoOperaciona",
                                        "horaTerminoOperacion",
                                        "calculado",
                                        "contado",
                                        "diferencia",
                                        "empleadoNumeroDocumento",
                                        "empleadoInformacion",
                                        "empleadoCelular",
                                        "empleadoDireccion",
                                        "montoTotal",
                                        "montoPagado",
                                        "montoDiferencial",
                                        "obsevacion_descripción",
                                        "monto_inicial_caja",
                                        "monto_efectivo_caja",
                                        "monto_tarjeta_caja",
                                        "monto_ingreso_caja",
                                        "monto_egreso_caja");
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
                                        Tools.roundingValue(importeBruto, 2),
                                        "-" + Tools.roundingValue(descuentoBruto, 2),
                                        Tools.roundingValue(subImporteNeto, 2),
                                        Tools.roundingValue(ventaTB.getImpuesto(), 2),
                                        Tools.roundingValue(subImporteNeto, 2),
                                        Tools.roundingValue(importeNeto, 2),
                                        Tools.roundingValue(tarjeta, 2),
                                        Tools.roundingValue(efectivo, 2),
                                        Tools.roundingValue(vuelto, 2),
                                        ventaTB.getClienteTB().getNumeroDocumento(),
                                        ventaTB.getClienteTB().getInformacion(),
                                        ventaTB.getCodigo(),
                                        ventaTB.getClienteTB().getCelular(),
                                        monedaCadena.Convertir(Tools.roundingValue(totalVenta, 2), true, ventaTB.getMonedaTB().getNombre()),
                                        "",
                                        "",
                                        "",
                                        "",
                                        "");
                                
                            }

                            billPrintable.generatePDFPrint(hbEncabezado, hbDetalle, hbPie);

                            DocPrintJob job = billPrintable.findPrintService(Session.NOMBRE_IMPRESORA_VENTA, PrinterJob.lookupPrintServices()).createPrintJob();

                            if (job != null) {
                                PrinterJob pj = PrinterJob.getPrinterJob();
                                pj.setPrintService(job.getPrintService());
                                pj.setJobName(Session.NOMBRE_IMPRESORA_VENTA);
                                Book book = new Book();
                                book.append(billPrintable, billPrintable.getPageFormat(pj));
                                pj.setPageable(book);
                                pj.print();
                                if (Session.CORTAPAPEL_IMPRESORA_VENTA) {
                                    billPrintable.printCortarPapel(Session.NOMBRE_IMPRESORA_VENTA);
                                }
                                return "completed";
                            } else {
                                return "error_name";
                            }
                        } else {
                            billPrintable.loadEstructuraTicket(Session.TICKET_VENTA_ID, Session.TICKET_VENTA_RUTA, hbEncabezado, hbDetalleCabecera, hbPie);
                            return imprimirVenta(ventaTB, arrList, Session.NOMBRE_IMPRESORA_VENTA, Session.CORTAPAPEL_IMPRESORA_VENTA);
                        }

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

    private String imprimirVenta(VentaTB ventaTB, ObservableList<SuministroTB> suministroTBs, String printerName, boolean printerCut) {
        ArrayList<HBox> object = new ArrayList<>();
        int rows = 0;
        int lines = 0;
        for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
            object.add((HBox) hbEncabezado.getChildren().get(i));
            HBox box = ((HBox) hbEncabezado.getChildren().get(i));
            rows++;
            lines += billPrintable.hbEncebezado(box,
                    ventaTB.getComprobanteName(),
                    ventaTB.getSerie() + "-" + ventaTB.getNumeracion(),
                    ventaTB.getClienteTB().getNumeroDocumento(),
                    ventaTB.getClienteTB().getInformacion(),
                    ventaTB.getClienteTB().getCelular(),
                    ventaTB.getClienteTB().getDireccion(),
                    ventaTB.getCodigo(),
                    monedaCadena.Convertir(Tools.roundingValue(totalVenta, 2), true, ventaTB.getMonedaTB().getNombre()),
                    ventaTB.getFechaVenta(),
                    ventaTB.getHoraVenta(),
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "0",
                    "0",
                    "0",
                    "0",
                    "0");
        }

        for (int m = 0; m < suministroTBs.size(); m++) {
            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                HBox hBox = new HBox();
                hBox.setId("dc_" + m + "" + i);
                HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                rows++;
                lines += billPrintable.hbDetalle(hBox, box, suministroTBs, m);
                object.add(hBox);
            }
        }

        for (int i = 0; i < hbPie.getChildren().size(); i++) {
            object.add((HBox) hbPie.getChildren().get(i));
            HBox box = ((HBox) hbPie.getChildren().get(i));
            rows++;
            lines += billPrintable.hbPie(box, ventaTB.getMonedaTB().getSimbolo(),
                    Tools.roundingValue(ventaTB.getSubImporte(), 2),
                    "-" + Tools.roundingValue(ventaTB.getDescuento(), 2),
                    Tools.roundingValue(ventaTB.getSubImporte(), 2),
                    Tools.roundingValue(ventaTB.getImpuesto(), 2),
                    Tools.roundingValue(ventaTB.getSubImporte(), 2),
                    Tools.roundingValue(ventaTB.getTotal(), 2),
                    Tools.roundingValue(ventaTB.getTarjeta(), 2),
                    Tools.roundingValue(ventaTB.getEfectivo(), 2),
                    Tools.roundingValue(ventaTB.getVuelto(), 2),
                    ventaTB.getClienteTB().getNumeroDocumento(),
                    ventaTB.getClienteTB().getInformacion(),
                    ventaTB.getCodigo(),
                    ventaTB.getClienteTB().getCelular(),
                    monedaCadena.Convertir(Tools.roundingValue(totalVenta, 2), true, ventaTB.getMonedaTB().getNombre()),
                    "",
                    "",
                    "",
                    "",
                    "");
        }
        return billPrintable.modelTicket(rows + lines + 1 + 5, lines, object, printerName, printerCut);
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
            if (ventaTB != null) {
                onEventImprimirVenta();
            } else {
                Tools.AlertMessageWarning(window, "Detalle Venta", "No se puede generar el reporte po problemas en al carga de información");
            }
        }
    }

    @FXML
    private void onActionImprimir(ActionEvent event) {
        if (ventaTB != null) {
            onEventImprimirVenta();
        } else {
            Tools.AlertMessageWarning(window, "Detalle Venta", "No se puede generar el reporte po problemas en al carga de información");
        }
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) throws IOException {
        fxPrincipalController.getVbContent().getChildren().remove(window);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(ventaRealizadasController.getWindow(), 0d);
        AnchorPane.setTopAnchor(ventaRealizadasController.getWindow(), 0d);
        AnchorPane.setRightAnchor(ventaRealizadasController.getWindow(), 0d);
        AnchorPane.setBottomAnchor(ventaRealizadasController.getWindow(), 0d);
        fxPrincipalController.getVbContent().getChildren().add(ventaRealizadasController.getWindow());
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

    public void setInitVentasController(FxVentaRealizadasController ventaRealizadasController, FxPrincipalController fxPrincipalController) {
        this.ventaRealizadasController = ventaRealizadasController;
        this.fxPrincipalController = fxPrincipalController;
    }

}
