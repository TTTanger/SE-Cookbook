package g.controller;

import java.net.URL;
import java.util.ResourceBundle;

import g.service.CategoryService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

/**
 * Controller for the category view. Handles the display and interaction of categories and recipes.
 *
 * @author Junzhe Luo
 */
public class CategoryViewController implements Initializable {

    /** VBox for the left pane (category list) */
    @FXML
    private VBox leftPane;
    /** VBox for the right pane (recipe detail) */
    @FXML
    private VBox rightPane;

    // Controllers for included FXML components
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
    private int currentCategoryId = -1;

    /**
     * Initializes the controller and sets up callbacks for category and recipe selection.
     * @param location The location used to resolve relative paths for the root object, or null if unknown.
     * @param resources The resources used to localize the root object, or null if not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoryListController.setLeftEmptyLabel(leftEmptyLabel);
        listViewController.setCenterEmptyLabel(centerEmptyLabel);
        // Listen for category selection on the left
        categoryListController.setOnItemSelected(category -> {
            if (category != null) {
                currentCategoryId = category.getCategoryId();
                listViewController.loadRecipesByCategory(currentCategoryId);
                recipeDetailCardController.showEmptyMessage(); // Clear right detail when switching category
                // Show list, hide empty message
                categoryEmptyLabel.setVisible(false);
                categoryEmptyLabel.setManaged(false);
                listViewController.setListViewVisible(true);
            } else {
                currentCategoryId = -1;
                listViewController.clearList(); // Clear middle list when no category selected
                recipeDetailCardController.showEmptyMessage();
                // Show empty message, hide list
                categoryEmptyLabel.setText("Select a Category");
                categoryEmptyLabel.setVisible(true);
                categoryEmptyLabel.setManaged(true);
                listViewController.setListViewVisible(false);
            }
        });

        // Listen for recipe selection in the middle
        listViewController.setCallback(recipeId -> {
            if (recipeId != -1) {
                recipeDetailCardController.loadRecipeData(recipeId);
            } else {
                recipeDetailCardController.showEmptyMessage(); // Show prompt when no recipe selected
            }
        });

        // Search bar callback: combine with current category
        searchBarController.setCallback(keyword -> {
            if (currentCategoryId > 0) {
                listViewController.searchInCategory(currentCategoryId, keyword);
            } else {
                listViewController.clearList(); // Clear if no category selected
            }
            recipeDetailCardController.showEmptyMessage();
        });

        // Clear on initialization
        listViewController.clearList();
        recipeDetailCardController.showEmptyMessage();
        // Default: show empty message, hide list
        categoryEmptyLabel.setVisible(true);
        categoryEmptyLabel.setManaged(true);
        listViewController.setListViewVisible(false);
    }

    /**
     * Handles the create category button click event.
     */
    @FXML
    public void onCreateClicked() {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Create New Category");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter the category name:");

        dialog.showAndWait().ifPresent(name -> {
            if (name != null && !name.trim().isEmpty()) {
                boolean success = new CategoryService().createCategory(name.trim());
                if (success) {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION, "Category created successfully!");
                    alert.showAndWait();
                    // Optionally refresh category list
                    categoryListController.refreshList();
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, "Failed to create category!");
                    alert.showAndWait();
                }
            } else {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING, "Category name cannot be empty!");
                alert.showAndWait();
            }
        });
    }

    /**
     * Handles the update category button click event.
     */
    @FXML
    public void onUpdateClicked() {
        if (currentCategoryId == -1) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING, "Please select a category to update!");
            alert.showAndWait();
            return;
        }

        // Get the selected category name
        String currentName = categoryListController.getSelectedCategoryName();
        if (currentName == null) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, "Failed to get selected category!");
            alert.showAndWait();
            return;
        }

        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog(currentName);
        dialog.setTitle("Update Category");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter the new category name:");

        dialog.showAndWait().ifPresent(newName -> {
            if (newName != null && !newName.trim().isEmpty()) {
                boolean success = new CategoryService().updateCategory(currentCategoryId, newName.trim());
                if (success) {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION, "Category updated successfully!");
                    alert.showAndWait();
                    // Refresh category list
                    categoryListController.refreshList();
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, "Failed to update category!");
                    alert.showAndWait();
                }
            } else {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING, "Category name cannot be empty!");
                alert.showAndWait();
            }
        });
    }

    /**
     * Handles the delete category button click event.
     */
    @FXML
    public void onDeleteClicked() {
        if (currentCategoryId == -1) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING, "Please select a category to delete!");
            alert.showAndWait();
            return;
        }

        // Get the selected category name
        String categoryName = categoryListController.getSelectedCategoryName();
        if (categoryName == null) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, "Failed to get selected category!");
            alert.showAndWait();
            return;
        }

        javafx.scene.control.Alert confirmAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete category '" + categoryName + "'? This action cannot be undone.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                boolean success = new CategoryService().deleteCategory(currentCategoryId);
                if (success) {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION, "Category deleted successfully!");
                    alert.showAndWait();
                    // Clear current selection and refresh list
                    currentCategoryId = -1;
                    categoryListController.refreshList();
                    listViewController.clearList();
                    recipeDetailCardController.showEmptyMessage();
                    categoryEmptyLabel.setVisible(true);
                    categoryEmptyLabel.setManaged(true);
                    listViewController.setListViewVisible(false);
                } else {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, "Failed to delete category!");
                    alert.showAndWait();
                }
            }
        });
    }

    /**
     * Refreshes the recipe list in the current category.
     */
    @FXML
    public void refreshList() {
        listViewController.refreshListInCategory(currentCategoryId);
    }
}