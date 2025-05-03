package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;


public class MyHook {
	
	private static final String CONFIG_FILE_PATH = System.getProperty("user.dir") + "/src/test/resources/config.properties";
    private static Properties properties;
    private static final String xpath_file_path = System.getProperty("user.dir") + "/src/test/resources/xpath.properties";
    private static Properties xpaths = new Properties();

    static {
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
    }
	
	
	WebDriver driver;
	private String browser = properties.getProperty("browser");
	private Boolean headless = Boolean.parseBoolean(properties.getProperty("headless"));
	
	@Before(order =2)
	public void setUp() throws IOException {

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
	    		//System.out.println("The URL: "+url+ " cannot be rached. Recieved status code: "+responsecode+" and response message as: "+responsemessage);
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

}
