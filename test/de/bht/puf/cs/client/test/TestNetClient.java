package de.bht.puf.cs.client.test;

import de.bht.puf.cs.Score;
import de.bht.puf.cs.client.NetClient;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestNetClient {
    
    public TestNetClient() {
    }

    
    @Test
    public void TestSendScore () {
        String name = "Player";
        int score = 300;
        String time = "0:05:35";
        
        assertTrue(NetClient.sendScore(name, score, time));
        assertTrue(NetClient.sendScore((new Score(name, score, time))));
    }
    
    @Test
    public void TestGetHighscore () {
        String hs;
        
        hs = NetClient.getHighscore();
        assertTrue(!hs.isEmpty());
        System.out.print(hs);
    }
}
