package vn.fpoly.project_tt_iku.authentication.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username không được để trống")
    private String username;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 3, message = "Password phải có ít nhất 3 ký tự")
    private String password;
}
