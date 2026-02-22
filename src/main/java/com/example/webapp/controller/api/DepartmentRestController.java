package com.example.webapp.controller.api;

import com.example.webapp.dto.DepartmentDTO;
import com.example.webapp.entity.Department;
import com.example.webapp.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API Controller for Department resources.
 * Follows RESTful naming conventions with /api/v1/departments.
 */
@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentRestController {

    private final DepartmentService departmentService;

    public DepartmentRestController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    /**
     * GET /api/v1/departments - Get all departments
     */
    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        List<DepartmentDTO> departments = departmentService.getAllDepartments().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(departments);
    }

    /**
     * GET /api/v1/departments/{id} - Get department by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable Long id) {
        return departmentService.getDepartmentById(id)
                .map(dept -> ResponseEntity.ok(convertToDTO(dept)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/v1/departments - Create new department
     * Only TEACHER and AUTHORITY can create departments
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'AUTHORITY')")
    public ResponseEntity<DepartmentDTO> createDepartment(@RequestBody DepartmentDTO departmentDTO) {
        Department saved = departmentService.saveDepartment(departmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(saved));
    }

    /**
     * PUT /api/v1/departments/{id} - Update existing department
     * Only TEACHER and AUTHORITY can update departments
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'AUTHORITY')")
    public ResponseEntity<DepartmentDTO> updateDepartment(@PathVariable Long id, @RequestBody DepartmentDTO departmentDTO) {
        try {
            Department updated = departmentService.updateDepartment(id, departmentDTO);
            return ResponseEntity.ok(convertToDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/v1/departments/{id} - Delete department
     * Only TEACHER and AUTHORITY can delete departments
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'AUTHORITY')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        if (departmentService.getDepartmentById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    private DepartmentDTO convertToDTO(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setDescription(department.getDescription());
        return dto;
    }
}
