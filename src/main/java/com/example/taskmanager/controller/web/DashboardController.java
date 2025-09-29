package com.example.taskmanager.controller.web;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.service.ITaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ITaskService taskService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<Task> tasks = taskService.findUserTasks(username);

                // Efficiently count all statuses in a single pass
        Map<TaskStatus, Long> statusCounts = tasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

        model.addAttribute("tasks", tasks);
        model.addAttribute("pendingCount", statusCounts.getOrDefault(TaskStatus.PENDING, 0L));
        model.addAttribute("inProgressCount", statusCounts.getOrDefault(TaskStatus.IN_PROGRESS, 0L));
        model.addAttribute("completedCount", statusCounts.getOrDefault(TaskStatus.COMPLETED, 0L));
        model.addAttribute("username", username);

        return "dashboard";
    }
}