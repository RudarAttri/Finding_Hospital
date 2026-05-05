package testRunner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        // Path to .feature files
        features = "Features",

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

        // Run all 3 feature tags by default.
        // Override per-run: -Dcucumber.filter.tags="@hospitals"
        tags = "@hospitals or @diagnostics or @corporateWellness"
)
public class TestRunner {
    // Intentionally empty — JUnit uses this class as the Cucumber entry point.
}