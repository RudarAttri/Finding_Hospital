package pagedObjects;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import utilities.WaitHelper;
import java.util.List;

/**
 * Page Object for the Practo Corporate Wellness "Schedule a Demo" page.
 *
 * Navigation flow (mimics real user):
 *   1. Open Practo homepage
 *   2. Click/hover "For Corporates" in top nav
 *   3. Click "Health & Wellness Plans" link
 *   4. Land on the corporate form
 *
 * Real form field IDs (from the actual page):
 *   #name, #organizationName, #contactNumber, #officialEmailId,
 *   #organizationSize (select), #interestedIn (select)
 */
public class CorporateWellnessPage extends BasePage {

    // ── Homepage navigation locators ─────────────────────────────────────────
    @FindBy(xpath = "//span[contains(@class,'nav-interact') " +
            "and contains(text(),'For Corporates')]" +
            "/ancestor::div[contains(@class,'dropdown-toggle')] | " +
            "//span[contains(text(),'For Corporates')]")
    private WebElement forCorporatesMenu;

    @FindBy(xpath = "//a[normalize-space()='Health & Wellness Plans' " +
            "or contains(text(),'Health & Wellness') " +
            "or contains(text(),'Wellness Plans')]")
    private WebElement healthWellnessLink;

    // ── Form field locators (real IDs from the page) ─────────────────────────
    @FindBy(id = "name")
    private WebElement nameField;

    @FindBy(id = "organizationName")
    private WebElement orgNameField;

    @FindBy(id = "contactNumber")
    private WebElement phoneField;

    @FindBy(id = "officialEmailId")
    private WebElement emailField;

    @FindBy(id = "organizationSize")
    private WebElement orgSizeDropdown;

    @FindBy(id = "interestedIn")
    private WebElement interestedInDropdown;

    @FindBy(xpath = "//button[contains(text(),'Schedule a demo') " +
            "or contains(text(),'Schedule') " +
            "or contains(text(),'Submit')]")
    private WebElement scheduleButton;

    private static final By INLINE_ERROR = By.xpath(
            "//*[contains(@class,'error') or contains(@class,'warning') " +
                    "or contains(@class,'invalid') or contains(@class,'u-red-text') " +
                    "or contains(@class,'corporate-form__error')]");

    // ── Navigation ────────────────────────────────────────────────────────────
    /**
     * Navigates via homepage menu (real user flow):
     * Practo home → For Corporates → Health & Wellness Plans
     */
    public void navigateToCorporateWellness() {
        // Step 1: Go to Practo homepage
        driver.get("https://www.practo.com");
        sleep(3000);
        System.out.println("Homepage opened: " + driver.getCurrentUrl());

        // Step 2: Click "For Corporates" dropdown in top nav
        try {
            WaitHelper.waitForVisibilityOfElement(forCorporatesMenu);
            scrollTo(forCorporatesMenu);
            sleep(500);
            jsClick(forCorporatesMenu);
            System.out.println("Clicked 'For Corporates' menu");
            sleep(1500);
        } catch (Exception e) {
            System.out.println("'For Corporates' menu issue: " + e.getMessage());
        }

        // Step 3: Click "Health & Wellness Plans" link
        try {
            WaitHelper.waitForVisibilityOfElement(healthWellnessLink);
            scrollTo(healthWellnessLink);
            sleep(500);
            jsClick(healthWellnessLink);
            System.out.println("Clicked 'Health & Wellness Plans' link");
            sleep(3000);
        } catch (Exception e) {
            System.out.println("Wellness link issue, falling back to direct URL: "
                    + e.getMessage());
            driver.get("https://www.practo.com/plus/corporate");
            sleep(3000);
        }

        // Step 4: Wait for the form to load
        WaitHelper.waitForVisibilityOfElement(nameField);
        System.out.println("Corporate Wellness form loaded. URL: "
                + driver.getCurrentUrl());
    }

    // ── Field actions ─────────────────────────────────────────────────────────
    public void enterInvalidName(String v) {
        WaitHelper.waitForVisibilityOfElement(nameField);
        scrollTo(nameField);
        nameField.clear();
        if (v != null && !v.isEmpty()) nameField.sendKeys(v);
        System.out.println("Entered name: " + v);
    }

