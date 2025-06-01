package utils;
import io.cucumber.java.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReportingHooks {
    
    @AfterAll
    public static void generateAllureReport() {
        try {
        	String CUSTOM_TITLE = TestHooks.getProperties().getProperty("app");
        	String allureResultsDir = System.getProperty("user.dir") + File.separator + "target" + File.separator + "allure-results";
            Files.createDirectories(Paths.get(allureResultsDir));
            
            generateAllureCategories();
            AllureEnvironmentUtils();
            
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
            	Path reportPath = Paths.get(
                        System.getProperty("user.dir"), 
                        "target", 
                        "allure-reports",
                        "index.html"
                    );
            	
            	String htmlContent = Files.readString(reportPath, StandardCharsets.UTF_8);
            	if (!htmlContent.contains(CUSTOM_TITLE)) {
                    htmlContent = htmlContent.replaceAll(
                        "(?i)<title[^>]*>.*?</title>",
                        "<title>"+CUSTOM_TITLE+"</title>"
                    );
                }
            	Files.write(reportPath, htmlContent.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.TRUNCATE_EXISTING);
            	
            	
                System.out.println("Allure report generated successfully at: " + 
                    Paths.get("allure-report", "index.html").toAbsolutePath());
                ParmeterCaptureHooks.cleanup();
            }
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to generate Allure report: " + e.getMessage());
            e.printStackTrace();
        }
        
        
    }
    
    public static void generateAllureCategories() {
    	
    	String jsonContent = "["
                + "{\"name\":\"Test Passed\",\"matchedStatuses\":[\"passed\"]},"
                + "{\"name\":\"Test Failed\",\"matchedStatuses\":[\"failed\"]},"
                + "{\"name\":\"Test Skipped\",\"matchedStatuses\":[\"skipped\"]},"
                + "{\"name\":\"Unknown\",\"matchedStatuses\":[\"broken\",\"unknown\"]}"
                + "]";
    	 
    	 String projectDir = System.getProperty("user.dir");
         String allureResultsDir = projectDir + File.separator + "target" + File.separator + "allure-results";
         String filePath = allureResultsDir + File.separator + "categories.json";

         try {
            
             try (FileWriter writer = new FileWriter(filePath)) {
                 writer.write(jsonContent);
             }
         } catch (IOException e) {
             System.err.println("Failed to generate Allure categories.json: " + e.getMessage());
         }
    }
    
    public static void AllureEnvironmentUtils() {
    	
    	String environment = TestHooks.getProperties().getProperty("env");
    	String browser = TestHooks.getProperties().getProperty("browser");
    	String headless = TestHooks.getProperties().getProperty("headless");
    	String url = TestHooks.getProperties().getProperty("url");
    	if (browser.equalsIgnoreCase("safari"))
    		headless = "false";
    	if(!headless.equalsIgnoreCase("true"))
    		headless = "false";
    	Map<String, String> envData = new HashMap<>();
        envData.put("OS", System.getProperty("os.name"));
   //     envData.put("Java Version", System.getProperty("java.version"));
   //     envData.put("User", System.getProperty("user.name"));
        envData.put("Environment", environment);      // e.g., QA/Staging/Production
        envData.put("Browser", browser);
        envData.put("Headless", headless);
        envData.put("Application", url);
        
        
        StringBuilder propertiesContent = new StringBuilder();
        for (Map.Entry<String, String> entry : envData.entrySet()) {
            propertiesContent.append(String.format("%s=%s%n", entry.getKey(), entry.getValue()));
        }
        
        String projectDir = System.getProperty("user.dir");
        String allureResultsDir = projectDir + File.separator + "target" + File.separator + "allure-results";
        String filePath = allureResultsDir + File.separator + "environment.properties";
        
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(propertiesContent.toString());
        }
        catch (IOException e) {
        System.err.println("Failed to create environment.properties: " + e.getMessage());
        }
    }
 }