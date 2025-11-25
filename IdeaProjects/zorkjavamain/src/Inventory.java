import java.util.ArrayList;

public class Inventory<T extends Item> {
    private ArrayList<T> items = new ArrayList<>();

    public void add(T item) {
        items.add(item);
    }

    public void remove(T item) {
        items.remove(item);
    }

    public boolean contains(String name) {
        for (T item : items) {
            if (item.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    public T get(String name) {
        for (T item : items) {
            if (item.getName().equalsIgnoreCase(name)) return item;
        }
        return null;
    }

    @Override
    public String toString() {
        if (items.isEmpty()) return "Your inventory is empty.";

        StringBuilder sb = new StringBuilder("You are carrying: ");
        for (T item : items) {
            sb.append(item.getName()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
}
