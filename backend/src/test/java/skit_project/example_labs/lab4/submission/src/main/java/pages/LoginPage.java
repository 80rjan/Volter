//package skit_project.example_labs.lab4.submission.src.main.java.pages;
//
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.FindBy;
//
//public class LoginPage extends AbstractPage {
//
//    @FindBy(name = "username")
//    private WebElement usernameInput;
//
//    @FindBy(name = "password")
//    private WebElement passwordInput;
//
//    @FindBy(css = "button[type='submit']")
//    private WebElement loginButton;
//
//    public LoginPage(WebDriver driver) {
//        super(driver);
//    }
//
//    public void login(String username, String password) {
//        clearAndType(usernameInput, username);
//        clearAndType(passwordInput, password);
//        waitAndClick(loginButton);
//    }
//}
