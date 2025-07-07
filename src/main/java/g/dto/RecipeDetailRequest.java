package g.dto;

import java.util.List;

import g.model.Ingredient;
import g.model.Recipe;

/**
 * Data Transfer Object for recipe detail requests.
 * This class is used to transfer recipe and ingredient data from
 * the presentation layer to the service layer for updates.
 * 
 * @author Xinyuan Jiang
 * @since 2025-6-15
 */
public class RecipeDetailRequest {
    
    private Recipe recipe;
    private List<Ingredient> ingredients;
    private List<Integer> deleteIds;

    /**
     * Default constructor for RecipeDetailRequest.
     */
    public RecipeDetailRequest() {
    }

    /**
     * Constructs a new RecipeDetailRequest with the specified parameters.
     * 
     * @param recipe the recipe to be updated
     * @param ingredients the list of ingredients for the recipe
     * @param deleteIds the list of ingredient IDs to be deleted
     */
    public RecipeDetailRequest(Recipe recipe, List<Ingredient> ingredients, List<Integer> deleteIds) {
        this.recipe = recipe;
        this.ingredients = ingredients;
        this.deleteIds = deleteIds;
    }

    /**
     * Gets the recipe to be updated.
     * 
     * @return the recipe
     */
    public Recipe getRecipe() {
        return recipe;
    }

    /**
     * Sets the recipe to be updated.
     * 
     * @param recipe the new recipe
     */
    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    /**
     * Gets the list of ingredients for the recipe.
     * 
     * @return the list of ingredients
     */
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     * Sets the list of ingredients for the recipe.
     * 
     * @param ingredients the new list of ingredients
     */
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    /**
     * Gets the list of ingredient IDs to be deleted.
     * 
     * @return the list of delete IDs
     */
    public List<Integer> getDeleteIds() {
        return deleteIds;
    }

    /**
     * Sets the list of ingredient IDs to be deleted.
     * 
     * @param deleteIds the new list of delete IDs
     */
    public void setDeleteIds(List<Integer> deleteIds) {
        this.deleteIds = deleteIds;
    }

    @Override
    public String toString() {
        return "RecipeDetailRequest{" +
                "recipe=" + recipe +
                ", ingredients=" + ingredients +
                ", deleteIds=" + deleteIds +
                '}';
    }
}
