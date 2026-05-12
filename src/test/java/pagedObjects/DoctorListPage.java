package pagedObjects;

import factory.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class DoctorListPage {

    private WebDriver driver;
    private JavascriptExecutor js;


    // "Find Doctors Near You" card on home page
    private final By findDoctorsCard     = By.cssSelector("a[aria-label='Find Doctors Near You']");

    // Location input box — left box on /doctors page
    private final By locationBox         = By.cssSelector("input[data-qa-id='omni-searchbox-locality']");

    // Keyword input box — right box on /doctors page
    private final By keywordBox          = By.cssSelector("input[data-qa-id='omni-searchbox-keyword']");

    // Blue search button
    private final By searchButton        = By.cssSelector("button[data-qa-id='omni-searchbox-submit']");

    // Dropdown suggestion items
    private final By suggestionItem      = By.cssSelector("div[data-qa-id='omni-suggestion-listing']");

    // ✅ Doctor card wrapper — Image 2 DevTools:
    // <div class="listing-doctor-card" data-qa-id="doctor_card">
    private final By doctorCards         = By.cssSelector("div.listing-doctor-card");

    // ✅ Doctor name inside each card — Image 2 DevTools:
    // <h2 class="doctor-name">
    private final By doctorNameInCard    = By.cssSelector("h2.doctor-name");

    // ✅ Experience container — Image 2 & 3 DevTools:
    // <div class="uv2-spacer--xs-top" data-qa-id="doctor_experience">
    // Structure inside: "25" (text node) + <!-- --> + "&nbsp;" + <span>years experience overall</span>
    // Using data-qa-id which is most stable
    private final By experienceContainer = By.cssSelector("div[data-qa-id='doctor_experience']");

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------
    public DoctorListPage() {
        this.driver = DriverFactory.getDriver();
        this.js     = (JavascriptExecutor) driver;
    }

    // -----------------------------------------------------------------------
    // Actions
    // -----------------------------------------------------------------------

    public void clickFindDoctorsNearYouCard() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement card = wait.until(ExpectedConditions.elementToBeClickable(findDoctorsCard));
        card.click();
        System.out.println("[DoctorListPage] Clicked 'Find Doctors Near You' card.");
    }

    public boolean isDoctorsListingPageLoaded() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.urlContains("/doctors"));
        System.out.println("[DoctorListPage] Doctors page loaded: " + driver.getCurrentUrl());
        return driver.getCurrentUrl().contains("/doctors");
    }

    public void searchAndSubmit(String speciality) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Step A: Location box
        try {
            WebElement locBox = wait.until(ExpectedConditions.elementToBeClickable(locationBox));
            locBox.click();
            locBox.clear();
            locBox.sendKeys("Bangalore");
            System.out.println("[DoctorListPage] Typed 'Bangalore' in location box.");
            Thread.sleep(1200);
            List<WebElement> locSugg = driver.findElements(suggestionItem);
            if (!locSugg.isEmpty()) {
                locSugg.get(0).click();
                System.out.println("[DoctorListPage] Clicked first location suggestion.");
            }
            Thread.sleep(800);
        } catch (Exception e) {
            System.out.println("[DoctorListPage] Location box skipped: " + e.getMessage());
        }

        // Step B: Keyword box — type char by char
        try {
            Thread.sleep(500);
            WebElement kwBox = wait.until(ExpectedConditions.elementToBeClickable(keywordBox));
            kwBox.click();
            kwBox.clear();
            for (char c : speciality.toCharArray()) {
                kwBox.sendKeys(String.valueOf(c));
                Thread.sleep(80);
            }
            System.out.println("[DoctorListPage] Typed '" + speciality + "' in keyword box.");
            Thread.sleep(1500);

            // Click matching suggestion
            for (int i = 0; i < 10; i++) {
                try {
                    List<WebElement> fresh = driver.findElements(suggestionItem);
                    if (i >= fresh.size()) break;
                    String text = fresh.get(i).getText().trim();
                    System.out.println("[DoctorListPage] Suggestion [" + i + "]: " + text);
                    if (text.toLowerCase().contains(speciality.toLowerCase())) {
                        fresh.get(i).click();
                        System.out.println("[DoctorListPage] Clicked suggestion: " + text);
                        break;
                    }
                } catch (Exception ex) {
                    System.out.println("[DoctorListPage] Skipping stale suggestion at " + i);
                }
            }

            // Step C: Click Search button
            Thread.sleep(500);
            try {
                WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(searchButton));
                btn.click();
                System.out.println("[DoctorListPage] Clicked search button.");
            } catch (Exception e) {
                try {
                    WebElement btn = driver.findElement(searchButton);
                    js.executeScript("arguments[0].click();", btn);
                    System.out.println("[DoctorListPage] JS clicked search button.");
                } catch (Exception ex) {
                    System.out.println("[DoctorListPage] Search button not found.");
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean isDoctorResultsPageLoaded() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.urlContains("search"));
        System.out.println("[DoctorListPage] Results URL: " + driver.getCurrentUrl());
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(doctorCards));
        System.out.println("[DoctorListPage] Doctor cards loaded.");
        return true;
    }

    public List<String[]> collectTopDoctors(int topN) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(doctorCards));

        List<WebElement> cards = driver.findElements(doctorCards);
        int limit = Math.min(topN, cards.size());
        System.out.println("[DoctorListPage] Total cards: " + cards.size() + " | Collecting: " + limit);

        List<String[]> doctorData = new ArrayList<>();

        for (int i = 0; i < limit; i++) {

            // ✅ Re-fetch cards fresh each iteration to avoid stale reference
            List<WebElement> freshCards = driver.findElements(doctorCards);
            if (i >= freshCards.size()) break;
            WebElement card = freshCards.get(i);

            // ✅ Scroll the card into view using JavascriptExecutor
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", card);
            try { Thread.sleep(400); } catch (InterruptedException ignored) {}

            // --- Extract Name ---
            String name = "N/A";
            try {
                WebElement nameEl = card.findElement(doctorNameInCard);
                name = nameEl.getText().trim();
            } catch (Exception e) {
                System.out.println("[DoctorListPage] Name not found for card " + i);
            }

            // --- Extract Experience ---
            // ✅ Use JS textContent to read combined text node + span text
            // e.g. "25\nyears experience overall" → cleaned to "25 years experience overall"
            String experience = "N/A";
            try {
                WebElement expDiv = card.findElement(experienceContainer);
                // JS textContent gives us the raw combined text including text nodes
                String rawText = (String) js.executeScript(
                        "return arguments[0].textContent;", expDiv
                );
                if (rawText != null) {
                    // Clean up whitespace, newlines, &nbsp; characters
                    experience = rawText.replaceAll("\\s+", " ").trim();
                }
            } catch (Exception e) {
                System.out.println("[DoctorListPage] Experience not found for card " + i);
            }

            System.out.println("[DoctorListPage] " + (i + 1) + ". " + name + " | " + experience);
            doctorData.add(new String[]{name, experience});
        }

        return doctorData;
    }
}