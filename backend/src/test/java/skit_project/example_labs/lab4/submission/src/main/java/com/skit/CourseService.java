package skit_project.example_labs.lab4.submission.src.main.java.com.skit;

import java.util.List;

public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> findActiveCourses() {
        return courseRepository.findAll()
                .stream()
                .filter(Course::isActive)
                .toList();
    }

    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    public Course createCourse(String name, int credits) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Course name is required");
        }

        if (credits <= 0) {
            throw new IllegalArgumentException("Credits must be positive");
        }

        Course course = new Course(null, name, credits, true);
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        Course course = findById(id);
        courseRepository.deleteById(course.getId());
    }
}
