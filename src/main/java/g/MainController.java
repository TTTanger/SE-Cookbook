package g;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * Main controller for the application's primary layout.
 * This class manages the main content area and handles navigation
 * between different views (home and category pages).
 * 
 * @author Junzhe Luo
 * @since 2025-6-15
 */
public class MainController {
    
    @FXML 
    private StackPane contentPane;
    
    @FXML 
    private Node homePage;
    
    @FXML 
    private Node categoryPage;

    /**
     * Initializes the controller after FXML loading.
     * Sets up the initial view to show the home page.
     */
    @FXML
    public void initialize() {
        showHome(); 
    }

    /**
     * Shows the home page and hides the category page.
     * This method is called when the home navigation is selected.
     */
    @FXML
    private void showHome() {
        homePage.setVisible(true);
        categoryPage.setVisible(false);
    }

    /**
     * Shows the category page and hides the home page.
     * This method is called when the category navigation is selected.
     */
    @FXML
    private void showCategory() {
        homePage.setVisible(false);
        categoryPage.setVisible(true);
    }
}
