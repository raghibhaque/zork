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
        Room outside, theatre, pub, lab, office, basement;

        // create rooms
        outside = new Room("outside the main entrance of the university");
        theatre = new Room("in a lecture theatre");
        pub = new Room("in the campus pub");
        lab = new Room("in a computing lab");
        office = new Room("in the computing admin office");
        basement = new Room("in the gloomy basement");

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

        basement.setExit("north", lab);
        // create the player character and start outside
        player = new Character("player", outside);
        outside.addItem(new Item("key", "a small golden key"));
        lab.addItem(new Item("laptop", "a silver MacBook"));
        pub.addItem(new Item("book", "a dusty programming manual"));

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

    private void goRoom(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        Room nextRoom = player.getCurrentRoom().getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        } else {
            player.setCurrentRoom(nextRoom);
            System.out.println(player.getCurrentRoom().getLongDescription());
        }
    }

    public static void main(String[] args) {
        ZorkULGame game = new ZorkULGame();
        ZorkGUI.launch(ZorkGUI.class, args);
        game.play();

    }

    public String getCurrentRoomDescription() {
        System.out.println("Current Room Description:");
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

}
