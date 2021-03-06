package model;

import controller.tools.Tools;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

public class SuministroADO {

    public static String CrudSuministro(SuministroTB suministroTB, ObservableList<PreciosTB> tvPrecios) {
        String result = "";
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {

            CallableStatement codigoSuministro = null;
            PreparedStatement preparedSuministro = null;
            PreparedStatement preparedValidation = null;
            PreparedStatement preparedPrecios = null;

            try {
                DBUtil.getConnection().setAutoCommit(false);

                preparedValidation = DBUtil.getConnection().prepareStatement("select IdSuministro from SuministroTB where IdSuministro = ?");
                preparedValidation.setString(1, suministroTB.getIdSuministro());
                if (preparedValidation.executeQuery().next()) {
                    preparedValidation = DBUtil.getConnection().prepareStatement("select Clave from SuministroTB where IdSuministro <> ? and Clave = ?");
                    preparedValidation.setString(1, suministroTB.getIdSuministro());
                    preparedValidation.setString(2, suministroTB.getClave());
                    if (preparedValidation.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        result = "duplicate";
                    } else {
                        preparedValidation = DBUtil.getConnection().prepareStatement("select NombreMarca from SuministroTB where IdSuministro <> ? and NombreMarca = ?");
                        preparedValidation.setString(1, suministroTB.getIdSuministro());
                        preparedValidation.setString(2, suministroTB.getNombreMarca());
                        if (preparedValidation.executeQuery().next()) {
                            DBUtil.getConnection().rollback();
                            result = "duplicatename";
                        } else {
                            preparedSuministro = DBUtil.getConnection().prepareStatement("UPDATE SuministroTB SET Origen=?,Clave=?,ClaveAlterna=UPPER(?), NombreMarca=UPPER(?),NombreGenerico=UPPER(?),Categoria=?,Marca=?,Presentacion=?,StockMinimo=?,StockMaximo=?,PrecioCompra=?,PrecioVentaGeneral=?,UnidadCompra=?,UnidadVenta = ?,Estado=?,Lote=?,Inventario=?,NuevaImagen=?,Impuesto=?, ValorInventario = ?,ClaveSat = ?,TipoPrecio=? WHERE IdSuministro = ? ");

                            preparedSuministro.setInt(1, suministroTB.getOrigen());
                            preparedSuministro.setString(2, suministroTB.getClave());
                            preparedSuministro.setString(3, suministroTB.getClaveAlterna());
                            preparedSuministro.setString(4, suministroTB.getNombreMarca());
                            preparedSuministro.setString(5, suministroTB.getNombreGenerico());
                            preparedSuministro.setInt(6, suministroTB.getCategoria());
                            preparedSuministro.setInt(7, suministroTB.getMarcar());
                            preparedSuministro.setInt(8, suministroTB.getPresentacion());
                            preparedSuministro.setDouble(9, suministroTB.getStockMinimo());
                            preparedSuministro.setDouble(10, suministroTB.getStockMaximo());
                            preparedSuministro.setDouble(11, suministroTB.getCostoCompra());

                            preparedSuministro.setDouble(12, suministroTB.getPrecioVentaGeneral());

                            preparedSuministro.setInt(13, suministroTB.getUnidadCompra());
                            preparedSuministro.setInt(14, suministroTB.getUnidadVenta());
                            preparedSuministro.setInt(15, suministroTB.getEstado());
                            preparedSuministro.setBoolean(16, suministroTB.isLote());
                            preparedSuministro.setBoolean(17, suministroTB.isInventario());
                            //------------------------------------------------------------
                            preparedSuministro.setBytes(18, suministroTB.getNuevaImagen());
                            //
                            preparedSuministro.setInt(19, suministroTB.getImpuestoId());
                            preparedSuministro.setShort(20, suministroTB.getValorInventario());
                            preparedSuministro.setString(21, suministroTB.getClaveSat());
                            preparedSuministro.setBoolean(22, suministroTB.isTipoPrecio());
                            preparedSuministro.setString(23, suministroTB.getIdSuministro());

                            preparedSuministro.addBatch();
                            preparedSuministro.executeBatch();

                            preparedPrecios = DBUtil.getConnection().prepareStatement("DELETE FROM PreciosTB WHERE IdSuministro = ?");
                            preparedPrecios.setString(1, suministroTB.getIdSuministro());
                            preparedPrecios.addBatch();
                            preparedPrecios.executeBatch();

                            preparedPrecios = DBUtil.getConnection().prepareStatement("INSERT INTO PreciosTB(IdArticulo, IdSuministro, Nombre, Valor, Factor,Estado) VALUES(?,?,?,?,?,?)");
                            for (int i = 0; i < tvPrecios.size(); i++) {
                                preparedPrecios.setString(1, "");
                                preparedPrecios.setString(2, suministroTB.getIdSuministro());
                                preparedPrecios.setString(3, tvPrecios.get(i).getNombre().toUpperCase());
                                preparedPrecios.setDouble(4, tvPrecios.get(i).getValor());
                                preparedPrecios.setDouble(5, tvPrecios.get(i).getFactor() <= 0 ? 1 : tvPrecios.get(i).getFactor());
                                preparedPrecios.setBoolean(6, true);
                                preparedPrecios.addBatch();
                            }
                            preparedPrecios.executeBatch();

                            DBUtil.getConnection().commit();
                            result = "updated";
                        }
                    }
                } else {
                    preparedValidation = DBUtil.getConnection().prepareStatement("select Clave from SuministroTB where Clave = ?");
                    preparedValidation.setString(1, suministroTB.getClave());
                    if (preparedValidation.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        result = "duplicate";
                    } else {
                        preparedValidation = DBUtil.getConnection().prepareStatement("select NombreMarca from SuministroTB where NombreMarca = ?");
                        preparedValidation.setString(1, suministroTB.getNombreMarca());
                        if (preparedValidation.executeQuery().next()) {
                            DBUtil.getConnection().rollback();
                            result = "duplicatename";
                        } else {
                            codigoSuministro = DBUtil.getConnection().prepareCall("{? = call Fc_Suministro_Codigo_Alfanumerico()}");
                            codigoSuministro.registerOutParameter(1, java.sql.Types.VARCHAR);
                            codigoSuministro.execute();
                            String idSuministro = codigoSuministro.getString(1);

                            preparedSuministro = DBUtil.getConnection().prepareStatement("INSERT INTO "
                                    + "SuministroTB"
                                    + "(IdSuministro,"
                                    + "Origen,"
                                    + "Clave,"
                                    + "ClaveAlterna,"
                                    + "NombreMarca,"
                                    + "NombreGenerico,"
                                    + "Categoria,"
                                    + "Marca,"
                                    + "Presentacion,"
                                    + "StockMinimo,"
                                    + "StockMaximo,"
                                    + "PrecioCompra,"
                                    + "PrecioVentaGeneral,"
                                    + "Cantidad,"
                                    + "UnidadCompra,"
                                    + "UnidadVenta,"
                                    + "Estado,"
                                    + "Lote,"
                                    + "Inventario,"
                                    + "NuevaImagen,"
                                    + "Impuesto,"
                                    + "ValorInventario,"
                                    + "Imagen,"
                                    + "ClaveSat,TipoPrecio)"
                                    + "values(?,?,?,UPPER(?),UPPER(?),UPPER(?),UPPER(?),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

                            preparedSuministro.setString(1, idSuministro);
                            preparedSuministro.setInt(2, suministroTB.getOrigen());
                            preparedSuministro.setString(3, suministroTB.getClave());
                            preparedSuministro.setString(4, suministroTB.getClaveAlterna());
                            preparedSuministro.setString(5, suministroTB.getNombreMarca());
                            preparedSuministro.setString(6, suministroTB.getNombreGenerico());
                            preparedSuministro.setInt(7, suministroTB.getCategoria());
                            preparedSuministro.setInt(8, suministroTB.getMarcar());
                            preparedSuministro.setInt(9, suministroTB.getPresentacion());
                            preparedSuministro.setDouble(10, suministroTB.getStockMinimo());
                            preparedSuministro.setDouble(11, suministroTB.getStockMaximo());
                            preparedSuministro.setDouble(12, suministroTB.getCostoCompra());

                            preparedSuministro.setDouble(13, suministroTB.getPrecioVentaGeneral());

                            preparedSuministro.setDouble(14, 0);
                            preparedSuministro.setInt(15, suministroTB.getUnidadCompra());
                            preparedSuministro.setInt(16, suministroTB.getUnidadVenta());
                            preparedSuministro.setInt(17, suministroTB.getEstado());
                            preparedSuministro.setBoolean(18, suministroTB.isLote());
                            preparedSuministro.setBoolean(19, suministroTB.isInventario());
                            //------------------------------------------------------------
                            preparedSuministro.setBytes(20, suministroTB.getNuevaImagen());
//                            preparedSuministro.setString(20, suministroTB.getImagenFile() != null
//                                    ? selectFileImage("./img/" + idSuministro + "." + Tools.getFileExtension(suministroTB.getImagenFile()), suministroTB.getImagenFile())
//                                    : (suministroTB.getImagenTB().equalsIgnoreCase("") ? ""
//                                    : selectFileImage("./img/" + idSuministro + "." + Tools.getFileExtension(new File(suministroTB.getImagenTB())), new File(suministroTB.getImagenTB())))
//                            );
                            //
                            preparedSuministro.setInt(21, suministroTB.getImpuestoId());
                            preparedSuministro.setShort(22, suministroTB.getValorInventario());
                            preparedSuministro.setString(23, "");
                            preparedSuministro.setString(24, suministroTB.getClaveSat());
                            preparedSuministro.setBoolean(25, suministroTB.isTipoPrecio());

                            preparedSuministro.addBatch();
                            preparedSuministro.executeBatch();

                            preparedPrecios = DBUtil.getConnection().prepareStatement("INSERT INTO PreciosTB(IdArticulo, IdSuministro, Nombre, Valor, Factor,Estado) VALUES(?,?,?,?,?,?)");

                            for (int i = 0; i < tvPrecios.size(); i++) {
                                preparedPrecios.setString(1, "");
                                preparedPrecios.setString(2, idSuministro);
                                preparedPrecios.setString(3, tvPrecios.get(i).getNombre().toUpperCase());
                                preparedPrecios.setDouble(4, tvPrecios.get(i).getValor());
                                preparedPrecios.setDouble(5, tvPrecios.get(i).getFactor() <= 0 ? 1 : tvPrecios.get(i).getFactor());
                                preparedPrecios.setBoolean(6, true);
                                preparedPrecios.addBatch();
                            }
                            preparedPrecios.executeBatch();

                            DBUtil.getConnection().commit();
                            result = "registered";
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
                    if (preparedSuministro != null) {
                        preparedSuministro.close();
                    }
                    if (preparedValidation != null) {
                        preparedValidation.close();
                    }
                    if (codigoSuministro != null) {
                        codigoSuministro.close();
                    }
                    if (preparedValidation != null) {
                        preparedValidation.close();
                    }
                    if (preparedPrecios != null) {
                        preparedPrecios.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {
                    result = ex.getLocalizedMessage();
                }
            }
        } else {
            result = "No se puedo establecer una conexi??n, intente nuevamente.";
        }
        return result;
    }

    public static String selectFileImage(String ruta, File selectFile) {
        String rutaValidada = ruta;
        File fcom = new File(ruta);
        if (fcom.exists()) {
            fcom.delete();
        }

        FileInputStream inputStream = null;
        byte[] buffer = new byte[1024];
        try (FileOutputStream outputStream = new FileOutputStream(ruta)) {
            inputStream = new FileInputStream(selectFile.getAbsolutePath());
            int byteRead;
            while ((byteRead = inputStream.read(buffer)) != 1) {
                outputStream.write(buffer, 0, byteRead);
                outputStream.flush();
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                rutaValidada = "";
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    rutaValidada = "";
                }
            }
        }
        return rutaValidada;
    }

    public static Object ListSuministrosListaView(short tipo, String value, int posicionPagina, int filasPorPagina) {
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        try {
            DBUtil.dbConnect();
            Object[] objects = new Object[2];

            ObservableList<SuministroTB> empList = FXCollections.observableArrayList();
            preparedStatement = DBUtil.getConnection().prepareStatement("{call Sp_Listar_Suministros_Lista_View(?,?,?,?)}");
            preparedStatement.setShort(1, tipo);
            preparedStatement.setString(2, value);
            preparedStatement.setInt(3, posicionPagina);
            preparedStatement.setInt(4, filasPorPagina);
            rsEmps = preparedStatement.executeQuery();

            while (rsEmps.next()) {
                SuministroTB suministroTB = new SuministroTB();
                suministroTB.setId(rsEmps.getRow() + posicionPagina);
                suministroTB.setIdSuministro(rsEmps.getString("IdSuministro"));
                suministroTB.setClave(rsEmps.getString("Clave"));
                suministroTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                suministroTB.setCategoriaName(rsEmps.getString("Categoria"));
                suministroTB.setMarcaName(rsEmps.getString("Marca"));
                suministroTB.setCantidad(rsEmps.getDouble("Cantidad"));
                suministroTB.setCostoCompra(rsEmps.getDouble("PrecioCompra"));

                suministroTB.setPrecioVentaGeneral(rsEmps.getDouble("PrecioVentaGeneral"));

                suministroTB.setUnidadCompraName(rsEmps.getString("UnidadCompra"));
                suministroTB.setUnidadVenta(rsEmps.getInt("UnidadVenta"));
                suministroTB.setInventario(rsEmps.getBoolean("Inventario"));
                suministroTB.setImpuestoOperacion(rsEmps.getInt("Operacion"));
                suministroTB.setImpuestoId(rsEmps.getInt("Impuesto"));
                suministroTB.setImpuestoNombre(rsEmps.getString("ImpuestoNombre"));
                suministroTB.setImpuestoValor(rsEmps.getDouble("Valor"));
                suministroTB.setLote(rsEmps.getBoolean("Lote"));
                suministroTB.setValorInventario(rsEmps.getShort("ValorInventario"));
                ImageView image = new ImageView(new Image(suministroTB.getValorInventario() == 1 ? "/view/image/unidad.png" : "/view/image/balanza.png"));
                image.setFitWidth(32);
                image.setFitHeight(32);
                suministroTB.setImageValorInventario(image);
                suministroTB.setNuevaImagen(rsEmps.getBytes("NuevaImagen"));

                empList.add(suministroTB);
            }

            preparedStatement = DBUtil.getConnection().prepareStatement("{call Sp_Listar_Suministros_Lista_View_Count(?,?)}");
            preparedStatement.setShort(1, tipo);
            preparedStatement.setString(2, value);
            rsEmps = preparedStatement.executeQuery();
            Integer integer = 0;
            if (rsEmps.next()) {
                integer = rsEmps.getInt("Total");
            }

            objects[0] = empList;
            objects[1] = integer;

            return objects;
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
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
                return ex.getLocalizedMessage();
            }
        }
    }

    public static Object ListarSuministros(short opcion, String clave, String nombreMarca, int categoria, int marca, int posicionPagina, int filasPorPagina) {
        PreparedStatement preparedSuministros = null;
        PreparedStatement preparedTotales = null;
        ResultSet rsEmps = null;
        try {
            DBUtil.dbConnect();
            Object[] objects = new Object[2];
            ObservableList<SuministroTB> empList = FXCollections.observableArrayList();
            preparedSuministros = DBUtil.getConnection().prepareStatement("{call Sp_Listar_Suministros(?,?,?,?,?,?,?)}");
            preparedSuministros.setShort(1, opcion);
            preparedSuministros.setString(2, clave);
            preparedSuministros.setString(3, nombreMarca);
            preparedSuministros.setInt(4, categoria);
            preparedSuministros.setInt(5, marca);
            preparedSuministros.setInt(6, posicionPagina);
            preparedSuministros.setInt(7, filasPorPagina);
            rsEmps = preparedSuministros.executeQuery();
            while (rsEmps.next()) {
                SuministroTB suministroTB = new SuministroTB();
                suministroTB.setId(rsEmps.getRow() + posicionPagina);
                suministroTB.setIdSuministro(rsEmps.getString("IdSuministro"));
                suministroTB.setClave(rsEmps.getString("Clave"));
                suministroTB.setClaveAlterna(rsEmps.getString("ClaveAlterna"));
                suministroTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                suministroTB.setNombreGenerico(rsEmps.getString("NombreGenerico"));
                suministroTB.setStockMinimo(rsEmps.getDouble("StockMinimo"));
                suministroTB.setStockMaximo(rsEmps.getDouble("StockMaximo"));
                suministroTB.setCantidad(rsEmps.getDouble("Cantidad"));
                suministroTB.setUnidadCompraName(rsEmps.getString("UnidadCompraNombre"));
                suministroTB.setCostoCompra(rsEmps.getDouble("PrecioCompra"));
                suministroTB.setPrecioVentaGeneral(rsEmps.getDouble("PrecioVentaGeneral"));
                suministroTB.setCategoriaName(rsEmps.getString("Categoria"));
                suministroTB.setMarcaName(rsEmps.getString("Marca"));
                suministroTB.setEstadoName(rsEmps.getString("Estado"));
                suministroTB.setInventario(rsEmps.getBoolean("Inventario"));
                suministroTB.setValorInventario(rsEmps.getShort("ValorInventario"));
                suministroTB.setNuevaImagen(rsEmps.getBytes("NuevaImagen"));

                Label label = new Label(Tools.roundingValue(suministroTB.getCantidad(), 2));
                label.getStyleClass().add("labelRoboto13");
                label.setStyle(suministroTB.getCantidad() > 0 ? "-fx-text-fill:blue;" : "-fx-text-fill:red;");
                suministroTB.setLblCantidad(label);

                suministroTB.setImpuestoTB(new ImpuestoTB(rsEmps.getString("ImpuestoNombre"), rsEmps.getDouble("Valor")));

                empList.add(suministroTB);
            }

            preparedTotales = DBUtil.getConnection().prepareStatement("{call Sp_Listar_Suministros_Count(?,?,?,?,?)}");
            preparedTotales.setShort(1, opcion);
            preparedTotales.setString(2, clave);
            preparedTotales.setString(3, nombreMarca);
            preparedTotales.setInt(4, categoria);
            preparedTotales.setInt(5, marca);
            rsEmps = preparedTotales.executeQuery();
            Integer integer = 0;
            if (rsEmps.next()) {
                integer = rsEmps.getInt("Total");
            }

            objects[0] = empList;
            objects[1] = integer;

            return objects;
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        } catch (Exception ex) {
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (preparedSuministros != null) {
                    preparedSuministros.close();
                }
                if (preparedTotales != null) {
                    preparedTotales.close();
                }
                if (rsEmps != null) {
                    rsEmps.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }

    }

    public static SuministroTB Get_Suministros_For_Asignacion_By_Id(String idSuministro) {
        String selectStmt = "{call Sp_Get_Suministro_For_Asignacion_By_Id(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        SuministroTB suministroTB = null;
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, idSuministro);
            rsEmps = preparedStatement.executeQuery();
            if (rsEmps.next()) {
                suministroTB = new SuministroTB();
                suministroTB.setIdSuministro(rsEmps.getString("IdSuministro"));
                suministroTB.setClave(rsEmps.getString("Clave"));
                suministroTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                suministroTB.setCantidad(rsEmps.getDouble("Cantidad"));
                suministroTB.setUnidadCompraName(rsEmps.getString("UnidadCompraNombre"));
                suministroTB.setCostoCompra(rsEmps.getDouble("PrecioCompra"));
                suministroTB.setPrecioVentaGeneral(rsEmps.getDouble("PrecioVentaGeneral"));
                suministroTB.setMovimiento(suministroTB.getCantidad());
                TextField tf = new TextField(Tools.roundingValue(suministroTB.getCantidad(), 4));
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
                suministroTB.setTxtMovimiento(tf);
                Label label = new Label("PROCESO");
                label.getStyleClass().add("label-proceso");
                suministroTB.setEstadoAsignacion(label);
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
        return suministroTB;
    }

    public static SuministroTB GetSuministroById(String value) {
        String selectStmt = "{call Sp_Suministro_By_Id(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        SuministroTB suministroTB = null;
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, value);
            rsEmps = preparedStatement.executeQuery();
            if (rsEmps.next()) {
                suministroTB = new SuministroTB();
                suministroTB.setIdSuministro(rsEmps.getString("IdSuministro"));
                suministroTB.setOrigen(rsEmps.getInt("Origen"));
                suministroTB.setClave(rsEmps.getString("Clave"));
                suministroTB.setClaveAlterna(rsEmps.getString("ClaveAlterna"));
                suministroTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                suministroTB.setNombreGenerico(rsEmps.getString("NombreGenerico"));
                suministroTB.setCategoria(rsEmps.getInt("Categoria"));
                suministroTB.setCategoriaName(rsEmps.getString("CategoriaNombre"));
                suministroTB.setMarcar(rsEmps.getInt("Marca"));
                suministroTB.setMarcaName(rsEmps.getString("MarcaNombre"));
                suministroTB.setPresentacion(rsEmps.getInt("Presentacion"));
                suministroTB.setPresentacionName(rsEmps.getString("PresentacionNombre"));
                suministroTB.setUnidadCompra(rsEmps.getInt("UnidadCompra"));
                suministroTB.setUnidadCompraName(rsEmps.getString("UnidadCompraNombre"));
                suministroTB.setUnidadVenta(rsEmps.getInt("UnidadVenta"));
                suministroTB.setStockMinimo(rsEmps.getDouble("StockMinimo"));
                suministroTB.setStockMaximo(rsEmps.getDouble("StockMaximo"));
                suministroTB.setCantidad(rsEmps.getDouble("Cantidad"));
                suministroTB.setCostoCompra(rsEmps.getDouble("PrecioCompra"));

                suministroTB.setPrecioVentaGeneral(rsEmps.getDouble("PrecioVentaGeneral"));

                suministroTB.setEstado(rsEmps.getInt("Estado"));
                suministroTB.setLote(rsEmps.getBoolean("Lote"));
                suministroTB.setInventario(rsEmps.getBoolean("Inventario"));
                suministroTB.setValorInventario(rsEmps.getShort("ValorInventario"));
                suministroTB.setNuevaImagen(rsEmps.getBytes("NuevaImagen"));
                suministroTB.setImpuestoId(rsEmps.getInt("Impuesto"));
                suministroTB.setImpuestoNombre(rsEmps.getString("ImpuestoNombre"));
                suministroTB.setClaveSat(rsEmps.getString("ClaveSat"));
                suministroTB.setTipoPrecio(rsEmps.getBoolean("TipoPrecio"));
            }
        } catch (SQLException e) {
            System.out.println("La operaci??n de selecci??n de SQL ha fallado: " + e);

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
        return suministroTB;
    }

    public static ArrayList<Object> ListInventario(String producto, short tipoExistencia, String nameProduct, short opcion, int categoria, int marca, int posicionPaginacion, int filasPorPagina) {
        PreparedStatement preparedStatementSuministros = null;
        PreparedStatement preparedStatementTotales = null;
        PreparedStatement preparedStatementCantidad = null;
        ResultSet rsEmps = null;
        ArrayList<Object> objects = new ArrayList<>();
        ObservableList<SuministroTB> empList = FXCollections.observableArrayList();
        try {
            DBUtil.dbConnect();
            preparedStatementSuministros = DBUtil.getConnection().prepareStatement("{call Sp_Listar_Inventario_Suministros(?,?,?,?,?,?,?,?)}");
            preparedStatementSuministros.setString(1, producto);
            preparedStatementSuministros.setShort(2, tipoExistencia);
            preparedStatementSuministros.setString(3, nameProduct);
            preparedStatementSuministros.setShort(4, opcion);
            preparedStatementSuministros.setInt(5, categoria);
            preparedStatementSuministros.setInt(6, marca);
            preparedStatementSuministros.setInt(7, posicionPaginacion);
            preparedStatementSuministros.setInt(8, filasPorPagina);
            rsEmps = preparedStatementSuministros.executeQuery();
            while (rsEmps.next()) {
                SuministroTB suministroTB = new SuministroTB();
                suministroTB.setId(rsEmps.getRow() + posicionPaginacion);
                suministroTB.setIdSuministro(rsEmps.getString("IdSuministro"));
                suministroTB.setClave(rsEmps.getString("Clave"));
                suministroTB.setClaveAlterna(rsEmps.getString("ClaveAlterna"));
                suministroTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                suministroTB.setCostoCompra(rsEmps.getDouble("PrecioCompra"));
                suministroTB.setPrecioVentaGeneral(rsEmps.getDouble("PrecioVentaGeneral"));
                suministroTB.setCantidad(rsEmps.getDouble("Cantidad"));
                suministroTB.setUnidadCompraName(rsEmps.getString("UnidadCompra"));
                suministroTB.setEstado(rsEmps.getInt("Estado"));
                suministroTB.setEstadoName(rsEmps.getString("EstadoName"));
                suministroTB.setImporteNeto(rsEmps.getDouble("Total"));
                suministroTB.setStockMinimo(rsEmps.getDouble("StockMinimo"));
                suministroTB.setStockMaximo(rsEmps.getDouble("StockMaximo"));
                suministroTB.setInventario(rsEmps.getBoolean("Inventario"));
                suministroTB.setCategoriaName(rsEmps.getString("Categoria"));
                suministroTB.setMarcaName(rsEmps.getString("Marca"));

                Label lblCantidad = new Label(Tools.roundingValue(suministroTB.getCantidad(), 2) + " " + suministroTB.getUnidadCompraName());
                lblCantidad.getStyleClass().add("label-existencia");
                lblCantidad.getStyleClass().add(
                        suministroTB.getCantidad() <= 0
                        ? "label-existencia-negativa"
                        : suministroTB.getCantidad() > 0 && suministroTB.getCantidad() < suministroTB.getStockMinimo()
                        ? "label-existencia-intermedia"
                        : suministroTB.getCantidad() >= suministroTB.getStockMinimo() && suministroTB.getCantidad() < suministroTB.getStockMaximo()
                        ? "label-existencia-normal"
                        : "label-existencia-Excedentes");
                suministroTB.setLblCantidad(lblCantidad);

                empList.add(suministroTB);
            }
            objects.add(empList);

            preparedStatementTotales = DBUtil.getConnection().prepareStatement("{call Sp_Listar_Inventario_Suministros_Count(?,?,?,?,?,?)}");
            preparedStatementTotales.setString(1, producto);
            preparedStatementTotales.setShort(2, tipoExistencia);
            preparedStatementTotales.setString(3, nameProduct);
            preparedStatementTotales.setShort(4, opcion);
            preparedStatementTotales.setInt(5, categoria);
            preparedStatementTotales.setInt(6, marca);
            rsEmps = preparedStatementTotales.executeQuery();
            Integer integer = 0;
            if (rsEmps.next()) {
                integer = rsEmps.getInt("Total");
            }
            objects.add(integer);

            preparedStatementCantidad = DBUtil.getConnection().prepareStatement("SELECT COUNT(*) AS Total FROM SuministroTB WHERE Inventario = 1");
            rsEmps = preparedStatementCantidad.executeQuery();
            if (rsEmps.next()) {
                objects.add(rsEmps.getInt("Total"));
            } else {
                objects.add(0);
            }

        } catch (SQLException e) {
            System.out.println("Error en SuministroADO->ListInventario: " + e);
        } finally {
            try {
                if (preparedStatementSuministros != null) {
                    preparedStatementSuministros.close();
                }
                if (preparedStatementTotales != null) {
                    preparedStatementTotales.close();
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

    public static ObservableList<SuministroTB> List_Suministros_In_Produccion(short opcion, String clave, String nombre) {
        String selectStmt = "{call Sp_Listar_Suministros(?,?,?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        ObservableList<SuministroTB> empList = FXCollections.observableArrayList();

        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setShort(1, opcion);
            preparedStatement.setString(2, clave);
            preparedStatement.setString(3, nombre);
            rsEmps = preparedStatement.executeQuery();

            while (rsEmps.next()) {
                SuministroTB suministroTB = new SuministroTB();
                suministroTB.setId(rsEmps.getRow());
                suministroTB.setIdSuministro(rsEmps.getString("IdSuministro"));
                suministroTB.setClave(rsEmps.getString("Clave"));
                suministroTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                suministroTB.setCantidad(rsEmps.getDouble("Cantidad"));
                suministroTB.setUnidadCompraName(rsEmps.getString("UnidadCompraNombre"));
                suministroTB.setCostoCompra(rsEmps.getDouble("PrecioCompra"));
                suministroTB.setPrecioVentaGeneral(rsEmps.getDouble("PrecioVentaGeneral"));
                suministroTB.setInventario(rsEmps.getBoolean("Inventario"));
                suministroTB.setValorInventario(rsEmps.getShort("ValorInventario"));

                CheckBox checkBox = new CheckBox();
                checkBox.getStyleClass().add("check-box-contenido");
                checkBox.setText("");

                suministroTB.setValidar(checkBox);

                empList.add(suministroTB);
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

    private static String Registrar_Asignacion_Productos_Articulo(String idArticulo, TableView<SuministroTB> tvList) {
        String result = "";
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            CallableStatement statementCodigo = null;
            PreparedStatement statementAsignacion = null;
            PreparedStatement statementAsignacionDetalle = null;
            PreparedStatement statementArticulo = null;
            PreparedStatement statementKardexArticulo = null;
            PreparedStatement statementProducto = null;
            PreparedStatement statementKardexProducto = null;
            try {
                DBUtil.getConnection().setAutoCommit(false);

                statementCodigo = DBUtil.getConnection().prepareCall("{? = call Fc_Asignacion_Codigo_Alfanumerico()}");
                statementCodigo.registerOutParameter(1, java.sql.Types.VARCHAR);
                statementCodigo.execute();
                String idAsignacion = statementCodigo.getString(1);

                statementAsignacion = DBUtil.getConnection().prepareStatement("INSERT INTO AsignacionTB(IdAsignacion,IdArticulo,Hora,Fecha,Cantidad,Costo,Precio)VALUES(?,?,?,?,?,?,?)");
                statementAsignacionDetalle = DBUtil.getConnection().prepareStatement("INSERT INTO AsignacionDetalleTB(IdAsignacion,IdSuministro,Cantidad,Costo,Precio,Movimiento,Estado)VALUES(?,?,?,?,?,?,?)");
                statementArticulo = DBUtil.getConnection().prepareStatement("UPDATE ArticuloTB SET Cantidad = Cantidad + ?,PrecioCompra = ?,PrecioVentaGeneral = ? WHERE IdArticulo = ?");
                statementKardexArticulo = DBUtil.getConnection().prepareStatement("INSERT INTO KardexArticuloTB(IdArticulo,Fecha,Hora,Tipo,Movimiento,Detalle,Cantidad,CUnitario,CTotal) VALUES(?,?,?,?,?,?,?,?,?)");
                statementProducto = DBUtil.getConnection().prepareStatement("UPDATE SuministroTB SET Cantidad = Cantidad - ? WHERE IdSuministro = ?");
                statementKardexProducto = DBUtil.getConnection().prepareStatement("INSERT INTO KardexSuministroTB(IdSuministro,Fecha,Hora,Tipo,Movimiento,Detalle,Cantidad,CUnitario,CTotal) VALUES(?,?,?,?,?,?,?,?,?)");

                double cantidad = 0;
                double costoPromedio = 0;
                double precioPromedio = 0;
                int rows = tvList.getItems().size();

                for (int i = 0; i < tvList.getItems().size(); i++) {
                    cantidad += tvList.getItems().get(i).getMovimiento();
                    costoPromedio += tvList.getItems().get(i).getCostoCompra();
                    precioPromedio += tvList.getItems().get(i).getPrecioVentaGeneral();

                    statementAsignacionDetalle.setString(1, idAsignacion);
                    statementAsignacionDetalle.setString(2, tvList.getItems().get(i).getIdSuministro());
                    statementAsignacionDetalle.setDouble(3, tvList.getItems().get(i).getCantidad());
                    statementAsignacionDetalle.setDouble(4, tvList.getItems().get(i).getCostoCompra());
                    statementAsignacionDetalle.setDouble(5, tvList.getItems().get(i).getPrecioVentaGeneral());
                    statementAsignacionDetalle.setDouble(6, tvList.getItems().get(i).getMovimiento());
                    statementAsignacionDetalle.setBoolean(7, true);
                    statementAsignacionDetalle.addBatch();

                    statementProducto.setDouble(1, tvList.getItems().get(i).getMovimiento());
                    statementProducto.setString(2, tvList.getItems().get(i).getIdSuministro());
                    statementProducto.addBatch();

                    statementKardexProducto.setString(1, tvList.getItems().get(i).getIdSuministro());
                    statementKardexProducto.setString(2, Tools.getDate());
                    statementKardexProducto.setString(3, Tools.getTime("HH:mm:ss"));
                    statementKardexProducto.setShort(4, (short) 2);
                    statementKardexProducto.setInt(5, 1);
                    statementKardexProducto.setString(6, "Asignaci??n de producto(s) a art??culo");
                    statementKardexProducto.setDouble(7, tvList.getItems().get(i).getMovimiento());
                    statementKardexProducto.setDouble(8, tvList.getItems().get(i).getCostoCompra());
                    statementKardexProducto.setDouble(9, tvList.getItems().get(i).getMovimiento() * tvList.getItems().get(i).getCostoCompra());
                    statementKardexProducto.addBatch();
                }

                statementAsignacion.setString(1, idAsignacion);
                statementAsignacion.setString(2, idArticulo);
                statementAsignacion.setString(3, Tools.getDate());
                statementAsignacion.setString(4, Tools.getTime("HH:mm:ss"));
                statementAsignacion.setDouble(5, cantidad);
                statementAsignacion.setDouble(6, (costoPromedio / rows));
                statementAsignacion.setDouble(7, (precioPromedio / rows));
                statementAsignacion.addBatch();

                statementArticulo.setDouble(1, cantidad);
                statementArticulo.setDouble(2, (costoPromedio / rows));
                statementArticulo.setDouble(3, (precioPromedio / rows));
                statementArticulo.setString(4, idArticulo);
                statementArticulo.addBatch();

                statementKardexArticulo.setString(1, idArticulo);
                statementKardexArticulo.setString(2, Tools.getDate());
                statementKardexArticulo.setString(3, Tools.getTime("HH:mm:ss"));
                statementKardexArticulo.setShort(4, (short) 1);
                statementKardexArticulo.setInt(5, 4);
                statementKardexArticulo.setString(6, "Asignaci??n de producto(s) a art??culo");
                statementKardexArticulo.setDouble(7, cantidad);
                statementKardexArticulo.setDouble(8, (costoPromedio / rows));
                statementKardexArticulo.setDouble(9, cantidad * (costoPromedio / rows));
                statementKardexArticulo.addBatch();

                statementAsignacion.executeBatch();
                statementAsignacionDetalle.executeBatch();
                statementProducto.executeBatch();
                statementKardexProducto.executeBatch();
                statementArticulo.executeBatch();
                statementKardexArticulo.executeBatch();
                DBUtil.getConnection().commit();
                result = "inserted";
            } catch (SQLException ex) {
                try {
                    DBUtil.getConnection().rollback();
                } catch (SQLException e) {
                    result = e.getLocalizedMessage();
                }
            } finally {
                try {
                    if (statementCodigo != null) {
                        statementCodigo.close();
                    }
                    if (statementAsignacion != null) {
                        statementAsignacion.close();
                    }
                    if (statementAsignacionDetalle != null) {
                        statementAsignacionDetalle.close();
                    }
                    if (statementArticulo != null) {
                        statementArticulo.close();
                    }
                    if (statementKardexArticulo != null) {
                        statementKardexArticulo.close();
                    }
                    if (statementProducto != null) {
                        statementProducto.close();
                    }
                    if (statementKardexProducto != null) {
                        statementKardexProducto.close();
                    }
                } catch (SQLException ex) {
                    result = ex.getLocalizedMessage();
                }
            }
        } else {
            result = "No se puedo establecer una conexi??n, intente nuevamente.";
        }
        return result;
    }

    public static Object[] Validate_Lote_By_IdSuministro(String idSuministro) {
        Object[] result = new Object[2];
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement statementValidate = null;
            try {
                statementValidate = DBUtil.getConnection().prepareStatement("SELECT Cantidad,Lote FROM SuministroTB WHERE IdSuministro = ?");
                statementValidate.setString(1, idSuministro);
                ResultSet resultSet = statementValidate.executeQuery();
                if (resultSet.next()) {
                    if (resultSet.getDouble("Cantidad") > 0) {
                        result[0] = "mayor";
                        result[1] = resultSet.getBoolean("Lote");
                    }
                }
            } catch (SQLException ex) {
                result[0] = ex.getLocalizedMessage();
            } finally {
                try {
                    if (statementValidate != null) {
                        statementValidate.close();
                    }
                    DBUtil.dbDisconnect();
                } catch (SQLException ex) {

                }
            }
        } else {
            result[0] = "No se puedo establecer una conexi??n con el servidor, intente nuevamente.";
        }
        return result;
    }

    public static String RemoverSuministro(String idSuministro) {
        String result = "";
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            PreparedStatement preparedValidate = null;
            PreparedStatement preparedSuministro = null;
            try {

                DBUtil.getConnection().setAutoCommit(false);
                preparedValidate = DBUtil.getConnection().prepareStatement("SELECT * FROM DetalleCompraTB WHERE IdArticulo = ?");
                preparedValidate.setString(1, idSuministro);
                if (preparedValidate.executeQuery().next()) {
                    DBUtil.getConnection().rollback();
                    result = "compra";
                } else {
                    preparedValidate = DBUtil.getConnection().prepareStatement("SELECT * FROM KardexSuministroTB WHERE IdSuministro = ?");
                    preparedValidate.setString(1, idSuministro);
                    if (preparedValidate.executeQuery().next()) {
                        DBUtil.getConnection().rollback();
                        result = "kardex_compra";
                    } else {
                        preparedValidate = DBUtil.getConnection().prepareStatement("SELECT * FROM DetalleVentaTB WHERE IdArticulo = ?");
                        preparedValidate.setString(1, idSuministro);
                        if (preparedValidate.executeQuery().next()) {
                            DBUtil.getConnection().rollback();
                            result = "venta";
                        } else {
                            preparedSuministro = DBUtil.getConnection().prepareStatement("DELETE FROM SuministroTB WHERE IdSuministro = ?");
                            preparedSuministro.setString(1, idSuministro);
                            preparedSuministro.addBatch();
                            preparedSuministro.executeBatch();
                            DBUtil.getConnection().commit();
                            result = "removed";
                        }
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
                    if (preparedValidate != null) {
                        preparedValidate.close();
                    }
                    if (preparedSuministro != null) {
                        preparedSuministro.close();
                    }
//                    if (preparedBusqueda != null) {
//                        preparedBusqueda.close();
//                    }
//                    if (preparedArticulo != null) {
//                        preparedArticulo.close();
//                    }
                } catch (SQLException ex) {
                    result = ex.getLocalizedMessage();
                }
            }
        } else {
            result = "No se pudo establecer la conexi??n, revise el estado.";
        }
        return result;
    }

    public static ObservableList<PreciosTB> GetItemPriceList(String idSuministro) {
        PreparedStatement statementVendedor = null;
        ObservableList<PreciosTB> empList = FXCollections.observableArrayList();
        DBUtil.dbConnect();
        if (DBUtil.getConnection() != null) {
            try {
                statementVendedor = DBUtil.getConnection().prepareStatement("{CALL Sp_Listar_Precios_By_IdSuministro(?)}");
                statementVendedor.setString(1, idSuministro);
                try (ResultSet resultSet = statementVendedor.executeQuery()) {
                    while (resultSet.next()) {
                        PreciosTB preciosTB = new PreciosTB();
                        preciosTB.setId(resultSet.getRow());
                        preciosTB.setIdPrecios(resultSet.getInt("IdPrecios"));
                        preciosTB.setNombre(resultSet.getString("Nombre").toUpperCase());
                        preciosTB.setValor(resultSet.getDouble("Valor"));
                        preciosTB.setFactor(resultSet.getDouble("Factor") <= 0 ? 1 : resultSet.getDouble("Factor"));
                        empList.add(preciosTB);
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

        return empList;
    }

    public static SuministroTB Get_Suministro_By_Search(String value) {
        String selectStmt = "{call Sp_Listar_Suministro_By_Search(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        SuministroTB suministroTB = null;
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, value);
            rsEmps = preparedStatement.executeQuery();
            if (rsEmps.next()) {
                suministroTB = new SuministroTB();
                suministroTB.setIdSuministro(rsEmps.getString("IdSuministro"));
                suministroTB.setClave(rsEmps.getString("Clave"));
                suministroTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                suministroTB.setMarcaName(rsEmps.getString("Marca"));
                suministroTB.setPresentacionName(rsEmps.getString("Presentacion"));
                suministroTB.setCantidad(rsEmps.getDouble("Cantidad"));
                suministroTB.setCostoCompra(rsEmps.getDouble("PrecioCompra"));
                suministroTB.setPrecioVentaGeneral(rsEmps.getDouble("PrecioVentaGeneral"));
                suministroTB.setUnidadVenta(rsEmps.getInt("UnidadVenta"));
                suministroTB.setLote(rsEmps.getBoolean("Lote"));
                suministroTB.setInventario(rsEmps.getBoolean("Inventario"));
                suministroTB.setImpuestoOperacion(rsEmps.getInt("Operacion"));
                suministroTB.setImpuestoId(rsEmps.getInt("Impuesto"));
                suministroTB.setImpuestoNombre(rsEmps.getString("ImpuestoNombre"));
                suministroTB.setImpuestoValor(rsEmps.getDouble("Valor"));
                suministroTB.setValorInventario(rsEmps.getShort("ValorInventario"));
                suministroTB.setUnidadCompraName(rsEmps.getString("UnidadCompra"));
            }
        } catch (SQLException e) {
            System.out.println("La operaci??n de selecci??n de SQL ha fallado: " + e);
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
        return suministroTB;
    }

    public static List<SuministroTB> getSearchComboBoxSuministros(String buscar, boolean directo) {
        String selectStmt = directo ? "SELECT IdSuministro,Clave,NombreMarca FROM SuministroTB WHERE IdSuministro = ?" : "SELECT IdSuministro,Clave,NombreMarca FROM SuministroTB WHERE Clave LIKE CONCAT(?,'%') OR NombreMarca LIKE CONCAT(?,'%')";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        List<SuministroTB> suministroTBs = new ArrayList<>();
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            if (directo) {
                preparedStatement.setString(1, buscar);
            } else {
                preparedStatement.setString(1, buscar);
                preparedStatement.setString(2, buscar);
            }
            rsEmps = preparedStatement.executeQuery();
            while (rsEmps.next()) {
                SuministroTB suministroTB = new SuministroTB();
                suministroTB.setIdSuministro(rsEmps.getString("IdSuministro"));
                suministroTB.setClave(rsEmps.getString("Clave"));
                suministroTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                suministroTBs.add(suministroTB);
            }

        } catch (SQLException e) {

        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (rsEmps != null) {
                    rsEmps.close();
                }
            } catch (SQLException e) {

            }
        }
        return suministroTBs;
    }

    public static String UpdatedInventarioStockMM(String idSuministro, String stockMinimo, String stockMaximo) {
        PreparedStatement preparedUpdatedSuministro = null;
        DBUtil.dbConnect();
        if (DBUtil.getConnection() == null) {
            return "No se pudo conectar al servidor, intente nuevamente.";
        }
        try {
            DBUtil.getConnection().setAutoCommit(false);

            preparedUpdatedSuministro = DBUtil.getConnection().prepareStatement("UPDATE SuministroTB SET StockMinimo = ? , StockMaximo = ? WHERE IdSuministro = ?");
            preparedUpdatedSuministro.setDouble(1, Double.parseDouble(stockMinimo));
            preparedUpdatedSuministro.setDouble(2, Double.parseDouble(stockMaximo));
            preparedUpdatedSuministro.setString(3, idSuministro);
            preparedUpdatedSuministro.addBatch();

            preparedUpdatedSuministro.executeBatch();
            DBUtil.getConnection().commit();
            return "updated";
        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException e) {
            }
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (preparedUpdatedSuministro != null) {
                    preparedUpdatedSuministro.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException e) {
            }
        }
    }

    public static SuministroTB List_Suministros_Movimiento(String idSuministro) {
        String selectStmt = "{call Sp_Get_Suministro_For_Movimiento(?)}";
        PreparedStatement preparedStatement = null;
        ResultSet rsEmps = null;
        SuministroTB suministroTB = null;
        try {
            DBUtil.dbConnect();
            preparedStatement = DBUtil.getConnection().prepareStatement(selectStmt);
            preparedStatement.setString(1, idSuministro);
            rsEmps = preparedStatement.executeQuery();
            if (rsEmps.next()) {
                suministroTB = new SuministroTB();
                suministroTB.setId(rsEmps.getRow());
                suministroTB.setIdSuministro(rsEmps.getString("IdSuministro"));
                suministroTB.setClave(rsEmps.getString("Clave"));
                suministroTB.setNombreMarca(rsEmps.getString("NombreMarca"));
                suministroTB.setCantidad(rsEmps.getDouble("Cantidad"));
                suministroTB.setUnidadCompraName(rsEmps.getString("UnidadCompraNombre"));
                suministroTB.setCostoCompra(rsEmps.getDouble("PrecioCompra"));
                suministroTB.setPrecioVentaGeneral(rsEmps.getDouble("PrecioVentaGeneral"));
                suministroTB.setInventario(rsEmps.getBoolean("Inventario"));
                suministroTB.setValorInventario(rsEmps.getShort("ValorInventario"));
                suministroTB.setDiferencia(suministroTB.getCantidad());
                suministroTB.setMovimiento(0);
                TextField tf = new TextField(Tools.roundingValue(0.00, 0));
                tf.getStyleClass().add("text-field-normal");
                tf.setOnKeyTyped(event -> {
                    char c = event.getCharacter().charAt(0);
                    if ((c < '0' || c > '9') && (c != '\b') && (c != '.')) {
                        event.consume();
                    }
                    if (c == '.' && tf.getText().contains(".") || c == '-') {
                        event.consume();
                    }
                });

                suministroTB.setTxtMovimiento(tf);

                CheckBox checkbox = new CheckBox("");
                checkbox.getStyleClass().add("check-box-contenido");
                checkbox.setSelected(true);
                checkbox.setDisable(true);

                Button button = new Button();
                button.getStyleClass().add("buttonDark");
                ImageView view = new ImageView(new Image("/view/image/remove.png"));
                view.setFitWidth(24);
                view.setFitHeight(24);
                button.setGraphic(view);
                suministroTB.setBtnRemove(button);

                suministroTB.setValidar(checkbox);

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
        return suministroTB;
    }

}
