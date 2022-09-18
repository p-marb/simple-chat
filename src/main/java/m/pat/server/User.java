package m.pat.server;

import m.pat.net.ChatPacket;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {

    private String name;

    private static List<User> onlineUsers = new ArrayList<>();
    private PrintWriter out;
    private Channel currentChannel;
    private ChatPacket.ClientState clientState;
    private UUID uuid;
    private Socket socket;

    /**
     *
     * Initialize a new online user of the server.
     *
     * @param name name of the user
     */
    public User(String name, Socket socket){
        this.name = name;
        this.currentChannel = null;
        this.clientState = ChatPacket.ClientState.cClientState_JOINED;
        this.uuid = UUID.randomUUID();
        try{
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch(IOException e){
            e.printStackTrace();
        }
        this.socket = socket;

        onlineUsers.add(this);
    }

    /**
     * Method to properly disconnect the user from the server.
     */
    public void disconnect(){
        onlineUsers.remove(this);
        if(getCurrentChannel() != null) getCurrentChannel().removeUser(this);
        setCurrentChannel(null);
    }

    public UUID getUuid(){
        return this.uuid;
    }



    /**
     * The username of the user.
     * @return the username
     */
    public String getName(){
        return this.name;
    }

    /**
     * Returns a string of the username.
     * @return the username
     */
    public String toString(){
        return getName();
    }


    /**
     * A list of every online users' name.
     * @return list of users that are online
     */
    public static List<String> getOnlineUsers(){
        List<String> l = null;
        for(User u : onlineUsers){
            l.add(u.getName());
        }
        return l;
    }


    public void setCurrentChannel(Channel channel){
        this.currentChannel = channel;
    }

    public Channel getCurrentChannel(){
        return this.currentChannel;
    }

    public void setClientState(ChatPacket.ClientState cs){
        this.clientState =cs;
    }

    public ChatPacket.ClientState getClientState(){
        return this.clientState;
    }

    /**
     * Returns a user from a name
     * @param name name of the user to retrieve
     * @return either null if no user is found or the user
     */
    public static User getFromName(String name){
        for(User u : onlineUsers){
            if(u.getName().equalsIgnoreCase(name)) return u;
        }
        return null;
    }

    /**
     *
     * Returns a user from a UUID.
     * @param uuid the UUID to look for
     * @return either null of no user or user if found
     */
    public static User getFromUUID(UUID uuid){
        for(User u : onlineUsers){
            if(u.getUuid().equals(uuid)) return u;
        }
        return null;
    }

    public void write(ChatPacket chatPacket){
        this.out.println(chatPacket);
    }

    private Socket getSocket(){
        return this.socket;
    }

    public static User getFromSocket(Socket s){
        for(User u : onlineUsers){
            if(u.getSocket() == s) return u;
        }
        return null;
    }

}
