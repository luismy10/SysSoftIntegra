package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CuentasClienteADO {
    
    public static CuentasClienteTB Get_CuentasCliente_ById(String idVenta) {
        CuentasClienteTB cuentasClienteTB = null;
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementCuentas = null;
            try {
                statementCuentas = DBUtil.getConnection().prepareStatement("{call Sp_Get_CuentasCliente_By_Id(?)}");
                statementCuentas.setString(1, idVenta);
                try (ResultSet result = statementCuentas.executeQuery()) {
                    if (result.next()) {
                        cuentasClienteTB = new CuentasClienteTB();
                        cuentasClienteTB.setIdCuentasCliente(result.getInt("IdCuentaClientes"));
                        cuentasClienteTB.setIdCliente(result.getString("IdCliente"));
                        cuentasClienteTB.setPlazosName(result.getString("Nombre"));                        
                        cuentasClienteTB.setFechaVencimiento(result.getTimestamp("FechaVencimiento").toLocalDateTime());
                        cuentasClienteTB.setMontoInicial(result.getDouble("MontoInicial")); 
                    }
                }                
            } catch (SQLException ex) {
                
            } finally {
                try {
                    if (statementCuentas != null) {
                        statementCuentas.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    
                }
            }
        }
        return cuentasClienteTB;
    }
}
