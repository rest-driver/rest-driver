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
package com.github.restdriver.serverdriver;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.github.restdriver.serverdriver.http.exception.RuntimeMappingException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Provides static helper methods for XML representations.
 * 
 * @author mjg
 */
public final class Xml {

    private Xml() {
    }

    /**
     * Converts the given string to an XML element.
     * 
     * @param xml The XML string to be converted
     * @return The converted element
     */
    public static Element asXml(String xml) {

        try {
            Element document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8"))).getDocumentElement();
            return document;

        } catch (IOException e) {
            throw new RuntimeMappingException("Failed to create XML document", e);

        } catch (SAXException e) {
            throw new RuntimeMappingException("Failed to create XML document", e);

        } catch (ParserConfigurationException e) {
            throw new RuntimeMappingException("Failed to create XML document", e);
        }

    }

    /**
     * Extracts an XPath value from an XML element and returns the result as a string.
     * 
     * @param expression The XPath expression to use for extraction
     * @param element The element to use the XPath expression on
     * @return The result of evaluating the XPath expression on the element
     */
    public static String extractXPathValue(String expression, Element element) {

        XPath xPath = XPathFactory.newInstance().newXPath();

        XPathExpression compiledXPath;

        try {
            compiledXPath = xPath.compile(expression);

        } catch (XPathExpressionException e) {
            throw new RuntimeException("Failed to compile XPath '" + expression + "'", e);

        }

        try {
            return compiledXPath.evaluate(element, XPathConstants.STRING).toString();

        } catch (XPathExpressionException e) {
            throw new RuntimeException("Failed to evaluate XPath '" + expression + "'", e);
        }

    }

}
