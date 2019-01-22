/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bht.puf.cs.client.test;

import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)				
@Suite.SuiteClasses({	
    TestNetClient1.class,
    TestNetClient2.class,     
})

/**
 *
 * @author Christian
 */
public class TestNetClientStart {
    
    public TestNetClientStart() {
    }
    
    /*@Test
    public void runAllTests() {
    Class<?>[] classes = { TestNetClient1.class, TestNetClient2.class };
    
    // ParallelComputer(true,true) will run all classes and methods
    // in parallel.  (First arg for classes, second arg for methods)
    JUnitCore.runClasses(new ParallelComputer(true, false), classes);
    }*/
}

