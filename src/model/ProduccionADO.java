package model;

import controller.tools.SearchComboBox;
import controller.tools.Session;
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

    public static String Crud_Produccion(ProduccionTB produccionTB) {

        CallableStatement statementProduccionCodigo = null;
        CallableStatement statementMermaCodigo = null;
        PreparedStatement statementValidate = null;
        PreparedStatement statementProducion = null;
        PreparedStatement statementProduccionDetalle = null;
        PreparedStatement statementProduccionHistorial = null;
        PreparedStatement statementSuministro = null;
        PreparedStatement statementSuministroKardex = null;
        PreparedStatement statementInventario = null;
        PreparedStatement statementInventarioKardex = null;
        PreparedStatement statementMerma = null;
        PreparedStatement statementMermaDetalle = null;

        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            statementValidate = DBUtil.getConnection().prepareCall("SELECT * FROM ProduccionTB WHERE IdProduccion = ?");
            statementValidate.setString(1, produccionTB.getIdProduccion());
            if (statementValidate.executeQuery().next()) {

                statementValidate = DBUtil.getConnection().prepareCall("SELECT * FROM ProduccionTB WHERE IdProduccion = ? AND Estado <> 2");
                statementValidate.setString(1, produccionTB.getIdProduccion());
                if (statementValidate.executeQuery().next()) {
                    DBUtil.getConnection().rollback();
                    return "state";
                } else {
                    statementProducion = DBUtil.getConnection().prepareStatement("UPDATE ProduccionTB SET Dias = Dias + ?, Horas = Horas + ?, Minutos = Minutos + ?,Estado = ? WHERE IdProduccion = ?");
                    statementProducion.setInt(1, produccionTB.getDias());
                    statementProducion.setInt(2, produccionTB.getHoras());
                    statementProducion.setInt(3, produccionTB.getMinutos());
                    statementProducion.setInt(4, produccionTB.getEstado());
                    statementProducion.setString(5, produccionTB.getIdProduccion());
                    statementProducion.addBatch();

                    statementProduccionDetalle = DBUtil.getConnection().prepareStatement("DELETE FROM ProduccionDetalleTB WHERE IdProduccion = ?");
                    statementProduccionDetalle.setString(1, produccionTB.getIdProduccion());
                    statementProduccionDetalle.addBatch();
                    statementProduccionDetalle.executeBatch();

                    statementValidate = DBUtil.getConnection().prepareStatement("SELECT PrecioCompra FROM SuministroTB WHERE IdSuministro = ? ");
                    statementProduccionDetalle = DBUtil.getConnection().prepareStatement("INSERT INTO ProduccionDetalleTB(IdProduccion,IdProducto,Cantidad,Costo)VALUES(?,?,?,?)");

                    double costoMateriaPrima = 0;
                    double cantidadTotal = 0;

                    for (SuministroTB suministroTB : produccionTB.getSuministroInsumos()) {
                        statementValidate.setString(1, suministroTB.getCbSuministro().getSelectionModel().getSelectedItem().getIdSuministro());
                        ResultSet resultSet = statementValidate.executeQuery();
                        if (resultSet.next()) {
                            statementProduccionDetalle.setString(1, produccionTB.getIdProduccion());
                            statementProduccionDetalle.setString(2, suministroTB.getCbSuministro().getSelectionModel().getSelectedItem().getIdSuministro());
                            statementProduccionDetalle.setDouble(3, Double.parseDouble(suministroTB.getTxtCantidad().getText()));
                            statementProduccionDetalle.setDouble(4, resultSet.getDouble("PrecioCompra"));
                            statementProduccionDetalle.addBatch();

                            cantidadTotal += Double.parseDouble(suministroTB.getTxtCantidad().getText());
                            costoMateriaPrima += resultSet.getDouble("PrecioCompra") * Double.parseDouble(suministroTB.getTxtCantidad().getText());
                        }
                    }

                    statementProduccionHistorial = DBUtil.getConnection().prepareStatement("INSERT INTO ProduccionHistorialTB(IdProduccion,IdEmpleado,Fecha,Hora,Movimiento,Dias,Horas,Minutos,Descripcion) VALUES(?,?,?,?,?,?,?,?,?)");
                    statementProduccionHistorial.setString(1, produccionTB.getIdProduccion());
                    statementProduccionHistorial.setString(2, Session.USER_ID);
                    statementProduccionHistorial.setString(3, produccionTB.getFechaRegistro());
                    statementProduccionHistorial.setString(4, produccionTB.getHoraRegistro());
                    statementProduccionHistorial.setDouble(5, cantidadTotal);
                    statementProduccionHistorial.setInt(6, produccionTB.getDias());
                    statementProduccionHistorial.setInt(7, produccionTB.getHoras());
                    statementProduccionHistorial.setInt(8, produccionTB.getMinutos());
                    statementProduccionHistorial.setString(9, produccionTB.getDescripcion());
                    statementProduccionHistorial.addBatch();

                    statementSuministro = DBUtil.getConnection().prepareStatement("UPDATE SuministroTB set Cantidad = Cantidad + ?, PrecioCompra = ? where IdSuministro = ?");
                    statementSuministroKardex = DBUtil.getConnection().prepareStatement("INSERT INTO KardexSuministroTB(IdSuministro,Fecha,Hora,Tipo,Movimiento,Detalle,Cantidad,Costo,Total)VALUES(?,?,?,?,?,?,?,?,?)");
                    statementInventario = DBUtil.getConnection().prepareStatement("UPDATE SuministroTB set Cantidad = (Cantidad - ?) where IdSuministro = ?");
                    statementInventarioKardex = DBUtil.getConnection().prepareStatement("INSERT INTO KardexSuministroTB(IdSuministro,Fecha,Hora,Tipo,Movimiento,Detalle,Cantidad,Costo, Total)VALUES(?,?,?,?,?,?,?,?,?)");

                    if (produccionTB.getEstado() == 1) {
                        double costoProducto = (costoMateriaPrima + produccionTB.getCostoAdicional()) / produccionTB.getCantidad();

                        statementSuministro.setDouble(1, produccionTB.getCantidad());
                        statementSuministro.setDouble(2, costoProducto);
                        statementSuministro.setString(3, produccionTB.getIdProducto());
                        statementSuministro.addBatch();

                        statementSuministroKardex.setString(1, produccionTB.getIdProducto());
                        statementSuministroKardex.setString(2, Tools.getDate());
                        statementSuministroKardex.setString(3, Tools.getTime());
                        statementSuministroKardex.setShort(4, (short) 1);
                        statementSuministroKardex.setInt(5, 2);
                        statementSuministroKardex.setString(6, "INGRESO POR PRODUCCIÓN DEL LA NUMERACIÓN " + produccionTB.getIdProduccion());
                        statementSuministroKardex.setDouble(7, produccionTB.getCantidad());
                        statementSuministroKardex.setDouble(8, costoProducto);
                        statementSuministroKardex.setDouble(9, produccionTB.getCantidad() * costoProducto);
                        statementSuministroKardex.addBatch();

                        for (SuministroTB suministroTB : produccionTB.getSuministroInsumos()) {
                            statementValidate.setString(1, suministroTB.getCbSuministro().getSelectionModel().getSelectedItem().getIdSuministro());
                            ResultSet resultSet = statementValidate.executeQuery();
                            if (resultSet.next()) {

                                statementInventario.setDouble(1, Double.parseDouble(suministroTB.getTxtCantidad().getText()));
                                statementInventario.setString(2, suministroTB.getCbSuministro().getSelectionModel().getSelectedItem().getIdSuministro());
                                statementInventario.addBatch();

                                statementInventarioKardex.setString(1, suministroTB.getCbSuministro().getSelectionModel().getSelectedItem().getIdSuministro());
                                statementInventarioKardex.setString(2, Tools.getDate());
                                statementInventarioKardex.setString(3, Tools.getTime());
                                statementInventarioKardex.setShort(4, (short) 2);
                                statementInventarioKardex.setInt(5, 1);
                                statementInventarioKardex.setString(6, "SALIDA POR PRODUCCIÓN DEL LA NUMERACIÓN " + produccionTB.getIdProduccion());
                                statementInventarioKardex.setDouble(7, Double.parseDouble(suministroTB.getTxtCantidad().getText()));
                                statementInventarioKardex.setDouble(8, resultSet.getDouble("PrecioCompra"));
                                statementInventarioKardex.setDouble(9, Double.parseDouble(suministroTB.getTxtCantidad().getText()) * resultSet.getDouble("PrecioCompra"));
                                statementInventarioKardex.addBatch();

                            }
                        }
                    }

                    statementProducion.executeBatch();
                    statementProduccionDetalle.executeBatch();
                    statementProduccionHistorial.executeBatch();
                    statementSuministro.executeBatch();
                    statementSuministroKardex.executeBatch();
                    statementInventario.executeBatch();
                    statementInventarioKardex.executeBatch();
                    DBUtil.getConnection().commit();
                    return "updated";
                }

            } else {

                statementProduccionCodigo = DBUtil.getConnection().prepareCall("{? = call Fc_Produccion_Codigo_Alfanumerico()}");
                statementProduccionCodigo.registerOutParameter(1, java.sql.Types.VARCHAR);
                statementProduccionCodigo.execute();
                String id_produccion = statementProduccionCodigo.getString(1);

                statementProducion = DBUtil.getConnection().prepareStatement("INSERT INTO ProduccionTB("
                        + "IdProduccion,"
                        + "Proyecto,"
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
                        + ")VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                statementProducion.setString(1, id_produccion);
                statementProducion.setString(2, produccionTB.getProyecto());
                statementProducion.setString(3, produccionTB.getFechaInicio());
                statementProducion.setString(4, produccionTB.getHoraInicio());
                statementProducion.setInt(5, produccionTB.getDias());
                statementProducion.setInt(6, produccionTB.getHoras());
                statementProducion.setInt(7, produccionTB.getMinutos());
                statementProducion.setString(8, produccionTB.getIdProducto());
                statementProducion.setBoolean(9, produccionTB.isTipoOrden());
                statementProducion.setString(10, produccionTB.getIdEncargado());
                statementProducion.setString(11, produccionTB.getDescripcion());
                statementProducion.setString(12, produccionTB.getFechaRegistro());
                statementProducion.setString(13, produccionTB.getHoraRegistro());
                statementProducion.setDouble(14, produccionTB.getCantidad());
                statementProducion.setDouble(15, produccionTB.getCostoAdicional());
                statementProducion.setInt(16, produccionTB.getEstado());
                statementProducion.addBatch();

                statementValidate = DBUtil.getConnection().prepareStatement("SELECT PrecioCompra FROM SuministroTB WHERE IdSuministro = ? ");
                statementProduccionDetalle = DBUtil.getConnection().prepareStatement("INSERT INTO ProduccionDetalleTB(IdProduccion,IdProducto,Cantidad,Costo)VALUES(?,?,?,?)");

                double costoMateriaPrima = 0;
                double cantidadTotal = 0;

                for (SuministroTB suministroTB : produccionTB.getSuministroInsumos()) {
                    statementValidate.setString(1, suministroTB.getCbSuministro().getSelectionModel().getSelectedItem().getIdSuministro());
                    ResultSet resultSet = statementValidate.executeQuery();
                    if (resultSet.next()) {
                        statementProduccionDetalle.setString(1, id_produccion);
                        statementProduccionDetalle.setString(2, suministroTB.getCbSuministro().getSelectionModel().getSelectedItem().getIdSuministro());
                        statementProduccionDetalle.setDouble(3, Double.parseDouble(suministroTB.getTxtCantidad().getText()));
                        statementProduccionDetalle.setDouble(4, resultSet.getDouble("PrecioCompra"));
                        statementProduccionDetalle.addBatch();

                        cantidadTotal += Double.parseDouble(suministroTB.getTxtCantidad().getText());
                        costoMateriaPrima += resultSet.getDouble("PrecioCompra") * Double.parseDouble(suministroTB.getTxtCantidad().getText());
                    }
                }

                statementProduccionHistorial = DBUtil.getConnection().prepareStatement("INSERT INTO ProduccionHistorialTB(IdProduccion,IdEmpleado,Fecha,Hora,Movimiento,Dias,Horas,Minutos,Descripcion) VALUES(?,?,?,?,?,?,?,?,?)");
                statementProduccionHistorial.setString(1, id_produccion);
                statementProduccionHistorial.setString(2, Session.USER_ID);
                statementProduccionHistorial.setString(3, produccionTB.getFechaRegistro());
                statementProduccionHistorial.setString(4, produccionTB.getHoraRegistro());
                statementProduccionHistorial.setDouble(5, cantidadTotal);
                statementProduccionHistorial.setInt(6, produccionTB.getDias());
                statementProduccionHistorial.setInt(7, produccionTB.getHoras());
                statementProduccionHistorial.setInt(8, produccionTB.getMinutos());
                statementProduccionHistorial.setString(9, produccionTB.getDescripcion());
                statementProduccionHistorial.addBatch();

                statementSuministro = DBUtil.getConnection().prepareStatement("UPDATE SuministroTB set Cantidad = Cantidad + ?, PrecioCompra = ? where IdSuministro = ?");
                statementSuministroKardex = DBUtil.getConnection().prepareStatement("INSERT INTO KardexSuministroTB(IdSuministro,Fecha,Hora,Tipo,Movimiento,Detalle,Cantidad,Costo,Total)VALUES(?,?,?,?,?,?,?,?,?)");
                statementInventario = DBUtil.getConnection().prepareStatement("UPDATE SuministroTB set Cantidad = (Cantidad - ?) where IdSuministro = ?");
                statementInventarioKardex = DBUtil.getConnection().prepareStatement("INSERT INTO KardexSuministroTB(IdSuministro,Fecha,Hora,Tipo,Movimiento,Detalle,Cantidad,Costo, Total)VALUES(?,?,?,?,?,?,?,?,?)");
                statementMerma = DBUtil.getConnection().prepareStatement("INSERT INTO MermaTB(IdMerma,IdProduccion,IdUsuario) VALUES(?,?,?)");
                statementMermaDetalle = DBUtil.getConnection().prepareStatement("INSERT INTO MermaDetalleTB(IdMerma,IdProducto,Cantidad,Costo) VALUES(?,?,?,?)");

                if (produccionTB.getEstado() == 1) {
                    double costoProducto = (costoMateriaPrima + produccionTB.getCostoAdicional()) / produccionTB.getCantidad();

                    statementSuministro.setDouble(1, produccionTB.getCantidad());
                    statementSuministro.setDouble(2, costoProducto);
                    statementSuministro.setString(3, produccionTB.getIdProducto());
                    statementSuministro.addBatch();

                    statementSuministroKardex.setString(1, produccionTB.getIdProducto());
                    statementSuministroKardex.setString(2, Tools.getDate());
                    statementSuministroKardex.setString(3, Tools.getTime());
                    statementSuministroKardex.setShort(4, (short) 1);
                    statementSuministroKardex.setInt(5, 2);
                    statementSuministroKardex.setString(6, "INGRESO POR PRODUCCIÓN DEL LA NUMERACIÓN " + id_produccion);
                    statementSuministroKardex.setDouble(7, produccionTB.getCantidad());
                    statementSuministroKardex.setDouble(8, costoProducto);
                    statementSuministroKardex.setDouble(9, produccionTB.getCantidad() * costoProducto);
                    statementSuministroKardex.addBatch();

                    for (SuministroTB suministroTB : produccionTB.getSuministroInsumos()) {
                        statementValidate.setString(1, suministroTB.getCbSuministro().getSelectionModel().getSelectedItem().getIdSuministro());
                        ResultSet resultSet = statementValidate.executeQuery();
                        if (resultSet.next()) {

                            statementInventario.setDouble(1, Double.parseDouble(suministroTB.getTxtCantidad().getText()));
                            statementInventario.setString(2, suministroTB.getCbSuministro().getSelectionModel().getSelectedItem().getIdSuministro());
                            statementInventario.addBatch();

                            statementInventarioKardex.setString(1, suministroTB.getCbSuministro().getSelectionModel().getSelectedItem().getIdSuministro());
                            statementInventarioKardex.setString(2, Tools.getDate());
                            statementInventarioKardex.setString(3, Tools.getTime());
                            statementInventarioKardex.setShort(4, (short) 2);
                            statementInventarioKardex.setInt(5, 1);
                            statementInventarioKardex.setString(6, "SALIDA POR PRODUCCIÓN DEL LA NUMERACIÓN " + id_produccion);
                            statementInventarioKardex.setDouble(7, Double.parseDouble(suministroTB.getTxtCantidad().getText()));
                            statementInventarioKardex.setDouble(8, resultSet.getDouble("PrecioCompra"));
                            statementInventarioKardex.setDouble(9, Double.parseDouble(suministroTB.getTxtCantidad().getText()) * resultSet.getDouble("PrecioCompra"));
                            statementInventarioKardex.addBatch();
                        }
                    }

                    if (!produccionTB.getSuministroMermas().isEmpty()) {
                        statementMermaCodigo = DBUtil.getConnection().prepareCall("{? = call Fc_Merma_Codigo_Alfanumerico()}");
                        statementMermaCodigo.registerOutParameter(1, java.sql.Types.VARCHAR);
                        statementMermaCodigo.execute();
                        String id_merma = statementMermaCodigo.getString(1);

                        statementMerma.setString(1, id_merma);
                        statementMerma.setString(2, id_produccion);
                        statementMerma.setString(3, Session.USER_ID);
                        statementMerma.addBatch();

                        for (SuministroTB suministroTB : produccionTB.getSuministroMermas()) {
                            statementValidate.setString(1, suministroTB.getCbSuministro().getSelectionModel().getSelectedItem().getIdSuministro());
                            ResultSet resultSet = statementValidate.executeQuery();
                            if (resultSet.next()) {
                                statementMermaDetalle.setString(1, id_merma);
                                statementMermaDetalle.setString(2, suministroTB.getCbSuministro().getSelectionModel().getSelectedItem().getIdSuministro());
                                statementMermaDetalle.setDouble(3, Double.parseDouble(suministroTB.getTxtCantidad().getText()));
                                statementMermaDetalle.setDouble(4, resultSet.getDouble("PrecioCompra"));
                                statementMermaDetalle.addBatch();
                            }
                        }

                    }

                }

                statementProducion.executeBatch();
                statementProduccionDetalle.executeBatch();
                statementProduccionHistorial.executeBatch();
                statementInventario.executeBatch();
                statementInventarioKardex.executeBatch();
                statementSuministro.executeBatch();
                statementSuministroKardex.executeBatch();
                statementMerma.executeBatch();
                statementMermaDetalle.executeBatch();
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
                if (statementProducion != null) {
                    statementProducion.close();
                }
                if (statementProduccionDetalle != null) {
                    statementProduccionDetalle.close();
                }
                if (statementProduccionCodigo != null) {
                    statementProduccionCodigo.close();
                }
                if (statementProduccionHistorial != null) {
                    statementProduccionHistorial.close();
                }
                if (statementInventario != null) {
                    statementInventario.close();
                }
                if (statementSuministro != null) {
                    statementSuministro.close();
                }
                if (statementValidate != null) {
                    statementValidate.close();
                }
                if (statementSuministroKardex != null) {
                    statementSuministroKardex.close();
                }
                if (statementInventarioKardex != null) {
                    statementInventarioKardex.close();
                }
                if (statementMerma != null) {
                    statementMerma.close();
                }
                if (statementMermaDetalle != null) {
                    statementMermaDetalle.close();
                }
            } catch (SQLException e) {

            }
        }
    }

    public static Object ListarProduccion(int tipo, String fechaInicio, String fechaFinal, String busqueda, int estado, int posicionPagina, int filasPorPagina) {
        PreparedStatement statementListar = null;
        try {
            DBUtil.dbConnect();
            Object[] objects = new Object[2];

            ObservableList<ProduccionTB> produccionTBs = FXCollections.observableArrayList();
            statementListar = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_Produccion(?,?,?,?,?,?,?)}");
            statementListar.setInt(1, tipo);
            statementListar.setString(2, fechaInicio);
            statementListar.setString(3, fechaFinal);
            statementListar.setString(4, busqueda);
            statementListar.setInt(5, estado);
            statementListar.setInt(6, posicionPagina);
            statementListar.setInt(7, filasPorPagina);
            ResultSet resultSet = statementListar.executeQuery();
            while (resultSet.next()) {
                ProduccionTB produccionTB = new ProduccionTB();
                produccionTB.setId(resultSet.getRow() + posicionPagina);
                produccionTB.setIdProduccion(resultSet.getString("IdProduccion"));
                produccionTB.setProyecto(resultSet.getString("Proyecto"));
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

            statementListar = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_Produccion_Count(?,?,?,?,?)}");
            statementListar.setInt(1, tipo);
            statementListar.setString(2, fechaInicio);
            statementListar.setString(3, fechaFinal);
            statementListar.setString(4, busqueda);
            statementListar.setInt(5, estado);
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
            statementProduccion = DBUtil.getConnection().prepareStatement("{CALL Sp_Obtener_Produccion_ById(?)}");
            statementProduccion.setString(1, idProduccion);
            resultSet = statementProduccion.executeQuery();
            if (resultSet.next()) {
                ProduccionTB produccionTB = new ProduccionTB();
                produccionTB.setFechaRegistro(resultSet.getDate("FechaRegistro").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                produccionTB.setFechaInicio(resultSet.getDate("FechaInico").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                produccionTB.setDias(resultSet.getInt("Dias"));
                produccionTB.setHoras(resultSet.getInt("Horas"));
                produccionTB.setMinutos(resultSet.getInt("Minutos"));
                produccionTB.setTipoOrden(resultSet.getBoolean("TipoOrden"));
                produccionTB.setDescripcion(resultSet.getString("Descripcion"));
                produccionTB.setIdProducto(resultSet.getString("IdProducto"));
                produccionTB.setCantidad(resultSet.getInt("Cantidad"));
                produccionTB.setCostoAdicional(resultSet.getDouble("CostoAdicioanal"));
                SuministroTB newSuministroTB = new SuministroTB(resultSet.getString("IdProducto"), resultSet.getString("Clave"), resultSet.getString("NombreMarca"));
                newSuministroTB.setUnidadCompraName(resultSet.getString("Medida"));
                produccionTB.setSuministroTB(newSuministroTB);
                produccionTB.setEmpleadoTB(new EmpleadoTB(resultSet.getString("IdEmpleado"), resultSet.getString("Apellidos"), resultSet.getString("Nombres")));
                produccionTB.setEstado(resultSet.getInt("Estado"));

                statementDetalleProduccion = DBUtil.getConnection().prepareStatement("{CALL Sp_Obtener_Detalle_Produccion_ById(?)}");
                statementDetalleProduccion.setString(1, idProduccion);
                resultSet = statementDetalleProduccion.executeQuery();
                ArrayList<SuministroTB> suministroTBs = new ArrayList();
                while (resultSet.next()) {
                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setId(resultSet.getRow());
                    suministroTB.setClave(resultSet.getString("Clave"));
                    suministroTB.setNombreMarca(resultSet.getString("NombreMarca"));
                    suministroTB.setUnidadCompraName(resultSet.getString("Medida"));
                    suministroTB.setCostoCompra(resultSet.getDouble("Costo"));
                    suministroTB.setCantidad(resultSet.getDouble("Cantidad"));
                    suministroTBs.add(suministroTB);
                }
                produccionTB.setSuministroInsumos(suministroTBs);
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

    public static Object Obtener_Produccion_Editor_ById(String idProduccion) {
        PreparedStatement statementProduccion = null;
        PreparedStatement statementDetalleProduccion = null;
        ResultSet resultSet = null;
        try {
            DBUtil.dbConnect();
            statementProduccion = DBUtil.getConnection().prepareStatement("{CALL Sp_Obtener_Produccion_ById(?)}");
            statementProduccion.setString(1, idProduccion);
            resultSet = statementProduccion.executeQuery();
            if (resultSet.next()) {
                ProduccionTB produccionTB = new ProduccionTB();
                produccionTB.setFechaRegistro(resultSet.getDate("FechaRegistro").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                produccionTB.setHoraRegistro(resultSet.getTime("HoraRegistro").toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss a")));
                produccionTB.setFechaInicio(resultSet.getDate("FechaInico").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                produccionTB.setDias(resultSet.getInt("Dias"));
                produccionTB.setHoras(resultSet.getInt("Horas"));
                produccionTB.setMinutos(resultSet.getInt("Minutos"));
                produccionTB.setTipoOrden(resultSet.getBoolean("TipoOrden"));
                produccionTB.setDescripcion(resultSet.getString("Descripcion"));
                produccionTB.setIdProducto(resultSet.getString("IdProducto"));
                produccionTB.setCantidad(resultSet.getInt("Cantidad"));
                SuministroTB newSuministroTB = new SuministroTB(resultSet.getString("IdProducto"), resultSet.getString("Clave"), resultSet.getString("NombreMarca"));
                newSuministroTB.setUnidadCompraName(resultSet.getString("Medida"));
                produccionTB.setSuministroTB(newSuministroTB);
                produccionTB.setEmpleadoTB(new EmpleadoTB(resultSet.getString("IdEmpleado"), resultSet.getString("Apellidos"), resultSet.getString("Nombres")));
                produccionTB.setEstado(resultSet.getInt("Estado"));

                statementDetalleProduccion = DBUtil.getConnection().prepareStatement("{CALL Sp_Obtener_Detalle_Produccion_ById(?)}");
                statementDetalleProduccion.setString(1, idProduccion);
                resultSet = statementDetalleProduccion.executeQuery();
                ArrayList<SuministroTB> suministroTBs = new ArrayList();
                while (resultSet.next()) {
                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setId(resultSet.getRow());
                    suministroTB.setIdSuministro(resultSet.getString("IdSuministro"));
                    suministroTB.setClave(resultSet.getString("Clave"));
                    suministroTB.setNombreMarca(resultSet.getString("NombreMarca"));
                    suministroTB.setUnidadCompraName(resultSet.getString("Medida"));
                    suministroTB.setCostoCompra(resultSet.getDouble("Costo"));
                    suministroTB.setCantidad(resultSet.getDouble("Cantidad"));

                    ComboBox<SuministroTB> comboBox = new ComboBox();
                    comboBox.setPromptText("-- Selecionar --");
                    comboBox.setPrefWidth(220);
                    comboBox.setPrefHeight(30);
                    comboBox.setMaxWidth(Double.MAX_VALUE);
                    suministroTB.setCbSuministro(comboBox);

                    SearchComboBox<SuministroTB> searchComboBox = new SearchComboBox<>(suministroTB.getCbSuministro(), false);
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
                            List<SuministroTB> list = SuministroADO.getSearchComboBoxSuministros(searchComboBox.getSearchComboBoxSkin().getSearchBox().getText().trim());
                            list.forEach(p -> suministroTB.getCbSuministro().getItems().add(p));
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
                    suministroTB.setSearchComboBox(searchComboBox);
                    suministroTB.getCbSuministro().getItems().add(new SuministroTB(suministroTB.getIdSuministro(), suministroTB.getClave(), suministroTB.getNombreMarca()));
                    suministroTB.getCbSuministro().getSelectionModel().select(0);

                    TextField textField = new TextField(Tools.roundingValue(suministroTB.getCantidad(), 2));
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
                    suministroTB.setTxtCantidad(textField);

                    Button button = new Button();
                    button.getStyleClass().add("buttonLightError");
                    button.setAlignment(Pos.CENTER);
                    button.setPrefWidth(Control.USE_COMPUTED_SIZE);
                    button.setPrefHeight(Control.USE_COMPUTED_SIZE);
                    ImageView imageView = new ImageView(new Image("/view/image/remove-gray.png"));
                    imageView.setFitWidth(20);
                    imageView.setFitHeight(20);
                    button.setGraphic(imageView);

                    suministroTB.setBtnRemove(button);
                    suministroTBs.add(suministroTB);
                }
                produccionTB.setSuministroInsumos(suministroTBs);
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
