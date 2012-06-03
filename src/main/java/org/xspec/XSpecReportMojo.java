package org.xspec;

import net.sf.saxon.TransformerFactoryImpl;
import org.xspec.compiler.XSpecTestFiles;
import org.xspec.compiler.MessageHolder;
import org.xspec.compiler.ZipHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.site.renderer.SiteRenderer;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Collection;
import java.util.Locale;

/**
 * XSpec report generation
 *
 * @execute phase="test"
 * @goal report
 */
public class XSpecReportMojo extends AbstractMavenReport {

    /**
     * @parameter default-value="${project.reporting.outputDirectory}"
     * @required
     */
    protected File outputDirectory;

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @component
     * @required
     * @readonly
     */
    private SiteRenderer siteRenderer;

    /**
     *
     */
    private TransformerFactoryImpl transformerFactory = new TransformerFactoryImpl();

    public XSpecReportMojo() {
        MessageHolder.setIgnoreErrors(true);
    }

    protected String getOutputDirectory() {
        return outputDirectory.getAbsolutePath();
    }

    protected MavenProject getProject() {
        return project;
    }

    @Override
    protected void executeReport(Locale locale) throws MavenReportException {
        // Set up params for the xslt-stylesheet
        final String projectBaseDirURI = project.getBasedir().toURI().toString();
        final String projectBaseDirPath = FilenameUtils.separatorsToUnix(project.getBasedir().getAbsolutePath());
        final String resultDir = projectBaseDirURI + "target/xspec";
        final String reportDir  = new File(getOutputDirectory()).toURI().toString() + "xspec";

        final String resultFiles = toStringList(FileUtils.listFiles(new File(project.getBasedir().getAbsolutePath() + "/target/xspec"), new IOFileFilter() {
            public boolean accept(File file) {
                return test(file);
            }

            public boolean accept(File dir, String name) {
                return test(dir);
            }
            
            private boolean test(File f){
                return f.getName().endsWith(XSpecTestFiles.RESULT_SUFFIX);
            }
        }, null));

        final StringWriter writer = new StringWriter();
        try {
            final Templates templateXspec = transformerFactory.newTemplates(new StreamSource(getClass().getClassLoader().getResourceAsStream("maven-report.xsl")));
            final Transformer transformer = templateXspec.newTransformer();
            transformer.setParameter("resultsDirectoryPath", resultDir);
            transformer.setParameter("projectBaseDir", projectBaseDirPath);
            transformer.setParameter("reportDirectoryPath", reportDir);
            transformer.setParameter("testResultFileList", resultFiles);
            transformer.transform(new StreamSource(new StringReader("<xml/>")), new StreamResult(writer));
        } catch (Exception e) {
            getLog().error(e.getMessage());
            throw new MavenReportException(e.getMessage());
        }

        //  Copy files to target/site/xspec from src/main/resources/site

        final File cssDir = new File(outputDirectory, "css");
        final File imagesDir = new File(outputDirectory, "images");
        ZipHandler.createFolders(cssDir, imagesDir);
        try {
            //IOUtils.copy(getClass().getClassLoader().getResourceAsStream("site/icon_error_sml.gif"), new FileWriter(new File(imagesDir, "icon_error_sml.gif")));
            //IOUtils.copy(getClass().getClassLoader().getResourceAsStream("site/icon_success_sml.gif"), new FileWriter(new File(imagesDir, "icon_success_sml.gif")));
            //IOUtils.copy(getClass().getClassLoader().getResourceAsStream("site/test-report.css"), new FileWriter(new File(cssDir, "xspec-test-report.css")));
            IOUtils.copy(getClass().getClassLoader().getResourceAsStream("site/xspec-test-report.css"), new FileOutputStream(new File(cssDir, "xspec-test-report.css")));
        } catch (IOException e) {
            getLog().error(e.getMessage());
            throw new MavenReportException(e.getMessage());
        }

        getSink().head();
        getSink().title();
        getSink().text("XSpec test reports");
        getSink().title_();
        getSink().head_();

        getSink().body();
        getSink().rawText("<div class=\"section\"><h2>XSpec Test reports</h2></div>");
        getSink().paragraph();
        getSink().rawText(writer.toString());
        getSink().paragraph_();
        getSink().body_();

        getSink().flush();
    }

    private String toStringList(Collection collection) {
        final StringBuilder stringList = new StringBuilder();
        if(collection != null){
            for (Object o : collection) {
                stringList.append(((File) o).getName()).append(" ");
            }
        }
        return stringList.toString().trim();
    }

    protected SiteRenderer getSiteRenderer() {
        return siteRenderer;
    }

    public String getDescription(Locale arg0) {
        return "XSpec test report";
    }

    public String getName(Locale arg0) {
        return "XSpec";
    }

    public String getOutputName() {
        return "xspec-reports";
    }
}
