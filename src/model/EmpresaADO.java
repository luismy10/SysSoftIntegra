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
                        + "RazonSocial=?,NombreComercial=?,Image=?,Ubigeo=?\n"
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
                statementEmpresa.setBytes(12, empresaTB.getImage());
                statementEmpresa.setInt(13, empresaTB.getIdUbigeo());
                statementEmpresa.setInt(14, empresaTB.getIdEmpresa());
                statementEmpresa.addBatch();

                statementEmpresa.executeBatch();
                DBUtil.getConnection().commit();
                return "updated";
            } else {
                statementEmpresa = DBUtil.getConnection().prepareStatement("INSERT INTO EmpresaTB"
                        + "(GiroComercial,\n"
                        + "Nombre,\n"
                        + "Telefono,\n"
                        + "Celular,\n"
                        + "PaginaWeb,\n"
                        + "Email,\n"
                        + "Domicilio,\n"
                        + "TipoDocumento,\n"
                        + "NumeroDocumento,\n"
                        + "RazonSocial,\n"
                        + "NombreComercial,\n"
                        + "Image,\n"
                        + "Ubigeo)\n"
                        + "values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
                statementEmpresa.setBytes(12, empresaTB.getImage());
                statementEmpresa.setInt(13, empresaTB.getIdUbigeo());
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
            System.out.println("La operaci??n de selecci??n de SQL ha fallado: " + e);
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
            preparedStatement = DBUtil.getConnection().prepareStatement("{CALL Sp_Obtener_Empresa()}");
            rsEmps = preparedStatement.executeQuery();
            if (rsEmps.next()) {
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
                empresaTB.setImage(rsEmps.getBytes("Image"));
                empresaTB.setUbigeoTB(new UbigeoTB(rsEmps.getInt("IdUbigeo"), rsEmps.getString("CodigoUbigeo"), rsEmps.getString("Departamento"), rsEmps.getString("Provincia"), rsEmps.getString("Distrito")));
            }
        } catch (SQLException e) {
            System.out.println("La operaci??n de selecci??n de SQL ha fallado: " + e);
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
