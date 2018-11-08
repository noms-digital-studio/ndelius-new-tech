Feature: Short Format Pre-sentence Report - Sources of information

  Background: Delius user is on the "Sources of information" UI within the Short Format Pre-sentence Report
    Given that the Delius user is on the "Sources of information" page within the Short Format Pre-sentence Report

  Scenario: Delius user specifies other source but does not enter any text into the required field

    Given that the "Other (please specify below)" is ticked
    When they select the "Continue" button
    Then  the following error messages are displayed
      | Other Source(s) of Information | Enter the other information source details |

  Scenario: Delius user wants to continue writing the Short Format Pre-sentence Report

    Given Delius User completes the "Sources of information" UI within the Short Format Pre-sentence Report
    Then the user should be directed to the "Check your report" UI