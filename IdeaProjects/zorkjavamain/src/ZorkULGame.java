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
    private Parser parser;
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
                        but the Ember Altar has grown cold.
                        Without its flame, the northern gate will never open.
                        
                        Take the Torch , the last spark still loyal to mankind.
                        Ignite the altar. Only then will the path ahead answer you."
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
                "Pick the gem up. The old riddleman used to describe the stolen gift as 'That which is bound to stone, \n" +
                        " yet yearns forever skyward.'\n" +
                        " If you wish the way to open, speak the thing he named to the empty air "
        );
        chamberOfEchoes.addItem(this.note);
        crucible.addItem(new Item("Flame Core", "A burning orb of blue fire. Its warmth feels... wrong."));
        registerRooms();

        NPC Acheron = new NPC ("Acheron", vaultOfChains,
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




    private Map<String, Room> roomMap = new HashMap<>();

    private void registerRooms() {
        roomMap.put("embers", hallOfEmbers);
        roomMap.put("echoes", chamberOfEchoes);
        roomMap.put("iron spire", ironSpire);
        roomMap.put("iron", ironSpire);
        roomMap.put("ironspire", ironSpire);
        roomMap.put("spire", ironSpire);
        roomMap.put("crucible", crucible);
        roomMap.put("chains" , vaultOfChains );
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
                break;
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

            Item emberFragment = new Item("Ember Fragment",
                    "A glowing shard of primordial fire. Warm, alive, and waiting.");
            player.addItem(emberFragment);

            return;
        }
        System.out.println(npcName + " does not want the " + itemName + ".");
    }



    public void getInventory() {
        System.out.println(player.getInventoryString());
    }

    @CommandDescription("Moves the player to a different room.")
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
        if (nextRoom == crucible) {
            Item stone = player.getItem("Ember Fragment");
            if (stone == null) {
                System.out.println(
                        "A barrier of burning air blocks your path.\n" +
                                "Symbols flash across the obsidian gate.\n" +
                                "You sense it requires the Ember Fragment to pass."
                );
                return;
            }
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
                System.out.println(npc.speak());
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
        // VAULT OF CHAINS PUZZLE
        if (player.getCurrentRoom() == vaultOfChains && !chainsPuzzleSolved) {
            if (spoken.equalsIgnoreCase("prometheus")) {

                chainsPuzzleSolved = true;

                System.out.println(
                        "The chains clatter violently...\n" +
                                "A hidden compartment opens in the wall.\n" +
                                "Inside lies an ancient stone tablet."
                );

                Item etched = new Item("Etched Stone",
                        "A tablet depicting Prometheus bound to the rock, holding stolen fire." +
                                "You sense it may be important...");
                player.addItem(etched);

                System.out.println("You obtain: Etched Stone.");
                return;

            } else {
                System.out.println("Your voice echoes off the iron walls, but nothing happens.");
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

                javafx.application.Platform.exit();
                return;
            }

            // WRONG ANSWER
            sentinelFailCount++;

            if (sentinelFailCount == 1) {
                System.out.println("The Sentinel's eyes narrow. \"Think carefully, wanderer.\"");
                return;
            }

            if (sentinelFailCount == 2) {
                System.out.println("The Sentinel's flame flickers violently. \"Your ignorance grows… dangerous.\"");
                return;
            }

            if (sentinelFailCount >= 3) {
                System.out.println(
                        "The Crucible falls silent.\n" +
                                "The Sentinel steps forward.\n" +
                                "\"Enough.\"\n\n" +
                                "A blade of molten obsidian cleaves the air.\n" +
                                "Heat and shadow swallow you whole.\n\n" +
                                "*** YOU HAVE LOST ***"
                );

                javafx.application.Platform.exit();
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

        if (itemName.toLowerCase().equals("note")) {
            System.out.println(
                    "The note unfurls by itself...\n" +
                            "A whisper crawls across the chamber:\n\n" +
                            "\"That which is bound to stone, yet yearns forever skyward...\"\n" +
                            "\"Speak its name to the hollow air, and the path will open.\""
            );
        } else {
            System.out.println("There's nothing written on that.");
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

        Item item = player.getItem(itemName);
        if (item == null) {
            System.out.println("You don't have a " + itemName + ".");
            return;
        }


        if (itemName.equals("torch") && player.getCurrentRoom() == hallOfEmbers) {
            hallOfEmbers.igniteAltar();
            System.out.println("The torch flares brightly as you ignite the Ember Altar!");
            System.out.println("The northern gate rumbles open...");
            return;
        }
        System.out.println("You can’t use that here.");
    }
    public String getNPCinCurrentRoom() {
        if (player.getCurrentRoom().getNPCs().isEmpty())
            return null;
        return player.getCurrentRoom().getNPCs().getFirst().getName();
    }

    public static void main(String[] args) {
        ZorkGUI.launch(ZorkGUI.class, args);
    }

} // end of class