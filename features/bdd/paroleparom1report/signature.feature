Feature: Parole Report

  Background:
    Given Delius User is on the "Signature" UI within the Parole Report

  Scenario: Delius user does not complete the relevant questions on the UI

    When they select the "Submit" button
    Then the following error messages are displayed
      | Name                              | Enter the report author                  |
      | NPS Division and LDU              | Enter the NPS division and LDU           |
      | Office address                    | Enter the office address                 |
      | Email address                     | Enter the email address                  |
      | Telephone number and extension    | Enter the telephone number and extension |
      | Completion date                   | Enter the completion date                |

  Scenario: Delius user wants to sign and date their parole report

    When they enter the following information
      | Name                              | Jane Doe                               |
      | NPS Division and LDU              | Stafford, Midlands                     |
      | Office address                    | 4 Lichfield Road, Stafford ST17 4JX    |
      | Email address                     | jane.doe@nps.gov.uk                    |
      | Telephone number and extension    | 0124 5896456                           |
      | Completion date                   | 06/12/2018                             |

    Then the following information should be saved in the prisoner parole report
      | signatureName                     | Jane Doe                               |
      | signatureDivision                 | Stafford, Midlands                     |
      | signatureOfficeAddress            | 4 Lichfield Road, Stafford ST17 4JX    |
      | signatureEmail                    | jane.doe@nps.gov.uk                    |
      | signatureTelephone                | 0124 5896456                           |
      | signatureDate                     | 06/12/2018                             |

  Scenario: The parole report needs to be counter signed

    When they enter the following information
      | Name                              | Jane Doe                               |
      | NPS Division and LDU              | Stafford, Midlands                     |
      | Office address                    | 4 Lichfield Road, Stafford ST17 4JX    |
      | Email address                     | jane.doe@nps.gov.uk                    |
      | Telephone number and extension    | 0124 5896456                           |
      | Name of countersignature          | Joe Bloggs                             |
      | Role of countersignature          | SPO                                    |
      | Completion date                   | 06/12/2018                             |

    Then the following information should be saved in the prisoner parole report
      | signatureName                     | Jane Doe                               |
      | signatureDivision                 | Stafford, Midlands                     |
      | signatureOfficeAddress            | 4 Lichfield Road, Stafford ST17 4JX    |
      | signatureEmail                    | jane.doe@nps.gov.uk                    |
      | signatureTelephone                | 0124 5896456                           |
      | signatureCounterName              | Joe Bloggs                             |
      | signatureCounterRole              | SPO                                    |
      | signatureDate                     | 06/12/2018                             |

  Scenario: Delius user wants to submit their Parole report WITHOUT counter signature

    When they enter the following information
      | Name                              | Jane Doe                               |
      | NPS Division and LDU              | Stafford, Midlands                     |
      | Office address                    | 4 Lichfield Road, Stafford ST17 4JX    |
      | Email address                     | jane.doe@nps.gov.uk                    |
      | Telephone number and extension    | 0124 5896456                           |
      | Completion date                   | 06/12/2018                             |

    And they select the "Submit" button
    Then the user should be directed to the "Report saved" UI

  Scenario: Delius user wants to submit their Parole report WITH counter signature

    When they enter the following information
      | Name                              | Jane Doe                               |
      | NPS Division and LDU              | Stafford, Midlands                     |
      | Office address                    | 4 Lichfield Road, Stafford ST17 4JX    |
      | Email address                     | jane.doe@nps.gov.uk                    |
      | Telephone number and extension    | 0124 5896456                           |
      | Name of countersignature          | Joe Bloggs                             |
      | Role of countersignature          | SPO                                    |
      | Completion date                   | 06/12/2018                             |

    And they select the "Submit" button
    Then the user should be directed to the "Report saved" UI