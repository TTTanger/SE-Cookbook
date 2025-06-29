package g.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

/**
 * Controller for the search bar component.
 * This class handles search functionality including performing searches,
 * clearing search results, and managing search callbacks.
 * 
 * @author Junzhe Luo
 * @since 2025-6-15
 */
public class SearchBarController {

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button clearButton;

    private SearchCallback callback;

    /**
     * Performs a search operation using the current search field text.
     * This method is called when the search button is clicked or
     * when the enter key is pressed in the search field.
     * 
     * @param event the action event that triggered the search
     */
    @FXML
    public void performSearch(ActionEvent event) {
        String keyword = searchField.getText();
        if (callback != null) {
            callback.onSearch(keyword == null ? "" : keyword.trim());
        }
    }

    /**
     * Clears the search field and triggers a search with an empty string
     * to reset the search results.
     * 
     * @param event the action event that triggered the clear operation
     */
    @FXML
    public void clearSearch(ActionEvent event) {
        searchField.clear();
        if (callback != null) {
            callback.onSearch(""); // Trigger search with empty string to clear results
        }
    }

    /**
     * Sets the search callback interface to handle search operations.
     * 
     * @param callback the search callback to be set
     */
    public void setCallback(SearchCallback callback) {
        this.callback = callback;
    }

    /**
     * Callback interface for search operations.
     * Implementations of this interface will handle the actual search logic.
     * 
     * @author Junzhe Luo
     * @since 2025-6-15
     */
    public interface SearchCallback {
        /**
         * Called when a search operation is performed.
         * 
         * @param keyword the search keyword (trimmed and non-null)
         */
        void onSearch(String keyword);
    }
}
