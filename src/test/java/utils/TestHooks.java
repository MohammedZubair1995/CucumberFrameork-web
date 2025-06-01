package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
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


public class TestHooks {
	
	private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static Properties configProperties;
    private static Properties locators;
	private String targetBrowser;
	private Boolean isHeadlessMode;
	
	 static {
	        // Load config files once at class loading
	        try {
	            configProperties = new Properties();
	            configProperties.load(new FileInputStream(System.getProperty("user.dir") + "/src/test/resources/config.properties"));
	            
	            locators = new Properties();
	            locators.load(new FileInputStream(System.getProperty("user.dir") + "/src/test/resources/locators.properties"));
	        } catch (IOException e) {
	            throw new RuntimeException("Failed to load properties files", e);
	        }
	    }
	
	@Before(order =2)
	public void initializeBrowser() throws IOException {

       
		targetBrowser=configProperties.getProperty("browser");
		isHeadlessMode=Boolean.parseBoolean(configProperties.getProperty("headless"));		
		
		WebDriver driver = BrowserFactory.createDriver(targetBrowser, isHeadlessMode);
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.get(configProperties.getProperty("url"));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
		driverThreadLocal.set(driver);
		
	}
	
	 @Before(order =1)
	    public void connection() throws IOException {
		 
		 URL link = URI.create(configProperties.getProperty("url")).toURL(); 
				 
	    	HttpURLConnection connect = (HttpURLConnection) link.openConnection();
	    	//connect.connect();
	    	int responsecode = connect.getResponseCode();
	    	String responsemessage = connect.getResponseMessage();
	    	if (responsecode>=400) {
	    		String errorMessage = "The URL: "+configProperties.getProperty("url")+ " cannot be rached. Recieved status code: "+responsecode+" and response message as: "+responsemessage;
	    		
	    		Allure.description(errorMessage);
	            Allure.addAttachment("Connection Failure", "text/plain", errorMessage);
	            
	            Allure.step("URL Connection Failed", () -> {
	                throw new AssertionError(errorMessage);
	            });
	    		
	    		System.exit(1);
	    	} 
	    }

	public WebDriver getDriver() {
		return driverThreadLocal.get();
	}
	
	public static Properties getXpaths() {
		return locators;
	}
	
	public static Properties getProperties() {
		return configProperties;
	}

	@After(order=1)
	public void tearDown() {
		WebDriver driver = getDriver();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
        }
		
	}
	
	@AfterStep(order=1)
    public void afterStep(Scenario scenario) {
        if (scenario.isFailed()) {
            // Take screenshot and attach to report
            byte[] screenshot = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", scenario.getName());
            
        }
	}

}
