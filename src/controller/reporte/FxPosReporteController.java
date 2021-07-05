package controller.reporte;

import controller.configuracion.empleados.FxEmpleadosListaController;
import controller.contactos.clientes.FxClienteListaController;
import controller.menus.FxPrincipalController;
import controller.tools.FilesRouters;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
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

public class FxPosReporteController implements Initializable {

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
    private HBox hbTipoPago;
    @FXML
    private CheckBox cbTipoPagoSeleccionar;
    @FXML
    private RadioButton rbContado;
    @FXML
    private RadioButton rbCredito;

    private FxPrincipalController fxPrincipalController;

    private String idCliente;

    private String idEmpleado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.actualDate(Tools.getDate(), dpFechaInicial);
        Tools.actualDate(Tools.getDate(), dpFechaFinal);
        ToggleGroup groupFormaPago = new ToggleGroup();
        rbContado.setToggleGroup(groupFormaPago);
        rbCredito.setToggleGroup(groupFormaPago);
        cbDocumentos.getItems().clear();
        TipoDocumentoADO.GetDocumentoCombBoxVentas().forEach(e -> cbDocumentos.getItems().add(e));
        idCliente = idEmpleado = "";
    }

    private void openWindowClientes() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_CLIENTE_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxClienteListaController controller = fXMLLoader.getController();
            controller.setInitPostReporteController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Elija un cliente", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
            controller.fillCustomersTable("");
        } catch (IOException ex) {
            System.out.println("Venta reporte controller:" + ex.getLocalizedMessage());
        }
    }

    private void openWindowVendedores() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_EMPLEADO_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxEmpleadosListaController controller = fXMLLoader.getController();
            controller.setInitPostReporteController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Elija un vendedor", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
            controller.fillEmpleadosTable("");
        } catch (IOException ex) {
            System.out.println("Venta reporte controller:" + ex.getLocalizedMessage());
        }
    }

    private JasperPrint reportGenerate() throws JRException {
        ArrayList<VentaTB> list = VentaADO.GetReporteGenetalVentas(
                Tools.getDatePicker(dpFechaInicial),
                Tools.getDatePicker(dpFechaFinal),
                cbDocumentosSeleccionar.isSelected() ? 0 : cbDocumentos.getSelectionModel().getSelectedItem().getIdTipoDocumento(),
                idCliente,
                idEmpleado,
                cbTipoPagoSeleccionar.isSelected() ? 0 : rbContado.isSelected() ? 1 : 2);

        double totalcontado = 0;
        double totalcredito = 0;
        double totalanulado = 0;
        for (VentaTB vt : list) {
            if (vt.getNotaCreditoTB() != null) {
                totalanulado += vt.getTotal();
            } else {
                switch (vt.getEstado()) {
                    case 1:
                        totalcontado += vt.getTotal();
                        break;
                    case 2:
                        totalcredito += vt.getTotal();
                        break;
                    default:
                        totalanulado += vt.getTotal();
                        break;
                }
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
        return jasperPrint;
    }

    private void openViewVisualizar() {
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

                URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxReportViewController controller = fXMLLoader.getController();
                controller.setFileName("ListaDeVentas");
                controller.setJasperPrint(reportGenerate());
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

    @FXML
    private void onKeyPressedVisualizar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openViewVisualizar();
        }
    }

    @FXML
    private void onActionVisualizar(ActionEvent event) {
        openViewVisualizar();
    }

    @FXML
    private void onKeyPressedPdf(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionPdf(ActionEvent event) {

    }

    @FXML
    private void onKeyPressedExcel(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionExcel(ActionEvent event) {

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
    private void onActionCbTipoPago(ActionEvent event) {
        hbTipoPago.setDisable(cbTipoPagoSeleccionar.isSelected());
    }

    @FXML
    private void onKeyPressedClientes(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowClientes();
        }
    }

    @FXML
    private void onActionClientes(ActionEvent event) {
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

    public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }
}
