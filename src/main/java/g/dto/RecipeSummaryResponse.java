package g.dto;

/**
 * Data Transfer Object for recipe summary information in API responses.
 * This class is used to transfer basic recipe information (ID, title, image)
 * between the service layer and the presentation layer.
 * 
 * @author Xinyuan Jiang
 * @since 2025-6-15
 */
public class RecipeSummaryResponse {
    private int recipeId;
    private String title;
    private String imgAddr;

    /**
     * Constructs a new RecipeSummaryResponse with the specified parameters.
     * 
     * @param recipeId the unique identifier for the recipe
     * @param title the title of the recipe
     * @param imgAddr the image address/path for the recipe
     * @throws IllegalArgumentException if title is null or empty
     */
    public RecipeSummaryResponse(int recipeId, String title, String imgAddr) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipe title cannot be null or empty");
        }
        this.recipeId = recipeId;
        this.title = title.trim();
        this.imgAddr = imgAddr != null ? imgAddr.trim() : "";
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
     * Gets the title of this recipe.
     * 
     * @return the recipe title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the image address/path of this recipe.
     * 
     * @return the image address
     */
    public String getImgAddr() {
        return imgAddr;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RecipeSummaryResponse that = (RecipeSummaryResponse) obj;
        return recipeId == that.recipeId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(recipeId);
    }

    @Override
    public String toString() {
        return "RecipeSummaryResponse{" +
                "recipeId=" + recipeId +
                ", title='" + title + '\'' +
                ", imgAddr='" + imgAddr + '\'' +
                '}';
    }
}
