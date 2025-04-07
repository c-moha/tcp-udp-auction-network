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
    private static final int UDP_PORT = 6000;
    private static final int TCP_PORT = 5000;

    public static void main(String[] args) throws IOException {
        int TCP_Port = TCP_PORT;
        int UDP_Port = UDP_PORT;

        System.out.println("The plateform is starting...");
        System.out.println("Welcome to our P2P auction system.");
        String command = "";
        while (true) {

            if (command.contains("exit")) {
                break;
            }
            System.out.println("Select a valid choice: \n1) Login\n2) Register");

            command = scanner.nextLine().trim();

            switch (command) {
                case "1":
                    login();
                    break;

                case "2":
                    register();
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

    static void login() {
        String Username, Pw, Cred;
        int comIndex = -1;
        while (comIndex == -1) {
            System.out.println("Please Enter Your Credentials in the format of Username,Password:");
            Cred = scanner.nextLine().trim();
            comIndex = Cred.indexOf(",");
            if (comIndex != -1) {
                Username = Cred.substring(0, comIndex);
                Pw = Cred.substring(comIndex + 1, Cred.length());
                break;
            }
        }
    }

    static void register() throws UnknownHostException {
        String regUsername, regPw, regRole;

        System.out.println("Please Enter Your Credentials\n Username:");
        regUsername = scanner.nextLine().trim();
        System.out.println("Password:");
        regPw = scanner.nextLine().trim();
        while (true) {
            System.out.println("Wished Role (Buyer/Seller):");
            regRole = scanner.nextLine().trim();
            if (regRole.toUpperCase().equals("BUYER") || regRole.toUpperCase().equals("SELLER")) {
                break;
            }
        }

        Packet pack = new Packet("REGISTER", "1234", regUsername + "," + regPw, regRole, getIP(),
                String.valueOf(UDP_PORT), String.valueOf(TCP_PORT));

        // Will only exit the loop when the right format is followed with "Username,Pw"
    }

    static void sendUDP(Packet pack) {
        String message = pack.getMessage();
        try {
            InetAddress serverAddress = InetAddress.getByName("localhost");
            int serverPort = 6000;

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
