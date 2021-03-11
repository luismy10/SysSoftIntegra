package model;

import controller.tools.SearchComboBox;
import controller.tools.Tools;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

public class FormulaADO {

    public static Object ListarFormulas(int opcion, String fechaInicio, String fechaFinal, String buscar, int posicionPagina, int filasPorPagina) {
        PreparedStatement statementLista = null;
        try {
            DBUtil.dbConnect();
            Object[] objects = new Object[2];

            ObservableList<FormulaTB> formulaTBs = FXCollections.observableArrayList();
            statementLista = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_Formulas(?,?,?,?,?,?)}");
            statementLista.setInt(1, opcion);
            statementLista.setString(2, fechaInicio);
            statementLista.setString(3, fechaFinal);
            statementLista.setString(4, buscar);
            statementLista.setInt(5, posicionPagina);
            statementLista.setInt(6, filasPorPagina);
            ResultSet resultSet = statementLista.executeQuery();
            while (resultSet.next()) {
                FormulaTB formulaTB = new FormulaTB();
                formulaTB.setId(resultSet.getRow() + posicionPagina);
                formulaTB.setIdFormula(resultSet.getString("IdFormula"));
                formulaTB.setTitulo(resultSet.getString("Titulo"));
                formulaTB.setCantidad(resultSet.getDouble("Cantidad"));
                formulaTB.setInstrucciones(resultSet.getString("Instrucciones"));
                formulaTB.setFecha(resultSet.getDate("Fecha").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                formulaTB.setHora(resultSet.getTime("Hora").toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss a")));

                SuministroTB suministroTB = new SuministroTB();
                suministroTB.setClave(resultSet.getString("Clave"));
                suministroTB.setNombreMarca(resultSet.getString("NombreMarca"));
                formulaTB.setSuministroTB(suministroTB);

                formulaTBs.add(formulaTB);
            }

            statementLista = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_Formulas_Count(?,?,?,?)}");
            statementLista.setInt(1, opcion);
            statementLista.setString(2, fechaInicio);
            statementLista.setString(3, fechaFinal);
            statementLista.setString(4, buscar);
            resultSet = statementLista.executeQuery();
            Integer cantidadTotal = 0;
            if (resultSet.next()) {
                cantidadTotal = resultSet.getInt("Total");
            }

            objects[0] = formulaTBs;
            objects[1] = cantidadTotal;

            return objects;
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementLista != null) {
                    statementLista.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }

    public static String Crud_Formula(FormulaTB formulaTB) {
        CallableStatement statementCodigo = null;
        PreparedStatement statementValidate = null;
        PreparedStatement statementFormula = null;
        PreparedStatement statementDetalle = null;
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            statementValidate = DBUtil.getConnection().prepareStatement("SELECT * FROM FormulaTB WHERE IdFormula = ?");
            statementValidate.setString(1, formulaTB.getIdFormula());
            if (statementValidate.executeQuery().next()) {
                statementFormula = DBUtil.getConnection().prepareStatement("UPDATE FormulaTB SET Titulo = ?,Cantidad = ?,IdSuministro = ?,CostoAdicional = ?,Instrucciones = ? WHERE IdFormula = ?");
                statementFormula.setString(1, formulaTB.getTitulo());
                statementFormula.setDouble(2, formulaTB.getCantidad());
                statementFormula.setString(3, formulaTB.getIdSuministro());
                statementFormula.setDouble(4, formulaTB.getCostoAdicional());
                statementFormula.setString(5, formulaTB.getInstrucciones());
                statementFormula.setString(6, formulaTB.getIdFormula());
                statementFormula.addBatch();
                statementFormula.executeBatch();

                statementDetalle = DBUtil.getConnection().prepareStatement("DELETE FROM FormulaDetalleTB WHERE IdFormula = ?");
                statementDetalle.setString(1, formulaTB.getIdFormula());
                statementDetalle.addBatch();
                statementDetalle.executeBatch();

                statementDetalle = DBUtil.getConnection().prepareStatement("INSERT INTO FormulaDetalleTB(IdFormula,IdInsumo,Cantidad) VALUES(?,?,?)");
                for (InsumoTB insumoTB : formulaTB.getInsumoTBs()) {
                    statementDetalle.setString(1, formulaTB.getIdFormula());
                    statementDetalle.setString(2, insumoTB.getComboBox().getSelectionModel().getSelectedItem().getIdInsumo());
                    statementDetalle.setDouble(3, Double.parseDouble(insumoTB.getTxtCantidad().getText()));
                    statementDetalle.addBatch();
                }
                statementDetalle.executeBatch();

                DBUtil.getConnection().commit();
                return "updated";
            } else {

                statementCodigo = DBUtil.getConnection().prepareCall("{? = call Fc_Formula_Alfanumerico()}");
                statementCodigo.registerOutParameter(1, java.sql.Types.VARCHAR);
                statementCodigo.execute();
                String id_Formula = statementCodigo.getString(1);

                statementFormula = DBUtil.getConnection().prepareStatement("INSERT INTO FormulaTB(IdFormula,Titulo,Cantidad,IdSuministro,CostoAdicional,Instrucciones,Fecha,Hora) VALUES(?,?,?,?,?,?,?,?)");
                statementFormula.setString(1, id_Formula);
                statementFormula.setString(2, formulaTB.getTitulo());
                statementFormula.setDouble(3, formulaTB.getCantidad());
                statementFormula.setString(4, formulaTB.getIdSuministro());
                statementFormula.setDouble(5, formulaTB.getCostoAdicional());
                statementFormula.setString(6, formulaTB.getInstrucciones());
                statementFormula.setString(7, formulaTB.getFecha());
                statementFormula.setString(8, formulaTB.getHora());
                statementFormula.addBatch();

                statementDetalle = DBUtil.getConnection().prepareStatement("INSERT INTO FormulaDetalleTB(IdFormula,IdInsumo,Cantidad) VALUES(?,?,?)");
                for (InsumoTB insumoTB : formulaTB.getInsumoTBs()) {
                    statementDetalle.setString(1, id_Formula);
                    statementDetalle.setString(2, insumoTB.getComboBox().getSelectionModel().getSelectedItem().getIdInsumo());
                    statementDetalle.setDouble(3, Double.parseDouble(insumoTB.getTxtCantidad().getText()));
                    statementDetalle.addBatch();
                }

                statementFormula.executeBatch();
                statementDetalle.executeBatch();
                DBUtil.getConnection().commit();
                return "inserted";
            }
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException e) {

            }
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementValidate != null) {
                    statementValidate.close();
                }
                if (statementFormula != null) {
                    statementFormula.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }

