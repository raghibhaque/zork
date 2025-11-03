import java.util.ArrayList;

public class Character {
    private String name;
    private Room currentRoom;
    private ArrayList<Item> inventory = new ArrayList<>();

    public Character(String name, Room startingRoom) {
        this.name = name;
        this.currentRoom = startingRoom;
    }

    public String getName() {
        return name;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

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
        for (Item i : inventory) {
            if (i.getName().equalsIgnoreCase(itemName)) {
                return i;
            }
        }
        return null;
    }

    public String getInventoryString() {
        if (inventory.isEmpty()) {
            return "Your inventory is empty.";
        }

        StringBuilder sb = new StringBuilder("You are carrying: ");
        for (Item i : inventory) {
            sb.append(i.getName()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
}
