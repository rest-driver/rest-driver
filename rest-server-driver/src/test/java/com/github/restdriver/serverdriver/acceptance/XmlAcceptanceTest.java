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
package com.github.restdriver.serverdriver.acceptance;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import com.github.restdriver.XmlUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.restdriver.serverdriver.Xml;

public class XmlAcceptanceTest {
    
    private static final String XML = "<person><name><first>John</first><surname>Does</surname></name></person>";
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void extractXPathValueExtractsSuccessfully() {
        assertThat(Xml.extractXPathValue("/person/name/first", XmlUtil.asXml(XML)), is("John"));
    }
    
    @Test
    public void extractXPathValueThrowsRuntimeExceptionOnBadXPath() {
        thrown.expect(RuntimeException.class);
        Xml.extractXPathValue("*&**gibberish", XmlUtil.asXml(XML));
    }
    
}
