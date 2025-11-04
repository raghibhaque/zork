import java.util.HashMap;
import java.util.Map;

public class Room {
    private String description;
    private Map<String, Room> exits; // Map direction to neighboring Room
    private Map<String, Item> items;

    public Room(String description) {
        this.description = description;
        exits = new HashMap<>();
        items = new HashMap<>();
    }

    public String getDescription() {
        return description;
    }

    public void setExit(String direction, Room neighbor) {
        exits.put(direction, neighbor);
    }

    public Room getExit(String direction) {
        return exits.get(direction);
    }

    public String getExitString() {
        StringBuilder sb = new StringBuilder();
        for (String direction : exits.keySet()) {
            sb.append(direction).append(" ");
        }
        return sb.toString().trim();
    }

    public String getLongDescription() {
        return "You are " + description + ".\n"
                + getItemString() + "\n"
                + "Exits: " + getExitString();
    }

    public void addItem(Item item) {
        items.put(item.getName().toLowerCase(), item);
    }

    public Item removeItem(String itemName) {
        return items.remove(itemName.toLowerCase());
    }

    public Item getItem(String itemName) {
        return items.get(itemName.toLowerCase());
    }

    public String getItemString() {
        if (items.isEmpty()) {
            return "No items in this room.";
        }
        StringBuilder sb = new StringBuilder("Items in this room: ");
        for (String itemName : items.keySet()) {
            sb.append(itemName).append(", ");
        }
        // remove last comma + space
        return sb.substring(0, sb.length() - 2);
    }
}
