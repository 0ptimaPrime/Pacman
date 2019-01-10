/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.puf.bht;

import de.bht.puf.cs.server.Highscore;
import java.io.IOException;

/**
 *
 * @author Christian
 */
public class EXCH {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Highscore hs = new Highscore();
        hs.addScore("Bart",240,"0:05:23");

        System.out.print(hs.getHighscoreString());
    }
    
}
