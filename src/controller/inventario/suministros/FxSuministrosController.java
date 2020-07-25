package controller.inventario.suministros;

import controller.configuracion.etiquetas.FxEtiquetasBusquedaController;
import controller.tools.FilesRouters;
import controller.tools.ObjectGlobal;
import controller.tools.SearchComboBox;
import controller.tools.Session;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.DetalleADO;
import model.DetalleTB;
import model.ImpuestoADO;
import model.ImpuestoTB;
import model.PrivilegioTB;
import model.SuministroADO;
import model.SuministroTB;

public class FxSuministrosController implements Initializable {

    @FXML
    private HBox hbWindow;
    @FXML
    private Label lblLoad;
    @FXML
    private TextField txtClave;
    @FXML
    private TextField txtNombre;
    @FXML
    private TableView<SuministroTB> tvList;
    @FXML
    private TableColumn<SuministroTB, Integer> tcNumeracion;
    @FXML
    private TableColumn<SuministroTB, String> tcDescripcion;
    @FXML
    private TableColumn<SuministroTB, String> tcCategoria;
    @FXML
    private TableColumn<SuministroTB, String> tcMarcar;
    @FXML
    private TableColumn<SuministroTB, Label> tcCantidad;
    @FXML
    private TableColumn<SuministroTB, String> tcCosto;
    @FXML
    private ComboBox<DetalleTB> cbCategoria;
    @FXML
    private ComboBox<DetalleTB> cbMarca;
    @FXML
    private ImageView ivPrincipal;
    @FXML
    private Text lblName;
    @FXML
    private Text lblPrice;
    @FXML
    private Text lblPriceBruto;
    @FXML
    private Text lblImpuesto;
    @FXML
    private Label lblQuantity;
    @FXML
    private Label lblPaginaActual;
    @FXML
    private Label lblPaginaSiguiente;
    @FXML
    private HBox hbContenedorBotonos;
    @FXML
    private Button btnAgregar;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnClonar;
    @FXML
    private Button btnRecargar;
    @FXML
    private Button btnEtiqueta;
    @FXML
    private Button btnSuprimir;
    /*
    Objectos de la ventana principal y venta que agrega al os hijos
     */
    private AnchorPane vbPrincipal;

    private AnchorPane vbContent;

    /*
    Controller suministros     
     */
    private FXMLLoader fXMLSuministrosProceso;

    private ScrollPane nodeSuministrosProceso;

    private FxSuministrosProcesoController suministrosProcesoController;

    private ArrayList<ImpuestoTB> arrayArticulosImpuesto;

    private ObservableList<PrivilegioTB> privilegioTBs;

    private int paginacion;

    private int totalPaginacion;

    private short opcion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            hbWindow.setOnKeyReleased((KeyEvent event) -> {
                if (null != event.getCode()) {
                    switch (event.getCode()) {
                        case F1:
                            openWindowAdd();
                            break;

                        case F2:
                            openWindowEdit();
                            break;

                        case F3:
                            onViewArticuloClone();
                            break;

//                    case F4:
//                        executeCloneArticulo();
//                        break;
                        case F5:
                            if (!lblLoad.isVisible()) {
                                paginacion = 1;
                                fillTableSuministros((short) 0, "", "", 0, 0);
                                opcion = 0;
                            }
                            break;
                        case F6:
                            eventEtiqueta();
                            break;
                        case F7:
                            txtClave.requestFocus();
                            txtClave.selectAll();
                            break;

                        case F8:
                            txtNombre.requestFocus();
                            txtNombre.selectAll();
                            break;
                        case F9:
                            cbCategoria.requestFocus();
                            break;
                        case F10:
                            cbMarca.requestFocus();
                            break;
                        case DELETE:
                            break;
                    }
                }
            });

            fXMLSuministrosProceso = new FXMLLoader(getClass().getResource(FilesRouters.FX_SUMINISTROS_PROCESO));
            nodeSuministrosProceso = fXMLSuministrosProceso.load();
            suministrosProcesoController = fXMLSuministrosProceso.getController();

