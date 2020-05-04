package model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CajaADO {

    public static String AperturarCaja(CajaTB cajaTB, BancoHistorialTB bancoHistorialTB) {
        String result = "";
        CallableStatement statementCodigoCaja = null;
        PreparedStatement statementCaja = null;

        CallableStatement codigoBancoHistorial = null;
        PreparedStatement preparedBanco = null;
        PreparedStatement preparedBancoHistorial = null;

        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            try {

                DBUtil.getConnection().setAutoCommit(false);

                statementCodigoCaja = DBUtil.getConnection().prepareCall("{? = call Fc_Caja_Codigo_Alfanumerico()}");
                statementCodigoCaja.registerOutParameter(1, java.sql.Types.VARCHAR);
                statementCodigoCaja.execute();
                String idCaja = statementCodigoCaja.getString(1);

                statementCaja = DBUtil.getConnection().prepareStatement("INSERT INTO CajaTB(IdCaja,FechaApertura,FechaCierre,HoraApertura,HoraCierre,Estado,Contado,Calculado,Diferencia,IdUsuario,IdBanco)VALUES(?,?,?,?,?,?,?,?,?,?,?)");
                statementCaja.setString(1, idCaja);
                statementCaja.setString(2, cajaTB.getFechaApertura());
                statementCaja.setString(3, cajaTB.getFechaApertura());
                statementCaja.setString(4, cajaTB.getHoraApertura());
                statementCaja.setString(5, cajaTB.getHoraApertura());
                statementCaja.setBoolean(6, cajaTB.isEstado());
                statementCaja.setDouble(7, cajaTB.getContado());
                statementCaja.setDouble(8, cajaTB.getCalculado());
                statementCaja.setDouble(9, cajaTB.getDiferencia());
                statementCaja.setString(10, cajaTB.getIdUsuario());
                statementCaja.setString(11, cajaTB.getIdBanco());
                statementCaja.addBatch();


                preparedBanco = DBUtil.getConnection().prepareStatement("UPDATE Banco SET SaldoInicial = SaldoInicial + ? WHERE IdBanco = ?");
                preparedBanco.setDouble(1, bancoHistorialTB.getEntrada());
                preparedBanco.setString(2, bancoHistorialTB.getIdBanco());
                preparedBanco.addBatch();

                preparedBancoHistorial = DBUtil.getConnection().prepareStatement("INSERT INTO BancoHistorialTB(IdBanco,IdProcedencia,Descripcion,Fecha,Hora,Entrada,Salida)VALUES(?,?,?,?,?,?,?)");
                preparedBancoHistorial.setString(1, bancoHistorialTB.getIdBanco());
                preparedBancoHistorial.setString(2, "");
                preparedBancoHistorial.setString(3, bancoHistorialTB.getDescripcion());
                preparedBancoHistorial.setString(4, bancoHistorialTB.getFecha());
                preparedBancoHistorial.setString(5, bancoHistorialTB.getHora());
                preparedBancoHistorial.setDouble(6, bancoHistorialTB.getEntrada());
                preparedBancoHistorial.setDouble(7, 0);
                preparedBancoHistorial.addBatch();

                statementCaja.executeBatch();
                preparedBanco.executeBatch();
                preparedBancoHistorial.executeBatch();
                DBUtil.getConnection().commit();
                result = "registrado";
            } catch (SQLException ex) {
                try {
                    DBUtil.getConnection().rollback();
                    result = ex.getLocalizedMessage();
                } catch (SQLException ex1) {
                    result = ex1.getLocalizedMessage();
                }
            } finally {
                try {
                    if (statementCodigoCaja != null) {
                        statementCodigoCaja.close();
                    }
                    if (statementCaja != null) {
                        statementCaja.close();
                    }

                    if (codigoBancoHistorial != null) {
                        codigoBancoHistorial.close();
                    }
                    if (preparedBanco != null) {
                        preparedBanco.close();
                    }
                    if (preparedBancoHistorial != null) {
                        preparedBancoHistorial.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    result = ex.getLocalizedMessage();
                }
            }
        } else {
            result = "No se puedo establecer una conexión con el servidor, intente nuevamente.";
        }
        return result;
    }

    public static CajaTB ValidarCreacionCaja(String idUsuario) {
        CajaTB cajaTB = new CajaTB();
        PreparedStatement statementValidar = null;
        PreparedStatement statementVentas = null;
        try {
            DBUtil.dbConnect();
            statementValidar = DBUtil.getConnection().prepareStatement("SELECT * FROM CajaTB WHERE IdUsuario = ? AND Estado = 1");
            statementValidar.setString(1, idUsuario);
            if (statementValidar.executeQuery().next()) {
                statementValidar = DBUtil.getConnection().prepareStatement("SELECT IdCaja FROM CajaTB WHERE IdUsuario = ? AND Estado = 1 AND CAST(FechaApertura AS DATE) = CAST(GETDATE() AS DATE)");
                statementValidar.setString(1, idUsuario);
                if (statementValidar.executeQuery().next()) {
                    ResultSet resultSet = statementValidar.executeQuery();
                    cajaTB.setId(2);
                    cajaTB.setIdCaja(resultSet.next() ? "" + resultSet.getString("IdCaja") : "0");
                } else {
                    statementValidar = DBUtil.getConnection().prepareStatement("SELECT IdCaja,FechaApertura,HoraApertura FROM CajaTB WHERE IdUsuario = ? AND Estado = 1");
                    statementValidar.setString(1, idUsuario);
                    ResultSet resultSet = statementValidar.executeQuery();
                    if (resultSet.next()) {
                        cajaTB.setId(3);
                        cajaTB.setIdCaja(resultSet.getString("IdCaja"));
                        cajaTB.setFechaApertura(resultSet.getDate("FechaApertura").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)) + " ");
                        cajaTB.setHoraApertura(resultSet.getTime("HoraApertura").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                    } else {
                        cajaTB.setId(4);
                        cajaTB.setIdCaja("");
                    }
                }
            } else {
                cajaTB.setId(1);
                cajaTB.setIdCaja("");
            }

        } catch (SQLException ex) {
            System.out.println("Error en caja:" + ex.getLocalizedMessage());
        } finally {
            try {
                if (statementVentas != null) {
                    statementVentas.close();
                }
                if (statementValidar != null) {
                    statementValidar.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                System.out.println("Error en caja:" + ex.getLocalizedMessage());
            }
        }
        return cajaTB;
    }

    public static CajaTB ValidarAperturaCajaParaTrabajadores(String idTrabajador) {
        CajaTB cajaTB = null;
        PreparedStatement statementCaja = null;
        try {
            DBUtil.dbConnect();
            statementCaja = DBUtil.getConnection().prepareStatement("SELECT IdCaja,Estado FROM CajaTB WHERE IdUsuario = ? AND CAST(FechaApertura AS DATE) = CAST(GETDATE() AS DATE) AND Estado = 1");
            statementCaja.setString(1, idTrabajador);
            try (ResultSet result = statementCaja.executeQuery()) {
                if (result.next()) {
                    cajaTB = new CajaTB();
                    cajaTB.setIdCaja(result.getString("IdCaja"));
                    cajaTB.setEstado(result.getBoolean("Estado"));
                }
            }
        } catch (SQLException ex) {

        } finally {
            try {
                if (statementCaja != null) {
                    statementCaja.close();
                }

                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return cajaTB;
    }

    public static String CerrarAperturaCaja(String idCaja, String date, String time, boolean state, double contado, double calculado) {
        String cajaTB = "";
        PreparedStatement statementCaja = null;
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);
            statementCaja = DBUtil.getConnection().prepareStatement("UPDATE CajaTB SET FechaCierre = ?,HoraCierre = ?,Contado = ?,Calculado = ?,Diferencia = ?,Estado=? WHERE IdCaja = ?");
            statementCaja.setString(1, date);
            statementCaja.setString(2, time);
            statementCaja.setDouble(3, contado);
            statementCaja.setDouble(4, calculado);
            statementCaja.setDouble(5, contado - calculado);
            statementCaja.setBoolean(6, state);
            statementCaja.setString(7, idCaja);
            statementCaja.addBatch();

            statementCaja.executeBatch();
            DBUtil.getConnection().commit();
            cajaTB = "completed";
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException e) {

            }
            cajaTB = ex.getLocalizedMessage();
        } finally {
            try {
                if (statementCaja != null) {
                    statementCaja.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return cajaTB;
    }

    public static ObservableList<CajaTB> ListarCajasAperturadas(String fechaInicial, String fechaFinal) {
        PreparedStatement statementLista = null;
        ObservableList<CajaTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            statementLista = DBUtil.getConnection().prepareStatement("{call Sp_ListarCajasAperturadas(?,?)}");
            statementLista.setString(1, fechaInicial);
            statementLista.setString(2, fechaFinal);

            ResultSet result = statementLista.executeQuery();
            while (result.next()) {
                CajaTB cajaTB = new CajaTB();
                cajaTB.setIdCaja(result.getString("IdCaja"));
                cajaTB.setFechaApertura(result.getDate("FechaApertura").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
                cajaTB.setHoraApertura(result.getTime("HoraApertura").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                cajaTB.setFechaCierre(result.getDate("FechaCierre").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
                cajaTB.setHoraCierre(result.getTime("HoraCierre").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                cajaTB.setEstado(result.getBoolean("Estado"));
                cajaTB.setContado(result.getDouble("Contado"));
                cajaTB.setCalculado(result.getDouble("Calculado"));
                cajaTB.setDiferencia(result.getDouble("Diferencia"));
                cajaTB.setEmpleadoTB(new EmpleadoTB(result.getString("Apellidos"), result.getString("Nombres")));
                empList.add(cajaTB);
            }
        } catch (SQLException ex) {
            System.out.println("Error en la funcion ListarCajasAperturadas xd:" + ex.getLocalizedMessage());
        } finally {
            try {
                if (statementLista != null) {
                    statementLista.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return empList;
    }

    public static ObservableList<CajaTB> ListarCajasAperturadasByUser(String idUsuario) {
        PreparedStatement statementLista = null;
        ObservableList<CajaTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            statementLista = DBUtil.getConnection().prepareStatement("{call Sp_ListarCajasAperturadasPorUsuario(?)}");
            statementLista.setString(1, idUsuario);

            ResultSet result = statementLista.executeQuery();
            while (result.next()) {
                CajaTB cajaTB = new CajaTB();
                cajaTB.setId(result.getRow());
                cajaTB.setIdCaja(result.getString("IdCaja"));
                cajaTB.setFechaApertura(result.getDate("FechaApertura").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
                cajaTB.setHoraApertura(result.getTime("HoraApertura").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                cajaTB.setFechaCierre(result.getDate("FechaCierre").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
                cajaTB.setHoraCierre(result.getTime("HoraCierre").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                cajaTB.setEstado(result.getBoolean("Estado"));
                cajaTB.setContado(result.getDouble("Contado"));
                cajaTB.setCalculado(result.getDouble("Calculado"));
                cajaTB.setDiferencia(result.getDouble("Diferencia"));
                cajaTB.setEmpleadoTB(new EmpleadoTB(result.getString("Apellidos"), result.getString("Nombres")));
                empList.add(cajaTB);
            }
        } catch (SQLException ex) {
            System.out.println("Error en la funcion ListarCajasAperturadas:" + ex.getLocalizedMessage());
        } finally {
            try {
                if (statementLista != null) {
                    statementLista.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return empList;
    }

    public static String CrudListaCaja(String idListaCaja, String nombre, boolean estado) {
        String result = "";
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            CallableStatement codigoListaCaja = null;
            PreparedStatement statementValidar = null;
            PreparedStatement statementCaja = null;
            try {
                DBUtil.getConnection().setAutoCommit(false);
                statementValidar = DBUtil.getConnection().prepareStatement("SELECT * FROM ListaCajaTB WHERE IdListaCaja = ?");
                statementValidar.setString(1, idListaCaja);
                if (statementValidar.executeQuery().next()) {
                    statementCaja = DBUtil.getConnection().prepareStatement("UPDATE ListaCajaTB\n"
                            + "   SET Nombre = ? \n"
                            + "      ,Estado = ? \n"
                            + " WHERE IdListaCaja = ?");
                    statementCaja.setString(1, nombre);
                    statementCaja.setBoolean(2, estado);
                    statementCaja.setString(3, idListaCaja);
                    statementCaja.addBatch();
                    statementCaja.executeBatch();
                    DBUtil.getConnection().commit();
                    result = "updated";
                } else {
                    codigoListaCaja = DBUtil.getConnection().prepareCall("{? = call [Fc_Lista_Caja_Codigo_Alfanumerico]()}");
                    codigoListaCaja.registerOutParameter(1, java.sql.Types.VARCHAR);
                    codigoListaCaja.execute();
                    String idCaja = codigoListaCaja.getString(1);

                    statementCaja = DBUtil.getConnection().prepareStatement("INSERT INTO ListaCajaTB(IdListaCaja,Nombre,Estado)VALUES(?,?,?)");
                    statementCaja.setString(1, idCaja);
                    statementCaja.setString(2, nombre);
                    statementCaja.setBoolean(3, estado);
                    statementCaja.addBatch();
                    statementCaja.executeBatch();
                    DBUtil.getConnection().commit();
                    result = "insert";
                }
            } catch (SQLException ex) {
                try {
                    DBUtil.getConnection().rollback();
                } catch (SQLException e) {
                }
                result = ex.getLocalizedMessage();
            } finally {
                try {
                    if (codigoListaCaja != null) {
                        codigoListaCaja.close();
                    }
                    if (statementValidar != null) {
                        statementValidar.close();
                    }
                    if (statementCaja != null) {
                        statementCaja.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    result = ex.getLocalizedMessage();
                }
            }

        } else {
            result = "No se puedo conectar al servidor, revise su conexión e intente nuevamente.";
        }
        return result;
    }

    public static ObservableList<ListaCajaTB> ListListaCaja() {
        String selectStmt = "SELECT * FROM ListaCajaTB";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<ListaCajaTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                ListaCajaTB listaCajaTB = new ListaCajaTB();
                listaCajaTB.setIdListaCaja(rsEmps.getString("IdListaCaja"));
                listaCajaTB.setNombre(rsEmps.getString("Nombre").toUpperCase());
                listaCajaTB.setEstado(rsEmps.getBoolean("Estado"));
                empList.add(listaCajaTB);
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
