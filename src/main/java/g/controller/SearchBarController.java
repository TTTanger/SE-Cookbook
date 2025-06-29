package g.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

/**
 * Controller for the search bar component.
 * Handles search functionality and clear search operations.
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
     * Execute search logic
     */
    @FXML
    public void performSearch(ActionEvent event) {
        String keyword = searchField.getText();
        if (callback != null) {
            callback.onSearch(keyword == null ? "" : keyword.trim());
        }
    }

    /**
     * Clear search field
     */
    @FXML
    public void clearSearch(ActionEvent event) {
        searchField.clear();
        if (callback != null) {
            callback.onSearch(""); // Trigger search with empty string to clear results
        }
    }

    /**
     * Handle enter key press in search field
     */
    @FXML
    public void onEnterPressed() {
        performSearch(null);
    }

    /**
     * Set the search callback
     */
    public void setCallback(SearchCallback callback) {
        this.callback = callback;
    }

    /**
     * Callback interface for search operations
     */
    public interface SearchCallback {
        void onSearch(String keyword);
    }
}
