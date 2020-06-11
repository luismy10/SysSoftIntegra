package controller.reporte;

import controller.tools.FilesRouters;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.awt.HeadlessException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.GlobalADO;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

public class FxGlobalReporteController implements Initializable {

    @FXML
    private VBox vbWindow;
    @FXML
    private DatePicker dpFechaInicial;
    @FXML
    private DatePicker dpFechaFinal;

    private AnchorPane vbPrincipal;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.actualDate(Tools.getDate(), dpFechaInicial);
        Tools.actualDate(Tools.getDate(), dpFechaFinal);
    }

    private void openWindowReporte() {
        try {
            
            ArrayList<Double> arrayList = GlobalADO.ReporteGlobal(Tools.getDatePicker(dpFechaInicial),Tools.getDatePicker(dpFechaFinal));
            if(arrayList.isEmpty()){
               Tools.AlertMessageWarning(vbWindow, "Reporte de Inventario", "No hay datos para mostrar en el reporte.");
               return; 
            }
            
            double compraContado = arrayList.get(0) == null ? 0 : arrayList.get(0);
            double compraCredito = arrayList.get(1) == null ? 0 : arrayList.get(1);
            
            InputStream dir = getClass().getResourceAsStream("/report/ReporteGeneral.jasper");

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(dir);
            Map map = new HashMap();
            map.put("PERIODO", dpFechaInicial.getValue().format(DateTimeFormatter.ofPattern("dd/MM/YYYY")) + " - " + dpFechaFinal.getValue().format(DateTimeFormatter.ofPattern("dd/MM/YYYY")));
            map.put("COMPRASCONTADO", Tools.roundingValue(compraContado, 2));
            map.put("COMPRASCREDITO", Tools.roundingValue(compraCredito, 2));
            map.put("COMPRASTOTAL", Tools.roundingValue(compraContado + compraCredito, 2));
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, new JREmptyDataSource());

            URL url = getClass().getResource(FilesRouters.FX_REPORTE_VIEW);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxReportViewController controller = fXMLLoader.getController();
            controller.setJasperPrint(jasperPrint);
            controller.show();
            Stage stage = WindowStage.StageLoader(parent, "Inventario General");
            stage.setResizable(true);
            stage.show();
            stage.requestFocus();

        } catch (HeadlessException | JRException | IOException ex) {
            Tools.AlertMessageError(vbWindow, "Reporte de Inventario", "Error al generar el reporte : " + ex.getLocalizedMessage());
        }
    }

    @FXML
    private void onKeyPressedReporteGeneral(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowReporte();
        }
    }

    @FXML
    private void onActionReporteGeneral(ActionEvent event) {
        openWindowReporte();
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
