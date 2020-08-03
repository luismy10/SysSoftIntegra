package controller.menus;

import controller.inventario.valorinventario.FxListaInventarioController;
import controller.tools.FilesRouters;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.GlobalADO;
import model.SuministroTB;

public class FxInicioController implements Initializable {

    @FXML
    private ScrollPane spWindow;
    @FXML
    private Text lblFechaActual;
    @FXML
    private Text lblActualización;
    @FXML
    private Text lblArticulo;
    @FXML
    private Text lblCliente;
    @FXML
    private Text lblProveedor;
    @FXML
    private Text lblTrabajador;
    @FXML
    private Text lblVentasTotales;
    @FXML
    private Text lblComprasTotales;
    @FXML
    private BarChart<String, Double> bcProductos;
    @FXML
    private PieChart pcInventario;
    @FXML
    private Text lblVentasPagar;
    @FXML
    private Text lblComprasPagar;
    @FXML
    private Text lblNegativos;
    @FXML
    private Text lblIntermedios;
    @FXML
    private Text lblNecesarias;
    @FXML
    private Text lblExcentes;

    private final XYChart.Series productosSeries = new XYChart.Series<>();

    private ObservableList<PieChart.Data> datas = FXCollections.observableArrayList(
            new PieChart.Data("Productos Negativos", 0),
            new PieChart.Data("Productos Intermedios", 0),
            new PieChart.Data("Productos Necesarios", 0),
            new PieChart.Data("Productos Excedentes", 0)
    );

