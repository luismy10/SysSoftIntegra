package controller.reporte;

import controller.tools.Tools;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;

import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import javax.imageio.ImageIO;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

public class FxReportViewController implements Initializable {

    @FXML
    private BorderPane bpWindow;
    @FXML
    private ImageView imageView;
    @FXML
    private Slider zoomLevel;
    protected Node view;
    @FXML
    private Label lblReportPages;
    @FXML
    private TextField txtPage;
    @FXML
    private Button btnFirstPage;
    @FXML
    private Button btnBackPage;
    @FXML
    private Button btnNextPage;
    @FXML
    private Button btnLastPage;

    private JasperPrint jasperPrint;

    private SimpleIntegerProperty currentPage;

    private String reportFilename;

    private Double zoomFactor;

    private double imageHeight;

    private double imageWidth;

    private int reportPages;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentPage = new SimpleIntegerProperty(this, "currentPage", 1);
    }

    public void show() {
        zoomFactor = 1d;
        zoomLevel.setValue(100d);
        imageView.setX(0);
        imageView.setY(0);
        imageHeight = jasperPrint.getPageHeight();
        imageWidth = jasperPrint.getPageWidth();

        zoomLevel.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            zoomFactor = newValue.doubleValue() / 100;
            imageView.setFitHeight(imageHeight * zoomFactor);
            imageView.setFitWidth(imageWidth * zoomFactor);
        });

        if (jasperPrint.getPages().size() > 0) {
            reportPages = jasperPrint.getPages().size();
            lblReportPages.setText("/ " + reportPages);
            disableUnnecessaryButtons(1);
            viewPage(0);
        }
    }

    private WritableImage getImage(int pageNumber) {
        BufferedImage image = null;
        try {
            image = (BufferedImage) JasperPrintManager.printPageToImage(jasperPrint, pageNumber, 2);
        } catch (JRException e) {

        }
        WritableImage fxImage = new WritableImage(jasperPrint.getPageWidth(), jasperPrint.getPageHeight());
        return SwingFXUtils.toFXImage(image, fxImage);
    }

    private void viewPage(int pageNumber) {
        imageView.setFitHeight(imageHeight * zoomFactor);
        imageView.setFitWidth(imageWidth * zoomFactor);
        imageView.setImage(getImage(pageNumber));
    }

    private void renderPage(int pageNumber) {
        setCurrentPage(pageNumber);
        disableUnnecessaryButtons(pageNumber);
        txtPage.setText(Integer.toString(pageNumber));
        viewPage(pageNumber - 1);
    }

    private void disableUnnecessaryButtons(int pageNumber) {
        btnBackPage.setDisable(pageNumber == 1);
        btnFirstPage.setDisable(pageNumber == 1);
        btnNextPage.setDisable(pageNumber == reportPages);
        btnLastPage.setDisable(pageNumber == reportPages);
    }

    @FXML
    private void onActionPrint() {
        try {
            JasperPrintManager.printReport(jasperPrint, true);
        } catch (JRException e) {

        }
    }

    @FXML
    public boolean onActionSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF Document", Arrays.asList("*.pdf", "*.PDF")));
        fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG image", Arrays.asList("*.png", "*.PNG")));
        fileChooser.getExtensionFilters().add(new ExtensionFilter("DOCX Document", Arrays.asList("*.docx", "*.DOCX")));
        fileChooser.getExtensionFilters().add(new ExtensionFilter("XLSX Document", Arrays.asList("*.xlsx", "*.XLSX")));
        fileChooser.getExtensionFilters().add(new ExtensionFilter("HTML Document", Arrays.asList("*.html", "*.HTML")));
        File file = fileChooser.showSaveDialog(bpWindow.getScene().getWindow());
        if (fileChooser.getSelectedExtensionFilter() != null && fileChooser.getSelectedExtensionFilter().getExtensions() != null) {
            try {
                List<String> selectedExtension = fileChooser.getSelectedExtensionFilter().getExtensions();
                if (selectedExtension.contains("*.pdf")) {
                    JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
                } else if (selectedExtension.contains("*.png")) {
                    for (int i = 0; i < jasperPrint.getPages().size(); i++) {
                        String fileNumber = "0000" + Integer.toString(i + 1);
                        fileNumber = fileNumber.substring(fileNumber.length() - 4, fileNumber.length());
                        WritableImage image = getImage(i);
                        String[] fileTokens = file.getAbsolutePath().split("\\.");
                        String filename = "";

                        if (fileTokens.length > 0) {
                            for (int i2 = 0; i2 < fileTokens.length - 1; i2++) {
                                filename = filename + fileTokens[i2] + ((i2 < fileTokens.length - 2) ? "." : "");
                            }
                            filename = filename + fileNumber + "." + fileTokens[fileTokens.length - 1];
                        } else {
                            filename = file.getAbsolutePath() + fileNumber;
                        }
                        System.out.println(filename);
                        File imageFile = new File(filename);
                        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", imageFile);
                        System.out.println(imageFile.getAbsolutePath());
                    }

                } else if (selectedExtension.contains("*.html")) {
                    JasperExportManager.exportReportToHtmlFile(jasperPrint, file.getAbsolutePath());
                } else if (selectedExtension.contains("*.docx")) {
                    JRDocxExporter exporter = new JRDocxExporter();
                    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                    exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, file.getAbsolutePath());
                    exporter.exportReport();
                    System.out.println("docx");
                } else if (selectedExtension.contains("*.xlsx")) {
                    JRXlsxExporter exporter = new JRXlsxExporter();
                    exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
                    exporter.setParameter(JRXlsExporterParameter.OUTPUT_FILE_NAME, file.getAbsolutePath());
                    exporter.exportReport();
                    System.out.println("xlsx");
                }
            } catch (JRException | IOException e) {

            }
        }
        return false;
    }

    @FXML
    private void onActionFirstPage(ActionEvent event) {
        renderPage(1);
    }

    @FXML
    private void onActionBackPage(ActionEvent event) {
        renderPage(getCurrentPage() - 1);
    }

    @FXML
    private void onActionNextPage(ActionEvent event) {
        renderPage(getCurrentPage() + 1);
    }

    @FXML
    private void onActionLastPage(ActionEvent event) {
        renderPage(reportPages);
    }

    @FXML
    private void onActionPage(ActionEvent event) {
        if (Tools.isNumericInteger(txtPage.getText())) {
            int page = Integer.parseInt(txtPage.getText());
            renderPage(((page > 0 && page <= reportPages) ? page : 1));
        } else {
            txtPage.setText(String.valueOf(getCurrentPage()));
        }
    }

    @FXML
    private void onKeyTypedPage(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b')) {
            event.consume();
        }
    }

    public String getReportFilename() {
        return reportFilename;
    }

    public void setReportFilename(String reportFilename) {
        this.reportFilename = reportFilename;
    }

    public void setCurrentPage(int pageNumber) {
        currentPage.set(pageNumber);
    }

    public int getCurrentPage() {
        return currentPage.get();
    }

    public void setJasperPrint(JasperPrint jasperPrint) {
        this.jasperPrint = jasperPrint;
    }

}