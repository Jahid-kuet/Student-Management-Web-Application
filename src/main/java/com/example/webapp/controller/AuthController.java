package com.example.webapp.controller;

import com.example.webapp.dto.UserDTO;
import com.example.webapp.entity.Role;
import com.example.webapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/students";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") UserDTO userDTO, Model model) {
        if (userService.existsByUsername(userDTO.getUsername())) {
            model.addAttribute("error", "Username already exists");
            model.addAttribute("user", userDTO);
            return "register";
        }

        if (userService.existsByEmail(userDTO.getEmail())) {
            model.addAttribute("error", "Email already exists");
            model.addAttribute("user", userDTO);
            return "register";
        }

        // Public registration always creates STUDENT accounts
        userDTO.setRole(Role.STUDENT);
        userService.registerUser(userDTO);
        return "redirect:/login?registered";
    }
}
