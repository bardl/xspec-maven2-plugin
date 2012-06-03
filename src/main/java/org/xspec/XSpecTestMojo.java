package org.xspec;

import org.xspec.compiler.MessageHolder;
import org.xspec.compiler.XSpecTest;
import org.xspec.result.XSpecTestResult;
import org.xspec.result.XSpecTestResults;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

/**
 * Goal which runs xspec unit tests as a part of the maven test phase
 *
 * @goal test
 * @phase test
 */
public class XSpecTestMojo extends AbstractMojo {

    /**
     * Location of the file.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    /**
     * Location of the file.
     *
     * @parameter expression="${basedir}"
     * @required
     */
    private File baseDirectory;

    /**
     * Location of the xspec test-files
     *
     * @parameter
     */
    private File xspecDirectory = null;

    /**
     * Location of the xslt files.
     *
     * @parameter
     */
    private File xsltDirectory = null;


    /**
     * Reuse the surefire skip test flag.
     * <p/>
     * Skips the xspec-tests if this is set to true
     *
     * @parameter expression="${maven.test.skip}"
     */
    private boolean skip = false;
    

    public void execute() throws MojoExecutionException, MojoFailureException {
        checkMandatoryParameters();

        if (skip) {
            getLog().info("Tests are skipped.");
        } else {
            try {
                long start = System.currentTimeMillis();
                final XSpecTestResults testResults = new XSpecTest(outputDirectory, xspecDirectory, xsltDirectory, getLog()).executeTests();
                writeOutput(testResults, System.currentTimeMillis() - start);

                if (testResults.hasFailedTests() && !MessageHolder.isIgnoreErrors()) {
                    throw new MojoFailureException("Tests failed");
                }
            } catch (MojoFailureException me) {
                throw me;
            } catch (Exception e) {
                getLog().error(e.getMessage());
                throw new MojoExecutionException(e.getMessage());
            }
        }
    }

    private void writeOutput(XSpecTestResults testResults, long executionTime) {
        final File outputFolder = new File(outputDirectory, "xspec");

        getLog().info("XSpec report directory: " + outputFolder.getAbsolutePath());
        getLog().info("XSpec directory: " + xspecDirectory.getAbsolutePath());

        StringBuilder result = new StringBuilder();
        result.append("\n-------------------------------------------------------\n");
        result.append("X S P E C - T E S T S");
        result.append("\n-------------------------------------------------------\n");

        for (XSpecTestResult testResult : testResults.getTestResults()) {
            result.append(testResult.getReport());
        }

        result.append(testResults.getReport(executionTime));

        if (testResults.hasFailedTests() && !MessageHolder.isIgnoreErrors()) {
            result.append("There are test failures.");
            result.append("Please refer to " + outputFolder.getAbsolutePath() + " for the individual test results.");
        }

        getLog().info(result.toString());
    }

    private void checkMandatoryParameters() throws MojoFailureException {
        if (xspecDirectory == null) {
            throw new MojoFailureException("Mandatory parameter [xspecDirectory] is not specified for xspec maven plugin. " +
                    "\n\nExample:\n\n" +
                    "<plugin>\n" +
                    "\t...\n" +
                    "\t<configuration>\n" +
                    "\t\t<xspecDirectory>src/test/xspec</xspecDirectory>\n" +
                    "\t</configuration>\n" +
                    "\t...\n" +
                    "</plugin>\n\n");
        }
    }
}
