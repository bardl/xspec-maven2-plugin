package org.xspec.compiler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XSpecStylesheet extends DefaultHandler {

    private String styleSheetToTest;

    public String getStyleSheetToTest() {
        return styleSheetToTest;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (removeNamespace(qName).equals("description")) {
             styleSheetToTest = attributes.getValue("stylesheet");
        }
    }

    private String removeNamespace(String qName) {
        if (qName.indexOf(":") != -1) {
            return qName.substring(qName.indexOf(":")+1);
        } else {
            return qName;
        }
    }
}
