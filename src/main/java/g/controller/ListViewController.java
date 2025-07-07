package g.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import g.dto.RecipeSummaryResponse;
import g.service.CategoryService;
import g.service.RecipeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;

/**
 * Controller for the recipe list view. This class handles the display and selection of recipes,
 * including loading recipes by category, searching, and managing the ListView component.
 *
 * @author Junzhe Luo
 * @since 2025-6-15
 */
public class ListViewController implements Initializable {

    /** Logger for logging messages */
    private static final Logger LOGGER = Logger.getLogger(ListViewController.class.getName());

    /** Service for recipe operations */
    private final RecipeService recipeService;
    
    /** Service for category operations */
    private final CategoryService categoryService;

    /** ListView for displaying recipes */
    @FXML
    private ListView<RecipeSummaryResponse> listView;

    /** Label for empty recipe list in center pane */
    private Label centerEmptyLabel;

    /** Callback for recipe selection events */
    private ActionCallback callback;

    /**
     * Constructor initializes the recipe and category services.
     */
    public ListViewController() {
        this.recipeService = new RecipeService();
        this.categoryService = new CategoryService();
    }

    /**
     * Fetches all recipe summaries from the service.
     * 
     * @return List of RecipeSummaryResponse objects
     */
    public List<RecipeSummaryResponse> fetchAllRecipeSummary() {
        LOGGER.info("Fetching recipe summary");
        return recipeService.getAllRecipeSummary();
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
        LOGGER.info("ListViewController initialized");
        LOGGER.info("ListView element is null: " + (listView == null));

        setupListView();
        setupMouseClickHandler();
    }

