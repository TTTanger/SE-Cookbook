package g.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Database utility class that provides methods for managing database
 * connections and initialization. This class handles SQLite database 
 * connections, table creation, and connection management operations.
 *
 * @author Junzhe Luo
 * @since 2025-6-15
 */
public class DBUtil {
    
    private static final Logger LOGGER = Logger.getLogger(DBUtil.class.getName());
    
    /**
     * The database file path in user's home directory
     */
    private static final String DB_FILENAME = "cookbook.db";
    
    /**
     * The JDBC URL for the SQLite database connection
     */
    private static String url;

    static {
        initializeDatabasePath();
    }

    /**
     * Initializes the database path and ensures the data directory exists.
     * Uses user's home directory to ensure write permissions.
     */
    private static void initializeDatabasePath() {
        try {
            // Use user's home directory for database storage
            String userHome = System.getProperty("user.home");
            File dataDir = new File(userHome, ".cookbook");
            if (!dataDir.exists()) {
                boolean created = dataDir.mkdirs();
                if (created) {
                    LOGGER.info("Created data directory: " + dataDir.getAbsolutePath());
                } else {
                    LOGGER.warning("Failed to create data directory: " + dataDir.getAbsolutePath());
                }
            }
            
            File dbFile = new File(dataDir, DB_FILENAME);
            url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            LOGGER.info("Database URL initialized: " + url);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing database path", e);
        }
    }

    /**
     * Establishes and returns a new connection to the SQLite database.
     *
     * @return A Connection object representing the database connection
     * @throws SQLException if a database access error occurs or the URL is null
     */
    public static Connection getConnection() throws SQLException {
        if (url == null) {
            throw new SQLException("Database URL is not initialized");
        }
        return DriverManager.getConnection(url);
    }

    /**
     * Initializes the SQLite database by creating necessary tables if they do not exist.
     * This method creates the following tables:
     * - category: stores recipe categories
     * - recipe: stores recipe information
     * - ingredient: stores recipe ingredients
     * - category_recipe: stores the many-to-many relationship between categories and recipes
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create category table
            stmt.execute("CREATE TABLE IF NOT EXISTS category (" +
                        "category_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "category_name TEXT NOT NULL)");
            
            // Create recipe table
            stmt.execute("CREATE TABLE IF NOT EXISTS recipe (" +
                        "recipe_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "title TEXT NOT NULL, " +
                        "prep_time INTEGER, " +
                        "cook_time INTEGER, " +
                        "instruction TEXT, " +
                        "img_addr TEXT, " +
                        "serve INTEGER)");
            
            // Create ingredient table
            stmt.execute("CREATE TABLE IF NOT EXISTS ingredient (" +
                        "pair_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER, " +
                        "ingredient_name TEXT, " +
                        "ingredient_amount INTEGER, " +
                        "unit TEXT, " +
                        "FOREIGN KEY(recipe_id) REFERENCES recipe(recipe_id))");
            
            // Create category_recipe table
            stmt.execute("CREATE TABLE IF NOT EXISTS category_recipe (" +
                        "category_id INTEGER, " +
                        "recipe_id INTEGER, " +
                        "PRIMARY KEY(category_id, recipe_id), " +
                        "FOREIGN KEY(category_id) REFERENCES category(category_id), " +
                        "FOREIGN KEY(recipe_id) REFERENCES recipe(recipe_id))");
            
            LOGGER.info("Database tables initialized successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing database tables", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    /**
     * Safely closes a database connection. If the connection is null, no action
     * is taken. Any SQLExceptions that occur during closing are logged.
     *
     * @param conn The Connection object to be closed
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                LOGGER.fine("Database connection closed successfully");
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing database connection", e);
            }
        }
    }

    /**
     * Test method to verify database connectivity and initialization.
     * This method is primarily used for testing purposes.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            initializeDatabase();
            Connection conn = getConnection();
            LOGGER.info("Database connection test successful!");
            close(conn);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection test failed!", e);
        }
    }
}
