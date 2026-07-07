//package skit_project.example_labs.lab4.submission.src.main.java.pages;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.TimeoutException;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.FindBy;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//
//import java.util.List;
//
//public class UserManagementPage extends AbstractPage {
//
//    public static final String URL =
//            "https://opensource-demo.orangehrmlive.com/web/index.php/admin/viewSystemUsers";
//
//    @FindBy(xpath = "//button[normalize-space()='Add']")
//    private WebElement addButton;
//
//    // Username search input identified by its label (no name/id on the input)
//    @FindBy(xpath = "//div[contains(@class,'oxd-input-group')][.//label[normalize-space()='Username']]//input")
//    private WebElement usernameSearchInput;
//
//    // The Search button is type="submit"; Reset button is not
//    @FindBy(css = "button[type='submit']")
//    private WebElement searchButton;
//
//    public UserManagementPage(WebDriver driver) {
//        super(driver);
//    }
//
//    public AddUserPage clickAddButton() {
//        waitAndClick(addButton);
//        return new AddUserPage(driver);
//    }
//
//    public void searchByUsername(String username) {
//        clearAndType(usernameSearchInput, username);
//        waitAndClick(searchButton);
//        waitForResultsToLoad();
//    }
//
//    public boolean isUserVisible(String username) {
//        waitForResultsToLoad();
//        List<WebElement> rows = driver.findElements(
//                By.cssSelector(".oxd-table-body .oxd-table-row"));
//        return rows.stream().anyMatch(row -> row.getText().contains(username));
//    }
//
//    // "No Records Found" is not a static DOM element — the table body has zero rows when
//    // a search returns no results
//    public boolean isNoRecordsFound() {
//        waitForResultsToLoad();
//
//        return !driver.findElements(By.xpath(
//                "//span[normalize-space()='No Records Found']"
//        )).isEmpty();
//    }
//
//    public void deleteUserByUsername(String username) {
//        searchByUsername(username);
//        // Use oxd-table-row class (not role="row" which the card-table may omit)
//        WebElement deleteButton = findClickable(By.xpath(
//                "//div[contains(@class,'oxd-table-row')]"
//                        + "[.//div[normalize-space()='" + username + "']]"
//                        + "//i[contains(@class,'bi-trash')]/parent::button"));
//        deleteButton.click();
//        confirmDeletion();
//    }
//
//    public void selectUserCheckbox(String username) {
//        WebElement checkbox = findClickable(By.xpath(
//                "//div[contains(@class,'oxd-table-row')]"
//                        + "[.//div[normalize-space()='" + username + "']]"
//                        + "//input[@type='checkbox']"));
//        checkbox.click();
//    }
//
//    public void clickDeleteSelected() {
//        findClickable(By.xpath("//button[normalize-space()='Delete Selected']")).click();
//    }
//
//    public void confirmDeletion() {
//        findClickable(By.xpath("//button[normalize-space()='Yes, Delete']")).click();
//        // Wait for confirmation dialog to close
//        wait.until(ExpectedConditions.invisibilityOfElementLocated(
//                By.xpath("//button[normalize-space()='Yes, Delete']")));
//    }
//
//    public String getToastMessage() {
//        return findVisible(By.cssSelector(".oxd-toast")).getText();
//    }
//
//    private void waitForResultsToLoad() {
//        wait.until(ExpectedConditions.presenceOfElementLocated(
//                By.cssSelector(".oxd-table-body")));
//        // OrangeHRM shows a loading overlay while fetching — wait for it to clear.
//        // invisibilityOfElementLocated returns true immediately if selector matches nothing.
//        wait.until(ExpectedConditions.invisibilityOfElementLocated(
//                By.cssSelector(".oxd-loading-spinner, .oxd-table-loader")));
//    }
//}
