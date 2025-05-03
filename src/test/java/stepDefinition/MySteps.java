package stepDefinition;

import io.cucumber.java.en.*;

public class MySteps {
	
	@Given("I want to write a step with precondition")
	public void givenMethod() {
		System.out.println("This is given method");
		
	}

}
