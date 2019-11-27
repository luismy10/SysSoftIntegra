package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TicketADO {

    public static String CrudTicket(TicketTB ticketTB) {
        String result = "";
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementTicket = null;
            PreparedStatement statementValidar = null;
            try {
                DBUtil.getConnection().setAutoCommit(false);
                statementValidar = DBUtil.getConnection().prepareStatement("SELECT idTicket FROM TicketTB WHERE idTicket = ?");
                statementValidar.setInt(1, ticketTB.getId());
                if (statementValidar.executeQuery().next()) {
                    statementValidar = DBUtil.getConnection().prepareStatement("SELECT nombre FROM TicketTB WHERE idTicket <> ? and nombre = ?");
                    statementValidar.setInt(1, ticketTB.getId());
                    statementValidar.setString(2, ticketTB.getNombreTicket());
                    if (statementValidar.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        result = "duplicate";
                    } else {
                        statementTicket = DBUtil.getConnection().prepareStatement("UPDATE TicketTB SET ruta = ? WHERE idTicket = ?");
                        statementTicket.setString(1, ticketTB.getRuta());
                        statementTicket.setInt(2, ticketTB.getId());
                        statementTicket.addBatch();

                        statementTicket.executeBatch();
                        DBUtil.getConnection().commit();
                        result = "updated";
                    }

                } else {
                    statementValidar = DBUtil.getConnection().prepareStatement("SELECT nombre FROM TicketTB WHERE nombre = ?");
                    statementValidar.setString(1, ticketTB.getNombreTicket());
                    if (statementValidar.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        result = "duplicate";
                    } else {
                        statementTicket = DBUtil.getConnection().prepareStatement("INSERT INTO TicketTB(nombre,tipo,predeterminado,ruta)VALUES(?,?,?,?)");
                        statementTicket.setString(1, ticketTB.getNombreTicket());
                        statementTicket.setInt(2, ticketTB.getTipo());
                        statementTicket.setBoolean(3, ticketTB.isPredeterminado());
                        statementTicket.setString(4, ticketTB.getRuta());
                        statementTicket.addBatch();

                        statementTicket.executeBatch();
                        DBUtil.getConnection().commit();
                        result = "registered";
                    }
                }

            } catch (SQLException ex) {
                try {
                    DBUtil.getConnection().rollback();
                } catch (SQLException exr) {
                    result = exr.getLocalizedMessage();
                }
                result = ex.getLocalizedMessage();
            } finally {
                try {
                    if (statementTicket != null) {
                        statementTicket.close();
                    }
                    if (statementValidar != null) {
                        statementValidar.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    result = ex.getLocalizedMessage();
                }
            }

        } else {
            result = "No se puedo conectac al servidor, intente nuevamente.";
        }
        return result;
    }

    public static ArrayList<TicketTB> ListTicket() {
        ArrayList<TicketTB> list = new ArrayList<>();
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementLista = null;
            ResultSet resultSet = null;
            try {
                statementLista = DBUtil.getConnection().prepareStatement("SELECT * FROM TicketTB");
                resultSet = statementLista.executeQuery();
                while (resultSet.next()) {
                    TicketTB ticketTB = new TicketTB();
                    ticketTB.setId(resultSet.getInt(1));
                    ticketTB.setNombreTicket(resultSet.getString(2));
                    ticketTB.setTipo(resultSet.getInt(3));
                    ticketTB.setPredeterminado(resultSet.getBoolean(4));
                    ticketTB.setRuta(resultSet.getString(5));
                    list.add(ticketTB);
                }
            } catch (SQLException ex) {

            } finally {
                try {
                    if (statementLista != null) {
                        statementLista.close();
                    }
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {

                }

            }
        }
        return list;
    }

    public static ArrayList<TicketTB> ListTipoTicket() {
        ArrayList<TicketTB> list = new ArrayList<>();
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementLista = null;
            ResultSet resultSet = null;
            try {
                statementLista = DBUtil.getConnection().prepareStatement("SELECT * FROM TipoTicketTB");
                resultSet = statementLista.executeQuery();
                while (resultSet.next()) {
                    TicketTB ticketTB = new TicketTB();
                    ticketTB.setId(resultSet.getInt(1));
                    ticketTB.setNombreTicket(resultSet.getString(2));
                    list.add(ticketTB);
                }
            } catch (SQLException ex) {

            } finally {
                try {
                    if (statementLista != null) {
                        statementLista.close();
                    }
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {

                }

            }
        }
        return list;
    }

    public static TicketTB GetTicketRuta(int id) {
        TicketTB ticketTB = null;
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementLista = null;
            ResultSet resultSet = null;
            try {
                statementLista = DBUtil.getConnection().prepareStatement("SELECT ruta FROM TicketTB WHERE idTicket = ?");
                statementLista.setInt(1, id);
                resultSet = statementLista.executeQuery();
                if (resultSet.next()) {
                    ticketTB = new TicketTB();
                    ticketTB.setRuta(resultSet.getString("ruta"));
                }
            } catch (SQLException ex) {

            } finally {
                try {
                    if (statementLista != null) {
                        statementLista.close();
                    }
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {

                }

            }
        }
        return ticketTB;
    }
}
