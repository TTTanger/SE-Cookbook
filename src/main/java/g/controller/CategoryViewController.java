package g.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;


import g.service.CategoryService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

/**
 * Controller for the category view. This class handles the display and interaction 
 * of categories and recipes, including category management operations and 
 * recipe browsing within categories.
 *
 * @author Junzhe Luo
 * @since 2025-6-15
 */
public class CategoryViewController implements Initializable {

    /** Logger for logging messages */
    private static final Logger LOGGER = Logger.getLogger(CategoryViewController.class.getName());
    
    /** No category selected */
    private static final int NO_CATEGORY_SELECTED = -1;

    /** VBox for the left pane (category list) */
    @FXML
    private VBox leftPane;
    
    /** VBox for the right pane (recipe detail) */
    @FXML
    private VBox rightPane;

    /** Controller for the category list */
    @FXML
    private CategoryListController categoryListController;
    
    /** Controller for the recipe list */
    @FXML
    private ListViewController listViewController;
    
    /** Controller for the recipe detail card */
    @FXML
    private RecipeDetailCardController recipeDetailCardController;
    
    /** Controller for the search bar */
    @FXML
    private SearchBarController searchBarController;
    
    /** Label for empty category message */
    @FXML
    private Label categoryEmptyLabel;
    
    /** Button for updating category */
    @FXML
    private javafx.scene.control.Button updateCategoryButton;
    
    /** Button for deleting category */
    @FXML
    private javafx.scene.control.Button deleteCategoryButton;
    
    /** Label for empty left pane */
    @FXML
    private Label leftEmptyLabel;
    
    /** Label for empty recipe list in center pane */
    @FXML
    private Label centerEmptyLabel;

    /** The currently selected category ID, -1 means none selected */
    private int currentCategoryId = NO_CATEGORY_SELECTED;

    /**
     * Initializes the controller and sets up callbacks for category and recipe selection.
     * This method configures the interaction between different components and sets up
     * the initial view state.
     * 
     * @param location The location used to resolve relative paths for the root object, or null if unknown
     * @param resources The resources used to localize the root object, or null if not localized
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupLabels();
        setupCategorySelectionCallback();
        setupRecipeSelectionCallback();
        setupSearchCallback();
        setupInitialState();
        recipeDetailCardController.setCallback(new RecipeDetailCardController.DetailCallback() {
            @Override
            public void onRecipeDeleted(int recipeId) {
                listViewController.loadRecipesByCategory(currentCategoryId);
                recipeDetailCardController.showEmptyMessage();
            }
            @Override
            public void onRecipeUpdated(int recipeId) {
                listViewController.loadRecipesByCategory(currentCategoryId);
                recipeDetailCardController.loadRecipeData(recipeId);
            }
            @Override
            public void onRecipeCategorized() {
                listViewController.loadRecipesByCategory(currentCategoryId);
            }
            @Override
            public void onBack() {
                recipeDetailCardController.showEmptyMessage();
            }
        });
    }

    /**
     * Sets up the labels for empty states.
     */
    private void setupLabels() {
        categoryListController.setLeftEmptyLabel(leftEmptyLabel);
        listViewController.setCenterEmptyLabel(centerEmptyLabel);
    }

    /**
     * Sets up the callback for category selection.
     */
    private void setupCategorySelectionCallback() {
        categoryListController.setOnItemSelected(category -> {
            if (category != null) {
                currentCategoryId = category.getCategoryId();
                listViewController.loadRecipesByCategory(currentCategoryId);
                recipeDetailCardController.showEmptyMessage(); 
                categoryEmptyLabel.setVisible(false);
                categoryEmptyLabel.setManaged(false);
                listViewController.setListViewVisible(true);
            } else {
                currentCategoryId = NO_CATEGORY_SELECTED;
                listViewController.clearList(); 
                recipeDetailCardController.showEmptyMessage();
                categoryEmptyLabel.setText("Select a Category");
                categoryEmptyLabel.setVisible(true);
                categoryEmptyLabel.setManaged(true);
                listViewController.setListViewVisible(false);
            }
        });
    }

    /**
     * Sets up the callback for recipe selection.
     */
    private void setupRecipeSelectionCallback() {
        listViewController.setCallback(recipeId -> {
            if (recipeId != NO_CATEGORY_SELECTED) {
                recipeDetailCardController.loadRecipeData(recipeId);
            } else {
                recipeDetailCardController.showEmptyMessage(); 
            }
        });
    }

    /**
     * Sets up the callback for search functionality.
     */
    private void setupSearchCallback() {
        searchBarController.setCallback(keyword -> {
            if (currentCategoryId > 0) {
                listViewController.searchInCategory(currentCategoryId, keyword);
            } else {
                listViewController.clearList(); 
            }
            recipeDetailCardController.showEmptyMessage();
        });
    }

