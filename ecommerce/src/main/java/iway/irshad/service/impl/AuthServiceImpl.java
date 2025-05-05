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

        String subject = "New Ecom Verification Code(OTP)";
        String content = "Your verification code is - " + otp;

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
