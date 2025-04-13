package Common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ItemDatabase {
    private static final String FILE_NAME = "items.dat";
    public static List<Items> items = new ArrayList<>();

    static {
        loadItems();
    }

    public static synchronized boolean subscribeUser(String itemName, UserInfo user) {
        Items item = getItemByName(itemName);

        if (item == null) {
            return false;
        }
        if (!item.getSubscriber().contains(user)) {
            item.addSubscriber(user);
            saveItems();
        }

        return true;

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

    public static void loadItems() {
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

    public static boolean itemExist(String name) {
        for (Items item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static void notifyBid(String item, String bidderName) throws IOException {
        System.out.println(" Inside notifyBid");

        Items itemNotified = getItemByName(item);
        if (itemNotified == null) {
            System.out.println(" Item not found: " + item);
            return;
        }

        UserInfo owner = itemNotified.getOwner();
        if (owner == null || owner.getUdpPort() == null || owner.getUdpPort().isEmpty()) {
            System.out.println("Owner info incomplete for item: " + item);
            return;
        }

        InetAddress clientAddress = InetAddress.getByName(owner.getIpAddress());
        int ownerPort = Integer.parseInt(owner.getUdpPort());
        String ownerMessageText = bidderName + " just bid on your item: " + item;
        byte[] ownerMessage = ownerMessageText.getBytes();

        DatagramSocket socket = new DatagramSocket();

        DatagramPacket ownerPacket = new DatagramPacket(
                ownerMessage, ownerMessage.length, clientAddress, ownerPort);
        socket.send(ownerPacket);
        System.out.println("Sent bid notification to owner at " + clientAddress + ":" + ownerPort);

        // Notify subscribers
        for (UserInfo user : itemNotified.getSubscriber()) {
            String messageText = bidderName + " placed a bid on " + item;
            byte[] message = messageText.getBytes();
            try {
                InetAddress subAddr = InetAddress.getByName(user.getIpAddress());
                int subPort = Integer.parseInt(user.getUdpPort());
                DatagramPacket packet = new DatagramPacket(message, message.length, subAddr, subPort);
                socket.send(packet);
                System.out.println(
                        "Sent bid notification to subscriber " + user.getName() + " at " + subAddr + ":" + subPort);
            } catch (Exception e) {
                System.err.println(" Failed to notify subscriber: " + user.getName());
                e.printStackTrace();
            }
        }

        socket.close();
    }

    public static void removeItem(String name) {
        items.remove(getItemByName(name));
    }

}
