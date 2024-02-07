Feature: Multiplication

  Scenario: Multiplying numbers Positive
    Given a number 2
    When I multiply the number by 3
    Then the result should be "6"

  Scenario: Multiplying numbers Negative
    Given a number -1
    When I multiply the number by 10
    Then the result should be "-10"

  Scenario: Multiplying numbers Contains Zero
    Given a number 0
    When I multiply the number by 120
    Then the result should be "0"
