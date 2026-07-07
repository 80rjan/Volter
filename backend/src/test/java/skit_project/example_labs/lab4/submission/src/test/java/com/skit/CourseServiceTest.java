package skit_project.example_labs.lab4.submission.src.test.java.com.skit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import skit_project.example_labs.lab4.submission.src.main.java.com.skit.Course;
import skit_project.example_labs.lab4.submission.src.main.java.com.skit.CourseRepository;
import skit_project.example_labs.lab4.submission.src.main.java.com.skit.CourseService;

import java.util.List;
import java.util.Optional;

import static junit.framework.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course activeCourse;
    private Course inactiveCourse;

    @BeforeEach
    void setUp() {
        activeCourse = new Course(1L, "Math", 6, true);
        inactiveCourse = new Course(2L, "Physics", 6, false);
    }

    @Test
    void shouldReturnActiveCourses() {
        when(courseRepository.findAll()).thenReturn(List.of(activeCourse, inactiveCourse));

        List<Course> activeCourses = courseService.findActiveCourses();

        assertEquals(1, activeCourses.size());
        assertTrue(activeCourses.contains(activeCourse));
        assertFalse(activeCourses.contains(inactiveCourse));
    }

    @Test
    void shouldFindCourseById() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(activeCourse));

        Course course = courseService.findById(1L);

        assertEquals(activeCourse, course);
    }

    @Test
    void shouldThrowExceptionWhenCourseNotFound() {
        when(courseRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> courseService.findById(3L));
    }

    @Test
    void shouldCreateCourse() {
        Course savedCourse = new Course(3L, "Chemistry", 6, true);
        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        Course course = courseService.createCourse("Chemistry", 6);

        assertEquals(savedCourse, course);
    }

    @Test
    void shouldThrowExceptionWhenCourseNameIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> courseService.createCourse("", 6));
        assertThrows(IllegalArgumentException.class, () -> courseService.createCourse(null, 6));
    }

    @Test
    void shouldThrowExceptionWhenCreditsAreInvalid() {
        assertThrows(IllegalArgumentException.class, () -> courseService.createCourse("Chemistry", 0));
        assertThrows(IllegalArgumentException.class, () -> courseService.createCourse("Chemistry", -1));
    }

    @Test

    void shouldDeleteCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(activeCourse));

        doNothing().when(courseRepository).deleteById(1L);

        courseService.deleteCourse(1L);

        verify(courseRepository).findById(1L);
        verify(courseRepository).deleteById(1L);

    }
}
