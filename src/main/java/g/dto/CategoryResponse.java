package g.dto;

/**
 * Data Transfer Object for category information in API responses.
 * This class is used to transfer category data between the service layer
 * and the presentation layer.
 * 
 * @author Xinyuan Jiang
 * @since 2025-6-15
 */
public class CategoryResponse {
    
    /**
     * Unique identifier for the category.
     * This field is used to identify the category in the database.
     */
    private int categoryId;
    private String categoryName;

    /**
     * Constructs a new CategoryResponse with the specified parameters.
     * 
     * @param categoryId the unique identifier for the category
     * @param categoryName the name of the category
     * @throws IllegalArgumentException if categoryName is null or empty
     */
    public CategoryResponse(int categoryId, String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        this.categoryId = categoryId;
        this.categoryName = categoryName.trim();
    }

    /**
     * Gets the unique identifier of this category.
     * 
     * @return the category ID
     */
    public int getCategoryId() {
        return categoryId;
    }   

    /**
     * Gets the name of this category.
     * 
     * @return the category name
     */
    public String getCategoryName() {
        return categoryName;
    }
}
