package Common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class DataUsers {
    public static final String FILE_NAME = "clients.dat";
    public static final String BUYER_FILE = "buyer.dat";

    //
    private static HashMap<String, UserInfo> users = new HashMap<>();
    public static HashMap<String, UserInfo> buyers = new HashMap<>();

    // Static region which will load user the moment one instance of this type is
    // created
    static {
        loadUsers();
    }

    // Register a new user
    public static boolean registerUser(UserInfo user) {

        // condition that happens when someone already has that userName
        if (users.containsKey(user.getName())) {
            return false;
        }

        if (user.getRole().toUpperCase().equals("BUYER")) {
            System.out.println("Debug: Registering a buyer");
            buyers.put(user.getName(), user);
        }
        users.put(user.getName(), user);
        // Persistence here by saving the user
        saveUsers();
        return true;
    }

    // Deregister:
    public static boolean deregisterUser(String username) {
        if (!users.containsKey(username)) {
            return false; // User doesn't exist
        }
        if (users.get(username).getRole().toUpperCase().equals("BUYER")) {
            buyers.remove(username);
        }
        users.remove(username); // Remove user from HashMap
        saveUsers(); // Persist updated users map to file
        return true;
    }

    // Check login credentials
    public static boolean checkCredentials(String username, String password) {
        if (!users.containsKey(username)) {
            return false;
        }
        UserInfo user = users.get(username);
        return user.getPassword().equals(password);
    }

    public static boolean userExists(String username) {
        return users.containsKey(username);
    }

    // Optionally: get user by name
    public static UserInfo getUser(String username) {
        return users.get(username);
    }

    // Optionally: print all users (for debugging)
    public static void printAllUsers() {
        for (UserInfo user : users.values()) {
            System.out.println(user);
        }
    }

    // Save the HashMap to a file using serialization
    private static void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(users);
        } catch (IOException e) {

            System.out.println(" Failed to save users.");

            e.printStackTrace();

        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BUYER_FILE))) {
            oos.writeObject(buyers);
        } catch (IOException e) {

            System.out.println(" Failed to save buyers.");

            e.printStackTrace();

        }

    }

    // Update the elements insid ethe hashmap for when i update the ports of the
    // buyers to communicate with them
    public static void updateBuyers(String key, String port) {
        if (buyers.containsKey(key)) {
            UserInfo user = buyers.get(key);
            buyers.remove(key);
            user.setUDP(port);
            buyers.put(key, user);
            System.out.println("We are in the updateBuyers and the port nimber: " + port);
        }
        // This is to update the "owner" of the items
        else {
            UserInfo seller = users.get(key);
            for (Items item : ItemDatabase.items) {
                if (item.getOwner().getName().equals(key)) {
                    item.getOwner().setUDP(port); // Directly update the object
                }
            }
            ItemDatabase.saveItems();

            System.out.println("We are in the updateBuyers for sellers and the port nimber: " + port);
        }
        saveUsers();
        loadUsers();
        ItemDatabase.saveItems();
        ItemDatabase.loadItems();
    }

    // Load the HashMap from a file
    private static void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            users = (HashMap<String, UserInfo>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            users = new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BUYER_FILE))) {
            buyers = (HashMap<String, UserInfo>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            buyers = new HashMap<>();
        }
    }

    // Notify all Buyers
    public static void notifyBuyers() throws IOException {
        loadUsers();

        for (UserInfo buyer : buyers.values()) {
            System.out.println("This buyer's name and port is: " + buyer.getName() + ", " + buyer.getUdpPort());
            DatagramSocket socket = new DatagramSocket();
            byte[] buffer = new byte[1024];
            buffer = "New item appeared".getBytes();
            InetAddress serverAddress = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress,
                    Integer.parseInt(buyer.getUdpPort()));
            socket.send(packet);
        }
    }

}
