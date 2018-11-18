package pl.politechnika.msikora;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        String serverAddress;
        serverAddress = scanner.nextLine();
        Socket s = new Socket();


        boolean running = true;
        byte[] messageByte = new byte[1000];
        try {
            DataInputStream in = new DataInputStream(s.getInputStream());
            PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            while (running) {
                int bytesRead = in.read(messageByte);
                String messageString = new String(messageByte, 0, bytesRead);
                System.out.println("Client " + s.getInetAddress() + ";" + s.getPort() + " SAYS:" + messageString);
                if (messageString.equalsIgnoreCase("quit")) {
                    running = false;
                    System.out.print("Stopping client thread for client " + s.getInetAddress() + ";" + s.getPort());
                    continue;
                }
                System.out.println(messageString);
                out.flush();
            }
        }
        catch (Exception e) {
            System.out.println("Client " + s.getPort() + " disconected. " + e.getMessage());
        }

    }
}
