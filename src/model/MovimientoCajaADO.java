package model;

import controller.tools.Session;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MovimientoCajaADO {

    public static MovimientoCajaTB FondoCaja(String idCaja) {
        MovimientoCajaTB movimientoCajaTB = null;
        PreparedStatement statementVentas = null;
        try {
            DBUtil.dbConnect();
            statementVentas = DBUtil.getConnection().prepareStatement("SELECT * FROM MovimientoCajaTB WHERE IdCaja = ? AND Movimiento = ?");
            statementVentas.setString(1, idCaja);
            statementVentas.setString(2, "FONC");
            if (statementVentas.executeQuery().next()) {
                movimientoCajaTB = new MovimientoCajaTB();
                try (ResultSet result = statementVentas.executeQuery()) {
                    while (result.next()) {
                        movimientoCajaTB.setSaldo(movimientoCajaTB.getSaldo() + result.getDouble("Saldo"));
                    }
                }
            }
        } catch (SQLException ex) {

        } finally {
            try {
                if (statementVentas != null) {
                    statementVentas.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return movimientoCajaTB;
    }

    public static MovimientoCajaTB VentasEfectivo(String idCaja) {
        MovimientoCajaTB movimientoCajaTB = null;
        PreparedStatement statementVentas = null;
        try {
            DBUtil.dbConnect();
            statementVentas = DBUtil.getConnection().prepareStatement("SELECT * FROM MovimientoCajaTB WHERE IdCaja = ? AND Movimiento = ?");
            statementVentas.setString(1, idCaja);
            statementVentas.setString(2, "VEN");
            if (statementVentas.executeQuery().next()) {
                movimientoCajaTB = new MovimientoCajaTB();
                try (ResultSet result = statementVentas.executeQuery()) {
                    while (result.next()) {
                        movimientoCajaTB.setSaldo(movimientoCajaTB.getSaldo() + result.getDouble("Saldo"));
                    }
                }
            }
        } catch (SQLException ex) {

        } finally {
            try {
                if (statementVentas != null) {
                    statementVentas.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return movimientoCajaTB;
    }

    public static MovimientoCajaTB VentasCredito(String idCaja) {
        MovimientoCajaTB movimientoCajaTB = null;
        PreparedStatement statementVentas = null;
        try {
            DBUtil.dbConnect();
            statementVentas = DBUtil.getConnection().prepareStatement("SELECT * FROM MovimientoCajaTB WHERE IdCaja = ? AND Movimiento = ?");
            statementVentas.setString(1, idCaja);
            statementVentas.setString(2, "VENCRE");
            if (statementVentas.executeQuery().next()) {
                movimientoCajaTB = new MovimientoCajaTB();
                try (ResultSet result = statementVentas.executeQuery()) {
                    while (result.next()) {
                        movimientoCajaTB.setSaldo(movimientoCajaTB.getSaldo() + result.getDouble("Saldo"));
                    }
                }
            }
        } catch (SQLException ex) {

        } finally {
            try {
                if (statementVentas != null) {
                    statementVentas.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return movimientoCajaTB;
    }

    public static MovimientoCajaTB EgresosEfectivoCompra(String idCaja) {
        MovimientoCajaTB movimientoCajaTB = null;
        PreparedStatement statementVentas = null;
        try {
            DBUtil.dbConnect();
            statementVentas = DBUtil.getConnection().prepareStatement("SELECT * FROM MovimientoCajaTB WHERE IdCaja = ? AND Movimiento = ?");
            statementVentas.setString(1, idCaja);
            statementVentas.setString(2, "COM");
            if (statementVentas.executeQuery().next()) {
                movimientoCajaTB = new MovimientoCajaTB();
                try (ResultSet result = statementVentas.executeQuery()) {
                    while (result.next()) {
                        movimientoCajaTB.setSaldo(movimientoCajaTB.getSaldo() + result.getDouble("Saldo"));
                    }
                }
            }
        } catch (SQLException ex) {

        } finally {
            try {
                if (statementVentas != null) {
                    statementVentas.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return movimientoCajaTB;
    }

    public static MovimientoCajaTB IngresosEfectivo(String idCaja) {
        MovimientoCajaTB movimientoCajaTB = null;
        PreparedStatement statementVentas = null;
        try {
            DBUtil.dbConnect();
            statementVentas = DBUtil.getConnection().prepareStatement("SELECT * FROM MovimientoCajaTB WHERE IdCaja = ? AND Movimiento = ?");
            statementVentas.setString(1, idCaja);
            statementVentas.setString(2, "ENTR");
            if (statementVentas.executeQuery().next()) {
                movimientoCajaTB = new MovimientoCajaTB();
                try (ResultSet result = statementVentas.executeQuery()) {
                    while (result.next()) {
                        movimientoCajaTB.setSaldo(movimientoCajaTB.getSaldo() + result.getDouble("Saldo"));
                    }
                }
            }
        } catch (SQLException ex) {

        } finally {
            try {
                if (statementVentas != null) {
                    statementVentas.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return movimientoCajaTB;
    }

    public static MovimientoCajaTB EgresosEfectivo(String idCaja) {
        MovimientoCajaTB movimientoCajaTB = null;
        PreparedStatement statementVentas = null;
        try {
            DBUtil.dbConnect();
            statementVentas = DBUtil.getConnection().prepareStatement("SELECT * FROM MovimientoCajaTB WHERE IdCaja = ? AND Movimiento = ?");
            statementVentas.setString(1, idCaja);
            statementVentas.setString(2, "SALI");
            if (statementVentas.executeQuery().next()) {
                movimientoCajaTB = new MovimientoCajaTB();
                try (ResultSet result = statementVentas.executeQuery()) {
                    while (result.next()) {
                        movimientoCajaTB.setSaldo(movimientoCajaTB.getSaldo() + result.getDouble("Saldo"));
                    }
                }
            }
        } catch (SQLException ex) {

        } finally {
            try {
                if (statementVentas != null) {
                    statementVentas.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return movimientoCajaTB;
    }

    public static MovimientoCajaTB DevolucionesEfectivo(String idCaja) {
        MovimientoCajaTB movimientoCajaTB = null;
        PreparedStatement statementVentas = null;
        try {
            DBUtil.dbConnect();
            statementVentas = DBUtil.getConnection().prepareStatement("SELECT * FROM MovimientoCajaTB WHERE IdCaja = ? AND Movimiento = ?");
            statementVentas.setString(1, idCaja);
            statementVentas.setString(2, "VENCAN");
            if (statementVentas.executeQuery().next()) {
                movimientoCajaTB = new MovimientoCajaTB();
                try (ResultSet result = statementVentas.executeQuery()) {
                    while (result.next()) {
                        movimientoCajaTB.setSaldo(movimientoCajaTB.getSaldo() + result.getDouble("Saldo"));
                    }
                }
            }
        } catch (SQLException ex) {

        } finally {
            try {
                if (statementVentas != null) {
                    statementVentas.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return movimientoCajaTB;
    }

    public static String AperturarCaja_Movimiento(CajaTB cajaTB, MovimientoCajaTB movimientoCajaTB) {
        String result = "";
        CallableStatement statementCodigoCaja = null;
        PreparedStatement statementCaja = null;
        PreparedStatement statementMovimiento = null;
        final String queryCaja = "INSERT INTO CajaTB(IdCaja,FechaApertura,FechaCierre,HoraApertura,HoraCierre,Estado,IdUsuario,Contado,Calculado,Diferencia,FechaRegistro,HoraRegistro)VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        final String queryMovimiento = "INSERT INTO MovimientoCajaTB(IdCaja,IdUsuario,FechaMovimiento,HoraMovimiento,Comentario,Movimiento,Entrada,Salidas,Saldo)VALUES(?,?,?,?,?,?,?,?,?)";
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            statementCodigoCaja = DBUtil.getConnection().prepareCall("{? = call Fc_Caja_Codigo_Alfanumerico()}");
            statementCodigoCaja.registerOutParameter(1, java.sql.Types.VARCHAR);
            statementCodigoCaja.execute();
            String idCaja = statementCodigoCaja.getString(1);

            statementCaja = DBUtil.getConnection().prepareStatement(queryCaja);
            statementCaja.setString(1, idCaja);
            statementCaja.setString(2, cajaTB.getFechaApertura());
            statementCaja.setString(3, cajaTB.getFechaApertura());
            statementCaja.setString(4, cajaTB.getHoraApertura());
            statementCaja.setString(5, cajaTB.getHoraApertura());
            statementCaja.setBoolean(6, cajaTB.isEstado());
            statementCaja.setString(7, cajaTB.getIdUsuario());
            statementCaja.setDouble(8, cajaTB.getContado());
            statementCaja.setDouble(9, cajaTB.getCalculado());
            statementCaja.setDouble(10, cajaTB.getDiferencia());
            statementCaja.setString(11, cajaTB.getFechaRegistro());
            statementCaja.setString(12, cajaTB.getHoraRegistro());
            statementCaja.addBatch();

            statementMovimiento = DBUtil.getConnection().prepareStatement(queryMovimiento);
            statementMovimiento.setString(1, idCaja);
            statementMovimiento.setString(2, movimientoCajaTB.getIdUsuario());
            statementMovimiento.setString(3, movimientoCajaTB.getFechaMovimiento());
            statementMovimiento.setString(4, movimientoCajaTB.getHoraMovimiento());
            statementMovimiento.setString(5, movimientoCajaTB.getComentario());
            statementMovimiento.setString(6, movimientoCajaTB.getMovimiento());
            statementMovimiento.setDouble(7, movimientoCajaTB.getEntrada());
            statementMovimiento.setDouble(8, movimientoCajaTB.getSalidas());
            statementMovimiento.setDouble(9, movimientoCajaTB.getSaldo());
            statementMovimiento.addBatch();

            statementCaja.executeBatch();
            statementMovimiento.executeBatch();
            DBUtil.getConnection().commit();
            Session.CAJA_ID = idCaja;
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
                if (statementMovimiento != null) {
                    statementMovimiento.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                result = ex.getLocalizedMessage();
            }
        }
        return result;
    }

    public static String Registrar_Movimiento(MovimientoCajaTB movimientoCajaTB) {
        String result = "";
        PreparedStatement statementMovimiento = null;
        final String queryMovimiento = "INSERT INTO MovimientoCajaTB(IdCaja,IdUsuario,FechaMovimiento,Comentario,Movimiento,Entrada,Salidas,Saldo)VALUES(?,?,?,?,?,?,?,?)";
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);

            statementMovimiento = DBUtil.getConnection().prepareStatement(queryMovimiento);
            statementMovimiento.setString(1, movimientoCajaTB.getIdCaja());
            statementMovimiento.setString(2, movimientoCajaTB.getIdUsuario());
            statementMovimiento.setTimestamp(3, Timestamp.valueOf(movimientoCajaTB.getFechaMovimiento()));
            statementMovimiento.setString(4, movimientoCajaTB.getComentario());
            statementMovimiento.setString(5, movimientoCajaTB.getMovimiento());
            statementMovimiento.setDouble(6, movimientoCajaTB.getEntrada());
            statementMovimiento.setDouble(7, movimientoCajaTB.getSalidas());
            statementMovimiento.setDouble(8, movimientoCajaTB.getSaldo());
            statementMovimiento.addBatch();

            statementMovimiento.executeBatch();
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
                if (statementMovimiento != null) {
                    statementMovimiento.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                result = ex.getLocalizedMessage();
            }
        }
        return result;
    }

    public static ObservableList<MovimientoCajaTB> ListarCajasAperturadas(String idCaja) {
        PreparedStatement statementLista = null;
        ObservableList<MovimientoCajaTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            statementLista = DBUtil.getConnection().prepareStatement("{call Sp_Lista_Movimiento_Caja_ById(?)}");
            statementLista.setString(1, idCaja);
            double saldoActual;
            double saldoTotal = 0;
            try (ResultSet result = statementLista.executeQuery()) {
                while (result.next()) {
                    MovimientoCajaTB movimientoCajaTB = new MovimientoCajaTB();
                    movimientoCajaTB.setFechaMovimiento(result.getDate("FechaMovimiento").toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
                    movimientoCajaTB.setHoraMovimiento(result.getTime("HoraMovimiento").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                    movimientoCajaTB.setComentario(result.getString("Comentario"));
                    movimientoCajaTB.setMovimiento(result.getString("Movimiento"));
                    movimientoCajaTB.setEntrada(result.getDouble("Entrada"));
                    movimientoCajaTB.setSalidas(result.getDouble("Salidas"));

                    saldoActual = movimientoCajaTB.getEntrada()-movimientoCajaTB.getSalidas();
                    saldoTotal = saldoTotal + saldoActual;

                    movimientoCajaTB.setSaldo(saldoTotal);
                    empList.add(movimientoCajaTB);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Funacion ListarCajasAperturadas:" + ex.getLocalizedMessage());
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

}
