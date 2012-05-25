package nu.jgm.maven.plugin.xspec.utils;

import net.sf.saxon.expr.SlashExpression;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: bardl
 * Date: 2012-maj-16
 * Time: 18:56:05
 * To change this template use File | Settings | File Templates.
 */
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
