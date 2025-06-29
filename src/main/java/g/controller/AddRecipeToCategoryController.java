package g.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import g.dto.CategoryResponse;
import g.service.CategoryService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddRecipeToCategoryController implements Initializable {

    @FXML
    private VBox categoryCheckBoxContainer;

    private final CategoryService categoryService = new CategoryService();

    private int recipeId = -1; // Current recipe ID being operated on
    private List<CategoryResponse> originalCategories = List.of(); // Record initially selected categories

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Category loading delayed until setRecipeId
        System.out.println("AddRecipeToCategoryController initialized");
    }

    /**
     * Set the current recipe ID being operated on (called when opening window externally)
     */
    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
        System.out.println("Setting current recipe ID: " + recipeId);

        // 1. Query all categories
        List<CategoryResponse> allCategories = categoryService.getAllCategories();
        // 2. Query which categories this recipe already belongs to
        originalCategories = categoryService.getCategoriesByRecipeId(recipeId);

        // Extract the set of IDs of the initially selected categories
        List<Integer> originalCategoryIds = originalCategories.stream()
                .map(CategoryResponse::getCategoryId)
                .collect(Collectors.toList());

        categoryCheckBoxContainer.getChildren().clear();
        for (CategoryResponse category : allCategories) {
            CheckBox checkBox = new CheckBox(category.getCategoryName());
            checkBox.setUserData(category.getCategoryId());
            // If it already belongs to this category, check it
            if (originalCategoryIds.contains(category.getCategoryId())) {
                checkBox.setSelected(true);
            }
            categoryCheckBoxContainer.getChildren().add(checkBox);
        }
    }

    @FXML
    private void onConfirm() {
        List<Integer> selectedCategoryIds = categoryCheckBoxContainer.getChildren().stream()
                .filter(node -> node instanceof CheckBox cb && cb.isSelected())
                .map(node -> (Integer) ((CheckBox) node).getUserData())
                .collect(Collectors.toList());

        if (recipeId == -1) {
            showAlert("Error", "Recipe ID not specified!");
            return;
        }

        List<Integer> originalCategoryIds = originalCategories.stream()
                .map(CategoryResponse::getCategoryId)
                .collect(Collectors.toList());

        // Check if there's a change
        boolean changed = !(selectedCategoryIds.size() == originalCategoryIds.size()
                && selectedCategoryIds.containsAll(originalCategoryIds)
                && originalCategoryIds.containsAll(selectedCategoryIds));

        if (!changed) {
            // No change, just close the window
            closeWindow();
            return;
        }

        // Update only if there's a change
        boolean updateSuccess = categoryService.updateRecipeToCategory(selectedCategoryIds, recipeId);
        if (!updateSuccess) {
            showAlert("Failure", "Failed to update categories!");
            return;
        }

        showAlert("Success", "Categories updated!");
        closeWindow();
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) categoryCheckBoxContainer.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}