package g.dto;

import java.util.List;
import java.util.Objects;

import g.model.Ingredient;
import g.model.Recipe;

/**
 * Data Transfer Object for recipe detail responses.
 * This class is used to transfer complete recipe information including
 * ingredients from the service layer to the presentation layer.
 * 
 * @author Xinyuan Jiang
 * @since 2025-6-15
 */
public class RecipeDetailResponse {

    private Recipe recipe;
    private List<Ingredient> ingredients;

    /**
     * Constructs a new RecipeDetailResponse with the specified parameters.
     * 
     * @param recipe the recipe information
     * @param ingredients the list of ingredients for the recipe
     */
    public RecipeDetailResponse(Recipe recipe, List<Ingredient> ingredients) {
        this.recipe = recipe;
        this.ingredients = ingredients;
    }

    /**
     * Gets the recipe information.
     * 
     * @return the recipe
     */
    public Recipe getRecipe() {
        return recipe;
    }

    /**
     * Gets the list of ingredients for the recipe.
     * 
     * @return the list of ingredients
     */
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RecipeDetailResponse that = (RecipeDetailResponse) obj;
        return Objects.equals(recipe, that.recipe) &&
               Objects.equals(ingredients, that.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipe, ingredients);
    }

    @Override
    public String toString() {
        return "RecipeDetailResponse{" +
                "recipe=" + recipe +
                ", ingredients=" + ingredients +
                '}';
    }
}