            tcNumeracion.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
            tcDescripcion.setCellValueFactory(cellData -> Bindings.concat(
                    cellData.getValue().getClave() + (cellData.getValue().getClaveAlterna().isEmpty() ? "" : " - ") + cellData.getValue().getClaveAlterna()
                    + "\n" + cellData.getValue().getNombreMarca())
            );

            tcCategoria.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getCategoriaName()));
            tcMarcar.setCellValueFactory(cellData -> Bindings.concat(cellData.getValue().getMarcaName()));
            tcCosto.setCellValueFactory(cellData -> Bindings.concat(Tools.roundingValue(cellData.getValue().getCostoCompra(), 2)));
            tcCantidad.setCellValueFactory(new PropertyValueFactory<>("lblCantidad"));

            tcNumeracion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.06));
            tcDescripcion.prefWidthProperty().bind(tvList.widthProperty().multiply(0.31));
            tcCategoria.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));
            tcMarcar.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));
            tcCosto.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));
            tcCantidad.prefWidthProperty().bind(tvList.widthProperty().multiply(0.15));

            arrayArticulosImpuesto = new ArrayList<>();
            ImpuestoADO.GetTipoImpuestoCombBox().forEach(e -> {
                arrayArticulosImpuesto.add(new ImpuestoTB(e.getIdImpuesto(), e.getNombreImpuesto(), e.getValor(), e.getPredeterminado()));
            });

            paginacion = 1;
            opcion = 0;
            filtercbCategoria();

        } catch (IOException ex) {
            System.out.println("Error en Suministros Controller:" + ex.getLocalizedMessage());
        }
    }

    public void loadPrivilegios(ObservableList<PrivilegioTB> privilegioTBs) {
        this.privilegioTBs = privilegioTBs;
        if (privilegioTBs.get(0).getIdPrivilegio() != 0 && !privilegioTBs.get(0).isEstado()) {
            hbContenedorBotonos.getChildren().remove(btnAgregar);
        } else {

        }

        if (privilegioTBs.get(1).getIdPrivilegio() != 0 && !privilegioTBs.get(1).isEstado()) {
            hbContenedorBotonos.getChildren().remove(btnEditar);
        } else {

        }

        if (privilegioTBs.get(2).getIdPrivilegio() != 0 && !privilegioTBs.get(2).isEstado()) {
            hbContenedorBotonos.getChildren().remove(btnClonar);
        } else {

        }

        if (privilegioTBs.get(3).getIdPrivilegio() != 0 && !privilegioTBs.get(3).isEstado()) {
            hbContenedorBotonos.getChildren().remove(btnRecargar);
        } else {

        }

        if (privilegioTBs.get(4).getIdPrivilegio() != 0 && !privilegioTBs.get(4).isEstado()) {
            hbContenedorBotonos.getChildren().remove(btnEtiqueta);
        } else {

        }

        if (privilegioTBs.get(5).getIdPrivilegio() != 0 && !privilegioTBs.get(5).isEstado()) {
            hbContenedorBotonos.getChildren().remove(btnSuprimir);
        } else {

        }

        if (privilegioTBs.get(6).getIdPrivilegio() != 0 && !privilegioTBs.get(6).isEstado()) {
            txtClave.setDisable(true);
        } else {

        }

        if (privilegioTBs.get(7).getIdPrivilegio() != 0 && !privilegioTBs.get(7).isEstado()) {
            txtNombre.setDisable(true);
        } else {

        }

        if (privilegioTBs.get(8).getIdPrivilegio() != 0 && !privilegioTBs.get(8).isEstado()) {
            cbCategoria.setDisable(true);
        } else {

        }

        if (privilegioTBs.get(9).getIdPrivilegio() != 0 && !privilegioTBs.get(9).isEstado()) {
            cbMarca.setEditable(true);
        } else {

        }
    }

    private void filtercbCategoria() {
        SearchComboBox<DetalleTB> searchComboBoxCategoria = new SearchComboBox<>(cbCategoria,true);
        searchComboBoxCategoria.setFilter((item, text) -> item.getNombre().get().toLowerCase().contains(text.toLowerCase()));
        searchComboBoxCategoria.getComboBox().getItems().addAll(DetalleADO.GetDetailId("0006"));
        searchComboBoxCategoria.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
            if (null == t.getCode()) {
                searchComboBoxCategoria.getSearchComboBoxSkin().getSearchBox().requestFocus();
                searchComboBoxCategoria.getSearchComboBoxSkin().getSearchBox().selectAll();
            } else {
                switch (t.getCode()) {
                    case ENTER:
                    case SPACE:
                    case ESCAPE:
                        if (!lblLoad.isVisible()) {
                            paginacion = 1;
                            fillTableSuministros((short) 3, "", "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle().get(), 0);
                            opcion = 3;
                        }
                        searchComboBoxCategoria.getComboBox().hide();
                        break;
                    case UP:
                    case DOWN:
                    case LEFT:
                    case RIGHT:
                        break;
                    default:
                        searchComboBoxCategoria.getSearchComboBoxSkin().getSearchBox().requestFocus();
                        searchComboBoxCategoria.getSearchComboBoxSkin().getSearchBox().selectAll();
                        break;
                }
            }
        });
        searchComboBoxCategoria.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                searchComboBoxCategoria.getComboBox().getSelectionModel().select(item);
                if (searchComboBoxCategoria.getSearchComboBoxSkin().isClickSelection()) {
                    if (!lblLoad.isVisible()) {
                        paginacion = 1;
                        fillTableSuministros((short) 3, "", "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle().get(), 0);
                        opcion = 3;
                    }
                    searchComboBoxCategoria.getComboBox().hide();
                }
            }
        });

        SearchComboBox<DetalleTB> searchComboBoxMarca = new SearchComboBox<>(cbMarca,true);
        searchComboBoxMarca.setFilter((item, text) -> item.getNombre().get().toLowerCase().contains(text.toLowerCase()));
        searchComboBoxMarca.getComboBox().getItems().addAll(DetalleADO.GetDetailId("0007"));
        searchComboBoxMarca.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
            if (null == t.getCode()) {
                searchComboBoxMarca.getSearchComboBoxSkin().getSearchBox().requestFocus();
                searchComboBoxMarca.getSearchComboBoxSkin().getSearchBox().selectAll();
            } else {
                switch (t.getCode()) {
                    case ENTER:
                    case SPACE:
                    case ESCAPE:
                        if (!lblLoad.isVisible()) {
                            paginacion = 1;
                            fillTableSuministros((short) 4, "", "", 0, cbMarca.getSelectionModel().getSelectedItem().getIdDetalle().get());
                            opcion = 4;
                        }
                        searchComboBoxMarca.getComboBox().hide();
                        break;
                    case UP:
                    case DOWN:
                    case LEFT:
                    case RIGHT:
                        break;
                    default:
                        searchComboBoxMarca.getSearchComboBoxSkin().getSearchBox().requestFocus();
                        searchComboBoxMarca.getSearchComboBoxSkin().getSearchBox().selectAll();
                        break;
                }
            }
        });
        searchComboBoxMarca.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                searchComboBoxMarca.getComboBox().getSelectionModel().select(item);
                if (searchComboBoxMarca.getSearchComboBoxSkin().isClickSelection()) {
                    if (!lblLoad.isVisible()) {
                        paginacion = 1;
                        fillTableSuministros((short) 4, "", "", 0, cbMarca.getSelectionModel().getSelectedItem().getIdDetalle().get());
                        opcion = 4;
                    }
                    searchComboBoxMarca.getComboBox().hide();
                }
            }
        });
    }

    public void onEventPaginacion() {
        switch (opcion) {
            case 0:
                fillTableSuministros((short) 0, "", "", 0, 0);
                break;
            case 1:
                fillTableSuministros((short) 1, txtClave.getText().trim(), "", 0, 0);
                break;
            case 2:
                fillTableSuministros((short) 2, "", txtNombre.getText().trim(), 0, 0);
                break;
            case 3:
                fillTableSuministros((short) 3, "", "", cbCategoria.getSelectionModel().getSelectedItem().getIdDetalle().get(), 0);
                break;
            default:
                fillTableSuministros((short) 4, "", "", 0, cbMarca.getSelectionModel().getSelectedItem().getIdDetalle().get());
                break;
        }
    }

    private void openAlertMessageWarning(String message) {
        ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
        Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Productos", message, false);
        vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
    }

    public void fillTableSuministros(short opcion, String clave, String nombreMarca, int categoria, int marca) {
        ExecutorService exec = Executors.newCachedThreadPool((runnable) -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        Task<ArrayList<Object>> task = new Task<ArrayList<Object>>() {
            @Override
            public ArrayList<Object> call() {
                return SuministroADO.ListarSuministros(opcion, clave, nombreMarca, categoria, marca, (paginacion - 1) * 20, 20);
            }
        };

        task.setOnSucceeded((e) -> {
            ArrayList<Object> objects = task.getValue();
            if (!objects.isEmpty()) {
                tvList.setItems((ObservableList<SuministroTB>) objects.get(0));
                if (!tvList.getItems().isEmpty()) {
                    tvList.getSelectionModel().select(0);
                    onViewDetailSuministro();
                }

                int integer = (int) (Math.ceil((double) (((Integer) objects.get(1)) / 20.00)));
                totalPaginacion = integer;
                lblPaginaActual.setText(paginacion + "");
                lblPaginaSiguiente.setText(totalPaginacion + "");
            }
            lblLoad.setVisible(false);
        });
        task.setOnFailed((e) -> {
            lblLoad.setVisible(false);
        });
        task.setOnScheduled((e) -> {
            lblLoad.setVisible(true);
        });
        exec.execute(task);
        if (!exec.isShutdown()) {
            exec.shutdown();
        }
    }

    private void openWindowAdd() {
        suministrosProcesoController.setInitControllerSuministros(this, vbPrincipal, vbContent);
        //
        vbContent.getChildren().clear();
        AnchorPane.setLeftAnchor(nodeSuministrosProceso, 0d);
        AnchorPane.setTopAnchor(nodeSuministrosProceso, 0d);
        AnchorPane.setRightAnchor(nodeSuministrosProceso, 0d);
        AnchorPane.setBottomAnchor(nodeSuministrosProceso, 0d);
        vbContent.getChildren().add(nodeSuministrosProceso);
        //
        suministrosProcesoController.setInitArticulo();
    }

    private void openWindowEdit() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            suministrosProcesoController.loadPrivilegios(privilegioTBs);
            suministrosProcesoController.setInitControllerSuministros(this, vbPrincipal, vbContent);
            //
            vbContent.getChildren().clear();
            AnchorPane.setLeftAnchor(nodeSuministrosProceso, 0d);
            AnchorPane.setTopAnchor(nodeSuministrosProceso, 0d);
            AnchorPane.setRightAnchor(nodeSuministrosProceso, 0d);
            AnchorPane.setBottomAnchor(nodeSuministrosProceso, 0d);
            vbContent.getChildren().add(nodeSuministrosProceso);
            //
            suministrosProcesoController.setValueEdit(tvList.getSelectionModel().getSelectedItem().getIdSuministro());
        } else {
            openAlertMessageWarning("Debe seleccionar un producto para editarlo");
            tvList.requestFocus();
        }
    }

    private void onViewArticuloClone() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            suministrosProcesoController.setInitControllerSuministros(this, vbPrincipal, vbContent);
            //
            vbContent.getChildren().clear();
            AnchorPane.setLeftAnchor(nodeSuministrosProceso, 0d);
            AnchorPane.setTopAnchor(nodeSuministrosProceso, 0d);
            AnchorPane.setRightAnchor(nodeSuministrosProceso, 0d);
            AnchorPane.setBottomAnchor(nodeSuministrosProceso, 0d);
            vbContent.getChildren().add(nodeSuministrosProceso);
            //
            suministrosProcesoController.setValueClone(tvList.getSelectionModel().getSelectedItem().getIdSuministro());
        } else {
            openAlertMessageWarning("Debe seleccionar un producto para clonarlo");
            tvList.requestFocus();
        }
    }

    private void eventEtiqueta() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            try {
                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                URL url = getClass().getResource(FilesRouters.FX_ETIQUETA_BUSQUEDA);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxEtiquetasBusquedaController controller = fXMLLoader.getController();
                controller.setInitSuministroController(this);
                controller.onLoadEtiquetas(1);
                //
                Stage stage = WindowStage.StageLoaderModal(parent, "Buscar etiquetas", hbWindow.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
                stage.show();
            } catch (IOException ex) {
                System.out.println(ex.getLocalizedMessage());
            }
        } else {
            Tools.AlertMessageWarning(hbWindow, "Productos", "Seleccione un item para imprimir su etiqueta");
        }
    }

    private void removedArticulo() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            short value = Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.CONFIRMATION, "Productos", "¿Está seguro de eliminar el producto?", true);
            if (value == 1) {
                String result = SuministroADO.RemoverSuministro(tvList.getSelectionModel().getSelectedItem().getIdSuministro());

                if (result.equalsIgnoreCase("compra")) {
                    Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Productos", "El producto esta ligado a una compra, no se puede eliminar hasta que la compra sea borrada.", false);
                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                } else if (result.equalsIgnoreCase("kardex_compra")) {
                    Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Productos", "El producto tiene un kardex ligado, no se puede eliminar hasta que el karder sea borrado.", false);
                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                } else if (result.equalsIgnoreCase("venta")) {
                    Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.WARNING, "Productos", "El producto esta ligado a una venta, no se puede eliminar hasta que la venta sea borrada.", false);
                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                } else if (result.equalsIgnoreCase("removed")) {
                    Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.INFORMATION, "Productos", "Se elimino correctamente", false);
                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                } else {
                    Tools.AlertMessage(hbWindow.getScene().getWindow(), Alert.AlertType.ERROR, "Productos", result, false);
                    vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
                }
            } else {
                vbPrincipal.getChildren().remove(ObjectGlobal.PANE);
            }
        } else {
            openAlertMessageWarning("Seleccione un item para eliminarlo");
        }
    }

    public double getTaxValue(int impuesto) {
        double valor = 0;
        for (int i = 0; i < arrayArticulosImpuesto.size(); i++) {
            if (arrayArticulosImpuesto.get(i).getIdImpuesto() == impuesto) {
                valor = arrayArticulosImpuesto.get(i).getValor();
                break;
            }
        }
        return valor;
    }

    public String getTaxName(int impuesto) {
        String valor = "";
        for (int i = 0; i < arrayArticulosImpuesto.size(); i++) {
            if (arrayArticulosImpuesto.get(i).getIdImpuesto() == impuesto) {
                valor = arrayArticulosImpuesto.get(i).getNombreImpuesto();
                break;
            }
        }
        return valor;
    }

    public void onViewDetailSuministro() {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            SuministroTB suministroTB = tvList.getSelectionModel().getSelectedItem();
            ivPrincipal.setImage(new Image(suministroTB.getImagenTB().equalsIgnoreCase("")
                    ? "/view/image/no-image.png"
                    : new File(suministroTB.getImagenTB()).toURI().toString()));
            lblName.setText(suministroTB.getNombreMarca());
            lblPrice.setText(
                    Session.MONEDA_SIMBOLO + " "
                    + Tools.roundingValue(suministroTB.getPrecioVentaGeneral() + (suministroTB.getPrecioVentaGeneral() * (getTaxValue(suministroTB.getImpuestoArticulo()) / 100.00)),
                            2)
            );
            lblPriceBruto.setText(
                    "Precio Bruto: "
                    + Tools.roundingValue(suministroTB.getPrecioVentaGeneral(), 2));
            lblImpuesto.setText(
                    "Impuesto: " + getTaxName(suministroTB.getImpuestoArticulo())
            );
            lblQuantity.setText(Tools.roundingValue(suministroTB.getCantidad(), 2) + " " + suministroTB.getUnidadCompraName());
        }
    }

    @FXML
    private void onMouseClickedList(MouseEvent event) {
        if (event.getClickCount() == 1) {
            onViewDetailSuministro();
        } else if (event.getClickCount() == 2) {
            openWindowEdit();
        }
    }

    @FXML
    private void onKeyReleasedList(KeyEvent event) {
        if (tvList.getSelectionModel().getSelectedIndex() >= 0) {
            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case UP:
                        onViewDetailSuministro();
                        break;
                    case DOWN:
                        onViewDetailSuministro();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @FXML
    private void onKeyPressedAdd(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowAdd();
        }
    }

    @FXML
    private void onActionAdd(ActionEvent event) {
        openWindowAdd();
    }

    @FXML
    private void onKeyPressedEdit(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowEdit();
        }
    }

    @FXML
    private void onActionEdit(ActionEvent event) {
        openWindowEdit();
    }

    @FXML
    private void onKeyPressedClone(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onViewArticuloClone();
        }
    }

    @FXML
    private void onActionClone(ActionEvent event) {
        onViewArticuloClone();
    }

    private void onKeyPressedCloneArticulo(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onViewArticuloClone();
        }
    }

