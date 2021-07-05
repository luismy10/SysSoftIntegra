package controller.posterminal.venta;

import controller.configuracion.impresoras.FxOpcionesImprimirController;
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
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.HistorialSuministroSalidaADO;
import model.HistorialSuministroSalidaTB;
import model.SuministroTB;
import model.VentaTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FxPostVentaLlevarControllerHistorial implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private TextField txtProducto;
    @FXML
    private TableView<HistorialSuministroSalidaTB> tvList;
    @FXML
    private TableColumn<HistorialSuministroSalidaTB, String> tvNumero;
    @FXML
    private TableColumn<HistorialSuministroSalidaTB, String> tvFecha;
    @FXML
    private TableColumn<HistorialSuministroSalidaTB, String> tvCantidad;
    @FXML
    private TableColumn<HistorialSuministroSalidaTB, String> tvObservacion;

    private FxPostVentaDetalleController ventaDetalleController;

    private VentaTB ventaTB;
    
    private SuministroTB suministroTB;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
        tvNumero.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tvFecha.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getFecha() + "\n" + cellData.getValue().getHora()));
        tvCantidad.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getCantidad()));
        tvObservacion.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getObservacion()));
        tvList.setPlaceholder(Tools.placeHolderTableView("No hay datos para mostrar.", "-fx-text-fill:#020203;", false));
    }

    public void loadData(VentaTB ventaTB, SuministroTB suministroTB) {
        this.ventaTB = ventaTB;
        this.suministroTB=suministroTB;
        txtProducto.setText(suministroTB.getNombreMarca());
        fillTableHistorialMovimiento();
    }

    private void fillTableHistorialMovimiento() {
        ExecutorService executor = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<Object> task = new Task<Object>() {
            @Override
            protected Object call() {
                return HistorialSuministroSalidaADO.ListarHistorialSuministroSalida(ventaTB.getIdVenta(), suministroTB.getIdSuministro());
            }
        };

        task.setOnScheduled(w -> {
            tvList.getItems().clear();
            tvList.setPlaceholder(Tools.placeHolderTableView("Cargando información...", "-fx-text-fill:#020203;", true));
        });

        task.setOnFailed(w -> {
            tvList.setPlaceholder(Tools.placeHolderTableView(task.getMessage(), "-fx-text-fill:#a70820;", false));
        });

        task.setOnSucceeded(w -> {
            Object object = task.getValue();
            if (object instanceof ObservableList) {
                ObservableList<HistorialSuministroSalidaTB> ventaTBs = (ObservableList<HistorialSuministroSalidaTB>) object;
                if (!ventaTBs.isEmpty()) {
                    tvList.setItems(ventaTBs);
                } else {
                    tvList.setPlaceholder(Tools.placeHolderTableView("No hay datos para mostrar.", "-fx-text-fill:#020203;", false));
                }
            } else if (object instanceof String) {
                tvList.setPlaceholder(Tools.placeHolderTableView((String) object, "-fx-text-fill:#a70820;", false));
            } else {
                tvList.setPlaceholder(Tools.placeHolderTableView("Error en traer los datos, intente nuevamente.", "-fx-text-fill:#a70820;", false));
            }
        });

        executor.execute(task);
        if (executor.isShutdown()) {
            executor.shutdown();
        }

    }

    private void openWindowReporte() {
        try {
            //InputStream imgInputStreamIcon = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            InputStream imgInputStream = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            if (Session.COMPANY_IMAGE != null) {
                imgInputStream = new ByteArrayInputStream(Session.COMPANY_IMAGE);
            }

            InputStream dir = getClass().getResourceAsStream("/report/ReporteHistorialMoivimiento.jasper");

            Map map = new HashMap();
            map.put("LOGO", imgInputStream);
            //map.put("ICON", imgInputStreamIcon);
            map.put("EMPRESA", Session.COMPANY_RAZON_SOCIAL);
            map.put("DIRECCION", Session.COMPANY_DOMICILIO);
            map.put("TELEFONOCELULAR", "TELÉFONO: " + Session.COMPANY_TELEFONO + " CELULAR: " + Session.COMPANY_CELULAR);
            map.put("EMAIL", "EMAIL: " + Session.COMPANY_EMAIL);
            map.put("DOCUMENTOEMPRESA", "R.U.C " + Session.COMPANY_NUMERO_DOCUMENTO);

            map.put("CLIENTE_INFORMACION", ventaTB.getClienteTB().getInformacion());
            map.put("CLIENTE_CELULAR", ventaTB.getClienteTB().getCelular());
            map.put("CLIENTE_EMAIL", ventaTB.getClienteTB().getEmail());
            map.put("PRODUCTO", suministroTB.getNombreMarca());

            map.put("COMPROBANTE", ventaTB.getSerie()+"-"+ventaTB.getNumeracion());
            map.put("FECHA", ventaTB.getFechaVenta()+" "+ventaTB.getHoraVenta());
            map.put("TIPO_ESTADO", ventaTB.getTipoName()+" "+ventaTB.getEstadoName());
            map.put("CANTIDAD", Tools.roundingValue(suministroTB.getCantidad(), 2));

            JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JRBeanCollectionDataSource(tvList.getItems()));

            URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxReportViewController controller = fXMLLoader.getController();
            //controller.setFileName(ventaTB.getComprobanteName().toUpperCase() + " " + ventaTB.getSerie() + "-" + ventaTB.getNumeracion());
            controller.setJasperPrint(jasperPrint);
            controller.show();
            Stage stage = WindowStage.StageLoader(parent, "Historial de Salida");
            stage.setResizable(true);
            stage.show();
            stage.requestFocus();

        } catch (IOException | JRException ex) {
            Tools.AlertMessageError(apWindow, "Reporte de Historial de Salida", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    private void printTicket() {
        FxOpcionesImprimirController imprimirController = new FxOpcionesImprimirController();
        imprimirController.loadComponents();
        imprimirController.setInitPostOpcionesVentaLlevar(this);
        imprimirController.loadDataHistorialSuministroLlevar(ventaTB.getIdVenta(), suministroTB.getIdSuministro());
        imprimirController.printEventHistorialSuministroLlevarTicket();
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
    private void onKeyPressedTicket(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            printTicket();
        }
    }

    @FXML
    private void onActionTicket(ActionEvent event) {
        printTicket();
    }

    @FXML
    private void onKeyPressedClose(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(apWindow);
        }
    }

    @FXML
    private void onActionClose(ActionEvent event) {
        Tools.Dispose(apWindow);
    }

    public void setInitVentaDetalleController(FxPostVentaDetalleController ventaDetalleController) {
        this.ventaDetalleController = ventaDetalleController;
    }

}
