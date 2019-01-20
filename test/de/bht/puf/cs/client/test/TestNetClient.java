package de.bht.puf.cs.client.test;

import de.bht.puf.cs.Score;
import de.bht.puf.cs.client.NetClient;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 * JUnit Tests for NetClient
 * 
 * @since JUnit 4
 * @version 1.0
 * @author Christian Schroeter
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestNetClient {
    
    //params for client Score
    String name;
    int score;
    String time;
    
    static NetClient client;
    
    public TestNetClient(String name, int score, String time) {
        this.name = name;
        this.score = score;
        this.time = time;
    }
    
    @BeforeClass
    public static void Test0_setUp() {
        client = new NetClient();
    }
    
    /**
     * Test to send a new Score to NetServer with params
     */
    @Test
    public void Test1_sendScoreWithParams () {
        assertTrue(client.sendScore(name, score, time));
    }
    
    /**
     * Test to send a new Score to NetServer as Score
     */
    @Test
    public void Test2_sendScore () {
        assertTrue(client.sendScore((new Score(name, score, time))));
    }
    
    /**
     * Test to request HighSCore from NetServer
     */
    @Test
    public void Test3_getHighscore () {
        String hs;
        
        hs = client.getHighscore();
        assertTrue(!hs.isEmpty());
        System.out.print(hs + "\n\n");
    }
    
    @AfterClass
    public static void Test4_cleanUp () {
        assertTrue(client.closeSession());
    }
}
