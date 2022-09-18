package m.pat.net;

/**
 *
 * Class to handle the packet architecture between server and client.
 */
public class ChatPacket {

    public enum ClientState {
        cClientState_REQUEST_JOIN(0),
        cClientState_JOINED(1),
        cClientState_REQUEST_CHANNEL(2),
        cClientState_IN_CHANNEL(3);

        private final int clientState;
        ClientState(int state){
            this.clientState = state;
        }

        public static ClientState fromId(int id){
            for(ClientState cs : values()){
                if(cs.clientState == id) return cs;
            }
            return null;
        }
    }

    public enum PacketType {
        cPacketJoin(ClientState.cClientState_REQUEST_JOIN, 0),
        sPacketDenyJoin(ClientState.cClientState_REQUEST_JOIN, 1),
        sPacketAcceptJoin(ClientState.cClientState_JOINED, 0),
        sPacketSendChannels(ClientState.cClientState_JOINED, 1),
        cPacketJoinChannel(ClientState.cClientState_REQUEST_CHANNEL, 0),
        sPacketDenyChannelJoin(ClientState.cClientState_REQUEST_CHANNEL, 1),
        sPacketAcceptChannelJoin(ClientState.cClientState_REQUEST_CHANNEL, 2),
        sPacketSendMembers(ClientState.cClientState_IN_CHANNEL, 0),
        cPacketSendMessage(ClientState.cClientState_IN_CHANNEL, 1),
        sPacketSendNewMessage(ClientState.cClientState_IN_CHANNEL, 2),
        sPacketSendNewUser(ClientState.cClientState_IN_CHANNEL, 3),
        sPacketSendOldUser(ClientState.cClientState_IN_CHANNEL, 4),
        cPacketLeaveChannel(ClientState.cClientState_IN_CHANNEL, 5);
        private final ClientState clientState;
        private final int packetType;

        PacketType(ClientState clientState, int packetType){
            this.clientState = clientState;
            this.packetType = packetType;
        }

        public static PacketType fromId(int cs, int id){
            for(PacketType pType : values()){
                if(pType.packetType == id && pType.clientState.clientState == cs ) return pType;
            }
            return null;
        }

    }

    private PacketType packetType;
    private String body = "";

    public ChatPacket(PacketType header){
        this.packetType = header;
    }

    public ChatPacket(PacketType header, String body){
        this.packetType = header;
        this.body = body;
    }

    public String toString(){
        return packetType.clientState.clientState + ":" + packetType.packetType + "_" + body;
    }

    public ClientState getClientState(){
        return packetType.clientState;
    }

    public PacketType getPacketType(){
        return packetType;
    }

    public String getBody(){
        return body;
    }

}
