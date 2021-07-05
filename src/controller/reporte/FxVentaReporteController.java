package controller.reporte;

import controller.configuracion.empleados.FxEmpleadosListaController;
import controller.contactos.clientes.FxClienteListaController;
import controller.menus.FxPrincipalController;
import controller.tools.FilesRouters;
import controller.tools.Session;
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
import javafx.scene.control.ComboBox;
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
import model.TipoDocumentoADO;
import model.TipoDocumentoTB;
import model.VentaADO;
import model.VentaTB;
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
            controller.setInitVentaReporteController(this);
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
            controller.setInitVentaReporteController(this);
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
        ArrayList<VentaTB> list = VentaADO.GetReporteGenetalVentasLibres(
                Tools.getDatePicker(dpFechaInicial),
                Tools.getDatePicker(dpFechaFinal),
                cbDocumentosSeleccionar.isSelected()? 0: cbDocumentos.getSelectionModel().getSelectedItem().getIdTipoDocumento(),
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

    private void onEventPdf() {
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
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
                fileChooser.setTitle("Reporte de Ventas");
                fileChooser.setInitialFileName("ListaDeVentas");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("PDF Documento", Arrays.asList("*.pdf", "*.PDF"))
                );
                File file = fileChooser.showSaveDialog(window.getScene().getWindow());
                if (file != null) {
                    file = new File(file.getAbsolutePath());
                    if (file.getName().endsWith("pdf") || file.getName().endsWith("PDF")) {
                        JasperExportManager.exportReportToPdfFile(reportGenerate(), file.getAbsolutePath());
                    }
                }
            }
        } catch (JRException ex) {
            Tools.AlertMessageError(window, "Reporte General de Ventas", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    private void onEventExcel() {
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
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
            fileChooser.setTitle("Reporte de Ventas");
            fileChooser.setInitialFileName("ListaDeVentas");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Libro de Excel (*.xlsx)", "*.xlsx"),
                    new FileChooser.ExtensionFilter("Libro de Excel(1997-2003) (*.xls)", "*.xls")
            );
            File file = fileChooser.showSaveDialog(window.getScene().getWindow());
            if (file != null) {
                file = new File(file.getAbsolutePath());
                if (file.getName().endsWith("xls") || file.getName().endsWith("xlsx")) {
                    try {

                        ArrayList<VentaTB> list = VentaADO.GetReporteGenetalVentasLibres(
                                Tools.getDatePicker(dpFechaInicial),
                                Tools.getDatePicker(dpFechaFinal),
                                cbDocumentosSeleccionar.isSelected() ? 0 : cbDocumentos.getSelectionModel().getSelectedItem().getIdTipoDocumento(),
                                idCliente,
                                idEmpleado,
                                cbTipoPagoSeleccionar.isSelected() ? 0 : rbContado.isSelected() ? 1 : 2);

                        Workbook workbook;
                        if (file.getName().endsWith("xls")) {
                            workbook = new HSSFWorkbook();
                        } else {
                            workbook = new XSSFWorkbook();
                        }

                        Sheet sheet = workbook.createSheet("Ventas");

                        Font font = workbook.createFont();
                        font.setFontHeightInPoints((short) 12);
                        font.setBold(true);
                        font.setColor(HSSFColor.BLACK.index);

                        CellStyle cellStyleHeader = workbook.createCellStyle();
                        cellStyleHeader.setFont(font);
                        cellStyleHeader.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                        String header[] = {"Id", "Fecha", "Documento", "Cliente", "Comprobante", "Serie", "Numeración", "Tipo de Venta", "Estado", "Importe"};

                        Row headerRow = sheet.createRow(0);
                        for (int i = 0; i < header.length; i++) {
                            Cell cell = headerRow.createCell(i);
                            cell.setCellStyle(cellStyleHeader);
                            cell.setCellValue(header[i].toUpperCase());

                        }
                        
                        CellStyle cellStyle = workbook.createCellStyle();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getEstadoName().equalsIgnoreCase("ANULADO") || list.get(i).getNotaCreditoTB() != null) {
                                Font fontRed = workbook.createFont();
                                fontRed.setColor(HSSFColor.RED.index);
                                cellStyle = workbook.createCellStyle();
                                cellStyle.setFont(fontRed);
                                cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                            } else {
                                Font fontBlack = workbook.createFont();
                                fontBlack.setColor(HSSFColor.BLACK.index);
                                fontBlack.setBold(false);
                                cellStyle.setFont(fontBlack);
                            }

                            Row row = sheet.createRow(i + 1);
                            Cell cell1 = row.createCell(0);
                            cell1.setCellStyle(cellStyle);
                            cell1.setCellValue(String.valueOf(i + 1));
                            cell1.setCellType(Cell.CELL_TYPE_STRING);
                            sheet.autoSizeColumn(cell1.getColumnIndex());

                            Cell cell2 = row.createCell(1);
                            cell2.setCellStyle(cellStyle);
                            cell2.setCellValue(String.valueOf(list.get(i).getFechaVenta()));
                            cell2.setCellType(Cell.CELL_TYPE_STRING);
                            sheet.autoSizeColumn(cell2.getColumnIndex());

                            Cell cell3 = row.createCell(2);
                            cell3.setCellStyle(cellStyle);
                            cell3.setCellValue(String.valueOf(list.get(i).getClienteTB().getNumeroDocumento()));
                            cell3.setCellType(Cell.CELL_TYPE_STRING);
                            sheet.autoSizeColumn(cell3.getColumnIndex());

                            Cell cell4 = row.createCell(3);
                            cell4.setCellStyle(cellStyle);
                            cell4.setCellValue(String.valueOf(list.get(i).getClienteTB().getInformacion()));
                            cell4.setCellType(Cell.CELL_TYPE_STRING);
                            sheet.autoSizeColumn(cell4.getColumnIndex());

                            Cell cell5 = row.createCell(4);
                            cell5.setCellStyle(cellStyle);
                            cell5.setCellValue(list.get(i).getComprobanteName());
                            cell5.setCellType(Cell.CELL_TYPE_STRING);
                            sheet.autoSizeColumn(cell5.getColumnIndex());

                            Cell cell6 = row.createCell(5);
                            cell6.setCellStyle(cellStyle);
                            cell6.setCellValue(list.get(i).getSerie());
                            cell6.setCellType(Cell.CELL_TYPE_STRING);
                            sheet.autoSizeColumn(cell6.getColumnIndex());

                            Cell cell7 = row.createCell(6);
                            cell7.setCellStyle(cellStyle);
                            cell7.setCellValue(list.get(i).getNumeracion());
                            cell7.setCellType(Cell.CELL_TYPE_STRING);
                            sheet.autoSizeColumn(cell7.getColumnIndex());

                            Cell cell8 = row.createCell(7);
                            cell8.setCellStyle(cellStyle);
                            cell8.setCellValue(list.get(i).getTipoName());
                            cell8.setCellType(Cell.CELL_TYPE_STRING);
                            sheet.autoSizeColumn(cell8.getColumnIndex());

                            Cell cell9 = row.createCell(8);
                            cell9.setCellStyle(cellStyle);
                            cell9.setCellValue(list.get(i).getEstadoName());
                            cell9.setCellType(Cell.CELL_TYPE_STRING);
                            sheet.autoSizeColumn(cell9.getColumnIndex());

                            Cell cell10 = row.createCell(9);
                            cellStyle = workbook.createCellStyle();
                            cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
                            cell10.setCellStyle(cellStyle);
                            cell10.setCellValue(Double.parseDouble(Tools.roundingValue(list.get(i).getTotal(), 2)));
                            cell10.setCellType(Cell.CELL_TYPE_NUMERIC);
                            sheet.autoSizeColumn(cell10.getColumnIndex());
                        }
                        try (FileOutputStream out = new FileOutputStream(file)) {
                            workbook.write(out);
                        }
                        workbook.close();
                        Tools.openFile(file.getAbsolutePath());

                    } catch (IOException ex) {
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Exportar", "Error al exportar el archivo, intente de nuevo.", false);
                    }

                } else {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Exportar", "Elija un formato valido", false);
                }
            }
        }
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
