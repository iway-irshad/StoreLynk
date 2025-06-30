package iway.irshad.service.impl;

import iway.irshad.config.JwtProvider;
import iway.irshad.domain.USER_ROLE;
import iway.irshad.entity.Cart;
import iway.irshad.entity.Seller;
import iway.irshad.entity.User;
import iway.irshad.entity.VerificationCode;
import iway.irshad.repository.CartRepository;
import iway.irshad.repository.SellerRepository;
import iway.irshad.repository.UserRepository;
import iway.irshad.repository.VerificationCodeRepository;
import iway.irshad.request.LoginRequest;
import iway.irshad.response.AuthResponse;
import iway.irshad.response.SignupRequest;
import iway.irshad.service.AuthService;
import iway.irshad.service.EmailService;
import iway.irshad.utils.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final JwtProvider jwtProvider;
    private final CustomUserServiceImpl customUserService;
    private final SellerRepository sellerRepository;

    @Override
    public void sendLoginOTP(String email, USER_ROLE role) throws Exception {
        String SIGNING_PREFIX = "signing_";

        if (email.startsWith(SIGNING_PREFIX)) {
            email = email.substring(SIGNING_PREFIX.length());

            if (role.equals(USER_ROLE.ROLE_SELLER)) {
                Seller seller = sellerRepository.findByEmail(email);
                if (seller == null) {
                    throw new Exception("Seller does not exist with this email");
                }

            } else {
                User user = userRepository.findByEmail(email);
                if (user == null) {
                    throw new Exception("User does not exist with this email");
                }
            }


        }

        VerificationCode isExist = verificationCodeRepository.findByEmail(email);
        if (isExist != null) {
            verificationCodeRepository.delete(isExist);
        }

        String otp = OtpUtil.generateOtp();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(email);
        verificationCodeRepository.save(verificationCode);

        String subject = "Your StoreLynk Verification Code";

        String content = """
<html>
  <body style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 30px;">
    <table style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.05);">
      <tr>
        <td style="text-align: center;">
          <h2 style="color: #009688; margin-bottom: 10px;">StoreLynk</h2>
          <p style="font-size: 16px; color: #555555;">Your One-Time Password (OTP) is below</p>
        </td>
      </tr>
      <tr>
        <td style="text-align: center; padding: 20px 0;">
          <div style="display: inline-block; padding: 10px 20px; font-size: 24px; letter-spacing: 5px; font-weight: bold; background-color: #e0f2f1; color: #00796b; border-radius: 6px;">
            """ + otp + """
          </div>
        </td>
      </tr>
      <tr>
        <td style="padding-top: 20px; font-size: 14px; color: #777;">
          <p>Please do not share this code with anyone. It will expire in 10 minutes.</p>
          <p>If you didn’t request this code, please ignore this email.</p>
        </td>
      </tr>
      <tr>
        <td style="padding-top: 30px; font-size: 14px; color: #aaa; text-align: center;">
          <hr style="border: none; border-top: 1px solid #eee; margin: 20px 0;">
          <p>&copy; 2025 StoreLynk. All rights reserved.</p>
          <p>Contact: reply.web7@gmail.com</p>
        </td>
      </tr>
    </table>
  </body>
</html>
""";


        emailService.sendVerificationOTPEmail(email, subject, content);

    }

    @Override
    public String createUser(SignupRequest request) throws Exception {

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(request.getEmail());

        if (verificationCode == null || !verificationCode.getOtp().equals(request.getOtp()) ) {
            throw new Exception("Invalid verification code");
        }


        User user = userRepository.findByEmail(request.getEmail());

        if (user == null) {
            User newUser = new User();
            newUser.setEmail(request.getEmail());
            newUser.setFirstName(request.getFirstName());
            newUser.setLastName(request.getLastName());
            newUser.setRole(USER_ROLE.ROLE_CUSTOMER);
            newUser.setPhone("9304200977");
            newUser.setPassword(passwordEncoder.encode(request.getOtp()));
            user = userRepository.save(newUser);

            Cart cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_CUSTOMER.toString()));
        Authentication auth = new UsernamePasswordAuthenticationToken(request.getEmail(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        return jwtProvider.generateToken(auth);
    }

    @Override
    public AuthResponse signing(LoginRequest loginRequest) {

        String username = loginRequest.getEmail();
        String otp = loginRequest.getOtp();

        Authentication authentication = authenticate(username, otp);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwtToken(jwtToken);
        authResponse.setMessage("Login successful");
        User user = userRepository.findByEmail(username);
        authResponse.setName(user.getFirstName() + " " + user.getLastName());

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roleName = authorities.isEmpty()?null:authorities.iterator().next().getAuthority();

        authResponse.setRole(USER_ROLE.valueOf(roleName));

        return authResponse;
    }

    private Authentication authenticate(String username, String otp) {

        UserDetails userDetails = customUserService.loadUserByUsername(username);

        String SELLER_PREFIX = "seller_";
        if(username.startsWith(SELLER_PREFIX)) {
            username = username.substring(SELLER_PREFIX.length());
        }

        if(userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(username);

        if(verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new BadCredentialsException("Wrong otp");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
