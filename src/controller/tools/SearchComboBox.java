
package controller.tools;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;

public class SearchComboBox{
    
    private final ComboBox comboBox;
    private final FilteredList<String> filterList;
    private BiPredicate<String, String> filter;

    public SearchComboBox(ComboBox comboBox) {
        this.comboBox = comboBox;
        this.filterList = new FilteredList<>(FXCollections.observableArrayList());
        this.filter = (i, s) -> true;
        this.comboBox.setSkin(new SearchComboBoxSkin(this));
    }

    public void setFilter(BiPredicate<String, String> filter) {
        this.filter = filter;
    }

    public BiPredicate<String, String> getFilter() {
        return filter;
    }

    public void setPredicateFilter(Predicate<String> predicate) {
        filterList.setPredicate(predicate);
    }

    public FilteredList<String> getFilterList() {
        return filterList;
    }

    public ComboBox getComboBox() {
        return comboBox;
    }
    
    

}