    private short count = 59;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initClock();
        initGraphics();
    }

    private void initClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, (ActionEvent e) -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d ', de' MMMM 'de' yyyy HH:mm:ss");
            lblFechaActual.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            count--;
            Platform.runLater(() -> {
                lblActualización.setText("El dashboard  se va actualizar en " + count + " segundos");
            });
            if (count == 0) {
                Platform.runLater(() -> {
                    ArrayList<Object> arrayList = GlobalADO.DashboardLoad(Tools.getDate());
                    lblVentasTotales.setText(Session.MONEDA_SIMBOLO + " " + arrayList.get(0));
                    lblComprasTotales.setText(Session.MONEDA_SIMBOLO + " " + arrayList.get(1));
                    lblArticulo.setText((String) arrayList.get(2));
                    lblCliente.setText((String) arrayList.get(3));
                    lblProveedor.setText((String) arrayList.get(4));
                    lblTrabajador.setText((String) arrayList.get(5));

                    ArrayList<SuministroTB> listaProductos = (ArrayList<SuministroTB>) arrayList.get(10);

                    loadGraphics((int) arrayList.get(6), (int) arrayList.get(7), (int) arrayList.get(8), (int) arrayList.get(9), listaProductos);

                    lblVentasPagar.setText(Tools.roundingValue((int) arrayList.get(11), 0));
                    lblComprasPagar.setText(Tools.roundingValue((int) arrayList.get(12), 0));

                });
                count = 59;
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void initGraphics() {

        productosSeries.setName(Tools.getDate());
        productosSeries.getData().add(new XYChart.Data("Producto 1", 0));
        productosSeries.getData().add(new XYChart.Data("Producto 2", 0));
        productosSeries.getData().add(new XYChart.Data("Producto 3", 0));
        productosSeries.getData().add(new XYChart.Data("Producto 4", 0));
        productosSeries.getData().add(new XYChart.Data("Producto 5", 0));
        productosSeries.getData().add(new XYChart.Data("Producto 6", 0));
        productosSeries.getData().add(new XYChart.Data("Producto 7", 0));
        productosSeries.getData().add(new XYChart.Data("Producto 8", 0));
        productosSeries.getData().add(new XYChart.Data("Producto 9", 0));
        productosSeries.getData().add(new XYChart.Data("Producto 10", 0));
        bcProductos.getData().add(productosSeries);

        datas = FXCollections.observableArrayList(
                new PieChart.Data("Productos Negativos", 0),
                new PieChart.Data("Productos Intermedios", 0),
                new PieChart.Data("Productos Necesarios", 0),
                new PieChart.Data("Productos Excedentes", 0)
        );
        pcInventario.setData(datas);
    }

    private void loadGraphics(double negativas, double intermedias, double necesarias, double excedentes, ArrayList<SuministroTB> arrayList) {

        if (!arrayList.isEmpty()) {

            productosSeries.getData().set(0, new XYChart.Data(arrayList.get(0) == null ?"Sin producto":arrayList.get(0).getNombreMarca().length()>10?arrayList.get(0).getNombreMarca().substring(0, 9)+"...":arrayList.get(0).getNombreMarca(), arrayList.get(0) == null ? 0 : (int) arrayList.get(0).getCantidad()));
            productosSeries.getData().set(1, new XYChart.Data(arrayList.get(1) == null ?"Sin producto":arrayList.get(1).getNombreMarca().length()>10?arrayList.get(1).getNombreMarca().substring(0, 9)+"...":arrayList.get(1).getNombreMarca(), arrayList.get(1) == null ? 0 : (int) arrayList.get(1).getCantidad()));
            productosSeries.getData().set(2, new XYChart.Data(arrayList.get(2) == null ?"Sin producto":arrayList.get(2).getNombreMarca().length()>10?arrayList.get(2).getNombreMarca().substring(0, 9)+"...":arrayList.get(2).getNombreMarca(), arrayList.get(2) == null ? 0 : (int) arrayList.get(2).getCantidad()));
            productosSeries.getData().set(3, new XYChart.Data(arrayList.get(3) == null ?"Sin producto":arrayList.get(3).getNombreMarca().length()>10?arrayList.get(3).getNombreMarca().substring(0, 9)+"...":arrayList.get(3).getNombreMarca(), arrayList.get(3) == null ? 0 : (int) arrayList.get(3).getCantidad()));
            productosSeries.getData().set(4, new XYChart.Data(arrayList.get(4) == null ?"Sin producto":arrayList.get(4).getNombreMarca().length()>10?arrayList.get(4).getNombreMarca().substring(0, 9)+"...":arrayList.get(4).getNombreMarca(), arrayList.get(4) == null ? 0 : (int) arrayList.get(4).getCantidad()));
            productosSeries.getData().set(5, new XYChart.Data(arrayList.get(5) == null ?"Sin producto":arrayList.get(5).getNombreMarca().length()>10?arrayList.get(5).getNombreMarca().substring(0, 9)+"...":arrayList.get(5).getNombreMarca(), arrayList.get(5) == null ? 0 : (int) arrayList.get(5).getCantidad()));
            productosSeries.getData().set(6, new XYChart.Data(arrayList.get(6) == null ?"Sin producto":arrayList.get(6).getNombreMarca().length()>10?arrayList.get(6).getNombreMarca().substring(0, 9)+"...":arrayList.get(6).getNombreMarca(), arrayList.get(6) == null ? 0 : (int) arrayList.get(6).getCantidad()));
            productosSeries.getData().set(7, new XYChart.Data(arrayList.get(7) == null ?"Sin producto":arrayList.get(7).getNombreMarca().length()>10?arrayList.get(7).getNombreMarca().substring(0, 9)+"...":arrayList.get(7).getNombreMarca(), arrayList.get(7) == null ? 0 : (int) arrayList.get(7).getCantidad()));
            productosSeries.getData().set(8, new XYChart.Data(arrayList.get(8) == null ?"Sin producto":arrayList.get(8).getNombreMarca().length()>10?arrayList.get(8).getNombreMarca().substring(0, 9)+"...":arrayList.get(8).getNombreMarca(), arrayList.get(8) == null ? 0 : (int) arrayList.get(8).getCantidad()));
            productosSeries.getData().set(9, new XYChart.Data(arrayList.get(9) == null ?"Sin producto":arrayList.get(9).getNombreMarca().length()>10?arrayList.get(9).getNombreMarca().substring(0, 9)+"...":arrayList.get(9).getNombreMarca(), arrayList.get(9) == null ? 0 : (int) arrayList.get(9).getCantidad()));

        } else {
            for (int i = 0; i < 10; i++) {
                productosSeries.getData().set(i, new XYChart.Data("Producto " + (i + 1), 0));
            }
        }

        lblNegativos.setText("" + negativas);
        lblIntermedios.setText("" + intermedias);
        lblNecesarias.setText("" + necesarias);
        lblExcentes.setText("" + excedentes);

        datas = FXCollections.observableArrayList(
                new PieChart.Data("Productos Negativos", negativas),
                new PieChart.Data("Productos Intermedios", intermedias),
                new PieChart.Data("Productos Necesarios", necesarias),
                new PieChart.Data("Productos Excedentes", excedentes)
        );
        pcInventario.setData(datas);
    }

    private void onEventInventario(short existencia) {
        try {
            // ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_LISTAR_INVENTARIO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxListaInventarioController controller = fXMLLoader.getController();
            controller.loadData(existencia);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Inventario general", spWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding((w) -> {
                // vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onKeyPressedNegativos(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventInventario((short) 1);
        }
    }

    @FXML
    private void onActionNegativos(ActionEvent event) {
        onEventInventario((short) 1);
    }

    @FXML
    private void onKeyPressedIntermedios(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventInventario((short) 2);
        }
    }

    @FXML
    private void onActionIntermedios(ActionEvent event) {
        onEventInventario((short) 2);
    }

    @FXML
    private void onKeyPressedNecesarios(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventInventario((short) 3);
        }

    }

    @FXML
    private void onActionNecesarios(ActionEvent event) {
        onEventInventario((short) 3);
    }

    @FXML
    private void onKeyPressedExcedentes(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventInventario((short) 4);
        }

    }

    @FXML
    private void onActionExcedentes(ActionEvent event) {
        onEventInventario((short) 4);
    }

}
