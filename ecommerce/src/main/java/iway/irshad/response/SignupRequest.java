package iway.irshad.response;

import lombok.Data;

@Data
public class SignupRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String otp;
}
