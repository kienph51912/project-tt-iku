package vn.fpoly.project_tt_iku.service;

import org.springframework.dao.DataIntegrityViolationException;
import vn.fpoly.project_tt_iku.core.service.TaskService;
import vn.fpoly.project_tt_iku.core.repository.TaskRepository;
import vn.fpoly.project_tt_iku.entity.Task;
import vn.fpoly.project_tt_iku.entity.User;
import vn.fpoly.project_tt_iku.expection.ApiException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private User admin;
    private Task task;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(2L)
                .username("kien")
                .role(User.Role.USER)
                .build();

        admin = User.builder()
                .id(1L)
                .username("admin")
                .role(User.Role.ADMIN)
                .build();

        task = Task.builder()
                .id(10L)
                .title("Test Task")
                .description("Desc")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();
    }

    // ============================================================
    // 1. TEST CREATE TASK
    // ============================================================
    @Test
    void testCreateTaskSuccess() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task saved = taskService.createTask(task);

        assertNotNull(saved);
        assertEquals("Test Task", saved.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    // ============================================================
    // 2. TEST UPDATE TASK
    // ============================================================
    @Test
    void testUpdateTaskSuccess() {
        Task update = Task.builder()
                .title("Updated")
                .description("Updated Desc")
                .completed(true)
                .build();

        when(taskRepository.save(task)).thenReturn(task);

        Task updated = taskService.updateTask(task, update);

        assertEquals("Updated", updated.getTitle());
        assertEquals(true, updated.getCompleted());
    }

    // ============================================================
    // 3. TEST DELETE TASK
    // ============================================================
    @Test
    void testDeleteTaskSuccess() {
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTask(task);

        verify(taskRepository, times(1)).delete(task);
    }

    // ============================================================
    // 4. TEST USER KHÔNG ĐƯỢC XEM TASK CỦA NGƯỜI KHÁC
    // ============================================================
    @Test
    void testGetTaskByUser_NotOwner_ThrowException() {
        Task taskOther = Task.builder()
                .id(99L)
                .user(User.builder().id(999L).build())
                .build();

        when(taskRepository.findById(99L)).thenReturn(Optional.of(taskOther));

        ApiException ex = assertThrows(ApiException.class, () ->
                taskService.getTaskByIdAndUserOrAdmin(99L, user)
        );

        assertEquals("TASK_NOT_FOUND", ex.getCode());
    }

    // ============================================================
    // 5. TEST ADMIN LẤY TASK CỦA BẤT KỲ USER
    // ============================================================
    @Test
    void testAdminGetAnyTask_Success() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        Task found = taskService.getTaskByIdAndUserOrAdmin(10L, admin);

        assertNotNull(found);
        assertEquals(10L, found.getId());
    }

    // ============================================================
    // 6. TEST FILTER + PAGINATION (USER)
    // ============================================================
    @Test
    void testPaginationFilter_ByUser() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        Page<Task> mockPage = new PageImpl<>(List.of(task));

        when(taskRepository.findByUser(user, pageable)).thenReturn(mockPage);

        var result = taskService.phanTrang(0, 5, user, null, "createdAt", "desc");

        assertEquals(1, result.getData().size());
        assertEquals("Test Task", result.getData().get(0).getTitle());
    }

    // ============================================================
    // 7. TEST FILTER COMPLETED = TRUE
    // ============================================================
    @Test
    void testPaginationFilter_CompletedTrue() {
        task.setCompleted(true);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> mockPage = new PageImpl<>(List.of(task));

        when(taskRepository.findByUserAndCompleted(eq(user), eq(true), any(Pageable.class)))
                .thenReturn(mockPage);

        var result = taskService.phanTrang(0, 10, user, true, "createdAt", "asc");

        assertEquals(1, result.getData().size());
        assertTrue(result.getData().get(0).getCompleted());
    }

    // ============================================================
    // 8. TEST ADMIN PHÂN TRANG LẤY ALL TASK
    // ============================================================
    @Test
    void testAdminPagination_GetAllTasks() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        Page<Task> mockPage = new PageImpl<>(List.of(task));

        when(taskRepository.findAll(pageable)).thenReturn(mockPage);

        var result = taskService.phanTrang(0, 10, admin, null, "createdAt", "desc");

        assertEquals(1, result.getData().size());
        assertEquals(10L, result.getData().get(0).getId());
    }

    @Test
    void testDeleteTask_FailedBecauseConstraint() {

        doThrow(new DataIntegrityViolationException("FK violation"))
                .when(taskRepository)
                .delete(task);

        ApiException ex = assertThrows(ApiException.class, () ->
                taskService.deleteTask(task)
        );

        assertEquals("DELETE_TASK_FAILED", ex.getCode());
    }

    @Test
    void testGetTaskById_TaskNotFound() {

        when(taskRepository.findById(123L)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () ->
                taskService.getTaskByIdAndUserOrAdmin(123L, user)
        );

        assertEquals("TASK_NOT_FOUND", ex.getCode());
    }

    @Test
    void testAdminPagination_FilterCompleted() {

        task.setCompleted(true);

        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").ascending());
        Page<Task> mockPage = new PageImpl<>(List.of(task));

        when(taskRepository.findByCompleted(true, pageable))
                .thenReturn(mockPage);

        var result = taskService.phanTrang(0, 5, admin, true, "createdAt", "asc");

        assertEquals(1, result.getData().size());
        assertTrue(result.getData().get(0).getCompleted());
    }
}
