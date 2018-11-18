/*
 * Decompiled with CFR 0_132.
 */
package rw;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import rw.ClientServiceThread;
import rw.MainFrame;

public class TCPServer
extends Thread {
    ServerSocket serwerek = null;
    ArrayList clientsList = new ArrayList();

    public void Start(int port) {
        try {
            if (this.serwerek != null && this.serwerek.isBound()) {
                this.serwerek.close();
            }
            this.serwerek = new ServerSocket(port);
            this.start();
        }
        catch (IOException e) {
            this.serwerek = null;
            System.out.println(e.getMessage());
            MainFrame.add2output(e.getMessage());
        }
    }

    public void Stop() {
        try {
            if (this.serwerek != null && this.serwerek.isBound()) {
                this.serwerek.close();
                System.out.println("Server stopped.");
                MainFrame.add2output("Server stopped");
                for (int i = 0; i < this.clientsList.size(); ++i) {
                    Socket s = (Socket)this.clientsList.get(i);
                    if (!s.isConnected()) continue;
                    s.close();
                }
            }
            this.serwerek = null;
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            MainFrame.add2output(e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("Server is working on port " + this.serwerek.getLocalPort());
            System.out.println("Waiting for new client");
            MainFrame.add2output("Server is working on port " + this.serwerek.getLocalPort());
            MainFrame.add2output("Waiting for new client");
            do {
                Socket clientSocket = this.serwerek.accept();
                this.clientsList.add(clientSocket);
                MainFrame.refreshClientsCount(1);
                ClientServiceThread cliThread = new ClientServiceThread(clientSocket);
                cliThread.start();
            } while (true);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            MainFrame.add2output(e.getMessage());
            return;
        }
    }

    public static void main(String[] args) {
        TCPServer mojSerwerek = new TCPServer();
        mojSerwerek.Start(7);
    }
}

