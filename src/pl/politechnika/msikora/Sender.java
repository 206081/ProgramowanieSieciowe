package pl.politechnika.msikora;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;
import java.util.Scanner;

public class Sender extends Thread {

    Thread thread;
    private Nick nick;
    private Room room;
    private String multicast;
    private int port;

    Sender(Nick nick, Room room, String multicast, int port) {
        this.nick = nick;
        this.room = room;
        this.multicast = multicast;
        this.port = port;
        thread = new Thread(this);
        thread.start();
    }

    private void joinRoom(InetAddress group, MulticastSocket socket) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Write room name: ");
        room.setRoom(scanner.nextLine());
        String joinRoomNick = "JOIN " + room.getRoom() + " " + nick.getNick().substring(5);
        DatagramPacket joinRoom = new DatagramPacket(joinRoomNick.getBytes(), joinRoomNick.length(),
                group, socket.getLocalPort());
        try {
            socket.send(joinRoom);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        String nickName = nick.getNick().substring(5);
        String roomName;
        InetAddress group = null;
        MulticastSocket socket = null;

        try {
            group = InetAddress.getByName(multicast);
            socket = new MulticastSocket(port);
            socket.joinGroup(group);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        boolean checkWHOIS = false;

        while (true) {
            roomName = room.getRoom();
            String message = scanner.nextLine();

            if (message.toUpperCase().equals("WHOIS " + roomName)) {
                room.resetList();
                checkWHOIS = true;
            }

            message = "MSG " + nickName + " " + roomName + ": " + message;
            DatagramPacket dp = new DatagramPacket(message.getBytes(), message.length(), group, port);

            try {
                socket.send(dp);
                if (checkWHOIS) {
                    checkWHOIS = false;
                    sleep(500);
                    List<String> nicks = room.getNicksInRoom();
                    System.out.print("People in room: " + nicks.toString().replace("[", "").replace("]", ""));
                }

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            //Response when user left the room.
            if (message.split(" ")[message.split(" ").length - 1].toUpperCase().equals("LEFT")) {
                joinRoom(group, socket);
            }
        }
    }
}
