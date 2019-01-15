package de.bht.puf.cs.server;

import de.bht.puf.cs.Score;
import java.util.Comparator;
import java.util.Date;

/**
 * Compares two Score Objects
 * 
 * @version 1.0
 * @author Christian Schroeter
 */
public class ScoreComp implements Comparator<Score> {
    
    /**
     * Override of Comperator.compare to compare to Scores
     * 
     * First Score.score ist compared of both objects.
     * If Score.score is equal, Score.time will be compared.
     * 
     * @param score1    Score
     * @param score2    Score to compare with
     * @return          -1 if Score1 is better; 1 if Score2 is better; 0 if Scores are equal 
     */
    @Override
    public int compare(Score score1, Score score2) {

        int s1s = score1.getScore();
        int s2s = score2.getScore();
        Date s1t = score1.getTimeAsDate();
        Date s2t = score2.getTimeAsDate();

        if (s1s > s2s){
            return -1;
        }else if (s1s < s2s){
            return +1;
        }else if (s1t.compareTo(s2t) > 0 ){
            return +1;
        }else if (s1t.compareTo(s2t) < 0 ){
            return -1;
        }else{
            return 0;
        }
    }
}
