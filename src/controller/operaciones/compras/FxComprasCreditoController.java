package controller.operaciones.compras;

import controller.consultas.compras.FxComprasDetalleController;
import controller.consultas.pagar.FxCuentasPorPagarController;
import controller.reporte.FxReportViewController;
import controller.tools.FilesRouters;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.awt.HeadlessException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.CompraADO;
import model.CompraCreditoTB;
import model.ProveedorTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FxComprasCreditoController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private Label lblProveedor;
    @FXML
    private TableView<CompraCreditoTB> tvList;
    @FXML
    private TableColumn<CompraCreditoTB, CheckBox> tcOpcion;
    @FXML
    private TableColumn<CompraCreditoTB, String> tcCouta;
    @FXML
    private TableColumn<CompraCreditoTB, String> tcFecha;
    @FXML
    private TableColumn<CompraCreditoTB, Text> tcEstado;
    @FXML
    private TableColumn<CompraCreditoTB, String> tcMonto;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblMontoPagado;

    private FxCuentasPorPagarController cuentasPorPagarController;

    private String idCompra;

    private double monto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
        tcOpcion.setCellValueFactory(new PropertyValueFactory<>("cbSeleccion"));
        tcCouta.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getCuota()));
        tcFecha.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getFechaRegistro()));
        tcEstado.setCellValueFactory(new PropertyValueFactory<>("txtEstado"));
        tcMonto.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getMonto(), 2)));
    }

    public void loadData(String idCompra, String proveedor, double total) {
        this.idCompra = idCompra;
        lblProveedor.setText(proveedor);
        lblTotal.setText(Tools.roundingValue(total, 2));
        loadTableCompraCredito();
    }

    public void loadTableCompraCredito() {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ObservableList<CompraCreditoTB>> task = new Task<ObservableList<CompraCreditoTB>>() {
            @Override
            public ObservableList<CompraCreditoTB> call() {
                return CompraADO.Listar_Compra_Credito(idCompra);
            }
        };
        task.setOnScheduled(w -> {
            lblLoad.setVisible(true);
        });
        task.setOnFailed(w -> {
            lblLoad.setVisible(false);
        });
        task.setOnSucceeded(w -> {
            double montoPagado = 0;
            ObservableList<CompraCreditoTB> observableList = task.getValue();
            montoPagado = observableList.stream().filter((creditoTB) -> (creditoTB.isEstado())).map((creditoTB) -> creditoTB.getMonto()).reduce(montoPagado, (accumulator, _item) -> accumulator + _item);
            lblMontoPagado.setText(Tools.roundingValue(montoPagado, 2));
            tvList.setItems(observableList);
            lblLoad.setVisible(false);
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void eventAmortizar() {
        int value = 0;
        value = tvList.getItems().stream().map((CompraCreditoTB cctb) -> cctb.getCbSeleccion().isSelected() && !cctb.getCbSeleccion().isDisable() ? 1 : 0).reduce(value, Integer::sum);
        if (value == 0) {
            Tools.AlertMessageWarning(apWindow, "Generar Pago", "Seleccione un elemento de la lista para continuar.");
            return;
        }

        try {
            URL url = getClass().getResource(FilesRouters.FX_AMARTIZAR_PAGOS);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxAmortizarPagosController controller = fXMLLoader.getController();
            controller.setInitValues(idCompra, tvList);
            controller.setInitAmortizarPagosController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Generar Pago", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.show();

        } catch (IOException ex) {
            System.out.println("Controller banco" + ex.getLocalizedMessage());
        }

    }

    private void openWindowReport() {
        try {
            ArrayList<CompraCreditoTB> list = new ArrayList();
            tvList.getItems().stream().filter((cctb) -> (cctb.getCbSeleccion().isSelected() && cctb.getCbSeleccion().isDisable() && cctb.isEstado())).forEachOrdered((CompraCreditoTB cctb) -> {
                CompraCreditoTB compraCreditoTB = new CompraCreditoTB();
                compraCreditoTB.setId(1);
                compraCreditoTB.setFechaPago("Se realizó el pago de la fecha del " + cctb.getFechaRegistro() + " por el monto de " + Tools.roundingValue(cctb.getMonto(), 2));
                compraCreditoTB.setMonto(cctb.getMonto());
                monto += cctb.getMonto();
                list.add(compraCreditoTB);
            });

            if (list.isEmpty()) {
                Tools.AlertMessageWarning(apWindow, "Compra realizada", "No hay registros para mostrar en el reporte.");
                return;
            }

            ProveedorTB proveedorTB = CompraADO.Obtener_Proveedor_Por_Id_Compra(idCompra);

            InputStream imgInputStream = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            Map map = new HashMap();
            map.put("LOGO", imgInputStream);
            map.put("EMPRESA", Session.COMPANY_RAZON_SOCIAL);
            map.put("DIRECCION", Session.COMPANY_DOMICILIO);
            map.put("TELEFONOCELULAR", "TEL.: " + Session.COMPANY_TELEFONO + " CEL.: " + Session.COMPANY_CELULAR);
            map.put("EMAIL", "Email: " + Session.COMPANY_EMAIL);
            map.put("DOCUMENTOEMPRESA", "R.U.C " + Session.COMPANY_NUM_DOCUMENTO);
            
            map.put("FECHA_EMISION", Tools.getDate("dd/MM/yyyy"));
            map.put("PROVEEDOR", proveedorTB.getRazonSocial());
            map.put("PROVEEDORNOMDOCUMENTO", proveedorTB.getTipoDocumentoName() + ":");
            map.put("PROVEEDORNUMDOCUMENTO", proveedorTB.getNumeroDocumento());
            map.put("PROVEEDORDIRECCION", proveedorTB.getDireccion());
            map.put("PROVEEDORTELEFONOS", proveedorTB.getTelefono() + " - " + proveedorTB.getCelular());
            map.put("PROVEEDOREMAIL", proveedorTB.getEmail());

            map.put("TOTAL", Tools.roundingValue(monto, 2));

            JasperPrint jasperPrint = JasperFillManager.fillReport(FxComprasDetalleController.class.getResourceAsStream("/report/CompraAmortizar.jasper"), map, new JRBeanCollectionDataSource(list));

            URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxReportViewController controller = fXMLLoader.getController();
            controller.setJasperPrint(jasperPrint);
            controller.show();
            Stage stage = WindowStage.StageLoader(parent, "Reporte de Pago");
            stage.setResizable(true);
            stage.show();
            stage.requestFocus();
        } catch (HeadlessException | JRException | IOException ex) {
            Tools.AlertMessageError(apWindow, "Reporte de Pago", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onKeyPressedAmortizar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            eventAmortizar();
        }
    }

    @FXML
    private void onActionAmortizar(ActionEvent event) {
        eventAmortizar();
    }

    @FXML
    private void onKeyPressedReporte(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowReport();
        }
    }

    @FXML
    private void onActionReporte(ActionEvent event) {
        openWindowReport();
    }

    public void setInitControllerComprasCredito(FxCuentasPorPagarController cuentasPorPagarController) {
        this.cuentasPorPagarController = cuentasPorPagarController;
    }

}
