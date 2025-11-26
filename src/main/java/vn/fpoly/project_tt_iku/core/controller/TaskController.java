package vn.fpoly.project_tt_iku.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.fpoly.project_tt_iku.core.dto.request.TaskRequest;
import vn.fpoly.project_tt_iku.core.dto.response.TaskResponse;
import vn.fpoly.project_tt_iku.core.service.TaskService;
import vn.fpoly.project_tt_iku.core.service.UserService;
import vn.fpoly.project_tt_iku.entity.Task;
import vn.fpoly.project_tt_iku.entity.User;
import vn.fpoly.project_tt_iku.expection.ApiException;
import vn.fpoly.project_tt_iku.util.ResponseObject;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;


    private User getCurrentUser(Authentication auth) {
        return userService.findByUsername(auth.getName());
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseObject<List<TaskResponse>>> getAllTasksForAdmin(Authentication auth) {
        User currentUser = getCurrentUser(auth);
        if (currentUser.getRole() != User.Role.ADMIN) {
            log.warn("User {} không có quyền truy cập getAll", currentUser.getUsername());
            throw new ApiException("Không có quyền truy cập", "403");
        }

        List<TaskResponse> tasks = taskService.getAllTasks()
                .stream()
                .map(t -> new TaskResponse(t.getId(), t.getTitle(), t.getDescription(), t.getCompleted()))
                .collect(Collectors.toList());

        log.info("User {} lấy tất cả task", currentUser.getUsername());
        return ResponseEntity.ok(ResponseObject.success(tasks, "Lấy tất cả task thành công"));
    }

    @GetMapping
    public ResponseEntity<ResponseObject<List<TaskResponse>>> getAll(Authentication auth) {
        User user = getCurrentUser(auth);
        List<TaskResponse> tasks = taskService.getTasksByUser(user)
                .stream()
                .map(t -> new TaskResponse(t.getId(), t.getTitle(), t.getDescription(), t.getCompleted()))
                .collect(Collectors.toList());

        log.info("User {} lấy tất cả task của mình", user.getUsername());
        return ResponseEntity.ok(ResponseObject.success(tasks, "Lấy task thành công"));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseObject<TaskResponse>> getById(@PathVariable Long id, Authentication auth) {
        User user = getCurrentUser(auth);
        Task task = taskService.getTaskByIdAndUserOrAdmin(id, user);

        TaskResponse response = new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getCompleted());
        log.info("User {} lấy task id={}", user.getUsername(), id);
        return ResponseEntity.ok(ResponseObject.success(response, "Lấy task thành công"));
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseObject<TaskResponse>> create(@Valid @RequestBody TaskRequest request, Authentication auth) {
        User user = getCurrentUser(auth);
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .completed(request.getCompleted() != null ? request.getCompleted() : false)
                .user(user)
                .build();

        Task saved = taskService.createTask(task);
        TaskResponse response = new TaskResponse(saved.getId(), saved.getTitle(), saved.getDescription(), saved.getCompleted());

        log.info("User {} tạo task id={}", user.getUsername(), saved.getId());
        return ResponseEntity.ok(ResponseObject.success(response, "Tạo task thành công"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseObject<TaskResponse>> update(@PathVariable Long id, @Valid @RequestBody TaskRequest request, Authentication auth) {
        User user = getCurrentUser(auth);
        Task task = taskService.getTaskByIdAndUserOrAdmin(id, user);

        Task updated = taskService.updateTask(task, Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .completed(request.getCompleted())
                .build());

        TaskResponse response = new TaskResponse(updated.getId(), updated.getTitle(), updated.getDescription(), updated.getCompleted());
        log.info("User {} cập nhật task id={}", user.getUsername(), id);
        return ResponseEntity.ok(ResponseObject.success(response, "Cập nhật task thành công"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseObject<Void>> delete(@PathVariable Long id, Authentication auth) {
        User user = getCurrentUser(auth);
        Task task = taskService.getTaskByIdAndUserOrAdmin(id, user);

        taskService.deleteTask(task);
        log.info("User {} xóa task id={}", user.getUsername(), id);
        return ResponseEntity.ok(ResponseObject.success(null, "Xóa task thành công"));
    }

    @GetMapping("/paging")
    public ResponseObject<?> phanTrangTasks(
            @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "completed", required = false) Boolean completed,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            Authentication auth) {

        User user = getCurrentUser(auth);

        return ResponseObject.success(
                taskService.phanTrang(pageNo, pageSize, user, completed, sortBy, sortDir),
                "Lấy task phân trang thành công"
        );
    }

}
