package Server;

import java.net.*;
import java.util.*;

public class BroadcastSystem {
    private static final List<InetSocketAddress> buyerClients = new ArrayList<>();

    public static synchronized void addBuyer(InetSocketAddress address) {
        buyerClients.add(address);
    }

    public static synchronized void broadcastToBuyers(String message) {
        for (InetSocketAddress address : buyerClients) {
            try (DatagramSocket socket = new DatagramSocket()) {
                byte[] buffer = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address.getAddress(),
                        address.getPort());
                socket.send(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}