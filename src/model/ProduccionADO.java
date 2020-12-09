package report;

import controller.tools.Tools;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.DBUtil;
import model.SuministroTB;

public class ProduccionADO {

    public static String RegistrarProduccion(ProduccionTB produccionTB) {
        PreparedStatement statementRegisrar = null;
        CallableStatement statementCodigo = null;
        CallableStatement statementNumeroOrden = null;
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            statementCodigo = DBUtil.getConnection().prepareCall("{? = call Fc_Producir_Codigo_Alfanumerico()}");
            statementCodigo.registerOutParameter(1, java.sql.Types.VARCHAR);
            statementCodigo.execute();
            String id_produccion = statementCodigo.getString(1);
            
            statementNumeroOrden = DBUtil.getConnection().prepareCall("{? = call Fc_Produccion_Nrm_Orden_Numerico()}");
            statementNumeroOrden.registerOutParameter(1, java.sql.Types.INTEGER);
            statementNumeroOrden.execute();
            int numero_produccion = (int) statementNumeroOrden.getObject(1);

            statementRegisrar = DBUtil.getConnection().prepareStatement("INSERT INTO ProduccionTB("
                    + "IdProduccion,"
                    + "FechaProduccion,"
                    + "HoraProduccion,"
                    + "FechaInicio,"
                    + "FechaTermino,"
                    + "IdSuministro,"
                    + "NumeroOrden,"
                    + "Estado,"
                    + "TipoOrden"
                    + ")VALUES(?,?,?,?,?,?,?,?,?)");
            statementRegisrar.setString(1, id_produccion);
            statementRegisrar.setString(2, produccionTB.getFechaProduccion());
            statementRegisrar.setString(3, produccionTB.getHoraProduccion());
            statementRegisrar.setString(4, produccionTB.getFechaInicio());
            statementRegisrar.setString(5, produccionTB.getFechaTermino());
            statementRegisrar.setString(6, produccionTB.getIdSuministro());
            statementRegisrar.setInt(7, numero_produccion);
            statementRegisrar.setShort(8, produccionTB.getEstado());
            statementRegisrar.setBoolean(9, produccionTB.isTipoOrden());
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
                produccionTB.setSuministroTB(new SuministroTB(resultSet.getString("Clave"), resultSet.getString("NombreMarca")));
                produccionTB.setFechaProduccion(resultSet.getDate("FechaProduccion").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM//yyyy")));
                produccionTB.setHoraProduccion(resultSet.getTime("HoraProduccion").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                produccionTB.setFechaInicio(resultSet.getDate("FechaInicio").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM//yyyy")));
                produccionTB.setFechaTermino(resultSet.getDate("FechaTermino").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM//yyyy")));
                produccionTB.setNumeroOrden(resultSet.getInt("NumeroOrden"));
                produccionTB.setEstado(resultSet.getShort("Estado"));
                produccionTB.setTipoOrden(resultSet.getBoolean("TipoOrden"));
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
