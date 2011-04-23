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
            throw new RuntimeException("Failed to create XML document", e);

        } catch (SAXException e) {
            throw new RuntimeException("Failed to create XML document", e);

        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Failed to create XML document", e);
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
