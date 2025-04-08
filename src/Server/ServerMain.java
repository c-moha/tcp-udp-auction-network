package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;

import Common.Packet;
import Server.UDP_Listener;

public class ServerMain {
    private static ServerSocket tcp;
    private static DatagramSocket udp;

    private static final int UDP_PORT = 5200;
    private static final int TCP_PORT = 6200;

    public static void main(String[] args) throws IOException {
        tcp = new ServerSocket(5200);
        udp = new DatagramSocket(6200);
        byte[] buff = new byte[1024];

        DatagramPacket pack = new DatagramPacket(buff, buff.length);

        Thread UdpListenerThread = new Thread(new UDP_Listener(udp));
        Thread TcpListenerThread = new Thread(new TCP_Listener(tcp));

        UdpListenerThread.start();
        TcpListenerThread.start();

    }

}
