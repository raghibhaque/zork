import javafx.application.Platform;
import javafx.scene.control.TextArea;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

public class ZorkULGame {
    private final Parser parser;
    private Character player;
    private TextArea outputArea;
    private static final String SAVE_FILE = "savegame.ser";
    private Room hallOfEmbers;
    private Room chamberOfEchoes;
    private Room ashenGarden;
    private Item note;
    private Room ironSpire;
    private boolean chainsPuzzleSolved = false;
    private boolean finalRiddleSolved = false;
    private Room crucible;
    private Room vaultOfChains;
    private int sentinelFailCount = 0;


    // Simple annotation for marking commands
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface CommandDescription {
        String value();
    }


    public ZorkULGame() {
        createRooms();
        parser = new Parser();
        print("ZorkULGame started");

    }

    public ZorkULGame(TextArea outputArea) { // GUI version
        this.outputArea = outputArea;
        createRooms();
        // loadGameIfExists();
        parser = new Parser();
    }

    private void createRooms() {
        hallOfEmbers = new Room(
                """
                        You awaken in the Hall of Embers.
                        The walls flicker faintly with dying torches. Charred murals of gods and flame line the stone.
                        The air is heavy with the scent of ash — and the faint echo of fire long forgotten.\
                        Find me the gem and I will give you a pious reward."""
        );

        chamberOfEchoes = new Room(
                "You enter the Chamber of Echoes.\n" +
                        "The sound of your footsteps repeats endlessly. Whispered voices murmur truths and lies alike.\n" +
                        "You feel as though the walls themselves are listening. The shiny ruby glimmers among the generations of skulls." +
                        "The note strikes your eyes."
        );

        ironSpire = new Room(
                """
                        You stand before the Iron Spire.
                        An obsidian tower looms above, its surface etched with glowing runes.
                        The air hums faintly — power dormant, waiting to be awakened."""
        );

        ashenGarden = new Room(
                """
                        You step into the Ashen Garden.
                        What once were trees are now blackened silhouettes. Their roots still pulse faintly with red embers beneath the soil.
                        Something ancient sleeps here, beneath the ash."""
        );

        crucible = new Room(
                """
                        You arrive in the Crucible.
                        A massive forge dominates the center. Blue fire burns without heat — whispering in a forgotten tongue.
                        This place feels like both a tomb and a beginning."""
        );

        vaultOfChains = new Room(
                "You descend into the Vault of Chains.\n" +
                        "Rusting shackles hang from the ceiling. The scent of iron and smoke fills your lungs.\n" +
                        "On the walls are carvings of a man bound to stone — the punishment of Prometheus."
        );

        hallOfEmbers.setExit("north", chamberOfEchoes);
        chamberOfEchoes.setExit("south", hallOfEmbers);
        ironSpire.setExit("west", chamberOfEchoes);
        ashenGarden.setExit("north", ironSpire);
        ashenGarden.setExit("east", vaultOfChains);
        vaultOfChains.setExit("west", ashenGarden);
        vaultOfChains.setExit("south", crucible);
        crucible.setExit("north", vaultOfChains);
        ironSpire.setExit("south", ashenGarden);
        //  ashenGarden.setExit("south", ironSpire);


        player = new Character("player", hallOfEmbers);


        NPC Orpheon = new NPC("Orpheon", hallOfEmbers,
                """
                        "Ah… you wake at last, Child of Ash.
                        
                        This hall once blazed with the fire of Prometheus,
                        but its heart has gone cold. The Ember Altar lies dormant,
                        and with it, the northern gate remains sealed.
                        
                        Take the Torch — the last loyal spark of mankind.
                        Only a true flame can stir the altar back to life.
                        
                        And listen well,
                        
                        Beyond this chamber lies the Chamber of Echoes.
                        Within its walls rests a stolen glimmer — a gem touched by ancient fire.
                        Bring it to me, and the flame will remember your name."
                        """
        );

        hallOfEmbers.addNPC(Orpheon);
        NPC sentinel = new NPC("Sentinel", crucible,
                """
                        "A towering figure of living obsidian guards the inner flame.
                        Speak the truth, wanderer. The First Fire bends only to those who understand it.
                        The forge dims around you.
                        "You have three chances. On the fourth… you will burn as all false prophets do."
                        """
        );
        crucible.addNPC(sentinel);

        NPC Pyrrak = new NPC(
                "Pyrrak",
                ironSpire,
                """
                                 A towering figure of cracked iron leans against the runed wall.
                                 Its voice grinds like metal on stone.
                        \s
                                 "The path you walk... it does not stretch forever, wanderer.
                                 All roads of ash and ember narrow toward one place."
                        \s
                                 Its single ember-eye flickers weakly.
                        \s
                                 "The Crucible waits.\s
                                 The First Flame sleeps inside it, but not peacefully.
                                 What you carry… what you *will* carry… will decide whether it wakes or dies."
                        \s
                                 The figure lowers its head, as if already mourning.
                        \s
                                 "Go when you are ready.\s
                                 But know this—no one returns unchanged from the Crucible."
                                \s"""
        );
        ironSpire.addNPC(Pyrrak);


        hallOfEmbers.addItem(new Item("torch", "A weakly burning torch — the last flame of Prometheus."));

        chamberOfEchoes.addItem(new Item("Gem", "The trapped gold veins evoke the image of caged fire."));
        this.note = new Item("note",
                "The riddle speaks of something bound to stone,\n" +
                        "yet always pulling upward against its fate.\n" +
                        "It holds things together, and it is held down itself.\n\n" +
                        "Whisper its name to the chamber if you seek the path onward."
        );
        chamberOfEchoes.addItem(this.note);
        crucible.addItem(new Item("Flame Core", "A burning orb of blue fire. Its warmth feels... wrong."));
        registerRooms();

        NPC Acheron = new NPC("Acheron", vaultOfChains,
                "Acheron follows your every move, silent, patient, waiting." +
                        "“Say the name of the fire-thief, the one punished by the gods.\n" +
                        "Only then will my chains break.”"
        );
        vaultOfChains.addNPC(Acheron);

        NPC Sylpha = new NPC(
                "Sylpha",
                ashenGarden,
                """
                        A faint figure forms among the ashen trees — neither alive nor dead.
                        Her voice is soft, carried by the smoldering wind.
                        
                        "You carry embers of purpose, wanderer… but embers alone are not enough."
                        
                        Her outline flickers like smoke.
                        
                        "The path you seek ends in the Crucible. There, truth is stripped bare,
                        and only one answer can wake the First Flame."
                        
                        A pause… almost sorrow.
                        
                        "Be warned. Those who speak falsely are not merely wrong — 
                        the Crucible judges intent, and its judgment is… absolute."
                        
                        She fades, ember by ember.
                        
                        "Go with caution, Child of Ash. The Flame remembers everything."
                        """
        );
        ashenGarden.addNPC(Sylpha);


    }


