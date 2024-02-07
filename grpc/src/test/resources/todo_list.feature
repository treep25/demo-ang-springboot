Feature: TODO List
  As a user
  I want to be able to manage my tasks
  So that I can stay organized

  Scenario: Adding a new task
    Given I am on the homepage
    When I add a new task with title "Buy groceries"
    Then the task with title "Buy groceries" should be added to the list

  Scenario: Completing a task
    Given I am on the homepage
    And I have a task with title "Do laundry" in the list
    When I mark the task with title "Do laundry" as completed
    Then the task with title "Do laundry" should be marked as completed

  Scenario: Deleting a task
    Given I am on the homepage
    And I have a task with title "Clean the house" in the list
    When I delete the task with title "Clean the house"
    Then the task with title "Clean the house" should be removed from the list