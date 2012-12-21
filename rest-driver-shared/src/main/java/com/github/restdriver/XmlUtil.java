package com.github.restdriver;

import com.github.restdriver.exception.RuntimeXmlParseException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public final class XmlUtil {

    private XmlUtil() {
    }

    private static final int PARSE_ERROR_EXCERPT_LENGTH = 16;

    private static Element throwRuntimeXmlParseException(String xml, Exception e) {
        throw new RuntimeXmlParseException("Can't parse XML.  Bad content >> " + xml.substring(0, PARSE_ERROR_EXCERPT_LENGTH) + "...", e);
    }

    /**
     * Converts the given string to an XML element.
     *
     * @param xml The XML string to be converted
     * @return The converted element
     */
    public static Element asXml(String xml) {

        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8"))).getDocumentElement();

        } catch (IOException e) {
            return throwRuntimeXmlParseException(xml, e);

        } catch (SAXException e) {
            return throwRuntimeXmlParseException(xml, e);

        } catch (ParserConfigurationException e) {
            return throwRuntimeXmlParseException(xml, e);
        }

    }

}
