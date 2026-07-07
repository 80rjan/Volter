//package skit_project.example_labs.lab2.submission.src.test.java.com.skit;
//
//import org.junit.jupiter.api.Test;
//
//public class AppTest {
//
//    @Test
//    public void testEP_NoLoops() {
//        int[] temps = {};
//        String result = App.analyzeTemperatures(temps);
//        assert result.equals("No warm days.");
//    }
//
//    @Test
//    public void testEP_Invalid() {
//        int[] temps = {100};
//        String result = App.analyzeTemperatures(temps);
//        assert result.equals("Invalid temperatures detected.");
//    }
//
//    @Test
//    public void testPP_ValidAllWarm() {
//        int[] temps = {25};
//        String result = App.analyzeTemperatures(temps);
//        assert result.equals("All days were warm.");
//    }
//
//    @Test
//    public void testPP_ValidSomeWarm() {
//        int[] temps = {25, 35, 15};
//        String result = App.analyzeTemperatures(temps);
//        assert result.equals("Some days were warm.");
//    }
//}
