package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.stream.Stream;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import io.qameta.allure.Allure;

public class BrowserFactory {
	
	private static void ensureDownloadDirectoryExists(String path) {
		 try {
		        Path directory = Paths.get(path);
		        if (!Files.exists(directory)) {
		            Files.createDirectories(directory);
		        }
		        
		        try (Stream<Path> files = Files.list(directory)) {
		            files.filter(Files::isRegularFile)
		                 .forEach(file -> {
		                     try {
		                         Files.delete(file);
		                     } catch (IOException e) {
		                         System.err.println("Failed to delete file: " + file + " - " + e.getMessage());
		                     }
		                 });
		        }
		    } catch (IOException e) {
		        throw new RuntimeException("Failed to setup download directory", e);
		    }
    }
	
	public static WebDriver createDriver(String browserType, boolean runHeadless) {
		
        WebDriver driver;
        String downloadPath = System.getProperty("user.dir") + "/target/Downloads";
        ensureDownloadDirectoryExists(downloadPath);
		
        switch (browserType.toLowerCase()) {
        case "chrome":
            ChromeOptions chromeOptions = new ChromeOptions();
            HashMap<String, Object> chromePrefs = new HashMap<>();
            chromePrefs.put("download.default_directory", downloadPath);
            chromePrefs.put("download.prompt_for_download", false);
            chromePrefs.put("download.directory_upgrade", true);
            
            chromeOptions.setExperimentalOption("prefs", chromePrefs);
            if (runHeadless) {
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
            if (runHeadless) {
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
            if (runHeadless) {
                edgeOptions.addArguments("--headless=new");
            }
            driver = new EdgeDriver(edgeOptions);
            break;
            
        case "safari":
            SafariOptions safariOptions = new SafariOptions();
            safariOptions.setCapability("safari:automaticInspection", true);
            safariOptions.setCapability("safari:automaticProfiling", true);

           
            if (runHeadless) {
                Allure.addAttachment("Safari Browser Warning", 
                    "text/plain", 
                    "Safari browser launched in normal mode since it doesn't support headless mode");
            }

            driver = new SafariDriver(safariOptions) {
                private final String defaultDownloadPath = System.getProperty("user.home") + "/Downloads/";
                private final String targetDownloadPath = downloadPath;

                @Override
                public void get(String url) {
                    
                    if (url.startsWith("http") && (url.endsWith(".pdf") || url.endsWith(".txt") || url.endsWith(".csv") || 
                                                 url.endsWith(".xlsx") || url.endsWith(".zip"))) {
                        handleDownload(url);
                    } else {
                        super.get(url);
                    }
                }

                private void handleDownload(String fileUrl) {
                    String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
                    
                   
                    ((JavascriptExecutor) this).executeScript(
                        "var link = document.createElement('a');" +
                        "link.href = arguments[0];" +
                        "link.download = arguments[1];" +
                        "document.body.appendChild(link);" +
                        "link.click();" +
                        "document.body.removeChild(link);", 
                        fileUrl, fileName);

                    waitAndMoveFile(fileName);
                }

                private void waitAndMoveFile(String fileName) {
                    File source = new File(defaultDownloadPath + fileName);
                    File target = new File(targetDownloadPath + fileName);
                    
                    int attempts = 0;
                    while (attempts++ < 30) { // Wait up to 30 seconds
                        try {
                            if (source.exists()) {
                                Files.move(source.toPath(), target.toPath(), 
                                          StandardCopyOption.REPLACE_EXISTING);
                                Allure.addAttachment("File Downloaded", 
                                    "text/plain", 
                                    "Moved file from " + source + " to " + target);
                                return;
                            }
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            Allure.addAttachment("Download Warning", 
                                "text/plain", 
                                "Error moving file: " + e.getMessage());
                        }
                    }
                    Allure.addAttachment("Download Timeout", 
                        "text/plain", 
                        "File not found after 30 seconds: " + source);
                }
            };
            break;
            
        default:
            throw new IllegalArgumentException("Unsupported browser: " + browserType);
   
		
		
	}
		return driver;

	}
}
