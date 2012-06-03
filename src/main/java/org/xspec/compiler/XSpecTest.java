package org.xspec.compiler;

import net.sf.saxon.Controller;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.om.NamespaceResolver;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;
import net.sf.saxon.trans.XPathException;
import org.xspec.result.XSpecTestResult;
import org.xspec.result.XSpecTestResults;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.xml.sax.InputSource;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

public class XSpecTest {


    private static final String XSPEC_MAIN = "{http://www.jenitennison.com/xslt/xspec}main";


    /*
       Default to saxon9
    */
    private TransformerFactoryImpl transformerFactory = new TransformerFactoryImpl();

    /*
      Create a temp folder for xspec-files
     */
    private final File tempDir = new File(System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID());

    private final File xspecTestGenFile;

    private final File xspecReportGenFile;

    private final File outputDir;

    private final File xspecDirectory;

    private final File xsltDirectory;

    private final StreamSource junitXslt;

    private File junitReportsDir = null;

    private Log log;


    public XSpecTest(File outputDir, File xspecDirectory, File xsltDirectory, Log log) {
        this(outputDir, xspecDirectory, null, xsltDirectory, log);
    }

    public XSpecTest(File outputDir, File xspecDirectory, File junitReportsDir, File xsltDirectory, Log log) {
        this.outputDir = outputDir;
        this.xspecDirectory = xspecDirectory;
        this.xsltDirectory = xsltDirectory;
        this.xspecTestGenFile = new File(tempDir, "xspec-0.3.0/src/compiler/generate-xspec-tests.xsl");
        this.xspecReportGenFile = new File(tempDir, "xspec-0.3.0/src/reporter/format-xspec-report.xsl");
        this.junitXslt = new StreamSource(getClass().getClassLoader().getResourceAsStream("junit-report.xsl"));
        this.junitReportsDir = junitReportsDir;
        this.log = log;
    }

    private void runTests(final XSpecTestFiles xSpecBean) throws Exception {
        Source xspecXsl = new StreamSource(xspecTestGenFile);
        Source xspecReportXml = new StreamSource(xspecReportGenFile);
        Source xspecTestFile = new StreamSource(xSpecBean.getFileUnderTest());

        Templates templateXspec = transformerFactory.newTemplates(xspecXsl);

        
        //transforms xspec file to executable xslt test file
        Transformer transformerTestFile = templateXspec.newTransformer();
        transformerTestFile.setParameter("xslt_dir", xsltDirectory.getCanonicalPath().replaceAll("(\\\\)", "/") + "/");
        transformerTestFile.transform(xspecTestFile, new StreamResult(xSpecBean.getOutputFileTest()));

        //transformation executing the test with the given initial template to the results file
        Transformer testExecTransformer = transformerFactory.newTransformer(new StreamSource(xSpecBean.getOutputFileTest()));
        ((Controller) testExecTransformer).setInitialTemplate(XSPEC_MAIN);
        testExecTransformer.transform(xspecXsl, new StreamResult(xSpecBean.getOutputFileResult()));

        if (junitReportsDir != null) {
            final Templates junitTemplates = transformerFactory.newTemplates(junitXslt);
            final Transformer transformer = junitTemplates.newTransformer();
            transformer.transform(new StreamSource(xSpecBean.getOutputFileResult()), new StreamResult(new File(junitReportsDir, "TEST-unit-unit-" + xSpecBean.getFileName() + ".xml")));
        } else {
            Templates templateReport = transformerFactory.newTemplates(xspecReportXml);
            //transforms the result of the test execution to a xhtml report
            Transformer transformerReport = templateReport.newTransformer();
            transformerReport.transform(new StreamSource(xSpecBean.getOutputFileResult()), new StreamResult(xSpecBean.getOutputFileReport()));
        }
    }


    public XSpecTestResults executeTests() throws Exception {

        final XSpecTestResults testResults = new XSpecTestResults();
        //transformerFactory.getConfiguration().setMessageEmitterClass(nu.jgm.maven.plugin.xspec.utils.NullMessageEmitter.class.getName());

        final File outputFolder = new File(outputDir, "xspec");

        //  Create temp folder
        ZipHandler.createFolders(outputFolder, tempDir);

        try {
            ZipHandler.unzipFile(tempDir, ZipHandler.createTempZipFile("xspec-0.3.0", "zip"));
            final Collection collection = FileUtils.listFiles(xspecDirectory, new String[]{"xspec"}, true);
            if (collection != null && !collection.isEmpty()) {
                for (Object file : collection) {
                    runTests(new XSpecTestFiles(outputFolder, (File) file, xspecDirectory, xsltDirectory));
                }

                // Examine result-files and look for failures
                parseResultFiles(outputFolder, testResults);

            } else {
                writeDebug("There are no tests to run.");
            }
            return testResults;

        } finally {
            FileUtils.deleteQuietly(tempDir);
        }
    }

    private void parseResultFiles(File outputFolder, XSpecTestResults testResults) throws MalformedURLException, XPathException, FileNotFoundException {
        final File[] files = outputFolder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(XSpecTestFiles.RESULT_SUFFIX);
            }
        });

        if (files != null) {
            for (File file : files) {
                final XSpecTestResult result = parseXSpecResultFile(file);


                testResults.addTestResult(result);
            }
        }
    }

    private XSpecTestResult parseXSpecResultFile(File file) throws MalformedURLException, XPathException {
        //  Open file
        final String systemId = file.toURI().toURL().toString();
        writeDebug("Scanning xspec file [" + systemId + "].");

        final SAXSource saxSource = new SAXSource(new InputSource(systemId));
        final XPathEvaluator evaluator = new XPathEvaluator();
        evaluator.setNamespaceResolver(new NamespaceResolver() {
            public String getURIForPrefix(String s, boolean b) {
                return s.equals("x") ? "http://www.jenitennison.com/xslt/xspec" : null;
            }

            public Iterator iteratePrefixes() {
                return null;
            }
        });
        // count number of tests
        final XPathExpression countTestExpression = evaluator.createExpression("count(//x:test)");
        final Long testCount = (Long) countTestExpression.evaluateSingle(saxSource);

        // Count number of failed tests
        final XPathExpression failedExpression = evaluator.createExpression("count(//x:test[@successful = 'false'])");
        final Long failedCount = (Long) failedExpression.evaluateSingle(saxSource);

        // Count number of skipped tests
        final XPathExpression skippedTestExpression = evaluator.createExpression("count(//x:test[@successful = 'skipped'])");
        final Long skippedCount = (Long) skippedTestExpression.evaluateSingle(saxSource);
        final XSpecTestResult result = new XSpecTestResult(testCount.intValue(), failedCount.intValue(), skippedCount.intValue(), file.getName().replace(XSpecTestFiles.XSPEC_SUFFIX, ""));
        return result;
    }

    private void writeDebug(String output) {
        log.debug(output);
    }
}
