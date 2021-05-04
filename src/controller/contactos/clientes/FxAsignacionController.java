package controller.contactos.clientes;

import controller.tools.Tools;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import model.DetalleADO;
import model.DetalleTB;
import model.DirectorioADO;
import model.DirectorioTB;

public class FxAsignacionController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private ComboBox<DetalleTB> cbAtributo;
    @FXML
    private TextField txtValor;
    @FXML
    private Button btnToAction;

    private long idDirectorio;

    private String idPersona;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
        idDirectorio = 0;
        DetalleADO.GetDetailIdName("2", "0002", "").forEach(e -> {
            cbAtributo.getItems().add(new DetalleTB(e.getIdDetalle(), e.getNombre()));
        });
    }

    private void aValidityProcess() {
        if (cbAtributo.getSelectionModel().getSelectedIndex() < 0) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Asignación", "Seleccione el tipo de atributo, por favor.", false);
            cbAtributo.requestFocus();
        } else if (txtValor.getText().equalsIgnoreCase("")) {
            Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Asignación", "Ingrese el valor, por favor.", false);
            txtValor.requestFocus();
        } else {
            short confirmation = Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Asignación", "¿Esta seguro de continuar?", true);
            if (confirmation == 1) {
                DirectorioTB directorioTB = new DirectorioTB();
                directorioTB.setIdDirectorio(idDirectorio);
                directorioTB.setAtributo(cbAtributo.getSelectionModel().getSelectedItem().getIdDetalle());
                directorioTB.setValor(txtValor.getText());
                directorioTB.setIdPersona(idPersona);
                String result = DirectorioADO.CrudEntity(directorioTB);
                switch (result) {
                    case "registered":
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Asignación", "Registrado correctamente.", false);

                        break;
                    case "updated":
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.INFORMATION, "Asignación", "Actualizado correctamente.", false);

                        Tools.Dispose(window);
                        break;
                    case "error":
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.WARNING, "Asignación", "No se puedo completar la ejecución.", false);

                        break;
                    default:
                        Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Asignación", result, false);

                        break;
                }

            } else {
                Tools.AlertMessage(window.getScene().getWindow(), Alert.AlertType.ERROR, "Asignación", "No hay conexión al servidor.", false);
            }
        }
    }

    @FXML
    private void onKeyPressedRegister(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            aValidityProcess();
        }
    }

    @FXML
    private void onActionToRegister(ActionEvent event) {
        aValidityProcess();
    }

    @FXML
    private void onKeyPressedCancel(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Tools.Dispose(window);
        }
    }

    @FXML
    private void onActionCancel(ActionEvent event) {
        Tools.Dispose(window);
    }

    void setViewAdd(String idPersona) {
        this.idPersona = idPersona;
    }

    void setViewUpdate(long idDirectorio, int idAtributo, String valor) {
        this.idDirectorio = idDirectorio;
        btnToAction.setText("Actualizar");
        btnToAction.getStyleClass().add("buttonFourth");
        ObservableList<DetalleTB> lsatrib = cbAtributo.getItems();
        for (int i = 0; i < lsatrib.size(); i++) {
            if (idAtributo == lsatrib.get(i).getIdDetalle()) {
                cbAtributo.getSelectionModel().select(i);
                break;
            }
        }
        txtValor.setText(valor);
    }

}
