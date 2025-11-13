/* This game is a classic text-based adventure set in a university environment.
   The player starts outside the main entrance and can navigate through different rooms like a 
   lecture theatre, campus pub, computing lab, and admin office using simple text commands (e.g., "go east", "go west").
    The game provides descriptions of each location and lists possible exits.

Key features include:
Room navigation: Moving among interconnected rooms with named exits.
Simple command parser: Recognizes a limited set of commands like "go", "help", and "quit".
Player character: Tracks current location and handles moving between rooms.
Text descriptions: Provides immersive text output describing the player's surroundings and available options.
Help system: Lists valid commands to guide the player.
Overall, it recreates the classic Zork interactive fiction experience with a university-themed setting, 
emphasizing exploration and simple command-driven gameplay
*/

import javafx.scene.control.TextArea;

public class ZorkULGame {
    private Parser parser;
    private Character player;
    private TextArea outputArea;

    public ZorkULGame() {
        createRooms();
        parser = new Parser();
    }
    public ZorkULGame(TextArea outputArea) { // GUI version
        this.outputArea = outputArea;
        createRooms();
        parser = new Parser();
    }




    private void createRooms() {
        Room hallOfEmbers, chamberOfEchoes, ironSpire, ashenGarden, crucible, vaultOfChains, keepersQuarters;

        hallOfEmbers = new Room("You awaken in the Hall of Embers.\n" +
                "The walls flicker faintly with dying torches. Charred murals of gods and flame line the stone.\n" +
                "The air is heavy with the scent of ash — and the faint echo of fire long forgotten.");

        chamberOfEchoes = new Room("You enter the Chamber of Echoes.\n" +
                "The sound of your footsteps repeats endlessly. Whispered voices murmur truths and lies alike.\n" +
                "You feel as though the walls themselves are listening.");

        ironSpire = new Room("You stand before the Iron Spire.\n" +
                "An obsidian tower looms above, its surface etched with glowing runes.\n" +
                "The air hums faintly — power dormant, waiting to be awakened.");

        ashenGarden = new Room("You step into the Ashen Garden.\n" +
                "What once were trees are now blackened silhouettes. Their roots still pulse faintly with red embers beneath the soil.\n" +
                "Something ancient sleeps here, beneath the ash.");

        crucible = new Room("You arrive in the Crucible.\n" +
                "A massive forge dominates the center. Blue fire burns without heat — whispering in a forgotten tongue.\n" +
                "This place feels like both a tomb and a beginning.");

        vaultOfChains = new Room("You descend into the Vault of Chains.\n" +
                "Rusting shackles hang from the ceiling. The scent of iron and smoke fills your lungs.\n" +
                "On the walls are carvings of a man bound to stone — the punishment of Prometheus.");

        keepersQuarters = new Room("You find a dimly lit chamber — the Keeper’s Quarters.\n" +
                "Ash and soot cover everything. A broom leans against the wall beside a pile of extinguished torches.\n" +
                "This must be where the Keeper of Ashes once rested.");



        hallOfEmbers.setExit("north", chamberOfEchoes);
        chamberOfEchoes.setExit("south", hallOfEmbers);
        chamberOfEchoes.setExit("east", ironSpire);
        ironSpire.setExit("west", chamberOfEchoes);
        ironSpire.setExit("south", ashenGarden);
        ashenGarden.setExit("north", ironSpire);
        ashenGarden.setExit("east", vaultOfChains);
        vaultOfChains.setExit("west", ashenGarden);
        vaultOfChains.setExit("south", keepersQuarters);
        keepersQuarters.setExit("north", vaultOfChains);
        keepersQuarters.setExit("east", crucible);
        crucible.setExit("west", keepersQuarters);


        player = new Character("player", hallOfEmbers);

        NPC keeper = new NPC("Keeper", keepersQuarters,
                "He gave them the fire... and they burned the world with it.\n" +
                        "Now I sweep the ashes, so the gods don’t see what remains.");
        keepersQuarters.addNPC(keeper);


        NPC Leonidus = new NPC("Leonidus", hallOfEmbers,"Tutorial test");
        hallOfEmbers.addNPC(Leonidus);

        hallOfEmbers.addItem(new Item("note", "An old, crumbling piece of parchment."));
        hallOfEmbers.addItem(new Item("Torch", "A weakly burning torch — the last flame of Prometheus."));
        chamberOfEchoes.addItem(new Item("Echo Crystal", "A shimmering crystal that hums when you speak near it."));
        ironSpire.addItem(new Item("Heart of Fire", "A glowing ember pulsing faintly. It hums with restrained power."));
        ashenGarden.addItem(new Item("Tear of Gaia", "A small, hardened drop of glowing sap. It feels alive."));
        vaultOfChains.addItem(new Item("Broken Chain", "A piece of divine metal — once used to bind a god."));
        keepersQuarters.addItem(new Item("Journal", "A scorched book written by the Keeper. Some pages are still legible."));
        crucible.addItem(new Item("Flame Core", "A burning sphere of blue fire. Its warmth feels... wrong."));
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
        String commandWord = command.getCommandWord();

        if (commandWord == null) {
            System.out.println("I don't understand your command...");
            return false;
        }

        switch (commandWord) {
            case "help":
                printHelp();
                break;
            case "go":
                goRoom(command);
                break;
            case "take":
                takeItem(command);
                break;
            case "drop":
                dropItem(command);
                break;
            case "inventory":
                System.out.println(player.getInventoryString());
                break;
            case "read":
                readItem(command);
                break;
            case "talk":
                talkToNPC(command);
                break;
            case "quit":
                if (command.hasSecondWord()) {
                    System.out.println("Quit what?");
                    return false;
                } else {
                    return true; // signal to quit
                }
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

    public String getPlayerInventory() {
        return player.getInventoryString();
    }


    private void goRoom(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        Room nextRoom = player.getCurrentRoom().getExit(direction);

        if (nextRoom == null) {
            print("No door here!");
        }
        if (nextRoom.getDescription().contains("basement") && player.getItem("key") == null) {
            System.out.println("The basement door is locked tight. You need the old key.");
            return;
        }
        if(player.getItem("vial") != null ){
            System.out.println("The vial shakes uncontrollably...");
            return;
        }

        else {
            player.setCurrentRoom(nextRoom);
            System.out.println(player.getCurrentRoom().getLongDescription());
        }
    }

    public static void main(String[] args) {
        ZorkGUI.launch(ZorkGUI.class, args);
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
                npc.speak(outputArea);
                return;
            }
        }

        System.out.println("There’s no one named " + npcName + " here.");
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
        } else {
            player.removeItem(item);
            player.getCurrentRoom().addItem(item);
            item.setLocation(player.getCurrentRoom().getDescription());
            System.out.println("You dropped the " + item.getName() + ".");
        }
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

        switch (itemName.toLowerCase()) {
            case "note":
                System.out.println("test");
                break;

            case "diary":
                System.out.println("The diary pages are torn and smudged with ink...\n\"Day 67: The machine in the lab won’t stop humming. We sealed the lower gate, but it whispers still.\"");
                break;

            case "document":
                System.out.println("The document bears the seal of Edenhelm:\n\"Effective immediately, all public references to the Helios Study are forbidden.\"");
                break;

            default:
                print("There's nothing written on that.");
                break;
        }
    }

    private void print(String message) {
        if (outputArea != null) {
            outputArea.appendText(message + "\n");
        } else {
            System.out.println(message);
        }
    }
} // end of class
