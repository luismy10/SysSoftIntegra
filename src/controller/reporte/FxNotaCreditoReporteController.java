package controller.reporte;

import controller.contactos.clientes.FxClienteListaController;
import controller.menus.FxPrincipalController;
import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
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
import model.NotaCreditoADO;
import model.NotaCreditoTB;
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

public class FxNotaCreditoReporteController implements Initializable {

    @FXML
    private VBox vbWindow;
    @FXML
    private DatePicker dpFechaInicial;
    @FXML
    private DatePicker dpFechaFinal;
    @FXML
    private CheckBox cbClientes;
    @FXML
    private TextField txtClientes;
    @FXML
    private Button btnClientes;

    private FxPrincipalController fxPrincipalController;

    private String idCliente;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.actualDate(Tools.getDate(), dpFechaInicial);
        Tools.actualDate(Tools.getDate(), dpFechaFinal);
        idCliente = "";
    }

    private void openWindowClientes() {
        try {
            fxPrincipalController.openFondoModal();
            URL url = getClass().getResource(FilesRouters.FX_CLIENTE_LISTA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxClienteListaController controller = fXMLLoader.getController();
            controller.setInitNotaCreditoReporteController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Elija un cliente", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> fxPrincipalController.closeFondoModal());
            stage.show();
            controller.fillCustomersTable("");
        } catch (IOException ex) {
            System.out.println("Nota Credito reporte controller:" + ex.getLocalizedMessage());
        }
    }

    private JasperPrint reportGenerate() throws JRException {
        ArrayList<NotaCreditoTB> ingresoTBs = NotaCreditoADO.GetReporteNotaCredito(
                Tools.getDatePicker(dpFechaInicial),
                Tools.getDatePicker(dpFechaFinal),
                cbClientes.isSelected() ? 0 : 1,
                idCliente);

        InputStream dir = getClass().getResourceAsStream("/report/NotaCreditoGeneral.jasper");
        Map map = new HashMap();
        map.put("CLIENTE", cbClientes.isSelected() ? "TODOS" : txtClientes.getText());
        map.put("PERIODO", "DEL " + Tools.formatDate(Tools.getDatePicker(dpFechaInicial)) + " al " + Tools.formatDate(Tools.getDatePicker(dpFechaFinal)));

        JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JRBeanCollectionDataSource(ingresoTBs));
        return jasperPrint;
    }

    private void openViewVisualizar() {
        try {
            if (!cbClientes.isSelected() && idCliente.equalsIgnoreCase("") && txtClientes.getText().isEmpty()) {
                Tools.AlertMessageWarning(vbWindow, "Reporte General de Notas de Credito", "Ingrese un cliente para generar el reporte.");
                btnClientes.requestFocus();
            } else {

                URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxReportViewController controller = fXMLLoader.getController();
                controller.setFileName("ListaDeNotaCredito");
                controller.setJasperPrint(reportGenerate());
                controller.show();
                Stage stage = WindowStage.StageLoader(parent, "Reporte General de Notas de Credito");
                stage.setResizable(true);
                stage.show();
                stage.requestFocus();
            }

        } catch (Exception ex) {
            Tools.println(ex.getLocalizedMessage());
            //Tools.AlertMessageError(vbWindow, "Reporte General de Nota de Credito", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    private void onEventPdf() {
        try {
            if (!cbClientes.isSelected() && idCliente.equalsIgnoreCase("") && txtClientes.getText().isEmpty()) {
                Tools.AlertMessageWarning(vbWindow, "Reporte General de Notas de Credito", "Ingrese un cliente para generar el reporte.");
                btnClientes.requestFocus();
            } else {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
                fileChooser.setTitle("Reporte de Notas de Crédito");
                fileChooser.setInitialFileName("ListaDeNotaCredito");
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
        } catch (Exception ex) {
            Tools.println(ex.getLocalizedMessage());
            //Tools.AlertMessageError(vbWindow, "Reporte General de Notas de Credito", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    private void onEventExcel() {
        if (!cbClientes.isSelected() && idCliente.equalsIgnoreCase("") && txtClientes.getText().isEmpty()) {
            Tools.AlertMessageWarning(vbWindow, "Reporte General de Notas de Credito", "Ingrese un cliente para generar el reporte.");
            btnClientes.requestFocus();
        } else {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
            fileChooser.setTitle("Reporte de Nota de Credito");
            fileChooser.setInitialFileName("ListaDeNotasCredito");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Libro de Excel (*.xlsx)", "*.xlsx"),
                    new FileChooser.ExtensionFilter("Libro de Excel(1997-2003) (*.xls)", "*.xls")
            );
            File file = fileChooser.showSaveDialog(vbWindow.getScene().getWindow());
            if (file != null) {
                file = new File(file.getAbsolutePath());
                if (file.getName().endsWith("xls") || file.getName().endsWith("xlsx")) {
                    try {

                        ArrayList<NotaCreditoTB> notaCreditoTBs = NotaCreditoADO.GetReporteNotaCredito(
                                Tools.getDatePicker(dpFechaInicial),
                                Tools.getDatePicker(dpFechaFinal),
                                cbClientes.isSelected() ? 0 : 1,
                                idCliente);

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
                        cellStyleHeader.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                        String header[] = {"Id", "N° Documento", "Cliente", "Serie", "Numeracion", "Total"};

                        Row headerRow = sheet.createRow(0);
                        for (int i = 0; i < header.length; i++) {
                            Cell cell = headerRow.createCell(i);
                            cell.setCellStyle(cellStyleHeader);
                            cell.setCellValue(header[i].toUpperCase());
                        }

                        CellStyle cellStyle = workbook.createCellStyle();
                        for (int i = 0; i < notaCreditoTBs.size(); i++) {
                            Row row = sheet.createRow(i + 1);
                            Cell cell1 = row.createCell(0);
                            cell1.setCellStyle(cellStyle);
                            cell1.setCellValue(String.valueOf(i + 1));
                            cell1.setCellType(Cell.CELL_TYPE_STRING);
                            sheet.autoSizeColumn(cell1.getColumnIndex());

                            Cell cell2 = row.createCell(1);
                            cell2.setCellStyle(cellStyle);
                            cell2.setCellValue(notaCreditoTBs.get(i).getClienteTB().getNumeroDocumento());
                            cell2.setCellType(Cell.CELL_TYPE_STRING);
                            sheet.autoSizeColumn(cell2.getColumnIndex());

                            Cell cell3 = row.createCell(2);
                            cell3.setCellStyle(cellStyle);
                            cell3.setCellValue(notaCreditoTBs.get(i).getClienteTB().getInformacion());
                            cell3.setCellType(Cell.CELL_TYPE_STRING);
                            sheet.autoSizeColumn(cell3.getColumnIndex());

                            Cell cell4 = row.createCell(3);
                            cell4.setCellStyle(cellStyle);
                            cell4.setCellValue(notaCreditoTBs.get(i).getSerie());
                            cell4.setCellType(Cell.CELL_TYPE_STRING);
                            sheet.autoSizeColumn(cell4.getColumnIndex());

                            Cell cell5 = row.createCell(4);
                            cell5.setCellStyle(cellStyle);
                            cell5.setCellValue(notaCreditoTBs.get(i).getNumeracion());
                            cell5.setCellType(Cell.CELL_TYPE_STRING);
                            sheet.autoSizeColumn(cell5.getColumnIndex());
                            
                             Cell cell6 = row.createCell(5);
                            cellStyle = workbook.createCellStyle();
                            cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
                            cell6.setCellStyle(cellStyle);
                            cell6.setCellValue(Double.parseDouble(Tools.roundingValue(notaCreditoTBs.get(i).getImporteNeto(), 2)));
                            cell6.setCellType(Cell.CELL_TYPE_NUMERIC);
                            sheet.autoSizeColumn(cell6.getColumnIndex());
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
    private void onActionCbClientes(ActionEvent event) {
        if (cbClientes.isSelected()) {
            btnClientes.setDisable(true);
            txtClientes.setText("");
            idCliente = "";
        } else {
            btnClientes.setDisable(false);
        }
    }

    @FXML
    private void onKeyPressedClientes(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowClientes();
        }
    }

    public void setClienteVentaReporte(String idCliente, String datos) {
        this.idCliente = idCliente;
        txtClientes.setText(datos);
    }

    public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }

    @FXML
    private void onActionClientes(ActionEvent event) {
    }

}
