package controller.inventario.articulo;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import model.LoteTB;

public class FxArticuloDetalleController implements Initializable {

    @FXML
    private TableView<LoteTB> tvList;
    @FXML
    private TableColumn<LoteTB, String> tcExistenciaInicial;
    @FXML
    private TableColumn<LoteTB, String> tcExistenciaActual;
    @FXML
    private TableColumn<LoteTB, String> tcCantidad;

    private FxArticulosController articuloController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tcExistenciaInicial.setCellValueFactory(cellData
                -> Bindings.concat(cellData.getValue().getExistenciaInicial()));
        tcExistenciaActual.setCellValueFactory(cellData
                -> Bindings.concat(cellData.getValue().getExistenciaActual()));
        tcCantidad.setCellValueFactory(cellData
                -> Bindings.concat(cellData.getValue().getFechaCaducidad().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))));
    }

    @FXML
    private void onMouseClickedSeleccionado(MouseEvent event) {
        articuloController.changeViewArticuloSeleccionado();
        if (articuloController.getTvList().getSelectionModel().getSelectedIndex() >= 0) {
//            articuloController.onViewDetailArticulo();
        }
    }

    void setArticuloController(FxArticulosController articuloController) {
        this.articuloController = articuloController;
    }

    public TableView<LoteTB> getTvList() {
        return tvList;
    }

}
