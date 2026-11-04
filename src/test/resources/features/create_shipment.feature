@create
Feature: Creating a shipment
  As an operator
  I want to register a shipment with valid details
  So that a label can be bought for it

  Background:
    Given I am signed in as "operator"
    And I am on the new shipment page

  @smoke
  Scenario: Create a shipment with valid details
    When I create a shipment with:
      | Carrier         | FEDEX        |
      | Service level   | OVERNIGHT    |
      | Recipient name  | Grace Hopper |
      | Destination ZIP | 94105        |
      | Weight (oz)     | 12.5         |
    Then I see the confirmation "Shipment created."
    And the shipment details show:
      | Status     | Label created      |
      | Carrier    | FEDEX              |
      | Weight     | 12.5 oz            |
      | Created by | operator@acme.test |
    And the API returns the same shipment

  Scenario Outline: Invalid "<field>" is rejected: <case>
    When I create a shipment with "<field>" set to "<value>"
    Then the "<field>" field shows the error "<message>"
    And the shipment is not created

    Examples:
      | case                 | field           | value  | message                                 |
      | empty                | Reference       |        | Reference is required                   |
      | contains spaces      | Reference       | PO 123 | Use 3-40 letters, digits or dashes      |
      | too short            | Recipient name  | A      | Recipient name is required              |
      | four digits          | Destination ZIP | 9410   | ZIP must be 5 digits                    |
      | letters              | Destination ZIP | 94I05  | ZIP must be 5 digits                    |
      | zero                 | Weight (oz)     | 0      | Weight must be between 0.1 and 2400 oz  |
      | above the limit      | Weight (oz)     | 2401   | Weight must be between 0.1 and 2400 oz  |
      | not selected         | Carrier         |        | Choose a carrier                        |

  Scenario: A reference can only be used once
    Given a shipment created by "operator"
    When I create a shipment with "Reference" set to that shipment's reference
    Then the "Reference" field shows the error "Reference already exists"
