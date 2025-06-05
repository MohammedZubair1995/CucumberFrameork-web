package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {
protected WebDriver driver;
    
    @FindBy(xpath = "//input[@name='username']")
    public WebElement username;
    
    @FindBy(xpath = "//input[@name='password']")
    public WebElement password;
    
    @FindBy(xpath = "//button[@type='submit']")
    public WebElement loginButton;
    
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
}
