package model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class InsumoADO {

    public static ArrayList<Object> ListarInsumosListaView(short tipo, String busqueda, int posicionPagina, int filasPorPagina) {
        PreparedStatement statementLista = null;
        ObservableList<InsumoTB> empList = FXCollections.observableArrayList();
        ResultSet rsEmps = null;
        ArrayList<Object> objects = new ArrayList<>();
        try {
            DBUtil.dbConnect();
            statementLista = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_Insumo(?,?,?,?,?,?,?,?)}");
            statementLista.setShort(1, tipo);
            statementLista.setString(2, busqueda);
            statementLista.setInt(3, 0);
            statementLista.setString(4, "");
            statementLista.setString(5, "");
            statementLista.setString(6, "");
            statementLista.setInt(7, posicionPagina);
            statementLista.setInt(8, filasPorPagina);
            rsEmps = statementLista.executeQuery();

            while (rsEmps.next()) {
                InsumoTB insumoTB = new InsumoTB();
                insumoTB.setId(rsEmps.getRow());
                insumoTB.setIdInsumo(rsEmps.getString("IdInsumo"));
                insumoTB.setClaveAlterna(rsEmps.getString("ClaveAlterna"));
                insumoTB.setClave(rsEmps.getString("Clave"));
                insumoTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                insumoTB.setMedidaName(rsEmps.getString("Medida"));
                insumoTB.setCategoriaName(rsEmps.getString("Categoria"));
                insumoTB.setCosto(rsEmps.getDouble("Costo"));
                insumoTB.setCantidad(rsEmps.getDouble("Cantidad"));
                empList.add(insumoTB);
            }

            objects.add(empList);

            statementLista = DBUtil.getConnection().prepareStatement("{call Sp_Listar_Insumos_Count(?,?,?,?,?,?)}");
            statementLista.setShort(1, tipo);
            statementLista.setString(2, busqueda);
            statementLista.setInt(3, 0);
            statementLista.setString(4, "");
            statementLista.setString(5, "");
            statementLista.setString(6, "");
            rsEmps = statementLista.executeQuery();
            Integer integer = 0;
            if (rsEmps.next()) {
                integer = rsEmps.getInt("Total");
            }
            objects.add(integer);
        } catch (SQLException ex) {
            System.out.println("Error en listar insumos: " + ex.getLocalizedMessage());
        } finally {
            try {
                if (statementLista != null) {
                    statementLista.close();
                }
                if (rsEmps != null) {
                    rsEmps.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return objects;
    }
    
    public static Object ListarInsumos(short opcion, String clave, int categoria, String clase, String subClase, String subSubClase, int posicionPagina, int filasPorPagina) {

        PreparedStatement preparedInsumos = null;
        PreparedStatement preparedTotales = null;
        ResultSet rsEmps = null;

        try {
            DBUtil.dbConnect();
            Object[] object = new Object[2];

            preparedInsumos = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_Insumo(?,?,?,?,?,?,?,?)}");
            preparedInsumos.setShort(1, opcion);
            preparedInsumos.setString(2, clave);
            preparedInsumos.setInt(3, categoria);
            preparedInsumos.setString(4, clase);
            preparedInsumos.setString(5, subClase);
            preparedInsumos.setString(6, subSubClase);
            preparedInsumos.setInt(7, posicionPagina);
            preparedInsumos.setInt(8, filasPorPagina);
            rsEmps = preparedInsumos.executeQuery();
            ObservableList<InsumoTB> empList = FXCollections.observableArrayList();
            while (rsEmps.next()) {
                InsumoTB insumosTB = new InsumoTB();
                insumosTB.setId(rsEmps.getRow() + posicionPagina);
                insumosTB.setIdInsumo(rsEmps.getString("IdInsumo"));
                insumosTB.setClave(rsEmps.getString("Clave"));
                insumosTB.setClaveAlterna(rsEmps.getString("ClaveAlterna"));
                insumosTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                insumosTB.setMedidaName(rsEmps.getString("Medida"));
                insumosTB.setCategoriaName(rsEmps.getString("NombreCategoria"));
                insumosTB.setNombreClase(rsEmps.getString("Clase"));
                insumosTB.setNombreSubClase(rsEmps.getString("SubClase"));
                insumosTB.setNombreSubSubClase(rsEmps.getString("SubSubClase"));
                insumosTB.setCantidad(rsEmps.getDouble("Cantidad"));
                insumosTB.setCosto(rsEmps.getDouble("Costo"));
                insumosTB.setStockMinimo(rsEmps.getDouble("StockMinimo"));
                insumosTB.setStockMaximo(rsEmps.getDouble("StockMaximo"));
                empList.add(insumosTB);
            }
            object[0] = empList;

            preparedTotales = DBUtil.getConnection().prepareStatement("{call Sp_Listar_Insumos_Count(?,?,?,?,?,?)}");
            preparedTotales.setShort(1, opcion);
            preparedTotales.setString(2, clave);
            preparedTotales.setInt(3, categoria);
            preparedTotales.setString(4, clase);
            preparedTotales.setString(5, subClase);
            preparedTotales.setString(6, subSubClase);
            rsEmps = preparedTotales.executeQuery();
            Integer integer = 0;
            if (rsEmps.next()) {
                integer = rsEmps.getInt("Total");
            }

            object[1] = integer;

            return object;
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (preparedInsumos != null) {
                    preparedInsumos.close();
                }
                if (preparedTotales != null) {
                    preparedTotales.close();
                }
                if (rsEmps != null) {
                    rsEmps.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }

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
                insumoTB.setIdClase(resultSet.getString("IdClase"));
                insumoTB.setNombreClase(resultSet.getString("Clase"));
                insumoTB.setIdSubClase(resultSet.getString("IdSubClase"));
                insumoTB.setNombreSubClase(resultSet.getString("SubClase"));
                insumoTB.setIdSubSubClase(resultSet.getString("IdSubSubClase"));
                insumoTB.setNombreSubSubClase(resultSet.getString("SubSubClase"));
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
                        statementRegistrar = DBUtil.getConnection().prepareStatement("UPDATE InsumoTB SET Clave = ?,ClaveAlterna = ?,NombreMarca = ?,Medida = ?,Categoria=?,Clase=?,SubClase=?,SubSubClase=?,Costo=?,StockMinimo=?,StockMaximo=? WHERE IdInsumo = ?");
                        statementRegistrar.setString(1, insumoTB.getClave());
                        statementRegistrar.setString(2, insumoTB.getClaveAlterna());
                        statementRegistrar.setString(3, insumoTB.getNombreMarca());
                        statementRegistrar.setInt(4, insumoTB.getMedida());
                        statementRegistrar.setInt(5, insumoTB.getCategoria());
                        statementRegistrar.setString(6, insumoTB.getIdClase());
                        statementRegistrar.setString(7, insumoTB.getIdSubClase());
                        statementRegistrar.setString(8, insumoTB.getIdSubSubClase());
                        statementRegistrar.setDouble(9, insumoTB.getCosto());
                        statementRegistrar.setDouble(10, insumoTB.getStockMinimo());
                        statementRegistrar.setDouble(11, insumoTB.getStockMaximo());
                        statementRegistrar.setString(12, insumoTB.getIdInsumo());
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

                        statementRegistrar = DBUtil.getConnection().prepareStatement("INSERT INTO InsumoTB(IdInsumo,Clave,ClaveAlterna,NombreMarca,Medida,Categoria,Clase,SubClase,SubSubClase,Costo,StockMinimo,StockMaximo) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
                        statementRegistrar.setString(1, idInsumo);
                        statementRegistrar.setString(2, insumoTB.getClave());
                        statementRegistrar.setString(3, insumoTB.getClaveAlterna());
                        statementRegistrar.setString(4, insumoTB.getNombreMarca());
                        statementRegistrar.setInt(5, insumoTB.getMedida());
                        statementRegistrar.setInt(6, insumoTB.getCategoria());
                        statementRegistrar.setString(7, insumoTB.getIdClase());
                        statementRegistrar.setString(8, insumoTB.getIdSubClase());
                        statementRegistrar.setString(9, insumoTB.getIdSubSubClase());
                        statementRegistrar.setDouble(10, insumoTB.getCosto());
                        statementRegistrar.setDouble(11, insumoTB.getStockMinimo());
                        statementRegistrar.setDouble(12, insumoTB.getStockMaximo());
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

    public static List<InsumoTB> getSearchComboBoxInsumos(String buscar) {
        String selectStmt = "SELECT IdInsumo,Clave,NombreMarca FROM InsumoTB WHERE Clave LIKE ? OR NombreMarca LIKE ?";
        PreparedStatement preparedStatement = null;
        List<InsumoTB> insumoTBs = new ArrayList<>();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, buscar + "%");
            preparedStatement.setString(2, buscar + "%");
            try (ResultSet rsEmps = preparedStatement.executeQuery()) {
                while (rsEmps.next()) {
                    InsumoTB insumoTB = new InsumoTB();
                    insumoTB.setIdInsumo(rsEmps.getString("IdInsumo"));
                    insumoTB.setClave(rsEmps.getString("Clave"));
                    insumoTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                    insumoTBs.add(insumoTB);
                }
            }
        } catch (SQLException e) {

        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {

            }
        }
        return insumoTBs;
    }

}
