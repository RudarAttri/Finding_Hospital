@doctors
Feature: Finding Doctors Near You on Practo

  As a user of Practo
  I want to find doctors near me by clicking "Find Doctors Near You"
  So that I can view the names and experience of available doctors

  Background:
    Given I am on the Practo home page

  @findDoctors
  Scenario: Find doctors by clicking Find Doctors Near You and display their names and experience
    When I click on "Find Doctors Near You" card on the home page
    Then the doctors listing page should load
    When I search for "doctors" in the search box and submit
    Then the doctor results page should load with doctor cards
    When I collect the top 10 doctor details
    Then I display the names and experience of all collected doctors
