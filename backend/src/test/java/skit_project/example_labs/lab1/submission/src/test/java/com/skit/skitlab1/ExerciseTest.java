//package skit_project.example_labs.lab1.submission.src.test.java.com.skit.skitlab1;
//
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.MethodSource;
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.List;
//
//
//public class ExerciseTest {
//
////            * list1 = ["Apple", "banana", "Cherry", "apple"]
////            * list2 = ["BANANA", "cherry", "Durian"]
////            * Output: ["banana", "Cherry"]
//    public static Collection<Object[]> inputs() {
//        return Arrays.asList(new Object[][] {
//                {List.of("Banana", "cherry"), List.of("APPLE", "BANANA"), List.of("banana")},  // F, F, F, F, F, NE
//                {List.of(), List.of("APPLE", "BANANA"), List.of()},  // F, F, T, F, F, NE -> E
//                {List.of("APPLE", "BANANA"), List.of(), List.of()},  // F, F, F, T, F, NE -> E
//                {List.of("Durian", "cherry"), List.of("APPLE", "BANANA"), List.of()},  // F, F, F, F, F, E
//        });
//    }
//
//    @ParameterizedTest
//    @MethodSource("inputs")
//    public void testFindCommonIgnoreCase(List<String> list1, List<String> list2, List<String> expected) {
//        List<String> result = Exercise.findCommonIgnoreCase(list1, list2);
//        assert result.equals(expected) : "Expected: " + expected + ", but got: " + result;
//    }
//}
