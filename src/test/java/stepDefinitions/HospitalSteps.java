package stepDefinitions;

import io.cucumber.java.en.*;
import org.junit.Assert;
import pagedObjects.*;
import java.util.*;

public class HospitalSteps {

    private final HomePage            homePage    = new HomePage();
    private final HospitalListingPage listingPage = new HospitalListingPage();
    private final HospitalDetailPage  detailPage  = new HospitalDetailPage();

    private List<String[]> hospitalLinks = new ArrayList<>();
    private List<String>   matchingNames = new ArrayList<>();
    private String         listingUrl    = "";

    // ── Background ────────────────────────────────────────────────────
    @Given("I am on the Practo home page")
    public void iAmOnThePractoHomePage() {
        System.out.println("On Practo home page. URL: " + homePage.getCurrentUrl());
        Assert.assertTrue("Should be on practo.com",
                homePage.getCurrentUrl().contains("practo.com"));
    }

    // ── Scenario steps ────────────────────────────────────────────────
    @When("I search for hospitals in {string}")
    public void iSearchForHospitalsIn(String city) {
        homePage.searchForHospitalsIn(city);
    }

    @Then("the hospital listing page should load")
    public void theListingPageShouldLoad() {
        listingPage.waitForListingToLoad();
        listingUrl = listingPage.getListingPageUrl();
        System.out.println("Listing URL: " + listingUrl);
        Assert.assertFalse("Listing URL must not be empty", listingUrl.isEmpty());
    }

    @When("I collect the top {int} hospital links")
    public void iCollectTopHospitalLinks(int limit) {
        hospitalLinks = listingPage.collectHospitalLinks(limit);
        System.out.println("Total hospitals collected: " + hospitalLinks.size());
        Assert.assertFalse("At least one hospital must be found", hospitalLinks.isEmpty());
    }

    @Then("I visit each hospital and filter by Open 24x7, Parking facility, and Rating above 3.5")
    public void iVisitAndFilter() {
        System.out.println("\n--- Visiting each hospital detail page ---\n");

        for (int i = 0; i < hospitalLinks.size(); i++) {
            String name = hospitalLinks.get(i)[0];
            String url  = hospitalLinks.get(i)[1];

            System.out.println("========================================");
            System.out.println("Visiting [" + (i+1) + "] : " + name);

            detailPage.openHospital(url);

            if (detailPage.meetsAllCriteria()) {
                matchingNames.add(name
                        + " | Rating: " + detailPage.getRating()
                        + " | Open 24x7: Yes | Parking: Yes");
            }

            listingPage.returnToListing(listingUrl);
        }
    }

    @And("I display the names of all matching hospitals")
    public void iDisplayMatchingHospitals() {
        System.out.println("\n============================================================");
        System.out.println("  RESULT: Hospitals — Open 24x7 + Parking + Rating > 3.5  ");
        System.out.println("============================================================\n");

        if (matchingNames.isEmpty()) {
            System.out.println("No hospitals matched all 3 conditions.");
        } else {
            for (int i = 0; i < matchingNames.size(); i++) {
                System.out.println((i+1) + ". " + matchingNames.get(i));
            }
        }

        System.out.println("\nTotal Matching: " + matchingNames.size());
        System.out.println("============================================================");
        Assert.assertNotNull(matchingNames);
    }
}