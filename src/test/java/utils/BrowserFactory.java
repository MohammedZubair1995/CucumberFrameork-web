package utils;

import java.io.File;
import java.util.HashMap;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

public class BrowserFactory {
	
	private static void ensureDownloadDirectoryExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    file.delete();
                }
            }
        }
    }
	
	public static WebDriver createDriver(String browser, boolean headless) {
		
        WebDriver driver;
        String downloadPath = System.getProperty("user.dir") + "/target/Downloads";
        ensureDownloadDirectoryExists(downloadPath);
		
        switch (browser.toLowerCase()) {
        case "chrome":
            ChromeOptions chromeOptions = new ChromeOptions();
            HashMap<String, Object> chromePrefs = new HashMap<>();
            chromePrefs.put("download.default_directory", downloadPath);
            chromePrefs.put("download.prompt_for_download", false);
            chromePrefs.put("download.directory_upgrade", true);
            
            chromeOptions.setExperimentalOption("prefs", chromePrefs);
            if (headless) {
                chromeOptions.addArguments("--headless=new");
            }
            driver = new ChromeDriver(chromeOptions);
            break;
            
        case "firefox":
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            FirefoxProfile profile = new FirefoxProfile();
      //      profile.setPreference("browser.download.folderList", 2); // Use custom location
            profile.setPreference("browser.download.dir", downloadPath);
            profile.setPreference("browser.download.useDownloadDir", true);
            profile.setPreference("browser.download.viewableInternally.enabledTypes", "");
            profile.setPreference("browser.helperApps.neverAsk.saveToDisk", 
                "application/pdf,application/octet-stream,application/vnd.ms-excel");
            firefoxOptions.setProfile(profile);
            if (headless) {
                firefoxOptions.addArguments("--headless");
            }
            driver = new FirefoxDriver(firefoxOptions);
            break;
            
        case "edge":
        	 
            EdgeOptions edgeOptions = new EdgeOptions();
            HashMap<String, Object> edgePrefs = new HashMap<>();
            edgePrefs.put("download.default_directory", downloadPath);
            edgePrefs.put("download.prompt_for_download", false);
            edgePrefs.put("download.directory_upgrade", true);
            edgeOptions.setExperimentalOption("prefs", edgePrefs);
            if (headless) {
                edgeOptions.addArguments("--headless=new");
            }
            driver = new EdgeDriver(edgeOptions);
            break;
            
        default:
            throw new IllegalArgumentException("Unsupported browser: " + browser);
   
		
		
	}
		return driver;

	}
}
