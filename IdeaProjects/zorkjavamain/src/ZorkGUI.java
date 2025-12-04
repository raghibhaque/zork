import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.util.Duration;

import java.util.Objects;
import java.util.Optional;
import javafx.scene.media.AudioClip;


public class ZorkGUI extends Application {

    private ZorkULGame game;
    private TextArea outputArea;
    private ImageView roomImage;
    private Stage inventoryStage;
    private boolean inventoryOpen = false;
    private Button giveBtn;
    private Button useBtn;
    private String previousRoomName = "";
    private boolean isMovementCommand = false;
    private boolean isTalk = false;
    private Button talkBtn;
    private Button takeBtn;
    private Button dropBtn;





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
        game.setOnGameWin(() -> playSound("win.mp3"));
        game.setOnGameLose(() -> playSound("lose.mp3"));


        BorderPane root = new BorderPane();

        // *** DARK MODE BACKGROUND FOR MAIN ROOT ***
        root.setStyle("-fx-background-color: #111;");

        root.setCenter(outputArea);

        roomImage = new ImageView();
        roomImage.setFitWidth(480);
        roomImage.setFitHeight(320);
        roomImage.setPreserveRatio(true);

        ImageView minimapImage = new ImageView();
        minimapImage.setFitWidth(256);
        minimapImage.setFitHeight(256);
        minimapImage.setPreserveRatio(true);

