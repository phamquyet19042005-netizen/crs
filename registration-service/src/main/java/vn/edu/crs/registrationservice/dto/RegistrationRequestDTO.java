package vn.edu.crs.registrationservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestDTO {

    @NotNull(message = "studentId khong duoc de trong")
    private Long studentId;

    @NotNull(message = "courseId khong duoc de trong")
    private Long courseId;
}
