package stepDefinition;

import io.cucumber.java.en.When;

public class MySteps2 {
	
	@When("I am inside when")
	public void whenMethod() {
		
		System.out.println("This is when method");
	}

}
