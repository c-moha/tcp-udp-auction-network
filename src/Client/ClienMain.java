package Client;

import java.net.Socket;
import java.util.Scanner;

public class ClienMain {
    private Socket socket;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("The plateform is starting...");
        System.out.println("Welcome to our P2P auction system.");
        String command = "";
        while (true) {

            if (command.contains("exit")) {
                break;
            }
            System.out.println("Select a valid choice: \n1) Login\n2) Register");

            command = scanner.nextLine().trim();

            switch (command) {
                case "1":
                    login();
                    break;

                case "2":
                    register();
                    break;

                default:
                    continue;

            }

        }

    }

    static void login() {
        String Username, Pw, Cred;
        int comIndex = -1;
        while (comIndex == -1) {
            System.out.println("Please Enter Your Credentials in the format of Username,Password:");
            Cred = scanner.nextLine().trim();
            comIndex = Cred.indexOf(",");
            if (comIndex != -1) {
                Username = Cred.substring(0, comIndex);
                Pw = Cred.substring(comIndex + 1, Cred.length());
                break;
            }
        }
    }

    static void register() {
        String regUsername, regPw, regCred;
        int comIndex = -1;
        while (comIndex == -1) {
            System.out.println("Please Enter Your Credentials in the format of Username,Password:");
            regCred = scanner.nextLine().trim();
            comIndex = regCred.indexOf(",");
            if (comIndex != -1) {
                regUsername = regCred.substring(0, comIndex);
                regPw = regCred.substring(comIndex + 1, regCred.length());
                break;
            }
        }
        // Will only exit the loop when the right format is followed with "Username,Pw"

    }

}
