package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmpresaADO {

    public static String CrudEntity(EmpresaTB empresaTB) {
        PreparedStatement statementValidate = null;
        PreparedStatement statementEmpresa = null;
        DBUtil.dbConnect();
        if (DBUtil.getConnection() == null) {
            return "No se pudo completar su proceso por problemas de red, intente nuevamente.";
        }
        try {
            DBUtil.getConnection().setAutoCommit(false);
            statementValidate = DBUtil.getConnection().prepareStatement("SELECT * FROM EmpresaTB WHERE IdEmpresa = ?");
            statementValidate.setInt(1, empresaTB.getIdEmpresa());
            if (statementValidate.executeQuery().next()) {
                statementEmpresa = DBUtil.getConnection().prepareStatement("UPDATE EmpresaTB\n"
                        + "SET GiroComercial=?,Nombre = ?,Telefono=?,Celular=?,PaginaWeb=?,Email=?,Domicilio=?,\n"
                        + "TipoDocumento=?,NumeroDocumento=?,\n"
                        + "RazonSocial=?,NombreComercial=?,Pais=?,Ciudad=?,Provincia=?,Distrito=?,Image=?\n"
                        + "WHERE IdEmpresa=?");
                statementEmpresa.setInt(1, empresaTB.getGiroComerial());
                statementEmpresa.setString(2, empresaTB.getNombre());
                statementEmpresa.setString(3, empresaTB.getTelefono());
                statementEmpresa.setString(4, empresaTB.getCelular());
                statementEmpresa.setString(5, empresaTB.getPaginaWeb());
                statementEmpresa.setString(6, empresaTB.getEmail());
                statementEmpresa.setString(7, empresaTB.getDomicilio());
                statementEmpresa.setInt(8, empresaTB.getTipoDocumento());
                statementEmpresa.setString(9, empresaTB.getNumeroDocumento());
                statementEmpresa.setString(10, empresaTB.getRazonSocial());
                statementEmpresa.setString(11, empresaTB.getNombreComercial());
                statementEmpresa.setString(12, empresaTB.getPais());
                statementEmpresa.setInt(13, empresaTB.getCiudad());
                statementEmpresa.setInt(14, empresaTB.getProvincia());
                statementEmpresa.setInt(15, empresaTB.getDistrito());
                statementEmpresa.setBytes(16, empresaTB.getImage());
                statementEmpresa.setInt(17, empresaTB.getIdEmpresa());
                statementEmpresa.addBatch();
                

                statementEmpresa.executeBatch();
                DBUtil.getConnection().commit();
                return "updated";
            } else {
                statementEmpresa = DBUtil.getConnection().prepareStatement("INSERT INTO EmpresaTB"
                        + "(GiroComercial,Nombre,Telefono,Celular,PaginaWeb,Email,Domicilio,\n"
                        + "TipoDocumento,NumeroDocumento,RazonSocial,NombreComercial,Pais,Ciudad,Provincia,Distrito)\n"
                        + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                statementEmpresa.setInt(1, empresaTB.getGiroComerial());
                statementEmpresa.setString(2, empresaTB.getNombre());
                statementEmpresa.setString(3, empresaTB.getTelefono());
                statementEmpresa.setString(4, empresaTB.getCelular());
                statementEmpresa.setString(5, empresaTB.getPaginaWeb());
                statementEmpresa.setString(6, empresaTB.getEmail());
                statementEmpresa.setString(7, empresaTB.getDomicilio());
                statementEmpresa.setInt(8, empresaTB.getTipoDocumento());
                statementEmpresa.setString(9, empresaTB.getNumeroDocumento());
                statementEmpresa.setString(10, empresaTB.getRazonSocial());
                statementEmpresa.setString(11, empresaTB.getNombreComercial());
                statementEmpresa.setString(12, empresaTB.getPais());
                statementEmpresa.setInt(13, empresaTB.getCiudad());
                statementEmpresa.setInt(14, empresaTB.getProvincia());
                statementEmpresa.setInt(15, empresaTB.getDistrito());
                statementEmpresa.addBatch();

                statementEmpresa.executeBatch();
                DBUtil.getConnection().commit();
                return "registered";
            }
        } catch (SQLException e) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException ex) {

            }
            return e.getLocalizedMessage();
        } finally {
            try {
                if (statementValidate != null) {
                    statementValidate.close();
                }
                if (statementEmpresa != null) {
                    statementEmpresa.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }

    public static boolean isConfiguration() {
        PreparedStatement statementValidate = null;
        try {
            DBUtil.dbConnect();
            statementValidate = DBUtil.getConnection().prepareStatement("select * from EmpresaTB");
            if (statementValidate.executeQuery().next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("La operación de selección de SQL ha fallado: " + e);
        } finally {
            try {
                if (statementValidate != null) {
                    statementValidate.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException e) {

            }
        }
        return false;
    }

    public static EmpresaTB GetEmpresa() {
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        EmpresaTB empresaTB = null;
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement("select * from EmpresaTB");
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                empresaTB = new EmpresaTB();
                empresaTB.setIdEmpresa(rsEmps.getInt("IdEmpresa"));
                empresaTB.setGiroComerial(rsEmps.getInt("GiroComercial"));
                empresaTB.setNombre(rsEmps.getString("Nombre"));
                empresaTB.setTelefono(rsEmps.getString("Telefono"));
                empresaTB.setCelular(rsEmps.getString("Celular"));
                empresaTB.setPaginaWeb(rsEmps.getString("PaginaWeb"));
                empresaTB.setEmail(rsEmps.getString("Email"));
                empresaTB.setDomicilio(rsEmps.getString("Domicilio"));

                empresaTB.setTipoDocumento(rsEmps.getInt("TipoDocumento"));
                empresaTB.setNumeroDocumento(rsEmps.getString("NumeroDocumento"));
                empresaTB.setRazonSocial(rsEmps.getString("RazonSocial"));
                empresaTB.setNombreComercial(rsEmps.getString("NombreComercial"));
                empresaTB.setPais(rsEmps.getString("Pais"));
                empresaTB.setCiudad(rsEmps.getInt("Ciudad"));
                empresaTB.setProvincia(rsEmps.getInt("Provincia"));
                empresaTB.setDistrito(rsEmps.getInt("Distrito"));
                empresaTB.setImage(rsEmps.getBytes("Image"));
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
        return empresaTB;
    }

}
