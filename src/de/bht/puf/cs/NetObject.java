package de.bht.puf.cs;

import java.io.Serializable;

/**
 * Network Exchange Object for NetServer and NetClient
 * 
 * @serial 
 * @version 1.0
 * @author Christian Schroeter
 */
public class NetObject implements Serializable {

    //0 = HighScore Request
    //1 = add Score Request
    //99 = undefined
    private int type = 99;
    private boolean ack = false;
    private Score score;
    private String highscore;
    
    /**
     * NetObject with defined request type
     * 0 - HighScore Request; 1 - add Score Request; 99 - undefined
     * 
     * @param type  Rewuest type
     */
    public NetObject (int type) {
        this.type = type;
    }
    
    /**
     * NetObject with unknown request type
     */
    public NetObject () {}
    
    /**
     * Set Request type
     * 0 - HighScore Request; 1 - add Score Request; 99 - undefined
     * 
     * @param type  Request type 
     */
    public void setType (int type) {
        this.type = type;
    }
    
    /**
     * Get Request type
     * 0 - HighScore Request; 1 - add Score Request; 99 - undefined
     * 
     * @return      Request type
     */
    public int getType () {
        return this.type;
    }
    
    /**
     * Set ack param for replys
     * 
     * @param ack   request acknowledgement
     */
    public void setAck(boolean ack) {
        this.ack = ack;
    }
    
    /**
     * Get ack for send request
     * 
     * @return      true if request received successfull 
     */
    public boolean getAck() {
        return this.ack;
    }
    
    /**
     * Set Score for exchange
     * 
     * @param score Score to transmit
     */
    public void setScore(Score score) {
        this.score = score;
    }
    
    /**
     * Get Score for exchange
     * 
     * @return Score
     */
    public Score getScore () {
        return this.score;
    }
    
    /**
     * Set HighScore String for exchange
     * 
     * @param hs    HighScore String 
     */
    public void setHighscore(String hs) {
        this.highscore = hs;
    }
    
    /**
     * Get HighSCore String for exchange
     * 
     * @return      multiline HighScore String 
     */
    public String getHighscore () {
        return this.highscore;
    }    
}
