package g.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import g.model.Ingredient;
import g.utils.DBUtil;

/**
 * Data Access Object for Ingredient table.
 * This class provides database operations for managing recipe ingredients,
 * including CRUD operations and ingredient retrieval.
 * 
 * @author Xinyuan Jiang
 * @since 2025-6-15
 */
public class IngredientDAO {

    /**
     * Adds a new ingredient to the database for a specific recipe.
     * 
     * @param recipeId the ID of the recipe to which the ingredient belongs
     * @param ingredientName the name of the ingredient
     * @param ingredientAmount the amount of the ingredient
     * @param ingredientUnit the unit of measurement for the ingredient
     * @return true if the ingredient was added successfully, false otherwise
     */
    public boolean addIngredient(int recipeId, String ingredientName, int ingredientAmount, String ingredientUnit) {
        String sql = "INSERT INTO ingredient (recipe_id, ingredient_name, ingredient_amount, unit) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, recipeId);
            stmt.setString(2, ingredientName);
            stmt.setInt(3, ingredientAmount);
            stmt.setString(4, ingredientUnit);


            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int pairId = generatedKeys.getInt(1);
                        System.out.println("Generated Recipe ID: " + pairId);
                        return rowsAffected > 0;
                    }
                }
            }

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deletes an ingredient from the database by its pair ID.
     * 
     * @param pairId the ID of the ingredient to delete
     * @return true if the ingredient was deleted successfully, false otherwise
     */
    public boolean deleteIngredient(int pairId) {
        String sql = "DELETE FROM ingredient WHERE pair_id = ? ";
        try (Connection conn = DBUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pairId);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes all ingredients associated with a specific recipe ID.
     * 
     * @param recipeId the ID of the recipe whose ingredients should be deleted
     * @return true if any ingredients were deleted, false otherwise
     */
    public boolean deleteIngredientsByRecipeId(int recipeId) {
        String sql = "DELETE FROM ingredient WHERE recipe_id = ?";
        try (Connection conn = DBUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, recipeId);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing ingredient in the database.
     * 
     * @param pairId the ID of the ingredient to update
     * @param recipeId the ID of the recipe to which the ingredient belongs
     * @param ingredientName the new name of the ingredient
     * @param ingredientAmount the new amount of the ingredient
     * @param ingredientUnit the new unit of measurement for the ingredient
     * @return true if the ingredient was updated successfully, false otherwise
     */
    public boolean updateIngredient(int pairId, int recipeId, String ingredientName, int ingredientAmount, String ingredientUnit) {
        String sql = "UPDATE ingredient SET ingredient_name = ?, ingredient_amount = ?, unit = ? WHERE pair_id = ? AND recipe_id = ?";

        try (Connection conn = DBUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ingredientName);
            stmt.setInt(2, ingredientAmount);
            stmt.setString(3, ingredientUnit);
            stmt.setInt(4, pairId);
            stmt.setInt(5, recipeId);


            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all ingredients for a specific recipe ID.
     * 
     * @param recipeId the ID of the recipe to retrieve ingredients for
     * @return a list of ingredients associated with the recipe, or an empty list if none found
     */
    public List<Ingredient> getIngredientsByRecipeId(int recipeId) {
        String sql = "SELECT * FROM ingredient WHERE recipe_id = ?";
        List<Ingredient> ingredients = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, recipeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setIngredientName(rs.getString("ingredient_name"));
                    ingredient.setIngredientAmount(rs.getInt("ingredient_amount"));
                    ingredient.setIngredientUnit(rs.getString("unit"));
                    ingredient.setPairId(rs.getInt("pair_id"));
                    ingredients.add(ingredient);
                    System.out.println("Ingredient found: " + ingredient.getIngredientName());
                }
            }

            return ingredients;

        } catch (Exception e) {
            e.printStackTrace();
            return ingredients; 
        }
    }
}
