@auth
Feature: Signing in
  As a ShipDesk user
  I want to sign in with my company account
  So that I only see and change my company's shipments

  @smoke
  Scenario: An operator signs in
    Given I am on the sign-in page
    When I sign in as "operator"
    Then I see the shipments list
    And I am identified as "Otto Operator" with the role "operator"

  Scenario Outline: Sign-in is refused: <case>
    Given I am on the sign-in page
    When I sign in with email "<email>" and password "<password>"
    Then I see the sign-in error "<message>"
    And I am still on the sign-in page

    Examples:
      | case             | email              | password         | message                                             |
      | wrong password   | operator@acme.test | not-the-password | Invalid email or password                           |
      | unknown email    | nobody@acme.test   | Demo-Passw0rd!   | Invalid email or password                           |
      | missing password | operator@acme.test |                  | Email and password are required                     |
      | locked account   | locked@acme.test   | Demo-Passw0rd!   | This account is locked. Contact your administrator. |

  Scenario: Visitors are sent to sign in and then back to where they were going
    Given I am not signed in
    When I open the new shipment page
    Then I am asked to sign in
    When I sign in as "operator"
    Then I see the new shipment form
