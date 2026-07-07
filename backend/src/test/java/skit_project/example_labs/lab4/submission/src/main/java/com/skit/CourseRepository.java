package skit_project.example_labs.lab4.submission.src.main.java.com.skit;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    List<Course> findAll();
    Optional<Course> findById(Long id);
    Course save(Course course);
    void deleteById(Long id);
}
