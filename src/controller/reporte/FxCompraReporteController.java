package controller.reporte;

import controller.contactos.proveedores.FxProveedorListaController;
import controller.menus.FxPrincipalController;
import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.CompraADO;
import model.CompraTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

    private FxPrincipalController fxPrincipalController;

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

    private JasperPrint reportGenerate() throws JRException {
        ArrayList<CompraTB> list = CompraADO.GetReporteGenetalCompras(
                Tools.getDatePicker(dpFechaInicial),
                Tools.getDatePicker(dpFechaFinal),
                idProveedor,
                cbFormasPagoSeleccionar.isSelected() ? 0 : rbContado.isSelected() ? 1 : 2);

        double totalContado = 0;
        double totalCredito = 0;
        double totalAnulados = 0;
        for (CompraTB c : list) {
            switch (c.getEstado()) {
                case 1:
                    totalContado += c.getTotal();
                    break;
                case 2:
                    totalCredito += c.getTotal();
                    break;
                default:
                    totalAnulados += c.getTotal();
                    break;
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
        return jasperPrint;
    }

    private void openWindowProveedores() {
        try {
            fxPrincipalController.openFondoModal();
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
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.setOnShown(w -> controller.initTable());
            stage.show();
        } catch (IOException ex) {
            System.out.println("Controller reporte" + ex.getLocalizedMessage());
        }
    }

    private void openViewVisualizar() {
        try {
            if (!cbProveedoresSeleccionar.isSelected() && idProveedor.equalsIgnoreCase("") && txtProveedor.getText().isEmpty()) {
                Tools.AlertMessageWarning(vbWindow, "Reporte General de Compras", "Ingrese un proveedor para generar el reporte.");
                btnProveedor.requestFocus();
            } else {

                URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxReportViewController controller = fXMLLoader.getController();
                controller.setJasperPrint(reportGenerate());
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

    private void onEventPdf() {
        try {
            if (!cbProveedoresSeleccionar.isSelected() && idProveedor.equalsIgnoreCase("") && txtProveedor.getText().isEmpty()) {
                Tools.AlertMessageWarning(vbWindow, "Reporte General de Compras", "Ingrese un proveedor para generar el reporte.");
                btnProveedor.requestFocus();
            } else {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Reporte de Compra");
                fileChooser.setInitialFileName("ListaDeCompras");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("PDF Documento", Arrays.asList("*.pdf", "*.PDF"))
                );
                File file = fileChooser.showSaveDialog(vbWindow.getScene().getWindow());
                if (file != null) {
                    file = new File(file.getAbsolutePath());
                    if (file.getName().endsWith("pdf") || file.getName().endsWith("PDF")) {
                        JasperExportManager.exportReportToPdfFile(reportGenerate(), file.getAbsolutePath());
                    }
                }
            }
        } catch (JRException ex) {
            Tools.AlertMessageError(vbWindow, "Reporte General de Compras", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    private void onEventExcel() {
        if (!cbProveedoresSeleccionar.isSelected() && idProveedor.equalsIgnoreCase("") && txtProveedor.getText().isEmpty()) {
            Tools.AlertMessageWarning(vbWindow, "Reporte General de Compras", "Ingrese un proveedor para generar el reporte.");
            btnProveedor.requestFocus();
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Reporte de Compras");
            fileChooser.setInitialFileName("ListaDeCompras");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Libro de Excel (*.xlsx)", "*.xlsx"),
                    new FileChooser.ExtensionFilter("Libro de Excel(1997-2003) (*.xls)", "*.xls")
            );
            File file = fileChooser.showSaveDialog(vbWindow.getScene().getWindow());
            if (file != null) {
                file = new File(file.getAbsolutePath());
                if (file.getName().endsWith("xls") || file.getName().endsWith("xlsx")) {
                    try {

                        Workbook workbook;
                        if (file.getName().endsWith("xls")) {
                            workbook = new HSSFWorkbook();
                        } else {
                            workbook = new XSSFWorkbook();
                        }

                        Sheet sheet = workbook.createSheet("Compras");
                        sheet.setColumnWidth(5, 20*256);

                        Font font = workbook.createFont();
                        font.setFontHeightInPoints((short) 12);
                        font.setBold(true);
                        font.setColor(HSSFColor.BLACK.index);

                        CellStyle cellStyle = workbook.createCellStyle();
                        cellStyle.setFont(font);
                        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                        String header[] = {"Id", "Fecha", "Cliente", "Comprobante", "Tipo de Compra", "Estado", "Importe"};

                        Row headerRow = sheet.createRow(0);
                        for (int i = 0; i < header.length; i++) {
                            Cell cell = headerRow.createCell(i);
                            cell.setCellStyle(cellStyle);
                            cell.setCellValue(header[i].toUpperCase());

                        }

                        try (FileOutputStream out = new FileOutputStream(file)) {
                            workbook.write(out);
                        }
                        workbook.close();
                        Tools.openFile(file.getAbsolutePath());
                    } catch (IOException ex) {
                        Tools.AlertMessage(vbWindow.getScene().getWindow(), Alert.AlertType.ERROR, "Exportar", "Error al exportar el archivo, intente de nuevo.", false);
                    }
                } else {
                    Tools.AlertMessage(vbWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Exportar", "Elija un formato valido", false);
                }

            }
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
            onEventPdf();
        }
    }

    @FXML
    private void onActionPdf(ActionEvent event) {
        onEventPdf();
    }

    @FXML
    private void onKeyPressedExcel(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventExcel();
        }
    }

    @FXML
    private void onActionExcel(ActionEvent event) {
        onEventExcel();
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

    public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }

}
