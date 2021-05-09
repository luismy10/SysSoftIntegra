package controller.produccion.producir;

import controller.menus.FxPrincipalController;
import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import model.FormulaADO;
import model.FormulaTB;
import model.SuministroTB;

public class FxFormulaVisualizarController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private Label lblTitulo;
    @FXML
    private Text lblCreado;
    @FXML
    private Text lblFechaCreacion;
    @FXML
    private Text lblCostoAdicional;
    @FXML
    private GridPane gpList;
    @FXML
    private ScrollPane spBody;
    @FXML
    private HBox hbLoad;
    @FXML
    private Label lblMessageLoad;
    @FXML
    private Button btnAceptarLoad;

    private FxFormulaController fxFormulaController;

    private FxPrincipalController fxPrincipalController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setInitComponents(String idFormula) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<Object> task = new Task<Object>() {
            @Override
            protected Object call() {
                return FormulaADO.Obtener_Formula_ById(idFormula, 1);
            }
        };

        task.setOnScheduled(w -> {
            spBody.setDisable(true);
            hbLoad.setVisible(true);
            lblMessageLoad.setText("Cargando datos...");
            lblMessageLoad.setTextFill(Color.web("#ffffff"));
            btnAceptarLoad.setVisible(false);
        });
        task.setOnFailed(w -> {
            lblMessageLoad.setText(task.getException().getLocalizedMessage());
            lblMessageLoad.setTextFill(Color.web("#ff6d6d"));
            btnAceptarLoad.setVisible(true);
        });
        task.setOnSucceeded(w -> {
            Object object = task.getValue();
            if (object instanceof FormulaTB) {
                FormulaTB formulaTB = (FormulaTB) object;
                lblTitulo.setText("FORMULA DE PRODUCCIÓN - " + formulaTB.getTitulo());
                lblCreado.setText(formulaTB.getEmpleadoTB().getApellidos()+" - "+formulaTB.getEmpleadoTB().getNombres());
                lblFechaCreacion.setText(formulaTB.getFecha() + " - " + formulaTB.getHora());
                lblCostoAdicional.setText(Tools.roundingValue(formulaTB.getCostoAdicional(), 2));
                spBody.setDisable(false);
                hbLoad.setVisible(false);
            } else {
                lblMessageLoad.setText((String) object);
                lblMessageLoad.setTextFill(Color.web("#ff6d6d"));
                btnAceptarLoad.setVisible(true);
            }
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void closerView() {
        fxPrincipalController.getVbContent().getChildren().remove(apWindow);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(fxFormulaController.getHbWindow(), 0d);
        AnchorPane.setTopAnchor(fxFormulaController.getHbWindow(), 0d);
        AnchorPane.setRightAnchor(fxFormulaController.getHbWindow(), 0d);
        AnchorPane.setBottomAnchor(fxFormulaController.getHbWindow(), 0d);
        fxPrincipalController.getVbContent().getChildren().add(fxFormulaController.getHbWindow());
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        closerView();
    }

    public void setInitFormulaController(FxFormulaController fxFormulaController, FxPrincipalController fxPrincipalController) {
        this.fxFormulaController = fxFormulaController;
        this.fxPrincipalController = fxPrincipalController;
    }

    @FXML
    private void onKeyPressedAceptarLoad(KeyEvent event) {
    }

    @FXML
    private void onActionAceptarLoad(ActionEvent event) {
    }

}
