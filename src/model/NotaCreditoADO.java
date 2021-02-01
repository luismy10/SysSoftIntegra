package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class NotaCreditoADO {

    public static ArrayList<Object> ListarComprobanteParaNotaCredito(String comprobante) {
        PreparedStatement statementNotaCredito = null;
        try {
            DBUtil.dbConnect();

            ArrayList<Object> objects = new ArrayList();

            statementNotaCredito = DBUtil.getConnection().prepareStatement("SELECT IdTipoDocumento,Nombre FROM TipoDocumentoTB WHERE NotaCredito = 1");
            ArrayList<TipoDocumentoTB> arrayNotaCredito = new ArrayList<>();
            try (ResultSet resultSet = statementNotaCredito.executeQuery()) {
                while (resultSet.next()) {
                    arrayNotaCredito.add(new TipoDocumentoTB(resultSet.getInt("IdTipoDocumento"), resultSet.getString("Nombre")));
                }
            }

            statementNotaCredito = DBUtil.getConnection().prepareStatement("SELECT IdMoneda,Nombre,Predeterminado FROM MonedaTB");
            ArrayList<MonedaTB> arrayMoneda = new ArrayList<>();
            try (ResultSet resultSet = statementNotaCredito.executeQuery()) {
                while (resultSet.next()) {
                    arrayMoneda.add(new MonedaTB(resultSet.getInt("IdTipoDocumento"), resultSet.getString("Nombre")));
                }
            }

            statementNotaCredito = DBUtil.getConnection().prepareStatement("SELECT IdTipoDocumento,Nombre FROM TipoDocumentoTB");
            ArrayList<TipoDocumentoTB> arrayTipoComprobante = new ArrayList<>();
            try (ResultSet resultSet = statementNotaCredito.executeQuery()) {
                while (resultSet.next()) {
                    arrayTipoComprobante.add(new TipoDocumentoTB(resultSet.getInt("IdTipoDocumento"), resultSet.getString("Nombre")));
                }
            }

            statementNotaCredito = DBUtil.getConnection().prepareStatement("SELECT IdDetalle,Nombre FROM DetalleTB WHERE IdMantenimiento = '0019'");
            ArrayList<DetalleTB> arrayMotivoAnulacion = new ArrayList<>();
            try (ResultSet resultSet = statementNotaCredito.executeQuery()) {
                while (resultSet.next()) {
                    arrayMotivoAnulacion.add(new DetalleTB(resultSet.getInt("IdDetalle"), resultSet.getString("Nombre")));
                }
            }

            statementNotaCredito = DBUtil.getConnection().prepareStatement("SELECT IdDetalle,Nombre FROM DetalleTB WHERE IdMantenimiento = '0003'");
            ArrayList<DetalleTB> arrayTipoDocumento = new ArrayList<>();
            try (ResultSet resultSet = statementNotaCredito.executeQuery()) {
                while (resultSet.next()) {
                    arrayTipoDocumento.add(new DetalleTB(resultSet.getInt("IdDetalle"), resultSet.getString("Nombre")));
                }
            }

            statementNotaCredito = DBUtil.getConnection().prepareStatement("SELECT \n"
                    + "            v.IdVenta,\n"
                    + "            v.Comprobante,\n"
                    + "            v.Serie,\n"
                    + "            v.Numeracion,\n"
                    + "            c.IdCliente,\n"
                    + "            c.TipoDocumento,\n"
                    + "            c.NumeroDocumento,\n"
                    + "            c.Informacion,\n"
                    + "            c.Direccion,\n"
                    + "            c.Celular,\n"
                    + "            c.Email \n"
                    + "            FROM VentaTB AS v INNER JOIN ClienteTB AS c ON c.IdCliente = v.Cliente\n"
                    + "            WHERE CONCAT(v.Serie,'-',v.Numeracion) = ? ");
            statementNotaCredito.setString(1, comprobante);
            VentaTB ventaTB = null;
            try (ResultSet resultSet = statementNotaCredito.executeQuery()) {
                if (resultSet.next()) {
                    ventaTB = new VentaTB();
                    ventaTB.setIdVenta(resultSet.getString("IdVenta"));
                    ventaTB.setIdComprobante(resultSet.getInt("Comprobante"));
                    ventaTB.setSerie(resultSet.getString("Serie"));
                    ventaTB.setNumeracion(resultSet.getString("Numeracion"));
                    ClienteTB clienteTB = new ClienteTB();
                    clienteTB.setIdCliente(resultSet.getString("IdCliente"));
                    clienteTB.setTipoDocumento(resultSet.getInt("TipoDocumento"));
                    clienteTB.setNumeroDocumento(resultSet.getString("NumeroDocumento"));
                    clienteTB.setInformacion(resultSet.getString("Informacion"));
                    clienteTB.setDireccion(resultSet.getString("Direccion"));
                    clienteTB.setCelular(resultSet.getString("Celular"));
                    clienteTB.setEmail(resultSet.getString("Email"));
                    ventaTB.setClienteTB(clienteTB);
                }
            }

            statementNotaCredito = DBUtil.getConnection().prepareStatement("SELECT \n"
                    + "            dv.IdVenta,\n"
                    + "            dv.IdArticulo,\n"
                    + "            s.Clave,\n"
                    + "            s.NombreMarca,\n"
                    + "            isnull(d.Nombre,'') UnidadMarca,\n"
                    + "            dv.Cantidad,\n"
                    + "            dv.PrecioVenta,\n"
                    + "            dv.Descuento,\n"
                    + "            dv.ValorImpuesto,\n"
                    + "            dv.NombreImpuesto \n"
                    + "            FROM DetalleVentaTB AS dv \n"
                    + "            INNER JOIN SuministroTB AS s ON s.IdSuministro = dv.IdArticulo\n"
                    + "            LEFT JOIN DetalleTB AS d ON d.IdDetalle = s.UnidadCompra AND d.IdMantenimiento = '0013'\n"
                    + "            WHERE dv.IdVenta = ?");
            statementNotaCredito.setString(1, ventaTB.getIdVenta());
            ArrayList<DetalleVentaTB> arrayDetalleVenta = new ArrayList<>();
            try (ResultSet resultSet = statementNotaCredito.executeQuery()) {
                while (resultSet.next()) {
                    DetalleVentaTB detalleVentaTB = new DetalleVentaTB();
                    detalleVentaTB.setIdVenta(resultSet.getString("IdVenta"));
                    detalleVentaTB.setIdArticulo(resultSet.getString("IdArticulo"));
                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setClave(resultSet.getString("Clave"));
                    suministroTB.setNombreMarca(resultSet.getString("NombreMarca"));
                    suministroTB.setUnidadCompraName(resultSet.getString("UnidadMarca"));
                    suministroTB.setCantidad(resultSet.getDouble("Cantidad"));
                    suministroTB.setPrecioVentaGeneral(resultSet.getDouble("PrecioVenta"));
                    suministroTB.setDescuento(resultSet.getDouble("Descuento"));
                    suministroTB.setImpuestoValor(resultSet.getDouble("ValorImpuesto"));
                    suministroTB.setImpuestoNombre(resultSet.getString("NombreImpuesto"));
                    detalleVentaTB.setSuministroTB(suministroTB);
                    arrayDetalleVenta.add(detalleVentaTB);
                }
            }

            objects.add(arrayNotaCredito);
            objects.add(arrayMoneda);
            objects.add(arrayTipoComprobante);
            objects.add(arrayMotivoAnulacion);
            objects.add(arrayTipoDocumento);
            objects.add(ventaTB);
            objects.add(arrayDetalleVenta);

            return objects;
        } catch (SQLException ex) {

        } finally {
            try {
                if (statementNotaCredito != null) {
                    statementNotaCredito.close();
                }
            } catch (SQLException ex) {

            }

        }
        return null;
    }

    public static Object ListarNotasCredito(int opcion, String buscar, String fechaInico, String fechaFinal, int posicionPagina, int filasPorPagina) {
        PreparedStatement statementNotaCredito = null;
        ResultSet resultSet = null;
        try {
            DBUtil.dbConnect();
            Object[] objects = new Object[2];
            ObservableList<NotaCreditoTB> notaCreditoTBs = FXCollections.observableArrayList();            
            statementNotaCredito = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_NotaCredito(?,?,?,?,?,?)}");
            statementNotaCredito.setInt(1, opcion);
            statementNotaCredito.setString(2, buscar);
            statementNotaCredito.setString(3, fechaInico);
            statementNotaCredito.setString(4, fechaFinal);
            statementNotaCredito.setInt(5, posicionPagina);
            statementNotaCredito.setInt(6, filasPorPagina);
            resultSet = statementNotaCredito.executeQuery();
            while (resultSet.next()) {
                NotaCreditoTB notaCreditoTB = new NotaCreditoTB();
                notaCreditoTB.setId(resultSet.getRow() + posicionPagina);
                notaCreditoTB.setIdVenta(resultSet.getString("IdVenta"));
                notaCreditoTB.setIdNotaCredito(resultSet.getString("IdNotaCredito"));
                notaCreditoTB.setFechaRegistro(resultSet.getDate("FechaRegistro").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                notaCreditoTB.setHoraRegistro(resultSet.getTime("HoraRegistro").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                notaCreditoTB.setNombreComprobante(resultSet.getString("NotaCreditoComprobante"));
                notaCreditoTB.setSerie(resultSet.getString("SerieNotaCredito"));
                notaCreditoTB.setNumeracion(resultSet.getString("NumeracioNotaCredito"));

                ClienteTB clienteTB = new ClienteTB();
                clienteTB.setNumeroDocumento(resultSet.getString("NumeroDocumento"));
                clienteTB.setInformacion(resultSet.getString("Informacion"));
                notaCreditoTB.setClienteTB(clienteTB);

                VentaTB ventaTB = new VentaTB();
                ventaTB.setComprobanteName(resultSet.getString("VentaComprobante"));
                ventaTB.setSerie(resultSet.getString("SerieVenta"));
                ventaTB.setNumeracion(resultSet.getString("NumeracionVenta"));
                notaCreditoTB.setVentaTB(ventaTB);

                notaCreditoTB.setImporteNeto(resultSet.getDouble("Total"));
                notaCreditoTBs.add(notaCreditoTB);
            }

            statementNotaCredito = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_NotaCredito_Count(?,?,?,?)}");
            statementNotaCredito.setInt(1, opcion);
            statementNotaCredito.setString(2, buscar);
            statementNotaCredito.setString(3, fechaInico);
            statementNotaCredito.setString(4, fechaFinal);
            resultSet = statementNotaCredito.executeQuery();
            Integer cantidadTotal = 0;
            if (resultSet.next()) {
                cantidadTotal = resultSet.getInt("Total");
            }

            objects[0] = notaCreditoTBs;
            objects[1] = cantidadTotal;
            return objects;
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementNotaCredito != null) {
                    statementNotaCredito.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }

    public static Object DetalleNotaCreditoById(String idNotaCredito) {
        PreparedStatement statementNotaCredito = null;
        ResultSet resultSet = null;
        try {
            DBUtil.dbConnect();
            statementNotaCredito = DBUtil.getConnection().prepareStatement("{CALL Sp_Obtener_NotaCredito_ById(?)}");
            statementNotaCredito.setString(1, idNotaCredito);
            resultSet = statementNotaCredito.executeQuery();
            NotaCreditoTB notaCreditoTB = null;
            if (resultSet.next()) {
                notaCreditoTB = new NotaCreditoTB();
                notaCreditoTB.setId(resultSet.getRow());
                notaCreditoTB.setIdVenta(resultSet.getString("IdVenta"));
                notaCreditoTB.setCodigoAlterno(resultSet.getString("CodigoAlterno"));
                notaCreditoTB.setIdNotaCredito(resultSet.getString("IdNotaCredito"));
                notaCreditoTB.setFechaRegistro(resultSet.getDate("FechaRegistro").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                notaCreditoTB.setHoraRegistro(resultSet.getTime("HoraRegistro").toLocalTime().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
                notaCreditoTB.setNombreComprobante(resultSet.getString("NotaCreditoComprobante"));
                notaCreditoTB.setSerie(resultSet.getString("SerieNotaCredito"));
                notaCreditoTB.setNumeracion(resultSet.getString("NumeracioNotaCredito"));
                notaCreditoTB.setNombreMotivo(resultSet.getString("MotivoAnulacion"));

                MonedaTB monedaTB = new MonedaTB();
                monedaTB.setNombre(resultSet.getString("Moneda"));
                monedaTB.setAbreviado(resultSet.getString("Abreviado"));
                monedaTB.setSimbolo(resultSet.getString("Simbolo"));
                notaCreditoTB.setMonedaTB(monedaTB);

                ClienteTB clienteTB = new ClienteTB();
                clienteTB.setTipoDocumentoName(resultSet.getString("TipoDocumento"));
                clienteTB.setIdAuxiliar(resultSet.getString("IdAuxiliar"));
                clienteTB.setNumeroDocumento(resultSet.getString("NumeroDocumento"));
                clienteTB.setInformacion(resultSet.getString("Informacion"));
                clienteTB.setCelular(resultSet.getString("Celular"));
                clienteTB.setTelefono(resultSet.getString("Telefono"));
                clienteTB.setEmail(resultSet.getString("Email"));
                clienteTB.setDireccion(resultSet.getString("Direccion"));
                notaCreditoTB.setClienteTB(clienteTB);

                VentaTB ventaTB = new VentaTB();
                ventaTB.setComprobanteName(resultSet.getString("VentaComprobante"));
                ventaTB.setSerie(resultSet.getString("SerieVenta"));
                ventaTB.setNumeracion(resultSet.getString("NumeracionVenta"));
                notaCreditoTB.setVentaTB(ventaTB);

                statementNotaCredito = DBUtil.getConnection().prepareStatement("{CALL Sp_Obtener_NotaCredito_Detalle_ById(?)}");
                statementNotaCredito.setString(1, idNotaCredito);
                resultSet = statementNotaCredito.executeQuery();
                ArrayList<NotaCreditoDetalleTB> creditoDetalleTBs = new ArrayList<>();
                while (resultSet.next()) {
                    NotaCreditoDetalleTB notaCreditoDetalleTB = new NotaCreditoDetalleTB();
                    notaCreditoDetalleTB.setId(resultSet.getRow());
                    SuministroTB suministroTB = new SuministroTB();
                    suministroTB.setClave(resultSet.getString("Clave"));
                    suministroTB.setNombreMarca(resultSet.getString("NombreMarca"));
                    suministroTB.setUnidadCompraName(resultSet.getString("Unidad"));

                    notaCreditoDetalleTB.setCantidad(resultSet.getDouble("Cantidad"));
                    notaCreditoDetalleTB.setPrecio(resultSet.getDouble("Precio"));
                    notaCreditoDetalleTB.setDescuento(resultSet.getDouble("Descuento"));
                    notaCreditoDetalleTB.setValorImpuesto(resultSet.getDouble("ValorImpuesto"));
                    notaCreditoDetalleTB.setImporte(notaCreditoDetalleTB.getCantidad() * (notaCreditoDetalleTB.getPrecio() - notaCreditoDetalleTB.getDescuento()));

                    notaCreditoDetalleTB.setSuministroTB(suministroTB);
                    creditoDetalleTBs.add(notaCreditoDetalleTB);
                }
                notaCreditoTB.setNotaCreditoDetalleTBs(creditoDetalleTBs);
            }

            return notaCreditoTB;
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (statementNotaCredito != null) {
                    statementNotaCredito.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }
}
