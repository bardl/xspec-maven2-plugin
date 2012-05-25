package nu.jgm.maven.plugin.xspec.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: bardl
 * Date: 2012-maj-19
 * Time: 10:14:29
 * To change this template use File | Settings | File Templates.
 */
public class XSpecTestResultsTest {

    @Test
    public void validateNumberOfTestsInXSpecTestResults() {
        XSpecTestResults xSpecTestResult = initiateXSpecTestResults();
        Assert.assertEquals("Assert that number of tests are correct", xSpecTestResult.getNumberOfTests(), 9);
        Assert.assertEquals("Assert that number of skipped tests are correct", xSpecTestResult.getNumberOfSkippedTests(), 2);
        Assert.assertEquals("Assert that number of failed tests are correct", xSpecTestResult.getNumberOfFailedTests(), 3);
    }

    @Test
    public void createXSpecTestResultsReport() {
        XSpecTestResults xSpecTestResult = initiateXSpecTestResults();
        Assert.assertEquals("Assert that number of tests are correct", xSpecTestResult.getReport(10000),
                "\nResults :\n\nTests run: 9, Failures: 3, Skipped: 2, Elapsed time: 10.0 sec.\n");
    }

    @Test
    public void xSpecTestResultsWithFailedTestsShouldFlagThatIsHasFailed() {
        XSpecTestResults xSpecTestResult = initiateXSpecTestResults();
        Assert.assertTrue("Assert that test results have failed", xSpecTestResult.hasFailedTests());
    }

    @Test
    public void xSpecTestResultsWithNoFailedTestsShouldNotFlagThatIsHasFailed() {
        XSpecTestResults xSpecTestResult = new XSpecTestResults();
        xSpecTestResult.addTestResult(new XSpecTestResult(3, 0, 1, "test1"));
        xSpecTestResult.addTestResult(new XSpecTestResult(4, 0, 0, "test2"));
        xSpecTestResult.addTestResult(new XSpecTestResult(2, 0, 1, "test3"));
        Assert.assertFalse("Assert that test results have failed", xSpecTestResult.hasFailedTests());
    }

    private XSpecTestResults initiateXSpecTestResults() {
        XSpecTestResults xSpecTestResult = new XSpecTestResults();
        xSpecTestResult.addTestResult(new XSpecTestResult(3, 1, 1, "test1"));
        xSpecTestResult.addTestResult(new XSpecTestResult(4, 2, 0, "test2"));
        xSpecTestResult.addTestResult(new XSpecTestResult(2, 0, 1, "test3"));
        return xSpecTestResult;
    }
}
