package model;

import controller.tools.Tools;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;

public class KardexADO {

    public static ArrayList<Object> List_Kardex_By_Id_Suministro(short opcion, String value, String fechaInicial, String fechaFinal, int posicionPagina, int filasPorPagina) {
        String selectStmt = "{call Sp_Listar_Kardex_Suministro_By_Id(?,?,?,?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;

        ArrayList<Object> objects = new ArrayList<>();
        ObservableList<KardexTB> empList = FXCollections.observableArrayList();

        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setShort(1, opcion);
            preparedStatement.setString(2, value);
            preparedStatement.setString(3, fechaInicial);
            preparedStatement.setString(4, fechaFinal);
//            preparedStatement.setInt(5, posicionPagina);
//            preparedStatement.setInt(6, filasPorPagina);

            rsEmps = preparedStatement.executeQuery();

            double cantidad = 0;
            double saldo = 0;

            while (rsEmps.next()) {
                KardexTB kardexTB = new KardexTB();
                kardexTB.setId(rsEmps.getRow() + posicionPagina);
                kardexTB.setIdArticulo(rsEmps.getString("IdSuministro"));
                kardexTB.setFecha(rsEmps.getDate("Fecha").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
                kardexTB.setHora(rsEmps.getTime("Hora").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                kardexTB.setTipo(rsEmps.getShort("Tipo"));
                kardexTB.setMovimientoName(rsEmps.getString("Nombre"));
                kardexTB.setDetalle(rsEmps.getString("Detalle"));
                kardexTB.setCantidad(rsEmps.getDouble("Cantidad"));
                kardexTB.setCosto(rsEmps.getDouble("Costo"));
                kardexTB.setTotal(rsEmps.getDouble("Total"));

                cantidad = cantidad + (kardexTB.getTipo() == 1 ? kardexTB.getCantidad() : -kardexTB.getCantidad());

                Label lblCantidadEntrada = new Label(kardexTB.getTipo() == 1 ? Tools.roundingValue(kardexTB.getCantidad(), 4) : "");
                lblCantidadEntrada.getStyleClass().add("labelRobotoBold13");
                lblCantidadEntrada.setStyle("-fx-max-width:Infinity;-fx-max-height:Infinity;-fx-background-color:#c6efd0;-fx-text-fill:#297521;-fx-alignment:center-right;");

                Label lblCantidadSalida = new Label(kardexTB.getTipo() == 2 ? "-" + Tools.roundingValue(kardexTB.getCantidad(), 4) : "");
                lblCantidadSalida.getStyleClass().add("labelRobotoBold13");
                lblCantidadSalida.setStyle("-fx-max-width:Infinity;-fx-max-height:Infinity;-fx-background-color:#ffc6d1;-fx-text-fill:#890d15;-fx-alignment:center-right;");

                Label lblExistencia = new Label(Tools.roundingValue(cantidad, 4));
                lblExistencia.getStyleClass().add("labelRoboto13");
                lblExistencia.setStyle("-fx-text-fill:#020203;-fx-font-weight:bold;");

                Label lblCosto = new Label(Tools.roundingValue(kardexTB.getCosto(), 4));
                lblCosto.getStyleClass().add("labelRoboto13");
                lblCosto.setStyle("-fx-text-fill:#020203;-fx-font-weight:bold;");

                Label lblDebe = new Label(kardexTB.getTipo() == 1 ? Tools.roundingValue(kardexTB.getTotal(), 4) : "");
                lblDebe.getStyleClass().add("labelRoboto13");
                lblDebe.setStyle("-fx-text-fill:#020203;-fx-font-weight:bold;");

                Label lblHaber = new Label(kardexTB.getTipo() == 2 ? "-" + Tools.roundingValue(kardexTB.getTotal(), 4) : "");
                lblHaber.getStyleClass().add("labelRoboto13");
                lblHaber.setStyle("-fx-text-fill:#020203;-fx-font-weight:bold;");

                saldo = saldo + (kardexTB.getTipo() == 1 ? kardexTB.getTotal() : -kardexTB.getTotal());

                Label lblSaldo = new Label(Tools.roundingValue(saldo, 4));
                lblSaldo.getStyleClass().add("labelRoboto13");
                lblSaldo.setStyle("-fx-text-fill:#020203;-fx-font-weight:bold;");

                kardexTB.setLblEntreda(lblCantidadEntrada);
                kardexTB.setLblSalida(lblCantidadSalida);
                kardexTB.setLblExistencia(lblExistencia);
                kardexTB.setLblCosto(lblCosto);
                kardexTB.setLblDebe(lblDebe);
                kardexTB.setLblHaber(lblHaber);
                kardexTB.setLblSaldo(lblSaldo);

                empList.add(kardexTB);
            }

            objects.add(empList);
            objects.add(cantidad);
            objects.add(saldo);

//            preparedStatement = DBUtil.getConnection().prepareStatement("{call Sp_Listar_Kardex_Suministro_By_Id_Count(?,?,?,?)}");
//            preparedStatement.setShort(1, opcion);
//            preparedStatement.setString(2, value);
//            preparedStatement.setString(3, fechaInicial);
//            preparedStatement.setString(4, fechaFinal);
//            rsEmps = preparedStatement.executeQuery();
//            Integer integer = 0;
//            if (rsEmps.next()) {
//                integer = rsEmps.getInt("Total");
//            }
//            objects.add(integer);
//            objects.add(cantidad);
//            objects.add(total);
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
        return objects;
    }

}
