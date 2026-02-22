package com.example.webapp.controller.api;

import com.example.webapp.config.SecurityConfig;
import com.example.webapp.dto.CourseDTO;
import com.example.webapp.entity.Course;
import com.example.webapp.service.CourseService;
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
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseRestController.class)
@Import(SecurityConfig.class)
class CourseRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("GET /api/v1/courses - Should return all courses")
    @WithMockUser(roles = "STUDENT")
    void getAllCourses_shouldReturnCourses() throws Exception {
        Course course = new Course();
        course.setId(1L);
        course.setCode("CS101");
        course.setName("Intro to CS");

        when(courseService.getAllCourses()).thenReturn(List.of(course));

        mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("CS101"))
                .andExpect(jsonPath("$[0].name").value("Intro to CS"));
    }

    @Test
    @DisplayName("POST /api/v1/courses - Authority should create course")
    @WithMockUser(roles = "AUTHORITY")
    void createCourse_authorityShouldCreate() throws Exception {
        CourseDTO dto = new CourseDTO();
        dto.setCode("CS101");
        dto.setName("Intro to CS");
        dto.setCredits(3);

        Course saved = new Course();
        saved.setId(1L);
        saved.setCode("CS101");
        saved.setName("Intro to CS");
        saved.setCredits(3);

        when(courseService.saveCourse(any(CourseDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("CS101"));
    }

    @Test
    @DisplayName("POST /api/v1/courses - Teacher should be forbidden from creating course")
    @WithMockUser(roles = "TEACHER")
    void createCourse_teacherShouldBeForbidden() throws Exception {
        CourseDTO dto = new CourseDTO();
        dto.setCode("CS101");

        mockMvc.perform(post("/api/v1/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/v1/courses/{id} - Only Authority should delete course")
    @WithMockUser(roles = "AUTHORITY")
    void deleteCourse_authorityShouldDelete() throws Exception {
        Course course = new Course();
        course.setId(1L);

        when(courseService.getCourseById(1L)).thenReturn(Optional.of(course));
        doNothing().when(courseService).deleteCourse(1L);

        mockMvc.perform(delete("/api/v1/courses/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/courses/{id} - Teacher should be forbidden from deleting course")
    @WithMockUser(roles = "TEACHER")
    void deleteCourse_teacherShouldBeForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/courses/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
