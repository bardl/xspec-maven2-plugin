package nu.jgm.maven.plugin.xspec.model;

/**
 * @author Joakim Sundqvist
 */
public class XSpecTestResult {

    private int numberOfTests;

    private int failedTests;

    private int skippedTests;

    private String testName;


    public XSpecTestResult(int numberOfTests, int failedTests, int skippedTests, String testName) {
        this.numberOfTests = numberOfTests;
        this.failedTests = failedTests;
        this.skippedTests = skippedTests;
        this.testName = testName;
    }

    public int getSkippedTests() {
        return skippedTests;
    }

    public int getNumberOfTests() {
        return numberOfTests;
    }

    public int getFailedTests() {
        return failedTests;
    }

    public String getTestName() {
        return testName;
    }

    public String getReport(){
        final StringBuilder stringBuilder = new StringBuilder("Running ");
        stringBuilder.append(testName)
                .append("\nTests run: ")
                .append(numberOfTests)
                .append(", Failures: ")
                .append(failedTests)
                .append(", Skipped: ")
                .append(skippedTests);
        if(failedTests > 0){
            stringBuilder.append(" <<< FAILURE");
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("XSpecTestResult");
        sb.append("{numberOfTests=").append(numberOfTests);
        sb.append(", failedTests=").append(failedTests);
        sb.append(", skippedTests=").append(skippedTests);
        sb.append(", testName='").append(testName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
