package org.example;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/**
 * Static driver bootstrap kept for reference.
 * The live framework uses DriverFactory (ThreadLocal).
 */
public class BaseClass {

    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected static JavascriptExecutor js;
    private static final String BASE_URL = "https://www.practo.com";

    public static void initDriver() {
        if (driver == null) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-popup-blocking");

            driver = new ChromeDriver(options);
            wait   = new WebDriverWait(driver, Duration.ofSeconds(20));
            js     = (JavascriptExecutor) driver;

            driver.get(BASE_URL);
            System.out.println("Browser launched -> " + BASE_URL);
        }
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
            System.out.println("Browser closed.");
        }
    }

    public static WebDriver       getDriver()  { return driver; }
    public static WebDriverWait   getWait()    { return wait;   }
    public static JavascriptExecutor getJs()   { return js;     }
    public static String          getBaseUrl() { return BASE_URL; }
}