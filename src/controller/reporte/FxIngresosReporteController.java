package controller.reporte;

import controller.configuracion.empleados.FxEmpleadosListaController;
import controller.menus.FxPrincipalController;
import controller.tools.FilesRouters;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.awt.Color;
import java.awt.HeadlessException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.IngresoADO;
import model.IngresoTB;
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
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FxIngresosReporteController implements Initializable {

    @FXML
    private VBox vbWindow;
    @FXML
    private DatePicker dpFechaInicial;
    @FXML
    private DatePicker dpFechaFinal;
    @FXML
    private CheckBox cbVendedorSelect;
    @FXML
    private TextField txtVendedores;
    @FXML
    private Button btnVendedor;

    private FxPrincipalController fxPrincipalController;

    private String idVendedor;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.actualDate(Tools.getDate(), dpFechaInicial);
        Tools.actualDate(Tools.getDate(), dpFechaFinal);
        idVendedor = "";
    }

    private void openWindowVendedores() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_EMPLEADO_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxEmpleadosListaController controller = fXMLLoader.getController();
            controller.setInitIngresosReporteController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Elija un vendedor", vbWindow.getScene().getWindow());
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
        ArrayList<IngresoTB> ingresoTBs = IngresoADO.GetResumenIngresos(
                Tools.getDatePicker(dpFechaInicial),
                Tools.getDatePicker(dpFechaFinal),
                cbVendedorSelect.isSelected() ? 0 : 1,
                idVendedor);

        double totalEfectivoIngreso = 0;
        double totalEfectivoSalida = 0;
        double totalTarjetaIngreso = 0;
        double totalTarjetaSalida = 0;

        for (IngresoTB ingresoTB : ingresoTBs) {
            if (ingresoTB.getFormaIngreso().equalsIgnoreCase("EFECTIVO")) {
                if (ingresoTB.getTransaccion().equalsIgnoreCase("COMPRAS")
                        || ingresoTB.getTransaccion().equalsIgnoreCase("EGRESO LIBRE")
                        || ingresoTB.getTransaccion().equalsIgnoreCase("CUENTAS POR PAGAR")) {
                    totalEfectivoIngreso += 0;
                    totalEfectivoSalida += ingresoTB.getEfectivo();
                } else {
                    totalEfectivoIngreso += ingresoTB.getEfectivo();
                    totalEfectivoSalida += 0;
                }
            } else {
                if (ingresoTB.getTransaccion().equalsIgnoreCase("COMPRAS")
                        || ingresoTB.getTransaccion().equalsIgnoreCase("EGRESO LIBRE")
                        || ingresoTB.getTransaccion().equalsIgnoreCase("CUENTAS POR PAGAR")) {
                    totalTarjetaIngreso += 0;
                    totalTarjetaSalida += ingresoTB.getTarjeta();
                } else {
                    totalTarjetaIngreso += ingresoTB.getTarjeta();
                    totalTarjetaSalida += 0;
                }
            }
        }

        InputStream imgInputStreamIcon = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);
        InputStream imgInputStream = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);
        if (Session.COMPANY_IMAGE != null) {
            imgInputStream = new ByteArrayInputStream(Session.COMPANY_IMAGE);
        }
        InputStream dir = getClass().getResourceAsStream("/report/Ingresos.jasper");
        Map map = new HashMap();
        map.put("LOGO", imgInputStream);
        map.put("ICON", imgInputStreamIcon);
        map.put("EMPRESA", Session.COMPANY_RAZON_SOCIAL);
        map.put("DIRECCION", Session.COMPANY_DOMICILIO);
        map.put("TELEFONOCELULAR", "TELÉFONO: " + Session.COMPANY_TELEFONO + " CELULAR: " + Session.COMPANY_CELULAR);
        map.put("EMAIL", "EMAIL: " + Session.COMPANY_EMAIL);
        map.put("FECHAS", "DEL " + Tools.formatDate(Tools.getDatePicker(dpFechaInicial)) + " al " + Tools.formatDate(Tools.getDatePicker(dpFechaFinal)));
        map.put("INGRESO_EFECTIVO", Tools.roundingValue(totalEfectivoIngreso, 2));
        map.put("SALIDA_EFECTIVO", Tools.roundingValue(totalEfectivoSalida, 2));
        map.put("INGRESO_TARJETA", Tools.roundingValue(totalTarjetaIngreso, 2));
        map.put("SALIDA_TARJETA", Tools.roundingValue(totalTarjetaSalida, 2));

        JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JRBeanCollectionDataSource(ingresoTBs));
        return jasperPrint;
    }

    private void openViewVisualizar() {
        try {
            if (!cbVendedorSelect.isSelected() && idVendedor.equalsIgnoreCase("") && txtVendedores.getText().isEmpty()) {
                Tools.AlertMessageWarning(vbWindow, "Reporte General de Movimientos", "Ingrese un vendedor para generar el reporte.");
                btnVendedor.requestFocus();
            } else {

                URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxReportViewController controller = fXMLLoader.getController();
                controller.setFileName("ListaDeMovimientos");
                controller.setJasperPrint(reportGenerate());
                controller.show();
                Stage stage = WindowStage.StageLoader(parent, "Reporte General de Movimientos");
                stage.setResizable(true);
                stage.show();
                stage.requestFocus();
            }

        } catch (HeadlessException | JRException | IOException ex) {
            Tools.AlertMessageError(vbWindow, "Reporte General de Movimientos", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    private void onEventPdf() {
        try {
            if (!cbVendedorSelect.isSelected() && idVendedor.equalsIgnoreCase("") && txtVendedores.getText().isEmpty()) {
                Tools.AlertMessageWarning(vbWindow, "Reporte General de Movimientos", "Ingrese un vendedor para generar el reporte.");
                btnVendedor.requestFocus();
            } else {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
                fileChooser.setTitle("Reporte de Ingresos");
                fileChooser.setInitialFileName("ListaDeVentas");
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
        } catch (HeadlessException | JRException ex) {
            Tools.AlertMessageError(vbWindow, "Reporte General de Movimientos", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    private void onEventExcel() {
        if (!cbVendedorSelect.isSelected() && idVendedor.equalsIgnoreCase("") && txtVendedores.getText().isEmpty()) {
            Tools.AlertMessageWarning(vbWindow, "Reporte General de Movimientos", "Ingrese un vendedor para generar el reporte.");
            btnVendedor.requestFocus();
        } else {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
            fileChooser.setTitle("Reporte de Ingresos");
            fileChooser.setInitialFileName("ListaDeIngresos");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Libro de Excel (*.xlsx)", "*.xlsx"),
                    new FileChooser.ExtensionFilter("Libro de Excel(1997-2003) (*.xls)", "*.xls")
            );
            File file = fileChooser.showSaveDialog(vbWindow.getScene().getWindow());
            if (file != null) {
                file = new File(file.getAbsolutePath());
                if (file.getName().endsWith("xls") || file.getName().endsWith("xlsx")) {
                    try {
                        ArrayList<IngresoTB> ingresoTBs = IngresoADO.GetListaIngresos(
                                Tools.getDatePicker(dpFechaInicial),
                                Tools.getDatePicker(dpFechaFinal),
                                cbVendedorSelect.isSelected() ? 0 : 1,
                                idVendedor
                        );

                        Workbook workbook;
                        if (file.getName().endsWith("xls")) {
                            workbook = new HSSFWorkbook();
                        } else {
                            workbook = new XSSFWorkbook();
                        }

                        Sheet sheet = workbook.createSheet("Ingresos");

                        Font font = workbook.createFont();
                        font.setFontHeightInPoints((short) 12);
                        font.setBold(true);
                        font.setColor(HSSFColor.BLACK.index);

                        CellStyle cellStyleHeader = workbook.createCellStyle();
                        cellStyleHeader.setFont(font);
                        cellStyleHeader.setFillForegroundColor(IndexedColors.AUTOMATIC.getIndex());
                        cellStyleHeader.setAlignment(CellStyle.ALIGN_CENTER);
                        cellStyleHeader.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

                        Row row = sheet.createRow(0);

                        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
                        Cell cellId = row.createCell(0);
                        cellId.setCellStyle(cellStyleHeader);
                        cellId.setCellValue("Id");

                        sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));
                        Cell cellVendedor = row.createCell(1);
                        cellVendedor.setCellStyle(cellStyleHeader);
                        cellVendedor.setCellValue("Vendedor");

                        sheet.addMergedRegion(new CellRangeAddress(0, 1, 2, 2));
                        Cell cellProcedencia = row.createCell(2);
                        cellProcedencia.setCellStyle(cellStyleHeader);
                        cellProcedencia.setCellValue("Procedencia");

                        sheet.addMergedRegion(new CellRangeAddress(0, 1, 3, 3));
                        Cell cellDetalle = row.createCell(3);
                        cellDetalle.setCellStyle(cellStyleHeader);
                        cellDetalle.setCellValue("Detalle");

                        sheet.addMergedRegion(new CellRangeAddress(0, 1, 4, 4));
                        Cell cellFecha = row.createCell(4);
                        cellFecha.setCellStyle(cellStyleHeader);
                        cellFecha.setCellValue("Fecha");

                        sheet.addMergedRegion(new CellRangeAddress(0, 0, 5, 6));
                        Cell cellEfectivo = row.createCell(5);
                        cellEfectivo.setCellStyle(cellStyleHeader);
                        cellEfectivo.setCellValue("Efectivo");

                        sheet.addMergedRegion(new CellRangeAddress(0, 0, 7, 8));
                        Cell cellTarjeta = row.createCell(7);
                        cellTarjeta.setCellStyle(cellStyleHeader);
                        cellTarjeta.setCellValue("Tarjeta");

                        row = sheet.createRow(1);

                        Cell cellIngreso1 = row.createCell(5);
                        cellIngreso1.setCellStyle(cellStyleHeader);
                        cellIngreso1.setCellValue("Ingreso");

                        Cell cellSalida1 = row.createCell(6);
                        cellSalida1.setCellStyle(cellStyleHeader);
                        cellSalida1.setCellValue("Salida");

                        Cell cellIngreso2 = row.createCell(7);
                        cellIngreso2.setCellStyle(cellStyleHeader);
                        cellIngreso2.setCellValue("Ingreso");

                        Cell cellSalida2 = row.createCell(8);
                        cellSalida2.setCellStyle(cellStyleHeader);
                        cellSalida2.setCellValue("Salida");

                        CellStyle cellStyle = workbook.createCellStyle();
                        for (int i = 0; i < ingresoTBs.size(); i++) {

                            row = sheet.createRow(i + 2);
                            Cell cell1 = row.createCell(0);
                            cellStyle = workbook.createCellStyle();
                            cellStyle.setDataFormat(workbook.createDataFormat().getFormat("@"));
                            cell1.setCellStyle(cellStyle);
                            cell1.setCellValue(String.valueOf(i + 1));
                            cell1.setCellType(Cell.CELL_TYPE_STRING);
                            sheet.autoSizeColumn(cell1.getColumnIndex());

                            Cell cell2 = row.createCell(1);
                            cell2.setCellStyle(cellStyle);
                            cell2.setCellValue(ingresoTBs.get(i).getEmpleadoTB().getApellidos() + " " + ingresoTBs.get(i).getEmpleadoTB().getNombres());
                            cell2.setCellType(Cell.CELL_TYPE_STRING);
                            sheet.autoSizeColumn(cell2.getColumnIndex());

                            Cell cell3 = row.createCell(2);
                            cell3.setCellStyle(cellStyle);
                            cell3.setCellValue(ingresoTBs.get(i).getTransaccion());
                            cell3.setCellType(Cell.CELL_TYPE_STRING);
                            sheet.autoSizeColumn(cell3.getColumnIndex());

                            Cell cell4 = row.createCell(3);
                            cell4.setCellStyle(cellStyle);
                            cell4.setCellValue(ingresoTBs.get(i).getDetalle());
                            cell4.setCellType(Cell.CELL_TYPE_STRING);
                            sheet.autoSizeColumn(cell4.getColumnIndex());

                            Cell cell5 = row.createCell(4);
                            cell5.setCellStyle(cellStyle);
                            cell5.setCellValue(ingresoTBs.get(i).getFecha());
                            cell5.setCellType(Cell.CELL_TYPE_STRING);
                            sheet.autoSizeColumn(cell5.getColumnIndex());

                            if (ingresoTBs.get(i).getFormaIngreso().equalsIgnoreCase("EFECTIVO")) {

                                if (ingresoTBs.get(i).getTransaccion().equalsIgnoreCase("COMPRAS")
                                        || ingresoTBs.get(i).getTransaccion().equalsIgnoreCase("EGRESO LIBRE")
                                        || ingresoTBs.get(i).getTransaccion().equalsIgnoreCase("CUENTAS POR PAGAR")) {

                                    Cell cell6 = row.createCell(5);
                                    cellStyle = workbook.createCellStyle();
                                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
                                    cell6.setCellStyle(cellStyle);
                                    cell6.setCellValue(0);
                                    cell6.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    sheet.autoSizeColumn(cell6.getColumnIndex());

                                    Cell cell7 = row.createCell(6);
                                    cellStyle = workbook.createCellStyle();
                                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
                                    cell7.setCellStyle(cellStyle);
                                    cell7.setCellValue(Double.parseDouble(Tools.roundingValue(ingresoTBs.get(i).getMonto(), 2)));
                                    cell7.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    sheet.autoSizeColumn(cell7.getColumnIndex());

                                    Cell cell8 = row.createCell(7);
                                    cellStyle = workbook.createCellStyle();
                                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
                                    cell8.setCellStyle(cellStyle);
                                    cell8.setCellValue(0);
                                    cell8.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    sheet.autoSizeColumn(cell8.getColumnIndex());

                                    Cell cell9 = row.createCell(8);
                                    cellStyle = workbook.createCellStyle();
                                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
                                    cell9.setCellStyle(cellStyle);
                                    cell9.setCellValue(0);
                                    cell9.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    sheet.autoSizeColumn(cell9.getColumnIndex());
                                } else {

                                    Cell cell6 = row.createCell(5);
                                    cellStyle = workbook.createCellStyle();
                                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
                                    cell6.setCellStyle(cellStyle);
                                    cell6.setCellValue(Double.parseDouble(Tools.roundingValue(ingresoTBs.get(i).getMonto(), 2)));
                                    cell6.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    sheet.autoSizeColumn(cell6.getColumnIndex());

                                    Cell cell7 = row.createCell(6);
                                    cellStyle = workbook.createCellStyle();
                                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
                                    cell7.setCellStyle(cellStyle);
                                    cell7.setCellValue(0);
                                    cell7.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    sheet.autoSizeColumn(cell7.getColumnIndex());

                                    Cell cell8 = row.createCell(7);
                                    cellStyle = workbook.createCellStyle();
                                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
                                    cell8.setCellStyle(cellStyle);
                                    cell8.setCellValue(0);
                                    cell8.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    sheet.autoSizeColumn(cell8.getColumnIndex());

                                    Cell cell9 = row.createCell(8);
                                    cellStyle = workbook.createCellStyle();
                                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
                                    cell9.setCellStyle(cellStyle);
                                    cell9.setCellValue(0);
                                    cell9.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    sheet.autoSizeColumn(cell9.getColumnIndex());
                                }

                            } else {

                                if (ingresoTBs.get(i).getTransaccion().equalsIgnoreCase("COMPRAS")
                                        || ingresoTBs.get(i).getTransaccion().equalsIgnoreCase("EGRESO LIBRE")
                                        || ingresoTBs.get(i).getTransaccion().equalsIgnoreCase("CUENTAS POR PAGAR")) {

                                    Cell cell6 = row.createCell(5);
                                    cellStyle = workbook.createCellStyle();
                                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
                                    cell6.setCellStyle(cellStyle);
                                    cell6.setCellValue(0);
                                    cell6.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    sheet.autoSizeColumn(cell6.getColumnIndex());

                                    Cell cell7 = row.createCell(6);
                                    cellStyle = workbook.createCellStyle();
                                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
                                    cell7.setCellStyle(cellStyle);
                                    cell7.setCellValue(0);
                                    cell7.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    sheet.autoSizeColumn(cell7.getColumnIndex());

                                    Cell cell8 = row.createCell(7);
                                    cellStyle = workbook.createCellStyle();
                                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
                                    cell8.setCellStyle(cellStyle);
                                    cell8.setCellValue(0);
                                    cell8.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    sheet.autoSizeColumn(cell8.getColumnIndex());

                                    Cell cell9 = row.createCell(8);
                                    cellStyle = workbook.createCellStyle();
                                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
                                    cell9.setCellStyle(cellStyle);
                                    cell9.setCellValue(Double.parseDouble(Tools.roundingValue(ingresoTBs.get(i).getMonto(), 2)));
                                    cell9.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    sheet.autoSizeColumn(cell9.getColumnIndex());
                                } else {

                                    Cell cell6 = row.createCell(5);
                                    cellStyle = workbook.createCellStyle();
                                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
                                    cell6.setCellStyle(cellStyle);
                                    cell6.setCellValue(0);
                                    cell6.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    sheet.autoSizeColumn(cell6.getColumnIndex());

                                    Cell cell7 = row.createCell(6);
                                    cellStyle = workbook.createCellStyle();
                                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
                                    cell7.setCellStyle(cellStyle);
                                    cell7.setCellValue(0);
                                    cell7.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    sheet.autoSizeColumn(cell7.getColumnIndex());

                                    Cell cell8 = row.createCell(7);
                                    cellStyle = workbook.createCellStyle();
                                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
                                    cell8.setCellStyle(cellStyle);
                                    cell8.setCellValue(Double.parseDouble(Tools.roundingValue(ingresoTBs.get(i).getMonto(), 2)));
                                    cell8.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    sheet.autoSizeColumn(cell8.getColumnIndex());

                                    Cell cell9 = row.createCell(8);
                                    cellStyle = workbook.createCellStyle();
                                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
                                    cell9.setCellStyle(cellStyle);
                                    cell9.setCellValue(0);
                                    cell9.setCellType(Cell.CELL_TYPE_NUMERIC);
                                    sheet.autoSizeColumn(cell9.getColumnIndex());
                                }

                            }

                        }

                        try (FileOutputStream out = new FileOutputStream(file)) {
                            workbook.write(out);
                        }
                        workbook.close();
                        Tools.openFile(file.getAbsolutePath());
                    } catch (IOException ex) {
                        Tools.AlertMessage(vbWindow.getScene().getWindow(), Alert.AlertType.ERROR, "Exportar", "Error al exportar el archivo, intente de nuevo.", false);
                    }
                }
            }
        }
    }

    @FXML
    private void onActionVendedores(ActionEvent event) {
        if (cbVendedorSelect.isSelected()) {
            btnVendedor.setDisable(true);
            txtVendedores.setText("");
            idVendedor = "";
        } else {
            btnVendedor.setDisable(false);
        }
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

    public void setVendorReporte(String idVendedor, String datos) {
        this.idVendedor = idVendedor;
        txtVendedores.setText(datos);
    }

    public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }

}
