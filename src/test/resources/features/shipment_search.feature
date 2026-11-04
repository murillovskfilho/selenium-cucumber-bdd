@search
Feature: Finding shipments
  As an operator
  I want to search and filter my company's shipments
  So that I can answer customer questions quickly

  Background:
    Given I am signed in as "operator"

  @smoke
  Scenario: Find a shipment by its reference
    Given a shipment created by "operator" with:
      | Carrier        | DHL               |
      | Recipient name | Katherine Johnson |
    When I search for that shipment's reference
    Then the list shows only that shipment with:
      | Carrier   | DHL               |
      | Recipient | Katherine Johnson |
      | Status    | Label created     |

  Scenario: Filter by status
    When I filter the list by status "Delivered"
    Then every shipment in the list has the status "Delivered"

  Scenario: Nothing matches the search
    When I search for "NO-SUCH-REFERENCE"
    Then I see the message "No shipments match your filters."

  Scenario: Page through a long result
    When I search for "ACME-PO"
    Then I see "Page 1 of 3" with 10 shipments
    When I go to the next page
    Then I see "Page 2 of 3" with 10 shipments
