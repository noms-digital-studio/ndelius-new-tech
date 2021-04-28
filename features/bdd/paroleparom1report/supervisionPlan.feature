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
      | Detail the supervision plan for release | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. |
    Then this information should be saved in the report
    And the following information should be saved in the report
      | supervisionPlanRequired | yes |
    When  they select the "Continue" button
    Then  the user should be directed to the "Recommendation" UI
