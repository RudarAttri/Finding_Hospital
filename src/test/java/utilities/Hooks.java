package utilities;

import factory.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Hooks {

    /** Runs before each Scenario: start browser and navigate to Practo. */
    @Before
    public void setUp(Scenario scenario) {
        System.out.println("\n========== Starting: " + scenario.getName() + " ==========");
        WebDriver driver = DriverFactory.initDriver();
        driver.get("https://www.practo.com");
    }

    /** Runs after each Scenario: attach screenshot on failure, then quit. */
    @After
    public void tearDown(Scenario scenario) {
        WebDriver driver = DriverFactory.getDriver();

        if (scenario.isFailed() && driver != null) {
            try {
                byte[] screenshot = ((TakesScreenshot) driver)
                        .getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Failure Screenshot");
                System.out.println("Screenshot attached for: " + scenario.getName());
            } catch (Exception e) {
                System.out.println("Screenshot failed: " + e.getMessage());
            }
        }

        System.out.println("========== Ended: " + scenario.getName()
                + " | Status: " + scenario.getStatus() + " ==========\n");
        DriverFactory.quitDriver();
    }
}