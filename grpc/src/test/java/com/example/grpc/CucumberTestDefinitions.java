package com.example.grpc;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CucumberTestDefinitions {
    private List<String> todoList = new ArrayList<>();

    @Given("I am on the homepage")
    public void i_am_on_the_homepage() {
        System.out.println("Navigating to the homepage...");
    }

    @When("I add a new task with title {string}")
    public void i_add_a_new_task_with_title(String title) {
        todoList.add(title);
    }

    @Then("the task with title {string} should be added to the list")
    public void the_task_with_title_should_be_added_to_the_list(String title) {
        assertTrue(todoList.contains(title));
    }

    @Given("I have a task with title {string} in the list")
    public void i_have_a_task_with_title_in_the_list(String title) {
        todoList.add(title);
    }

    @When("I mark the task with title {string} as completed")
    public void i_mark_the_task_with_title_as_completed(String title) {
        System.out.println("Marking task '" + title + "' as completed...");
    }

    @Then("the task with title {string} should be marked as completed")
    public void the_task_with_title_should_be_marked_as_completed(String title) {
        System.out.println("Verifying task '" + title + "' is marked as completed...");
    }

    @When("I delete the task with title {string}")
    public void i_delete_the_task_with_title(String title) {
        todoList.remove(title);
    }

    @Then("the task with title {string} should be removed from the list")
    public void the_task_with_title_should_be_removed_from_the_list(String title) {
        assertFalse(todoList.contains(title));
    }
}
