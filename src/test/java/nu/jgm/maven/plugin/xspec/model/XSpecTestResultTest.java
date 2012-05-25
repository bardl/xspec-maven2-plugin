package nu.jgm.maven.plugin.xspec.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: bardl
 * Date: 2012-maj-19
 * Time: 09:45:05
 * To change this template use File | Settings | File Templates.
 */
public class XSpecTestResultTest {

    @Test
    public void createXSpecTestResult() {
        XSpecTestResult xSpecTestResult = new XSpecTestResult(2, 1, 0, "nameOfTest");
        Assert.assertEquals("Wrong number of run tests", xSpecTestResult.getNumberOfTests(), 2);
        Assert.assertEquals("Wrong number of failed tests", xSpecTestResult.getFailedTests(), 1);
        Assert.assertEquals("Wrong number of skipped tests", xSpecTestResult.getSkippedTests(), 0);
        Assert.assertEquals("Wrong name of test", xSpecTestResult.getTestName(), "nameOfTest");
    }

    @Test
    public void createXSpecTestResultReport() {
        XSpecTestResult xSpecTestResult = new XSpecTestResult(2, 1, 0, "nameOfTest");
        Assert.assertEquals("Test result report of xspec test is incorrect", xSpecTestResult.getReport(), "Running nameOfTest\nTests run: 2, Failures: 1, Skipped: 0 <<< FAILURE\n");
    }
}
