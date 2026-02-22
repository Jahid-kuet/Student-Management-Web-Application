package com.example.webapp.controller.api;

import com.example.webapp.config.SecurityConfig;
import com.example.webapp.dto.StudentDTO;
import com.example.webapp.entity.Student;
import com.example.webapp.service.StudentService;
import com.example.webapp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentRestController.class)
@Import(SecurityConfig.class)
class StudentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("GET /api/v1/students - Should return all students")
    @WithMockUser(roles = "STUDENT")
    void getAllStudents_shouldReturnStudents() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Test Student");
        student.setRoll("TEST-001");

        when(studentService.getAllStudents()).thenReturn(List.of(student));

        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Student"))
                .andExpect(jsonPath("$[0].roll").value("TEST-001"));
    }

    @Test
    @DisplayName("GET /api/v1/students/{id} - Should return student by ID")
    @WithMockUser(roles = "STUDENT")
    void getStudentById_shouldReturnStudent() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Test Student");

        when(studentService.getStudentById(1L)).thenReturn(Optional.of(student));

        mockMvc.perform(get("/api/v1/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Student"));
    }

    @Test
    @DisplayName("GET /api/v1/students/{id} - Should return 404 when not found")
    @WithMockUser(roles = "STUDENT")
    void getStudentById_shouldReturn404WhenNotFound() throws Exception {
        when(studentService.getStudentById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/students/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/students - Teacher should create student")
    @WithMockUser(roles = "TEACHER")
    void createStudent_teacherShouldCreate() throws Exception {
        StudentDTO dto = new StudentDTO();
        dto.setName("New Student");
        dto.setRoll("NEW-001");

        Student saved = new Student();
        saved.setId(1L);
        saved.setName("New Student");
        saved.setRoll("NEW-001");

        when(studentService.saveStudent(any(StudentDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Student"));
    }

    @Test
    @DisplayName("POST /api/v1/students - Student should be forbidden")
    @WithMockUser(roles = "STUDENT")
    void createStudent_studentShouldBeForbidden() throws Exception {
        StudentDTO dto = new StudentDTO();
        dto.setName("New Student");

        mockMvc.perform(post("/api/v1/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /api/v1/students/{id} - Teacher should update student")
    @WithMockUser(roles = "TEACHER")
    void updateStudent_teacherShouldUpdate() throws Exception {
        StudentDTO dto = new StudentDTO();
        dto.setName("Updated Name");
        dto.setRoll("UPD-001");

        Student updated = new Student();
        updated.setId(1L);
        updated.setName("Updated Name");
        updated.setRoll("UPD-001");

        when(studentService.updateStudent(eq(1L), any(StudentDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/students/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @DisplayName("DELETE /api/v1/students/{id} - Teacher should delete student")
    @WithMockUser(roles = "TEACHER")
    void deleteStudent_teacherShouldDelete() throws Exception {
        Student student = new Student();
        student.setId(1L);

        when(studentService.getStudentById(1L)).thenReturn(Optional.of(student));
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/api/v1/students/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/students/{id} - Student should be forbidden from deleting")
    @WithMockUser(roles = "STUDENT")
    void deleteStudent_studentShouldBeForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/students/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
