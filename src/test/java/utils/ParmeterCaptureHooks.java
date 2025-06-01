package utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class ParmeterCaptureHooks {
	
	private static final ThreadLocal<Map<String, String>> PARAMETERS = 
            ThreadLocal.withInitial(LinkedHashMap::new);
        
    @Before(order=3)
    public void captureScenarioParameters(Scenario scenario) {
        PARAMETERS.get().clear();
        
       
        scenario.getSourceTagNames().stream()
            .filter(tag -> tag.startsWith("@param:"))
            .map(tag -> tag.substring(7).split("=", 2))
            .forEach(parts -> PARAMETERS.get().put(parts[0], parts.length > 1 ? parts[1] : ""));
        
        
        captureFromScenarioText(scenario);
    }

    private void captureFromScenarioText(Scenario scenario) {
        String scenarioText = getScenarioText(scenario);
        
       
        Pattern pattern = Pattern.compile("[\"<]([^\"<>]+)[\">]");
        Matcher matcher = pattern.matcher(scenarioText);
        
        while (matcher.find()) {
            String param = matcher.group(1).trim();
            if (!param.isEmpty()) {
                PARAMETERS.get().put("arg_" + param, "[runtime_value]");
            }
        }
        
        
        captureFromScenarioName(scenario.getName());
        
       
        if (scenario.getSourceTagNames().contains("@")) {
            captureDataTableParameters(scenarioText);
        }
    }
    
    private void captureDataTableParameters(String scenarioText) {
        try {
            
            String[] lines = scenarioText.split("\n");
            
           
            List<String> tableRows = Arrays.stream(lines)
                .filter(line -> line.trim().startsWith("|"))
                .collect(Collectors.toList());
            
            if (!tableRows.isEmpty()) {
               
                String[] headers = tableRows.get(0).split("\\|");
                
               
                for (int i = 1; i < tableRows.size(); i++) {
                    String[] values = tableRows.get(i).split("\\|");
                    for (int j = 1; j < headers.length && j < values.length; j++) {
                        String header = headers[j].trim();
                        String value = values[j].trim();
                        if (!header.isEmpty()) {
                            PARAMETERS.get().put("table_" + header + "_row" + i, 
                                value.isEmpty() ? "[empty]" : value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error capturing data table parameters: " + e.getMessage());
        }
    }
    
    private void captureFromScenarioName(String scenarioName) {
        Pattern pattern = Pattern.compile("[\\[(]([^\\]\\)]+)[\\]\\)]");
        Matcher matcher = pattern.matcher(scenarioName);
        
        if (matcher.find()) {
            String paramsString = matcher.group(1);
            Arrays.stream(paramsString.split("[,;]"))
                .map(param -> param.split("[:=]"))
                .forEach(parts -> {
                    String key = parts[0].trim();
                    String value = parts.length > 1 ? parts[1].trim() : "";
                    if (!key.isEmpty()) {
                        PARAMETERS.get().put("name_" + key, value);
                    }
                });
        }
    }
    
    private String getScenarioText(Scenario scenario) {
        
        return scenario.toString();
    }
    
    public static void cleanup() {
        PARAMETERS.remove();
    }

}
