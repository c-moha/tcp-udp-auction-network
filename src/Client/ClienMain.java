package Client;

import java.io.IOException;
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
    private static final int UDP_PORT = 6200;
    private static final int TCP_PORT = 5200;
    private static boolean isLoggedIn = false;
    private static String loggedInUsername = "";
    private static String userRole = "";

    public static void main(String[] args) throws IOException {

        System.out.println("The plateform is starting...");
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
        String ipAddress = ip.getHostAddress();
        return ipAddress;
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

        System.out.printf("Please Enter Your Credentials to Registe");

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
            if (regRole.toUpperCase().equals("BUYER") || regRole.toUpperCase().equals("SELLER")) {
                break;
            }
        }
        Packet pack = new Packet("REGISTER", "1234", regUsername + "," + regPw, regRole, getIP(),
                String.valueOf(UDP_PORT), String.valueOf(TCP_PORT));
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

            // Receive response
            byte[] buffer = new byte[1024];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);

            String reply = new String(response.getData(), 0, response.getLength()).trim();

            if (reply.startsWith("LOGGED-IN")) {
                isLoggedIn = true;
                String[] elements = reply.split("\\|");
                String role = elements[elements.length - 1].trim();
                userRole = role;
            } else if (reply.startsWith("ITEM_LIST|")) {
                String[] parts = reply.substring(10).split(";");
                System.out.println("\n--- Active Items ---");
                for (String item : parts) {
                    String[] fields = item.split("\\|");
                    if (fields.length >= 4) {
                        System.out.println("Item: " + fields[0]);
                        System.out.println("Description: " + fields[1]);
                        System.out.println("Price: " + fields[2]);
                        System.out.println("Time Remaining: " + fields[3] + " sec");
                        System.out.println("-------------------------");
                    }
                }
            } else {
                System.out.println("Server replied: " + reply);
            }

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
                    isLoggedIn = false;
                    loggedInUsername = "";
                    userRole = "";
                    System.out.println("Logged out.");
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
                    bidItem(); // NEW
                    break;

                case "3":
                    isLoggedIn = false;
                    loggedInUsername = "";
                    userRole = "";
                    System.out.println("Logged out.");
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    public static void bidItem() {
        System.out.print("Enter the item name to bid on: ");
        String itemName = scanner.nextLine().trim();

        System.out.print("Enter your bid amount: ");
        String bidInput = scanner.nextLine().trim();

        double bidAmount;
        try {
            bidAmount = Double.parseDouble(bidInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid bid amount format.");
            return;
        }

        Packet pack = new Packet("BID_ITEM", Packet.getCount(), itemName, String.valueOf(bidAmount));
        sendUDP(pack);
    }

    static void listItem() throws UnknownHostException {
        String itemName, description;
        double price;
        int duration;

        System.out.print("Item Name: ");
        itemName = scanner.nextLine().trim();

        System.out.print("Description: ");
        description = scanner.nextLine().trim();

        System.out.print("Starting Price: ");
        price = Double.parseDouble(scanner.nextLine().trim());

        System.out.print("Auction Duration (in seconds): ");
        duration = Integer.parseInt(scanner.nextLine().trim());

        Packet pack = new Packet("LIST_ITEM", Packet.getCount(), itemName, description, String.valueOf(price),
                String.valueOf(duration));
        sendUDP(pack);
    }

    static void requestItemList() {
        Packet pack = new Packet("VIEW_ITEMS", Packet.getCount());
        sendUDP(pack);
    }

    static void sendTCP() {

    }

}
