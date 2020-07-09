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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
    private BarChart<String, Number> bcVentasMes;
    private BarChart<String, Number> bcProductos;
    @FXML
    private PieChart pcCompras;
    @FXML
    private Text lblCantidadNegativa;
    @FXML
    private Text lblCantidadIntermedia;
    @FXML
    private Text lblCantidadNecesaria;
    @FXML
    private Text lblCantidadExcedente;
    @FXML
    private HBox hbListaProductosVendidos;

    private XYChart.Series ventasSeries = new XYChart.Series<>();

    private ObservableList<PieChart.Data> datas = FXCollections.observableArrayList(
            new PieChart.Data("Compras al contado", 0),
            new PieChart.Data("Compras al crédito", 0),
            new PieChart.Data("Compras anuladas", 0),
            new PieChart.Data("Compras guardadas", 0)
    );

    private short count = 10;

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
                    lblCantidadNegativa.setText("Cantidades Negativas: " + arrayList.get(6));
                    lblCantidadIntermedia.setText("Cantidades Intermedias: " + arrayList.get(7));
                    lblCantidadNecesaria.setText("Cantidades Necesarias: " + arrayList.get(8));
                    lblCantidadExcedente.setText("Cantidades Excedentes: " + arrayList.get(9));

                    ArrayList<SuministroTB> listaProductos = (ArrayList<SuministroTB>) arrayList.get(10);

                    loadGraphics(listaProductos);
                });
                count = 10;
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
                new PieChart.Data("Compras al contado", 0),
                new PieChart.Data("Compras al crédito", 0),
                new PieChart.Data("Compras anuladas", 0),
                new PieChart.Data("Compras guardadas", 0)
        );
        pcCompras.setData(datas);
        
       
        
        VBox vBox = new VBox();
        vBox.getChildren().add(new Label("asdasdt6"));
        vBox.getChildren().add(new Label("asdasdy7"));
        vBox.getChildren().add(new Label("asdasd5"));
        vBox.getChildren().add(new Label("asdasd4"));
        vBox.getChildren().add(new Label("asdasd2"));
        vBox.getChildren().add(new Label("asdasd3"));
        vBox.getChildren().add(new Label("asdasd1"));
        hbListaProductosVendidos.getChildren().add(vBox);
         

    }

    private void loadGraphics(ArrayList<SuministroTB> listaProductos) {
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

    }

}
