package controller.reporte;

import controller.contactos.proveedores.FxProveedorListaController;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.CompraADO;
import model.CompraTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class FxCompraReporteController implements Initializable {

    @FXML
    private VBox vbWindow;
    @FXML
    private DatePicker dpFechaInicial;
    @FXML
    private DatePicker dpFechaFinal;
    @FXML
    private CheckBox cbProveedoresSeleccionar;
    @FXML
    private TextField txtProveedor;
    @FXML
    private Button btnProveedor;
    @FXML
    private RadioButton rbContado;
    @FXML
    private RadioButton rbCredito;
    @FXML
    private CheckBox cbFormasPagoSeleccionar;
    @FXML
    private HBox hbFormasPago;

    private AnchorPane vbPrincipal;

    private String idProveedor;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.actualDate(Tools.getDate(), dpFechaInicial);
        Tools.actualDate(Tools.getDate(), dpFechaFinal);
        ToggleGroup groupFormaPago = new ToggleGroup();
        rbContado.setToggleGroup(groupFormaPago);
        rbCredito.setToggleGroup(groupFormaPago);
        idProveedor = "";
    }

    private void openWindowReporteGeneral() {
        try {
            if (!cbProveedoresSeleccionar.isSelected() && idProveedor.equalsIgnoreCase("") && txtProveedor.getText().isEmpty()) {
                Tools.AlertMessageWarning(vbWindow, "Reporte General de Compras", "Ingrese un proveedor para generar el reporte.");
                btnProveedor.requestFocus();
            } else {
                ArrayList<CompraTB> list = CompraADO.GetReporteGenetalCompras(
                        Tools.getDatePicker(dpFechaInicial),
                        Tools.getDatePicker(dpFechaFinal),
                        idProveedor,
                        cbFormasPagoSeleccionar.isSelected()? 0 : rbContado.isSelected() ? 1 : 2);
                if (list.isEmpty()) {
                    Tools.AlertMessageWarning(vbWindow, "Repsorte General de Compras", "No hay registros para mostrar en el reporte.");
                    return;
                }
                double totalContado = 0;
                double totalCredito = 0;
                double totalAnulados = 0;
                for(CompraTB c : list){
                    if(c.getEstado() == 1){
                        totalContado+=c.getTotal();
                    }else if(c.getEstado() == 2){
                        totalCredito+=c.getTotal();
                    }else if(c.getEstado() == 3){
                        totalAnulados+=c.getTotal();
                    }
                }
                

                Map map = new HashMap();
                map.put("PERIODO", dpFechaInicial.getValue().format(DateTimeFormatter.ofPattern("dd/MM/YYYY")) + " - " + dpFechaFinal.getValue().format(DateTimeFormatter.ofPattern("dd/MM/YYYY")));
                map.put("ORDEN", "TODOS");
                map.put("PROVEEDOR", cbProveedoresSeleccionar.isSelected() ? "TODOS" : txtProveedor.getText().toUpperCase());
                map.put("ESTADO", "TODOS");
                map.put("COMPRACONTADO", Tools.roundingValue(totalContado, 2));
                map.put("COMPRACREDITO", Tools.roundingValue(totalCredito, 2));
                map.put("COMPRANULADAS", Tools.roundingValue(totalAnulados, 2));

                JasperPrint jasperPrint = JasperFillManager.fillReport(FxVentaReporteController.class.getResourceAsStream("/report/CompraGeneral.jasper"), map, new JRBeanCollectionDataSource(list));

                URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxReportViewController controller = fXMLLoader.getController();
                controller.setJasperPrint(jasperPrint);
                controller.show();
                Stage stage = WindowStage.StageLoader(parent, "Reporte General de Compras");
                stage.setResizable(true);
                stage.show();
                stage.requestFocus();
            }

        } catch (HeadlessException | JRException | IOException ex) {
            Tools.AlertMessageError(vbWindow, "Reporte General de Compras", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    private void openWindowProveedores() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_PROVEEDORES_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxProveedorListaController controller = fXMLLoader.getController();
            controller.setInitComprasReporteController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Proveedor", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();
            controller.fillCustomersTable("");
        } catch (IOException ex) {
            System.out.println("Controller reporte" + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onActionCbProveedoresSeleccionar(ActionEvent event) {
        if (cbProveedoresSeleccionar.isSelected()) {
            btnProveedor.setDisable(true);
            txtProveedor.setText("");
            idProveedor = "";
        } else {
            btnProveedor.setDisable(false);
        }
    }

    @FXML
    private void onActionCbFormasPagoSeleccionar(ActionEvent event) {
        if (cbFormasPagoSeleccionar.isSelected()) {
            hbFormasPago.setDisable(true);
        } else {
            hbFormasPago.setDisable(false);
        }
    }

    @FXML
    private void onKeyPressedReporteGeneral(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowReporteGeneral();
        }
    }

    @FXML
    private void onActionReporteGeneral(ActionEvent event) {
        openWindowReporteGeneral();
    }

    @FXML
    private void onKeyPressedProveedor(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowProveedores();
        }
    }

    @FXML
    private void onActionProveedor(ActionEvent event) {
        openWindowProveedores();
    }

    public void setInitCompraReporteValue(String idProveedor, String datosProveedor) {
        this.idProveedor = idProveedor;
        txtProveedor.setText(datosProveedor);
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
