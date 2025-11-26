package vn.fpoly.project_tt_iku.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.fpoly.project_tt_iku.core.dto.request.UserRequest;
import vn.fpoly.project_tt_iku.core.dto.response.UserResponse;
import vn.fpoly.project_tt_iku.core.service.UserService;
import vn.fpoly.project_tt_iku.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(Authentication auth) {
        checkAdmin(auth);
        List<UserResponse> users = userService.getAllUsers()
                .stream()
                .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getRole()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/delete/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication auth) {
        checkAdmin(auth);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request, Authentication auth) {
        checkAdmin(auth);
        User user = userService.createUser(request.getUsername(), request.getPassword(), request.getRole());
        UserResponse response = new UserResponse(user.getId(), user.getUsername(), user.getRole());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(@Valid @PathVariable Long id,
                                                   @RequestBody UserRequest request,
                                                   Authentication auth) {
        checkAdmin(auth);
        User user = userService.updateUser(id, request.getUsername(), request.getPassword(), request.getRole());
        UserResponse response = new UserResponse(user.getId(), user.getUsername(), user.getRole());
        return ResponseEntity.ok(response);
    }

    private void checkAdmin(Authentication auth) {
        User currentUser = userService.findByUsername(auth.getName());
        if (currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Không có quyền truy cập");
        }
    }
}
