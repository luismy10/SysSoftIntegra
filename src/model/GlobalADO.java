package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GlobalADO {

    public static ArrayList<Double> ReporteGlobal(String fechaInicial,String fechaFinal) {
        PreparedStatement preparedComprasContado = null;
        PreparedStatement preparedComprasCredito = null;
        ResultSet resultSet = null;
        ArrayList<Double> list = new ArrayList<>();
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            try {
                preparedComprasContado = DBUtil.getConnection().prepareStatement("select sum(Total) as 'totalcontado' from CompraTB where TipoCompra = 1 and FechaCompra = ?");
                preparedComprasContado.setString(1, fechaInicial);
                resultSet = preparedComprasContado.executeQuery();
                if(resultSet.next()){
                    list.add(resultSet.getDouble("totalcontado"));
                }
                preparedComprasCredito = DBUtil.getConnection().prepareStatement("select sum(Total) as 'totalcredito' from CompraTB where TipoCompra = 2 and FechaCompra = ?");
                preparedComprasCredito.setString(1, fechaInicial);
                resultSet = preparedComprasCredito.executeQuery();
                if(resultSet.next()){
                    list.add(resultSet.getDouble("totalcredito") );
                }
            } catch (SQLException ex) {
                System.out.println(ex.getLocalizedMessage());
            } finally {
                try{
                    if(preparedComprasContado != null){
                        preparedComprasContado.close();
                    }
                    if(preparedComprasCredito != null){
                        preparedComprasCredito.close();
                    }
                    if(resultSet != null){
                        resultSet.close();
                    }
                    DBUtil.dbDisconnect();
                }catch(SQLException ex){
                    
                }
            }
        }

        return list;
    }

}
