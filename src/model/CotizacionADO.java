package model;

import controller.tools.Tools;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;

public class CotizacionADO {

    public static String[] GuardarCotizacion(CotizacionTB cotizacionTB) {
        String result[] = new String[2];
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            CallableStatement statementCodigoCotizacion = null;
            PreparedStatement statementValidacion = null;
            PreparedStatement statementCotizacion = null;
            PreparedStatement statementDetalleCotizacion = null;
            PreparedStatement statementDetalleCotizacionBorrar = null;
            try {
                
                DBUtil.getConnection().setAutoCommit(false);

                statementValidacion = DBUtil.getConnection().prepareStatement("SELECT * FROM CotizacionTB WHERE IdCotizacion = ?");
                statementValidacion.setString(1, cotizacionTB.getIdCotizacion());
                if (statementValidacion.executeQuery().next()) {

                    statementCotizacion = DBUtil.getConnection().prepareStatement("UPDATE CotizacionTB SET "
                            + "IdCliente=?"
                            + ",IdVendedor=?"
                            + ",IdMoneda=?"
                            + ",FechaCotizacion=?"
                            + ",HoraCotizacion=?"
                            + ",FechaVencimiento=?"
                            + ",HoraVencimiento=?"
                            + ",SubTotal=?"
                            + ",Descuento=?"
                            + ",Impuesto=?"
                            + ",Total=?"
                            + ",Estado=?"
                            + ",Observaciones=? "
                            + "WHERE IdCotizacion = ?");

                    statementCotizacion.setString(1, cotizacionTB.getIdCliente());
                    statementCotizacion.setString(2, cotizacionTB.getIdVendedor());
                    statementCotizacion.setInt(3, cotizacionTB.getIdMoneda());
                    statementCotizacion.setString(4, cotizacionTB.getFechaCotizacion());
                    statementCotizacion.setString(5, cotizacionTB.getHoraCotizacion());
                    statementCotizacion.setString(6, cotizacionTB.getFechaVencimiento());
                    statementCotizacion.setString(7, cotizacionTB.getHoraVencimiento());
                    statementCotizacion.setDouble(8, cotizacionTB.getSubTotal());
                    statementCotizacion.setDouble(9, cotizacionTB.getDescuento());
                    statementCotizacion.setDouble(10, cotizacionTB.getImpuesto());
                    statementCotizacion.setDouble(11, cotizacionTB.getTotal());
                    statementCotizacion.setShort(12, cotizacionTB.getEstado());
                    statementCotizacion.setString(13, cotizacionTB.getObservaciones());
                    statementCotizacion.setString(14, cotizacionTB.getIdCotizacion());
                    statementCotizacion.addBatch();

                    statementDetalleCotizacionBorrar = DBUtil.getConnection().prepareStatement("DELETE FROM DetalleCotizacionTB WHERE IdCotizacion = ?");
                    statementDetalleCotizacionBorrar.setString(1, cotizacionTB.getIdCotizacion());
                    statementDetalleCotizacionBorrar.addBatch();
                    statementDetalleCotizacionBorrar.executeBatch();

                    statementDetalleCotizacion = DBUtil.getConnection().prepareStatement("INSERT INTO DetalleCotizacionTB"
                            + "(IdCotizacion"
                            + ",IdSuministro"
                            + ",Cantidad"
                            + ",Precio"
                            + ",Descuento"
                            + ",IdImpuesto)"
                            + "VALUES(?,?,?,?,?,?)");

                    for (DetalleCotizacionTB detalleCotizacionTB : cotizacionTB.getDetalleCotizacionTBs()) {
                        statementDetalleCotizacion.setString(1, cotizacionTB.getIdCotizacion());
                        statementDetalleCotizacion.setString(2, detalleCotizacionTB.getIdSuministros());
                        statementDetalleCotizacion.setDouble(3, detalleCotizacionTB.getCantidad());
                        statementDetalleCotizacion.setDouble(4, detalleCotizacionTB.getPrecio());
                        statementDetalleCotizacion.setDouble(5, detalleCotizacionTB.getDescuento());
                        statementDetalleCotizacion.setInt(6, detalleCotizacionTB.getIdImpuesto());
                        statementDetalleCotizacion.addBatch();
                    }

                    statementCotizacion.executeBatch();
                    statementDetalleCotizacion.executeBatch();
                    DBUtil.getConnection().commit();
                    result[0] = "1";
                    result[1] = cotizacionTB.getIdCotizacion();
                } else {

                    statementCodigoCotizacion = DBUtil.getConnection().prepareCall("{? = call Fc_Cotizacion_Codigo_Alfanumerico()}");
                    statementCodigoCotizacion.registerOutParameter(1, java.sql.Types.VARCHAR);
                    statementCodigoCotizacion.execute();
                    String idCotizacion = statementCodigoCotizacion.getString(1);

                    statementCotizacion = DBUtil.getConnection().prepareStatement("INSERT INTO CotizacionTB"
                            + "(IdCotizacion"
                            + ",IdCliente"
                            + ",IdVendedor"
                            + ",IdMoneda"
                            + ",FechaCotizacion"
                            + ",HoraCotizacion"
                            + ",FechaVencimiento"
                            + ",HoraVencimiento"
                            + ",SubTotal"
                            + ",Descuento"
                            + ",Impuesto"
                            + ",Total"
                            + ",Estado"
                            + ",Observaciones)"
                            + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                    statementCotizacion.setString(1, idCotizacion);
                    statementCotizacion.setString(2, cotizacionTB.getIdCliente());
                    statementCotizacion.setString(3, cotizacionTB.getIdVendedor());
                    statementCotizacion.setInt(4, cotizacionTB.getIdMoneda());
                    statementCotizacion.setString(5, cotizacionTB.getFechaCotizacion());
                    statementCotizacion.setString(6, cotizacionTB.getHoraCotizacion());
                    statementCotizacion.setString(7, cotizacionTB.getFechaVencimiento());
                    statementCotizacion.setString(8, cotizacionTB.getHoraVencimiento());
                    statementCotizacion.setDouble(9, cotizacionTB.getSubTotal());
                    statementCotizacion.setDouble(10, cotizacionTB.getDescuento());
                    statementCotizacion.setDouble(11, cotizacionTB.getImpuesto());
                    statementCotizacion.setDouble(12, cotizacionTB.getTotal());
                    statementCotizacion.setShort(13, cotizacionTB.getEstado());
                    statementCotizacion.setString(14, cotizacionTB.getObservaciones());
                    statementCotizacion.addBatch();

                    statementDetalleCotizacion = DBUtil.getConnection().prepareStatement("INSERT INTO DetalleCotizacionTB"
                            + "(IdCotizacion"
                            + ",IdSuministro"
                            + ",Cantidad"
                            + ",Precio"
                            + ",Descuento"
                            + ",IdImpuesto)"
                            + "VALUES(?,?,?,?,?,?)");

                    for (DetalleCotizacionTB detalleCotizacionTB : cotizacionTB.getDetalleCotizacionTBs()) {
                        statementDetalleCotizacion.setString(1, idCotizacion);
                        statementDetalleCotizacion.setString(2, detalleCotizacionTB.getIdSuministros());
                        statementDetalleCotizacion.setDouble(3, detalleCotizacionTB.getCantidad());
                        statementDetalleCotizacion.setDouble(4, detalleCotizacionTB.getPrecio());
                        statementDetalleCotizacion.setDouble(5, detalleCotizacionTB.getDescuento());
                        statementDetalleCotizacion.setInt(6, detalleCotizacionTB.getIdImpuesto());
                        statementDetalleCotizacion.addBatch();
                    }

                    statementCotizacion.executeBatch();
                    statementDetalleCotizacion.executeBatch();
                    DBUtil.getConnection().commit();
                    result[0] = "0";
                    result[1] = idCotizacion;
                }
            } catch (SQLException ex) {
                try {
                    DBUtil.getConnection().rollback();
                } catch (SQLException e) {

                }
                result[0] = "2";
                result[1] = ex.getLocalizedMessage();
            } finally {
                try {
                    if (statementCodigoCotizacion != null) {
                        statementCodigoCotizacion.close();
                    }
                    if (statementValidacion != null) {
                        statementValidacion.close();
                    }
                    if (statementCotizacion != null) {
                        statementCotizacion.close();
                    }
                    if (statementDetalleCotizacion != null) {
                        statementDetalleCotizacion.close();
                    }
                    if (statementDetalleCotizacionBorrar != null) {
                        statementDetalleCotizacionBorrar.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException e) {

                }
            }
        } else {
            result[0] = "3";
            result[1] = "No se pudo conectar con el servidor, intente nuevamente";
        }
        return result;
    }

    public static ObservableList<CotizacionTB> CargarCotizacion(short opcion, String buscar, String fechaInicio, String fechaFinal) {
        ObservableList<CotizacionTB> cotizacionTBs = FXCollections.observableArrayList();
        PreparedStatement statementCotizaciones = null;
        try {
            DBUtil.dbConnect();
            statementCotizaciones = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_Cotizacion(?,?,?,?)}");
            statementCotizaciones.setShort(1, opcion);
            statementCotizaciones.setString(2, buscar);
            statementCotizaciones.setString(3, fechaInicio);
            statementCotizaciones.setString(4, fechaFinal);
            try (ResultSet result = statementCotizaciones.executeQuery()) {
                while (result.next()) {
                    CotizacionTB cotizacionTB = new CotizacionTB();
                    cotizacionTB.setId(result.getRow());
                    cotizacionTB.setIdCotizacion(result.getString("IdCotizacion"));
                    cotizacionTB.setFechaCotizacion(result.getDate("FechaCotizacion").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    cotizacionTB.setHoraCotizacion(result.getTime("HoraCotizacion").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                    cotizacionTB.setEmpleadoTB(new EmpleadoTB(result.getString("Apellidos"), result.getString("Nombres")));
                    cotizacionTB.setClienteTB(new ClienteTB(result.getString("Informacion")));
                    cotizacionTB.setMonedaTB(new MonedaTB(result.getString("SimboloMoneda")));
                    cotizacionTB.setTotal(result.getDouble("Total"));
                    cotizacionTBs.add(cotizacionTB);
                }
            }
        } catch (SQLException ex) {

        } finally {
            try {
                if (statementCotizaciones != null) {
                    statementCotizaciones.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return cotizacionTBs;
    }

    public static ArrayList<Object> CargarCotizacionVenta(String idCotizacion) {
        ArrayList<Object> objects = new ArrayList();
        CotizacionTB cotizacionTB = null;
        ObservableList<SuministroTB> cotizacionTBs = FXCollections.observableArrayList();
        PreparedStatement statementCotizacione = null;
        PreparedStatement statementDetalleCotizacione = null;
        try {
            DBUtil.dbConnect();

            statementCotizacione = DBUtil.getConnection().prepareStatement("{CALL Sp_Obtener_Cotizacion_ById(?)}");
            statementCotizacione.setString(1, idCotizacion);
            try (ResultSet result = statementCotizacione.executeQuery()) {
                if (result.next()) {
                    cotizacionTB = new CotizacionTB();
                    cotizacionTB.setId(result.getRow());
                    cotizacionTB.setIdCotizacion(result.getString("IdCotizacion"));
                    cotizacionTB.setClienteTB(new ClienteTB(result.getString("IdCliente"), result.getInt("TipoDocumento"), result.getString("NumeroDocumento"), result.getString("Informacion"), result.getString("Celular"), result.getString("Email"), result.getString("Direccion")));
                    cotizacionTB.setIdMoneda(result.getInt("IdMoneda"));
                    cotizacionTB.setObservaciones(result.getString("Observaciones"));
                }
            }

            objects.add(cotizacionTB);

            statementDetalleCotizacione = DBUtil.getConnection().prepareStatement("{CALL Sp_Obtener_Detalle_Cotizacion_ById(?)}");
            statementDetalleCotizacione.setString(1, idCotizacion);
            try (ResultSet result = statementDetalleCotizacione.executeQuery()) {
                while (result.next()) {
                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setIdSuministro(result.getString("IdSuministro"));
                    suministroTB.setClave(result.getString("Clave"));
                    suministroTB.setNombreMarca(result.getString("NombreMarca"));
                    suministroTB.setCantidad(result.getDouble("Cantidad"));
                    suministroTB.setCostoCompra(result.getDouble("PrecioCompra"));

                    double precio = result.getDouble("Precio");
                    double descuento = result.getDouble("Descuento");
                    double porcentajeRestante = precio * (descuento / 100.00);
                    double preciocalculado = precio - porcentajeRestante;

                    suministroTB.setDescuento(result.getDouble("Descuento"));
                    suministroTB.setDescuentoCalculado(porcentajeRestante);
                    suministroTB.setDescuentoSumado(porcentajeRestante * suministroTB.getCantidad());

                    suministroTB.setPrecioVentaGeneralUnico(precio);
                    suministroTB.setPrecioVentaGeneralReal(preciocalculado);
                    suministroTB.setPrecioVentaGeneralAuxiliar(preciocalculado);

                    suministroTB.setImpuestoOperacion(result.getInt("Operacion"));
                    suministroTB.setImpuestoId(result.getInt("Impuesto"));
                    suministroTB.setImpuestoNombre(result.getString("ImpuestoNombre"));
                    suministroTB.setImpuestoValor(result.getDouble("Valor"));

                    double impuesto = Tools.calculateTax(suministroTB.getImpuestoValor(), suministroTB.getPrecioVentaGeneralReal());

                    suministroTB.setImpuestoSumado(suministroTB.getCantidad() * impuesto);
                    suministroTB.setPrecioVentaGeneral(suministroTB.getPrecioVentaGeneralReal() + impuesto);

                    suministroTB.setSubImporte(suministroTB.getPrecioVentaGeneralUnico() * suministroTB.getCantidad());
                    suministroTB.setSubImporteDescuento(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
                    suministroTB.setTotalImporte(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());

                    suministroTB.setInventario(result.getBoolean("Inventario"));
                    suministroTB.setUnidadVenta(result.getInt("UnidadVenta"));
                    suministroTB.setValorInventario(result.getShort("ValorInventario"));

                    Button button = new Button("X");
                    button.getStyleClass().add("buttonDark");

                    suministroTB.setRemover(button);
                    cotizacionTBs.add(suministroTB);
                }
            }

            objects.add(cotizacionTBs);

        } catch (SQLException ex) {
            Tools.println(ex.getLocalizedMessage());
        } finally {
            try {
                if (statementCotizacione != null) {
                    statementCotizacione.close();
                }
                if (statementDetalleCotizacione != null) {
                    statementDetalleCotizacione.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return objects;
    }

    public static ArrayList<Object> CargarCotizacionReporte(String idCotizacion) {
        ArrayList<Object> objects = new ArrayList();
        CotizacionTB cotizacionTB = null;
        ObservableList<SuministroTB> cotizacionTBs = FXCollections.observableArrayList();
        PreparedStatement statementCotizacione = null;
        PreparedStatement statementDetalleCotizacione = null;
        try {
            DBUtil.dbConnect();
            statementCotizacione = DBUtil.getConnection().prepareStatement("{CALL Sp_Obtener_Cotizacion_Reporte_ById(?)}");
            statementCotizacione.setString(1, idCotizacion);
            try (ResultSet result = statementCotizacione.executeQuery()) {
                if (result.next()) {
                    cotizacionTB = new CotizacionTB();
                    cotizacionTB.setId(result.getRow());
                    cotizacionTB.setIdCotizacion(idCotizacion);
                    cotizacionTB.setFechaCotizacion(result.getDate("FechaCotizacion").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    cotizacionTB.setHoraCotizacion(result.getTime("HoraCotizacion").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                    cotizacionTB.setEmpleadoTB(new EmpleadoTB(result.getString("Apellidos"), result.getString("Nombres")));
                    cotizacionTB.setClienteTB(new ClienteTB(result.getString("IdCliente"), result.getInt("TipoDocumento"), result.getString("NumeroDocumento"), result.getString("Informacion"), result.getString("Telefono"), result.getString("Celular"), result.getString("Email"), result.getString("Direccion")));
                    cotizacionTB.setMonedaTB(new MonedaTB(result.getString("Nombre"), result.getString("Simbolo")));
                    cotizacionTB.setObservaciones(result.getString("Observaciones"));
                }
            }

            statementDetalleCotizacione = DBUtil.getConnection().prepareStatement("{CALL Sp_Obtener_Detalle_Cotizacion_Reporte_ById(?)}");
            statementDetalleCotizacione.setString(1, idCotizacion);
            try (ResultSet result = statementDetalleCotizacione.executeQuery()) {
                while (result.next()) {
                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setId(result.getRow());
                    suministroTB.setIdSuministro(result.getString("IdSuministro"));
                    suministroTB.setClave(result.getString("Clave"));
                    suministroTB.setNombreMarca(result.getString("NombreMarca"));
                    suministroTB.setUnidadCompraName(result.getString("UnidadCompraNombre"));
                    suministroTB.setCantidad(result.getDouble("Cantidad"));
                    suministroTB.setCostoCompra(result.getDouble("PrecioCompra"));

                    double precio = result.getDouble("Precio");
                    double descuento = result.getDouble("Descuento");
                    double porcentajeRestante = precio * (descuento / 100.00);
                    double preciocalculado = precio - porcentajeRestante;

                    suministroTB.setDescuento(result.getDouble("Descuento"));
                    suministroTB.setDescuentoCalculado(porcentajeRestante);
                    suministroTB.setDescuentoSumado(porcentajeRestante * suministroTB.getCantidad());

                    suministroTB.setPrecioVentaGeneralUnico(precio);
                    suministroTB.setPrecioVentaGeneralReal(preciocalculado);
                    suministroTB.setPrecioVentaGeneralAuxiliar(preciocalculado);

                    suministroTB.setImpuestoOperacion(result.getInt("Operacion"));
                    suministroTB.setImpuestoId(result.getInt("Impuesto"));
                    suministroTB.setImpuestoNombre(result.getString("ImpuestoNombre"));
                    suministroTB.setImpuestoValor(result.getDouble("Valor"));

                    double impuesto = Tools.calculateTax(suministroTB.getImpuestoValor(), suministroTB.getPrecioVentaGeneralReal());

                    suministroTB.setImpuestoSumado(suministroTB.getCantidad() * impuesto);
                    suministroTB.setPrecioVentaGeneral(suministroTB.getPrecioVentaGeneralReal() + impuesto);

                    suministroTB.setSubImporte(suministroTB.getPrecioVentaGeneralUnico() * suministroTB.getCantidad());
                    suministroTB.setSubImporteDescuento(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());
                    suministroTB.setTotalImporte(suministroTB.getCantidad() * suministroTB.getPrecioVentaGeneralReal());

                    suministroTB.setInventario(result.getBoolean("Inventario"));
                    suministroTB.setUnidadVenta(result.getInt("UnidadVenta"));
                    suministroTB.setValorInventario(result.getShort("ValorInventario"));

                    Button button = new Button("X");
                    button.getStyleClass().add("buttonDark");

                    suministroTB.setRemover(button);
                    cotizacionTBs.add(suministroTB);
                }
            }
            objects.add(cotizacionTB);
            objects.add(cotizacionTBs);
        } catch (SQLException ex) {
            Tools.println(ex.getLocalizedMessage());
        } finally {
            try {
                if (statementCotizacione != null) {
                    statementCotizacione.close();
                }
                if (statementDetalleCotizacione != null) {
                    statementDetalleCotizacione.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
        return objects;
    }

}
