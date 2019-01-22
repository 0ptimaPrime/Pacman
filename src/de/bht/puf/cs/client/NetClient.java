package de.bht.puf.cs.client;

import de.bht.puf.cs.NetObject;
import de.bht.puf.cs.Score;
import java.io.*;
import java.net.Socket;

/**
 * Client to interact with running NetServer
 * 
 * @version 1.0
 * @author Christian Schroeter
 */
public class NetClient {

    private final static String SERVER = "127.0.0.1";
    //Alternate Server
    //private final static String SERVER = "beuth.cs-networks.info";
    private final static int PORT = 11111;
    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    
    public NetClient () {
        openSession();
    }
    
    public static boolean closeSession() {
        NetObject obj = sendAndReceive((new NetObject(2)));
        try {
            in.close();
            out.close();
            socket.close();
            
            return true;
        } catch (IOException ex) {
        }
        
        return false;
    }
    
    
    public static void openSession() {
        try {
            //open new Socket
            socket = new Socket (SERVER, PORT);
            
            //create in and out object streams
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
        }
    }
        
    /**
     * Send Score to NetServer
     * 
     * @param name  Playername
     * @param score Gamescore
     * @param time  Gameduration
     * @return      true if successfull
     */
    public static boolean sendScore(String name, int score, String time) {
        
        //create new NetObject and define type as "add Score request"
        NetObject nObj = new NetObject(1);
        nObj.setScore((new Score(name, score, time)));
        //send request and store reply
        NetObject obj = sendAndReceive(nObj);
        
        return obj.getAck();
    }
    
    /**
     * Send Score to NetServer
     * 
     * @param score Score to add
     * @return      true if successfull
     */
    public static boolean sendScore(Score score) {
        
        //create new NetObject and define type as "add Score request"
        NetObject nObj = new NetObject(1);
        nObj.setScore(score);
        //send request and store reply
        NetObject obj = sendAndReceive(nObj);
        
        return obj.getAck();
    }
    
    /**
     * Request current HighScore from Server
     * 
     * @return  HighScore as multiline String 
     */
    public static String getHighscore() {
        
        //send HighScore request and store reply
        NetObject obj = sendAndReceive((new NetObject(0)));
        
        return obj.getHighscore();
    }
    
    /**
     * connect to NetServer, send request and return reply
     * 
     * @param obj   NetObject to send to NetServer
     * @return      NetObject received from NetServer
     */
    private static NetObject sendAndReceive (NetObject obj) {
        
        NetObject reply;
        
        //try to connect to NetServer
        try {
            //send NetObject to open socket
            out.writeObject(obj);
            
            //store NetObject retrieved from socket
            reply = (NetObject) in.readObject();
            
            //return retrieved reply
            return reply;
            
        } catch (IOException | ClassNotFoundException ex) {
            
        }
        
        //return NetObject with type undefined
        return new NetObject(99);        
    }

}
