package model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmpresaADO {

    public static String CrudEntity(EmpresaTB empresaTB) {
        String selectStmt = "{call Sp_Crud_MiEmpresa(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        CallableStatement callableStatement = null;
        try {
            DBUtil.dbConnect();
            callableStatement = DBUtil.getConnection().prepareCall(selectStmt);
            callableStatement.setInt("IdEmpresa", empresaTB.getIdEmpresa());
            callableStatement.setInt("GiroComercial", empresaTB.getGiroComerial());
            callableStatement.setString("Nombre", empresaTB.getNombre());
            callableStatement.setString("Telefono", empresaTB.getTelefono());
            callableStatement.setString("Celular", empresaTB.getCelular());
            callableStatement.setString("PaginaWeb", empresaTB.getPaginaWeb());
            callableStatement.setString("Email", empresaTB.getEmail());
            callableStatement.setString("Domicilio", empresaTB.getDomicilio());
            //facturación
            callableStatement.setInt("TipoDocumento", empresaTB.getTipoDocumento());
            callableStatement.setString("NumeroDocumento", empresaTB.getNumeroDocumento());
            callableStatement.setString("RazonSocial", empresaTB.getRazonSocial());
            callableStatement.setString("NombreComercial", empresaTB.getNombreComercial());
            callableStatement.setString("Pais", empresaTB.getPais());
            callableStatement.setInt("Ciudad", empresaTB.getCiudad());
            callableStatement.setInt("Provincia", empresaTB.getProvincia());
            callableStatement.setInt("Distrito", empresaTB.getDistrito());
            //
            callableStatement.registerOutParameter("Message", java.sql.Types.VARCHAR, 20);
            callableStatement.execute();
            return callableStatement.getString("Message");
        } catch (SQLException e) {
            return e.getLocalizedMessage();
        } finally {
            try {
                if (callableStatement != null) {
                    callableStatement.close();
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
            if(statementValidate.executeQuery().next()){
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
