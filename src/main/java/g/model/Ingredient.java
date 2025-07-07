package g.model;

/**
 * Represents an ingredient in a recipe.
 * This class encapsulates the information about an ingredient including
 * its name, amount, unit, and its relationship to a recipe.
 * 
 * @author Xinyuan Jiang
 * @since 2025-6-15
 */
public class Ingredient {

    /** Pair ID */
    private int pairId;

    /** Recipe ID */
    private int recipeId;

    /** Ingredient name */
    private String ingredientName;

    /** Ingredient amount */
    private int ingredientAmount;

    /** Ingredient unit */
    private String ingredientUnit;

    /**
     * Default constructor for Ingredient.
     */
    public Ingredient() {
    }

    /**
     * Constructs a new Ingredient with the specified parameters.
     * 
     * @param pairId the unique identifier for this ingredient-recipe pair
     * @param recipeId the ID of the recipe this ingredient belongs to
     * @param ingredientName the name of the ingredient
     * @param ingredientAmount the amount of the ingredient
     * @param ingredientUnit the unit of measurement for the ingredient
     * @throws IllegalArgumentException if ingredientName is null or empty, or if amount is negative
     */
    public Ingredient(int pairId, int recipeId, String ingredientName, int ingredientAmount, String ingredientUnit) {
        if (ingredientName == null || ingredientName.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingredient name cannot be null or empty");
        }
        if (ingredientAmount < 0) {
            throw new IllegalArgumentException("Ingredient amount cannot be negative");
        }
        
        this.pairId = pairId;
        this.recipeId = recipeId;
        this.ingredientName = ingredientName.trim();
        this.ingredientAmount = ingredientAmount;
        this.ingredientUnit = ingredientUnit != null ? ingredientUnit.trim() : "";
    }

    /**
     * Gets the unique identifier for this ingredient-recipe pair.
     * 
     * @return the pair ID
     */
    public int getPairId() {
        return this.pairId;
    }

    /**
     * Sets the unique identifier for this ingredient-recipe pair.
     * 
     * @param pairId the new pair ID
     */
    public void setPairId(int pairId) {
        this.pairId = pairId;
    }

    /**
     * Gets the ID of the recipe this ingredient belongs to.
     * 
     * @return the recipe ID
     */
    public int getRecipeId() {
        return this.recipeId;
    }

    /**
     * Sets the ID of the recipe this ingredient belongs to.
     * 
     * @param recipeId the new recipe ID
     */
    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    /**
     * Gets the name of this ingredient.
     * 
     * @return the ingredient name
     */
    public String getIngredientName() {
        return this.ingredientName;
    }

    /**
     * Sets the name of this ingredient.
     * 
     * @param ingredientName the new ingredient name
     * @throws IllegalArgumentException if ingredientName is null or empty
     */
    public void setIngredientName(String ingredientName) {
        if (ingredientName == null || ingredientName.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingredient name cannot be null or empty");
        }
        this.ingredientName = ingredientName.trim();
    }

    /**
     * Gets the amount of this ingredient.
     * 
     * @return the ingredient amount
     */
    public int getIngredientAmount() {
        return this.ingredientAmount;
    }

    /**
     * Sets the amount of this ingredient.
     * 
     * @param ingredientAmount the new ingredient amount
     * @throws IllegalArgumentException if amount is negative
     */
    public void setIngredientAmount(int ingredientAmount) {
        if (ingredientAmount < 0) {
            throw new IllegalArgumentException("Ingredient amount cannot be negative");
        }
        this.ingredientAmount = ingredientAmount;
    }

    /**
     * Gets the unit of measurement for this ingredient.
     * 
     * @return the ingredient unit
     */
    public String getIngredientUnit() {
        return this.ingredientUnit;
    }

    /**
     * Sets the unit of measurement for this ingredient.
     * 
     * @param ingredientUnit the new ingredient unit
     */
    public void setIngredientUnit(String ingredientUnit) {
        this.ingredientUnit = ingredientUnit != null ? ingredientUnit.trim() : "";
    }

    /**
     * Returns a string representation of the ingredient.
     * 
     * @return a string representation of the ingredient
     */
    @Override
    public String toString() {
        return "{" +
            " pairId='" + getPairId() + "'" +
            ", recipeId='" + getRecipeId() + "'" +
            ", ingredientName='" + getIngredientName() + "'" +
            ", ingredientAmount='" + getIngredientAmount() + "'" +
            ", unit='" + getIngredientUnit() + "'" +
            "}";
    }
}
