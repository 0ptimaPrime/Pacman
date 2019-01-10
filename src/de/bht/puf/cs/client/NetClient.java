package de.bht.puf.cs.client;

import de.bht.puf.cs.NetObject;
import de.bht.puf.cs.Score;
import java.io.*;
import java.net.Socket;

public class NetClient {

    private final static String SERVER = "127.0.0.1";
    private final static int PORT = 11111;
    
    public static boolean sendScore(String name, int score, String time) {
        
        NetObject nObj = new NetObject(1);
        nObj.setScore((new Score(name, score, time)));
        NetObject obj = sendAndReceive(nObj);
        
        return obj.getAck();
    }
    
    public static boolean sendScore(Score score) {
        
        NetObject nObj = new NetObject(1);
        nObj.setScore(score);
        NetObject obj = sendAndReceive(nObj);
        
        return obj.getAck();
    }
    
    public static String getHighscore() {
        
        NetObject obj = sendAndReceive((new NetObject(0)));
        
        return obj.getHighscore();
    }
    
    private static synchronized NetObject sendAndReceive (NetObject obj) {
        
        NetObject reply;
        
        try {
            Socket socket = new Socket (NetClient.SERVER, NetClient.PORT);
            
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            out.writeObject(obj);
            
            reply = (NetObject) in.readObject();
            
            out.close();
            in.close();
            socket.close();
            
            return reply;
            
        } catch (IOException | ClassNotFoundException ex) {
            
        }
        
        return new NetObject(99);        
    }
}