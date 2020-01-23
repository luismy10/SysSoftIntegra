

package controller.tools;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;


public class HBoxModel extends HBox{
    
    private MenuItem menuItem;

    public HBoxModel() {
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }    
    
}
