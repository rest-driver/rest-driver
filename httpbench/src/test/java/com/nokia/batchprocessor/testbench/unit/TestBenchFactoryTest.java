package com.nokia.batchprocessor.testbench.unit;

import junit.framework.Assert;

import org.junit.Test;

import com.nokia.batchprocessor.testbench.BenchServer;
import com.nokia.batchprocessor.testbench.TestBenchFactory;

public class TestBenchFactoryTest {

    @Test
    public void simpleTest() {
        // Hopefully no exceptions here
        Assert.assertEquals(BenchServer.class, new TestBenchFactory().createBenchServer().getClass());
    }
}
