package controller.inventario.inventarioinicial;

import controller.tools.Tools;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import model.ArticuloADO;
import model.ArticuloTB;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class FxImportarInventarioController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private Label lblLoad;
    @FXML
    private TableView<ArticuloTB> tvList;
    @FXML
    private TableColumn<ArticuloTB, String> tcId;
    @FXML
    private TableColumn<ArticuloTB, String> tcClave;
    @FXML
    private TableColumn<ArticuloTB, String> tcArticulo;
    @FXML
    private TableColumn<ArticuloTB, String> tcLote;
    @FXML
    private TableColumn<ArticuloTB, String> tcCaducidad;
    @FXML
    private TableColumn<ArticuloTB, Double> tcCompra;
    @FXML
    private TableColumn<ArticuloTB, Double> tcPrecio;
    @FXML
    private TableColumn<ArticuloTB, Double> tcExistencias;
    @FXML
    private Label lblTotaImportadas;
    @FXML
    private Button btnIniciar;
    @FXML
    private Button btnImportar;

    private int count;

    private File file;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcId.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getIdArticulo()));
        tcClave.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getClave()));
        tcArticulo.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getNombreMarca()));
        tcLote.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().isLote() ? "SI" : "NO"
        ));
        tcCaducidad.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getFechaRegistro().get().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))));
        tcCompra.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getCostoCompra()).asObject());
        tcPrecio.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrecioVentaGeneral()).asObject());
        tcExistencias.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getCantidad()).asObject());
        count = 0;
    }

    @FXML
    private void onActionIniciar(ActionEvent event) {
        if (!tvList.getItems().isEmpty()) {
            short value = Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Importar", "¿Esta seguro de continuar?", true);
            if (value == 1) {
//                String result = ArticuloADO.ImportarArticulos(tvList);
//                switch (result) {
//                    case "register":
//                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Importar inventario", "Se ha ingresado el inventario inicial correctamente", false);
//                        break;
//                    default:
//                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Importar inventario", result, false);
//                        break;
//                }
            }
        } else {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Importar", "La lista para iniciar esta vacía", false);
        }

    }

    @FXML
    private void onActionImportar(ActionEvent event) {
        exportData();
    }

    private void exportData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Inventario inicial");
        fileChooser.setInitialFileName("Inventario Inicial");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Libro de Excel (*.xlsx)", "*.xlsx"),
                new FileChooser.ExtensionFilter("Libro de Excel(1997-2003) (*.xls)", "*.xls")
        );
        file = fileChooser.showOpenDialog(window.getScene().getWindow());
        if (file != null) {
            
            file = new File(file.getAbsolutePath());
            if (file.getName().endsWith("xls") || file.getName().endsWith("xlsx")) {

                Service<ObservableList<ArticuloTB>> service = new Service<ObservableList<ArticuloTB>>() {
                    @Override
                    protected Task<ObservableList<ArticuloTB>> createTask() {
                        return new Task<ObservableList<ArticuloTB>>() {
                            @Override
                            protected ObservableList<ArticuloTB> call() throws Exception {
                                ObservableList<ArticuloTB> listImportada = FXCollections.observableArrayList();
                                try {
                                    Workbook workbook = WorkbookFactory.create(file);
                                    Sheet sheet = workbook.getSheetAt(0);
                                    Iterator<Row> iterator = sheet.rowIterator();
                                    int indexRow = -1;

                                    while (iterator.hasNext()) {
                                        indexRow++;
                                        Row row = iterator.next();
                                        Cell cell1 = row.getCell(0);
                                        Cell cell2 = row.getCell(1);
                                        Cell cell3 = row.getCell(2);
                                        Cell cell4 = row.getCell(3);
                                        Cell cell5 = row.getCell(4);
                                        Cell cell6 = row.getCell(5);
                                        Cell cell7 = row.getCell(6);
                                        Cell cell8 = row.getCell(7);
                                        if (indexRow > 0) {
                                            try {
                                                count++;
                                                updateMessage("" + count);
                                                ArticuloTB articuloTB = new ArticuloTB();
                                                articuloTB.setIdArticulo(cell1.getStringCellValue());
                                                articuloTB.setClave(cell2.getStringCellValue());
                                                articuloTB.setNombreMarca(cell3.getStringCellValue());
                                                articuloTB.setLote(cell4.getStringCellValue().equalsIgnoreCase("SI"));
                                                articuloTB.setFechaRegistro(new java.sql.Date(cell5.getDateCellValue().getTime()).toLocalDate());
                                                articuloTB.setCostoCompra(cell6.getNumericCellValue());
                                                articuloTB.setPrecioVentaGeneral(cell7.getNumericCellValue());
                                                articuloTB.setCantidad(cell8.getNumericCellValue());
                                                listImportada.add(articuloTB);
                                            } catch (Exception ex) {
                                                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Importar", "El formato de las celdas no son correctas.", false);
                                                break;
                                            }
                                        }
                                    }
                                } catch (EncryptedDocumentException ex) {
                                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Importar", "Error al importar el archivo, intente de nuevo.", false);
                                } catch (IOException | InvalidFormatException ex) {
                                    Logger.getLogger(FxImportarInventarioController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                return listImportada;
                            }
                        };
                    }
                };

                lblTotaImportadas.textProperty().bind(service.messageProperty());

                service.setOnSucceeded((WorkerStateEvent e) -> {
                    tvList.setItems((ObservableList<ArticuloTB>) service.getValue());
                    lblLoad.setVisible(false);
                    btnImportar.setDisable(false);
                    btnIniciar.setDisable(false);
                    lblTotaImportadas.textProperty().unbind();
                });
                service.setOnFailed((WorkerStateEvent e) -> {
                    lblLoad.setVisible(false);
                    btnImportar.setDisable(false);
                    btnIniciar.setDisable(false);
                    lblTotaImportadas.textProperty().unbind();
                });

                service.setOnScheduled((WorkerStateEvent e) -> {
                    lblLoad.setVisible(true);
                    btnImportar.setDisable(true);
                    btnIniciar.setDisable(true);
                });
                service.restart();

            } else {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Importar", "Elija un formato valido.", false);
            }
        }

    }

}
