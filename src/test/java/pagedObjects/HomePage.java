package pagedObjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utilities.WaitHelper;

public class HomePage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────
    @FindBy(css = "input[data-qa-id='omni-searchbox-locality']")
    private WebElement locationInput;

    @FindBy(css = "input[data-qa-id='omni-searchbox-keyword']")
    private WebElement keywordInput;

    // First city in location auto-suggestion dropdown
    @FindBy(xpath = "//*[@id='c-omni-container']/div/div[1]/div[2]/div[2]/div[1]")
    private WebElement firstLocationSuggestion;

    // 'Hospital' type entry (4th item) in keyword suggestion list
    @FindBy(xpath = "//*[@id='c-omni-container']/div/div[2]/div[2]/div[1]/div[4]")
    private WebElement hospitalTypeSuggestion;

    @FindBy(xpath = "//a[contains(@href,'diagnostics') or contains(text(),'Diagnostics')]")
    private WebElement diagnosticsLink;

    @FindBy(xpath = "//a[contains(text(),'Corporate Wellness') or contains(@href,'corporate')]")
    private WebElement corporateWellnessLink;

    // ── Actions ───────────────────────────────────────────────────────
    public void enterLocation(String city) {
        WaitHelper.waitForVisibilityOfElement(locationInput);
        scrollTo(locationInput);
        sleep(500);
        jsClick(locationInput);
        locationInput.clear();
        locationInput.sendKeys(city);
        System.out.println("Typed location: " + city);
        sleep(2000);
    }

    public void selectFirstLocationSuggestion() {
        WaitHelper.waitForVisibilityOfElement(firstLocationSuggestion);
        scrollTo(firstLocationSuggestion);
        sleep(500);
        jsClick(firstLocationSuggestion);
        System.out.println("Selected first location suggestion");
        sleep(2000);
    }

    public void enterKeyword(String keyword) {
        WaitHelper.waitForVisibilityOfElement(keywordInput);
        scrollTo(keywordInput);
        sleep(500);
        jsClick(keywordInput);
        keywordInput.clear();
        keywordInput.sendKeys(keyword);
        System.out.println("Typed keyword: " + keyword);
        sleep(2000);
    }

    public void selectHospitalTypeSuggestion() {
        WaitHelper.waitForVisibilityOfElement(hospitalTypeSuggestion);
        scrollTo(hospitalTypeSuggestion);
        sleep(500);
        jsClick(hospitalTypeSuggestion);
        System.out.println("Selected Hospital type from suggestion");
        sleep(3000);
    }

    /** Convenience: full search for hospitals in a city. */
    public void searchForHospitalsIn(String city) {
        enterLocation(city);
        selectFirstLocationSuggestion();
        enterKeyword("hospital");
        selectHospitalTypeSuggestion();
    }

    public void goToDiagnostics() {
        WaitHelper.waitForVisibilityOfElement(diagnosticsLink);
        jsClick(diagnosticsLink);
        sleep(2000);
        System.out.println("Navigated to Diagnostics page");
    }

    public void goToCorporateWellness() {
        WaitHelper.waitForVisibilityOfElement(corporateWellnessLink);
        jsClick(corporateWellnessLink);
        sleep(2000);
        System.out.println("Navigated to Corporate Wellness page");
    }
}