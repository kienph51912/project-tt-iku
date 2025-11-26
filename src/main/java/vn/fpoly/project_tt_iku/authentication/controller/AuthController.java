package vn.fpoly.project_tt_iku.authentication.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.fpoly.project_tt_iku.authentication.dto.LoginRequest;
import vn.fpoly.project_tt_iku.authentication.dto.RegisterRequest;
import vn.fpoly.project_tt_iku.core.service.UserService;
import vn.fpoly.project_tt_iku.authentication.service.JwtUtils;
import vn.fpoly.project_tt_iku.entity.User;
import vn.fpoly.project_tt_iku.expection.ApiException;
import vn.fpoly.project_tt_iku.util.ResponseObject;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<ResponseObject<String>> register(@RequestBody @Valid RegisterRequest request) {
        log.info("Register request received for username: {}", request.getUsername());
        User user = userService.register(request); // nếu lỗi → ApiException
        log.info("User registered successfully: {}", user.getUsername());
        return ResponseEntity.ok(new ResponseObject<>(user.getUsername(), "Đăng ký thành công"));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject<String>> login(@RequestBody @Valid LoginRequest request) {
        log.info("Login request for username: {}", request.getUsername());
        User user = userService.findByUsername(request.getUsername()); // nếu lỗi → ApiException

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password for username: {}", request.getUsername());
            throw new ApiException("Mật khẩu không hợp lệ", "INVALID_PASSWORD");
        }

        String token = jwtUtils.generateToken(user.getUsername());
        log.info("User {} logged in successfully", user.getUsername());
        return ResponseEntity.ok(ResponseObject.success(token, "Đăng nhập thành công"));
    }
}


//package vn.fpoly.project_tt_iku.authentication.controller;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import vn.fpoly.project_tt_iku.authentication.dto.JwtResponse;
//import vn.fpoly.project_tt_iku.authentication.dto.LoginRequest;
//import vn.fpoly.project_tt_iku.authentication.dto.RegisterRequest;
//import vn.fpoly.project_tt_iku.authentication.service.JwtUtils;
//import vn.fpoly.project_tt_iku.core.service.UserService;
//import vn.fpoly.project_tt_iku.entity.User;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@RequiredArgsConstructor
//@RestController
//@RequestMapping("/api/v1/auth")
//public class AuthController {
//
//    private final UserService userService;
//    private final JwtUtils jwtUtils;
//    private final AuthenticationManager authenticationManager;
//    private final PasswordEncoder passwordEncoder;
//
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
//        User user = userService.register(request);
//        return ResponseEntity.ok("User registered: " + user.getUsername());
//    }
//
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
//        User user = userService.findByUsername(request.getUsername());
//
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
//        }
//
//        String token = jwtUtils.generateToken(user.getUsername());
//        return ResponseEntity.ok(new JwtResponse(token));
//    }
//}
//
//
