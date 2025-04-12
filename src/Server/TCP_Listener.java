package Server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class TCP_Listener implements Runnable {
    private ServerSocket socket;

    public TCP_Listener(ServerSocket socket) {
        this.socket = socket;
    }


    public void run() {
        System.out.println("TCP Listener running...");

        /*while (true) {
            try {
                
                Socket clientSocket = socket.accept();
                System.out.println("New TCP connection accepted from " 
                                   + clientSocket.getInetAddress());

                
                Thread t = new Thread(new TCP_Request(clientSocket));
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        
    }

}
