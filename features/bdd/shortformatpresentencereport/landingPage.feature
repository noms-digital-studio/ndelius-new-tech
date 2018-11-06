Feature: Short Format Pre-Sentence Report - Landing Page

  Scenario: User wants to start writing the Short Format Pre-Sentence Report

    Given that the user is on the SFR landing page
    When they select the "Start now" button on the SFR landing page
    Then the user should be directed to the "Offender details" page within the SFR
