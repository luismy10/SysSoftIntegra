package controller.operaciones.cortecaja;

import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
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
import javafx.stage.WindowEvent;
import model.MovimientoCajaADO;
import model.MovimientoCajaTB;

public class FxCajaConsultasController implements Initializable {

    @FXML
    private ScrollPane window;
    @FXML
    private Label lblTrabajador;
    @FXML
    private Label lblFechaCorte;
    @FXML
    private Label lblFondoCaja;
    @FXML
    private Label lblVentasEfectivo;
    @FXML
    private Label lblEntradas;
    @FXML
    private Label lblSalidas;
    @FXML
    private Label lblDevoluciones;
    @FXML
    private Label lblTotalDineroCaja;
    @FXML
    private Label lblEfectivo;
    @FXML
    private Label lblVentasCredito;
    @FXML
    private GridPane gpList;

    private AnchorPane windowinit;

    private double totalDineroCaja;

    private ObservableList<MovimientoCajaTB> arrList = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void loadDataCorteCaja(String fecha, String usuario, String idCaja) {
        clearListas();
        lblFechaCorte.setText(fecha);
        lblTrabajador.setText(usuario);
        MovimientoCajaTB fondoCaja = MovimientoCajaADO.FondoCaja(idCaja);
        if (fondoCaja != null) {
            lblFondoCaja.setText(Tools.roundingValue(fondoCaja.getSaldo(), 2));
            totalDineroCaja = fondoCaja.getSaldo();
        }

        MovimientoCajaTB ventasEfectivo = MovimientoCajaADO.VentasEfectivo(idCaja);
        if (ventasEfectivo != null) {
            lblEfectivo.setText(Tools.roundingValue(ventasEfectivo.getSaldo(), 2));
            lblVentasEfectivo.setText(Tools.roundingValue(ventasEfectivo.getSaldo(), 2));
            totalDineroCaja = totalDineroCaja + ventasEfectivo.getSaldo();
        }

        MovimientoCajaTB ventasCredito = MovimientoCajaADO.VentasCredito(idCaja);
        if (ventasCredito != null) {
            lblVentasCredito.setText(Tools.roundingValue(ventasCredito.getSaldo(), 2));
        }

        MovimientoCajaTB ingresosEfectivo = MovimientoCajaADO.IngresosEfectivo(idCaja);
        if (ingresosEfectivo != null) {
            lblEntradas.setText(Tools.roundingValue(ingresosEfectivo.getSaldo(), 2));
            totalDineroCaja = totalDineroCaja + ingresosEfectivo.getSaldo();
        }

        MovimientoCajaTB egresosEfectivoCompra = MovimientoCajaADO.EgresosEfectivoCompra(idCaja);
        if (egresosEfectivoCompra != null) {
            lblSalidas.setText("-" + Tools.roundingValue(egresosEfectivoCompra.getSaldo(), 2));
            totalDineroCaja = totalDineroCaja - egresosEfectivoCompra.getSaldo();
        }

        MovimientoCajaTB egresosEfectivo = MovimientoCajaADO.EgresosEfectivo(idCaja);
        if (egresosEfectivo != null) {
            lblSalidas.setText("-" + Tools.roundingValue(egresosEfectivo.getSaldo(), 2));
            totalDineroCaja = totalDineroCaja - egresosEfectivo.getSaldo();
        }

        MovimientoCajaTB devolucionesEfectivo = MovimientoCajaADO.DevolucionesEfectivo(idCaja);
        if (devolucionesEfectivo != null) {
            lblDevoluciones.setText("-" + Tools.roundingValue(devolucionesEfectivo.getSaldo(), 2));
            totalDineroCaja = totalDineroCaja - devolucionesEfectivo.getSaldo();
        }

        lblTotalDineroCaja.setText(Session.MONEDA + " " + Tools.roundingValue(Math.abs(totalDineroCaja), 2));

        fillVentasDetalleTable(MovimientoCajaADO.ListarCajasAperturadas(idCaja));
    }

    private void fillVentasDetalleTable(ObservableList<MovimientoCajaTB> empList) {
        arrList = empList;
        for (int i = 0; i < arrList.size(); i++) {
            gpList.add(addElementGridPane("l1" + (i + 1), arrList.get(i).getFechaMovimiento() + "\n" + arrList.get(i).getHoraMovimiento() + "", Pos.CENTER_LEFT, "#020203"), 0, (i + 1));
            gpList.add(addElementGridPane("l2" + (i + 1), arrList.get(i).getComentario(), Pos.CENTER_LEFT, "#020203"), 1, (i + 1));
            gpList.add(addElementGridPane("l3" + (i + 1), arrList.get(i).getMovimiento(), Pos.CENTER_LEFT, "#020203"), 2, (i + 1));
            gpList.add(addElementGridPane("l4" + (i + 1), Tools.roundingValue(arrList.get(i).getEntrada(), 2), Pos.CENTER_RIGHT, "#020203"), 3, (i + 1));
            gpList.add(addElementGridPane("l5" + (i + 1), (arrList.get(i).getSalidas() <= 0 ? "" : "-") + Tools.roundingValue(arrList.get(i).getSalidas(), 2), Pos.CENTER_RIGHT, "#ff0000"), 4, (i + 1));
            gpList.add(addElementGridPane("l6" + (i + 1), Tools.roundingValue(arrList.get(i).getSaldo(), 2), Pos.CENTER_RIGHT, "#0d4e9e"), 5, (i + 1));
        }
    }

    private Label addElementGridPane(String id, String nombre, Pos pos, String textFill) {
        Label label = new Label(nombre);
        label.setId(id);
        label.setTextFill(Color.web(textFill));
        label.setStyle("-fx-background-color: #dddddd;-fx-padding: 0.4166666666666667em 0.8333333333333334em 0.4166666666666667em 0.8333333333333334em;");
        label.getStyleClass().add("labelRoboto14");
        label.setAlignment(pos);
        label.setWrapText(true);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        return label;
    }

    private void clearListas() {
        lblFondoCaja.setText("00.00");
        lblVentasEfectivo.setText("00.00");
        lblEntradas.setText("00.00");
        lblSalidas.setText("00.00");
        lblDevoluciones.setText("00.00");
        lblEfectivo.setText("00.00");
        lblTotalDineroCaja.setText("00.00");
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
            stage.setOnHiding((WindowEvent WindowEvent) -> {
                windowinit.getChildren().remove(ObjectGlobal.PANE);
            });
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

    @FXML
    private void onKeyPressedReport(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionReport(ActionEvent event) {

    }

    @FXML
    private void onKeyPressedTicket(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionTicket(ActionEvent event) {

    }

    private void onKeyPressedDetalleMovimiento(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    public void setContent(AnchorPane windowinit) {
        this.windowinit = windowinit;
    }

}
