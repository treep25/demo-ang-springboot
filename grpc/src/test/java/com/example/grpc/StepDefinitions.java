package com.example.grpc;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertEquals;

public class StepDefinitions {
    private int number;
    private String result;

    @Given("a number {int}")
    public void initNumber(int num) {
        number = num;
    }

    @When("I multiply the number by {int}")
    public void mulBy(int multiplier) {
        result = String.valueOf(number * multiplier);
    }

    @Then("the result should be {string}")
    public void getResult(String expected) {
        assertEquals(expected, result);
    }

    @When("I divide the number by {int}")
    public void divideBy(int multiplier) {
        try {
            result = String.valueOf(number / multiplier);

        } catch (ArithmeticException ex) {
            result = String.valueOf(ex.getLocalizedMessage());
        }
    }
}
