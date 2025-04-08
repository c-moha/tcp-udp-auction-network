package Common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ItemDatabase {
    private static final String FILE_NAME = "items.dat";
    private static List<Items> items = new ArrayList<>();

    static {
        loadItems();
    }

    public static synchronized boolean addItem(Items item) {
        items.add(item);
        saveItems();
        return true;
    }

    public static synchronized List<Items> getItems() {
        return new ArrayList<>(items);
    }

    public static void saveItems() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(items);
        } catch (IOException e) {
            System.out.println("Failed to save items.");
            e.printStackTrace();
        }
    }

    private static void loadItems() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            items = (List<Items>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            items = new ArrayList<>();
        }
    }

    public static List<Items> getAllItems() {
        return new ArrayList<>(items); // return a copy to prevent external modification
    }

    public static Items getItemByName(String name) {
        for (Items item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }
}
