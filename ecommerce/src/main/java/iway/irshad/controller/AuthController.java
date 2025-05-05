package iway.irshad.controller;

import iway.irshad.domain.USER_ROLE;
import iway.irshad.request.LoginOtpRequest;
import iway.irshad.request.LoginRequest;
import iway.irshad.response.ApiResponse;
import iway.irshad.response.AuthResponse;
import iway.irshad.response.SignupRequest;
import iway.irshad.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody SignupRequest signupRequest) throws Exception {

        String jwtToken = authService.createUser(signupRequest);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwtToken(jwtToken);
        authResponse.setMessage("Register successful");
        authResponse.setRole(USER_ROLE.ROLE_CUSTOMER);

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/sent/login-signup-otp")
    public ResponseEntity<ApiResponse> sentOtpHandler(@RequestBody LoginOtpRequest request) throws Exception {
        authService.sendLoginOTP(request.getEmail(), request.getRole());

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("OTP sent successfully");

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/signing")
    public ResponseEntity<AuthResponse> loginHandler(
            @RequestBody LoginRequest loginRequest
            ) {

        AuthResponse authResponse = authService.signing(loginRequest);

        return ResponseEntity.ok(authResponse);
    }
}