    private final Map<String, Room> roomMap = new HashMap<>();

    private void registerRooms() {
        roomMap.put("embers", hallOfEmbers);
        roomMap.put("echoes", chamberOfEchoes);
        roomMap.put("iron spire", ironSpire);
        roomMap.put("iron", ironSpire);
        roomMap.put("ironspire", ironSpire);
        roomMap.put("spire", ironSpire);
        roomMap.put("crucible", crucible);
        roomMap.put("chains", vaultOfChains);
    }


    public void play() {
        printWelcome();

        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        print("Thank you for playing. Goodbye.");
    }

    private void printWelcome() {
        print("");
        print("Welcome to the game!");
        print("Type 'help' if you need help.");
        print("");
        print(player.getCurrentRoom().getLongDescription());
    }

    boolean processCommand(Command command) {
        switch (command.getCommandWord()) {
            case HELP:
                printHelp();
                break;
            case GO:
                goRoom(command);
                break;
            case TAKE:
                takeItem(command);
                break;
            case DROP:
                dropItem(command);
                break;
            case TELEPORT:
                teleport(command);
                break;
            case INVENTORY:
                print(player.getInventoryString());
                break;
            case READ:
                readItem(command);
                break;
            case SAY:
                sayWord(command);
                break;
            case TALK:
                talkToNPC(command);
                break;
            case SAVE:
                saveGame();
                break;
            case LOAD:
                loadGameIfExists();
                break;
            case GIVE:
                giveItem(command);
                break;
            case USE:
                useItem(command);
                break;
            case INVBTN:
                getInventory();
                break;
            case ATTACK:
                getAttack();
                break;

            case QUIT:
                if (command.hasSecondWord()) {
                    print("Quit what?");
                    return false;
                }
                return true;

            case UNKNOWN:
            default:
                print("I don't know what you mean...");
                break;
        }
        return false;
    }

