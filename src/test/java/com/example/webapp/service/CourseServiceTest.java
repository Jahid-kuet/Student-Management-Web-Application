package com.example.webapp.service;

import com.example.webapp.dto.CourseDTO;
import com.example.webapp.entity.Course;
import com.example.webapp.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ModelMapper modelMapper;

    private CourseService courseService;

    @BeforeEach
    void setUp() {
        courseService = new CourseService(courseRepository, modelMapper);
    }

    @Test
    @DisplayName("Should return all courses")
    void getAllCourses_shouldReturnCourses() {
        Course course = new Course();
        course.setId(1L);
        course.setCode("CS101");
        course.setName("Intro to CS");

        when(courseRepository.findAll()).thenReturn(List.of(course));

        List<Course> courses = courseService.getAllCourses();

        assertEquals(1, courses.size());
        assertEquals("CS101", courses.get(0).getCode());
        verify(courseRepository).findAll();
    }

    @Test
    @DisplayName("Should return course by ID")
    void getCourseById_shouldReturnCourse() {
        Course course = new Course();
        course.setId(1L);
        course.setCode("CS101");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Optional<Course> result = courseService.getCourseById(1L);

        assertTrue(result.isPresent());
        assertEquals("CS101", result.get().getCode());
    }

    @Test
    @DisplayName("Should save course")
    void saveCourse_shouldSaveCourse() {
        CourseDTO dto = new CourseDTO();
        dto.setCode("CS101");
        dto.setName("Intro to CS");
        dto.setCredits(3);

        Course course = new Course();
        course.setCode("CS101");
        course.setName("Intro to CS");
        course.setCredits(3);

        Course savedCourse = new Course();
        savedCourse.setId(1L);
        savedCourse.setCode("CS101");
        savedCourse.setName("Intro to CS");

        when(modelMapper.map(dto, Course.class)).thenReturn(course);
        when(courseRepository.save(course)).thenReturn(savedCourse);

        Course result = courseService.saveCourse(dto);

        assertNotNull(result.getId());
        assertEquals("CS101", result.getCode());
    }

    @Test
    @DisplayName("Should update course")
    void updateCourse_shouldUpdateExistingCourse() {
        Course existingCourse = new Course();
        existingCourse.setId(1L);
        existingCourse.setCode("CS101");
        existingCourse.setName("Old Name");

        CourseDTO dto = new CourseDTO();
        dto.setCode("CS102");
        dto.setName("New Name");
        dto.setCredits(4);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        Course result = courseService.updateCourse(1L, dto);

        assertEquals("CS102", result.getCode());
        assertEquals("New Name", result.getName());
    }

    @Test
    @DisplayName("Should delete course")
    void deleteCourse_shouldDeleteById() {
        doNothing().when(courseRepository).deleteById(1L);

        courseService.deleteCourse(1L);

        verify(courseRepository).deleteById(1L);
    }
}
