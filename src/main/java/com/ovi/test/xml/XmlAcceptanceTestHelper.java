package com.ovi.test.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

}
