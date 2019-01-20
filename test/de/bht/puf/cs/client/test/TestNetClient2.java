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
public class TestNetClient2 extends TestNetClient {

    public TestNetClient2() {
        super("Player2", 450, "0:04:55");
    }
}