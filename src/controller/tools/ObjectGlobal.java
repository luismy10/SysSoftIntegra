package controller.tools;

import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class ObjectGlobal {

    /*
    Objecto de conexion
     */
    public static String ADDRES;
    public static String PORT;
    public static String DATABASENAME;
    public static String USER;
    public static String PASSWORD;
    /*
     */
    public static AnchorPane PANE;

    public static AnchorPane PANE_PRINCIPAL;

    public static String QR_PERU_DATA = "|0|0|0|0|0|0|0|0|";

    public static ArrayList<String> DATA_CLIENTS = new ArrayList<>();

    static {
        PANE_PRINCIPAL = new AnchorPane();
    }

    public static void InitializationTransparentBackground(AnchorPane vbPrincipal) {
        PANE.setStyle("-fx-background-color: black");
        PANE.setTranslateX(0);
        PANE.setTranslateY(0);
        PANE.setPrefWidth(Session.WIDTH_WINDOW);
        PANE.setPrefHeight(Session.HEIGHT_WINDOW);
        PANE.setOpacity(0.55);
        vbPrincipal.getChildren().add(PANE);
    }

    public static void InitializationTransparentBackgroundPrincipal(AnchorPane vbPrincipal) {
        PANE_PRINCIPAL.setStyle("-fx-background-color:  transparent");
        PANE_PRINCIPAL.setTranslateX(0);
        PANE_PRINCIPAL.setTranslateY(0);
        PANE_PRINCIPAL.setPrefWidth(Session.WIDTH_WINDOW);
        PANE_PRINCIPAL.setPrefHeight(Session.HEIGHT_WINDOW);

        Label label = new Label("Cargando menus \n Espere por favor...");
        label.getStyleClass().add("labelRobotoBold17");
        label.setTextAlignment(TextAlignment.CENTER);
        label.setTextFill(Color.BLACK);
        label.setAlignment(Pos.CENTER);

        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);
        AnchorPane.setBottomAnchor(label, 0.0);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8)");

        AnchorPane.setLeftAnchor(anchorPane, 0.0);
        AnchorPane.setTopAnchor(anchorPane, 0.0);
        AnchorPane.setRightAnchor(anchorPane, 0.0);
        AnchorPane.setBottomAnchor(anchorPane, 0.0);

        PANE_PRINCIPAL.getChildren().add(anchorPane);
        PANE_PRINCIPAL.getChildren().add(label);

        vbPrincipal.getChildren().add(PANE_PRINCIPAL);
    }

}
