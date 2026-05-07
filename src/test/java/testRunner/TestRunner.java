package testRunner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(

        // ✅ List feature files EXPLICITLY in the order you want them to execute:
        //    1. hospitals.feature  → runs FIRST  (browser opens, runs, browser CLOSES)
        //    2. doctors.feature    → runs SECOND (fresh browser opens, runs, browser CLOSES)
        features = {
                "Features/hospitals.feature",
                "Features/doctors.feature"
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
public class TestRunner {
        // Intentionally empty — JUnit uses this class as the Cucumber entry point.
}