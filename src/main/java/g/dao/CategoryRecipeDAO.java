package g.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import g.utils.DBUtil;

/**
 * Data Access Object for CategoryRecipe table.
 * This class provides database operations for managing relationships between recipes and categories.
 * 
 * @author Xinyuan Jiang
 * @since 2025-6-15
 */
public class CategoryRecipeDAO {

    /**
     * Adds a recipe to multiple categories in the database.
     *
     * @param categoryIds the list of category IDs to associate with the recipe
     * @param recipeId the ID of the recipe to add
     * @return true if the recipe was added to all categories successfully, false otherwise
     */
    public boolean addToCategory(List<Integer> categoryIds, int recipeId) {
        String sql = "INSERT INTO category_recipe (category_id, recipe_id) VALUES (?, ?)";
        
        try (Connection conn = DBUtil.getConnection();PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int categoryId : categoryIds) {
                stmt.setInt(1, categoryId);
                stmt.setInt(2, recipeId);
                stmt.addBatch(); 
            }

            int[] rowsAffected = stmt.executeBatch(); 
            System.out.println("Rows affected: " + rowsAffected);

            for (int count : rowsAffected) {
                if (count == 0) return false;
            }
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Removes all category associations for the given recipe.
     *
     * @param recipeId the ID of the recipe whose category links should be cleared
     * @return true if any associations were removed, false otherwise
     */
    public boolean clearCategoriesForRecipe(int recipeId) {
        String sql = "DELETE FROM category_recipe WHERE recipe_id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, recipeId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Removes all recipe associations from the given category.
     * Always returns true if the SQL executes successfully, even if no recipes are associated.
     * This allows deleting a category even if it has no recipes.
     * 
     * @param categoryId the ID of the category
     * @return true if SQL executes successfully, false otherwise
     */
    public boolean removeAllRecipesFromCategory(int categoryId) {
        String sql = "DELETE FROM category_recipe WHERE category_id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            stmt.executeUpdate(); 
            return true; 
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all recipe IDs associated with a given category ID.
     *
     * @param categoryId the ID of the category to retrieve recipe IDs for
     * @return a list of recipe IDs associated with the category, or null if an error occurs
     */
    public List<Integer> getRecipeIdsByCategoryId(int categoryId) {
        String sql = "SELECT * FROM category_recipe WHERE category_id = ?";
        List<Integer> recipeIds = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int recipeId = rs.getInt("recipe_id");
                recipeIds.add(recipeId);
            }
            return recipeIds;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Retrieves all category IDs associated with a given recipe ID.
     *
     * @param recipeId the ID of the recipe to retrieve category IDs for
     * @return a list of category IDs associated with the recipe, or an empty list if an error occurs
     */
    public List<Integer> getCategoryIdsByRecipeId(int recipeId) {
        String sql = "SELECT category_id FROM category_recipe WHERE recipe_id = ?";
        List<Integer> categoryIds = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, recipeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int categoryId = rs.getInt("category_id");
                    categoryIds.add(categoryId);
                }
            }

            return categoryIds;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}

