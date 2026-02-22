package com.example.webapp.service;

import com.example.webapp.dto.DepartmentDTO;
import com.example.webapp.entity.Department;
import com.example.webapp.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ModelMapper modelMapper;

    private DepartmentService departmentService;

    @BeforeEach
    void setUp() {
        departmentService = new DepartmentService(departmentRepository, modelMapper);
    }

    @Test
    @DisplayName("Should return all departments")
    void getAllDepartments_shouldReturnDepartments() {
        Department dept = new Department();
        dept.setId(1L);
        dept.setName("Computer Science");

        when(departmentRepository.findAll()).thenReturn(List.of(dept));

        List<Department> departments = departmentService.getAllDepartments();

        assertEquals(1, departments.size());
        assertEquals("Computer Science", departments.get(0).getName());
        verify(departmentRepository).findAll();
    }

    @Test
    @DisplayName("Should return department by ID")
    void getDepartmentById_shouldReturnDepartment() {
        Department dept = new Department();
        dept.setId(1L);
        dept.setName("Computer Science");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));

        Optional<Department> result = departmentService.getDepartmentById(1L);

        assertTrue(result.isPresent());
        assertEquals("Computer Science", result.get().getName());
    }

    @Test
    @DisplayName("Should save department")
    void saveDepartment_shouldSaveDepartment() {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setName("Computer Science");
        dto.setDescription("CS Department");

        Department dept = new Department();
        dept.setName("Computer Science");
        dept.setDescription("CS Department");

        Department savedDept = new Department();
        savedDept.setId(1L);
        savedDept.setName("Computer Science");

        when(modelMapper.map(dto, Department.class)).thenReturn(dept);
        when(departmentRepository.save(dept)).thenReturn(savedDept);

        Department result = departmentService.saveDepartment(dto);

        assertNotNull(result.getId());
        assertEquals("Computer Science", result.getName());
    }

    @Test
    @DisplayName("Should update department")
    void updateDepartment_shouldUpdateExistingDepartment() {
        Department existingDept = new Department();
        existingDept.setId(1L);
        existingDept.setName("Old Name");

        DepartmentDTO dto = new DepartmentDTO();
        dto.setName("New Name");
        dto.setDescription("New Description");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(existingDept));
        when(departmentRepository.save(any(Department.class))).thenAnswer(inv -> inv.getArgument(0));

        Department result = departmentService.updateDepartment(1L, dto);

        assertEquals("New Name", result.getName());
        assertEquals("New Description", result.getDescription());
    }

    @Test
    @DisplayName("Should delete department")
    void deleteDepartment_shouldDeleteById() {
        doNothing().when(departmentRepository).deleteById(1L);

        departmentService.deleteDepartment(1L);

        verify(departmentRepository).deleteById(1L);
    }
}
