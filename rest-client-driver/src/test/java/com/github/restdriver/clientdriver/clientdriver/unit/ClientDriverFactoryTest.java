package com.github.restdriver.clientdriver.clientdriver.unit;

import com.github.restdriver.clientdriver.ClientDriver;
import junit.framework.Assert;

import org.junit.Test;

import com.github.restdriver.clientdriver.ClientDriverFactory;

public class ClientDriverFactoryTest {

	@Test
	public void simpleTest() {
		// Hopefully no exceptions here
		Assert.assertEquals(ClientDriver.class, new ClientDriverFactory().createClientDriver().getClass());
	}
}
