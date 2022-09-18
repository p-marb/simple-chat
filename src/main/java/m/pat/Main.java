package m.pat;

import m.pat.client.ClientModule;
import m.pat.server.ServerModule;

import java.util.Scanner;

/**
 *
 * Main client for interfacing with both the client and server end interfaces.
 */
public class Main {

    private static final int DEFAULT_PORT = 0;
    private static ClientModule clientModule;
    private static ServerModule serverModule;


    public static void main(String[] args){
        System.out.println("Would you like to run Server or Client module? Type !client/!server");
        Scanner in = new Scanner(System.in);
        String input;
        while(((input = in.nextLine()) != null)){
            switch(input){
                case "!client":
                    System.out.println("Please connect with the format USERNAME@127.0.0.1:12345");
                    clientModule = new ClientModule(in.nextLine());
                    clientModule.start();

                    break;
                case "!server":
                    serverModule = new ServerModule(DEFAULT_PORT);
                    serverModule.start();
                    break;

                case "!leave":

                    break;
                default:
                    if(input.startsWith("!join")){
                        String channel = input.split(" ")[1];
                        clientModule.join(channel);
                    } else if(input.startsWith("!client")){
                        if(input.split(" ").length > 1){
                            //start in quick mode (!client <port>)
                            clientModule = new ClientModule("test@127.0.0.1:" + input.split(" ")[1]);
                            clientModule.start();
                        }
                    } else {
                        clientModule.say(input);
                }
                    break;
            }
        }

    }
}
