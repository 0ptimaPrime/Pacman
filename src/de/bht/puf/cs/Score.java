package de.bht.puf.cs;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Score stores all relevant information to add them to the HighSCore
 * 
 * @serial 
 * @version 1.0
 * @author Christian Schroeter
 */
public class Score implements Serializable{
    private int score;
    private String name;
    private Date time;
    private DateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    /**
     * Returns stored Gamescore
     * 
     * @return  Gamescore 
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Returns stored Playername
     * 
     * @return  Playername 
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Returns Gameduration as String
     * 
     * @return  Gameduration as String 
     */
    public String getTime() {
        return this.time.toString();
    }
    
    /**
     * Returns Gameduration as Date
     * 
     * @return  Gameduration as Date 
     */
    public Date getTimeAsDate() {
        return this.time;
    }

    /**
     * Create new Score
     * 
     * @param name  Playername
     * @param score Gamescore
     * @param time  Gameduration as String to parse
     */
    public Score(String name, int score, String time) {
        //try to parse and store params
        try {
            this.score = score;
            this.name = name;
            this.time = sdf.parse(time);
        } catch (ParseException ex) {
            //Output for logging purposes
            System.out.println("#######[ PST ERROR ]#######");
            System.out.println("received time is misconfigured and couldn't be parsed");
            System.out.println("###########################");
            System.out.println();
        }
    }
    
    /**
     * Create new Score
     * 
     * @param name  Playername
     * @param score Gamescore
     * @param time  Gameduration
     */
    public Score(String name, int score, Date time) {
        this.score = score;
        this.name = name;
        this.time = time;
    }
}
