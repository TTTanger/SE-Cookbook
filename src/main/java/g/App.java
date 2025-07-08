package g;

import java.io.IOException;
import java.util.Locale;

import g.utils.DBUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

/**
 * Main JavaFX application class for the Cookbook application.
 * This class initializes the application, sets up the main window,
 * and manages scene transitions.
 * 
 * @author Junzhe Luo
 * @since 2025-6-15
 */
public class App extends Application {

    /** Scene for the application */
    private static Scene scene;
    
    /** Window width */
    private static final int WINDOW_WIDTH = 1350;

    /** Window height */
    private static final int WINDOW_HEIGHT = 800;

    /** Minimum window width */
    private static final int MIN_WIDTH = 1280;

    /** Minimum window height */
    private static final int MIN_HEIGHT = 960;

    /**
     * Initializes and displays the main application window.
     * Sets up the scene, stylesheets, and window properties.
     * 
     * @param stage the primary stage for the application
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("Starting JavaFX application...");
        
        scene = new Scene(loadFXML("main"), WINDOW_WIDTH, WINDOW_HEIGHT);
        
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("/g/app.css").toExternalForm());
        
        System.setProperty("javafx.userAgentStylesheetUrl", "");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/g/cookbook.ico")));
        stage.setScene(scene);
        stage.setMinWidth(MIN_WIDTH); 
        stage.setMinHeight(MIN_HEIGHT);
        stage.setTitle("Cookbook Application");
        stage.show();
        
        System.out.println("JavaFX application window should be visible now");
    }

    /**
     * Changes the root node of the current scene to the specified FXML view.
     * 
     * @param fxml the name of the FXML file (without extension)
     * @throws IOException if the FXML file cannot be loaded
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Loads an FXML file and returns the root node.
     * 
     * @param fxml the name of the FXML file (without extension)
     * @return the root node of the loaded FXML
     * @throws IOException if the FXML file cannot be loaded
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Main entry point for the application.
     * Initializes the database and launches the JavaFX application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        DBUtil.initializeDatabase();
        DBUtil.initializeUserImageDirectory();
        launch(args);
    }
}
