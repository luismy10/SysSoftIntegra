package model;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ImageADO {

    public static String CrudImage(ImagenTB imagenTB) {
        PreparedStatement preparedImagen = null;
        PreparedStatement preparedValidation = null;
        try {
            DBUtil.dbConnect();
            DBUtil.getConnection().setAutoCommit(false);
            preparedValidation = DBUtil.getConnection().prepareStatement("select IdImagen from ImagenTB where IdImagen=? and IdRelacionado=?");
            preparedValidation.setLong(1, imagenTB.getIdImage());
            preparedValidation.setString(2, imagenTB.getIdRelacionado());
            if (preparedValidation.executeQuery().next()) {
                preparedImagen = DBUtil.getConnection().prepareCall("update ImagenTB set Imagen=? where IdImagen=? and IdRelacionado=?");
                preparedImagen.setBinaryStream(1, imagenTB.getFile());
                preparedImagen.setLong(2, imagenTB.getIdImage());
                preparedImagen.setString(3, imagenTB.getIdRelacionado());
                preparedImagen.addBatch();
                preparedImagen.executeBatch();
                DBUtil.getConnection().commit();
                return "update";
            } else {
                preparedImagen = DBUtil.getConnection().prepareCall("insert into ImagenTB(Imagen,IdRelacionado) values(?,?)");
                preparedImagen.setBinaryStream(1, imagenTB.getFile());
                preparedImagen.setString(2, imagenTB.getIdRelacionado());
                preparedImagen.addBatch();
                preparedImagen.executeBatch();
                DBUtil.getConnection().commit();
                return "insert";
            }

        } catch (SQLException ex) {
            try {
                DBUtil.getConnection().rollback();
            } catch (SQLException exr) {
                return exr.getLocalizedMessage();
            }
            return ex.getLocalizedMessage();
        } finally {
            try {
                if (preparedImagen != null) {
                    preparedImagen.close();
                }
                if (preparedValidation != null) {
                    preparedValidation.close();
                }
                DBUtil.dbDisconnect();
            } catch (SQLException ex) {
                return ex.getLocalizedMessage();
            }
        }
    }


}
