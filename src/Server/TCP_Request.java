package Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import Common.DataUsers;
import Common.Items;
import Common.Packet;
import Common.UserInfo;

public class TCP_Request implements Runnable {
    private static final int TCP_PORT = 5200;
    private Socket clientSocket;

    public TCP_Request(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
         System.out.println("Handling new TCP client...");
        try (
            
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            PrintWriter writer = new PrintWriter(output, true);
        ) {
            String line = reader.readLine();
            if (line != null) {
                line = line.trim();            // remove extra whitespace
                String[] parts = line.split("\\|"); 
                String command = parts[0].toUpperCase();
                if(command.equals("INFORM_Res")){
                        CreditCardInfo(parts);
                }

            }

            
            
        


        }
         catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close(); 
            } catch (IOException e) { 
                e.printStackTrace(); 
            }
        }
    }

    //Quand le server recoit le message avec le credit card info
    public void CreditCardInfo(String[] parts){
        if (parts.length != 6) {
            System.out.println("Wrong format");
            return;
        }
        String rq = parts[1];
        String Name = parts[2];
        String CreditNumber = parts[3];
        String ExpDate = parts[4];
        String Address = parts[5];



    }

    public void AuctionClosed(Items item)
    {
       if(item.getTimeRemainingSeconds()== 0 ){
        //if(bidder exist)
         //notifyBuyer();
         //NotifySeller();
         //else no bidder, 
         //NotifyNoBid()
         
       }
        
    }

    public void NotifyNoBid(Items item){
        UserInfo seller = item.getSellerName() ;

        try {
            Socket sellerSocket = new Socket(seller.getIpAddress(), Integer.parseInt(seller.getTcpPort()));
            PrintWriter writer = new PrintWriter(sellerSocket.getOutputStream(), true);
            
        
            String rq = Packet.getCount();
            String message = "NON_OFFER|" + rq + "|" + item.getName();
            writer.println(message);
            System.out.println("Sent to seller: " + message);
            
            sellerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }


    public void NotifySeller(Items item){
        UserInfo seller = item.getSellerName() ;

        try {
            Socket sellerSocket = new Socket(seller.getIpAddress(), Integer.parseInt(seller.getTcpPort()));
            PrintWriter writer = new PrintWriter(sellerSocket.getOutputStream(), true);
            
            UserInfo buyer = item.getSellerName() ; //change ca pour le bhaye buyer
            String rq = Packet.getCount();
            String message = "SOLD|" + rq + "|" + item.getName() + "|" + item.getPrice() + "|" + buyer;
            writer.println(message);
            System.out.println("Sent to seller: " + message);
            
            sellerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    public void notifyBuyer(Items item){
        UserInfo buyer = item.getSellerName();//change ca ;

        try {
            Socket buyerSocket = new Socket(buyer.getIpAddress(), Integer.parseInt(buyer.getTcpPort()));
            PrintWriter writer = new PrintWriter(buyerSocket.getOutputStream(), true);
            
        
            String rq = Packet.getCount();
            String name = item.getName();
            Double price = item.getPrice();
            UserInfo Seller = item.getSellerName();

            String message = "WINNER|" + rq + "|" + name + "|"+ price+ "|" + Seller;
            writer.println(message);
            System.out.println("Sent to seller: " + message);
            
            buyerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
    

