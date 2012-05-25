package nu.jgm.maven.plugin.xspec.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joakim Sundqvist
 */
public class XSpecTestResults { 

    private int numberOfTests;
    private int numberOfFailedTests;
    private int numberOfSkippedTests;

    private final List<XSpecTestResult> testResults = new ArrayList<XSpecTestResult>(20);

    public void addTestResult(XSpecTestResult xSpecTestResult){
        testResults.add(xSpecTestResult);
        numberOfTests += xSpecTestResult.getNumberOfTests();
        numberOfFailedTests += xSpecTestResult.getFailedTests();
        numberOfSkippedTests += xSpecTestResult.getSkippedTests();
    }

    public int getNumberOfTests(){
        return numberOfTests;
    }

    public boolean hasFailedTests(){
        return numberOfFailedTests > 0;
    }

    public int getNumberOfFailedTests(){
        return numberOfFailedTests;
    }

     public int getNumberOfSkippedTests(){
        return numberOfSkippedTests;
    }

    public List<XSpecTestResult> getTestResults() {
        return testResults;
    }

    public String getReport(long executionTime){
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nResults :\n\n")
                .append("Tests run: ")
                .append(getNumberOfTests())
                .append(", Failures: ")
                .append(getNumberOfFailedTests())
                .append(", Skipped: ")
                .append(getNumberOfSkippedTests())
                .append(", Elapsed time: ")
                .append(executionTime / 1000.0d)
                .append(" sec.\n");
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("XSpecTestResults");
        sb.append("{testResults=").append(testResults);
        sb.append('}');
        return sb.toString();
    }
}
