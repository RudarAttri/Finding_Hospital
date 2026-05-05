package factory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    /**
     * Initialises a ChromeDriver and stores it in ThreadLocal.
     * Called once per scenario via Hooks @Before.
     */
    public static WebDriver initDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        WebDriver driver = new ChromeDriver(options);
        tlDriver.set(driver);
        System.out.println("Driver initialised: " + driver);
        return driver;
    }

    /** Returns the WebDriver bound to the current thread. */
    public static WebDriver getDriver() {
        return tlDriver.get();
    }

    /** Quits and removes the driver for the current thread. */
    public static void quitDriver() {
        if (tlDriver.get() != null) {
            tlDriver.get().quit();
            tlDriver.remove();
            System.out.println("Driver quit and removed from ThreadLocal.");
        }
    }
}