Feature: Parole Report

  Background:
    Given that the Delius user is on the "Current sentence plan and response" page within the Parole Report

  Scenario: Delius user wants to enter details of the current sentence plan in the offender parole report

    Given that the Delius user wants to enter details of the current sentence plan in the offender parole report
    When  they enter the following information

      | Detail the prisoner`s current sentence plan objectives, including their response  | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. |

    Then this information should be saved in the prisoner parole report

  Scenario: Delius User does not enter any text into "Detail the prisoner`s current sentence plan objectives, including their response" free text field

    Given that the Delius user does not enter any text into "Detail the prisoner`s current sentence plan objectives, including their response" free text field
    When they select "continue"
    Then an error message must be displayed

  Scenario: Delius user wants to continue writing the Parole Report

    Given that the Delius user has entered text into the "Detail the prisoner`s current sentence plan objectives, including their response" free text field
    When  they select "continue"
    Then  they should be directed to "Multi Agency Public Protection Arrangements (MAPPA)" UI

  Scenario: Delius user wants to leave the parole report

    Given that the Delius User enters character into the "Detail the prisoner`s current sentence plan objectives, including their response" free text field
    When  they select "Close" button on the UI
    Then  they should be directed to the "Draft Report Saved" UI
