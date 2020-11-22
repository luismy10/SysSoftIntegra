package controller.reporte;

import controller.configuracion.empleados.FxEmpleadosListaController;
import controller.contactos.clientes.FxClienteListaController;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.awt.HeadlessException;
import java.io.IOException;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.TipoDocumentoADO;
import model.TipoDocumentoTB;
import model.VentaADO;
import model.VentaTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FxVentaReporteController implements Initializable {

    @FXML
    private VBox window;
    @FXML
    private DatePicker dpFechaInicial;
    @FXML
    private DatePicker dpFechaFinal;
    @FXML
    private ComboBox<TipoDocumentoTB> cbDocumentos;
    @FXML
    private CheckBox cbDocumentosSeleccionar;
    @FXML
    private TextField txtClientes;
    @FXML
    private Button btnClientes;
    @FXML
    private CheckBox cbClientesSeleccionar;
    @FXML
    private TextField txtVendedores;
    @FXML
    private Button btnVendedor;
    @FXML
    private CheckBox cbVendedoresSeleccionar;
    @FXML
    private DatePicker dpFechaInicialGlobal;
    @FXML
    private DatePicker dpFechaFinalGlobal;
    @FXML
    private ComboBox<String> cbOrdenar;
    @FXML
    private ComboBox<String> cbOrden;
    @FXML
    private CheckBox cbMostrarVenta;

    private AnchorPane vbPrincipal;

    private String idCliente;

    private String idEmpleado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.actualDate(Tools.getDate(), dpFechaInicial);
        Tools.actualDate(Tools.getDate(), dpFechaFinal);
        Tools.actualDate(Tools.getDate(), dpFechaInicialGlobal);
        Tools.actualDate(Tools.getDate(), dpFechaFinalGlobal);
        cbDocumentos.getItems().clear();
        TipoDocumentoADO.GetDocumentoCombBox().forEach(e -> {
            cbDocumentos.getItems().add(new TipoDocumentoTB(e.getIdTipoDocumento(), e.getNombre(), e.isPredeterminado()));
        });
        idCliente = idEmpleado = "";
//        cbMostar.getItems().addAll("Día", "Semana", "Quincena", "Mensual", "Año");
//        cbMostar.getSelectionModel().select(0);
        cbOrdenar.getItems().addAll("Fecha", "Total");
        cbOrdenar.getSelectionModel().select(0);
        cbOrden.getItems().addAll("Ascendente", "Descentente");
        cbOrden.getSelectionModel().select(0);
    }

    private void openWindowClientes() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_CLIENTE_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxClienteListaController controller = fXMLLoader.getController();
            controller.setInitVentaReporteController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Elija un cliente", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
            controller.fillCustomersTable("");
        } catch (IOException ex) {
            System.out.println("Venta reporte controller:" + ex.getLocalizedMessage());
        }
    }

    private void openWindowVendedores() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_EMPLEADO_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxEmpleadosListaController controller = fXMLLoader.getController();
            controller.setInitVentaReporteController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Elija un vendedor", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
            controller.fillEmpleadosTable("");
        } catch (IOException ex) {
            System.out.println("Venta reporte controller:" + ex.getLocalizedMessage());
        }
    }

    private void openViewReporteGeneral() {
        try {
            if (!cbDocumentosSeleccionar.isSelected() && cbDocumentos.getSelectionModel().getSelectedIndex() < 0) {
                Tools.AlertMessageWarning(window, "Reporte General de Ventas", "Seleccione un documento para generar el reporte.");
                cbDocumentos.requestFocus();
            } else if (!cbClientesSeleccionar.isSelected() && idCliente.equalsIgnoreCase("") && txtClientes.getText().isEmpty()) {
                Tools.AlertMessageWarning(window, "Reporte General de Ventas", "Ingrese un cliente para generar el reporte.");
                btnClientes.requestFocus();
            } else if (!cbVendedoresSeleccionar.isSelected() && idEmpleado.equalsIgnoreCase("") && txtVendedores.getText().isEmpty()) {
                Tools.AlertMessageWarning(window, "Reporte General de Ventas", "Ingrese un empleado para generar el reporte.");
                btnVendedor.requestFocus();
            } else {
                ArrayList<VentaTB> list = VentaADO.GetReporteGenetalVentas(
                        Tools.getDatePicker(dpFechaInicial),
                        Tools.getDatePicker(dpFechaFinal),
                        cbDocumentosSeleccionar.isSelected() ? 0 : cbDocumentos.getSelectionModel().getSelectedItem().getIdTipoDocumento(),
                        idCliente,
                        idEmpleado);
                if (list.isEmpty()) {
                    Tools.AlertMessageWarning(window, "Reporte General de Ventas", "No hay registros para mostrar en el reporte.");
                    return;
                }

                double totalcontado = 0;
                double totalcredito = 0;
                double totalanulado = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getEstado() == 1) {
                        totalcontado += list.get(i).getTotal();
                    }else if(list.get(i).getEstado() == 2){
                        totalcredito += list.get(i).getTotal();
                    }else if(list.get(i).getEstado() == 3){
                        totalanulado += list.get(i).getTotal();
                    }
                }

                Map map = new HashMap();
                map.put("PERIODO", dpFechaInicial.getValue().format(DateTimeFormatter.ofPattern("dd/MM/YYYY")) + " - " + dpFechaFinal.getValue().format(DateTimeFormatter.ofPattern("dd/MM/YYYY")));
                map.put("DOCUMENTO", cbDocumentosSeleccionar.isSelected() ? "TODOS" : cbDocumentos.getSelectionModel().getSelectedItem().getNombre());
                map.put("ORDEN", "TODOS");
                map.put("CLIENTE", cbClientesSeleccionar.isSelected() ? "TODOS" : txtClientes.getText().toUpperCase());
                map.put("ESTADO", "TODOS");
                map.put("VENDEDOR", cbVendedoresSeleccionar.isSelected() ? "TODOS" : txtVendedores.getText().toUpperCase());
                map.put("TOTAANULADO", Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(totalanulado, 2));
                map.put("TOTALCREDITO", Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(totalcredito, 2));
                map.put("TOTALCONTADO", Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(totalcontado, 2));

                JasperPrint jasperPrint = JasperFillManager.fillReport(FxVentaReporteController.class.getResourceAsStream("/report/VentaGeneral.jasper"), map, new JRBeanCollectionDataSource(list));

                URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxReportViewController controller = fXMLLoader.getController();
                controller.setJasperPrint(jasperPrint);
                controller.show();
                Stage stage = WindowStage.StageLoader(parent, "Reporte General de Ventas");
                stage.setResizable(true);
                stage.show();
                stage.requestFocus();
            }

        } catch (HeadlessException | JRException | IOException ex) {
            Tools.AlertMessageError(window, "Reporte General de Ventas", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

//    private void openWindowReporteGlobal() {
//        try {
////            if (cbMostar.getSelectionModel().getSelectedIndex() < 0) {
////                Tools.AlertMessageWarning(window, "Reporte Global de Ventas", "Seleccione la forma de mostrar el reporte.");
////                cbMostar.requestFocus();
////            } else 
//            if (cbOrdenar.getSelectionModel().getSelectedIndex() < 0) {
//                Tools.AlertMessageWarning(window, "Reporte Global de Ventas", "Seleccione la forma de ordenar el reporte.");
//                cbOrdenar.requestFocus();
//            } else {
//                ArrayList<VentaTB> list = VentaADO.GetReporteSumaVentaPorDia(
//                        Tools.getDatePicker(dpFechaInicialGlobal),
//                        Tools.getDatePicker(dpFechaFinalGlobal),
//                        !(cbOrdenar.getSelectionModel().getSelectedIndex() == 0),
//                        !(cbOrden.getSelectionModel().getSelectedIndex() == 0));
//
//                if (list.isEmpty()) {
//                    Tools.AlertMessageWarning(window, "Reporte Global de Ventas", "No hay registros para mostrar en el reporte.");
//                    return;
//                }
//
//                double total = 0;
//
//                for (int i = 0; i < list.size(); i++) {
//                    total = total + list.get(i).getTotal();
//                }
//
//                ArrayList<VentaTB> newList = new ArrayList<>();
//                int count = 0;
//
//                for (int i = 0; i < list.size(); i++) {
//                    if (validateDuplicateDate(newList, list.get(i))) {
//                        for (int j = 0; j < newList.size(); j++) {
//                            if (newList.get(j).getFechaVenta().equalsIgnoreCase(list.get(i).getFechaVenta())) {
//                                VentaTB newVenta = newList.get(j);
//                                newVenta.setFechaVenta(list.get(i).getFechaVenta());
//                                newVenta.setTotal(newVenta.getTotal() + list.get(i).getTotal());
//                            }
//                        }
//                    } else {
//                        count++;
//                        VentaTB newVenta = new VentaTB();
//                        newVenta.setId(count);
//                        newVenta.setFechaVenta(list.get(i).getFechaVenta());
//                        newVenta.setTotal(list.get(i).getTotal());
//                        newList.add(newVenta);
//                    }
//                }
//
//                ArrayList<VentaTB> detail_list = new ArrayList<>();
//                detail_list.add(new VentaTB(1, "campras", 00.00));
//                detail_list.add(new VentaTB(2, "ventas", 00.00));
//                detail_list.add(new VentaTB(3, "lotes", 00.00));
//                detail_list.add(new VentaTB(4, "campras", 00.00));
//                detail_list.add(new VentaTB(5, "ventas", 00.00));
//                detail_list.add(new VentaTB(6, "lotes", 00.00));
//
//                Map map = new HashMap();
//                map.put("PERIODO", dpFechaInicialGlobal.getValue().format(DateTimeFormatter.ofPattern("dd/MM/YYYY")) + " - " + dpFechaFinalGlobal.getValue().format(DateTimeFormatter.ofPattern("dd/MM/YYYY")));
//                map.put("MOSTRAR", "Día");
//                map.put("ORDEN", cbOrdenar.getSelectionModel().getSelectedItem() + " - " + cbOrden.getSelectionModel().getSelectedItem());
//                map.put("TOTAL", Session.MONEDA_SIMBOLO + " " + total);
//                map.put("DETALLE_VENTA", new JRBeanCollectionDataSource(detail_list));
//
//                JasperPrint jasperPrint = JasperFillManager.fillReport(FxVentaReporteController.class.getResourceAsStream("/report/VentaGeneralTotales.jasper"), map, new JRBeanCollectionDataSource(newList));
//
//                URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
//                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
//                Parent parent = fXMLLoader.load(url.openStream());
//                //Controlller here
//                FxReportViewController controller = fXMLLoader.getController();
//                controller.setJasperPrint(jasperPrint);
//                controller.show();
//                Stage stage = WindowStage.StageLoader(parent, "Reporte Global de Ventas");
//                stage.setResizable(true);
//                stage.show();
//                stage.requestFocus();
//            }
//        } catch (HeadlessException | JRException | IOException ex) {
//            Tools.AlertMessageError(window, "Reporte Global de Ventas", "Error al generar el reporte : " + ex.getLocalizedMessage());
//        }
//    }
    private static boolean validateDuplicateDate(ArrayList<VentaTB> view, VentaTB ventaTB) {
        boolean value = false;
        for (int i = 0; i < view.size(); i++) {
            if (view.get(i).getFechaVenta().equals(ventaTB.getFechaVenta())) {
                value = true;
                break;
            }
        }
        return value;
    }

    @FXML
    private void onActionCbDocumentosSeleccionar(ActionEvent event) {
        if (cbDocumentosSeleccionar.isSelected()) {
            cbDocumentos.setDisable(true);
            cbDocumentos.getSelectionModel().select(null);
        } else {
            cbDocumentos.setDisable(false);
        }
    }

    @FXML
    private void onActionCbClientesSeleccionar(ActionEvent event) {
        if (cbClientesSeleccionar.isSelected()) {
            btnClientes.setDisable(true);
            txtClientes.setText("");
            idCliente = "";
        } else {
            btnClientes.setDisable(false);
        }
    }

    @FXML
    private void onActionCbVendedoresSeleccionar(ActionEvent event) {
        if (cbVendedoresSeleccionar.isSelected()) {
            btnVendedor.setDisable(true);
            txtVendedores.setText("");
            idEmpleado = "";
        } else {
            btnVendedor.setDisable(false);
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
    private void onKeyPressedClientes(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowClientes();
        }
    }

    @FXML
    private void onActionClientes(ActionEvent event) throws IOException {
        openWindowClientes();
    }

    @FXML
    private void onKeyPressedVendedor(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowVendedores();
        }
    }

    @FXML
    private void onActionVendedor(ActionEvent event) {
        openWindowVendedores();
    }

    @FXML
    private void onKeyPressedPrueba(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
//            openWindowReporteGlobal();
        }
    }

    @FXML
    private void onActionPrueba(ActionEvent event) {
//        openWindowReporteGlobal();
    }

    public void setClienteVentaReporte(String idCliente, String datos) {
        this.idCliente = idCliente;
        txtClientes.setText(datos);
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public TextField getTxtVendedores() {
        return txtVendedores;
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
