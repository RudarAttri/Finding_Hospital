package stepDefinitions;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.BaseClass;
import org.testng.Assert;
import pagedObjects.DoctorListPage;

import java.util.List;

public class DoctorsSteps extends BaseClass {

    // -----------------------------------------------------------------------
    // Shared state across steps within the same scenario
    // -----------------------------------------------------------------------
    private DoctorListPage doctorListPage;
    private List<String[]> collectedDoctors; // { name, experience }

    // -----------------------------------------------------------------------
    // Step 1
    // Feature line:
    //   When I click on "Find Doctors Near You" card on the home page
    // -----------------------------------------------------------------------
    @When("I click on {string} card on the home page")
    public void i_click_on_card_on_the_home_page(String cardName) {
        System.out.println("[DoctorsSteps] Clicking card: " + cardName);
        doctorListPage = new DoctorListPage();
        doctorListPage.clickFindDoctorsNearYouCard();
    }

    // -----------------------------------------------------------------------
    // Step 2
    // Feature line:
    //   Then the doctors listing page should load
    // -----------------------------------------------------------------------
    @Then("the doctors listing page should load")
    public void the_doctors_listing_page_should_load() {
        boolean loaded = doctorListPage.isDoctorsListingPageLoaded();
        Assert.assertTrue(loaded, "Doctors listing page did NOT load.");
        System.out.println("[DoctorsSteps] Doctors listing page loaded successfully.");
    }

    // -----------------------------------------------------------------------
    // Step 3
    // Feature line:
    //   When I search for "Dermatologist" in the search box and submit
    // ✅ Calls searchAndSubmit() — matches method in DoctorListPage exactly
    // -----------------------------------------------------------------------
    @When("I search for {string} in the search box and submit")
    public void i_search_for_in_the_search_box_and_submit(String speciality) {
        System.out.println("[DoctorsSteps] Searching for: " + speciality);
        doctorListPage.searchAndSubmit(speciality);   // ✅ correct method name
    }

    // -----------------------------------------------------------------------
    // Step 4
    // Feature line:
    //   Then the doctor results page should load with doctor cards
    // -----------------------------------------------------------------------
    @Then("the doctor results page should load with doctor cards")
    public void the_doctor_results_page_should_load_with_doctor_cards() {
        boolean loaded = doctorListPage.isDoctorResultsPageLoaded();
        Assert.assertTrue(loaded, "Doctor results page did NOT load.");
        System.out.println("[DoctorsSteps] Doctor results page loaded with cards.");
    }

    // -----------------------------------------------------------------------
    // Step 5
    // Feature line:
    //   When I collect the top 10 doctor details
    // -----------------------------------------------------------------------
    @When("I collect the top {int} doctor details")
    public void i_collect_the_top_doctor_details(int topN) {
        System.out.println("[DoctorsSteps] Collecting top " + topN + " doctors...");
        collectedDoctors = doctorListPage.collectTopDoctors(topN);
        Assert.assertFalse(collectedDoctors.isEmpty(),
                "No doctor details were collected from the results page.");
        System.out.println("[DoctorsSteps] Collected " + collectedDoctors.size() + " doctors.");
    }

    // -----------------------------------------------------------------------
    // Step 6
    // Feature line:
    //   Then I display the names and experience of all collected doctors
    // -----------------------------------------------------------------------
    @Then("I display the names and experience of all collected doctors")
    public void i_display_the_names_and_experience_of_all_collected_doctors() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║           DOCTORS — NAME & EXPERIENCE RESULTS                    ║");
        System.out.println("╠══════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  %-4s  %-38s  %-16s  ║%n", "No.", "Doctor Name", "Experience");
        System.out.println("╠══════════════════════════════════════════════════════════════════╣");

        for (int i = 0; i < collectedDoctors.size(); i++) {
            String name       = collectedDoctors.get(i)[0];
            String experience = collectedDoctors.get(i)[1];
            System.out.printf("║  %-4d  %-38s  %-16s  ║%n", (i + 1), name, experience);
        }

        System.out.println("╚══════════════════════════════════════════════════════════════════╝\n");

        for (String[] doctor : collectedDoctors) {
            Assert.assertNotEquals(doctor[0], "N/A",
                    "A doctor name could not be extracted.");
        }
    }
}