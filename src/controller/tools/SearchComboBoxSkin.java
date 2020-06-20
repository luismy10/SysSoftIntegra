package controller.tools;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.control.Skinnable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.ProveedorTB;

public class SearchComboBoxSkin<T> extends ComboBoxListViewSkin {

    private VBox box;
    private final TextField searchBox;
    private final ListView<T> itemView;
    private boolean clickSelection = false;

    public SearchComboBoxSkin(SearchComboBox searchComboBox) {
        super(searchComboBox.getComboBox());

        searchBox = new TextField();
        searchBox.getStyleClass().add("text-field-normal");
        searchBox.setPromptText("Ingrese los datos a buscar");
        searchBox.textProperty().addListener((ObservableValue<? extends String> p, String o, String text) -> {
            searchComboBox.setPredicateFilter(item
                    -> text.isEmpty()
                    ? true
                    : searchComboBox.getFilter().test(item, text)
            );
//                if(!itemView.getItems().isEmpty()){
//                   itemView.getSelectionModel().select(0);
//                }
        });

        itemView = new ListView<>();
        itemView.setItems(searchComboBox.getFilterList());

        // administrar la seleccion de un nuevo item
        itemView.getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
            if (item != null) {
                searchComboBox.getComboBox().getSelectionModel().select(item);
                if (itemView.getSelectionModel().getSelectedItem() instanceof ProveedorTB) {
                    Session.PROVEEDOR_ID = ((ProveedorTB) itemView.getSelectionModel().getSelectedItem()).getIdProveedor();
                }

                // ocultar popup cuando el item fue seleccionado mediante un click
                if (clickSelection) {
                    searchComboBox.getComboBox().hide();
                }
            }
        });

        // ocultar popup al usar las teclas determindas ENTER, ESC, SPACE
        itemView.setOnKeyPressed(t -> {
            if (null != t.getCode()) {
                switch (t.getCode()) {
                    case ENTER:
                        searchComboBox.getComboBox().hide();
//                        if (itemView.getSelectionModel().getSelectedItem() instanceof ProveedorTB) {
//                            ProveedorTB proveedorTB = (ProveedorTB) itemView.getSelectionModel().getSelectedItem();
//                        } else if (itemView.getSelectionModel().getSelectedItem() instanceof SuministroTB) {
//                            SuministroTB suministroTB = (SuministroTB) itemView.getSelectionModel().getSelectedItem();
//                        }
                        break;
                    case SPACE:
                        searchComboBox.getComboBox().hide();
                        break;
                    case ESCAPE:
                        searchComboBox.getComboBox().hide();
                        break;
                    case UP:
                        break;
                    case DOWN:
                        break;
                    case LEFT:
                        break;
                    case RIGHT:
                        break;
                    default:
                        searchBox.requestFocus();
                        searchBox.selectAll();
                        break;
                }
            }
        });

        // cambia el foco del TextField al ListView usando las teclas ENTER y ESC
        searchBox.setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                if (!itemView.getItems().isEmpty()) {
                    itemView.getSelectionModel().select(0);
                    itemView.requestFocus();
                }
            } else if (t.getCode() == KeyCode.ESCAPE) {
                searchComboBox.getComboBox().hide();
            }
        });

        // se ha hecho click sobre el ListView
        itemView.addEventFilter(MouseEvent.ANY, me -> clickSelection = me.getEventType().equals(MouseEvent.MOUSE_PRESSED));

        searchComboBox.getComboBox().addEventHandler(KeyEvent.KEY_PRESSED, event -> searchComboBox.getComboBox().show());

    }

    @Override
    protected PopupControl getPopup() {

        // redefinir el combobox popup
        super.getPopup().setSkin(new Skin<Skinnable>() {
            @Override
            public Skinnable getSkinnable() {
                return null;
            }

            @Override
            public Node getNode() {
                return createPopupContent();
            }

            @Override
            public void dispose() {

            }
        });

        return super.getPopup();
    }

    private Node createPopupContent() {
        box = new VBox(searchBox, itemView);
        box.setSpacing(4.0);
        box.setPadding(new Insets(4.0));
        box.setStyle("-fx-background-color:white;-fx-border-color:#999;-fx-border-width:1px;");
        box.setMinWidth(getSkinnable().getWidth());
        box.setPrefWidth(getSkinnable().getWidth());
        box.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(box, Priority.ALWAYS);
        return box;
    }

    @Override
    protected void handleControlPropertyChanged(String p) {
        super.handleControlPropertyChanged(p);
        if ("SHOWING".equals(p)) {
            ComboBox<T> scb = ((ComboBox) getSkinnable());
            box.setMinWidth(scb.getWidth());
            box.setPrefWidth(scb.getWidth());
            if (scb.isShowing()) {
                searchBox.clear();
                searchBox.requestFocus();
                itemView.getSelectionModel().select(scb.getValue());
            }
        }
    }

    public ListView<T> getItemView() {
        return itemView;
    }

}
