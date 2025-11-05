import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.Optional;

public class ZorkGUI extends Application {
    private ZorkULGame game;
    private TextArea outputArea;
    private ImageView roomImage;


    @Override
    public void start(Stage primaryStage) {
        game = new ZorkULGame();

        // Create the main layout
        BorderPane root = new BorderPane();

        // Create and configure the text area for game output
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setPrefRowCount(10);
        root.setCenter(outputArea);

        // Create and configure the image view for room visuals
        roomImage = new ImageView();
        roomImage.setFitWidth(400);
        roomImage.setFitHeight(200);
        roomImage.setPreserveRatio(true);

        // Create direction buttons in a grid
        GridPane buttonGrid = new GridPane();
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setHgap(10);
        buttonGrid.setVgap(10);

        Button northBtn = new Button("North");
        Button southBtn = new Button("South");
        Button eastBtn = new Button("East");
        Button westBtn = new Button("West");
        Button takeBtn = new Button("Take");
        Button dropBtn = new Button("Drop");


        // Add buttons to the grid
        buttonGrid.add(northBtn, 1, 0);  // top
        buttonGrid.add(westBtn, 0, 1);   // left
        buttonGrid.add(eastBtn, 2, 1);   // right
        buttonGrid.add(southBtn, 1, 2);  // bottom
        buttonGrid.add(takeBtn, 0, 3);   // new row
        buttonGrid.add(dropBtn, 2, 3);   // new row


        root.setBottom(buttonGrid);

        // Add button actions
        northBtn.setOnAction(e -> processCommand("go north"));
        southBtn.setOnAction(e -> processCommand("go south"));
        eastBtn.setOnAction(e -> processCommand("go east"));
        westBtn.setOnAction(e -> processCommand("go west"));

        takeBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Take Item");
            dialog.setHeaderText("Pick up an item");
            dialog.setContentText("Enter the item name:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(item -> processCommand("take " + item));
        });

        dropBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Drop Item");
            dialog.setHeaderText("Drop an item");
            dialog.setContentText("Enter the item name:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(item -> processCommand("drop " + item));
        });

        TextField inputField = new TextField();
        inputField.setPromptText("Type a command (e.g. go north, take key, 'help' to display all commands)");
        inputField.setOnAction(event -> {
            processCommand(inputField.getText());
            inputField.clear();
        });

        VBox topBox = new VBox(0);
        topBox.setAlignment(Pos.CENTER);
        topBox.setStyle("-fx-background-color: #000000;");
        topBox.getChildren().addAll(inputField, roomImage);
        root.setTop(topBox);


        // Create the scene and show the stage
        Scene scene = new Scene(root, 400, 500);
        primaryStage.setTitle("Zork University Adventure");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Show initial game state
        displayGameState();
    }

    private void processCommand(String command) {
        // Split the command into words
        String[] words = command.split(" ");
        if (words.length >= 2) {
            Command cmd = new Command(words[0], words[1]);
            game.processCommand(cmd);
            displayGameState();
        }
    }

    private void displayGameState() {
        // Clear the output area and show the current room description
        outputArea.clear();
        outputArea.appendText(game.getCurrentRoomDescription() + "\n");
        outputArea.appendText(game.getPlayerInventory() + "\n");

        // Update room image based on current room description
        String roomName = game.getCurrentRoomDescription().toLowerCase();

        if (roomName.contains("outside")) {
            roomImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/outside.png")).toExternalForm()));
        }
        else if (roomName.contains("basement")) {
            roomImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/basement.png")).toExternalForm()));
        }
        else if (roomName.contains("theatre")) {
            roomImage.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/theatre.png")).toExternalForm()));
        }
        else {
            roomImage.setImage(null);
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}