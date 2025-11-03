import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ZorkGUI extends Application {
    private ZorkULGame game;
    private TextArea outputArea;

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

        // Create direction buttons in a grid
        GridPane buttonGrid = new GridPane();
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setHgap(10);
        buttonGrid.setVgap(10);

        Button northBtn = new Button("North");
        Button southBtn = new Button("South");
        Button eastBtn = new Button("East");
        Button westBtn = new Button("West");

        // Add buttons to the grid
        buttonGrid.add(northBtn, 1, 0);  // North at top
        buttonGrid.add(westBtn, 0, 1);   // West at left
        buttonGrid.add(eastBtn, 2, 1);   // East at right
        buttonGrid.add(southBtn, 1, 2);  // South at bottom

        root.setBottom(buttonGrid);

        // Add button actions
        northBtn.setOnAction(e -> processCommand("go north"));
        southBtn.setOnAction(e -> processCommand("go south"));
        eastBtn.setOnAction(e -> processCommand("go east"));
        westBtn.setOnAction(e -> processCommand("go west"));

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
    }

    public static void main(String[] args) {
        launch(args);
    }
}