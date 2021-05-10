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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

public class ProduccionADO {

    public static String Registrar_Produccion(ProduccionTB produccionTB) {
//        PreparedStatement statementValidate = null;
        PreparedStatement statementRegisrar = null;
//        PreparedStatement statementDetalle = null;
        CallableStatement statementCodigo = null;
//        PreparedStatement statementInventario = null;
//        PreparedStatement statementInsumo = null;

        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            statementCodigo = DBUtil.getConnection().prepareCall("{? = call Fc_Produccion_Codigo_Alfanumerico()}");
            statementCodigo.registerOutParameter(1, java.sql.Types.VARCHAR);
            statementCodigo.execute();
            String id_produccion = statementCodigo.getString(1);

            statementRegisrar = DBUtil.getConnection().prepareStatement("INSERT INTO ProduccionTB("
                    + "IdProduccion,"
                    + "FechaInico,"
                    + "HoraInicio,"
                    + "Dias,"
                    + "Horas,"
                    + "Minutos,"
                    + "IdProducto,"
                    + "TipoOrden,"
                    + "IdEncargado,"
                    + "Descripcion,"
                    + "FechaRegistro,"
                    + "HoraRegistro,"
                    + "Cantidad,"
                    + "CostoAdicioanal,"
                    + "Estado"
                    + ")VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            statementRegisrar.setString(1, id_produccion);
            statementRegisrar.setString(2, produccionTB.getFechaInicio());
            statementRegisrar.setString(3, produccionTB.getHoraInicio());
            statementRegisrar.setInt(4, produccionTB.getDias());
            statementRegisrar.setInt(5, produccionTB.getHoras());
            statementRegisrar.setInt(6, produccionTB.getMinutos());
            statementRegisrar.setString(7, produccionTB.getIdProducto());
            statementRegisrar.setBoolean(8, produccionTB.isTipoOrden());
            statementRegisrar.setString(9, produccionTB.getIdEncargado());
            statementRegisrar.setString(10, produccionTB.getDescripcion());
            statementRegisrar.setString(11, produccionTB.getFechaRegistro());
            statementRegisrar.setString(12, produccionTB.getHoraRegistro());
            statementRegisrar.setDouble(13, produccionTB.getCantidad());
            statementRegisrar.setDouble(14, produccionTB.getCostoAdicional());
            statementRegisrar.setInt(15, produccionTB.getEstado());
            statementRegisrar.addBatch();

//            statementValidate = DBUtil.getConnection().prepareStatement("SELECT Costo FROM InsumoTB WHERE IdInsumo = ? ");
//            statementDetalle = DBUtil.getConnection().prepareStatement("INSERT INTO ProduccionDetalleTB(IdProduccion,IdProducto,Cantidad,Costo,CantidadUtilizada,CantidadAUtilizar, Merma)VALUES(?,?,?,?,?,?,?)");

            double CostoTotal = 0;
//            for (SuministroTB suministroTB : produccionTB.getSuministroTB()) {
//                statementValidate.setString(1, suministroTB.getComboBox().getSelectionModel().getSelectedItem().getIdInsumo());
//                ResultSet resultSet = statementValidate.executeQuery();
//                if (resultSet.next()) {
//                    statementDetalle.setString(1, id_produccion);
//                    statementDetalle.setString(2, insumoTB.getComboBox().getSelectionModel().getSelectedItem().getIdInsumo());
//                    statementDetalle.setDouble(3, Double.parseDouble(insumoTB.getTxtCantidad().getText()) / produccionTB.getCantidad());
//                    statementDetalle.setDouble(4, resultSet.getDouble("Costo"));
//                    statementDetalle.setDouble(5, (Double.parseDouble(insumoTB.getTxtCantidad().getText()) / produccionTB.getCantidad()) * produccionTB.getCantidad());
//                    statementDetalle.setDouble(6, 0);
//                    statementDetalle.setDouble(7, 0);
//                    statementDetalle.addBatch();
//                    CostoTotal += (Double.parseDouble(insumoTB.getTxtCantidad().getText()) * resultSet.getDouble("Costo"));
//                }
//            }

            if (produccionTB.getEstado() == 1) {
//                statementInventario = DBUtil.getConnection().prepareStatement("update SuministroTB set Cantidad = Cantidad + ?, PrecioCompra = ? where IdSuministro = ?");
//                statementInventario.setDouble(1, produccionTB.getCantidad());
//                statementInventario.setDouble(2, CostoTotal);
//                statementInventario.setString(3, produccionTB.getIdProducto());
//                statementInventario.addBatch();

//                statementInsumo = DBUtil.getConnection().prepareStatement("UPDATE InsumoTB set Cantidad = (Cantidad - ?) where IdInsumo = ?");
//                for (InsumoTB insumoTB : produccionTB.getInsumoTBs()) {
//                    statementInsumo.setDouble(1, Double.parseDouble(insumoTB.getTxtCantidad().getText()) * produccionTB.getCantidad());
//                    statementInsumo.setString(2, insumoTB.getComboBox().getSelectionModel().getSelectedItem().getIdInsumo());
//                    statementInsumo.addBatch();
//                }
//                statementInventario.executeBatch();
//                statementInsumo.executeBatch();
            }

//            statementDetalle.executeBatch();
            statementRegisrar.executeBatch();

            DBUtil.getConnection().commit();
            return "registrado";
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException e) {

            }
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementRegisrar != null) {
                    statementRegisrar.close();
                }
//                if (statementDetalle != null) {
//                    statementDetalle.close();
//                }
                if (statementCodigo != null) {
                    statementCodigo.close();
                }
//                if (statementInventario != null) {
//                    statementInventario.close();
//                }
//                if (statementInsumo != null) {
//                    statementInsumo.close();
//                }
//                if (statementValidate != null) {
//                    statementValidate.close();
//                }
            } catch (SQLException e) {

            }
        }
    }

    public static String Actualizar_Produccion(ProduccionTB produccionTB) {
        PreparedStatement statementValidate = null;
        PreparedStatement statementActualizar = null;
        PreparedStatement statementDetalle = null;
        PreparedStatement statementInventario = null;
        PreparedStatement statementInsumo = null;
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            statementActualizar = DBUtil.getConnection().prepareStatement("UPDATE ProduccionTB SET FechaInico = ?, HoraInicio = ?, Dias = ?, Horas = ?, Minutos = ?, "
                    + "TipoOrden = ?, Descripcion = ?, Cantidad = ?, Estado = ?\n"
                    + "WHERE IdProduccion = ?");
            statementActualizar.setString(1, produccionTB.getFechaInicio());
            statementActualizar.setString(2, produccionTB.getHoraInicio());
            statementActualizar.setInt(3, produccionTB.getDias());
            statementActualizar.setInt(4, produccionTB.getHoras());
            statementActualizar.setInt(5, produccionTB.getMinutos());
            statementActualizar.setBoolean(6, produccionTB.isTipoOrden());
            statementActualizar.setString(7, produccionTB.getDescripcion());
            statementActualizar.setDouble(8, produccionTB.getCantidad());
            statementActualizar.setDouble(9, produccionTB.getEstado());
            statementActualizar.setString(10, produccionTB.getIdProduccion());
            statementActualizar.addBatch();

            statementDetalle = DBUtil.getConnection().prepareStatement("DELETE FROM ProduccionDetalleTB WHERE IdProduccion = ?");
            statementDetalle.setString(1, produccionTB.getIdProduccion());
            statementDetalle.addBatch();
            statementDetalle.executeBatch();

            statementValidate = DBUtil.getConnection().prepareStatement("SELECT Costo FROM InsumoTB WHERE IdInsumo = ? ");
            statementDetalle = DBUtil.getConnection().prepareStatement("INSERT INTO ProduccionDetalleTB(IdProduccion,IdProducto,Cantidad,Costo,CantidadUtilizada,CantidadAUtilizar,Merma)VALUES(?,?,?,?,?,?,?)");

            double CostoTotal = 0;
//            for (InsumoTB insumoTB : produccionTB.getInsumoTBs()) {
//                statementValidate.setString(1, insumoTB.getComboBox().getSelectionModel().getSelectedItem().getIdInsumo());
//                ResultSet resultSet = statementValidate.executeQuery();
//                if (resultSet.next()) {
//                    Tools.println(insumoTB.getComboBox().getSelectionModel().getSelectedItem().getIdInsumo());
//                    statementDetalle.setString(1, produccionTB.getIdProduccion());
//                    statementDetalle.setString(2, insumoTB.getComboBox().getSelectionModel().getSelectedItem().getIdInsumo());
//                    statementDetalle.setDouble(3, Double.parseDouble(insumoTB.getTxtCantidad().getText()));
//                    statementDetalle.setDouble(4, resultSet.getDouble("Costo"));
//                    statementDetalle.setDouble(5, Double.parseDouble(insumoTB.getTxtCantidadUtilizada().getText()) + Double.parseDouble(insumoTB.getTxtCantidadAUtilizar().getText()));
//                    statementDetalle.setDouble(6, 0);
//                    statementDetalle.setDouble(7, Double.parseDouble(insumoTB.getTxtMerma().getText().equalsIgnoreCase("") ? "0" : insumoTB.getTxtMerma().getText()));
//                    statementDetalle.addBatch();
//                    CostoTotal += (Double.parseDouble(insumoTB.getTxtCantidad().getText()) * resultSet.getDouble("Costo"));
//                }
//            }

            if (produccionTB.getEstado() == 1) {
                statementInventario = DBUtil.getConnection().prepareStatement("update SuministroTB set Cantidad = Cantidad + ?, PrecioCompra = ? where IdSuministro = ?");
                statementInventario.setDouble(1, produccionTB.getCantidad());
                statementInventario.setDouble(2, CostoTotal);
                statementInventario.setString(3, produccionTB.getIdProducto());
                statementInventario.addBatch();

                statementInsumo = DBUtil.getConnection().prepareStatement("UPDATE InsumoTB set Cantidad = (Cantidad - ?) where IdInsumo = ?");
//                for (InsumoTB insumoTB : produccionTB.getInsumoTBs()) {
////                    statementInsumo.setDouble(1, Double.parseDouble(insumoTB.getTxtCantidad().getText()) * produccionTB.getCantidad());
//                    statementInsumo.setDouble(1, Double.parseDouble(insumoTB.getTxtCantidadUtilizada().getText()));
//                    statementInsumo.setString(2, insumoTB.getComboBox().getSelectionModel().getSelectedItem().getIdInsumo());
//                    statementInsumo.addBatch();
//                }

                statementInventario.executeBatch();
                statementInsumo.executeBatch();
            }
            statementActualizar.executeBatch();
            statementDetalle.executeBatch();
            DBUtil.getConnection().commit();
            return "actualizado";
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException e) {

            }
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementActualizar != null) {
                    statementActualizar.close();
                }
                if (statementDetalle != null) {
                    statementDetalle.close();
                }
                if (statementInventario != null) {
                    statementInventario.close();
                }
                if (statementInsumo != null) {
                    statementInsumo.close();
                }
                if (statementValidate != null) {
                    statementValidate.close();
                }
            } catch (SQLException e) {

            }
        }
    }

    public static Object ListarProduccion(int tipo, String fechaInicio, String fechaFinal, String busqueda, int posicionPagina, int filasPorPagina) {
        PreparedStatement statementListar = null;
        try {
            DBUtil.dbConnect();
            Object[] objects = new Object[2];

            ObservableList<ProduccionTB> produccionTBs = FXCollections.observableArrayList();
            statementListar = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_Produccion(?,?,?,?,?,?)}");
            statementListar.setInt(1, tipo);
            statementListar.setString(2, fechaInicio);
            statementListar.setString(3, fechaFinal);
            statementListar.setString(4, busqueda);
            statementListar.setInt(5, posicionPagina);
            statementListar.setInt(6, filasPorPagina);
            ResultSet resultSet = statementListar.executeQuery();
            while (resultSet.next()) {
                ProduccionTB produccionTB = new ProduccionTB();
                produccionTB.setId(resultSet.getRow() + posicionPagina);
                produccionTB.setIdProduccion(resultSet.getString("IdProduccion"));
                produccionTB.setFechaRegistro(resultSet.getDate("FechaRegistro").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM//yyyy")));
                produccionTB.setHoraRegistro(resultSet.getTime("HoraRegistro").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                produccionTB.setFechaInicio(resultSet.getDate("FechaInico").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM//yyyy")));
                produccionTB.setHoraInicio(resultSet.getTime("HoraInicio").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                produccionTB.setDias(resultSet.getInt("Dias"));
                produccionTB.setHoras(resultSet.getInt("Horas"));
                produccionTB.setMinutos(resultSet.getInt("Minutos"));
                produccionTB.setCantidad(resultSet.getDouble("Cantidad"));

                SuministroTB suministroTB = new SuministroTB();
                suministroTB.setClave(resultSet.getString("Clave"));
                suministroTB.setNombreMarca(resultSet.getString("NombreMarca"));
                suministroTB.setUnidadCompraName(resultSet.getString("Medida"));
                produccionTB.setSuministroTB(suministroTB);

                EmpleadoTB empleadoTB = new EmpleadoTB();
                empleadoTB.setNumeroDocumento(resultSet.getString("NumeroDocumento"));
                empleadoTB.setApellidos(resultSet.getString("Apellidos"));
                empleadoTB.setNombres(resultSet.getString("Nombres"));
                produccionTB.setEmpleadoTB(empleadoTB);

                Label label = new Label();
                switch (resultSet.getInt("Estado")) {
                    case 3:
                        produccionTB.setEstado(3);
                        label.setText("ANULADO");
                        label.getStyleClass().add("label-proceso");
                        break;
                    case 2:
                        produccionTB.setEstado(2);
                        label.setText("EN PRODUCCIÓN");
                        label.getStyleClass().add("label-medio");
                        break;
                    default:
                        produccionTB.setEstado(1);
                        label.setText("COMPLETADO");
                        label.getStyleClass().add("label-asignacion");
                        break;
                }
                produccionTB.setLblEstado(label);

                produccionTBs.add(produccionTB);
            }

            statementListar = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_Produccion_Count(?,?,?,?)}");
            statementListar.setInt(1, tipo);
            statementListar.setString(2, fechaInicio);
            statementListar.setString(3, fechaFinal);
            statementListar.setString(4, busqueda);
            resultSet = statementListar.executeQuery();
            Integer cantidadTotal = 0;
            if (resultSet.next()) {
                cantidadTotal = resultSet.getInt("Total");
            }

            objects[0] = produccionTBs;
            objects[1] = cantidadTotal;

            return objects;
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementListar != null) {
                    statementListar.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }

    public static Object Obtener_Produccion_ById(String idProduccion) {
        PreparedStatement statementProduccion = null;
        PreparedStatement statementDetalleProduccion = null;
        ResultSet resultSet = null;

        try {
            DBUtil.dbConnect();
            statementProduccion = DBUtil.getConnection().prepareStatement("select p.IdProduccion,p.FechaRegistro,p.HoraRegistro,\n"
                    + "		p.FechaInico,p.HoraInicio,p.Dias,p.Horas,p.Minutos,\n"
                    + "		s.Clave,p.IdProducto, s.NombreMarca, p.Descripcion,\n"
                    + "		p.Cantidad,d.Nombre as Medida, p.TipoOrden,\n"
                    + "		e.NumeroDocumento,p.IdEncargado, e.Apellidos,e.Nombres,\n"
                    + "		p.Estado\n"
                    + "		from ProduccionTB as p\n"
                    + "		inner join SuministroTB as s on s.IdSuministro = p.IdProducto\n"
                    + "		inner join DetalleTB as d on d.IdDetalle = s.UnidadCompra and d.IdMantenimiento = '0013'\n"
                    + "		inner join EmpleadoTB as e on e.IdEmpleado = p.IdEncargado where IdProduccion = ?");
            statementProduccion.setString(1, idProduccion);
            resultSet = statementProduccion.executeQuery();
            if (resultSet.next()) {
                ProduccionTB produccionTB = new ProduccionTB();
                produccionTB.setFechaRegistro(resultSet.getString("FechaRegistro"));
                produccionTB.setFechaInicio(resultSet.getString("FechaInico"));
                produccionTB.setDias(resultSet.getInt("Dias"));
                produccionTB.setHoras(resultSet.getInt("Horas"));
                produccionTB.setMinutos(resultSet.getInt("Minutos"));
                produccionTB.setIdEncargado(resultSet.getString("IdEncargado"));
                produccionTB.setTipoOrden(resultSet.getBoolean("TipoOrden"));
                produccionTB.setDescripcion(resultSet.getString("Descripcion"));
                produccionTB.setIdProducto(resultSet.getString("IdProducto"));
                produccionTB.setCantidad(resultSet.getInt("Cantidad"));
                produccionTB.setSuministroTB(new SuministroTB(resultSet.getString("IdProducto"), resultSet.getString("Clave"), resultSet.getString("NombreMarca")));
                produccionTB.setEmpleadoTB(new EmpleadoTB(resultSet.getString("IdEncargado"), resultSet.getString("Apellidos"), resultSet.getString("Nombres")));
                produccionTB.setEstado(resultSet.getInt("Estado"));

                statementDetalleProduccion = DBUtil.getConnection().prepareStatement("SELECT i.IdInsumo, pd.IdProducto, i.Clave,i.NombreMarca,ISNULL(d.Nombre, 'S/M') AS Medida,pd.Cantidad, pd.CantidadUtilizada, pd.CantidadAUtilizar,pd.Costo, pd.Merma FROM ProduccionDetalleTB AS pd\n"
                        + "INNER JOIN InsumoTB AS i ON pd.IdProducto = i.IdInsumo\n"
                        + "INNER JOIN DetalleTB AS d ON d.IdDetalle = i.Medida AND d.IdMantenimiento = '0013'\n"
                        + "WHERE pd.IdProduccion = ?");
                statementDetalleProduccion.setString(1, idProduccion);
                resultSet = statementDetalleProduccion.executeQuery();

                ArrayList<SuministroTB> insumoTBs = new ArrayList();

                while (resultSet.next()) {
                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setCostoCompra(resultSet.getDouble("Costo"));
                    suministroTB.setUnidadCompraName(resultSet.getString("Medida"));
                    suministroTB.setCantidad(resultSet.getDouble("Cantidad"));
                    suministroTB.setNombreMarca(resultSet.getString("NombreMarca"));
//                    suministroTB.setCantidadUtilizada(resultSet.getDouble("CantidadUtilizada"));
//                    suministroTB.setCantidadAUtilizar(resultSet.getDouble("CantidadAUtilizar"));
//                    suministroTB.setMerma(resultSet.getDouble("Merma"));
//
//                    ComboBox<SuministroTB> comboBox = new ComboBox();
//                    comboBox.setPromptText("-- Selecionar --");
//                    comboBox.setPrefWidth(220);
//                    comboBox.setPrefHeight(30);
//                    comboBox.setMaxWidth(Double.MAX_VALUE);
//                    suministroTB.setComboBox(comboBox);

//                    SearchComboBox<SuministroTB> searchComboBox = new SearchComboBox<>(insumoTB.getComboBox(), false);
//                    searchComboBox.getSearchComboBoxSkin().getSearchBox().setOnKeyPressed(t -> {
//                        if (t.getCode() == KeyCode.ENTER) {
//                            if (!searchComboBox.getSearchComboBoxSkin().getItemView().getItems().isEmpty()) {
//                                searchComboBox.getSearchComboBoxSkin().getItemView().getSelectionModel().select(0);
//                                searchComboBox.getSearchComboBoxSkin().getItemView().requestFocus();
//                            }
//                        } else if (t.getCode() == KeyCode.ESCAPE) {
//                            searchComboBox.getComboBox().hide();
//                        }
//                    });
//
//                    searchComboBox.getSearchComboBoxSkin().getSearchBox().setOnKeyReleased(t -> {
//                        if (!Tools.isText(searchComboBox.getSearchComboBoxSkin().getSearchBox().getText())) {
//                            searchComboBox.getComboBox().getItems().clear();
//                            List<InsumoTB> list = InsumoADO.getSearchComboBoxInsumos(searchComboBox.getSearchComboBoxSkin().getSearchBox().getText().trim());
//                            list.forEach(p -> searchComboBox.getComboBox().getItems().add(p));
//                        }
//                    });
//                    searchComboBox.getSearchComboBoxSkin().getItemView().setOnKeyPressed(t -> {
//                        switch (t.getCode()) {
//                            case ENTER:
//                            case SPACE:
//                            case ESCAPE:
//                                searchComboBox.getComboBox().hide();
//                                break;
//                            case UP:
//                            case DOWN:
//                            case LEFT:
//                            case RIGHT:
//                                break;
//                            default:
//                                searchComboBox.getSearchComboBoxSkin().getSearchBox().requestFocus();
//                                searchComboBox.getSearchComboBoxSkin().getSearchBox().selectAll();
//                                break;
//                        }
//                    });
//                    searchComboBox.getSearchComboBoxSkin().getItemView().getSelectionModel().selectedItemProperty().addListener((p, o, item) -> {
//                        if (item != null) {
//                            searchComboBox.getComboBox().getSelectionModel().select(item);
//                            if (searchComboBox.getSearchComboBoxSkin().isClickSelection()) {
//                                searchComboBox.getComboBox().hide();
//                            }
//                        }
//                    });
//
////                    insumoTB.setSearchComboBox(searchComboBox);
////                    insumoTB.getComboBox().getItems().add(new InsumoTB(resultSet.getString("IdInsumo"), resultSet.getString("Clave"), resultSet.getString("NombreMarca")));
////                    insumoTB.getComboBox().getSelectionModel().select(0);
//
//                    TextField textField = new TextField(Tools.roundingValue(insumoTB.getCantidad(), 2));
//                    textField.setDisable(true);
//                    textField.setPromptText("0.00");
//                    textField.getStyleClass().add("text-field-normal");
//                    textField.setPrefWidth(220);
//                    textField.setPrefHeight(30);
//                    textField.setOnKeyTyped(event -> {
//                        char c = event.getCharacter().charAt(0);
//                        if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
//                            event.consume();
//                        }
//                        if (c == '.' && textField.getText().contains(".")) {
//                            event.consume();
//                        }
//                    });
//                    insumoTB.setTxtCantidad(textField);
//
//                    TextField textFieldCU = new TextField(Tools.roundingValue(insumoTB.getCantidadUtilizada(), 2));
//                    textFieldCU.setDisable(true);
//                    textFieldCU.setPromptText("0.00");
//                    textFieldCU.getStyleClass().add("text-field-normal");
//                    textFieldCU.setPrefWidth(220);
//                    textFieldCU.setPrefHeight(30);
//                    textFieldCU.setOnKeyTyped(event -> {
//                        char c = event.getCharacter().charAt(0);
//                        if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
//                            event.consume();
//                        }
//                        if (c == '.' && textFieldCU.getText().contains(".")) {
//                            event.consume();
//                        }
//                    });
//                    insumoTB.setTxtCantidadUtilizada(textFieldCU);
//
//                    TextField textFieldCaU = new TextField(Tools.roundingValue(insumoTB.getCantidadAUtilizar(), 2));
//                    textFieldCaU.setPromptText("0.00");
//                    textFieldCaU.getStyleClass().add("text-field-normal");
//                    textFieldCaU.setPrefWidth(220);
//                    textFieldCaU.setPrefHeight(30);
//                    textFieldCaU.setOnKeyTyped(event -> {
//                        char c = event.getCharacter().charAt(0);
//                        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
//                            event.consume();
//                        }
//                        if (c == '.' && textFieldCaU.getText().contains(".") && (c != '.')) {
//                            event.consume();
//                        }
//                    });
//                    insumoTB.setTxtCantidadAUtilizar(textFieldCaU);
//
//                    TextField textFieldMerma = new TextField(Tools.roundingValue(insumoTB.getMerma(), 2));
//                    textFieldMerma.setPromptText("0.00");
//                    textFieldMerma.getStyleClass().add("text-field-normal");
//                    textFieldMerma.setPrefWidth(220);
//                    textFieldMerma.setPrefHeight(30);
//                    textFieldMerma.setOnKeyTyped(event -> {
//                        char c = event.getCharacter().charAt(0);
//                        if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
//                            event.consume();
//                        }
//                        if (c == '.' && textFieldMerma.getText().contains(".") && (c != '.')) {
//                            event.consume();
//                        }
//                    });
//                    insumoTB.setTxtMerma(textFieldMerma);
//
//                    Button button = new Button();
//                    button.getStyleClass().add("buttonLightError");
//                    button.setAlignment(Pos.CENTER);
//                    button.setPrefWidth(Control.USE_COMPUTED_SIZE);
//                    button.setPrefHeight(Control.USE_COMPUTED_SIZE);
////                    button.setMaxWidth(Double.MAX_VALUE);
////                    button.setMaxHeight(Double.MAX_VALUE);
//                    ImageView imageView = new ImageView(new Image("/view/image/remove-gray.png"));
//                    imageView.setFitWidth(20);
//                    imageView.setFitHeight(20);
//                    button.setGraphic(imageView);
//                    insumoTB.setBtnRemove(button);
//                    insumoTBs.add(insumoTB);
                }

                produccionTB.setInsumoTBs(insumoTBs);
                return produccionTB;
            } else {
                throw new Exception("No se pudo obtener los datos de la producción");
            }
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } catch (Exception ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementProduccion != null) {
                    statementProduccion.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statementDetalleProduccion != null) {
                    statementDetalleProduccion.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }

    public static String AnularProduccion(String idProduccion) {
//        PreparedStatement statementValidate = null;
        PreparedStatement statementRegistrar = null;
//        PreparedStatement statementDetalle = null;
        String result = "";
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);
//            if (estado == 2) {
            statementRegistrar = DBUtil.getConnection().prepareStatement("UPDATE ProduccionTB SET Estado = 3 WHERE IdProduccion = ?");
            statementRegistrar.setString(1, idProduccion);
            statementRegistrar.addBatch();

            statementRegistrar.executeBatch();
            DBUtil.getConnection().commit();
            result = "anulado";
            //            } 
            //            else if (estado == 1) {
            //                statementRegistrar = DBUtil.getConnection().prepareStatement("UPDATE ProduccionTB SET Estado = 3 WHERE IdProduccion = ?");
            //                statementRegistrar.setString(1, idProduccion);
            //                statementRegistrar.addBatch();
            //
            //                statementValidate = DBUtil.getConnection().prepareStatement("select IdProducto, CantidadUtilizada from ProduccionDetalleTB WHERE IdProduccion = ?");
            //                statementDetalle = DBUtil.getConnection().prepareStatement("UPDATE InsumoTB set Cantidad = (Cantidad + ?) where IdInsumo = ?");
            //
            //                statementValidate.setString(1, idProduccion);
            //                ResultSet resultSet = statementValidate.executeQuery();
            //                while (resultSet.next()) {
            //                    statementDetalle.setDouble(1, resultSet.getDouble("CantidadUtilizada"));
            //                    statementDetalle.setString(2, resultSet.getString("IdProducto"));
            //                    statementDetalle.addBatch();
            //                }
            //
            //                statementRegistrar.executeBatch();
            //                statementDetalle.executeBatch();
            //                DBUtil.getConnection().commit();
            //                result = "anulado";
            //            } 
//            else if(estado == 1){
//                result = "No se puede anular una producción que ya se encuentra en estado COMPLETADO";
//            } else {
//                result = "Dicha producción ya se encuentra anulada";
//            }

            return result;
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException e) {

            }
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementRegistrar != null) {
                    statementRegistrar.close();
                }
//                if (statementDetalle != null) {
//                    statementDetalle.close();
//                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
    }
}
