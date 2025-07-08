package g.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.logging.Level;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for the home view. This class handles the main navigation and recipe management,
 * including displaying recipe lists, recipe details, and managing the create recipe functionality.
 *
 * @author Junzhe Luo
 * @since 2025-6-15
 */
public class HomeController implements Initializable {
    
    /** Logger for logging messages */
    private static final Logger LOGGER = Logger.getLogger(HomeController.class.getName());
    
    /** Controller for the recipe list view */
    @FXML
    private ListViewController listViewController;
    
    /** Controller for the recipe detail card view */
    @FXML
    private RecipeDetailCardController recipeDetailCardController;
    
    /** Controller for the update view */
    @FXML
    private UpdateViewController updateViewController;
    
    /** Controller for the search bar */
    @FXML
    private SearchBarController searchBarController;
    
    /** VBox for the empty pane (placeholder) */
    @FXML 
    private VBox emptyPane;
    
    /** Parent node for the recipe detail card */
    @FXML 
    private Parent recipeDetailCard;

    /**
     * Initializes the controller, sets up callbacks and default view.
     * This method configures the interaction between different components
     * and sets up the initial empty pane view.
     * 
     * @param location The location used to resolve relative paths for the root object, or null if unknown
     * @param resources The resources used to localize the root object, or null if not localized
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupListViewControllerCallback();
        setupRecipeDetailCardCallback();
        setupSearchBarCallback();
        showEmptyPane(); 
    }

    /**
     * Sets up the callback for the list view controller to handle recipe selection.
     */
    private void setupListViewControllerCallback() {
        listViewController.setCallback(recipeId -> {
            recipeDetailCardController.loadRecipeData(recipeId);
            showDetailPane();
        });
    }

    /**
     * Sets up the callback for the recipe detail card controller.
     */
    private void setupRecipeDetailCardCallback() {
        recipeDetailCardController.setCallback(new RecipeDetailCardController.DetailCallback() {
            @Override
            public void onRecipeDeleted(int recipeId) {
                listViewController.refreshList();
                showEmptyPane();
            }
            
            @Override
            public void onRecipeUpdated(int recipeId) {
                listViewController.refreshListAndRetainSelection(recipeId);
            }
            
            @Override
            public void onBack() {
                LOGGER.info("Back button clicked, returning to list view");
                showEmptyPane();
            }
        });
    }

    /**
     * Sets up the callback for the search bar controller.
     */
    private void setupSearchBarCallback() {
        searchBarController.setCallback(new SearchBarController.SearchCallback() {
            @Override
            public void onSearch(String query) {
                listViewController.search(query);
            }
        });
    }

    /**
     * Handles the create recipe button click event. Opens the create recipe dialog.
     * This method loads the CreateView FXML and displays it in a new stage.
     */
    @FXML
    public void onCreateClicked() {
        LOGGER.info("Create button clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/g/CreateView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1000, 600);
            scene.getStylesheets().add(getClass().getResource("/g/app.css").toExternalForm());
            CreateViewController controller = loader.getController();
            
            controller.setOnCreateSuccess(() -> {
                LOGGER.info("CreateView created successfully, refreshing ListView");
                listViewController.refreshList();
            });
            
            Stage stage = new Stage();
            stage.setTitle("Create Recipe");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load CreateView.fxml page", e);
        }
    }

    /**
     * Shows the empty pane and hides the recipe detail card.
     * This method is called when no recipe is selected or when returning from detail view.
     */
    public void showEmptyPane() {
        emptyPane.setVisible(true);
        emptyPane.setManaged(true);
        recipeDetailCard.setVisible(false);
        recipeDetailCard.setManaged(false);
    }

    /**
     * Shows the recipe detail card and hides the empty pane.
     * This method is called when a recipe is selected from the list.
     */
    private void showDetailPane() {
        emptyPane.setVisible(false);
        emptyPane.setManaged(false);
        recipeDetailCard.setVisible(true);
        recipeDetailCard.setManaged(true);
    }
}
