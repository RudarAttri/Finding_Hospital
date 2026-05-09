@hospitals
Feature: Finding Hospitals in Bangalore

  As a user of Practo
  I want to find hospitals in Bangalore that are open 24x7,
  have parking, and are rated above 3.5
  So that I can choose the best healthcare facility

  Background:
    Given I am on the Practo home page

  @findHospitals
  Scenario: Find hospitals that are Open 24x7, have Parking and Rating above 3.5
    When I search for hospitals in "Bangalore"
    Then the hospital listing page should load
    When I collect the top 10 hospital links
    Then I visit each hospital and filter by Open 24x7, Parking facility, and Rating above 3.5
    And I display the names of all matching hospitals