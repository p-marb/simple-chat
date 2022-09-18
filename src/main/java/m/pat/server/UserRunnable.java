package m.pat.server;

import m.pat.net.ChatPacket;
import m.pat.net.ChatPacketBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class UserRunnable implements Runnable {

    private static HashMap<Thread, Socket> connectedList = new HashMap<>();
        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private Thread userThread;
        private PacketHandler packetHandler;

        public UserRunnable(Socket socket){
            client = socket;
        }

        public void initialize(){
            userThread = new Thread(this);
            connectedList.put(userThread, client);
            userThread.start();
        }

        public void run(){
            try {
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out = new PrintWriter(client.getOutputStream(), true);
                String str;


                while((str = in.readLine()) != null){
                    if(!ChatPacketBuilder.validate(str)){
                        // This isn't shit we care about.
                        client.close();
                        System.out.println("Closed " + client.getInetAddress() + " due to nonsense communication.");
                    } else {
                        PacketHandler packetHandler = new PacketHandler(ChatPacketBuilder.create(str), client);
                        ChatPacket packetResp = packetHandler.handle();
                        if(packetResp != null){
                            if(packetResp.getPacketType() == ChatPacket.PacketType.sPacketDenyJoin){
                                out.println(packetResp);
                                client.close();
                            }
                        }
                    }
                }
                User u = User.getFromSocket(client);
                if(u!=null) {
                    System.out.println("Old user [" + u.getName() + "] has left");
                    u.disconnect();
                } else {
                    System.out.println("Socket closed without user: " + client.getInetAddress() + ":" + client.getPort());
                }



            } catch(IOException e){
                User u = User.getFromSocket(client);
                if(u!=null) {
                    System.out.println("Old user [" + u.getName() + "] has left");
                    u.disconnect();
                } else {
                    System.out.println("Socket closed without user: " + client.getInetAddress() + ":" + client.getPort());
                }
                connectedList.remove(client);
            }
        }



}

