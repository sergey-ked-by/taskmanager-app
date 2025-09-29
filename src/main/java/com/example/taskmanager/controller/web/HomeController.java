package com.example.taskmanager.controller.web;

import com.example.taskmanager.dto.UserRegistrationDto;
import com.example.taskmanager.exception.UserAlreadyExistsException;
import com.example.taskmanager.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final IUserService userService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("userDto") UserRegistrationDto dto,
                                      BindingResult result, RedirectAttributes redirectAttributes) {
        if (!userService.isUsernameAvailable(dto.getUsername())) {
            result.rejectValue("username", "username.exists", "This username is already taken");
        }
        if (result.hasErrors()) {
            return "auth/register";
        }
        userService.registerUser(dto);
        redirectAttributes.addFlashAttribute("success", "Registration successful! Please log in.");
        return "redirect:/login";
    }
}