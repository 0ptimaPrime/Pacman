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
    private final static int PORT = 11111;
    
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
            //open new Socket
            Socket socket = new Socket (NetClient.SERVER, NetClient.PORT);
            
            //create in and out object streams
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            //send NetObject to open socket
            out.writeObject(obj);
            
            //store NetObject retrieved from socket
            reply = (NetObject) in.readObject();
            
            //close open streams and socket
            out.close();
            in.close();
            socket.close();
            
            //return retrieved reply
            return reply;
            
        } catch (IOException | ClassNotFoundException ex) {
            
        }
        
        //return NetObject with type undefined
        return new NetObject(99);        
    }
}