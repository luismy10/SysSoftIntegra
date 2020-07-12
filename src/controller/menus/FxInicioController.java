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
    private PieChart pcCompras;
    @FXML
    private Text lblVentasPagar;
    @FXML
    private Text lblComprasPagar;

    private final XYChart.Series ventasSeries = new XYChart.Series<>();

    private ObservableList<PieChart.Data> datas = FXCollections.observableArrayList(
            new PieChart.Data("Compras al contado", 0),
            new PieChart.Data("Compras al crédito", 0),
            new PieChart.Data("Compras anuladas", 0),
            new PieChart.Data("Compras guardadas", 0)
    );

    private short count = 30;

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
                    ArrayList<Object> arrayList = GlobalADO.ReporteInicio(Tools.getDate());
                    lblVentasTotales.setText(Session.MONEDA_SIMBOLO + " " + arrayList.get(0));
                    lblComprasTotales.setText(Session.MONEDA_SIMBOLO + " " + arrayList.get(1));
                    lblArticulo.setText((String) arrayList.get(2));
                    lblCliente.setText((String) arrayList.get(3));
                    lblProveedor.setText((String) arrayList.get(4));
                    lblTrabajador.setText((String) arrayList.get(5));

                    loadGraphics((int) arrayList.get(6), (int) arrayList.get(7), (int) arrayList.get(8), (int) arrayList.get(9));

//                     ArrayList<SuministroTB> listaProductos = (ArrayList<SuministroTB>) arrayList.get(10);
                    lblVentasPagar.setText(Tools.roundingValue((int) arrayList.get(11), 0));
                    lblComprasPagar.setText(Tools.roundingValue((int) arrayList.get(12), 0));

                });
                count = 30;
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void initGraphics() {

        ventasSeries.setName(Tools.getDate());
        ventasSeries.getData().add(new XYChart.Data("Semana 1", 0));
        ventasSeries.getData().add(new XYChart.Data("Semana 2", 0));
        ventasSeries.getData().add(new XYChart.Data("Semana 3", 0));
        ventasSeries.getData().add(new XYChart.Data("Semana 4", 0));
        bcVentasMes.getData().add(ventasSeries);

        datas = FXCollections.observableArrayList(
                new PieChart.Data("Producto con cantidades Negativas", 0),
                new PieChart.Data("Producto con cantidades Intermedias", 0),
                new PieChart.Data("Producto con cantidades Necesarias", 0),
                new PieChart.Data("Producto con cantidades Excedentes", 0)
        );
        pcCompras.setData(datas);

    }

    private void loadGraphics(double negativas, double intermedias, double necesarias, double excedentes) {
        ventasSeries.getData().set(0, new XYChart.Data("Semana 1", new Random().nextInt(101)));
        ventasSeries.getData().set(1, new XYChart.Data("Semana 2", new Random().nextInt(101)));
        ventasSeries.getData().set(2, new XYChart.Data("Semana 3", new Random().nextInt(101)));
        ventasSeries.getData().set(3, new XYChart.Data("Semana 4", new Random().nextInt(101)));

        datas = FXCollections.observableArrayList(
                new PieChart.Data("Producto con cantidades Negativas: "+negativas, negativas),
                new PieChart.Data("Producto con cantidades Intermedias: "+intermedias, intermedias),
                new PieChart.Data("Producto con cantidades Necesarias: "+necesarias, necesarias),
                new PieChart.Data("Producto con cantidades Excedentes: "+excedentes, excedentes)
        );
        pcCompras.setData(datas);

    }

}
