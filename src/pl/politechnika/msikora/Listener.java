package pl.politechnika.msikora;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Listener extends Thread {

    Thread thread;
    private Nick nick;
    private InetAddress inetAddress;
    private int port;
    private Room room;

    Listener(Nick nick, Room room, InetAddress inetAddress, int port) {
        this.nick = nick;
        this.room = room;
        this.inetAddress = inetAddress;
        this.port = port;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        InetAddress multicast_group;
        MulticastSocket multicastSocket = null;

        try {
            multicast_group = this.inetAddress;
            multicastSocket = new MulticastSocket(this.port);
            multicastSocket.joinGroup(multicast_group);
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] buf = new byte[1024];

        while (true) {

            String roomName = room.getRoom();
            String nickName = nick.getNick().substring(5);
            DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
            try {
                assert multicastSocket != null;
                multicastSocket.receive(datagramPacket);
                String received = new String(datagramPacket.getData(),
                        datagramPacket.getOffset(), datagramPacket.getLength());

                //Check that NICK is BUSY
                if (received.toUpperCase().equals(nick.getNick().toUpperCase())) {
                    String nickBusy = received + " BUSY";
                    DatagramPacket busy = new DatagramPacket(nickBusy.getBytes(), nickBusy.length(),
                            inetAddress, port);
                    multicastSocket.send(busy);
                }

                //Print NICK that joined the room.
                if (received.split(" ")[0].equals("JOIN"))
                    if (received.split(" ")[1].equals(roomName)) {
                        System.out.println(received.split(" ")[2] + " join to room.");
                    }

                //Collecting NICKs for WHOIS
                if (received.split(" ")[0].equals("ROOM"))
                    if (received.split(" ")[1].equals(roomName))
                        room.addToList(received.split(" ")[2]);

                if (received.split(" ").length > 3) {
                    //Printing received data with room validation:
                    if (received.split(" ")[0].equals("MSG"))
                        if (received.split(" ")[2].equals(roomName + ":") &&
                                !received.split(" ")[3].toUpperCase().equals("WHOIS") &&
                                !received.split(" ")[3].toUpperCase().equals("LEFT"))
                            System.out.println(received);

                    if (received.split(" ")[2].equals(roomName + ":")) {
                        //Printing who LEFT the ROOM
                        if (received.split(" ")[3].toUpperCase().equals("LEFT") &&
                                !received.split(" ")[1].equals(nickName))
                            System.out.println(received.split(" ")[1] + " left room: " + roomName);
                            //Response for WHOIS
                        else if (received.split(" ")[3].toUpperCase().equals("WHOIS")) {
                            if (received.split(" ")[4].toUpperCase().equals(roomName)) {
                                String whoIsMessage = "ROOM " + roomName + " " + nickName;
                                DatagramPacket dp = new DatagramPacket(whoIsMessage.getBytes(), whoIsMessage.length(),
                                        inetAddress, port);
                                multicastSocket.send(dp);
                            } else if (received.split(" ")[1].equals(nickName)) {
                                throw new ArrayIndexOutOfBoundsException();//Throw exception when WHOIS is wrong rised.
                            }
                        }
                    }
                }
            } catch (ArrayIndexOutOfBoundsException out) {
                System.out.println("Give correct room");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Something goes wrong restart program.");
                break;
            }
        }
    }
}
