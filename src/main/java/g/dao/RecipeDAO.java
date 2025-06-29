package g.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import g.model.Recipe;
import g.utils.DBUtil;

/**
 * Data Access Object for Recipe entities.
 * This class provides database operations for managing recipes including
 * CRUD operations, search functionality, and recipe retrieval.
 * 
 * @author Xinyuan Jiang
 * @since 2025-6-15
 */
public class RecipeDAO {
    
    private static final Logger LOGGER = Logger.getLogger(RecipeDAO.class.getName());
    
    private static final String CREATE_RECIPE_SQL = 
        "INSERT INTO recipe (title, prep_time, cook_time, instruction, img_addr, serve) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String DELETE_RECIPE_SQL = "DELETE FROM recipe WHERE recipe_id = ?";
    private static final String UPDATE_RECIPE_SQL = 
        "UPDATE recipe SET title = ?, prep_time = ?, cook_time = ?, instruction = ?, img_addr = ?, serve = ? WHERE recipe_id = ?";
    private static final String GET_RECIPE_BY_ID_SQL = "SELECT * FROM recipe WHERE recipe_id = ?";
    private static final String GET_RECIPE_SUMMARY_BY_ID_SQL = "SELECT recipe_id, title, img_addr FROM recipe WHERE recipe_id = ?";
    private static final String GET_RECIPE_SUMMARY_BY_TITLE_SQL = "SELECT recipe_id, title, img_addr FROM recipe WHERE title LIKE ?";
    private static final String GET_ALL_RECIPE_SUMMARY_SQL = "SELECT recipe_id, title, img_addr FROM recipe";

