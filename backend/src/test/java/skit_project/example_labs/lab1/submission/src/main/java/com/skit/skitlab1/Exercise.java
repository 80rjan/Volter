package skit_project.example_labs.lab1.submission.src.main.java.com.skit.skitlab1;

import java.util.List;

public class Exercise {
    public static List<String> findCommonIgnoreCase(List<String> list1, List<String> list2) {
        if (list1 == null || list2 == null) {
            throw new IllegalArgumentException("Input lists cannot be null");
        }

        return list1.stream()
                .filter(item1 -> list2.stream()
                        .anyMatch(item1::equalsIgnoreCase))
                .map(String::toLowerCase)
                .toList();
    }
}
