/**
 * Copyright © 2010-2011 Nokia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.restdriver.clientdriver.unit;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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
