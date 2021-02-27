package controller.operaciones.cortecaja;

import controller.configuracion.impresoras.FxOpcionesImprimirController;
import controller.reporte.FxReportViewController;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.awt.HeadlessException;
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
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.CajaADO;
import model.CajaTB;
import model.MovimientoCajaTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FxCajaConsultasController implements Initializable {

    @FXML
    private ScrollPane window;
    @FXML
    private Label lblLoad;
    @FXML
    private GridPane gpList;
    @FXML
    private Label lblInicoTurno;
    @FXML
    private Label lblFinTurno;
    @FXML
    private Label lblMontoBase;
    @FXML
    private Label lblBase;
    @FXML
    private Label lblVentaEfectivo;
    @FXML
    private Label lblVentaTarjeta;
    @FXML
    private Label lblIngresosEfectivo;
    @FXML
    private Label lblRetirosEfectivo;
    @FXML
    private Label lblContado;
    @FXML
    private Label lblCanculado;
    @FXML
    private Label lblDiferencia;
    @FXML
    private Label lblTotal;

    private CajaTB cajaTB;

    private ArrayList<MovimientoCajaTB> arrList;

    private AnchorPane vbPrincipal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void loadDataCorteCaja(String idCaja) {

        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<Object> task = new Task<Object>() {
            @Override
            public Object call() {
                return CajaADO.ListarMovimientoPorById(idCaja);
            }
        };

        task.setOnSucceeded(e -> {
            Object objects = task.getValue();
            if (objects instanceof Object[]) {
                Object[] objectData = (Object[]) objects;
                if (objectData[0] != null) {
                    cajaTB = (CajaTB) objectData[0];
                    ArrayList<Double> arrayList = (ArrayList<Double>) objectData[1];

                    lblInicoTurno.setText(cajaTB.getFechaApertura() + " " + cajaTB.getHoraApertura());
                    lblFinTurno.setText(cajaTB.getFechaCierre() + " " + cajaTB.getHoraCierre());
                    lblMontoBase.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(0), 2));
                    lblBase.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(0), 2));

                    lblContado.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(cajaTB.getContado(), 2));
                    lblCanculado.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(cajaTB.getCalculado(), 2));
                    lblDiferencia.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(cajaTB.getDiferencia(), 2));

                    lblVentaEfectivo.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(1), 2));
                    lblVentaTarjeta.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(2), 2));
                    lblIngresosEfectivo.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(3), 2));
                    lblRetirosEfectivo.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(4), 2));
                    lblTotal.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue((arrayList.get(0) + arrayList.get(1) + arrayList.get(3)) - arrayList.get(4), 2));

                    fillVentasDetalleTable((ArrayList<MovimientoCajaTB>) objectData[2]);
                } else {
                    Tools.AlertMessageError(window, "Corte de caja", "Hay valores nulos en el momento de cargar los datos.");
                }
            } else if (objects instanceof String) {
                Tools.AlertMessageError(window, "Corte de caja", (String) objects);
            } else {
                Tools.AlertMessageError(window, "Corte de caja", "Ha ocurrido un error grave, comuníquese con su proveedor del sistema.");
            }
            lblLoad.setVisible(false);
        });

        task.setOnFailed(e -> {
            Tools.AlertMessageError(window, "Corte de caja", "Se genero un problema en ejecutar la tarea.");
            lblLoad.setVisible(false);
        });

        task.setOnScheduled(e -> {
            lblLoad.setVisible(true);
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }

    }

    private void fillVentasDetalleTable(ArrayList<MovimientoCajaTB> arrList) {
        this.arrList = arrList;
        for (int i = 0; i < arrList.size(); i++) {
            gpList.add(addElementGridPane("l1" + (i + 1), "" + arrList.get(i).getId(), Pos.CENTER_LEFT, "#020203"), 0, (i + 1));
            gpList.add(addElementGridPane("l2" + (i + 1), arrList.get(i).getFechaMovimiento() + "\n" + arrList.get(i).getHoraMovimiento(), Pos.CENTER_LEFT, "#020203"), 1, (i + 1));
            gpList.add(addElementGridPane("l3" + (i + 1), arrList.get(i).getComentario(), Pos.CENTER_LEFT, "#020203"), 2, (i + 1));
            gpList.add(addElementGridPane("l4" + (i + 1),
                    arrList.get(i).getTipoMovimiento() == 1 ? "MONTO INICIAL"
                    : arrList.get(i).getTipoMovimiento() == 2 ? "VENTA EN EFECTIVO"
                    : arrList.get(i).getTipoMovimiento() == 3 ? "VENTA CON TARJETA"
                    : arrList.get(i).getTipoMovimiento() == 4 ? "INGRESO DE DINERO"
                    : "SALIDAS DE DINERO",
                    Pos.CENTER_LEFT, "#020203"), 3, (i + 1));
            gpList.add(addElementGridPane("l5" + (i + 1),
                    arrList.get(i).getTipoMovimiento() == 1
                    || arrList.get(i).getTipoMovimiento() == 2
                    || arrList.get(i).getTipoMovimiento() == 3
                    || arrList.get(i).getTipoMovimiento() == 4
                    ? Tools.roundingValue(arrList.get(i).getMonto(), 2)
                    : "",
                    Pos.CENTER_RIGHT, "#0d4e9e"), 4, (i + 1));
            gpList.add(addElementGridPane("l6" + (i + 1),
                    arrList.get(i).getTipoMovimiento() == 1
                    || arrList.get(i).getTipoMovimiento() == 2
                    || arrList.get(i).getTipoMovimiento() == 3
                    || arrList.get(i).getTipoMovimiento() == 4
                    ? ""
                    : Tools.roundingValue(arrList.get(i).getMonto(), 2),
                    Pos.CENTER_RIGHT, "#ff0000"), 5, (i + 1));
        }
    }

    private Label addElementGridPane(String id, String nombre, Pos pos, String textFill) {
        Label label = new Label(nombre);
        label.setId(id);
        label.setTextFill(Color.web(textFill));
        label.setStyle("-fx-background-color: #eaeaea;-fx-padding: 0.4166666666666667em 0.8333333333333334em 0.4166666666666667em 0.8333333333333334em;");
        label.getStyleClass().add("labelRoboto13");
        label.setAlignment(pos);
        label.setWrapText(true);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        return label;
    }

    private void openWindowCaja() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_CAJA_BUSQUEDA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxCajaBusquedaController controller = fXMLLoader.getController();
            controller.setInitCajaConsultasController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccionar corte de caja", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void onOpenReporte() {
        try {
            if (cajaTB != null) {
                InputStream imgInputStream = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

                if (Session.COMPANY_IMAGE != null) {
                    imgInputStream = new ByteArrayInputStream(Session.COMPANY_IMAGE);
                }
                InputStream dir = getClass().getResourceAsStream("/report/CortedeCaja.jasper");
//
                Map map = new HashMap();
                map.put("LOGO", imgInputStream);
                map.put("EMPRESA", Session.COMPANY_RAZON_SOCIAL);
                map.put("INICIODETURNO", cajaTB.getFechaApertura());
                map.put("HORAINICIO", cajaTB.getHoraApertura());
                map.put("FINDETURNO", cajaTB.getFechaCierre());
                map.put("HORAFIN", cajaTB.getHoraCierre());
                map.put("CONTADO", Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(cajaTB.getContado(), 2));
                map.put("CALCULADO", Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(cajaTB.getCalculado(), 2));
                map.put("DIFERENCIA", Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(cajaTB.getDiferencia(), 2));
                map.put("CAJEROASISTENTE", cajaTB.getEmpleadoTB().getNombres() + " " + cajaTB.getEmpleadoTB().getApellidos());
                map.put("BASE", "");
                map.put("VENTASENEFECTIVO", "");
                map.put("VENTASCONTARJETA", "");
                map.put("INGRESOSDEEFECTIVO", "");
                map.put("SALIDASDEEFECTIVO", "");
                map.put("TOTAL", "");

                JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JRBeanCollectionDataSource(arrList));
//                JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JREmptyDataSource());

                URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxReportViewController controller = fXMLLoader.getController();
                controller.setJasperPrint(jasperPrint);
                controller.show();
                Stage stage = WindowStage.StageLoader(parent, "Corte de Caja");
                stage.setResizable(true);
                stage.show();
                stage.requestFocus();
            } else {
                Tools.AlertMessageWarning(window, "Reporte de Corte de Caja", "No hay datos para mostrar en el reporte");
            }
        } catch (HeadlessException | JRException | IOException ex) {
            Tools.AlertMessageError(window, "Reporte de Corte de Caja", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    public void openModalImpresion(String idCaja) {
        FxOpcionesImprimirController imprimirController = new FxOpcionesImprimirController();
        imprimirController.loadComponents();
        imprimirController.setInitOpcionesImprimirCorteCaja(this);
        imprimirController.loadDataCorteCaja(idCaja);
        imprimirController.printEventCorteCajaTicket();
    }

    @FXML
    private void onKeyPressedSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowCaja();
        }
    }

    @FXML
    private void onActionSearch(ActionEvent event) {
        openWindowCaja();
    }

    @FXML
    private void OnKeyPressedReporte(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onOpenReporte();
        }
    }

    @FXML
    private void onActionReporte(ActionEvent event) {
        onOpenReporte();
    }

    @FXML
    private void onKeyPressedTicket(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openModalImpresion(cajaTB.getIdCaja());
        }
    }

    @FXML
    private void onActionTicket(ActionEvent event) {
        if (cajaTB != null) {
            openModalImpresion(cajaTB.getIdCaja());
        } else {
            Tools.AlertMessageWarning(window, "Reporte de Corte de Caja", "No hay datos para mostrar en el ticket");
        }
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
