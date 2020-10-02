package controller.operaciones.cotizacion;

import controller.reporte.FxReportViewController;
import controller.tools.ConvertMonedaCadena;
import controller.tools.FilesRouters;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.CotizacionADO;
import model.CotizacionTB;
import model.SuministroTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FxCotizacionDetalleController implements Initializable {

    @FXML
    private ScrollPane spWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private Button btnReporte;
    @FXML
    private Text lblNumero;
    @FXML
    private Text lblFechaVenta;
    @FXML
    private Text lblCliente;
    @FXML
    private Text lbClienteInformacion;
    @FXML
    private Text lbCorreoElectronico;
    @FXML
    private Text lbDireccion;
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
    private GridPane gpImpuestos;
    @FXML
    private Label lblTotal;

    private FxCotizacionRealizadasController cotizacionRealizadasController;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    private CotizacionTB cotizacionTB;

    private String idCotizacion;

    private ObservableList<SuministroTB> arrList = null;

    private ConvertMonedaCadena monedaCadena;

    private double subTotal = 0;

    private double descuentoTotal = 0;

    private double subTotalImporte = 0;

    private double totalImpuesto = 0;

    private double totalImporte = 0;

    private double total = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        monedaCadena = new ConvertMonedaCadena();
    }

    public void setInitComponents(String idCotizacion) {
        this.idCotizacion = idCotizacion;

        ExecutorService executor = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            protected ArrayList<Object> call() {
                return CotizacionADO.CargarCotizacionReporte(idCotizacion);
            }
        };
        task.setOnSucceeded(e -> {
            ArrayList<Object> objects = task.getValue();
            if (!objects.isEmpty()) {
                if (objects.get(0) != null && objects.get(1) != null) {
                    cotizacionTB = (CotizacionTB) objects.get(0);

                    lblNumero.setText("N° " + cotizacionTB.getIdCotizacion());
                    lblFechaVenta.setText(cotizacionTB.getFechaCotizacion());
                    lblCliente.setText(cotizacionTB.getClienteTB().getInformacion());
                    lbClienteInformacion.setText("TEL.: " + cotizacionTB.getClienteTB().getTelefono() + " CEL.: " + cotizacionTB.getClienteTB().getCelular());
                    lbCorreoElectronico.setText(cotizacionTB.getClienteTB().getEmail());
                    lbDireccion.setText(cotizacionTB.getClienteTB().getDireccion());
                    lblObservaciones.setText(cotizacionTB.getObservaciones());
                    lblVendedor.setText(cotizacionTB.getEmpleadoTB().getApellidos() + " " + cotizacionTB.getEmpleadoTB().getNombres());

                    ObservableList<SuministroTB> cotizacionTBs = (ObservableList<SuministroTB>) objects.get(1);
                    fillVentasDetalleTable(cotizacionTBs, cotizacionTB.getMonedaTB().getSimbolo());
                }
                lblLoad.setVisible(false);
            } else {
                btnReporte.setDisable(true);
                lblLoad.setVisible(false);
            }

        });
        task.setOnScheduled(e -> {
            lblLoad.setVisible(true);
        });
        task.setOnFailed(e -> {
            lblLoad.setVisible(false);
        });
        executor.execute(task);
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
    }

    private void fillVentasDetalleTable(ObservableList<SuministroTB> empList, String simbolo) {
        arrList = empList;
        for (int i = 0; i < arrList.size(); i++) {
            gpList.add(addElementGridPane("l1" + (i + 1), arrList.get(i).getId() + "", Pos.CENTER), 0, (i + 1));
            gpList.add(addElementGridPane("l2" + (i + 1), arrList.get(i).getClave() + "\n" + arrList.get(i).getNombreMarca(), Pos.CENTER_LEFT), 1, (i + 1));
            gpList.add(addElementGridPane("l3" + (i + 1), Tools.roundingValue(arrList.get(i).getCantidad(), 2), Pos.CENTER_RIGHT), 2, (i + 1));
            gpList.add(addElementGridPane("l4" + (i + 1), arrList.get(i).getUnidadCompraName(), Pos.CENTER_LEFT), 3, (i + 1));
            gpList.add(addElementGridPane("l5" + (i + 1), arrList.get(i).getImpuestoNombre(), Pos.CENTER_RIGHT), 4, (i + 1));
            gpList.add(addElementGridPane("l6" + (i + 1), simbolo + "" + Tools.roundingValue(arrList.get(i).getPrecioVentaGeneral(), 2), Pos.CENTER_RIGHT), 5, (i + 1));
            gpList.add(addElementGridPane("l7" + (i + 1), Tools.roundingValue(arrList.get(i).getDescuento(), 2) + "%", Pos.CENTER_RIGHT), 6, (i + 1));
            gpList.add(addElementGridPane("l8" + (i + 1), simbolo + "" + Tools.roundingValue(arrList.get(i).getPrecioVentaGeneral() * arrList.get(i).getCantidad(), 2), Pos.CENTER_RIGHT), 7, (i + 1));
        }
        calculateTotales(simbolo);
    }

    private Label addElementGridPane(String id, String nombre, Pos pos) {
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

    public void calculateTotales(String simbolo) {

        subTotal = 0;
        arrList.forEach(e -> subTotal += e.getSubImporte());
        lblValorVenta.setText(simbolo + " " + Tools.roundingValue(subTotal, 2));

        descuentoTotal = 0;
        arrList.forEach(e -> descuentoTotal += e.getDescuentoSumado());
        lblDescuento.setText(simbolo + " " + (Tools.roundingValue(descuentoTotal * (-1), 2)));

        subTotalImporte = 0;
        arrList.forEach(e -> subTotalImporte += e.getSubImporteDescuento());
        lblSubTotal.setText(simbolo + " " + Tools.roundingValue(subTotalImporte, 2));

        gpImpuestos.getChildren().clear();
        totalImpuesto = 0;
        for (int i = 0; i < arrList.size(); i++) {
            totalImpuesto += arrList.get(i).getImpuestoSumado();
        }

        gpImpuestos.add(addLabelTitle("IMPUESTO GENERADO:", Pos.CENTER_LEFT), 0, 0 + 1);
        gpImpuestos.add(addLabelTotal(simbolo + " " + Tools.roundingValue(totalImpuesto, 2), Pos.CENTER_RIGHT), 1, 0 + 1);

        totalImporte = 0;
        total = 0;
        arrList.forEach(e -> totalImporte += e.getTotalImporte());
        total = totalImporte + totalImpuesto;
        lblTotal.setText(simbolo + " " + Tools.roundingValue(total, 2));
    }

    private void reportA4(CotizacionTB cotizacionTB) {
        try {
            ArrayList<SuministroTB> list = new ArrayList();
            for (int i = 0; i < arrList.size(); i++) {
                SuministroTB stb = new SuministroTB();
                stb.setId(i + 1);
                stb.setClave(arrList.get(i).getClave());
                stb.setNombreMarca(arrList.get(i).getNombreMarca());
                stb.setCantidad(arrList.get(i).getCantidad());
                stb.setUnidadCompraName(arrList.get(i).getUnidadCompraName());
                stb.setSubImporte(arrList.get(i).getSubImporte());
                stb.setDescuentoSumado(arrList.get(i).getDescuentoSumado());
                stb.setSubImporteDescuento(arrList.get(i).getSubImporteDescuento());
                stb.setImpuestoSumado(arrList.get(i).getImpuestoSumado());
                stb.setPrecioVentaGeneral(arrList.get(i).getPrecioVentaGeneral());
                stb.setDescuento(arrList.get(i).getDescuento());
                stb.setTotalImporte(arrList.get(i).getCantidad() * +arrList.get(i).getPrecioVentaGeneral());
                list.add(stb);
            }

            if (list.isEmpty()) {
                Tools.AlertMessageWarning(spWindow, "Venta realizada", "No hay registros para mostrar en el reporte.");
                return;
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
            map.put("VALORSOLES", monedaCadena.Convertir(Tools.roundingValue(total, 2), true, cotizacionTB.getMonedaTB().getNombre()));

            map.put("VALOR_VENTA", Tools.roundingValue(subTotal, 2));
            map.put("DESCUENTO", "-"+Tools.roundingValue(descuentoTotal, 2));
            map.put("SUB_IMPORTE", Tools.roundingValue(subTotalImporte, 2));
            map.put("IMPUESTO_TOTAL", Tools.roundingValue(totalImpuesto, 2));
            map.put("IMPORTE_TOTAL", Tools.roundingValue(total, 2));
            map.put("OBSERVACION", cotizacionTB.getObservaciones());

            JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JRBeanCollectionDataSource(list));

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
            Tools.AlertMessageError(spWindow, "Reporte de Cotizacion", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        vbContent.getChildren().remove(spWindow);
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(cotizacionRealizadasController.getHbWindow(), 0d);
        AnchorPane.setTopAnchor(cotizacionRealizadasController.getHbWindow(), 0d);
        AnchorPane.setRightAnchor(cotizacionRealizadasController.getHbWindow(), 0d);
        AnchorPane.setBottomAnchor(cotizacionRealizadasController.getHbWindow(), 0d);
        vbContent.getChildren().add(cotizacionRealizadasController.getHbWindow());
    }

    @FXML
    private void onKeyPressedReporte(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            reportA4(cotizacionTB);
        }
    }

    @FXML
    private void onActionReporte(ActionEvent event) {
        reportA4(cotizacionTB);
    }

    public void setInitCotizacionesRealizadasController(FxCotizacionRealizadasController cotizacionRealizadasController, AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.cotizacionRealizadasController = cotizacionRealizadasController;
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
