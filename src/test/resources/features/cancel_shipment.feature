@cancel
Feature: Cancelling a shipment
  As an administrator
  I want to cancel shipments that have not left the warehouse
  So that we do not pay for labels we will not use

  Background:
    Given a shipment created by "operator"
    And I am signed in as "admin"
    And I open that shipment

  @smoke
  Scenario: Cancel after confirming
    When I cancel the shipment and confirm
    Then I see the confirmation "Shipment cancelled."
    And the shipment status is "Cancelled"
    And the API reports the shipment as "CANCELLED"

  Scenario: Change of mind
    When I cancel the shipment but dismiss the confirmation
    Then the shipment status is "Label created"
    And the API reports the shipment as "LABEL_CREATED"

  Scenario: Someone else cancelled it first
    Given another administrator cancels that shipment
    When I cancel the shipment and confirm
    Then I see the error "A shipment in status CANCELLED cannot be cancelled"
