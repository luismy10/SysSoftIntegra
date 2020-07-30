package model;

import controller.tools.Tools;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;

public class KardexADO {

    public static ObservableList<KardexTB> List_Kardex_By_Id_Suministro(short opcion,String value, String fechaInicial, String fechaFinal) {
        String selectStmt = "{call Sp_Listar_Kardex_Suministro_By_Id(?,?,?,?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<KardexTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setShort(1, opcion);
            preparedStatement.setString(2, value);
            preparedStatement.setString(3, fechaInicial);
            preparedStatement.setString(4, fechaFinal);
            rsEmps = preparedStatement.executeQuery();

            double cantidad = 0;
            double costo;
            double total = 0;

            while (rsEmps.next()) {
                KardexTB kardexTB = new KardexTB();
                kardexTB.setId(rsEmps.getRow());
                kardexTB.setIdArticulo(rsEmps.getString("IdSuministro"));
                kardexTB.setFecha(rsEmps.getDate("Fecha").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                kardexTB.setHora(rsEmps.getTime("Hora").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                kardexTB.setTipo(rsEmps.getShort("Tipo"));
                kardexTB.setMovimientoName(rsEmps.getString("Nombre"));
                kardexTB.setDetalle(rsEmps.getString("Detalle"));
                kardexTB.setCantidad(rsEmps.getDouble("Cantidad")); 
                kardexTB.setCosto(rsEmps.getDouble("Costo"));
                kardexTB.setTotal(rsEmps.getDouble("Total"));
//                cantidad = (kardexTB.getTipo() == 1 ? kardexTB.getCantidad() : -kardexTB.getCantidad());

                Label lblCantidadEntrada = new Label(kardexTB.getTipo() == 1 ? Tools.roundingValue(kardexTB.getCantidad(), 4) : "");
                lblCantidadEntrada.getStyleClass().add("labelRoboto14");
                lblCantidadEntrada.setStyle("-fx-text-fill:#4caf50;-fx-font-weight:bold;");

                Label lblCostoEntrada = new Label(kardexTB.getTipo() == 1 ? Tools.roundingValue(kardexTB.getCosto(), 4) : "");
                lblCostoEntrada.getStyleClass().add("labelRoboto14");
                lblCostoEntrada.setStyle("-fx-text-fill:#000000;-fx-font-weight:bold;");

                Label lblTotalEntrada = new Label(kardexTB.getTipo() == 1 ? Tools.roundingValue(kardexTB.getTotal(), 4) : "");
                lblTotalEntrada.getStyleClass().add("labelRoboto14");
                lblTotalEntrada.setStyle("-fx-text-fill:#000000;-fx-font-weight:bold;");

                kardexTB.setLblCantidadEntreda(lblCantidadEntrada);
                kardexTB.setLblCostoEntrada(lblCostoEntrada);
                kardexTB.setLblTotalEntrada(lblTotalEntrada);

//                
                
                Label lblCantidadSalida = new Label(kardexTB.getTipo() == 2 ? Tools.roundingValue(kardexTB.getCantidad(),4) : "");
                lblCantidadSalida.getStyleClass().add("labelRoboto14");
                lblCantidadSalida.setStyle("-fx-text-fill:#f44336;-fx-font-weight:bold;");
                
                Label lblCostoSalida = new Label(kardexTB.getTipo() == 2 ? Tools.roundingValue(kardexTB.getCosto(), 4) : "");
                lblCostoSalida.getStyleClass().add("labelRoboto14");
                lblCostoSalida.setStyle("-fx-text-fill:#000000;-fx-font-weight:bold;");
                
                Label lblTotalSalida = new Label(kardexTB.getTipo() == 2 ? Tools.roundingValue(kardexTB.getTotal(), 4) : "");
                lblTotalSalida.getStyleClass().add("labelRoboto14");
                lblTotalSalida.setStyle("-fx-text-fill:#000000;-fx-font-weight:bold;");

                kardexTB.setLblCantidadSalida(lblCantidadSalida);
                kardexTB.setLblCostoSalida(lblCostoSalida);
                kardexTB.setLblTotalSalida(lblTotalSalida);

//                

                cantidad = cantidad + (kardexTB.getTipo() == 1 ? kardexTB.getCantidad() : -kardexTB.getCantidad());
                
                total = total + (kardexTB.getTipo() == 1 ? kardexTB.getTotal(): -kardexTB.getTotal());
                
                costo = total / cantidad;

                Label lblCantidadSaldo = new Label(Tools.roundingValue(cantidad, 4));
                lblCantidadSaldo.getStyleClass().add("labelRoboto14");
                lblCantidadSaldo.setStyle("-fx-text-fill:#000000;-fx-font-weight:bold;");
                
                Label lblCostoSaldo = new Label(Tools.roundingValue(costo, 4));
                lblCostoSaldo.getStyleClass().add("labelRoboto14");
                lblCostoSaldo.setStyle("-fx-text-fill:#000000;-fx-font-weight:bold;");
                
                Label lblTotalSaldo = new Label(Tools.roundingValue(total, 4));
                lblTotalSaldo.getStyleClass().add("labelRoboto14");
                lblTotalSaldo.setStyle("-fx-text-fill:#000000;-fx-font-weight:bold;");

                kardexTB.setLblCantidadSaldo(lblCantidadSaldo);
                kardexTB.setLblCostoSaldo(lblCostoSaldo);
                kardexTB.setLblTotalSaldo(lblTotalSaldo);

                empList.add(kardexTB);
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

}
