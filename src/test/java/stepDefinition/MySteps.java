package stepDefinition;

import io.cucumber.java.en.*;

public class MySteps {
	
	@Given("This is a given method")
	public void givenMethod() {
		System.out.println("This is a given method");
		}
	
	@When("This is a when method")
	public void whenMethod() {
		
		System.out.println("This is a when method");
	}
	
	@And("This is a and method")
	public void andMethod() {
		
		System.out.println("This is a and method");
	}
	
	@But("This is a but method")
	public void butMethod() {
		
		System.out.println("This is a but method");
	}
	
	@Then("This is a then method")
	public void thenMethod() {
		
		System.out.println("This is a then method");
	}
	
	@Given("This is background step")
	public void background() {
		
		System.out.println("This is a background method");
	}

}
