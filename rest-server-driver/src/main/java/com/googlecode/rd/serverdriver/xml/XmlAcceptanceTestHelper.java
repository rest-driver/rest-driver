package com.googlecode.rd.serverdriver.xml;

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

public final class XmlAcceptanceTestHelper {

	public static Element asXml(final String xml) {

		try {
			final Element document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8"))).getDocumentElement();
			return document;
		} catch (final IOException e) {
			throw new RuntimeException("Failed to create XML document", e);
		} catch (final SAXException e) {
			throw new RuntimeException("Failed to create XML document", e);
		} catch (final ParserConfigurationException e) {
			throw new RuntimeException("Failed to create XML document", e);
		}

	}

	public static String extractXPathValue(final String expression, final Element element) {

		final XPath xPath = XPathFactory.newInstance().newXPath();

		final XPathExpression compiledXPath;

		try {
			compiledXPath = xPath.compile(expression);
		} catch (final XPathExpressionException e) {
			throw new RuntimeException("Failed to compile XPath '" + expression + "'", e);
		}

		try {
			return compiledXPath.evaluate(element, XPathConstants.STRING).toString();
		} catch (final XPathExpressionException e) {
			throw new RuntimeException("Failed to evaluate XPath '" + expression + "'", e);
		}

	}

}
