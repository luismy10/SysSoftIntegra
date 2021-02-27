package model;

import controller.tools.Tools;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
public class ProduccionADO {

    public static String RegistrarProduccion(ProduccionTB produccionTB) {
        PreparedStatement statementRegisrar = null;
        CallableStatement statementCodigo = null;
        CallableStatement statementNumeroOrden = null;
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            statementCodigo = DBUtil.getConnection().prepareCall("{? = call Fc_Produccion_Codigo_Alfanumerico()}");
            statementCodigo.registerOutParameter(1, java.sql.Types.VARCHAR);
            statementCodigo.execute();
            String id_produccion = statementCodigo.getString(1);

            statementRegisrar = DBUtil.getConnection().prepareStatement("INSERT INTO ProduccionTB("
                    + "IdProduccion,"      
                    + "IdProducto,"
                    + "TipoOrden,"
                    + "IdEncargado,"
                    + "Descripcion"
                    + "FechaRegistro"
                    + "HoraRegistro"
                    + ")VALUES(?,?,?,?,?,?,?)");
            statementRegisrar.setString(1, id_produccion);
            statementRegisrar.setString(2, produccionTB.getIdProducto());
            statementRegisrar.setBoolean(3, produccionTB.isTipoOrden());
            statementRegisrar.setString(4, produccionTB.getIdEncargado());
            statementRegisrar.setString(5, produccionTB.getDescripcion());
            statementRegisrar.setString(6, produccionTB.getFechaRegistro());
            statementRegisrar.setString(7, produccionTB.getHoraRegistro());
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

    public static ObservableList<ProduccionTB> ListarProduccion() {
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
        } catch (SQLException ex) {
            Tools.println("Error en listar: " + ex.getLocalizedMessage());
        } finally {
            try {
                if (statementListar != null) {
                    statementListar.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return produccionTBs;
    }

}
