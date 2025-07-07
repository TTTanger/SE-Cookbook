package g.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import g.dao.IngredientDAO;
import g.dao.RecipeDAO;
import g.dto.RecipeDetailRequest;
import g.dto.RecipeDetailResponse;
import g.dto.RecipeSummaryResponse;
import g.model.Ingredient;
import g.model.Recipe;

/**
 * Service class for managing recipes and their ingredients.
 * This class provides methods to create, update, delete, and retrieve recipes
 * along with their associated ingredients.
 * 
 * @author Xinyuan Jiang
 * @since 2025-6-15
 */
public class RecipeService {

    /**
     * Data Access Object for recipes.
     * This DAO is used to interact with the recipe data in the database.
     */
    private final RecipeDAO recipeDAO;
    private final IngredientDAO ingredientDAO;

    /**
     * Default constructor for RecipeService.
     */
    public RecipeService() {
        this.recipeDAO = new RecipeDAO();
        this.ingredientDAO = new IngredientDAO();
    }

    /**
     * Constructs a new RecipeService with the specified DAOs. 
     */
    public RecipeService(RecipeDAO recipeDAO, IngredientDAO ingredientDAO) {
        this.recipeDAO = recipeDAO;
        this.ingredientDAO = ingredientDAO;
    }

    /**
     * Creates a new recipe along with its ingredients.
     * 
     * @param request the request containing recipe and ingredient details
     * @return true if the recipe was created successfully, false otherwise
     */
    public boolean createRecipe(RecipeDetailRequest request) {

        Recipe recipe = request.getRecipe();
        List<Ingredient> ingredients = request.getIngredients();
        int createRecipeResult = recipeDAO.createRecipe(
                recipe.getTitle(),
                recipe.getPrepTime(),
                recipe.getCookTime(),
                recipe.getInstruction(),
                recipe.getImgAddr(),
                recipe.getServe()
        );

        if (createRecipeResult == -1) {
            System.out.println("RecipeService failed to create recipe");
            return false;
        }

        for (Ingredient ingredient : ingredients) {
            boolean createIngredientResult = ingredientDAO.addIngredient(
                    createRecipeResult,
                    ingredient.getIngredientName(),
                    ingredient.getIngredientAmount(),
                    ingredient.getIngredientUnit()
            );
            if (!createIngredientResult) {
                System.out.println("RecipeService failed to insert ingredient: " + ingredient);
                return false;
            }
        }
        return true;
    }

    /**
     * Deletes a recipe and its associated ingredients by recipe ID.
     * 
     * @param recipeId the ID of the recipe to delete
     * @return true if the recipe and its ingredients were deleted successfully, false otherwise
     */
    public boolean deleteRecipe(int recipeId) {
        boolean deleteIngredients = ingredientDAO.deleteIngredientsByRecipeId(recipeId);
        boolean deletedRecipe = recipeDAO.deleteRecipe(recipeId);
        if (!deleteIngredients) {
            return false;
        } else if (!deletedRecipe) {
            return false;
        }
        return true;
    }

    /**
     * Updates an existing recipe and its ingredients.
     * 
     * @param request the request containing updated recipe and ingredient details
     * @return true if the recipe was updated successfully, false otherwise
     */
    public boolean updateRecipe(RecipeDetailRequest request) {
        Recipe recipe = request.getRecipe();
        System.out.println("RecipeService: Updating recipe with id " + recipe.getRecipeId());
        List<Ingredient> ingredients = request.getIngredients();
        List<Integer> deleteList = request.getDeleteIds();
        for (int pairId : deleteList) {
            System.out.println("RecipeService: Deleting ingredient with pairId " + pairId);
            ingredientDAO.deleteIngredient(pairId);
        }
        
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getPairId() == 0) {
               
                boolean insertIngredientResult = ingredientDAO.addIngredient(
                        recipe.getRecipeId(),
                        ingredient.getIngredientName(),
                        ingredient.getIngredientAmount(),
                        ingredient.getIngredientUnit());
                if (!insertIngredientResult) {
                    System.out.println("Failed to insert ingredient: " + ingredient);
                    return false;
                }
            } else {
                boolean updateIngredientResult = ingredientDAO.updateIngredient(
                        ingredient.getPairId(),
                        recipe.getRecipeId(),
                        ingredient.getIngredientName(),
                        ingredient.getIngredientAmount(),
                        ingredient.getIngredientUnit()
                );
                if (!updateIngredientResult) {
                    System.out.println("Failed to update ingredient: " + ingredient);
                    return false;
                }
            }
        }

        boolean updateRecipeResult = recipeDAO.updateRecipe(
                recipe.getRecipeId(),
                recipe.getTitle(),
                recipe.getPrepTime(),
                recipe.getCookTime(),
                recipe.getInstruction(),
                recipe.getImgAddr(),
                recipe.getServe()
        );
        if (!updateRecipeResult) {
            System.out.println("Failed to create recipe");
            return false;
        }
        return true;
    }

    /**
     * Retrieves a recipe by its ID along with its ingredients.
     * 
     * @param recipeId the ID of the recipe to retrieve
     * @return a RecipeDetailResponse containing the recipe and its ingredients
     */
    public RecipeDetailResponse getRecipeById(int recipeId) {
        Recipe recipe = recipeDAO.getRecipeById(recipeId);
        List<Ingredient> ingredients = ingredientDAO.getIngredientsByRecipeId(recipeId);
        RecipeDetailResponse response = new RecipeDetailResponse(recipe, ingredients);
        return response;
    }

    /**
     * Retrieves a list of recipe summaries filtered by title keyword.
     * 
     * @param keyword the keyword to filter recipe titles
     * @return a list of RecipeSummaryResponse containing recipe summaries
     */
    public List<RecipeSummaryResponse> getRecipeSummaryByTitle(String keyword) {
        List<RecipeSummaryResponse> responses = new ArrayList<>();
        try {
            List<Recipe> recipes = recipeDAO.getRecipeSummaryByTitle(keyword);
            for (Recipe recipe : recipes) {
                RecipeSummaryResponse response = new RecipeSummaryResponse(
                        recipe.getRecipeId(),
                        recipe.getTitle(),
                        recipe.getImgAddr()
                );
                responses.add(response);
            }
            return responses;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves all recipe summaries.
     * 
     * @return a list of RecipeSummaryResponse containing all recipe summaries
     */
    public List<RecipeSummaryResponse> getAllRecipeSummary() {
        List<RecipeSummaryResponse> responses = new ArrayList<>();
        try {
            List<Recipe> recipes = recipeDAO.getAllRecipeSummary();
            for (Recipe recipe : recipes) {
                RecipeSummaryResponse response = new RecipeSummaryResponse(
                        recipe.getRecipeId(),
                        recipe.getTitle(),
                        recipe.getImgAddr()
                );
                responses.add(response);
            }
            return responses;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
