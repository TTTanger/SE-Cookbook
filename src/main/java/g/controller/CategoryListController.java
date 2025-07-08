package g.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;


import g.dto.CategoryResponse;
import g.service.CategoryService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;

/**
 * Controller for the category list view. This class handles the display and selection 
 * of categories, including loading categories from the service and managing the ListView.
 * 
 * @author Junzhe Luo
 * @since 2025-6-15
 */
public class CategoryListController implements Initializable {

    /** Logger for logging messages */
    private static final Logger LOGGER = Logger.getLogger(CategoryListController.class.getName());

    /** Service for category operations */
    private final CategoryService categoryService;

    /** ListView for displaying categories */
    @FXML
    private ListView<CategoryResponse> listView;

    /** Label for empty category message on the left */
    private Label leftEmptyLabel;

    /** Callback for category selection events */
    private CategorySelectCallback callback;

    /**
     * Constructor initializes the category service.
     */
    public CategoryListController() {
        this.categoryService = new CategoryService();
    }

    /**
     * Fetches all categories from the service.
     * 
     * @return List of CategoryResponse objects
     */
    public List<CategoryResponse> fetchAllCategories() {
        LOGGER.info("Fetching all categories");
        return categoryService.getAllCategories();
    }

    /**
     * Initializes the controller and sets up the ListView.
     * This method configures the ListView with cell factory and mouse click handler.
     * 
     * @param location The location used to resolve relative paths for the root object, or null if unknown
     * @param resources The resources used to localize the root object, or null if not localized
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.info("CategoryListController initialized");
        LOGGER.info("ListView element is null: " + (listView == null));

        setupListView();
        setupMouseClickHandler();
    }

    /**
     * Sets up the ListView with initial data and cell factory.
     */
    private void setupListView() {
        List<CategoryResponse> rawList = fetchAllCategories();
        ObservableList<CategoryResponse> observableList = FXCollections.observableArrayList(rawList);
        listView.setItems(observableList);

        listView.setCellFactory(lv -> new javafx.scene.control.ListCell<CategoryResponse>() {
            private final javafx.scene.layout.VBox card = new javafx.scene.layout.VBox();
            private final javafx.scene.control.Label nameLabel = new javafx.scene.control.Label();
            {
                card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 16; -fx-effect: dropshadow(gaussian, #dee2e6, 4, 0.1, 0, 2); -fx-cursor: hand;");
                card.setSpacing(4);
                nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #228be6;");
                nameLabel.setWrapText(true);
                card.getChildren().add(nameLabel);
                card.setOnMouseEntered(e -> {
                    card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 12; -fx-padding: 16; -fx-effect: dropshadow(gaussian, #a5d8ff, 8, 0.2, 0, 4); -fx-cursor: hand; -fx-scale-x: 1.02; -fx-scale-y: 1.02;");
                });
                card.setOnMouseExited(e -> {
                    card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 16; -fx-effect: dropshadow(gaussian, #dee2e6, 4, 0.1, 0, 2); -fx-cursor: hand; -fx-scale-x: 1.0; -fx-scale-y: 1.0;");
                });
                setGraphic(card);
            }
            @Override
            protected void updateItem(CategoryResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    nameLabel.setText(item.getCategoryName());
                    setGraphic(card);
                }
            }
        });
    }

    /**
     * Sets up the mouse click handler for category selection.
     */
    private void setupMouseClickHandler() {
        listView.setOnMouseClicked(event -> {
            CategoryResponse selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null && callback != null) {
                callback.onCategorySelected(selected);
            }
        });
    }

    /**
     * Refreshes the category list in the ListView.
     * This method reloads all categories and updates the empty label visibility.
     */
    public void refreshList() {
        List<CategoryResponse> rawList = fetchAllCategories();
        ObservableList<CategoryResponse> observableList = FXCollections.observableArrayList(rawList);
        listView.setItems(observableList);
        
        updateEmptyLabelVisibility(observableList.isEmpty());
        LOGGER.info("Category ListView refreshed");
    }

    /**
     * Gets the name of the currently selected category.
     * 
     * @return The name of the selected category, or null if no category is selected
     */
    public String getSelectedCategoryName() {
        CategoryResponse selected = listView.getSelectionModel().getSelectedItem();
        return selected != null ? selected.getCategoryName() : null;
    }

    /**
     * Updates the visibility of the empty label based on whether the list is empty.
     * 
     * @param isEmpty true if the list is empty, false otherwise
     */
    private void updateEmptyLabelVisibility(boolean isEmpty) {
        if (leftEmptyLabel != null) {
            leftEmptyLabel.setVisible(isEmpty);
            leftEmptyLabel.setManaged(isEmpty);
        }
    }

    /**
     * Callback interface for category selection.
     * 
     * @author Junzhe Luo
     * @since 2025-6-15
     */
    public interface CategorySelectCallback {
        /**
         * Called when a category is selected from the list.
         * 
         * @param item the selected category
         */
        void onCategorySelected(CategoryResponse item);
    }

    /**
     * Sets the callback for category selection.
     * 
     * @param callback The callback to set
     */
    public void setOnItemSelected(CategorySelectCallback callback) {
        this.callback = callback;
    }

    /**
     * Sets the label for empty category list on the left pane.
     * 
     * @param label the label to set
     */
    public void setLeftEmptyLabel(Label label) {
        this.leftEmptyLabel = label;
    }
}