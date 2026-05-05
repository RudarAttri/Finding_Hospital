package pagedObjects;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utilities.WaitHelper;
import java.util.List;

/**
 * Page Object for the Practo Corporate Wellness page.
 * Fills the enquiry form with invalid data and captures alert/inline errors.
 */
public class CorporateWellnessPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────
    @FindBy(xpath = "//input[@placeholder='Name' or @name='name' or @id='name']")
    private WebElement nameField;

    @FindBy(xpath = "//input[@placeholder='Email' or @name='email' or @id='email']")
    private WebElement emailField;

    @FindBy(xpath = "//input[@placeholder='Mobile' or @name='phone' or @name='mobile']")
    private WebElement phoneField;

    @FindBy(xpath = "//input[@placeholder='Company' or @name='company' or @id='company']")
    private WebElement companyField;

    @FindBy(xpath = "//input[contains(@placeholder,'employee') or @name='employees']")
    private WebElement employeesField;

    @FindBy(xpath = "//button[contains(text(),'Schedule') or contains(text(),'Submit') or contains(text(),'Get')]")
    private WebElement scheduleButton;

    private static final By INLINE_ERROR = By.xpath(
            "//*[contains(@class,'error') or contains(@class,'warning') " +
                    "or contains(@class,'invalid') or contains(@class,'u-red-text')]");

    // ── Actions ───────────────────────────────────────────────────────
    public void navigateToCorporateWellness() {
        driver.get("https://www.practo.com/corporate-wellness");
        sleep(3000);
        System.out.println("Corporate Wellness loaded. URL: " + driver.getCurrentUrl());
    }

    public void enterInvalidName(String v) {
        WaitHelper.waitForVisibilityOfElement(nameField);
        scrollTo(nameField); nameField.clear(); nameField.sendKeys(v);
        System.out.println("Entered name: " + v);
    }

    public void enterInvalidEmail(String v) {
        WaitHelper.waitForVisibilityOfElement(emailField);
        scrollTo(emailField); emailField.clear(); emailField.sendKeys(v);
        System.out.println("Entered email: " + v);
    }

    public void enterInvalidPhone(String v) {
        try {
            WaitHelper.waitForVisibilityOfElement(phoneField);
            scrollTo(phoneField); phoneField.clear(); phoneField.sendKeys(v);
            System.out.println("Entered phone: " + v);
        } catch (Exception e) { System.out.println("Phone field not found: " + e.getMessage()); }
    }

    public void enterCompany(String v) {
        try {
            WaitHelper.waitForVisibilityOfElement(companyField);
            scrollTo(companyField); companyField.clear(); companyField.sendKeys(v);
        } catch (Exception e) { System.out.println("Company field not found: " + e.getMessage()); }
    }

    public void enterInvalidEmployeeCount(String v) {
        try {
            WaitHelper.waitForVisibilityOfElement(employeesField);
            scrollTo(employeesField); employeesField.clear(); employeesField.sendKeys(v);
        } catch (Exception e) { System.out.println("Employees field not found: " + e.getMessage()); }
    }

    public void clickSchedule() {
        WaitHelper.waitForVisibilityOfElement(scheduleButton);
        scrollTo(scheduleButton); sleep(500); jsClick(scheduleButton);
        System.out.println("Clicked schedule button"); sleep(2000);
    }

    /** Handles JS alert; falls back to inline DOM error if no alert. */
    public String captureAlertMessage() {
        try {
            WaitHelper.waitForAlert();
            Alert alert = driver.switchTo().alert();
            String message = alert.getText();
            System.out.println("JS Alert captured: " + message);
            alert.accept();
            return message;
        } catch (Exception e) {
            System.out.println("No JS alert — checking inline errors...");
            return captureInlineErrorMessage();
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
        } catch (Exception e) { System.out.println("Inline error check failed: " + e.getMessage()); }
        return "No error message found";
    }
}