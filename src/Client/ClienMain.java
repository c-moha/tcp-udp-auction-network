package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import Common.Packet;

public class ClienMain {
    private Socket socket;
    private static final Scanner scanner = new Scanner(System.in);
    private static final int UDP_PORT = 6200;
    private static final int TCP_PORT = 5200;

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

        }
    }

    public static String getIP() throws UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();
        String ipAddress = ip.getHostAddress();
        return ipAddress;
    }

    static void login() throws UnknownHostException {
        String Username, Pw, Role;
        System.out.printf("Please Enter Your Credentials to LogIn");
        do {
            System.out.printf("Username:");
            Username = scanner.nextLine().trim();
        } while (Username.isEmpty());
        do {
            System.out.printf("Password:");
            Pw = scanner.nextLine().trim();
        } while (Pw.isEmpty());

        while (true) {
            System.out.println("Role (Buyer/Seller):");
            Role = scanner.nextLine().trim();
            if (Role.toUpperCase().equals("BUYER") || Role.toUpperCase().equals("SELLER")) {
                break;
            }
        }

        Packet pack = new Packet("LOGIN", Packet.getCount(), Username + "," + Pw, Role, getIP(),
                String.valueOf(UDP_PORT), String.valueOf(TCP_PORT));

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
            System.out.println("Server replied: " + reply);

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static void sendTCP() {

    }
}
