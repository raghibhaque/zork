import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
    private Stage inventoryStage;
    private boolean inventoryOpen = false;

    @Override
    public void start(Stage primaryStage) {
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);

        // *** DARK MODE TEXT AREA ***
        outputArea.setStyle(
                "-fx-control-inner-background: #000;" +
                        "-fx-background-color: #000;" +
                        "-fx-text-fill: #ffb366;" +
                        "-fx-highlight-fill: #444;" +
                        "-fx-highlight-text-fill: white;"
        );

        game = new ZorkULGame(outputArea);

        BorderPane root = new BorderPane();

        // *** DARK MODE BACKGROUND FOR MAIN ROOT ***
        root.setStyle("-fx-background-color: #111;");

        root.setCenter(outputArea);

        roomImage = new ImageView();
        roomImage.setFitWidth(480);
        roomImage.setFitHeight(320);
        roomImage.setPreserveRatio(true);

        ImageView minimapImage = new ImageView();
        minimapImage.setFitWidth(240);
        minimapImage.setFitHeight(240);
        minimapImage.setPreserveRatio(true);

        try {
            minimapImage.setImage(new Image(Objects.requireNonNull(
                    getClass().getResource("/images/minimap.png")).toExternalForm()
            ));
        } catch (Exception e) {
            System.out.println("Minimap not found yet!");
        }

        HBox imageBar = new HBox(10);
        imageBar.setAlignment(Pos.CENTER);

        // *** DARK MODE IMAGE BACKGROUND ***
        imageBar.setStyle("-fx-background-color: black;");

        imageBar.getChildren().addAll(minimapImage, roomImage);

        TextField inputField = new TextField();
        inputField.setPromptText("Type command (e.g. go north, take torch, talk Orpheon)");
        inputField.setOnAction(event -> {
            processCommand(inputField.getText());
            inputField.clear();
        });

        VBox topBox = new VBox(5);
        topBox.setAlignment(Pos.CENTER);

        // *** DARK TOP BAR ***
        topBox.setStyle("-fx-background-color: black;");

        topBox.getChildren().addAll(inputField, imageBar);

        root.setTop(topBox);

        GridPane buttonGrid = new GridPane();
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setHgap(10);
        buttonGrid.setVgap(10);

        // *** DARK BUTTON GRID ***
        buttonGrid.setStyle("-fx-background-color: #111;");

        Button northBtn = new Button("North");
        Button southBtn = new Button("South");
        Button eastBtn = new Button("East");
        Button westBtn = new Button("West");

        Button takeBtn = new Button("Take");
        Button dropBtn = new Button("Drop");
        Button inventoryBtn = new Button("Inventory");
        Button saveBtn = new Button("Save Game");
        Button loadBtn = new Button("Load Game");

        buttonGrid.add(northBtn, 1, 0);
        buttonGrid.add(westBtn, 0, 1);
        buttonGrid.add(eastBtn, 2, 1);
        buttonGrid.add(southBtn, 1, 2);

        buttonGrid.add(takeBtn, 0, 3);
        buttonGrid.add(inventoryBtn, 1, 3);
        buttonGrid.add(dropBtn, 2, 3);
        buttonGrid.add(saveBtn, 3, 3);
        buttonGrid.add(loadBtn, 4, 3);

        root.setBottom(buttonGrid);

        northBtn.setOnAction(e -> processCommand("go north"));
        southBtn.setOnAction(e -> processCommand("go south"));
        eastBtn.setOnAction(e -> processCommand("go east"));
        westBtn.setOnAction(e -> processCommand("go west"));

        saveBtn.setOnAction(e -> processCommand("save game"));
        loadBtn.setOnAction(e -> processCommand("load game"));

        takeBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Take Item");
            dialog.setHeaderText("Pick up an item");
            dialog.setContentText("Enter item name:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(item -> processCommand("take " + item));
        });

        dropBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Drop Item");
            dialog.setHeaderText("Drop an item");
            dialog.setContentText("Enter item name:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(item -> processCommand("drop " + item));
        });

        inventoryBtn.setOnAction(e -> toggleInventoryWindow());

        Scene scene = new Scene(root, 700, 800);
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W: processCommand("go north"); break;
                case S: processCommand("go south"); break;
                case A: processCommand("go west"); break;
                case D: processCommand("go east"); break;
            }
        });


        primaryStage.getIcons().add(new Image(
                Objects.requireNonNull(getClass().getResource("/images/logo.png")).toExternalForm()));
        primaryStage.setTitle("Flames of Prometheus");


        primaryStage.setScene(scene);
        primaryStage.show();

        displayGameState();
    }

    private void processCommand(String command) {
        String[] words = command.trim().split("\\s+");
        Command cmd;

        String w1 = words.length > 0 ? words[0] : null;
        String w2 = words.length > 1 ? words[1] : null;
        String w3 = words.length > 2 ? words[2] : null;

        CommandWord cw = new CommandWords().getCommandWord(w1);
        cmd = new Command(cw, w2, w3);

        game.processCommand(cmd);
        displayGameState();
    }

    private void displayGameState() {
        outputArea.clear();
        outputArea.appendText(game.getCurrentRoomDescription() + "\n\n");
        outputArea.appendText(game.getPlayerInventory() + "\n");

        String roomName = game.getCurrentRoomDescription().toLowerCase();

        if (roomName.contains("embers")) {
            roomImage.setImage(new Image(
                    Objects.requireNonNull(getClass().getResource("/images/hall.png")).toExternalForm()
            ));
        }
        else if (roomName.contains("echoes")){
            roomImage.setImage(new Image(
                    Objects.requireNonNull(getClass().getResource("/images/echoes.png")).toExternalForm()
            ));
        }
        else if (roomName.contains("spire")) {
            roomImage.setImage(new Image(
                    Objects.requireNonNull(getClass().getResource("/images/spire.png")).toExternalForm()
            ));
        }
        else {
            roomImage.setImage(null);
        }
    }

    private void toggleInventoryWindow() {
        if (inventoryOpen && inventoryStage != null) {
            inventoryStage.close();
            inventoryOpen = false;
            return;
        }

        inventoryStage = new Stage();
        inventoryStage.setTitle("Your Inventory");

        TextArea invText = new TextArea(game.getPlayerInventory());
        invText.setEditable(false);
        invText.setWrapText(true);

        // *** DARK INVENTORY WINDOW ***
        invText.setStyle(
                "-fx-control-inner-background: #222;" +
                        "-fx-background-color: #222;" +
                        "-fx-text-fill: #fff;"
        );

        Scene invScene = new Scene(invText, 300, 200);
        inventoryStage.setScene(invScene);
        inventoryStage.show();

        inventoryOpen = true;

        inventoryStage.setOnCloseRequest(e -> inventoryOpen = false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
