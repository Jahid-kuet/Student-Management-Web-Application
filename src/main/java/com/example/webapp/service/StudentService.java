package com.example.webapp.service;

import com.example.webapp.dto.StudentDTO;
import com.example.webapp.entity.Course;
import com.example.webapp.entity.Department;
import com.example.webapp.entity.Student;
import com.example.webapp.repository.CourseRepository;
import com.example.webapp.repository.DepartmentRepository;
import com.example.webapp.repository.StudentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;

    public StudentService(StudentRepository studentRepository, 
                          DepartmentRepository departmentRepository,
                          CourseRepository courseRepository,
                          ModelMapper modelMapper) {
        this.studentRepository = studentRepository;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.modelMapper = modelMapper;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Student saveStudent(StudentDTO studentDTO) {
        Student student = new Student();
        student.setName(studentDTO.getName());
        student.setRoll(studentDTO.getRoll());
        
        if (studentDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(studentDTO.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            student.setDepartment(department);
        }
        
        if (studentDTO.getCourseIds() != null && !studentDTO.getCourseIds().isEmpty()) {
            Set<Course> courses = new HashSet<>(courseRepository.findAllById(studentDTO.getCourseIds()));
            student.setCourses(courses);
        }
        
        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, StudentDTO studentDTO) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        student.setName(studentDTO.getName());
        student.setRoll(studentDTO.getRoll());
        
        if (studentDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(studentDTO.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            student.setDepartment(department);
        }
        
        if (studentDTO.getCourseIds() != null) {
            Set<Course> courses = new HashSet<>(courseRepository.findAllById(studentDTO.getCourseIds()));
            student.setCourses(courses);
        }
        
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}
