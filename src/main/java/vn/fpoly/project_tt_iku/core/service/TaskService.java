package vn.fpoly.project_tt_iku.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.fpoly.project_tt_iku.entity.Task;
import vn.fpoly.project_tt_iku.entity.User;
import vn.fpoly.project_tt_iku.core.repository.TaskRepository;
import vn.fpoly.project_tt_iku.expection.ApiException;
import vn.fpoly.project_tt_iku.util.PageableObject;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        log.info("Lấy tất cả task");
        return taskRepository.findAll();
    }

    public List<Task> getTasksByUser(User user) {
        log.info("Lấy task của user: {}", user.getUsername());
        return taskRepository.findByUserId(user.getId());
    }

    public Task getTaskByIdAndUserOrAdmin(Long taskId, User user) {
        return taskRepository.findById(taskId)
                .filter(task -> task.getUser().getId().equals(user.getId()) || user.getRole() == User.Role.ADMIN)
                .orElseThrow(() -> new ApiException("Task không tồn tại hoặc không thuộc về user", "TASK_NOT_FOUND"));
    }

    public Task createTask(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        log.info("Tạo task '{}' cho user {}", task.getTitle(), task.getUser().getUsername());
        return taskRepository.save(task);
    }

    public Task updateTask(Task task, Task updated) {
        task.setTitle(updated.getTitle());
        task.setDescription(updated.getDescription());
        task.setCompleted(updated.getCompleted());
        log.info("Cập nhật task id={} cho user {}", task.getId(), task.getUser().getUsername());
        return taskRepository.save(task);
    }

    public void deleteTask(Task task) {
        try {
            log.info("Xóa task id={} của user {}", task.getId(), task.getUser().getUsername());
            taskRepository.delete(task);
        } catch(DataIntegrityViolationException ex) {
            log.error("Không thể xóa task id={} do ràng buộc dữ liệu", task.getId());
            throw new ApiException("Không thể xóa task do ràng buộc dữ liệu", "DELETE_TASK_FAILED");
        }
    }

    public PageableObject<Task> phanTrang(Integer pageNo, Integer pageSize, User user, Boolean completed,
                                          String sortBy, String sortDir) {

        Sort.Direction direction = Sort.Direction.fromString(sortDir != null ? sortDir : "desc");
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(direction, sortBy != null ? sortBy : "createdAt"));

        Page<Task> taskPage;

        if (user.getRole() == User.Role.ADMIN) {
            // Admin: lấy tất cả task
            if (completed == null) {
                taskPage = taskRepository.findAll(pageable);
            } else {
                taskPage = taskRepository.findByCompleted(completed, pageable);
            }
        } else {
            // User bình thường: chỉ lấy task của họ
            if (completed == null) {
                taskPage = taskRepository.findByUser(user, pageable);
            } else {
                taskPage = taskRepository.findByUserAndCompleted(user, completed, pageable);
            }
        }
        return new PageableObject<>(taskPage);
    }
}
