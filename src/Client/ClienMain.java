// ✅ Full Updated ClienMain.java with all necessary additions for Broadcast Support
package Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import Common.ItemDatabase;
import Common.Items;
import Common.Packet;

public class ClienMain {
    private Socket socket;
    private static final Scanner scanner = new Scanner(System.in);
    private static final int UDP_PORT = 6200; // Server port
    private static int CLIENT_UDP_PORT; // 🔥 Will be dynamically chosen
    private static final int TCP_PORT = 5200;
    private static boolean isLoggedIn = false;
    private static String loggedInUsername = "";
    private static String userRole = "";

    // ✅ Added for broadcast listening
    private static DatagramSocket broadcastSocket;
    private static Thread broadcastThread;

    public static void main(String[] args) throws IOException {
        // ✅ Assign dynamic port for this client instance
        DatagramSocket tempSocket = new DatagramSocket();
        CLIENT_UDP_PORT = tempSocket.getLocalPort();
        tempSocket.close();

        System.out.println("The platform is starting...");
        System.out.println("Welcome to our P2P auction system.");
        String command = "";
        while (true) {
            if (command.contains("exit")) {
                break;
            }
            System.out.println("Select a valid choice: \n1) Login\n2) Register\n3) De-Register");
            command = scanner.nextLine().trim();

            switch (command) {
                case "1":
                    login();
                    break;
                case "2":
                    register();
                    break;
                case "3":
                    deRegister();
                    break;
                default:
                    continue;
            }

            if (isLoggedIn) {
                if (userRole.equalsIgnoreCase("SELLER")) {
                    sellerMenu();
                } else if (userRole.equalsIgnoreCase("BUYER")) {
                    buyerMenu();
                }
            }
        }
    }

