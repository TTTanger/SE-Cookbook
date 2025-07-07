package g.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import g.dto.CalculateResponse;
import g.dto.RecipeDetailResponse;
import g.model.Ingredient;
import g.model.Recipe;
import g.service.CalculateService;
import g.service.RecipeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;

/**
 * Controller for the recipe detail card view. Handles the display and
 * interaction of recipe details.
 *
 * @author Junzhe Luo
 */
public class RecipeDetailCardController implements Initializable {

    /** Service for recipe operations */
    private final RecipeService recipeService;

    /** Service for ingredient calculation */
    private final CalculateService calculateService;

    /** The current recipe ID */
    private int recipeId;

    /** User image directory */
    private static final String USER_IMG_DIR = System.getProperty("user.home") + File.separator + "Documents"
            + File.separator + "cookbook" + File.separator + "imgs";

    /** Update view controller */
    @FXML
    private UpdateViewController updateViewController;

    /** Label for the recipe title */
    @FXML
    private Label title;

    /** Label for preparation time */
    @FXML
    private Label prepTime;

    /** Label for cooking time */
    @FXML
    private Label cookTime;

    /** TextArea for ingredients (not used in table) */
    @FXML
    private TextArea ingredients;

    /** Label for instructions */
    @FXML
    private TextArea instructions;

    /** Spinner for servings */
    @FXML
    private Spinner<Integer> serveSpinner;

    /** TableView for ingredients */
    @FXML
    private TableView<Ingredient> ingredientsTable;

    /** TableColumn for ingredient name */
    @FXML
    private TableColumn<Ingredient, String> ingredientNameCol;

    /** TableColumn for ingredient amount */
    @FXML
    private TableColumn<Ingredient, Integer> ingredientAmountCol;

    /** TableColumn for ingredient unit */
    @FXML
    private TableColumn<Ingredient, String> ingredientUnitCol;

    /** Label for empty recipe message */
    @FXML
    private Label emptyLabel;

    /** VBox for detail content */
    @FXML
    private VBox detailContainer;

    /** ImageView for recipe image */
    @FXML
    private ImageView imgView;

    /** AnchorPane for empty message */
    @FXML
    private AnchorPane emptyPane;

    /** VBox for ingredients */
    @FXML
    private VBox ingredientsBox;

    /** Label for instructions */
    @FXML
    private Label instructionsLabel;

    /** Button for categorize recipe */
    @FXML
    private Button recipeCategorizeButton;

    /** Button for update recipe */
    @FXML
    private Button recipeUpdateButton;

    /** Button for delete recipe */
    @FXML
    private Button recipeDeleteButton;

    /** Button for back */
    @FXML
    private Button recipeBackButton;
    

    /**
     * Constructor initializes the recipe and calculation services.
     */
    public RecipeDetailCardController() {
        this.recipeService = new RecipeService();
        this.calculateService = new CalculateService();

    }

