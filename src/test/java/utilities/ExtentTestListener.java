package utilities;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import factory.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * TestNG Listener — automatically logs every test to ExtentReports.
 * Place under: src/test/java/utilities/ExtentTestListener.java
 *
 * Wire it up in Testng.xml with:
 *   <listeners>
 *     <listener class-name="utilities.ExtentTestListener"/>
 *   </listeners>
 */
public class ExtentTestListener implements ITestListener, ISuiteListener {

    private static final ThreadLocal<ExtentTest> tlTest = new ThreadLocal<>();
    private ExtentReports extent;

    // ─── Suite lifecycle ─────────────────────────────────────────────────────
    @Override
    public void onStart(ISuite suite) {
        extent = ExtentReportManager.getInstance();
        System.out.println("\n>>> Suite started: " + suite.getName() + " <<<");
    }

    @Override
    public void onFinish(ISuite suite) {
        ExtentReportManager.flush();
        System.out.println(">>> Suite finished: " + suite.getName() + " <<<\n");
    }

    // ─── Per-test lifecycle ──────────────────────────────────────────────────
    @Override
    public void onTestStart(ITestResult result) {
        String testName = buildTestName(result);

        ExtentTest test = extent.createTest(testName);
        test.assignCategory(result.getMethod().getRealClass().getSimpleName());

        Object[] params = result.getParameters();
        if (params != null && params.length > 0) {
            test.info("<b>Test Data:</b> " + Arrays.toString(params));
        }

        tlTest.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = tlTest.get();
        if (test != null) {
            test.log(Status.PASS, "Test passed");
            attachScreenshot(test, "PASSED_" + result.getMethod().getMethodName());
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = tlTest.get();
        if (test != null) {
            test.log(Status.FAIL, "Test failed: " + result.getThrowable());
            test.fail(result.getThrowable());
            attachScreenshot(test, "FAILED_" + result.getMethod().getMethodName());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = tlTest.get();
        if (test != null) {
            test.log(Status.SKIP, "Test skipped: " + result.getThrowable());
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────
    private String buildTestName(ITestResult result) {
        Object[] params = result.getParameters();
        // If first parameter is the TestCaseID (like "TC01"), use that
        if (params != null && params.length > 0 && params[0] != null) {
            String first = params[0].toString();
            if (first.matches("TC\\d+.*")) {
                String type = (params.length > 1 && params[1] != null)
                        ? " [" + params[1] + "]" : "";
                return first + type + " — " + result.getMethod().getMethodName();
            }
        }
        return result.getMethod().getMethodName();
    }

    private void attachScreenshot(ExtentTest test, String name) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver == null) return;

            String ts       = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String safeName = name.replaceAll("[^a-zA-Z0-9]", "_");

            File dir = new File("Reports/Extent/screenshots");
            if (!dir.exists()) dir.mkdirs();

            String relPath = "screenshots/" + safeName + "_" + ts + ".png";
            File   destFile = new File("Reports/Extent/" + relPath);

            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            org.openqa.selenium.io.FileHandler.copy(src, destFile);

            test.info("Screenshot:",
                    MediaEntityBuilder.createScreenCaptureFromPath(relPath).build());
        } catch (Exception e) {
            test.info("Could not attach screenshot: " + e.getMessage());
        }
    }
}