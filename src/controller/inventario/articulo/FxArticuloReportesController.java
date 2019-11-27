package controller.inventario.articulo;

import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.awt.HeadlessException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import model.ArticuloADO;
import model.ArticuloTB;
import model.DetalleADO;
import model.DetalleTB;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

public class FxArticuloReportesController implements Initializable {

    @FXML
    private VBox window;
    @FXML
    private Label lblLoad;
    @FXML
    private ComboBox<String> cbUnidadVenta;
    @FXML
    private ComboBox<DetalleTB> cbCategoria;
    @FXML
    private TableView<ArticuloTB> tvList;
    @FXML
    private TableColumn<ArticuloTB, String> tcClave;
    @FXML
    private TableColumn<ArticuloTB, String> tcClaveAlterna;
    @FXML
    private TableColumn<ArticuloTB, String> tcDocument;
    @FXML
    private TableColumn<ArticuloTB, String> tcUnidadVenta;
    @FXML
    private TextField txtTitulo;

    private AnchorPane content;

    private boolean stateRequest;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcClave.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getClave()
        ));
        tcClaveAlterna.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getClaveAlterna()
        ));
        tcDocument.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getNombreMarca()
        ));
        tcUnidadVenta.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getUnidadVenta() == 1 ? "Por Unidad/Pza" : "A Granel(Peso)"
        ));
        stateRequest = true;
        cbUnidadVenta.getItems().addAll("Todos", "Por Unidad/Pza", "A Granel(Peso)");
        cbUnidadVenta.getSelectionModel().select(0);

        cbCategoria.getItems().add(new DetalleTB(new SimpleIntegerProperty(0), new SimpleStringProperty("Todos")));
        DetalleADO.GetDetailId("0006").forEach(e -> {
            cbCategoria.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
        cbCategoria.getSelectionModel().select(0);
    }

    public void fillArticlesTable(int unidadVenta, int categoria) {

        ExecutorService exec = Executors.newCachedThreadPool((Runnable runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ObservableList<ArticuloTB>> task = new Task<ObservableList<ArticuloTB>>() {
            @Override
            public ObservableList<ArticuloTB> call() {
                //return ArticuloADO.ListArticulosCodBar(unidadVenta, categoria);
                return null;
            }
        };

        task.setOnSucceeded((WorkerStateEvent e) -> {
            tvList.setItems((ObservableList<ArticuloTB>) task.getValue());
            lblLoad.setVisible(false);
            stateRequest = true;
        });
        task.setOnFailed((WorkerStateEvent event) -> {
            lblLoad.setVisible(false);
            stateRequest = false;
        });

        task.setOnScheduled((WorkerStateEvent event) -> {
            lblLoad.setVisible(true);
            stateRequest = false;
        });
        exec.execute(task);

        if (!exec.isShutdown()) {
            exec.shutdown();
        }

    }

    public void openWindowReporte(boolean validar, short option) {

        try {
            ArrayList<ArticuloTB> list = new ArrayList();
            for (int i = 0; i < tvList.getItems().size(); i++) {
                list.add(validar
                        ? new ArticuloTB(tvList.getItems().get(i).getClave(), tvList.getItems().get(i).getNombreMarca())
                        : new ArticuloTB(tvList.getItems().get(i).getClaveAlterna(), tvList.getItems().get(i).getNombreMarca(), true));
            }

            Map map = new HashMap();
            map.put("TITLE", txtTitulo.getText());

            String file = "GenerarCodBar.jasper";
            switch (option) {
                case 1:
                    file = "GenerarCodBar.jasper";
                    break;
                case 2:
                    file = "GenerarCodBarCuadriculax2.jasper";
                    break;
                case 3:
                    file = "GenerarCodBarCuadriculax3.jasper";
                    break;
                default:
                    break;
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(FxArticuloReportesController.class.getResourceAsStream(validar ? "/report/" + file : "/report/GenerarClaveAlt.jasper"), map, new JRBeanCollectionDataSource(list));

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setIconImage(new ImageIcon(getClass().getResource(FilesRouters.IMAGE_ICON)).getImage());
            jasperViewer.setTitle("Lista de atículos");
            jasperViewer.setSize(840, 650);
            jasperViewer.setLocationRelativeTo(null);
            jasperViewer.setVisible(true);

        } catch (HeadlessException | JRException | ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            System.out.println("Error al generar el reporte : " + ex);
        }

    }

    private void InitializationTransparentBackground() {
        ObjectGlobal.PANE.setStyle("-fx-background-color: black");
        ObjectGlobal.PANE.setTranslateX(0);
        ObjectGlobal.PANE.setTranslateY(0);
        ObjectGlobal.PANE.setPrefWidth(Session.WIDTH_WINDOW);
        ObjectGlobal.PANE.setPrefHeight(Session.HEIGHT_WINDOW);
        ObjectGlobal.PANE.setOpacity(0.7f);
        content.getChildren().add(ObjectGlobal.PANE);
    }

    private void openWindowArticulos() throws IOException {
        InitializationTransparentBackground();
        URL url = getClass().getResource(FilesRouters.FX_ARTICULO_LISTA);
        FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
        Parent parent = fXMLLoader.load(url.openStream());
        //Controlller here
        FxArticuloListaController controller = fXMLLoader.getController();
        controller.setInitReporteArticuloController(this);
        //
        Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione un Artículo", window.getScene().getWindow());
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setOnHiding((WindowEvent WindowEvent) -> {
            content.getChildren().remove(ObjectGlobal.PANE);
        });
        stage.show();
        controller.loadElements((short)1,"");
    }

    private void openWindowFormatoReporte() throws IOException {
        InitializationTransparentBackground();
        URL url = getClass().getResource(FilesRouters.FX_REPORTE_OPCION);
        FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
        Parent parent = fXMLLoader.load(url.openStream());
        //Controlller here
        FxReporteOpcionController controller = fXMLLoader.getController();
        controller.setInitReporteArticuloController(this);
        //
        Stage stage = WindowStage.StageLoaderModal(parent, "Seleccione el formato del reporte", window.getScene().getWindow());
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setOnHiding((WindowEvent WindowEvent) -> {
            content.getChildren().remove(ObjectGlobal.PANE);
        });
        stage.show();

    }

    @FXML
    private void onKeyPressedArticulo(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowArticulos();
        }
    }

    @FXML
    private void onActionArticulo(ActionEvent event) throws IOException {
        openWindowArticulos();
    }

    @FXML
    private void onKeyPressedGenerar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (stateRequest) {
                switch (cbUnidadVenta.getSelectionModel().getSelectedIndex()) {
                    case 0:
                        fillArticlesTable(0,
                                cbCategoria.getSelectionModel().getSelectedIndex() >= 0
                                ? cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle().get()
                                : 0
                        );
                        break;
                    case 1:
                        fillArticlesTable(1,
                                cbCategoria.getSelectionModel().getSelectedIndex() >= 0
                                ? cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle().get()
                                : 0
                        );
                        break;
                    case 2:
                        fillArticlesTable(2,
                                cbCategoria.getSelectionModel().getSelectedIndex() >= 0
                                ? cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle().get()
                                : 0
                        );
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @FXML
    private void onActionGenerar(ActionEvent event) {
        if (stateRequest) {
            switch (cbUnidadVenta.getSelectionModel().getSelectedIndex()) {
                case 0:
                    fillArticlesTable(0,
                            cbCategoria.getSelectionModel().getSelectedIndex() >= 0
                            ? cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle().get()
                            : 0
                    );
                    break;
                case 1:
                    fillArticlesTable(1,
                            cbCategoria.getSelectionModel().getSelectedIndex() >= 0
                            ? cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle().get()
                            : 0
                    );
                    break;
                case 2:
                    fillArticlesTable(2,
                            cbCategoria.getSelectionModel().getSelectedIndex() >= 0
                            ? cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle().get()
                            : 0
                    );
                    break;
                default:
                    break;
            }
        }
    }

    @FXML
    private void onKeyPressedVizualizarAlterna(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!tvList.getItems().isEmpty()) {
                openWindowReporte(false, (short) 0);
            } else {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Reporte", "La lista está vacía", false);
            }
        }
    }

    @FXML
    private void onActionVizualizarAlterna(ActionEvent event) {
        if (!tvList.getItems().isEmpty()) {
            openWindowReporte(false, (short) 0);
        } else {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Reporte", "La lista está vacía", false);
        }
    }

    @FXML
    private void onKeyPressedVisualizar(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            if (cbUnidadVenta.getSelectionModel().getSelectedIndex() >= 0) {
                if (txtTitulo.getText().isEmpty()) {
                    Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Reporte artículos", "Ingrese el título del documento", false);
                    txtTitulo.requestFocus();
                } else {
                    openWindowFormatoReporte();
                }
            }
        }
    }

    @FXML
    private void onActionVisualizar(ActionEvent event) throws IOException {
        if (cbUnidadVenta.getSelectionModel().getSelectedIndex() >= 0) {
            if (txtTitulo.getText().isEmpty()) {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Reporte artículos", "Ingrese el título del documento", false);
                txtTitulo.requestFocus();
            } else {
                openWindowFormatoReporte();
            }
        }
    }

    @FXML
    private void onKeyPressedQuitar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
                ObservableList<ArticuloTB> articuloSelect, allArticulos;
                allArticulos = tvList.getItems();
                articuloSelect = tvList.getSelectionModel().getSelectedItems();
                articuloSelect.forEach(allArticulos::remove);
            }
        }
    }

    @FXML
    private void onActionQuitar(ActionEvent event) {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            ObservableList<ArticuloTB> articuloSelect, allArticulos;
            allArticulos = tvList.getItems();
            articuloSelect = tvList.getSelectionModel().getSelectedItems();
            articuloSelect.forEach(allArticulos::remove);
        }
    }

    public TableView<ArticuloTB> getTvList() {
        return tvList;
    }

    public void setContent(AnchorPane content) {
        this.content = content;
    }

}
