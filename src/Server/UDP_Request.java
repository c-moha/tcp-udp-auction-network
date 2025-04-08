package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import Common.DataUsers;
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

        switch (command) {
            case "REGISTER":
                try {
                    registrationReq(parts);
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
            send = new DatagramSocket();
            buffer = "Success".getBytes();
            packet = new DatagramPacket(buffer, buffer.length, clientIP, clientPort);
            send.send(packet);
            System.out.println("Success, we are able to register: " + name + "," + password);

        } else {
            send = new DatagramSocket();
            buffer = "Failed".getBytes();
            packet = new DatagramPacket(buffer, buffer.length, clientIP, clientPort);
            send.send(packet);
        }
    }

}
