package controller.menus;

import controller.tools.Session;
import controller.tools.Tools;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
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
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import model.GlobalADO;

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
    private BarChart<String, Number> bcVentasMes;
    @FXML
    private BarChart<String, Number> bcProductos;
    @FXML
    private PieChart pcCompras;

    private final XYChart.Series ventasSeries = new XYChart.Series<>();

    private final XYChart.Series productosSeries = new XYChart.Series<>();

    private ObservableList<PieChart.Data> datas = FXCollections.observableArrayList(
            new PieChart.Data("Compras al contado", 0),
            new PieChart.Data("Compras al crédito", 0),
            new PieChart.Data("Compras anuladas", 0),
            new PieChart.Data("Compras guardadas", 0)
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
                    ArrayList<String> arrayList = GlobalADO.ReporteInicio(Tools.getDate());
                    lblVentasTotales.setText(Session.MONEDA_SIMBOLO + " " + arrayList.get(0));
                    lblComprasTotales.setText(Session.MONEDA_SIMBOLO + " " + arrayList.get(1));
                    lblArticulo.setText(arrayList.get(2));
                    lblCliente.setText(arrayList.get(3));
                    lblProveedor.setText(arrayList.get(4));
                    lblTrabajador.setText(arrayList.get(5));
                    loadGraphics();
                });
                count = 59;
            }
            
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void initGraphics() {

        ventasSeries.setName("Mayo 2020");
        ventasSeries.getData().add(new XYChart.Data("Semana 1", 0));
        ventasSeries.getData().add(new XYChart.Data("Semana 2", 0));
        ventasSeries.getData().add(new XYChart.Data("Semana 3", 0));
        ventasSeries.getData().add(new XYChart.Data("Semana 4", 0));
        bcVentasMes.getData().add(ventasSeries);

        datas = FXCollections.observableArrayList(
                new PieChart.Data("Compras al contado", 0),
                new PieChart.Data("Compras al crédito", 0),
                new PieChart.Data("Compras anuladas", 0),
                new PieChart.Data("Compras guardadas", 0)
        );
        pcCompras.setData(datas);

        productosSeries.setName("Pollo");
        productosSeries.getData().add(new XYChart.Data("Arroz", 0));
        productosSeries.getData().add(new XYChart.Data("Pollo", 0));
        productosSeries.getData().add(new XYChart.Data("Gaseosa", 0));
        productosSeries.getData().add(new XYChart.Data("Pan", 0));
        bcProductos.getData().add(productosSeries);
    }

    private void loadGraphics() {
        ventasSeries.getData().set(0, new XYChart.Data("Semana 1", new Random().nextInt(101)));
        ventasSeries.getData().set(1, new XYChart.Data("Semana 2", new Random().nextInt(101)));
        ventasSeries.getData().set(2, new XYChart.Data("Semana 3", new Random().nextInt(101)));
        ventasSeries.getData().set(3, new XYChart.Data("Semana 4", new Random().nextInt(101)));

        datas = FXCollections.observableArrayList(
                new PieChart.Data("Compras al contado", new Random().nextInt(101)),
                new PieChart.Data("Compras al crédito", new Random().nextInt(101)),
                new PieChart.Data("Compras anuladas", new Random().nextInt(101)),
                new PieChart.Data("Compras guardadas", new Random().nextInt(101))
        );
        pcCompras.setData(datas);

        productosSeries.getData().set(0, new XYChart.Data("Arroz", new Random().nextInt(101)));
        productosSeries.getData().set(1, new XYChart.Data("Pollo", new Random().nextInt(101)));
        productosSeries.getData().set(2, new XYChart.Data("Gaseosa", new Random().nextInt(101)));
        productosSeries.getData().set(3, new XYChart.Data("Pan", new Random().nextInt(101)));
    }

}
