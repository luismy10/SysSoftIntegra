package controller.inventario.movimientos;

import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.MovimientoCajaTB;
import model.MovimientoInventarioADO;
import model.MovimientoInventarioTB;
import model.SuministroTB;

public class FxMovimientoCajaController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private TextField txtEfectivo;
    @FXML
    private TextField txtObservacion;
    @FXML
    private Button btnEjecutar;
    @FXML
    private Label lblTipoMovimiento;

    private FxMovimientosProcesoController movimientosProcesoController;

    private MovimientoInventarioTB inventarioTB;

    private TableView<SuministroTB> tableView;

    private boolean tipoMovimiento;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void loadData(boolean tipoMovimiento, MovimientoInventarioTB inventarioTB, TableView<SuministroTB> tableView) {
        lblTipoMovimiento.setText(tipoMovimiento ? "Realizar salida de dinero" : "Realizar ingreso de dinero");
        ImageView imageView = new ImageView(new Image(tipoMovimiento ? "/view/image/remove-item.png" : "/view/image/accept.png"));
        imageView.setFitWidth(24);
        imageView.setFitHeight(24);
        lblTipoMovimiento.setGraphic(imageView);
        this.tipoMovimiento = tipoMovimiento;
        this.inventarioTB = inventarioTB;
        this.tableView = tableView;
    }

    private void onEventAceptar() {
        if (!Tools.isNumeric(txtEfectivo.getText().trim())) {
            Tools.AlertMessageWarning(apWindow, "Movimiento de caja", "Ingrese el valor monetario a devolver.");
            txtEfectivo.requestFocus();
        } else if (txtObservacion.getText().trim().isEmpty()) {
            Tools.AlertMessageWarning(apWindow, "Movimiento de caja", "Ingrese el motivo de la devolución de productos.");
            txtObservacion.requestFocus();
        } else {
            short validate = Tools.AlertMessageConfirmation(apWindow, "Movimiento de caja", "¿Está seguro de continuar?");
            if (validate == 1) {
                btnEjecutar.setDisable(true);
                MovimientoCajaTB movimientoCajaTB = new MovimientoCajaTB();
                movimientoCajaTB.setFechaMovimiento(Tools.getDate());
                movimientoCajaTB.setHoraMovimiento(Tools.getHour());
                movimientoCajaTB.setComentario(txtObservacion.getText().trim());
                movimientoCajaTB.setTipoMovimiento(tipoMovimiento ? (short) 5 : (short) 4);
                movimientoCajaTB.setMonto(Double.parseDouble(txtEfectivo.getText()));
                String result = MovimientoInventarioADO.Crud_Movimiento_Inventario_Con_Caja(movimientoCajaTB, inventarioTB, tableView);
                if (result.equalsIgnoreCase("registered")) {
                    Tools.AlertMessageInformation(apWindow, "Movimiento de caja", "Se completo el registro correctamente.");
                    Tools.Dispose(apWindow);
                    movimientosProcesoController.clearComponents();
                } else if (result.equalsIgnoreCase("nocaja")) {
                    Tools.AlertMessageWarning(apWindow, "Movimiento de caja", "No tiene aperturado un caja para realizar el movimiento.");
                    btnEjecutar.setDisable(false);
                } else {
                    Tools.AlertMessageError(apWindow, "Movimiento de caja", result);
                    btnEjecutar.setDisable(false);
                }
            }

        }
    }

    @FXML
    private void onActionAceptar(ActionEvent event) {
        onEventAceptar();
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onEventAceptar();
        }
    }

    @FXML
    private void onKeyTypedEfectivo(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
            event.consume();
        }
        if (c == '.' && txtEfectivo.getText().contains(".")) {
            event.consume();
        }
    }

    public void setInitMovimientoProcesoController(FxMovimientosProcesoController movimientosProcesoController) {
        this.movimientosProcesoController = movimientosProcesoController;
    }

}
