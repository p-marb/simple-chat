package m.pat.net;

public class ChatPacketBuilder {

    /**
     * Creates a ChatPacket object from a string of input,
     * usually through the server-client communication.
     *
     * @param input the input received
     * @return ChatPacket object from input
     */
    public static ChatPacket create(String input){
            if(input.split(":").length > 1){
                if(input.split(":")[1].split("_").length > 1){


                    String header = input.split("_")[0];
                    String body = input.split("_")[1];

                    int cStateId = Integer.parseInt(header.split(":")[0]);
                    int packetId = Integer.parseInt(header.split(":")[1]);

                    return new ChatPacket(ChatPacket.PacketType.fromId(cStateId, packetId), body);

                } else {
                    String header = input.split("_")[0];

                    int cStateId = Integer.parseInt(header.split(":")[0]);
                    int packetId = Integer.parseInt(header.split(":")[1]);

                    return new ChatPacket(ChatPacket.PacketType.fromId(cStateId, packetId), "");

                }
            }
            return null;
    }

    /**
     * Validates whether an input of query is an actual
     * packet or not.
     * @param input the suspected packet
     * @return whether the packet is actually real or not
     */
    public static boolean validate(String input){
        return create(input) != null;
    }
}
