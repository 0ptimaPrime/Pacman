package de.bht.puf.cs;

import java.io.Serializable;

public class NetObject implements Serializable {

    //0 = HighScore Request
    //1 = add Score Request
    //99 = undefined
    private int type = 99;
    private boolean ack = false;
    private Score score;
    private String highscore;
    
    public NetObject (int type) {
        this.type = type;
    }
    
    public NetObject () {}
    
    public void setType (int type) {
        this.type = type;
    }
    
    public int getType () {
        return this.type;
    }
        
    public void setAck(boolean ack) {
        this.ack = ack;
    }
    
    public boolean getAck() {
        return this.ack;
    }
    
    public void setScore(Score score) {
        this.score = score;
    }
    
    public Score getScore () {
        return this.score;
    }
    
    public void setHighscore(String hs) {
        this.highscore = hs;
    }
    
    public String getHighscore () {
        return this.highscore;
    }    
}
