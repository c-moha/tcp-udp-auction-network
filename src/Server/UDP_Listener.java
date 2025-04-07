package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.Buffer;
import java.nio.channels.SocketChannel;

public class UDP_Listener implements Runnable {
    private DatagramSocket socket;

    public UDP_Listener(DatagramSocket socket) {
        this.socket = socket;
    }

    public void run() {
        System.out.println("UDP Listener running...");
        while (true) {

            byte[] buffer = new byte[1024];
            DatagramPacket pack = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(pack);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Thread Udp_Req = new Thread(new UDP_Request(pack));
            Udp_Req.start();
        }

    }
}
