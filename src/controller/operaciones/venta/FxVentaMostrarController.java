package controller.operaciones.venta;

import controller.tools.BillPrintable;
import controller.tools.FilesRouters;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.EmpleadoTB;
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.SuministroTB;
import model.VentaADO;
import model.VentaTB;

public class FxVentaMostrarController implements Initializable {

    @FXML
    private AnchorPane apWindow;
    @FXML
    private TextField txtSearch;
    @FXML
    private Label lblLoad;
    @FXML
    private TableView<VentaTB> tvVentas;
    @FXML
    private TableColumn<VentaTB, String> tcNumero;
    @FXML
    private TableColumn<VentaTB, String> tcCliente;
    @FXML
    private TableColumn<VentaTB, String> tcDocumento;
    @FXML
    private TableColumn<VentaTB, String> tcFechaHora;
    @FXML
    private TableColumn<VentaTB, String> tcTotal;
    @FXML
    private Label lblCliente;
    @FXML
    private Label lblComprobante;
    @FXML
    private Label lblTipo;
    @FXML
    private Label lblVendedor;
    @FXML
    private Label lblObservaciones;
    @FXML
    private Label lblEstado;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblPago;
    @FXML
    private TableView<SuministroTB> tvDetalleVenta;
    @FXML
    private TableColumn<SuministroTB, String> tcCantidad;
    @FXML
    private TableColumn<SuministroTB, String> tcDescripcion;
    @FXML
    private TableColumn<SuministroTB, String> tcPrecio;
    @FXML
    private TableColumn<SuministroTB, String> tcDescuento;
    @FXML
    private TableColumn<SuministroTB, String> tcImporte;
    @FXML
    private HBox hbContenidoTabla;
    @FXML
    private Button btnCancelarVenta;

    private ObservableList<SuministroTB> arrList = null;

    private BillPrintable billPrintable;

    private String nombreTicketImpresion;

    private VBox hbEncabezado;

    private VBox hbDetalleCabecera;

    private VBox hbPie;

    private String idVenta;

    private double efectivo, vuelto;

