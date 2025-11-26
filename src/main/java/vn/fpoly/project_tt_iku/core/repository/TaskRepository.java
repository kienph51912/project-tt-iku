package vn.fpoly.project_tt_iku.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.project_tt_iku.entity.Task;
import vn.fpoly.project_tt_iku.entity.User;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    void deleteAllByUser(User user);

    // Lấy task theo user + trạng thái + pageable
    Page<Task> findByUserAndCompleted(User user, Boolean completed, Pageable pageable);

    // Nếu muốn lấy tất cả task của user (không filter trạng thái)
    Page<Task> findByUser(User user, Pageable pageable);

    Page<Task> findByCompleted(Boolean completed, Pageable pageable);

}
