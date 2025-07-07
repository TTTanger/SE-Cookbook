package g.model;

/**
 * Represents a recipe category in the cookbook application.
 * This class encapsulates the basic information about a recipe category
 * including its unique identifier and name.
 * 
 * @author Xinyuan Jiang
 * @since 2025-6-15
 */
public class Category {
    /** Category ID */
    private int categoryId;

    /** Category name */
    private String categoryName;

    /**
     * Constructs a new Category with the specified ID and name.
     * 
     * @param categoryId the unique identifier for the category
     * @param categoryName the name of the category
     * @throws IllegalArgumentException if categoryName is null or empty
     */
    public Category(int categoryId, String categoryName) {
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
     * Sets the unique identifier of this category.
     * 
     * @param categoryId the new category ID
     */
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Gets the name of this category.
     * 
     * @return the category name
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Sets the name of this category.
     * 
     * @param categoryName the new category name
     * @throws IllegalArgumentException if categoryName is null or empty
     */
    public void setCategoryName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }
        this.categoryName = categoryName.trim();
    }

    /**
     * Returns a string representation of the category.
     * 
     * @return a string representation of the category
     */
    @Override 
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Category category = (Category) obj;
        return categoryId == category.categoryId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(categoryId);
    }
}
