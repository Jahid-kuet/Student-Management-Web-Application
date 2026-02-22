package com.example.webapp.controller.api;

import com.example.webapp.dto.CourseDTO;
import com.example.webapp.entity.Course;
import com.example.webapp.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API Controller for Course resources.
 * Follows RESTful naming conventions with /api/v1/courses.
 */
@RestController
@RequestMapping("/api/v1/courses")
public class CourseRestController {

    private final CourseService courseService;

    public CourseRestController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * GET /api/v1/courses - Get all courses
     */
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<CourseDTO> courses = courseService.getAllCourses().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(courses);
    }

    /**
     * GET /api/v1/courses/{id} - Get course by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .map(course -> ResponseEntity.ok(convertToDTO(course)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/v1/courses - Create new course
     * Only AUTHORITY can create courses
     */
    @PostMapping
    @PreAuthorize("hasRole('AUTHORITY')")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {
        Course saved = courseService.saveCourse(courseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(saved));
    }

    /**
     * PUT /api/v1/courses/{id} - Update existing course
     * TEACHER and AUTHORITY can update courses
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'AUTHORITY')")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long id, @RequestBody CourseDTO courseDTO) {
        try {
            Course updated = courseService.updateCourse(id, courseDTO);
            return ResponseEntity.ok(convertToDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/v1/courses/{id} - Delete course
     * Only AUTHORITY can delete courses
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AUTHORITY')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        if (courseService.getCourseById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setCode(course.getCode());
        dto.setName(course.getName());
        dto.setDescription(course.getDescription());
        dto.setCredits(course.getCredits());
        return dto;
    }
}
