package controller.operaciones.guiaremision;

import controller.menus.FxPrincipalController;
import controller.reporte.FxReportViewController;
import controller.tools.FilesRouters;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
import javafx.util.Duration;
import model.GuiaRemisionADO;
import model.GuiaRemisionDetalleTB;
import model.GuiaRemisionTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FxGuiaRemisionDetalleController implements Initializable {

    @FXML
    private ScrollPane spWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private Button btnReporte;
    @FXML
    private Text lblNumero;
    @FXML
    private Text lblFechaTraslado;
    @FXML
    private Text lblHoraTraslado;
    @FXML
    private Text lblCliente;
    @FXML
    private Text lblClienteTelefonoCelular;
    @FXML
    private Text lblCorreoElectronico;
    @FXML
    private Text lblDireccion;
    @FXML
    private Text lblMotivoTraslado;
    @FXML
    private Text lblModalidadTraslado;
    @FXML
    private Text lblPesoBultos;
    @FXML
    private Text lblConducto;
    @FXML
    private Text lblTelefono;
    @FXML
    private Text lblNumeroPlaca;
    @FXML
    private Text lblMarcaVehiculo;
    @FXML
    private Text lblDireccionPartida;
    @FXML
    private Text lblDireccionLlegada;
    @FXML
    private Text lblTipoComprobante;
    @FXML
    private Text lblNumeroBultos;
    @FXML
    private Text lblSerieNumeracion;
    @FXML
    private GridPane gpList;

    private FxGuiaRemisionRealizadasController guiaRemisionRealizadasController;

    private FxPrincipalController fxPrincipalController;

    private String idGuiaRemision;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setInitComponents(String idGuiaRemision) {
        this.idGuiaRemision = idGuiaRemision;

        ExecutorService executor = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<GuiaRemisionTB> task = new Task<GuiaRemisionTB>() {
            @Override
            protected GuiaRemisionTB call() {
                return GuiaRemisionADO.CargarGuiaRemisionReporte(idGuiaRemision);
            }
        };
        task.setOnSucceeded(e -> {
            GuiaRemisionTB guiaRemisionTB = task.getValue();
            if (guiaRemisionTB != null) {
                lblNumero.setText(guiaRemisionTB.getSerie() + "-" + guiaRemisionTB.getNumeracion());
                lblFechaTraslado.setText(guiaRemisionTB.getFechaTraslado());
                lblHoraTraslado.setText(guiaRemisionTB.getHoraTraslado());
                lblCliente.setText(guiaRemisionTB.getClienteTB().getNumeroDocumento() + guiaRemisionTB.getClienteTB().getInformacion());
                lblClienteTelefonoCelular.setText(guiaRemisionTB.getClienteTB().getCelular());
                lblCorreoElectronico.setText(guiaRemisionTB.getClienteTB().getEmail());
                lblDireccion.setText(guiaRemisionTB.getClienteTB().getDireccion());
                lblMotivoTraslado.setText(guiaRemisionTB.getMotivoTrasladoDescripcion());
                lblModalidadTraslado.setText(guiaRemisionTB.getModalidadTrasladDescripcion());
                lblPesoBultos.setText(Tools.roundingValue(guiaRemisionTB.getPesoBruto(), 2));
                lblNumeroBultos.setText(Tools.roundingValue(guiaRemisionTB.getNumeroBultos(), 0));
                lblConducto.setText(guiaRemisionTB.getNumeroConductor() + " - " + guiaRemisionTB.getNombreConductor());
                lblTelefono.setText(guiaRemisionTB.getTelefonoCelularConducto());
                lblNumeroPlaca.setText(guiaRemisionTB.getNumeroPlaca());
                lblMarcaVehiculo.setText(guiaRemisionTB.getMarcaVehiculo());
                lblDireccionPartida.setText(guiaRemisionTB.getDireccionPartida());
                lblDireccionLlegada.setText(guiaRemisionTB.getDireccionLlegada());
                lblTipoComprobante.setText(guiaRemisionTB.getComprobanteAsociado());
                lblSerieNumeracion.setText(guiaRemisionTB.getSerieFactura() + "-" + guiaRemisionTB.getNumeracionFactura());
                fillVentasDetalleTable(guiaRemisionTB.getListGuiaRemisionDetalle());
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

    private void fillVentasDetalleTable(ObservableList<GuiaRemisionDetalleTB> empList) {
        for (int i = 0; i < empList.size(); i++) {
            gpList.add(addElementGridPane("l1" + (i + 1), "" + (i + 1), Pos.CENTER), 0, (i + 1));
            gpList.add(addElementGridPane("l2" + (i + 1), empList.get(i).getDescripcion(), Pos.CENTER_LEFT), 1, (i + 1));
            gpList.add(addElementGridPane("l3" + (i + 1), empList.get(i).getCodigo(), Pos.CENTER_LEFT), 2, (i + 1));
            gpList.add(addElementGridPane("l4" + (i + 1), empList.get(i).getUnidad(), Pos.CENTER_RIGHT), 3, (i + 1));
            gpList.add(addElementGridPane("l5" + (i + 1), Tools.roundingValue(empList.get(i).getCantidad(), 2), Pos.CENTER_RIGHT), 4, (i + 1));
            gpList.add(addElementGridPane("l6" + (i + 1), Tools.roundingValue(empList.get(i).getPeso(), 2), Pos.CENTER_RIGHT), 5, (i + 1));
        }
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

    private void reportGuiaRemision(GuiaRemisionTB guiaRemisionTB) {
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

            map.put("NUMERACION_GUIA_REMISION", guiaRemisionTB.getSerie() + "-" + guiaRemisionTB.getNumeracion());
            map.put("FECHA_TRASLADO", guiaRemisionTB.getFechaTraslado());
            map.put("NUMERO_FACTURA", guiaRemisionTB.getSerieFactura() + "-" + guiaRemisionTB.getNumeracionFactura());

            map.put("DIRECCION_PARTIDA", guiaRemisionTB.getDireccionPartida());
            map.put("UBIGEO_PARTIDA", guiaRemisionTB.getUbigeoPartidaDescripcion());

            map.put("DIRECCION_LLEGAGA", guiaRemisionTB.getDireccionLlegada());
            map.put("UBIGEO_LLEGADA", guiaRemisionTB.getUbigeoLlegadaDescripcion());

            map.put("NOMBRE_COMERCIAL", Session.COMPANY_NOMBRE_COMERCIAL);
            map.put("RUC_EMPRESA", Session.COMPANY_NUMERO_DOCUMENTO);

            map.put("NOMBRE_CLIENTE", guiaRemisionTB.getClienteTB().getInformacion());
            map.put("RUC_CLIENTE", guiaRemisionTB.getClienteTB().getNumeroDocumento());

            map.put("DATOS_TRANSPORTISTA", guiaRemisionTB.getNombreConductor());
            map.put("DOCUMENTO_TRANSPORTISTA", guiaRemisionTB.getNumeroConductor());
            map.put("MARCA_VEHICULO", guiaRemisionTB.getMarcaVehiculo());
            map.put("NUMERO_PLACA", guiaRemisionTB.getNumeroPlaca());

            map.put("MOTIVO_TRANSPORTE", guiaRemisionTB.getMotivoTrasladoDescripcion());

            JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JRBeanCollectionDataSource(guiaRemisionTB.getListGuiaRemisionDetalle()));
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
            Tools.AlertMessageError(spWindow, "Reporte de Ventas", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    private void onExecuteImpresion(String idGuiaRemision) {
        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<GuiaRemisionTB> task = new Task<GuiaRemisionTB>() {
            @Override
            public GuiaRemisionTB call() {
                return GuiaRemisionADO.CargarGuiaRemisionReporte(idGuiaRemision);
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
            GuiaRemisionTB remisionTB = task.getValue();
            if (remisionTB != null) {
                reportGuiaRemision(remisionTB);
                Tools.showAlertNotification("/view/image/succes_large.png",
                        "Generando reporte",
                        "Se genero correctamente el reporte.",
                        Duration.seconds(5),
                        Pos.BOTTOM_RIGHT);

            } else {
                Tools.println("dentroo..");
                Tools.showAlertNotification("/view/image/error_large.png",
                        "Generando reporte",
                        "Se produjo al obtenener los datos.",
                        Duration.seconds(10),
                        Pos.CENTER);
            }
        }
        );
        exec.execute(task);

        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        fxPrincipalController.getVbContent().getChildren().remove(spWindow);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(guiaRemisionRealizadasController.getHbWindow(), 0d);
        AnchorPane.setTopAnchor(guiaRemisionRealizadasController.getHbWindow(), 0d);
        AnchorPane.setRightAnchor(guiaRemisionRealizadasController.getHbWindow(), 0d);
        AnchorPane.setBottomAnchor(guiaRemisionRealizadasController.getHbWindow(), 0d);
        fxPrincipalController.getVbContent().getChildren().add(guiaRemisionRealizadasController.getHbWindow());
    }

    @FXML
    private void onKeyPressedReporte(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onExecuteImpresion(idGuiaRemision);
        }
    }

    @FXML
    private void onActionReporte(ActionEvent event) {
        onExecuteImpresion(idGuiaRemision);
    }

    public void setInitGuiaRemisionRealizadasController(FxGuiaRemisionRealizadasController guiaRemisionRealizadasController, FxPrincipalController fxPrincipalController) {
        this.guiaRemisionRealizadasController = guiaRemisionRealizadasController;
        this.fxPrincipalController = fxPrincipalController;
    }

}
