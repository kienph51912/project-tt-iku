package vn.fpoly.project_tt_iku.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import vn.fpoly.project_tt_iku.entity.User;

@Data
public class UserRequest {

    @NotBlank(message = "Username không được để trống")
    @Size(max = 50, message = "Username không quá 50 ký tự")
    private String username;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, message = "Password phải có ít nhất 6 ký tự")
    private String password;

    @NotNull(message = "Role phải được khai báo")
    private User.Role role;
}
