package de.bht.puf.cs.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Server to reply all valid request from NetClient
 * Server stores HighScore to localstore
 * 
 * @version 1.0
 * @author Christian Schroeter
 */
public class NetServer {

    //define NetServer port
    private final static int PORT = 11111;
    
    //Vector to store active clients
    static ArrayList<ClientHandler> activeClients = new ArrayList<>();
    
    static Highscore hs;
    
    /**
     * Start NetServer to listen on requests and reply them 
     */
    public static void main(String[] args) {
        
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        hs = new Highscore();
                
        //continously run server
        while (true) {    
            try {
                //oen new socket
                ServerSocket srv = new ServerSocket(NetServer.PORT);
                //Output for logging purposes
                System.out.print(
                        "Server running and listening on port " + NetServer.PORT +"\n"
                      + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n\n");
                
                while(true) {
                    try {
                        //accept incoming request
                        Socket socket = srv.accept();
                                                
                        //create random session UUID
                        //UUID uuid = UUID.randomUUID();
                        //long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
                        //String id = Long.toString(l, Character.MAX_RADIX);
                        String id = socket.getInetAddress().toString().substring(1) + ":" + Integer.toString(socket.getPort()) + " @ " + Long.toString(cal.getTimeInMillis());
                        
                        //Output for logging purposes
                        System.out.print("-------[ SRV INFO  ]-------\n"
                                       + "New Connection initiated\n"
                                       + sdf.format(cal.getTime()) + "\n"
                                       + "Session: " + id + "\n"
                                       + "---------------------------\n\n");
                        
                        //create in and out object streams
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                        //create Handler for new client
                        ClientHandler client = new ClientHandler(socket, in, out, id);
                        
                        //run as Thread to support other clients
                        Thread clientSession = new Thread(client);
                        
                        activeClients.add(client);
                        clientSession.start();
                        
                        activeClients.forEach(curClient -> curClient.closeDisconnected());
                        activeClients.trimToSize();
                        
                    } catch (IOException ex) {
                        
                    }
                }
                
            } catch (BindException e) {
                //Output for logging purposes
                System.out.print("#######[ SRV ERROR ]#######\n"
                               + "server port already in use\n"
                               + "###########################\n\n");
                try {
                    //wait before another start of NetServer
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {}
            } catch (IOException e) {
                
            }
        }
    }
}