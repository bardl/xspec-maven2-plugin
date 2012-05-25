package nu.jgm.maven.plugin.xspec;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import nu.jgm.maven.plugin.xspec.model.XSpecTestResult;
import nu.jgm.maven.plugin.xspec.model.XSpecTestResults;
import nu.jgm.maven.plugin.xspec.utils.MessageHolder;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

/**
 * Goal which runs xspec unit tests as a part of the maven test phase
 *
 * @author Joakim Sundqvist
 * @author Johan M�r�n
 * @goal test
 * @phase test
 * @since 0.1
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
     * Location of the xspec test-files
     *
     * @parameter
     */
    private File xspecDirectory = null;


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
                final XSpecTestResults testResults = new XSpecTestRunner(outputDirectory, xspecDirectory, getLog()).executeTests();
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
