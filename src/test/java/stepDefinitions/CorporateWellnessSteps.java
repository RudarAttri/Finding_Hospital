package stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import pagedObjects.CorporateWellnessPage;

import java.util.Map;

public class CorporateWellnessSteps {

    private final CorporateWellnessPage wellnessPage = new CorporateWellnessPage();
    private String warningMessage = "";

    @When("I navigate to the Corporate Wellness page")
    public void iNavigateToTheCorporateWellnessPage() {
        wellnessPage.navigateToCorporateWellness();
    }

    @And("I fill the form with invalid details")
    public void iFillTheFormWithInvalidDetails(DataTable dataTable) {
        // DataTable rows: | field | value |
        for (Map<String, String> row : dataTable.asMaps()) {
            String field = row.get("field");
            String value = row.get("value");

            if ("name".equalsIgnoreCase(field)) {
                wellnessPage.enterInvalidName(value);
            } else if ("email".equalsIgnoreCase(field)) {
                wellnessPage.enterInvalidEmail(value);
            } else if ("phone".equalsIgnoreCase(field)) {
                wellnessPage.enterInvalidPhone(value);
            } else if ("company".equalsIgnoreCase(field)) {
                wellnessPage.enterCompany(value);
            } else if ("employees".equalsIgnoreCase(field)) {
                wellnessPage.enterInvalidEmployeeCount(value);
            } else {
                System.out.println("Unknown field: " + field);
            }
        }
    }

    @And("I click the Schedule button")
    public void iClickTheScheduleButton() {
        wellnessPage.clickSchedule();
    }

    @Then("a warning or error message should be displayed")
    public void aWarningOrErrorMessageShouldBeDisplayed() {
        warningMessage = wellnessPage.captureAlertMessage();
        System.out.println("Warning message received: " + warningMessage);
        Assert.assertFalse("A warning message should have been displayed",
                warningMessage.isEmpty() || warningMessage.equals("No error message found"));
    }

    @And("I capture and display the warning message")
    public void iCaptureAndDisplayTheWarningMessage() {
        System.out.println("\n============================================================");
        System.out.println("   Corporate Wellness Form — Warning / Alert Message         ");
        System.out.println("============================================================");
        System.out.println("   Message : " + warningMessage);
        System.out.println("============================================================\n");
    }
}