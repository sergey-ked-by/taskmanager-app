package com.example.taskmanager.controller.web;

import com.example.taskmanager.dto.TaskCreateDto;
import com.example.taskmanager.dto.TaskUpdateDto;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.service.ITaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final ITaskService taskService;

    @GetMapping
    public String listTasks(Model model, Authentication authentication) {
        model.addAttribute("tasks", taskService.findUserTasks(authentication.getName()));
        return "tasks/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("taskDto", new TaskCreateDto());
        model.addAttribute("pageTitle", "Create New Task"); // To reuse the form
        return "tasks/form";
    }

    @PostMapping("/create")
    public String createTask(@Valid @ModelAttribute("taskDto") TaskCreateDto taskDto, BindingResult result,
                             Authentication authentication, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Create New Task");
            return "tasks/form";
        }
        taskService.createTask(taskDto, authentication.getName());
        redirectAttributes.addFlashAttribute("success", "Task created successfully!");
        return "redirect:/dashboard";
    }

    @GetMapping("/{id}")
    public String viewTask(@PathVariable Long id, Model model) {
        model.addAttribute("task", taskService.findTaskById(id));
        return "tasks/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Task task = taskService.findTaskById(id);
        model.addAttribute("taskDto", TaskUpdateDto.fromEntity(task)); // Use a mapper
        model.addAttribute("taskId", id);
        model.addAttribute("pageTitle", "Edit Task");
        model.addAttribute("statuses", TaskStatus.values());
        return "tasks/form";
    }

    @PostMapping("/{id}/edit")
    public String editTask(@PathVariable Long id, @Valid @ModelAttribute("taskDto") TaskUpdateDto taskDto,
                           BindingResult result, Authentication authentication, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Task");
            model.addAttribute("taskId", id);
            model.addAttribute("statuses", TaskStatus.values());
            return "tasks/form";
        }
        taskService.updateTask(id, taskDto, authentication.getName());
        redirectAttributes.addFlashAttribute("success", "Task updated successfully!");
        return "redirect:/tasks/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        taskService.deleteTask(id, authentication.getName());
        redirectAttributes.addFlashAttribute("success", "Task deleted successfully!");
        return "redirect:/dashboard";
    }
}