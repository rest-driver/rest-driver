package com.github.restdriver.matchers;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hamcrest.TypeSafeMatcher;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.github.restdriver.matchers.util.HamcrestConverter;
import com.google.common.base.Function;

import static org.hamcrest.Matchers.*;

public class HasXPath {

    private static final HamcrestConverter<Node, String> NODE_TO_STRING_MATCHER = new HamcrestConverter<Node, String>(new Function<String, Node>() {
        @Override
        public Node apply(String s) {
            return asXml(s);
        }
    });

    /**
     * Converts the given string to an XML element.
     *
     * @param xml The XML string to be converted
     * @return The converted element
     */
    public static Element asXml(String xml) {
        final int PARSE_ERROR_EXCERPT_LENGTH = 16;

        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8"))).getDocumentElement();

        } catch (IOException e) {
            throw new RuntimeException("Can't parse XML.  Bad content >> " + xml.substring(0, PARSE_ERROR_EXCERPT_LENGTH) + "...", e);

        } catch (SAXException e) {
            throw new RuntimeException("Can't parse XML.  Bad content >> " + xml.substring(0, PARSE_ERROR_EXCERPT_LENGTH) + "...", e);

        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Can't parse XML.  Bad content >> " + xml.substring(0, PARSE_ERROR_EXCERPT_LENGTH) + "...", e);
        }

    }

    public static TypeSafeMatcher<String> hasStringXPath(String xpath, String value) {

        return NODE_TO_STRING_MATCHER.convert(hasXPath(xpath, is(value)));
    }

    public static TypeSafeMatcher<String> hasStringXPath(String xpath) {

        return NODE_TO_STRING_MATCHER.convert(hasXPath(xpath));
    }
}