//    private void onActionCloneArticulo(ActionEvent event) {
//        executeCloneArticulo();
//    }
    @FXML
    private void onKeyPressedReload(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                paginacion = 1;
                fillTableSuministros((short) 0, "", "", 0, 0);
                opcion = 0;
            }
        }
    }

    @FXML
    private void onActionReload(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            paginacion = 1;
            fillTableSuministros((short) 0, "", "", 0, 0);
            opcion = 0;
        }
    }

    @FXML
    private void onKeyPressedRemove(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            removedArticulo();
        }
    }

    @FXML
    private void onActionRemove(ActionEvent event) {
        removedArticulo();
    }

    @FXML
    private void onActionClave(ActionEvent event) {
        if (!tvList.getItems().isEmpty()) {
            tvList.requestFocus();
            tvList.getSelectionModel().select(0);
        }
    }

    @FXML
    private void onActionNombre(ActionEvent event) {
        if (!tvList.getItems().isEmpty()) {
            tvList.requestFocus();
            tvList.getSelectionModel().select(0);
        }
    }

    @FXML
    private void onKeyReleasedSearchCode(KeyEvent event) {
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
                && event.getCode() != KeyCode.PAUSE
                && event.getCode() != KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                paginacion = 1;
                fillTableSuministros((short) 1, txtClave.getText().trim(), "", 0, 0);
                opcion = 1;
            }
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
                && event.getCode() != KeyCode.PAUSE
                && event.getCode() != KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                paginacion = 1;
                fillTableSuministros((short) 2, "", txtNombre.getText().trim(), 0, 0);
                opcion = 2;
            }
        }
    }

    @FXML
    private void onKeyPressedEtiquetas(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            eventEtiqueta();
        }
    }

    @FXML
    private void onActionEtiquetas(ActionEvent event) {
        eventEtiqueta();
    }

    @FXML
    private void onKeyPressedAnterior(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                if (paginacion > 1) {
                    paginacion--;
                    onEventPaginacion();
                }
            }
        }
    }

    @FXML
    private void onActionAnterior(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (paginacion > 1) {
                paginacion--;
                onEventPaginacion();
            }
        }
    }

    @FXML
    private void onKeyPressedSiguiente(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (!lblLoad.isVisible()) {
                if (paginacion < totalPaginacion) {
                    paginacion++;
                    onEventPaginacion();
                }
            }
        }
    }

    @FXML
    private void onActionSiguiente(ActionEvent event) {
        if (!lblLoad.isVisible()) {
            if (paginacion < totalPaginacion) {
                paginacion++;
                onEventPaginacion();
            }
        }
    }

    public TableView<SuministroTB> getTvList() {
        return tvList;
    }

    public HBox getHbWindow() {
        return hbWindow;
    }

    public void setContent(AnchorPane vbPrincipal, AnchorPane vbContent) {
        this.vbPrincipal = vbPrincipal;
        this.vbContent = vbContent;
    }

}
