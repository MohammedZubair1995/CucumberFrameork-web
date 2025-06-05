package stepDefinition;

import java.util.List;
import org.testng.Assert;

import actions.LoginPageActions;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import utils.TestHooks;

public class Steps2 {
	
	LoginPageActions lpa = new LoginPageActions(TestHooks.getDriver());
	@Given("Login using <username> and <password>")
	public void givenMethod(DataTable dataTable){
		
		List<List<String>> rows = dataTable.asLists(String.class);
	    
	    for (List<String> row : rows) {
	        String username = row.get(0); 
	        String password = row.get(1);
	        System.out.println("Username: " + username + ", Password: " + password);
	        lpa.enterUsername(username);
	        lpa.enterPassword(password);
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