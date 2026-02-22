package com.example.webapp.service;

import com.example.webapp.dto.UserDTO;
import com.example.webapp.entity.Role;
import com.example.webapp.entity.User;
import com.example.webapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, modelMapper, passwordEncoder);
    }

    @Test
    @DisplayName("Should load user by username")
    void loadUserByUsername_shouldReturnUser() {
        User user = new User("testuser", "password", "test@test.com", Role.STUDENT);
        user.setId(1L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername("testuser");

        assertEquals("testuser", result.getUsername());
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void loadUserByUsername_shouldThrowException_whenNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, 
            () -> userService.loadUserByUsername("unknown"));
    }

    @Test
    @DisplayName("Should register new user")
    void registerUser_shouldRegisterUser() {
        UserDTO dto = new UserDTO();
        dto.setUsername("newuser");
        dto.setPassword("password123");
        dto.setEmail("new@test.com");
        dto.setRole(Role.STUDENT);

        User savedUser = new User("newuser", "encodedPassword", "new@test.com", Role.STUDENT);
        savedUser.setId(1L);

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(dto);

        assertEquals("newuser", result.getUsername());
        verify(passwordEncoder).encode("password123");
    }

    @Test
    @DisplayName("Should register user with default STUDENT role")
    void registerUser_shouldDefaultToStudentRole() {
        UserDTO dto = new UserDTO();
        dto.setUsername("newuser");
        dto.setPassword("password123");
        dto.setEmail("new@test.com");
        dto.setRole(null);

        User savedUser = new User("newuser", "encodedPassword", "new@test.com", Role.STUDENT);
        savedUser.setId(1L);

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(dto);

        assertEquals(Role.STUDENT, result.getRole());
    }

    @Test
    @DisplayName("Should check if username exists")
    void existsByUsername_shouldReturnTrue() {
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertTrue(userService.existsByUsername("existinguser"));
    }

    @Test
    @DisplayName("Should check if email exists")
    void existsByEmail_shouldReturnTrue() {
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertTrue(userService.existsByEmail("existing@test.com"));
    }

    @Test
    @DisplayName("Should delete user")
    void deleteUser_shouldDeleteById() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }
}
