//package skit_project.example_labs.lab3.submission.src.test.java.com.skit;
//
//import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.MethodSource;
//import org.junit.jupiter.params.provider.Arguments;
//
//import java.util.stream.Stream;
//
//class AppTest {
//
//    static Stream<Arguments> raccCases() {
//        return Stream.of(
//                // 1. age > 65
//                Arguments.of(70, 40.0, false, false, 40.0 * 0.8),
//                Arguments.of(60, 40.0, false, false, 40.0 * 1.2 + 60 * 0.1),
//
//                // 2. isHighRisk
//                Arguments.of(60, 40.0, true, false, 40.0 * 0.8),
//                Arguments.of(60, 40.0, false, false, 40.0 * 1.2 + 60 * 0.1),
//
//                // 3. weight < 50
//                Arguments.of(70, 40.0, true, false, 40.0 * 0.8),
//                Arguments.of(70, 60.0, true, false, 60.0 * 1.2 + 70 * 0.1),
//
//                // 4. !hasAllergy
//                Arguments.of(70, 40.0, true, false, 40.0 * 0.8),
//                Arguments.of(70, 40.0, true, true, 40.0 * 1.2 + 70 * 0.1)
//        );
//    }
//
//    @ParameterizedTest
//    @MethodSource("raccCases")
//    void testRACC(int age, double weight, boolean isHighRisk, boolean hasAllergy, double expected) {
//        assertEquals(
//                expected,
//                App.calculateDosage(age, weight, isHighRisk, hasAllergy),
//                0.0001
//        );
//    }
//}