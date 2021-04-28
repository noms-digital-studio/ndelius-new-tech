@Parom1
Feature: Parole Report

  Background:
  Given Delius user is on the "Resettlement plan for release" UI on the Parole Report

  Scenario: Delius user wants to enter Resettlement details for an offender within their Parole report

    When  they enter the following information
      | Detail the resettlement plan for release | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. |
    When they select the "Continue" button
    Then the user should be directed to the "Supervision plan for release" UI

  Scenario: User does not complete the "Resettlement plan for release" page

    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Detail the resettlement plan for release | Enter the resettlement plan for release |

  Scenario: User wants to close the report

    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI
