package finalmission.ballparkreservation.auth;

import finalmission.ballparkreservation.auth.dto.LoginRequest;
import finalmission.ballparkreservation.auth.dto.LoginResponse;
import finalmission.ballparkreservation.auth.dto.SignupRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody final SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody final LoginRequest request) {
        String token = authService.login(request);
        final ResponseCookie cookie = ResponseCookie.from("token", token).build();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie + "; Path=/;")
                .build();
    }
}
