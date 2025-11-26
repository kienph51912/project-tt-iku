package vn.fpoly.project_tt_iku.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc

public class TaskIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    // ============================================
    // 🔹 GET ALL TASKS
    // ============================================
    @Test
    @WithMockUser(username = "kien", roles = {"USER"})
    void testGetAllTasks() throws Exception {
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3)); // dùng đúng format API
    }

    // ============================================
    // 🔹 GET TASK BY ID
    // ============================================
    @Test
    @WithMockUser(username = "kien", roles = {"USER"})
    void testGetTaskById() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/get/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Task 2"));
    }


    // ============================================
    // 🔹 CREATE TASK
    // ============================================
    @Test
    @WithMockUser(username = "kien", roles = {"USER"})
    void testCreateTask() throws Exception {

        var newTask = new TaskRequest("New Task", "Description", false, null); // userId không cần

        mockMvc.perform(
                        post("/api/v1/tasks/create")  // sửa URL
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newTask))
                                .with(csrf()) // nếu CSRF enable
                )
                .andExpect(status().isOk()) // controller trả ResponseEntity.ok
                .andExpect(jsonPath("$.data.title").value("New Task"))
                .andExpect(jsonPath("$.data.description").value("Description"))
                .andExpect(jsonPath("$.data.completed").value(false));
    }


    // ============================================
    // 🔹 UPDATE TASK
    // ============================================
    @Test
    @WithMockUser(username = "kien", roles = {"USER"})
    void testUpdateTask() throws Exception {

        var update = new TaskRequest("Updated Task", "Updated", true, null); // userId không cần

        mockMvc.perform(
                        put("/api/v1/tasks/update/2") // sửa URL
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(update))
                                .with(csrf()) // nếu CSRF enable
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Updated Task"))
                .andExpect(jsonPath("$.data.description").value("Updated"))
                .andExpect(jsonPath("$.data.completed").value(true));
    }


    // ============================================
    // 🔹 DELETE TASK
    // ============================================
    @Test
    @WithMockUser(username = "kien", roles = {"USER"})
    void testDeleteTask() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/delete/2")
                        .with(csrf()) // nếu CSRF được enable
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa task thành công"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // DTO tạm thời
    static record TaskRequest(String title, String description, boolean completed, Long userId) {}
}