    private void printHelp() {
        print("You are lost. You are alone. The flame is burning.");
        print("Your command words are: ");
        parser.showCommands();
    }

    public void getAttack() {
        print("You are alone. You are alone. You are alone.");
    }

    public String getPlayerInventory() {
        return player.getInventoryString();
    }

    private void giveItem(Command command) {
        if (!command.hasSecondWord()) {
            print("Give what?");
            return;
        }
        if (!command.hasThirdWord()) {
            print("Give it to who?");
            return;
        }

        String itemName = command.getSecondWord();
        String npcName = command.getThirdWord();

        Item item = player.getItem(itemName);

        if (item == null) {
            print("You don't have " + itemName + "!");
            return;
        }

        NPC target = null;
        for (NPC npc : player.getCurrentRoom().getNPCs()) {
            if (npc.getName().equalsIgnoreCase(npcName)) {
                target = npc;
                break;
            }
        }

        if (target == null) {
            print("There is no " + npcName + " here.");
            return;
        }

        if (npcName.equalsIgnoreCase("Orpheon") && itemName.equalsIgnoreCase("Gem")) {

            print(
                    """
                            Orpheon takes the Gem into his palm.
                            "Ah… the Echoing Core," he whispers.
                            "You have done well, Child of Ash. Take this — the Ember Fragment.\s
                            With it, the old flame remembers you."
                            """
            );

            player.removeItem(item);

            Item emberFragment = new Item("Ember Fragment",
                    "A glowing shard of primordial fire. Warm, alive, and waiting.");
            player.addItem(emberFragment);

            return;
        }
        print(npcName + " does not want the " + itemName + ".");
    }


    public void getInventory() {
        print(player.getInventoryString());
    }

    @CommandDescription("Moves the player to a different room.")
    private void goRoom(Command command) {
        if (!command.hasSecondWord()) {
            print("Go where?");
            return;
        }

        String direction = command.getSecondWord();
        Room nextRoom = player.getCurrentRoom().getExit(direction);

        if (nextRoom == null) {
            print("No door here!");
            return;
        }

        // BLOCK NORTH IN HALL OF EMBERS
        if (player.getCurrentRoom() == hallOfEmbers && direction.equalsIgnoreCase("north") && !hallOfEmbers.isAltarIgnited()) {

            print("A wall of cold ash blocks your path. The Ember Altar is dormant.");
            return;
        }
        if (nextRoom == crucible) {

            Item fragment = player.getItem("Ember Fragment");
            Item stone = player.getItem("Etched Stone");

            if (fragment == null || stone == null) {
                print(
                        "A barrier of burning air blocks your path.\n" +
                                "Symbols flash across the obsidian gate.\n" +
                                "It seems to respond only to those who carry BOTH:\n" +
                                " - The Ember Fragment\n" +


                                " - The Etched Stone"
                );
                return;
            }
        }


       player.setCurrentRoom(nextRoom);
     //   print(player.getCurrentRoom().getLongDescription());
    }

    public String getCurrentRoomDescription() {
        return player.getCurrentRoom().getLongDescription();
    }

