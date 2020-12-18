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
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.GlobalADO;
import model.SuministroTB;
import org.controlsfx.control.Notifications;

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
    @FXML
    private VBox vbProductoMasVendidos;

    private ObservableList<PieChart.Data> datas = FXCollections.observableArrayList(
            new PieChart.Data("Productos Negativos", 0),
            new PieChart.Data("Productos Intermedios", 0),
            new PieChart.Data("Productos Necesarios", 0),
            new PieChart.Data("Productos Excedentes", 0)
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
                    // notificationState("Estado del producto","Tiene 15 dás para su prueba, despues de ello\n se va bloquear el producto, gracias por elegirnos.","warning_large.png",Pos.TOP_RIGHT);
                    //notificationState("Estado del inventario","Tiene un total de "+((int) arrayList.get(6))+" producto negativos,\n actualize su inventario por favor.","warning_large.png",Pos.TOP_RIGHT);
                    //  notificationState("SysSoftIntegra","Usa la AppSysSoftIntegra para realizar consular\n en tiempo real de sus tiendas.","logo.png",Pos.TOP_LEFT);
                });
                count = 59;
            }
        }, 0, 1, TimeUnit.SECONDS);

    }

    public void initGraphics() {
        datas = FXCollections.observableArrayList(
                new PieChart.Data("Productos Negativos", 0),
                new PieChart.Data("Productos Intermedios", 0),
                new PieChart.Data("Productos Necesarios", 0),
                new PieChart.Data("Productos Excedentes", 0)
        );
        pcInventario.setData(datas);
    }

    private void loadGraphics(double negativas, double intermedias, double necesarias, double excedentes, ArrayList<SuministroTB> arrayList) {
        lblNegativos.setText("" + Tools.roundingValue(negativas, 0));
        lblIntermedios.setText("" + Tools.roundingValue(intermedias, 0));
        lblNecesarias.setText("" + Tools.roundingValue(necesarias, 0));
        lblExcentes.setText("" + Tools.roundingValue(excedentes, 0));

        datas = FXCollections.observableArrayList(
                new PieChart.Data("Productos Negativos", negativas),
                new PieChart.Data("Productos Intermedios", intermedias),
                new PieChart.Data("Productos Necesarios", necesarias),
                new PieChart.Data("Productos Excedentes", excedentes)
        );
        pcInventario.setData(datas);
        
        vbProductoMasVendidos.getChildren().clear();
        arrayList.forEach(e -> {        
            productoModel(e.getNombreMarca(),Tools.roundingValue(e.getPrecioVentaGeneral(),2),Tools.roundingValue(e.getCantidad(),2));
        });
    }

    private void productoModel(String product,String price,String quantity) {        

        HBox hBoxDetail = new HBox();
        hBoxDetail.setStyle("-fx-border-color: #cccccc;-fx-border-width: 0px 0px 1px 0px;");

        HBox hBoxImage = new HBox();
        HBox.setHgrow(hBoxImage, Priority.SOMETIMES);
        hBoxImage.setStyle("-fx-padding: 0.6666666666666666em;");
        ImageView imageView = new ImageView(new Image("/view/image/noimage.jpg"));
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(false);
        hBoxImage.getChildren().add(imageView);

        HBox hBoxContent = new HBox();
        HBox.setHgrow(hBoxContent, Priority.ALWAYS);

        VBox vBoxProduct = new VBox();
        HBox.setHgrow(vBoxProduct, Priority.ALWAYS);
        vBoxProduct.setAlignment(Pos.CENTER_LEFT);
        vBoxProduct.setStyle("-fx-padding: 0.6666666666666666em;");
        Label lblProducto = new Label(product);
        lblProducto.setStyle("-fx-text-fill:#020203;");
        lblProducto.getStyleClass().add("labelRobotoBold15");
        Label lblPrice = new Label("Precio: "+price);
        lblPrice.setStyle("-fx-text-fill:#545050;");
        lblPrice.getStyleClass().add("labelRoboto13");
        lblPrice.setMinWidth(Control.USE_COMPUTED_SIZE);
        lblPrice.setMinHeight(Control.USE_COMPUTED_SIZE);
        lblPrice.setPrefWidth(Control.USE_COMPUTED_SIZE);
        lblPrice.setPrefHeight(Control.USE_COMPUTED_SIZE);
        lblPrice.setMaxWidth(Control.USE_COMPUTED_SIZE);
        lblPrice.setMaxHeight(Control.USE_COMPUTED_SIZE);
        vBoxProduct.getChildren().addAll(lblProducto, lblPrice);

        VBox vBoxQuantity = new VBox();
        HBox.setHgrow(vBoxQuantity, Priority.SOMETIMES);
        vBoxQuantity.setAlignment(Pos.CENTER_RIGHT);
        vBoxQuantity.setStyle("-fx-padding:0.6666666666666666em;");
        Label lblQuantity = new Label("Cantidad: "+quantity);
        lblQuantity.setStyle("-fx-background-color: #0766cc;-fx-text-fill: #ffffff;-fx-padding: 10px 15px;-fx-border-radius: 0.25em;-fx-border-color: #0766cc;-fx-background-radius: 0.25em;");
        lblQuantity.getStyleClass().add("labelRoboto13");
        lblQuantity.setMinWidth(Control.USE_COMPUTED_SIZE);
        lblQuantity.setMinHeight(Control.USE_COMPUTED_SIZE);
        lblQuantity.setPrefWidth(Control.USE_COMPUTED_SIZE);
        lblQuantity.setPrefHeight(Control.USE_COMPUTED_SIZE);
        lblQuantity.setMaxWidth(Control.USE_COMPUTED_SIZE);
        lblQuantity.setMaxHeight(Control.USE_COMPUTED_SIZE);
        vBoxQuantity.getChildren().add(lblQuantity);
        hBoxContent.getChildren().addAll(vBoxProduct, vBoxQuantity);

        hBoxDetail.getChildren().addAll(hBoxImage, hBoxContent);
        vbProductoMasVendidos.getChildren().add(hBoxDetail);
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
            Stage stage = WindowStage.StageLoader(parent, "Inventario general");
            stage.setResizable(true);
            stage.sizeToScene();
            stage.show();
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void notificationState(String title, String message, String url, Pos pos) {
        Image image = new Image("/view/image/" + url);
        Notifications notifications = Notifications.create()
                .title(title)
                .text(message)
                .graphic(new ImageView(image))
                .hideAfter(Duration.seconds(5))
                .position(pos)
                .onAction(n -> {
                    Tools.println(n);
                });
        notifications.darkStyle();
        notifications.show();
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
