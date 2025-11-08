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

import javafx.scene.control.TextInputControl;

import java.util.ArrayList;
import java.util.Scanner;

public class ZorkULGame {
    private Parser parser;
    private Character player;

    public ZorkULGame() {
        createRooms();
        parser = new Parser();
    }


    private void createRooms() {
        Room outside, theatre, pub, lab, office, basement, cellar;

        // create rooms
        outside = new Room("Outside the main entrance of the university \n" +
                "The campus is silent. The windows above are dark... but you can’t shake the feeling that something is watching you.");
        theatre = new Room("in a lecture theatre");
        pub = new Room("In the campus pub");
        lab = new Room("In a computing lab");
        office = new Room("In the computing admin office");
        basement = new Room("In the gloomy basement");
        cellar = new Room("A pungent smell emanates deep within...");

        // initialise room exits
        outside.setExit("east", theatre);
        outside.setExit("south", lab);
        outside.setExit("west", pub);
        outside.setExit("north", basement);

        theatre.setExit("west", outside);

        pub.setExit("east", outside);
        pub.setExit("south", basement);

        lab.setExit("north", outside);
        lab.setExit("east", office);

        office.setExit("west", lab);
        office.setExit("south", pub);

        basement.setExit("north", lab);
        basement.setExit("east", cellar);

        // create the player character and start outside
        player = new Character("player", outside);
        NPC janitor = new NPC("Janitor", outside,
                "You shouldn’t be here after dark… not since the incident in the basement.");
        outside.addNPC(janitor);
        lab.addItem(new Item("laptop", "a silver MacBook"));
        pub.addItem(new Item("book", "a dusty programming manual"));

        // all items
        office.addItem(new Item("key", "a small golden key. It has the word basement etched into it..."));
        outside.addItem(new Item("note", "An old piece of papyrus."));
        lab.addItem(new Item("Vial", "a cracked rusty vial, it smells a bit funny."));
        pub.addItem(new Item("book", "a dusty programming manual"));
        basement.addItem(new Item("Diary", "A ragged diary, seems like someone put alot of their secrets here."));
        office.addItem(new Item("Document", "a very important piece of paper with Edenhelm's logo on the front"));
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
        System.out.println("Welcome to the University adventure!");
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
        System.out.println("You are lost. You are alone. You wander around the university.");
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
            System.out.println("There is no door!");
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
                npc.speak();
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
            System.out.println("Read what?");
            return;
        }
        String itemName = command.getSecondWord();
        Item item = player.getItem(itemName);

        if (item == null) {
            System.out.println("You don't have " + itemName + " to read.");
            return;
        }



        switch (itemName.toLowerCase()) {
            case "diary":
                System.out.println("The diary pages are torn and smudged with ink...");
                System.out.println("\"Day 67: The machine in the lab won’t stop humming. "
                        + "We sealed the lower gate, but it whispers still. If anyone finds this—do not stay after midnight.\"");
                break;
                case "document":
                    System.out.println("The document is stamped 'PROJECT HELIOS — Edenhelm University.'");
                    System.out.println("\"Effective immediately, all public references to the Helios Study... "
                            + "The press has begun to circulate rumors. Faculty are to deny any knowledge. "
                            + "Let us ensure its light does not draw unwanted eyes.\"");
                    break;
            case "note":
                System.out.println("To whomever enters Edenhelm — the gates will not open again until dawn. Find the truth. End it.");
                System.out.println("FIND THE KEY.");
                break;
            default:
                System.out.println("There's nothing to read in that.");
        }
    }
} // end of class