    private void takeItem(Command command) {
        if (!command.hasSecondWord()) {
            print("Take what?");
            return;
        }

        String itemName = command.getSecondWord();
        Item item = player.getCurrentRoom().getItem(itemName);

        if (item == null) {
            print("There is no " + itemName + " here!");
        } else if (!item.isVisible()) {
            print("You can’t see that item right now!");
        } else {
            player.addItem(item);
            player.getCurrentRoom().removeItem(itemName);
            item.setLocation("inventory");
            print("You picked up the " + item.getName() + ".");
        }
    }

    private void talkToNPC(Command command) {
        if (!command.hasSecondWord()) {
            print("Talk to who?");
            if (outputArea != null) outputArea.appendText("Talk to who?\n");
            return;
        }

        String npcName = command.getSecondWord();
        Room currentRoom = player.getCurrentRoom();

        for (NPC npc : currentRoom.getNPCs()) {
            if (npc.getName().equalsIgnoreCase(npcName)) {
                String dialogue = npc.speak();   // returns String (console-safe)

                // print to console
                print(dialogue);
                return;
            }
        }

        String msg = "There’s no one named " + npcName + " here.";

        print(msg);
    }


    private boolean echoPuzzleSolved = false;

    private void sayWord(Command command) {
        if (!command.hasSecondWord()) {
            print("Say what?");
            return;
        }

        String spoken = command.getSecondWord().toLowerCase();


        if (spoken.equals("admin")) {
            print("Prometheus loves cheaters.");
            player.addItem(new Item("Gem", "Admin generated"));
            player.addItem(new Item("Ember Fragment", "Admin generated"));
            player.addItem(new Item("Etched Stone", "Admin generated"));
            player.addItem(new Item("Primordial Flame", "Admin generated"));


            hallOfEmbers.setExit("north", chamberOfEchoes);
            chamberOfEchoes.setExit("east", ironSpire);
            ironSpire.setExit("south", ashenGarden);
            ashenGarden.setExit("east", vaultOfChains);
            vaultOfChains.setExit("south", crucible);
            crucible.setExit("north", vaultOfChains);

            hallOfEmbers.igniteAltar();
            chainsPuzzleSolved = true;
            echoPuzzleSolved = true;

            return;
        }

        // First Riddle w/ gem
        if (player.getCurrentRoom() == chamberOfEchoes && !echoPuzzleSolved) {
            if (spoken.equals("chain")) {
                echoPuzzleSolved = true;

                print(
                        "Your voice rebounds endlessly...\n" +
                                "The chamber trembles.\n" +
                                "A hidden archway reveals itself to the east!"
                );

                chamberOfEchoes.setExit("east", ironSpire);
                return;
            } else {
                print("The walls repeat your words... but nothing happens.");
                return;
            }
        }
        // VAULT OF CHAINS PUZZLE
        if (player.getCurrentRoom() == vaultOfChains && !chainsPuzzleSolved) {
            if (spoken.equalsIgnoreCase("prometheus")) {

                chainsPuzzleSolved = true;

                print(
                        "The chains clatter violently...\n" +
                                "A hidden compartment opens in the wall.\n" +
                                "Inside lies an ancient stone tablet."
                );

                Item etched = new Item("Etched Stone",
                        "A tablet depicting Prometheus bound to the rock, holding stolen fire." +
                                "You sense it may be important...");
                player.addItem(etched);

                print("You obtain: Etched Stone.");

            } else {
                print("Your voice echoes off the iron walls, but nothing happens.");
            }
            return;
        }


        // SENTINEL FINAL RIDDLE
        if (player.getCurrentRoom() == crucible && !finalRiddleSolved) {

            if (spoken.equals("fire")) {

                finalRiddleSolved = true;

                print(
                        "The Sentinel bows its head.\n" +
                                "\"You speak the truth. Fire was the first gift…\"\n" +
                                "It extends its hand, revealing a blazing orb.\n" +
                                "You receive: Primordial Flame."
                );

                Item flame = new Item("Primordial Flame",
                        "The First Fire — blazing with impossible brilliance.");
                player.addItem(flame);

                print(
                        "As you grasp the Primordial Flame, the world ignites in gold.\n" +
                                "Ash lifts from the ground. Stone cracks and blooms.\n" +
                                "The ancient halls roar back to life.\n\n" +
                                "You have restored the Primordial Flame.\n" +
                                "The age of ash is over.\n\n" +
                                "*** YOU WIN ***"
                );

                if (onGameWin != null) onGameWin.run();
                scheduleExit();
                return;
            }

            // WRONG ANSWER
            sentinelFailCount++;

            if (sentinelFailCount == 1) {
                print("The Sentinel's eyes narrow. \"Think carefully, wanderer.\"");
                return;
            }

            if (sentinelFailCount == 2) {
                print("The Sentinel's flame flickers violently. \"Your ignorance grows… dangerous.\"");
                return;
            }

            if (sentinelFailCount >= 3) {
                print(
                        "The Crucible falls silent.\n" +
                                "The Sentinel steps forward.\n" +
                                "\"Enough.\"\n\n" +
                                "A blade of molten obsidian cleaves the air.\n" +
                                "Heat and shadow swallow you whole.\n\n" +
                                "*** YOU HAVE LOST ***"
                );
                if (onGameLose != null) onGameLose.run();
                scheduleExit();
                return;

            }
        }
    }
    private void print(String text) {
        System.out.println(text);
        if (outputArea != null) {
            Platform.runLater(() -> outputArea.appendText(text + "\n"));
        }
    }

