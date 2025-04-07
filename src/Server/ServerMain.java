package Server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

import Common.Packet;
import Server.UDP_Listener;

public class ServerMain {
    private static ServerSocket tcp;
    private static DatagramSocket udp;

    public static void main(String[] args) throws IOException {
        tcp = new ServerSocket(5000);
        udp = new DatagramSocket(6000);

        Thread UdpThread = new Thread(new UDP_Listener(udp));
        Thread TcpThread = new Thread(new TCP_Listener(tcp));

    }

}
