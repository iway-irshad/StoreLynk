package iway.irshad.controller;

import iway.irshad.config.JwtProvider;
import iway.irshad.domain.AccountStatus;
import iway.irshad.entity.Seller;
import iway.irshad.entity.SellerReport;
import iway.irshad.entity.VerificationCode;
import iway.irshad.exceptions.SellerException;
import iway.irshad.repository.VerificationCodeRepository;
import iway.irshad.request.LoginRequest;
import iway.irshad.response.AuthResponse;
import iway.irshad.service.AuthService;
import iway.irshad.service.EmailService;
import iway.irshad.service.SellerReportService;
import iway.irshad.service.SellerService;
import iway.irshad.utils.OtpUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sellers")
public class SellerController {
    private final SellerService sellerService;
    private final AuthService authService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final JwtProvider jwtProvider;
    private final SellerReportService sellerReportService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginSeller(
            @RequestBody LoginRequest request
            ) throws Exception {

        System.out.println("Request:-----------------------------> Reaches in backend");
        String otp = request.getOtp();
        String email = request.getEmail();

        request.setEmail("seller_" + email);
        System.out.println(otp + " " + email);

        request.setEmail("seller_" + email);
        AuthResponse authResponse = authService.signing(request);
        return ResponseEntity.ok(authResponse);
    }

    @PatchMapping("/verify/{otp}")
    public ResponseEntity<Seller> verifySellerEmail(@PathVariable String otp) throws Exception {
        VerificationCode verificationCode = verificationCodeRepository.findByOtp(otp);

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new Exception("Wrong otp");
        }
        Seller seller = sellerService.verifyEmail(verificationCode.getEmail(), otp);

        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Seller> createSeller(@RequestBody Seller seller) throws Exception, MessagingException {
        Seller savedSeller = sellerService.createSeller(seller);

        String otp = OtpUtil.generateOtp();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(seller.getEmail());
        verificationCodeRepository.save(verificationCode);

        String subject = "New-eCommerce Email Verification Code";
        String frontend_url = "http://localhost:8080/verify-sellers/";
        String text = "Welcome to the New ECommerce, verify your account using this link" + frontend_url;
        emailService.sendVerificationOTPEmail(seller.getEmail(), subject, text);

        return new ResponseEntity<>(savedSeller, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable Long id) throws SellerException {
        Seller seller = sellerService.getSellerById(id);
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<Seller> getSellerByJwt(@RequestHeader("Authorization") String jwt) throws Exception {
        Seller seller = sellerService.getSellerProfile(jwt);
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    @GetMapping("/report")
    public ResponseEntity<SellerReport> getSellerReport(
            @RequestHeader("Authorization") String jwtToken ) {
        Seller seller = sellerService.getSellerProfile(jwtToken);
        SellerReport sellerReport = sellerReportService.getSellerReport(seller);
        return new ResponseEntity<>(sellerReport, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Seller>> getSellers(@RequestParam(required = false) AccountStatus status) throws Exception {
        List<Seller> sellers = sellerService.getAllSellers(status);
        return ResponseEntity.ok(sellers);
    }

    @PatchMapping
    public ResponseEntity<Seller> updateSeller(@RequestHeader("Authorization") String jwt, @RequestBody Seller seller) throws Exception {
        Seller profile = sellerService.getSellerProfile(jwt);
        Seller updatedSeller = sellerService.updateSeller(profile.getId(), seller);
        return ResponseEntity.ok(updatedSeller);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Seller> deleteSeller(@PathVariable Long id) throws Exception {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }

}
