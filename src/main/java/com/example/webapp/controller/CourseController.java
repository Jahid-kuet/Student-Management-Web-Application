package com.example.webapp.controller;

import com.example.webapp.dto.CourseDTO;
import com.example.webapp.entity.Course;
import com.example.webapp.service.CourseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public String getCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "courses";
    }

    // Only AUTHORITY can add courses
    @GetMapping("/add")
    @PreAuthorize("hasRole('AUTHORITY')")
    public String addCourse(Model model) {
        model.addAttribute("course", new CourseDTO());
        return "course-form";
    }

    // Only AUTHORITY can store (create) courses
    @PostMapping("/store")
    @PreAuthorize("hasRole('AUTHORITY')")
    public String storeCourse(@ModelAttribute("course") CourseDTO courseDTO) {
        courseService.saveCourse(courseDTO);
        return "redirect:/courses";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'AUTHORITY')")
    public String editCourse(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setId(course.getId());
        courseDTO.setCode(course.getCode());
        courseDTO.setName(course.getName());
        courseDTO.setDescription(course.getDescription());
        courseDTO.setCredits(course.getCredits());
        
        model.addAttribute("course", courseDTO);
        return "course-form";
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'AUTHORITY')")
    public String updateCourse(@PathVariable Long id, @ModelAttribute("course") CourseDTO courseDTO) {
        courseService.updateCourse(id, courseDTO);
        return "redirect:/courses";
    }

    // Only AUTHORITY can delete courses
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('AUTHORITY')")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/courses";
    }
}
