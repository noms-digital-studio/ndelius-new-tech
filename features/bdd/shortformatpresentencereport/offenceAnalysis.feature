Feature: Short Format Pre-sentence Report - Offence analysis

  Background: Delius user is on the "Offence analysis" UI within the Short Format Pre-sentence Report
    Given that the Delius user is on the "Offence analysis" page within the Short Format Pre-sentence Report

  Scenario: Delius user wants to continue writing the Short Format Pre-sentence Report

    Given Delius User completes the "Offence analysis" UI within the Short Format Pre-sentence Report
    When they select the "Continue" button within the SFR
    Then  the user should be directed to the "Offender assessment" page within the SFR