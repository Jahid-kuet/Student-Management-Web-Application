package com.example.webapp.controller;

import com.example.webapp.dto.DepartmentDTO;
import com.example.webapp.entity.Department;
import com.example.webapp.service.DepartmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public String getDepartments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "departments";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAnyRole('TEACHER', 'AUTHORITY')")
    public String addDepartment(Model model) {
        model.addAttribute("department", new DepartmentDTO());
        return "department-form";
    }

    @PostMapping("/store")
    @PreAuthorize("hasAnyRole('TEACHER', 'AUTHORITY')")
    public String storeDepartment(@ModelAttribute("department") DepartmentDTO departmentDTO) {
        departmentService.saveDepartment(departmentDTO);
        return "redirect:/departments";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'AUTHORITY')")
    public String editDepartment(@PathVariable Long id, Model model) {
        Department department = departmentService.getDepartmentById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        
        DepartmentDTO departmentDTO = new DepartmentDTO();
        departmentDTO.setId(department.getId());
        departmentDTO.setName(department.getName());
        departmentDTO.setDescription(department.getDescription());
        
        model.addAttribute("department", departmentDTO);
        return "department-form";
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'AUTHORITY')")
    public String updateDepartment(@PathVariable Long id, @ModelAttribute("department") DepartmentDTO departmentDTO) {
        departmentService.updateDepartment(id, departmentDTO);
        return "redirect:/departments";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'AUTHORITY')")
    public String deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return "redirect:/departments";
    }
}
