Feature: Parole Report - Prisoner contact

  Background:
    Given Delius User is on the "MAPPA" UI within the Parole Report

  Scenario: Delius user does not specify if the prisoner is eligible for MAPPA

    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Is the prisoner eligible for MAPPA? | Specify if the prisoner is eligible for MAPPA |

  Scenario: Delius user specifies that the prisoner is eligible for MAPPA but does not enter any information

    Given they select the "Yes" option on the "Is the prisoner eligible for MAPPA?"
    When  they select the "Continue" button
    Then  the following error messages are displayed
      | When was the prisoner screened for MAPPA (MAPPA Q completed)? | Enter the date when the prisoner was screened for MAPPA |
      | What is the prisoner's current MAPPA category? | Select the prisoner's current MAPPA category |
      | What is the prisoner's current MAPPA level? | Select the prisoner's current MAPPA level |

  Scenario: Delius user wants to enter the Multi Agency Public Protection Arrangements (MAPPA) information

    Given they select the "Yes" option on the "Is the prisoner eligible for MAPPA?"
    And they enter the date "07/08/2018" for "What date was the prisoner screened for MAPPA (MAPPAQ completed)?"
    And they select the "1" option on the "What is the prisoner's current MAPPA category?"
    And they select the "2" option on the "What is the prisoner's current MAPPA level?"
    When they select the "Continue" button
    Then the user should be directed to the "Current risk assessment" UI

  Scenario: Delius user specifies prisoner is not eligible for MAPPA

    Given they select the "No" option on the "Is the prisoner eligible for MAPPA?"
    When they select the "Continue" button
    Then the user should be directed to the "Current risk assessment" UI

  Scenario: Delius user wants to close the report

    When they select the "Close" button
    Then the user should be directed to the "Draft report saved" UI
