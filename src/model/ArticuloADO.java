package model;

import controller.tools.Session;
import controller.tools.Tools;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class ArticuloADO {

    private static String CrudArticulo(ArticuloTB articuloTB) {
        String result = "";
        PreparedStatement preparedArticulo = null;
        PreparedStatement preparedValidation = null;

        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);
            preparedValidation = DBUtil.getConnection().prepareStatement("select IdArticulo from ArticuloTB where IdArticulo = ?");
            preparedValidation.setString(1, articuloTB.getIdArticulo());

            if (preparedValidation.executeQuery().next()) {
                preparedValidation = DBUtil.getConnection().prepareStatement("select Clave from ArticuloTB where IdArticulo <> ? and Clave = ?");
                preparedValidation.setString(1, articuloTB.getIdArticulo());
                preparedValidation.setString(2, articuloTB.getClave());
                if (preparedValidation.executeQuery().next()) {
                    DBUtil.getConnection().rollback();
                    result = "duplicate";
                } else {
                    preparedValidation = DBUtil.getConnection().prepareStatement("select NombreMarca from ArticuloTB where IdArticulo <> ? and NombreMarca = ?");
                    preparedValidation.setString(1, articuloTB.getIdArticulo());
                    preparedValidation.setString(2, articuloTB.getNombreMarca());
                    if (preparedValidation.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        result = "duplicatename";
                    } else {
                        preparedArticulo = DBUtil.getConnection().prepareStatement("update ArticuloTB set "
                                + "Clave=?, "
                                + "ClaveAlterna=UPPER(?), "
                                + "NombreMarca=UPPER(?), "
                                + "NombreGenerico=UPPER(?), "
                                + "Marca=?, "
                                + "StockMinimo=?, "
                                + "StockMaximo=?, "
                                + "Imagen=? "
                                + "where IdArticulo = ?");
                        preparedArticulo.setString(1, articuloTB.getClave());
                        preparedArticulo.setString(2, articuloTB.getClaveAlterna());
                        preparedArticulo.setString(3, articuloTB.getNombreMarca());
                        preparedArticulo.setString(4, articuloTB.getNombreGenerico());
                        preparedArticulo.setInt(5, articuloTB.getMarcar());
                        preparedArticulo.setDouble(6, articuloTB.getStockMinimo());
                        preparedArticulo.setDouble(7, articuloTB.getStockMaximo());

                        preparedArticulo.setString(8, articuloTB.getImagenTB());
                        preparedArticulo.setString(9, articuloTB.getIdArticulo());

                        preparedArticulo.addBatch();
                        preparedArticulo.executeBatch();

                        DBUtil.getConnection().commit();
                        result = "updated";
                    }
                }
            }
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException exr) {

            }
            result = ex.getLocalizedMessage();
        } finally {
            try {
                if (preparedArticulo != null) {
                    preparedArticulo.close();
                }
              
                if (preparedValidation != null) {
                    preparedValidation.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                result = ex.getLocalizedMessage();
            }
        }
        return result;
    }

    private static String ImportarArticulos(TableView<ArticuloTB> tvList) {
        PreparedStatement preparedArticulo = null;
        PreparedStatement preparedHistorialArticulo = null;
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);
            preparedArticulo = DBUtil.getConnection().prepareStatement("update ArticuloTB set PrecioCompra = ?,PrecioVenta = ?,Cantidad = ? where IdArticulo = ?");

            preparedHistorialArticulo = DBUtil.getConnection().prepareStatement("insert into HistorialArticuloTB(IdArticulo,FechaRegistro,TipoOperacion,Entrada,Salida,Saldo,UsuarioRegistro) values(?,GETDATE(),?,?,?,?,?)");

            for (int i = 0; i < tvList.getItems().size(); i++) {
                preparedArticulo.setDouble(1, tvList.getItems().get(i).getCostoCompra());
                preparedArticulo.setDouble(2, tvList.getItems().get(i).getPrecioVentaGeneral());
                preparedArticulo.setDouble(3, tvList.getItems().get(i).getCantidad());
                preparedArticulo.setString(4, tvList.getItems().get(i).getIdArticulo());
                preparedArticulo.addBatch();

                preparedHistorialArticulo.setString(1, tvList.getItems().get(i).getIdArticulo());
                preparedHistorialArticulo.setString(2, "Actualización del artículo");
                preparedHistorialArticulo.setDouble(3, tvList.getItems().get(i).getCantidad());
                preparedHistorialArticulo.setDouble(4, 0);
                preparedHistorialArticulo.setDouble(5, tvList.getItems().get(i).getCantidad());
                preparedHistorialArticulo.setString(6, Session.USER_ID);
                preparedHistorialArticulo.addBatch();
            }

            preparedArticulo.executeBatch();
            preparedHistorialArticulo.executeBatch();
            DBUtil.getConnection().commit();
            return "register";
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException exr) {
                return exr.getLocalizedMessage();
            }
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (preparedArticulo != null) {
                    preparedArticulo.close();
                }
                if (preparedHistorialArticulo != null) {
                    preparedHistorialArticulo.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }
        }
    }

    private static ObservableList<ArticuloTB> ListArticulos(short option, String value, int categoria) {
        String selectStmt = "{call Sp_Listar_Articulo(?,?,?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<ArticuloTB> empList = FXCollections.observableArrayList();
        try { 
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setShort(1, option);
            preparedStatement.setString(2, value);
            preparedStatement.setInt(3, categoria);
            rsEmps = preparedStatement.executeQuery();

            while (rsEmps.next()) {
                ArticuloTB articuloTB = new ArticuloTB();
                articuloTB.setId(rsEmps.getRow());
                articuloTB.setIdArticulo(rsEmps.getString("IdArticulo"));
                articuloTB.setClave(rsEmps.getString("Clave"));
                articuloTB.setClaveAlterna(rsEmps.getString("ClaveAlterna"));
                articuloTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                articuloTB.setNombreGenerico(rsEmps.getString("NombreGenerico"));
                articuloTB.setMarcaName(rsEmps.getString("Marca"));
                articuloTB.setCantidad(rsEmps.getDouble("Cantidad"));
                articuloTB.setPrecioVentaGeneral(rsEmps.getDouble("PrecioVentaGeneral"));
                articuloTB.setImpuestoArticulo(rsEmps.getInt("Impuesto"));
                articuloTB.setUnidadCompraName(rsEmps.getString("UnidadCompraNombre"));
                articuloTB.setUnidadVenta(rsEmps.getInt("UnidadVenta"));
                articuloTB.setCategoriaName(rsEmps.getString("Categoria"));
                articuloTB.setEstadoName(rsEmps.getString("Estado"));
                articuloTB.setImagenTB(rsEmps.getString("Imagen"));
                empList.add(articuloTB);
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
    
    private static ObservableList<ArticuloTB> ListArticulosPaginacion(int paginacion) {
        String selectStmt = "{call Sp_Listar_Articulo_Paginacion(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<ArticuloTB> empList = FXCollections.observableArrayList();
        try { 
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setInt(1, paginacion);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                ArticuloTB articuloTB = new ArticuloTB();
                articuloTB.setId(rsEmps.getRow()+paginacion);
                articuloTB.setIdArticulo(rsEmps.getString("IdArticulo"));
                articuloTB.setClave(rsEmps.getString("Clave"));
                articuloTB.setClaveAlterna(rsEmps.getString("ClaveAlterna"));
                articuloTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                articuloTB.setNombreGenerico(rsEmps.getString("NombreGenerico"));
                articuloTB.setMarcaName(rsEmps.getString("Marca"));
                articuloTB.setCantidad(rsEmps.getDouble("Cantidad"));
                articuloTB.setPrecioVentaGeneral(rsEmps.getDouble("PrecioVentaGeneral"));
                articuloTB.setImpuestoArticulo(rsEmps.getInt("Impuesto"));
                articuloTB.setUnidadCompraName(rsEmps.getString("UnidadCompraNombre"));
                articuloTB.setUnidadVenta(rsEmps.getInt("UnidadVenta"));
                articuloTB.setCategoriaName(rsEmps.getString("Categoria"));
                articuloTB.setEstadoName(rsEmps.getString("Estado"));
                articuloTB.setImagenTB(rsEmps.getString("Imagen"));
                empList.add(articuloTB);
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

    private static int GetAllArticulos() {
        String selectStmt = "SELECT COUNT(*) FROM ArticuloTB";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        int total = 0;
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            rsEmps = preparedStatement.executeQuery();
            total = rsEmps.next() ? rsEmps.getInt(1) : 0;
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
        return total;
    }

    private static ObservableList<ArticuloTB> ListArticulosListaView(short opcion, String value) {
        String selectStmt = "{call Sp_Listar_Articulo_Lista_View(?,?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<ArticuloTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setShort(1, opcion);
            preparedStatement.setString(2, value);
            rsEmps = preparedStatement.executeQuery();

            while (rsEmps.next()) {
                ArticuloTB articuloTB = new ArticuloTB();
                articuloTB.setId(rsEmps.getRow());
                articuloTB.setIdArticulo(rsEmps.getString("IdArticulo"));
                articuloTB.setClave(rsEmps.getString("Clave"));
                articuloTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                articuloTB.setMarcaName(rsEmps.getString("Marca"));
                articuloTB.setCantidad(rsEmps.getDouble("Cantidad"));
                articuloTB.setCostoCompra(rsEmps.getDouble("PrecioCompra"));

                articuloTB.setPrecioVentaGeneral(rsEmps.getDouble("PrecioVentaGeneral"));
                articuloTB.setPrecioMargenGeneral(rsEmps.getShort("PrecioMargenGeneral"));
                articuloTB.setPrecioUtilidadGeneral(rsEmps.getDouble("PrecioUtilidadGeneral"));

                articuloTB.setUnidadVenta(rsEmps.getInt("UnidadVenta"));
                articuloTB.setUnidadCompraName(rsEmps.getString("UnidadCompra"));
                articuloTB.setInventario(rsEmps.getBoolean("Inventario"));
                articuloTB.setImpuestoArticulo(rsEmps.getInt("Impuesto"));
                articuloTB.setLote(rsEmps.getBoolean("Lote"));
                articuloTB.setValorInventario(rsEmps.getBoolean("ValorInventario"));
                articuloTB.setOrigenName(rsEmps.getString("Origen"));
                empList.add(articuloTB);
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

    private static ArticuloTB GetArticulosById(String idArticulo) {
        String selectStmt = "{call Sp_Get_Articulo_By_Id(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ArticuloTB articuloTB = null;
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, idArticulo);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                articuloTB = new ArticuloTB();
                articuloTB.setIdArticulo(rsEmps.getString("IdArticulo"));
                articuloTB.setOrigen(rsEmps.getInt("Origen"));
                articuloTB.setClave(rsEmps.getString("Clave"));
                articuloTB.setClaveAlterna(rsEmps.getString("ClaveAlterna"));
                articuloTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                articuloTB.setNombreGenerico(rsEmps.getString("NombreGenerico"));
                articuloTB.setCategoria(rsEmps.getInt("Categoria"));
                articuloTB.setCategoriaName(rsEmps.getString("CategoriaNombre"));
                articuloTB.setMarcar(rsEmps.getInt("Marca"));
                articuloTB.setMarcaName(rsEmps.getString("MarcaNombre"));
                articuloTB.setPresentacion(rsEmps.getInt("Presentacion"));
                articuloTB.setPresentacionName(rsEmps.getString("PresentacionNombre"));
                articuloTB.setUnidadCompra(rsEmps.getInt("UnidadCompra"));
                articuloTB.setUnidadCompraName(rsEmps.getString("UnidadCompraNombre"));
                articuloTB.setUnidadVenta(rsEmps.getInt("UnidadVenta"));
                articuloTB.setStockMinimo(rsEmps.getDouble("StockMinimo"));
                articuloTB.setStockMaximo(rsEmps.getDouble("StockMaximo"));
                articuloTB.setCantidad(rsEmps.getDouble("Cantidad"));
                articuloTB.setCostoCompra(rsEmps.getDouble("PrecioCompra"));

                articuloTB.setPrecioVentaGeneral(rsEmps.getDouble("PrecioVentaGeneral"));
                articuloTB.setPrecioMargenGeneral(rsEmps.getShort("PrecioMargenGeneral"));
                articuloTB.setPrecioUtilidadGeneral(rsEmps.getDouble("PrecioUtilidadGeneral"));

                articuloTB.setEstado(rsEmps.getInt("Estado"));
                articuloTB.setLote(rsEmps.getBoolean("Lote"));
                articuloTB.setInventario(rsEmps.getBoolean("Inventario"));
                articuloTB.setImagenTB(rsEmps.getString("Imagen"));
                articuloTB.setImpuestoArticulo(rsEmps.getInt("Impuesto"));
                articuloTB.setImpuestoArticuloName(rsEmps.getString("ImpuestoNombre"));

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
        return articuloTB;
    }

    private static ObservableList<ArticuloTB> ListIniciarInventario() {
        String selectStmt = "SELECT IdArticulo,Clave,NombreMarca,Lote,PrecioCompra,PrecioVenta,Cantidad "
                + "FROM ArticuloTB WHERE Cantidad = 0 and UnidadVenta = 1 and Inventario = 1";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<ArticuloTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                ArticuloTB articuloTB = new ArticuloTB();
                articuloTB.setIdArticulo(rsEmps.getString("IdArticulo"));
                articuloTB.setClave(rsEmps.getString("Clave"));
                articuloTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                articuloTB.setLote(rsEmps.getBoolean("Lote"));
                articuloTB.setCostoCompra(rsEmps.getDouble("PrecioCompra"));
                articuloTB.setPrecioVentaGeneral(rsEmps.getDouble("PrecioVenta"));
                articuloTB.setCantidad(rsEmps.getDouble("Cantidad"));
                empList.add(articuloTB);
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

    private static ObservableList<ArticuloTB> ListInventario() {
        String selectStmt = "{call Sp_Listar_Inventario_Articulos()}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<ArticuloTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                ArticuloTB articuloTB = new ArticuloTB();
                articuloTB.setId(rsEmps.getRow());
                articuloTB.setIdArticulo(rsEmps.getString("IdArticulo"));
                articuloTB.setClave(rsEmps.getString("Clave"));
                articuloTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                articuloTB.setCostoCompra(rsEmps.getDouble("PrecioCompra"));
                articuloTB.setCantidad(rsEmps.getDouble("Cantidad"));
                articuloTB.setUnidadCompraName(rsEmps.getString("UnidadCompra"));
                articuloTB.setEstadoName(rsEmps.getString("Estado"));
                articuloTB.setTotalImporte(rsEmps.getDouble("Total"));
                empList.add(articuloTB);
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

    private static ArticuloTB Get_Articulo_By_Search(String value) {
        String selectStmt = "{call Sp_Listar_Articulo_By_Search(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ArticuloTB articuloTB = null;
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, value);
            rsEmps = preparedStatement.executeQuery();
            if (rsEmps.next()) {
                articuloTB = new ArticuloTB();
                articuloTB.setIdArticulo(rsEmps.getString("IdArticulo"));
                articuloTB.setClave(rsEmps.getString("Clave"));
                articuloTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                articuloTB.setMarcaName(rsEmps.getString("Marca"));
                articuloTB.setPresentacionName(rsEmps.getString("Presentacion"));
                articuloTB.setCantidad(rsEmps.getDouble("Cantidad"));
                articuloTB.setCostoCompra(rsEmps.getDouble("PrecioCompra"));
                articuloTB.setPrecioVentaGeneral(rsEmps.getDouble("PrecioVentaGeneral"));
                articuloTB.setPrecioVentaUnico(rsEmps.getDouble("PrecioVentaGeneral"));
                articuloTB.setUnidadVenta(rsEmps.getInt("UnidadVenta"));
                articuloTB.setLote(rsEmps.getBoolean("Lote"));
                articuloTB.setInventario(rsEmps.getBoolean("Inventario"));
                articuloTB.setImpuestoArticulo(rsEmps.getInt("Impuesto"));
                articuloTB.setValorInventario(rsEmps.getBoolean("ValorInventario"));
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
        return articuloTB;
    }

    private static void ListArticulosPaginacion() {
        PreparedStatement statementTotal = null;
        ResultSet resultSet = null;
        try {
            DBUtil.dbConnect();
            statementTotal = DBUtil.getConnection().prepareStatement("select count(*) as Total from ArticuloTB");
            resultSet = statementTotal.executeQuery();
            int total = 0;
            if (resultSet.next()) {
                total = resultSet.getInt("Total");
            }
            System.out.println(total);
        } catch (SQLException e) {

        } finally {
            try {
                if (statementTotal != null) {
                    statementTotal.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {

            }

        }
    }

    private static ObservableList<ArticuloTB> ListArticulosCodBar(int unidadVenta, int categoria) {
        String selectStmt = "{call Sp_Generar_Listardo_CodBar(?,?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<ArticuloTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setInt(1, unidadVenta);
            preparedStatement.setInt(2, categoria);
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                ArticuloTB articuloTB = new ArticuloTB();
                articuloTB.setClave(rsEmps.getString("Clave"));
                articuloTB.setClaveAlterna(rsEmps.getString("ClaveAlterna"));
                articuloTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                articuloTB.setUnidadVenta(rsEmps.getInt("UnidadVenta"));
                empList.add(articuloTB);
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

    private static String RemoveArticulo(String idArticulo) {
        String result = "";
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementValidar = null;
            PreparedStatement statementArticulo = null;
            try {
                DBUtil.getConnection().setAutoCommit(false);

                statementValidar = DBUtil.getConnection().prepareStatement("SELECT * FROM KardexArticuloTB WHERE IdArticulo = ?");
                statementValidar.setString(1, idArticulo);
                if (statementValidar.executeQuery().next()) {
                    DBUtil.getConnection().rollback();
                    result = "kardex";
                } else {
                    statementValidar = DBUtil.getConnection().prepareStatement("SELECT * FROM DetalleVentaTB WHERE IdArticulo = ?");
                    statementValidar.setString(1, idArticulo);
                    if (statementValidar.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        result = "venta";
                    } else {
                        statementArticulo = DBUtil.getConnection().prepareStatement("DELETE FROM ArticuloTB WHERE IdArticulo = ?");
                        statementArticulo.setString(1, idArticulo);
                        statementArticulo.addBatch();

                        statementArticulo.executeBatch();
                        DBUtil.getConnection().commit();
                        result = "removed";
                    }
                }

            } catch (SQLException ex) {
                try {
                    result = ex.getLocalizedMessage();
                    DBUtil.getConnection().rollback();
                } catch (SQLException e) {
                    result = e.getLocalizedMessage();
                }
            } finally {
                try {
                    if (statementValidar != null) {
                        statementValidar.close();
                    }
                    if (statementArticulo != null) {
                        statementArticulo.close();
                    }

                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    result = ex.getLocalizedMessage();
                }
            }
        } else {
            result = "No se pudo establecer la conexión, revise el estado.";
        }
        return result;

    }

    private static ArticuloTB Get_Articulo_Verificar_Movimiento(String clave) {
        PreparedStatement statementVendedor = null;
        ArticuloTB articuloTB = null;
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            try {
                statementVendedor = DBUtil.getConnection().prepareStatement("SELECT IdArticulo,Clave FROM ArticuloTB WHERE Clave = ?");
                statementVendedor.setString(1, clave);
                try (ResultSet resultSet = statementVendedor.executeQuery()) {
                    if (resultSet.next()) {
                        articuloTB = new ArticuloTB();
                        articuloTB.setIdArticulo(resultSet.getString("IdArticulo"));
                        articuloTB.setClave(resultSet.getString("Clave"));
                    }
                }
            } catch (SQLException ex) {

            } finally {
                try {
                    if (statementVendedor != null) {
                        statementVendedor.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {

                }
            }

        }

        return articuloTB;
    }

    private static ObservableList<ArticuloTB> List_Articulo_Movimiento(String idArticulo) {
        String selectStmt = "{call Sp_Listar_Articulo_Movimiento(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<ArticuloTB> empList = FXCollections.observableArrayList();

        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, idArticulo);
            rsEmps = preparedStatement.executeQuery();

            while (rsEmps.next()) {
                ArticuloTB articuloTB = new ArticuloTB();
                articuloTB.setIdArticulo(rsEmps.getString("IdArticulo"));
                articuloTB.setClave(rsEmps.getString("Clave"));
                articuloTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                articuloTB.setCantidad(rsEmps.getDouble("Cantidad"));
                articuloTB.setUnidadCompraName(rsEmps.getString("UnidadCompraNombre"));
                articuloTB.setCostoCompra(rsEmps.getDouble("PrecioCompra"));
                articuloTB.setPrecioVentaGeneral(rsEmps.getDouble("PrecioVentaGeneral"));
                articuloTB.setInventario(rsEmps.getBoolean("Inventario"));
                articuloTB.setValorInventario(rsEmps.getBoolean("ValorInventario"));
                TextField tf = new TextField(Tools.roundingValue(articuloTB.getCantidad(), 4));
                tf.getStyleClass().add("text-field-normal");
                tf.setOnKeyTyped((KeyEvent event) -> {
                    char c = event.getCharacter().charAt(0);
                    if ((c < '0' || c > '9') && (c != '\b') && (c != '.') && (c != '-')) {
                        event.consume();
                    }
                    if (c == '.' && tf.getText().contains(".") || c == '-' && tf.getText().contains("-")) {
                        event.consume();
                    }
                });
                articuloTB.setMovimiento(tf);

                CheckBox checkbox = new CheckBox("");
                checkbox.getStyleClass().add("check-box-contenido");
                checkbox.setSelected(true);
                articuloTB.setValidar(checkbox);

                empList.add(articuloTB);

            }

        } catch (SQLException e) {
            System.out.println("La operacion de selecciona de SQl ha fallado" + e);
        } finally {

            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (rsEmps != null) {
                    rsEmps.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException e) {
            }
        }
        return empList;
    }

    private static String Registrar_Articulo_Movimiento(String idMovimientoInventario, String observacion, TableView<MovimientoInventarioDetalleTB> tvList) {
        String result = "";
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {

            PreparedStatement movimiento_inventario = null;
            PreparedStatement statementBusqueda = null;
            PreparedStatement statementArticulo = null;
            PreparedStatement statementKardexArticulo = null;
            PreparedStatement suministro_update = null;
            PreparedStatement suministro_kardex = null;

            try {
                DBUtil.getConnection().setAutoCommit(false);

                movimiento_inventario = DBUtil.getConnection().prepareStatement("UPDATE MovimientoInventarioTB SET Estado = ? WHERE IdMovimientoInventario = ?");

                statementArticulo = DBUtil.getConnection().prepareStatement("UPDATE ArticuloTB SET Cantidad = Cantidad + ?,PrecioCompra=?,PrecioVentaGeneral=? WHERE IdArticulo = ?");

                statementKardexArticulo = DBUtil.getConnection().prepareStatement("INSERT INTO "
                        + "KardexArticuloTB("
                        + "IdArticulo,"
                        + "Fecha,"
                        + "Hora,"
                        + "Tipo,"
                        + "Movimiento,"
                        + "Detalle,"
                        + "Cantidad,"
                        + "CUnitario,"
                        + "CTotal) "
                        + "VALUES(?,?,?,?,?,?,?,?,?)");

                suministro_update = DBUtil.getConnection().prepareStatement("UPDATE SuministroTB SET Cantidad = Cantidad - ? WHERE IdSuministro = ?");

                suministro_kardex = DBUtil.getConnection().prepareStatement("INSERT INTO "
                        + "KardexSuministroTB("
                        + "IdSuministro,"
                        + "Fecha,"
                        + "Hora,"
                        + "Tipo,"
                        + "Movimiento,"
                        + "Detalle,"
                        + "Cantidad,"
                        + "CUnitario,"
                        + "CTotal) "
                        + "VALUES(?,?,?,?,?,?,?,?,?)");

                int countVerificar = 0;
                for (int i = 0; i < tvList.getItems().size(); i++) {

                    statementBusqueda = DBUtil.getConnection().prepareStatement("SELECT IdArticulo,PrecioCompra,PrecioVentaGeneral FROM ArticuloTB WHERE Clave = ?");
                    statementBusqueda.setString(1, tvList.getItems().get(i).getSuministroTB().getClave());
                    if (statementBusqueda.executeQuery().next()) {
                        ResultSet resultSet = statementBusqueda.executeQuery();
                        if (resultSet.next()) {

                            if (tvList.getItems().get(i).getActualizarPrecio().isSelected()) {
                                statementArticulo.setDouble(1, tvList.getItems().get(i).getCantidad());
                                statementArticulo.setDouble(2, tvList.getItems().get(i).getCosto());
                                statementArticulo.setDouble(3, tvList.getItems().get(i).getPrecio());
                                statementArticulo.setString(4, resultSet.getString("IdArticulo"));
                                statementArticulo.addBatch();
                            } else {
                                statementArticulo.setDouble(1, tvList.getItems().get(i).getCantidad());
                                statementArticulo.setDouble(2, resultSet.getDouble("PrecioCompra"));
                                statementArticulo.setDouble(3, resultSet.getDouble("PrecioVentaGeneral"));
                                statementArticulo.setString(4, resultSet.getString("IdArticulo"));
                                statementArticulo.addBatch();
                            }

                            statementKardexArticulo.setString(1, resultSet.getString("IdArticulo"));
                            statementKardexArticulo.setString(2, Tools.getDate());
                            statementKardexArticulo.setString(3, Tools.getHour());
                            statementKardexArticulo.setShort(4, (short) 1);
                            statementKardexArticulo.setInt(5, 4);
                            statementKardexArticulo.setString(6, observacion);
                            statementKardexArticulo.setDouble(7, tvList.getItems().get(i).getCantidad());
                            statementKardexArticulo.setDouble(8, tvList.getItems().get(i).getCosto());
                            statementKardexArticulo.setDouble(9, tvList.getItems().get(i).getCantidad() * tvList.getItems().get(i).getCosto());
                            statementKardexArticulo.addBatch();

                            suministro_update.setDouble(1, tvList.getItems().get(i).getCantidad());
                            suministro_update.setString(2, tvList.getItems().get(i).getIdSuministro());
                            suministro_update.addBatch();

                            suministro_kardex.setString(1, tvList.getItems().get(i).getIdSuministro());
                            suministro_kardex.setString(2, Tools.getDate());
                            suministro_kardex.setString(3, Tools.getHour());
                            suministro_kardex.setShort(4, (short) 2);
                            suministro_kardex.setInt(5, 1);
                            suministro_kardex.setString(6, observacion);
                            suministro_kardex.setDouble(7, tvList.getItems().get(i).getCantidad());
                            suministro_kardex.setDouble(8, tvList.getItems().get(i).getCosto());
                            suministro_kardex.setDouble(9, tvList.getItems().get(i).getCantidad() * tvList.getItems().get(i).getCosto());
                            suministro_kardex.addBatch();

                            countVerificar++;
                        }

                    }
                }

                if (countVerificar == tvList.getItems().size()) {
                    movimiento_inventario.setShort(1, (short) 1);
                    movimiento_inventario.setString(2, idMovimientoInventario);
                    movimiento_inventario.addBatch();

                    movimiento_inventario.executeBatch();
                    statementArticulo.executeBatch();
                    statementKardexArticulo.executeBatch();
                    suministro_update.executeBatch();
                    suministro_kardex.executeBatch();
                    DBUtil.getConnection().commit();
                    result = "registrado";
                } else {
                    DBUtil.getConnection().rollback();
                    result = "error";
                }

            } catch (SQLException ex) {
                try {
                    result = ex.getLocalizedMessage();
                    DBUtil.getConnection().rollback();
                } catch (SQLException e) {
                    result = e.getLocalizedMessage();
                }
            } finally {
                try {
                    if (movimiento_inventario != null) {
                        movimiento_inventario.close();
                    }
                    if (statementBusqueda != null) {
                        statementBusqueda.close();
                    }
                    if (statementArticulo != null) {
                        statementArticulo.close();
                    }
                    if (statementKardexArticulo != null) {
                        statementKardexArticulo.close();
                    }
                    if (suministro_update != null) {
                        suministro_update.close();
                    }
                    if (suministro_kardex != null) {
                        suministro_kardex.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {

                }
            }
        } else {
            result = "No se pudo establecer la conexión, revise el estado.";
        }
        return result;
    }

}
