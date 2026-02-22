package com.example.webapp.controller.api;

import com.example.webapp.dto.StudentDTO;
import com.example.webapp.entity.Student;
import com.example.webapp.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API Controller for Student resources.
 * Follows RESTful naming conventions with /api/v1/students.
 */
@RestController
@RequestMapping("/api/v1/students")
public class StudentRestController {

    private final StudentService studentService;

    public StudentRestController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * GET /api/v1/students - Get all students
     */
    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentDTO> students = studentService.getAllStudents().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(students);
    }

    /**
     * GET /api/v1/students/{id} - Get student by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(student -> ResponseEntity.ok(convertToDTO(student)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/v1/students - Create new student
     * Only TEACHER and AUTHORITY can create students
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'AUTHORITY')")
    public ResponseEntity<StudentDTO> createStudent(@RequestBody StudentDTO studentDTO) {
        Student saved = studentService.saveStudent(studentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(saved));
    }

    /**
     * PUT /api/v1/students/{id} - Update existing student
     * Only TEACHER and AUTHORITY can update students
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'AUTHORITY')")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Long id, @RequestBody StudentDTO studentDTO) {
        try {
            Student updated = studentService.updateStudent(id, studentDTO);
            return ResponseEntity.ok(convertToDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/v1/students/{id} - Delete student
     * Students cannot delete - only TEACHER and AUTHORITY can
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'AUTHORITY')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        if (studentService.getStudentById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    private StudentDTO convertToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setName(student.getName());
        dto.setRoll(student.getRoll());
        if (student.getDepartment() != null) {
            dto.setDepartmentId(student.getDepartment().getId());
        }
        return dto;
    }
}
