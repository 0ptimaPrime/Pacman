/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.puf.bht;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Christian
 */
public class Highscore {
    // An arraylist of the type "score" we will use to work with the scores inside the class
    private ArrayList<Score> scores;

    // The name of the file where the highscores will be saved
    private static final String HIGHSCORE_FILE = "scores.dat";
    
    static int maxScoresCount = 10;

    //Initialising an in and outputStream for working with the file
    ObjectOutputStream outputStream = null;
    ObjectInputStream inputStream = null;

    public Highscore() {
        //initialising the scores-arraylist
        scores = new ArrayList<Score>();
    }
    
    public ArrayList<Score> getScores() throws IOException {
        loadScoreFile();
        sort();
        return scores;
    }
    
    private void sort() {
        ScoreComp comparator = new ScoreComp();
        Collections.sort(scores, comparator);
    }
    
    public void addScore(String name, int score) throws IOException {
        loadScoreFile();
        scores.add(new Score(name, score));
        updateScoreFile();
    }
    
    public void loadScoreFile() throws IOException {
        try {
            inputStream = new ObjectInputStream(new FileInputStream(HIGHSCORE_FILE));
            scores = (ArrayList<Score>) inputStream.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("[Laad] FNF Error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("[Laad] IO Error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("[Laad] CNF Error: " + e.getMessage());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException e) {
                System.out.println("[Laad] IO Error: " + e.getMessage());
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
    
    public String getHighscoreString() throws IOException {
        String highscoreString = "";
        
        ArrayList<Score> scores;
        scores = getScores();

        int i = 0;
        int x = scores.size();
        if (x > maxScoresCount) {
            x = maxScoresCount;
        }
        while (i < x) {
            highscoreString += (i + 1) + ".\t" + scores.get(i).getName() + "\t\t" + scores.get(i).getScore() + "\n";
            i++;
        }
        return highscoreString;
    }
}