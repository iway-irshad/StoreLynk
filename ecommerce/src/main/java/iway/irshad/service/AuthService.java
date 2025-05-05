package iway.irshad.service;

import iway.irshad.domain.USER_ROLE;
import iway.irshad.request.LoginRequest;
import iway.irshad.response.AuthResponse;
import iway.irshad.response.SignupRequest;

public interface AuthService {

    void sendLoginOTP(String email, USER_ROLE role) throws Exception;

    String createUser(SignupRequest request) throws Exception;

    AuthResponse signing(LoginRequest loginRequest);
}
