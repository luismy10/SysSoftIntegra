package controller.operaciones.pedidos;

import controller.menus.FxPrincipalController;
import controller.reporte.FxReportViewController;
import controller.tools.BillPrintable;
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
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.PedidoADO;
import model.PedidoTB;
import model.SuministroTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FxPedidosDetalleController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private ScrollPane spBody;
    @FXML
    private Text lblNumero;
    @FXML
    private Text lblFechaVenta;
    @FXML
    private Text lblProveedor;
    @FXML
    private Text lblCelularTelefono;
    @FXML
    private Text lbCorreoElectronico;
    @FXML
    private Text lblDireccion;
    @FXML
    private Text lblObservaciones;
    @FXML
    private Text lblVendedor;
    @FXML
    private Label lblLoad;
    @FXML
    private HBox hbLoad;
    @FXML
    private Label lblMessageLoad;
    @FXML
    private Button btnAceptarLoad;
    @FXML
    private GridPane gpList;
    @FXML
    private Label lblImporteBruto;
    @FXML
    private Label lblDescuento;
    @FXML
    private Label lblSubImporte;
    @FXML
    private GridPane gpImpuestos;
    @FXML
    private Label lblImporteTotal;

    private FxPrincipalController fxPrincipalController;

    private FxPedidosRealizadosController pedidosRealizadosController;

    private ArrayList<SuministroTB> arrList = null;

    private PedidoTB pedidoTB;

    private ConvertMonedaCadena monedaCadena;

    private BillPrintable billPrintable;

    private AnchorPane hbEncabezado;

    private AnchorPane hbDetalleCabecera;

    private AnchorPane hbPie;

    private double importeBruto = 0;

    private double descuentoBruto = 0;

    private double subImporteNeto = 0;

    private double impuestoNeto = 0;

    private double importeTotal = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        monedaCadena = new ConvertMonedaCadena();
        billPrintable = new BillPrintable();
        hbEncabezado = new AnchorPane();
        hbDetalleCabecera = new AnchorPane();
        hbPie = new AnchorPane();
    }

    public void loadComponents(String idPedido) {
        ExecutorService executor = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<Object> task = new Task<Object>() {
            @Override
            protected Object call() {
                return PedidoADO.CargarPedidoById(idPedido);
            }
        };
        task.setOnScheduled(e -> {
            lblLoad.setVisible(true);
            spBody.setDisable(true);
            hbLoad.setVisible(true);
            lblMessageLoad.setText("Cargando datos...");
            lblMessageLoad.setTextFill(Color.web("#ffffff"));
            btnAceptarLoad.setVisible(false);
        });
        task.setOnFailed(e -> {
            lblLoad.setVisible(false);
            lblMessageLoad.setText(task.getException().getLocalizedMessage());
            lblMessageLoad.setTextFill(Color.web("#ff6d6d"));
            btnAceptarLoad.setVisible(true);
        });
        task.setOnSucceeded(e -> {
            Object object = task.getValue();
            if (object instanceof PedidoTB) {
                pedidoTB = (PedidoTB) object;
                lblNumero.setText("N° " + pedidoTB.getIdPedido());
                lblFechaVenta.setText(pedidoTB.getFechaEmision());
                lblProveedor.setText(pedidoTB.getProveedorTB().getNumeroDocumento() + " - " + pedidoTB.getProveedorTB().getRazonSocial());
                lblCelularTelefono.setText(pedidoTB.getProveedorTB().getCelular() + " - " + pedidoTB.getProveedorTB().getTelefono());
                lbCorreoElectronico.setText(pedidoTB.getProveedorTB().getEmail());
                lblDireccion.setText(pedidoTB.getProveedorTB().getDireccion());
                lblObservaciones.setText(pedidoTB.getObservacion());
                lblVendedor.setText(pedidoTB.getEmpleadoTB().getApellidos() + " " + pedidoTB.getEmpleadoTB().getNombres());

                fillVentasDetalleTable(pedidoTB.getSuministroTBs(), pedidoTB.getMonedaTB().getSimbolo());

                spBody.setDisable(false);
                hbLoad.setVisible(false);
            } else {
                lblMessageLoad.setText((String) object);
                lblMessageLoad.setTextFill(Color.web("#ff6d6d"));
                btnAceptarLoad.setVisible(true);
            }
            lblLoad.setVisible(false);
        });
        executor.execute(task);
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
    }

    private void fillVentasDetalleTable(ArrayList<SuministroTB> empList, String simbolo) {
        arrList = empList;
        for (int i = 0; i < arrList.size(); i++) {
            gpList.add(addElementGridPane("l1" + (i + 1), arrList.get(i).getId() + "", Pos.CENTER), 0, (i + 1));
            gpList.add(addElementGridPane("l2" + (i + 1), arrList.get(i).getClave() + "\n" + arrList.get(i).getNombreMarca(), Pos.CENTER_LEFT), 1, (i + 1));
            gpList.add(addElementGridPane("l3" + (i + 1), Tools.roundingValue(arrList.get(i).getCantidad(), 2), Pos.CENTER_RIGHT), 2, (i + 1));
            gpList.add(addElementGridPane("l4" + (i + 1), arrList.get(i).getUnidadCompraName(), Pos.CENTER_LEFT), 3, (i + 1));
            gpList.add(addElementGridPane("l5" + (i + 1), Tools.roundingValue(arrList.get(i).getImpuestoValor(), 2) + "%", Pos.CENTER_RIGHT), 4, (i + 1));
            gpList.add(addElementGridPane("l6" + (i + 1), simbolo + "" + Tools.roundingValue(arrList.get(i).getCostoCompra(), 2), Pos.CENTER_RIGHT), 5, (i + 1));
            gpList.add(addElementGridPane("l7" + (i + 1), "-" + Tools.roundingValue(arrList.get(i).getDescuento(), 2), Pos.CENTER_RIGHT), 6, (i + 1));
            gpList.add(addElementGridPane("l8" + (i + 1), simbolo + "" + Tools.roundingValue(arrList.get(i).getImporteNeto(), 2), Pos.CENTER_RIGHT), 7, (i + 1));
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
        label.setStyle("-fx-text-fill: #020203;-fx-padding:  0.4166666666666667em 0em  0.4166666666666667em 0em;");
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
        label.getStyleClass().add("labelRoboto15");
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

        importeBruto = 0;
        arrList.forEach(e -> {
            double descuento = e.getDescuento();
            double costoDescuento = e.getCostoCompra() - descuento;
            double subCosto = Tools.calculateTaxBruto(e.getImpuestoValor(), costoDescuento);
            double costoBruto = subCosto + descuento;
            importeBruto += costoBruto * e.getCantidad();
        });
        lblImporteBruto.setText(simbolo + " " + Tools.roundingValue(importeBruto, 2));

        descuentoBruto = 0;
        arrList.forEach(e -> {
            double descuento = e.getDescuento();
            descuentoBruto += e.getCantidad() * descuento;
        });
        lblDescuento.setText(simbolo + " " + (Tools.roundingValue(descuentoBruto * (-1), 2)));

        subImporteNeto = 0;
        arrList.forEach(e -> {
            double descuento = e.getDescuento();
            double costoDescuento = e.getCostoCompra() - descuento;
            double subCosto = Tools.calculateTaxBruto(e.getImpuestoValor(), costoDescuento);
            subImporteNeto += e.getCantidad() * subCosto;
        });
        lblSubImporte.setText(simbolo + " " + Tools.roundingValue(subImporteNeto, 2));

        gpImpuestos.getChildren().clear();
        impuestoNeto = 0;
        arrList.forEach(e -> {
            double descuento = e.getDescuento();
            double costoDescuento = e.getCostoCompra() - descuento;
            double subCosto = Tools.calculateTaxBruto(e.getImpuestoValor(), costoDescuento);
            double impuesto = Tools.calculateTax(e.getImpuestoValor(), subCosto);
            impuestoNeto += e.getCantidad() * impuesto;
        });

        gpImpuestos.add(addLabelTitle("IMPUESTO GENERADO:", Pos.CENTER_LEFT), 0, 0 + 1);
        gpImpuestos.add(addLabelTotal(simbolo + " " + Tools.roundingValue(impuestoNeto, 2), Pos.CENTER_RIGHT), 1, 0 + 1);

        importeTotal = 0;
        importeTotal = subImporteNeto + impuestoNeto;
        lblImporteTotal.setText(simbolo + " " + Tools.roundingValue(importeTotal, 2));
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        fxPrincipalController.getVbContent().getChildren().remove(apWindow);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(pedidosRealizadosController.getVbWindow(), 0d);
        AnchorPane.setTopAnchor(pedidosRealizadosController.getVbWindow(), 0d);
        AnchorPane.setRightAnchor(pedidosRealizadosController.getVbWindow(), 0d);
        AnchorPane.setBottomAnchor(pedidosRealizadosController.getVbWindow(), 0d);
        fxPrincipalController.getVbContent().getChildren().add(pedidosRealizadosController.getVbWindow());
    }

    private void reportA4() {
        try {
            InputStream imgInputStreamIcon = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            InputStream imgInputStream = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            if (Session.COMPANY_IMAGE != null) {
                imgInputStream = new ByteArrayInputStream(Session.COMPANY_IMAGE);
            }

            InputStream dir = getClass().getResourceAsStream("/report/Pedido.jasper");

            Map map = new HashMap();
            map.put("LOGO", imgInputStream);
            map.put("ICON", imgInputStreamIcon);
            map.put("EMPRESA", Session.COMPANY_RAZON_SOCIAL);
            map.put("DIRECCION", Session.COMPANY_DOMICILIO);
            map.put("TELEFONOCELULAR", "TELÉFONO: " + Session.COMPANY_TELEFONO + " CELULAR: " + Session.COMPANY_CELULAR);
            map.put("EMAIL", "EMAIL: " + Session.COMPANY_EMAIL);

            map.put("DOCUMENTOEMPRESA", "R.U.C " + Session.COMPANY_NUMERO_DOCUMENTO);
            map.put("NOMBREDOCUMENTO", "PEDIDO");
            map.put("NUMERODOCUMENTO", "N° " + pedidoTB.getIdPedido());

            map.put("DOCUMENTOCLIENTE", "");
            map.put("DATOSCLIENTE", pedidoTB.getProveedorTB().getRazonSocial());
            map.put("NUMERODOCUMENTOCLIENTE", pedidoTB.getProveedorTB().getNumeroDocumento());
            map.put("CELULARCLIENTE", pedidoTB.getProveedorTB().getCelular() + " " + pedidoTB.getProveedorTB().getTelefono());
            map.put("EMAILCLIENTE", pedidoTB.getProveedorTB().getEmail());
            map.put("DIRECCIONCLIENTE", pedidoTB.getProveedorTB().getDireccion());

            map.put("FECHAEMISION", pedidoTB.getFechaEmision());
            map.put("MONEDA", pedidoTB.getMonedaTB().getNombre());
            map.put("CONDICIONPAGO", "");

            map.put("SIMBOLO", pedidoTB.getMonedaTB().getSimbolo());
            map.put("VALORSOLES", monedaCadena.Convertir(Tools.roundingValue(importeTotal, 2), true, pedidoTB.getMonedaTB().getNombre()));

            map.put("VALOR_VENTA", Tools.roundingValue(importeBruto, 2));
            map.put("DESCUENTO", "-" + Tools.roundingValue(descuentoBruto, 2));
            map.put("SUB_IMPORTE", Tools.roundingValue(subImporteNeto, 2));
            map.put("IMPUESTO_TOTAL", Tools.roundingValue(impuestoNeto, 2));
            map.put("IMPORTE_TOTAL", Tools.roundingValue(importeTotal, 2));
            map.put("OBSERVACION", pedidoTB.getObservacion());

            JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JRBeanCollectionDataSource(arrList));

            URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxReportViewController controller = fXMLLoader.getController();
            controller.setFileName("PEDIDO N° " + pedidoTB.getIdPedido());
            controller.setJasperPrint(jasperPrint);
            controller.show();
            Stage stage = WindowStage.StageLoader(parent, "Pedido realizados");
            stage.setResizable(true);
            stage.show();
            stage.requestFocus();

        } catch (IOException | JRException ex) {
            Tools.AlertMessageError(apWindow, "Reporte de Pedido", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onKeyPressedReporte(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            reportA4();
        }
    }

    @FXML
    private void onActionReporte(ActionEvent event) {
        reportA4();
    }

    @FXML
    private void onKeyPressedTicket(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionTicket(ActionEvent event) {

    }

    @FXML
    private void onKeyPressedAceptarLoad(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            fxPrincipalController.getVbContent().getChildren().remove(apWindow);
            fxPrincipalController.getVbContent().getChildren().clear();
            AnchorPane.setLeftAnchor(pedidosRealizadosController.getVbWindow(), 0d);
            AnchorPane.setTopAnchor(pedidosRealizadosController.getVbWindow(), 0d);
            AnchorPane.setRightAnchor(pedidosRealizadosController.getVbWindow(), 0d);
            AnchorPane.setBottomAnchor(pedidosRealizadosController.getVbWindow(), 0d);
            fxPrincipalController.getVbContent().getChildren().add(pedidosRealizadosController.getVbWindow());
        }
    }

    @FXML
    private void onActionAceptarLoad(ActionEvent event) {
        fxPrincipalController.getVbContent().getChildren().remove(apWindow);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(pedidosRealizadosController.getVbWindow(), 0d);
        AnchorPane.setTopAnchor(pedidosRealizadosController.getVbWindow(), 0d);
        AnchorPane.setRightAnchor(pedidosRealizadosController.getVbWindow(), 0d);
        AnchorPane.setBottomAnchor(pedidosRealizadosController.getVbWindow(), 0d);
        fxPrincipalController.getVbContent().getChildren().add(pedidosRealizadosController.getVbWindow());
    }

    public void setInitPedidosRealizadasController(FxPedidosRealizadosController pedidosRealizadosController, FxPrincipalController fxPrincipalController) {
        this.pedidosRealizadosController = pedidosRealizadosController;
        this.fxPrincipalController = fxPrincipalController;
    }

}
