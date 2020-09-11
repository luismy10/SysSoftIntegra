package controller.operaciones.guiaremision;

import controller.reporte.FxReportViewController;
import controller.tools.FilesRouters;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.GuiaRemisionTB;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

public class FxGuiaRemisionController implements Initializable {

    @FXML
    private ScrollPane spWindow;
    @FXML
    private TableView<GuiaRemisionTB> tvList;
    @FXML
    private TableColumn<GuiaRemisionTB, String> tcNumero;
    @FXML
    private TableColumn<GuiaRemisionTB, String> tcCodigo;
    @FXML
    private TableColumn<GuiaRemisionTB, String> tcDescripcion;
    @FXML
    private TableColumn<GuiaRemisionTB, String> tcMedida;
    @FXML
    private TableColumn<GuiaRemisionTB, String> tcCantidad;
    @FXML
    private TableColumn<GuiaRemisionTB, String> tcPeso;
    @FXML
    private TableColumn<GuiaRemisionTB, HBox> tcOpcion;

    private AnchorPane vbPrincipal;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        

        tcNumero.prefWidthProperty().bind(tvList.widthProperty().multiply(0.06));
        tcCodigo.prefWidthProperty().bind(tvList.widthProperty().multiply(0.13));
        tcDescripcion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.23));
        tcMedida.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcCantidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcPeso.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        tcOpcion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.14));
        
    }

    private void reportGuiaRemision() {
        try {
            InputStream imgInputStreamIcon = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            InputStream imgInputStream = getClass().getResourceAsStream(FilesRouters.IMAGE_LOGO);

            if (Session.COMPANY_IMAGE != null) {
                imgInputStream = new ByteArrayInputStream(Session.COMPANY_IMAGE);
            }
            InputStream dir = getClass().getResourceAsStream("/report/GuiadeRemision.jasper");
            Map map = new HashMap();
            map.put("LOGO", imgInputStream);
            map.put("ICON", imgInputStreamIcon);
            map.put("RUC_EMPRESA", Session.COMPANY_NUMERO_DOCUMENTO);
            map.put("NOMBRE_EMPRESA", Session.COMPANY_RAZON_SOCIAL);
            map.put("DIRECCION_EMPRESA", Session.COMPANY_DOMICILIO);
            map.put("TELEFONO_EMPRESA", Session.COMPANY_TELEFONO);
            map.put("CELULAR_EMPRESA", Session.COMPANY_CELULAR);
            map.put("EMAIL_EMPRESA", Session.COMPANY_EMAIL);

            JasperPrint jasperPrint = JasperFillManager.fillReport(dir, map, new JREmptyDataSource());
            URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxReportViewController controller = fXMLLoader.getController();
            controller.setJasperPrint(jasperPrint);
            controller.show();
            Stage stage = WindowStage.StageLoader(parent, "Guia de remisión");
            stage.setResizable(true);
            stage.show();
            stage.requestFocus();
        } catch (JRException | IOException ex) {
            Tools.AlertMessageError(spWindow, "Reporte de Ventas", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onKeyPressedToRegister(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {

        }
    }

    @FXML
    private void onActionToRegister(ActionEvent event) {

    }

    @FXML
    private void onKeyPressedPrevisualizar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            reportGuiaRemision();
        }
    }

    @FXML
    private void onActionPrevisualizar(ActionEvent event) {
        reportGuiaRemision();
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
