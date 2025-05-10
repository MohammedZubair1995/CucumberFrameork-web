package stepDefinition;

import java.util.List;

import org.testng.Assert;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

public class MySteps2 {
	
	@Given("Login using <username> and <password>")
	public void givenMethod(DataTable dataTable){
		
		List<List<String>> rows = dataTable.asLists(String.class);
	    
	    for (List<String> row : rows) {
	        String username = row.get(0); // First column
	        String password = row.get(1); // Second column
	        
	        System.out.println("Username: " + username + ", Password: " + password);
	    }
	    
	}
	
	@Given("Failed test case")
	 public void failedTestCase() {
		Assert.assertFalse(true);
	}
	
	@Given("This is {string} steps")
	 public void dataTest(String data) {
		System.out.println("Status = "+data);
		
	}
	
	@Given("^This is a scenario outline (.*) and (.*)$")
	public void scnarioOutline(String name, String pass) {
	        
	        System.out.println("Username: " + name + ", Password: " + pass);
	    }
}