    public static String getIP() throws UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();
        return ip.getHostAddress();
    }

    static void login() throws UnknownHostException {
        String Username, Pw;
        System.out.printf("Please Enter Your Credentials to LogIn");
        do {
            System.out.printf("Username:");
            Username = scanner.nextLine().trim();
        } while (Username.isEmpty());
        do {
            System.out.printf("Password:");
            Pw = scanner.nextLine().trim();
        } while (Pw.isEmpty());

        Packet pack = new Packet("LOGIN", Packet.getCount(), Username + "," + Pw);
        sendUDP(pack);
    }

    static void register() throws UnknownHostException {
        String regUsername, regPw, regRole;
        System.out.printf("Please Enter Your Credentials to Register\n");

        do {
            System.out.printf("Username:");
            regUsername = scanner.nextLine().trim();
        } while (regUsername.isEmpty());

        do {
            System.out.printf("Password:");
            regPw = scanner.nextLine().trim();
        } while (regPw.isEmpty());

        while (true) {
            System.out.println("Wished Role (Buyer/Seller):");
            regRole = scanner.nextLine().trim();
            if (regRole.equalsIgnoreCase("BUYER") || regRole.equalsIgnoreCase("SELLER"))
                break;
        }

        Packet pack = new Packet("REGISTER", "1234", regUsername + "," + regPw, regRole, getIP(),
                String.valueOf(CLIENT_UDP_PORT), String.valueOf(TCP_PORT));
        sendUDP(pack);
    }

    static void deRegister() {
        String deregUsername, deregPw;
        System.out.println("Please Enter Your Credentials to De-Register");

        do {
            System.out.printf("Username:");
            deregUsername = scanner.nextLine().trim();
        } while (deregUsername.isEmpty());

        do {
            System.out.printf("Password:");
            deregPw = scanner.nextLine().trim();
        } while (deregPw.isEmpty());

        Packet pack = new Packet("DE-REGISTER", Packet.getCount(), deregUsername + "," + deregPw);
        sendUDP(pack);
    }

    static void sendUDP(Packet pack) {
        String message = pack.getMessage();
        System.out.println("Sending this message:" + message);
        try {
            InetAddress serverAddress = InetAddress.getByName("localhost");
            int serverPort = UDP_PORT;

            DatagramSocket socket = new DatagramSocket();
            byte[] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, serverPort);
            socket.send(packet);

            byte[] buffer = new byte[1024];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);

            String reply = new String(response.getData(), 0, response.getLength()).trim();

            if (reply.startsWith("LOGGED-IN")) {
                isLoggedIn = true;
                String[] elements = reply.split("\\|");
                userRole = elements[elements.length - 1].trim();
                if (userRole.equalsIgnoreCase("BUYER")) {
                    startBroadcastListener(); // ✅ Start broadcast thread
                }
            }

            System.out.println("Server replied: " + reply);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void sellerMenu() throws UnknownHostException {
        while (isLoggedIn && userRole.equalsIgnoreCase("SELLER")) {
            System.out.println("\nSELLER MENU:\n1) List an item\n2) Logout");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    listItem();
                    break;
                case "2":
                    logout();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    static void buyerMenu() throws UnknownHostException {
        while (isLoggedIn && userRole.equalsIgnoreCase("BUYER")) {
            System.out.println("\nBUYER MENU:\n1) View active auctions\n2) Bid on item\n3) Logout");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    requestItemList();
                    break;
                case "2":
                    bidItem();
                    break;
                case "3":
                    logout();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public static void logout() {
        isLoggedIn = false;
        loggedInUsername = "";
        userRole = "";
        stopBroadcastListener(); // ✅ Stop thread
        System.out.println("Logged out.");
    }

    public static void bidItem() {
        System.out.print("Enter the item name to bid on: ");
        String itemName = scanner.nextLine().trim();
        System.out.print("Enter your bid amount: ");
        String bidInput = scanner.nextLine().trim();

        try {
            double bidAmount = Double.parseDouble(bidInput);
            Packet pack = new Packet("BID_ITEM", Packet.getCount(), itemName, String.valueOf(bidAmount));
            sendUDP(pack);
        } catch (NumberFormatException e) {
            System.out.println("Invalid bid amount format.");
        }
    }

    static void listItem() throws UnknownHostException {
        System.out.print("Item Name: ");
        String itemName = scanner.nextLine().trim();
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        System.out.print("Starting Price: ");
        double price = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Auction Duration (in seconds): ");
        int duration = Integer.parseInt(scanner.nextLine().trim());

        Packet pack = new Packet("LIST_ITEM", Packet.getCount(), itemName, description, String.valueOf(price),
                String.valueOf(duration));
        sendUDP(pack);
    }

    static void requestItemList() {
        Packet pack = new Packet("VIEW_ITEMS", Packet.getCount());
        sendUDP(pack);
    }

    // ✅ Background listener
    public static void startBroadcastListener() {
        broadcastThread = new Thread(() -> {
            try {
                broadcastSocket = new DatagramSocket(CLIENT_UDP_PORT);
                byte[] buffer = new byte[1024];
                while (!broadcastSocket.isClosed()) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    broadcastSocket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength()).trim();

                    if (message.startsWith("NEW_ITEM|")) {
                        String[] parts = message.split("\\|");
                        if (parts.length >= 5) {
                            System.out.println("\n📢 NEW ITEM LISTED!");
                            System.out.println("Item: " + parts[2]);
                            System.out.println("Description: " + parts[3]);
                            System.out.println("Price: " + parts[4]);
                            System.out.println("-----------------------------");
                        }
                    }
                }
            } catch (IOException e) {
                if (!broadcastSocket.isClosed())
                    e.printStackTrace();
            }
        });
        broadcastThread.start();
    }

    public static void stopBroadcastListener() {
        try {
            if (broadcastSocket != null && !broadcastSocket.isClosed())
                broadcastSocket.close();
            if (broadcastThread != null && broadcastThread.isAlive())
                broadcastThread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     public static void sendTCP(String message, String ipAddress, int port) {
        try (Socket socket = new Socket(ipAddress, port);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            writer.println(message);
            System.out.println("Sent TCP message: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
