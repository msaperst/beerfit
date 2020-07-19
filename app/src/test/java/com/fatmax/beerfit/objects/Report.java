package com.fatmax.beerfit.objects;

import com.testpros.fast.WebDriver;
import com.testpros.fast.reporter.Reporter;
import com.testpros.fast.reporter.Step;
import com.testpros.fast.reporter.Step.Status;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Capabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class Report {


    private static final Logger log = LoggerFactory.getLogger(Report.class);

    final static String testCaseTemplate = "https://raw.githubusercontent.com/msaperst/beerfit/feature/parallelTesting/app/src/test/resources/testCaseTemplate.html";
    final static String testResultTemplate = "https://raw.githubusercontent.com/msaperst/beerfit/feature/parallelTesting/app/src/test/resources/testResultTemplate.html";
    public final static File testResults = new File("build/reports/tests");

    public static void addTestCase(Map<String, Reporter> testsExecuted, WebDriver driver, String methodName) {
        if (testsExecuted.containsKey(methodName)) {
            log.error("Test case name '" + methodName + "' a duplicate! This will mess up your reports");
        }
        testsExecuted.put(methodName, driver.getReporter());
    }

    public static String convertStackTrace(Throwable throwable) {
        //TODO - toggle hiding stack trace
        StringBuilder stringBuilder = new StringBuilder();
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            stringBuilder.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;").append(stackTraceElement.toString());
        }
        return stringBuilder.toString();
    }

    public static Status updateOverallStatus(Status currentStatus, WebDriver driver) {
        Status overallStatus;
        // add to overall status
        Status testStatus = driver.getReporter().getStatus();
        if (testStatus == Status.CHECK && currentStatus != Status.FAIL) {
            overallStatus = Status.CHECK;
        } else if (testStatus == Status.FAIL) {
            overallStatus = Status.FAIL;
        } else {
            overallStatus = currentStatus;
        }
        return overallStatus;
    }

    public static void writeTestReport(WebDriver driver, String methodName) throws IOException {
        StringBuilder steps = new StringBuilder();
        int passed = 0;
        int failed = 0;
        int checked = 0;
        for (Step step : driver.getReporter().getSteps()) {
            Status status = step.getStatus();
            switch (status) {
                case PASS:
                    passed++;
                    break;
                case FAIL:
                    failed++;
                    break;
                case CHECK:
                    checked++;
                    break;
            }
            steps.append("<tr>");
            steps.append("<td>").append(step.getNumber()).append("</td>");
            steps.append("<td>").append(step.getAction()).append("</td>");
            steps.append("<td>").append(step.getExpected()).append("</td>");
            steps.append("<td>").append(step.getActual()).append("</td>");
            if (step.getScreenshot() != null) {
                //TODO - toggle images
                steps.append("<td>").append("<img height='200' src='data:image/png;base64,").append(step.getScreenshot()).append("'/>").append("</td>");
            } else {
                steps.append("<td></td>");
            }
            steps.append("<td class='").append(status).append("'>").append(status).append("</td>");
            steps.append("<td>").append(step.getTime()).append("</td>");
            steps.append("</tr>");
        }
        StringBuilder caps = new StringBuilder();
        Capabilities capabilities = driver.getCapabilities();
        for( String capability : capabilities.getCapabilityNames() ) {
            caps.append("<tr><th class='title'>").append(capability).append("</th><td>")
                    .append(capabilities.getCapability(capability)).append("</td></tr>");
        }
        String report = getContent(new URL(testCaseTemplate)).replace("$testCaseName", methodName)
                .replace("$testCaseStatus", driver.getReporter().getStatus().toString())
                .replace("$totalSteps", String.valueOf(driver.getReporter().getSteps().size()))
                .replace("$stepsPassed", String.valueOf(passed))
                .replace("$stepsFailed", String.valueOf(failed))
                .replace("$stepsChecked", String.valueOf(checked))
                .replace("$testCaseTime", driver.getReporter().getRunTime() + " ms")
                .replace("$capabilities", caps.toString())
                .replace("$rows", steps.toString());
        File reportFile = new File(testResults, methodName + ".webdrivers.get().html");
        FileUtils.writeStringToFile(reportFile, report, Charset.defaultCharset());
    }

    public static void writeOverallReport(Map<String, Reporter> testsExecuted, Status overallStatus, long startTime) throws IOException {
        StringBuilder testCaseList = new StringBuilder();
        int passed = 0;
        int failed = 0;
        int checked = 0;
        int ignored = 0;    // TODO - need to add capability
        for (Map.Entry<String, Reporter> testCase : testsExecuted.entrySet()) {
            Status status = testCase.getValue().getStatus();
            switch (status) {
                case PASS:
                    passed++;
                    break;
                case FAIL:
                    failed++;
                    break;
                case CHECK:
                    checked++;
                    break;
                default:
                    ignored++;
            }
            testCaseList.append("<tr class='").append(status.toString()).append("'>");
            testCaseList.append("<td>").append(testCase.getKey()).append("</td>");
            testCaseList.append("<td>").append(status.toString()).append("</td>");
            testCaseList.append("<td>").append(testCase.getValue().getRunTime()).append(" ms</td>");
            testCaseList.append("<td>");
            testCaseList.append("<a href='").append(testCase.getKey()).append(".webdrivers.get().html'>WebDriver</a> ");
            testCaseList.append("<a href='").append(testCase.getKey()).append(".appium.log'>Appium</a>");
            testCaseList.append("</td>");
            testCaseList.append("</tr>");
        }
        String report = getContent(new URL(testResultTemplate)).replace("$testSuiteName", "Test Suite")
                .replace("$overallResult", overallStatus.toString())
                .replace("$totalTests", String.valueOf(testsExecuted.size()))
                .replace("$testsPassed", String.valueOf(passed))
                .replace("$testFailed", String.valueOf(failed))
                .replace("$testChecked", String.valueOf(checked))
                .replace("$testsIgnored", String.valueOf(ignored))
                .replace("$totalTime", new Date().getTime() - startTime + " ms")
                .replace("$testResults", testCaseList.toString());
        File reportFile = new File(testResults, "index.html");
        FileUtils.writeStringToFile(reportFile, report, Charset.defaultCharset());
    }

    private static String getContent(URL url) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            for (String line; (line = reader.readLine()) != null; ) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }
}
