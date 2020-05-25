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

    public static ObservableList<KardexTB> List_Kardex_By_Id_Articulo(String value) {
        String selectStmt = "{call Sp_Listar_Kardex_Articulo_By_Id(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<KardexTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, value);
            rsEmps = preparedStatement.executeQuery();

            double cantidadActual;
            double cantidadTotal = 0;

            double cTotalActual;
            double cTotalTotal = 0;

            while (rsEmps.next()) {
                KardexTB kardexTB = new KardexTB();
                kardexTB.setIdArticulo(rsEmps.getString("IdArticulo"));
                kardexTB.setFecha(rsEmps.getString("Fecha"));
                kardexTB.setHora(rsEmps.getTime("Hora").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                kardexTB.setTipo(rsEmps.getShort("Tipo"));
                kardexTB.setMovimientoName(rsEmps.getString("Nombre"));
                kardexTB.setDetalle(rsEmps.getString("Detalle"));
                kardexTB.setCantidad(rsEmps.getDouble("Cantidad"));
//                kardexTB.setcUnitario(rsEmps.getDouble("CUnitario"));
//                kardexTB.setcTotal(rsEmps.getDouble("CTotal"));

                cantidadActual = (kardexTB.getTipo() == 1 ? kardexTB.getCantidad() : -kardexTB.getCantidad());
                cantidadTotal = cantidadTotal + cantidadActual;
                kardexTB.setCantidadTotal(cantidadTotal);

//                kardexTB.setcUnictarioTotal(kardexTB.getcUnitario());
//                cTotalActual = (kardexTB.getTipo() == 1 ? kardexTB.getcTotal(): -kardexTB.getcTotal());
//                cTotalTotal = cTotalTotal + cTotalActual;
//                kardexTB.setcTotalTotal(cTotalTotal);
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

    public static ObservableList<KardexTB> List_Kardex_By_Id_Suministro(String value) {
        String selectStmt = "{call Sp_Listar_Kardex_Suministro_By_Id(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<KardexTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, value);
            rsEmps = preparedStatement.executeQuery();

            double cantidadInicial = 0;
            double cantidadActual;
            double cantidadTotal = 0;

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

               
                cantidadActual = (kardexTB.getTipo() == 1 ? kardexTB.getCantidad() : -kardexTB.getCantidad());
                cantidadTotal = cantidadTotal + cantidadActual;
                
                 cantidadInicial = cantidadTotal - cantidadActual;

                kardexTB.setInicial(cantidadInicial);
                kardexTB.setCantidadTotal(cantidadTotal);
                
                Label lblInicial = new Label(Tools.roundingValue(kardexTB.getInicial(), 4));
                lblInicial.getStyleClass().add("labelRoboto14");
                lblInicial.setStyle("-fx-text-fill:#0066ff;-fx-font-weight:bold;");
                Label lblEntreda = new Label(kardexTB.getTipo() == 1 ? Tools.roundingValue(kardexTB.getCantidad(), 4) : "0.0000");
                lblEntreda.getStyleClass().add("labelRoboto14");
                lblEntreda.setStyle("-fx-text-fill:#33cc33;-fx-font-weight:bold;");
                Label lblSalida = new Label(kardexTB.getTipo() == 2 ? Tools.roundingValue(kardexTB.getCantidad()== 0 ? kardexTB.getCantidad(): -kardexTB.getCantidad(), 4):"0.0000");
                lblSalida.getStyleClass().add("labelRoboto14");
                lblSalida.setStyle("-fx-text-fill:#ff0000;-fx-font-weight:bold;");
                Label lblSaldo = new Label(Tools.roundingValue(kardexTB.getCantidadTotal(), 4));
                lblSaldo.getStyleClass().add("labelRoboto14");
                lblSaldo.setStyle("-fx-text-fill:#000000;-fx-font-weight:bold;");
                
                kardexTB.setLblInicial(lblInicial);
                kardexTB.setLblEntrada(lblEntreda);
                kardexTB.setLblSalida(lblSalida);
                kardexTB.setLblSaldo(lblSaldo);
                
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
