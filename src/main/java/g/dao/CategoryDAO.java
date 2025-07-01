package g.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import g.model.Category;
import g.utils.DBUtil;

/**
 * Data Access Object for Category entities.
 * This class provides database operations for managing recipe categories
 * including CRUD operations and category retrieval.
 * 
 * @author Xinyuan Jiang
 * @since 2025-6-15
 */
public class CategoryDAO {
    
    private static final Logger LOGGER = Logger.getLogger(CategoryDAO.class.getName());
    
    private static final String CREATE_CATEGORY_SQL = "INSERT INTO category (category_name) VALUES (?)";
    private static final String DELETE_CATEGORY_SQL = "DELETE FROM category WHERE category_id = ?";
    private static final String UPDATE_CATEGORY_SQL = "UPDATE category SET category_name = ? WHERE category_id = ?";
    private static final String GET_CATEGORY_BY_ID_SQL = "SELECT category_id, category_name FROM category WHERE category_id = ?";
    private static final String GET_ALL_CATEGORIES_SQL = "SELECT * FROM category";

    /**
     * Creates a new category in the database.
     * 
     * @param categoryName the name of the category to create
     * @return true if the category was created successfully, false otherwise
     * @throws IllegalArgumentException if categoryName is null or empty
     */
    public boolean createCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }

        try (Connection conn = DBUtil.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(CREATE_CATEGORY_SQL)) {
            
            stmt.setString(1, categoryName.trim());
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Category created: " + categoryName + ", rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating category: " + categoryName, e);
            return false;
        }
    }

    /**
     * Deletes a category from the database.
     * Throws SQLException if deletion fails, so the service layer can provide detailed error messages.
     * @param categoryId the ID of the category to delete
     * @return true if the category was deleted successfully, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean deleteCategory(int categoryId) throws SQLException {
        try (Connection conn = DBUtil.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(DELETE_CATEGORY_SQL)) {
            stmt.setInt(1, categoryId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Category deleted: " + categoryId + ", rows affected: " + rowsAffected);
                return true;
            } else {
                throw new SQLException("No category deleted. The category may not exist.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting category: " + categoryId, e);
            throw e;
        }
    }

    /**
     * Updates a category in the database.
     * 
     * @param categoryId the ID of the category to update
     * @param categoryName the new name for the category
     * @return true if the category was updated successfully, false otherwise
     * @throws IllegalArgumentException if categoryName is null or empty
     */
    public boolean updateCategory(int categoryId, String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }

        try (Connection conn = DBUtil.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(UPDATE_CATEGORY_SQL)) {
            
            stmt.setString(1, categoryName.trim());
            stmt.setInt(2, categoryId);
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Category updated: " + categoryId + ", rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating category: " + categoryId, e);
            return false;
        }
    }

    /**
     * Retrieves categories by their IDs.
     * 
     * @param categoryIds the list of category IDs to retrieve
     * @return a list of categories found, empty list if none found or error occurs
     */
    public List<Category> getCategoriesByIds(List<Integer> categoryIds) {
        List<Category> categories = new ArrayList<>();

        if (categoryIds == null || categoryIds.isEmpty()) {
            return categories;
        }

        try (Connection conn = DBUtil.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(GET_CATEGORY_BY_ID_SQL)) {
            
            for (int categoryId : categoryIds) {
                stmt.setInt(1, categoryId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        categories.add(new Category(
                            rs.getInt("category_id"),
                            rs.getString("category_name")
                        ));
                    }
                }
            }
            LOGGER.info("Retrieved " + categories.size() + " categories by IDs");
            return categories;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving categories by IDs", e);
            return categories;
        }
    }

    /**
     * Retrieves all categories from the database.
     * 
     * @return a list of all categories, null if an error occurs
     */
    public List<Category> getAllCategories() {
        try (Connection conn = DBUtil.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_CATEGORIES_SQL); 
             ResultSet rs = stmt.executeQuery()) {
            
            List<Category> categories = new ArrayList<>();
            while (rs.next()) {
                categories.add(new Category(
                    rs.getInt("category_id"), 
                    rs.getString("category_name")
                ));
            }
            LOGGER.info("Retrieved " + categories.size() + " categories");
            return categories;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all categories", e);
            return null;
        }
    }

    public static void main(String[] args) {

        // CategoryDAO categorydao = new CategoryDAO();
        
        // categorydao.createCategory("tc1");
        // categorydao.createCategory("tc2");
        // categorydao.createCategory("tc3");
        // categorydao.createCategory("tc4");
        // categorydao.deleteCategory(13);
        // categorydao.updateCategory(14, "update");
        // List<Category> categories =categorydao.getAllCategories();
        // System.out.println("Success: Found " + categories.size() + " categories.");
        //     for (Category category : categories) {
        //         System.out.println("Category ID: " + category.getCategoryId() +
        //                            ", Name: " + category.getCategoryName());
        // }

    }
}