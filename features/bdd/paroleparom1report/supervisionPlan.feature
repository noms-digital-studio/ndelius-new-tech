@Parom1
Feature: Parole Report - Supervision plan for release UI

  Background:
    Given Delius User is on the "Supervision plan for release" UI

  Scenario: User does not complete the "Supervision plan for release" UI

    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Detail the supervision plan for release | Enter the supervision plan for release |

  Scenario: User wants to close the report

    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI

  Scenario: Delius user wants to enter Resettlement details for an offender within their Parole report

    When  they enter the following information
      | Detail the supervision plan for release | Some supervision plan for release text |
    Then the following information should be saved in the report
      | supervisionPlanDetail | Some supervision plan for release text |
    And  they select the "Continue" button
    Then  the user should be directed to the "Recommendation" UI
