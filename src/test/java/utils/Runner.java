package utils;

import org.testng.annotations.DataProvider;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
		features = {"src/test/resources/features"},
		glue = {"stepDefinition", "utils"},
		plugin = {"pretty",
				"html:target/cucumber-report/report.html",
				"json:target/cucumber-report/report.json",
				"junit:target/cucumber-report/report.xml",
				"rerun:target/failed_scenarios/failed_scenarios.txt",
				"io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"})

public class Runner extends AbstractTestNGCucumberTests {
	
	@Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();

}
	static {
        // Set thread count for parallel execution
        System.setProperty("dataproviderthreadcount", "3");
    }
}