package controller.operaciones.cortecaja;

import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.CajaADO;
import model.CajaTB;
import model.MovimientoCajaTB;

public class FxCajaConsultasController implements Initializable {

    @FXML
    private ScrollPane window;
    @FXML
    private Label lblLoad;
    @FXML
    private GridPane gpList;
    @FXML
    private Label lblInicoTurno;
    @FXML
    private Label lblFinTurno;
    @FXML
    private Label lblMontoBase;
    @FXML
    private Label lblBase;
    @FXML
    private Label lblVentaEfectivo;
    @FXML
    private Label lblVentaTarjeta;
    @FXML
    private Label lblIngresosEfectivo;
    @FXML
    private Label lblRetirosEfectivo;
    @FXML
    private Label lblContado;
    @FXML
    private Label lblCanculado;
    @FXML
    private Label lblDiferencia;
    @FXML
    private Label lblTotal;
    
    private AnchorPane windowinit;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void loadDataCorteCaja(String idCaja) {

        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task< ArrayList<Object>> task = new Task< ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                return CajaADO.ListarMovimientoPorById(idCaja);
            }
        };

        task.setOnSucceeded(e -> {
            ArrayList<Object> objects = task.getValue();
            if (!objects.isEmpty()) {
                if (objects.get(0) != null && objects.get(1) != null && objects.get(2) != null) {
                    CajaTB cajaTB = (CajaTB) objects.get(0);
                    ArrayList<Double> arrayList = (ArrayList<Double>) objects.get(1);

                    lblInicoTurno.setText(cajaTB.getFechaApertura() + " " + cajaTB.getHoraApertura());
                    lblFinTurno.setText(cajaTB.getFechaCierre()+ " " + cajaTB.getHoraCierre());
                    lblMontoBase.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(0), 2));
                    lblBase.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(0), 2));

                    lblContado.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(cajaTB.getContado(), 2));
                    lblCanculado.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(cajaTB.getCalculado(), 2));
                    lblDiferencia.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(cajaTB.getDiferencia(), 2));
                    
                    lblVentaEfectivo.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(1), 2));
                    lblVentaTarjeta.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(2), 2));
                    lblIngresosEfectivo.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(3), 2));
                    lblRetirosEfectivo.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(4), 2));
                    lblTotal.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue((arrayList.get(0) + arrayList.get(1) + arrayList.get(3)) - arrayList.get(4), 2));

                    fillVentasDetalleTable((ArrayList<MovimientoCajaTB>) objects.get(2));
                } else {
                    Tools.AlertMessageError(window, "Corte de caja", "No se pudo realizar la petición por problemas de conexión, intente nuevamente.");
                }
            } else {
                Tools.AlertMessageError(window, "Corte de caja", "No se pudo realizar la petición por problemas de conexión, intente nuevamente.");
            }
            lblLoad.setVisible(false);
        });

        task.setOnFailed(e -> {
            Tools.AlertMessageError(window, "Corte de caja", "No se pudo realizar la petición por problemas de conexión, intente nuevamente.");
            lblLoad.setVisible(false);
        });

        task.setOnScheduled(e -> {
            lblLoad.setVisible(true);
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }

    }

    private void fillVentasDetalleTable(ArrayList<MovimientoCajaTB> arrList) {
        for (int i = 0; i < arrList.size(); i++) {
            gpList.add(addElementGridPane("l1" + (i + 1), "" + arrList.get(i).getId(), Pos.CENTER_LEFT, "#020203"), 0, (i + 1));
            gpList.add(addElementGridPane("l2" + (i + 1), arrList.get(i).getFechaMovimiento() + "\n" + arrList.get(i).getHoraMovimiento(), Pos.CENTER_LEFT, "#020203"), 1, (i + 1));
            gpList.add(addElementGridPane("l3" + (i + 1), arrList.get(i).getComentario(), Pos.CENTER_LEFT, "#020203"), 2, (i + 1));
            gpList.add(addElementGridPane("l4" + (i + 1),
                    arrList.get(i).getTipoMovimiento() == 1 ? "MONTO INICIAL" : arrList.get(i).getTipoMovimiento() == 2 ? "VENTA CON EFECTIVO"
                    : arrList.get(i).getTipoMovimiento() == 3 ? "VENTA CON TARJETA"
                    : arrList.get(i).getTipoMovimiento() == 4 ? "INGRESO DE DINERO" : "SALIDAS DE DINERO",
                    Pos.CENTER_LEFT, "#020203"), 3, (i + 1));
            gpList.add(addElementGridPane("l5" + (i + 1),
                    arrList.get(i).getTipoMovimiento() == 1
                    || arrList.get(i).getTipoMovimiento() == 2
                    || arrList.get(i).getTipoMovimiento() == 3
                    || arrList.get(i).getTipoMovimiento() == 4
                    ? Tools.roundingValue(arrList.get(i).getMonto(), 2)
                    : "",
                    Pos.CENTER_RIGHT, "#0d4e9e"), 4, (i + 1));
            gpList.add(addElementGridPane("l6" + (i + 1),
                    arrList.get(i).getTipoMovimiento() == 1
                    || arrList.get(i).getTipoMovimiento() == 2
                    || arrList.get(i).getTipoMovimiento() == 3
                    || arrList.get(i).getTipoMovimiento() == 4
                    ? ""
                    : Tools.roundingValue(arrList.get(i).getMonto(), 2),
                    Pos.CENTER_RIGHT, "#ff0000"), 5, (i + 1));
        }
    }

    private Label addElementGridPane(String id, String nombre, Pos pos, String textFill) {
        Label label = new Label(nombre);
        label.setId(id);
        label.setTextFill(Color.web(textFill));
        label.setStyle("-fx-background-color: #eaeaea;-fx-padding: 0.4166666666666667em 0.8333333333333334em 0.4166666666666667em 0.8333333333333334em;");
        label.getStyleClass().add("labelRoboto14");
        label.setAlignment(pos);
        label.setWrapText(true);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        return label;
    }

    private void openWindowCaja() {
        try {
            ObjectGlobal.InitializationTransparentBackground(windowinit);
            URL url = getClass().getResource(FilesRouters.FX_CAJA_BUSQUEDA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxCajaBusquedaController controller = fXMLLoader.getController();
            controller.setInitCajaConsultasController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccionar corte de caja", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> windowinit.getChildren().remove(ObjectGlobal.PANE));
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
            System.out.println(ex);

        }
    }

    @FXML
    private void onKeyPressedSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowCaja();
        }
    }

    @FXML
    private void onActionSearch(ActionEvent event) {
        openWindowCaja();
    }

    public void setContent(AnchorPane windowinit) {
        this.windowinit = windowinit;
    }

}
