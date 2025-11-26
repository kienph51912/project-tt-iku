package vn.fpoly.project_tt_iku.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.project_tt_iku.authentication.dto.RegisterRequest;
import vn.fpoly.project_tt_iku.entity.User;
import vn.fpoly.project_tt_iku.expection.ApiException;
import vn.fpoly.project_tt_iku.core.repository.UserRepository;
import vn.fpoly.project_tt_iku.core.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    // Đăng ký user bình thường
    public User register(RegisterRequest request) {
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new ApiException("Username đã tồn tại", "USERNAME_TAKEN");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .build();
        return userRepository.save(user);
    }

    // Lấy user theo username
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User không tồn tại", "USER_NOT_FOUND"));
    }

    // Tạo user (dành cho admin)
    public User createUser(String username, String rawPassword, User.Role role) {
        if(userRepository.existsByUsername(username)) {
            throw new ApiException("Username đã tồn tại", "USERNAME_TAKEN");
        }
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();
        return userRepository.save(user);
    }

    // updateUser
    @Transactional
    public User updateUser(Long id, String username, String rawPassword, User.Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User không tồn tại", "USER_NOT_FOUND"));

        // Check username mới trùng với user khác
        if(username != null && !username.isEmpty()) {
            Optional<User> existing = userRepository.findByUsername(username);
            if(existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new ApiException("Username đã tồn tại", "USERNAME_TAKEN");
            }
            user.setUsername(username);
        }

        if(rawPassword != null && !rawPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }

        // Check role null
        if(role == null) {
            throw new ApiException("Role không hợp lệ", "INVALID_ROLE");
        }
        user.setRole(role);

        return userRepository.save(user);
    }



    // Lấy tất cả user
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // deleteUser
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User không tồn tại", "USER_NOT_FOUND"));

        // Xóa task liên quan
        taskRepository.deleteAllByUser(user);

        try {
            userRepository.delete(user);
        } catch(DataIntegrityViolationException ex) {
            // Nếu có ràng buộc foreign key khác
            throw new ApiException("Không thể xóa user do ràng buộc dữ liệu", "DELETE_FAILED");
        }
    }

}
