/*
 * Decompiled with CFR 0_132.
 */
package rw;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import rw.MainFrame;

public class ClientServiceThread
extends Thread {
    Socket clientSocket;
    boolean running = true;
    byte[] messageByte = new byte[1000];

    ClientServiceThread(Socket s) {
        this.clientSocket = s;
    }

    @Override
    public void run() {
        Socket s = this.clientSocket;
        System.out.println("Accepted Client from: " + this.clientSocket.getInetAddress().getHostName() + ":" + this.clientSocket.getPort());
        MainFrame.add2output("Accepted connection request from: " + this.clientSocket.getInetAddress().getHostName() + ":" + this.clientSocket.getPort());
        try {
            DataInputStream in = new DataInputStream(this.clientSocket.getInputStream());
            PrintWriter out = new PrintWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
            while (this.running) {
                int bytesRead = in.read(this.messageByte);
                String messageString = new String(this.messageByte, 0, bytesRead);
                System.out.println("Client " + s.getInetAddress() + ";" + s.getPort() + " SAYS:" + messageString);
                MainFrame.add2output("CLIENT " + s.getInetAddress() + ";" + s.getPort() + " SAYS:" + messageString);
                if (messageString.equalsIgnoreCase("quit")) {
                    this.running = false;
                    System.out.print("Stopping client thread for client " + s.getInetAddress() + ";" + s.getPort());
                    MainFrame.add2output("Stopping client thread for client " + s.getInetAddress() + ";" + s.getPort());
                    continue;
                }
                out.println(messageString);
                out.flush();
            }
        }
        catch (Exception e) {
            System.out.println("Client " + this.clientSocket.getPort() + " disconected. " + e.getMessage());
            MainFrame.add2output("Client " + s.getInetAddress() + ";" + s.getPort() + " DISCONNECTED. " + e.getMessage());
            MainFrame.refreshClientsCount(-1);
        }
        try {
            this.clientSocket.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            MainFrame.add2output(e.getMessage());
        }
    }
}

