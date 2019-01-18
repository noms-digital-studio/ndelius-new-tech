Feature: Offender Summary

  Background: Delius user is on the "Offender Summary" UI

  Scenario: Delius user wants to view the details of Kieron Robinson next appointment

    Given that Kieron Robinson has the following details saved for their next appointment
      | Contact type | 3 Way Meeting (NS) |
      | Date         | 07/12/2018         |
      | Start time   | 12:28              |
      | Location     | 1 REGARTH AVENUE   |
      | Provider     | NPS London         |
      | Team         | OMU A              |
      | Officer      | Pheim, Sophie Zz   |
    And they navigate to the offender summary page
    When the Delius user selects the "Offender manager" link on the "Offender Summary" UI
    And they expand the "Next appointment details" content section
    Then the screen should expand to show the following next appointment
      | Contact type | 3 Way Meeting (NS) |
      | Date         | 07/12/2018         |
      | Start time   | 12:28              |
      | Location     | 1 REGARTH AVENUE   |
      | Provider     | NPS London         |
      | Team         | OMU A              |
      | Officer      | Pheim, Sophie Zz   |


  Scenario: Offender does not have a next appointment

    Given that the offender does not have a next appointment within Delius
    And they navigate to the offender summary page
    When the Delius user selects the "Offender manager" link on the "Offender Summary" UI
    And they expand the "Next appointment details" content section
    Then the screen should expand to show the following next appointment
      | Contact type | Unknown |
      | Date         | Unknown |
      | Start time   | Unknown |
      | Location     | Unknown |
      | Provider     | Unknown |
      | Team         | Unknown |
      | Officer      | Unknown |