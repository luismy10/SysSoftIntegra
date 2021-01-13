package controller.tools;

import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import org.controlsfx.control.Notifications;

public class Tools {

    public static short AlertMessageConfirmation(Node node, String title, String value) {
        return AlertMessage(node.getScene().getWindow(), Alert.AlertType.CONFIRMATION, title, value, true);
    }

    public static short AlertMessageInformation(Node node, String title, String value) {
        return AlertMessage(node.getScene().getWindow(), Alert.AlertType.INFORMATION, title, value, false);
    }

    public static short AlertMessageWarning(Node node, String title, String value) {
        return AlertMessage(node.getScene().getWindow(), Alert.AlertType.WARNING, title, value, false);
    }

    public static short AlertMessageError(Node node, String title, String value) {
        return AlertMessage(node.getScene().getWindow(), Alert.AlertType.ERROR, title, value, false);
    }

    public static short AlertMessage(Window window, Alert.AlertType type, String title, String message, boolean validation) {
        final URL url = Tools.class.getClass().getResource(FilesRouters.STYLE_ALERT);
        Alert alert = new Alert(type);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(FilesRouters.IMAGE_ICON));
        alert.setTitle(title);
        alert.setContentText(message);
        alert.setHeaderText(null);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner(window);
        alert.getDialogPane().getStylesheets().add(url.toExternalForm());
        alert.getButtonTypes().clear();