        try {
            minimapImage.setImage(new Image(Objects.requireNonNull(
                    getClass().getResource("/images/minimap.png")).toExternalForm()
            ));
            minimapImage.setSmooth(false);
            minimapImage.setCache(false);
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
        northBtn.setTooltip(new Tooltip("Move north into the next room"));

        Button southBtn = new Button("South");
        southBtn.setTooltip(new Tooltip("Move south into the next room"));

        Button eastBtn = new Button("East");
        eastBtn.setTooltip(new Tooltip("Move east into the next room"));

        Button westBtn = new Button("West");
        westBtn.setTooltip(new Tooltip("Move west into the next room"));

        takeBtn = new Button("Take");
        takeBtn.setTooltip(new Tooltip("Pick up an item in this room"));

        dropBtn = new Button("Drop");
        dropBtn.setTooltip(new Tooltip("Drop an item from your inventory"));

        Button inventoryBtn = new Button("Inventory");
        inventoryBtn.setTooltip(new Tooltip("Open your inventory window"));

        Button saveBtn = new Button("Save Game");
        saveBtn.setTooltip(new Tooltip("Save your current game progress"));

        Button loadBtn = new Button("Load Game");
        loadBtn.setTooltip(new Tooltip("Load your previously saved game"));

        talkBtn = new Button("Talk");
        talkBtn.setTooltip(new Tooltip("Talk to the NPC in this room"));

        giveBtn = new Button("Give");
        giveBtn.setTooltip(new Tooltip("Give the Gem to Orpheon"));

        useBtn = new Button("Use");
        useBtn.setTooltip(new Tooltip("Use an item such as the Torch"));

        Button helpBtn = new Button("Help");
        helpBtn.setTooltip(new Tooltip("Show available commands"));

        helpBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Help");
            alert.setHeaderText("Available Commands & Controls");
            alert.setContentText(
                    "Movement: go north/south/east/west or W A S D\n" +
                            "Items: take <item>, drop <item>, use <item>\n" +
                            "NPCs: talk <name>, give <item> <name>\n" +
                            "Other: inventory, save game, load game, ESC to quit."
            );
            alert.showAndWait();
        });
        buttonGrid.add(helpBtn, 1, 0);




        buttonGrid.add(northBtn, 2, 0);
        buttonGrid.add(westBtn, 1, 1);
        buttonGrid.add(eastBtn, 3, 1);
        buttonGrid.add(southBtn, 2, 2);

        buttonGrid.add(giveBtn, 4, 1);
        buttonGrid.add(talkBtn, 4, 2);

        buttonGrid.add(dropBtn, 6, 0);
        buttonGrid.add(inventoryBtn, 6, 1);
        buttonGrid.add(takeBtn, 6, 2);
        buttonGrid.add(useBtn, 6, 3);

        buttonGrid.add(saveBtn, 1, 3);
        buttonGrid.add(loadBtn, 3, 3);

        root.setBottom(buttonGrid);

        northBtn.setOnAction(e ->{ processCommand("go north");
            playSound("move.mp3");
        });
        southBtn.setOnAction(e -> {processCommand("go south");
            playSound("move.mp3");
        });
        eastBtn.setOnAction(e -> {
            processCommand("go east");
            playSound("move.mp3");
        });
        westBtn.setOnAction(e -> {
            processCommand("go west");
            playSound("move.mp3");
        });

        saveBtn.setOnAction(e -> processCommand("save game"));
        loadBtn.setOnAction(e -> processCommand("load game"));

        talkBtn.setOnAction(e -> {
            String npcName = game.getNPCinCurrentRoom();
            if (npcName != null) {
                processCommand("talk " + npcName);
            } else {
                outputArea.appendText("\nThere is no one here to talk to.\n");
            }
        });



        takeBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Take Item");
            dialog.setHeaderText("Pick up an item");
            dialog.setContentText("Enter item name:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(item -> {
                processCommand("take " + item);
                playSound("pickup.mp3");
            });
        });



        dropBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Drop Item");
            dialog.setHeaderText("Drop an item");
            dialog.setContentText("Enter item name:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(item -> processCommand("drop " + item));
        });

        giveBtn.setOnAction(e -> processCommand("give Gem Orpheon"));


        inventoryBtn.setOnAction(e -> toggleInventoryWindow());

        useBtn.setOnAction(e -> processCommand("use torch"));


        Scene scene = new Scene(root, 700, 800);
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W:
                    processCommand("go north");
                    break;
                case S:
                    processCommand("go south");
                    break;
                case A:
                    processCommand("go west");
                    break;
                case D:
                    processCommand("go east");
                    break;
                case ESCAPE:
                    javafx.application.Platform.exit();
                    break;
            }
        });


        primaryStage.getIcons().add(new Image(
                Objects.requireNonNull(getClass().getResource("/images/logo.png")).toExternalForm()));
        primaryStage.setTitle("Flames of Prometheus");


        primaryStage.setScene(scene);
        primaryStage.setWidth(1270);
        primaryStage.setHeight(800);



        primaryStage.show();

        displayGameState();
    }

    private void processCommand(String command) {
        String[] words = command.trim().split("\\s+");
        Command cmd;

        String w1 = words.length > 0 ? words[0] : null;
        String w2 = words.length > 1 ? words[1] : null;
        String w3 = words.length > 2 ? words[2] : null;

        // movement detection
        isMovementCommand = w1 != null && w1.equalsIgnoreCase("go");

        // TALK detection (this is the fix)
        isTalk = w1 != null && w1.equalsIgnoreCase("talk");

        CommandWord cw = new CommandWords().getCommandWord(w1);

        if (w1.equalsIgnoreCase("take")) playSound("pickup.mp3");
        if (w1.equalsIgnoreCase("use")) playSound("use.mp3");

        cmd = new Command(cw, w2, w3);

        game.processCommand(cmd);

        // do NOT refresh GUI if talking
        if (!isTalk) {
            displayGameState();
        }

        // reset after talking
        isTalk = false;

        outputArea.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(250), outputArea);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

    }



    private void displayGameState() {
        outputArea.clear();
        outputArea.appendText(game.getCurrentRoomDescription() + "\n\n");
        outputArea.appendText(game.getPlayerInventory() + "\n");
        talkBtn.setDisable(game.getNPCinCurrentRoom() == null);
        takeBtn.setDisable(!game.roomHasItems());
        dropBtn.setDisable(!game.playerHasItems());


        // Check if room changed AND it was a movement command
        String currentRoomName = game.getCurrentRoomDescription().toLowerCase();
        boolean roomChanged = !currentRoomName.equals(previousRoomName) && isMovementCommand;

        updateRoomImage(roomChanged);

        // Update previous room name
        previousRoomName = currentRoomName;

        // Reset movement flag
        isMovementCommand = false;

        if (playerHasGem()) {
            giveBtn.setVisible(true);
        } else {
            giveBtn.setVisible(false);
        }
        if (playerHasTorch()) {
            useBtn.setVisible(true);
        } else {
            useBtn.setVisible(false);
        }
    }

    /**
     * Updates the room image with a smooth fade transition only if room changed
     */
    private void updateRoomImage(boolean roomChanged) {
        String roomName = game.getCurrentRoomDescription().toLowerCase();
        Image newImage = null;

        if (roomName.contains("ashen")) {
            newImage = new Image(
                    Objects.requireNonNull(getClass().getResource("/images/garden.png")).toExternalForm()
            );
        }
        else if(roomName.contains("crucible")) {
            newImage = new Image(
                    Objects.requireNonNull(getClass().getResource("/images/crucible.png")).toExternalForm()
            );
        }
        else if (roomName.contains("vault")) {
            newImage = new Image(
                    Objects.requireNonNull(getClass().getResource("/images/chains.png")).toExternalForm()
            );
        }
        else if (roomName.contains("embers")) {
            newImage = new Image(
                    Objects.requireNonNull(getClass().getResource("/images/hall.png")).toExternalForm()
            );
        }
        else if (roomName.contains("echoes")){
            newImage = new Image(
                    Objects.requireNonNull(getClass().getResource("/images/echoes.png")).toExternalForm()
            );
        }
        else if (roomName.contains("spire")) {
            newImage = new Image(
                    Objects.requireNonNull(getClass().getResource("/images/spire.png")).toExternalForm()
            );
        }

        // Apply fade transition only if room actually changed
        if (roomChanged) {
            applyFadeTransition(newImage);
        } else {
            // Just set the image directly without animation
            roomImage.setImage(newImage);
        }
    }

    /**
     * Applies a fade out -> fade in transition when changing room images
     */
    private void applyFadeTransition(Image newImage) {
        // Fade out current image
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), roomImage);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        // Fade in new image
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), roomImage);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Set the new image when fade out completes
        fadeOut.setOnFinished(e -> roomImage.setImage(newImage));

        // Chain the transitions
        SequentialTransition transition = new SequentialTransition(fadeOut, fadeIn);
        transition.play();
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

    private boolean playerHasGem() {
        return game.getPlayerInventory().toLowerCase().contains("gem");
    }

    private boolean playerHasTorch() {
        return game.getPlayerInventory().toLowerCase().contains("torch");
    }


    private void playSound(String fileName) {
        try {
            AudioClip clip = new AudioClip(
                    Objects.requireNonNull(getClass().getResource("/sfx/" + fileName)).toExternalForm()
            );
            clip.play();
        } catch (Exception e) {
            System.out.println("Sound file not found: " + fileName);
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}