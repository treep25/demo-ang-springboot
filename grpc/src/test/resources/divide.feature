Feature: Dividing

  Scenario: Div numbers Positive
    Given a number 2
    When I divide the number by 2
    Then the result should be "1"

  Scenario: Div numbers Negative
    Given a number -10
    When I divide the number by 2
    Then the result should be "-5"

  Scenario: Div numbers Zero
    Given a number 1
    When I divide the number by 0
    Then the result should be "/ by zero"
