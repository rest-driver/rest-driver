package com.googlecode.rd.clientdriver.unit;

import junit.framework.Assert;

import org.junit.Test;

import com.googlecode.rd.clientdriver.ClientDriver;
import com.googlecode.rd.clientdriver.ClientDriverFactory;

public class ClientDriverFactoryTest {

	@Test
	public void simpleTest() {
		// Hopefully no exceptions here
		Assert.assertEquals(ClientDriver.class, new ClientDriverFactory().createClientDriver().getClass());
	}
}
