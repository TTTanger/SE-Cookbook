package g.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import g.dao.CategoryDAO;
import g.dao.CategoryRecipeDAO;
import g.dao.RecipeDAO;
import g.dto.CategoryResponse;
import g.dto.RecipeSummaryResponse;
import g.model.Category;
import g.model.Recipe;

/**
 * Service class for managing recipe categories.
 * This class provides business logic for category operations including
 * CRUD operations, recipe-category relationships, and data transformation.
 * 
 * @author Xinyuan Jiang
 * @since 2025-6-15
 */
public class CategoryService {
    private static final Logger LOGGER = Logger.getLogger(CategoryService.class.getName());
    
    /**
     * Data Access Object for category-recipe relationships.
     * This DAO is used to manage the many-to-many relationship between recipes and categories.
     */
    private final CategoryRecipeDAO categoryRecipeDAO;
    private final RecipeDAO recipeDAO; 
    private final CategoryDAO categoryDAO;

    /**
     * Default constructor for CategoryService.
     */
    public CategoryService() {
        categoryDAO = new CategoryDAO();
        recipeDAO = new RecipeDAO();
        categoryRecipeDAO = new CategoryRecipeDAO();
    }

    /**
     * Updates the categories associated with a recipe.
     * This method first clears existing category associations and then
     * adds the new category associations.
     * 
     * @param categoryIds the list of category IDs to associate with the recipe
     * @param recipeId the ID of the recipe to update
     * @return true if the update was successful, false otherwise
     */
    public boolean updateRecipeToCategory(List<Integer> categoryIds, int recipeId) {
        try {
            
            List<Integer> currentCategoryIds = categoryRecipeDAO.getCategoryIdsByRecipeId(recipeId);

            if ((currentCategoryIds == null || currentCategoryIds.isEmpty()) && 
                (categoryIds == null || categoryIds.isEmpty())) {
                LOGGER.info("No categories to update for recipe " + recipeId);
                return true;
            }

            if (currentCategoryIds != null && !currentCategoryIds.isEmpty()) {
                boolean clearSuccess = categoryRecipeDAO.clearCategoriesForRecipe(recipeId);
                if (!clearSuccess) {
                    LOGGER.warning("Failed to clear old category relations for recipe " + recipeId);
                    return false;
                }
            }

            if (categoryIds != null && !categoryIds.isEmpty()) {
                boolean addSuccess = categoryRecipeDAO.addToCategory(categoryIds, recipeId);
                if (!addSuccess) {
                    LOGGER.warning("Failed to add recipe " + recipeId + " to categories");
                    return false;
                }
                LOGGER.info("Recipe " + recipeId + " successfully added to categories");
            }

            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating recipe categories for recipe " + recipeId, e);
            return false;
        }
    }

    /**
     * Creates a new category.
     * 
     * @param categoryName the name of the category to create
     * @return true if the category was created successfully, false otherwise
     * @throws IllegalArgumentException if categoryName is null or empty
     */
    public boolean createCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }

        try {
            boolean success = categoryDAO.createCategory(categoryName.trim());
            if (success) {
                LOGGER.info("Category '" + categoryName + "' created successfully");
            } else {
                LOGGER.warning("Failed to create category '" + categoryName + "'");
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating category: " + categoryName, e);
            return false;
        }
    }

    /**
     * Deletes a category and removes all its recipe associations.
     * Returns false and logs the error message if deletion fails, so the UI can display detailed info.
     * @param categoryId the ID of the category to delete
     * @return true if the category was deleted successfully, false otherwise
     */
    public boolean deleteCategory(int categoryId) {
        try {
            
            boolean clearSuccess = categoryRecipeDAO.removeAllRecipesFromCategory(categoryId);
            if (!clearSuccess) {
                LOGGER.warning("Failed to clear recipe relations for category " + categoryId);
                return false;
            }

            boolean deleteSuccess = false;
            try {
                deleteSuccess = categoryDAO.deleteCategory(categoryId);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error deleting category " + categoryId + ": " + e.getMessage(), e);
                return false;
            }
            if (deleteSuccess) {
                LOGGER.info("Category " + categoryId + " deleted successfully");
            } else {
                LOGGER.warning("Failed to delete category " + categoryId);
            }
            return deleteSuccess;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting category " + categoryId, e);
            return false;
        }
    }

    /**
     * Updates a category's name.
     * 
     * @param categoryId the ID of the category to update
     * @param categoryName the new name for the category
     * @return true if the category was updated successfully, false otherwise
     * @throws IllegalArgumentException if categoryName is null or empty
     */
    public boolean updateCategory(int categoryId, String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }

        try {
            boolean success = categoryDAO.updateCategory(categoryId, categoryName.trim());
            if (success) {
                LOGGER.info("Category " + categoryId + " updated successfully");
            } else {
                LOGGER.warning("Failed to update category " + categoryId);
            }
            return success;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating category " + categoryId, e);
            return false;
        }
    }

    /**
     * Retrieves all categories and converts them to response DTOs.
     * 
     * @return a list of CategoryResponse objects, empty list if error occurs
     */
    public List<CategoryResponse> getAllCategories() {
        try {
            List<Category> categoryList = categoryDAO.getAllCategories();
            List<CategoryResponse> responses = new ArrayList<>();

            if (categoryList != null) {
                for (Category category : categoryList) {
                    responses.add(new CategoryResponse(
                        category.getCategoryId(),
                        category.getCategoryName()
                    ));
                }
            }

            LOGGER.info("Retrieved " + responses.size() + " categories");
            return responses;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all categories", e);
            return Collections.emptyList();
        }
    }
   
    /**
     * Retrieves recipe summaries for a specific category.
     * 
     * @param categoryId the ID of the category
     * @return a list of RecipeSummaryResponse objects, empty list if error occurs
     */
    public List<RecipeSummaryResponse> getRecipeSummaryByCategoryId(int categoryId) {
        try {
            List<Integer> recipeIds = categoryRecipeDAO.getRecipeIdsByCategoryId(categoryId);
            List<RecipeSummaryResponse> responses = new ArrayList<>();

            for (int recipeId : recipeIds) {
                Recipe recipe = recipeDAO.getRecipeSummaryById(recipeId);
                if (recipe != null) {
                    responses.add(new RecipeSummaryResponse(
                        recipe.getRecipeId(),
                        recipe.getTitle(),
                        recipe.getImgAddr()
                    ));
                }
            }

            LOGGER.info("Retrieved " + responses.size() + " recipes for category " + categoryId);
            return responses;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving recipes for category " + categoryId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves categories associated with a specific recipe.
     * 
     * @param recipeId the ID of the recipe
     * @return a list of CategoryResponse objects, empty list if error occurs
     */
    public List<CategoryResponse> getCategoriesByRecipeId(int recipeId) {
        try {
            List<Integer> categoryIds = categoryRecipeDAO.getCategoryIdsByRecipeId(recipeId);
            List<Category> categories = categoryDAO.getCategoriesByIds(categoryIds);
            List<CategoryResponse> responses = new ArrayList<>();

            for (Category category : categories) {
                responses.add(new CategoryResponse(
                    category.getCategoryId(),
                    category.getCategoryName()
                ));
            }

            LOGGER.info("Retrieved " + responses.size() + " categories for recipe " + recipeId);
            return responses;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving categories for recipe " + recipeId, e);
            return Collections.emptyList();
        }
    }

}


