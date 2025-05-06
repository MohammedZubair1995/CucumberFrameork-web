package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Link;
import io.qameta.allure.model.Parameter;


public class MyHook {
	
	private static final String CONFIG_FILE_PATH = System.getProperty("user.dir") + "/src/test/resources/config.properties";
    private static Properties properties = new Properties();
    private static final String xpath_file_path = System.getProperty("user.dir") + "/src/test/resources/xpath.properties";
    private static Properties xpaths = new Properties();

    
	
	
	WebDriver driver;
	private String browser = properties.getProperty("browser");
	private Boolean headless = Boolean.parseBoolean(properties.getProperty("headless"));
	
	@Before(order =2)
	public void setUp() throws IOException {

		properties = new Properties();
        try {
        	
            properties.load(new FileInputStream(CONFIG_FILE_PATH));
        } catch (IOException e) {
        	
        	String errorMessage = "Config file not found "+CONFIG_FILE_PATH;
			Allure.description(errorMessage );
            Allure.addAttachment("Configuration Failure", "text/plain", errorMessage);
            
            Allure.step("Configuration Failed", () -> {
                throw new AssertionError(errorMessage);
            });
        	
        }
		
		
		try {
			xpaths.load(new FileInputStream(xpath_file_path));
		} catch (FileNotFoundException e) {
			String errorMessage = "xpath file not found "+xpath_file_path;
			Allure.description(errorMessage );
            Allure.addAttachment("Configuration Failure", "text/plain", errorMessage);
            
            Allure.step("Configuration Failed", () -> {
                throw new AssertionError(errorMessage);
            });
		}
		
//		Allure.parameter("Browser", browser);
//		Allure.parameter("Headless mode", headless.toString());
//		Allure.link(properties.getProperty("url"));
		
		Allure.addAttachment("Browser Launched", "text/plain",browser );
		Allure.addAttachment("Browser launched in headless mode", "text/plain",headless.toString() );
		Allure.addAttachment("Application", "text/html", "<a href='"+properties.getProperty("url")+"'>Application</a>");
		
		
		
		driver = BrowserFactory.createDriver(browser, headless);
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.get(properties.getProperty("url"));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
		
		
	}
	
	 @Before(order =1)
	    public void connection() throws IOException {

	    	URL link = new URL(properties.getProperty("url"));
	    	HttpURLConnection connect = (HttpURLConnection) link.openConnection();
	    	connect.connect();
	    	int responsecode = connect.getResponseCode();
	    	String responsemessage = connect.getResponseMessage();
	    	if (responsecode>=400) {
	    		String errorMessage = "The URL: "+properties.getProperty("url")+ " cannot be rached. Recieved status code: "+responsecode+" and response message as: "+responsemessage;
	    		
	    		Allure.description(errorMessage);
	            Allure.addAttachment("Connection Failure", "text/plain", errorMessage);
	            
	            Allure.step("URL Connection Failed", () -> {
	                throw new AssertionError(errorMessage);
	            });
	    		
	    		System.exit(1);
	    	} 
	    }

	public WebDriver getDriver() {
		return driver;
	}
	
	public static Properties getXpaths() {
		return xpaths;
	}
	
	public static Properties getProperties() {
		return properties;
	}

	@After
	public void tearDown() {
		if (driver != null) {
            driver.quit();
        }
		
	}
	
	@AfterStep
    public void afterStep(Scenario scenario) {
        if (scenario.isFailed()) {
            // Take screenshot and attach to report
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", scenario.getName());
        }
    }
	
	@AfterAll
    public static void generateAllureReport() {
        try {
            // Ensure the allure-results directory exists
            Path resultsDir = Paths.get("allure-results");
            if (!Files.exists(resultsDir)) {
                Files.createDirectories(resultsDir);
            }

            // Generate Allure report
            ProcessBuilder processBuilder = new ProcessBuilder();
            String command;
            
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                command = "cmd.exe /c allure generate --single-file target/allure-results -o target/allure-reports --clean";
            } else {
                command = "sh -c allure generate --single-file target/allure-results -o target/allure-reports --clean";
            }
            
            processBuilder.command(command.split(" "));
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                System.err.println("Allure report generation failed with exit code: " + exitCode);
            } else {
                System.out.println("Allure report generated successfully at: " + 
                    Paths.get("allure-report", "index.html").toAbsolutePath());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to generate Allure report: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
