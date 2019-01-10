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

public class NetServer {

    private final static int PORT = 11111;
    
    public static void main(String[] args) {
        
        NetObject obj;
        Highscore hs = new Highscore();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
                
        while (true) {    
            try {
                ServerSocket srv = new ServerSocket(NetServer.PORT);
                System.out.println("Server running and listening on port " + NetServer.PORT);
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                System.out.println();

                while(true) {
                    try {
                        Socket socket = srv.accept();
                        System.out.println("-------[ SRV INFO  ]-------");
                        System.out.println("New Connection initiated");
                        System.out.println(sdf.format(cal.getTime()));
                        System.out.println("Remote: " + socket.getInetAddress().toString().substring(1) + ":" + socket.getPort());
                        System.out.println("---------------------------");
                        System.out.println();

                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                        obj = (NetObject) in.readObject();
                        
                        
                        switch (obj.getType()) {
                            case 0:
                                System.out.println("-------[ GHSR INFO ]-------");
                                System.out.println("Get Highscore Request received.");
                                obj.setHighscore(hs.getHighscoreString());
                                obj.setAck(true);
                                out.writeObject(obj);
                                System.out.println("--> Highscore send");
                                System.out.println("---------------------------");
                                System.out.println();
                              break;
                            case 1:
                                System.out.println("-------[ ASR INFO  ]-------");
                                System.out.println("Add Score Request received.");
                                System.out.println("---------------------------");
                                System.out.println();
                                hs.addScore(obj.getScore());
                                obj.setAck(true);
                                out.writeObject(obj);
                              break;
                            default:
                                System.out.println("-------[ SRV WARN  ]-------");
                                System.out.println("Unknown Request received.");
                                System.out.println("---------------------------");
                                System.out.println();
                        }
                        
                        
                        System.out.println();
                        in.close();
                        out.close();
                    } catch (IOException | ClassNotFoundException ex) {
                        System.out.print(ex);
                    }
                }
                
            } catch (BindException e) {
                System.out.println("#######[ SRV ERROR ]#######");
                System.out.println("server port already in use");
                System.out.println("###########################");
                System.out.println();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {}
            } catch (IOException e) {
                
            }
        }
    }
}
