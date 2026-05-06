package org.example;

import factory.DriverFactory;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;
import pagedObjects.CorporateWellnessPage;
import utilities.ExcelUtils;
import utilities.ScreenshotHelper;

/**
 * TestNG data-driven test for the Corporate Wellness "Schedule a Demo" form.
 *
 * STRATEGY:
 *   The submit button itself is the source of truth.
 *   Practo's form keeps the button DISABLED until all fields are valid.
 *
 *   • VALID data   → button gets enabled by the form's own validation
 *   • INVALID data → button stays disabled
 *
 *   We do NOT click the button or look for popups, because:
 *     - reCAPTCHA appears after click and confuses success detection
 *     - Form validation on the button is the most reliable signal
 *
 * ActualStatus convention:
 *   PASS = button enabled (form considered the data valid)
 *   FAIL = button disabled (form considered the data invalid)
 */
public class CorporateWellnessTest {

    private CorporateWellnessPage wellnessPage;

    private static final String EXCEL_PATH = "TestData/CorporateWellnessData.xlsx";
    private static final String SHEET_NAME = "Registration";
    private static final int    COL_ID     = 0;
    private static final int    COL_STATUS = 8;

    private final ExcelUtils excel = new ExcelUtils(EXCEL_PATH, SHEET_NAME);

    @DataProvider(name = "registrationData")
    public Object[][] getRegistrationData() throws Exception {
        return excel.getSheetData();
    }

    @BeforeMethod
    public void setUp() {
        DriverFactory.initDriver();
        wellnessPage = new CorporateWellnessPage();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        DriverFactory.quitDriver();
    }

    @Test(dataProvider = "registrationData",
            description  = "Corporate Wellness — data-driven via Apache POI")
    public void testCorporateWellnessRegistration(
            String testId,
            String testType,
            String name,
            String email,
            String phone,
            String company,
            String employees,
            String expectedResult,
            String actualStatusIgnored) {

        System.out.println("\n========================================");
        System.out.println(testId + " | Type: " + testType
                + " | Expected: " + expectedResult);
        System.out.println("Data → name='" + name + "' | email='" + email
                + "' | phone='" + phone + "' | company='" + company
                + "' | employees='" + employees + "'");
        System.out.println("========================================");

        String actualStatus = "FAIL";

        try {
            // Open page
            wellnessPage.navigateToCorporateWellness();

            // Fill form
            wellnessPage.enterInvalidName(name);
            wellnessPage.enterInvalidEmail(email);
            wellnessPage.enterInvalidPhone(phone);
            wellnessPage.enterCompany(company);
            wellnessPage.enterInvalidEmployeeCount(employees);

            ScreenshotHelper.takeScreenshot(testId + "_" + testType + "_FormFilled");

            // ─── KEY: button state IS the truth — don't click, don't look for popups ─
            boolean buttonEnabled = wellnessPage.isScheduleButtonEnabled();
            System.out.println("Schedule button enabled: " + buttonEnabled);

            //   Button enabled  → form considered the data valid     → PASS
            //   Button disabled → form considered the data invalid   → FAIL
            actualStatus = buttonEnabled ? "PASS" : "FAIL";

            // We DON'T click the button. Why?
            //   - For VALID rows: clicking triggers reCAPTCHA which confuses detection
            //                     and could spam the actual Practo backend
            //   - For INVALID rows: button is disabled, click does nothing anyway
            // The button state alone tells us whether the form's validation passed.

            ScreenshotHelper.takeScreenshot(testId + "_" + testType + "_AfterValidation");

            System.out.println("──── DEBUG ────");
            System.out.println("  Button enabled? " + buttonEnabled);
            System.out.println("  ActualStatus: " + actualStatus);
            System.out.println("───────────────");

            System.out.println(testId + " | Expected: " + expectedResult
                    + " | Actual: " + actualStatus
                    + " | Match: " + actualStatus.equalsIgnoreCase(expectedResult));

            // Verify expected matches actual
            Assert.assertEquals(actualStatus, expectedResult.toUpperCase(),
                    testId + " — Expected " + expectedResult
                            + " but form behavior was " + actualStatus);

        } catch (AssertionError ae) {
            ScreenshotHelper.takeScreenshot(testId + "_FAILED");
            throw ae;
        } catch (Exception e) {
            ScreenshotHelper.takeScreenshot(testId + "_ERROR");
            actualStatus = "ERROR";
            throw new RuntimeException(e);
        } finally {
            excel.writeStatusByTestCaseId(testId, actualStatus, COL_ID, COL_STATUS);
        }
    }
}