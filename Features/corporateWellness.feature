@corporateWellness
Feature: Corporate Wellness Form Validation

  As a user of Practo
  I want to verify that the Corporate Wellness form shows warnings
  When invalid details are submitted

  Background:
    Given I am on the Practo home page

  @invalidForm
  Scenario: Fill invalid details in Corporate Wellness form and capture warning
    When I navigate to the Corporate Wellness page
    And I fill the form with invalid details
      | field     | value           |
      | name      | 1234!@#         |
      | email     | invalid-email@@ |
      | phone     | abc             |
      | company   | Test Corp       |
      | employees | xyz             |
    And I click the Schedule button
    Then a warning or error message should be displayed
    And I capture and display the warning message