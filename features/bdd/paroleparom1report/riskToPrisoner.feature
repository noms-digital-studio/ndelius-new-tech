Feature: Parole Report

  Background:
    Given Delius user is on the "Risk to the prisoner" UI on the Parole Report

  Scenario: Delius user wants to add "Risk to the prisoner" details to the offender's Parole Report

    When they select the radio button with id "selfHarmCommunity_yes"
    When they select the radio button with id "selfHarmCustody_no"
    When they select the radio button with id "othersHarmCommunity_yes"
    When they select the radio button with id "othersHarmCommunity_no"
    When they select the "Continue" button
    Then the user should be directed to the "RoSH analysis" UI
    
  Scenario: Delius user wants to leave the parole report

    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI
