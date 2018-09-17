Feature: Parole Report

  Background:
    Given Delius user is on the "Current RoSH community" UI on the Parole Report

  Scenario: Delius user wants to enter RoSH community data for an offender within their parole report

    When they select the "Low" option on the "Public"
    And they select the "Medium" option on the "Known adult"
    And they select the "High" option on the "Children"
    And they select the "Very high" option on the "Prisoners"
    And they select the "Low" option on the "Staff"
    When they select the "Continue" button
    Then the user should be directed to the "Current ROSH: custody" UI

  Scenario: Delius User does not complete the relevant fields within the "RoSH Analysis" UI

    When  they select the "Continue" button
    Then  the following error messages are displayed
      | Public | Select the risk to the public |
      | Known adult | Select the risk to any known adult |
      | Children | Select the risk to children |
      | Prisoners | Select the risk to prisoners |
      | Staff | Select the risk to staff |

  Scenario: Delius user wants to leave the parole report

    When  they select the "Close" button
    Then  the user should be directed to the "Draft report saved" UI