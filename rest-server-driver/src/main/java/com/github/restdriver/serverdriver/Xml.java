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

public final class Xml {

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
