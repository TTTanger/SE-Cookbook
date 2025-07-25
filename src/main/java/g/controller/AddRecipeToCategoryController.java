package g.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import java.util.logging.Level;

import g.dto.CategoryResponse;
import g.service.CategoryService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for adding recipes to categories. This class provides a dialog interface
 * for managing the category associations of a specific recipe through checkboxes.
 * 
 * @author Junzhe Luo
 * @since 2025-6-15
 */
public class AddRecipeToCategoryController implements Initializable {

    /** Logger for logging messages */
    private static final Logger LOGGER = Logger.getLogger(AddRecipeToCategoryController.class.getName());
    
    /** Invalid recipe ID */
    private static final int INVALID_RECIPE_ID = -1;

    /** Container for category checkboxes */
    @FXML
    private VBox categoryCheckBoxContainer;

    /** Service for category operations */
    private final CategoryService categoryService = new CategoryService();

    /** Current recipe ID being operated on */
    private int recipeId = INVALID_RECIPE_ID;
    
    /** Record initially selected categories */
    private List<CategoryResponse> originalCategories = List.of();

    private Runnable onCategorized;
    public void setOnCategorized(Runnable onCategorized) {
        this.onCategorized = onCategorized;
    }

    /**
     * Initializes the controller. Category loading is delayed until setRecipeId is called.
     * 
     * @param location The location used to resolve relative paths for the root object, or null if unknown
     * @param resources The resources used to localize the root object, or null if not localized
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.info("AddRecipeToCategoryController initialized");
    }

    /**
     * Sets the current recipe ID being operated on and loads the category data.
     * This method is called when opening the window externally.
     * 
     * @param recipeId the ID of the recipe to manage categories for
     */
    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
        LOGGER.info("Setting current recipe ID: " + recipeId);

        try {
            loadCategoryData();
            setupCategoryCheckboxes();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error setting up recipe categories for recipe ID: " + recipeId, e);
            showAlert("Error", "Failed to load category data!");
        }
    }

    /**
     * Loads all categories and the categories currently associated with the recipe.
     */
    private void loadCategoryData() {
        originalCategories = categoryService.getCategoriesByRecipeId(recipeId);
    }

    /**
     * Sets up the category checkboxes in the UI.
     */
    private void setupCategoryCheckboxes() {
        List<CategoryResponse> allCategories = categoryService.getAllCategories();
        
        List<Integer> originalCategoryIds = originalCategories.stream()
                .map(CategoryResponse::getCategoryId)
                .collect(Collectors.toList());

        categoryCheckBoxContainer.getChildren().clear();
        for (CategoryResponse category : allCategories) {
            CheckBox checkBox = new CheckBox(category.getCategoryName());
            checkBox.setUserData(category.getCategoryId());
            if (originalCategoryIds.contains(category.getCategoryId())) {
                checkBox.setSelected(true);
            }
            categoryCheckBoxContainer.getChildren().add(checkBox);
        }
    }

    /**
     * Handles the confirm button click event.
     * This method processes the selected categories and updates the recipe's category associations.
     */
    @FXML
    private void onConfirm() {
        if (recipeId == INVALID_RECIPE_ID) {
            LOGGER.warning("Attempted to confirm with invalid recipe ID");
            showAlert("Error", "Recipe ID not specified!");
            return;
        }

        try {
            List<Integer> selectedCategoryIds = getSelectedCategoryIds();
            List<Integer> originalCategoryIds = getOriginalCategoryIds();

            boolean changed = hasCategorySelectionChanged(selectedCategoryIds, originalCategoryIds);

            if (!changed) {
                LOGGER.info("No changes detected, closing window");
                closeWindow();
                return;
            }

            boolean updateSuccess = categoryService.updateRecipeToCategory(selectedCategoryIds, recipeId);
            if (!updateSuccess) {
                LOGGER.warning("Failed to update categories for recipe: " + recipeId);
                showAlert("Failure", "Failed to update categories!");
                return;
            }

            LOGGER.info("Categories updated successfully for recipe: " + recipeId);
            showAlert("Success", "Categories updated!");
            if (onCategorized != null) {
                onCategorized.run();
            }
            closeWindow();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error confirming category changes for recipe: " + recipeId, e);
            showAlert("Error", "An error occurred while updating categories!");
        }
    }

    /**
     * Gets the IDs of currently selected categories from the checkboxes.
     * 
     * @return list of selected category IDs
     */
    private List<Integer> getSelectedCategoryIds() {
        return categoryCheckBoxContainer.getChildren().stream()
                .filter(node -> node instanceof CheckBox cb && cb.isSelected())
                .map(node -> (Integer) ((CheckBox) node).getUserData())
                .collect(Collectors.toList());
    }

    /**
     * Gets the IDs of originally selected categories.
     * 
     * @return list of original category IDs
     */
    private List<Integer> getOriginalCategoryIds() {
        return originalCategories.stream()
                .map(CategoryResponse::getCategoryId)
                .collect(Collectors.toList());
    }

    /**
     * Checks if the category selection has changed.
     * 
     * @param selectedIds the currently selected category IDs
     * @param originalIds the originally selected category IDs
     * @return true if the selection has changed, false otherwise
     */
    private boolean hasCategorySelectionChanged(List<Integer> selectedIds, List<Integer> originalIds) {
        return !(selectedIds.size() == originalIds.size()
                && selectedIds.containsAll(originalIds)
                && originalIds.containsAll(selectedIds));
    }

    /**
     * Handles the cancel button click event.
     * This method closes the window without making any changes.
     */
    @FXML
    private void onCancel() {
        LOGGER.info("Cancel button clicked, closing window");
        closeWindow();
    }

    /**
     * Closes the current window.
     */
    private void closeWindow() {
        Stage stage = (Stage) categoryCheckBoxContainer.getScene().getWindow();
        stage.close();
    }

    /**
     * Shows an alert dialog with the specified title and content.
     * 
     * @param title the title of the alert
     * @param content the content message of the alert
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        if ("Error".equalsIgnoreCase(title) || "Failure".equalsIgnoreCase(title)) {
            alert.setTitle("Error");
        } else if ("Success".equalsIgnoreCase(title)) {
            alert.setTitle("Success");
        } else {
            alert.setTitle("Info");
        }
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}