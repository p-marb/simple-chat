package m.pat.client;

import m.pat.net.ChatPacket;
import m.pat.net.ChatPacketBuilder;
import m.pat.client.PacketHandler;
import m.pat.server.UserRunnable;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientModule {

    private String username;
    private String address;
    private int port;
    private static Thread clientThread;
    private static Socket clientSocket;
    private static PacketHandler packetHandler;


    public ClientModule(String args){
        username = args.split("@")[0];
        address = args.split("@")[1].split(":")[0];
        port = Integer.parseInt(args.split("@")[1].split(":")[1]);
    }

    public void start(){
        clientThread = new Thread(new ClientRunnable());
        clientThread.start();
    }

    public String getUsername(){
        return this.username;
    }

    public String getAddress(){
        return this.address;
    }

    public int getPort(){
        return this.port;
    }

    /**
     * Attempts to join a channel
     * @param channel name of channel to join
     */
    public void join(String channel){
        packetHandler.join(channel);
    }

    public void say(String input){
        packetHandler.say(input);
    }

    private class ClientRunnable implements Runnable {

        private static PrintWriter out;
        private static BufferedReader in;

        public void run(){
            try{
                clientSocket = new Socket(getAddress(), getPort());

                out = new PrintWriter( clientSocket.getOutputStream(), true );
                in = new BufferedReader( new InputStreamReader( clientSocket.getInputStream() ) );

                out.println(new ChatPacket(ChatPacket.PacketType.cPacketJoin, getUsername()));
                System.out.println("Attempting to connect: " + getUsername() + "@" + getAddress() + ":" + getPort());

                String str;
                while((str = in.readLine()) != null){
                    if(!ChatPacketBuilder.validate(str)){
                        // This isn't shit we care about.
                        clientSocket.close();
                        System.out.println("Closed " + clientSocket.getInetAddress() + " due to nonsense communication. (" + str + ")");
                    } else {
                        packetHandler = new PacketHandler(ChatPacketBuilder.create(str), out);
                        packetHandler.handle();
                    }
                }
                System.out.println("Lost connection to server.");

            } catch(IOException e){
                System.out.println("Could not connect: " + e.getMessage());
            }
        }

        public static void send(ChatPacket chatPacket){
            out.println(chatPacket);
        }
    }



}
