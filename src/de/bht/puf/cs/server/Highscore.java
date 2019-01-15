package de.bht.puf.cs.server;

import de.bht.puf.cs.Score;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * HighScore read and write Scores sorted to localstorage on NetServer
 * 
 * @version 1.0
 * @author Christian Schroeter
 */
public class Highscore {
    private ArrayList<Score> scores;

    private static final String HIGHSCORE_FILE = "scores.dat";
    
    static int maxScoresCount = 10;

    ObjectOutputStream outputStream = null;
    ObjectInputStream inputStream = null;

    public Highscore() {
        scores = new ArrayList<Score>();
    }
    
    public ArrayList<Score> getScores() {
        loadScoreFile();
        sort();
        return scores;
    }
    
    private void sort() {
        ScoreComp comparator = new ScoreComp();
        Collections.sort(scores, comparator);
    }
    
    public void addScore(Score score) {
        loadScoreFile();
        scores.add(score);
        updateScoreFile();
    }
    
    public void addScore(String name, int score, String time) {
        loadScoreFile();
        scores.add(new Score(name, score, time));
        updateScoreFile();
    }
    
    public void loadScoreFile() {
        try {
            inputStream = new ObjectInputStream(new FileInputStream(HIGHSCORE_FILE));
            scores = (ArrayList<Score>) inputStream.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("#######[ FNF ERROR ]#######");
            System.out.println(e.getMessage());
            System.out.println("############################");
            System.out.println();
        } catch (IOException e) {
            System.out.println("#######[ IO ERROR  ]#######");
            System.out.println(e.getMessage());
            System.out.println("############################");
            System.out.println();
        } catch (ClassNotFoundException e) {
            System.out.println("#######[ CNF ERROR ]#######");
            System.out.println(e.getMessage());
            System.out.println("############################");
            System.out.println();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException e) {
                System.out.println("#######[ IO ERROR  ]#######");
                System.out.println(e.getMessage());
                System.out.println("############################");
                System.out.println();
            }
        }
    }
    
    public void updateScoreFile() {
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(HIGHSCORE_FILE));
            outputStream.writeObject(scores);
        } catch (FileNotFoundException e) {
            System.out.println("[Update] FNF Error: " + e.getMessage() + ",the program will try and make a new file");
        } catch (IOException e) {
            System.out.println("[Update] IO Error: " + e.getMessage());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException e) {
                System.out.println("[Update] Error: " + e.getMessage());
            }
        }
    }
    
    public String getHighscoreString() {
        String highscoreString = "";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        
        ArrayList<Score> scoreList;
        scoreList = getScores();

        int i = 0;
        int x = scoreList.size();
        if (x > maxScoresCount) {
            x = maxScoresCount;
        }
        while (i < x) {
            highscoreString += (i + 1) + ".\t" +  scoreList.get(i).getScore() + "\t" + sdf.format(scoreList.get(i).getTimeAsDate()).toString() + "\t" + scoreList.get(i).getName() +"\n";
            i++;
        }
        return highscoreString;
    }
}