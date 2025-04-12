package Client;

import java.net.DatagramPacket;

public class NotificationRequest implements Runnable {
    DatagramPacket packet;

    public NotificationRequest(DatagramPacket packet) {
        this.packet = packet;
    }

    @Override
    public void run() {

        System.out.println("Notification caught something");
        String message = convertPacketToString(packet);

        System.out.println(message);

    }

    public String convertPacketToString(DatagramPacket packet) {
        String reply = new String(packet.getData(), 0, packet.getLength()).trim();
        return reply;
    }

}
