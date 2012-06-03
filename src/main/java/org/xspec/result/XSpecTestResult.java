package org.xspec.result;

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
}
