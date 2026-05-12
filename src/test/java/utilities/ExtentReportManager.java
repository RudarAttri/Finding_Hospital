package utilities;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ExtentReportManager {

    private static volatile ExtentReports extent;   // volatile for thread visibility
    private static String reportPath;
    private static final Object LOCK = new Object();

    public static ExtentReports getInstance() {
        // Double-checked locking — fast path when already created
        if (extent == null) {
            synchronized (LOCK) {
                if (extent == null) {
                    createInstance();
                }
            }
        }
        return extent;
    }

    private static void createInstance() {
        // Create Reports/Extent directory if it doesn't exist
        File dir = new File("Reports/Extent");
        if (!dir.exists()) dir.mkdirs();

        // Timestamped report file
        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        reportPath = "Reports/Extent/ExtentReport_" + ts + ".html";

        // Configure the HTML reporter
        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("Practo Automation Report");
        spark.config().setReportName("Corporate Wellness — Data-Driven Test Results");
        spark.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
        spark.config().setEncoding("utf-8");

        // Build the ExtentReports object
        extent = new ExtentReports();
        extent.attachReporter(spark);

        // System / environment info shown in the report header
        extent.setSystemInfo("Project",      "Finding_Hospital");
        extent.setSystemInfo("Application",  "Practo");
        extent.setSystemInfo("Module",       "Corporate Wellness Form");
        extent.setSystemInfo("Test Framework", "TestNG + Apache POI (Parallel)");
        extent.setSystemInfo("Browser",      "Chrome");
        extent.setSystemInfo("OS",           System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("Tester",       System.getProperty("user.name"));

        System.out.println("Extent report initialized: " + reportPath);
    }

    public static String getReportPath() {
        return reportPath;
    }

    /** Flushes and closes the report — must be called at end of suite. */
    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
            System.out.println("Extent report saved at: " + reportPath);
        }
    }
}