    /**
     * Creates a new recipe in the database.
     * 
     * @param title the title of the recipe
     * @param prepTime the preparation time in minutes
     * @param cookTime the cooking time in minutes
     * @param instruction the cooking instructions
     * @param imgAddr the image address/path
     * @param serve the number of servings
     * @return the generated recipe ID if successful, -1 otherwise
     * @throws IllegalArgumentException if title is null or empty, or if times/serve are negative
     */
    public int createRecipe(String title, int prepTime, int cookTime, String instruction, String imgAddr, int serve) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipe title cannot be null or empty");
        }
        if (prepTime < 0 || cookTime < 0 || serve < 0) {
            throw new IllegalArgumentException("Times and serving size cannot be negative");
        }
        
        try (Connection conn = DBUtil.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(CREATE_RECIPE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, title.trim());
            stmt.setInt(2, prepTime);
            stmt.setInt(3, cookTime);
            stmt.setString(4, instruction != null ? instruction.trim() : "");
            stmt.setString(5, imgAddr != null ? imgAddr.trim() : "");
            stmt.setInt(6, serve);

            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Recipe created: " + title + ", rows affected: " + rowsAffected);
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int recipeId = generatedKeys.getInt(1);
                        LOGGER.info("Generated Recipe ID: " + recipeId);
                        return recipeId;
                    }
                }
            }

            return -1;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating recipe: " + title, e);
            return -1;
        }
    }

    /**
     * Deletes a recipe from the database.
     * 
     * @param recipeId the ID of the recipe to delete
     * @return true if the recipe was deleted successfully, false otherwise
     */
    public boolean deleteRecipe(int recipeId) {
        try (Connection conn = DBUtil.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(DELETE_RECIPE_SQL)) {

            stmt.setInt(1, recipeId);
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Recipe deleted: " + recipeId + ", rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting recipe: " + recipeId, e);
            return false;
        }
    }

    /**
     * Updates a recipe in the database.
     * 
     * @param recipeId the ID of the recipe to update
     * @param title the new title of the recipe
     * @param prepTime the new preparation time in minutes
     * @param cookTime the new cooking time in minutes
     * @param instruction the new cooking instructions
     * @param imgAddr the new image address/path
     * @param serve the new number of servings
     * @return true if the recipe was updated successfully, false otherwise
     * @throws IllegalArgumentException if title is null or empty, or if times/serve are negative
     */
    public boolean updateRecipe(int recipeId, String title, int prepTime, int cookTime, String instruction, String imgAddr, int serve) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipe title cannot be null or empty");
        }
        if (prepTime < 0 || cookTime < 0 || serve < 0) {
            throw new IllegalArgumentException("Times and serving size cannot be negative");
        }
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_RECIPE_SQL)) {

            stmt.setString(1, title.trim());
            stmt.setInt(2, prepTime);
            stmt.setInt(3, cookTime);
            stmt.setString(4, instruction != null ? instruction.trim() : "");
            stmt.setString(5, imgAddr != null ? imgAddr.trim() : "");
            stmt.setInt(6, serve);
            stmt.setInt(7, recipeId);

            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Recipe updated: " + recipeId + ", rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating recipe: " + recipeId, e);
            return false;
        }
    }

    /**
     * Retrieves a complete recipe by its ID.
     * 
     * @param recipeId the ID of the recipe to retrieve
     * @return the Recipe object if found, null otherwise
     */
    public Recipe getRecipeById(int recipeId) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_RECIPE_BY_ID_SQL)) {

            stmt.setInt(1, recipeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Recipe recipe = new Recipe();
                    recipe.setRecipeId(rs.getInt("recipe_id"));
                    recipe.setTitle(rs.getString("title"));
                    recipe.setPrepTime(rs.getInt("prep_time"));
                    recipe.setCookTime(rs.getInt("cook_time"));
                    recipe.setInstruction(rs.getString("instruction"));
                    recipe.setImgAddr(rs.getString("img_addr"));
                    recipe.setServe(rs.getInt("serve"));

                    LOGGER.info("Recipe found: " + recipe.getTitle());
                    return recipe;
                } else {
                    LOGGER.info("No recipe found for ID: " + recipeId);
                    return null;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving recipe: " + recipeId, e);
            return null;
        }
    }

    /**
     * Retrieves a recipe summary (ID, title, image) by its ID.
     * 
     * @param recipeId the ID of the recipe to retrieve
     * @return the Recipe object with summary information if found, null otherwise
     */
    public Recipe getRecipeSummaryById(int recipeId) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_RECIPE_SUMMARY_BY_ID_SQL)) {

            stmt.setInt(1, recipeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Recipe recipe = new Recipe();
                    recipe.setRecipeId(rs.getInt("recipe_id"));
                    recipe.setTitle(rs.getString("title"));
                    recipe.setImgAddr(rs.getString("img_addr"));
                    return recipe;
                } else {
                    LOGGER.info("No recipe found for ID: " + recipeId);
                    return null;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving recipe summary: " + recipeId, e);
            return null; 
        }
    }

    /**
     * Searches for recipes by title keyword.
     * 
     * @param keyword the keyword to search for in recipe titles
     * @return a list of Recipe objects with summary information
     * @throws IllegalArgumentException if keyword is null
     */
    public List<Recipe> getRecipeSummaryByTitle(String keyword) {
        if (keyword == null) {
            throw new IllegalArgumentException("Search keyword cannot be null");
        }
        
        List<Recipe> recipes = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_RECIPE_SUMMARY_BY_TITLE_SQL)) {

            stmt.setString(1, "%" + keyword.trim() + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Recipe recipe = new Recipe();
                    recipe.setRecipeId(rs.getInt("recipe_id"));
                    recipe.setTitle(rs.getString("title"));
                    recipe.setImgAddr(rs.getString("img_addr"));
                    
                    recipes.add(recipe);
                }
            }

            LOGGER.info("Found " + recipes.size() + " recipes matching keyword: " + keyword);
            return recipes;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching recipes by title: " + keyword, e);
            return recipes; 
        }
    }

    /**
     * Retrieves all recipe summaries from the database.
     * 
     * @return a list of Recipe objects with summary information
     */
    public List<Recipe> getAllRecipeSummary() {
        List<Recipe> recipes = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_RECIPE_SUMMARY_SQL)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Recipe recipe = new Recipe();
                    recipe.setRecipeId(rs.getInt("recipe_id"));
                    recipe.setTitle(rs.getString("title"));
                    recipe.setImgAddr(rs.getString("img_addr"));

                    recipes.add(recipe);
                }
            }

            LOGGER.info("Retrieved " + recipes.size() + " recipe summaries");
            return recipes;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all recipe summaries", e);
            return recipes; 
        }
    }
   
    // public boolean existsByTitle(String title) {
    //     String sql = "SELECT COUNT(*) FROM recipe WHERE title = ?";
    //     try (Connection conn = DBUtil.getConnection();
    //         PreparedStatement stmt = conn.prepareStatement(sql)) {

    //         stmt.setString(1, title);

    //         try (ResultSet rs = stmt.executeQuery()) {
    //             if (rs.next()) {
    //                 int count = rs.getInt(1);
    //                 System.out.println("Recipe title '" + title + "' count: " + count);
    //                 return count > 0;
    //             }
    //         }

    //         return false;

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return false;
    //     }
    // }

    public static void main(String[] args) {

        // RecipeDAO recipeDAO = new RecipeDAO();

        // recipeDAO.createRecipe("tr2", 6, 30, "Instructions for test2 recipe", "img/test_recipe_2.jpg",2);

        // recipeDAO.deleteRecipe(3);

        // recipeDAO.updateRecipe(4, "Updated Recipe", 25, 55, "Updated instructions", "img/updated_recipe.jpg",90);

        // int recipeId =4;
        // Recipe recipe = recipeDAO.getRecipeById(recipeId);
        // if (recipe != null && recipe.getTitle() != null) {
        //     System.out.println("Found recipe: " + recipe.getTitle());
        //     System.out.println("Prep time: " + recipe.getPrepTime());
        //     System.out.println("Cook time: " + recipe.getCookTime());
        //     System.out.println("Instruction: " + recipe.getInstruction());
        //     System.out.println("ImgAddr: " + recipe.getImgAddr());
        //     System.out.println("Serve: " + recipe.getServe());
        // } else {
        //     System.out.println("No recipe found for ID: " + recipeId);
        // }

        // String keyword = "Test";
        // List<Recipe> recipes = recipeDAO.getRecipesByTitle(keyword);
        // if (recipes.isEmpty()) {
        //     System.out.println("No recipes found.");
        // } else {
        //     for (Recipe recipe : recipes) {
        //         System.out.println("Found recipe: " + recipe.getTitle());
        //         System.out.println("Prep time: " + recipe.getPrepTime());
        //         System.out.println("Cook time: " + recipe.getCookTime());
        //         System.out.println("Instruction: " + recipe.getInstruction());
        //         System.out.println("ImgAddr: " + recipe.getImgAddr());
        //         System.out.println("Serve: " + recipe.getServe());
        //     }
        // }

        // List<Recipe> recipes = recipeDAO.getAllRecipes();
        // if (recipes != null && !recipes.isEmpty()) {
        //     for (Recipe recipe : recipes) {
        //         System.out.println("Recipe ID: " + recipe.getRecipeId());
        //         System.out.println("Title: " + recipe.getTitle());
        //         System.out.println("Prep Time: " + recipe.getPrepTime());
        //         System.out.println("Cook Time: " + recipe.getCookTime());
        //         System.out.println("Instruction: " + recipe.getInstruction());
        //         System.out.println("ImgAddr: " + recipe.getImgAddr());
        //         System.out.println("Serve: " + recipe.getServe());
        //         System.out.println("-----------------------------");
        //     }
        // } else {
        //     System.out.println("No recipes found.");
        // }

    }
}
