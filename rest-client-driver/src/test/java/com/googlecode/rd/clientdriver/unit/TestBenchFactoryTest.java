package com.googlecode.rd.clientdriver.unit;

import junit.framework.Assert;

import org.junit.Test;

import com.googlecode.rd.clientdriver.BenchServer;
import com.googlecode.rd.clientdriver.TestBenchFactory;

public class TestBenchFactoryTest {

	@Test
	public void simpleTest() {
		// Hopefully no exceptions here
		Assert.assertEquals(BenchServer.class, new TestBenchFactory().createBenchServer().getClass());
	}
}