    /**
     * Sets up the initial state of the view.
     */
    private void setupInitialState() {
        listViewController.clearList();
        recipeDetailCardController.showEmptyMessage();
        categoryEmptyLabel.setVisible(true);
        categoryEmptyLabel.setManaged(true);
        listViewController.setListViewVisible(false);
    }

    /**
     * Handles the create category button click event.
     * This method shows a dialog to input the category name and creates a new category.
     */
    @FXML
    public void onCreateClicked() {
        LOGGER.info("Create category button clicked");
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New Category");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter the category name:");

        dialog.showAndWait().ifPresent(name -> {
            if (name != null && !name.trim().isEmpty()) {
                boolean success = new CategoryService().createCategory(name.trim());
                if (success) {
                    LOGGER.info("Category created successfully: " + name);
                    showAlert(Alert.AlertType.INFORMATION, "Category created successfully!");
                    categoryListController.refreshList();
                } else {
                    LOGGER.warning("Failed to create category: " + name);
                    showAlert(Alert.AlertType.ERROR, "Failed to create category!");
                }
            } else {
                LOGGER.warning("Attempted to create category with empty name");
                showAlert(Alert.AlertType.WARNING, "Category name cannot be empty!");
            }
        });
    }

    /**
     * Handles the update category button click event.
     * This method shows a dialog to update the selected category name.
     */
    @FXML
    public void onUpdateClicked() {
        LOGGER.info("Update category button clicked");
        if (currentCategoryId == NO_CATEGORY_SELECTED) {
            showAlert(Alert.AlertType.WARNING, "Please select a category to update!");
            return;
        }

        String currentName = categoryListController.getSelectedCategoryName();
        if (currentName == null) {
            LOGGER.warning("Failed to get selected category name");
            showAlert(Alert.AlertType.ERROR, "Failed to get selected category!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(currentName);
        dialog.setTitle("Update Category");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter the new category name:");

        dialog.showAndWait().ifPresent(newName -> {
            if (newName != null && !newName.trim().isEmpty()) {
                boolean success = new CategoryService().updateCategory(currentCategoryId, newName.trim());
                if (success) {
                    LOGGER.info("Category updated successfully: " + currentName + " -> " + newName);
                    showAlert(Alert.AlertType.INFORMATION, "Category updated successfully!");
                    categoryListController.refreshList();
                } else {
                    LOGGER.warning("Failed to update category: " + currentName);
                    showAlert(Alert.AlertType.ERROR, "Failed to update category!");
                }
            } else {
                LOGGER.warning("Attempted to update category with empty name");
                showAlert(Alert.AlertType.WARNING, "Category name cannot be empty!");
            }
        });
    }

    /**
     * Handles the delete category button click event.
     * This method shows a confirmation dialog and deletes the selected category.
     */
    @FXML
    public void onDeleteClicked() {
        LOGGER.info("Delete category button clicked");
        if (currentCategoryId == NO_CATEGORY_SELECTED) {
            showAlert(Alert.AlertType.WARNING, "Please select a category to delete!");
            return;
        }

        String categoryName = categoryListController.getSelectedCategoryName();
        if (categoryName == null) {
            LOGGER.warning("Failed to get selected category name for deletion");
            showAlert(Alert.AlertType.ERROR, "Failed to get selected category!");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete category '" + categoryName + "'? This action cannot be undone.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = new CategoryService().deleteCategory(currentCategoryId);
                if (success) {
                    LOGGER.info("Category deleted successfully: " + categoryName);
                    showAlert(Alert.AlertType.INFORMATION, "Category deleted successfully!");
                    currentCategoryId = NO_CATEGORY_SELECTED;
                    categoryListController.refreshList();
                    listViewController.clearList();
                    recipeDetailCardController.showEmptyMessage();
                    categoryEmptyLabel.setVisible(true);
                    categoryEmptyLabel.setManaged(true);
                    listViewController.setListViewVisible(false);
                } else {
                    LOGGER.warning("Failed to delete category: " + categoryName);
                    showAlert(Alert.AlertType.ERROR, "Failed to delete category!");
                }
            }
        });
    }

    /**
     * Shows an alert dialog with the specified type and message.
     * 
     * @param alertType the type of alert to show
     * @param message the message to display
     */
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message);
        switch (alertType) {
            case ERROR -> alert.setTitle("Error");
            case WARNING -> alert.setTitle("Warning");
            case CONFIRMATION -> alert.setTitle("Confirm");
            default -> alert.setTitle("Info");
        }
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    /**
     * Refreshes the data when CategoryView is shown.
     */
    public void refreshData() {
        if (categoryListController != null) {
            categoryListController.refreshList();
        }
        if (listViewController != null && currentCategoryId > 0) {
            listViewController.loadRecipesByCategory(currentCategoryId);
        } else if (listViewController != null) {
            listViewController.clearList();
        }
    }
}