package iway.irshad.service;

import iway.irshad.domain.AccountStatus;
import iway.irshad.entity.Seller;
import iway.irshad.exceptions.SellerException;

import java.util.List;

public interface SellerService {
    Seller getSellerProfile(String jwtToken);
    Seller createSeller(Seller seller) throws Exception;
    Seller getSellerById(Long id) throws SellerException;
    Seller getSellerByEmail(String email);
    List<Seller> getAllSellers(AccountStatus status);
    Seller updateSeller(Long id, Seller seller) throws Exception;
    void deleteSeller(Long id) throws Exception;
    Seller verifyEmail(String email, String otp);
    Seller updateSellerAccountStatus(Long id, AccountStatus status) throws Exception;
}
