package testRunner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Cucumber Runner — migrated from JUnit to TestNG.
 *
 * ★ WHAT CHANGED ★
 *   OLD: @RunWith(Cucumber.class) + io.cucumber.junit.* imports
 *   NEW: extends AbstractTestNGCucumberTests + io.cucumber.testng.* imports
 *
 * ★ WHAT DID NOT CHANGE ★
 *   • All @CucumberOptions content (features, glue, plugins, tags) — IDENTICAL
 *   • Hooks.java                                                    — unchanged
 *   • Step definitions                                              — unchanged
 *   • Feature files                                                 — unchanged
 *   • DriverFactory                                                 — unchanged
 *   • Cucumber's internal scenario flow                             — unchanged
 *
 * ★ WHY THIS MIGRATION ★
 *   So this runner can be included in a TestNG suite XML
 *   (MasterSuite.xml) alongside our regular TestNG tests.
 *   This lets BOTH suites run in parallel through a single TestNG
 *   entry point instead of two separate Maven commands.
 *
 * ★ HOW TO RUN ★
 *   • Standalone (this class only):   right-click → Run 'TestRunner'
 *   • Together with TestNG tests:     run MasterSuite.xml
 */
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
public class TestRunner extends AbstractTestNGCucumberTests {
        // Intentionally empty — TestNG uses this class as the Cucumber entry point.
        // Inherits scenarios() @DataProvider and runScenario() @Test
        // from AbstractTestNGCucumberTests.
}