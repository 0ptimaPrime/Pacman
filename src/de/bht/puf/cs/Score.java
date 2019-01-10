package de.bht.puf.cs;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Score {
    private int score;
    private String name;
    private Date time;
    private DateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public int getScore() {
        return this.score;
    }

    public String getName() {
        return this.name;
    }
    
    public String getTime() {
        return this.time.toString();
    }
    
    public Date getTimeAsDate() {
        return this.time;
    }

    public Score(String name, int score, String time) {
        try {
            this.score = score;
            this.name = name;
            this.time = sdf.parse(time);
        } catch (ParseException ex) {
            System.out.println("#######[ PST ERROR ]#######");
            System.out.println("received time is misconfigured and couldn't be parsed");
            System.out.println("###########################");
            System.out.println();
        }
    }
}
