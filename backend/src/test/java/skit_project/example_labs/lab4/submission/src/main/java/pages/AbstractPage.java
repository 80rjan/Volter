//package skit_project.example_labs.lab4.submission.src.main.java.pages;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.PageFactory;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//
//import java.time.Duration;
//
//public abstract class AbstractPage {
//    protected WebDriver driver;
//    protected WebDriverWait wait;
//
//    public AbstractPage(WebDriver driver) {
//        this.driver = driver;
//        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
//        PageFactory.initElements(driver, this);
//    }
//
//    protected void waitAndClick(WebElement element) {
//        wait.until(ExpectedConditions.elementToBeClickable(element));
//        element.click();
//    }
//
//    protected void waitForVisible(WebElement element) {
//        wait.until(ExpectedConditions.visibilityOf(element));
//    }
//
//    protected void clearAndType(WebElement element, String text) {
//        waitForVisible(element);
//        element.clear();
//        element.sendKeys(text);
//    }
//
//    protected WebElement findClickable(By locator) {
//        return wait.until(ExpectedConditions.elementToBeClickable(locator));
//    }
//
//    protected WebElement findVisible(By locator) {
//        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
//    }
//
//    public String getCurrentUrl() {
//        return driver.getCurrentUrl();
//    }
//
//    public String getTitle() {
//        return driver.getTitle();
//    }
//}
