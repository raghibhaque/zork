import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class Room implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    private String description;
    private Map<String, Room> exits; // Map direction to neighboring Room
    private Map<String, Item> items;
    private boolean altarIgnited = false;
    public boolean isAltarIgnited() {
        return altarIgnited;
    }
    public void igniteAltar() {
        this.altarIgnited = true;
    }

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
        return description + "\n"
                + getItemString() + "\n"
                + getNPCString() + "\n"
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

    private ArrayList<NPC> npcs = new ArrayList<>();

    public void addNPC(NPC npc) {
        npcs.add(npc);
    }

    public void removeNPC(NPC npc) {
        npcs.remove(npc);
    }

    public ArrayList<NPC> getNPCs() {
        return npcs;
    }

    public String getNPCString() {
        if (npcs.isEmpty()) {
            return "No one else is here.";
        }
        StringBuilder sb = new StringBuilder("You see: ");
        for (NPC npc : npcs) {
            sb.append(npc.getName()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
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
    public boolean hasItems() {
        return !items.isEmpty();
    }

}
