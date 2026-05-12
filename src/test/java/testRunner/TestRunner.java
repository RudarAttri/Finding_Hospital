package testRunner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;


@CucumberOptions(

        // ✅ List feature files EXPLICITLY in the order you want them to execute:
        //    1. hospitals.feature  → runs FIRST  (browser opens, runs, browser CLOSES)
        //    2. doctors.feature    → runs SECOND (fresh browser opens, runs, browser CLOSES)
        features = {
                "Features/1_hospitals.feature",
                "Features/2_doctors.feature"
        },

        // Packages containing step definitions and hooks
        glue = {"stepDefinitions", "utilities"},

        // Reporting plugins
        plugin = {
                "pretty",
                "html:Reports/cucumber-html-report.html",
                "json:Reports/cucumber-report.json",
                "junit:Reports/cucumber-junit.xml"
        },

        monochrome = true,
        dryRun     = false,

        // ✅ Include both @hospitals and @doctors so both scenarios execute
        tags = "@hospitals or @doctors"
)
public class TestRunner extends AbstractTestNGCucumberTests {
        // Intentionally empty — TestNG uses this class as the Cucumber entry point.
        // Inherits scenarios() @DataProvider and runScenario() @Test
        // from AbstractTestNGCucumberTests.
}