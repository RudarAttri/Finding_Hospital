package pagedObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utilities.WaitHelper;
import java.util.List;
public class HospitalDetailPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────
    @FindBy(xpath = "//h1")
    private WebElement pageHeading;

    private static final By RATING = By.xpath(
            "//span[contains(@class,'common__star-rating__value')]");

    private static final By OPEN_24X7 = By.xpath(
            "//p[contains(@class,'u-green-text') " +
                    "and contains(normalize-space(text()),'Open 24')]");

    private static final By PARKING = By.xpath(
            "//*[contains(translate(text()," +
                    "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'parking')]");

    // ── Actions ───────────────────────────────────────────────────────
    public void openHospital(String url) {
        driver.get(url);
        sleep(3000);
        WaitHelper.waitForVisibilityOfElement(pageHeading);
        sleep(1000);
        System.out.println("   Opened: " + driver.getTitle());
    }

    public String getRating() {
        try {
            List<WebElement> els = driver.findElements(RATING);
            if (!els.isEmpty()) {
                String r = els.get(0).getText().trim();
                System.out.println("   Rating    : " + r);
                return r;
            }
        } catch (Exception e) {
            System.out.println("   Rating check failed: " + e.getMessage());
        }
        System.out.println("   Rating    : Not found");
        return "0";
    }

    public boolean isOpen24x7() {
        try {
            boolean open = !driver.findElements(OPEN_24X7).isEmpty();
            System.out.println("   Open 24x7 : " + open);
            return open;
        } catch (Exception e) {
            System.out.println("   Open 24x7 check failed: " + e.getMessage());
            return false;
        }
    }

    public boolean hasParkingFacility() {
        try {
            boolean parking = !driver.findElements(PARKING).isEmpty();
            System.out.println("   Parking   : " + parking);
            return parking;
        } catch (Exception e) {
            System.out.println("   Parking check failed: " + e.getMessage());
            return false;
        }
    }

    /** Returns true when Open24x7 AND Parking AND Rating > 3.5 */
    public boolean meetsAllCriteria() {
        String  ratingStr = getRating();
        boolean open      = isOpen24x7();
        boolean parking   = hasParkingFacility();
        try {
            double rating = Double.parseDouble(ratingStr);
            boolean match = open && parking && rating > 3.5;
            System.out.println("   >>> " + (match ? "MATCHED" : "NOT matched")
                    + " | Open24x7=" + open + " | Parking=" + parking
                    + " | Rating=" + ratingStr);
            return match;
        } catch (NumberFormatException e) {
            System.out.println("   Rating parse failed: " + ratingStr);
            return false;
        }
    }
}