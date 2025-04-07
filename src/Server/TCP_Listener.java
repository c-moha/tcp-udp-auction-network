package Server;

import java.net.DatagramSocket;
import java.net.ServerSocket;

public class TCP_Listener implements Runnable {
    private ServerSocket socket;

    public TCP_Listener(ServerSocket socket) {
        this.socket = socket;
    }

    public void run() {
        System.out.println("TCP Listener running...");
    }

}
