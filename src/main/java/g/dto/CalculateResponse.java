package g.dto;

import java.util.List;
import java.util.Objects;

import g.model.Ingredient;

/**
 * Data Transfer Object for calculation results in API responses.
 * This class is used to transfer calculated ingredient data between
 * the service layer and the presentation layer.
 * 
 * @author Xinyuan Jiang
 * @since 2025-6-15
 */
public class CalculateResponse {
    private List<Ingredient> ingredients; 

    /**
     * Default constructor for CalculateResponse.
     */
    public CalculateResponse() {
    }

    /**
     * Constructs a new CalculateResponse with the specified ingredients.
     * 
     * @param ingredients the list of calculated ingredients
     */
    public CalculateResponse(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    /**
     * Gets the list of calculated ingredients.
     * 
     * @return the list of ingredients
     */
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     * Sets the list of calculated ingredients.
     * 
     * @param ingredients the new list of ingredients
     */
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CalculateResponse that = (CalculateResponse) obj;
        return Objects.equals(ingredients, that.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredients);
    }

    @Override
    public String toString() {
        return "CalculateResponse{" +
                "ingredients=" + ingredients +
                '}';
    }
}



