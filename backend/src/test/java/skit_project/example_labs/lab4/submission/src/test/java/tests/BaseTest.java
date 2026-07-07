//package skit_project.example_labs.lab4.submission.src.test.java.tests;
//
//import io.github.bonigarcia.wdm.WebDriverManager;
//import org.junit.After;
//import org.junit.Before;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import skit_project.example_labs.lab4.submission.src.main.java.pages.LoginPage;
//import skit_project.example_labs.lab4.submission.src.main.java.pages.UserManagementPage;
//
//public abstract class BaseTest {
//
//    private static final String LOGIN_URL =
//            "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";
//    private static final String ADMIN_USERNAME = "Admin";
//    private static final String ADMIN_PASSWORD = "admin123";
//
//    protected WebDriver driver;
//    protected UserManagementPage userManagementPage;
//
//    @Before
//    public void setUp() {
//        WebDriverManager.chromedriver().setup();
//        driver = new ChromeDriver();
//        driver.manage().window().maximize();
//        // Do NOT set implicitlyWait — mixing implicit and explicit waits causes
//        // unpredictable combined timeouts. All waits are handled explicitly in page objects.
//
//        driver.get(LOGIN_URL);
//        LoginPage loginPage = new LoginPage(driver);
//        loginPage.login(ADMIN_USERNAME, ADMIN_PASSWORD);
//
//        driver.get(UserManagementPage.URL);
//        userManagementPage = new UserManagementPage(driver);
//    }
//
//    @After
//    public void tearDown() {
//        if (driver != null) {
//            driver.quit();
//        }
//    }
//
//    protected String generateUniqueUsername() {
//        return "testuser_" + System.currentTimeMillis();
//    }
//
//    protected UserManagementPage goToUserManagement() {
//        driver.get(UserManagementPage.URL);
//        userManagementPage = new UserManagementPage(driver);
//        return userManagementPage;
//    }
//}
