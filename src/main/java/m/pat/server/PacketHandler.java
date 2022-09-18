package m.pat.server;

import m.pat.net.ChatPacket;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class PacketHandler {


    private ChatPacket chatPacket;
    private PrintWriter client;
    private Socket socket;
    public PacketHandler(ChatPacket chatPacket, Socket socket){
        this.chatPacket = chatPacket;
        try{
            if(socket!=null)this.client = new PrintWriter(socket.getOutputStream(), true);
        } catch(IOException e){
            e.printStackTrace();
        }
        this.socket = socket;
    }

    public ChatPacket handle(){
        ChatPacket.ClientState cs = chatPacket.getClientState();
        ChatPacket[] packets;
        //System.out.println("CS: " + cs + ", " + chatPacket.getPacketType() + "[" + chatPacket.getBody() + "]");
        switch (cs){
            case cClientState_REQUEST_JOIN:
                // New user wanting to join server
                //System.out.println("Client connected: " + chatPacket.getBody());


                if(User.getFromName(chatPacket.getBody()) != null){  // Check to see if the username exists or not
                    return new ChatPacket(ChatPacket.PacketType.sPacketDenyJoin, "NAME EXISTS");


                } else {
                    User user = new User(chatPacket.getBody(), socket);
                    System.out.println("New user [" + user.getName() + ":" + user.getUuid().toString() + "] has connected");

                    client.println(new ChatPacket(ChatPacket.PacketType.sPacketSendChannels, Channel.getChannelList().toString()));
                    client.println(new ChatPacket(ChatPacket.PacketType.sPacketAcceptJoin, user.getUuid().toString()));
                }

            case cClientState_JOINED:


                break;

            case cClientState_REQUEST_CHANNEL:
                if(chatPacket.getPacketType() == ChatPacket.PacketType.cPacketJoinChannel){
                    String uuid = chatPacket.getBody().split("#")[0];
                    String channel = chatPacket.getBody().split("#")[1];
                    // Check if the user is legitimate or not
                    User u = User.getFromUUID(UUID.fromString(uuid));
                    if(u != null){
                        u.setClientState(ChatPacket.ClientState.cClientState_REQUEST_CHANNEL);
                        // Check if requested channel exists
                        if(Channel.getChannel(channel) != null){
                            Channel ch = Channel.getChannel(channel);
                            // Check if user is in requested channel already
                            if(!ch.getUserList().contains(u)){
                                if(u.getCurrentChannel()!=null) u.getCurrentChannel().removeUser(u);
                                ch.addUser(u);
                                u.setClientState(ChatPacket.ClientState.cClientState_IN_CHANNEL);
                                System.out.println("[" + u.getName() + "] now chatting in " + "(#" + ch.getChannelName() + ")");
                                client.println(new ChatPacket(ChatPacket.PacketType.sPacketAcceptChannelJoin, ch.getChannelName()));
                                client.println(new ChatPacket(ChatPacket.PacketType.sPacketSendMembers, ch.getMemberNames().toString()));
                            } else {
                                client.println(new ChatPacket(ChatPacket.PacketType.sPacketDenyChannelJoin, "ALREADY IN CHANNEL"));
                            }


                        } else {
                            // Channel doesn't exist
                            client.println(new ChatPacket(ChatPacket.PacketType.sPacketDenyChannelJoin, "DOESNT EXIST"));
                        }
                    } else {
                        // User is null
                    }
                }
                break;

            case cClientState_IN_CHANNEL:

                if(chatPacket.getPacketType() == ChatPacket.PacketType.cPacketSendMessage){
                    if(chatPacket.getBody().split("#").length > 1){ // Check if there actually is a message
                        String uuid = chatPacket.getBody().split("#")[0];
                        String message = chatPacket.getBody().split("#")[1];
                        User u = User.getFromUUID(UUID.fromString(uuid));
                        if(u.getCurrentChannel() != null){
                            System.out.println("[" + u.getName() + "#" + u.getCurrentChannel().getChannelName() + "] : " + message);
                            u.getCurrentChannel().broadcast(u, message);
                        }
                    }
                }

                break;
        }
        return null;
    }
}
