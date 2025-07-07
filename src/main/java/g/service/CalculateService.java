package g.service;

import java.util.ArrayList;
import java.util.List;

import g.dao.IngredientDAO;
import g.dao.RecipeDAO;
import g.dto.CalculateResponse;
import g.model.Ingredient;

/**
 * Service class for calculating ingredient amounts based on recipe servings.
 * This class provides methods to scale ingredient amounts according to the
 * desired number of servings for a recipe.
 * 
 * @author Xinyuan Jiang
 * @since 2025-6-15
 */
public class CalculateService {

    /**
     * Data Access Object for ingredients.
     * This DAO is used to interact with the ingredient data in the database.
     */
    private final IngredientDAO ingredientDAO;
    private final RecipeDAO recipeDAO;
        
    /**
     * Default constructor for CalculateService.
     */
    public CalculateService() {
        this.ingredientDAO = new IngredientDAO();
        this.recipeDAO = new RecipeDAO();
    }

    /**
     * Calculates the scaled ingredient amounts for a recipe based on the desired number of servings.
     * 
     * @param recipeId the ID of the recipe to calculate ingredients for
     * @param serve the desired number of servings
     * @return a CalculateResponse containing the scaled ingredients
     */
    public CalculateResponse IngredientCalculate(int recipeId, int serve) {
       
        CalculateResponse response = new CalculateResponse();

        List<Ingredient> ingredients = ingredientDAO.getIngredientsByRecipeId(recipeId);

        int originalServe = recipeDAO.getRecipeById(recipeId).getServe();


        double scaleFactor = (double) serve / originalServe;


        List<Ingredient> scaledIngredients = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            Ingredient result = new Ingredient();
            result.setPairId(ingredient.getPairId());
            result.setRecipeId(ingredient.getRecipeId());
            result.setIngredientName(ingredient.getIngredientName());
            result.setIngredientAmount((int) Math.ceil(ingredient.getIngredientAmount() * scaleFactor));
            result.setIngredientUnit(ingredient.getIngredientUnit());
            scaledIngredients.add(result);
        }

        response.setIngredients(scaledIngredients);

        return response;
    }
}
