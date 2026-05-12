package pagedObjects;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import utilities.WaitHelper;
import java.util.List;

public class CorporateWellnessPage extends BasePage {

    // ── Homepage navigation ──────────────────────────────────────────────────
    @FindBy(xpath = "//span[contains(@class,'nav-interact') " +
            "and contains(text(),'For Corporates')]" +
            "/ancestor::div[contains(@class,'dropdown-toggle')] | " +
            "//span[contains(text(),'For Corporates')]")
    private WebElement forCorporatesMenu;

    @FindBy(xpath = "//a[normalize-space()='Health & Wellness Plans' " +
            "or contains(text(),'Health & Wellness') " +
            "or contains(text(),'Wellness Plans')]")
    private WebElement healthWellnessLink;

    // ── Form fields ──────────────────────────────────────────────────────────
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

    // Success message / thank-you popup ONLY (not reCAPTCHA)
    // Looks for explicit success keywords in non-recaptcha elements
    private static final By SUCCESS_MESSAGE = By.xpath(
            "//*[" +
                    "(contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ'," +
                    "'abcdefghijklmnopqrstuvwxyz'),'thank you') " +
                    "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ'," +
                    "'abcdefghijklmnopqrstuvwxyz'),'we will get back') " +
                    "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ'," +
                    "'abcdefghijklmnopqrstuvwxyz'),'submitted successfully') " +
                    "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ'," +
                    "'abcdefghijklmnopqrstuvwxyz'),'request received') " +
                    "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ'," +
                    "'abcdefghijklmnopqrstuvwxyz'),'demo has been scheduled')" +
                    ") " +
                    "and not(ancestor::iframe) " +
                    "and not(contains(@id,'recaptcha')) " +
                    "and not(contains(@class,'recaptcha')) " +
                    "and not(contains(@class,'g-recaptcha'))" +
                    "]");

    private static final By INLINE_ERROR = By.xpath(
            "//*[contains(@class,'error') or contains(@class,'warning') " +
                    "or contains(@class,'invalid') or contains(@class,'u-red-text') " +
                    "or contains(@class,'corporate-form__error')]");

    // ── Navigation ────────────────────────────────────────────────────────────
    public void navigateToCorporateWellness() {
        driver.get("https://www.practo.com");
        sleep(3000);
        System.out.println("Homepage opened: " + driver.getCurrentUrl());

        try {
            WaitHelper.waitForVisibilityOfElement(forCorporatesMenu);
            scrollTo(forCorporatesMenu);
            sleep(500);
            jsClick(forCorporatesMenu);
            sleep(1500);
        } catch (Exception e) {
            System.out.println("'For Corporates' menu issue: " + e.getMessage());
        }

        try {
            WaitHelper.waitForVisibilityOfElement(healthWellnessLink);
            scrollTo(healthWellnessLink);
            sleep(500);
            jsClick(healthWellnessLink);
            sleep(3000);
        } catch (Exception e) {
            System.out.println("Falling back to direct URL: " + e.getMessage());
            driver.get("https://www.practo.com/plus/corporate");
            sleep(3000);
        }

        WaitHelper.waitForVisibilityOfElement(nameField);
        System.out.println("Form loaded. URL: " + driver.getCurrentUrl());
    }

    // ── Field actions ─────────────────────────────────────────────────────────
    public void enterInvalidName(String v) {
        WaitHelper.waitForVisibilityOfElement(nameField);
        scrollTo(nameField);
        nameField.clear();
        if (v != null && !v.isEmpty()) nameField.sendKeys(v);
    }

    public void enterInvalidEmail(String v) {
        WaitHelper.waitForVisibilityOfElement(emailField);
        scrollTo(emailField);
        emailField.clear();
        if (v != null && !v.isEmpty()) emailField.sendKeys(v);
    }

