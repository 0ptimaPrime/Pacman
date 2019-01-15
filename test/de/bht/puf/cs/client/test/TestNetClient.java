package de.bht.puf.cs.client.test;

import de.bht.puf.cs.Score;
import de.bht.puf.cs.client.NetClient;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit Tests for NetClient
 * 
 * @version 1.0
 * @author Christian Schroeter
 */
public class TestNetClient {
    
    public TestNetClient() {
    }

    
    /**
     * Test to send a new Score to NetServer in both valid options
     */
    @Test
    public void TestSendScore () {
        String name = "Player";
        int score = 300;
        String time = "0:05:25";
        
        assertTrue(NetClient.sendScore(name, score, time));
        score = 310;
        assertTrue(NetClient.sendScore((new Score(name, score, time))));
    }
    
    /**
     * Test to request HighSCore from NetServer
     */
    @Test
    public void TestGetHighscore () {
        String hs;
        
        hs = NetClient.getHighscore();
        assertTrue(!hs.isEmpty());
        System.out.print(hs);
    }
}
