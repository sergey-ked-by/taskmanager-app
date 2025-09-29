package com.example.taskmanager.controller.web;

import com.example.taskmanager.dto.StatisticsDto;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.service.ITaskService;
import com.example.taskmanager.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminWebController {

    private final IUserService userService;
    private final ITaskService taskService;

    /**
     * Displays the main admin dashboard with statistics.
     * @param model the model to add attributes to for the view
     * @return the name of the admin dashboard view
     */
    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("userCount", userService.getUserCount());
        model.addAttribute("adminCount", userService.getAdminCount());
        model.addAttribute("taskCount", taskService.findAllTasks().size());
        return "admin/dashboard";
    }

    /**
     * Displays the page with a list of all users.
     * @param model the model to add attributes to for the view
     * @return the name of the users list view
     */
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("roles", Role.values()); // Pass all possible roles for the dropdown
        return "admin/users";
    }

    /**
     * Handles changing a user's role.
     * @param userId             the ID of the user to update
     * @param role               the new role to assign
     * @param redirectAttributes attributes for the redirect
     * @return a redirect to the users list page
     */
    @PostMapping("/users/update-role")
    public String updateUserRole(@RequestParam("userId") Long userId, @RequestParam("role") Role role, RedirectAttributes redirectAttributes) {
        userService.updateUserRole(userId, role);
        redirectAttributes.addFlashAttribute("success", "Роль пользователя успешно обновлена.");
        return "redirect:/admin/users";
    }

    /**
     * Handles user deletion.
     * @param userId             the ID of the user to delete
     * @param redirectAttributes attributes for the redirect
     * @return a redirect to the users list page
     */
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("success", "Пользователь успешно удален.");
        return "redirect:/admin/users";
    }
}