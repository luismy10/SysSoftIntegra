package model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;

public class ProduccionADO {

    public static String Registrar_Produccion(ProduccionTB produccionTB) {
        PreparedStatement statementRegisrar = null;
        PreparedStatement statementDetalle = null;
        CallableStatement statementCodigo = null;
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            statementCodigo = DBUtil.getConnection().prepareCall("{? = call Fc_Produccion_Codigo_Alfanumerico()}");
            statementCodigo.registerOutParameter(1, java.sql.Types.VARCHAR);
            statementCodigo.execute();
            String id_produccion = statementCodigo.getString(1);

            statementRegisrar = DBUtil.getConnection().prepareStatement("INSERT INTO ProduccionTB("
                    + "IdProduccion,"
                    + "FechaInico,"
                    + "HoraInicio,"
                    + "Dias,"
                    + "Horas,"
                    + "Minutos,"
                    + "IdProducto,"
                    + "TipoOrden,"
                    + "IdEncargado,"
                    + "Descripcion,"
                    + "FechaRegistro,"
                    + "HoraRegistro,"
                    + "Cantidad,"
                    + "Estado"
                    + ")VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            statementRegisrar.setString(1, id_produccion);
            statementRegisrar.setString(2, produccionTB.getFechaInicio());
            statementRegisrar.setString(3, produccionTB.getHoraInicio());
            statementRegisrar.setInt(4, produccionTB.getDias());
            statementRegisrar.setInt(5, produccionTB.getHoras());
            statementRegisrar.setInt(6, produccionTB.getMinutos());
            statementRegisrar.setString(7, produccionTB.getIdProducto());
            statementRegisrar.setBoolean(8, produccionTB.isTipoOrden());
            statementRegisrar.setString(9, produccionTB.getIdEncargado());
            statementRegisrar.setString(10, produccionTB.getDescripcion());
            statementRegisrar.setString(11, produccionTB.getFechaRegistro());
            statementRegisrar.setString(12, produccionTB.getHoraRegistro());
            statementRegisrar.setDouble(13, produccionTB.getCantidad());
            statementRegisrar.setInt(14, produccionTB.getEstado());
            statementRegisrar.addBatch();

            statementDetalle = DBUtil.getConnection().prepareStatement("INSERT INTO ProduccionDetalleTB(IdProduccion,IdProducto,Cantidad)VALUES(?,?,?)");
            for (InsumoTB insumoTB : produccionTB.getInsumoTBs()) {
                statementDetalle.setString(1, id_produccion);
                statementDetalle.setString(2, insumoTB.getComboBox().getSelectionModel().getSelectedItem().getIdInsumo());
                statementDetalle.setDouble(3, Double.parseDouble(insumoTB.getTxtCantidad().getText()));
                statementDetalle.addBatch();
            }

            statementRegisrar.executeBatch();
            statementDetalle.executeBatch();
            DBUtil.getConnection().commit();
            return "registrado";
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException e) {

            }
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementRegisrar != null) {
                    statementRegisrar.close();
                }
                if (statementDetalle != null) {
                    statementDetalle.close();
                }
            } catch (SQLException e) {

            }
        }
    }

    public static Object ListarProduccion(int tipo, String fechaInicio, String fechaFinal, String busqueda, int posicionPagina, int filasPorPagina) {
        PreparedStatement statementListar = null;
        try {
            DBUtil.dbConnect();
            Object[] objects = new Object[2];

            ObservableList<ProduccionTB> produccionTBs = FXCollections.observableArrayList();
            statementListar = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_Produccion(?,?,?,?,?,?)}");
            statementListar.setInt(1, tipo);
            statementListar.setString(2, fechaInicio);
            statementListar.setString(3, fechaFinal);
            statementListar.setString(4, busqueda);
            statementListar.setInt(5, posicionPagina);
            statementListar.setInt(6, filasPorPagina);
            ResultSet resultSet = statementListar.executeQuery();
            while (resultSet.next()) {
                ProduccionTB produccionTB = new ProduccionTB();
                produccionTB.setId(resultSet.getRow() + posicionPagina);
                produccionTB.setIdProduccion(resultSet.getString("IdProduccion"));
                produccionTB.setFechaRegistro(resultSet.getDate("FechaRegistro").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM//yyyy")));
                produccionTB.setHoraRegistro(resultSet.getTime("HoraRegistro").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                produccionTB.setFechaInicio(resultSet.getDate("FechaInico").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM//yyyy")));
                produccionTB.setHoraInicio(resultSet.getTime("HoraInicio").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                produccionTB.setDias(resultSet.getInt("Dias"));
                produccionTB.setHoras(resultSet.getInt("Horas"));
                produccionTB.setMinutos(resultSet.getInt("Minutos"));
                produccionTB.setCantidad(resultSet.getDouble("Cantidad"));

                SuministroTB suministroTB = new SuministroTB();
                suministroTB.setClave(resultSet.getString("Clave"));
                suministroTB.setNombreMarca(resultSet.getString("NombreMarca"));
                suministroTB.setUnidadCompraName(resultSet.getString("Medida"));
                produccionTB.setSuministroTB(suministroTB);

                EmpleadoTB empleadoTB = new EmpleadoTB();
                empleadoTB.setNumeroDocumento(resultSet.getString("NumeroDocumento"));
                empleadoTB.setApellidos(resultSet.getString("Apellidos"));
                empleadoTB.setNombres(resultSet.getString("Nombres"));
                produccionTB.setEmpleadoTB(empleadoTB);

                Label label = new Label();
                switch (resultSet.getInt("Estado")) {
                    case 3:
                        label.setText("ANULADO");
                        label.getStyleClass().add("label-proceso");
                        break;
                    case 2:
                        label.setText("EN PRODUCCIÓN");
                        label.getStyleClass().add("label-medio");
                        break;
                    default:
                        label.setText("COMPLETADO");
                        label.getStyleClass().add("label-asignacion");
                        break;
                }
                produccionTB.setLblEstado(label);

                produccionTBs.add(produccionTB);
            }

            statementListar = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_Produccion_Count(?,?,?,?)}");
            statementListar.setInt(1, tipo);
            statementListar.setString(2, fechaInicio);
            statementListar.setString(3, fechaFinal);
            statementListar.setString(4, busqueda);
            resultSet = statementListar.executeQuery();
            Integer cantidadTotal = 0;
            if (resultSet.next()) {
                cantidadTotal = resultSet.getInt("Total");
            }

            objects[0] = produccionTBs;
            objects[1] = cantidadTotal;

            return objects;
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementListar != null) {
                    statementListar.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }

}
