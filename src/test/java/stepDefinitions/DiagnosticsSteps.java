package stepDefinitions;
import io.cucumber.java.en.*;
import org.junit.Assert;
import pagedObjects.DiagnosticsPage;
import java.util.List;

public class DiagnosticsSteps {

    private final DiagnosticsPage diagnosticsPage = new DiagnosticsPage();
    private List<String> topCities;

    @When("I navigate to the Diagnostics page")
    public void iNavigateToTheDiagnosticsPage() {
        diagnosticsPage.navigateToDiagnostics();
    }

    @Then("I should see the top cities listed")
    public void iShouldSeeTopCitiesListed() {
        topCities = diagnosticsPage.getTopCityNames();
        Assert.assertFalse("At least one city must be listed", topCities.isEmpty());
    }

    @And("I store all city names in a list")
    public void iStoreAllCityNamesInAList() {
        System.out.println("Cities stored in list. Count: " + topCities.size());
        Assert.assertNotNull("City list must not be null", topCities);
    }

    @And("I display all collected city names")
    public void iDisplayAllCollectedCityNames() {
        System.out.println("\n============================================================");
        System.out.println("   Top Cities from Diagnostics Page                         ");
        System.out.println("============================================================\n");

        for (int i = 0; i < topCities.size(); i++) {
            System.out.println((i+1) + ". " + topCities.get(i));
        }

        System.out.println("\nTotal Cities Found: " + topCities.size());
        System.out.println("============================================================");
    }
}