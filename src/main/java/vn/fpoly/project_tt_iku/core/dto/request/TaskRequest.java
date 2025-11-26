package vn.fpoly.project_tt_iku.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {

    @NotBlank(message = "Title không được để trống")
    @Size(max = 300, message = "Title không quá 300 ký tự")
    private String title;

    @Size(max = 1500, message = "Description không quá 1500 ký tự")
    private String description;

    @NotNull(message = "Completed chỉ được khai báo true hoặc false")
    private Boolean completed;
}
