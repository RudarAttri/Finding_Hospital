@diagnostics
Feature: Diagnostics Top Cities

  As a user of Practo
  I want to see all top cities listed on the Diagnostics page
  So that I can find diagnostic services in my city

  Background:
    Given I am on the Practo home page

  @topCities
  Scenario: Pick all top city names from the Diagnostics page and store in a list
    When I navigate to the Diagnostics page
    Then I should see the top cities listed
    And I store all city names in a list
    And I display all collected city names