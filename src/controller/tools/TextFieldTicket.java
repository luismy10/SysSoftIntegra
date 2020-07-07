package controller.tools;

import javafx.scene.control.TextField;

public class TextFieldTicket extends TextField {

    private short columnWidth;

    private boolean multilineas;

    private short lines;
    
    private String variable;
    
    private String fontName;

    public TextFieldTicket(String title, String id) {
        super(title);
        setId(id);
    }

    public void setPreferredSize(double width, double height) {
        //setMinWidth(width);
        setPrefWidth(width);
        setPrefHeight(height);
    }

    public short getColumnWidth() {
        return columnWidth;
    }

    public void setColumnWidth(short columnWidth) {
        this.columnWidth = columnWidth;
    }

    public boolean isMultilineas() {
        return multilineas;
    }

    public void setMultilineas(boolean multilineas) {
        this.multilineas = multilineas;
    }

    public short getLines() {
        return lines;
    }

    public void setLines(short lines) {
        this.lines = lines;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

}