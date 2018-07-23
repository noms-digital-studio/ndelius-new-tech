Feature: Parole report

Scenario : Delius user wants to enter intervention details for a prisoner in his parole report

Given that the Delius user is on the "Interventions" page within the Parole Report
And   they want to enter the intervention details for a prisoner
When  they enter the following information

| Detail the interventions the prisoner has completed | Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. |
| Interventions Summary| Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. |

Then this information should be saved in the prisoner parole report


Scenario: Delius user wants to know what details they should enter for "detail the interventions the prisoner has completed"

Given that the Delius user is in the Parole Report
And   they want to enter the intervention details for prisoner
When  they select "what to include" hyperlink
Then  the screen should be expanded to display the following text "Those designed to reduce risk, those designed to increase social capital, employment training"

Scenario: Delius user wants to leave the "Interventions" screen without putting any details in the free text fields

Given that the Delius user is in the "Interventions" page in the Parole report
And   the user does not any enter any characters in the free text fields on the page
When  they select the "Continue button"
Then  an error message must be displayed

Scenario: Delius user wants to continue entering Prisoner details in the Parole report

Given that the Delius user has entered details into "Detail the interventions the prisoner has completed" and "Interventions Summary" field
When  they select the Continue button UI
Then  the user should be directed to the "Current sentence plan and response" UI

Scenario: Delius user wants to close the report

Given that the Delius User is in the "Interventions" UI
When  they select Close button on the UI
Then  they should be directed to the "Draft Report Saved" UI
