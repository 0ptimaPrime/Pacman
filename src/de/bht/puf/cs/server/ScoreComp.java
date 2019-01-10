package de.bht.puf.cs.server;

import de.bht.puf.cs.Score;
import java.util.Comparator;

public class ScoreComp implements Comparator<Score> {
    @Override
    public int compare(Score score1, Score score2) {

        int sc1 = score1.getScore();
        int sc2 = score2.getScore();

        if (sc1 > sc2){
            return -1;
        }else if (sc1 < sc2){
            return +1;
        }else{
            return 0;
        }
    }
}
