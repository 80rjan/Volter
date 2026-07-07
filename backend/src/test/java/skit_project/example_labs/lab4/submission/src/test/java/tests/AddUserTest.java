//package skit_project.example_labs.lab4.submission.src.test.java.tests;
//
//import org.junit.Test;
//import skit_project.example_labs.lab4.submission.src.main.java.pages.AddUserPage;
//
//import static org.junit.Assert.assertTrue;
//
///**
// * Tests for the Add System User functionality in OrangeHRM.
// *
// * Credentials: Admin / admin123
// * Target: Admin > User Management > Users > Add
// */
//public class AddUserTest extends BaseTest {
//
//    private static final String VALID_PASSWORD = "Admin1234!";
//    private static final String EMPLOYEE_SEARCH = "a";
//
//    @Test
//    public void testAddUserSuccessfully() {
//        String username = generateUniqueUsername();
//
//        AddUserPage addUserPage = userManagementPage.clickAddButton();
//        addUserPage.fillUserForm("ESS", EMPLOYEE_SEARCH, "Enabled", username, VALID_PASSWORD);
//        addUserPage.clickSave();
//
//        assertTrue("Success toast should appear after saving",
//                addUserPage.isSuccessToastDisplayed());
//
//        goToUserManagement();
//        userManagementPage.searchByUsername(username);
//        assertTrue("Newly created user should appear in the list",
//                userManagementPage.isUserVisible(username));
//    }
//
//    @Test
//    public void testAddAdminRoleUser() {
//        String username = generateUniqueUsername();
//
//        AddUserPage addUserPage = userManagementPage.clickAddButton();
//        addUserPage.selectUserRole("Admin");
//        addUserPage.selectEmployee(EMPLOYEE_SEARCH);
//        addUserPage.selectStatus("Enabled");
//        addUserPage.enterUsername(username);
//        addUserPage.enterPassword(VALID_PASSWORD);
//        addUserPage.enterConfirmPassword(VALID_PASSWORD);
//        addUserPage.clickSave();
//
//        assertTrue("Admin role user should be saved successfully",
//                addUserPage.isSuccessToastDisplayed());
//    }
//
//    @Test
//    public void testAddDisabledUser() {
//        String username = generateUniqueUsername();
//
//        AddUserPage addUserPage = userManagementPage.clickAddButton();
//        addUserPage.fillUserForm("ESS", EMPLOYEE_SEARCH, "Disabled", username, VALID_PASSWORD);
//        addUserPage.clickSave();
//
//        assertTrue("Disabled user should be saved successfully",
//                addUserPage.isSuccessToastDisplayed());
//
//        goToUserManagement();
//        userManagementPage.searchByUsername(username);
//        assertTrue("Disabled user should appear in the list",
//                userManagementPage.isUserVisible(username));
//    }
//
//    @Test
//    public void testAddUserWithEmptyUsername() {
//        AddUserPage addUserPage = userManagementPage.clickAddButton();
//        addUserPage.selectUserRole("ESS");
//        addUserPage.selectEmployee(EMPLOYEE_SEARCH);
//        addUserPage.selectStatus("Enabled");
//        addUserPage.enterPassword(VALID_PASSWORD);
//        addUserPage.enterConfirmPassword(VALID_PASSWORD);
//        addUserPage.clickSave();
//
//        assertTrue("Required field error should appear when username is empty",
//                addUserPage.hasErrorMessage("Required"));
//    }
//
//    @Test
//    public void testAddUserWithMismatchedPasswords() {
//        String username = generateUniqueUsername();
//
//        AddUserPage addUserPage = userManagementPage.clickAddButton();
//        addUserPage.selectUserRole("ESS");
//        addUserPage.selectEmployee(EMPLOYEE_SEARCH);
//        addUserPage.selectStatus("Enabled");
//        addUserPage.enterUsername(username);
//        addUserPage.enterPassword(VALID_PASSWORD);
//        addUserPage.enterConfirmPassword("WrongPass9!");
//        addUserPage.clickSave();
//
//        assertTrue("Password mismatch error should appear",
//                addUserPage.hasErrorMessage("Passwords do not match"));
//    }
//
//    @Test
//    public void testAddUserWithShortPassword() {
//        String username = generateUniqueUsername();
//
//        AddUserPage addUserPage = userManagementPage.clickAddButton();
//        addUserPage.selectUserRole("ESS");
//        addUserPage.selectEmployee(EMPLOYEE_SEARCH);
//        addUserPage.selectStatus("Enabled");
//        addUserPage.enterUsername(username);
//        addUserPage.enterPassword("Ab1!");
//        addUserPage.enterConfirmPassword("Ab1!");
//        addUserPage.clickSave();
//
//        // OrangeHRM password length error contains "8 characters"
//        assertTrue("Short password error should appear",
//                addUserPage.hasErrorMessage("Should have at least 7 characters"));
//    }
//
//    @Test
//    public void testAddUserWithDuplicateUsername() {
//        String username = generateUniqueUsername();
//
//        AddUserPage addUserPage = userManagementPage.clickAddButton();
//        addUserPage.fillUserForm("ESS", EMPLOYEE_SEARCH, "Enabled", username, VALID_PASSWORD);
//        addUserPage.clickSave();
//        assertTrue("Success toast should appear after saving",
//                addUserPage.isSuccessToastDisplayed());
//
//        goToUserManagement();
//        addUserPage = userManagementPage.clickAddButton();
//        addUserPage.fillUserForm("ESS", EMPLOYEE_SEARCH, "Enabled", username, VALID_PASSWORD);
//        addUserPage.clickSave();
//
//        assertTrue("Duplicate username error should appear",
//                addUserPage.hasErrorMessage("Already exists"));
//    }
//
//    @Test
//    public void testAddUserWithNoFieldsFilled() {
//        AddUserPage addUserPage = userManagementPage.clickAddButton();
//        addUserPage.clickSave();
//
//        assertTrue("Required field errors should appear when nothing is filled",
//                !addUserPage.getErrorMessages().isEmpty());
//    }
//}
