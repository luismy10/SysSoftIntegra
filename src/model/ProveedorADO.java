package model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProveedorADO {

    public static String CrudEntity(ProveedorTB proveedorTB) {
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement preparedValidation = null;
            CallableStatement codigoProveedor = null;
            PreparedStatement preparedProveedor = null;
            
            try {
                DBUtil.getConnection().setAutoCommit(false);

                preparedValidation = DBUtil.getConnection().prepareStatement("select IdProveedor from ProveedorTB where IdProveedor = ?");
                preparedValidation.setString(1, proveedorTB.getIdProveedor().get());
                if (preparedValidation.executeQuery().next()) {
                    preparedValidation = DBUtil.getConnection().prepareStatement("select NumeroDocumento from ProveedorTB where IdProveedor <> ? and NumeroDocumento = ?");
                    preparedValidation.setString(1, proveedorTB.getIdProveedor().get());
                    preparedValidation.setString(2, proveedorTB.getNumeroDocumento().get());
                    if (preparedValidation.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        return "duplicate";
                    } else {
                        preparedProveedor = DBUtil.getConnection().prepareCall("UPDATE ProveedorTB SET TipoDocumento=?,NumeroDocumento=?,RazonSocial=UPPER(?),NombreComercial=UPPER(?),Pais=?,Ciudad=?,Provincia=?,Distrito=?,Ambito=?,Estado=?,Telefono=?,Celular=?,Email=?,PaginaWeb=?,Direccion=?, Representante=? WHERE IdProveedor=?");

                        preparedProveedor.setInt(1, proveedorTB.getTipoDocumento());
                        preparedProveedor.setString(2, proveedorTB.getNumeroDocumento().get());
                        preparedProveedor.setString(3, proveedorTB.getRazonSocial().get());
                        preparedProveedor.setString(4, proveedorTB.getNombreComercial().get());
                        preparedProveedor.setString(5, proveedorTB.getPais());
                        preparedProveedor.setInt(6, proveedorTB.getCiudad());
                        preparedProveedor.setInt(7, proveedorTB.getProvincia());
                        preparedProveedor.setInt(8, proveedorTB.getDistrito());
                        preparedProveedor.setInt(9, proveedorTB.getAmbito());
                        preparedProveedor.setInt(10, proveedorTB.getEstado());
                        preparedProveedor.setString(11, proveedorTB.getTelefono());
                        preparedProveedor.setString(12, proveedorTB.getCelular());
                        preparedProveedor.setString(13, proveedorTB.getEmail());
                        preparedProveedor.setString(14, proveedorTB.getPaginaWeb());
                        preparedProveedor.setString(15, proveedorTB.getDireccion());
                        preparedProveedor.setString(16, proveedorTB.getRepresentante());
                        preparedProveedor.setString(17, proveedorTB.getIdProveedor().get());
                        preparedProveedor.addBatch();

                        preparedProveedor.executeBatch();
                        DBUtil.getConnection().commit();
                        return "updated";
                    }
                } else {
                    preparedValidation = DBUtil.getConnection().prepareStatement("select NumeroDocumento from ProveedorTB where NumeroDocumento = ?");
                    preparedValidation.setString(1, proveedorTB.getNumeroDocumento().get());
                    if (preparedValidation.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        return "duplicate";
                    } else {
                        codigoProveedor = DBUtil.getConnection().prepareCall("{? = call Fc_Proveedor_Codigo_Alfanumerico()}");
                        codigoProveedor.registerOutParameter(1, java.sql.Types.VARCHAR);
                        codigoProveedor.execute();
                        String idProveedor = codigoProveedor.getString(1);

                        preparedProveedor = DBUtil.getConnection().prepareCall("INSERT INTO ProveedorTB(IdProveedor,TipoDocumento,NumeroDocumento,RazonSocial,NombreComercial,Pais,Ciudad,Provincia,Distrito,Ambito,Estado,Telefono,Celular,Email,PaginaWeb,Direccion,UsuarioRegistro,FechaRegistro,representante)"
                                + "values(?,?,?,UPPER(?),UPPER(?),?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                        preparedProveedor.setString(1, idProveedor);
                        preparedProveedor.setInt(2, proveedorTB.getTipoDocumento());
                        preparedProveedor.setString(3, proveedorTB.getNumeroDocumento().get());
                        preparedProveedor.setString(4, proveedorTB.getRazonSocial().get());
                        preparedProveedor.setString(5, proveedorTB.getNombreComercial().get());
                        preparedProveedor.setString(6, proveedorTB.getPais());
                        preparedProveedor.setInt(7, proveedorTB.getCiudad());
                        preparedProveedor.setInt(8, proveedorTB.getProvincia());
                        preparedProveedor.setInt(9, proveedorTB.getDistrito());
                        preparedProveedor.setInt(10, proveedorTB.getAmbito());
                        preparedProveedor.setInt(11, proveedorTB.getEstado());
                        preparedProveedor.setString(12, proveedorTB.getTelefono());
                        preparedProveedor.setString(13, proveedorTB.getCelular());
                        preparedProveedor.setString(14, proveedorTB.getEmail());
                        preparedProveedor.setString(15, proveedorTB.getPaginaWeb());
                        preparedProveedor.setString(16, proveedorTB.getDireccion());
                        preparedProveedor.setString(17, proveedorTB.getUsuarioRegistro());
                        preparedProveedor.setTimestamp(18, Timestamp.valueOf(proveedorTB.getFechaRegistro()));
                        preparedProveedor.setString(19, proveedorTB.getRepresentante());
                        preparedProveedor.addBatch();
                        
                        preparedProveedor.executeBatch();                       
                        DBUtil.getConnection().commit();
                        return "registered";
                    }
                }

            } catch (SQLException ex) {
                try {
                    DBUtil.getConnection().rollback();
                } catch (SQLException exr) {
                    return exr.getLocalizedMessage();
                }
                return ex.getLocalizedMessage();
            } finally {
                try {
                    if (preparedProveedor != null) {
                        preparedProveedor.close();
                    }
                    if (preparedValidation != null) {
                        preparedValidation.close();
                    }
                    if (codigoProveedor != null) {
                        codigoProveedor.close();
                    }                   
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    return ex.getLocalizedMessage();
                }
            }
        } else {
            return "No hay conexión al servidor, intente nuevamente o revise su conexión.";
        }
    }

    public static String RemoveProveedor(String idProveedor) {
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement preparedValidation = null;
            PreparedStatement preparedProveedor = null;
            try {
                DBUtil.getConnection().setAutoCommit(false);

                preparedValidation = DBUtil.getConnection().prepareStatement("SELECT * FROM CompraTB WHERE Proveedor = ?");
                preparedValidation.setString(1, idProveedor);
                if (preparedValidation.executeQuery().next()) {
                    DBUtil.getConnection().rollback();
                    return "exists";
                } else {
                    preparedProveedor = DBUtil.getConnection().prepareStatement("DELETE FROM ProveedorTB WHERE IdProveedor = ?");
                    preparedProveedor.setString(1, idProveedor);
                    preparedProveedor.addBatch();
                    preparedProveedor.executeBatch();
                    DBUtil.getConnection().commit();
                    return "removed";
                }
            } catch (SQLException ex) {
                try {
                    DBUtil.getConnection().rollback();
                } catch (SQLException exr) {
                    return exr.getLocalizedMessage();
                }
                return ex.getLocalizedMessage();
            } finally {
                try {
                    if (preparedValidation != null) {
                        preparedValidation.close();
                    }
                    if (preparedProveedor != null) {
                        preparedProveedor.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    return ex.getLocalizedMessage();
                }
            }
        } else {
            return "No hay conexión al servidor, intente nuevamente o revise su conexión.";
        }
    }

    public static ObservableList<ProveedorTB> ListProveedor(String... value) {
        String selectStmt = "{call Sp_Listar_Proveedor(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<ProveedorTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, value[0]);
            rsEmps = preparedStatement.executeQuery();

            while (rsEmps.next()) {
                ProveedorTB proveedorTB = new ProveedorTB();
                proveedorTB.setId(rsEmps.getRow());
                proveedorTB.setIdProveedor(rsEmps.getString("IdProveedor"));
                proveedorTB.setTipoDocumentoName(rsEmps.getString("Documento"));
                proveedorTB.setNumeroDocumento(rsEmps.getString("NumeroDocumento"));
                proveedorTB.setRazonSocial(rsEmps.getString("RazonSocial"));
                proveedorTB.setNombreComercial(rsEmps.getString("NombreComercial"));
                proveedorTB.setEstadoName(rsEmps.getString("Estado"));
                proveedorTB.setTelefono(rsEmps.getString("Telefono"));
                proveedorTB.setCelular(rsEmps.getString("Celular"));
                proveedorTB.setFechaRegistro(rsEmps.getTimestamp("FRegistro").toLocalDateTime());
                proveedorTB.setRepresentante(rsEmps.getString("Representante"));
                empList.add(proveedorTB);
            }
        } catch (SQLException e) {
            System.out.println("La operación de selección de SQL ha fallado: " + e);

        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (rsEmps != null) {
                    rsEmps.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return empList;
    }

    public static ProveedorTB GetIdLisProveedor(String documento) {
        String selectStmt = "{call Sp_Get_Proveedor_By_Id(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ProveedorTB proveedorTB = null;
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, documento);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                proveedorTB = new ProveedorTB();
                proveedorTB.setIdProveedor(rsEmps.getString("IdProveedor"));
                proveedorTB.setTipoDocumento(rsEmps.getInt("TipoDocumento"));
                proveedorTB.setRazonSocial(rsEmps.getString("RazonSocial"));
                proveedorTB.setNombreComercial(rsEmps.getString("NombreComercial"));
                proveedorTB.setPais(rsEmps.getString("Pais"));
                proveedorTB.setCiudad(rsEmps.getInt("Ciudad"));
                proveedorTB.setProvincia(rsEmps.getInt("Provincia"));
                proveedorTB.setDistrito(rsEmps.getInt("Distrito"));
                proveedorTB.setAmbito(rsEmps.getInt("Ambito"));
                proveedorTB.setEstado(rsEmps.getInt("Estado"));
                proveedorTB.setTelefono(rsEmps.getString("Telefono"));
                proveedorTB.setCelular(rsEmps.getString("Celular"));
                proveedorTB.setEmail(rsEmps.getString("Email"));
                proveedorTB.setPaginaWeb(rsEmps.getString("PaginaWeb"));
                proveedorTB.setDireccion(rsEmps.getString("Direccion"));
                proveedorTB.setRepresentante(rsEmps.getString("Representante"));
            }
        } catch (SQLException e) {
            System.out.println("La operación de selección de SQL ha fallado: " + e);
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (rsEmps != null) {
                    rsEmps.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return proveedorTB;
    }

    public static String GetProveedorId(String value) {
        String selectStmt = "SELECT IdProveedor FROM ProveedorTB WHERE NumeroDocumento = ?";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        String IdProveedor = "";
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, value);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                IdProveedor = rsEmps.getString("IdProveedor");
            }
        } catch (SQLException e) {
            System.out.println("La operación de selección de SQL ha fallado: " + e);

        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (rsEmps != null) {
                    rsEmps.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return IdProveedor;
    }

}
