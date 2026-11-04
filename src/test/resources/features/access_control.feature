@rbac
Feature: Access control
  Each role sees only the actions it is allowed to perform,
  and the API enforces the same rules when the UI is bypassed.

  Scenario Outline: Creating shipments is <availability> for a <persona>
    Given I am signed in as "<persona>"
    When I open the new shipment page
    Then the new shipment form is <availability>

    Examples:
      | persona  | availability  |
      | admin    | available     |
      | operator | available     |
      | viewer   | not available |

  Scenario Outline: Cancelling shipments is <availability> for a <persona>
    Given a shipment created by "admin"
    And I am signed in as "<persona>"
    When I open that shipment
    Then the cancel action is <availability>

    Examples:
      | persona  | availability  |
      | admin    | available     |
      | operator | not available |
      | viewer   | not available |

  @api
  Scenario Outline: The API refuses a <persona> who bypasses the UI to <action>
    Given a shipment created by "admin"
    When "<persona>" calls the API to <action>
    Then the API answers with status <status>

    Examples:
      | persona  | action               | status |
      | viewer   | create a shipment    | 403    |
      | operator | cancel that shipment | 403    |
      | admin    | cancel that shipment | 200    |

  @tenancy
  Scenario: Another company cannot open our shipment
    Given a shipment created by "operator"
    And I am signed in as "globex operator"
    When I open that shipment
    Then I see the page title "Shipment not found"
    And the API hides that shipment from "globex operator" with status 404

  @tenancy
  Scenario: Another company cannot find our shipment by searching
    Given a shipment created by "operator"
    And I am signed in as "globex operator"
    When I search for that shipment's reference
    Then I see the message "No shipments match your filters."