    private Runnable onGameLose;

    public void setOnGameLose(Runnable r) {
        this.onGameLose = r;
    }


    private void scheduleExit() {
        javafx.application.Platform.runLater(() -> {
            javafx.animation.PauseTransition delay =  new javafx.animation.PauseTransition(javafx.util.Duration.seconds(5)); // wait 5s for win.mp3 to play
            delay.setOnFinished(event -> javafx.application.Platform.exit());
            delay.play();
        });
    }


    private void dropItem(Command command) {
        if (!command.hasSecondWord()) {
            print("Drop what?");
            return;
        }
        String itemName = command.getSecondWord();
        Item item = player.getItem(itemName);

        if (item == null) {
            print("You don't have " + itemName + "!");
            return;
        }

        player.removeItem(item);
        player.getCurrentRoom().addItem(item);
        item.setLocation(player.getCurrentRoom().getDescription());
        print("You dropped the " + item.getName() + ".");
    }

    private void readItem(Command command) {
        if (!command.hasSecondWord()) {
            print("Read what?");
            return;
        }

        String itemName = command.getSecondWord();
        Item item = player.getItem(itemName);

        if (item == null) {
            item = player.getCurrentRoom().getItem(itemName);
        }

        if (item == null) {
            print("There is no " + itemName + " here to read.");
            return;
        }

        if (itemName.equalsIgnoreCase("note")) {
            print(
                    "The note unfurls by itself...\n" +
                            "A whisper crawls across the chamber:\n\n" +
                            "\"That which is bound to stone, yet yearns forever skyward...\"\n" +
                            "\"Speak its name to the hollow air, and the path will open.\""
            );
        } else {
            print("There's nothing written on that.");
        }
    }

