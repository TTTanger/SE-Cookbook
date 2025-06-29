package g.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import g.dto.CategoryResponse;
import g.service.CategoryService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;

/**
 * Controller for the category list view. Handles the display and selection of categories.
 * 
 * @author Junzhe Luo
 */
public class CategoryListController implements Initializable {

    /** Service for category operations */
    private final CategoryService categoryService;

    /** ListView for displaying categories */
    @FXML
    private ListView<CategoryResponse> listView;

    /** Label for empty category message on the left */
    private Label leftEmptyLabel;

    /**
     * Constructor initializes the category service.
     */
    public CategoryListController() {
        this.categoryService = new CategoryService();
    }

    /**
     * Fetches all categories from the service.
     * @return List of CategoryResponse objects
     */
    public List<CategoryResponse> fetchAllCategories() {
        System.out.println("Fetching all categories...");
        return categoryService.getAllCategories();
    }

    /**
     * Initializes the controller and sets up the ListView.
     * @param location The location used to resolve relative paths for the root object, or null if unknown.
     * @param resources The resources used to localize the root object, or null if not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("CategoryListController initialized!");
        System.out.println("ListView element is null: " + (listView == null));

        List<CategoryResponse> rawList = fetchAllCategories();
        ObservableList<CategoryResponse> observableList = FXCollections.observableArrayList(rawList);
        listView.setItems(observableList);

        listView.setCellFactory(lv -> new javafx.scene.control.ListCell<CategoryResponse>() {
            @Override
            protected void updateItem(CategoryResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCategoryName()); 
                }
            }
        });

        listView.setOnMouseClicked(event -> {
            CategoryResponse selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null && callback != null) {
                callback.onCategorySelected(selected);
            }
        });
    }

    /**
     * Refreshes the category list in the ListView.
     */
    public void refreshList() {
        List<CategoryResponse> rawList = fetchAllCategories();
        ObservableList<CategoryResponse> observableList = FXCollections.observableArrayList(rawList);
        listView.setItems(observableList);
        if (leftEmptyLabel != null) {
            if (observableList.isEmpty()) {
                leftEmptyLabel.setVisible(true);
                leftEmptyLabel.setManaged(true);
            } else {
                leftEmptyLabel.setVisible(false);
                leftEmptyLabel.setManaged(false);
            }
        }
        System.out.println("Category ListView refreshed!");
    }

    /**
     * Gets the name of the currently selected category.
     * @return The name of the selected category, or null if no category is selected
     */
    public String getSelectedCategoryName() {
        CategoryResponse selected = listView.getSelectionModel().getSelectedItem();
        return selected != null ? selected.getCategoryName() : null;
    }

    /**
     * Callback interface for category selection.
     */
    public interface CategorySelectCallback {
        void onCategorySelected(CategoryResponse item);
    }
    private CategorySelectCallback callback;

    /**
     * Sets the callback for category selection.
     * @param callback The callback to set
     */
    public void setOnItemSelected(CategorySelectCallback callback) {
        this.callback = callback;
    }

    public void setLeftEmptyLabel(Label label) {
        this.leftEmptyLabel = label;
    }
}