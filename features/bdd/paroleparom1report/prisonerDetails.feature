Feature: Parole Report

  Background: Delius user is on the "Prisoner details" UI within the Parole Report
    Given that the Delius user is on the "Prisoner details" page within the Parole Report

  Scenario: Delius user wants to enter details for Male prisoner whom has Indeterminate sentence

    Given that the delius user want to enter for Male prisoner who has Indeterminate sentence
    And they input the following information
      | Prison or Young Offender Institution | Doncaster          |
      | Prisoner`s full name                 | Kieron Dobson      |
      | Prison number                        | P98793-123         |
      | NOMIS number                         | N2124214-3423      |
    And they select the "A" option on the "Current prison category"
    And they enter the following information
      | Offence                              | Aggravated assault |
      | Sentence                             | 4 years            |
    And they select the "Indeterminate" option on the "Sentence type"
    And they input the following information
      | Tariff length                        | 5 years            |
    And they enter the date "29/06/2019" for "Tariff expiry date"
    Then this information should be saved in the prisoner parole report

#Scenario: Delius user wants to enter details for Male prisoner whom has Determinate sentence
#
#Given that the delius user want to enter for Male prisoner who has Determinate sentence
#When  they enter the following information
#
#| Prison or Young Offender Institution                        | Doncaster                           |
#| Prisoner's full name                                        | Kieron Dobson                       |
#| Prison number                                               | P98793-123                          |
#| NOMIS number                                                | N2124214-3423                       |
#| Prisoner's Category                                         | C                                   |
#| Offence                                                     | Aggravated assault                  |
#| Sentence                                                    | 20 years                            |
#| Sentence type                                               | Determinate                         |
#| Automatic release date / non parole eligibility date Day    | 08                                  |
#| Automatic release date / non parole eligibility date Month  | 11                                  |
#| Automatic release date / non parole eligibility date Year   | 2031                                |
#
#Then this information should be saved in the report
#
#
#Scenario: Delius user wants to enter details for Female prisoner who has Indeterminate sentence
#
#Given that the delius user want to enter for Male prisoner who has Determinate sentence
#When  they enter the following information
#
#| Prison or Young Offender Institution  | York                                |
#| Prisoner's full name                  | Jane Doe                            |
#| Prison number                         | P98793-123                          |
#| NOMIS number                          | N2124214-3423                       |
#| Prisoner's Category                   | Restricted                          |
#| Offence                               | Aggravated assault                  |
#| Sentence                              | 5 years                             |
#| Sentence type                         | Determinate                         |
#| Tariff Length                         | 5 years                             |
#| Day                                   | 29                                  |
#| Month                                 | 6                                   |
#| Year                                  | 2019                                |
#
#
#Scenario: Delius user wants to enter details for female prisoner whom has Determinate sentence
#
#Given that the delius user want to enter for Male prisoner who has Determinate sentence
#When  they enter the following information
#
#| Prison or Young Offender Institution                        | Doncaster                           |
#| Prisoner's full name                                        | Jane Doe                            |
#| Prison number                                               | P98793-123                          |
#| NOMIS number                                                | N2124214-3423                       |
#| Prisoner's Category                                         | A                                   |
#| Offence                                                     | Aggravated assault                  |
#| Sentence                                                    | 20 years                            |
#| Sentence type                                               | Determinate                         |
#| Parole eligibility date Day                                 | 08                                  |
#| Parole eligibility date Month                               | 12                                  |
#| Parole eligibility date Year                                | 2021                                |
#
#Then this information should be saved in the report
#
#Scenario: Delius user wants to close the parole report
#
#Given that the Delius user wants to close the parole report
#When  they select "close"
#Then  they should be directed to the "Draft report" UI
#
#Scenario: Delius user wants to continue writing the Parole report
#
#Given that the Delius user has completed all the relevant fields within the "Prisoner details" UI
#When  they select "continue"
#Then  they should be directed to the "Offender manager: prisoner contact" UI
#
#Scenario: Delius user does not complete all the relevant fields on the UI for an offender whom has indeterminate sentence
#
#Given that the delius user does not complete all the relevant fields on the UI
#When  they select "continue"
#Then  the following information must be displayed
#| Prison or Young Offender Institution  | Enter the prison or young offender Institution               |
#| Prisoner's full name                  | Enter the prisoner’s full name                               |
#| Prison number                         | Enter the prison number                                      |
#| NOMIS number                          | Enter the NOMIS number                                       |
#| Prisoner's Category                   | Select the current prison category                           |
#| Offence                               | Enter the offence                                            |
#| Sentence                              | Enter the sentence                                           |
#| Sentence type                         | Select the sentence type                                     |
#| Tariff Length                         | Enter the tariff length                                      |
#| Day                                   | Enter the tariff expiry date                                 |
#| Month                                 | Enter the tariff expiry date                                 |
#| Year                                  | Enter the tariff expiry date                                 |
#
#
#
#Scenario: Delius user does not complete all the relevant fields on the UI for an offender whom has Determinate sentence
#
#Given that the delius user does not complete all the relevant fields on the UI
#When  they select "continue"
#Then  the following information must be displayed
#| Prison or Young Offender Institution                        | Enter the prison or young offender Institution               |
#| Prisoner's full name                                        | Enter the prisoner’s full name                               |
#| Prison number                                               | Enter the prison number                                      |
#| NOMIS number                                                | Enter the NOMIS number                                       |
#| Prisoner's Category                                         | Select the current prison category                           |
#| Offence                                                     | Enter the offence                                            |
#| Sentence                                                    | Enter the sentence                                           |
#| Sentence type                                               | Select the sentence type                                     |
#
#
#Scenario: Delius user does not complete all the relevant fields on the UI for an offender whom has Indeterminate sentence
#
#Given that the delius user does not complete all the relevant fields on the UI
#When  they select "continue"
#Then  the following information must be displayed
#| Prison or Young Offender Institution                        | Enter the prison or young offender Institution               |
#| Prisoner's full name                                        | Enter the prisoner’s full name                               |
#| Prison number                                               | Enter the prison number                                      |
#| NOMIS number                                                | Enter the NOMIS number                                       |
#| Prisoner's Category                                         | Select the current prison category                           |
#| Offence                                                     | Enter the offence                                            |
#| Sentence                                                    | Enter the sentence                                           |
#| Sentence type                                               | Select the sentence type                                     |
#| Tariff Length                                               | Enter the tariff length                                      |
#| Day                                                         | Enter the tariff expiry date                                 |
#| Month                                                       | Enter the tariff expiry date                                 |
#| Year                                                        | Enter the tariff expiry date                                 |
