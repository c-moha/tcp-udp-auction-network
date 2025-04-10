package Client;

import java.net.DatagramPacket;

public class NotificationRequest implements Runnable {
    DatagramPacket packet;

    public NotificationRequest(DatagramPacket packet) {
        this.packet = packet;
    }

    @Override
    public void run() {

    }

}
