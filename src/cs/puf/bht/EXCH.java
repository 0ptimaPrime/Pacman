/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.puf.bht;

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
        hs.addScore("Bart",240);
        hs.addScore("Marge",300);
        hs.addScore("Maggie",220);
        hs.addScore("Homer",100);
        hs.addScore("Lisa",270);

        System.out.print(hs.getHighscoreString());
    }
    
}
