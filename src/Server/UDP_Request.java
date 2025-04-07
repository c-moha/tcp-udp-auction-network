package Server;

import java.net.DatagramPacket;

import Common.DataUsers;
import Common.UserInfo;

public class UDP_Request implements Runnable {

    DatagramPacket packet;

    public UDP_Request(DatagramPacket pack) {
        this.packet = pack;
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

                break;
        }

    }

    public void registrationReq(String[] parts) {
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
            // sendReply("REGISTER_SUCCESS|" + name);
        } else {
            // sendReply("REGISTER_FAIL|User already exists");
        }
    }

}
