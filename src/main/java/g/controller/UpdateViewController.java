package g.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import g.dto.RecipeDetailRequest;
import g.dto.RecipeDetailResponse;
import g.model.Ingredient;
import g.model.Recipe;
import g.service.RecipeService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controller for the update recipe view. Handles the update of existing recipes.
 * 
 * @author Junzhe Luo
 * @since 2025-6-15
 */
public class UpdateViewController {

    /** Service for recipe operations */
    private final RecipeService recipeService;

    /** Previous data */
    @FXML
    private RecipeDetailResponse previousData;

    /** Title field */
    @FXML
    private TextField titleField;
    
    /** Preparation time field */
    @FXML
    private TextField prepTimeField;

    /** Cooking time field */
    @FXML
    private TextField cookTimeField;

    /** Servings field */
    @FXML
    private TextField serveField;

    /** Instruction field */
    @FXML
    private TextArea instructionField;

    /** Ingredient container */
    @FXML
    private VBox ingredientContainer;

    /** Submit button */
    @FXML
    private Button submitButton;

    /** Upload button */
    @FXML
    private Button uploadButton;

    /** Clear image button */
    @FXML
    private Button clearImageButton;

    /** Deleted pair IDs */
    private final List<Integer> deletedPairIds = new ArrayList<>();

    /** Uploaded image path */
    private String uploadedImgPath = null;

    /** Original image path */
    private String originalImgPath = null;

    /** ImageView for previewing the uploaded image */
    @FXML
    private ImageView imgPreview;

    /** Label for image preview hint */
    @FXML
    private Label imgHint;

    /** User image directory */
    private static final String USER_IMG_DIR = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "cookbook" + File.separator + "imgs";

