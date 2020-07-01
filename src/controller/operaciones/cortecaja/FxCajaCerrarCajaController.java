package controller.operaciones.cortecaja;

import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.CajaADO;

public class FxCajaCerrarCajaController implements Initializable {

    @FXML
    private AnchorPane window;
    @FXML
    private TextField txtEfectivo;
    @FXML
    private Text lblFechaCierre;
    @FXML
    private Label lblValorTarjeta;

    private String idActual;

    private double calculado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(window, KeyEvent.KEY_RELEASED);
        lblFechaCierre.setText(Tools.getDate() + " " + Tools.getHour("hh:mm a"));
    }

    public void loadDataInit(String idActual, double calculado,double valorTarjeta) {
        this.idActual = idActual;
        this.calculado = calculado;
        lblValorTarjeta.setText(Tools.roundingValue(valorTarjeta, 2));
    }

    private void onEventAceptar() throws IOException {
        if (!Tools.isNumeric(txtEfectivo.getText())) {
            Tools.AlertMessageWarning(window, "Corte de caja", "Ingrese el monto actual de caja.");
            txtEfectivo.requestFocus();
        } else {
            short option = Tools.AlertMessageConfirmation(window, "Corte de caja", "¿Está seguro de cerrar su turno?");
            if (option == 1) {
                String result = CajaADO.CerrarAperturaCaja(idActual, Tools.getDate(), Tools.getHour("HH:mm:ss"), false, Double.parseDouble(txtEfectivo.getText()), calculado);
                if (result.equalsIgnoreCase("completed")) {
                    Tools.AlertMessageInformation(window ,"Corte de caja", "Se cerro correctamente la caja.");
                    Tools.Dispose(window);
                    
                    URL urllogin = getClass().getResource(FilesRouters.FX_LOGIN);
                    FXMLLoader fXMLLoaderLogin = WindowStage.LoaderWindow(urllogin);
                    Parent parent = fXMLLoaderLogin.load(urllogin.openStream());
                    Scene scene = new Scene(parent);
                    Stage primaryStage = new Stage();
                    primaryStage.getIcons().add(new Image(FilesRouters.IMAGE_ICON));
                    primaryStage.setScene(scene);
                    primaryStage.initStyle(StageStyle.DECORATED);
                    primaryStage.setTitle(FilesRouters.TITLE_APP);
                    primaryStage.centerOnScreen();
                    primaryStage.setMaximized(true);
                    primaryStage.show();
                    primaryStage.requestFocus();
                    
                } else {
                    Tools.AlertMessageWarning(window, "Corte de caja", result);
                }
            }
        }

    }

    @FXML
    private void onActionAceptar(ActionEvent event) throws IOException {
        onEventAceptar();
    }

    @FXML
    private void onKeyPressedAceptar(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER) {
            onEventAceptar();
        }
    }

    @FXML
    private void onKeyTypedEfectivo(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
            event.consume();
        }
        if (c == '.' && txtEfectivo.getText().contains(".")) {
            event.consume();
        }
    }

}
