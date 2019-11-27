package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class UtilidadADO {

    public static ArrayList<Utilidad> listUtilidadVenta(boolean opcion, String fechaInicial, String fechaFinal, String busqueda) {
        String selectStmt = "{call Sp_Listar_Utilidad(?,?,?,?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ArrayList<Utilidad> list = new ArrayList<>();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setBoolean(1, opcion);
            preparedStatement.setString(2, fechaInicial);
            preparedStatement.setString(3, fechaFinal);
            preparedStatement.setString(4, busqueda);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                Utilidad utilidad = new Utilidad();
                utilidad.setId(rsEmps.getRow()); 
                utilidad.setIdSuministro(rsEmps.getString("IdSuministro"));
                utilidad.setClave(rsEmps.getString("Clave"));
                utilidad.setNombreMarca(rsEmps.getString("NombreMarca"));
                utilidad.setCantidad(rsEmps.getDouble("Cantidad"));
                
                utilidad.setCostoVenta(rsEmps.getDouble("Costo"));
                utilidad.setCostoVentaTotal(rsEmps.getDouble("CostoTotal")); 
                
                utilidad.setPrecioVenta(rsEmps.getDouble("Precio"));
                utilidad.setPrecioVentaTotal(rsEmps.getDouble("PrecioTotal"));
                
                utilidad.setUtilidad(rsEmps.getDouble("Utilidad")); 
                
                utilidad.setValorInventario(rsEmps.getBoolean("ValorInventario"));
                utilidad.setUnidadCompra(rsEmps.getString("UnidadCompra"));
                utilidad.setSimboloMoneda(rsEmps.getString("Simbolo"));

                list.add(utilidad);
            }            

        } catch (SQLException ex) {
            System.out.println(ex.getLocalizedMessage());
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (rsEmps != null) {
                    rsEmps.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException e) {
            }
        }
        return list;
    }
    
}
