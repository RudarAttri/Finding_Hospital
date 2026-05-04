package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws InterruptedException {

        WebDriver driver = new ChromeDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        driver.manage().window().maximize();
        driver.get("https://www.practo.com");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Step 1: Type Bangalore in location box
        try {
            WebElement locationBox = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("input[data-qa-id='omni-searchbox-locality']")
                    ));
            js.executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", locationBox);
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", locationBox);
            locationBox.clear();
            locationBox.sendKeys("Bangalore");
            System.out.println("Typed Bangalore in location box");
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Location box failed: " + e.getMessage());
        }

        // Step 2: Click "Bangalore" CITY from auto-suggestion
        try {
            WebElement locationSuggestion = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[@id=\"c-omni-container\"]/div/div[1]/div[2]/div[2]/div[1]")
                    ));
            js.executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", locationSuggestion);
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", locationSuggestion);
            System.out.println("Clicked Bangalore from location suggestion");
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Location suggestion click failed: " + e.getMessage());
        }

        // Step 3: Type hospital in search box
        try {
            WebElement searchBox = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("input[data-qa-id='omni-searchbox-keyword']")
                    ));
            js.executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", searchBox);
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", searchBox);
            searchBox.clear();
            searchBox.sendKeys("hospital");
            System.out.println("Typed hospital in search box");
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Search box failed: " + e.getMessage());
        }

        // Step 4: Click "Hospital" TYPE from auto-suggestion
        try {
            WebElement hospitalSuggestion = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[@id=\"c-omni-container\"]/div/div[2]/div[2]/div[1]/div[4]")
                    ));
            js.executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", hospitalSuggestion);
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", hospitalSuggestion);
            System.out.println("Clicked Hospital - TYPE from suggestion");
            Thread.sleep(3000);
        } catch (Exception e) {
            System.out.println("Hospital suggestion click failed: " + e.getMessage());
        }

        System.out.println("Title: " + driver.getTitle());
        System.out.println("Current URL: " + driver.getCurrentUrl());

        // Step 5: Wait for listing page
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@id=\"container\"]/div/div[3]/div/div[1]")));
            Thread.sleep(2000);
            System.out.println("Listing page loaded");
        } catch (Exception e) {
            System.out.println("Listing page load failed: " + e.getMessage());
        }

        // Step 6: Save base listing page URL (without page number)
        String baseListingUrl = driver.getCurrentUrl();

        // Step 7: COLLECT HOSPITALS ACROSS PAGES USING PAGINATION
        ArrayList<String> hospitalLinks = new ArrayList<>();
        ArrayList<String> hospitalNames = new ArrayList<>();

        int desiredCount = 50;     // <-- how many hospitals you want
        int currentPage  = 1;
        int maxPages     = 10;     // safety limit

        while (hospitalLinks.size() < desiredCount && currentPage <= maxPages) {

            System.out.println("\n========== PAGE " + currentPage + " ==========");

            // Wait for current page to load
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[@id=\"container\"]/div/div[3]/div/div[1]//ol//li")));
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println("Page " + currentPage + " load failed: " + e.getMessage());
                break;
            }

            // Scroll to bottom to ensure all cards on this page render
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(2000);

            // Get all hospital cards on current page
            List<WebElement> hospitalItems = driver.findElements(
                    By.xpath("//*[@id=\"container\"]/div/div[3]/div/div[1]//ol//li[.//div[contains(@class,'c-estb-card')]]"));

            System.out.println("Hospitals found on page " + currentPage + ": " + hospitalItems.size());

            // Collect from this page
            for (WebElement item : hospitalItems) {
                if (hospitalLinks.size() >= desiredCount) break;

                try {
                    js.executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", item);
                    Thread.sleep(200);

                    WebElement nameLink = item.findElement(
                            By.xpath(".//div[contains(@class,'c-estb-card')]" +
                                    "//div[contains(@class,'col-2')]//a"));

                    String name = nameLink.getText().trim();
                    String link = nameLink.getAttribute("href");

                    if (!hospitalLinks.contains(link)) {
                        hospitalNames.add(name);
                        hospitalLinks.add(link);
                        System.out.println("Collected (" + hospitalLinks.size() + "): " + name);
                    }
                } catch (Exception e) {
                    System.out.println("Failed to collect a card: " + e.getMessage());
                }
            }

            // Stop if we already have enough
            if (hospitalLinks.size() >= desiredCount) {
                System.out.println("Reached desired count of " + desiredCount);
                break;
            }

            // Navigate to next page using URL parameter (most reliable)
            currentPage++;
            String nextPageUrl;
            if (baseListingUrl.contains("page=")) {
                nextPageUrl = baseListingUrl.replaceAll("page=\\d+", "page=" + currentPage);
            } else if (baseListingUrl.contains("?")) {
                nextPageUrl = baseListingUrl + "&page=" + currentPage;
            } else {
                nextPageUrl = baseListingUrl + "?page=" + currentPage;
            }

            System.out.println("Navigating to page " + currentPage + ": " + nextPageUrl);
            driver.get(nextPageUrl);
            Thread.sleep(3000);
        }

        System.out.println("\n>>> Total hospitals collected: " + hospitalLinks.size() + " <<<");

        // Step 8: ArrayList to store matching hospitals
        ArrayList<String> matchingHospitals = new ArrayList<>();

        System.out.println("\n--- Navigating into each hospital ---\n");

        // Step 9: Visit each hospital detail page
        for (int i = 20; i < hospitalLinks.size(); i++) {

            String hospitalName = hospitalNames.get(i);
            String hospitalUrl  = hospitalLinks.get(i);

            System.out.println("========================================");
            System.out.println("Visiting [" + (i + 1) + "/" + hospitalLinks.size() + "] : " + hospitalName);

            boolean isOpen24x7 = false;
            String  rating     = "0";

            try {
                driver.get(hospitalUrl);
                Thread.sleep(3000);

                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//h1")));
                Thread.sleep(1000);

                // Check Rating
                try {
                    List<WebElement> ratingElements = driver.findElements(
                            By.xpath("//span[contains(@class,'common__star-rating__value')]"));
                    if (!ratingElements.isEmpty()) {
                        rating = ratingElements.get(0).getText().trim();
                        System.out.println("   Rating    : " + rating);
                    } else {
                        System.out.println("   Rating    : Not found");
                    }
                } catch (Exception e) {
                    System.out.println("   Rating check failed: " + e.getMessage());
                }

                // Check Open 24x7
                try {
                    List<WebElement> open24x7Elements = driver.findElements(
                            By.xpath("//p[contains(@class,'u-green-text') " +
                                    "and contains(normalize-space(text()),'Open 24')]"));
                    isOpen24x7 = !open24x7Elements.isEmpty();
                    System.out.println("   Open 24x7 : " + isOpen24x7);
                } catch (Exception e) {
                    System.out.println("   Open 24x7 check failed: " + e.getMessage());
                }

                // Apply filter
                try {
                    double ratingValue = Double.parseDouble(rating);
                    if (isOpen24x7 && ratingValue > 3.5) {
                        matchingHospitals.add(hospitalName +
                                " | Rating: " + rating +
                                " | Open 24x7: Yes");
                        System.out.println("   >>> MATCHED <<<");
                    } else {
                        System.out.println("   >>> NOT matched | Open24x7=" +
                                isOpen24x7 + " | Rating=" + rating);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("   Rating parse failed: " + rating);
                }

            } catch (Exception e) {
                System.out.println("   Detail page error: " + e.getMessage());
            }

            // Navigate back
            try {
                driver.navigate().back();
                Thread.sleep(2000);
            } catch (Exception e) {
                System.out.println("   Navigate back failed: " + e.getMessage());
            }
        }

        // Step 10: Print final results
        System.out.println("\n============================================================");
        System.out.println("   FINAL RESULT: Hospitals Open 24x7 with Rating > 3.5     ");
        System.out.println("============================================================\n");

        if (matchingHospitals.isEmpty()) {
            System.out.println("No hospitals matched all conditions.");
        } else {
            for (int i = 0; i < matchingHospitals.size(); i++) {
                System.out.println((i + 1) + ". " + matchingHospitals.get(i));
            }
        }

        System.out.println("\n============================================================");
        System.out.println("Total Hospitals Checked  : " + hospitalLinks.size());
        System.out.println("Total Matching Hospitals : " + matchingHospitals.size());
        System.out.println("============================================================");

        Thread.sleep(5000);
        driver.quit();
    }
}