    public void saveGame() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {

            GameState gs = new GameState();
            gs.player = this.player;
            gs.chainsPuzzleSolved = this.chainsPuzzleSolved;
            gs.echoPuzzleSolved = this.echoPuzzleSolved;
            gs.finalRiddleSolved = this.finalRiddleSolved;
            gs.sentinelFailCount = this.sentinelFailCount;

            gs.hallOfEmbers = this.hallOfEmbers;
            gs.chamberOfEchoes = this.chamberOfEchoes;
            gs.ashenGarden = this.ashenGarden;
            gs.ironSpire = this.ironSpire;
            gs.vaultOfChains = this.vaultOfChains;
            gs.crucible = this.crucible;

            out.writeObject(gs);
            print("Game saved.");

        } catch (IOException e) {
            print("Save failed: " + e.getMessage());
        }
    }

    public void loadGameIfExists() {
        File f = new File(SAVE_FILE);
        if (!f.exists()) {
            print("No save file found.");
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {

            GameState gs = (GameState) in.readObject();

            // Restore fields
            this.player = gs.player;
            this.chainsPuzzleSolved = gs.chainsPuzzleSolved;
            this.echoPuzzleSolved = gs.echoPuzzleSolved;
            this.finalRiddleSolved = gs.finalRiddleSolved;
            this.sentinelFailCount = gs.sentinelFailCount;

            this.hallOfEmbers = gs.hallOfEmbers;
            this.chamberOfEchoes = gs.chamberOfEchoes;
            this.ashenGarden = gs.ashenGarden;
            this.ironSpire = gs.ironSpire;
            this.vaultOfChains = gs.vaultOfChains;
            this.crucible = gs.crucible;

            // Restore room connections
            rebuildRoomConnections();

            // Restore puzzle rewards
            if (chainsPuzzleSolved) {
                if (player.getItem("Etched Stone") == null) {
                    player.addItem(new Item("Etched Stone",
                            "A tablet depicting Prometheus bound to the rock, holding stolen fire."));
                    print("Etched Stone restored.");
                }
            }

            if (echoPuzzleSolved) {
                // If needed, restore Ember Fragment here
            }

            print("Game loaded.");
            print(player.getCurrentRoom().getLongDescription());

        } catch (Exception e) {
            print("Load failed: " + e.getMessage());
        }
    }


    private void teleport(Command command) {
        if (!command.hasSecondWord()) {
            print("Teleport where?");
            return;
        }

        String roomName = command.getSecondWord().toLowerCase();

        Room target = roomMap.get(roomName);

        if (target == null) {
            print("No such room: " + roomName);
            return;
        }

        player.setCurrentRoom(target);
        print("You teleport to:");
        print(target.getLongDescription());
    }

    private void useItem(Command command) {
        if (!command.hasSecondWord()) {
            print("Use what?");
            return;
        }

        String itemName = command.getSecondWord().toLowerCase();
        Item item = player.getItem(itemName);

        if (item == null) {
            print("You don't have a " + itemName + ".");
            return;
        }

        // Torch puzzle
        if (itemName.equals("torch") && player.getCurrentRoom() == hallOfEmbers) {
            hallOfEmbers.igniteAltar();
            print("The torch flares brightly as you ignite the Ember Altar!");
            print("The northern gate rumbles open...");
            player.removeItem(item);
            return;
        }

        print("You can’t use that here.");
    }


    private void rebuildRoomConnections() {
        hallOfEmbers.setExit("north", chamberOfEchoes);
        chamberOfEchoes.setExit("south", hallOfEmbers);
        ironSpire.setExit("west", chamberOfEchoes);
        ashenGarden.setExit("north", ironSpire);
        ashenGarden.setExit("east", vaultOfChains);
        vaultOfChains.setExit("west", ashenGarden);
        vaultOfChains.setExit("south", crucible);
        crucible.setExit("north", vaultOfChains);
        ironSpire.setExit("south", ashenGarden);

        // if puzzle solved → also restore extra exits
        if (echoPuzzleSolved)
            chamberOfEchoes.setExit("east", ironSpire);

    }


    public String getNPCinCurrentRoom() {
        if (player.getCurrentRoom().getNPCs().isEmpty())
            return null;
        return player.getCurrentRoom().getNPCs().getFirst().getName();
    }


    public static class GameState implements Serializable {
        public Character player;
        public boolean chainsPuzzleSolved;
        public boolean echoPuzzleSolved;
        public boolean finalRiddleSolved;
        public int sentinelFailCount;
        public Room hallOfEmbers;
        public Room chamberOfEchoes;
        public Room ashenGarden;
        public Room ironSpire;
        public Room vaultOfChains;
        public Room crucible;
    }

    private Runnable onGameWin;
    public void setOnGameWin(Runnable r) {
        this.onGameWin = r;
    }

    public boolean roomHasItems() {
        return player.getCurrentRoom().hasItems();
    }

    public boolean playerHasItems() {
        return !player.getInventory().isEmpty();
    }




    public static void main(String[] args) {
        ZorkGUI.launch(ZorkGUI.class, args);
    }

} // end of class