package vicinity.ConnectionManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vicinity.model.Globals;


public class ChatServer implements Runnable{

    private final static String TAG ="ChatServer";

    private static ChatServer server;
    private ServerSocket serverSocket;
    private ExecutorService executorService = Executors.newFixedThreadPool(20);

    @Override
    public void run() {
        try {
            System.out.println("Starting Server");
            serverSocket = new ServerSocket(Globals.CHAT_PORT);

            while(true) {
                System.out.println("Waiting for request");
                try {
                    Socket s = serverSocket.accept();
                    System.out.println("Processing request");
                    executorService.submit(new ServiceRequest(s));
                } catch(IOException ioe) {
                    System.out.println("Error accepting connection");
                    ioe.printStackTrace();
                }
            }
        }catch(IOException e) {
            System.out.println("Error starting Server on "+Globals.CHAT_PORT);
            e.printStackTrace();
        }
    }

}