    public void enterInvalidEmail(String v) {
        WaitHelper.waitForVisibilityOfElement(emailField);
        scrollTo(emailField);
        emailField.clear();
        if (v != null && !v.isEmpty()) emailField.sendKeys(v);
        System.out.println("Entered email: " + v);
    }

    public void enterInvalidPhone(String v) {
        try {
            WaitHelper.waitForVisibilityOfElement(phoneField);
            scrollTo(phoneField);
            phoneField.clear();
            if (v != null && !v.isEmpty()) phoneField.sendKeys(v);
            System.out.println("Entered phone: " + v);
        } catch (Exception e) {
            System.out.println("Phone field issue: " + e.getMessage());
        }
    }

    public void enterCompany(String v) {
        try {
            WaitHelper.waitForVisibilityOfElement(orgNameField);
            scrollTo(orgNameField);
            orgNameField.clear();
            if (v != null && !v.isEmpty()) orgNameField.sendKeys(v);
            System.out.println("Entered organization: " + v);
        } catch (Exception e) {
            System.out.println("Organization field issue: " + e.getMessage());
        }
    }

    /**
     * Selects org size + interested-in dropdowns.
     * Index 0 (or empty) = leave blank → invalid.
     * Index > 0 = pick first real option (so VALID rows can submit).
     */
    public void enterInvalidEmployeeCount(String v) {
        try {
            WaitHelper.waitForVisibilityOfElement(orgSizeDropdown);
            scrollTo(orgSizeDropdown);

            int idx = parseIndex(v);
            if (idx <= 0) {
                System.out.println("Org size + Interested-In left blank (invalid case)");
                return;
            }

            // Organization Size
            Select s = new Select(orgSizeDropdown);
            if (idx < s.getOptions().size()) {
                s.selectByIndex(idx);
                System.out.println("Selected org size: "
                        + s.getFirstSelectedOption().getText());
            }

            // Interested In
            WaitHelper.waitForVisibilityOfElement(interestedInDropdown);
            Select s2 = new Select(interestedInDropdown);
            if (idx < s2.getOptions().size()) {
                s2.selectByIndex(idx);
                System.out.println("Selected interested-in: "
                        + s2.getFirstSelectedOption().getText());
            }
        } catch (Exception e) {
            System.out.println("Dropdown issue: " + e.getMessage());
        }
    }

    private int parseIndex(String v) {
        if (v == null || v.trim().isEmpty()) return 0;
        try {
            double d = Double.parseDouble(v.trim());
            return d > 0 ? 1 : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ── Submit ────────────────────────────────────────────────────────────────
    public void clickSchedule() {
        WaitHelper.waitForVisibilityOfElement(scheduleButton);
        scrollTo(scheduleButton);
        sleep(500);
        try {
            jsClick(scheduleButton);
            System.out.println("Clicked schedule button");
        } catch (Exception e) {
            System.out.println("Schedule click failed (likely disabled): " + e.getMessage());
        }
        sleep(2000);
    }

    // ── Error capture ─────────────────────────────────────────────────────────
    public String captureAlertMessage() {
        // 1. JS alert?
        try {
            WaitHelper.waitForAlert();
            Alert alert = driver.switchTo().alert();
            String message = alert.getText();
            System.out.println("JS Alert: " + message);
            alert.accept();
            return message;
        } catch (Exception e) {
            // 2. Inline error?
            String inline = captureInlineErrorMessage();
            if (!inline.equals("No error message found")) return inline;

            // 3. Submit button disabled = silent rejection
            try {
                if (!scheduleButton.isEnabled()) {
                    return "Submit button disabled — form rejected invalid input";
                }
                String aria = scheduleButton.getAttribute("aria-disabled");
                String cls  = scheduleButton.getAttribute("class");
                if ("true".equalsIgnoreCase(aria)
                        || (cls != null && cls.toLowerCase().contains("disabled"))) {
                    return "Submit button disabled — form rejected invalid input";
                }
            } catch (Exception ignored) {}

            return "No error message found";
        }
    }

    public String captureInlineErrorMessage() {
        try {
            List<WebElement> errors = driver.findElements(INLINE_ERROR);
            for (WebElement err : errors) {
                String text = err.getText().trim();
                if (!text.isEmpty()) {
                    System.out.println("Inline error: " + text);
                    return text;
                }
            }
        } catch (Exception e) {
            System.out.println("Inline error check failed: " + e.getMessage());
        }
        return "No error message found";
    }
}