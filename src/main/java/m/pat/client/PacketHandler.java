package m.pat.client;

import m.pat.net.ChatPacket;

import java.io.PrintWriter;
import java.util.UUID;

public class PacketHandler {


    private ChatPacket chatPacket;
    private PrintWriter client;
    private static UUID uuid;

    public PacketHandler(ChatPacket chatPacket, PrintWriter client){
        this.chatPacket = chatPacket;
        this.client = client;
    }

    public PacketHandler(){

    }

     public ChatPacket handle(){
         ChatPacket.ClientState clientState = chatPacket.getClientState();
         //System.out.println("CS: " + clientState + ", " + chatPacket.getPacketType() + "[" + chatPacket.getBody() + "]");
         switch (clientState){
             case cClientState_REQUEST_JOIN:
                 // Means we prolly didnt get access to server...
                 if(chatPacket.getPacketType() == ChatPacket.PacketType.sPacketDenyJoin){
                     if(chatPacket.getBody().length() > 1){
                         System.out.println("Could not join server. Reason: " + chatPacket.getBody());
                     } else {
                         System.out.println("Could not join server.");
                     }
                 }
                 break;
             case cClientState_JOINED:
                 if(chatPacket.getPacketType() == ChatPacket.PacketType.sPacketAcceptJoin){
                     uuid = UUID.fromString(chatPacket.getBody());
                 } else if(chatPacket.getPacketType() == ChatPacket.PacketType.sPacketSendChannels){
                     System.out.println("Available channels to join: " + chatPacket.getBody());
                 }
                 break;

             case cClientState_REQUEST_CHANNEL:
                 if(chatPacket.getPacketType() == ChatPacket.PacketType.sPacketAcceptChannelJoin){
                     System.out.println("Joined the channel [#" + chatPacket.getBody() + "]");
                 }

                 break;

             case cClientState_IN_CHANNEL:
                 switch(chatPacket.getPacketType()){
                     case sPacketSendMembers:
                         System.out.println("Channel members: [" + chatPacket.getBody() + "]");
                        break;
                     case sPacketSendNewUser:
                         System.out.println("New user joined the channel [" + chatPacket.getBody() + "]");
                         break;
                     case sPacketSendOldUser:
                         System.out.println("Old user left the channel [" + chatPacket.getBody() + "]");
                         break;
                     case sPacketSendNewMessage:
                         System.out.println("[" + chatPacket.getBody().split(":")[0] + "] : " + chatPacket.getBody().split(":")[1]);
                         break;
                 }
                 break;
         }
         return null;
    }

    public void join(String channel){
        if (client != null && uuid != null) {
            client.println(new ChatPacket(ChatPacket.PacketType.cPacketJoinChannel, uuid.toString() + "#" + channel));
        } else {
            System.out.println("You are not connected to any server.");
        }
    }

    public void say(String str){
        if (client != null && uuid != null) {
            client.println(new ChatPacket(ChatPacket.PacketType.cPacketSendMessage, uuid.toString() + "#" + str));
        } else {
            System.out.println("You are not connected to any server.");
        }
    }
}
