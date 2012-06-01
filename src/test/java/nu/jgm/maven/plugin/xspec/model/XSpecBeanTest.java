package nu.jgm.maven.plugin.xspec.model;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: bardl
 * Date: 2012-maj-16
 * Time: 22:51:37
 * To change this template use File | Settings | File Templates.
 */
public class XSpecBeanTest {

    @Test
    public void createXSpecBean() throws IOException, SAXException, ParserConfigurationException {
        String outputDirectory = "outputdir";
        String resource = new File(getClass().getClassLoader().getResource("test_xslt/escape-for-regex.xspec").getFile()).getCanonicalPath();
        XSpecBean xSpecBean = new XSpecBean(new File(outputDirectory), new File(resource), new File("test/resources/test_xslt"), null);
        Assert.assertEquals("Wrong path for xspec-file", resource, xSpecBean.getFileUnderTest().getCanonicalPath());
        Assert.assertEquals("Wrong filename for xslt file to test", new File("test/resources/test_xslt/escape-for-regex.xslt").getCanonicalPath(), xSpecBean.getFileName());
        Assert.assertEquals("Wrong filename for report file", new File(outputDirectory + "/escape-for-regex-report.htm").getCanonicalPath(), xSpecBean.getOutputFileReport().getCanonicalPath());
        Assert.assertEquals("Wrong filename for result file", new File(outputDirectory + "/escape-for-regex-result.xml").getCanonicalPath(), xSpecBean.getOutputFileResult().getCanonicalPath());
        Assert.assertEquals("Wrong filename output file to test", new File(outputDirectory + "/escape-for-regex-test.xsl").getCanonicalPath(), xSpecBean.getOutputFileTest().getCanonicalPath());
        Assert.assertEquals("Wrong filename output file to test", new File(outputDirectory + "/escape-for-regex-test.xsl").getCanonicalPath(), xSpecBean.getOutputFileTest().getCanonicalPath());        
    }
}
