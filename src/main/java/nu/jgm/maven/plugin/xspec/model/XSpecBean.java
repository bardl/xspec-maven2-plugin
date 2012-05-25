package nu.jgm.maven.plugin.xspec.model;

import nu.jgm.maven.plugin.xspec.utils.XSpecStylesheet;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

/**
 * @author Joakim Sundqvist
 */
public class XSpecBean {

    public static final String RESULT_SUFFIX = "-result.xml";
    public static final String REPORT_SUFFIX = "-report.htm";
    public static final String TEST_SUFFIX = "-test.xsl";
    public static final String XSPEC_SUFFIX = ".xspec";

    private final File outputFileTest;
    private final File outputFileResult;
    private final File outputFileReport;
    private final File fileUnderTest;

    private final String name;


    /**
     * @param outputDir output directory for generated files
     * @param xspecFile the xspec file under test
     * @param baseDir the basedir of the xspec tests
     */
    public XSpecBean(final File outputDir, final File xspecFile, final File baseDir) throws IOException, SAXException, ParserConfigurationException {
        name = getFileToTest(xspecFile, baseDir);

        String fileExtension = "";
        if (name.lastIndexOf(".") != -1) {
            fileExtension = name.substring(name.lastIndexOf("."));
        }

        String simpleFilename = new File(name).getName();
        outputFileTest = new File(outputDir, simpleFilename.replace(fileExtension, TEST_SUFFIX));
        outputFileResult = new File(outputDir, simpleFilename.replace(fileExtension, RESULT_SUFFIX));
        outputFileReport = new File(outputDir, simpleFilename.replace(fileExtension, REPORT_SUFFIX));
        this.fileUnderTest = xspecFile;
    }

    private String getFileToTest(File xspecFile, File baseDir) throws SAXException, ParserConfigurationException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
	    SAXParser saxParser = factory.newSAXParser();
        XSpecStylesheet specStylesheet = new XSpecStylesheet();
        saxParser.parse(xspecFile.getCanonicalPath(), specStylesheet);
        return new File(addFolderSuffixIfNeeded(baseDir) + specStylesheet.getStyleSheetToTest()).getCanonicalPath();
    }

    private String addFolderSuffixIfNeeded(File baseDir) throws IOException {
        return baseDir.getCanonicalFile().getAbsolutePath() + "/";
    }


    public File getOutputFileTest() {
        return outputFileTest;
    }

    public File getOutputFileResult() {
        return outputFileResult;
    }

    public File getOutputFileReport() {
        return outputFileReport;
    }

    public File getFileUnderTest() {
        return fileUnderTest;
    }

    public String getFileName() {
        return name.replace(XSPEC_SUFFIX, "");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("XSpecBean {");
        sb.append("\n\t");
        sb.append("outputFileTest=").append(outputFileTest);
        sb.append("\n\t");
        sb.append("outputFileResult=").append(outputFileResult);
        sb.append("\n\t");
        sb.append("outputFileReport=").append(outputFileReport);
        sb.append("\n\t");
        sb.append("fileUnderTest=").append(fileUnderTest);
        sb.append("\n\t");
        sb.append("fileName=").append(name);
        sb.append("\n");
        sb.append('}');
        return sb.toString();
    }
}
