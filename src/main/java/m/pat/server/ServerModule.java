package m.pat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * ServerModule class for starting, stopping and controlling the server.
 */
public class ServerModule {

    private final int port;
    private boolean listening;

    private static Thread serverThread;

    public ServerModule(int port){
        this.port = port;
        Channel defChannel = new Channel("default");
        Channel chanChannel = new Channel("channel");
    }

    public void start(){
        serverThread = new Thread(new ServerRunnable());
        serverThread.start();
    }

    public void stop(){

    }

    /**
     * Thread to run the logic of the server in.
     */
    public class ServerRunnable implements Runnable {

        public void run(){
            // Run Server Logic
            try{
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Beginning server with port: " + serverSocket.getLocalPort());
                while(!serverSocket.isClosed()){
                    UserRunnable user = new UserRunnable(serverSocket.accept());
                    user.initialize();
                }

            } catch(IOException e){
                System.out.println("Couldn't start server: " + e.getMessage());
            }
        }
    }






}
