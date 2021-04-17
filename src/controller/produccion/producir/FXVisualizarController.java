package controller.produccion.producir;

import controller.menus.FxPrincipalController;
import controller.tools.Tools;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import model.ProduccionADO;
import model.ProduccionTB;
import model.SuministroTB;

public class FXVisualizarController implements Initializable {

    @FXML
    private ScrollPane window;
    @FXML
    private Label lblLoad;
    @FXML
    private Label lblProducto;
    @FXML
    private Label lblCantidad;
    @FXML
    private Label lblAlmacen;
    @FXML
    private Label lblEmpleado;
    @FXML
    private Label lblFechaCreacion;
    @FXML
    private Label lblFechaProduccion;
    @FXML
    private Label lblEstado;
    @FXML
    private Label lblDuracion;
    @FXML
    private GridPane gpList;
    @FXML
    private Label lblDescripcion;
    @FXML
    private Label lblCostoMateriaPrima;
    @FXML
    private Label lblCostoProducto;
    @FXML
    private Label lblTotal;

    private FxProducirController producirController;

    private FxPrincipalController fxPrincipalController;

    private String idProduccion;

    private ArrayList<SuministroTB> insumoTBs;

    private Double costo;
    
    private Double total;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        insumoTBs = new ArrayList();
        costo = (double) 0;
        total = (double) 0;
    }

    public void setInitComponents(String idProduccion) {
        this.idProduccion = idProduccion;

        ExecutorService executor = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<Object> task = new Task<Object>() {
            @Override
            protected Object call() {
                return ProduccionADO.Obtener_Produccion_ById(idProduccion);
            }
        };

        task.setOnScheduled(e -> {
            lblLoad.setVisible(true);
        });

        task.setOnSucceeded(e -> {
            Object object = task.getValue();
            if (object instanceof ProduccionTB) {
                Tools.println("entro");
                ProduccionTB produccionTB = (ProduccionTB) object;
                lblProducto.setText(produccionTB.getSuministroTB().getNombreMarca());
                lblCantidad.setText(String.valueOf(produccionTB.getCantidad()));
                lblLoad.setVisible(false);
                lblEmpleado.setText(produccionTB.getEmpleadoTB().getNombres() + produccionTB.getEmpleadoTB().getApellidos());
                lblFechaCreacion.setText(produccionTB.getFechaRegistro());
                lblFechaProduccion.setText(produccionTB.getFechaInicio());
                switch (produccionTB.getEstado()) {
                    case 1:
                        lblEstado.setText("Completado");
                        break;
                    case 2:
                        lblEstado.setText("En Producción");
                        break;
                    default:
                        lblEstado.setText("Anulado");
                        break;
                }
                lblDuracion.setText(produccionTB.getDias() + " Dias - " + produccionTB.getHoras() + " Horas - " + produccionTB.getMinutos() + " Minutos");
                insumoTBs.addAll(produccionTB.getInsumoTBs());

                fillProduccionDetalleTable();

                lblDescripcion.setText(produccionTB.getDescripcion());
                lblCostoMateriaPrima.setText(String.valueOf(costo));
                lblCostoProducto.setText(String.valueOf(total / produccionTB.getCantidad()));
                lblTotal.setText(String.valueOf(costo));
            } else if (object instanceof String) {
                SQLException exception = (SQLException) object;
                Tools.AlertMessageWarning(window, "Visualizar Proceso", exception.getLocalizedMessage());
            } else {
                Tools.AlertMessageWarning(window, "Visualizar Proceso", "Se produjo un error interno, comuníquese con su proveedor del sistema.");
            }
        });

        task.setOnFailed(e -> {
            lblLoad.setVisible(false);
        });
        executor.execute(task);
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
    }

    private void fillProduccionDetalleTable() {

//        for (int i = 0; i < insumoTBs.size(); i++) {
//            gpList.add(addElementGridPaneLabel("l1" + (i + 1), (i + 1) + "", Pos.CENTER), 0, (i + 1));
//            gpList.add(addElementGridPaneLabel("l2" + (i + 1), Tools.roundingValue(insumoTBs.get(i).getCantidadUtilizada(), 2), Pos.CENTER), 1, (i + 1));
//            gpList.add(addElementGridPaneLabel("l3" + (i + 1), insumoTBs.get(i).getMedidaName(), Pos.CENTER), 2, (i + 1));
//            gpList.add(addElementGridPaneLabel("l4" + (i + 1), Tools.roundingValue(insumoTBs.get(i).getCosto(), 2), Pos.CENTER), 3, (i + 1));
//            gpList.add(addElementGridPaneLabel("l5" + (i + 1), Tools.roundingValue((insumoTBs.get(i).getCosto() * insumoTBs.get(i).getCantidadUtilizada()), 2), Pos.CENTER), 4, (i + 1));
//            gpList.add(addElementGridPaneLabel("l6" + (i + 1), insumoTBs.get(i).getNombreMarca(), Pos.CENTER), 5, (i + 1));
//            gpList.add(addElementGridPaneLabel("l7" + (i + 1), "S/C", Pos.CENTER), 6, (i + 1));
//
//            costo += (insumoTBs.get(i).getCosto() * insumoTBs.get(i).getCantidadUtilizada());
//        }
        total = costo;
    }

    private Label addElementGridPaneLabel(String id, String nombre) {
        Label label = new Label(nombre);
        label.setId(id);
        label.setStyle("-fx-background-color: #020203;-fx-text-fill:#ffffff;-fx-padding: 0.6666666666666666em 0.16666666666666666em 0.6666666666666666em 0.16666666666666666em;-fx-font-weight: 100");
        label.getStyleClass().add("labelOpenSansRegular13");
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        return label;
    }

    private Label addElementGridPaneLabel(String id, String nombre, Pos pos) {
        Label label = new Label(nombre);
        label.setId(id);
        label.setStyle("-fx-text-fill:#020203;-fx-padding: 0.4166666666666667em 0.8333333333333334em 0.4166666666666667em 0.8333333333333334em;");
        label.getStyleClass().add("labelRoboto13");
        label.setAlignment(pos);
        label.setWrapText(true);
        label.setPrefWidth(Control.USE_COMPUTED_SIZE);
        label.setPrefHeight(Control.USE_COMPUTED_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        return label;
    }

    @FXML
    private void onMouseClickedBehind(MouseEvent event) {
        fxPrincipalController.getVbContent().getChildren().remove(window);
        fxPrincipalController.getVbContent().getChildren().clear();
        AnchorPane.setLeftAnchor(producirController.getWindow(), 0d);
        AnchorPane.setTopAnchor(producirController.getWindow(), 0d);
        AnchorPane.setRightAnchor(producirController.getWindow(), 0d);
        AnchorPane.setBottomAnchor(producirController.getWindow(), 0d);
        fxPrincipalController.getVbContent().getChildren().add(producirController.getWindow());
    }

    public void setInitControllerProducir(FxProducirController producirController) {
        this.producirController = producirController;
    }

    public void setContent(FxPrincipalController fxPrincipalController) {
        this.fxPrincipalController = fxPrincipalController;
    }

}
