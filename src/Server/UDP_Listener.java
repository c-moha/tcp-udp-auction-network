package Server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.channels.SocketChannel;

public class UDP_Listener implements Runnable {
    private DatagramSocket socket;

    public UDP_Listener(DatagramSocket socket) {
        this.socket = socket;
    }

    public void run() {
        System.out.println("UDP Listener running...");
    }
}
