package finalmission.ballparkreservation.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginResponse(
        @NotBlank String email,
        @NotBlank String name,
        @NotNull int age
) {
}
