package vn.fpoly.project_tt_iku.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import vn.fpoly.project_tt_iku.authentication.service.CustomUserDetailsService;
import vn.fpoly.project_tt_iku.authentication.service.JwtUtils;
import vn.fpoly.project_tt_iku.core.controller.TaskController;
import vn.fpoly.project_tt_iku.core.dto.request.TaskRequest;
import vn.fpoly.project_tt_iku.core.dto.response.TaskResponse;
import vn.fpoly.project_tt_iku.core.service.TaskService;
import vn.fpoly.project_tt_iku.core.service.UserService;
import vn.fpoly.project_tt_iku.entity.Task;
import vn.fpoly.project_tt_iku.entity.User;
import vn.fpoly.project_tt_iku.util.PageableObject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserService userService;

    @MockBean
    private Authentication authentication;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtUtils jwtUtils;


    @Autowired
    private ObjectMapper objectMapper;

    private User mockUser;

    @BeforeEach
    void setup() {
        mockUser = User.builder()
                .id(2L)
                .username("kien")
                .role(User.Role.USER)
                .build();

        Mockito.when(authentication.getName()).thenReturn("kien");
        Mockito.when(userService.findByUsername("kien")).thenReturn(mockUser);
    }

    // ============================
    // TEST GET ALL TASKS
    // ============================
    @Test
    @WithMockUser(username = "kien", roles = {"USER"})
    void testGetAllTasks() throws Exception {

        Task task = Task.builder()
                .id(10L)
                .title("Test Task")
                .description("Demo")
                .completed(false)
                .build();

        Mockito.when(taskService.getTasksByUser(mockUser))
                .thenReturn(List.of(task));

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Test Task"));
    }


    // ============================
    // TEST PAGING
    // ============================
    @Test
    @WithMockUser(username = "kien", roles = {"USER"})
    void testPagingTasks() throws Exception {

        Task t = Task.builder().id(1L).title("Paging Test").completed(false).build();
        Page<Task> page = new PageImpl<>(List.of(t));
        PageableObject<Task> pageableObject = new PageableObject<>(page);

        Mockito.when(taskService.phanTrang(
                anyInt(), anyInt(), eq(mockUser), any(), any(), any()
        )).thenReturn(pageableObject);

        mockMvc.perform(get("/api/v1/tasks/paging")
                        .param("pageNo", "0")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.data[0].title").value("Paging Test"));
    }


    // ============================
    // TEST CREATE TASK
    // ============================
    @Test
    @WithMockUser(username = "kien", roles = {"USER"})
    void testCreateTask() throws Exception {

        TaskRequest req = new TaskRequest("New Task", "Desc", false);

        Task saved = Task.builder()
                .id(100L)
                .title(req.getTitle())
                .description(req.getDescription())
                .completed(req.getCompleted())
                .user(mockUser)
                .build();

        Mockito.when(taskService.createTask(any(Task.class)))
                .thenReturn(saved);

        mockMvc.perform(post("/api/v1/tasks/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))  // <-- thêm CSRF token
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("New Task"))
                .andExpect(jsonPath("$.data.description").value("Desc"))
                .andExpect(jsonPath("$.data.completed").value(false));
    }

    // ============================
    // TEST UPDATE TASK
    // ============================

    @Test
    @WithMockUser(username = "kien", roles = {"USER"})
    void testUpdateTask() throws Exception {

        TaskRequest req = new TaskRequest("Updated", "New Desc", true);

        Task existing = Task.builder()
                .id(50L)
                .title("Old")
                .description("Old Desc")
                .completed(false)
                .user(mockUser)
                .build();

        Task updated = Task.builder()
                .id(50L)
                .title(req.getTitle())
                .description(req.getDescription())
                .completed(req.getCompleted())
                .user(mockUser)
                .build();

        Mockito.when(taskService.getTaskByIdAndUserOrAdmin(anyLong(), eq(mockUser)))
                .thenReturn(existing);

        Mockito.when(taskService.updateTask(any(Task.class), any(Task.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/v1/tasks/update/50")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf())) // <--- thêm CSRF
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.completed").value(true))
                .andExpect(jsonPath("$.data.title").value("Updated"));
    }


    // ============================
    // TEST DELETE TASK
    // ============================
    @Test
    @WithMockUser(username = "kien", roles = {"USER"})
    void testDeleteTask() throws Exception {

        Task task = Task.builder()
                .id(5L)
                .title("Delete me")
                .user(mockUser)
                .build();

        Mockito.when(taskService.getTaskByIdAndUserOrAdmin(anyLong(), eq(mockUser)))
                .thenReturn(task);

        mockMvc.perform(delete("/api/v1/tasks/delete/5")
                        .with(csrf()))  // <--- thêm CSRF token
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Xóa task thành công"));
    }
}
