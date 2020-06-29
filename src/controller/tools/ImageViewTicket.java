package controller.tools;

import javafx.scene.image.ImageView;

public class ImageViewTicket extends ImageView {

    private short columnWidth;

    private String variable;

    private byte[] url;
    
    public ImageViewTicket() {
        super();
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

    public byte[] getUrl() {
        return url;
    }

    public void setUrl(byte[] url) {
        this.url = url;
    }
    

}