    public static Object Obtener_Formula_ById(String idFormula) {
        PreparedStatement statementFormula = null;
        PreparedStatement statementDetalle = null;
        ResultSet resultSet = null;
        try {
            DBUtil.dbConnect();
            statementFormula = DBUtil.getConnection().prepareStatement("select f.IdFormula,f.Titulo,f.Cantidad,f.CostoAdicional,f.CostoAdicional,\n"
                    + "f.Instrucciones,f.Fecha,f.Hora,s.IdSuministro,s.Clave,s.NombreMarca\n"
                    + "from FormulaTB as f \n"
                    + "inner join SuministroTB as s on s.IdSuministro = f.IdSuministro\n"
                    + "where f.IdFormula = ?");
            statementFormula.setString(1, idFormula);
            resultSet = statementFormula.executeQuery();
            if (resultSet.next()) {
                FormulaTB formulaTB = new FormulaTB();
                formulaTB.setIdFormula(resultSet.getString("IdFormula"));
                formulaTB.setTitulo(resultSet.getString("Titulo"));
                formulaTB.setCantidad(resultSet.getDouble("Cantidad"));
                formulaTB.setIdSuministro(resultSet.getString("IdSuministro"));
                formulaTB.setSuministroTB(new SuministroTB(resultSet.getString("IdSuministro"), resultSet.getString("Clave"), resultSet.getString("NombreMarca")));
                formulaTB.setCostoAdicional(resultSet.getDouble("CostoAdicional"));
                formulaTB.setInstrucciones(resultSet.getString("Instrucciones"));
                formulaTB.setFecha(resultSet.getDate("Fecha").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                formulaTB.setHora(resultSet.getTime("Hora").toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss a")));

                statementDetalle = DBUtil.getConnection().prepareStatement("select i.IdInsumo,i.Clave,i.NombreMarca,fd.Cantidad from\n"
                        + "FormulaDetalleTB as fd \n"
                        + "inner join InsumoTB as i on fd.IdInsumo = i.IdInsumo\n"
                        + "where fd.IdFormula = ?");
                statementDetalle.setString(1, idFormula);
                resultSet = statementDetalle.executeQuery();
                ArrayList<InsumoTB> insumoTBs = new ArrayList();
                while (resultSet.next()) {
                    InsumoTB insumoTB = new InsumoTB();
                    insumoTB.setCantidad(resultSet.getDouble("Cantidad"));

                    ComboBox<InsumoTB> comboBox = new ComboBox();
                    comboBox.setPromptText("-- Selecionar --");
                    comboBox.setPrefWidth(220);
                    comboBox.setPrefHeight(30);
                    comboBox.setMaxWidth(Double.MAX_VALUE);
                    insumoTB.setComboBox(comboBox);

                    SearchComboBox<InsumoTB> searchComboBox = new SearchComboBox<>(insumoTB.getComboBox(), false);
                    searchComboBox.getSearchComboBoxSkin().getSearchBox().setOnKeyPressed(t -> {
                        if (t.getCode() == KeyCode.ENTER) {
                            if (!searchComboBox.getSearchComboBoxSkin().getItemView().getItems().isEmpty()) {
                                searchComboBox.getSearchComboBoxSkin().getItemView().getSelectionModel().select(0);
                                searchComboBox.getSearchComboBoxSkin().getItemView().requestFocus();
                            }
                        } else if (t.getCode() == KeyCode.ESCAPE) {
                            searchComboBox.getComboBox().hide();
                        }
                    });
                    searchComboBox.getSearchComboBoxSkin().getSearchBox().setOnKeyReleased(t -> {
                        if (!Tools.isText(searchComboBox.getSearchComboBoxSkin().getSearchBox().getText())) {
                            searchComboBox.getComboBox().getItems().clear();
                            List<InsumoTB> list = InsumoADO.getSearchComboBoxInsumos(searchComboBox.getSearchComboBoxSkin().getSearchBox().getText().trim());
                            list.forEach(p -> searchComboBox.getComboBox().getItems().add(p));
                        }
                    });
                    searchComboBox.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
                        switch (t.getCode()) {
                            case ENTER:
                            case SPACE:
                            case ESCAPE:
                                searchComboBox.getComboBox().hide();
                                break;
                            case UP:
                            case DOWN:
                            case LEFT:
                            case RIGHT:
                                break;
                            default:
                                searchComboBox.getSearchComboBoxSkin().getSearchBox().requestFocus();
                                searchComboBox.getSearchComboBoxSkin().getSearchBox().selectAll();
                                break;
                        }
                    });
                    searchComboBox.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
                        if (item != null) {
                            searchComboBox.getComboBox().getSelectionModel().select(item);
                            if (searchComboBox.getSearchComboBoxSkin().isClickSelection()) {
                                searchComboBox.getComboBox().hide();
                            }
                        }
                    });
                    insumoTB.setSearchComboBox(searchComboBox);
                    insumoTB.getComboBox().getItems().add(new InsumoTB(resultSet.getString("IdInsumo"), resultSet.getString("Clave"), resultSet.getString("NombreMarca")));
                    insumoTB.getComboBox().getSelectionModel().select(0);

                    TextField textField = new TextField(Tools.roundingValue(insumoTB.getCantidad(), 2));
                    textField.setPromptText("0.00");
                    textField.getStyleClass().add("text-field-normal");
                    textField.setPrefWidth(220);
                    textField.setPrefHeight(30);
                    textField.setOnKeyTyped(event -> {
                        char c = event.getCharacter().charAt(0);
                        if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
                            event.consume();
                        }
                        if (c == '.' && textField.getText().contains(".")) {
                            event.consume();
                        }
                    });
                    insumoTB.setTxtCantidad(textField);

                    Button button = new Button();
                    button.getStyleClass().add("buttonLightError");
                    button.setAlignment(Pos.CENTER);
                    button.setPrefWidth(Control.USE_COMPUTED_SIZE);
                    button.setPrefHeight(Control.USE_COMPUTED_SIZE);
//                    button.setMaxWidth(Double.MAX_VALUE);
//                    button.setMaxHeight(Double.MAX_VALUE);
                    ImageView imageView = new ImageView(new Image("/view/image/remove-gray.png"));
                    imageView.setFitWidth(20);
                    imageView.setFitHeight(20);
                    button.setGraphic(imageView);
                    insumoTB.setBtnRemove(button);
                    insumoTBs.add(insumoTB);
                }
                formulaTB.setInsumoTBs(insumoTBs);
                return formulaTB;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementFormula != null) {
                    statementFormula.close();
                }
                if (statementDetalle != null) {
                    statementDetalle.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }

    public static Object Obtener_Formula_ByIdProducto(String idProducto) {
        PreparedStatement statementFormula = null;
        ResultSet resultSet = null;
        try {
            DBUtil.dbConnect();
            statementFormula = DBUtil.getConnection().prepareStatement("SELECT IdFormula,Titulo FROM FormulaTB WHERE IdSuministro = ?");
            statementFormula.setString(1, idProducto);
            resultSet = statementFormula.executeQuery();
            List<FormulaTB> formulaTBs = new ArrayList<>();
            while (resultSet.next()) {
                FormulaTB formulaTB = new FormulaTB();
                formulaTB.setIdFormula(resultSet.getString("IdFormula"));
                formulaTB.setTitulo(resultSet.getString("Titulo"));
                formulaTBs.add(formulaTB);
            }
            return formulaTBs;
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementFormula != null) {
                    statementFormula.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }

    public static String Eliminar_Formula_ById(String idFormula) {
        PreparedStatement statementValidar = null;
        PreparedStatement statementFormula = null;
        PreparedStatement statementDetalle = null;
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            statementValidar = DBUtil.getConnection().prepareStatement("SELECT * FROM FormulaTB WHERE IdFormula = ?");
            statementValidar.setString(1, idFormula);
            if (statementValidar.executeQuery().next()) {
                statementFormula = DBUtil.getConnection().prepareStatement("DELETE FROM FormulaTB WHERE IdFormula = ?");
                statementFormula.setString(1, idFormula);
                statementFormula.addBatch();
                statementFormula.executeBatch();

                statementDetalle = DBUtil.getConnection().prepareStatement("DELETE FROM FormulaDetalleTB WHERE IdFormula = ?");
                statementDetalle.setString(1, idFormula);
                statementDetalle.addBatch();
                statementDetalle.executeBatch();

                DBUtil.getConnection().commit();
                return "deleted";
            } else {
                DBUtil.getConnection().rollback();
                return "noid";
            }
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException e) {

            }
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementValidar != null) {
                    statementValidar.close();
                }
                if (statementFormula != null) {
                    statementFormula.close();
                }
                if (statementDetalle != null) {
                    statementDetalle.close();
                }
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }

}