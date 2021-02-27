package model;

import controller.tools.Tools;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProduccionADO {

    public static String Registrar_Produccion(ProduccionTB produccionTB) {
        PreparedStatement statementRegisrar = null;
        CallableStatement statementCodigo = null;
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
                    + "Cantidad"
                    + ")VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
            statementRegisrar.addBatch();

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
            } catch (SQLException e) {

            }
        }
    }

    public static Object ListarProduccion(int tipo, String fechaInicio, String fechaFinal, String busqueda, int posicionPagina, int filasPorPagina) {
        PreparedStatement statementListar = null;
        ObservableList<ProduccionTB> produccionTBs = FXCollections.observableArrayList();
        try {
            statementListar = DBUtil.getConnection().prepareStatement("{CALL ListarProduccion()}");
            ResultSet resultSet = statementListar.executeQuery();
            while (resultSet.next()) {
                ProduccionTB produccionTB = new ProduccionTB();
                produccionTB.setId(resultSet.getRow());
                produccionTB.setIdProduccion(resultSet.getString("IdProduccion"));
//                produccionTB.setSuministroTB(new SuministroTB(resultSet.getString("Clave"), resultSet.getString("NombreMarca")));
//                produccionTB.setFechaProduccion(resultSet.getDate("FechaProduccion").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM//yyyy")));
//                produccionTB.setHoraProduccion(resultSet.getTime("HoraProduccion").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
//                produccionTB.setFechaInicio(resultSet.getDate("FechaInicio").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM//yyyy")));
//                produccionTB.setFechaTermino(resultSet.getDate("FechaTermino").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM//yyyy")));
//                produccionTB.setNumeroOrden(resultSet.getInt("NumeroOrden"));
//                produccionTB.setEstado(resultSet.getShort("Estado"));
//                produccionTB.setTipoOrden(resultSet.getBoolean("TipoOrden"));
                produccionTBs.add(produccionTB);
            }
            return null;
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

}
