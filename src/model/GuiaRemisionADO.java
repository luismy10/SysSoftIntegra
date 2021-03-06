package model;

import controller.tools.Tools;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GuiaRemisionADO {

    public static String InsertarGuiaRemision(GuiaRemisionTB guiaRemisionTB) {

        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            CallableStatement serie_numeracion = null;
            CallableStatement statementIdGuiaRemision = null;
            PreparedStatement statementGuiaRemision = null;
            PreparedStatement statementGuiaRemisionDetalle = null;
            PreparedStatement comprobante = null;

            try {
                DBUtil.getConnection().setAutoCommit(false);

                serie_numeracion = DBUtil.getConnection().prepareCall("{? = call Fc_Serie_Numero(?)}");
                serie_numeracion.registerOutParameter(1, java.sql.Types.VARCHAR);
                serie_numeracion.setInt(2, guiaRemisionTB.getIdComprobante());
                serie_numeracion.execute();
                String[] id_comprabante = serie_numeracion.getString(1).split("-");

                statementIdGuiaRemision = DBUtil.getConnection().prepareCall("{? = call Fc_GuiaRemision_Codigo_Alfanumerico()}");
                statementIdGuiaRemision.registerOutParameter(1, java.sql.Types.VARCHAR);
                statementIdGuiaRemision.execute();
                String idGuiaRemision = statementIdGuiaRemision.getString(1);

                statementGuiaRemision = DBUtil.getConnection().prepareStatement("INSERT INTO GuiaRemisionTB"
                        + "(IdGuiaRemision"
                        + ",Comprobante"
                        + ",Serie"
                        + ",Numeracion"
                        + ",IdCliente"
                        + ",IdVendedor"
                        + ",Email"
                        + ",IdMotivoTraslado"
                        + ",IdModalidadTraslado"
                        + ",FechaTraslado"
                        + ",HoraTraslado"
                        + ",Peso"
                        + ",NumeroBultos"
                        + ",IdTipoDocumentoConducto"
                        + ",NumeroDocumentoConductor"
                        + ",NombresConductor"
                        + ",TelefonoConducto"
                        + ",NumeroPlaca"
                        + ",MarcaVehiculo"
                        + ",DireccionPartida"
                        + ",IdUbigeoPartida"
                        + ",DireccionLlegada"
                        + ",IdUbigeoLlegada"
                        + ",IdTipoComprobante"
                        + ",SerieFactura"
                        + ",NumeracionFactura)"
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                statementGuiaRemision.setObject(1, idGuiaRemision);
                statementGuiaRemision.setObject(2, guiaRemisionTB.getIdComprobante());
                statementGuiaRemision.setObject(3, id_comprabante[0]);
                statementGuiaRemision.setObject(4, id_comprabante[1]);
                statementGuiaRemision.setObject(5, guiaRemisionTB.getIdCliente());
                statementGuiaRemision.setObject(6, guiaRemisionTB.getIdVendedor());
                statementGuiaRemision.setObject(7, guiaRemisionTB.getEmail());
                statementGuiaRemision.setObject(8, guiaRemisionTB.getIdMotivoTraslado());
                statementGuiaRemision.setObject(9, guiaRemisionTB.getIdModalidadTraslado());
                statementGuiaRemision.setObject(10, guiaRemisionTB.getFechaTraslado());
                statementGuiaRemision.setObject(11, Tools.getTime());
                statementGuiaRemision.setObject(12, guiaRemisionTB.getPesoBruto());
                statementGuiaRemision.setObject(13, guiaRemisionTB.getNumeroBultos());
                statementGuiaRemision.setObject(14, guiaRemisionTB.getTipoDocumentoConducto());
                statementGuiaRemision.setObject(15, guiaRemisionTB.getNumeroConductor());
                statementGuiaRemision.setObject(16, guiaRemisionTB.getNombreConductor());
                statementGuiaRemision.setObject(17, guiaRemisionTB.getTelefonoCelularConducto());
                statementGuiaRemision.setObject(18, guiaRemisionTB.getNumeroPlaca());
                statementGuiaRemision.setObject(19, guiaRemisionTB.getMarcaVehiculo());
                statementGuiaRemision.setObject(20, guiaRemisionTB.getDireccionPartida());
                statementGuiaRemision.setObject(21, guiaRemisionTB.getIdUbigeoPartida());
                statementGuiaRemision.setObject(22, guiaRemisionTB.getDireccionLlegada());
                statementGuiaRemision.setObject(23, guiaRemisionTB.getIdUbigeoLlegada());
                statementGuiaRemision.setObject(24, guiaRemisionTB.getIdTipoComprobanteFactura());
                statementGuiaRemision.setObject(25, guiaRemisionTB.getSerieFactura());
                statementGuiaRemision.setObject(26, guiaRemisionTB.getNumeracionFactura());
                statementGuiaRemision.addBatch();

                statementGuiaRemisionDetalle = DBUtil.getConnection().prepareStatement("INSERT INTO GuiaRemisionDetalleTB"
                        + "(IdGuiaRemision"
                        + ",IdSuministro"
                        + ",Codigo"
                        + ",Descripcion"
                        + ",Unidad"
                        + ",Cantidad"
                        + ",Peso)"
                        + "VALUES(?,?,?,?,?,?,?)");
                ObservableList<GuiaRemisionDetalleTB> list = guiaRemisionTB.getListGuiaRemisionDetalle();
                for (GuiaRemisionDetalleTB guiad : list) {
                    statementGuiaRemisionDetalle.setString(1, idGuiaRemision);
                    statementGuiaRemisionDetalle.setString(2, guiad.getIdSuministro());
                    statementGuiaRemisionDetalle.setString(3, guiad.getTxtCodigo().getText());
                    statementGuiaRemisionDetalle.setString(4, guiad.getTxtDescripcion().getText());
                    statementGuiaRemisionDetalle.setString(5, guiad.getTxtUnidad().getText());
                    statementGuiaRemisionDetalle.setDouble(6, Double.parseDouble(guiad.getTxtCantidad().getText()));
                    statementGuiaRemisionDetalle.setDouble(7, Double.parseDouble(guiad.getTxtPeso().getText()));
                    statementGuiaRemisionDetalle.addBatch();
                }

                comprobante = DBUtil.getConnection().prepareStatement("INSERT INTO ComprobanteTB(IdTipoDocumento,Serie,Numeracion,FechaRegistro)VALUES(?,?,?,?)");

                comprobante.setInt(1, guiaRemisionTB.getIdComprobante());
                comprobante.setString(2, id_comprabante[0]);
                comprobante.setString(3, id_comprabante[1]);
                comprobante.setTimestamp(4, Tools.getDateHour());
                comprobante.addBatch();

                statementGuiaRemision.executeBatch();
                statementGuiaRemisionDetalle.executeBatch();
                comprobante.executeBatch();
                DBUtil.getConnection().commit();
                return "register/" + idGuiaRemision;
            } catch (SQLException ex) {
                try {
                    DBUtil.getConnection().rollback();
                    return ex.getLocalizedMessage() + "/";
                } catch (SQLException e) {
                    return e.getLocalizedMessage() + "/";
                }
            } finally {
                try {
                    if (statementIdGuiaRemision != null) {
                        statementIdGuiaRemision.close();
                    }
                    if (statementGuiaRemision != null) {
                        statementGuiaRemision.close();
                    }
                    if (statementGuiaRemisionDetalle != null) {
                        statementGuiaRemisionDetalle.close();
                    }
                    if (comprobante != null) {
                        comprobante.close();
                    }
                } catch (SQLException ex) {

                }
            }
        } else {
            return "No se puedo completar el proceso por un problema de conexi??n intente nuevamente o comun??quese con su proveedor./";
        }

    }

    public static ObservableList<GuiaRemisionTB> CargarGuiaRemision(short opcion, String buscar, String fechaInicio, String fechaFinal) {
        ObservableList<GuiaRemisionTB> remisionTBs = FXCollections.observableArrayList();
        PreparedStatement statementCotizaciones = null;
        try {
            DBUtil.dbConnect();
            statementCotizaciones = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_GuiaRemision(?,?,?,?)}");
            statementCotizaciones.setShort(1, opcion);
            statementCotizaciones.setString(2, buscar);
            statementCotizaciones.setString(3, fechaInicio);
            statementCotizaciones.setString(4, fechaFinal);
            try (ResultSet result = statementCotizaciones.executeQuery()) {
                while (result.next()) {
                    GuiaRemisionTB guiaRemisionTB = new GuiaRemisionTB();
                    guiaRemisionTB.setId(result.getRow());
                    guiaRemisionTB.setIdGuiaRemision(result.getString("IdGuiaRemision"));
                    guiaRemisionTB.setEmpleadoTB(new EmpleadoTB(result.getString("NumeroDocumentoVendedor"), result.getString("ApellidosVendedor"), result.getString("NombresVendedor")));
                    guiaRemisionTB.setSerie(result.getString("Serie"));
                    guiaRemisionTB.setNumeracion(result.getString("Numeracion"));
                    guiaRemisionTB.setFechaTraslado(result.getDate("FechaTraslado").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    guiaRemisionTB.setHoraTraslado(result.getTime("HoraTraslado").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                    guiaRemisionTB.setClienteTB(new ClienteTB(result.getString("NumeroDocumento"), result.getString("Informacion")));
                    remisionTBs.add(guiaRemisionTB);
                }
            }
        } catch (SQLException ex) {
            Tools.println("Error en sql: " + ex.getLocalizedMessage());
        } finally {
            try {
                if (statementCotizaciones != null) {
                    statementCotizaciones.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return remisionTBs;
    }

    public static GuiaRemisionTB CargarGuiaRemisionReporte(String idGuiaRemision) {
        GuiaRemisionTB guiaRemisionTB = null;

        PreparedStatement statementGuiaRemision = null;
        PreparedStatement statementGuiaRemisionDetalle = null;
        try {
            DBUtil.dbConnect();
            statementGuiaRemision = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_GuiaRemision_ById(?)}");
            statementGuiaRemision.setString(1, idGuiaRemision);
            try (ResultSet result = statementGuiaRemision.executeQuery()) {
                if (result.next()) {
                    guiaRemisionTB = new GuiaRemisionTB();
                    guiaRemisionTB.setIdGuiaRemision(result.getString("IdGuiaRemision").toUpperCase());
                    guiaRemisionTB.setSerie(result.getString("Serie").toUpperCase());
                    guiaRemisionTB.setNumeracion(result.getString("Numeracion").toUpperCase());
                    guiaRemisionTB.setClienteTB(new ClienteTB(result.getString("NumeroDocumento").toUpperCase(), result.getString("Informacion").toUpperCase(), result.getString("Celular"), result.getString("Email").toUpperCase(), result.getString("Direccion").toUpperCase()));
                    guiaRemisionTB.setMotivoTrasladoDescripcion(result.getString("MotivoTraslado").toUpperCase());
                    guiaRemisionTB.setModalidadTrasladDescripcion(result.getString("ModalidadTraslado").toUpperCase());
                    guiaRemisionTB.setFechaTraslado(result.getDate("FechaTraslado").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    guiaRemisionTB.setHoraTraslado(result.getTime("HoraTraslado").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                    guiaRemisionTB.setPesoBruto(result.getDouble("Peso"));
                    guiaRemisionTB.setNumeroBultos(result.getInt("NumeroBultos"));

                    guiaRemisionTB.setNumeroConductor(result.getString("NumeroDocumentoConductor").toUpperCase());
                    guiaRemisionTB.setNombreConductor(result.getString("NombresConductor").toUpperCase());
                    guiaRemisionTB.setTelefonoCelularConducto(result.getString("TelefonoConducto").toUpperCase());
                    guiaRemisionTB.setNumeroPlaca(result.getString("NumeroPlaca").toUpperCase());
                    guiaRemisionTB.setMarcaVehiculo(result.getString("MarcaVehiculo").toUpperCase());

                    guiaRemisionTB.setDireccionPartida(result.getString("DireccionPartida").toUpperCase());
                    guiaRemisionTB.setUbigeoPartidaDescripcion(result.getString("UbigeoPartida").toUpperCase());
                    guiaRemisionTB.setDireccionLlegada(result.getString("DireccionLlegada").toUpperCase());
                    guiaRemisionTB.setUbigeoLlegadaDescripcion(result.getString("UbigeoLlegada").toUpperCase());
                    guiaRemisionTB.setComprobanteAsociado(result.getString("ComprobanteAsociado"));
                    guiaRemisionTB.setSerieFactura(result.getString("SerieFactura").toUpperCase());
                    guiaRemisionTB.setNumeracionFactura(result.getString("NumeracionFactura").toUpperCase());

                    statementGuiaRemisionDetalle = DBUtil.getConnection().prepareStatement("SELECT * FROM GuiaRemisionDetalleTB WHERE IdGuiaRemision = ?");
                    statementGuiaRemisionDetalle.setString(1, idGuiaRemision);
                    try (ResultSet result1 = statementGuiaRemisionDetalle.executeQuery()) {
                        ObservableList<GuiaRemisionDetalleTB> observableList = FXCollections.observableArrayList();
                        while (result1.next()) {
                            GuiaRemisionDetalleTB guiaRemisionDetalleTB = new GuiaRemisionDetalleTB();
                            guiaRemisionDetalleTB.setId(result1.getRow());
                            guiaRemisionDetalleTB.setCodigo(result1.getString("Codigo"));
                            guiaRemisionDetalleTB.setDescripcion(result1.getString("Descripcion"));
                            guiaRemisionDetalleTB.setUnidad(result1.getString("Unidad"));
                            guiaRemisionDetalleTB.setCantidad(result1.getDouble("Cantidad"));
                            guiaRemisionDetalleTB.setPeso(result1.getDouble("Peso"));
                            observableList.add(guiaRemisionDetalleTB);
                        }
                        guiaRemisionTB.setListGuiaRemisionDetalle(observableList);
                    }
                }
            }

        } catch (SQLException ex) {
            Tools.println(ex.getLocalizedMessage());
        } finally {
            try {
                if (statementGuiaRemision != null) {
                    statementGuiaRemision.close();
                }
                if (statementGuiaRemisionDetalle != null) {
                    statementGuiaRemisionDetalle.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return guiaRemisionTB;
    }

}
