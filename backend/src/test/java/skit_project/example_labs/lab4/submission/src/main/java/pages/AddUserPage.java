//package skit_project.example_labs.lab4.submission.src.main.java.pages;
//
//import org.openqa.selenium.*;
//import org.openqa.selenium.support.FindBy;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class AddUserPage extends AbstractPage {
//
//    // @FindBy List<WebElement> has NO built-in wait — accessing .get(0) on an empty list
//    // throws IndexOutOfBoundsException if the form hasn't rendered yet.
//    // Instead, dropdowns are found via findClickable() inside each method so the
//    // 15-second explicit wait is always applied before the element is accessed.
//
//    @FindBy(css = ".oxd-autocomplete-text-input input")
//    private WebElement employeeNameInput;
//
//    @FindBy(xpath = "//div[contains(@class,'oxd-input-group')][.//label[normalize-space()='Username']]//input")
//    private WebElement usernameInput;
//
//    @FindBy(xpath = "(//input[@type='password'])[1]")
//    private WebElement passwordInput;
//
//    @FindBy(xpath = "(//input[@type='password'])[2]")
//    private WebElement confirmPasswordInput;
//
//    @FindBy(css = "button[type='submit']")
//    private WebElement saveButton;
//
//    @FindBy(css = "span.oxd-input-group__message")
//    private List<WebElement> errorMessages;
//
//    public AddUserPage(WebDriver driver) {
//        super(driver);
//    }
//
//    public void selectUserRole(String role) {
//        // findClickable waits up to 15 s — safe even if the form is still loading
//        findClickable(By.xpath(
//                "//div[contains(@class,'oxd-input-group')]"
//                        + "[.//label[normalize-space()='User Role']]"
//                        + "//div[contains(concat(' ',normalize-space(@class),' '),' oxd-select-text ')]"
//        )).click();
//        selectDropdownOption(role);
//    }
//
//    public void selectStatus(String status) {
//        findClickable(By.xpath(
//                "//div[contains(@class,'oxd-input-group')]"
//                        + "[.//label[normalize-space()='Status']]"
//                        + "//div[contains(concat(' ',normalize-space(@class),' '),' oxd-select-text ')]"
//        )).click();
//        selectDropdownOption(status);
//    }
//
//    public void selectEmployee(String partialName) {
//        employeeNameInput.click();
//
//        employeeNameInput.clear();
//        employeeNameInput.sendKeys(partialName);
//
//        By optionTextLocator = By.cssSelector(
//                ".oxd-autocomplete-option span");
//
//        // wait until actual option text appears
//        WebElement firstOption = wait.until(driver ->
//                driver.findElements(optionTextLocator).stream()
//                        .filter(WebElement::isDisplayed)
//                        .findFirst()
//                        .orElse(null));
//
//        // click actual text element
//        wait.until(ExpectedConditions.elementToBeClickable(firstOption));
//
//        firstOption.click();
//
//        // wait until dropdown closes
//        wait.until(ExpectedConditions.invisibilityOf(firstOption));
//    }
//
//    public void enterUsername(String username) {
//        clearAndType(usernameInput, username);
//    }
//
//    public void enterPassword(String password) {
//        clearAndType(passwordInput, password);
//    }
//
//    public void enterConfirmPassword(String confirmPassword) {
//        clearAndType(confirmPasswordInput, confirmPassword);
//    }
//
//    public void clickSave() {
//        waitAndClick(saveButton);
//    }
//
//    public void fillUserForm(String role, String employeeSearch, String status,
//                             String username, String password) {
//        selectUserRole(role);
//        selectEmployee(employeeSearch);
//        selectStatus(status);
//        enterUsername(username);
//        enterPassword(password);
//        enterConfirmPassword(password);
//    }
//
//    public List<String> getErrorMessages() {
//        try {
//            // Poll until at least one span has non-empty text (spans may be in DOM but empty before validation fires)
//            wait.until(d -> d.findElements(By.cssSelector("span.oxd-input-group__message"))
//                    .stream().anyMatch(el -> !el.getText().isEmpty()));
//        } catch (TimeoutException e) {
//            return List.of();
//        }
//        return driver.findElements(By.cssSelector("span.oxd-input-group__message"))
//                .stream()
//                .map(WebElement::getText)
//                .filter(t -> !t.isEmpty())
//                .collect(Collectors.toList());
//    }
//
//    public boolean hasErrorMessage(String expectedText) {
//        try {
//            // Poll until any span contains the expected text (avoids race where spans exist but text not yet set)
//            return wait.until(d -> d.findElements(By.cssSelector("span.oxd-input-group__message"))
//                    .stream().anyMatch(el -> el.getText().contains(expectedText)));
//        } catch (TimeoutException e) {
//            return false;
//        }
//    }
//
//    public boolean isSuccessToastDisplayed() {
//        try {
//            findVisible(By.cssSelector(".oxd-toast--success"));
//            return true;
//        } catch (TimeoutException e) {
//            return false;
//        }
//    }
//
//    public String getToastMessage() {
//        return findVisible(By.cssSelector(".oxd-toast")).getText();
//    }
//
//    private void selectDropdownOption(String optionText) {
//        findClickable(By.xpath(
//                "//div[@role='option'][.//span[normalize-space()='" + optionText + "']]"
//        )).click();
//    }
//}
