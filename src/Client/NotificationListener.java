package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import Server.UDP_Request;

public class NotificationListener implements Runnable {

    private int port;
    private DatagramSocket Listener;

    public NotificationListener(int ports) throws SocketException {
        this.port = ports;

    }

    @Override
    public void run() {
        try {
            Listener = new DatagramSocket(port);
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        while (true) {

            byte[] buffer = new byte[1024];
            DatagramPacket pack = new DatagramPacket(buffer, buffer.length);
            try {
                Listener.receive(pack);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Thread Notif_Req = new Thread(new NotificationRequest(pack));
            Notif_Req.start();
        }

    }

}