    private VentaTB ventaTB;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Tools.DisposeWindow(apWindow, KeyEvent.KEY_RELEASED);
        tcNumero.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getId()));
        tcCliente.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getClienteTB().getInformacion()));
        tcFechaHora.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getFechaVenta() + "\n"
                + cellData.getValue().getHoraVenta()
        ));
        tcDocumento.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getSerie() + "\n"
                + cellData.getValue().getNumeracion()
        ));
        tcTotal.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMonedaName() + " " + Tools.roundingValue(cellData.getValue().getTotal(), 2)));

        tcNumero.prefWidthProperty().bind(tvVentas.widthProperty().multiply(0.08));
        tcCliente.prefWidthProperty().bind(tvVentas.widthProperty().multiply(0.24));
        tcDocumento.prefWidthProperty().bind(tvVentas.widthProperty().multiply(0.3));
        tcFechaHora.prefWidthProperty().bind(tvVentas.widthProperty().multiply(0.18));
        tcTotal.prefWidthProperty().bind(tvVentas.widthProperty().multiply(0.18));

        tcCantidad.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getCantidad(), 2)
        ));
        tcDescripcion.setCellValueFactory(cellData -> Bindings.concat(
                cellData.getValue().getClave() + "\n" + cellData.getValue().getNombreMarca()
        ));
        tcPrecio.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getPrecioVentaGeneral(), 2)
        ));
        tcDescuento.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getDescuento(), 2) + "%"
        ));
        tcImporte.setCellValueFactory(cellData -> Bindings.concat(
                Tools.roundingValue(cellData.getValue().getPrecioVentaGeneral()*cellData.getValue().getCantidad(), 2)
        ));

        billPrintable = new BillPrintable();
        hbEncabezado = new VBox();
        hbDetalleCabecera = new VBox();
        hbPie = new VBox();
    }

    private void fillVentasTable(String value) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<ObservableList<VentaTB>> task = new Task<ObservableList<VentaTB>>() {
            @Override
            public ObservableList<VentaTB> call() {
                return VentaADO.ListVentasMostrar(value);
            }
        };

        task.setOnSucceeded(e -> {
            tvVentas.setItems(task.getValue());
            lblLoad.setVisible(false);
        });
        task.setOnFailed(e -> {
            lblLoad.setVisible(false);
        });

        task.setOnScheduled(e -> {
            lblLoad.setVisible(true);
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }
    
    private void fillVentasTable10Primeras() {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });
        Task<ObservableList<VentaTB>> task = new Task<ObservableList<VentaTB>>() {
            @Override
            public ObservableList<VentaTB> call() {
                return VentaADO.ListVentas10Primeras();
            }
        };

        task.setOnSucceeded(e -> {
            tvVentas.setItems(task.getValue());
            lblLoad.setVisible(false);
        });
        task.setOnFailed(e -> {
            lblLoad.setVisible(false);
        });

        task.setOnScheduled(e -> {
            lblLoad.setVisible(true);
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    public void setInitComponents(String idVenta) {
        this.idVenta = idVenta;
        try {

            ExecutorService executor = Executors.newCachedThreadPool((runnable) -> {
                Thread t = new Thread(runnable);
                t.setDaemon(true);
                return t;
            });

            Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
                @Override
                protected ArrayList<Object> call() {
                    ArrayList<Object> objects = VentaADO.ListCompletaVentasDetalle(idVenta);
                    return objects;
                }
            };

            task.setOnScheduled(e -> {
                lblLoad.setVisible(true);
            });
            task.setOnRunning(e -> {
                lblLoad.setVisible(true);
            });
            task.setOnFailed(e -> {
                lblLoad.setVisible(false);
            });
            task.setOnSucceeded(e -> {

                ArrayList<Object> objects = task.getValue();

                if (!objects.isEmpty()) {
                    ventaTB = (VentaTB) objects.get(0);
                    EmpleadoTB empleadoTB = (EmpleadoTB) objects.get(1);
                    ObservableList<SuministroTB> empList = (ObservableList<SuministroTB>) objects.get(2);
                    if (ventaTB != null) {
//                        lblFechaHora.setText(ventaTB.getFechaVenta() + " " + ventaTB.getHoraVenta());
                        lblCliente.setText(ventaTB.getClienteTB().getNumeroDocumento() + " " + ventaTB.getClienteTB().getInformacion());
                        lblComprobante.setText(ventaTB.getComprobanteName());
                        nombreTicketImpresion = ventaTB.getComprobanteName();
                        lblComprobante.setText(ventaTB.getSerie() + "-" + ventaTB.getNumeracion());
                        lblObservaciones.setText(ventaTB.getObservaciones());
                        lblTipo.setText(ventaTB.getTipoName());
                        lblEstado.setText(ventaTB.getEstadoName());
                        if (ventaTB.getEstadoName().equalsIgnoreCase("ANULADO")) {
                            btnCancelarVenta.setDisable(true);
                            hbContenidoTabla.getStyleClass().add("hbBoxBackgroundImage");
                        } else {
                            btnCancelarVenta.setDisable(false);
                            hbContenidoTabla.getStyleClass().remove("hbBoxBackgroundImage");
                        }
                        
                        lblTotal.setText(ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(Double.parseDouble(Tools.roundingValue(ventaTB.getTotal(), 1)), 2));
                        lblPago.setText(ventaTB.getMonedaTB().getSimbolo() + " " + Tools.roundingValue(ventaTB.getEfectivo(), 2));
                        efectivo = ventaTB.getEfectivo();
                        vuelto = ventaTB.getVuelto();
//                        Session.TICKET_CODIGOVENTA = ventaTB.getCodigo();
//                        Session.TICKET_SIMBOLOMONEDA = ventaTB.getMonedaTB().getSimbolo();
                    }

                    if (empleadoTB != null) {
                        lblVendedor.setText(empleadoTB.getApellidos() + " " + empleadoTB.getNombres());
                    }
                    fillVentasDetalleTable(empList);

                }

                lblLoad.setVisible(false);
            });

            executor.execute(task);
            if (!executor.isShutdown()) {
                executor.shutdown();
            }
        } catch (Exception ex) {
            System.out.println(ex.getLocalizedMessage());
            lblLoad.setVisible(false);
        }

    }

    private void fillVentasDetalleTable(ObservableList<SuministroTB> empList) {
        arrList = empList;
        tvDetalleVenta.setItems(empList);
    }

    private void loadTicket() {
//        billPrintable.loadEstructuraTicket(Session.RUTA_TICKET_VENTA, hbEncabezado, hbDetalleCabecera, hbPie);
    }

    private void imprimirVenta(String ticket) {
        if ("".equals(idVenta) || idVenta == null) {
            return;
        }
        if (Session.ESTADO_IMPRESORA_VENTA && Session.NOMBRE_IMPRESORA_VENTA != null) {
            loadTicket();
            ArrayList<HBox> object = new ArrayList<>();
            int rows = 0;
            int lines = 0;
            for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
                object.add((HBox) hbEncabezado.getChildren().get(i));
                HBox box = ((HBox) hbEncabezado.getChildren().get(i));
                rows++;
                //lines += billPrintable.hbEncebezado(box, nombreTicketImpresion, ticket, ventaTB.getClienteTB().getNumeroDocumento(), ventaTB.getClienteTB().getInformacion());
            }

            for (int m = 0; m < arrList.size(); m++) {
                for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                    HBox hBox = new HBox();
                    hBox.setId("dc_" + m + "" + i);
                    HBox box = ((HBox) hbDetalleCabecera.getChildren().get(i));
                    rows++;
                    //lines += billPrintable.hbDetalle(hBox, box, arrList, m);
                    object.add(hBox);
                }
            }

            for (int i = 0; i < hbPie.getChildren().size(); i++) {
                object.add((HBox) hbPie.getChildren().get(i));
                HBox box = ((HBox) hbPie.getChildren().get(i));
                rows++;
//                lines += billPrintable.hbPie(box, Tools.roundingValue(subImporte, 2), Tools.roundingValue(descuento, 2), Tools.roundingValue(subTotalImporte, 2), Tools.roundingValue(totalImporte, 2), efectivo, vuelto, ventaTB.getClienteTB().getNumeroDocumento(), ventaTB.getClienteTB().getNumeroDocumento());
            }
//            billPrintable.modelTicket(apWindow, rows + lines + 1 + 5, lines, object, "Ticket", "Error el imprimir el ticket.", Session.NOMBRE_IMPRESORA, Session.CORTAPAPEL_IMPRESORA);
        } else {
            Tools.AlertMessageWarning(apWindow, "Detalle de venta", "No esta configurado la impresora :D");
        }
    }


    private void cancelVenta() {
        try {
            if ("".equals(idVenta) || idVenta == null) {
                return;
            }

            URL url = getClass().getResource(FilesRouters.FX_VENTA_DEVOLUCION);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxVentaDevolucionController controller = fXMLLoader.getController();
            controller.setInitVentaMostrar(this);
            controller.setLoadVentaDevolucion(idVenta, arrList, lblComprobante.getText(), lblTotal.getText(), ventaTB.getTotal());
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Cancelar la venta", apWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> {
                idVenta = "";
            });
            stage.show();
        } catch (IOException ex) {

        }
    }

    @FXML
    private void onKeyPressed10PrimerasVentas(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                fillVentasTable10Primeras();
            }
        }
    }

    @FXML
    private void onAction10PrimerasVentas(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            fillVentasTable10Primeras();
        }
    }

    @FXML
    private void onKeyReleasedSearch(KeyEvent event) {
        if (event.getCode() != KeyCode.ESCAPE
                && event.getCode() != KeyCode.F1
                && event.getCode() != KeyCode.F2
                && event.getCode() != KeyCode.F3
                && event.getCode() != KeyCode.F4
                && event.getCode() != KeyCode.F5
                && event.getCode() != KeyCode.F6
                && event.getCode() != KeyCode.F7
                && event.getCode() != KeyCode.F8
                && event.getCode() != KeyCode.F9
                && event.getCode() != KeyCode.F10
                && event.getCode() != KeyCode.F11
                && event.getCode() != KeyCode.F12
                && event.getCode() != KeyCode.ALT
                && event.getCode() != KeyCode.CONTROL
                && event.getCode() != KeyCode.UP
                && event.getCode() != KeyCode.DOWN
                && event.getCode() != KeyCode.RIGHT
                && event.getCode() != KeyCode.LEFT
                && event.getCode() != KeyCode.TAB
                && event.getCode() != KeyCode.CAPS
                && event.getCode() != KeyCode.SHIFT
                && event.getCode() != KeyCode.HOME
                && event.getCode() != KeyCode.WINDOWS
                && event.getCode() != KeyCode.ALT_GRAPH
                && event.getCode() != KeyCode.CONTEXT_MENU
                && event.getCode() != KeyCode.END
                && event.getCode() != KeyCode.INSERT
                && event.getCode() != KeyCode.PAGE_UP
                && event.getCode() != KeyCode.PAGE_DOWN
                && event.getCode() != KeyCode.NUM_LOCK
                && event.getCode() != KeyCode.PRINTSCREEN
                && event.getCode() != KeyCode.SCROLL_LOCK
                && event.getCode() != KeyCode.PAUSE) {
            if (!lblLoad.isVisible()) {
                fillVentasTable(txtSearch.getText().trim());
            }
        }
    }

    @FXML
    private void onMouseClickedTvVentas(MouseEvent event) {
        if (event.getClickCount() == 2) {
            if (tvVentas.getSelectionModel().getSelectedIndex() >= 0) {
                if (!lblLoad.isVisible()) {
                    setInitComponents(tvVentas.getSelectionModel().getSelectedItem().getIdVenta());
                }
            }
        }
    }

    @FXML
    private void onKeyPressedTvVentas(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (tvVentas.getSelectionModel().getSelectedIndex() >= 0) {
                if (!lblLoad.isVisible()) {
                    setInitComponents(tvVentas.getSelectionModel().getSelectedItem().getIdVenta());
                }
            }
        }
    }

    @FXML
    private void onKeyPressedCancelar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            cancelVenta();
        }
    }

    @FXML
    private void onActionCancelar(ActionEvent event) {
        cancelVenta();
    }

    @FXML
    private void onKeyPressedImprimir(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            imprimirVenta(lblComprobante.getText());
        }
    }

    @FXML
    private void onActionImprimir(ActionEvent event) {
        imprimirVenta(lblComprobante.getText());
    }

}
