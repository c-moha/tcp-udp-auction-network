package Server;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class BroadcastSystem {
    private static final List<InetSocketAddress> buyerClients = new ArrayList<>();

    public static synchronized void addBuyer(InetSocketAddress address) {
        buyerClients.add(address);
    }

    public static void broadcastToBuyers(String message) {
        for (InetSocketAddress buyer : buyerClients) {
            try (DatagramSocket socket = new DatagramSocket()) {
                byte[] data = message.getBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length, buyer.getAddress(), buyer.getPort());
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}