    /**
     * Constructor initializes the recipe service.
     */
    public UpdateViewController() {
        this.recipeService = new RecipeService();
    }

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        System.out.println("UpdateViewController initialized");
        if (ingredientContainer.getChildren().isEmpty()) {
            addIngredient();
        }
        if (uploadedImgPath != null && !uploadedImgPath.isEmpty()) {
            imgPreview.setImage(new Image(new File(USER_IMG_DIR + File.separator + uploadedImgPath).toURI().toString()));
            imgHint.setVisible(false);
        } else {
            imgPreview.setImage(null);
            imgHint.setVisible(true);
        }
    }

    /**
     * Loads the previous data into the fields.
     * 
     * @param title the title of the recipe
     * @param prepTime the preparation time of the recipe
     * @param cookTime the cooking time of the recipe
     * @param serve the number of servings of the recipe
     * @param ingredients the ingredients of the recipe
     * @param instructions the instructions of the recipe
     * @param imgAddr the image address of the recipe
     */
    @FXML
    public void loadPreviousData(String title, int prepTime, int cookTime, int serve,
            List<Ingredient> ingredients, String instructions, String imgAddr) {
        ingredientContainer.getChildren().clear();

        this.titleField.setText(title);
        this.prepTimeField.setText(String.valueOf(prepTime));
        this.cookTimeField.setText(String.valueOf(cookTime));
        this.serveField.setText(String.valueOf(serve));

        if (ingredients == null || ingredients.isEmpty()) {
            addIngredient();
        } else {
            for (Ingredient ingredient : ingredients) {
                HBox entry = new HBox(10);
                entry.setUserData(ingredient.getPairId());
                System.out.println("Pair ID: " + ingredient.getPairId());

                TextField nameField = new TextField(ingredient.getIngredientName());
                nameField.setPromptText("Name");

                TextField quantityField = new TextField(String.valueOf(ingredient.getIngredientAmount()));
                quantityField.setPromptText("Amount(Only Integer)");

                TextField unitField = new TextField(ingredient.getIngredientUnit());
                unitField.setPromptText("Unit");

                Button addButton = new Button("+");
                addButton.getStyleClass().add("button");
                addButton.setOnAction(e -> addIngredient());

                Button removeBtn = new Button("-");
                removeBtn.getStyleClass().add("button");
                removeBtn.setOnAction(e -> {
                    if (ingredientContainer.getChildren().size() > 1) {
                        Integer ingId = (Integer) entry.getUserData();
                        if (ingId != null) {
                            deletedPairIds.add(ingId);
                            System.out.println("Deleted ingredient ID: " + ingId);
                        }
                        ingredientContainer.getChildren().remove(entry);
                        updateRemoveButtons();
                    }
                });

                entry.getChildren().addAll(nameField, new Label(":"), quantityField, unitField, removeBtn, addButton);
                ingredientContainer.getChildren().add(entry);
            }
        }
        this.instructionField.setText(instructions);
        
        this.originalImgPath = imgAddr;
        if (imgAddr != null && !imgAddr.isEmpty() && !"Upload_Img.png".equals(imgAddr)) {
            String imgFileName = imgAddr;
            if (imgFileName.startsWith("imgs/")) {
                imgFileName = imgFileName.substring(5);
            }
            
            File imgFile = new File(USER_IMG_DIR + File.separator + imgFileName);
            if (imgFile.exists()) {
                imgPreview.setImage(new Image(imgFile.toURI().toString()));
                imgHint.setVisible(false);
            } else {
                File oldImgFile = new File("imgs" + File.separator + imgFileName);
                if (oldImgFile.exists()) {
                    imgPreview.setImage(new Image(oldImgFile.toURI().toString()));
                    imgHint.setVisible(false);
                } else {
                    imgPreview.setImage(null);
                    imgHint.setVisible(true);
                }
            }
        } else {
            imgPreview.setImage(null);
            imgHint.setVisible(true);
        }
        
        updateRemoveButtons();
    }

    /**
     * Handles the upload button click event.
     */
    @FXML
    public void uploadClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
        if (file != null) {
            try {
                String ext = file.getName().substring(file.getName().lastIndexOf('.'));
                String newName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ext;
                File imgsDir = new File(USER_IMG_DIR);
                if (!imgsDir.exists()) {
                    imgsDir.mkdirs();
                }
                File dest = new File(imgsDir, newName);
                Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                uploadedImgPath = newName; 
                imgPreview.setImage(new Image(dest.toURI().toString()));
                imgHint.setVisible(false);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Image uploaded successfully!", ButtonType.OK);
                alert.setTitle("Info");
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Image upload failed!", ButtonType.OK);
                alert.setTitle("Error");
                alert.showAndWait();
            }
        }
    }

    /**
     * Handles the clear image button click event. Clears the uploaded image and preview.
     */
    @FXML
    public void clearImageClicked() {
        uploadedImgPath = null;
        imgPreview.setImage(null);
        imgHint.setVisible(true);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Image cleared successfully!", ButtonType.OK);
        alert.setTitle("Info");
        alert.showAndWait();
    }

    /**
     * Adds a new ingredient to the ingredient container.
     */
    @FXML
    private void addIngredient() {
        HBox entry = new HBox(10);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Amount");

        TextField unitField = new TextField();
        unitField.setPromptText("Unit");

        Button addButton = new Button("+");
        addButton.getStyleClass().add("button");
        addButton.setOnAction(e -> addIngredient());

        Button removeBtn = new Button("-");
        removeBtn.getStyleClass().add("button");
        removeBtn.setOnAction(e -> {
            if (ingredientContainer.getChildren().size() > 1) {
                Integer ingId = (Integer) entry.getUserData();
                if (ingId != null) {
                    deletedPairIds.add(ingId);
                    System.out.println("Deleted ingredient ID: " + ingId);
                }
                ingredientContainer.getChildren().remove(entry);
                updateRemoveButtons();
            }
        });

        entry.getChildren().addAll(nameField, new Label(":"), quantityField, unitField, removeBtn, addButton);
        ingredientContainer.getChildren().add(entry);

        updateRemoveButtons(); 
    }
    
    /**
     * Updates the state of remove buttons for all ingredient rows. Only enabled if more than one row exists.
     */
    private void updateRemoveButtons() {
        int count = ingredientContainer.getChildren().size();
        for (var node : ingredientContainer.getChildren()) {
            if (node instanceof HBox hbox) {
                for (var child : hbox.getChildren()) {
                    if (child instanceof Button btn && "-".equals(btn.getText())) {
                        btn.setDisable(count <= 1);
                    }
                }
            }
        }
    }

    /**
     * Sets the previous data for the recipe.
     * 
     * @param previousData the previous data
     */
    public void setPreviousData(RecipeDetailResponse previousData) {
        this.previousData = previousData;
        Recipe previousRecipe = previousData.getRecipe();
        System.out.println("Setting previous data for recipe: " + previousRecipe.getTitle());
        loadPreviousData(
                previousRecipe.getTitle(),
                previousRecipe.getPrepTime(),
                previousRecipe.getCookTime(),
                previousRecipe.getServe(),
                previousData.getIngredients(),
                previousRecipe.getInstruction(),
                previousRecipe.getImgAddr()
        );
        System.out.println("Previous data ingredients:" + previousData.getIngredients());
    }

    /**
     * Handles the update recipe button click event.
     */
    public void handleUpdateRecipe() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to save the changes to the recipe?");
        var result = confirmAlert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return; 
        }

        String title = titleField.getText();
        String prepTime = prepTimeField.getText();
        String cookTime = cookTimeField.getText();
        String serve = serveField.getText();
        String instruction = instructionField.getText();
        String imgAddr = (uploadedImgPath != null && !uploadedImgPath.isEmpty()) ? uploadedImgPath : originalImgPath;

        if (title == null || title.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Title cannot be empty");
            alert.setTitle("Error");
            alert.showAndWait();
            return;
        }
        if (instruction == null || instruction.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Instruction cannot be empty");
            alert.setTitle("Error");
            alert.showAndWait();
            return;
        }
        int serveInt, prepInt, cookInt;
        try {
            serveInt = Integer.parseInt(serve);
            if (serveInt <= 0) throw new Exception();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Servings must be a positive integer");
            alert.setTitle("Error");
            alert.showAndWait();
            return;
        }
        try {
            prepInt = Integer.parseInt(prepTime);
            if (prepInt < 0) throw new Exception();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Prep Time must be a non-negative integer");
            alert.setTitle("Error");
            alert.showAndWait();
            return;
        }
        try {
            cookInt = Integer.parseInt(cookTime);
            if (cookInt < 0) throw new Exception();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Cook Time must be a non-negative integer");
            alert.setTitle("Error");
            alert.showAndWait();
            return;
        }
        List<String> ingredientNames = new ArrayList<>();
        for (var node : ingredientContainer.getChildren()) {
            if (node instanceof HBox hbox) {
                List<javafx.scene.Node> fields = hbox.getChildren();
                TextField nameField = null;
                TextField quantityField = null;
                int fieldCount = 0;
                for (javafx.scene.Node child : fields) {
                    if (child instanceof TextField tf) {
                        if (fieldCount == 0) {
                            nameField = tf;
                        } else if (fieldCount == 1) {
                            quantityField = tf;
                        } 
                        fieldCount++;
                    }
                }
                if (nameField == null || nameField.getText().trim().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Ingredient Name cannot be empty");
                    alert.setTitle("Error");
                    alert.showAndWait();
                    return;
                }
                String ingName = nameField.getText().trim();
                if (ingredientNames.contains(ingName)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Ingredient Name must be unique");
                    alert.setTitle("Error");
                    alert.showAndWait();
                    return;
                }
                ingredientNames.add(ingName);
                int quantity;
                try {
                    quantity = Integer.parseInt(quantityField.getText());
                    if (quantity <= 0) throw new Exception();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Ingredient Amount must be a positive integer");
                    alert.setTitle("Error");
                    alert.showAndWait();
                    return;
                }
            }
        }

        Recipe recipe = new Recipe();
        recipe.setRecipeId(previousData.getRecipe().getRecipeId());
        recipe.setTitle(title);
        recipe.setPrepTime(Integer.parseInt(prepTime));
        recipe.setCookTime(Integer.parseInt(cookTime));
        recipe.setServe(Integer.parseInt(serve));
        recipe.setInstruction(instruction);
        recipe.setImgAddr((imgAddr != null && !imgAddr.isEmpty()) ? imgAddr : null);

        List<Ingredient> ingredients = new ArrayList<>();
        for (var node : ingredientContainer.getChildren()) {
            if (node instanceof HBox hbox) {
                List<javafx.scene.Node> fields = hbox.getChildren();

                TextField nameField = null;
                TextField quantityField = null;
                TextField unitField = null;

                int fieldCount = 0;
                for (javafx.scene.Node child : fields) {
                    if (child instanceof TextField tf) {
                        if (fieldCount == 0) {
                            nameField = tf;
                        } else if (fieldCount == 1) {
                            quantityField = tf;
                        } else if (fieldCount == 2) {
                            unitField = tf;
                        }
                        fieldCount++;
                    }
                }

                if (nameField != null && quantityField != null && unitField != null) {
                    String name = nameField.getText();
                    String quantityStr = quantityField.getText();
                    String unit = unitField.getText();

                    try {
                        int quantity = Integer.parseInt(quantityStr);
                        Ingredient ing = new Ingredient();
                        ing.setRecipeId(recipe.getRecipeId());
                        ing.setIngredientName(name);
                        ing.setIngredientAmount(quantity);
                        ing.setIngredientUnit(unit);
                        Object pairId = hbox.getUserData();
                        if (pairId instanceof Integer) {
                            ing.setPairId((Integer) pairId);
                        }
                        ingredients.add(ing);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid quantity: " + quantityStr);
                    }
                }
            }
        }

        RecipeDetailRequest request = new RecipeDetailRequest(recipe, ingredients, deletedPairIds);
        boolean success = recipeService.updateRecipe(request);
        if (success) {
            Alert info = new Alert(Alert.AlertType.INFORMATION, "Recipe updated successfully!", ButtonType.OK);
            info.setTitle("Info");
            info.showAndWait();

            if (updateCallback != null) {
                updateCallback.onUpdateSuccess();
            }

            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Callback interface for update recipe.
     * 
     * @author Junzhe Luo
     * @since 2025-6-15
     */
    public interface UpdateCallback {
        /**
         * Called when the update recipe is successful.
         */
        void onUpdateSuccess();
    }

    /** Update callback */
    private UpdateCallback updateCallback;

    /**
     * Sets the update callback.
     * 
     * @param callback the update callback
     */
    public void setUpdateCallback(UpdateCallback callback) {
        this.updateCallback = callback;
    }
}
