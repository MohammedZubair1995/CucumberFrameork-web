package actions;

import org.openqa.selenium.WebDriver;

import pages.LoginPage;

public class LoginPageActions {

private LoginPage login;
    
    public LoginPageActions(WebDriver driver) {
        login = new LoginPage(driver);
    }
    
    public void enterUsername(String username) {
        login.username.clear();
        login.username.sendKeys(username);
    }
    
    public void enterPassword(String password) {
        login.password.clear();
        login.password.sendKeys(password);
    }
}
