package com.github.restdriver.clientdriver.unit;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.github.restdriver.SocketUtil;
import com.github.restdriver.clientdriver.ClientDriver;
import com.github.restdriver.clientdriver.SecureClientDriver;
import com.github.restdriver.clientdriver.SecureClientDriverFactory;
import com.github.restdriver.clientdriver.integration.SecureClientDriverTest;

public class SecureClientDriverFactoryTest {

    @Test
    public void createSecureClientDriverWithGivenPort() throws Exception {

        // Arrange
        SecureClientDriverFactory factory = new SecureClientDriverFactory();

        // Act
        ClientDriver driver = factory.createClientDriver(SocketUtil.getFreePort(), getKeystore(), "password",
                "certificate");

        // Assert
        assertThat(driver, instanceOf(ClientDriver.class));
        assertThat(driver, instanceOf(SecureClientDriver.class));

    }

    @Test
    public void createSecureClientDriverWithRandomPort() throws Exception {

        // Arrange
        SecureClientDriverFactory factory = new SecureClientDriverFactory();

        // Act
        ClientDriver driver = factory.createClientDriver(getKeystore(), "password", "certificate");

        // Assert
        assertThat(driver, instanceOf(ClientDriver.class));
        assertThat(driver, instanceOf(SecureClientDriver.class));

    }

    @Test
    public void createSecureClientDriverWithRandomPortUsingFluentApi() throws Exception {

        // Arrange
        SecureClientDriverFactory factory = new SecureClientDriverFactory();

        // Act
        ClientDriver driver = factory.keyStore(getKeystore()).password("password").certAlias("certificate").build();

        // Assert
        assertThat(driver, instanceOf(ClientDriver.class));
        assertThat(driver, instanceOf(SecureClientDriver.class));

    }

    @Test
    public void createSecureClientDriverWithGivenPortUsingFluentApi() throws Exception {

        // Arrange
        SecureClientDriverFactory factory = new SecureClientDriverFactory();

        // Act
        ClientDriver driver = factory.keyStore(getKeystore()).password("password").certAlias("certificate")
                .port(SocketUtil.getFreePort()).build();

        // Assert
        assertThat(driver, instanceOf(ClientDriver.class));
        assertThat(driver, instanceOf(SecureClientDriver.class));

    }

    static KeyStore getKeystore() throws Exception {
        ClassLoader loader = SecureClientDriverTest.class.getClassLoader();
        byte[] binaryContent = IOUtils.toByteArray(loader.getResourceAsStream("keystore.jks"));
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new ByteArrayInputStream(binaryContent), "password".toCharArray());
        return keyStore;
    }

}
