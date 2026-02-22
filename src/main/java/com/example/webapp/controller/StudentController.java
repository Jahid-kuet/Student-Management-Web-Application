package com.example.webapp.controller;

import com.example.webapp.dto.StudentDTO;
import com.example.webapp.entity.Student;
import com.example.webapp.service.CourseService;
import com.example.webapp.service.DepartmentService;
import com.example.webapp.service.StudentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final DepartmentService departmentService;
    private final CourseService courseService;

    public StudentController(StudentService studentService, 
                             DepartmentService departmentService,
                             CourseService courseService) {
        this.studentService = studentService;
        this.departmentService = departmentService;
        this.courseService = courseService;
    }

    @GetMapping
    public String getStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('TEACHER', 'AUTHORITY')")
    public String addStudent(Model model) {
        model.addAttribute("student", new StudentDTO());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("courses", courseService.getAllCourses());
        return "student-form";
    }

    @PostMapping("/store")
    @PreAuthorize("hasAnyRole('TEACHER', 'AUTHORITY')")
    public String storeStudent(@ModelAttribute("student") StudentDTO studentDTO, Model model) {
        studentService.saveStudent(studentDTO);
        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'AUTHORITY')")
    public String editStudent(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(student.getId());
        studentDTO.setName(student.getName());
        studentDTO.setRoll(student.getRoll());
        if (student.getDepartment() != null) {
            studentDTO.setDepartmentId(student.getDepartment().getId());
        }
        
        model.addAttribute("student", studentDTO);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("studentCourses", student.getCourses());
        return "student-form";
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'AUTHORITY')")
    public String updateStudent(@PathVariable Long id, @ModelAttribute("student") StudentDTO studentDTO) {
        studentService.updateStudent(id, studentDTO);
        return "redirect:/students";
    }

    // Students cannot delete - only TEACHER and AUTHORITY can
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'AUTHORITY')")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/students";
    }
}