    /**
     * Initializes the controller and sets up the TableView and Spinner.
     * 
     * @param location  The location used to resolve relative paths for the root
     *                  object, or null if unknown.
     * @param resources The resources used to localize the root object, or null if
     *                  not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (serveSpinner.getValueFactory() == null) {
            serveSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        }
        System.out.println("RecipeDetailCardController initialized");
    }

    /**
     * Loads recipe data by recipe ID and updates the detail view.
     * 
     * @param recipeId The recipe ID
     */
    @FXML
    public void loadRecipeData(int recipeId) {
        this.recipeId = recipeId;
        RecipeDetailResponse recipeDetail = recipeService.getRecipeById(recipeId);
        Recipe recipe = recipeDetail.getRecipe();
        this.title.setText(recipe.getTitle());
        this.prepTime.setText(String.valueOf(recipe.getPrepTime()));
        this.cookTime.setText(String.valueOf(recipe.getCookTime()));
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100,
                recipe.getServe());
        serveSpinner.setValueFactory(valueFactory);
        serveSpinner.setEditable(true);
        serveSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            updateIngredientsBox(recipeId, newValue);
        });
        updateIngredientsBox(recipeId, serveSpinner.getValue());
        instructionsLabel.setText(recipe.getInstruction());
        if (recipe.getImgAddr() != null && !recipe.getImgAddr().isEmpty()) {
            try {
                Image img = null;
                String imgFileName = recipe.getImgAddr();
                if (imgFileName.startsWith("imgs/")) {
                    imgFileName = imgFileName.substring(5);
                }
                File imgFile = new File(USER_IMG_DIR + File.separator + imgFileName);
                if (imgFile.exists()) {
                    img = new Image(imgFile.toURI().toString(), true);
                } else {
                    File oldImgFile = new File("imgs" + File.separator + imgFileName);
                    if (oldImgFile.exists()) {
                        img = new Image(oldImgFile.toURI().toString(), true);
                    }
                }
                imgView.setImage(img);
            } catch (Exception e) {
                imgView.setImage(null);
            }
        } else {
            imgView.setImage(null);
        }
        showRecipeDetail();
    }

    /**
     * Handles the delete recipe button click event.
     * 
     * @param event The action event
     */
    @FXML
    public void onRecipeDeleteClicked(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this recipe?");
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                recipeService.deleteRecipe(recipeId);
                System.out.println("Recipe with ID " + recipeId + " deleted successfully.");
                if (callback != null) {
                    callback.onRecipeDeleted(recipeId);
                }
            }
        });
    }

    /**
     * Handles the update recipe button click event.
     * 
     * @param event The action event
     */
    @FXML
    public void onRecipeUpdateClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/g/UpdateView.fxml"));
            Parent root = loader.load();
            this.updateViewController = loader.getController();
            RecipeDetailResponse recipeDetail = recipeService.getRecipeById(recipeId);
            updateViewController.setPreviousData(recipeDetail);
            updateViewController.setUpdateCallback(() -> {
                loadRecipeData(recipeId);
                if (callback != null) {
                    callback.onRecipeUpdated(recipeId);
                }
            });
            Stage stage = new Stage();
            stage.setTitle("Update Recipe");
            stage.setScene(new Scene(root, 1000, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load UpdateView.fxml page");
        }
    }

    /**
     * Handles the back button click event.
     * 
     * @param event The action event
     */
    @FXML
    public void onBackClicked(ActionEvent event) {
        showEmptyMessage(); // Clear current content and show "Please Select a Recipe"
        if (callback != null) {
            callback.onBack();
        }
    }

    /**
     * Handles the categorize recipe button click event.
     * 
     * @param event The action event
     */
    @FXML
    public void onRecipeCategorizeClicked(ActionEvent event) {
        System.out.println("Recipe categorize button clicked for recipe ID: " + recipeId);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/g/AddRecipeToCategory.fxml"));
            Parent root = loader.load();
            AddRecipeToCategoryController controller = loader.getController();
            controller.setRecipeId(recipeId);
            Stage stage = new Stage();
            stage.setTitle("Add Recipe to Category");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load AddRecipeToCategory.fxml page");
        }
    }

    /**
     * Callback interface for detail actions.
     */
    public interface DetailCallback {
        void onRecipeDeleted(int recipeId);

        void onRecipeUpdated(int recipeId);

        void onBack();
    }

    private DetailCallback callback;

    /**
     * Sets the callback for detail actions.
     * 
     * @param callback The callback to set
     */
    public void setCallback(DetailCallback callback) {
        this.callback = callback;
    }

    /**
     * Updates the ingredients display based on servings.
     * 
     * @param recipeId The recipe ID
     * @param serve    The number of servings
     */
    private void updateIngredientsBox(int recipeId, int serve) {
        CalculateResponse scaledIngredients = calculateService.IngredientCalculate(recipeId, serve);
        List<Ingredient> ingredientsList = scaledIngredients.getIngredients();
        ingredientsBox.getChildren().clear();
        GridPane grid = new GridPane();
        grid.setHgap(0);
        grid.setVgap(0);
        String[] headers = { "Name", "Amount", "Unit" };
        int[] minWidths = { 160, 60, 80 }; // name, amount, unit
        int[] prefWidths = { 200, 80, 100 };
        for (int col = 0; col < 3; col++) {
            Label header = new Label(headers[col]);
            header.setWrapText(true);
            header.setMinWidth(minWidths[col]);
            header.setPrefWidth(prefWidths[col]);
            header.setMaxWidth(Double.MAX_VALUE);
            header.getStyleClass().add("ingredient-header");
            header.setAlignment(javafx.geometry.Pos.TOP_LEFT);
            header.setMaxHeight(Double.MAX_VALUE);
            header.setStyle("-fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");
            GridPane.setHgrow(header, javafx.scene.layout.Priority.ALWAYS);
            GridPane.setVgrow(header, javafx.scene.layout.Priority.ALWAYS);
            grid.add(header, col, 0);
        }
        if (ingredientsList != null) {
            for (int row = 0; row < ingredientsList.size(); row++) {
                Ingredient ing = ingredientsList.get(row);
                String[] values = {
                        ing.getIngredientName(),
                        String.valueOf(ing.getIngredientAmount()),
                        ing.getIngredientUnit()
                };
                for (int col = 0; col < 3; col++) {
                    Label cell = new Label(values[col]);
                    cell.setWrapText(true);
                    cell.setMinWidth(minWidths[col]);
                    cell.setPrefWidth(prefWidths[col]);
                    cell.setMaxWidth(Double.MAX_VALUE);
                    cell.getStyleClass().add("ingredient-row");
                    if (row % 2 == 1)
                        cell.getStyleClass().add("alt");
                    cell.setAlignment(javafx.geometry.Pos.TOP_LEFT);
                    cell.setMaxHeight(Double.MAX_VALUE);
                    cell.setStyle("-fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");
                    GridPane.setHgrow(cell, javafx.scene.layout.Priority.ALWAYS);
                    GridPane.setVgrow(cell, javafx.scene.layout.Priority.ALWAYS);
                    grid.add(cell, col, row + 1);
                }
            }
        }

        for (int col = 0; col < 3; col++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setMinWidth(minWidths[col]);
            cc.setPrefWidth(prefWidths[col]);
            cc.setHgrow(javafx.scene.layout.Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }
        ingredientsBox.getChildren().add(grid);
    }

    /**
     * Show the empty message pane and hide the detail container.
     */
    public void showEmptyMessage() {
        emptyPane.setVisible(true);
        emptyPane.setManaged(true);
        detailContainer.setVisible(false);
        detailContainer.setManaged(false);

        if (ingredientsBox != null) {
            ingredientsBox.getChildren().clear();
        }

        if (instructionsLabel != null) {
            instructionsLabel.setText("");
        }
    }

    /**
     * Show the detail container and hide the empty message pane.
     */
    public void showRecipeDetail() {
        emptyPane.setVisible(false);
        emptyPane.setManaged(false);
        detailContainer.setVisible(true);
        detailContainer.setManaged(true);
    }
}
