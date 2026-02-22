package com.example.webapp.service;

import com.example.webapp.dto.CourseDTO;
import com.example.webapp.entity.Course;
import com.example.webapp.repository.CourseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;

    public CourseService(CourseRepository courseRepository, ModelMapper modelMapper) {
        this.courseRepository = courseRepository;
        this.modelMapper = modelMapper;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Course saveCourse(CourseDTO courseDTO) {
        Course course = modelMapper.map(courseDTO, Course.class);
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, CourseDTO courseDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setCode(courseDTO.getCode());
        course.setName(courseDTO.getName());
        course.setDescription(courseDTO.getDescription());
        course.setCredits(courseDTO.getCredits());
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
}
