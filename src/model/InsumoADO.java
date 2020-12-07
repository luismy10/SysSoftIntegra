package model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class InsumoADO {

    public static ObservableList<InsumoTB> ListarInsumos(String clave) {
        PreparedStatement statementLista = null;
        ObservableList<InsumoTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            statementLista = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_Insumo(?)}");
            statementLista.setString(1, clave);
            ResultSet resultSet = statementLista.executeQuery();
            while (resultSet.next()) {
                InsumoTB insumoTB = new InsumoTB();
                insumoTB.setId(resultSet.getRow());
                insumoTB.setIdInsumo(resultSet.getString("IdInsumo"));
                insumoTB.setClaveAlterna(resultSet.getString("ClaveAlterna"));
                insumoTB.setClave(resultSet.getString("Clave"));
                insumoTB.setNombreMarca(resultSet.getString("NombreMarca"));
                insumoTB.setMedidaName(resultSet.getString("Medida"));
                insumoTB.setCategoriaName(resultSet.getString("Categoria"));
                insumoTB.setCosto(resultSet.getDouble("Costo"));
                insumoTB.setCantidad(resultSet.getDouble("Cantidad"));
                empList.add(insumoTB);
            }
        } catch (SQLException ex) {
            System.out.println("Error en listar insumos: " + ex.getLocalizedMessage());
        } finally {
            try {
                if (statementLista != null) {
                    statementLista.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return empList;
    }

    public static InsumoTB GetInsumoById(String idInsumo) {
        PreparedStatement statementLista = null;
        InsumoTB insumoTB = null;
        try {
            DBUtil.dbConnect();
            statementLista = DBUtil.getConnection().prepareStatement("{CALL Sp_Obtener_Insumo_ById(?)}");
            statementLista.setString(1, idInsumo);
            ResultSet resultSet = statementLista.executeQuery();
            if (resultSet.next()) {
                insumoTB = new InsumoTB();
                insumoTB.setIdInsumo(resultSet.getString("IdInsumo"));
                insumoTB.setClave(resultSet.getString("Clave"));
                insumoTB.setClaveAlterna(resultSet.getString("ClaveAlterna"));
                insumoTB.setNombreMarca(resultSet.getString("NombreMarca"));
                insumoTB.setMedida(resultSet.getInt("Medida"));
                insumoTB.setMedidaName(resultSet.getString("MedidaName"));
                insumoTB.setCategoria(resultSet.getInt("Categoria"));
                insumoTB.setCategoriaName(resultSet.getString("CategoriaName"));
                insumoTB.setCosto(resultSet.getDouble("Costo"));
                insumoTB.setStockMinimo(resultSet.getDouble("StockMinimo"));
                insumoTB.setStockMaximo(resultSet.getDouble("StockMaximo"));
            }
        } catch (SQLException ex) {
            System.out.println("Error en obtener insumo: " + ex.getLocalizedMessage());
        } finally {
            try {
                if (statementLista != null) {
                    statementLista.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return insumoTB;
    }

    public static String CrudInsumo(InsumoTB insumoTB) {
        PreparedStatement statementValidar = null;
        PreparedStatement statementRegistrar = null;
        CallableStatement codigoInsumo = null;
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);
            statementValidar = DBUtil.getConnection().prepareStatement("SELECT * FROM InsumoTB WHERE IdInsumo = ?");
            statementValidar.setString(1, insumoTB.getIdInsumo());
            if (statementValidar.executeQuery().next()) {
                statementValidar = DBUtil.getConnection().prepareStatement("SELECT * FROM InsumoTB WHERE IdInsumo <> ?  and Clave = ?");
                statementValidar.setString(1, insumoTB.getIdInsumo());
                statementValidar.setString(2, insumoTB.getClave());
                if (statementValidar.executeQuery().next()) {
                    DBUtil.getConnection().rollback();
                    return "clave";
                } else {
                    statementValidar = DBUtil.getConnection().prepareStatement("SELECT * FROM InsumoTB WHERE IdInsumo<>? and NombreMarca = ?");
                    statementValidar.setString(1, insumoTB.getIdInsumo());
                    statementValidar.setString(2, insumoTB.getNombreMarca());
                    if (statementValidar.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        return "nombre";
                    } else {
                        statementRegistrar = DBUtil.getConnection().prepareStatement("UPDATE InsumoTB SET Clave = ?,ClaveAlterna = ?,NombreMarca = ?,Medida = ?,Categoria=?,Costo=?,StockMinimo=?,StockMaximo=? WHERE IdInsumo = ?");
                        statementRegistrar.setString(1, insumoTB.getClave());
                        statementRegistrar.setString(2, insumoTB.getClaveAlterna());
                        statementRegistrar.setString(3, insumoTB.getNombreMarca());
                        statementRegistrar.setInt(4, insumoTB.getMedida());
                        statementRegistrar.setInt(5, insumoTB.getCategoria());
                        statementRegistrar.setDouble(6, insumoTB.getCosto());
                        statementRegistrar.setDouble(7, insumoTB.getStockMinimo());
                        statementRegistrar.setDouble(8, insumoTB.getStockMaximo());
                        statementRegistrar.setString(9, insumoTB.getIdInsumo());
                        statementRegistrar.addBatch();

                        statementRegistrar.executeBatch();
                        DBUtil.getConnection().commit();
                        return "updated";
                    }
                }
            } else {
                statementValidar = DBUtil.getConnection().prepareStatement("SELECT * FROM InsumoTB WHERE Clave = ?");
                statementValidar.setString(1, insumoTB.getClave());
                if (statementValidar.executeQuery().next()) {
                    DBUtil.getConnection().rollback();
                    return "clave";
                } else {
                    statementValidar = DBUtil.getConnection().prepareStatement("SELECT * FROM InsumoTB WHERE NombreMarca = ?");
                    statementValidar.setString(1, insumoTB.getNombreMarca());
                    if (statementValidar.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        return "nombre";
                    } else {
                        codigoInsumo = DBUtil.getConnection().prepareCall("{? = call Fc_Insumo_Codigo_Alfanumerico()}");
                        codigoInsumo.registerOutParameter(1, java.sql.Types.VARCHAR);
                        codigoInsumo.execute();
                        String idInsumo = codigoInsumo.getString(1);

                        statementRegistrar = DBUtil.getConnection().prepareStatement("INSERT INTO InsumoTB(IdInsumo,Clave,ClaveAlterna,NombreMarca,Medida,Categoria,Cantidad,Costo,StockMinimo,StockMaximo) VALUES(?,?,?,?,?,?,0,?,?,?)");
                        statementRegistrar.setString(1, idInsumo);
                        statementRegistrar.setString(2, insumoTB.getClave());
                        statementRegistrar.setString(3, insumoTB.getClaveAlterna());
                        statementRegistrar.setString(4, insumoTB.getNombreMarca());
                        statementRegistrar.setInt(5, insumoTB.getMedida());
                        statementRegistrar.setInt(6, insumoTB.getCategoria());
                        statementRegistrar.setDouble(7, insumoTB.getCosto());
                        statementRegistrar.setDouble(8, insumoTB.getStockMinimo());
                        statementRegistrar.setDouble(9, insumoTB.getStockMaximo());
                        statementRegistrar.addBatch();

                        statementRegistrar.executeBatch();
                        DBUtil.getConnection().commit();
                        return "inserted";
                    }
                }

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
                if (statementRegistrar != null) {
                    statementRegistrar.close();
                }
                if (codigoInsumo != null) {
                    codigoInsumo.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
    }

    public static String EliminarInsumo(String idInsumo) {
        PreparedStatement statementRegistrar = null;
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            statementRegistrar = DBUtil.getConnection().prepareStatement("DELETE FROM InsumoTB WHERE IdInsumo = ?");
            statementRegistrar.setString(1, idInsumo);
            statementRegistrar.addBatch();

            statementRegistrar.executeBatch();
            DBUtil.getConnection().commit();
            return "deleted";
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
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
    }

}
