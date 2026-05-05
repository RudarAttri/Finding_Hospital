package pagedObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utilities.WaitHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for the Practo Hospital Listing / Search Results page.
 */
public class HospitalListingPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────
    private static final By HOSPITAL_ITEMS = By.xpath(
            "//*[@id='container']/div/div[3]/div/div[1]//ol//li" +
                    "[.//div[contains(@class,'c-estb-card')]]");

    private static final By NAME_LINK_IN_CARD = By.xpath(
            ".//div[contains(@class,'c-estb-card')]" +
                    "//div[contains(@class,'col-2')]//a");

    // ── Actions ───────────────────────────────────────────────────────
    public void waitForListingToLoad() {
        WaitHelper.waitForPresence(HOSPITAL_ITEMS);
        sleep(2000);
        System.out.println("Hospital listing page loaded.");
    }

    /**
     * Collects up to 'limit' hospital entries as String[]{name, url}.
     */
    public List<String[]> collectHospitalLinks(int limit) {
        List<WebElement> items     = driver.findElements(HOSPITAL_ITEMS);
        List<String[]>   collected = new ArrayList<>();
        System.out.println("Total hospital cards found: " + items.size());

        int count = Math.min(limit, items.size());
        for (int i = 0; i < count; i++) {
            try {
                WebElement item = items.get(i);
                scrollTo(item);
                sleep(400);
                WebElement link = item.findElement(NAME_LINK_IN_CARD);
                String name = link.getText().trim();
                String url  = link.getAttribute("href");
                if (!name.isEmpty() && url != null) {
                    collected.add(new String[]{name, url});
                    System.out.println("Collected (" + (i+1) + "): " + name);
                }
            } catch (Exception e) {
                System.out.println("Could not collect hospital " + (i+1) + ": " + e.getMessage());
            }
        }
        return collected;
    }

    public String getListingPageUrl() { return driver.getCurrentUrl(); }

    public void returnToListing(String listingUrl) {
        try {
            driver.navigate().back();
            sleep(2000);
            WaitHelper.waitForPresence(HOSPITAL_ITEMS);
            sleep(1000);
            System.out.println("Navigated back via browser back button.");
        } catch (Exception e) {
            driver.get(listingUrl);
            sleep(2000);
            System.out.println("Navigated back via URL fallback.");
        }
    }
}