package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws InterruptedException {

        //System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");

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

        // Step 5: Wait for listing page to load
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@id=\"container\"]/div/div[3]/div/div[1]")));
            Thread.sleep(2000);
            System.out.println("Listing page loaded");
        } catch (Exception e) {
            System.out.println("Listing page load failed: " + e.getMessage());
        }

        // Step 6: Save listing page URL to navigate back
        String listingPageUrl = driver.getCurrentUrl();

        // Step 7: Collect top 5 hospital links and names
        ArrayList<String> hospitalLinks = new ArrayList<>();
        ArrayList<String> hospitalNames = new ArrayList<>();

        try {
            List<WebElement> hospitalItems = driver.findElements(
                    By.xpath("//*[@id=\"container\"]/div/div[3]/div/div[1]//ol//li[.//div[contains(@class,'c-estb-card')]]"));

            System.out.println("Total hospital items found: " + hospitalItems.size());

            int limit = Math.min(5, hospitalItems.size());

            for (int i = 0; i < limit; i++) {
                try {
                    WebElement item = hospitalItems.get(i);
                    js.executeScript("arguments[0].scrollIntoView({behavior:'smooth', block:'center'});", item);
                    Thread.sleep(500);

                    WebElement nameLink = item.findElement(
                            By.xpath(".//div[contains(@class,'c-estb-card')]" +
                                    "//div[contains(@class,'col-2')]//a"));

                    String name = nameLink.getText().trim();
                    String link = nameLink.getAttribute("href");

                    hospitalNames.add(name);
                    hospitalLinks.add(link);

                    System.out.println("Collected (" + (i + 1) + "): " + name);
                } catch (Exception e) {
                    System.out.println("Failed to collect hospital " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to find hospital items: " + e.getMessage());
        }

        // Step 8: ArrayList to store matching hospitals
        ArrayList<String> matchingHospitals = new ArrayList<>();

        // Create screenshots folder once before the loop
        File screenshotDir = new File("screenshots");
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
            System.out.println("Screenshots folder created at: " + screenshotDir.getAbsolutePath());
        }

        System.out.println("\n--- Navigating into each of top 5 hospitals ---\n");

        // Step 9: Visit each hospital detail page one by one
        for (int i = 0; i < hospitalLinks.size(); i++) {

            String hospitalName = hospitalNames.get(i);
            String hospitalUrl  = hospitalLinks.get(i);

            System.out.println("========================================");
            System.out.println("Visiting [" + (i + 1) + "] : " + hospitalName);

            boolean isOpen24x7 = false;
            String  rating     = "0";

            try {
                // Navigate into hospital detail page
                driver.get(hospitalUrl);
                Thread.sleep(3000);

                // Wait for detail page header to load
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//h1")));
                Thread.sleep(1000);

                // Take screenshot with unique name per hospital
                // Format: Hospital_1_Manipal_Hospital.png
                try {
                    String safeName = hospitalName.replaceAll("[^a-zA-Z0-9]", "_");
                    File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    File destination = Paths.get("screenshots",
                            "Hospital_" + (i + 1) + "_" + safeName + ".png").toFile();
                    FileHandler.copy(screenshot, destination);
                    System.out.println("   Screenshot saved: " + destination.getAbsolutePath());
                } catch (Exception e) {
                    System.out.println("   Screenshot failed: " + e.getMessage());
                }

                // --- Check Rating ---
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

                // --- Check Open 24x7 ---
                try {
                    List<WebElement> open24x7Elements = driver.findElements(
                            By.xpath("//p[contains(@class,'u-green-text') " +
                                    "and contains(normalize-space(text()),'Open 24')]"));
                    isOpen24x7 = !open24x7Elements.isEmpty();
                    System.out.println("   Open 24x7 : " + isOpen24x7);
                } catch (Exception e) {
                    System.out.println("   Open 24x7 check failed: " + e.getMessage());
                }

                // --- Apply filter ---
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

            // Step 10: Navigate back to listing page
            try {
                System.out.println("   Navigating back to listing page...");
                driver.navigate().back();
                Thread.sleep(2000);
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[@id=\"container\"]/div/div[3]/div/div[1]")));
                Thread.sleep(1000);
                System.out.println("   Back on listing page\n");
            } catch (Exception e) {
                driver.get(listingPageUrl);
                Thread.sleep(2000);
                System.out.println("   Used URL to go back to listing page\n");
            }
        }

        // Step 11: Print final results
        System.out.println("\n============================================================");
        System.out.println("   FINAL RESULT: Hospitals Open 24x7 with Rating > 3.5     ");
        System.out.println("============================================================\n");

        if (matchingHospitals.isEmpty()) {
            System.out.println("No hospitals matched all conditions in top 5.");
        } else {
            for (int i = 0; i < matchingHospitals.size(); i++) {
                System.out.println((i + 1) + ". " + matchingHospitals.get(i));
            }
        }

        System.out.println("\n============================================================");
        System.out.println("Total Matching Hospitals : " + matchingHospitals.size());
        System.out.println("============================================================");

        Thread.sleep(5000);
        driver.quit();
    }
}

