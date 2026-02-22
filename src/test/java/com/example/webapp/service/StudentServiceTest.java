package com.example.webapp.service;

import com.example.webapp.dto.StudentDTO;
import com.example.webapp.entity.Course;
import com.example.webapp.entity.Department;
import com.example.webapp.entity.Student;
import com.example.webapp.repository.CourseRepository;
import com.example.webapp.repository.DepartmentRepository;
import com.example.webapp.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ModelMapper modelMapper;

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentService = new StudentService(studentRepository, departmentRepository, courseRepository, modelMapper);
    }

    @Test
    @DisplayName("Should return all students")
    void getAllStudents_shouldReturnStudents() {
        Student s1 = new Student();
        s1.setName("Rahim");
        s1.setRoll("CSE-001");

        when(studentRepository.findAll()).thenReturn(List.of(s1));

        List<Student> students = studentService.getAllStudents();

        assertEquals(1, students.size());
        assertEquals("Rahim", students.get(0).getName());
        verify(studentRepository).findAll();
    }

    @Test
    @DisplayName("Should return student by ID")
    void getStudentById_shouldReturnStudent() {
        Student student = new Student();
        student.setId(1L);
        student.setName("Test Student");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        Optional<Student> result = studentService.getStudentById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    @DisplayName("Should return empty when student not found")
    void getStudentById_shouldReturnEmpty_whenNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Student> result = studentService.getStudentById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should save student without department and courses")
    void saveStudent_shouldSaveBasicStudent() {
        StudentDTO dto = new StudentDTO();
        dto.setName("Karim");
        dto.setRoll("CSE-01");

        Student savedStudent = new Student();
        savedStudent.setId(1L);
        savedStudent.setName("Karim");
        savedStudent.setRoll("CSE-01");

        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        Student result = studentService.saveStudent(dto);

        assertEquals("Karim", result.getName());
        assertEquals("CSE-01", result.getRoll());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should save student with department")
    void saveStudent_shouldSaveStudentWithDepartment() {
        Department dept = new Department();
        dept.setId(1L);
        dept.setName("Computer Science");

        StudentDTO dto = new StudentDTO();
        dto.setName("Karim");
        dto.setRoll("CSE-01");
        dto.setDepartmentId(1L);

        Student savedStudent = new Student();
        savedStudent.setId(1L);
        savedStudent.setName("Karim");
        savedStudent.setDepartment(dept);

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        Student result = studentService.saveStudent(dto);

        assertNotNull(result.getDepartment());
        assertEquals("Computer Science", result.getDepartment().getName());
    }

    @Test
    @DisplayName("Should save student with courses")
    void saveStudent_shouldSaveStudentWithCourses() {
        Course course1 = new Course();
        course1.setId(1L);
        course1.setCode("CS101");

        StudentDTO dto = new StudentDTO();
        dto.setName("Karim");
        dto.setRoll("CSE-01");
        dto.setCourseIds(Set.of(1L));

        Student savedStudent = new Student();
        savedStudent.setId(1L);
        savedStudent.setName("Karim");

        when(courseRepository.findAllById(Set.of(1L))).thenReturn(List.of(course1));
        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        Student result = studentService.saveStudent(dto);

        assertNotNull(result);
        verify(courseRepository).findAllById(Set.of(1L));
    }

    @Test
    @DisplayName("Should update student")
    void updateStudent_shouldUpdateExistingStudent() {
        Student existingStudent = new Student();
        existingStudent.setId(1L);
        existingStudent.setName("Old Name");

        StudentDTO dto = new StudentDTO();
        dto.setName("New Name");
        dto.setRoll("NEW-001");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        Student result = studentService.updateStudent(1L, dto);

        assertEquals("New Name", result.getName());
        assertEquals("NEW-001", result.getRoll());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent student")
    void updateStudent_shouldThrowException_whenNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        StudentDTO dto = new StudentDTO();
        dto.setName("Test");

        assertThrows(RuntimeException.class, () -> studentService.updateStudent(99L, dto));
    }

    @Test
    @DisplayName("Should delete student")
    void deleteStudent_shouldDeleteById() {
        doNothing().when(studentRepository).deleteById(1L);

        studentService.deleteStudent(1L);

        verify(studentRepository).deleteById(1L);
    }
}
