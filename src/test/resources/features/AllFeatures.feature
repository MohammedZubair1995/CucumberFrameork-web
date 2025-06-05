#Severity. Allowed values are: “trivial”, “minor”, “normal”, “critical”, and “blocker”.
@Feature_Name

Feature: Title of your feature
  I want to use this template for my feature file

Background:
	  Given This is background step
	  
  @TC1 
  @allure.label.owner=john 
  @minor
  Scenario: This is to showcse scenario structure
    Given This is a given method
    When This is a when method
    And This is a and method
    But This is a but method
    Then This is a then method
    
   @TC2
   @trivial
   @allure.label.owner=john 
 Scenario: Data table scenario
		Given Login using <username> and <password>
		|username|password|
		|Admin|Admin|
		|user|password|
		|Admin|Admin123|
		
		@TC3
		@normal
		@allure.label.owner=john 
	Scenario: Failed test case
		Given Failed test case
		
		@TC4
		@critical
		@allure.label.owner=john 
		Scenario: Data passed as string
		Given This is "Data" steps
		
		@TC5
		@blocker
		@allure.label.owner=john 
		Scenario Outline: This is scenario line
		Given This is a scenario outline <name> and <password>
		
		Examples: Data example
		|name|password|
		|admin|admin|
		|usr|user|
		
		
		