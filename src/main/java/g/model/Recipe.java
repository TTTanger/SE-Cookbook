package g.model;

/**
 * Represents a recipe in the cookbook application.
 * This class encapsulates all the information about a recipe including
 * its title, preparation and cooking times, instructions, image address,
 * and serving size.
 * 
 * @author Xinyuan Jiang
 * @since 2025-6-15
 */
public class Recipe {
    private int recipeId;
    private String title;
    private int prepTime;
    private int cookTime;
    private String instruction;
    private String imgAddr;
    private int serve;

    /**
     * Default constructor for Recipe.
     */
    public Recipe() {
    }

    /**
     * Constructs a new Recipe with the specified parameters.
     * 
     * @param recipeId the unique identifier for the recipe
     * @param title the title of the recipe
     * @param prepTime the preparation time in minutes
     * @param cookTime the cooking time in minutes
     * @param instruction the cooking instructions
     * @param imgAddr the image address/path
     * @param serve the number of servings
     * @throws IllegalArgumentException if title is null or empty, or if times/serve are negative
     */
    public Recipe(int recipeId, String title, int prepTime, int cookTime, String instruction, String imgAddr, int serve) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipe title cannot be null or empty");
        }
        if (prepTime < 0 || cookTime < 0 || serve < 0) {
            throw new IllegalArgumentException("Times and serving size cannot be negative");
        }
        
        this.recipeId = recipeId;
        this.title = title.trim();
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.instruction = instruction != null ? instruction.trim() : "";
        this.imgAddr = imgAddr != null ? imgAddr.trim() : "";
        this.serve = serve;
    }
    
    /**
     * Gets the unique identifier of this recipe.
     * 
     * @return the recipe ID
     */
    public int getRecipeId() {
        return recipeId;
    }

    /**
     * Sets the unique identifier of this recipe.
     * 
     * @param recipeId the new recipe ID
     */
    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    /**
     * Gets the title of this recipe.
     * 
     * @return the recipe title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this recipe.
     * 
     * @param title the new recipe title
     * @throws IllegalArgumentException if title is null or empty
     */
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipe title cannot be null or empty");
        }
        this.title = title.trim();
    }

    /**
     * Gets the preparation time of this recipe in minutes.
     * 
     * @return the preparation time
     */
    public int getPrepTime() {
        return prepTime;
    }

    /**
     * Sets the preparation time of this recipe in minutes.
     * 
     * @param prepTime the new preparation time
     * @throws IllegalArgumentException if prepTime is negative
     */
    public void setPrepTime(int prepTime) {
        if (prepTime < 0) {
            throw new IllegalArgumentException("Preparation time cannot be negative");
        }
        this.prepTime = prepTime;
    }

    /**
     * Gets the cooking time of this recipe in minutes.
     * 
     * @return the cooking time
     */
    public int getCookTime() {
        return cookTime;
    }

    /**
     * Sets the cooking time of this recipe in minutes.
     * 
     * @param cookTime the new cooking time
     * @throws IllegalArgumentException if cookTime is negative
     */
    public void setCookTime(int cookTime) {
        if (cookTime < 0) {
            throw new IllegalArgumentException("Cooking time cannot be negative");
        }
        this.cookTime = cookTime;
    }

    /**
     * Gets the cooking instructions for this recipe.
     * 
     * @return the cooking instructions
     */
    public String getInstruction() {
        return instruction;
    }

    /**
     * Sets the cooking instructions for this recipe.
     * 
     * @param instruction the new cooking instructions
     */
    public void setInstruction(String instruction) {
        this.instruction = instruction != null ? instruction.trim() : "";
    }

    /**
     * Gets the image address/path for this recipe.
     * 
     * @return the image address
     */
    public String getImgAddr() {
        return imgAddr;
    }

    /**
     * Sets the image address/path for this recipe.
     * 
     * @param imgAddr the new image address
     */
    public void setImgAddr(String imgAddr) {
        this.imgAddr = imgAddr != null ? imgAddr.trim() : "";
    }

    /**
     * Gets the number of servings for this recipe.
     * 
     * @return the number of servings
     */
    public int getServe() {
        return serve;
    }

    /**
     * Sets the number of servings for this recipe.
     * 
     * @param serve the new number of servings
     * @throws IllegalArgumentException if serve is negative
     */
    public void setServe(int serve) {
        if (serve < 0) {
            throw new IllegalArgumentException("Serving size cannot be negative");
        }
        this.serve = serve;
    }

    /**
     * Gets the total time (preparation + cooking) for this recipe.
     * 
     * @return the total time in minutes
     */
    public int getTotalTime() {
        return prepTime + cookTime;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Recipe recipe = (Recipe) obj;
        return recipeId == recipe.recipeId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(recipeId);
    }
    
    @Override
    public String toString() {
        return "Recipe{" +
                "recipeId=" + recipeId +
                ", title='" + title + '\'' +
                ", prepTime=" + prepTime +
                ", cookTime=" + cookTime +
                ", instruction='" + instruction + '\'' +
                ", imgAddr='" + imgAddr + '\'' +
                ", serve=" + serve +
                '}';
    }
}
