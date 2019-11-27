package controller.configuracion.tickets;

import controller.configuracion.impresoras.FxImprimirController;
import controller.tools.FilesRouters;
import controller.tools.Json;
import controller.tools.ObjectGlobal;
import controller.tools.Session;
import controller.tools.TextFieldTicket;
import controller.tools.Tools;
import controller.tools.WindowStage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.TicketADO;
import model.TicketTB;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FxTicketController implements Initializable {

    @FXML
    private VBox window;
    @FXML
    private VBox hbEncabezado;
    @FXML
    private VBox hbDetalleCabecera;
    @FXML
    private VBox hbPie;
    @FXML
    private TextField txtAnchoColumna;
    @FXML
    private ComboBox<Pos> cbAlignment;
    @FXML
    private CheckBox cbMultilinea;
    @FXML
    private CheckBox cbEditable;
    @FXML
    private TextField txtVariable;

    private AnchorPane vbPrincipal;

    private TextFieldTicket reference;

    private int sheetWidth;

    private double pointWidth;

    private int idTicket;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pointWidth = 7.825;
        sheetWidth = 40;
        cbAlignment.getItems().add(Pos.CENTER_LEFT);
        cbAlignment.getItems().add(Pos.CENTER);
        cbAlignment.getItems().add(Pos.CENTER_RIGHT);
    }

    private void editTicket() {
//        File file = new File("./archivos/ticketventa.json");
//        if (file.exists()) {
//            JSONObject jSONObject = obtenerObjetoJSON(leerArchivoTexto(file.getAbsolutePath()));
//            hbEncabezado.getChildren().clear();
//            hbDetalleCabecera.getChildren().clear();
//            hbPie.getChildren().clear();
//            if (jSONObject.get("cabecera") != null) {
//                JSONObject cabeceraObjects = obtenerObjetoJSON(jSONObject.get("cabecera").toString());
//                for (int i = 0; i < cabeceraObjects.size(); i++) {
//                    HBox box = generateElement(hbEncabezado, "cb");
//                    JSONObject objectObtener = obtenerObjetoJSON(cabeceraObjects.get("cb_" + (i + 1)).toString());
//                    if (objectObtener.get("text") != null) {
//                        JSONObject object = obtenerObjetoJSON(objectObtener.get("text").toString());
//                        TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Integer.parseInt(object.get("lines").toString()), Integer.parseInt(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), "");
//                        box.getChildren().add(field);
//                    } else if (objectObtener.get("list") != null) {
//                        JSONArray array = obtenerArrayJSON(objectObtener.get("list").toString());
//                        Iterator it = array.iterator();
//                        while (it.hasNext()) {
//                            JSONObject object = obtenerObjetoJSON(it.next().toString());
//                            TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Integer.parseInt(object.get("lines").toString()), Integer.parseInt(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), "");
//                            box.getChildren().add(field);
//                        }
//                    }
//                }
//            }
//            if (jSONObject.get("detalle") != null) {
//                JSONObject detalleObjects = obtenerObjetoJSON(jSONObject.get("detalle").toString());
//                for (int i = 0; i < detalleObjects.size(); i++) {
//                    HBox box = generateElement(hbDetalleCabecera, "dr");
//                    JSONObject objectObtener = obtenerObjetoJSON(detalleObjects.get("dr_" + (i + 1)).toString());
//                    if (objectObtener.get("text") != null) {
//                        JSONObject object = obtenerObjetoJSON(objectObtener.get("text").toString());
//                        TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Integer.parseInt(object.get("lines").toString()), Integer.parseInt(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), "");
//                        box.getChildren().add(field);
//                    } else if (objectObtener.get("list") != null) {
//                        JSONArray array = obtenerArrayJSON(objectObtener.get("list").toString());
//                        Iterator it = array.iterator();
//                        while (it.hasNext()) {
//                            JSONObject object = obtenerObjetoJSON(it.next().toString());
//                            TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Integer.parseInt(object.get("lines").toString()), Integer.parseInt(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), "");
//                            box.getChildren().add(field);
//                        }
//                    }
//                }
//            }
//            if (jSONObject.get("pie") != null) {
//                JSONObject pieObjects = obtenerObjetoJSON(jSONObject.get("pie").toString());
//                for (int i = 0; i < pieObjects.size(); i++) {
//                    HBox box = generateElement(hbPie, "cp");
//                    JSONObject objectObtener = obtenerObjetoJSON(pieObjects.get("cp_" + (i + 1)).toString());
//                    if (objectObtener.get("text") != null) {
//                        JSONObject object = obtenerObjetoJSON(objectObtener.get("text").toString());
//                        TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Integer.parseInt(object.get("lines").toString()), Integer.parseInt(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), "");
//                        box.getChildren().add(field);
//                    } else if (objectObtener.get("list") != null) {
//                        JSONArray array = obtenerArrayJSON(objectObtener.get("list").toString());
//                        Iterator it = array.iterator();
//                        while (it.hasNext()) {
//                            JSONObject object = obtenerObjetoJSON(it.next().toString());
//                            TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Integer.parseInt(object.get("lines").toString()), Integer.parseInt(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), "");
//                            box.getChildren().add(field);
//                        }
//                    }
//                }
//            }
//        }
    }

    public void loadTicket(int idTicket, String ruta) {
        if (ruta != null) {
            this.idTicket = idTicket;
            JSONObject jSONObject = Json.obtenerObjetoJSON(ruta);
            hbEncabezado.getChildren().clear();
            hbDetalleCabecera.getChildren().clear();
            hbPie.getChildren().clear();
            if (jSONObject.get("cabecera") != null) {
                JSONObject cabeceraObjects = Json.obtenerObjetoJSON(jSONObject.get("cabecera").toString());
                for (int i = 0; i < cabeceraObjects.size(); i++) {
                    HBox box = generateElement(hbEncabezado, "cb");
                    JSONObject objectObtener = Json.obtenerObjetoJSON(cabeceraObjects.get("cb_" + (i + 1)).toString());
                    if (objectObtener.get("text") != null) {
                        JSONObject object = Json.obtenerObjetoJSON(objectObtener.get("text").toString());
                        TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Integer.parseInt(object.get("lines").toString()), Integer.parseInt(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()));
                        box.getChildren().add(field);
                    } else if (objectObtener.get("list") != null) {
                        JSONArray array = Json.obtenerArrayJSON(objectObtener.get("list").toString());
                        Iterator it = array.iterator();
                        while (it.hasNext()) {
                            JSONObject object = Json.obtenerObjetoJSON(it.next().toString());
                            TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Integer.parseInt(object.get("lines").toString()), Integer.parseInt(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()));
                            box.getChildren().add(field);
                        }
                    }
                }
            }
            if (jSONObject.get("detalle") != null) {
                JSONObject detalleObjects = Json.obtenerObjetoJSON(jSONObject.get("detalle").toString());
                for (int i = 0; i < detalleObjects.size(); i++) {
                    HBox box = generateElement(hbDetalleCabecera, "dr");
                    JSONObject objectObtener = Json.obtenerObjetoJSON(detalleObjects.get("dr_" + (i + 1)).toString());
                    if (objectObtener.get("text") != null) {
                        JSONObject object = Json.obtenerObjetoJSON(objectObtener.get("text").toString());
                        TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Integer.parseInt(object.get("lines").toString()), Integer.parseInt(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()));
                        box.getChildren().add(field);
                    } else if (objectObtener.get("list") != null) {
                        JSONArray array = Json.obtenerArrayJSON(objectObtener.get("list").toString());
                        Iterator it = array.iterator();
                        while (it.hasNext()) {
                            JSONObject object = Json.obtenerObjetoJSON(it.next().toString());
                            TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Integer.parseInt(object.get("lines").toString()), Integer.parseInt(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()));
                            box.getChildren().add(field);
                        }
                    }
                }
            }

            if (jSONObject.get("pie") != null) {
                JSONObject pieObjects = Json.obtenerObjetoJSON(jSONObject.get("pie").toString());
                for (int i = 0; i < pieObjects.size(); i++) {
                    HBox box = generateElement(hbPie, "cp");
                    JSONObject objectObtener = Json.obtenerObjetoJSON(pieObjects.get("cp_" + (i + 1)).toString());
                    if (objectObtener.get("text") != null) {
                        JSONObject object = Json.obtenerObjetoJSON(objectObtener.get("text").toString());
                        TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Integer.parseInt(object.get("lines").toString()), Integer.parseInt(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()));
                        box.getChildren().add(field);
                    } else if (objectObtener.get("list") != null) {
                        JSONArray array = Json.obtenerArrayJSON(objectObtener.get("list").toString());
                        Iterator it = array.iterator();
                        while (it.hasNext()) {
                            JSONObject object = Json.obtenerObjetoJSON(it.next().toString());
                            TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Integer.parseInt(object.get("lines").toString()), Integer.parseInt(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()));
                            box.getChildren().add(field);
                        }
                    }
                }
            }
        }
    }

    private Pos getAlignment(String align) {
        switch (align) {
            case "CENTER":
                return Pos.CENTER;
            case "CENTER_LEFT":
                return Pos.CENTER_LEFT;
            case "CENTER_RIGHT":
                return Pos.CENTER_RIGHT;
            default:
                return Pos.CENTER_LEFT;
        }
    }

    private void saveTicket() {
        try {
            if (!hbEncabezado.getChildren().isEmpty() && !hbDetalleCabecera.getChildren().isEmpty() && !hbPie.getChildren().isEmpty()) {
                JSONObject sampleObject = new JSONObject();
                JSONObject cabecera = new JSONObject();
                for (int i = 0; i < hbEncabezado.getChildren().size(); i++) {
                    HBox hBox = (HBox) hbEncabezado.getChildren().get(i);
                    JSONObject cb = new JSONObject();
                    if (hBox.getChildren().size() > 1) {
                        JSONArray kids = new JSONArray();
                        for (int v = 0; v < hBox.getChildren().size(); v++) {
                            TextFieldTicket field = (TextFieldTicket) hBox.getChildren().get(v);
                            JSONObject cbkid = new JSONObject();
                            cbkid.put("value", field.getText());
                            cbkid.put("width", field.getColumnWidth());
                            cbkid.put("align", field.getAlignment().toString());
                            cbkid.put("multiline", field.isMultilineas());
                            cbkid.put("lines", field.getLines());
                            cbkid.put("editable", field.isEditable());
                            cbkid.put("variable", field.getVariable());
                            kids.add(cbkid);
                        }
                        cb.put("list", kids);
                    } else {
                        TextFieldTicket field = (TextFieldTicket) hBox.getChildren().get(0);
                        JSONObject cbkid = new JSONObject();
                        cbkid.put("value", field.getText());
                        cbkid.put("width", field.getColumnWidth());
                        cbkid.put("align", field.getAlignment().toString());
                        cbkid.put("multiline", field.isMultilineas());
                        cbkid.put("lines", field.getLines());
                        cbkid.put("editable", field.isEditable());
                        cbkid.put("variable", field.getVariable());
                        cb.put("text", cbkid);
                    }
                    cabecera.put("cb_" + (i + 1), cb);
                }

                JSONObject detalle = new JSONObject();
                for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                    HBox hBox = (HBox) hbDetalleCabecera.getChildren().get(i);
                    JSONObject cb = new JSONObject();
                    if (hBox.getChildren().size() > 1) {
                        JSONArray kids = new JSONArray();
                        for (int v = 0; v < hBox.getChildren().size(); v++) {
                            TextFieldTicket field = (TextFieldTicket) hBox.getChildren().get(v);
                            JSONObject cbkid = new JSONObject();
                            cbkid.put("value", field.getText());
                            cbkid.put("width", field.getColumnWidth());
                            cbkid.put("align", field.getAlignment().toString());
                            cbkid.put("multiline", field.isMultilineas());
                            cbkid.put("lines", field.getLines());
                            cbkid.put("editable", field.isEditable());
                            cbkid.put("variable", field.getVariable());
                            kids.add(cbkid);
                        }
                        cb.put("list", kids);
                    } else {
                        TextFieldTicket field = (TextFieldTicket) hBox.getChildren().get(0);
                        JSONObject cbkid = new JSONObject();
                        cbkid.put("value", field.getText());
                        cbkid.put("width", field.getColumnWidth());
                        cbkid.put("align", field.getAlignment().toString());
                        cbkid.put("multiline", field.isMultilineas());
                        cbkid.put("lines", field.getLines());
                        cbkid.put("editable", field.isEditable());
                        cbkid.put("variable", field.getVariable());
                        cb.put("text", cbkid);
                    }
                    detalle.put("dr_" + (i + 1), cb);
                }

                JSONObject pie = new JSONObject();
                for (int i = 0; i < hbPie.getChildren().size(); i++) {
                    HBox hBox = (HBox) hbPie.getChildren().get(i);
                    JSONObject cb = new JSONObject();
                    if (hBox.getChildren().size() > 1) {
                        JSONArray kids = new JSONArray();
                        for (int v = 0; v < hBox.getChildren().size(); v++) {
                            TextFieldTicket field = (TextFieldTicket) hBox.getChildren().get(v);
                            JSONObject cbkid = new JSONObject();
                            cbkid.put("value", field.getText());
                            cbkid.put("width", field.getColumnWidth());
                            cbkid.put("align", field.getAlignment().toString());
                            cbkid.put("multiline", field.isMultilineas());
                            cbkid.put("lines", field.getLines());
                            cbkid.put("editable", field.isEditable());
                            cbkid.put("variable", field.getVariable());
                            kids.add(cbkid);
                        }
                        cb.put("list", kids);
                    } else {
                        TextFieldTicket field = (TextFieldTicket) hBox.getChildren().get(0);
                        JSONObject cbkid = new JSONObject();
                        cbkid.put("value", field.getText());
                        cbkid.put("width", field.getColumnWidth());
                        cbkid.put("align", field.getAlignment().toString());
                        cbkid.put("multiline", field.isMultilineas());

                        cbkid.put("lines", field.getLines());
                        cbkid.put("editable", field.isEditable());
                        cbkid.put("variable", field.getVariable());
                        cb.put("text", cbkid);
                    }
                    pie.put("cp_" + (i + 1), cb);
                }

                sampleObject.put("cabecera", cabecera);
                sampleObject.put("detalle", detalle);
                sampleObject.put("pie", pie);

                Files.write(Paths.get("./archivos/ticketventa.json"), sampleObject.toJSONString().getBytes());
                TicketTB ticketTB = new TicketTB();
                ticketTB.setId(idTicket);
                ticketTB.setNombreTicket("Venta(s)");
                ticketTB.setRuta(sampleObject.toJSONString());
                String result = TicketADO.CrudTicket(ticketTB);
                if (result.equalsIgnoreCase("duplicate")) {
                    Tools.AlertMessageWarning(window, "Ticket", "El nombre del formato ya existe, intente con otro.");
                } else if (result.equalsIgnoreCase("updated")) {
                    Tools.AlertMessageInformation(window, "Ticket", "Se actualizo correctamente el formato.");
                    Session.RUTA_TICKET_VENTA = sampleObject.toJSONString();
                } else if (result.equalsIgnoreCase("registered")) {
                    Tools.AlertMessageInformation(window, "Ticket", "Se guardo correctamente el formato.");
                    Session.RUTA_TICKET_VENTA = sampleObject.toJSONString();
                } else {
                    Tools.AlertMessageError(window, "Ticket", result);
                }
            } else {
                if (hbEncabezado.getChildren().isEmpty()) {
                    Tools.AlertMessageWarning(window, "Ticket", "El encabezado está vacío.");
                } else if (hbDetalleCabecera.getChildren().isEmpty()) {
                    Tools.AlertMessageWarning(window, "Ticket", "El detalle cabecera está vacío.");
                } else if (hbPie.getChildren().isEmpty()) {
                    Tools.AlertMessageWarning(window, "Ticket", "El pie está vacío.");
                }
            }

        } catch (IOException ex) {
            Tools.AlertMessageError(window, "Ticket", "No se pudo guardar la hoja con problemas de formato.");
            System.out.println(ex.getLocalizedMessage());
            System.out.println(ex);
        }

    }

    private void searchTicket() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_TICKET_BUSQUEDA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxTicketBusquedaController controller = fXMLLoader.getController();
            controller.setInitTicketController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccionar formato", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void procesoTicket() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_TICKET_PROCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxTicketProcesoController controller = fXMLLoader.getController();
            controller.setInitTicketController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Nuevo formato", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void windowTextMultilinea(HBox hBox) {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_TICKET_MULTILINEA);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxTicketMultilineaController controller = fXMLLoader.getController();
            controller.setInitTicketController(this);
            controller.setLoadComponent(hBox, sheetWidth);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar texto multilinea", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void windowTextVar(HBox hBox) {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_TICKET_VARIABLE);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxTicketVariableController controller = fXMLLoader.getController();
            controller.setInitTicketController(this);
            controller.setLoadComponent(hBox, sheetWidth);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar variable", window.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private HBox addElement(VBox contenedor, String id) {
        ImageView imgRemove = new ImageView(new Image("/view/image/remove-item.png"));
        imgRemove.setFitWidth(20);
        imgRemove.setFitHeight(20);

        ImageView imgText = new ImageView(new Image("/view/image/text.png"));
        imgText.setFitWidth(20);
        imgText.setFitHeight(20);

        ImageView imgTextVariable = new ImageView(new Image("/view/image/text-variable.png"));
        imgTextVariable.setFitWidth(20);
        imgTextVariable.setFitHeight(20);

        ImageView imgTextLines = new ImageView(new Image("/view/image/text-lines.png"));
        imgTextLines.setFitWidth(20);
        imgTextLines.setFitHeight(20);

        ImageView imgUnaLinea = new ImageView(new Image("/view/image/linea.png"));
        imgUnaLinea.setFitWidth(20);
        imgUnaLinea.setFitHeight(20);

        ImageView imgDobleLinea = new ImageView(new Image("/view/image/doble-linea.png"));
        imgDobleLinea.setFitWidth(20);
        imgDobleLinea.setFitHeight(20);

        HBox hBox = new HBox();
        hBox.setId(id);
        hBox.setPrefHeight(30);
        hBox.setStyle("-fx-padding:0 10 0 0;-fx-border-width: 1 1 1 1;-fx-border-color: #0066ff;");
        hBox.addEventHandler(MouseEvent.MOUSE_PRESSED, (Event event) -> {
            hBox.setStyle("-fx-padding:0 10 0 0;-fx-border-width: 1 1 1 1;-fx-border-color: #0066ff;-fx-background-color: rgb(250, 198, 203);");
        });
        hBox.addEventHandler(MouseEvent.MOUSE_EXITED, (Event event) -> {
            hBox.setStyle("-fx-padding:0 10 0 0;-fx-border-width: 1 1 1 1;-fx-border-color: #0066ff;-fx-background-color: white;");
        });

        ContextMenu contextMenu = new ContextMenu();
        MenuItem remove = new MenuItem("Remover Renglón");
        remove.setGraphic(imgRemove);
        remove.setOnAction((ActionEvent event) -> {
            contenedor.getChildren().remove(hBox);
        });
        MenuItem text = new MenuItem("Agregar Texto");
        text.setGraphic(imgText);
        text.setOnAction((ActionEvent event) -> {
            TextFieldTicket field = addElementTextField("iu", "Escriba aqui.", false, 0, 13, Pos.CENTER_LEFT, true, "");
            hBox.getChildren().add(field);
        });
        MenuItem textVariable = new MenuItem("Agregar Variable");
        textVariable.setGraphic(imgTextVariable);
        textVariable.setOnAction((ActionEvent event) -> {
            windowTextVar(hBox);
        });
        MenuItem textMultilinea = new MenuItem("Agregar Texto Multilínea");
        textMultilinea.setGraphic(imgTextLines);
        textMultilinea.setOnAction((ActionEvent event) -> {
            windowTextMultilinea(hBox);
        });
        MenuItem textUnaLinea = new MenuItem("Agregar Línea");
        textUnaLinea.setGraphic(imgUnaLinea);
        textUnaLinea.setOnAction((ActionEvent event) -> {
            String value = "";
            for (int i = 0; i < sheetWidth; i++) {
                value += "-";
            }
            TextFieldTicket field = addElementTextField("iu", value, false, 0, sheetWidth, Pos.CENTER_LEFT, false, "");
            hBox.getChildren().add(field);
        });
        MenuItem textDosLineas = new MenuItem("Agregar Doble Línea");
        textDosLineas.setGraphic(imgDobleLinea);
        textDosLineas.setOnAction((ActionEvent event) -> {
            String value = "";
            for (int i = 0; i < sheetWidth; i++) {
                value += "=";
            }
            TextFieldTicket field = addElementTextField("iu", value, false, 0, sheetWidth, Pos.CENTER_LEFT, false, "");
            hBox.getChildren().add(field);
        });

        contextMenu.getItems().addAll(text, textVariable, textMultilinea, textUnaLinea, textDosLineas, remove);
        hBox.setOnContextMenuRequested((ContextMenuEvent event) -> {
            contextMenu.show(hBox, event.getSceneX(), event.getSceneY());
            int widthContent = 0;
            for (int i = 0; i < hBox.getChildren().size(); i++) {
                widthContent += ((TextFieldTicket) hBox.getChildren().get(i)).getColumnWidth();
            }
            if ((widthContent + 13) > sheetWidth) {
                text.setDisable(true);
            } else {
                text.setDisable(false);
            }

            int widthNew = sheetWidth - widthContent;

            if ((widthContent + widthNew) <= sheetWidth) {
                textMultilinea.setDisable(false);
            } else {
                textMultilinea.setDisable(true);
            }

            if ((widthContent + sheetWidth) > sheetWidth) {
                textUnaLinea.setDisable(true);
            } else {
                textUnaLinea.setDisable(false);
            }
            if ((widthContent + sheetWidth) > sheetWidth) {
                textDosLineas.setDisable(true);
            } else {
                textDosLineas.setDisable(false);
            }
        });
        contenedor.getChildren().add(hBox);
        return hBox;
    }

    public TextFieldTicket addElementTextField(String id, String titulo, boolean multilinea, int lines, int widthColumn, Pos align, boolean editable, String variable) {
        TextFieldTicket field = new TextFieldTicket(titulo, id);
        field.setMultilineas(multilinea);
        field.setLines(lines);
        field.setColumnWidth(widthColumn);
        field.setVariable(variable);
        field.setEditable(editable);
        field.setPreferredSize((double) widthColumn * pointWidth, 30);
        field.getStyleClass().add("text-field-ticket");
        field.setAlignment(align);
        field.focusedProperty().addListener((ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) -> {
            if (newPropertyValue) {
                field.setStyle("-fx-background-color: #cecece;");
                txtAnchoColumna.setText(field.getColumnWidth() + "");
                reference = field;
                cbAlignment.getItems().forEach(e -> {
                    if (e.equals(field.getAlignment())) {
                        cbAlignment.getSelectionModel().select(e);
                    }
                });
                cbMultilinea.setSelected(field.isMultilineas());
                cbMultilinea.setText(field.isMultilineas() ? "Si" : "No");

                cbEditable.setSelected(field.isEditable());
                cbEditable.setText(field.isEditable() ? "Si" : "No");

                txtVariable.setText(field.getVariable());
            } else {
                field.setStyle("-fx-background-color: white;");
            }
        });

        field.lengthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (!field.isMultilineas()) {
                if (newValue.intValue() > oldValue.intValue()) {
                    if (field.getText().length() >= field.getColumnWidth()) {
                        field.setText(field.getText().substring(0, field.getColumnWidth()));
                    }
                }
            }
        });

        ImageView imgRemove = new ImageView(new Image("/view/image/remove-item.png"));
        imgRemove.setFitWidth(20);
        imgRemove.setFitHeight(20);

        ImageView imgAdaptParentWidth = new ImageView(new Image("/view/image/width-parent.png"));
        imgAdaptParentWidth.setFitWidth(20);
        imgAdaptParentWidth.setFitHeight(20);

        ImageView imgTextLeft = new ImageView(new Image("/view/image/text-left.png"));
        imgTextLeft.setFitWidth(20);
        imgTextLeft.setFitHeight(20);

        ImageView imgTextCenter = new ImageView(new Image("/view/image/text-center.png"));
        imgTextCenter.setFitWidth(20);
        imgTextCenter.setFitHeight(20);

        ImageView imgTextRight = new ImageView(new Image("/view/image/text-right.png"));
        imgTextRight.setFitWidth(20);
        imgTextRight.setFitHeight(20);

        ContextMenu contextMenu = new ContextMenu();

        MenuItem remove = new MenuItem("Remover Campo de Texto");
        remove.setGraphic(imgRemove);
        remove.setOnAction((ActionEvent event) -> {
            ((HBox) field.getParent()).getChildren().remove(field);
        });
        MenuItem textAdaptWidth = new MenuItem("Adaptar el Ancho del Padre");
        textAdaptWidth.setGraphic(imgAdaptParentWidth);
        textAdaptWidth.setOnAction((ActionEvent event) -> {
            field.setColumnWidth(sheetWidth);
            field.setPrefWidth(((double) field.getColumnWidth() * pointWidth));
        });
        MenuItem textLeft = new MenuItem("Alineación Izquierda");
        textLeft.setGraphic(imgTextLeft);
        textLeft.setOnAction((ActionEvent event) -> {
            if (!field.getText().isEmpty()) {
                field.setAlignment(Pos.CENTER_LEFT);
            }
        });
        MenuItem textCenter = new MenuItem("Alineación Central");
        textCenter.setGraphic(imgTextCenter);
        textCenter.setOnAction((ActionEvent event) -> {
            if (!field.getText().isEmpty()) {
                field.setAlignment(Pos.CENTER);
            }
        });
        MenuItem textRight = new MenuItem("Alineación Derecha");
        textRight.setGraphic(imgTextRight);
        textRight.setOnAction((ActionEvent event) -> {
            if (!field.getText().isEmpty()) {
                field.setAlignment(Pos.CENTER_RIGHT);
            }
        });
        contextMenu.getItems().addAll(remove, textAdaptWidth, new SeparatorMenuItem(), textLeft, textCenter, textRight);
        field.setContextMenu(contextMenu);
        field.setOnContextMenuRequested((event) -> {
            if (((HBox) field.getParent()).getChildren().size() > 1) {
                textAdaptWidth.setDisable(true);
            }
            if (field.isMultilineas()) {
                textLeft.setDisable(true);
                textCenter.setDisable(true);
                textRight.setDisable(true);
            }
        });
        return field;
    }

    private void actionAnchoColumnas() {
        if (reference != null) {
            if (Tools.isNumeric(txtAnchoColumna.getText())) {
                int widthContent = 0;
                for (int i = 0; i < ((HBox) reference.getParent()).getChildren().size(); i++) {
                    TextFieldTicket fieldTicket = ((TextFieldTicket) ((HBox) reference.getParent()).getChildren().get(i));
                    if (fieldTicket != reference) {
                        widthContent += fieldTicket.getColumnWidth();
                    }
                }
                if ((widthContent + Integer.parseInt(txtAnchoColumna.getText())) > sheetWidth) {
                    Tools.AlertMessageWarning(window, "Ticket", "No puede sobrepasar al ancho de la hoja.");
                } else {
                    if (!reference.isMultilineas() && reference.isEditable()) {
                        if (reference.getText().length() < Integer.parseInt(txtAnchoColumna.getText())) {
                            reference.setColumnWidth(Integer.parseInt(txtAnchoColumna.getText()));
                            reference.setPrefWidth(((double) reference.getColumnWidth() * pointWidth));
                        } else {
                            reference.setColumnWidth(Integer.parseInt(txtAnchoColumna.getText()));
                            reference.setPrefWidth(((double) reference.getColumnWidth() * pointWidth));
                            reference.setText(reference.getText().substring(0, reference.getColumnWidth()));
                        }
                    } else {
                        if (reference.getText().length() < Integer.parseInt(txtAnchoColumna.getText())) {
                            reference.setColumnWidth(Integer.parseInt(txtAnchoColumna.getText()));
                            reference.setPrefWidth(((double) reference.getColumnWidth() * pointWidth));
                        } else {
                            reference.setColumnWidth(Integer.parseInt(txtAnchoColumna.getText()));
                            reference.setPrefWidth(((double) reference.getColumnWidth() * pointWidth));
                        }
                    }
                }
            }
        }
    }

    private HBox generateElement(VBox contenedor, String id) {
        if (contenedor.getChildren().isEmpty()) {
            return addElement(contenedor, id + "1");
        } else {
            HBox hBox = (HBox) contenedor.getChildren().get(contenedor.getChildren().size() - 1);
            String idGenerate = hBox.getId();
            String codigo = idGenerate.substring(2);
            int valor = Integer.parseInt(codigo) + 1;
            String newCodigo = id + valor;
            return addElement(contenedor, newCodigo);
        }
    }

    private void openWindowImpresora() {
        try {
            if (!hbEncabezado.getChildren().isEmpty() && !hbDetalleCabecera.getChildren().isEmpty() && !hbPie.getChildren().isEmpty()) {

                ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                URL url = getClass().getResource(FilesRouters.FX_IMPRIMIR);
                FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                Parent parent = fXMLLoader.load(url.openStream());
                //Controlller here
                FxImprimirController controller = fXMLLoader.getController();
                controller.setInitTicketController(this);
                //
                Stage stage = WindowStage.StageLoaderModal(parent, "Imprimir Prueba", window.getScene().getWindow());
                stage.setResizable(false);
                stage.sizeToScene();
                stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
                stage.show();

            } else {
                if (hbEncabezado.getChildren().isEmpty()) {
                    Tools.AlertMessageWarning(window, "Ticket", "El encabezado está vacío.");
                } else if (hbDetalleCabecera.getChildren().isEmpty()) {
                    Tools.AlertMessageWarning(window, "Ticket", "El detalle cabecera está vacío.");
                } else if (hbPie.getChildren().isEmpty()) {
                    Tools.AlertMessageWarning(window, "Ticket", "El pie está vacío.");
                }
            }
        } catch (IOException ex) {

        }
    }

    @FXML
    private void onKeyPressNuevo(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            procesoTicket();
        }

    }

    @FXML
    private void onActionNuevo(ActionEvent event) {
        procesoTicket();
    }

    private void onKeyPressEditar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            editTicket();
        }
    }

    private void onActionEditar(ActionEvent event) {
        editTicket();
    }

    @FXML
    private void onKeyPressedSave(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            saveTicket();
        }
    }

    @FXML
    private void onActionSave(ActionEvent event) {
        saveTicket();
    }

    @FXML
    private void onKeyPressedPrint(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            openWindowImpresora();
        }
    }

    @FXML
    private void onActionPrint(ActionEvent event) {
        openWindowImpresora();
    }

    @FXML
    private void onMouseClickedEncabezadoAdd(MouseEvent event) {
        generateElement(hbEncabezado, "cb");
    }

    @FXML
    private void onMouseClickedPieAdd(MouseEvent event) {
        if (hbPie.getChildren().isEmpty()) {
            addElement(hbPie, "cp1");
        } else {
            HBox hBox = (HBox) hbPie.getChildren().get(hbPie.getChildren().size() - 1);
            String idGenerate = hBox.getId();
            String codigo = idGenerate.substring(2);
            int valor = Integer.parseInt(codigo) + 1;
            String newCodigo = "cp" + valor;
            addElement(hbPie, newCodigo);
        }
    }

    @FXML
    private void onMouseClickedDetalleCabeceraAdd(MouseEvent event) {
        if (hbDetalleCabecera.getChildren().isEmpty()) {
            addElement(hbDetalleCabecera, "dr1");
        } else {
            HBox hBox = (HBox) hbDetalleCabecera.getChildren().get(hbDetalleCabecera.getChildren().size() - 1);
            String idGenerate = hBox.getId();
            String codigo = idGenerate.substring(2);
            int valor = Integer.parseInt(codigo) + 1;
            String newCodigo = "dr" + valor;
            addElement(hbDetalleCabecera, newCodigo);
        }
    }

    @FXML
    private void onActionAnchoColumna(ActionEvent event) {
        actionAnchoColumnas();
    }

    @FXML
    private void onKeyTypedAnchoColumna(KeyEvent event) {
        char c = event.getCharacter().charAt(0);
        if ((c < '0' || c > '9') && (c != '\b')) {
            event.consume();
        }
    }

    @FXML
    private void onKeyPressedSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            searchTicket();
        }
    }

    @FXML
    private void onActionSearch(ActionEvent event) {
        searchTicket();
    }

    @FXML
    private void onActionMultilinea(ActionEvent event) {
        if (reference != null) {
            if (!cbMultilinea.isSelected()) {
                reference.setMultilineas(false);
                reference.setLines(0);
            } else {
                reference.setMultilineas(true);
                reference.setLines(1);
            }
        }
    }

    @FXML
    private void onKeyPressedClear(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            hbEncabezado.getChildren().clear();
            hbDetalleCabecera.getChildren().clear();
            hbPie.getChildren().clear();
            txtAnchoColumna.setText("");
            cbAlignment.getSelectionModel().select(-1);
            cbMultilinea.setSelected(false);
            cbEditable.setSelected(false);
            txtVariable.setText("");
        }
    }

    @FXML
    private void onActionClear(ActionEvent event) {
        hbEncabezado.getChildren().clear();
        hbDetalleCabecera.getChildren().clear();
        hbPie.getChildren().clear();
        txtAnchoColumna.setText("");
        cbAlignment.getSelectionModel().select(-1);
        cbMultilinea.setSelected(false);
        cbEditable.setSelected(false);
        txtVariable.setText("");
    }

    public VBox getHbEncabezado() {
        return hbEncabezado;
    }

    public VBox getHbDetalleCabecera() {
        return hbDetalleCabecera;
    }

    public VBox getHbPie() {
        return hbPie;
    }

    public int getSheetWidth() {
        return sheetWidth;
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

}