    /**
     * Sets up the ListView with initial data and cell factory.
     */
    private void setupListView() {
        List<RecipeSummaryResponse> rawList = fetchAllRecipeSummary();
        ObservableList<RecipeSummaryResponse> observableList = FXCollections.observableArrayList(rawList);
        listView.setItems(observableList);

        listView.setCellFactory(lv -> new javafx.scene.control.ListCell<RecipeSummaryResponse>() {
            @Override
            protected void updateItem(RecipeSummaryResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle());
                }
            }
        });
    }

    /**
     * Sets up the mouse click handler for recipe selection.
     */
    private void setupMouseClickHandler() {
        listView.setOnMouseClicked(event -> {
            RecipeSummaryResponse selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null && callback != null) {
                callback.onRecipeSelected(selected.getRecipeId());
            }
        });
    }

    /**
     * Loads recipes by category ID and displays them in the ListView.
     * 
     * @param categoryId The category ID
     */
    public void loadRecipesByCategory(int categoryId) {
        LOGGER.info("Loading recipes for category ID: " + categoryId);
        List<RecipeSummaryResponse> rawList = categoryService.getRecipeSummaryByCategoryId(categoryId);
        ObservableList<RecipeSummaryResponse> observableList = FXCollections.observableArrayList(rawList);
        listView.setItems(observableList);
        LOGGER.info("ListView loaded recipes for the category");

        updateEmptyLabelVisibility(observableList.isEmpty());
    }

    /**
     * Refreshes the recipe list with all recipes.
     */
    public void refreshList() {
        List<RecipeSummaryResponse> rawList = fetchAllRecipeSummary();
        ObservableList<RecipeSummaryResponse> observableList = FXCollections.observableArrayList(rawList);
        listView.setItems(observableList);
        LOGGER.info("ListView refreshed");

        updateEmptyLabelVisibility(observableList.isEmpty());
    }

    /**
     * Refreshes the recipe list for a specific category.
     * 
     * @param categoryId The category ID
     */
    public void refreshListInCategory(int categoryId) {
        LOGGER.info("Refreshing list in category ID: " + categoryId);
        List<RecipeSummaryResponse> rawList = categoryService.getRecipeSummaryByCategoryId(categoryId);
        ObservableList<RecipeSummaryResponse> observableList = FXCollections.observableArrayList(rawList);
        listView.setItems(observableList);
        LOGGER.info("ListView refreshed in category");

        updateEmptyLabelVisibility(observableList.isEmpty());
    }

    /**
     * Refreshes the recipe list and retains the selection of a specific recipe.
     * 
     * @param recipeIdToKeepSelected The recipe ID to keep selected
     */
    public void refreshListAndRetainSelection(int recipeIdToKeepSelected) {
        refreshList();

        for (RecipeSummaryResponse item : listView.getItems()) {
            if (item.getRecipeId() == recipeIdToKeepSelected) {
                listView.getSelectionModel().select(item);
                break;
            }
        }
    }

    /**
     * Searches recipes by keyword and updates the ListView.
     * 
     * @param keyword The search keyword
     */
    public void search(String keyword) {
        List<RecipeSummaryResponse> rawList = fetchAllRecipeSummary();
        List<RecipeSummaryResponse> filteredList;
        
        if (keyword == null || keyword.isBlank()) {
            filteredList = rawList;
        } else {
            filteredList = rawList.stream()
                    .filter(item -> item.getTitle() != null && 
                            item.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();
        }
        
        ObservableList<RecipeSummaryResponse> observableList = FXCollections.observableArrayList(filteredList);
        listView.setItems(observableList);
        LOGGER.info("ListView filtered and refreshed by keyword");

        updateEmptyLabelVisibility(observableList.isEmpty());
    }

    /**
     * Searches recipes in a specific category by keyword and updates the ListView.
     * 
     * @param categoryId The category ID
     * @param keyword The search keyword
     */
    public void searchInCategory(int categoryId, String keyword) {
        List<RecipeSummaryResponse> rawList = categoryService.getRecipeSummaryByCategoryId(categoryId);
        List<RecipeSummaryResponse> filteredList;
        
        if (keyword == null || keyword.isBlank()) {
            filteredList = rawList;
        } else {
            filteredList = rawList.stream()
                    .filter(item -> item.getTitle() != null && 
                            item.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();
        }
        
        ObservableList<RecipeSummaryResponse> observableList = FXCollections.observableArrayList(filteredList);
        listView.setItems(observableList);
        LOGGER.info("ListView filtered and refreshed by category and keyword");

        updateEmptyLabelVisibility(observableList.isEmpty());
    }

    /**
     * Clears the recipe list in the ListView.
     */
    public void clearList() {
        listView.getItems().clear();
        if (centerEmptyLabel != null) {
            centerEmptyLabel.setVisible(false);
            centerEmptyLabel.setManaged(false);
        }
    }

    /**
     * Updates the visibility of the empty label based on whether the list is empty.
     * 
     * @param isEmpty true if the list is empty, false otherwise
     */
    private void updateEmptyLabelVisibility(boolean isEmpty) {
        if (centerEmptyLabel != null) {
            centerEmptyLabel.setVisible(isEmpty);
            centerEmptyLabel.setManaged(isEmpty);
        }
    }

    /**
     * Callback interface for recipe item selection.
     * 
     * @author Junzhe Luo
     * @since 2025-6-15
     */
    public interface ActionCallback {
        /**
         * Called when a recipe is selected from the list.
         * 
         * @param recipeId the ID of the selected recipe
         */
        void onRecipeSelected(int recipeId);
    }

    /**
     * Sets the callback for recipe selection.
     * 
     * @param callback The callback to set
     */
    public void setCallback(ActionCallback callback) {
        this.callback = callback;
    }

    /**
     * Sets the visibility and managed state of the ListView.
     * 
     * @param visible true to show the ListView, false to hide it
     */
    public void setListViewVisible(boolean visible) {
        listView.setVisible(visible);
        listView.setManaged(visible);
    }

    /**
     * Sets the label for empty recipe list in center pane.
     * 
     * @param label the label to set
     */
    public void setCenterEmptyLabel(Label label) {
        this.centerEmptyLabel = label;
    }
}
