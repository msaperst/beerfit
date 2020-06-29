package com.fatmax.beerfit;

import com.testpros.fast.reporter.Step.Status;

import org.apache.commons.io.FileUtils;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.fatmax.beerfit.AppiumTestBase.testResults;

public class ExecutionListener extends RunListener {

    List<String> testCases = new ArrayList<>();
    String testResultTemplate = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \n" +
            "\"http://www.w3.org/TR/html4/loose.dtd\">\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
            "<style>.title { background:lightgrey; } .PASS { background:darkseagreen; } .FAIL { background:lightcoral; } table { border:2px solid darkgrey; border-collapse:collapse; } td,th { border:1px solid grey; padding:10px; }</style>\n" +
            "<title>$testSuiteName Overall Results</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>$testSuiteName Overall Results</h1>\n" +
            "<table>\n" +
            "<tr class='title'><th>Total Tests Run</th><th>Passed</th><th>Failed</th><th>Ignored</th><th>Total Time</th></tr>\n" +
            "<tr class='$overallResult'><td>$totalTests</td><td>$testsPassed</td><td>$testFailed</td><td>$testsIgnored</td><td>$totalTime</td></tr>\n" +
            "</table>\n" +
            "<h2>Individual Test Results</h2>\n" +
            "<table>\n" +
            "<tr class='title'><th>Test Name</th><th>Status</th><th>Reports</th></tr>\n" +
            "$testResults" +
            "</table>\n" +
            "</body>\n" +
            "</html>";

    /**
     * Called before any tests have been run.
     */
    @Override
    public void testRunStarted(Description description) {
        System.out.println("Number of tests to execute : " + description.testCount());
    }

    /**
     * Called when all tests have finished
     */
    @Override
    public void testRunFinished(Result result) throws IOException {
        System.out.println("Number of tests executed : " + result.getRunCount());
        Status overallStatus;
        if (result.getFailureCount() > 0) {
            overallStatus = Status.FAIL;
        } else {
            overallStatus = Status.PASS;
        }
        // TODO - fix testSuiteName
        String report = testResultTemplate.replace("$testSuiteName", "Test Suite")
                .replace("$overallResult", overallStatus.toString())
                .replace("$totalTests", String.valueOf(result.getRunCount()))
                .replace("$testsPassed", String.valueOf(result.getRunCount() - result.getFailureCount() - result.getIgnoreCount()))
                .replace("$testFailed", String.valueOf(result.getFailureCount()))
                .replace("$testsIgnored", String.valueOf(result.getIgnoreCount()))
                .replace("$totalTime", String.valueOf(result.getRunTime()) + " ms")
                .replace("$testResults", "")
                .replaceAll("\\$(.*?)Status", "PASS");
        File reportFile = new File(testResults, "index.html");
        FileUtils.writeStringToFile(reportFile, report, Charset.defaultCharset());
    }

    /**
     * Called when an atomic test is about to be started.
     */
    @Override
    public void testStarted(Description description) {
        System.out.println("Starting execution of test case : " + description.getMethodName());
    }

    private void recordTestRun(String testCaseName, Status status) {
        String stringStatus;
        if (status == null) {
            stringStatus = "$" + testCaseName + "Status";
        } else {
            stringStatus = status.toString();
        }
        if (!testCases.contains(testCaseName)) {
            testResultTemplate = testResultTemplate.replace("$testResults",
                    "<tr class=" + stringStatus + ">" +
                            "<td>" + testCaseName + "</td>" +
                            "<td>" + stringStatus + "</td>" +
                            "<td><a href='" + testCaseName + ".html'>Selenium</a></td></tr>\n$testResults");
            testCases.add(testCaseName);
        }
    }

    /**
     * Called when an atomic test has finished, whether the test succeeds or fails.
     */
    @Override
    public void testFinished(Description description) {
        System.out.println("Finished execution of test case : " + description.getMethodName());
        recordTestRun(description.getMethodName(), null);
    }

    /**
     * Called when an atomic test fails.
     */
    @Override
    public void testFailure(Failure failure) {
        System.out.println("Execution of test case failed : " + failure.getMessage());
        recordTestRun(failure.getDescription().getMethodName(), Status.FAIL);
    }

    /**
     * Called when a test will not be run, generally because a test method is annotated with Ignore.
     */
    @Override
    public void testIgnored(Description description) {
        System.out.println("Execution of test case ignored : " + description.getMethodName());
        //TODO - change to skip
        recordTestRun(description.getMethodName(), Status.CHECK);

    }
}
