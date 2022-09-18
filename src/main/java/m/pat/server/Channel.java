package m.pat.server;

import m.pat.net.ChatPacket;

import java.util.ArrayList;
import java.util.List;

public class Channel {

    private String channelName;
    private List<User> userList;
    private static List<Channel> channelList = new ArrayList<>();

    /**
     * Initializes a new channel.
     * @param channelName the name of the channel to create.
     */
    public Channel(String channelName){
        this.channelName = channelName;
        this.userList = new ArrayList<>();
        channelList.add(this);
    }

    public String getChannelName(){
        return this.channelName;
    }

    /**
     * List of the users in a channel.
     * @return the list of users in the channel
     */
    public List<User> getUserList(){
        return this.userList;
    }

    /**
     * Adds a user to a channel, sets the users current channel
     * to what is provided.
     * @param user the user to add
     * @return whether the user was successfully added or not.
     */
    public void addUser(User user){
        for(User u : getUserList()){
            u.write(new ChatPacket(ChatPacket.PacketType.sPacketSendNewUser, user.getName()));
        }
            userList.add(user);
            user.setCurrentChannel(this);
    }


    /**
     * Removes a user from a channel and sets the
     * users current channel to null.
     *
     * @param user the user to remove
     */
    public void removeUser(User user){
        userList.remove(user);
        user.setCurrentChannel(null);
        for(User u : getUserList()){
            u.write(new ChatPacket(ChatPacket.PacketType.sPacketSendOldUser, user.getName()));
        }
    }

    public String toString(){
        return this.channelName;
    }

    /**
     * Returns the channel, if found, from a given channel name.
     * @param name the name of a channel.
     * @return the channel from the name.
     */
    public static Channel getChannel(String name){
        for(Channel c : channelList){
            if(c.toString().equalsIgnoreCase(name)){
                return c;
            }
        }
        return null;
    }

    /**
     * Returns a list of all channels created.
     *
     * @return the list of channels.
     */
    public static List<Channel> getChannelList(){
        return channelList;
    }

    /**
     * Sends a message to all the members of a channel.
     * @param user the user who sent the message
     * @param msg the message to send
     */
    public void broadcast(User user, String msg){
        for(User u : getUserList()){
            u.write(new ChatPacket(ChatPacket.PacketType.sPacketSendNewMessage, user.getName() + ":" + msg));
        }
    }

    public List<String> getMemberNames(){
        List<String> l = new ArrayList<>();
        for(User u : getUserList()){
            l.add(u.getName());
        }
        return l;
    }



}
