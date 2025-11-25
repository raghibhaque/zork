import javafx.scene.control.TextArea;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ZorkULGame {
    private Parser parser;
    private Character player;
    private TextArea outputArea;
    private static final String SAVE_FILE = "savegame.ser";
    private Room hallOfEmbers;
    private Room chamberOfEchoes;
    private Item note;
    private Item puzzle1;
    private Room ironSpire;
    private boolean finalRiddleSolved = false;
    private Room crucible;

    public ZorkULGame() {
        createRooms();
        parser = new Parser();
        System.out.println("ZorkULGame started");

    }

    public ZorkULGame(TextArea outputArea) { // GUI version
        this.outputArea = outputArea;
        createRooms();
        // loadGameIfExists();
        parser = new Parser();
        printWelcome();
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
                        "You feel as though the walls themselves are listening. The shiny ruby glimmers among the generations of skulls."
        );

        ironSpire = new Room(
                """
                        You stand before the Iron Spire.
                        An obsidian tower looms above, its surface etched with glowing runes.
                        The air hums faintly — power dormant, waiting to be awakened."""
        );

        Room ashenGarden = new Room(
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

        Room vaultOfChains = new Room(
                "You descend into the Vault of Chains.\n" +
                        "Rusting shackles hang from the ceiling. The scent of iron and smoke fills your lungs.\n" +
                        "On the walls are carvings of a man bound to stone — the punishment of Prometheus."
        );

        hallOfEmbers.setExit("north", chamberOfEchoes);
        chamberOfEchoes.setExit("south", hallOfEmbers);
        ironSpire.setExit("west", chamberOfEchoes);
        ironSpire.setExit("south", ashenGarden);
        ashenGarden.setExit("north", ironSpire);
        ashenGarden.setExit("east", vaultOfChains);
        vaultOfChains.setExit("west", ashenGarden);
        vaultOfChains.setExit("south", crucible);
        crucible.setExit("north", vaultOfChains);

        player = new Character("player", hallOfEmbers);


        NPC Orpheon = new NPC("Orpheon", hallOfEmbers,
                "\"Ah… you wake at last, Child of Ash.\n" +
                        "\n" +
                        "This hall once blazed with the fire of Prometheus,\n" +
                        "but the Ember Altar has grown cold.\n" +
                        "Without its flame, the northern gate will never open.\n" +
                        "\n" +
                        "Take the Torch — the last spark still loyal to mankind —\n" +
                        "and ignite the altar. Only then will the path ahead answer you.\"\n"
        );
        hallOfEmbers.addNPC(Orpheon);
        NPC sentinel = new NPC("Sentinel", crucible,
                "A towering figure of living obsidian guards the inner flame.\n" +
                        "\"Speak the truth, wanderer. The First Fire answers only to the wise.\"7" +
                        "Defeat me with the Ember Fragment , if you wish to meet your DEMISE."
        );
        crucible.addNPC(sentinel);

        hallOfEmbers.addItem(new Item("torch", "A weakly burning torch — the last flame of Prometheus."));

        chamberOfEchoes.addItem(new Item("Gem", "The trapped gold veins evoke the image of caged fire."));
        this.note = new Item("note",
                "Pick the gem up. The old riddleman used to describe the stolen gift as 'That which is bound to stone, \n" +
                        " yet yearns forever skyward.'\n" +
                        " If you wish the way to open, speak the thing he named to the empty air "
        );
        chamberOfEchoes.addItem(this.note);
        vaultOfChains.addItem(new Item("EtchedStone",
                "Carving: 'He who stole the First Flame was bound in chains. "
                        + "Remember his gift, for fire is the key.'"));
        crucible.addItem(new Item("Flame Core", "A burning orb of blue fire. Its warmth feels... wrong."));
        registerRooms();
    }
    private Map<String, Room> roomMap = new HashMap<>();

    private void registerRooms() {
        roomMap.put("hallofembers", hallOfEmbers);
        roomMap.put("chamberofechoes", chamberOfEchoes);
        roomMap.put("ironspire", ironSpire);
        roomMap.put("crucible", crucible);
        // add all rooms here
    }


    public void play() {
        printWelcome();

        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing. Goodbye.");
    }

    private void printWelcome() {
        System.out.println();
        System.out.println("Welcome to the game!");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(player.getCurrentRoom().getLongDescription());
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
            case INVENTORY:
                System.out.println(player.getInventoryString());
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
                    System.out.println("Quit what?");
                    return false;
                }
                return true;

            case UNKNOWN:
            default:
                System.out.println("I don't know what you mean...");
                break;
        }
        return false;
    }

        private void printHelp() {
        System.out.println("You are lost. You are alone. The flame is burning.");
        System.out.print("Your command words are: ");
        parser.showCommands();
    }

    public void getAttack(){
        System.out.println("You are alone. You are alone. You are alone.");
    }
    public String getPlayerInventory() {
        return player.getInventoryString();
    }

    private void giveItem(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Give what?");
            return;
        }
        if (!command.hasThirdWord()) {
            System.out.println("Give it to who?");
            return;
        }

        String itemName = command.getSecondWord();
        String npcName = command.getThirdWord();

        Item item = player.getItem(itemName);

        if (item == null) {
            System.out.println("You don't have " + itemName + "!");
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
            System.out.println("There is no " + npcName + " here.");
            return;
        }

        if (npcName.equalsIgnoreCase("Orpheon") && itemName.equalsIgnoreCase("Gem")) {

            System.out.println(
                    """
                            Orpheon takes the Gem into his palm.
                            "Ah… the Echoing Core," he whispers.
                            "You have done well, Child of Ash. Take this — the Ember Fragment.\s
                            With it, the old flame remembers you."
                            """
            );

            player.removeItem(item);

            Item emberFragment = new Item("EmberFragment",
                    "A glowing shard of primordial fire. Warm, alive, and waiting.");
            player.addItem(emberFragment);

            return;
        }
        System.out.println(npcName + " does not want the " + itemName + ".");
    }



    public void getInventory() {
        System.out.println(player.getInventoryString());
    }

    private void goRoom(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();
        Room nextRoom = player.getCurrentRoom().getExit(direction);

        if (nextRoom == null) {
            System.out.println("No door here!");
            return;
        }

        // BLOCK NORTH IN HALL OF EMBERS
        if (player.getCurrentRoom() == hallOfEmbers && direction.equalsIgnoreCase("north") && !hallOfEmbers.isAltarIgnited()) {

            System.out.println("A wall of cold ash blocks your path. The Ember Altar is dormant.");
            return;
        }
        player.setCurrentRoom(nextRoom);
        System.out.println(player.getCurrentRoom().getLongDescription());
    }

    public String getCurrentRoomDescription() {
        return player.getCurrentRoom().getLongDescription();
    }

    private void takeItem(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Take what?");
            return;
        }

        String itemName = command.getSecondWord();
        Item item = player.getCurrentRoom().getItem(itemName);

        if (item == null) {
            System.out.println("There is no " + itemName + " here!");
        } else if (!item.isVisible()) {
            System.out.println("You can’t see that item right now!");
        } else {
            player.addItem(item);
            player.getCurrentRoom().removeItem(itemName);
            item.setLocation("inventory");
            System.out.println("You picked up the " + item.getName() + ".");
        }

        if (itemName.equals("Gem") && player.getCurrentRoom() == chamberOfEchoes) {
            System.out.println(
                    "A tarnished old note falls on the ground.\n" +
                            "It unfurls by itself, whispering:\n" +
                            "\"That which is bound to stone, yet yearns forever skyward...\n" +
                            "Speak its name to the hollow air, and the path will open.\""
            );

            note.setVisible(true);
            System.out.println(player.getCurrentRoom().getLongDescription());
        }

    }

    private void talkToNPC(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Talk to who?");
            return;
        }

        String npcName = command.getSecondWord();
        Room currentRoom = player.getCurrentRoom();

        for (NPC npc : currentRoom.getNPCs()) {
            if (npc.getName().equalsIgnoreCase(npcName)) {
                npc.speak(null);  // force console output
                return;
            }
        }

        System.out.println("There’s no one named " + npcName + " here.");
    }

    private boolean echoPuzzleSolved = false;

    private void sayWord(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Say what?");
            return;
        }

        String spoken = command.getSecondWord().toLowerCase();

        // First Riddle w/ gem
        if (player.getCurrentRoom() == chamberOfEchoes && !echoPuzzleSolved) {
            if (spoken.equals("chain")) {
                echoPuzzleSolved = true;

                System.out.println(
                        "Your voice rebounds endlessly...\n" +
                                "The chamber trembles.\n" +
                                "A hidden archway reveals itself to the east!"
                );

                chamberOfEchoes.setExit("east", ironSpire);
                return;
            } else {
                System.out.println("The walls repeat your words... but nothing happens.");
                return;
            }
        }

        // SENTINEL FINAL RIDDLE
        if (player.getCurrentRoom() == crucible && !finalRiddleSolved) {
            if (spoken.equals("fire")) {

                finalRiddleSolved = true;

                System.out.println(
                        "The Sentinel bows its head.\n" +
                                "\"You speak the truth. Fire was the first gift…\"\n" +
                                "It extends its hand, revealing a blazing orb.\n" +
                                "You receive: Primordial Flame."
                );

                Item flame = new Item("Primordial Flame",
                        "The First Fire — blazing with impossible brilliance.");
                player.addItem(flame);

                System.out.println(
                        "As you grasp the Primordial Flame, the world ignites in gold.\n" +
                                "Ash lifts from the ground. Stone cracks and blooms.\n" +
                                "The ancient halls roar back to life.\n\n" +
                                "You have restored the Primordial Flame.\n" +
                                "The age of ash is over.\n\n" +
                                "*** YOU WIN ***"
                );
                return;

            } else {
                System.out.println("The Sentinel's eyes narrow. \"That is not the truth.\"");
                return;
            }
        }
    }

    private void dropItem(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Drop what?");
            return;

        }

        String itemName = command.getSecondWord();
        Item item = player.getItem(itemName);

        if (item == null) {
            System.out.println("You don't have " + itemName + "!");
            return;
        }

        player.removeItem(item);
        player.getCurrentRoom().addItem(item);
        item.setLocation(player.getCurrentRoom().getDescription());
        System.out.println("You dropped the " + item.getName() + ".");
    }

    private void readItem(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Read what?");
            return;
        }

        String itemName = command.getSecondWord();
        Item item = player.getItem(itemName);

        if (item == null) {
            item = player.getCurrentRoom().getItem(itemName);
        }

        if (item == null) {
            System.out.println("There is no " + itemName + " here to read.");
            return;
        }

        switch (itemName.toLowerCase()) {
            case "note":
                System.out.println(
                        "The note unfurls by itself...\n" +
                                "A whisper crawls across the chamber:\n\n" +
                                "\"That which is bound to stone, yet yearns forever skyward...\"\n" +
                                "\"Speak its name to the hollow air, and the path will open.\""
                );
                break;
            default:
                System.out.println("There's nothing written on that.");
                break;
        }
    }

    public void saveGame() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            out.writeObject(player);
            System.out.println("Game saved.");
        } catch (IOException e) {
            System.out.println("Auto-save failed: " + e.getMessage());
        }
    }

    public void loadGameIfExists() {
        File f = new File(SAVE_FILE);
        if (!f.exists()) {
            System.out.println("No save file found. Starting new game.");
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            player = (Character) in.readObject();
            System.out.println("Game loaded automatically.");
            System.out.println(player.getCurrentRoom().getLongDescription());
        } catch (Exception e) {
            System.out.println("Failed to load save: " + e.getMessage());
        }
    }
    private void teleport(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Teleport where?");
            return;
        }

        String roomName = command.getSecondWord().toLowerCase();

        Room target = roomMap.get(roomName);

        if (target == null) {
            System.out.println("No such room: " + roomName);
            return;
        }

        player.setCurrentRoom(target);
        System.out.println("You teleport to:");
        System.out.println(target.getLongDescription());
    }

    private void useItem(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Use what?");
            return;
        }

        String itemName = command.getSecondWord().toLowerCase();
        if (itemName.equals("torch") && player.getCurrentRoom() == hallOfEmbers) {
            hallOfEmbers.igniteAltar();
            System.out.println("The torch flares brightly as you ignite the Ember Altar!");
            System.out.println("The northern gate rumbles open...");
            return;
        }
        System.out.println("You can’t use that here.");
    }

    public static void main(String[] args) {
        ZorkGUI.launch(ZorkGUI.class, args);
    }

} // end of class