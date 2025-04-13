package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import Common.DataUsers;
import Common.ItemDatabase;
import Common.Items;
import Common.Packet;
import Common.UserInfo;

public class UDP_Request implements Runnable {
    private static final int UDP_PORT = 6200;

    public DatagramPacket packet;
    public DatagramSocket send;
    public InetAddress clientIP;
    public int clientPort;

    public byte[] buffer = new byte[1024];

    public UDP_Request(DatagramPacket pack) {
        this.packet = pack;
        this.clientIP = this.packet.getAddress();
        this.clientPort = this.packet.getPort();
    }

    @Override
    public void run() {
        String message = new String(packet.getData(), 0, packet.getLength()).trim();
        String[] parts = message.split("\\|");

        if (packet.getLength() == 0) {
            return;
        }

        String command = parts[0].toUpperCase();
        System.out.println("\nReceived a request of type: " + command);

        switch (command) {
            case "REGISTER":
                try {
                    registrationReq(parts);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;

            case "LOGIN":
                logIn(parts);
                break;

            case "DE-REGISTER":

                deregisterReq(parts);

                break;

            case "LIST_ITEM":
                try {
                    listItemReq(parts);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;

            case "NEW_ITEM":
                displayNew(parts);
                break;

            case "VIEW_ITEMS":
                viewItemsReq();
                break;

            case "BID_ITEM":
                try {
                    bidItemReq(parts);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;

            // Continue here for the different types of requests/commands
        }

    }

    public void registrationReq(String[] parts) throws IOException {
        if (parts.length != 7) {
            // sendReply("REGISTER_FAIL|Invalid format");
            return;
        }

        String rq = parts[1];
        int index = parts[2].indexOf(",");
        String name = parts[2].substring(0, index);
        String password = parts[2].substring(index + 1);
        String role = parts[3];
        String ipAddress = parts[4];
        String udpPort = parts[5];
        String tcpPort = parts[6];

        UserInfo user = new UserInfo(rq, name, role, ipAddress, udpPort, tcpPort, password);
        boolean success = DataUsers.registerUser(user);

        if (success) {
            Packet response = new Packet("REGISTERED", Packet.getCount(), "");
            sendUDP(response, clientIP, clientPort);
            System.out.println("Success, we are able to register: " + name + "," + password);

        } else {
            Packet response = new Packet("REGISTER-DENIED", Packet.getCount(), "");
            sendUDP(response, clientIP, clientPort);
            System.out.println("Failed, we are un-able to register: " + name + "," + password);
        }
    }

    public void deregisterReq(String[] parts) {
        if (parts.length != 3) {
            // sendReply("REGISTER_FAIL|Invalid format");
            return;
        }

        String rq = parts[1];
        int index = parts[2].indexOf(",");
        String name = parts[2].substring(0, index);
        String password = parts[2].substring(index + 1);
        boolean success = DataUsers.deregisterUser(name);

        if (success) {
            Packet response = new Packet("DE-REGISTERED", Packet.getCount(), "");
            sendUDP(response, clientIP, clientPort);
            System.out.println("Success, we are able to register: " + name + "," + password);

        } else {
            Packet response = new Packet("DE-REGISTERED-DENIED", Packet.getCount(), "");
            sendUDP(response, clientIP, clientPort);
            System.out.println("Failed, we are un-able to de-register: " + name + "," + password);
        }

    }

    public void logIn(String[] parts) {

        if (parts.length != 3) {
            System.out.println("Wrong size");
            return;
        }
        String rq = parts[1];
        int index = parts[2].indexOf(",");
        String name = parts[2].substring(0, index);
        String password = parts[2].substring(index + 1);

        Boolean success = DataUsers.checkCredentials(name, password);

        if (success) {
            Packet response = new Packet("LOGGED-IN", Packet.getCount(), name, DataUsers.getUser(name).getRole());
            sendUDP(response, clientIP, clientPort);
            System.out.println("Success, we are able to log-in: " + name + "," + password);

        } else {
            Packet response = new Packet("LOGIN-DENIED", Packet.getCount());
            sendUDP(response, clientIP, clientPort);
            System.out.println("Failed, we are un-able to log-in: " + name + "," + password);
        }
    }

    public void listItemReq(String[] parts) throws IOException {
        if (parts.length != 7) {
            System.out.println("Wrong format");
            return;
        }

        String rq = parts[1];
        String name = parts[2];
        String desc = parts[3];
        double price = Double.parseDouble(parts[4]);
        int duration = Integer.parseInt(parts[5]);
        String userName = parts[6];

        Items item = new Items(rq, name, desc, price, duration, System.currentTimeMillis(), userName);

        boolean success = ItemDatabase.addItem(item);

        if (success) {
            Packet response = new Packet("ITEM_LISTED", Packet.getCount(), item.getName(), item.getDescription(),
                    String.valueOf(item.getPrice()));

            sendUDP(response, clientIP, clientPort);
            DataUsers.notifyBuyers();
            System.out.println("Success, we are able to List the item: " + name);
        } else {
            Packet response = new Packet("LIST_DENIED", Packet.getCount(), item.getName(), item.getDescription(),
                    String.valueOf(item.getPrice()));
            sendUDP(response, clientIP, clientPort);
            System.out.println("Failed, we are not able to List the item: " + name);
        }

    }

    public void displayNew(String[] parts) {
        String rq = parts[1];
        String name = parts[2];
        String desc = parts[3];
        double price = Double.parseDouble(parts[4]);

        System.out.println("NEW ITEM: " + name + ". Description:" + desc + ". At price: " + price + "$");
    }

    public void viewItemsReq() {
        StringBuilder builder = new StringBuilder();

        for (Items item : ItemDatabase.getAllItems()) {
            long timeLeft = item.getTimeRemainingSeconds();
            if (timeLeft > 0) {
                builder.append(item.getName()).append(",")
                        .append(item.getDescription()).append(",")
                        .append(item.getPrice()).append(",")
                        .append(timeLeft).append("s").append("|");
            }
        }

        // Remove the trailing '|' or handle empty case
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1); // remove last '|'
        } else {
            builder.append("No active items available");
        }

        // Send the properly formatted packet
        Packet response = new Packet("VIEW_ITEMS", Packet.getCount(), builder.toString());
        sendUDP(response, clientIP, clientPort);
    }

    public void bidItemReq(String[] parts) throws IOException {
        if (parts.length != 5) {
            sendUDP(new Packet("BID_FAILED", Packet.getCount(), "Invalid format"), clientIP, clientPort);
            return;
        }

        String itemName = parts[2];
        String userName = parts[4];
        double bidAmount;

        try {
            bidAmount = Double.parseDouble(parts[3]);
        } catch (NumberFormatException e) {
            sendUDP(new Packet("BID_FAILED", Packet.getCount(), "Invalid bid amount"), clientIP, clientPort);
            return;
        }

        Items item = ItemDatabase.getItemByName(itemName);
        if (item == null) {
            sendUDP(new Packet("BID_FAILED", Packet.getCount(), "Item not found"), clientIP, clientPort);
            return;
        }

        if (bidAmount <= item.getPrice()) {
            sendUDP(new Packet("BID_FAILED", Packet.getCount(), "Bid too low"), clientIP, clientPort);
            return;
        }

        // Update price
        item.setPrice(bidAmount);
        item.setHighestBidder(userName);
        ItemDatabase.loadItems();
        ItemDatabase.notifyBid(item.getName(), userName);
        ItemDatabase.saveItems(); // Save updated state

        sendUDP(new Packet("BID_SUCCESS", Packet.getCount(), item.getName(), String.valueOf(item.getPrice())), clientIP,
                clientPort);
        System.out.println("New bid on item: " + itemName + " | New price: " + bidAmount);
    }

    static void sendUDP(Packet pack, InetAddress ip, int tcp) {
        String message = pack.getMessage();
        System.out.println("Sending this message:" + message);
        try {

            DatagramSocket socket = new DatagramSocket();
            byte[] data = message.getBytes();

            DatagramPacket packet = new DatagramPacket(data, data.length, ip, tcp);
            socket.send(packet);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
