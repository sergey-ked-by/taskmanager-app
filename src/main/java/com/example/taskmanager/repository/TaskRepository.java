package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;
import com.example.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByUser(User user);
    
    List<Task> findByUserOrderByCreatedAtDesc(User user);
    
    List<Task> findByStatus(TaskStatus status);
    
    List<Task> findByUserAndStatus(User user, TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.title LIKE %:title%")
    List<Task> findByUserAndTitleContaining(User user, String title);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.user = :user AND t.status = :status")
    long countByUserAndStatus(User user, TaskStatus status);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    long countByStatus(TaskStatus status);
}