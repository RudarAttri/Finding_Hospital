package pagedObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import utilities.WaitHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for Practo Diagnostics page.
 * Extracts all top-city names displayed in the cities section.
 */
public class DiagnosticsPage extends BasePage {

    // Selector targets common "city link" patterns — adjust if DOM changes
    private static final By TOP_CITY_ITEMS = By.xpath(
            "//div[contains(@class,'c-city-list')]//a | " +
                    "//section[contains(.,'Top Cities')]//a | " +
                    "//div[contains(@class,'city')]//span[not(contains(@class,'icon'))]");

    public void waitForPageLoad() {
        sleep(3000);
        System.out.println("Diagnostics page loaded. URL: " + driver.getCurrentUrl());
    }

    public void navigateToDiagnostics() {
        driver.get("https://www.practo.com/diagnostics");
        waitForPageLoad();
    }

    /**
     * Collects all visible city names from the diagnostics page.
     * @return List of city name strings
     */
    public List<String> getTopCityNames() {
        List<String>     cityNames = new ArrayList<>();
        List<WebElement> cityEls   = driver.findElements(TOP_CITY_ITEMS);
        System.out.println("City elements found: " + cityEls.size());

        for (WebElement el : cityEls) {
            try {
                String text = el.getText().trim();
                if (!text.isEmpty()) cityNames.add(text);
            } catch (Exception ignored) { /* stale element — skip */ }
        }

        System.out.println("Top cities collected: " + cityNames);
        return cityNames;
    }
}