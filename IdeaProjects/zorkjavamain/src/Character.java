import java.io.*;

public class Character implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private Room currentRoom;
    private Inventory<Item> inventory = new Inventory<>();

    public Character(String name, Room startingRoom) {
        this.name = name;
        this.currentRoom = startingRoom;
    }

    public Character() {}

    public String getName() { return name; }

    public Room getCurrentRoom() { return currentRoom; }

    public void setCurrentRoom(Room room) { this.currentRoom = room; }

    public void move(String direction) {
        Room nextRoom = currentRoom.getExit(direction);
        if (nextRoom != null) {
            currentRoom = nextRoom;
            System.out.println("You moved to: " + currentRoom.getDescription());
        } else {
            System.out.println("You can't go that way!");
        }
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public void removeItem(Item item) {
        inventory.remove(item);
    }

    public Item getItem(String itemName) {
        return inventory.get(itemName);
    }

    public String getInventoryString() {
        return inventory.toString();
    }
}
