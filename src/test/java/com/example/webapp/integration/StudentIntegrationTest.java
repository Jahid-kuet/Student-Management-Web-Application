package com.example.webapp.integration;

import com.example.webapp.dto.StudentDTO;
import com.example.webapp.entity.Department;
import com.example.webapp.entity.Student;
import com.example.webapp.repository.DepartmentRepository;
import com.example.webapp.repository.StudentRepository;
import com.example.webapp.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StudentIntegrationTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save and retrieve student through service")
    void testSaveAndRetrieveStudentFlow() {
        // Create DTO
        StudentDTO dto = new StudentDTO();
        dto.setName("Integration Test Student");
        dto.setRoll("INT-001");

        // Save through Service
        Student savedStudent = studentService.saveStudent(dto);

        // Verify saved object
        assertNotNull(savedStudent.getId());
        assertEquals("Integration Test Student", savedStudent.getName());

        // Retrieve from DB
        Optional<Student> fetched = studentService.getStudentById(savedStudent.getId());

        assertTrue(fetched.isPresent());
        assertEquals("INT-001", fetched.get().getRoll());
    }

    @Test
    @DisplayName("Should save student with department")
    void testSaveStudentWithDepartment() {
        // Create and save department
        Department dept = new Department();
        dept.setName("Computer Science");
        dept.setDescription("CS Department");
        Department savedDept = departmentRepository.save(dept);

        // Create student with department
        StudentDTO dto = new StudentDTO();
        dto.setName("Student With Dept");
        dto.setRoll("CSE-001");
        dto.setDepartmentId(savedDept.getId());

        Student savedStudent = studentService.saveStudent(dto);

        // Verify department association
        assertNotNull(savedStudent.getDepartment());
        assertEquals("Computer Science", savedStudent.getDepartment().getName());
    }

    @Test
    @DisplayName("Should get all students")
    void testGetAllStudentsFlow() {
        // Save entities directly
        Student s1 = new Student();
        s1.setName("User 1");
        s1.setRoll("R1");
        studentRepository.save(s1);

        Student s2 = new Student();
        s2.setName("User 2");
        s2.setRoll("R2");
        studentRepository.save(s2);

        // Use Service to fetch all
        List<Student> allStudents = studentService.getAllStudents();

        assertEquals(2, allStudents.size());
    }

    @Test
    @DisplayName("Should update student")
    void testUpdateStudentFlow() {
        // Create and save student
        Student student = new Student();
        student.setName("Original Name");
        student.setRoll("ORIG-001");
        Student saved = studentRepository.save(student);

        // Update through service
        StudentDTO updateDto = new StudentDTO();
        updateDto.setName("Updated Name");
        updateDto.setRoll("UPD-001");

        Student updated = studentService.updateStudent(saved.getId(), updateDto);

        assertEquals("Updated Name", updated.getName());
        assertEquals("UPD-001", updated.getRoll());
    }

    @Test
    @DisplayName("Should delete student")
    void testDeleteStudentFlow() {
        // Create and save student
        Student student = new Student();
        student.setName("To Delete");
        student.setRoll("DEL-001");
        Student saved = studentRepository.save(student);

        // Delete through service
        studentService.deleteStudent(saved.getId());

        // Verify deletion
        Optional<Student> deleted = studentRepository.findById(saved.getId());
        assertFalse(deleted.isPresent());
    }
}