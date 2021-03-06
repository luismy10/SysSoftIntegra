package controller.reporte;

import controller.inventario.suministros.FxSuministrosListaController;
import controller.menus.FxPrincipalController;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.awt.HeadlessException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.DetalleADO;
import model.DetalleTB;
import model.Utilidad;
import model.UtilidadADO;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

public class FxVentaUtilidadesController implements Initializable {

    @FXML
    private VBox vbWindow;
    @FXML
    private DatePicker dpFechaInicial;
    @FXML
    private DatePicker dpFechaFinal;
    @FXML
    private CheckBox cbProductosSeleccionar;
    @FXML
    private TextField txtProducto;
    @FXML
    private Button btnProductos;
    @FXML
    private CheckBox cbMostrarProducto;
    @FXML
    private CheckBox cbCategoriaSeleccionar;
    @FXML
    private CheckBox cbMarcaSeleccionar;
    @FXML
    private CheckBox cbPresentacionSeleccionar;
    @FXML
    private ComboBox<DetalleTB> cbCategorias;
    @FXML
    private ComboBox<DetalleTB> cbMarcas;
    @FXML
    private ComboBox<DetalleTB> cbPresentacion;

    private FxPrincipalController fxPrincipalController;

    private String idSuministro;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.actualDate(Tools.getDate(), dpFechaInicial);
        Tools.actualDate(Tools.getDate(), dpFechaFinal);
        DetalleADO.GetDetailId("0006").forEach(e -> {
            cbCategorias.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        DetalleADO.GetDetailId("0007").forEach(e -> {
            cbMarcas.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        DetalleADO.GetDetailId("0008").forEach(e -> {
            cbPresentacion.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        idSuministro = "";
    }

//    public void fillUtilidadTable(short option, String fechaInicial, String fechaFinal) {
//        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
//            Thread t = new Thread(runnable);
//            t.setDaemon(true);
//            return t;
//        });
//        Task<ObservableList<Utilidad>> task = new Task<ObservableList<Utilidad>>() {
//            @Override
//            public ObservableList<Utilidad> call() {
//                return UtilidadADO.listUtilidadVenta(option, fechaInicial, fechaFinal, "");
//            }
//        };
//        task.setOnSucceeded((WorkerStateEvent e) -> {
//
//        });
//        task.setOnFailed((WorkerStateEvent event) -> {
//
//        });
//        task.setOnScheduled((WorkerStateEvent event) -> {
//
//        });
//        exec.execute(task);
//        if (!exec.isShutdown()) {
//            exec.shutdown();
//        }
//    }
    private static boolean validateDuplicateArticulo(ArrayList<Utilidad> view, Utilidad utilidad) {
        boolean ret = false;
        for (int i = 0; i < view.size(); i++) {
            if (view.get(i).getIdSuministro().equals(utilidad.getIdSuministro())) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    private void openWindowSuministros() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_SUMINISTROS_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxSuministrosListaController controller = fXMLLoader.getController();
            controller.setInitVentaUtilidadesController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Producto", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
            controller.fillSuministrosTable((short)0,""); 
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void openViewReporteGeneral() {
        try {
            if (!cbProductosSeleccionar.isSelected() && idSuministro.equalsIgnoreCase("") && txtProducto.getText().isEmpty()) {
                Tools.AlertMessageWarning(vbWindow, "Utilidades", "Ingrese un producto para generar el reporte.");
                btnProductos.requestFocus();
            } else if (!cbCategoriaSeleccionar.isSelected() && cbCategorias.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(vbWindow, "Utilidades", "Seleccione una categor??a para generar el reporte.");
                cbCategorias.requestFocus();
            } else if (!cbMarcaSeleccionar.isSelected() && cbMarcas.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(vbWindow, "Utilidades", "Seleccione una marca para generar el reporte.");
                cbMarcas.requestFocus();
            } else if (!cbPresentacionSeleccionar.isSelected() && cbPresentacion.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(vbWindow, "Utilidades", "Seleccione una presentaci??n para generar el reporte.");
                cbPresentacion.requestFocus();
            } else {
                ArrayList<Utilidad> detalle_list = UtilidadADO.listUtilidadVenta(
                        Tools.getDatePicker(dpFechaInicial),
                        Tools.getDatePicker(dpFechaFinal),
                        idSuministro,
                        cbCategoriaSeleccionar.isSelected() ? 0 : cbCategorias.getSelectionModel().getSelectedItem().getIdDetalle(),
                        cbMarcaSeleccionar.isSelected() ? 0 : cbMarcas.getSelectionModel().getSelectedItem().getIdDetalle(),
                        cbPresentacionSeleccionar.isSelected() ? 0 : cbPresentacion.getSelectionModel().getSelectedItem().getIdDetalle());
                if (detalle_list.isEmpty()) {
                    Tools.AlertMessageWarning(vbWindow, "Utilidades", "No hay registros para mostrar en el reporte.");
                    return;
                }

                double costoTotal = 0;
                double precioTotal = 0;
                double utilidadGenerada = 0;
                for (int i = 0; i < detalle_list.size(); i++) {
                    if (validateDuplicateArticulo(detalle_list, detalle_list.get(i))) {
                        costoTotal += detalle_list.get(i).getCostoVentaTotal();
                        precioTotal += detalle_list.get(i).getPrecioVentaTotal();
                        utilidadGenerada += detalle_list.get(i).getUtilidad();
                    } else {
                        costoTotal = detalle_list.get(i).getCostoVentaTotal();
                        precioTotal = detalle_list.get(i).getPrecioVentaTotal();
                        utilidadGenerada = detalle_list.get(i).getUtilidad();
                    }
                }

                ArrayList<Utilidad> list = new ArrayList<>();
                int count = 0;

                for (int i = 0; i < detalle_list.size(); i++) {
                    if (validateDuplicateArticulo(list, detalle_list.get(i))) {
                        for (int j = 0; j < list.size(); j++) {
                            if (list.get(j).getIdSuministro().equalsIgnoreCase(detalle_list.get(i).getIdSuministro())) {
                                Utilidad newUtilidad = list.get(j);
                                newUtilidad.setCantidad(newUtilidad.getCantidad() + detalle_list.get(i).getCantidad());

                                newUtilidad.setCostoVenta(detalle_list.get(i).getCostoVenta());
                                newUtilidad.setCostoVentaTotal(newUtilidad.getCostoVentaTotal() + detalle_list.get(i).getCostoVentaTotal());

                                newUtilidad.setPrecioVenta(detalle_list.get(i).getPrecioVenta());
                                newUtilidad.setPrecioVentaTotal(newUtilidad.getPrecioVentaTotal() + detalle_list.get(i).getPrecioVentaTotal());

                                newUtilidad.setUtilidad(newUtilidad.getUtilidad() + detalle_list.get(i).getUtilidad());
                            }
                        }
                    } else {
                        count++;
                        Utilidad newUtilidad = new Utilidad();
                        newUtilidad.setId(count);
                        newUtilidad.setIdSuministro(detalle_list.get(i).getIdSuministro());
                        newUtilidad.setClave(detalle_list.get(i).getClave());
                        newUtilidad.setNombreMarca(detalle_list.get(i).getNombreMarca());
                        newUtilidad.setCantidad(detalle_list.get(i).getCantidad());

                        newUtilidad.setCostoVenta(detalle_list.get(i).getCostoVenta());
                        newUtilidad.setCostoVentaTotal(detalle_list.get(i).getCostoVentaTotal());

                        newUtilidad.setPrecioVenta(detalle_list.get(i).getPrecioVenta());
                        newUtilidad.setPrecioVentaTotal(detalle_list.get(i).getPrecioVentaTotal());

                        newUtilidad.setUtilidad(detalle_list.get(i).getUtilidad());

                        newUtilidad.setValorInventario(detalle_list.get(i).isValorInventario());
                        newUtilidad.setMedida(detalle_list.get(i).getMedida());
                        newUtilidad.setSimboloMoneda(detalle_list.get(i).getSimboloMoneda());
                        list.add(newUtilidad);
                    }
                }

                InputStream dir = getClass().getResourceAsStream("/report/Utilidad.jasper");

                JasperReport jasperReport = (JasperReport) JRLoader.loadObject(dir);
                Map map = new HashMap();
                map.put("RANGO_FECHA", dpFechaInicial.getValue().format(DateTimeFormatter.ofPattern("dd/MM/YYYY")) + " - " + dpFechaFinal.getValue().format(DateTimeFormatter.ofPattern("dd/MM/YYYY")));
                map.put("PRODUCTOS", cbProductosSeleccionar.isSelected() ? "TODOS" : txtProducto.getText());
                map.put("CATEGORIA", cbCategoriaSeleccionar.isSelected() ? "TODOS" : cbCategorias.getSelectionModel().getSelectedItem().getNombre());
                map.put("MARCA", cbMarcaSeleccionar.isSelected() ? "TODOS" : cbMarcas.getSelectionModel().getSelectedItem().getNombre());
                map.put("PRESENTACION", cbPresentacionSeleccionar.isSelected() ? "TODOS" : cbPresentacion.getSelectionModel().getSelectedItem().getNombre());
                map.put("ORDEN", "");
                map.put("COSTO_TOTAL", Tools.roundingValue(costoTotal, 2));
                map.put("PRECIO_TOTAL", Tools.roundingValue(precioTotal, 2));
                map.put("UTILIDAD_GENERADA", Tools.roundingValue(utilidadGenerada, 2));

                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JRBeanCollectionDataSource(cbMostrarProducto.isSelected() ? detalle_list : list));

                URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxReportViewController controller = fXMLLoader.getController();
                controller.setJasperPrint(jasperPrint);
                controller.show();
                Stage stage = WindowStage.StageLoader(parent, "Reporte General de Utilidades");
                stage.setResizable(true);
                stage.show();
                stage.requestFocus();
            }
        } catch (HeadlessException | JRException | IOException ex) {
            Tools.AlertMessageError(vbWindow, "Utilidades", "Error al generar el reporte : " + ex);
        }
    }

    @FXML
    private void onKeyPressedReporteGeneral(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openViewReporteGeneral();
        }
    }

    @FXML
    private void onActionReporteGeneral(ActionEvent event) {
        openViewReporteGeneral();
    }

    @FXML
    private void onActionCbProductosSeleccionar(ActionEvent event) {
        if (cbProductosSeleccionar.isSelected()) {
            btnProductos.setDisable(true);
            idSuministro = "";
            txtProducto.setText("");
        } else {
            btnProductos.setDisable(false);
        }
    }

    @FXML
    private void onActionCbCategoriaSeleccionar(ActionEvent event) {
        if (cbCategoriaSeleccionar.isSelected()) {
            cbCategorias.setDisable(true);
            cbCategorias.getSelectionModel().select(null);
        } else {
            cbCategorias.setDisable(false);
        }
    }

    @FXML
    private void onActionCbMarcaSeleccionar(ActionEvent event) {
        if (cbMarcaSeleccionar.isSelected()) {
            cbMarcas.setDisable(true);
            cbMarcas.getSelectionModel().select(null);
        } else {
            cbMarcas.setDisable(false);
        }
    }

    @FXML
    private void onActionCbPresentacionSeleccionar(ActionEvent event) {
        if (cbPresentacionSeleccionar.isSelected()) {
            cbPresentacion.setDisable(true);
            cbPresentacion.getSelectionModel().select(null);
        } else {
            cbPresentacion.setDisable(false);
        }
    }

    @FXML
    private void onKeyPressedProductos(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowSuministros();
        }
    }

    @FXML
    private void onActionProductos(ActionEvent event) {
        openWindowSuministros();
    }

    public void setIdSuministro(String idSuministro) {
        this.idSuministro = idSuministro;
    }

    public TextField getTxtProducto() {
        return txtProducto;
    }

    public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }

}
