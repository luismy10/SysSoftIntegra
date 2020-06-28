
package controller.tools;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageViewTicket extends ImageView{

    private short columnWidth;

    private String variable;

    public ImageViewTicket(Image image) {
        super(image);
    }
    
    public short getColumnWidth() {
        return columnWidth;
    }

    public void setColumnWidth(short columnWidth) {
        this.columnWidth = columnWidth;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }
    
    

}
