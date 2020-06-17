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
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.CajaADO;
import model.CajaTB;
import model.MovimientoCajaADO;
import model.MovimientoCajaTB;
import model.PrivilegioTB;

public class FxCajaController implements Initializable {

    @FXML
    private VBox window;
    @FXML
    private Label lblCargo;
    @FXML
    private Label lblFecha;
    @FXML
    private Label lblFondoCaja;
    @FXML
    private Label lblDevoluciones;
    @FXML
    private Label lblEfectivo;
    @FXML
    private Label lblVentasEfectivo;
    @FXML
    private Label lblVentasCredito;
    @FXML
    private Label lblEntradas;
    @FXML
    private Label lblSalidas;
    @FXML
    private Label lblTotalDineroCaja;
    @FXML
    private Button btnRealizarCorte;
    @FXML
    private Button btnTerminarTurno;

    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    private double totalDineroCaja;

    private String idActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idActual = "";
    }

    private void corteCaja() {
        //es tan dificil busca en inter como es la estructura de un switch en java si lo se solo setoy ocupad dame un minuto
        //TE FALTO TMR AHI LO CAMBIO
        CajaTB cajaTB = CajaADO.ValidarCreacionCaja(Session.USER_ID);
        switch (cajaTB.getId()) {
            case 1:
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Corte de caja", "No tiene ninguna caja aperturada.", false);
                btnTerminarTurno.setDisable(true);
                break;
            case 2:
                idActual = cajaTB.getIdCaja();
                loadCaja();
                break;
            case 3:
                idActual = cajaTB.getIdCaja();
                loadCaja();
                break;
            default:
                break;
        }

    }

    private void loadCaja() {
        lblCargo.setText(Session.USER_NAME);
        lblFecha.setText(Tools.getDate("dd-MM-yyyy"));

        MovimientoCajaTB fondoCaja = MovimientoCajaADO.FondoCaja(idActual);
        if (fondoCaja != null) {
            lblFondoCaja.setText(Tools.roundingValue(fondoCaja.getSaldo(), 2));
            totalDineroCaja = fondoCaja.getSaldo();
        }

        MovimientoCajaTB ventasEfectivo = MovimientoCajaADO.VentasEfectivo(idActual);
        if (ventasEfectivo != null) {
            lblEfectivo.setText(Tools.roundingValue(ventasEfectivo.getSaldo(), 2));
            lblVentasEfectivo.setText(Tools.roundingValue(ventasEfectivo.getSaldo(), 2));
            totalDineroCaja = totalDineroCaja + ventasEfectivo.getSaldo();
        }

        MovimientoCajaTB ventasCredito = MovimientoCajaADO.VentasCredito(idActual);
        if (ventasCredito != null) {
            lblVentasCredito.setText(Tools.roundingValue(ventasCredito.getSaldo(), 2));
        }

        MovimientoCajaTB ingresosEfectivo = MovimientoCajaADO.IngresosEfectivo(idActual);
        if (ingresosEfectivo != null) {
            lblEntradas.setText(Tools.roundingValue(ingresosEfectivo.getSaldo(), 2));
            totalDineroCaja = totalDineroCaja + ingresosEfectivo.getSaldo();
        }

        MovimientoCajaTB egresosEfectivoCompra = MovimientoCajaADO.EgresosEfectivoCompra(idActual);
        if (egresosEfectivoCompra != null) {
            lblSalidas.setText("-" + Tools.roundingValue(egresosEfectivoCompra.getSaldo(), 2));
            totalDineroCaja = totalDineroCaja - egresosEfectivoCompra.getSaldo();
        }

        MovimientoCajaTB egresosEfectivo = MovimientoCajaADO.EgresosEfectivo(idActual);
        if (egresosEfectivo != null) {
            lblSalidas.setText("-" + Tools.roundingValue(egresosEfectivo.getSaldo(), 2));
            totalDineroCaja = totalDineroCaja - egresosEfectivo.getSaldo();
        }

        MovimientoCajaTB devolucionesEfectivo = MovimientoCajaADO.DevolucionesEfectivo(idActual);
        if (devolucionesEfectivo != null) {
            lblDevoluciones.setText("-" + Tools.roundingValue(devolucionesEfectivo.getSaldo(), 2));
            totalDineroCaja = totalDineroCaja - devolucionesEfectivo.getSaldo();
        }

        lblTotalDineroCaja.setText(Session.MONEDA_SIMBOLO + " " + Tools.roundingValue(Math.abs(totalDineroCaja), 2));
        btnTerminarTurno.setDisable(false);
    }

    public void loadPrivilegios(ObservableList<PrivilegioTB> privilegioTBs) {
        if (privilegioTBs.get(0).getIdPrivilegio() != 0 && !privilegioTBs.get(0).isEstado()) {
            btnRealizarCorte.setDisable(true);
        }

        if (privilegioTBs.get(1).getIdPrivilegio() != 0 && !privilegioTBs.get(1).isEstado()) {
            btnTerminarTurno.setDisable(true);
        }
    }

    private void terminarTurno() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_CAJA_CERRAR_CAJA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());

            FxCajaCerrarCajaController controller = fXMLLoader.getController();
            controller.setInitCajaController(idActual, totalDineroCaja, vbContent);

            Stage stage = WindowStage.StageLoaderModal(parent, "Realizar corte de caja", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding((WindowEvent WindowEvent) -> {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            });
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }

    }

    @FXML
    private void onActionCorte(ActionEvent event) {
        corteCaja();
    }

    @FXML
    private void onKeyPressedCorte(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            corteCaja();
        }
    }

    @FXML
    private void onKeyPressedTerminarTurno(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            terminarTurno();
        }
    }

    @FXML
    private void onActionTerminarTurno(ActionEvent event) {
        terminarTurno();
    }

    public void setContent(AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
