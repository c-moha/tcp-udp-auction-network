package Common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class DataUsers {
    private static final String FILE_NAME = "clients.dat";
    private static HashMap<String, UserInfo> users = new HashMap<>();

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
    }

    // Load the HashMap from a file
    private static void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            users = (HashMap<String, UserInfo>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            users = new HashMap<>(); // Start fresh if file doesn't exist
        }
    }
}
