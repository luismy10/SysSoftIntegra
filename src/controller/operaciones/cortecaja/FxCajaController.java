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
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.CajaADO;
import model.CajaTB;
import model.PrivilegioTB;

public class FxCajaController implements Initializable {

    @FXML
    private VBox window;
    @FXML
    private Label lblLoad;
    @FXML
    private Button btnRealizarCorte;
    @FXML
    private Button btnTerminarTurno;
    @FXML
    private Label lblTurno;
    @FXML
    private Label lblMontoBase;
    @FXML
    private Label lblTotalVentas;
    @FXML
    private Label lblBase;
    @FXML
    private Label lblVentaEfectivo;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblVentaTarjeta;
    @FXML
    private Label lblIngresosEfectivo;
    @FXML
    private Label lblRetirosEfectivo;

    private AnchorPane vbPrincipal;

    private double totalDineroCaja;

    private String idActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idActual = "";
    }

    public void loadPrivilegios(ObservableList<PrivilegioTB> privilegioTBs) {
        if (privilegioTBs.get(0).getIdPrivilegio() != 0 && !privilegioTBs.get(0).isEstado()) {
            btnRealizarCorte.setDisable(true);
        }

        if (privilegioTBs.get(1).getIdPrivilegio() != 0 && !privilegioTBs.get(1).isEstado()) {
            btnTerminarTurno.setDisable(true);
        }
    }

    private void onEventCorteCaja() {

        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task< ArrayList<Object>> task = new Task< ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                return CajaADO.ValidarAperturaCajaParaCerrar(Session.USER_ID);
            }
        };

        task.setOnSucceeded(e -> {
            ArrayList<Object> objects = task.getValue();
            if (objects.get(0) != null && objects.get(1) != null) {
                CajaTB cajaTB = (CajaTB) objects.get(0);
                ArrayList<Double> arrayList = (ArrayList<Double>) objects.get(1);
                switch (cajaTB.getId()) {
                    case 1:
                        Tools.AlertMessageWarning(window, "Corte de caja", "No tiene ninguna caja aperturada.");
                        btnTerminarTurno.setDisable(true);
                        break;
                    case 2:
                        lblTurno.setText(cajaTB.getFechaApertura() + " " + cajaTB.getHoraApertura());
                        lblMontoBase.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(cajaTB.getContado(), 2));
                        lblBase.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(cajaTB.getContado(), 2));

                        lblTotalVentas.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(0), 2));
                        lblVentaEfectivo.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(0), 2));
                        lblVentaTarjeta.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(1), 2));
                        lblIngresosEfectivo.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(2), 2));
                        lblRetirosEfectivo.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(3), 2));
                        double totalDineroUno = (cajaTB.getContado() + arrayList.get(0) + arrayList.get(2)) - arrayList.get(3);
                        lblTotal.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(totalDineroUno, 2));

                        btnTerminarTurno.setDisable(false);
                        idActual = cajaTB.getIdCaja();
                        break;
                    case 3:
                        lblTurno.setText(cajaTB.getFechaApertura() + " " + cajaTB.getHoraApertura());
                        lblMontoBase.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(cajaTB.getContado(), 2));
                        lblBase.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(cajaTB.getContado(), 2));

                        lblTotalVentas.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(0), 2));
                        lblVentaEfectivo.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(0), 2));
                        lblVentaTarjeta.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(1), 2));
                        lblIngresosEfectivo.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(2), 2));
                        lblRetirosEfectivo.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(arrayList.get(3), 2));
                        double totalDineroDos = (cajaTB.getContado() + arrayList.get(0) + arrayList.get(2)) - arrayList.get(3);
                        lblTotal.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(totalDineroDos, 2));

                        btnTerminarTurno.setDisable(false);
                        idActual = cajaTB.getIdCaja();
                        break;
                    default:
                        break;
                }
            } else {
                Tools.AlertMessageError(window, "Corte de caja", "No se pudo realizar la petición por problemas de conexión, intente nuevamente.");
                btnTerminarTurno.setDisable(true);
            }
            btnRealizarCorte.setDisable(false);
            lblLoad.setVisible(false);
        });

        task.setOnFailed(e -> {
            Tools.AlertMessageError(window, "Corte de caja", "No se pudo realizar la petición por problemas de conexión, intente nuevamente.");
            btnRealizarCorte.setDisable(false);
            lblLoad.setVisible(false);
        });

        task.setOnScheduled(e -> {
            btnRealizarCorte.setDisable(true);
            lblLoad.setVisible(true);
        });

        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }

    }

    private void loadCaja() {

//
//        MovimientoCajaTB fondoCaja = MovimientoCajaADO.FondoCaja(idActual);
//        if (fondoCaja != null) {
//            lblFondoCaja.setText(Tools.roundingValue(fondoCaja.getSaldo(), 2));
//            totalDineroCaja = fondoCaja.getSaldo();
//        }
//
//        MovimientoCajaTB ventasEfectivo = MovimientoCajaADO.VentasEfectivo(idActual);
//        if (ventasEfectivo != null) {
//            lblEfectivo.setText(Tools.roundingValue(ventasEfectivo.getSaldo(), 2));
//            lblVentasEfectivo.setText(Tools.roundingValue(ventasEfectivo.getSaldo(), 2));
//            totalDineroCaja = totalDineroCaja + ventasEfectivo.getSaldo();
//        }
//
//        MovimientoCajaTB ventasCredito = MovimientoCajaADO.VentasCredito(idActual);
//        if (ventasCredito != null) {
//            lblVentasCredito.setText(Tools.roundingValue(ventasCredito.getSaldo(), 2));
//        }
//
//        MovimientoCajaTB ingresosEfectivo = MovimientoCajaADO.IngresosEfectivo(idActual);
//        if (ingresosEfectivo != null) {
//            lblEntradas.setText(Tools.roundingValue(ingresosEfectivo.getSaldo(), 2));
//            totalDineroCaja = totalDineroCaja + ingresosEfectivo.getSaldo();
//        }
//
//        MovimientoCajaTB egresosEfectivoCompra = MovimientoCajaADO.EgresosEfectivoCompra(idActual);
//        if (egresosEfectivoCompra != null) {
//            lblSalidas.setText("-" + Tools.roundingValue(egresosEfectivoCompra.getSaldo(), 2));
//            totalDineroCaja = totalDineroCaja - egresosEfectivoCompra.getSaldo();
//        }
//
//        MovimientoCajaTB egresosEfectivo = MovimientoCajaADO.EgresosEfectivo(idActual);
//        if (egresosEfectivo != null) {
//            lblSalidas.setText("-" + Tools.roundingValue(egresosEfectivo.getSaldo(), 2));
//            totalDineroCaja = totalDineroCaja - egresosEfectivo.getSaldo();
//        }
//
//        MovimientoCajaTB devolucionesEfectivo = MovimientoCajaADO.DevolucionesEfectivo(idActual);
//        if (devolucionesEfectivo != null) {
//            lblDevoluciones.setText("-" + Tools.roundingValue(devolucionesEfectivo.getSaldo(), 2));
//            totalDineroCaja = totalDineroCaja - devolucionesEfectivo.getSaldo();
//        }
//
//        lblTotalDineroCaja.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(Math.abs(totalDineroCaja), 2));
//        btnTerminarTurno.setDisable(false);
    }

    private void onEventTerminarTurno() {
        try {
            if(idActual.equalsIgnoreCase("")){
                Tools.AlertMessageWarning(window, "Realizar corte de caja", "No se pudo completar la carga de información, realzar corte de caja de nuevo.");
                return;
            }
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_CAJA_CERRAR_CAJA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());

            FxCajaCerrarCajaController controller = fXMLLoader.getController();
            controller.loadDataInit(idActual, totalDineroCaja, 0);

            Stage stage = WindowStage.StageLoaderModal(parent, "Realizar corte de caja", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }

    }

    @FXML
    private void onActionCorte(ActionEvent event) {
        onEventCorteCaja();
    }

    @FXML
    private void onKeyPressedCorte(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventCorteCaja();
        }
    }

    @FXML
    private void onKeyPressedTerminarTurno(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventTerminarTurno();
        }
    }

    @FXML
    private void onActionTerminarTurno(ActionEvent event) {
        onEventTerminarTurno();
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
