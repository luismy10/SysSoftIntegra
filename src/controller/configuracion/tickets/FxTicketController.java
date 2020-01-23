package controller.configuracion.tickets;

import controller.configuracion.impresoras.FxImprimirController;
import controller.tools.FilesRouters;
import controller.tools.HBoxModel;
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
import javafx.scene.control.Control;
import javafx.scene.control.Label;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.TicketADO;
import model.TicketTB;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FxTicketController implements Initializable {

    @FXML
    private VBox vbWindow;
    @FXML
    private VBox vbContenedor;
    @FXML
    private AnchorPane apEncabezado;
//    private VBox hbEncabezado;
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
    @FXML
    private Label lblNombre;
    @FXML
    private Label lblColumnas;

    private AnchorPane vbPrincipal;

    private TextFieldTicket reference;

    private HBoxModel HboxReference;

    private short sheetWidth;

    private double pointWidth;

    private int idTicket;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pointWidth = 7.825;
        sheetWidth = 0;
        cbAlignment.getItems().add(Pos.CENTER_LEFT);
        cbAlignment.getItems().add(Pos.CENTER);
        cbAlignment.getItems().add(Pos.CENTER_RIGHT);
    }

    public void loadTicket(int idTicket, String nombre, String ruta) {
        if (ruta != null) {
            this.idTicket = idTicket;
            JSONObject jSONObject = Json.obtenerObjetoJSON(ruta);
            apEncabezado.getChildren().clear();
            hbDetalleCabecera.getChildren().clear();
            hbPie.getChildren().clear();
            lblNombre.setText(nombre);

            sheetWidth = jSONObject.get("column") != null ? Short.parseShort(jSONObject.get("column").toString()) : (short) 40;
            lblColumnas.setText("" + sheetWidth);

            vbContenedor.setPrefWidth(sheetWidth * pointWidth);

            if (jSONObject.get("cabecera") != null) {
                JSONObject cabeceraObjects = Json.obtenerObjetoJSON(jSONObject.get("cabecera").toString());
                for (int i = 0; i < cabeceraObjects.size(); i++) {
                    HBoxModel box = generateElement(apEncabezado, "cb");
                    JSONObject objectObtener = Json.obtenerObjetoJSON(cabeceraObjects.get("cb_" + (i + 1)).toString());
                    if (objectObtener.get("text") != null) {
                        JSONObject object = Json.obtenerObjetoJSON(objectObtener.get("text").toString());
                        TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Short.parseShort(object.get("lines").toString()), Short.parseShort(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()));
                        box.getChildren().add(field);
                    } else if (objectObtener.get("list") != null) {
                        JSONArray array = Json.obtenerArrayJSON(objectObtener.get("list").toString());
                        Iterator it = array.iterator();
                        while (it.hasNext()) {
                            JSONObject object = Json.obtenerObjetoJSON(it.next().toString());
                            TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Short.parseShort(object.get("lines").toString()), Short.parseShort(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()));
                            box.getChildren().add(field);
                        }
                    }
                }
            }
            if (jSONObject.get("detalle") != null) {
                JSONObject detalleObjects = Json.obtenerObjetoJSON(jSONObject.get("detalle").toString());
                for (int i = 0; i < detalleObjects.size(); i++) {
//                    HBox box = generateElement(hbDetalleCabecera, "dr");
//                    JSONObject objectObtener = Json.obtenerObjetoJSON(detalleObjects.get("dr_" + (i + 1)).toString());
//                    if (objectObtener.get("text") != null) {
//                        JSONObject object = Json.obtenerObjetoJSON(objectObtener.get("text").toString());
//                        TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Short.parseShort(object.get("lines").toString()), Short.parseShort(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()));
//                        box.getChildren().add(field);
//                    } else if (objectObtener.get("list") != null) {
//                        JSONArray array = Json.obtenerArrayJSON(objectObtener.get("list").toString());
//                        Iterator it = array.iterator();
//                        while (it.hasNext()) {
//                            JSONObject object = Json.obtenerObjetoJSON(it.next().toString());
//                            TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Short.parseShort(object.get("lines").toString()), Short.parseShort(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()));
//                            box.getChildren().add(field);
//                        }
//                    }
//                }
                }

                if (jSONObject.get("pie") != null) {
                    JSONObject pieObjects = Json.obtenerObjetoJSON(jSONObject.get("pie").toString());
                    for (int i = 0; i < pieObjects.size(); i++) {
//                    HBox box = generateElement(hbPie, "cp");
//                    JSONObject objectObtener = Json.obtenerObjetoJSON(pieObjects.get("cp_" + (i + 1)).toString());
//                    if (objectObtener.get("text") != null) {
//                        JSONObject object = Json.obtenerObjetoJSON(objectObtener.get("text").toString());
//                        TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Short.parseShort(object.get("lines").toString()), Short.parseShort(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()));
//                        box.getChildren().add(field);
//                    } else if (objectObtener.get("list") != null) {
//                        JSONArray array = Json.obtenerArrayJSON(objectObtener.get("list").toString());
//                        Iterator it = array.iterator();
//                        while (it.hasNext()) {
//                            JSONObject object = Json.obtenerObjetoJSON(it.next().toString());
//                            TextFieldTicket field = addElementTextField("iu", object.get("value").toString(), Boolean.valueOf(object.get("multiline").toString()), Short.parseShort(object.get("lines").toString()), Short.parseShort(object.get("width").toString()), getAlignment(object.get("align").toString()), Boolean.parseBoolean(object.get("editable").toString()), String.valueOf(object.get("variable").toString()));
//                            box.getChildren().add(field);
//                        }
                    }
                }
            }
        }
    }

    private void saveTicket() {
        try {
            int valideteUno = 0;
            int valideteDos = 0;
            int valideteTree = 0;
            for (int i = 0; i < apEncabezado.getChildren().size(); i++) {
                HBoxModel hBox = (HBoxModel) apEncabezado.getChildren().get(i);
                if (hBox.getChildren().isEmpty()) {
                    valideteUno++;
                }
            }

            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                HBoxModel hBox = (HBoxModel) hbDetalleCabecera.getChildren().get(i);
                if (hBox.getChildren().isEmpty()) {
                    valideteDos++;
                }
            }

            for (int i = 0; i < hbPie.getChildren().size(); i++) {
                HBoxModel hBox = (HBoxModel) hbPie.getChildren().get(i);
                if (hBox.getChildren().isEmpty()) {
                    valideteTree++;
                }
            }

            if (valideteUno > 0) {
                Tools.AlertMessageWarning(vbWindow, "Ticket", "Hay una fila en la cabecera sin contenido");
            } else if (valideteDos > 0) {
                Tools.AlertMessageWarning(vbWindow, "Ticket", "Hay una fila en el detalle sin contenido");
            } else if (valideteTree > 0) {
                Tools.AlertMessageWarning(vbWindow, "Ticket", "Hay una fila en el pie sin contenido");
            } else {
                if (!apEncabezado.getChildren().isEmpty() && !hbDetalleCabecera.getChildren().isEmpty() && !hbPie.getChildren().isEmpty()) {
                    JSONObject sampleObject = new JSONObject();
                    sampleObject.put("column", sheetWidth);
                    JSONObject cabecera = new JSONObject();
                    for (int i = 0; i < apEncabezado.getChildren().size(); i++) {
                        HBoxModel hBox = (HBoxModel) apEncabezado.getChildren().get(i);
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
                        HBoxModel hBox = (HBoxModel) hbDetalleCabecera.getChildren().get(i);
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
                        HBoxModel hBox = (HBoxModel) hbPie.getChildren().get(i);
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
                    ticketTB.setNombreTicket(lblNombre.getText());
                    ticketTB.setRuta(sampleObject.toJSONString());
                    String result = TicketADO.CrudTicket(ticketTB);
                    if (result.equalsIgnoreCase("duplicate")) {
                        Tools.AlertMessageWarning(vbWindow, "Ticket", "El nombre del formato ya existe, intente con otro.");
                    } else if (result.equalsIgnoreCase("updated")) {
                        Tools.AlertMessageInformation(vbWindow, "Ticket", "Se actualizo correctamente el formato.");
                        Session.RUTA_TICKET_VENTA = sampleObject.toJSONString();
                    } else if (result.equalsIgnoreCase("registered")) {
                        Tools.AlertMessageInformation(vbWindow, "Ticket", "Se guardo correctamente el formato.");
                        Session.RUTA_TICKET_VENTA = sampleObject.toJSONString();
                    } else {
                        Tools.AlertMessageError(vbWindow, "Ticket", result);
                    }
                } else {
                    if (apEncabezado.getChildren().isEmpty()) {
                        Tools.AlertMessageWarning(vbWindow, "Ticket", "El encabezado está vacío.");
                    } else if (hbDetalleCabecera.getChildren().isEmpty()) {
                        Tools.AlertMessageWarning(vbWindow, "Ticket", "El detalle cabecera está vacío.");
                    } else if (hbPie.getChildren().isEmpty()) {
                        Tools.AlertMessageWarning(vbWindow, "Ticket", "El pie está vacío.");
                    }
                }
            }
        } catch (IOException ex) {
            Tools.AlertMessageError(vbWindow, "Ticket", "No se pudo guardar la hoja con problemas de formato.");
            System.out.println(ex.getLocalizedMessage());
            System.out.println(ex);
        }
    }

    private HBoxModel generateElement(AnchorPane contenedor, String id) {
        if (contenedor.getChildren().isEmpty()) {
            return addElement(contenedor, id + "1", true);
        } else {
            HBoxModel hBox = (HBoxModel) contenedor.getChildren().get(contenedor.getChildren().size() - 1);
            String idGenerate = hBox.getId();
            String codigo = idGenerate.substring(2);
            int valor = Integer.parseInt(codigo) + 1;
            String newCodigo = id + valor;
            return addElement(contenedor, newCodigo, true);
        }
    }

    private HBoxModel addElement(AnchorPane contenedor, String id, boolean useLayout) {
        double layoutY = 0;
        if (useLayout) {
            for (int i = 0; i < contenedor.getChildren().size(); i++) {
                layoutY += ((HBoxModel) contenedor.getChildren().get(i)).getPrefHeight();
            }
        }

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

        HBoxModel hBox = new HBoxModel();
        hBox.setId(id);
        hBox.setLayoutX(0);
        hBox.setLayoutY(layoutY);
        hBox.setPrefWidth(sheetWidth * pointWidth);
        hBox.setPrefHeight(30);
        hBox.setStyle("-fx-padding:0 10 0 0;-fx-border-width: 1 1 1 1;-fx-border-color: #0066ff;");
        hBox.addEventHandler(MouseEvent.MOUSE_PRESSED, (Event event) -> {
            hBox.setStyle("-fx-padding:0 10 0 0;-fx-border-width: 1 1 1 1;-fx-border-color: #0066ff;-fx-background-color: rgb(250, 198, 203);");
            HboxReference = hBox;
        });
        hBox.addEventHandler(MouseEvent.MOUSE_EXITED, (Event event) -> {
            hBox.setStyle("-fx-padding:0 10 0 0;-fx-border-width: 1 1 1 1;-fx-border-color: #0066ff;-fx-background-color: white;");
        });

        ContextMenu contextMenu = new ContextMenu();
        MenuItem remove = new MenuItem("Remover Renglón");
        remove.setGraphic(imgRemove);
        remove.setOnAction(e -> {
            contenedor.getChildren().remove(hBox);
        });

        MenuItem text = new MenuItem("Agregar Texto");
        text.setGraphic(imgText);
        text.setOnAction(e -> {
            TextFieldTicket field = addElementTextField("iu", "Escriba aqui.", false, (short) 0, (short) 13, Pos.CENTER_LEFT, true, "");
            hBox.getChildren().add(field);
        });

        MenuItem textVariable = new MenuItem("Agregar Variable");
        textVariable.setGraphic(imgTextVariable);
        textVariable.setOnAction(e -> {
            windowTextVar(hBox);
        });

        MenuItem textMultilinea = new MenuItem("Agregar Texto Multilínea");
        textMultilinea.setGraphic(imgTextLines);
        textMultilinea.setOnAction(e -> {
            windowTextMultilinea(hBox);
        });

        MenuItem textUnaLinea = new MenuItem("Agregar Línea");
        textUnaLinea.setGraphic(imgUnaLinea);
        textUnaLinea.setOnAction(e -> {
            String value = "";
            for (int i = 0; i < sheetWidth; i++) {
                value += "-";
            }
            TextFieldTicket field = addElementTextField("iu", value, false, (short) 0, sheetWidth, Pos.CENTER_LEFT, false, "");
            hBox.getChildren().add(field);
        });

        MenuItem textDosLineas = new MenuItem("Agregar Doble Línea");
        textDosLineas.setGraphic(imgDobleLinea);
        textDosLineas.setOnAction(e -> {
            String value = "";
            for (int i = 0; i < sheetWidth; i++) {
                value += "=";
            }
            TextFieldTicket field = addElementTextField("iu", value, false, (short) 0, sheetWidth, Pos.CENTER_LEFT, false, "");
            hBox.getChildren().add(field);
        });

        contextMenu.getItems().addAll(text, textVariable, textMultilinea, textUnaLinea, textDosLineas, remove);
        hBox.setMenuItem(text);
        hBox.setOnContextMenuRequested((ContextMenuEvent event) -> {
            contextMenu.show(hBox, event.getSceneX(), event.getSceneY());
            short widthContent = 0;
            for (int i = 0; i < hBox.getChildren().size(); i++) {
                widthContent += ((TextFieldTicket) hBox.getChildren().get(i)).getColumnWidth();
            }

            text.setDisable((widthContent + 13) > sheetWidth);

            textUnaLinea.setDisable((widthContent + 13) > sheetWidth);

            textDosLineas.setDisable((widthContent + 13) > sheetWidth);

        });
        if (useLayout) {
            contenedor.getChildren().add(hBox);
        }
        return hBox;
    }

    public TextFieldTicket addElementTextField(String id, String titulo, boolean multilinea, short lines, short widthColumn, Pos align, boolean editable, String variable) {
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
        remove.setOnAction(e -> {
            ((HBoxModel) field.getParent()).getChildren().remove(field);
        });
        MenuItem textAdaptWidth = new MenuItem("Adaptar el Ancho del Padre");
        textAdaptWidth.setGraphic(imgAdaptParentWidth);
        textAdaptWidth.setOnAction(e -> {
            field.setColumnWidth(sheetWidth);
            field.setPrefWidth(((double) field.getColumnWidth() * pointWidth));
        });

        MenuItem textLeft = new MenuItem("Alineación Izquierda");
        textLeft.setGraphic(imgTextLeft);
        textLeft.setOnAction(e -> {
            if (!field.getText().isEmpty()) {
                field.setAlignment(Pos.CENTER_LEFT);
            }
        });
        MenuItem textCenter = new MenuItem("Alineación Central");
        textCenter.setGraphic(imgTextCenter);
        textCenter.setOnAction(e -> {
            if (!field.getText().isEmpty()) {
                field.setAlignment(Pos.CENTER);
            }
        });
        MenuItem textRight = new MenuItem("Alineación Derecha");
        textRight.setGraphic(imgTextRight);
        textRight.setOnAction(e -> {
            if (!field.getText().isEmpty()) {
                field.setAlignment(Pos.CENTER_RIGHT);
            }
        });
        contextMenu.getItems().addAll(remove, textAdaptWidth, new SeparatorMenuItem(), textLeft, textCenter, textRight);
        field.setContextMenu(contextMenu);
        field.setOnContextMenuRequested((event) -> {
            if (((HBoxModel) field.getParent()).getChildren().size() > 1) {
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
                for (int i = 0; i < ((HBoxModel) reference.getParent()).getChildren().size(); i++) {
                    TextFieldTicket fieldTicket = ((TextFieldTicket) ((HBoxModel) reference.getParent()).getChildren().get(i));
                    if (fieldTicket != reference) {
                        widthContent += fieldTicket.getColumnWidth();
                    }
                }
                if ((widthContent + Integer.parseInt(txtAnchoColumna.getText())) > sheetWidth) {
                    Tools.AlertMessageWarning(vbWindow, "Ticket", "No puede sobrepasar al ancho de la hoja.");
                } else {
                    if (!reference.isMultilineas() && reference.isEditable()) {
                        if (reference.getText().length() < Integer.parseInt(txtAnchoColumna.getText())) {
                            reference.setColumnWidth(Short.parseShort(txtAnchoColumna.getText()));
                            reference.setPrefWidth(((double) reference.getColumnWidth() * pointWidth));
                        } else {
                            reference.setColumnWidth(Short.parseShort(txtAnchoColumna.getText()));
                            reference.setPrefWidth(((double) reference.getColumnWidth() * pointWidth));
                            reference.setText(reference.getText().substring(0, reference.getColumnWidth()));
                        }
                    } else {
                        if (reference.getText().length() < Integer.parseInt(txtAnchoColumna.getText())) {
                            reference.setColumnWidth(Short.parseShort(txtAnchoColumna.getText()));
                            reference.setPrefWidth(((double) reference.getColumnWidth() * pointWidth));
                        } else {
                            reference.setColumnWidth(Short.parseShort(txtAnchoColumna.getText()));
                            reference.setPrefWidth(((double) reference.getColumnWidth() * pointWidth));
                        }
                    }
                }
            }
        }
    }

    private void clearPane() {
        apEncabezado.getChildren().clear();
        hbDetalleCabecera.getChildren().clear();
        hbPie.getChildren().clear();
        txtAnchoColumna.setText("");
        cbAlignment.getSelectionModel().select(null);
        cbMultilinea.setSelected(false);
        cbEditable.setSelected(false);
        txtVariable.setText("");
        lblNombre.setText("--");
        sheetWidth = 0;
        lblColumnas.setText(sheetWidth + "");
        vbContenedor.setPrefWidth(Control.USE_COMPUTED_SIZE);
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
            Stage stage = WindowStage.StageLoaderModal(parent, "Seleccionar formato", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void nuevoTicket() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_TICKET_PROCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxTicketProcesoController controller = fXMLLoader.getController();
            controller.setInitTicketController(this);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Nuevo formato", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void editarTicket() {
        try {
            ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
            URL url = getClass().getResource(FilesRouters.FX_TICKET_PROCESO);
            FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
            Parent parent = fXMLLoader.load(url.openStream());
            //Controlller here
            FxTicketProcesoController controller = fXMLLoader.getController();
            controller.setInitTicketController(this);
            controller.editarTicket(lblNombre.getText(), sheetWidth);
            //
            Stage stage = WindowStage.StageLoaderModal(parent, "Editar formato", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void windowTextMultilinea(HBoxModel hBox) {
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
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar texto multilinea", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void windowTextVar(HBoxModel hBox) {
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
            Stage stage = WindowStage.StageLoaderModal(parent, "Agregar variable", vbWindow.getScene().getWindow());
            stage.setResizable(false);
            stage.sizeToScene();
            stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void openWindowImpresora() {
        try {

            int valideteUno = 0;
            int valideteDos = 0;
            int valideteTree = 0;
            for (int i = 0; i < apEncabezado.getChildren().size(); i++) {
                HBoxModel hBox = (HBoxModel) apEncabezado.getChildren().get(i);
                if (hBox.getChildren().isEmpty()) {
                    valideteUno++;
                }
            }

            for (int i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
                HBoxModel hBox = (HBoxModel) hbDetalleCabecera.getChildren().get(i);
                if (hBox.getChildren().isEmpty()) {
                    valideteDos++;
                }
            }

            for (int i = 0; i < hbPie.getChildren().size(); i++) {
                HBoxModel hBox = (HBoxModel) hbPie.getChildren().get(i);
                if (hBox.getChildren().isEmpty()) {
                    valideteTree++;
                }
            }

            if (valideteUno > 0) {
                Tools.AlertMessageWarning(vbWindow, "Ticket", "Hay una fila en la cabecera sin contenido");
            } else if (valideteDos > 0) {
                Tools.AlertMessageWarning(vbWindow, "Ticket", "Hay una fila en el detalle sin contenido");
            } else if (valideteTree > 0) {
                Tools.AlertMessageWarning(vbWindow, "Ticket", "Hay una fila en el pie sin contenido");
            } else {

                if (!apEncabezado.getChildren().isEmpty() && !hbDetalleCabecera.getChildren().isEmpty() && !hbPie.getChildren().isEmpty()) {
                    ObjectGlobal.InitializationTransparentBackground(vbPrincipal);
                    URL url = getClass().getResource(FilesRouters.FX_IMPRIMIR);
                    FXMLLoader fXMLLoader = WindowStage.LoaderWindow(url);
                    Parent parent = fXMLLoader.load(url.openStream());
                    //Controlller here
                    FxImprimirController controller = fXMLLoader.getController();
                    controller.setInitTicketController(this);
                    //
                    Stage stage = WindowStage.StageLoaderModal(parent, "Imprimir Prueba", vbWindow.getScene().getWindow());
                    stage.setResizable(false);
                    stage.sizeToScene();
                    stage.setOnHiding(w -> vbPrincipal.getChildren().remove(ObjectGlobal.PANE));
                    stage.show();
                } else {
                    if (apEncabezado.getChildren().isEmpty()) {
                        Tools.AlertMessageWarning(vbWindow, "Ticket", "El encabezado está vacío.");
                    } else if (hbDetalleCabecera.getChildren().isEmpty()) {
                        Tools.AlertMessageWarning(vbWindow, "Ticket", "El detalle cabecera está vacío.");
                    } else if (hbPie.getChildren().isEmpty()) {
                        Tools.AlertMessageWarning(vbWindow, "Ticket", "El pie está vacío.");
                    }
                }
            }
        } catch (IOException ex) {

        }
    }

    @FXML
    private void onKeyPressNuevo(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            nuevoTicket();
        }

    }

    @FXML
    private void onActionNuevo(ActionEvent event) {
        nuevoTicket();
    }

    @FXML
    private void onKeyPressEditar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            editarTicket();
        }
    }

    @FXML
    private void onActionEditar(ActionEvent event) {
        editarTicket();
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
        generateElement(apEncabezado, "cb");
    }

    @FXML
    private void onMouseClickedPieAdd(MouseEvent event) {
        if (hbPie.getChildren().isEmpty()) {
//            addElement(hbPie, "cp1");
        } else {
            HBoxModel hBox = (HBoxModel) hbPie.getChildren().get(hbPie.getChildren().size() - 1);
            String idGenerate = hBox.getId();
            String codigo = idGenerate.substring(2);
            int valor = Integer.parseInt(codigo) + 1;
            String newCodigo = "cp" + valor;
//            addElement(hbPie, newCodigo);
        }
    }

    @FXML
    private void onMouseClickedDetalleCabeceraAdd(MouseEvent event) {
        if (hbDetalleCabecera.getChildren().isEmpty()) {
//            addElement(hbDetalleCabecera, "dr1");
        } else {
            HBoxModel hBox = (HBoxModel) hbDetalleCabecera.getChildren().get(hbDetalleCabecera.getChildren().size() - 1);
            String idGenerate = hBox.getId();
            String codigo = idGenerate.substring(2);
            int valor = Integer.parseInt(codigo) + 1;
            String newCodigo = "dr" + valor;
//            addElement(hbDetalleCabecera, newCodigo);
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
                reference.setLines((short) 0);
            } else {
                reference.setMultilineas(true);
                reference.setLines((short) 1);
            }
        }
    }

    @FXML
    private void onKeyPressedClear(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            clearPane();
        }
    }

    @FXML
    private void onActionClear(ActionEvent event) {
        clearPane();
    }

    public void editarTcket(String nombre, short widthPage) {
        lblNombre.setText(nombre);
        sheetWidth = widthPage;
        lblColumnas.setText("" + sheetWidth);
        vbContenedor.setPrefWidth(sheetWidth * pointWidth);

        for (short i = 0; i < apEncabezado.getChildren().size(); i++) {
            HBoxModel hBox = (HBoxModel) apEncabezado.getChildren().get(i);
            short newwidth = (short) (sheetWidth / hBox.getChildren().size());
            for (short j = 0; j < hBox.getChildren().size(); j++) {
                TextFieldTicket fieldTicket = (TextFieldTicket) hBox.getChildren().get(j);
                fieldTicket.setColumnWidth(newwidth);
                fieldTicket.setPrefWidth(newwidth * pointWidth);
            }
        }

        for (short i = 0; i < hbDetalleCabecera.getChildren().size(); i++) {
            HBoxModel hBox = (HBoxModel) hbDetalleCabecera.getChildren().get(i);
            short newwidth = (short) (sheetWidth / hBox.getChildren().size());
            for (short j = 0; j < hBox.getChildren().size(); j++) {
                TextFieldTicket fieldTicket = (TextFieldTicket) hBox.getChildren().get(j);
                fieldTicket.setColumnWidth(newwidth);
                fieldTicket.setPrefWidth(newwidth * pointWidth);
            }
        }

        for (short i = 0; i < hbPie.getChildren().size(); i++) {
            HBoxModel hBox = (HBoxModel) hbPie.getChildren().get(i);
            short newwidth = (short) (sheetWidth / hBox.getChildren().size());
            for (short j = 0; j < hBox.getChildren().size(); j++) {
                TextFieldTicket fieldTicket = (TextFieldTicket) hBox.getChildren().get(j);
                fieldTicket.setColumnWidth(newwidth);
                fieldTicket.setPrefWidth(newwidth * pointWidth);
            }
        }

    }

    public AnchorPane getHbEncabezado() {
        return apEncabezado;
    }

    public VBox getHbDetalleCabecera() {
        return hbDetalleCabecera;
    }

    public VBox getHbPie() {
        return hbPie;
    }

    public short getSheetWidth() {
        return sheetWidth;
    }

    public void setContent(AnchorPane vbPrincipal) {
        this.vbPrincipal = vbPrincipal;
    }

    @FXML
    private void onActionArriba(ActionEvent event) {

        for (int i = 0; i < apEncabezado.getChildren().size(); i++) {

            if (((HBoxModel) apEncabezado.getChildren().get(i)).equals(HboxReference)) {
                if (i == 0) {
                    break;
                }
                HBoxModel anterior = (HBoxModel) apEncabezado.getChildren().get(i - 1);

                HBoxModel oldHbox = addElement(apEncabezado, anterior.getId(), false);
                oldHbox.setLayoutY(anterior.getLayoutY());
                for (int r = 0; r < HboxReference.getChildren().size(); r++) {
                    TextFieldTicket tftAnterior = (TextFieldTicket) HboxReference.getChildren().get(r);
                    TextFieldTicket fieldTicket = addElementTextField(tftAnterior.getId(), tftAnterior.getText(), tftAnterior.isMultilineas(), tftAnterior.getLines(), tftAnterior.getColumnWidth(), tftAnterior.getAlignment(), tftAnterior.isEditable(), tftAnterior.getVariable());
                    oldHbox.getChildren().add(fieldTicket);
                }

                HBoxModel newHbox = addElement(apEncabezado, HboxReference.getId(), false);
                newHbox.setLayoutY(HboxReference.getLayoutY());
                for (int a = 0; a < anterior.getChildren().size(); a++) {
                    TextFieldTicket tftAnterior = (TextFieldTicket) anterior.getChildren().get(a);
                    TextFieldTicket fieldTicket = addElementTextField(tftAnterior.getId(), tftAnterior.getText(), tftAnterior.isMultilineas(), tftAnterior.getLines(), tftAnterior.getColumnWidth(), tftAnterior.getAlignment(), tftAnterior.isEditable(), tftAnterior.getVariable());
                    newHbox.getChildren().add(fieldTicket);
                }

                apEncabezado.getChildren().set(i - 1, oldHbox);
                apEncabezado.getChildren().set(i, newHbox);

            }
        }
    }

    @FXML
    private void onActionAbajo(ActionEvent event) {
        for (int i = 0; i < apEncabezado.getChildren().size(); i++) {        
            if (((HBoxModel) apEncabezado.getChildren().get(i)).equals(HboxReference)) {
                if (apEncabezado.getChildren().size() == (i + 1) ) {
                    break;
                }

                HBoxModel posterior = (HBoxModel) apEncabezado.getChildren().get(i + 1);

                HBoxModel oldHbox = addElement(apEncabezado, posterior.getId(), false);
                oldHbox.setLayoutY(posterior.getLayoutY());
                for (int r = 0; r < HboxReference.getChildren().size(); r++) {
                    TextFieldTicket tftAnterior = (TextFieldTicket) HboxReference.getChildren().get(r);
                    TextFieldTicket fieldTicket = addElementTextField(tftAnterior.getId(), tftAnterior.getText(), tftAnterior.isMultilineas(), tftAnterior.getLines(), tftAnterior.getColumnWidth(), tftAnterior.getAlignment(), tftAnterior.isEditable(), tftAnterior.getVariable());
                    oldHbox.getChildren().add(fieldTicket);
                }

                HBoxModel newHbox = addElement(apEncabezado, HboxReference.getId(), false);
                newHbox.setLayoutY(HboxReference.getLayoutY());
                for (int a = 0; a < posterior.getChildren().size(); a++) {
                    TextFieldTicket tftAnterior = (TextFieldTicket) posterior.getChildren().get(a);
                    TextFieldTicket fieldTicket = addElementTextField(tftAnterior.getId(), tftAnterior.getText(), tftAnterior.isMultilineas(), tftAnterior.getLines(), tftAnterior.getColumnWidth(), tftAnterior.getAlignment(), tftAnterior.isEditable(), tftAnterior.getVariable());
                    newHbox.getChildren().add(fieldTicket);
                }

                apEncabezado.getChildren().set(i, newHbox);
                apEncabezado.getChildren().set(i + 1, oldHbox);
            }
        }
    }

}