    public void enterInvalidPhone(String v) {
        try {
            WaitHelper.waitForVisibilityOfElement(phoneField);
            scrollTo(phoneField);
            phoneField.clear();
            if (v != null && !v.isEmpty()) phoneField.sendKeys(v);
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
        } catch (Exception e) {
            System.out.println("Org field issue: " + e.getMessage());
        }
    }

    public void enterInvalidEmployeeCount(String v) {
        try {
            WaitHelper.waitForVisibilityOfElement(orgSizeDropdown);
            scrollTo(orgSizeDropdown);

            int idx = parseIndex(v);
            if (idx <= 0) {
                System.out.println("Org size + Interested-In left blank (invalid case)");
                return;
            }

            Select s = new Select(orgSizeDropdown);
            if (idx < s.getOptions().size()) s.selectByIndex(idx);

            WaitHelper.waitForVisibilityOfElement(interestedInDropdown);
            Select s2 = new Select(interestedInDropdown);
            if (idx < s2.getOptions().size()) s2.selectByIndex(idx);
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
            System.out.println("Schedule click failed: " + e.getMessage());
        }
        sleep(2500);
    }

    public boolean isScheduleButtonEnabled() {
        try {
            WaitHelper.waitForVisibilityOfElement(scheduleButton);
            scrollTo(scheduleButton);
            sleep(500);

            String aria = scheduleButton.getAttribute("aria-disabled");
            String cls  = scheduleButton.getAttribute("class");

            boolean nativeEnabled = scheduleButton.isEnabled();
            boolean ariaDisabled  = "true".equalsIgnoreCase(aria);
            boolean classDisabled = cls != null && cls.toLowerCase().contains("disabled");

            boolean enabled = nativeEnabled && !ariaDisabled && !classDisabled;

            System.out.println("Button state → enabled=" + nativeEnabled
                    + " | aria-disabled=" + aria
                    + " | final=" + enabled);
            return enabled;
        } catch (Exception e) {
            System.out.println("Button state check failed: " + e.getMessage());
            return false;
        }
    }
    public boolean isSuccessMessageDisplayed() {
        sleep(2000); // give page time to render

        // First check JS alert
        try {
            WaitHelper.waitForAlert();
            Alert alert = driver.switchTo().alert();
            String msg = alert.getText();
            alert.accept();
            System.out.println("JS Alert (success): " + msg);
            // Only treat as success if alert mentions success keywords
            String lower = msg.toLowerCase();
            return lower.contains("thank") || lower.contains("success")
                    || lower.contains("received") || lower.contains("submitted");
        } catch (Exception ignored) {}

        // Check for visible success text on page (excluding reCAPTCHA)
        try {
            List<WebElement> hits = driver.findElements(SUCCESS_MESSAGE);
            for (WebElement el : hits) {
                if (!el.isDisplayed()) continue;

                // Double-check this isn't a reCAPTCHA element
                String tagName = el.getTagName();
                String id      = el.getAttribute("id");
                String cls     = el.getAttribute("class");

                if ("iframe".equalsIgnoreCase(tagName)) continue;
                if (id != null && id.toLowerCase().contains("recaptcha")) continue;
                if (cls != null && cls.toLowerCase().contains("recaptcha")) continue;
                if (cls != null && cls.toLowerCase().contains("g-recaptcha")) continue;

                String text = el.getText().trim();
                if (!text.isEmpty()) {
                    System.out.println("Success message found: " + text);
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Success-message check failed: " + e.getMessage());
        }

        System.out.println("No success message detected");
        return false;
    }

    // ── Error capture (kept for BDD compatibility) ───────────────────────────
    public String captureAlertMessage() {
        try {
            WaitHelper.waitForAlert();
            Alert alert = driver.switchTo().alert();
            String message = alert.getText();
            alert.accept();
            return message;
        } catch (Exception e) {
            String inline = captureInlineErrorMessage();
            if (!inline.equals("No error message found")) return inline;

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
                if (!text.isEmpty()) return text;
            }
        } catch (Exception e) {
            System.out.println("Inline error check failed: " + e.getMessage());
        }       return "No error message found";
    }
}