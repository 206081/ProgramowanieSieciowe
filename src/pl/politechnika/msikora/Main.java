package pl.politechnika.msikora;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        InetAddress multicast_group = null;
        String multicast = "224.0.0.10";
        int port = 10;
        MulticastSocket socket = new MulticastSocket(port);

        try {
            multicast_group = InetAddress.getByName(multicast);
            socket.joinGroup(multicast_group);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Nick nick = setNick(multicast_group, port, socket);
        Room room = new Room(setRoom());
        Sender sender = new Sender(nick, room, multicast, port);
        Listener listener = new Listener(nick, room, multicast_group, port);

        try {
            sender.thread.join();
            listener.thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String setRoom() {
        Scanner scanner = new Scanner(System.in);
        String roomName = "";

        while (roomName.length() <= 0) {
            System.out.println("Choose room: ");
            roomName = scanner.nextLine();
        }
        return roomName;
    }

    private static Nick setNick(InetAddress multicast_group, int port, MulticastSocket socket) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose nick: ");
        Nick nick = new Nick("NICK " + scanner.nextLine());

        DatagramPacket sendNick = new DatagramPacket(
                nick.getNick().getBytes(), nick.getNick().length(),
                multicast_group, port
        );

        try {
            socket.send(sendNick);
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (true) {
            byte[] buf = new byte[1024];
            DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
            String received = "";
            try {

                socket.setSoTimeout(1000);
                socket.receive(datagramPacket);
                received = new String(datagramPacket.getData(),
                        datagramPacket.getOffset(),
                        datagramPacket.getLength());
            } catch (SocketTimeoutException e) {
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (received.equals(nick.getNick() + " BUSY")) {
                System.out.println(received);
                nick = setNick(multicast_group, port, socket);
            }
        }
        return nick;
    }
}
