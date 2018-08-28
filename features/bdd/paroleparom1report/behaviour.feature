Feature: Parole report

  Background:
    Given that the Delius user is on the "Behaviour in prison" page within the Parole Report

  Scenario: Delius user wants to enter details of the offender's behaviour in Prison in the offender parole report

    Given: that the Delius user wants to enter details of the offender's behaviour in Prison in the offender parole report
    When:  they enter the following information

      | Detail the prisoner`s behaviour whilst in prison  | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Egestas purus viverra accumsan in nisl nisi scelerisque eu. |
      | RoTL summary                                      | Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.                                                                             |

    Then this information should be saved in the prisoner parole report

  Scenario: Delius user wants to leave the "Behaviour in Prison" page without entering any details into the "Detail the prisoner`s behaviour whilst in prison" free text fields

    Given that the Delius user does not enter any text into "Detail the prisoner`s behaviour whilst in prison" fields
    When they select "Continue" button
    Then an error message must be displayed

  Scenario: Delius user wants to leave the "Behaviour in Prison" page without entering any details into the "RoTL summary" free text fields

    Given that the Delius user does not enter any text into "RoTL summary" fields
    When they select "Continue" button
    Then an error message must be displayed

  Scenario: Delius user wants to leave the parole report

    Given that the Delius User is in the "Behaviour in Prison" UI
    When  they select "Close" button on the UI
    Then  they should be directed to the "Draft Report Saved" UI

  Scenario: Delius user wants to continue populating the Parole Report with information

    Given that the Delius User has completed all the relevant fields in "Behaviour in Prison" UI
    When they select "Continue" button
    Then the user must be directed to the "Interventions" Screen
