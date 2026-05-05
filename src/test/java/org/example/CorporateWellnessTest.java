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
 * ActualStatus convention (matches ExpectedResult column):
 *   PASS = the form accepted the user data (no warning shown)
 *   FAIL = the form rejected the user data (warning/error shown)
 *
 * For VALID rows  → ExpectedResult = PASS → test asserts ActualStatus = PASS
 * For INVALID rows → ExpectedResult = FAIL → test asserts ActualStatus = FAIL
 *
 * Excel columns (0-indexed):
 *   0 = TestCaseID
 *   1 = TestType        (VALID / INVALID)
 *   2 = Name
 *   3 = Email
 *   4 = Phone
 *   5 = Company
 *   6 = Employees
 *   7 = ExpectedResult  (PASS / FAIL)
 *   8 = ActualStatus    ← updated by this test (PASS / FAIL)
 */
public class CorporateWellnessTest {

    private CorporateWellnessPage wellnessPage;

    private static final String EXCEL_PATH = "TestData/CorporateWellnessData.xlsx";
    private static final String SHEET_NAME = "Registration";
    private static final int    COL_ID     = 0;
    private static final int    COL_STATUS = 8;

    private final ExcelUtils excel = new ExcelUtils(EXCEL_PATH, SHEET_NAME);

    // =========================================================================
    //  DATA PROVIDER
    // =========================================================================
    @DataProvider(name = "registrationData")
    public Object[][] getRegistrationData() throws Exception {
        return excel.getSheetData();
    }

    // =========================================================================
    //  SETUP / TEARDOWN
    // =========================================================================
    @BeforeMethod
    public void setUp() {
        DriverFactory.initDriver();
        wellnessPage = new CorporateWellnessPage();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        DriverFactory.quitDriver();
    }

    // =========================================================================
    //  TEST — runs once per Excel row
    // =========================================================================
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
        System.out.println("Data → " + name + " | " + email
                + " | " + phone + " | " + company + " | " + employees);
        System.out.println("========================================");

        // ActualStatus = "PASS" means form accepted, "FAIL" means form rejected
        String actualStatus = "FAIL";

        try {
            // Step 1 — open Corporate Wellness page
            wellnessPage.navigateToCorporateWellness();

            // Step 2 — fill the form
            wellnessPage.enterInvalidName(name);
            wellnessPage.enterInvalidEmail(email);
            wellnessPage.enterInvalidPhone(phone);
            wellnessPage.enterCompany(company);
            wellnessPage.enterInvalidEmployeeCount(employees);

            // Step 3 — screenshot of filled form
            ScreenshotHelper.takeScreenshot(testId + "_" + testType + "_FormFilled");

            // Step 4 — click Schedule
            wellnessPage.clickSchedule();

            // Step 5 — capture warning/alert
            String warning = wellnessPage.captureAlertMessage();
            System.out.println("Warning message: " + warning);

            // Step 6 — screenshot after submit
            ScreenshotHelper.takeScreenshot(testId + "_" + testType + "_AfterSubmit");

            // Step 7 — determine the form's actual outcome
            boolean formRejected = warning != null
                    && !warning.isEmpty()
                    && !warning.equalsIgnoreCase("No error message found");

            //  PASS = form accepted (no warning)
            //  FAIL = form rejected (warning shown)
            actualStatus = formRejected ? "FAIL" : "PASS";

            System.out.println(testId + " → ActualStatus = " + actualStatus
                    + " (Expected: " + expectedResult + ")");

            // Step 8 — verify expected matches actual
            if ("VALID".equalsIgnoreCase(testType)) {
                Assert.assertEquals(actualStatus, "PASS",
                        testId + " — Valid data should be accepted (PASS), but got: "
                                + actualStatus + ". Warning: " + warning);
            } else {
                Assert.assertEquals(actualStatus, "FAIL",
                        testId + " — Invalid data should be rejected (FAIL), but got: "
                                + actualStatus);
            }

        } catch (AssertionError ae) {
            ScreenshotHelper.takeScreenshot(testId + "_FAILED");
            // Don't change actualStatus — it already reflects what happened
            throw ae;

        } catch (Exception e) {
            ScreenshotHelper.takeScreenshot(testId + "_ERROR");
            actualStatus = "ERROR";
            throw new RuntimeException(e);

        } finally {
            // Step 9 — write actual status to Excel
            excel.writeStatusByTestCaseId(testId, actualStatus, COL_ID, COL_STATUS);
        }
    }
}