package de.bht.puf.cs.server;

import de.bht.puf.cs.NetObject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

class ClientHandler implements Runnable  
{ 
    final String id; 
    final ObjectInputStream in; 
    final ObjectOutputStream out; 
    Socket socket;
    NetObject obj;
      
    // constructor 
    public ClientHandler(Socket socket, ObjectInputStream in, ObjectOutputStream out, String id) { 
        this.in = in; 
        this.out = out;
        this.socket = socket;
        this.id = id;
    } 
  
    @Override
    public void run() {
        
        while (true)  
        { 
            try
            { 
                //store retrieved NetObject
                obj = (NetObject) in.readObject();
                obj.setServerSessionId(id);
                        
                //continue depending on request type
                switch (obj.getType()) {
                    case 0:
                        //Output for logging purposes
                        System.out.print("-------[ GHSR INFO ]-------\n"
                                       + "Session: " + id + "\n"
                                       + "Get Highscore Request received.\n"
                                       + "---------------------------\n\n");
                                
                        //add HighScore to NetObject and set Ack to true
                        obj.setHighscore(NetServer.hs.getHighscoreString());
                        obj.setAck(true);
                        
                        this.out.writeObject(obj); 
                        
                        System.out.print("-------[ GHSR INFO ]-------\n"
                                       + "Session: " + id + "\n"
                                       + "--> Highscore send\n"
                                       + "---------------------------\n\n");
                      break;
                    case 1:
                        //Output for logging purposes
                        System.out.print("-------[ ASR INFO  ]-------\n"
                                       + "Session: " + id + "\n"
                                       + "Add Score Request received.\n"
                                       + "---------------------------\n\n");
                              
                        //add Score to HighScore and set ACk to true
                        NetServer.hs.addScore(obj.getScore());
                        obj.setAck(true);
                           
                        // search for the recipient in the connected devices list. 
                        // activeClients is the vector storing client of active users 
                        this.out.writeObject(obj);
                      break;
                    case 2:
                        //Output for logging purposes
                        System.out.print("-------[ CSR INFO  ]-------\n"
                                       + "Session: " + id + "\n"
                                       + "Close Session Request received.\n"
                                       + "---------------------------\n\n");
                           
                        //close in and out streams
                        this.in.close();
                        this.out.close();
                        this.socket.close();
                        
                        
                        System.out.print("-------[ CSR INFO  ]-------\n"
                                       + "Session: " + id + "\n"
                                       + "--> Session " + id + " closed.\n"
                                       + "---------------------------\n\n");
                        
                        
                        
                        NetServer.activeClients.remove(this);
                        NetServer.activeClients.trimToSize();
                        //Output for logging purposes
                        System.out.print("-------[ SSI INFO  ]-------\n"
                                       + "Running connections: " + NetServer.activeClients.size() + "\n\n");
                      break;   
                    default:
                        //Output for logging purposes
                        System.out.print("-------[ SRV WARN  ]-------\n"
                                       + "Session: " + id + "\n"
                                       + "Unknown Request received.\n"
                                       + "---------------------------\n\n");
                }
                                      
            } catch (Exception e) { 
            }
        } 
    }
    
    public ClientHandler getThisSession () {
        for (ClientHandler client : NetServer.activeClients) {
            if (client.id.matches(this.id)) {
                return client;
            }
        }
        return null;
    }
    
    public void closeDisconnected () {
        if (!this.socket.isConnected()) {
            try {
                System.out.print("-------[ SCI INFO  ]-------\n"
                                       + "Session: " + id + "\n"
                                       + "Closed cause by disconnect.\n"
                                       + "---------------------------\n\n");
                this.in.close();
                this.out.close();
                this.socket.close();
                NetServer.activeClients.remove(this);      
            } catch (IOException ex) {
            }
        }
    }
} 