// ✅ Full Updated ClienMain.java with all necessary additions for Broadcast Support
package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import Common.DataUsers;
import Common.ItemDatabase;
import Common.Items;
import Common.Packet;
import Common.UserInfo;

public class ClienMain {
    private Socket socket;
    private static final Scanner scanner = new Scanner(System.in);

    // Ports of destination (Server)
    private static final int UDP_PORT = 6200; // Server port
    private static final int TCP_PORT = 5200;

    // Info
    private int CLIENT_UDP_PORT;
    private String ipAddress;

    // State of current client
    private UserInfo user;
    private boolean isLoggedIn = false;
    private String loggedInUsername = "";
    private String userRole = "";

    public ClienMain() throws IOException {
        this.user = new UserInfo();
        this.CLIENT_UDP_PORT = getFreePort();
        this.ipAddress = getIP();
    }

    public static void main(String[] args) throws IOException {
        // Setting up the curent Client
        ClienMain client = new ClienMain();

        // Starting prompt
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
                    client.login();
                    break;
                case "2":
                    client.register();
                    break;
                case "3":
                    client.deRegister();
                    break;
                default:
                    continue;
            }

            if (client.isLoggedIn) {

                if (client.userRole.equalsIgnoreCase("SELLER")) {
                    client.sellerMenu();
                } else if (client.userRole.equalsIgnoreCase("BUYER")) {
                    client.buyerMenu();
                }
            }
        }
    }

    // Prompt Menu
    private void sellerMenu() throws UnknownHostException {
        while (this.isLoggedIn && userRole.equalsIgnoreCase("SELLER")) {
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

    private void buyerMenu() throws UnknownHostException {
        while (isLoggedIn && userRole.equalsIgnoreCase("BUYER")) {
            System.out.println("\nBUYER MENU:\n1) View active auctions\n2) Bid on item\n3) Logout");
            String choice = scanner.nextLine().trim();
            String reply = null;
            switch (choice) {
                case "1":
                    reply = requestItemList();
                    break;
                case "2":
                    reply = bidItem();
                    break;
                case "3":
                    logout();
                    return;
                default:
                    System.out.println("Invalid choice.");
            }

            if (reply.startsWith("VIEW_ITEMS")) {
                boolean running = true;
                while (running) {
                    System.out.println("1) Subscrivbe to an item\n2) Back");
                    choice = scanner.nextLine().trim();
                    switch (choice) {
                        case "1":
                            subscribe();
                            break;

                        case "2":
                            running = false;

                            break;
                    }

                }
            }
        }
    }

    // Useful Functions
    public static String getIP() throws UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();
        return ip.getHostAddress();
    }

    public static int getFreePort() throws IOException {
        ServerSocket socket = new ServerSocket(0); // 0 = get available TCP port
        int availablePort = socket.getLocalPort();
        socket.close();
        return availablePort;
    }

    public String convertPacketToString(DatagramPacket packet) {
        String reply = new String(packet.getData(), 0, packet.getLength()).trim();
        return reply;
    }

    // Requests
    private void login() throws UnknownHostException {
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

    private void logout() {
        this.isLoggedIn = false;
        loggedInUsername = "";
        userRole = "";
        System.out.println("Logged out.");
    }

    private void register() throws UnknownHostException {
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

    private void deRegister() {
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

    private String bidItem() {
        System.out.print("Enter the item name to bid on: ");
        String itemName = scanner.nextLine().trim();
        System.out.print("Enter your bid amount: ");
        String bidInput = scanner.nextLine().trim();
        String reply = null;

        try {
            double bidAmount = Double.parseDouble(bidInput);
            Packet pack = new Packet("BID_ITEM", Packet.getCount(), itemName, String.valueOf(bidAmount));
            reply = sendUDP(pack);
        } catch (NumberFormatException e) {
            System.out.println("Invalid bid amount format.");
        }
        return reply;

    }

    private void listItem() throws UnknownHostException {
        System.out.print("Item Name: ");
        String itemName = scanner.nextLine().trim();
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        System.out.print("Starting Price: ");
        double price = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Auction Duration (in seconds): ");
        int duration = Integer.parseInt(scanner.nextLine().trim());

        Packet pack = new Packet("LIST_ITEM", Packet.getCount(), itemName, description, String.valueOf(price),
                String.valueOf(duration), user.getName());
        sendUDP(pack);
    }

    private String requestItemList() {
        Packet pack = new Packet("VIEW_ITEMS", Packet.getCount());
        return sendUDP(pack);
    }

    private String subscribe() throws UnknownHostException {
        String reply = null;
        String list = requestItemList();
        if (list.contains("No active items available")) {
            System.out.println("There is nothing to subscribe");
        } else {
            System.out.println("Please choose the element that you want to subscribe to:");
        }
        return reply;
    }

    // Core Communication with Server
    private String sendUDP(Packet pack) {
        String message = pack.getMessage();
        System.out.println("Sending this message:" + message);
        String reply = null;
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

            reply = new String(response.getData(), 0, response.getLength()).trim();

            if (reply.startsWith("LOGGED-IN")) {
                isLoggedIn = true;
                String[] elements = reply.split("\\|");

                // Get the current user role
                userRole = elements[elements.length - 1].trim();

                // Get the current user and update the port/address at which it is running
                String userName = elements[elements.length - 2].trim();
                this.user = DataUsers.getUser(userName);
                user.setUDP(Integer.toString(CLIENT_UDP_PORT));
                DataUsers.updateBuyers(userName, Integer.toString(CLIENT_UDP_PORT));

                // Starting the notification listener
                Thread notification = new Thread(new NotificationListener(CLIENT_UDP_PORT));
                notification.start();

            }

            System.out.println("Server replied: " + reply);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reply;
    }

    static void sendTCP() {
    }

    // Getters
    private int getPort() {
        return CLIENT_UDP_PORT;
    }
}