        ButtonType buttonTypeOne = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeTwo = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType buttonTypeClose = new ButtonType("Aceptar", ButtonBar.ButtonData.CANCEL_CLOSE);
        if (validation) {
            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
            Button buttonOne = (Button) alert.getDialogPane().lookupButton(buttonTypeOne);
            buttonOne.setDefaultButton(false);
            Button buttonTwo = (Button) alert.getDialogPane().lookupButton(buttonTypeTwo);
            buttonOne.setOnKeyPressed((KeyEvent event) -> {
                if (event.getCode() == KeyCode.ENTER) {
                    alert.setResult(buttonTypeOne);
                }
            });
            buttonTwo.setOnKeyPressed((KeyEvent event) -> {
                if (event.getCode() == KeyCode.ENTER) {
                    alert.setResult(buttonTypeTwo);
                }
            });
            Optional<ButtonType> optional = alert.showAndWait();
            return (short) (optional.get() == buttonTypeOne ? 1 : 0);
        } else {
            alert.getButtonTypes().setAll(buttonTypeClose);
            Button buttonOne = (Button) alert.getDialogPane().lookupButton(buttonTypeClose);
            buttonOne.setOnKeyPressed((KeyEvent event) -> {
                if (event.getCode() == KeyCode.ENTER) {
                    alert.setResult(buttonTypeClose);
                }
            });
            Optional<ButtonType> optional = alert.showAndWait();
            return (short) (optional.get() == buttonTypeClose ? 1 : 0);
        }
    }

    public static short AlertMessage(Window window, String title, String value) {
        final URL url = Tools.class.getClass().getResource(FilesRouters.STYLE_ALERT);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(FilesRouters.IMAGE_ICON));
        alert.setTitle(title);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner(window);
        alert.setHeaderText(null);
        alert.setContentText(value);
        alert.getDialogPane().getStylesheets().add(url.toExternalForm());
        alert.getButtonTypes().clear();

        ButtonType buttonTypeTwo = new ButtonType("Aceptar y no Imprimir", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeOne = new ButtonType("Aceptar e Imprimir", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeTwo, buttonTypeOne);
        Button buttonTwo = (Button) alert.getDialogPane().lookupButton(buttonTypeTwo);
        buttonTwo.setDefaultButton(false);
        Button buttonOne = (Button) alert.getDialogPane().lookupButton(buttonTypeOne);
        buttonTwo.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                alert.setResult(buttonTypeTwo);
            }
        });
        buttonOne.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                alert.setResult(buttonTypeOne);
            }
        });

        Optional<ButtonType> optional = alert.showAndWait();
        return (short) (optional.get() == buttonTypeTwo ? 0 : 1);

    }

    public static Alert AlertMessage(Window window, Alert.AlertType type, String value) {
        final URL url = Tools.class.getClass().getResource(FilesRouters.STYLE_ALERT);
        Alert alert = new Alert(type);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(FilesRouters.IMAGE_ICON));
        alert.setTitle("Movimiento");
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner(window);
        alert.setHeaderText(null);
        alert.setContentText(value);
        alert.getDialogPane().getStylesheets().add(url.toExternalForm());
        alert.show();
        return alert;
    }

    public static Alert AlertDialogMessage(Node node, Alert.AlertType type, String title, String value, String error) {
        final URL url = Tools.class.getClass().getResource(FilesRouters.STYLE_ALERT);
        Alert alert = new Alert(type);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(FilesRouters.IMAGE_ICON));
        alert.setTitle(title);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.initOwner(node.getScene().getWindow());
        alert.setHeaderText(null);
        alert.setContentText(value);
        alert.getDialogPane().getStylesheets().add(url.toExternalForm());

        TextArea textArea = new TextArea(error);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
        return alert;
    }

    public static void DisposeWindow(AnchorPane window, EventType<KeyEvent> eventType) {
        window.addEventHandler(eventType, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                Dispose(window);
                event.consume();
            }
        });
    }

    public static void Dispose(AnchorPane window) {
        Stage stage = (Stage) window.getScene().getWindow();
        stage.close();
    }

    public static String roundingValue(double valor, int decimals) {
        BigDecimal decimal = BigDecimal.valueOf(valor);
        decimal = decimal.setScale(decimals, RoundingMode.HALF_UP);
        return decimal.toPlainString();
    }

    public static boolean isNumeric(String cadena) {
        if (cadena.trim() == null || cadena.trim().isEmpty()) {
            return false;
        }
        boolean resultado;
        try {
            Double.parseDouble(cadena);
            resultado = true;
        } catch (NumberFormatException ex) {
            resultado = false;
        }
        return resultado;
    }

    public static boolean isNumericInteger(String cadena) {
        if (cadena.trim() == null || cadena.trim().isEmpty()) {
            return false;
        }
        boolean resultado;
        try {
            Integer.parseInt(cadena);
            resultado = true;
        } catch (NumberFormatException ex) {
            resultado = false;
        }
        return resultado;
    }

    public static boolean isNumericShort(String cadena) {
        if (cadena.trim() == null || cadena.trim().isEmpty()) {
            return false;
        }
        boolean resultado;
        try {
            Short.parseShort(cadena);
            resultado = true;
        } catch (NumberFormatException ex) {
            resultado = false;
        }
        return resultado;
    }

    public static String[] getDataPeople(String data) {
        if (data != null) {
            return data.trim().split(" ");
        } else {
            return null;
        }
    }

    public static double calculateTax(double porcentaje, double valor) {
        double igv = (double) (porcentaje / 100.00);
        return (double) (valor * igv);
    }

    public static double calculateAumento(double margen, double costo) {
        double totalimporte = costo + (costo * (margen / 100.00));
        return Double.parseDouble(Tools.roundingValue(totalimporte, 1));
    }

    public static double calculateValueNeto(double porcentaje, double value) {
        double subprimer = (porcentaje + 100);
        return value / (subprimer * 0.01);
    }

    public static void actualDate(String date, DatePicker datePicker) {
        if (date.contains("-")) {
            datePicker.setValue(LocalDate.of(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]), Integer.parseInt(date.split("-")[2])));
        } else if (date.contains("/")) {
            datePicker.setValue(LocalDate.of(Integer.parseInt(date.split("/")[0]), Integer.parseInt(date.split("/")[1]), Integer.parseInt(date.split("/")[2])));
        }
    }

    public static String getDatePicker(DatePicker datePicker) {
        LocalDate localDate = datePicker.getValue();
        return localDate.toString();
    }

    public static String getValueTable(TableView table, int position) {
        return Tools.getValueAt(table, table.getSelectionModel().getSelectedIndex(), position).toString();
    }

    public static Object getValueAt(TableView table, int rowIndex, int columnIndex) {
        if (rowIndex >= 0 && rowIndex < table.getItems().size() && columnIndex >= 0 && columnIndex < table.getColumns().size()) {
            return ((TableColumn) table.getColumns().get(columnIndex)).getCellObservableValue(rowIndex).getValue();
        } else {
            return null;
        }
    }

    public static void openFile(String ruta) {
        try {
            File path = new File(ruta);
            Desktop.getDesktop().open(path);
        } catch (IOException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.INFO, ex.getLocalizedMessage());
        }
    }

    public static boolean isText(String cadena) {
        return cadena == null || cadena.trim().isEmpty();
    }

    public static String getDate() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public static java.sql.Date getCurrentDate() throws ParseException {
        Date dateCurrent = new SimpleDateFormat("yyyy-MM-dd").parse(getDate());
        return new java.sql.Date(dateCurrent.getTime());
    }

    public static String getDate(String format) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String getHour() {
        Date date = new Date();
        SimpleDateFormat hour = new SimpleDateFormat("HH:mm:ss");
        return hour.format(date);
    }

    public static String getHour(String format) {
        Date date = new Date();
        SimpleDateFormat hour = new SimpleDateFormat(format);
        return hour.format(date);
    }

    public static Timestamp getDateHour() {
        return new Timestamp(new Date().getTime());
    }

    public static int convertMMtoPX(double milimetro) {
        return (int) (Double.parseDouble(Tools.roundingValue(2.83465 * milimetro, 0)) * 1.3333333333333333);
    }

    public static String getFileExtension(File file) {
        if (file == null) {
            return "";
        }
        String name = file.getName();
        int i = name.lastIndexOf('.');
        String ext = i > 0 ? name.substring(i + 1) : "";
        return ext;
    }

    public static void println(Object object) {
        System.out.println(object);
    }

    public static void print(Object object) {
        System.out.print(object);
    }

    public static byte[] getImageBytes(File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] bs = new byte[1024];
            for (int readNum; (readNum = inputStream.read(bs)) != -1;) {
                outputStream.write(bs, 0, readNum);
            }
            return outputStream.toByteArray();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
        return null;
    }

    public static byte[] getImageBytes(Image image, String extension) {
        try {
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), extension, byteOutput);
            return byteOutput.toByteArray();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
        return null;
    }

    public static void showAlertNotification(String url, String title, String message, Duration duration, Pos pos) {
        Image image = new Image(url);
        Notifications notifications = Notifications.create()
                .title(title)
                .text(message)
                .graphic(new ImageView(image))
                .hideAfter(duration)
                .position(pos)
                .onAction(n -> {

                });
        notifications.darkStyle();
        notifications.show();
    }

    public static String Generador0(int value) {
        if (value > 0 && value < 10) {
            return "00000" + value;
        } else if (value >= 10 && value < 100) {
            return "0000" + value;
        } else if (value >= 100 && value < 1000) {
            return "000" + value;
        } else if (value >= 1000 && value < 10000) {
            return "00" + value;
        } else if (value >= 10000 && value < 100000) {
            return "0" + value;
        } else {
            return "" + value;
        }
    }

    public static String AddText2Guines(String value) {
        return value.trim().isEmpty() ? "--" : value;
    }

}
