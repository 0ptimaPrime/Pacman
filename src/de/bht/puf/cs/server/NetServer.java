package de.bht.puf.cs.server;

import de.bht.puf.cs.NetObject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Server to reply all valid request from NetClient
 * Server stores HighScore to localstore
 * 
 * @version 1.0
 * @author Christian Schroeter
 */
public class NetServer {

    private final static int PORT = 11111;
    
    /**
     * Start NetServer to listen on requests and reply them 
     */
    public static void main(String[] args) {
        
        NetObject obj;
        Highscore hs = new Highscore();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        
        //continously run server
        while (true) {    
            try {
                //oen new socket
                ServerSocket srv = new ServerSocket(NetServer.PORT);
                //Output for logging purposes
                System.out.println("Server running and listening on port " + NetServer.PORT);
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println();

                while(true) {
                    try {
                        //accept incoming request
                        Socket socket = srv.accept();
                        
                        //Output for logging purposes
                        System.out.println("-------[ SRV INFO  ]-------");
                        System.out.println("New Connection initiated");
                        System.out.println(sdf.format(cal.getTime()));
                        System.out.println("Remote: " + socket.getInetAddress().toString().substring(1) + ":" + socket.getPort());
                        System.out.println("---------------------------");
                        System.out.println();

                        //create in and out object streams
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                        //store retrieved NetObject
                        obj = (NetObject) in.readObject();
                        
                        //continue depending on request type
                        switch (obj.getType()) {
                            case 0:
                                //Output for logging purposes
                                System.out.println("-------[ GHSR INFO ]-------");
                                System.out.println("Get Highscore Request received.");
                                
                                //add HighScore to NetObject and set Ack to true
                                obj.setHighscore(hs.getHighscoreString());
                                obj.setAck(true);
                                //send NetObject back to socket
                                out.writeObject(obj);
                                //Output for logging purposes
                                System.out.println("--> Highscore send");
                                System.out.println("---------------------------");
                                System.out.println();
                              break;
                            case 1:
                                //Output for logging purposes
                                System.out.println("-------[ ASR INFO  ]-------");
                                System.out.println("Add Score Request received.");
                                System.out.println("---------------------------");
                                System.out.println();
                                
                                //add Score to HighScore and set ACk to true
                                hs.addScore(obj.getScore());
                                obj.setAck(true);
                                //send NetObject back to socket
                                out.writeObject(obj);
                              break;
                            default:
                                //Output for logging purposes
                                System.out.println("-------[ SRV WARN  ]-------");
                                System.out.println("Unknown Request received.");
                                System.out.println("---------------------------");
                                System.out.println();
                        }
                        
                        
                        System.out.println();
                        //close in and out streams
                        in.close();
                        out.close();
                    } catch (IOException | ClassNotFoundException ex) {
                        
                    }
                }
                
            } catch (BindException e) {
                //Output for logging purposes
                System.out.println("#######[ SRV ERROR ]#######");
                System.out.println("server port already in use");
                System.out.println("###########################");
                System.out.println();
                try {
                    //wait before another start of NetServer
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {}
            } catch (IOException e) {
                
            }
        }
    }
}
